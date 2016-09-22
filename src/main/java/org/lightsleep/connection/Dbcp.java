/*
	Dbcp.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

/**
	Gets <b>Connection</b> objects using
	<a href="http://commons.apache.org/proper/commons-dbcp/" target="Apache">Apache Commons DBCP</a>.

	@since 1.1.0
	@author Masato Kokubo
*/
public class Dbcp extends AbstractConnectionSupplier {
	/**
		Constructs a new <b>Dbcp</b>.
		Use values specified in the lightsleep.properties
		file as the connection information.
	*/
	public Dbcp() {
	}

	/**
		Constructs a new <b>Dbcp</b>.<br>
		Use values specified in the lightsleep.properties and
		<i>&lt;<b>resourceName</b>&gt;</i>.properties
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
	protected DataSource getDataSource() {
		logger.debug(() -> "Dbcp.getDataSource: properties: " + properties);

		try {
			DataSource dataSource = BasicDataSourceFactory.createDataSource(properties);
			logger.debug(() -> "Dbcp.getDataSource: dataSource = " + dataSource);
			return dataSource;
		}
		catch (Exception e) {
			logger.error("Dbcp.getDataSource:", e);
		}
		return null;
	}
}
