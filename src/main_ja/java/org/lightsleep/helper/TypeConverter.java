// TypeConverter.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

import java.util.Map;
import java.util.function.Function;

/**
 * データ型を変換します。<br>
 *
 * 下記の <b>TypeConverter</b> オブジェクトを static マップに持ちます。
 * このマップは、{@linkplain #typeConverterMap()} メソッドで取得する事ができます。<br>
 * <br>
 *
 * <table class="additional">
 *   <caption><span>登録されている TypeConverter オブジェクト</span></caption>
 *   <tr><th>変換元データ型</th><th>変換先データ型</th><th>変換内容</th></tr>
 *
 *   <tr><td>Byte          </td><td rowspan="9">Boolean</td><td rowspan="7">0 ➔ false<br>1 ➔ true<br>その他の場合 ConvertException をスロー</td></tr>
 *   <tr><td>Short         </td></tr>
 *   <tr><td>Integer       </td></tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td><td>'0' ➔ false<br>'1' ➔ true<br>その他の場合 ConvertException をスロー</td></tr>
 *   <tr><td>String        </td><td>"0" ➔ false<br>"1" ➔ true<br>その他の場合 ConvertException をスロー</td></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="9">Byte</td><td>false ➔ 0<br>true ➔ 1</td></tr>
 *   <tr><td>Short         </td><td rowspan="7">範囲外の場合 ConvertException をスロー</td></tr>
 *   <tr><td>Integer       </td></tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td></tr>
 *   <tr><td>String        </td><td>非数値または範囲外の場合 ConvertException をスロー</td></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="9">Short</td><td>false ➔ 0<br>true ➔ 1</td></tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Integer       </td><td rowspan="6">範囲外の場合 ConvertException をスロー</td></tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td></tr>
 *   <tr><td>String        </td><td>非数値または範囲外の場合 ConvertException をスロー</td></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="10">Integer</td><td>false ➔ 0<br>true ➔ 1</td></tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Short         </td><td></td></tr>
 *   <tr><td>Long          </td><td rowspan="4">範囲外の場合 ConvertException をスロー</td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td><td></td></tr>
 *   <tr><td>String        </td><td>非数値または範囲外の場合 ConvertException をスロー</td></tr>
 *   <tr><td>java.util.Date</td><td>範囲外の場合 ConvertException をスロー</td></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="10">Long</td><td>false ➔ 0<br>true ➔ 1</td></tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Short         </td><td></td></tr>
 *   <tr><td>Integer       </td><td></td></tr>
 *   <tr><td>Float         </td><td rowspan="3">範囲外の場合 ConvertException をスロー</td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td><td></td></tr>
 *   <tr><td>String        </td><td>非数値または範囲外の場合 ConvertException をスロー</td></tr>
 *   <tr><td>java.util.Date</td><td>long 値を取得</td></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="9">Float</td><td>false ➔ 0.0F<br>true ➔ 1.0F</td></tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Short         </td><td></td></tr>
 *   <tr><td>Integer       </td><td></td></tr>
 *   <tr><td>Long          </td><td></td></tr>
 *   <tr><td>Double        </td><td></td></tr>
 *   <tr><td>BigDecimal    </td><td></td></tr>
 *   <tr><td>Character     </td><td></td></tr>
 *   <tr><td>String        </td><td>非数値の場合 ConvertException をスロー</td></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="9">Double</td><td>false ➔ 0.0D<br>true ➔ 1.0D</td></tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Short         </td><td></td></tr>
 *   <tr><td>Integer       </td><td></td></tr>
 *   <tr><td>Long          </td><td></td></tr>
 *   <tr><td>Float         </td><td></td></tr>
 *   <tr><td>BigDecimal    </td><td></td></tr>
 *   <tr><td>Character     </td><td></td></tr>
 *   <tr><td>String        </td><td>非数値の場合 ConvertException をスロー</td></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="9">BigDecimal</td><td>false ➔ <code>BigDecimal.ZERO</code><br>true ➔ <code>BigDecimal.ONE</code></td></tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Short         </td><td></td></tr>
 *   <tr><td>Integer       </td><td></td></tr>
 *   <tr><td>Long          </td><td></td></tr>
 *   <tr><td>Float         </td><td></td></tr>
 *   <tr><td>Double        </td><td></td></tr>
 *   <tr><td>Character     </td><td></td></tr>
 *   <tr><td>String        </td><td>非数値の場合 ConvertException をスロー</td></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="9">Character</td><td>false ➔ '0'<br>true ➔ '1'</td></tr>
 *   <tr><td>Byte          </td><td></td></tr>
 *   <tr><td>Short         </td><td></td></tr>
 *   <tr><td>Integer       </td><td rowspan="5">範囲外の場合 ConvertException をスロー</td></tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>String        </td><td>String の長さが1以外の場合 ConvertException をスロー</td></tr>
 *
 *   <tr><td>BigDecimal    </td><td rowspan="6">String</td><td>toPlainString() で変換</td></tr>
 *   <tr><td>java.uitl.Date</td><td rowspan="2"><code>"yyyy-MM-dd"</code></td></tr>
 *   <tr><td>java.sql.Date </td></tr>
 *   <tr><td>Time          </td><td><code>"HH:mm:ss"</code></td></tr>
 *   <tr><td>Timestamp     </td><td><code>"yyyy-MM-dd HH:mm:ss.SSS"</code></td></tr>
 *   <tr><td>Object        </td><td>toString() で変換</td></tr>
 *
 *   <tr><td>Integer       </td><td rowspan="4">java.util.Date</td><td></td></tr>
 *   <tr><td>Long          </td><td></td></tr>
 *   <tr><td>BigDecimal    </td><td>Long への変換で範囲外の場合 ConvertException をスロー</td></tr>
 *   <tr><td>String        </td><td><code>"yyyy-MM-dd"</code> ➔ String<br>フォーマット不正の場合 ConvertException をスロー</td></tr>
 *
 *   <tr><td>Integer       </td><td rowspan="5">java.sql.Date</td><td></td></tr>
 *   <tr><td>Long          </td><td></td></tr>
 *   <tr><td>BigDecimal    </td><td>Long への変換で範囲外の場合 ConvertException をスロー</td></tr>
 *   <tr><td>java.util.Date</td><td></td></tr>
 *   <tr><td>String        </td><td><code>"yyyy-MM-dd"</code> ➔ String<br>フォーマット不正の場合 ConvertException をスロー</td></tr>
 *
 *   <tr><td>Integer       </td><td rowspan="5">Time</td><td></td></tr>
 *   <tr><td>Long          </td><td></td></tr>
 *   <tr><td>BigDecimal    </td><td>Long への変換で範囲外の場合 ConvertException をスロー</td></tr>
 *   <tr><td>java.util.Date</td><td></td></tr>
 *   <tr><td>String        </td><td><code>"HH:mm:ss"</code> ➔ String<br>フォーマット不正の場合 ConvertException をスロー</td></tr>
 *
 *   <tr><td>Long          </td><td rowspan="5">Timestamp</td><td></td></tr>
 *   <tr><td>Integer       </td><td></td></tr>
 *   <tr><td>BigDecimal    </td><td>Long への変換で範囲外の場合 ConvertException をスロー</td></tr>
 *   <tr><td>java.util.Date</td><td></td></tr>
 *   <tr><td>String        </td><td><code>"yyyy-MM-dd HH:mm:ss"</code> または<br><code>"yyyy-MM-dd HH:mm:ss.SSS"</code> ➔ String<br>フォーマット不正の場合 ConvertException をスロー</td></tr>
 *
 *   <tr><td rowspan="4">Enum</td><td>Byte   </td><td rowspan="2">ordinal() で変換<br>範囲外の場合 ConvertException をスロー</td></tr>
 *   <tr>                         <td>Short  </td></tr>
 *   <tr>                         <td>Integer</td><td rowspan="2">ordinal() で変換</td></tr>
 *   <tr>                         <td>Long   </td></tr>
 * </table>
 *
 * @since 1.0
 * @author Masato Kokubo
 * @see org.lightsleep.database.Standard
 * @see org.lightsleep.database.MySQL
 * @see org.lightsleep.database.Oracle
 * @see org.lightsleep.database.PostgreSQL
 * @see org.lightsleep.database.SQLite
 * @see org.lightsleep.database.SQLServer
 */
