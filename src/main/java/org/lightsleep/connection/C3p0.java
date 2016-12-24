/*
	C3p0.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Consumer;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.DataSources;

/**
	Gets database connections using
	<a href="http://www.mchange.com/projects/c3p0/" target="c3p0">c3p0 JDBC Connection Pool</a>.
	That refer to the following properties of lightsleep.properties file.

	<div class="blankline">&nbsp;</div>

	<table class="additional">
		<caption><span>References in lightsleep.properties</span></caption>
		<tr><th>Property Name</th><th>Content</th></tr>
		<tr><td>url     </td><td>The URL of the database to be connected</td></tr>
		<tr><td>user    </td><td>The user name to use when connecting to a database</td></tr>
		<tr><td>password</td><td>The password to use when connecting to the database</td></tr>
	</table>

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

// 1.2.0
//	/**
//		Constructs a new <b>C3p0</b>.<br>
//		Uses values specified in the lightsleep.properties and
//		<i>&lt;<b>resourceName</b>&gt;</i>.properties and (c3p0.properties or c3p0-config.xml)
//		file as the connection information.
//
//		@param resourceName additional resource name
//	*/
//	public C3p0(String resourceName) {
//		super(resourceName);
//	}
////
	/**
		Constructs a new <b>C3p0</b>.
		Uses values specified in the lightsleep.properties and
		(c3p0.properties or c3p0-config.xml)
		file as the setting information.

		@param modifier a consumer to modify the properties

		@since 1.5.0
	*/
	public C3p0(Consumer<Properties> modifier) {
		super(modifier);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	protected DataSource getDataSource() {
	// 1.5.0
	//	logger.debug(() -> "C3p0.getDataSource: properties: " + properties);
	////

		// url
		String url = properties.getProperty("url");
		if (url == null)
			logger.error("C3p0.<init>: property url == null");

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
