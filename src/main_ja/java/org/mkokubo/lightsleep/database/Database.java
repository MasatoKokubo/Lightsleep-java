/*
	Database.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.database;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.mkokubo.lightsleep.Sql;
import org.mkokubo.lightsleep.helper.TypeConverter;

/**
	SQL を生成するためのインタフェースです。

	@since 1.0.0
	@author Masato Kokubo
*/
public interface Database {
	/**
		SELECT SQL で <b>OFFSET</b> の指定が有効かどうかを返します。

		@return <b>OFFSET</b> の指定が有効なら <b>true</b>、そうでなければ <b>false</b>
	*/
	default boolean isEnableOffset() {
		return false;
	}

	/**
		SELECT SQL を作成して返します。

		@param <E> エンティティの型

		@param sql Sql オブジェクト
		@param parameters SQL のパラメータを格納するリスト

		@return SELECT SQL 文字列
	*/
	<E> String selectSql(Sql<E> sql, List<Object> parameters);

	/**
		<b>OFFSET</b>/<b>LIMIT</b>,
		<b>FOR UPDATE</b>,
		<b>ORDER BY</b>
		を除いた SELECT SQL を作成して返します。

		@param <E> エンティティの型

		@param sql Sql オブジェクト
		@param parameters SQL のパラメータを格納するリスト

		@return SELECT SQL 文字列
	*/
	<E> String subSelectSql(Sql<E> sql, List<Object> parameters);

	/**
		<b>OFFSET</b>/<b>LIMIT</b>,
		<b>FOR UPDATE</b>,
		<b>ORDER BY</b>
		を除いた SELECT SQL を作成して返します。

		@param <E> エンティティの型

		@param sql Sql オブジェクト
		@param columnsSupplier カラム列文字列のサプライヤー
		@param parameters SQL のパラメータを格納するリスト

		@return SELECT SQL 文字列
	*/
	<E> String subSelectSql(Sql<E> sql, Supplier<CharSequence> columnsSupplier, List<Object> parameters);

	/**
		INSERT SQL を作成して返します。

		@param <E> エンティティの型

		@param sql Sql オブジェクト
		@param parameters SQL のパラメータを格納するリスト

		@return INSERT SQL 文字列
	*/
	<E> String insertSql(Sql<E> sql, List<Object> parameters);

	/**
		UPDATE SQL を作成して返します。

		@param <E> エンティティの型

		@param sql Sql オブジェクト
		@param parameters SQL のパラメータを格納するリスト

		@return UPDATE SQL 文字列
	*/
	<E> String updateSql(Sql<E> sql, List<Object> parameters);

	/**
		DELETE SQL を作成して返します。

		@param <E> エンティティの型

		@param sql Sql オブジェクト
		@param parameters SQL のパラメータを格納するリスト

		@return DELETE SQL 文字列
	*/
	<E> String deleteSql(Sql<E> sql, List<Object> parameters);

	/**
		<b>TypeConverter</b> マップを返します。

		@return <b>TypeConverter</b> マップ
	*/
	Map<String, TypeConverter<?, ?>> typeConverterMap();

	/**
		オブジェクトを指定の型に変換します。

		@param <T> 変換先の型

		@param value 変換するオブジェクト
		@param type 変換先の型のクラスオブジェクト

		@return 変換されたオブジェクト
	*/
	<T> T convert(Object value, Class<T> type);
}
