/*
	Dbcp.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.lightsleep.RuntimeSQLException;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

/**
	Gets <b>Connection</b> objects using
	<a href="http://commons.apache.org/proper/commons-dbcp/" target="Apache">Apache Commons DBCP</a>.

	@since 1.1.0
	@author Masato Kokubo
*/
public class Dbcp extends AbstractConnectionSupplier {
	// The logger
	private static final Logger logger = LoggerFactory.getLogger(Dbcp.class);

	// The data source
	private DataSource dataSource;

	/**
		Constructs a new <b>Jdbc</b>.
		Use values specified in the lightsleep.properties
		file as the connection information.
	*/
	public Dbcp() {
	}

	/**
		Constructs a new <b>Dbcp</b>.<br>
		Use values specified in the lightsleep.properties and
		<i>&lt<b>resourceName<b>&gt<i>.properties
		file as the connection information.

		@param resourceName the resource name
	*/
	public Dbcp(String resourceName) {
		super(resourceName);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	protected void init() {
		logger.debug(() -> "Dbcp.<init>: properties: " + properties);

		try {
			dataSource = BasicDataSourceFactory.createDataSource(properties);
			logger.debug(() -> "Dbcp.<init>: dataSource = " + dataSource);
		}
		catch (Exception e) {
			logger.error("Dbcp.<init>:", e);
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
