// AbstractConnectionSupplier.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
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
	private static final Resource resource = new Resource(AbstractConnectionSupplier.class);
	protected static final String messageUrlNotFound       = resource.getString("messageUrlNotFound"); // Used in ConnectionSupplier
	protected static final String messageMultipleUrlsFound = resource.getString("messageMultipleUrlsFound"); // Used in ConnectionSupplier
	private static final String messageMultipleUrlsDefined = resource.getString("messageMultipleUrlsDefined");

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
							logger.info("ConnectionSupplier.initClass: url: \"" + url + '"');
						}
						ConnectionSupplier beforeUrl = supplierMap.put(url, supplier);
						if (beforeUrl != null)
							logger.warn(MessageFormat.format(messageMultipleUrlsDefined, url));
					}
					catch (Exception e) {
						logger.error("ConnectionSupplier.initClass: url: \"" + url + "\", exception: " + e);
					}
				});
		}
		catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/** The properties */
	protected Properties jdbcProperties;

	// The data source
	private DataSource dataSource;

	// The database handler. @since 2.1.0
	private Database database = Standard.instance;

	/**
	 * Constructs a new <b>AbstractConnectionSupplier</b>.
	 *
	 * @param properties the properties with connection information
	 * @param modifier a consumer to modify properties
	 *
	 * @since 2.1.0
	 */
	protected AbstractConnectionSupplier(Properties properties, Consumer<Properties> modifier) {
		jdbcProperties = Objects.requireNonNull(properties);
		jdbcProperties.remove(Logger.class.getSimpleName());
		jdbcProperties.remove(Database.class.getSimpleName());
		jdbcProperties.remove(ConnectionSupplier.class.getSimpleName());
		Objects.requireNonNull(modifier, "modifier").accept(jdbcProperties);

		logger.debug(() -> getClass().getSimpleName() + ".<init>: jdbcProperties: " + jdbcProperties);

		if (!(this instanceof Jndi)) {
			// not Jndi
			String url = jdbcProperties.getProperty("url");
			try {
				database = Database.getInstance(url);
			}
			catch (IllegalArgumentException e) {
				logger.warn(e.toString());
			}
			logger.info(() -> getClass().getSimpleName()
				+ ".<init>: url: \"" + url + "\", database handler: " + database.getClass().getSimpleName());
		}
	}

	/**
	 * Returns a connection wrapper.
	 *
	 * @return a connection wrapper
	 *
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown
	 */
	@Override
	public ConnectionWrapper get() {
		try {
			boolean first = false;
			if (dataSource == null) {
				synchronized (this) {
					if (dataSource == null) {
						dataSource = getDataSource();
						first = true;
					}
				}
			}

			Connection connection = dataSource.getConnection();
			if (first) {
				// first time and Jndi
				DatabaseMetaData metaData = connection.getMetaData();

				if (this instanceof Jndi) {
					String url = metaData.getURL();
					if (url != null) {
						try {
							database = Database.getInstance(url);
						}
						catch (IllegalArgumentException e) {
							logger.warn(e.toString());
						}
						logger.info(() -> getClass().getSimpleName()
							+ ".get: connection.metaData.url: \"" + url + "\", database handler: " + database.getClass().getSimpleName());
					} else {
						logger.warn(() -> getClass().getSimpleName() + ".get: connection.metaData.url: null");
					}
				}

				if (logger.isInfoEnabled())
					logger.info("DBMS: " + metaData.getDatabaseProductName() + ' ' + metaData.getDatabaseProductVersion());
			}

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

			return new ConnectionWrapper(connection, database);
		}
		catch (SQLException e) {
			throw new RuntimeSQLException(getUrl(), e);
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
		return jdbcProperties.getProperty("url");
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
