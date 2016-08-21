/*
	Transaction.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import org.lightsleep.helper.Resource;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

/**
	A functional interface to execute transactions.

	<div class="sampleTitle"><span>An Example</span></div>
<div class="sampleCode"><pre>
Transaction.execute(connection -&gt; {
    new Sql&lt;&gt;(Person.class)
        .update(connection, person);
});
</pre></div>

	@author Masato Kokubo
*/
@FunctionalInterface
public interface Transaction {
	// The logger
	static final Logger logger = LoggerFactory.getLogger(Transaction.class);

	// Class resources
	static final Resource resource = new Resource(Transaction.class);
	static final String messageGet      = resource.get("messageGet");
	static final String messageClose    = resource.get("messageClose");
	static final String messageStart    = resource.get("messageStart");
	static final String messageEnd      = resource.get("messageEnd");
	static final String messageCommit   = resource.get("messageCommit");
	static final String messageRollback = resource.get("messageRollback");

	/**
		Describe the body of the transaction in this method.

		@param connection the database connection

		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database
	*/
	void executeBody(Connection connection);

	/**
		Executes the transaction in the following order.<br>
		<br>
		<ol>
			<li>Gets a database connection by calling <b>Sql.connectionSupplier</b>.</li>
			<li>Calls <b>transaction.executeBody</b> method.</li>
			<li>Commits the transaction.</li>
			<li>Closes the database connection.</li>
		</ol>
		<br>

		If an exception is thrown executing the transaction body, rollbacks rather than commit.<br>
		<br>

		Describe the transaction body in <b>transaction</b> using a lumbda expression.

		@param transaction a <b>Transaction</b> object

		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database
	*/
	static void execute(Transaction transaction) {
		Connection connection = null;
		boolean committed = false;
		try {
			// Gets a connection
			long beforeGetTime = System.nanoTime(); // The time before getConnectionSupplier.get
			connection = Sql.getConnectionSupplier().get();
			long afterGetTime = System.nanoTime(); // The time after getConnectionSupplier.get

			if (logger.isDebugEnabled()) {
				double time = (afterGetTime - beforeGetTime) / 1_000_000.0;
				DecimalFormat timeFormat = new DecimalFormat();
				timeFormat.setMinimumFractionDigits(0);
				timeFormat.setMaximumFractionDigits(3);
				logger.debug(
					Sql.getDatabase().getClass().getSimpleName()
					+ "/" + Sql.getConnectionSupplier().getClass().getSimpleName()
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
			logger.info(messageEnd);

			// Closes the connection
			long beforeCloseTime = System.nanoTime(); // The time before getConnectionSupplier.get
			connection.close();
			long afterCloseTime = System.nanoTime(); // The time after getConnectionSupplier.get

			if (logger.isDebugEnabled()) {
				double time = (afterCloseTime - beforeCloseTime) / 1_000_000.0;
				DecimalFormat timeFormat = new DecimalFormat();
				timeFormat.setMinimumFractionDigits(0);
				timeFormat.setMaximumFractionDigits(3);
				logger.debug(
					Sql.getDatabase().getClass().getSimpleName()
					+ "/" + Sql.getConnectionSupplier().getClass().getSimpleName()
					+ ": " + MessageFormat.format(messageClose, timeFormat.format(time))
				);
			}
		}
		catch (Throwable e) {
			logger.error("", e);
			if (connection != null) {
				if (!committed) {
					try {
						// Rollback
						rollback(connection);

						//  Logging of the transaction end
						logger.debug(() -> Sql.getDatabase().getClass().getSimpleName() + ": " + messageEnd);
					}
					catch (Throwable e2) {
						logger.error("", e2);
					}
				}

				// Closes the connection
				try {
					connection.close();
				}
				catch (Throwable e2) {
					logger.error("", e2);
				}
			}

			if (e instanceof Error) throw (Error)e;
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			if (e instanceof SQLException) throw new RuntimeSQLException(e);
			throw new RuntimeException(e);
		}
	}

	/**
		If the connection is not auto-commit, commits the transaction.

		@param connection the database connection

		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database
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
		catch (Error            e) {throw e;}
		catch (RuntimeException e) {throw e;}
		catch (SQLException     e) {throw new RuntimeSQLException(e);}
		catch (Throwable        e) {new RuntimeException(e);}
	}

	/**
		If the connection is not auto-commit, rollbacks the transaction.

		@param connection the database connection

		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database
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
		catch (Error            e) {throw e;}
		catch (RuntimeException e) {throw e;}
		catch (SQLException     e) {throw new RuntimeSQLException(e);}
		catch (Throwable        e) {new RuntimeException(e);}
	}
}
