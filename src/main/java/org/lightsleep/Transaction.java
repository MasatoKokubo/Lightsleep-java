// Transaction.java
// (C) 2016 Masato Kokubo

package org.lightsleep;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Objects;

import org.lightsleep.connection.ConnectionSupplier;
import org.lightsleep.connection.ConnectionWrapper;

/**
 * A functional interface to execute transactions.
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>Transaction.execute(conn -&gt; {</b>
 *     Optional&lt;Contact&gt; contactOpt = new Sql&lt;&gt;(Contact.class)
 *         .where("{id}={}", 1)
 *         .connection(conn)
 *         .select();
 *     contactOpt.ifPresent(contact -&gt; {
 *         contact.setBirthday(2017, 1, 1);
 *         new Sql&lt;&gt;(Contact.class)
 *             .connection(conn)
 *             .update(contact);
 *     });
 * <b>});</b>
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 * <b>Transaction.execute {</b>
 *     def contactOpt = new Sql&lt;&gt;(Contact)
 *         .where('{id}={}', 1)
 *         .connection(it)
 *         .select()
 *     contactOpt.ifPresent {Contact contact -&gt;
 *         contact.setBirthday(2017, 1, 1)
 *         new Sql&lt;&gt;(Contact)
 *             .connection(it)
 *             .update(contact)
 *     }
 * <b>}</b>
 * </pre></div>
 *
 * @author Masato Kokubo
 */
@FunctionalInterface
public interface Transaction {
	/**
	 * Describe the body of the transaction in this method.
	 *
	 * @param connection the connection wrapper
	 *
	 * @throws Exception if an error occurred
	 */
	void executeBody(ConnectionWrapper connection) throws Exception;

	/**
	 * Executes a transaction in the following order.
	 *
	 * <ol>
	 *   <li>Gets a connection wrapper by invoking <b>Sql.getConnectionSupplier().get()</b>.</li>
	 *   <li>Calls <b>transaction.executeBody</b> method.</li>
	 *   <li>Commits the transaction.</li>
	 *   <li>Closes the connection wrapper.</li>
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
	 * @param transaction the <b>Transaction</b> object
	 *
	 * @throws NullPointerException if <b>transaction</b> is <b>null</b>
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database
	 */
	static void execute(Transaction transaction) {
		execute(ConnectionSupplier.find(), transaction);
	}

