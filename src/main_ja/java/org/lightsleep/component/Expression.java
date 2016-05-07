/*
	Expression.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.component;

import java.util.List;

import org.lightsleep.Sql;

/**
	文字列と文字列中に埋め込む引数オブジェクトの配列で式を構成します。
	このクラスのオブジェクトは、条件としても使用できます。

	@since 1.0.0
	@author Masato Kokubo
*/
public class Expression implements Condition {
	/** 空の式 */
	public static final Expression empty = null;

	/** デフォルト値の式 */
	public static final Expression DEFAULT = new Expression("DEFAULT");

	/**
		Expression を構築します。

		@param content 式の文字列内容
		@param arguments 式に埋め込む引数配列

		@throws NullPointerException <b>content</b> または <b>arguments</b> が <b>null</b> の場合
	*/
	public Expression(String content, Object... arguments) {
	}

	/**
		式の文字列内容を返します。

		@return 式の文字列内容
	*/
	public String content() {
		return null;
	}

	/**
		式に埋め込む引数配列を返します。

		@return 式に埋め込む引数配列
	*/
	public Object[] arguments() {
		return null;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isEmpty() {
		return false;
	}

	/**
		{@inheritDoc}

		<table class="additinal">
			<caption>変換処理</caption>
			<tr>
				<th>変換前文字列</th>
				<th>変換後文字列</th>
			</tr>
			<tr>
				<td>{}</td>
				<td>コンストラクタの引数の <b>arguments</b> の要素</td>
			</tr>
			<tr>
				<td>{<i>プロパティ名</i>}</td>
				<td>カラム名</td>
			</tr>
			<tr>
				<td>{<i>テーブル別名</i>.<i>プロパティ名</i>}</td>
				<td>テーブル別名.カラム名</td>
			</tr>
			<tr>
				<td>{<i>テーブル別名</i>_<i>プロパティ名</i>}</td>
				<td>カラム別名</td>
			</tr>
			<tr>
				<td>{#<i>プロパティ名</i>}</td>
				<td>エンティティのプロパティ値</td>
			</tr>
		</table>
	*/
	@Override
	public <E> String toString(Sql<E> sql, List<Object> parameters) {
		return null;
	}
}
