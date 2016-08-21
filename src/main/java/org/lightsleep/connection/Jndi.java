/*
	Jndi.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.lightsleep.RuntimeSQLException;
import org.lightsleep.helper.Resource;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

/**
	Gets a data source using JNDI (Java Naming and Directory Interface) API.<br>
	Refers to the following properties of lightsleep.properties file.

	<div class="BlankLine">&nbsp;</div>

	<table class="additinal">
		<caption>References in lightsleep.properties</caption>
		<tr><th>Property Name</th><th>Content</th></tr>
		<tr><td>dataSource</td><td>The resource name of the data source</td></tr>
	</table>

	@since 1.1.0
	@author Masato Kokubo
*/
public class Jndi implements ConnectionSupplier {
	// The logger
	private static final Logger logger = LoggerFactory.getLogger(Jndi.class);

	// The data source name
	private String dataSourceName;

	//  THe data source
	private DataSource dataSource;

	/**
		Constructs a new <b>Jndi</b>.
		Use values specified in the lightsleep.properties file as the connection information.

		@see #Jndi(java.lang.String)
	*/
	public Jndi() {
		this(null);
	}

	/**
		Constructs a new <b>Jndi</b>.<br>

		Looks up the data source uses the string of <b>"java:/comp/env/" + dataSourceName</b>.
		If <b>dataSourceName</b> is <b>null</b>,
		uses the value that have been specified in the lightsleep.properties file.

		@param dataSourceName ther data source name (null permit)
	*/
	public Jndi(String dataSourceName) {
		logger.debug(() -> "Jndi.<init>: dataSourceName=" + dataSourceName);

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
					dataSourceName = Resource.globalResource.get("dataSource");
					if (dataSourceName == null) {
						logger.error("Jndi.lookup: property dataSource: " + dataSourceName);
						break block;
					}
				}

				logger.debug(() -> "Jndi.lookup: property dataSource: " + dataSourceName);

				// Gets a new Context
				Context initContext = new InitialContext();

				// Creates a string for lookup
				String lookupStr = "java:/comp/env/" + dataSourceName;
				logger.debug(() -> "Jndi.lookup: lookup string=" + lookupStr);

				// Do lookup
				dataSource = (DataSource)initContext.lookup(lookupStr);
			}
			catch (NamingException e) {
				logger.error("Jndi.lookup: dataSourceName=" + dataSourceName, e);
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
