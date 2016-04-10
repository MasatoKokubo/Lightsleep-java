/*
	PostgreSQL.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.database;

/**
	<a href="http://www.postgresql.org/" target="PostgreSQL">PostgreSQL</a>
	用のデータベース・ハンドラーです。<br>

	このクラスのオブジェクトは、
	{@linkplain org.mkokubo.lightsleep.helper.TypeConverter#typeConverterMap}
	の内容に加え以下の <b>TypeConverter</b> を追加した <b>TypeConverter</b> マップを持ちます。

	<table class="additinal">
		<caption>登録されている TypeConverter オブジェクト</caption>
		<tr><th>変換元データ型   </th><th>変換先データ型</th></tr>
		<tr><td>boolean</td><td>{@linkplain org.mkokubo.lightsleep.component.SqlString} (FALSE, TRUE)</td></tr>
		<tr><td>String </td><td>{@linkplain org.mkokubo.lightsleep.component.SqlString} (エスケープ・シーケンス対応)</td></tr>
	</table>

	@since 1.0.0
	@author Masato Kokubo
*/
public class PostgreSQL extends Standard {
	/**
		<b>PostgreSQL</b> オブジェクトを返します。

		@return PostgreSQL オブジェクト
	*/
	public static Database instance() {
		return null;
	}

	/**
		<b>PostgreSQL</b> を構築します。
	*/
	protected PostgreSQL() {
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean supportsOffsetLimit() {
		return true;
	}
}
