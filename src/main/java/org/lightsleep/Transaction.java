// Transaction.java
// (C) 2016 Masato Kokubo

package org.lightsleep;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Objects;

import org.lightsleep.connection.ConnectionSupplier;
import org.lightsleep.helper.Resource;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

/**
 * A functional interface to execute transactions.
 *
 * <div class="sampleTitle"><span>Example</span></div>
 * <div class="sampleCode"><pre>
 * <b>Transaction.execute(connection -&gt; {</b>
 *     new Sql&lt;&gt;(Person.class)
 *         .update(connection, person);
 * <b>});</b>
 * </pre></div>
 *
 * @author Masato Kokubo
 */
@FunctionalInterface
public interface Transaction {
	// The logger
	static final Logger logger = LoggerFactory.getLogger(Transaction.class);

	// Class resources
	static final Resource resource = new Resource(Transaction.class);
	static final String messageGet      = resource.getString("messageGet");
	static final String messageClose    = resource.getString("messageClose");
	static final String messageStart    = resource.getString("messageStart");
	static final String messageEnd      = resource.getString("messageEnd");
	static final String messageCommit   = resource.getString("messageCommit");
	static final String messageRollback = resource.getString("messageRollback");

	/**
	 * Describe the body of the transaction in this method.
	 *
	 * @param connection the database connection
	 *
	 * @throws Exception if an error occurred
	 */
	void executeBody(Connection connection) throws Exception;

	/**
	 * Executes the transaction in the following order.
	 *
	 * <ol>
	 *   <li>Gets a database connection by calling <b>Sql.getConnectionSupplier().get()</b>.</li>
	 *   <li>Calls <b>transaction.executeBody</b> method.</li>
	 *   <li>Commits the transaction.</li>
	 *   <li>Closes the database connection.</li>
	 * </ol>
	 *
	 * <p>
	 * If an exception is thrown executing the transaction body, rollbacks rather than commit.
	 * </p>
	 *
	 * <p>
	 * Describe the transaction body in <b>transaction</b> using a lumbda expression.
	 * </p>
	 *
	 * @param transaction a <b>Transaction</b> object
	 *
	 * @throws NullPointerException if <b>transaction</b> is null
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database
	 */
	static void execute(Transaction transaction) {
		execute(Sql.getConnectionSupplier(), transaction);
	}

