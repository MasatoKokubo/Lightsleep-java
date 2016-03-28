/*
	JndiConnection.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.mkokubo.lightsleep.RuntimeSQLException;
import org.mkokubo.lightsleep.helper.Resource;
import org.mkokubo.lightsleep.logger.Logger;
import org.mkokubo.lightsleep.logger.LoggerFactory;

/**
	<b>JndiConnection</b> is used when you want to use the data source
	to get by JNDI (Java Naming and Directory Interface) API.<br>
	Refers to the following properties of lightsleep.properties file.

	<div class="BlankLine">&nbsp;</div>

	<table class="additinal">
		<caption>References in lightsleep.properties</caption>
		<tr><th>Property Name</th><th>Content</th></tr>
		<tr><td>JndiConnection.dataSource</td><td>The resource name of the data source</td></tr>
	</table>

	@since 1.0.0
	@author Masato Kokubo
*/
public class JndiConnection implements ConnectionSupplier {
	// The logger
	private static final Logger logger = LoggerFactory.getLogger(JndiConnection.class);

	// The data source name
	private String dataSourceName;

	//  THe data source
	private DataSource dataSource;

	/**
		Constructs a new <b>JndiConnection</b>.
		Use values specified in the lightsleep.properties file as the connection information.

		@see #JndiConnection(java.lang.String)
	*/
	public JndiConnection() {
		this(null);
	}

	/**
		Constructs a new <b>JndiConnection</b>.<br>

		Looks up the data source uses the string of <b>"java:/comp/env/" + dataSourceName</b>.
		If <b>dataSourceName</b> is <b>null</b>,
		uses the value that have been specified in the lightsleep.properties file.

		@param dataSourceName ther data source name (null permit)
	*/
	public JndiConnection(String dataSourceName) {
		logger.debug(() -> "JndiConnection.<init>: dataSourceName=" + dataSourceName);

		this.dataSourceName = dataSourceName;
		lookup();
	}

	/**
		Do lookup.
	*/
	private void lookup() {
		block: {
			try {
				if (dataSourceName == null) {
					// If the data source name is not specified, gets it from properties.
					dataSourceName = Resource.globalResource.get("JndiConnection.dataSource");
					if (dataSourceName == null) {
						logger.fatal("JndiConnection.lookup: property JndiConnection.dataSource: " + dataSourceName);
						break block;
					}
				}

				if (logger.isDebugEnabled())
					logger.debug(() -> "JndiConnection.lookup: property JndiConnection.dataSource: " + dataSourceName);

				// Gets a new Context
				Context initContext = new InitialContext();

				// Creates a string for lookup
				String lookupStr = "java:/comp/env/" + dataSourceName;
				logger.info(() -> "JndiConnection.lookup: lookup string=" + lookupStr);

				// Do lookup
				dataSource = (DataSource)initContext.lookup(lookupStr);
			}
			catch (NamingException e) {
				logger.fatal("JndiConnection.lookup: dataSourceName=" + dataSourceName, e);
				break block;
			}
		}
	}

	/**
		{@inheritDoc}

		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database
	*/
	@Override
	public Connection get() {
		try {
			if (dataSource == null)
				lookup();

			Connection connection = dataSource.getConnection();
			return connection;
		}
		catch (SQLException e) {
			throw new RuntimeSQLException(e);
		}
	}
}
