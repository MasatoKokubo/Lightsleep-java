// SQLServer.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.util.List;
import java.util.function.Supplier;

import org.lightsleep.Sql;

/**
 * <a href="https://www.microsoft.com/ja-jp/server-cloud/products-SQL-Server-2014.aspx" target="SQL Server">Microsoft SQL Server</a>
 * 用のデータベース・ハンドラーです。<br>
 * 
 * このクラスのオブジェクトは、
 * {@linkplain Standard#typeConverterMap}.
 * に以下の <b>TypeConverter</b> を追加した <b>TypeConverter</b> マップを持ちます。
 * 
 * <table class="additional">
 *   <caption><span>登録されている TypeConverter オブジェクト</span></caption>
 *   <tr><th>変換元データ型</th><th>変換先データ型</th><th>変換フォーマット</th></tr>
 *   <tr><td>boolean      </td><td rowspan="6">{@linkplain org.lightsleep.component.SqlString}</td><td>0 か 1</td></tr>
 *   <tr><td>java.sql.Date</td><td>CAST('yyyy:MM:dd' AS DATE)</td></tr>
 *   <tr><td>Time         </td><td>CAST('HH:mm:ss' AS DATE)</td></tr>
 *   <tr><td>Timestamp    </td><td>CAST('yyyy-MM-dd HH:mm:ss.SSS' AS DATETIME2)</td></tr>
 *   <tr><td>String       </td><td>長い場合は <i>SQL パラメータ (?)</i>、そうでなければ '...' (...'+CHAR(n)+'... を含む場合あり)</td></tr>
 *   <tr><td>byte[]<br><i>(since 1.7.0)</i></td><td>常に <i>SQL パラメータ (?)</i></td></tr>
 * </table>
 * 
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
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
