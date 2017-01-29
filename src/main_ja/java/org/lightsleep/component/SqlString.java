/*
	SqlString.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.component;

/**
	このクラスは、値を <b>TypeConverter</b> クラスを使用して SQL 文字列に変換する際に使用します。

	@since 1.0.0
	@author Masato Kokubo
*/
public class SqlString {
//	/** SQL のパラメーター文字列 */
	public static final String PARAMETER = "?";

	/**
		SqlString を構築します。

		@param content 文字列
	*/
	public SqlString(String content) {
	}

	/**
		内容を返します。

		@return 内容
	*/
	public String content() {
		return null;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public String toString() {
		return null;
	}
}