	/**
	 * Executes a transaction in the following order.
	 *
	 * <ol>
	 *   <li>Gets a connection wrapper by invoking <b>connectionSupplier.get()</b>.</li>
	 *   <li>Calls <b>transaction.executeBody</b> method.</li>
	 *   <li>Commits the transaction.</li>
	 *   <li>Closes the connection wrapper.</li>
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
	 * @param transaction the <b>Transaction</b> object
	 *
	 * @throws NullPointerException if <b>connectionSupplier</b> or <b>transaction</b> is <b>null</b>
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database
	 *
	 * @since 1.5.0
	 */
	static void execute(ConnectionSupplier connectionSupplier, Transaction transaction) {
		Objects.requireNonNull(connectionSupplier, "connectionSupplier is null");
		Objects.requireNonNull(transaction, "transaction is null");

		ConnectionWrapper connection = null;
		boolean committed = false;
		try {
			// Gets a connection
			long beforeGetTime = System.nanoTime(); // The time before connectionSupplier.get
			connection = connectionSupplier.get();
			long afterGetTime = System.nanoTime(); // The time after connectionSupplier.get

			if (Sql.logger.isDebugEnabled()) {
				double time = (afterGetTime - beforeGetTime) / 1_000_000.0;
				DecimalFormat timeFormat = new DecimalFormat();
				timeFormat.setMinimumFractionDigits(0);
				timeFormat.setMaximumFractionDigits(3);
				String logHeader = connectionSupplier.toString() + ": ";
				Sql.logger.debug(logHeader
					+ MessageFormat.format(Sql.messageGet, timeFormat.format(time), connectionSupplier.getUrl()));

				Sql.logger.debug(logHeader + Sql.messageStart);
			}

			// Execute the transaction body
			transaction.executeBody(connection);

			// Commit
			commit(connection);
			committed = true;

			//  Logging of the transaction end
			Sql.logger.debug(Sql.messageEnd);
		}
		catch (Throwable e) {
			Sql.logger.error(e.toString(), e);
			if (connection != null) {
				if (!committed) {
					try {
						// Rollback
						rollback(connection);

						//  Logging of the transaction end
						if (Sql.logger.isDebugEnabled())
							Sql.logger.debug(connectionSupplier.toString() + ": " + Sql.messageEnd);
					}
					catch (Throwable e2) {
						Sql.logger.error(e2.toString(), e2);
					}
				}
			}

			if (e instanceof Error) throw (Error)e;
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			if (e instanceof SQLException) throw new RuntimeSQLException(e);
			throw new RuntimeException(e);
		}
		finally {
			if (connection != null) {
				// Closes the connection
				long beforeCloseTime = System.nanoTime(); // The time before connectionSupplier.get
				try {
					connection.close();
				}
				catch (SQLException e) {
					throw new RuntimeSQLException(e);
				}
				long afterCloseTime = System.nanoTime(); // The time after connectionSupplier.get

				if (Sql.logger.isDebugEnabled()) {
					double time = (afterCloseTime - beforeCloseTime) / 1_000_000.0;
					DecimalFormat timeFormat = new DecimalFormat();
					timeFormat.setMinimumFractionDigits(0);
					timeFormat.setMaximumFractionDigits(3);
					Sql.logger.debug(connectionSupplier.toString() + ": "
						+ MessageFormat.format(Sql.messageClose, timeFormat.format(time)));
				}
			}
		}
	}

	/**
	 * If the connection is not auto-commit, commits the transaction.
	 *
	 * @param connection the connection wrapper
	 *
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database
	 */
	static void commit(ConnectionWrapper connection) {
		Objects.requireNonNull(connection, "connection is null");

		try {
			if (!connection.getAutoCommit()) {
				// Is not not auto-commit
				long beforeExecTime = System.nanoTime(); // The time before execution
				connection.commit();
				long afterExecTime = System.nanoTime(); // The time after execution

				if (Sql.logger.isDebugEnabled()) {
					double time = (afterExecTime - beforeExecTime) / 1_000_000.0;
					DecimalFormat timeFormat = new DecimalFormat();
					timeFormat.setMinimumFractionDigits(0);
					timeFormat.setMaximumFractionDigits(3);
					Sql.logger.debug(connection.toString()
						+ ": " + MessageFormat.format(Sql.messageCommit, timeFormat.format(time)));
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
	 * @param connection the connection wrapper
	 *
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database
	 */
	static void rollback(ConnectionWrapper connection) {
		Objects.requireNonNull(connection, "connection is null");
		try {
			if (!connection.getAutoCommit()) {
				// Is not not auto-commit
				long beforeExecTime = System.nanoTime(); // The time before execution
				connection.rollback();
				long afterExecTime = System.nanoTime(); // The time after execution

				if (Sql.logger.isDebugEnabled()) {
					double time = (afterExecTime - beforeExecTime) / 1_000_000.0;
					DecimalFormat timeFormat = new DecimalFormat();
					timeFormat.setMinimumFractionDigits(0);
					timeFormat.setMaximumFractionDigits(3);
					Sql.logger.debug(connection.toString()
						+ ": " + MessageFormat.format(Sql.messageRollback, timeFormat.format(time)));
				}
			}
		}
		catch (RuntimeException e) {throw e;}
		catch (SQLException     e) {throw new RuntimeSQLException(e);}
		catch (Exception        e) {new RuntimeException(e);}
	}
}
