/*
	Jdbc.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.lightsleep.RuntimeSQLException;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

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
	// The logger
	private static final Logger logger = LoggerFactory.getLogger(Jdbc.class);

	// The URL
	private String url;

	/**
		Constructs a new <b>Jdbc</b>.
		Use values specified in the lightsleep.properties
		file as the connection information.
	*/
	public Jdbc() {
	}

	/**
		Constructs a new <b>Jdbc</b>.
		Use values specified in the lightsleep.properties and
		<i>&lt<b>resourceName<b>&gt<i>.properties
		file as the connection information.

		@param resourceName the resource name
	*/
	public Jdbc(String resourceName) {
		super(resourceName);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	protected void init() {
		logger.debug(() -> "Jdbc.<init>: properties: " + properties);

		// driver
		String driver = properties.getProperty("driver");
		properties.remove("driver");

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
		properties.remove("url");
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
