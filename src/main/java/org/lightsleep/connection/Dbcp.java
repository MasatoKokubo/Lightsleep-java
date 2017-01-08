// Dbcp.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.util.Properties;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

/**
 * Gets database connections using
 * <a href="http://commons.apache.org/proper/commons-dbcp/" target="Apache">Apache Commons DBCP 2</a>.
 * That refer to the following properties of lightsleep.properties file.
 *
 * <div class="blankline">&nbsp;</div>
 *
 * <table class="additional">
 *   <caption><span>References in lightsleep.properties</span></caption>
 *   <tr><th>Property Name</th><th>Content</th></tr>
 *   <tr><td>url     </td><td>The URL of the database to be connected</td></tr>
 *   <tr><td>username</td><td>The user name to use when connecting to a database</td></tr>
 *   <tr><td>password</td><td>The password to use when connecting to the database</td></tr>
 *   <tr>
 *     <td><i>Other property names</i></td>
 *     <td>
 *       <a href="http://commons.apache.org/proper/commons-dbcp/configuration.html" target="Apache">
 *         Other properties of DBCP 2
 *       </a>
 *     </td>
 *   </tr>
 * </table>
 *
 * @since 1.1.0
 * @author Masato Kokubo
 */
public class Dbcp extends AbstractConnectionSupplier {
	/**
	 * Constructs a new <b>Dbcp</b>.
	 * Use values specified in the lightsleep.properties
	 * file as the connection information.
	 */
	public Dbcp() {
	}

// 1.2.0
//	/**
//	 * Constructs a new <b>Dbcp</b>.<br>
//	 * Use values specified in the lightsleep.properties and
//	 * <i>&lt;<b>resourceName</b>&gt;</i>.properties
//	 * file as the connection information.
//	 *
//	 * @param resourceName the resource name
//	 */
//	public Dbcp(String resourceName) {
//		super(resourceName);
//	}
////

	/**
	 * Constructs a new <b>Dbcp</b>.
	 * Use values specified in the lightsleep.properties
	 * file as the connection information.
	 *
	 * @param modifier a consumer to modify the properties
	 *
	 * @since 1.5.0
	 */
	public Dbcp(Consumer<Properties> modifier) {
		super(modifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSource getDataSource() {
	// 1.5.0
	//	logger.debug(() -> "Dbcp.getDataSource: properties: " + properties);
	////

		try {
			DataSource dataSource = BasicDataSourceFactory.createDataSource(properties);
			logger.debug(() -> "Dbcp.getDataSource: dataSource = " + dataSource);
			return dataSource;
		}
		catch (Exception e) {
			logger.error("Dbcp.getDataSource:", e);
		}
		return null;
	}
}
