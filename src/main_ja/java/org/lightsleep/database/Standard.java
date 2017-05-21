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
 * 特定の DBMS に依存しないデータベース・ハンドラーです。<br>
 *
 * {@linkplain org.lightsleep.helper.TypeConverter} クラスが持つ
 * <b>TypeConverter</b> オブジェクトおよび以下の変換を行う
 * <b>TypeConverter</b> オブジェクトを持ちます。<br>
 * <br>
 *
 * <table class="additional">
 *   <caption><span>追加される TypeConverter オブジェクト</span></caption>
 *   <tr><th>変換元データ型</th><th>変換先データ型</th><th>変換内容</th></tr>
 *
 *   <tr><td>Clob          </td><td>String</td><td rowspan="2">長さが <code>Integer.MAX_VALUE</code> を超える場合 ConvertException をスロー<br>内容の取得時に SQLException がスローされた場合 ConvertException をスロー</td></tr>
 *
 *   <tr><td>Blob          </td><td>byte[]</td></tr>
 *
 *   <tr><td rowspan="13">java.sql.Array</td><td>boolean[]       </td><td rowspan="13">各要素を TypeConverter で配列要素のデータ型に変換</td></tr>
 *   <tr>                                    <td>byte[]          </td></tr>
 *   <tr>                                    <td>short[]         </td></tr>
 *   <tr>                                    <td>int[]           </td></tr>
 *   <tr>                                    <td>long[]          </td></tr>
 *   <tr>                                    <td>float[]         </td></tr>
 *   <tr>                                    <td>double[]        </td></tr>
 *   <tr>                                    <td>BigDecimal[]    </td></tr>
 *   <tr>                                    <td>String[]        </td></tr>
 *   <tr>                                    <td>java.util.Date[]</td></tr>
 *   <tr>                                    <td>java.sql.Date[] </td></tr>
 *   <tr>                                    <td>Time[]          </td></tr>
 *   <tr>                                    <td>Timestamp[]     </td></tr>
 *
 *   <tr><td>Boolean        </td><td rowspan="26">SqlString</td><td>false ➔ <code>FALSE</code><br>true ➔ <code>TRUE</code></td></tr>
 *   <tr><td>Object         </td><td rowspan="2"><code>'...'</code></td></tr>
 *   <tr><td>Character      </td></tr>
 *   <tr><td>BigDecimal     </td><td></td></tr>
 *   <tr><td>String         </td><td><code>'...'</code><br>長い場合は <code>?</code> <i>(SQLパラメータ)</i></td></tr>
 *   <tr><td>java.util.Date</td><td rowspan="2"><code>DATE'yyyy-MM-dd'</code></td></tr>
 *   <tr><td>java.sql.Date  </td></tr>
 *   <tr><td>Time           </td><td><code>TIME'HH:mm:ss'</code></td></tr>
 *   <tr><td>Timestamp      </td><td><code>TIMESTAMP'yyyy-MM-dd HH:mm:ss.SSS'</code></td></tr>
 *   <tr><td>Enum           </td><td><code>'...'</code> (toString() で変換)</td></tr>
 *   <tr><td>byte[]         </td><td><code>X'...'</code><br>長い場合は <code>?</code> <i>(SQLパラメータ)</i></td></tr>
 *   <tr><td>boolean[]      </td><td rowspan="14"><code>ARRAY[x,y,z,...]</code><br>各要素を TypeConverter で SqlString に変換</td></tr>
 *   <tr><td>char[]         </td></tr>
 *   <tr><td>byte[][]       </td></tr>
 *   <tr><td>short[]        </td></tr>
 *   <tr><td>int[]          </td></tr>
 *   <tr><td>long[]         </td></tr>
 *   <tr><td>float[]        </td></tr>
 *   <tr><td>double[]       </td></tr>
 *   <tr><td>BigDecimal[]   </td></tr>
 *   <tr><td>String[]       </td></tr>
 *   <tr><td>java.util.Date[]</td></tr>
 *   <tr><td>java.sql.Date[]</td></tr>
 *   <tr><td>Time[]         </td></tr>
 *   <tr><td>Timestamp[]    </td></tr>
 *   <tr><td>Iterable       </td><td><code>(x,y,z,...)</code><br>各要素を TypeConverter で SqlString に変換</td></tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 */
public class Standard implements Database {
	/**
	 * SQL が作成される時の文字列リテラルの最大長。
	 * 文字列リテラルがこの長さを超える場合、SQL のパラメータ (?) として生成します。<br>
	 * lightsleep.properties の <b>maxStringLiteralLength</b>
	 * の値が設定されます。(未定義の場合は 128)
	 */
	protected static final int maxStringLiteralLength = 0;

