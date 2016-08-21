/*
	C3p0.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.lightsleep.RuntimeSQLException;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

import com.mchange.v2.c3p0.DataSources;

/**
	Gets <b>Connection</b> objects using
	<a href="http://www.mchange.com/projects/c3p0/" target="c3p0">c3p0</a>.

	@since 1.1.0
	@author Masato Kokubo
*/
public class C3p0 extends AbstractConnectionSupplier {
	// The logger
	private static final Logger logger = LoggerFactory.getLogger(C3p0.class);

	// The data source
	private DataSource dataSource;

	/**
		Constructs a new <b>C3p0</b>.
		Uses values specified in the lightsleep.properties and
		(c3p0.properties or c3p0-config.xml)
		file as the setting information.
	*/
	public C3p0() {
	}

	/**
		Constructs a new <b>C3p0</b>.<br>
		Uses values specified in the lightsleep.properties and
		<i>&lt<b>resourceName<b>&gt<i>.properties and (c3p0.properties or c3p0-config.xml)
		file as the connection information.

		@param resourceName additional resource name
	*/
	public C3p0(String resourceName) {
		super(resourceName);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	protected void init() {
		logger.debug(() -> "C3p0.<init>: properties: " + properties);

		// url
		String url = properties.getProperty("url");
		if (url == null)
			logger.error("C3p0.<init>: property url = null");
		properties.remove("url");

		try {
			DataSource unpooledDataSource = DataSources.unpooledDataSource(url, properties);
			logger.debug(() -> "C3p0.<init>: unpooledDataSource = " + unpooledDataSource);
			dataSource = DataSources.pooledDataSource(unpooledDataSource);
			logger.debug(() -> "C3p0.<init>: dataSource = " + dataSource);
		}
		catch (SQLException e) {
			logger.error("C3p0.<init>:", e);
		}
	}

	/**
		{@inheritDoc}

		@throws RuntimeSQLException if a <b>SQLException</b> is thrown
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
