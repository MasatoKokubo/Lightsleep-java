// Standard.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.lightsleep.component.SqlString;
import org.lightsleep.helper.TypeConverter;
import org.lightsleep.Sql;

/**
 * 特定のDBMSに依存しないデータベース･ハンドラです。
 *
 * <p>
 * {@linkplain org.lightsleep.helper.TypeConverter}クラスが持つ
 * <b>TypeConverter</b>オブジェクトおよび以下の変換を行う
 * <b>TypeConverter</b>オブジェクトを持ちます。
 * </p>
 * <p>
 * このクラスのオブジェクトは、{@linkplain org.lightsleep.helper.TypeConverter#typeConverterMap}
 * に以下の<b>TypeConverter</b>オブジェクトを追加した<b>TypeConverter</b>マップを持ちます。
 * </p>
 *
 * <table class="additional">
 *   <caption><span>TypeConverterマップへの追加内容</span></caption>
 *   <tr><th colspan="2">キー: データ型</th><th rowspan="2">値: 変換関数</th></tr>
 *   <tr><th>変換元</th><th>変換先</th></tr>
 *
 *   <tr><td>Clob</td>
 *     <td>String</td><td rowspan="2">
 *       <div class="warning">
 *       変換元の長さが<b>Integer.MAX_VALUE</b>を超えるか、
 *       内容を取得中に<b>SQLException</b>がスローされた場合は、<b>ConvertException</b>をスロー
 *       </div>
 *     </td>
 *   </tr>
 * 
 *   <tr><td>Blob</td><td>byte[]</td></tr>
 * 
 *   <tr><td rowspan="19">java.sql.Array</td><td>boolean[]</td>
 *     <td rowspan="19">
 *       <b>TypeConverter</b>を使用して、<b>Array</b>の各要素を変換先の要素型に変換
 *     </td>
 *   </tr>
 *   <tr><td>byte[]          </td></tr>
 *   <tr><td>short[]         </td></tr>
 *   <tr><td>int[]           </td></tr>
 *   <tr><td>long[]          </td></tr>
 *   <tr><td>float[]         </td></tr>
 *   <tr><td>double[]        </td></tr>
 *   <tr><td>BigDecimal[]    </td></tr>
 *   <tr><td>String[]        </td></tr>
 *   <tr><td>java.util.Date[]</td></tr>
 *   <tr><td>Date[]          </td></tr>
 *   <tr><td>Time[]          </td></tr>
 *   <tr><td>Timestamp[]     </td></tr>
 *   <tr><td>LocalDateTime[] </td></tr>
 *   <tr><td>LocalDate[]     </td></tr>
 *   <tr><td>LocalTime[]     </td></tr>
 *   <tr><td>OffsetDateTime[]</td></tr>
 *   <tr><td>ZonedDateTime[] </td></tr>
 *   <tr><td>Instant[]       </td></tr>
 *   <tr>
 *     <td>
 *       Object<br>
 *       <span class="comment">(Boolean, Byte,<br>Short, Integer,<br>Long, Float,<br>Double, Character<br>, Enum, ...)</span>
 *     </td>
 *     <td rowspan="37">SqlString</td><td><b>new SqlString(source.toString())</b></td>
 *   </tr>
 *   <tr><td>Boolean        </td>
 *     <td>
 *       <b>new SqlString("FALSE")</b> <span class="comment">変換元の値が<b>false</b>の場合</span><br>
 *       <b>new SqlString("TRUE")</b> <span class="comment">変換元の値が<b>true</b>の場合</span>
 *     </td>
 *   </tr>
 *   <tr><td>BigDecimal     </td><td><b>new SqlString(object.toPlainString())</b></td></tr>
 *   <tr><td>String         </td>
 *     <td>
 *       <b>new SqlString("'" + source + "'")</b><br>
 *       <span class="comment">変換元の文字列中のシングルクォートは、連続する2個のシングルクォートに変換、<br>
 *       また制御文字は</span> <b>'...'+CHR(文字コード)+'...'</b><span class="comment"> に変換</span><br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>new SqlString(SqlString.PARAMETER, source)</b> <span class="comment">変換元の文字列が長すぎる場合</span>
 *     </td>
 *   </tr>
 *   <tr><td>Character      </td><td><b>Character</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>SqlString</b></td></tr>
 *   <tr><td>java.util.Date </td>
 *     <td rowspan="3">
 *       (<b>java.util.Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b>) <img src="../../../../images/arrow-right.gif" alt="->"> <b>new SqlString("DATE'" + string + '\'')</b>
 *     </td>
 *   </tr>
 *   <tr><td>Date           </td></tr>
 *   <tr><td>LocalDate      </td></tr>
 *   <tr><td>Time           </td>
 *     <td rowspan="2">
 *       (<b>Time</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>LocalTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b>) <img src="../../../../images/arrow-right.gif" alt="->"> <b>new SqlString("TIME'" + string + '\'')</b>
 *     </td>
 *   </tr>
 *   <tr><td>LocalTime      </td></tr>
 *   <tr><td>Timestamp      </td>
 *     <td rowspan="5">
 *       (<b>Timestamp</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>LocalDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>OffsetDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>ZonedDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> または<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Instant</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b>) <img src="../../../../images/arrow-right.gif" alt="->"> <b>new SqlString("TIMESTAMP'" + string + '\'')</b>
 *     </td>
 *   </tr>
 *   <tr><td>LocalDateTime  </td></tr>
 *   <tr><td>OffsetDateTime </td></tr>
 *   <tr><td>ZonedDateTime  </td></tr>
 *   <tr><td>Instant        </td></tr>
 *   <tr><td>byte[]         </td>
 *     <td>
 *       <b>new SqlString("X'" + hexadecimal string + "'")</b><br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>new SqlString(SqlString.PARAMETER, source)</b> <span class="comment">変換元のバイト配列が長すぎる場合</span>
 *     </td>
 *   </tr>
 *   <tr><td>boolean[]      </td>
 *     <td rowspan="20">
 *       <b>new SqlString("ARRAY[a,b,c,...]")</b><br>
 *       <div class="comment"><b>a,b,c,...</b>は変換元配列の各要素で、それぞれが<b>TypeConverter</b>を使用して<b>SqlString</b>に変換される</div>
 *     </td>
 *   </tr>
 *   <tr><td>char[]          </td></tr>
 *   <tr><td>byte[][]        </td></tr>
 *   <tr><td>short[]         </td></tr>
 *   <tr><td>int[]           </td></tr>
 *   <tr><td>long[]          </td></tr>
 *   <tr><td>float[]         </td></tr>
 *   <tr><td>double[]        </td></tr>
 *   <tr><td>BigDecimal[]    </td></tr>
 *   <tr><td>String[]        </td></tr>
 *   <tr><td>java.util.Date[]</td></tr>
 *   <tr><td>java.sql.Date[] </td></tr>
 *   <tr><td>Time[]          </td></tr>
 *   <tr><td>Timestamp[]     </td></tr>
 *   <tr><td>LocalDateTime[] </td></tr>
 *   <tr><td>LocalDate[]     </td></tr>
 *   <tr><td>LocalTime[]     </td></tr>
 *   <tr><td>OffsetDateTime[]</td></tr>
 *   <tr><td>ZonedDateTime[] </td></tr>
 *   <tr><td>Instant[]       </td></tr>
 *   <tr><td>Iterable        </td>
 *     <td>
 *       <b>new SqlString("(a,b,c,...)")</b><br>
 *       <div class="comment"><b>a,b,c,...</b>は変換元配列の各要素で、それぞれが<b>TypeConverter</b>を使用して<b>SqlString</b>に変換される</div>
 *     </td>
 *   </tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 */
