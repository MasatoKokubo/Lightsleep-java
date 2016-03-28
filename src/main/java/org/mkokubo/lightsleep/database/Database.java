/*
	Database.java
	(C) 2015 Masato Kokubo
*/
package org.mkokubo.lightsleep.database;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.mkokubo.lightsleep.Sql;
import org.mkokubo.lightsleep.helper.TypeConverter;

/**
	An interface to generate SQLs.

	@since 1.0.0
	@author Masato Kokubo
*/
public interface Database {
	/**
		Returns whether the <b>OFFSET</b> of the specified is valid in the SELECT SQL.

		@return <b>true</b> if the <b>OFFSET</b> of the specified is valid, <b>false</b> otherwise
	*/
	default boolean isEnableOffset() {
		return false;
	}

	/**
		Creates and returns a SELECT SQL.

		@param <E> type of the entity

		@param sql a Sql object
		@param parameters a list to add the parameters of the SQL

		@return a SELECT SQL string
	*/
	<E> String selectSql(Sql<E> sql, List<Object> parameters);

	/**
		Creates and returns a SELECT SQL excluding
		<b>OFFSET</b>/<b>LIMIT</b>,
		<b>FOR UPDATE</b> and
		<b>ORDER BY</b>.

		@param <E> type of the entity

		@param sql a Sql object
		@param parameters a list to add the parameters of the SQL

		@return a SELECT SQL string
	*/
	<E> String subSelectSql(Sql<E> sql, List<Object> parameters);

	/**
		Creates and returns a SELECT SQL excluding
		<b>OFFSET</b>/<b>LIMIT</b>,
		<b>FOR UPDATE</b> and
		<b>ORDER BY</b>.

		@param <E> type of the entity

		@param sql a Sql object
		@param columnsSupplier a Supplier of the columns string
		@param parameters a list to add the parameters of the SQL

		@return a SELECT SQL string
	*/
	<E> String subSelectSql(Sql<E> sql, Supplier<CharSequence> columnsSupplier, List<Object> parameters);

	/**
		Creates and returns a INSERT SQL.

		@param <E> type of the entity

		@param sql a Sql object
		@param parameters a list to add the parameters of the SQL

		@return a INSERT SQL string
	*/
	<E> String insertSql(Sql<E> sql, List<Object> parameters);

	/**
		Creates and returns a UPDATE SQL.

		@param <E> type of the entity

		@param sql a Sql object
		@param parameters a list to add the parameters of the SQL

		@return a INSERT SQL string
	*/
	<E> String updateSql(Sql<E> sql, List<Object> parameters);

	/**
		Creates and returns a DELETE SQL.

		@param <E> type of the entity

		@param sql a Sql object
		@param parameters a list to add the parameters of the SQL

		@return a INSERT SQL string
	*/
	<E> String deleteSql(Sql<E> sql, List<Object> parameters);

	/**
		Returns the <b>TypeConverter</b> map.

		@return the <b>TypeConverter</b> map
	*/
	Map<String, TypeConverter<?, ?>> typeConverterMap();

	/**
		Converts the object to the specified type.

		@param <T> the destination type

		@param value an object to be converted
		@param type the class object of the destination type

		@return a converted object
	*/
	<T> T convert(Object value, Class<T> type);
}
