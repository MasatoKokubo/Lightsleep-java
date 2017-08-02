// Jdbc.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.function.Consumer;

import javax.sql.DataSource;

/**
 * Gets database connections using the
 * <b>DriverManager</b> class.<br>
 * That refer to the following properties of lightsleep.properties file.
 *
 * <div class="blankline">&nbsp;</div>
 *
 * <table class="additional">
 *   <caption><span>References in lightsleep.properties</span></caption>
 *   <tr><th>Property Name</th><th>Content</th></tr>
 *   <tr><td>url     </td><td>The URL of the database to be connected</td></tr>
 *   <tr><td>user    </td><td>The user name to use when connecting to a database</td></tr>
 *   <tr><td>password</td><td>The password to use when connecting to the database</td></tr>
 *   <tr>
 *     <td><i>Other property names</i></td>
 *     <td>Other properties to be used to get a connection from <b>DriverManager</b> class</td>
 *   </tr>
 * </table>

 * @since 1.1.0
 * @author Masato Kokubo
 */
public class Jdbc extends AbstractConnectionSupplier {
	/**
	 * Constructs a new <b>Jdbc</b>.
	 * Use values specified in the lightsleep.properties
	 * file as the connection information.
	 */
	public Jdbc() {
	}

	/**
	 * Constructs a new <b>Jdbc</b>.
	 * Use values specified in the lightsleep.properties
	 * file as the connection information.
	 *
	 * @param modifier a consumer to modify the properties
	 *
	 * @since 1.5.0
	 */
	public Jdbc(Consumer<Properties> modifier) {
		super(modifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSource getDataSource() {
		return new DataSource() {
			@Override
			public PrintWriter getLogWriter() throws SQLException {
				return null;
			}

			@Override
			public void setLogWriter(PrintWriter out) throws SQLException {
			}

			@Override
			public void setLoginTimeout(int seconds) throws SQLException {
			}

			@Override
			public int getLoginTimeout() throws SQLException {
				return 0;
			}

			@Override
			public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
				return null;
			}

			@Override
			public <T> T unwrap(Class<T> iface) throws SQLException {
				return null;
			}

			@Override
			public boolean isWrapperFor(Class<?> iface) throws SQLException {
				return false;
			}

			@Override
			public Connection getConnection() throws SQLException {
				String url = properties.getProperty("url");
				if (url == null)
					logger.error("Jdbc.<init>: property url == null");

				Connection connection = DriverManager.getConnection(url, properties);
				connection.setAutoCommit(false);
				return connection;
			}

			@Override
			public Connection getConnection(String username, String password) throws SQLException {
				return null;
			}
		};
	}
}
