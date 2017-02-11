// TypeConverter.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

import java.util.Map;
import java.util.function.Function;

/**
 * データ型を変換するためのクラスです。<br>
 *
 * 以下の <b>TypeConverter</b> オブジェクトが <b>typeConverterMap</b> に登録されています。<br>
 * 
 * <table class="additional">
 *   <caption><span>登録されている TypeConverter オブジェクト</span></caption>
 *   <tr><th>変換元データ型</th><th>変換先データ型</th><th>変換フォーマット</th></tr>
 *
 *   <tr><td>Byte          </td><td rowspan="9">Boolean</td><td rowspan="9"></td></tr>
 *   <tr><td>Short         </td></tr>
 *   <tr><td>Integer       </td></tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td></tr>
 *   <tr><td>String        </td></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="9">Byte</td><td rowspan="9"></td></tr>
 *   <tr><td>Short         </td></tr>
 *   <tr><td>Integer       </td></tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td></tr>
 *   <tr><td>String        </td></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="9">Short</td><td rowspan="9"></td></tr>
 *   <tr><td>Short         </td></tr>
 *   <tr><td>Integer       </td></tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td></tr>
 *   <tr><td>String        </td></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="10">Integer</td><td rowspan="10"></td></tr>
 *   <tr><td>Short         </td></tr>
 *   <tr><td>Integer       </td></tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td></tr>
 *   <tr><td>String        </td></tr>
 *   <tr><td>java.util.Date<br><i>(since 1.8.0)</i></td></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="10">Long</td><td rowspan="10"></td></tr>
 *   <tr><td>Short         </td></tr>
 *   <tr><td>Integer       </td></tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td></tr>
 *   <tr><td>String        </td></tr>
 *   <tr><td>java.util.Date<br><i>(since 1.8.0)</i></td></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="9">Float</td><td rowspan="9"></td></tr>
 *   <tr><td>Short         </td></tr>
 *   <tr><td>Integer       </td></tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td></tr>
 *   <tr><td>String        </td></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="9">Double</td><td rowspan="9"></td></tr>
 *   <tr><td>Short         </td></tr>
 *   <tr><td>Integer       </td></tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td></tr>
 *   <tr><td>String        </td></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="9">BigDecimal</td><td rowspan="9"></td></tr>
 *   <tr><td>Short         </td></tr>
 *   <tr><td>Integer       </td></tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td></tr>
 *   <tr><td>String        </td></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="9">Character</td><td rowspan="9"></td></tr>
 *   <tr><td>Short         </td></tr>
 *   <tr><td>Integer       </td></tr>
 *   <tr><td>Long          </td></tr>
 *   <tr><td>Float         </td></tr>
 *   <tr><td>Double        </td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>Character     </td></tr>
 *   <tr><td>String        </td></tr>
 *
 *   <tr><td>Object        </td><td rowspan="6">String</td><td rowspan="2"></td></tr>
 *   <tr><td>BigDecimal    </td></tr>
 *   <tr><td>java.uitl.Date<br><i>(since 1.4.0)</i></td><td rowspan="2">"yyyy-MM-dd"</td></tr>
 *   <tr><td>java.sql.Date <br><i>(since 1.4.0)</i></td></tr>
 *   <tr><td>Time          <br><i>(since 1.4.0)</i></td><td>"HH:mm:ss"</td></tr>
 *   <tr><td>Timestamp                             </td><td>"yyyy-MM-dd HH:mm:ss.SSS"</td></tr>
 *
 *   <tr><td>Long          </td><td rowspan="4">java.util.Date<br><i>(since 1.4.0)</i></td><td rowspan="4"></td></tr>
 *   <tr><td>Integer<br><i>(since 1.8.0)</i></td></tr>
 *   <tr><td>BigDecimal<br><i>(since 1.8.0)</i></td></tr>
 *   <tr><td>String        </td></tr>
 *
 *   <tr><td>Long          </td><td rowspan="5">java.sql.Date</td><td rowspan="5"></td></tr>
 *   <tr><td>Integer<br><i>(since 1.8.0)</i></td></tr>
 *   <tr><td>BigDecimal<br><i>(since 1.8.0)</i></td></tr>
 *   <tr><td>java.util.Date</td></tr>
 *   <tr><td>String        </td></tr>
 *
 *   <tr><td>Long          </td><td rowspan="5">Time</td><td rowspan="5"></td></tr>
 *   <tr><td>Integer<br><i>(since 1.8.0)</i></td></tr>
 *   <tr><td>BigDecimal<br><i>(since 1.8.0)</i></td></tr>
 *   <tr><td>java.util.Date</td></tr>
 *   <tr><td>String        </td></tr>
 *
 *   <tr><td>Long          </td><td rowspan="5">Timestamp</td><td rowspan="5"></td></tr>
 *   <tr><td>Integer<br><i>(since 1.8.0)</i></td></tr>
 *   <tr><td>BigDecimal<br><i>(since 1.8.0)</i></td></tr>
 *   <tr><td>java.util.Date</td></tr>
 *   <tr><td>String        </td></tr>
 *
 *   <tr><td rowspan="4">Enum<br><i>(since 1.4.0)</i></td>
 *       <td>Integer</td><td rowspan="4"></td></tr>
 *   <tr><td>Byte   </td></tr>
 *   <tr><td>Short  </td></tr>
 *   <tr><td>Long   </td></tr>
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

		@throws NullPointerException <b>sourceType</b> または <b>destinType</b> が <b>null</b> の場合
	*/
	public static String key(Class<?> sourceType, Class<?> destinType) {
		return null;
	}

	/**
		<b>TypeConverter</b> マップに <b>TypeConverter</b> 配列の各要素を関連付けます。

		@param typeConverterMap <b>TypeConverter</b> マップ
		@param typeConverters <b>TypeConverter</b> オブジェクト配列

		@throws NullPointerException <b>typeConverterMap</b>, <b>typeConverters</b> または <b>typeConverters</b> の要素が <b>null</b> の場合
	*/
	public static void put(Map<String, TypeConverter<?, ?>> typeConverterMap, TypeConverter<?, ?>... typeConverters) {
	}

	/**
		<b>typeConverterMap</b> から
		<b>sourceType</b> を <b>destinType</b> に変換する <b>TypeConverter</b> オブジェクトを返します。<br>

		<b>sourceType</b> と <b>destinType</b> の組み合わせでマッチする
		<b>TypeConverter</b> オブジェクトが見つからない場合は、
		<b>sourceType</b> のスーパークラスやインタフェースでマッチするのを探します。<br>

		それでも見つからない場合は、<b>null</b> を返します。<br>

		スーパークラスまたはインターフェースで見つかった場合は、次回は直接見つかるようにマップに登録します。<br>

		@param <ST> 変換元のデータ型
		@param <DT> 変換先のデータ型

		@param typeConverterMap <b>TypeConverter</b> マップ
		@param sourceType 変換元のデータ型クラス
		@param destinType 変換先のデータ型クラス

		@return TypeConverter オブジェクト (見つからない場合は <b>null</b>)

		@throws NullPointerException typeConverterMap, <b>sourceType</b> または <b>destinType</b> が <b>null</b> の場合
	*/
	public static <ST, DT> TypeConverter<ST, DT> get(Map<String, TypeConverter<?, ?>> typeConverterMap, Class<ST> sourceType, Class<DT> destinType) {
		return null;
	}

	/**
		<b>source</b> == <b>null</b> の場合は、<b>null</b> を返します。<br>
		<b>destinType.isInstance(source)</b> の場合は、<b>source</b> を変換しないで返します。<br>
		コンバータが見つかった場合は、そのコンバータで <b>source</b> を変換したオブジェクトを返します。

		@param <ST> 変換元のデータ型
		@param <DT> 変換先のデータ型

		@param typeConverterMap <b>TypeConverter</b> マップ
		@param source 変換元のオブジェクト (<b>null</b> 可)
		@param destinType 変換先のデータ型クラス (プリミティブ型以外)

		@return データ型を変換されたオブジェクト (<b>null</b> 有)

		@throws NullPointerException <b>typeConverterMap</b> または <b>destinType</b> が <b>null</b> の場合
		@throws ConvertException コンバータが見つからない場合か変換処理で精度が落ちた場合
	*/
	public static <ST, DT> DT convert(Map<String, TypeConverter<?, ?>> typeConverterMap, ST source, Class<DT> destinType) {
		return null;
	}

	/**
	 * <b>TypeConverter</b> を構築します。
	 *
	 * @param sourceType 変換元のデータ型クラス
	 * @param destinType 変換先のデータ型クラス
	 * @param function 変換を実行する関数
	 *
	 * @throws NullPointerException <b>sourceType</b>, <b>destinType</b> または <b>function</b> が <b>null</b> の場合
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
	 * @throws NullPointerException <b>typeConverter1</b> または <b>typeConverter2</b> が <b>null</b> の場合
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

	/**
		データベースから取得した値をフィールドに格納する際のデータ変換に使用する <b>TypeConverter</b> マップです。<br>
	*/
	public static final Map<String, TypeConverter<?, ?>> typeConverterMap = null;
}