public class TypeConverter<ST, DT> {
	/**
		変換元のデータ型と変換先のデータ型の組み合わせで、マップのキーとして使用する文字列を作成します。

		@param sourceType 変換元のデータ型クラス
		@param destinType 変換先のデータ型クラス

		@return キー

		@throws NullPointerException <b>sourceType</b> または <b>destinType</b> が null の場合
	*/
	public static String key(Class<?> sourceType, Class<?> destinType) {
		return null;
	}

	/**
		<b>TypeConverter</b> マップに <b>TypeConverter</b> 配列の各要素を関連付けます。

		@param typeConverterMap <b>TypeConverter</b> マップ
		@param typeConverters <b>TypeConverter</b> オブジェクト配列

		@throws NullPointerException <b>typeConverterMap</b>, <b>typeConverters</b> または <b>typeConverters</b> の要素が null の場合
	*/
	public static void put(Map<String, TypeConverter<?, ?>> typeConverterMap, TypeConverter<?, ?>... typeConverters) {
	}

	/**
		<b>typeConverterMap</b> から
		<b>sourceType</b> を <b>destinType</b> に変換する <b>TypeConverter</b> オブジェクトを返します。<br>

		<b>sourceType</b> と <b>destinType</b> の組み合わせでマッチする
		<b>TypeConverter</b> オブジェクトが見つからない場合は、
		<b>sourceType</b> のスーパークラスやインタフェースでマッチするのを探します。<br>

		それでも見つからない場合は、null を返します。<br>

		スーパークラスまたはインターフェースで見つかった場合は、次回は直接見つかるようにマップに登録します。<br>

		@param <ST> 変換元のデータ型
		@param <DT> 変換先のデータ型

		@param typeConverterMap <b>TypeConverter</b> マップ
		@param sourceType 変換元のデータ型クラス
		@param destinType 変換先のデータ型クラス

		@return TypeConverter オブジェクト (見つからない場合は null)

		@throws NullPointerException typeConverterMap, <b>sourceType</b> または <b>destinType</b> が null の場合
	*/
	public static <ST, DT> TypeConverter<ST, DT> get(Map<String, TypeConverter<?, ?>> typeConverterMap, Class<ST> sourceType, Class<DT> destinType) {
		return null;
	}

