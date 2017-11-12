// SQLServer.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.util.List;
import java.util.function.Supplier;

import org.lightsleep.Sql;

/**
 * <a href="https://www.microsoft.com/ja-jp/server-cloud/products-SQL-Server-2014.aspx" target="SQL Server">Microsoft SQL Server</a>
 * 用のデータベース･ハンドラです。
 *
 * <p>
 * このクラスのオブジェクトは、{@linkplain Standard#typeConverterMap}
 * に以下の<b>TypeConverter</b>オブジェクトを追加した<b>TypeConverter</b>マップを持ちます。
 * </p>
 *
 * <table class="additional">
 *   <caption><span>追加されるTypeConverterオブジェクト</span></caption>
 *   <tr><th>変換元データ型</th><th>変換先データ型</th><th>変換内容</th></tr>
 *   <tr><td>Boolean      </td><td rowspan="6">SqlString</td><td>false ➔ <code>0</code><br>true ➔ <code>1</code></td></tr>
 *   <tr><td>java.sql.Date</td><td><code>CAST('yyyy:MM:dd' AS DATE)</code></td></tr>
 *   <tr><td>Time         </td><td><code>CAST('HH:mm:ss' AS DATE)</code></td></tr>
 *   <tr><td>Timestamp    </td><td><code>CAST('yyyy-MM-dd HH:mm:ss.SSS' AS DATETIME2)</code></td></tr>
 *   <tr><td>String       </td><td><code>'...'</code><br>制御文字は<code>'...'+CHAR(n)+'...'</code>に変換<br>長い文字列の場合は<code>?</code><i>(SQLパラメータ)</i></td></tr>
 *   <tr><td>byte[]</td><td><code>?</code><i>(SQLパラメータ)</i></td></tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class SQLServer extends Standard {
	/**
	 * このクラスの唯一のインスタンス
	 *
	 * @since 2.1.0
	 */
	public static final SQLServer instance = new SQLServer();

	/**
	 * このクラスの唯一のインスタンスを返します。
	 *
	 * <p>
	 * @deprecated リリース 2.1.0 より。代わりに{@link #instance}を使用してください。
	 * </p>
	 *
	 * @return このクラスの唯一のインスタンス
	 */
// 2.1.0
	@Deprecated
////
	public static Database instance() {
		return null;
	}

	/**
	 * <b>SQLServer</b>を構築します。
	 */
	protected SQLServer() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String selectSql(Sql<E> sql, List<Object> parameters) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String subSelectSql(Sql<E> sql, Supplier<CharSequence> columnsSupplier, List<Object> parameters) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.8.4
	 */
	@Override
	public <E> String updateSql(Sql<E> sql, List<Object> parameters) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.8.2
	 */
	@Override
	protected <E> void appendForUpdate(StringBuilder buff, Sql<E> sql) {
	}
}
