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
 * このクラスのオブジェクトは、
 * {@linkplain org.lightsleep.helper.TypeConverter#typeConverterMap}
 * に以下の <b>TypeConverter</b> を追加した <b>TypeConverter</b> マップを持ちます。

 * <table class="additional">
 *   <caption><span>登録されている TypeConverter オブジェクト</span></caption>
 *   <tr><th>変換元データ型</th><th>変換先データ型</th><th>変換フォーマット</th></tr>
 *
 *   <tr><td>Clob          </td><td>String</td><td></td></tr>
 *
 *   <tr><td>Blob          </td><td>byte[]</td><td></td></tr>
 *
 *   <tr><td rowspan="13">java.sql.Array</td><td>boolean[]      </td><td></td></tr>
 *   <tr>                                    <td>byte[]         </td><td></td></tr>
 *   <tr>                                    <td>short[]        </td><td></td></tr>
 *   <tr>                                    <td>int[]          </td><td></td></tr>
 *   <tr>                                    <td>long[]         </td><td></td></tr>
 *   <tr>                                    <td>float[]        </td><td></td></tr>
 *   <tr>                                    <td>double[]       </td><td></td></tr>
 *   <tr>                                    <td>BigDecimal[]   </td><td></td></tr>
 *   <tr>                                    <td>String[]       </td><td></td></tr>
 *   <tr>           <td>java.util.Date[]<br><i>(since 1.4.0)</i></td><td></td></tr>
 *   <tr>                                    <td>java.sql.Date[]</td><td></td></tr>
 *   <tr>                                    <td>Time[]         </td><td></td></tr>
 *   <tr>                                    <td>Timestamp[]    </td><td></td></tr>
 *
 *   <tr><td>Boolean        </td><td rowspan="26">{@linkplain org.lightsleep.component.SqlString}</td><td>FALSE か TRUE</td></tr>
 *   <tr><td>Object         </td><td>'...'</td></tr>
 *   <tr><td>Character      </td><td>'...'</td></tr>
 *   <tr><td>BigDecimal     </td><td></td></tr>
 *   <tr><td>String         </td><td>長い場合は <i>SQL パラメーター (?)</i>、そうでなければ '...' </td></tr>
 *   <tr><td>java.util.Date<br><i>(since 1.4.0)</i></td><td rowspan="2">DATE'yyyy-MM-dd'</td></tr>
 *   <tr><td>java.sql.Date  </td></tr>
 *   <tr><td>Time           </td><td>TIME'HH:mm:ss'</td></tr>
 *   <tr><td>Timestamp      </td><td>TIMESTAMP'yyyy-MM-dd HH:mm:ss.SSS'</td></tr>
 *   <tr><td>Enum           </td><td></td></tr>
 *   <tr><td>byte[]         </td><td>長い場合は <i>SQL パラメーター (?)</i>、そうでなければ X'...'</td></tr>
 *   <tr><td>boolean[]      </td><td rowspan="14">ARRAY[x,y,z,...]</td></tr>
 *   <tr><td>char[]         </td></tr>
 *   <tr><td>byte[][]       </td></tr>
 *   <tr><td>short[]        </td></tr>
 *   <tr><td>int[]          </td></tr>
 *   <tr><td>long[]         </td></tr>
 *   <tr><td>float[]        </td></tr>
 *   <tr><td>double[]       </td></tr>
 *   <tr><td>BigDecimal[]   </td></tr>
 *   <tr><td>String[]       </td></tr>
 *   <tr><td>java.util.Date[]<br><i>(since 1.4.0)</i></td></tr>
 *   <tr><td>java.sql.Date[]</td></tr>
 *   <tr><td>Time[]         </td></tr>
 *   <tr><td>Timestamp[]    </td></tr>
 *   <tr><td>Iterable       </td><td>(x,y,z,...)</td></tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 */
public class Standard implements Database {
	/**
		SQL が作成される時の文字列リテラルの最大長。
		文字列リテラルがこの長さを超える場合、SQL のパラメータ (?) として生成します。<br>
		lightsleep.properties の <b>maxStringLiteralLength</b>
		の値が設定されます。(未定義の場合は 128)
	*/
	protected static final int maxStringLiteralLength = 0;

	/**
		SQL が作成される時のバイナリ列リテラルの最大長。
		バイナリ列リテラルがこの長さを超える場合、SQL のパラメータ (?) として生成します。<br>
		lightsleep.properties の <b>maxBinaryLiteralLength</b>
		の値が設定されます。(未定義の場合は 128)
	*/
	protected static final int maxBinaryLiteralLength = 0;

// 1.8.0
//	/**
//		<b>boolean</b> から <b>SqlString</b> (FALSE か TRUE) へ変換する
//		<b>TypeConverter</b> オブジェクト
//	*/
//	public static final TypeConverter<Boolean, SqlString> booleanToSqlFalseTrueConverter = null;
////

	/**
		<b>boolean</b> から <b>SqlString</b> (0 か 1) へ変換する
		<b>TypeConverter</b> オブジェクト
	*/
// 1.8.0
//	public static final TypeConverter<Boolean, SqlString> booleanToSql01Converter = null;
	protected static final TypeConverter<Boolean, SqlString> booleanToSql01Converter = null;
////

// 1.8.0
//	/**
//		<b>boolean</b> から <b>SqlString</b> ('0' か '1') へ変換する
//		<b>TypeConverter</b> オブジェクト
//	*/
//	public static final TypeConverter<Boolean, SqlString> booleanToSqlChar01Converter = null;
//
//	/**
//		<b>boolean</b> から <b>SqlString</b> ('N' か 'Y') へ変換する
//		<b>TypeConverter</b> オブジェクト
//	*/
//	public static final TypeConverter<Boolean, SqlString> booleanToSqlNYConverter = null;
//
//	/**
//		<b>String</b> ("N" か "Y") から <b>boolean</b> へ変換する
//		<b>TypeConverter</b> オブジェクト
//	*/
//	public static final TypeConverter<String, Boolean> stringNYToBooleanConverter = null;
////

	/**
		<b>Standard</b> オブジェクトを返します。

		@return Standard オブジェクト
	*/
	public static Database instance() {
		return null;
	}

	/**
		<b>Standard</b> を構築します。
	*/
	protected Standard() {
	}

	/**
		以下のデータ型変換で使用する <b>TypeConverter</b> マップ<br>
		<ul>
			<li>SQL 生成時</li>
			<li>SELECT SQL で取得した値をエンティティに格納する際</li>
		</ul>
	*/
	protected final Map<String, TypeConverter<?, ?>> typeConverterMap = null;

	/**
		{@inheritDoc}
	*/
	@Override
	public <E> String selectSql(Sql<E> sql, List<Object> parameters) {
		return null;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public <E> String subSelectSql(Sql<E> sql, List<Object> parameters) {
		return null;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public <E> String subSelectSql(Sql<E> sql, Supplier<CharSequence> columnsSupplier, List<Object> parameters) {
		return null;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public <E> String insertSql(Sql<E> sql, List<Object> parameters) {
		return null;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public <E> String updateSql(Sql<E> sql, List<Object> parameters) {
		return null;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public <E> String deleteSql(Sql<E> sql, List<Object> parameters) {
		return null;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public Map<String, TypeConverter<?, ?>> typeConverterMap() {
		return null;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public <T> T convert(Object value, Class<T> type) {
		return null;
	}
}
