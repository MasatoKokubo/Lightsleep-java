/*
	TomcatCP.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.DataSourceFactory;
import org.lightsleep.RuntimeSQLException;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

/**
	Gets <b>Connection</b> objects using
	<a href="http://people.apache.org/~fhanik/jdbc-pool/jdbc-pool.html" target="Apache">Tomcat JDBC Connection Pool</a>.<br>

	@since 1.1.0
	@author Masato Kokubo
*/
public class TomcatCP extends AbstractConnectionSupplier {
	// The logger
	private static final Logger logger = LoggerFactory.getLogger(TomcatCP.class);

	// The data source
	private DataSource dataSource;

	/**
		Constructs a new <b>Jdbc</b>.
		Use values specified in the lightsleep.properties
		file as the connection information.
	*/
	public TomcatCP() {
	}

	/**
		Constructs a new <b>TomcatCP</b>.<br>
		Use values specified in the lightsleep.properties and
		<i>&lt<b>resourceName<b>&gt<i>.properties
		file as the connection information.

		@param resourceName the resource name
	*/
	public TomcatCP(String resourceName) {
		super(resourceName);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	protected void init() {
		logger.debug(() -> "TomcatCP.<init>: properties: " + properties);

		try {
			dataSource = new DataSourceFactory().createDataSource(properties);
			logger.debug(() -> "TomcatCP.<init>: dataSource = " + dataSource);
		}
		catch (Exception e) {
			logger.error("TomcatCP.<init>:", e);
		}
	}

	/**
		{@inheritDoc}

		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database
	*/
	@Override
	public Connection get() {
		try {
			Connection connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			return connection;
		}
		catch (SQLException e) {
			throw new RuntimeSQLException(e);
		}
	}
}
