// PostgreSQL.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * <a href="http://www.postgresql.org/" target="PostgreSQL">PostgreSQL</a>
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
 *   <tr><td>String </td><td rowspan="2">SqlString</td>
 *     <td>
 *       <b>new SqlString("'" + source + "'")</b><br>
 *       <span class="comment">変換元の文字列中のシングルクォートは、連続する2個のシングルクォートに変換、<br>
 *       また制御文字はエスケープシーケンスに変換 ( \b, \t, \n, \f, \r, \\ )</span><br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>new SqlString("E'" + source + "'")</b> <span class="comment">変換後の文字列がエスケープシーケンスを含む場合</span><br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>new SqlString(SqlString.PARAMETER, source)</b> <span class="comment">変換元の文字列が長すぎる場合</span>
 *     </td>
 *   </tr>
 *   <tr><td>byte[] </td>
 *     <td>
 *       <b>new SqlString("E'\\x" + hexadecimal string + "'")</b><br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>new SqlString(SqlString.PARAMETER, source)</b> <span class="comment">変換元のバイト配列が長すぎる場合</span>
 *     </td>
 *   </tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class PostgreSQL extends Standard {
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
	public static final PostgreSQL instance = new PostgreSQL();

	/**
	 * <b>PostgreSQL</b>を構築します。
	 */
	protected PostgreSQL() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportsOffsetLimit() {
		return true;
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