	/**
	 * Executes the transaction in the following order.
	 *
	 * <ol>
	 *   <li>Gets a database connection by calling <b>connectionSupplier.get()</b>.</li>
	 *   <li>Calls <b>transaction.executeBody</b> method.</li>
	 *   <li>Commits the transaction.</li>
	 *   <li>Closes the database connection.</li>
	 * </ol>
	 *
	 * <p>
	 * If an exception is thrown executing the transaction body, rollbacks rather than commit.
	 * </p>
	 *
	 * <p>
	 * Describe the transaction body in <b>transaction</b> using a lumbda expression.
	 * </p>
	 *
	 * @param connectionSupplier a <b>ConnectionSupplier</b> object
	 * @param transaction a <b>Transaction</b> object
	 *
	 * @throws NullPointerException if <b>connectionSupplier</b> or <b>transaction</b> is null
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database
	 *
	 * @since 1.5.0
	 */
	static void execute(ConnectionSupplier connectionSupplier, Transaction transaction) {
		Objects.requireNonNull(connectionSupplier, "connectionSupplier");
		Objects.requireNonNull(transaction, "transaction");

		Connection connection = null;
		boolean committed = false;
		try {
			// Gets a connection
			long beforeGetTime = System.nanoTime(); // The time before connectionSupplier.get
			connection = connectionSupplier.get();
			long afterGetTime = System.nanoTime(); // The time after connectionSupplier.get

			if (logger.isDebugEnabled()) {
				double time = (afterGetTime - beforeGetTime) / 1_000_000.0;
				DecimalFormat timeFormat = new DecimalFormat();
				timeFormat.setMinimumFractionDigits(0);
				timeFormat.setMaximumFractionDigits(3);
				logger.debug(
					Sql.getDatabase().getClass().getSimpleName()
					+ "/" + connectionSupplier.getClass().getSimpleName()
					+ ": " + MessageFormat.format(messageGet, timeFormat.format(time))
				);
			}

			// Logging of the transaction start
			logger.debug(() -> Sql.getDatabase().getClass().getSimpleName() + ": " + messageStart);

			// Execute the transaction body
			transaction.executeBody(connection);

			// Commit
			commit(connection);
			committed = true;

			//  Logging of the transaction end
			logger.debug(messageEnd);

			// Closes the connection
			long beforeCloseTime = System.nanoTime(); // The time before connectionSupplier.get
			connection.close();
			long afterCloseTime = System.nanoTime(); // The time after connectionSupplier.get

			if (logger.isDebugEnabled()) {
				double time = (afterCloseTime - beforeCloseTime) / 1_000_000.0;
				DecimalFormat timeFormat = new DecimalFormat();
				timeFormat.setMinimumFractionDigits(0);
				timeFormat.setMaximumFractionDigits(3);
				logger.debug(
					Sql.getDatabase().getClass().getSimpleName()
				// 1.5.0
				//	+ "/" + Sql.getConnectionSupplier().getClass().getSimpleName()
					+ "/" + connectionSupplier.getClass().getSimpleName()
				////
					+ ": " + MessageFormat.format(messageClose, timeFormat.format(time))
				);
			}
		}
		catch (Throwable e) {
			logger.error(e.toString(), e);
			if (connection != null) {
				if (!committed) {
					try {
						// Rollback
						rollback(connection);

						//  Logging of the transaction end
						logger.debug(() -> Sql.getDatabase().getClass().getSimpleName() + ": " + messageEnd);
					}
					catch (Throwable e2) {
						logger.error(e2.toString(), e2);
					}
				}

				// Closes the connection
				try {
					connection.close();
				}
				catch (Throwable e2) {
					logger.error(e2.toString(), e2);
				}
			}

			if (e instanceof Error) throw (Error)e;
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			if (e instanceof SQLException) throw new RuntimeSQLException(e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * If the connection is not auto-commit, commits the transaction.
	 *
	 * @param connection the database connection
	 *
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database
	 */
	static void commit(Connection connection) {
		try {
			if (!connection.getAutoCommit()) {
				// Is not not auto-commit
				long beforeExecTime = System.nanoTime(); // The time before execution
				connection.commit();
				long afterExecTime = System.nanoTime(); // The time after execution

				if (logger.isDebugEnabled()) {
					double time = (afterExecTime - beforeExecTime) / 1_000_000.0;
					DecimalFormat timeFormat = new DecimalFormat();
					timeFormat.setMinimumFractionDigits(0);
					timeFormat.setMaximumFractionDigits(3);
					logger.debug(Sql.getDatabase().getClass().getSimpleName() + ": " + MessageFormat.format(messageCommit, timeFormat.format(time)));
				}
			}
		}
		catch (RuntimeException e) {throw e;}
		catch (SQLException     e) {throw new RuntimeSQLException(e);}
		catch (Exception        e) {new RuntimeException(e);}
	}

	/**
	 * If the connection is not auto-commit, rollbacks the transaction.
	 *
	 * @param connection the database connection
	 *
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database
	 */
	static void rollback(Connection connection) {
		try {
			if (!connection.getAutoCommit()) {
				// Is not not auto-commit
				long beforeExecTime = System.nanoTime(); // The time before execution
				connection.rollback();
				long afterExecTime = System.nanoTime(); // The time after execution

				if (logger.isDebugEnabled()) {
					double time = (afterExecTime - beforeExecTime) / 1_000_000.0;
					DecimalFormat timeFormat = new DecimalFormat();
					timeFormat.setMinimumFractionDigits(0);
					timeFormat.setMaximumFractionDigits(3);
					logger.debug(Sql.getDatabase().getClass().getSimpleName() + ": " + MessageFormat.format(messageRollback, timeFormat.format(time)));
				}
			}
		}
		catch (RuntimeException e) {throw e;}
		catch (SQLException     e) {throw new RuntimeSQLException(e);}
		catch (Exception        e) {new RuntimeException(e);}
	}
}
