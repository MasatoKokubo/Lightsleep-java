// C3p0.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.lightsleep.helper.Resource;

import com.mchange.v2.c3p0.DataSources;

/**
 * Gets connection wrappers using
 * <a href="http://www.mchange.com/projects/c3p0/" target="c3p0">c3p0 JDBC Connection Pool</a>.
 * That refer to the following properties of lightsleep.properties file.
 *
 * <div class="blankline">&nbsp;</div>
 *
 * <table class="additional">
 *   <caption><span>References in lightsleep.properties</span></caption>
 *   <tr><th>Property Name</th><th>Content</th></tr>
 *   <tr><td>url     </td><td>The URL of the database to be connected</td></tr>
 *   <tr><td>user    </td><td>The user name to use when connecting to a database</td></tr>
 *   <tr><td>password</td><td>The password to use when connecting to the database</td></tr>
 * </table>
 *
 * @since 1.1.0
 * @author Masato Kokubo
 */
public class C3p0 extends AbstractConnectionSupplier {
	/**
	 * Constructs a new <b>C3p0</b>.
	 *
	 * <p>
	 * Uses values specified in the lightsleep.properties and
	 * (c3p0.properties or c3p0-config.xml)
	 * file as the setting information.
	 * </p>
	 */
	public C3p0() {
		super(Resource.getGlobal().getProperties(), props -> {});
	}

	/**
	 * Constructs a new <b>C3p0</b>.
	 *
	 * <p>
	 * Uses values specified in the lightsleep.properties and
	 * (c3p0.properties or c3p0-config.xml)
	 * file as the setting information.
	 * </p>
	 *
	 * @param modifier a consumer to modify the properties
	 *
	 * @since 1.5.0
	 */
	public C3p0(Consumer<Properties> modifier) {
		super(Resource.getGlobal().getProperties(), modifier);
	}

	/**
	 * Constructs a new <b>C3p0</b>.
	 *
	 * @param properties the properties with connection information
	 *
	 * @since 2.1.0
	 */
	public C3p0(Properties properties) {
		super(properties, props -> {});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataSource getDataSource() {
		try {
			DataSource unpooledDataSource = DataSources.unpooledDataSource(jdbcProperties.getProperty("url"), jdbcProperties);
			DataSource dataSource = DataSources.pooledDataSource(unpooledDataSource);
			return dataSource;
		}
		catch (SQLException e) {
			throw new RuntimeException("jdbcProperties: " + jdbcProperties, e);
		}
	}
}
