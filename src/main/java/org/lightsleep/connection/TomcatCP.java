/*
	TomcatCP.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.DataSourceFactory;

/**
	Gets <b>Connection</b> objects using
	<a href="http://people.apache.org/~fhanik/jdbc-pool/jdbc-pool.html" target="Apache">Tomcat JDBC Connection Pool</a>.<br>

	@since 1.1.0
	@author Masato Kokubo
*/
public class TomcatCP extends AbstractConnectionSupplier {
	/**
		Constructs a new <b>TomcatCP</b>.
		Use values specified in the lightsleep.properties
		file as the connection information.
	*/
	public TomcatCP() {
	}

	/**
		Constructs a new <b>TomcatCP</b>.<br>
		Use values specified in the lightsleep.properties and
		<i>&lt;<b>resourceName</b>&gt;</i>.properties
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
	protected DataSource getDataSource() {
		logger.debug(() -> "TomcatCP.getDataSource: properties: " + properties);

		try {
			DataSource dataSource = new DataSourceFactory().createDataSource(properties);
			logger.debug(() -> "TomcatCP.getDataSource: dataSource = " + dataSource);
			return dataSource;
		}
		catch (Exception e) {
			logger.error("TomcatCP.getDataSource:", e);
		}
		return null;
	}
}
