// ConnectionSupplier.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.lightsleep.database.Database;

/**
 * The interface of the supplier of the connection wrapeprs.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public interface ConnectionSupplier extends Supplier<ConnectionWrapper> {
	/**
	 * Returns the database handler related to this object.
	 *
	 * @return the database handler
	 *
	 * @since 2.1.0
	 */
	Database getDatabase();

	/**
	 * Returns the data source related to this object.
	 *
	 * @return the data source
	 *
	 * @since 2.1.0
	 */
	DataSource getDataSource();

	/**
	 * Returns the jdbc url related to this object.
	 *
	 * @return the jdbc url
	 *
	 * @since 2.1.0
	 */
	String getUrl();

	/**
	 * Create a <b>ConnectionSupplier</b>
	 *
	 * @param supplierName the class name of <b>ConnectionSupplier</b>
	 * @param properties the properties with connection information
	 * @return the created <b>ConnectionSupplier</b> object
	 *
	 * @throws RuntimeException if class can not be found or object can not be created
	 *
	 * @since 2.1.0
	 */
	@SuppressWarnings("unchecked")
	static ConnectionSupplier of(String supplierName, Properties properties) {
		Objects.requireNonNull(supplierName, "supplierName");

		if (supplierName.indexOf('.') < 0)
			supplierName = ConnectionSupplier.class.getPackage().getName() + '.' + supplierName;

		try {
			// Get a ConnectionSupplier class and instance
			return ((Class<? extends ConnectionSupplier>)Class.forName(supplierName))
				.getConstructor(Properties.class).newInstance(properties);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Finds the <b>ConnectionSupplier</b> object related to the url
	 * containing all the words of the specified <b>urlWords</b>.
	 *
	 * @param urlWords words in url
	 * @return the found <b>ConnectionSupplier</b> object
	 *
	 * @throws IllegalArgumentException if <b>ConnectionSupplier</b> object can not be found or multiple found
	 *
	 * @since 2.1.0
	 */
	static ConnectionSupplier find(String... urlWords) {
		Objects.requireNonNull(urlWords);

		List<Entry<String, ConnectionSupplier>> suppliers =
			AbstractConnectionSupplier.supplierMap.entrySet().stream()
				.filter(entry ->
					Arrays.stream(urlWords)
						.allMatch(urlWord -> entry.getKey().indexOf(urlWord) >= 0)
				)
				.collect(Collectors.toList());

		if (suppliers.isEmpty())
			// A url is not found.
			throw new IllegalArgumentException(
				MessageFormat.format(AbstractConnectionSupplier.messageUrlNotFound,
					Arrays.toString(urlWords)));

		if (suppliers.size() >= 2)
			// Multiple urls were found.
			throw new IllegalArgumentException(
				MessageFormat.format(AbstractConnectionSupplier.messageMultipleUrlsFound,
					suppliers.stream().map(Map.Entry::getKey).collect(Collectors.toList()),
					Arrays.toString(urlWords)));

		return suppliers.get(0).getValue();
	}
}