public class Standard implements Database {
	/**
	 * 制御文字を以外ASCII文字からなる文字列
	 *
	 * @since 2.2.0
	 */
	protected static final String ASCII_CHARS = "";

	/**
	 * パスワードのパターン文字列
	 *
	 * @since 2.2.0
	 */
	protected static final String PASSWORD_PATTERN = "";

	/**
	 * SQLが生成される時の文字列リテラルの最大長
	 *
	 * <p>
	 * 文字列リテラルがこの長さを超える場合、SQLのパラメータ(?)として生成します。<br>
	 * lightsleep.propertiesの<b>maxStringLiteralLength</b>の値が設定されます。(未定義の場合は 128)
	 * </p>
	 */
	public final int maxStringLiteralLength = 0;

	/**
	 * SQLが生成される時のバイナリ列リテラルの最大長
	 *
	 * <p>
	 * バイナリ列リテラルがこの長さを超える場合、SQLのパラメータ(?)として生成します。<br>
	 * lightsleep.propertiesの<b>maxBinaryLiteralLength</b>の値が設定されます。(未定義の場合は 128)
	 * </p>
	 */
	public final int maxBinaryLiteralLength = 0;

	/**
	 * <b>boolean</b>から<b>SqlString</b>(0か1)へ変換する
	 * <b>TypeConverter</b>オブジェクト
	 */
	protected static final TypeConverter<Boolean, SqlString> booleanToSql01Converter = null;

