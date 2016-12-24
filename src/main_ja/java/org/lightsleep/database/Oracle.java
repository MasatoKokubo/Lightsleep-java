/*
	Oracle.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.database;

import java.util.List;

import org.lightsleep.Sql;

/**
	<a href="https://www.oracle.com/database/index.html" target="Oracle">Oracle Database</a>
	用のデータベース・ハンドラーです。<br>

	このクラスのオブジェクトは、
	{@linkplain org.lightsleep.helper.TypeConverter#typeConverterMap}
	に以下の <b>TypeConverter</b> を追加した <b>TypeConverter</b> マップを持ちます。

	<table class="additional">
		<caption><span>登録されている TypeConverter オブジェクト</span></caption>
		<tr><th>変換元データ型</th><th>変換先データ型</th></tr>
		<tr><td>boolean       </td><td>{@linkplain org.lightsleep.component.SqlString} (0, 1)</td></tr>
		<tr><td>String        </td><td rowspan="2">{@linkplain org.lightsleep.component.SqlString}</td></tr>
		<tr><td>Time          </td></tr>
		<tr><td rowspan="4">oracle.sql.TIMESTAMP</td><td>java.util.Date<br><i>(since 1.4.0)</i></td></tr>
		<tr>                                         <td>java.sql.Date     </td></tr>
		<tr>                                         <td>java.sql.Time     </td></tr>
		<tr>                                         <td>java.sql.Timestamp</td></tr>
	</table>

	@since 1.0.0
	@author Masato Kokubo
*/
public class Oracle extends Standard {
	/**
		<b>Oracle</b> オブジェクトを返します。

		@return Oracle オブジェクト
	*/
	public static Database instance() {
		return null;
	}

	/**
		<b>Oracle</b> を構築します。
	*/
	protected Oracle() {
	}
}
