/*
	SqlEntityInfo.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.helper;

import java.util.Collection;
import java.util.stream.Stream;

/**
	テーブル別名、エンティティ情報とエンティティを持つインターフェースです。<br>

	@param <E> エンティティの型

	@since 1.0

	@author Masato Kokubo
*/
public interface SqlEntityInfo<E> {
	/**
		テーブル別名を返します。

		@return テーブル別名
	*/
	String tableAlias();

	/**
		エンティティ情報を返します。

		@return エンティティ情報
	*/
	EntityInfo<E> entityInfo();

	/**
		<b>Expression</b> クラスで参照されるエンティティを返します。

		@return エンティティ
	*/
	E entity();

	/**
		エンティティ情報が持っているすべてのカラム情報から作成される
		<b>SqlColumnInfo</b> のストリームを返します。

		@return SqlColumnInfo のストリーム
	*/
	default Stream<SqlColumnInfo> sqlColumnInfoStream() {
		return null;
	}

	/**
		指定された名前コレクションのいずれかにマッチするカラム情報から作成される
		<b>SqlColumnInfo</b> のストリームを返します。

		@param names 名前コレクション

		@return SqlColumnInfo のストリーム
	*/
	default Stream<SqlColumnInfo> selectedSqlColumnInfoStream(Collection<String> names) {
		return null;
	}
}
