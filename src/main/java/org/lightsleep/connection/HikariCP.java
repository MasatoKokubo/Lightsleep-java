// HikariCP.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.PropertyElf;

/**
 * Gets database connections using
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
	 * Use values specified in the lightsleep.properties file as the connection information.
	 */
	public HikariCP() {
	}

// 1.2.0
//	/**
//	 * Constructs a new <b>HikariCP</b>.<br>
//	 * Use values specified in the <i>&lt;<b>resourceName</b>&gt;</i>.properties file as the connection information.
//	 *
//	 * @param resourceName the resource name
//	 */
//	public HikariCP(String resourceName) {
//		super(resourceName);
//	}
////

	/**
	 * Constructs a new <b>HikariCP</b>.
	 * Use values specified in the lightsleep.properties file as the connection information.
	 *
	 * @param modifier a consumer to modify the properties
	 *
	 * @since 1.5.0
	 */
	public HikariCP(Consumer<Properties> modifier) {
		super(modifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSource getDataSource() {
	// 1.5.0
	//	logger.debug(() -> "HikariCP.getDataSource: properties: " + properties);
	////

		try {
			// Gets HikariCP properties to the properties2.
			Set<String> propertyNames = PropertyElf.getPropertyNames(HikariConfig.class);
			Properties properties2 = new Properties();
		// 1.5.1
		//	propertyNames.stream()
			propertyNames
		////
				.forEach(propertyName -> {
					if (properties.containsKey(propertyName))
						properties2.put(propertyName, properties.get(propertyName));
				});
			logger.debug(() -> "HikariCP.getDataSource: properties2: " + properties2);

			HikariConfig config = new HikariConfig(properties2);
			DataSource dataSource = new HikariDataSource(config);
			logger.debug(() -> "HikariCP.getDataSource: dataSource = " + dataSource);
			return dataSource;
		}
		catch (Exception e) {
		// 1.9.0
		//	logger.error("HikariCP.getDataSource:", e);
			logger.error("HikariCP.getDataSource: " + e, e);
		////
		}
		return null;
	}
}
