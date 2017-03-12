/*
	SqlComponent.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.component;

import java.util.List;

import org.lightsleep.Sql;

/**
	SQL の構成要素のインタフェースです。

	@since 1.0.0
	@author Masato Kokubo
*/
public interface SqlComponent {
	/**
		この構成要素が空かどうかを返します。

		@return 空なら <b>true</b>、そうでなければ <b>false</b>
	*/
	boolean isEmpty();

	/**
		このオブジェクトの SQL 文字列表現を返します。<br>
		文字列生成時にパラメータ文字 (<b>?</b>) を使用した場合は、
		パラメータ・オブジェクトをパラメータ・リストに追加します。

		@param <E> エンティティの型

		@param sql <b>Sql</b> オブジェクト
		@param parameters パラメータ・リスト

		@return SQL 文字列

		@throws NullPointerException <b>sql</b> または <b>parameters</b> が null の場合
	*/
	<E> String toString(Sql<E> sql, List<Object> parameters);
}
