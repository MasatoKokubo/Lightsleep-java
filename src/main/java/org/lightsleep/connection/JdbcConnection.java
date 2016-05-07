/*
	JdbcConnection.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.lightsleep.RuntimeSQLException;
import org.lightsleep.helper.Resource;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

/**
	<b>JdbcConnection</b> is used when you want to get <b>Connection</b> objects
	from <b>DriverManager</b> class.<br>
	That refer to the following properties of lightsleep.properties file.

	<div class="blankline">&nbsp;</div>

	<table class="additinal">
		<caption>References in lightsleep.properties</caption>
		<tr><th>Property Name</th><th>Content</th></tr>
		<tr><td>JdbcConnection.driver  </td><td>The class name of the JDBC driver</td></tr>
		<tr><td>JdbcConnection.url     </td><td>The URL of the database to be connected</td></tr>
		<tr><td>JdbcConnection.user    </td><td>The user name to use when connecting to a database</td></tr>
		<tr><td>JdbcConnection.password</td><td>The password when connecting to the database</td></tr>
		<tr>
			<td>JdbcConnection.<i>property name</i></td>
			<td>Other properties to be used to get a connection from <b>DriverManager</b> class</td>
		</tr>
	</table>

	@since 1.0.0
	@author Masato Kokubo
*/
public class JdbcConnection implements ConnectionSupplier {
	// The logger
	private static final Logger logger = LoggerFactory.getLogger(JdbcConnection.class);

	// The URL
	private final String url;

	// The properties
	private final Properties properties;

	/**
		Constructs a new <b>JdbcConnection</b>.
		Use values specified in the lightsleep.properties file as the connection information.

		@see #JdbcConnection(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	*/
	public JdbcConnection() {
		this(null, null, null, null, null);
	}

	/**
		Constructs a new <b>JdbcConnection</b>.
		Use values specified in the lightsleep.properties file as the connection information.

		@see #JdbcConnection(java.lang.String, java.lang.String, java.lang.String, java.lang.String)

		@param url2 the additional URL of the database to be connected
	*/
	public JdbcConnection(String url2) {
		this(null, null, url2, null, null);
	}

	/**
		Constructs a new <b>JdbcConnection</b>.<br>
		If <b>driver</b>, <b>url</b>, <b>user</b>, <b>password</b> is null,
		uses each of the value that have been specified in the lightsleep.properties file.<br>

		@param driver the class name of the JDBC driver
		@param url the URL of the database to be connected
		@param user the user name to use when connecting to a database
		@param password the password when connecting to the database
	*/
	public JdbcConnection(String driver, String url, String user, String password) {
		this(driver, url, null, user, password);
	}

	/**
		Constructs a new <b>JdbcConnection</b>.<br>
		If <b>driver</b>, <b>url</b>, <b>user</b>, <b>password</b> is null,
		uses each of the value that have been specified in the lightsleep.properties file.<br>
		If <b>url2</b> is not null, it is appended to <b>url</b>.

		@param driver the class name of the JDBC driver
		@param url the URL of the database to be connected
		@param url2 the additional URL
		@param user the user name to use when connecting to a database
		@param password the password when connecting to the database
	*/
	public JdbcConnection(String driver, String url, String url2, String user, String password) {
		if (logger.isInfoEnabled())
			logger.info(new StringBuilder()
				.append("JdbcConnection.<init>: argument driver: ").append(driver)
				.append(", url: ").append(url)
				.append(", url2: ").append(url2)
				.append(", user: ").append(user)
				.toString());

		properties = Resource.globalResource.getProperties("JdbcConnection");

		// driver
		if (driver == null) {
			driver = properties.getProperty("driver");
			if (logger.isInfoEnabled())
				logger.info("JdbcConnection.<init>: properties driver: " + driver);
		}
		properties.remove("driver");

		if (driver == null) {
			logger.fatal("JdbcConnection.<init>: property JdbcConnection.driver == null");
		} else {
			try {
				Class.forName(driver);
			}
			catch (Throwable e) {
				logger.fatal("JdbcConnection.<init>:", e);
			}
		}

		// url
		if (url == null)
			url = properties.getProperty("url");
		this.url = (url == null ? "" : url) + (url2 == null ? "" : url2);
		if (logger.isInfoEnabled())
			logger.info("JdbcConnection.<init>: url: " + this.url);
		properties.remove("url");

		// user
		if (user != null)
			properties.setProperty("user", user);

		// password
		if (password != null)
			properties.setProperty("password", password);

		if (logger.isInfoEnabled())
			logger.info("JdbcConnection.<init>: properties: " + properties);
	}

	/**
		{@inheritDoc}

		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database
	*/
	@Override
	public Connection get() {
		try {
			Connection connection = DriverManager.getConnection(url, properties);
			connection.setAutoCommit(false);
			return connection;
		}
		catch (SQLException e) {
			throw new RuntimeSQLException(e);
		}
	}
}
