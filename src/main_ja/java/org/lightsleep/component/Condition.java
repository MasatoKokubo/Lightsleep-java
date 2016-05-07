/*
	Condition.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.component;

import org.lightsleep.Sql;

/**
	条件インタフェースです。

	@since 1.0.0
	@author Masato Kokubo
*/
public interface Condition extends SqlComponent {
	/** 空の条件 */
	static final Condition EMPTY = new And();

	/** 全行を対象する条件 */
	static final Condition ALL = of("0 = 0");

	/**
		条件式を生成して返します。

		@see Expression

		@param content 条件式の文字列内容

		@return 条件式

		@throws NullPointerException <b>content</b> が <b>null</b> の場合
	*/
	static Condition of(String content) {
		return new Expression(content);
	}

	/**
		条件式を生成して返します。

		@see Expression

		@param content 条件式の文字列内容
		@param arguments 条件式に埋め込む引数配列

		@return 条件式

		@throws NullPointerException <b>content</b> または <b>arguments</b> が <b>null</b> の場合
	*/
	static Condition of(String content, Object... arguments) {
		return new Expression(content, arguments);
	}

	/**
		エンティティ条件を生成して返します。

		@see EntityCondition

		@param <E> エンティティ・クラス

		@param entity エンティティ

		@return エンティティ条件
	*/
	static <E> Condition of(E entity) {
		return null;
	}

	/**
		サブクエリ条件を生成して返します。

		@see SubqueryCondition

		@param <E> 外側のクエリの対象テーブルに対応するエンティティ・クラス
		@param <SE> サブクエリの対象テーブルに対応するエンティティ・クラス

		@param content サブクエリの SELECT 文より左部分の式の文字列内容
		@param outerSql 外側の Sql オブジェクト
		@param subSql サブクエリ用の Sql オブジェクト

		@return サブクエリ条件

		@throws NullPointerException <b>content</b>, <b>outerSql</b> または <b>subSql</b> が <b>null</b> の場合
	*/
	static <E, SE> Condition of(String content, Sql<E> outerSql, Sql<SE> subSql) {
		return null;
	}

	/**
		否定条件を返します。

		@see Not

		@return NOT(この条件)
	*/
	default Condition not() {
		return null;
	}

	/**
		(この条件 AND 指定の条件) を返します。

		@see And

		@param condition 条件

		@return この条件 AND 指定の条件

		@throws NullPointerException <b>condition</b> が <b>null</b> の場合
	*/
	default Condition and(Condition condition) {
		return null;
	}

	/**
		(この条件 AND 指定の条件) を返します。

		@see And
		@see Expression

		@param content 条件式の文字列内容
		@param arguments 条件式に埋め込む引数配列

		@return この条件 AND 指定の条件

		@throws NullPointerException <b>content</b> または <b>arguments</b> が <b>null</b> の場合
	*/
	default Condition and(String content, Object... arguments) {
		return null;
	}

	/**
		(この条件 AND 指定の条件) を返します。

		@see And
		@see SubqueryCondition

		@param <E> 外側のクエリの対象テーブルに対応するエンティティ・クラス
		@param <SE> サブクエリの対象テーブルに対応するエンティティ・クラス

		@param content サブクエリの SELECT 文より左部分の式の文字列内容
		@param outerSql 外側の Sql オブジェクト
		@param subSql サブクエリ用の Sql オブジェクト

		@return この条件 AND 指定の条件

		@throws NullPointerException <b>content</b>, <b>outerSql</b> または <b>subSql</b> が <b>null</b> の場合
	*/
	default <E, SE> Condition and(String content, Sql<E> outerSql, Sql<SE> subSql) {
		return null;
	}

	/**
		(この条件 OR 指定の条件) を返します。

		@see Or

		@param condition 条件

		@return この条件 OR 指定の条件

		@throws NullPointerException <b>condition</b> が <b>null</b> の場合
	*/
	default Condition or(Condition condition) {
		return null;
	}

	/**
		(この条件 OR 指定の条件) を返します。

		@see Or
		@see Expression

		@param content 条件式の文字列内容
		@param arguments 条件式に埋め込む引数配列

		@return この条件 OR 指定の条件

		@throws NullPointerException <b>content</b> または <b>arguments</b> が <b>null</b> の場合
	*/
	default Condition or(String content, Object... arguments) {
		return null;
	}

	/**
		(この条件 OR 指定の条件) を返します。

		@see Or
		@see SubqueryCondition

		@param <E> 外側のクエリの対象テーブルに対応するエンティティ・クラス
		@param <SE> サブクエリの対象テーブルに対応するエンティティ・クラス

		@param content サブクエリの SELECT 文より左部分の式の文字列内容
		@param outerSql 外側の Sql オブジェクト
		@param subSql サブクエリ用の Sql オブジェクト

		@return この条件 OR 指定の条件

		@throws NullPointerException <b>content</b>, <b>outerSql</b> または <b>subSql</b> が <b>null</b> の場合
	*/
	default <E, SE> Condition or(String content, Sql<E> outerSql, Sql<SE> subSql) {
		return null;
	}
}
