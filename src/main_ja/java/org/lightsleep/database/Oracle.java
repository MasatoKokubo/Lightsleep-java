// Oracle.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.sql.Connection;
import java.sql.ResultSet;

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
 *   <caption><span>TypeConverterマップへの追加内容</span></caption>
 *   <tr><th colspan="2">キー: データ型</th><th rowspan="2">値: 変換関数</th></tr>
 *   <tr><th>変換元</th><th>変換先</th></tr>
 *
 *   <tr><td>Boolean</td><td rowspan="2">SqlString</td>
 *     <td>
 *       <b>new SqlString("0")</b> <span class="comment">変換元の値が<b>false</b>の場合</span><br>
 *       <b>new SqlString("1")</b> <span class="comment">変換元の値が<b>true</b>の場合</span>
 *     </td>
 *   </tr>
 *   <tr><td>byte[]</td><td><b>new SqlString(SqlString.PARAMETER, source)</b></td></tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class Oracle extends Standard {
	/**
	 * パスワードのパターン文字列
	 *
	 * @since 2.2.0
	 */
	protected static final String PASSWORD_PATTERN = "";

	/**
	 * このクラスの唯一のインスタンス
	 *
	 * @since 2.1.0
	 */
	public static final Oracle instance = new Oracle();

	/**
	 * <b>Oracle</b>を構築します。
	 */
	protected Oracle() {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 2.2.0
	 */
	@Override
	public String maskPassword(String jdbcUrl) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 3.0.0
	 */
	@Override
	public Object getObject(Connection connection, ResultSet resultSet, String columnLabel) {
		return null;
	}
}
