// Jndi.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.lightsleep.helper.Resource;

/**
 * Gets a data source using JNDI (Java Naming and Directory Interface) API.<br>
 * Refers to the following properties of lightsleep.properties file.
 *
 * <div class="BlankLine">&nbsp;</div>
 *
 * <table class="additional">
 *   <caption><span>References in lightsleep.properties</span></caption>
 *   <tr><th>Property Name</th><th>Content</th></tr>
 *   <tr><td>dataSource</td><td>The resource name of the data source</td></tr>
 * </table>

 * @since 1.1.0
 * @author Masato Kokubo
 */
public class Jndi extends AbstractConnectionSupplier {
	// The data source name
	private String dataSourceName;

	/**
	 * Constructs a new <b>Jndi</b>.
	 * Use values specified in the lightsleep.properties file as the connection information.
	 *
	 * @see #Jndi(java.lang.String)
	 */
	public Jndi() {
	}

	/**
	 * Constructs a new <b>Jndi</b>.<br>
	 *
	 * Looks up the data source uses the string of <b>"java:/comp/env/" + dataSourceName</b>.
	 * If <b>dataSourceName</b> is <b>null</b>,
	 * uses the value that have been specified in the lightsleep.properties file.
	 *
	 * @param dataSourceName ther data source name (null permit)
	 */
	public Jndi(String dataSourceName) {
		logger.debug(() -> "Jndi.<init>: dataSourceName=" + dataSourceName);

		this.dataSourceName = dataSourceName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSource getDataSource() {
		try {
			if (dataSourceName == null) {
				// If the data source name is not specified, gets it from properties.
				dataSourceName = Resource.globalResource.get("dataSource");
				if (dataSourceName == null) {
					logger.error("Jndi.getDataSource: property dataSource: " + dataSourceName);
					return null;
				}
			}

			logger.debug(() -> "Jndi.getDataSource: property dataSource: " + dataSourceName);

			// Gets a new Context
			Context initContext = new InitialContext();

			// Creates a string for lookup
			String lookupStr = "java:/comp/env/" + dataSourceName;
			logger.debug(() -> "Jndi.lookup: lookup string=" + lookupStr);

			// Do lookup
			DataSource dataSource = (DataSource)initContext.lookup(lookupStr);
			return dataSource;
		}
		catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}
}
