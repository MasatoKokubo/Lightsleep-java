// SQLite.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

/**
 * <a href="https://www.sqlite.org/index.html" target="SQLite">SQLite</a>
 * 用のデータベース･ハンドラです。<br>
 *
 * スーパークラスで追加された<b>TypeConverter</b>オブジェクトに以下を追加するか置き換えます。<br>
 * <br>
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
 *       <span class="comment">変換元の文字列中のシングルクォートは、連続する2個のシングルクォートに変換</span><br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>new SqlString(SqlString.PARAMETER, source)</b> <span class="comment">変換元の文字列が長すぎる場合</span>
 *     </td>
 *   </tr>
 *   <tr><td>java.util.Date</td>
 *     <td rowspan="10">
 *       (<b>java.util.Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>LocalDate</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Time</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>LocalTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Timestamp</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>LocalDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>OffsetDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>ZonedDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Instant</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b>) <img src="../../../../images/arrow-right.gif" alt="->"> <b>new SqlString("'" + string + "'")</b>
 *     </td>
 *   </tr>
 *   <tr><td>Date          </td></tr>
 *   <tr><td>LocalDate     </td></tr>
 *   <tr><td>Time          </td></tr>
 *   <tr><td>LocalTime     </td></tr>
 *   <tr><td>Timestamp     </td></tr>
 *   <tr><td>LocalDateTime </td></tr>
 *   <tr><td>OffsetDateTime</td></tr>
 *   <tr><td>ZonedDateTime </td></tr>
 *   <tr><td>Instant       </td></tr>
 *   <tr><td>byte[]</td><td><b>new SqlString(SqlString.PARAMETER, source)</b></td></tr>
 * </table>
 *
 * @since 1.7.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class SQLite extends Standard {
	/**
	 * このクラスの唯一のインスタンス
	 *
	 * @since 2.1.0
	 */
	public static final SQLite instance = new SQLite();

	/**
	 * <b>SQLite</b>を構築します。
	 */
	protected SQLite() {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.8.2
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
}
