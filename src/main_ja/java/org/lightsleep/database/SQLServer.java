// SQLServer.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.sql.Connection;
import java.sql.ResultSet;
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
 *   <caption><span>TypeConverterマップへの追加内容</span></caption>
 *   <tr><th colspan="2">キー: データ型</th><th rowspan="2">値: 変換関数</th></tr>
 *   <tr><th>変換元</th><th>変換先</th></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="13">SqlString</td>
 *     <td>
 *       <b>new SqlString("0")</b> <span class="comment">変換元の値が<b>false</b>の場合</span><br>
 *       <b>new SqlString("1")</b> <span class="comment">変換元の値が<b>true</b>の場合</span>
 *     </td>
 *   </tr>
 *   <tr><td>String        </td>
 *     <td>
 *       <b>new SqlString("'" + source + "'")</b><br>
 *       <span class="comment">変換元の文字列中のシングルクォートは、連続する2個のシングルクォートに変換、<br>
 *       また制御文字は</span> <b>'...'+CHAR(文字コード)+'...'</b><span class="comment"> に変換</span><br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>new SqlString(SqlString.PARAMETER, source)</b> <span class="comment">変換元の文字列が長すぎる場合</span>
 *     </td>
 *   </tr>
 *   <tr><td>java.util.Date</td>
 *     <td rowspan="3">
 *       (<b>java.util.Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>LocalDate</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b>) <img src="../../../../images/arrow-right.gif" alt="->"> <b>new SqlString("CAST('" + string + "' AS DATE)")</b>
 *     </td>
 *   </tr>
 *   <tr><td>Date          </td>
 *   <tr><td>LocalDate     </td></tr>
 *   <tr><td>Time          </td>
 *     <td>
 *       <b>Time</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> <img src="../../../../images/arrow-right.gif" alt="->"><br>
 *       <b>new SqlString("CAST('" + string + "' AS TIME)")</b>
 *     </td>
 *   </tr>
 *   <tr><td>LocalTime     </td>
 *     <td>
 *       <b>LocalTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> <img src="../../../../images/arrow-right.gif" alt="->"><br>
 *       <b>new SqlString("CAST('" + string + "' AS TIME)")</b>
 *     </td>
 *   </tr>
 *   <tr><td>Timestamp     </td>
 *     <td rowspan="2">
 *       (<b>Timestamp</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>LocalDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b>) <img src="../../../../images/arrow-right.gif" alt="->"> <b>new SqlString("CAST('" + string + "' AS DATETIME2)")</b>
 *     </td>
 *   </tr>
 *   <tr><td>LocalDateTime </td></tr>
 *   <tr><td>OffsetDateTime</td>
 *     <td rowspan="3">
 *       (<b>OffsetDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>ZonedDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Instant</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b>) <img src="../../../../images/arrow-right.gif" alt="->"> <b>new SqlString("CAST('" + string + "' AS DATETIMEOFFSET)")</b>
 *     </td>
 *   </tr>
 *   <tr><td>ZonedDateTime </td></tr>
 *   <tr><td>Instant       </td></tr>
 *   <tr><td>byte[]</td><td><b>new SqlString(SqlString.PARAMETER, source)</b></td></tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class SQLServer extends Standard {
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
	public static final SQLServer instance = new SQLServer();

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
