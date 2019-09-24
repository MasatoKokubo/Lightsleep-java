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

	/**
	 * {@value}
	 * @since 2.2.0
	 */
	protected static final String URL = "url";

	/**
	 * {@value}
	 * @since 2.2.0
	 */
	protected static final String URLS = "urls";

	/**
	 * {@value}
	 * @since 2.2.0
	 */
	protected static final String JDBC_URL = "jdbcUrl";

	/**
	 * {@value}
	 * @since 2.2.0
	 */
	protected static final String DATA_SOURCE = "dataSource";

	/**
	 * {@value}
	 * @since 2.2.0
	 */
	protected static final String DATA_SOURCES = "dataSources";

	/**
	 * {@value}
	 * @since 2.2.0
	 */
	protected static final String USER = "user";

	/**
	 * {@value}
	 * @since 2.2.0
	 */
	protected static final String USERNAME = "username";

	/**
	 * The format string of conections
	 *
	 * <p>
	 * {0}: the simple class name of the database handler
	 * {1}: the simple class name of the connection supplier
	 * {2}: the jdbc URL of the connection
	 * </p>
	 *
	 * @since 2.2.0
	 */
	private static final String connectionLogFormat = Resource.getGlobal().getString("connectionLogFormat", "[{0}/{1}]");

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
			String urlsStr = properties.getProperty(URLS);
			if (urlsStr != null) {
				properties.remove(URLS);
			} else {
				// dataSources for Jndi
				urlsStr = properties.getProperty(DATA_SOURCES);
				if (urlsStr != null) {
					properties.remove(DATA_SOURCES);
				} else {
					// url
					urlStr = properties.getProperty(URL);
					if (urlStr != null) {
						properties.remove(URL);
					} else {
						// dataSource for Jndi
						urlStr = properties.getProperty(DATA_SOURCE);
						if (urlStr != null)
							properties.remove(DATA_SOURCE);
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
								supplierProperties.put(URL, url.substring(braIndex + 1).trim());
								supplier = ConnectionSupplier.of(url.substring(1, braIndex).trim(), supplierProperties);
							}
						}
						if (supplier == null) {
							supplierProperties.put(URL, url);
							supplier = ConnectionSupplier.of(supplierName, supplierProperties);
							logger.info("AbstractConnectionSupplier.initClass: url: \"" + supplier.getDatabase().maskPassword(url) + '"');
						}
						ConnectionSupplier beforeSupplier = supplierMap.put(url, supplier);
						if (beforeSupplier != null)
							logger.warn(MessageFormat.format(messageMultipleUrlsDefined, supplier.getDatabase().maskPassword(url)));
					}
					catch (Exception e) {
						logger.error("AbstractConnectionSupplier.initClass: url: \"" + url + '"', e);
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
		jdbcProperties = Objects.requireNonNull(properties, "properties is null");
		jdbcProperties.remove(Logger.class.getSimpleName());
		jdbcProperties.remove(Database.class.getSimpleName());
		jdbcProperties.remove(ConnectionSupplier.class.getSimpleName());
		Objects.requireNonNull(modifier, "modifier is null").accept(jdbcProperties);

		logger.debug(() -> getClass().getSimpleName() + ".<init>: jdbcProperties: " + jdbcProperties);

		if (!(this instanceof Jndi)) {
			// not Jndi
			String url = jdbcProperties.getProperty(URL);
			if (url == null)
				url = jdbcProperties.getProperty(DATA_SOURCE);
			try {
				database = Database.getInstance(url);
			}
			catch (IllegalArgumentException e) {
				logger.warn(e.toString());
			}
			if (logger.isInfoEnabled())
				logger.info(getClass().getSimpleName()
					+ ".<init>: url: \"" + database.maskPassword(url)
					+ "\", database handler: " + database.getClass().getSimpleName());
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
							+ ".get: connection.metaData.url: \"" + getDatabase().maskPassword(url)
							+ "\", database handler: " + database.getClass().getSimpleName());
					} else {
						logger.warn(() -> getClass().getSimpleName() + ".get: connection.metaData.url: null");
					}
				}

				if (logger.isInfoEnabled())
					logger.info("DBMS: "
						+ metaData.getDatabaseProductName() + ' ' + metaData.getDatabaseProductVersion());
			}

			boolean beforeAutoCommit = connection.getAutoCommit();
			int transactionIsolation = connection.getTransactionIsolation();
			connection.setAutoCommit(false);
			boolean afterAutoCommit = connection.getAutoCommit();

			logger.debug(() ->
				getClass().getSimpleName()
				+ ".get: connection.autoCommit: " + beforeAutoCommit + " -> " + afterAutoCommit
				+ ", connection.transactionIsolation: " + isolationLevelsMap.getOrDefault(transactionIsolation, "unknow")
			);

			return new ConnectionWrapper(connection, this);
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
		return jdbcProperties.getProperty(URL);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 2.1.0
	 */
	@Override
	public String toString() {
		String url = "";
		if (connectionLogFormat.indexOf("{2}") >= 0) {
			// has the parameter of the jdbc URL
			url = getUrl();
			if (url == null) {
				url = jdbcProperties.getProperty(DATA_SOURCE);
				if (url == null)
					url = "";
			} else {
				if (url.startsWith("jdbc:"))
					url = url.substring(5);
				url = getDatabase().maskPassword(url);
			}
		}
		return MessageFormat.format(connectionLogFormat,
			getDatabase().getClass().getSimpleName(), getClass().getSimpleName(), url);
	}
}
