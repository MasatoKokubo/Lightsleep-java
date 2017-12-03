// Jndi.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.util.Properties;
import java.util.function.Consumer;

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
// 2.1.0
//	private String dataSourceName;
////

	/**
	 * Constructs a new <b>Jndi</b>.
	 *
	 * <p>
	 * Use values specified in the lightsleep.properties file as the connection information.
	 * </p>
	 *
	 * @see #Jndi(java.lang.String)
	 */
	public Jndi() {
	// 2.1.0
		this(Resource.getGlobal().getProperties(), props -> {});
	////
	}

	/**
	 * Constructs a new <b>Jndi</b>.<br>
	 *
	 * <p>
	 * Looks up the data source uses the string of <b>"java:/comp/env/" + dataSourceName</b>.
	 * If <b>dataSourceName</b> is null,
	 * uses the value that have been specified in the lightsleep.properties file.
	 * </p>
	 *
	 * @param dataSourceName ther data source name (null permit)
	 */
	public Jndi(String dataSourceName) {
	// 2.1.0
	//	logger.debug(() -> "Jndi.<init>: dataSourceName=" + dataSourceName);
	//
	//	this.dataSourceName = dataSourceName;
		this(Resource.getGlobal().getProperties(), properties -> properties.put("dataSource", dataSourceName));
	////
	}

	/**
	 * Constructs a new <b>Jndi</b>.
	 *
	 * @param properties the properties with connection information
	 *
	 * @since 2.1.0
	 */
	public Jndi(Properties properties) {
		this(properties, props -> {});
	}

	/**
	 * Constructs a new <b>Jndi</b>.
	 *
	 * @param properties the properties with connection information
	 * @param modifier a consumer to modify the properties
	 *
	 * @since 2.1.0
	 */
	private Jndi(Properties properties, Consumer<Properties> modifier) {
		super(properties, modifier.andThen(props -> {
			// dataSource <- url
			String url = props.getProperty("url");
			String dataSource = props.getProperty("dataSource");
			if (url != null && dataSource == null) {
			// 2.1.1
			//	if (url.startsWith(":")) {
			//		int index = url.indexOf(':', 1);
			//		if (index >= 1)
			//			url = url.substring(index + 1);
			//	}
			////
				props.setProperty("dataSource", url);
				logger.info("Jndi.<init>: properties.dataSource <- \"" + url + '"');
			}
		}));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
// 2.1.0
//	protected DataSource getDataSource() {
	public DataSource getDataSource() {
		String lookupStr = "?";
////
		try {
		// 2.1.0
		//	if (dataSourceName == null) {
		//		// If the data source name is not specified, gets it from properties.
		//		dataSourceName = Resource.globalResource.getString("dataSource");
				String dataSourceName = jdbcProperties.getProperty("dataSource");
		////
				if (dataSourceName == null) {
					logger.error("Jndi.getDataSource: jdbcProperties dataSource: " + dataSourceName);
					return null;
				}
		// 2.1.0
		//	}
		////

		// 2.1.0
		//	logger.debug(() -> "Jndi.getDataSource: property dataSource: " + dataSourceName);
		////

			// Gets a new Context
			Context initContext = new InitialContext();

			// Creates a string for lookup
		// 2.1.0
		//	String lookupStr = "java:/comp/env/" + dataSourceName;
			lookupStr = dataSourceName.startsWith("jdbc/")
			// 2.1.1
			//	? "java:/comp/env/" + dataSourceName
			//	: "java:/comp/env/jdbc/" + dataSourceName;
				? "java:comp/env/" + dataSourceName
				: "java:comp/env/jdbc/" + dataSourceName;
			////
	////
			if (logger.isDebugEnabled())
				logger.debug("Jndi.lookup: \"" + lookupStr + '"');

			// Do lookup
			DataSource dataSource = (DataSource)initContext.lookup(lookupStr);
			return dataSource;
		}
		catch (NamingException e) {
			throw new RuntimeException("looked up string: \"" + lookupStr + '"', e);
		}
	}
}
