// AbstractConnectionSupplier.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.lightsleep.RuntimeSQLException;
import org.lightsleep.database.Database;
import org.lightsleep.database.Standard;
import org.lightsleep.helper.Resource;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

/**
 * An abstract class implementing the intersection of connection wrapper suppliers.
 *
 * @since 1.1.0
 * @author Masato Kokubo
 */
public abstract class AbstractConnectionSupplier implements ConnectionSupplier {
// 2.1.0
	// Class resources
	private static final Resource resource = new Resource(AbstractConnectionSupplier.class);
	protected static final String messageUrlNotFound       = resource.getString("messageUrlNotFound");
	protected static final String messageMultipleUrlsFound = resource.getString("messageMultipleUrlsFound");
////

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

// 2.1.0
	/** The map of key: url string and value: ConnectionSupplier */
	protected static final Map<String, ConnectionSupplier> supplierMap = new LinkedHashMap<>();
	static {
		initClass();
	}

	// Initializes this class.
	private static void initClass() {
		supplierMap.clear();

		// get a ConnectionSupplier class name
		String supplierName = Resource.getGlobal().getString(ConnectionSupplier.class.getSimpleName(), Jdbc.class.getSimpleName());
		logger.debug(() -> "AbstractConnectionSupplier.initClass: supplierName: " + supplierName);

		try {
			// get a Properties from lightsleep.propeprties 
			Properties properties = Resource.getGlobal().getProperties();
			logger.debug(() -> "AbstractConnectionSupplier.initClass: raw properties: " + properties);

			// urls
			String urlStr = null;
			String urlsStr = properties.getProperty("urls");
			if (urlsStr != null) {
				properties.remove("urls");
			} else {
				// dataSources for Jndi
				urlsStr = properties.getProperty("dataSources");
				if (urlsStr != null) {
					properties.remove("dataSources");
				} else {
					// url
					urlStr = properties.getProperty("url");
					if (urlStr != null) {
						properties.remove("url");
					} else {
						// dataSource for Jndi
						urlStr = properties.getProperty("dataSource");
						if (urlStr != null)
							properties.remove("dataSource");
					}
				}
			}

			String[] urls = urlsStr != null
				? urlsStr.split(",")
				: urlStr != null ? new String[] {urlStr} : new String[0];

			Arrays.stream(urls)
				.map(String::trim)
				.filter(url -> !url.isEmpty())
				.forEach(url -> {
					try {
						Properties supplierProperties = new Properties();
						properties.stringPropertyNames().forEach(name -> supplierProperties.put(name, properties.get(name)));

						ConnectionSupplier supplier = null;
						if (url.startsWith("[")) {
							// A connection supplier is specified at the head of url
							int braIndex = url.indexOf(']');
							if (braIndex > 0) {
								// Get a ConnectionSupplier class name
								supplierProperties.put("url", url.substring(braIndex + 1).trim());
								supplier = ConnectionSupplier.of(url.substring(1, braIndex).trim(), supplierProperties);
							}
						}
						if (supplier == null) {
							supplierProperties.put("url", url);
							supplier = ConnectionSupplier.of(supplierName, supplierProperties);
							logger.info("ConnectionSupplier.initClass: url: " + url);
						}
						ConnectionSupplier beforeUrl = supplierMap.put(url, supplier);
						if (beforeUrl != null)
							// DOTO Use resource message
							logger.warn("ConnectionSupplier.initClass: Multiple same urls are defined. url: '" + url + "'");
					}
					catch (Exception e) {
						logger.error("ConnectionSupplier.initClass: url: " + url + ", exception: " + e);
					}
				});
		}
		catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}
////

	/** The properties */
// 2.1.0
//	protected final Properties properties = Resource.globalResource.getProperties();
	protected Properties properties;
////

	// The data source
	private DataSource dataSource;

	// The database handler. @since 2.1.0
	private Database database;

// 2.1.0
//	/**
//	 * Constructs a new <b>AbstractConnectionSupplier</b>.
//	 * Use values specified in the lightsleep.properties file as the connection information.
//	 */
//	public AbstractConnectionSupplier() {
//		this(modifier -> {});
//	}
//
//	/**
//	 * Constructs a new <b>AbstractConnectionSupplier</b>.
//	 * Use values specified in the lightsleep.properties file as the connection information.
//	 *
//	 * @param modifier a consumer to modify the properties
//	 *
//	 * @since 1.5.0
//	 */
//	public AbstractConnectionSupplier(Consumer<Properties> modifier) {
//		properties.remove(Logger.class.getSimpleName());
//		properties.remove(Database.class.getSimpleName());
//		properties.remove(ConnectionSupplier.class.getSimpleName());
//		Objects.requireNonNull(modifier, "modifier").accept(properties);
//	
//		logger.debug(() -> getClass().getSimpleName() + ".<init>: properties: " + properties);
//	}
////

	/**
	 * Constructs a new <b>AbstractConnectionSupplier</b>.
	 *
	 * @param properties the properties with connection information
	 * @param modifier a consumer to modify properties
	 *
	 * @since 2.1.0
	 */
	protected AbstractConnectionSupplier(Properties properties, Consumer<Properties> modifier) {
		this.properties = Objects.requireNonNull(properties);
		properties.remove(Logger.class.getSimpleName());
		properties.remove(Database.class.getSimpleName());
		properties.remove(ConnectionSupplier.class.getSimpleName());
		Objects.requireNonNull(modifier, "modifier").accept(properties);

		logger.debug(() -> getClass().getSimpleName() + ".<init>: modified properties: " + properties);

		String url = properties.getProperty("url");
		try {
			database = Database.getInstance(url);
		}
		catch (IllegalArgumentException e) {
			logger.warn(e.toString());
			database = Standard.instance;
		}
		logger.info(() -> getClass().getSimpleName()
			+ ".<init>: url: " + url + ", database handler: " + database.getClass().getSimpleName());
	}

// 2.1.0
//	/**
//	 * Returns a data source.
//	 *
//	 * @return a data source
//	 */
//	protected abstract DataSource getDataSource();
////

	/**
	 * Returns a connection wrapper.
	 *
	 * @return a connection wrapper
	 *
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown
	 */
	@Override
// 2.1.0
//	public Connection get() {
	public ConnectionWrapper get() {
////
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

		// 2.1.0
		//	return connection;
			return new ConnectionWrapper(connection, database);
		////
		}
		catch (SQLException e) {
			throw new RuntimeSQLException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 2.1.0
	 */
	@Override
	public Database getDatabase() {
		return database;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 2.1.0
	 */
	@Override
	public String getUrl() {
		return properties.getProperty("url");
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 2.1.0
	 */
	@Override
	public String toString() {
		return getDatabase().getClass().getSimpleName() + '/' + getClass().getSimpleName();
	}
}
