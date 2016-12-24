/*
	AbstractConnectionSupplier.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.lightsleep.RuntimeSQLException;
import org.lightsleep.database.Database;
import org.lightsleep.helper.Resource;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

/**
	The abstract connection supplier

	@since 1.1.0
	@author Masato Kokubo
*/
public abstract class AbstractConnectionSupplier implements ConnectionSupplier {
	/** The logger */
	protected static final Logger logger = LoggerFactory.getLogger(AbstractConnectionSupplier.class);

	// The map of isolation levels
	private static Map<Integer, String> isolationLevelsMap = new HashMap<>();
	static {
		isolationLevelsMap.put(Connection.TRANSACTION_NONE            , "none");
		isolationLevelsMap.put(Connection.TRANSACTION_READ_UNCOMMITTED, "read-uncommitted");
		isolationLevelsMap.put(Connection.TRANSACTION_READ_COMMITTED  , "read-committed");
		isolationLevelsMap.put(Connection.TRANSACTION_REPEATABLE_READ , "repeatable-read");
		isolationLevelsMap.put(Connection.TRANSACTION_SERIALIZABLE    , "serializable");
	}

	/** The properties */
	protected Properties properties = Resource.globalResource.getProperties();

	// The data source
	private DataSource dataSource;

	/**
		Constructs a new <b>AbstractConnectionSupplier</b>.
		Use values specified in the lightsleep.properties file as the connection information.
	*/
	public AbstractConnectionSupplier() {
	// 1.2.0
	// 1.5.0
	//	properties.remove(Logger.class.getSimpleName());
	//	properties.remove(Database.class.getSimpleName());
	//	properties.remove(ConnectionSupplier.class.getSimpleName());
		this(modifier -> {});
	////
	}

	/**
		Constructs a new <b>AbstractConnectionSupplier</b>.
		Use values specified in the lightsleep.properties file as the connection information.

		@param modifier a consumer to modify the properties

		@since 1.5.0
	*/
	public AbstractConnectionSupplier(Consumer<Properties> modifier) {
		if (modifier == null)
			throw new NullPointerException(getClass().getSimpleName() + ".<init>: modifier == null");

		properties.remove(Logger.class.getSimpleName());
		properties.remove(Database.class.getSimpleName());
		properties.remove(ConnectionSupplier.class.getSimpleName());
		modifier.accept(properties);

		logger.debug(() -> getClass().getSimpleName() + ".<init>: properties: " + this.properties);
	}

// 1.2.0
//	/**
//		Constructs a new <b>AbstractConnectionSupplier</b>.
//		Use values specified in the lightsleep.properties and
//		<i>&lt;<b>resourceName</b>&gt;</i>.properties file as the connection information.
//
//		@param resourceName the resource name
//	*/
//	public AbstractConnectionSupplier(String resourceName) {
//		properties.putAll(new Resource(resourceName).getProperties());
//	}
////

	/**
		Returns a data source.

		@return a data source
	*/
	protected abstract DataSource getDataSource();

	/**
		Returns a database connection.

		@return a database connection

		@throws RuntimeSQLException if a <b>SQLException</b> is thrown
	*/
	@Override
	public Connection get() {
		try {
			if (dataSource == null) {
				synchronized (this) {
					if (dataSource == null) {
						dataSource = getDataSource();
					}
				}
			}

			Connection connection = dataSource.getConnection();
			boolean beforeAutoCommit = connection.getAutoCommit();
			int transactionIsolation = connection.getTransactionIsolation();
			connection.setAutoCommit(false);
			boolean afterAutoCommit = connection.getAutoCommit();

			logger.debug(() ->
				getClass().getSimpleName()
				+ ".get: connection.autoCommit: " + beforeAutoCommit + " -> " + afterAutoCommit
				+ ", connection.transactionIsolation: "
				+ isolationLevelsMap.getOrDefault(transactionIsolation, "unknow")
			);

			return connection;
		}
		catch (SQLException e) {
			throw new RuntimeSQLException(e);
		}
	}
}
