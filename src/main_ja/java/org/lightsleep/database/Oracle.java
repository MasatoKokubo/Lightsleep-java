// Oracle.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.util.List;

import org.lightsleep.Sql;

/**
 * <a href="https://www.oracle.com/database/index.html" target="Oracle">Oracle Database</a>
 * 用のデータベース･ハンドラです。
 *
 * <p>
 * このクラスのオブジェクトは、{@linkplain Standard#typeConverterMap}
 * に以下の<b>TypeConverter</b>オブジェクトを追加した<b>TypeConverter</b>マップを持ちます。
 * </p>
 *
 * <table class="additional">
 *   <caption><span>追加されるTypeConverter オブジェクト</span></caption>
 *   <tr><th>変換元データ型</th><th>変換先データ型</th><th>変換内容</th></tr>
 *   <tr><td>Boolean</td><td rowspan="3">SqlString</td><td>false ➔ <code>0</code><br>true ➔ <code>1</code></td></tr>
 *   <tr><td>Time   </td><td><code>TO_TIMESTAMP('1970-01-01 HH:mm:ss','YYYY-MM-DD HH24:MI:SS.FF3')</code></td></tr>
 *   <tr><td>byte[]</td><td><code>?</code><i>(SQLパラメータ)</i></td></tr>
 *   <tr><td rowspan="4">oracle.sql.TIMESTAMP</td><td>java.util.Date</td><td rowspan="4">値の取得時にSQLExceptionがスローされた場合ConvertExceptionをスロー</td></tr>
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
	 * このクラスの唯一のインスタンス
	 *
	 * @since 2.1.0
	 */
	public static final Oracle instance = new Oracle();

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
	 * <b>Oracle</b>を構築します。
	 */
	protected Oracle() {
	}
}
