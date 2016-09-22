/*
	C3p0.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import java.sql.SQLException;
import javax.sql.DataSource;

import com.mchange.v2.c3p0.DataSources;

/**
	Gets <b>Connection</b> objects using
	<a href="http://www.mchange.com/projects/c3p0/" target="c3p0">c3p0</a>.

	@since 1.1.0
	@author Masato Kokubo
*/
public class C3p0 extends AbstractConnectionSupplier {
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
		<i>&lt;<b>resourceName</b>&gt;</i>.properties and (c3p0.properties or c3p0-config.xml)
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
	protected DataSource getDataSource() {
		logger.debug(() -> "C3p0.getDataSource: properties: " + properties);

		// url
		String url = properties.getProperty("url");
		if (url == null)
			logger.error("C3p0.<init>: property url = null");

		try {
			DataSource unpooledDataSource = DataSources.unpooledDataSource(url, properties);
			logger.debug(() -> "C3p0.getDataSource: unpooledDataSource = " + unpooledDataSource);
			DataSource dataSource = DataSources.pooledDataSource(unpooledDataSource);
			logger.debug(() -> "C3p0.getDataSource: dataSource = " + dataSource);
			return dataSource;
		}
		catch (SQLException e) {
			logger.error("C3p0.<init>:", e);
		}
		return null;
	}
}
