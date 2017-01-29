// Oracle.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.util.List;

import org.lightsleep.Sql;

/**
 * <a href="https://www.oracle.com/database/index.html" target="Oracle">Oracle Database</a>
 * 用のデータベース・ハンドラーです。<br>
 *
 * このクラスのオブジェクトは、
 * {@linkplain Standard#typeConverterMap}.
 * に以下の <b>TypeConverter</b> を追加した <b>TypeConverter</b> マップを持ちます。
 *
 * <table class="additional">
 *   <caption><span>登録されている TypeConverter オブジェクト</span></caption>
 *   <tr><th>変換元データ型</th><th>変換先データ型</th><th>変換フォーマット</th></tr>
 *   <tr><td>boolean</td><td rowspan="4">{@linkplain org.lightsleep.component.SqlString}</td><td>0 か 1</td></tr>
 *   <tr><td>String </td><td>長い場合は <i>SQL パラメータ (?)</i>、そうでなければ '...' (...'||CHR(n)||'... を含む場合あり)</td></tr>
 *   <tr><td>Time   </td><td>TO_TIMESTAMP('1970-01-01 HH:mm:ss','YYYY-MM-DD HH24:MI:SS.FF3')</td></tr>
 *   <tr><td>byte[]<br><i>(since 1.7.0)</i></td><td>常に <i>SQL パラメータ (?)</i></td></tr>
 *   <tr><td rowspan="4">oracle.sql.TIMESTAMP</td><td>java.util.Date<br><i>(since 1.4.0)</i></td><td rowspan="4"></td></tr>
 *   <tr>                                         <td>java.sql.Date     </td></tr>
 *   <tr>                                         <td>java.sql.Time     </td></tr>
 *   <tr>                                         <td>java.sql.Timestamp</td></tr>
 * </table>
 * 
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
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
