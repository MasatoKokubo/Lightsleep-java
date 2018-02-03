// Database.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.lightsleep.Sql;
import org.lightsleep.helper.TypeConverter;

/**
 * An interface to generate SQLs.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public interface Database {
	/**
	 * Returns whether support <b>OFFSET</b> and <b>LIMIT</b> in the SELECT SQL.
	 *
	 * @return <b>true</b> if support <b>OFFSET</b> and <b>LIMIT</b>, <b>false</b> otherwise
	 */
	default boolean supportsOffsetLimit() {
		return false;
	}

	/**
	 * Creates and returns a SELECT SQL.
	 *
	 * @param <E> the type of the entity
	 * @param sql a <b>Sql</b> object
	 * @param parameters a list to add the parameters of the SQL
	 * @return a SELECT SQL string
	 *
	 * @throws IllegalStateException if SELECT SQL without columns was generated
	 */
	 <E> String selectSql(Sql<E> sql, List<Object> parameters);

	/**
	 * Creates and returns a SELECT SQL excluding
	 * <b>OFFSET</b>/<b>LIMIT</b>,
	 * <b>FOR UPDATE</b> and
	 * <b>ORDER BY</b>.
	 *
	 * @param <E> the type of the entity
	 * @param sql a <b>Sql</b> object
	 * @param parameters a list to add the parameters of the SQL
	 * @return a SELECT SQL string
	 *
	 * @throws IllegalStateException if SELECT SQL without columns was generated
	 */
	 <E> String subSelectSql(Sql<E> sql, List<Object> parameters);

	/**
	 * Creates and returns a SELECT SQL excluding
	 * <b>OFFSET</b>/<b>LIMIT</b>,
	 * <b>FOR UPDATE</b> and
	 * <b>ORDER BY</b>.
	 *
	 * @param <E> the type of the entity
	 * @param sql a <b>Sql</b> object
	 * @param columnsSupplier a Supplier of the columns string
	 * @param parameters a list to add the parameters of the SQL
	 * @return a SELECT SQL string
	 */
	 <E> String subSelectSql(Sql<E> sql, Supplier<CharSequence> columnsSupplier, List<Object> parameters);

	/**
	 * Creates and returns a INSERT SQL.
	 *
	 * @param <E> the type of the entity
	 * @param sql a <b>Sql</b> object
	 * @param parameters a list to add the parameters of the SQL
	 * @return a INSERT SQL string
	 */
	 <E> String insertSql(Sql<E> sql, List<Object> parameters);

	/**
	 * Creates and returns a UPDATE SQL.
	 *
	 * @param <E> the type of the entity
	 * @param sql a <b>Sql</b> object
	 * @param parameters a list to add the parameters of the SQL
	 * @return a INSERT SQL string
	 */
	 <E> String updateSql(Sql<E> sql, List<Object> parameters);

	/**
	 * Creates and returns a DELETE SQL.
	 *
	 * @param <E> the type of the entity
	 * @param sql a <b>Sql</b> object
	 * @param parameters a list to add the parameters of the SQL
	 * @return a INSERT SQL string
	 */
	 <E> String deleteSql(Sql<E> sql, List<Object> parameters);

	/**
	 * Returns the <b>TypeConverter</b> map.
	 *
	 * @return the <b>TypeConverter</b> map
	 */
	Map<String, TypeConverter<?, ?>> typeConverterMap();

	/**
	 * Converts the object to the specified type.
	 *
	 * @param <T> the destination type
	 * @param value an object to be converted
	 * @param type the class object of the destination type
	 * @return a converted object
	 */
	 <T> T convert(Object value, Class<T> type);

	/**
	 * Masks ths password of the JDBC URL.
	 *
	 * @param jdbcUrl a JDBC URL
	 * @return the JDBC URL masked the password
	 *
	 * @since 2.2.0
	 */
	String maskPassword(String jdbcUrl);

	/**
	 * Returns a database handler related to <b>jdbcUrl</b>.
	 *
	 * @param jdbcUrl a JDBC URL
	 * @return the database handler related to the JDBC URL
	 *
	 * @throws IllegalArgumentException if <b>jdbcUrl</b> does not contain a string that can identify a <b>Database</b> class
	 *
	 * @since 2.1.0
	 */
	@SuppressWarnings("unchecked")
	static Database getInstance(String jdbcUrl) {
		Objects.requireNonNull(jdbcUrl, "jdbcUrl");
		String[] words = jdbcUrl.split(":");
		for (String word : words) {
			if (word.equals("jdbc")) continue;
			if (!word.matches("[a-z0-9]+")) continue;

			Class<? extends Database> anchorClass;
			try {
				String anchorClassName = Database.class.getPackage().getName() + ".anchor." + word;
				anchorClass = (Class<? extends Database>)Class.forName(anchorClassName);
			} catch (ClassNotFoundException e) {
				continue;
			}

			try {
				Class<? extends Database> databaseClass = (Class<? extends Database>)anchorClass.getSuperclass();
				Database database = (Database)databaseClass.getField("instance").get(null);
				return database;

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		throw new IllegalArgumentException(jdbcUrl);
	}
}
