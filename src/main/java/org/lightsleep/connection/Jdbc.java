/*
	Jdbc.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import javax.sql.DataSource;

/**
	Gets <b>Connection</b> objects using the
	<b>DriverManager</b> class.<br>
	That refer to the following properties of lightsleep.properties file.

	<div class="blankline">&nbsp;</div>

	<table class="additinal">
		<caption>References in lightsleep.properties</caption>
		<tr><th>Property Name</th><th>Content</th></tr>
		<tr><td>driver  </td><td>The class name of the JDBC driver</td></tr>
		<tr><td>url     </td><td>The URL of the database to be connected</td></tr>
		<tr><td>user    </td><td>The user name to use when connecting to a database</td></tr>
		<tr><td>password</td><td>The password when connecting to the database</td></tr>
		<tr>
			<td><i>Other property names</i></td>
			<td>Other properties to be used to get a connection from <b>DriverManager</b> class</td>
		</tr>
	</table>

	@since 1.1.0
	@author Masato Kokubo
*/
public class Jdbc extends AbstractConnectionSupplier {
	// The URL
	private String url;

	/**
		Constructs a new <b>Jdbc</b>.
		Use values specified in the lightsleep.properties
		file as the connection information.
	*/
	public Jdbc() {
		init();
	}

	/**
		Constructs a new <b>Jdbc</b>.
		Use values specified in the lightsleep.properties and
		<i>&lt;<b>resourceName</b>&gt;</i>.properties
		file as the connection information.

		@param resourceName the resource name
	*/
	public Jdbc(String resourceName) {
		super(resourceName);
		init();
	}

	private void init() {
		logger.debug(() -> "Jdbc.<init>: properties: " + properties);

		// driver
		String driver = properties.getProperty("driver");

		if (driver == null) {
			logger.error("Jdbc.<init>: property driver = null");
		} else {
			try {
				Class.forName(driver);
			}
			catch (Exception e) {
				logger.error("Jdbc.<init>:", e);
			}
		}

		// url
		url = properties.getProperty("url");
	}

	/**
		{@inheritDoc}
	*/
	@Override
	protected DataSource getDataSource() {
		logger.debug(() -> "Jdbc.getDataSource: properties: " + properties);

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
