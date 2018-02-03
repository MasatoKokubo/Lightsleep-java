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
 * 特定の DBMS に依存しないデータベース･ハンドラです。
 *
 * <p>
 * {@linkplain org.lightsleep.helper.TypeConverter} クラスが持つ
 * <b>TypeConverter</b>オブジェクトおよび以下の変換を行う
 * <b>TypeConverter</b>オブジェクトを持ちます。
 * </p>
 * <p>
 * このクラスのオブジェクトは、{@linkplain org.lightsleep.helper.TypeConverter#typeConverterMap}
 * に以下の<b>TypeConverter</b>オブジェクトを追加した<b>TypeConverter</b>マップを持ちます。
 * </p>
 *
 * <table class="additional">
 *   <caption><span>追加されるTypeConverter オブジェクト</span></caption>
 *   <tr><th>変換元データ型</th><th>変換先データ型</th><th>変換内容</th></tr>
 *
 *   <tr><td>Clob          </td><td>String</td><td rowspan="2">長さが<code>Integer.MAX_VALUE</code>を超える場合ConvertExceptionをスロー<br>内容の取得時にSQLExceptionがスローされた場合ConvertExceptionをスロー</td></tr>
 *
 *   <tr><td>Blob          </td><td>byte[]</td></tr>
 *
 *   <tr><td rowspan="13">java.sql.Array</td><td>boolean[]       </td><td rowspan="13">各要素をTypeConverterで配列要素のデータ型に変換</td></tr>
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
 *   <tr><td>String         </td><td><code>'...'</code><br>制御文字は<code>'...'||CHR(n)||'...'</code>に変換<br>長い文字列場合は<code>?</code><i>(SQLパラメータ)</i></td></tr>
 *   <tr><td>java.util.Date</td><td rowspan="2"><code>DATE'yyyy-MM-dd'</code></td></tr>
 *   <tr><td>java.sql.Date  </td></tr>
 *   <tr><td>Time           </td><td><code>TIME'HH:mm:ss'</code></td></tr>
 *   <tr><td>Timestamp      </td><td><code>TIMESTAMP'yyyy-MM-dd HH:mm:ss.SSS'</code></td></tr>
 *   <tr><td>Enum           </td><td><code>'...'</code> (toString() で変換)</td></tr>
 *   <tr><td>byte[]         </td><td><code>X'...'</code><br>長いバイト配列の場合は<code>?</code><i>(SQLパラメータ)</i></td></tr>
 *   <tr><td>boolean[]      </td><td rowspan="14"><code>ARRAY[x,y,z,...]</code><br>各要素をTypeConverterでSqlStringに変換</td></tr>
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
 *   <tr><td>Iterable       </td><td><code>(x,y,z,...)</code><br>各要素をTypeConverterでSqlStringに変換</td></tr>
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
	 * このクラスの唯一のインスタンスを返します。
	 *
	 * <p>
	 * @deprecated リリース 2.1.0 より。代わりに{@link #instance}を使用してください。
	 * </p>
	 *
	 * @return このクラスの唯一のインスタンス
	 */
	@Deprecated
	public static Database instance() {
		return null;
	}

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
