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
		this(Resource.getGlobal().getProperties(), props -> {});
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
		this(Resource.getGlobal().getProperties(), properties -> properties.put("dataSource", dataSourceName));
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
			String url = props.getProperty(URL);
			String dataSource = props.getProperty(DATA_SOURCE);
			if (url != null && dataSource == null) {
				props.setProperty(DATA_SOURCE, url);
				logger.info("Jndi.<init>: properties.dataSource <- \"" + url + '"');
			}
		}));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataSource getDataSource() {
		String lookupStr = "?";
		try {
				String dataSourceName = jdbcProperties.getProperty("dataSource");
				if (dataSourceName == null) {
					logger.error("Jndi.getDataSource: jdbcProperties dataSource: " + dataSourceName);
					return null;
				}

			// Gets a new Context
			Context initContext = new InitialContext();

			// Creates a string for lookup
			lookupStr = dataSourceName.startsWith("jdbc/")
				? "java:comp/env/" + dataSourceName
				: "java:comp/env/jdbc/" + dataSourceName;

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