	/**
	 * このクラスの唯一のインスタンス
	 *
	 * @since 2.1.0
	 */
	public static final Standard instance = new Standard();

	/**
	 * 以下のデータ型変換で使用する<b>TypeConverter</b>マップ
	 * <ul>
	 *   <li>SQL 生成時</li>
	 *   <li>SELECT SQLで取得した値をエンティティに格納する際</li>
	 * </ul>
	 */
	protected final Map<String, TypeConverter<?, ?>> typeConverterMap = null;

	/**
	 * <b>Standard</b>を構築します。
	 */
	protected Standard() {
	}

	/**
	 * java.sql.Arrayを配列に変換します。
	 *
	 * @param <AT> 配列の型
	 * @param <CT> コンポーネントの型
	 * @param object 変換するオブジェクト
	 * @param arrayType 配列の型
	 * @param componentType コンポーネントの型
	 * @return 変換された配列
	 */
	protected <AT, CT> AT toArray(java.sql.Array object, Class<AT> arrayType, Class<CT> componentType) {
		return null;
	}

	/**
	 * 配列オブジェクトを<b>SqlString</b>に変換します。
	 *
	 * @param <CT> コンポーネントの型
	 * @param array 変換する配列
	 * @param componentType コンポーネントの型
	 * @return 変換された<b>SqlString</b>
	 */
	protected <CT> SqlString toSqlString(Object array, Class<CT> componentType) {
		return null;
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
	public <E> String subSelectSql(Sql<E> sql, List<Object> parameters) {
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
	 */
	@Override
	public <E> String insertSql(Sql<E> sql, List<Object> parameters) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String updateSql(Sql<E> sql, List<Object> parameters) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String deleteSql(Sql<E> sql, List<Object> parameters) {
		return null;
	}

	/**
	 * DISTINCT を<b>buff</b>に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b>オブジェクト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendDistinct(StringBuilder buff, Sql<E> sql) {
	}

	/**
	 * メインテーブルの名前と別名を<b>buff</b>に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b>オブジェクト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendMainTable(StringBuilder buff, Sql<E> sql) {
	}

	/**
	 * 結合テーブルの名前と別名を<b>buff</b>に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b>オブジェクト
	 * @param parameters SQLのパラメータを格納するリスト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendJoinTables(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
	}

	/**
	 * 挿入するカラム名と値を<b>buff</b>に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b>オブジェクト
	 * @param parameters SQLのパラメータを格納するリスト
	 *
	 * @since 1.8.4
	 */
	protected <E> void appendInsertColumnsAndValues(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
	}

	/**
	 * 更新するカラム名と値を<b>buff</b>に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b>オブジェクト
	 * @param parameters SQLのパラメータを格納するリスト
	 *
	 * @since 1.8.4
	 */
	protected <E> void appendUpdateColumnsAndValues(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
	}

	/**
	 * WHERE句を<b>buff</b>に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b>オブジェクト
	 * @param parameters SQLのパラメータを格納するリスト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendWhere(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
	}

	/**
	 * GROUP BY句を<b>buff</b>に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b>オブジェクト
	 * @param parameters SQLのパラメータを格納するリスト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendGroupBy(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
	}

	/**
	 * HAVING句を<b>buff</b>に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b>オブジェクト
	 * @param parameters SQLのパラメータを格納するリスト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendHaving(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
	}

	/**
	 * ORDER BY句を<b>buff</b>に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b>オブジェクト
	 * @param parameters SQLのパラメータを格納するリスト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendOrderBy(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
	}

	/**
	 * LIMIT句を<b>buff</b>に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b>オブジェクト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendLimit(StringBuilder buff, Sql<E> sql) {
	}

	/**
	 * OFFSET句を<b>buff</b>に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b>オブジェクト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendOffset(StringBuilder buff, Sql<E> sql) {
	}

	/**
	 * FOR UPDATE句を<b>buff</b>に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b>オブジェクト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendForUpdate(StringBuilder buff, Sql<E> sql) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, TypeConverter<?, ?>> typeConverterMap() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T convert(Object value, Class<T> type) {
		return null;
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