	/**
	 * SQL が作成される時のバイナリ列リテラルの最大長。
	 * バイナリ列リテラルがこの長さを超える場合、SQL のパラメータ (?) として生成します。<br>
	 * lightsleep.properties の <b>maxBinaryLiteralLength</b>
	 * の値が設定されます。(未定義の場合は 128)
	 */
	protected static final int maxBinaryLiteralLength = 0;

// 1.8.0
//	/**
//	 * <b>boolean</b> から <b>SqlString</b> (FALSE か TRUE) へ変換する
//	 * <b>TypeConverter</b> オブジェクト
//	 */
//	public static final TypeConverter<Boolean, SqlString> booleanToSqlFalseTrueConverter = null;
////

	/**
	 * <b>boolean</b> から <b>SqlString</b> (0 か 1) へ変換する
	 * <b>TypeConverter</b> オブジェクト
	 */
// 1.8.0
//	public static final TypeConverter<Boolean, SqlString> booleanToSql01Converter = null;
	protected static final TypeConverter<Boolean, SqlString> booleanToSql01Converter = null;
////

// 1.8.0
//	/**
//	 * <b>boolean</b> から <b>SqlString</b> ('0' か '1') へ変換する
//	 * <b>TypeConverter</b> オブジェクト
//	 */
//	public static final TypeConverter<Boolean, SqlString> booleanToSqlChar01Converter = null;
//
//	/**
//	 * <b>boolean</b> から <b>SqlString</b> ('N' か 'Y') へ変換する
//	 * <b>TypeConverter</b> オブジェクト
//	 */
//	public static final TypeConverter<Boolean, SqlString> booleanToSqlNYConverter = null;
//
//	/**
//	 * <b>String</b> ("N" か "Y") から <b>boolean</b> へ変換する
//	 * <b>TypeConverter</b> オブジェクト
//	 */
//	public static final TypeConverter<String, Boolean> stringNYToBooleanConverter = null;
////

	/**
	 * <b>Standard</b> オブジェクトを返します。
	 *
	 * @return Standard オブジェクト
	 */
	public static Database instance() {
		return null;
	}

	/**
	 * <b>Standard</b> を構築します。
	 */
	protected Standard() {
	}

	/**
	 * 以下のデータ型変換で使用する <b>TypeConverter</b> マップ<br>
	 * <ul>
	 *   <li>SQL 生成時</li>
	 *   <li>SELECT SQL で取得した値をエンティティに格納する際</li>
	 * </ul>
	 */
	protected final Map<String, TypeConverter<?, ?>> typeConverterMap = null;

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
	 * DISTINCT を <b>buff</b> に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b> オブジェクト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendsDistinct(StringBuilder buff, Sql<E> sql) {
	}

	/**
	 * メインテーブルの名前と別名を <b>buff</b> に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b> オブジェクト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendsMainTable(StringBuilder buff, Sql<E> sql) {
	}

	/**
	 * 結合テーブルの名前と別名を <b>buff</b> に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b> オブジェクト
	 * @param parameters SQL のパラメータを格納するリスト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendsJoinTables(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
	}

	/**
	 * 挿入するカラム名と値を <b>buff</b> に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b> オブジェクト
	 * @param parameters SQL のパラメータを格納するリスト
	 *
	 * @since 1.8.4
	 */
	protected <E> void appendsInsertColumnsAndValues(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
	}

	/**
	 * 更新するカラム名と値を <b>buff</b> に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b> オブジェクト
	 * @param parameters SQL のパラメータを格納するリスト
	 *
	 * @since 1.8.4
	 */
	protected <E> void appendsUpdateColumnsAndValues(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
	}

	/**
	 * WHERE 句を <b>buff</b> に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b> オブジェクト
	 * @param parameters SQL のパラメータを格納するリスト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendsWhere(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
	}

	/**
	 * GROUP BY 句を <b>buff</b> に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b> オブジェクト
	 * @param parameters SQL のパラメータを格納するリスト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendsGroupBy(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
	}

	/**
	 * HAVING 句を <b>buff</b> に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b> オブジェクト
	 * @param parameters SQL のパラメータを格納するリスト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendsHaving(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
	}

	/**
	 * ORDER BY 句を <b>buff</b> に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b> オブジェクト
	 * @param parameters SQL のパラメータを格納するリスト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendsOrderBy(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
	}

	/**
	 * LIMIT 句を <b>buff</b> に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b> オブジェクト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendsLimit(StringBuilder buff, Sql<E> sql) {
	}

	/**
	 * OFFSET 句を <b>buff</b> に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b> オブジェクト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendsOffset(StringBuilder buff, Sql<E> sql) {
	}

	/**
	 * FOR UPDATE 句を <b>buff</b> に追加します。
	 *
	 * @param <E> エンティティの型
	 * @param buff 追加される文字列バッファ
	 * @param sql <b>Sql</b> オブジェクト
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendsForUpdate(StringBuilder buff, Sql<E> sql) {
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
}
