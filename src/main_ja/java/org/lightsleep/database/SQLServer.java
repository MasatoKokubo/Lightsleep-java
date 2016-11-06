/*
	SQLServer.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.database;

import java.util.List;
import java.util.function.Supplier;

import org.lightsleep.Sql;

/**
	<a href="https://www.microsoft.com/ja-jp/server-cloud/products-SQL-Server-2014.aspx" target="SQL Server">Microsoft SQL Server</a>
	用のデータベース・ハンドラーです。<br>

	このクラスのオブジェクトは、
	{@linkplain org.lightsleep.helper.TypeConverter#typeConverterMap}
	に以下の <b>TypeConverter</b> を追加した <b>TypeConverter</b> マップを持ちます。

	<table class="additinal">
		<caption><span>登録されている TypeConverter オブジェクト</span></caption>
		<tr><th>変換元データ型   </th><th>変換先データ型</th></tr>
		<tr><td>boolean</td><td>{@linkplain org.lightsleep.component.SqlString} (0, 1)</td></tr>
		<tr><td>String </td><td>{@linkplain org.lightsleep.component.SqlString}</td></tr>
	</table>

	@since 1.0.0
	@author Masato Kokubo
*/
public class SQLServer extends Standard {
	/**
		<b>SQLServer</b> オブジェクトを返します。

		@return SQLServer オブジェクト
	*/
	public static Database instance() {
		return null;
	}

	/**
		<b>SQLServer</b> を構築します。
	*/
	protected SQLServer() {
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public <E> String selectSql(Sql<E> sql, List<Object> parameters) {
		return null;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public <E> String subSelectSql(Sql<E> sql, Supplier<CharSequence> columnsSupplier, List<Object> parameters) {
		return null;
	}
}
