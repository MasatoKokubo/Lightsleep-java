// HikariCP.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.lightsleep.helper.Resource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.PropertyElf;

/**
 * Gets connection wrappers using
 * <a href="http://brettwooldridge.github.io/HikariCP/" target="HikariCP">HikariCP JDBC Connection Pool</a>.
 * That refer to the following properties of lightsleep.properties file.
 *
 * <div class="blankline">&nbsp;</div>
 *
 * <table class="additional">
 *   <caption><span>References in lightsleep.properties</span></caption>
 *   <tr><th>Property Name</th><th>Content</th></tr>
 *   <tr><td>jdbcUrl </td><td>The URL of the database to be connected</td></tr>
 *   <tr><td>username</td><td>The user name to use when connecting to a database</td></tr>
 *   <tr><td>password</td><td>The password to use when connecting to the database</td></tr>
 *   <tr>
 *     <td><i>Other property names</i></td>
 *     <td>
 *       <a href="https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby" target="HikariCP">
 *         Other properties of HikariCP
 *       </a>
 *     </td>
 *   </tr>
 * </table>

 * @since 1.1.0
 * @author Masato Kokubo
 */
public class HikariCP extends AbstractConnectionSupplier {
	/**
	 * Constructs a new <b>HikariCP</b>.
	 *
	 * <p>
	 * Uses values specified in the lightsleep.properties file as the connection information.
	 * </p>
	 */
	public HikariCP() {
		this(Resource.getGlobal().getProperties(), props -> {});
	}

	/**
	 * Constructs a new <b>HikariCP</b>.
	 *
	 * <p>
	 * Uses values specified in the lightsleep.properties file as the connection information.
	 * </p>
	 *
	 * @param modifier a consumer to modify the properties
	 *
	 * @since 1.5.0
	 */
	public HikariCP(Consumer<Properties> modifier) {
		this(Resource.getGlobal().getProperties(), modifier);
	}

	/**
	 * Constructs a new <b>HikariCP</b>.
	 *
	 * @param properties the properties with connection information
	 *
	 * @since 2.1.0
	 */
	public HikariCP(Properties properties) {
		this(properties, props -> {});
	}

	/**
	 * Constructs a new <b>HikariCP</b>.
	 *
	 * @param properties the properties with connection information
	 * @param modifier a consumer to modify the properties
	 *
	 * @since 2.1.0
	 */
	private HikariCP(Properties properties, Consumer<Properties> modifier) {
		super(properties, modifier.andThen(props -> {
			// jdbcUrl <- url
		// 2.2.0
		//	String url = props.getProperty("url");
		//	String jdbcUrl = props.getProperty("jdbcUrl");
			String url = props.getProperty(URL);
			String jdbcUrl = props.getProperty(JDBC_URL);
		////
			if (url != null && jdbcUrl == null) {
			// 2.2.0
			//	props.setProperty("jdbcUrl", url);
			//	logger.info("HikariCP.<init>: properties.jdbcUrl <- properties.url: \"" + url + '"');
				props.setProperty(JDBC_URL, url);
				logger.info("HikariCP.<init>: properties.jdbcUrl <- properties.url");
			////
			} else if (url == null && jdbcUrl != null) {
			// 2.2.0
			//	props.setProperty("url", jdbcUrl);
			//	logger.info("HikariCP.<init>: properties.url <- properties.jdbcUrl: \"" + jdbcUrl + '"');
				props.setProperty(URL, jdbcUrl);
				logger.info("HikariCP.<init>: properties.url <- properties.jdbcUrl");
			////
			}

			// username <- user
			String user = props.getProperty("user");
			String username = props.getProperty("username");
			if (user != null && username == null) {
				props.setProperty("username", user);
				logger.info("HikariCP.<init>: properties.username <- properties.user: \"" + user + '"');
			}
		}));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataSource getDataSource() {
		Properties properties = new Properties();
		try {
			// Gets HikariCP properties to the properties2.
			Set<String> propertyNames = PropertyElf.getPropertyNames(HikariConfig.class);
			propertyNames
				.forEach(propertyName -> {
					if (jdbcProperties.containsKey(propertyName))
						properties.put(propertyName, jdbcProperties.get(propertyName));
				});
			logger.debug(() -> "HikariCP.getDataSource: properties: " + properties);

			HikariConfig config = new HikariConfig(properties);
			DataSource dataSource = new HikariDataSource(config);
			return dataSource;
		}
		catch (RuntimeException e) {throw e;}
		catch (Exception e) {
			throw new RuntimeException("properties: " + properties, e);
		}
	}
}
