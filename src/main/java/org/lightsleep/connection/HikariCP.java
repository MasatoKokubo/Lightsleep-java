/*
	HikariCP.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.lightsleep.RuntimeSQLException;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.PropertyElf;

/**
	Gets <b>Connection</b> objects using
	<a href="http://brettwooldridge.github.io/HikariCP/" target="Apache">HikariCP JDBC Connection Pool</a>.

	@since 1.1.0
	@author Masato Kokubo
*/
public class HikariCP extends AbstractConnectionSupplier {
	// The logger
	private static final Logger logger = LoggerFactory.getLogger(HikariCP.class);

	// The data source
	private DataSource dataSource;

	/**
		Constructs a new <b>Jdbc</b>.
		Use values specified in the lightsleep.properties file as the connection information.
	*/
	public HikariCP() {
	}

	/**
		Constructs a new <b>HikariCP</b>.<br>
		Use values specified in the <i>&lt<b>resourceName<b>&gt<i>.properties file as the connection information.

		@param resourceName the resource name
	*/
	public HikariCP(String resourceName) {
		super(resourceName);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	protected void init() {
		logger.debug(() -> "HikariCP.<init>: properties: " + properties);

		try {
			// Gets HikariCP properties to the properties2.
			Set<String> propertyNames = PropertyElf.getPropertyNames(HikariConfig.class);
			Properties properties2 = new Properties();
			propertyNames.stream()
				.forEach(propertyName -> {
					if (properties.containsKey(propertyName))
						properties2.put(propertyName, properties.get(propertyName));
				});
			logger.debug(() -> "HikariCP.<init>: properties2: " + properties2);

			HikariConfig config = new HikariConfig(properties2);
			dataSource = new HikariDataSource(config);
			logger.debug(() -> "HikariCP.<init>: dataSource = " + dataSource);
		}
		catch (Exception e) {
			logger.error("HikariCP.<init>:", e);
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
