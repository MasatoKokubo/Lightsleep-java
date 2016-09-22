/*
	HikariCP.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

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
	/**
		Constructs a new <b>HikariCP</b>.
		Use values specified in the lightsleep.properties file as the connection information.
	*/
	public HikariCP() {
	}

	/**
		Constructs a new <b>HikariCP</b>.<br>
		Use values specified in the <i>&lt;<b>resourceName</b>&gt;</i>.properties file as the connection information.

		@param resourceName the resource name
	*/
	public HikariCP(String resourceName) {
		super(resourceName);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	protected DataSource getDataSource() {
		logger.debug(() -> "HikariCP.getDataSource: properties: " + properties);

		try {
			// Gets HikariCP properties to the properties2.
			Set<String> propertyNames = PropertyElf.getPropertyNames(HikariConfig.class);
			Properties properties2 = new Properties();
			propertyNames.stream()
				.forEach(propertyName -> {
					if (properties.containsKey(propertyName))
						properties2.put(propertyName, properties.get(propertyName));
				});
			logger.debug(() -> "HikariCP.getDataSource: properties2: " + properties2);

			HikariConfig config = new HikariConfig(properties2);
			DataSource dataSource = new HikariDataSource(config);
			logger.debug(() -> "HikariCP.getDataSource: dataSource = " + dataSource);
			return dataSource;
		}
		catch (Exception e) {
			logger.error("HikariCP.getDataSource:", e);
		}
		return null;
	}
}
