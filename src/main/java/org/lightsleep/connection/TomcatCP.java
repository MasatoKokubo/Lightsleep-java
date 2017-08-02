// TomcatCP.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.util.Properties;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.DataSourceFactory;

/**
 * Gets database connections using
 * <a href="http://people.apache.org/~fhanik/jdbc-pool/jdbc-pool.html" target="Apache">Tomcat JDBC Connection Pool</a>.<br>
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
 *       <a href="http://people.apache.org/~fhanik/jdbc-pool/jdbc-pool.html" target="Apache">
 *         Other properties of Tomcat JDBC Connection Pool
 *       </a>
 *     </td>
 *   </tr>
 * </table>

 * @since 1.1.0
 * @author Masato Kokubo
 */
public class TomcatCP extends AbstractConnectionSupplier {
	/**
	 * Constructs a new <b>TomcatCP</b>.
	 * Use values specified in the lightsleep.properties
	 * file as the connection information.
	 */
	public TomcatCP() {
	}

	/**
	 * Constructs a new <b>TomcatCP</b>.
	 * Use values specified in the lightsleep.properties
	 * file as the connection information.
	 *
	 * @param modifier a consumer to modify the properties
	 *
	 * @since 1.5.0
	 */
	public TomcatCP(Consumer<Properties> modifier) {
		super(modifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSource getDataSource() {
		try {
			DataSource dataSource = new DataSourceFactory().createDataSource(properties);
			logger.debug(() -> "TomcatCP.getDataSource: dataSource = " + dataSource);
			return dataSource;
		}
		catch (Exception e) {
			logger.error("TomcatCP.getDataSource: " + e, e);
		}
		return null;
	}
}