	/**
		<b>source</b> == null の場合は、null を返します。<br>
		<b>destinType.isInstance(source)</b> の場合は、<b>source</b> を変換しないで返します。<br>
		コンバータが見つかった場合は、そのコンバータで <b>source</b> を変換したオブジェクトを返します。

		@param <ST> 変換元のデータ型
		@param <DT> 変換先のデータ型

		@param typeConverterMap <b>TypeConverter</b> マップ
		@param source 変換元のオブジェクト (null 可)
		@param destinType 変換先のデータ型クラス (プリミティブ型以外)

		@return データ型を変換されたオブジェクト (null 有)

		@throws NullPointerException <b>typeConverterMap</b> または <b>destinType</b> が null の場合
		@throws ConvertException コンバータが見つからない場合か変換処理で精度が落ちた場合
	*/
	public static <ST, DT> DT convert(Map<String, TypeConverter<?, ?>> typeConverterMap, ST source, Class<DT> destinType) {
		return null;
	}

	/**
	 * 各種の TypeConverter オブジェクトが登録された変更不可な <b>TypeConverter</b> マップを返します。
	 *
	 * @return <b>TypeConverter</b> マップ
	 *
	 * @since 1.8.1
	 */
	public static Map<String, TypeConverter<?, ?>>typeConverterMap() {
		return null;
	}

	/**
	 * <b>TypeConverter</b> を構築します。
	 *
	 * @param sourceType 変換元のデータ型クラス
	 * @param destinType 変換先のデータ型クラス
	 * @param function 変換を実行する関数
	 *
	 * @throws NullPointerException <b>sourceType</b>, <b>destinType</b> または <b>function</b> が null の場合
	*/
	public TypeConverter(Class<ST> sourceType, Class<DT> destinType, Function<? super ST, ? extends DT> function) {
	}

	/**
	 * <b>TypeConverter</b> を構築します。
	 *
	 * @param <MT> 中間の型
	 * @param typeConverter1 コンバーター1
	 * @param typeConverter2 コンバーター2
	 *
	 * @throws NullPointerException <b>typeConverter1</b> または <b>typeConverter2</b> が null の場合
	 *
	 * @since 1.8.0
	 */
	public <MT> TypeConverter(TypeConverter<ST, MT> typeConverter1, TypeConverter<MT, DT> typeConverter2) {
	}

	/**
		変換元のデータ型を返します。

		@return 変換元のデータ型
	*/
	public Class<ST> sourceType() {
		return null;
	}

	/**
		変換先のデータ型を返します。

		@return 変換先のデータ型
	*/
	public Class<DT> destinType() {
		return null;
	}

	/**
		データ型を変換する関数を返します。

		@return データ型を変換する関数
	*/
	public Function<? super ST, ? extends DT> function() {
		return null;
	}

	/**
		キーを返します。

		@return キー
	*/
	public String key() {
		return null;
	}

	/**
		<b>value</b> のデータ型を変換します。

		@param value 変換元のオブジェクト

		@return 変換されたオブジェクト
	*/
	public DT apply(ST value) {
		return null;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean equals(Object object) {
		return false;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public int hashCode() {
		return 0;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public String toString() {
		return null;
	}
}
