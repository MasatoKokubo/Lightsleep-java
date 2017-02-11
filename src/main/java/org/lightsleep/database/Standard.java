// Standard.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.lightsleep.Sql;
import org.lightsleep.component.Condition;
import org.lightsleep.component.Expression;
import org.lightsleep.component.SqlString;
import org.lightsleep.helper.ColumnInfo;
import org.lightsleep.helper.ConvertException;
import org.lightsleep.helper.Resource;
import org.lightsleep.helper.TypeConverter;
import org.lightsleep.helper.Utils;

/**
 * A database handler that does not depend on the particular DBMS.
 *
 * The object of this class has a <b>TypeConverter</b> map
 * with the following additional <b>TypeConverter</b> to
 * {@linkplain org.lightsleep.helper.TypeConverter#typeConverterMap}.
 *
 * <table class="additional">
 *   <caption><span>Registered TypeConverter objects</span></caption>
 *   <tr><th>Source data type</th><th>Destination data type</th><th>Conversion Format</th></tr>
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
 *   <tr><td>Boolean        </td><td rowspan="26">{@linkplain org.lightsleep.component.SqlString}</td><td>FALSE or TRUE</td></tr>
 *   <tr><td>Object         </td><td>'...'</td></tr>
 *   <tr><td>Character      </td><td>'...'</td></tr>
 *   <tr><td>BigDecimal     </td><td></td></tr>
 *   <tr><td>String         </td><td><i>sql parameter (?)</i> if too long, '...' otherwise</td></tr>
 *   <tr><td>java.util.Date<br><i>(since 1.4.0)</i></td><td rowspan="2">DATE'yyyy-MM-dd'</td></tr>
 *   <tr><td>java.sql.Date  </td></tr>
 *   <tr><td>Time           </td><td>TIME'HH:mm:ss'</td></tr>
 *   <tr><td>Timestamp      </td><td>TIMESTAMP'yyyy-MM-dd HH:mm:ss.SSS'</td></tr>
 *   <tr><td>Enum           </td><td></td></tr>
 *   <tr><td>byte[]         </td><td><i>sql parameter (?)</i> if too long, X'...' otherwise</td></tr>
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
	 * The maximum length of string literal when creates SQL.<br>
	 * If the string literal exceeds this length, it generated as SQL parameters (?).<br>
	 * The value of <b>maxStringLiteralLength</b> of lightsleep.properties has been set.
	 * (if undefined, 128)
	 */
// 1.7.0
//	protected static final int maxStringLiteralLength = Resource.globalResource.get(Integer.class, "maxStringLiteralLength", 128);
	public static final int maxStringLiteralLength = Resource.globalResource.get(Integer.class, "maxStringLiteralLength", 128);
////

	/**
	 * The maximum length of binary literal when creates SQL.<br>
	 * If the binary literal exceeds this length, it generated as SQL parameters (?).<br>
	 * The value of <b>maxBinaryLiteralLength</b> of lightsleep.properties has been set.
	 * (if undefined, 128)
	 */
// 1.7.0
//	protected static final int maxBinaryLiteralLength = Resource.globalResource.get(Integer.class, "maxBinaryLiteralLength", 128);
	public static final int maxBinaryLiteralLength = Resource.globalResource.get(Integer.class, "maxBinaryLiteralLength", 128);
////

// 1.8.0
//	/**
//	 * <b>TypeConverter</b> object to convert
//	 * from <b>boolean</b> to <b>SqlString</b> (FALSE or TRUE)
//	 */
//	public static final TypeConverter<Boolean, SqlString> booleanToSqlFalseTrueConverter =
//		new TypeConverter<>(Boolean.class, SqlString.class, object -> new SqlString(object ? "TRUE" : "FALSE"));
////

	/**
	 * <b>TypeConverter</b> object to convert
	 * from <b>Boolean</b> to <b>SqlString</b> (0 or 1)
	 */
// 1.8.0
//	public static final TypeConverter<Boolean, SqlString> booleanToSql01Converter =
	protected static final TypeConverter<Boolean, SqlString> booleanToSql01Converter =
////
		new TypeConverter<>(Boolean.class, SqlString.class, object -> new SqlString(object ? "1" : "0"));

// 1.8.0 (not used)
//	/**
//	 * <b>TypeConverter</b> object to convert
//	 * from <b>Boolean</b> to <b>SqlString</b> ('0' or '1')
//	 */
//	public static final TypeConverter<Boolean, SqlString> booleanToSqlChar01Converter =
//		new TypeConverter<>(Boolean.class, SqlString.class, object -> new SqlString(object ? "'1'" : "'0'"));
//
//	/**
//	 * <b>TypeConverter</b> object to convert
//	 * from <b>Boolean</b> to <b>SqlString</b> ('N' or 'Y')
//	 */
//	public static final TypeConverter<Boolean, SqlString> booleanToSqlNYConverter =
//		new TypeConverter<>(Boolean.class, SqlString.class, object -> new SqlString(object ? "'Y'" : "'N'"));
//
//	/**
//	 * <b>TypeConverter</b> object to convert
//	 * from <b>String</b> ("N" or "Y") to <b>Boolean</b>
//	 */
//	public static final TypeConverter<String, Boolean> stringNYToBooleanConverter =
//		new TypeConverter<>(String.class, Boolean.class, object -> {
//			if      ("N".equals(object)) return false;
//			else if ("Y".equals(object)) return true;
//			else throw new ConvertException(String.class, object, Boolean.class, null);
//		});
////

	// The Standard instance
	private static final Database instance = new Standard();

	/**
	 * Returns the <b>Standard</b> instance.
	 *
	 * @return the <b>Standard</b> instance
	 */
	public static Database instance() {
		return instance;
	}

	// The TypeConverter map
	protected final Map<String, TypeConverter<?, ?>> typeConverterMap = new LinkedHashMap<>(TypeConverter.typeConverterMap);

	/**
	 * Constructs a new <b>Standard</b>.
	 */
	@SuppressWarnings("unchecked")
	protected Standard() {
		// Clob -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Clob.class, String.class, object -> {
				try {
					long length = object.length();
					if (length > Integer.MAX_VALUE)
						throw new ConvertException(Clob.class, "length=" + length, String.class);
					return object.getSubString(1L, (int)length);
				}
				catch (SQLException e) {
					throw new ConvertException(Clob.class, object, String.class, null, e);
				}
			})
		);

		// Blob -> byte[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Blob.class, byte[].class, object -> {
				try {
					long length = object.length();
					if (length > Integer.MAX_VALUE)
						throw new ConvertException(Blob.class, "length=" + length, byte[].class);
					return object.getBytes(1L, (int)length);
				}
				catch (SQLException e) {
					throw new ConvertException(Blob.class, object, byte[].class, null, e);
				}
			})
		);

	// java.sql.Array -> *[]
		// java.sql.Array -> boolean[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, boolean[].class, object -> toArray(object, boolean[].class, boolean.class))
		);

		// java.sql.Array -> byte[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, byte[].class, object -> toArray(object, byte[].class, byte.class))
		);

		// java.sql.Array -> short[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, short[].class, object -> toArray(object, short[].class, short.class))
		);

		// java.sql.Array -> int[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, int[].class, object -> toArray(object, int[].class, int.class))
		);

		// java.sql.Array -> long[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, long[].class, object -> toArray(object, long[].class, long.class))
		);

		// java.sql.Array -> float[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, float[].class, object -> toArray(object, float[].class, float.class))
		);

		// java.sql.Array -> double[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, double[].class, object -> toArray(object, double[].class, double.class))
		);

		// java.sql.Array -> BigDecimal[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, BigDecimal[].class, object -> toArray(object, BigDecimal[].class, BigDecimal.class))
		);

		// java.sql.Array -> String[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, String[].class, object -> toArray(object, String[].class, String.class))
		);

		// java.sql.Array -> java.util.Date[] (since 1.4.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, java.util.Date[].class, object -> toArray(object, java.util.Date[].class, java.util.Date.class))
		);

		// java.sql.Array -> Date[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, Date[].class, object -> toArray(object, Date[].class, Date.class))
		);

		// java.sql.Array -> Time[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, Time[].class, object -> toArray(object, Time[].class, Time.class))
		);

		// java.sql.Array -> Timestamp[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, Timestamp[].class, object -> toArray(object, Timestamp[].class, Timestamp.class))
		);

	//	// java.sql.Array -> ArrayList (since 1.4.0)
	//	TypeConverter.put(typeConverterMap,
	//		new TypeConverter<>(java.sql.Array.class, ArrayList.class, object -> {
	//			ArrayList<Object> list = new ArrayList<>();
	//			try {
	//				Object array = object.getArray();
	//				int length = Array.getLength(array);
	//				for (int index = 0; index < length; ++index)
	//					list.add(Array.get(array, index));
	//			}
	//			catch (Exception e) {
	//				throw new ConvertException(object.getClass(), object, ArrayList.class, e);
	//			}
	//			return list;
	//		})
	//	);

	// * -> SqlString
		// Object -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Object.class, SqlString.class, object -> new SqlString(object.toString()))
		);

		// Boolean -> SqlString(FALSE, TRUE)
	// 1.8.0
	//	TypeConverter.put(typeConverterMap, booleanToSqlFalseTrueConverter);
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Boolean.class, SqlString.class, object -> new SqlString(object ? "TRUE" : "FALSE"))
		);
	////

		// BigDecimal -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigDecimal.class, SqlString.class, object -> new SqlString(object.toPlainString()))
		);

		// String -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, SqlString.class, object -> {
				if (object.length() > maxStringLiteralLength)
				// 1.7.0
				//	return SqlString.PARAMETER; // SQL Paramter
					return new SqlString(SqlString.PARAMETER, object); // SQL Paramter
				////

				StringBuilder buff = new StringBuilder(object.length() + 2);
				buff.append('\'');
				char[] chars = object.toCharArray();
			// 1.7.0
			//	for (int index = 0; index < chars.length; ++index) {
			//		char ch = chars[index];
				for (char ch : chars) {
			////
					if (ch == '\'')
						buff.append(ch);
					buff.append(ch);
				}
				buff.append('\'');
				return new SqlString(buff.toString());
			})
		);

		// Character -> String -> SqlString
		TypeConverter.put(typeConverterMap,
		// 1.8.0
		//	new TypeConverter<>(Character.class, SqlString.class,
		//		TypeConverter.get(typeConverterMap, Character.class, String.class).function()
		//		.andThen(TypeConverter.get(typeConverterMap, String.class, SqlString.class).function())
		//	)
			new TypeConverter<>(
				TypeConverter.get(typeConverterMap, Character.class, String.class),
				TypeConverter.get(typeConverterMap, String.class, SqlString.class)
			)
		);

		// java.util.Date -> String -> SqlString (since 1.4.0)
		TypeConverter.put(typeConverterMap,
		// 1.4.0
		//	new TypeConverter<>(java.util.Date.class, SqlString.class, object ->
		//		new SqlString("DATE'" + new Date(object.getTime()) + '\''))
		// 1.8.0
		//	new TypeConverter<>(java.util.Date.class, SqlString.class,
		//		TypeConverter.get(typeConverterMap, java.util.Date.class, String.class).function()
		//		.andThen(object -> new SqlString("DATE'" + object + '\''))
		//	)
			new TypeConverter<>(
				TypeConverter.get(typeConverterMap, java.util.Date.class, String.class),
				new TypeConverter<>(String.class, SqlString.class, object -> new SqlString("DATE'" + object + '\''))
			)
		////
		);

		// java.sql.Date -> String -> SqlString
		TypeConverter.put(typeConverterMap,
		// 1.4.0
		//	new TypeConverter<>(Date.class, SqlString.class, object -> new SqlString("DATE'" + object + '\''))
		// 1.8.0
		//	new TypeConverter<>(Date.class, SqlString.class,
		//		TypeConverter.get(typeConverterMap, Date.class, String.class).function()
		//		.andThen(object -> new SqlString("DATE'" + object + '\''))
		//	)
			new TypeConverter<>(
				TypeConverter.get(typeConverterMap, Date.class, String.class),
				new TypeConverter<>(String.class, SqlString.class, object -> new SqlString("DATE'" + object + '\''))
			)
		////
		);

		// Time -> String -> SqlString
		TypeConverter.put(typeConverterMap,
		// 1.4.0
		//	new TypeConverter<>(Time.class, SqlString.class, object -> new SqlString("TIME'" + object + '\''))
		// 1.8.0
		//	new TypeConverter<>(Time.class, SqlString.class,
		//		TypeConverter.get(typeConverterMap, Time.class, String.class).function()
		//		.andThen(object -> new SqlString("TIME'" + object + '\''))
		//	)
			new TypeConverter<>(
				TypeConverter.get(typeConverterMap, Time.class, String.class),
				new TypeConverter<>(String.class, SqlString.class, object -> new SqlString("TIME'" + object + '\''))
			)
		////
		);

		// Timestamp -> String -> SqlString
		TypeConverter.put(typeConverterMap,
		// 1.4.0
		//	new TypeConverter<>(Timestamp.class, SqlString.class, object -> new SqlString("TIMESTAMP'" + object + '\''))
		// 1.8.0
		//	new TypeConverter<>(Timestamp.class, SqlString.class,
		//		TypeConverter.get(typeConverterMap, Timestamp.class, String.class).function()
		//		.andThen(object -> new SqlString("TIMESTAMP'" + object + '\''))
		//	)
			new TypeConverter<>(
				TypeConverter.get(typeConverterMap, Timestamp.class, String.class),
				new TypeConverter<>(String.class, SqlString.class, object -> new SqlString("TIMESTAMP'" + object + '\''))
			)
		////
		);

		// Enum -> String -> SqlString
		TypeConverter.put(typeConverterMap,
		// 1.8.0
		//	new TypeConverter<>(Enum.class, SqlString.class,
		//		TypeConverter.get(typeConverterMap, Enum.class, String.class).function()
		//		.andThen(TypeConverter.get(typeConverterMap, String.class, SqlString.class).function())
		//	)
			new TypeConverter<>(
				TypeConverter.get(typeConverterMap, Enum.class, String.class),
				TypeConverter.get(typeConverterMap, String.class, SqlString.class)
			)
		);

		// boolean[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(boolean[].class, SqlString.class, object ->
			// 1.4.0
			//	toSqlString(object, boolean.class))
				toSqlString(object, Boolean.class))
			////
		);

		// char[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(char[].class, SqlString.class, object ->
			// 1.4.0
			//	toSqlString(object, char.class))
				toSqlString(object, Character.class))
			////
		);

		// byte[] -> SqlString
		TypeConverter.put(typeConverterMap,
		// 1.7.0
		//	new TypeConverter<>(byte[].class, SqlString.class, object -> new SqlString("?"))
			new TypeConverter<>(byte[].class, SqlString.class, object -> {
				if (object.length > maxBinaryLiteralLength)
					return new SqlString(SqlString.PARAMETER, object); // SQL Paramter

				StringBuilder buff = new StringBuilder(object.length * 2 + 3);
				buff.append("X'");
				for (int value : object) {
					value &= 0xFF;
					char ch = (char)((value >>> 4) + '0');
					if (ch > '9') ch += 'A' - ('9' + 1);
					buff.append(ch);
					ch = (char)((value & 0x0F) + '0');
					if (ch > '9') ch += 'A' - ('9' + 1);
					buff.append(ch);
				}
				buff.append('\'');
				return new SqlString(buff.toString());

			})
		////
		);

	// 1.7.0
		// byte[][] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(byte[][].class, SqlString.class, object ->
				toSqlString(object, byte[].class))
		);
	////

		// short[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(short[].class, SqlString.class, object ->
			// 1.4.0
			//	toSqlString(object, short.class))
				toSqlString(object, Short.class))
			////
		);

		// int[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(int[].class, SqlString.class, object ->
			// 1.4.0
			//	toSqlString(object, int.class))
				toSqlString(object, Integer.class))
			////
		);

		// long[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(long[].class, SqlString.class, object ->
			// 1.4.0
			//	toSqlString(object, long.class))
				toSqlString(object, Long.class))
			////
		);

		// float[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(float[].class, SqlString.class, object ->
			// 1.4.0
			//	toSqlString(object, float.class))
				toSqlString(object, Float.class))
			////
		);

		// double[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(double[].class, SqlString.class, object ->
			// 1.4.0
			//	toSqlString(object, double.class))
				toSqlString(object, Double.class))
			////
		);

		// BigDecimal[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigDecimal[].class, SqlString.class, object ->
				toSqlString(object, BigDecimal.class))
		);

		// String[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String[].class, SqlString.class, object ->
				toSqlString(object, String.class))
		);

		// java.util.Date[] -> SqlString (since 1.4.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.util.Date[].class, SqlString.class, object ->
				toSqlString(object, java.util.Date.class))
		);

		// Date[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Date[].class, SqlString.class, object ->
				toSqlString(object, Date.class))
		);

		// Time[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Time[].class, SqlString.class, object ->
				toSqlString(object, Time.class))
		);

		// Timestamp[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Timestamp[].class, SqlString.class, object ->
				toSqlString(object, Timestamp.class))
		);

		// Iterable -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Iterable.class, SqlString.class, object -> {
				Iterator<Object> iterator = object.iterator();
				Class<?> beforeElementType = null;
				Function<Object, SqlString> function = null;

				StringBuilder buff = new StringBuilder("(");

				for (int index = 0; iterator.hasNext(); ++index) {
					if (index > 0) buff.append(",");
					Object element = iterator.next();
					if (element == null)
						buff.append("NULL");
					else {
						Class<?> elementType = element.getClass();
						if (elementType != beforeElementType) {
						// 1.4.0
						//	function = (Function<Object, SqlString>)
						//		TypeConverter.get(typeConverterMap, elementType, SqlString.class).function();
							TypeConverter<?, SqlString> typeConverter = TypeConverter.get(typeConverterMap, elementType, SqlString.class);
							if (typeConverter == null)
								throw new ConvertException(elementType, element, SqlString.class);

							function = (Function<Object, SqlString>)typeConverter.function();
						////
							beforeElementType = elementType;
						}
						buff.append(function.apply(element).content());
					}
				}

				buff.append(')');
				return new SqlString(buff.toString());
			})
		);

	}

	// Converts an Array to a primitive java array
	@SuppressWarnings("unchecked")
	protected <AT, CT> AT toArray(java.sql.Array object, Class<AT> arrayType, Class<CT> componentType) {
		try {
			Object array = object.getArray();
			if (arrayType.isInstance(array))
				return (AT)array;

			AT result = (AT)Array.newInstance(componentType, Array.getLength(array));
			TypeConverter<Object, CT> typeConverter = null;
			for (int index = 0; index < Array.getLength(result); ++index) {
				Object value = Array.get(array, index);
				CT convertedValue = null;
				if (value != null) {
					if (Utils.toClassType(componentType).isInstance(value))
						convertedValue = (CT)value;
					else {
						if (typeConverter == null)
							typeConverter = (TypeConverter<Object, CT>)TypeConverter.get(typeConverterMap, value.getClass(), componentType);
					// 1.4.0
						if (typeConverter == null)
							throw new ConvertException(value.getClass(), value, componentType);
					////
						convertedValue = typeConverter.function().apply(value);
					}
				}
				Array.set(result, index, convertedValue);
			}
			return result;
		}
		catch (Exception e) {
			throw new ConvertException(object.getClass(), object, arrayType, e);
		}
	}

	// Converts an array to a String
	@SuppressWarnings("unchecked")
	protected <CT> SqlString toSqlString(Object array, Class<CT> componentType) {
	// 1.4.0
	//	Function<CT, SqlString> function =
	//		TypeConverter.get(typeConverterMap, componentType, SqlString.class).function();
		TypeConverter<CT, SqlString> typeConverter = TypeConverter.get(typeConverterMap, componentType, SqlString.class);
		if (typeConverter == null)
			throw new ConvertException(componentType, array, SqlString.class);

		Function<CT, SqlString> function = typeConverter.function();
	////
		StringBuilder buff = new StringBuilder("ARRAY[");
	// 1.7.0
		List<Object> parameters = new ArrayList<>();
	////
		for (int index = 0; index < Array.getLength(array); ++ index) {
			if (index > 0) buff.append(",");
		// 1.7.0
		//	buff.append(function.apply((CT)Array.get(array, index)).content());
			SqlString sqlString = function.apply((CT)Array.get(array, index));
			buff.append(sqlString.content());
			parameters.addAll(Arrays.asList(sqlString.parameters()));
		////
		}
		buff.append(']');
		return new SqlString(buff.toString(), parameters.toArray());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String selectSql(Sql<E> sql, List<Object> parameters) {
		StringBuilder buff = new StringBuilder();

		// SELECT ... FROM ... WHERE ... GROUP BY ... HAVING ...
		buff.append(subSelectSql(sql, parameters));

		// ORDER BY ...
		if (!sql.getOrderBy().isEmpty())
			buff.append(' ').append(sql.getOrderBy().toString(sql, parameters));

		if (supportsOffsetLimit()) {
			// LIMIT ...
			if (sql.getLimit() != Integer.MAX_VALUE)
				buff.append(" LIMIT ").append(sql.getLimit());

			// OFFSET ...
			if (sql.getOffset() != 0)
				buff.append(" OFFSET ").append(sql.getOffset());
		}

		// FOR UPDATE
		if (sql.isForUpdate()) {
			buff.append(" FOR UPDATE");

			// NO WAIT
			if (sql.isNoWait())
				buff.append(" NO WAIT");
		}

		return buff.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String subSelectSql(Sql<E> sql, List<Object> parameters) {
		return subSelectSql(sql, () -> {
			//  column name, ...
			StringBuilder buff = new StringBuilder();
			String[] delimiter = new String[] {""};
			sql.selectedJoinSqlColumnInfoStream()
				.filter(sqlColumnInfo -> {
					return sqlColumnInfo.columnInfo().selectable();
				})
				.forEach(sqlColumnInfo -> {
					buff.append(delimiter[0]);
					delimiter[0] = ", ";

					ColumnInfo columnInfo = sqlColumnInfo.columnInfo();
					String tableAlias  = sqlColumnInfo.tableAlias();
					String columnName  = columnInfo.getColumnName (tableAlias);
					String columnAlias = columnInfo.getColumnAlias(tableAlias);

					// gets expression
					Expression expression = sql.getExpression(columnInfo.propertyName());
					if (expression.isEmpty())
						expression = columnInfo.selectExpression();

					if (expression.isEmpty()) {
						if (!sql.getGroupBy().isEmpty()) buff.append("MIN(");

						// No expression ->  column name
						buff.append(columnName);

						if (!sql.getGroupBy().isEmpty()) buff.append(")");

						//  column alias
						if (!columnAlias.equals(columnName))
							buff.append(" AS ").append(columnAlias);

					} else {
						// Given expression
						buff.append(expression.toString(sql, parameters));

						//  column alias
						buff.append(" AS ").append(columnAlias);
					}
				});
			return buff;
		}, parameters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String subSelectSql(Sql<E> sql, Supplier<CharSequence> columnsSupplier, List<Object> parameters) {
		StringBuilder buff = new StringBuilder();

		// SELECT
		buff.append("SELECT ");

		// DISTINCT
		if (sql.isDistinct())
			buff.append("DISTINCT ");

		//  column name, ...
		buff.append(columnsSupplier.get());

		// FROM table name
		buff.append(" FROM ").append(sql.entityInfo().tableName());

		// table alias
		if (!sql.tableAlias().isEmpty())
			buff.append(" ").append(sql.tableAlias());

		// INNER / OUTER JOIN ...
		if (!sql.getJoinInfos().isEmpty()) {
		// 1.5.1
		//	sql.getJoinInfos().stream()
			sql.getJoinInfos()
		////
				.forEach(joinInfo -> {
					// INNER/OUTER JOIN table name
					buff.append(joinInfo.joinType().sql()).append(joinInfo.entityInfo().tableName());

					// table alias
					if (!joinInfo.tableAlias().isEmpty())
						buff.append(" ").append(joinInfo.tableAlias());

					// ON ...
					if (!joinInfo.on().isEmpty())
						buff.append(" ON ").append(joinInfo.on().toString(sql, parameters));
				});
		}

		// WHERE ...
		if (!sql.getWhere().isEmpty() && sql.getWhere() != Condition.ALL)
			buff.append(" WHERE ").append(sql.getWhere().toString(sql, parameters));

		// GROUP BY ...
		if (!sql.getGroupBy().isEmpty())
			buff.append(' ').append(sql.getGroupBy().toString(sql, parameters));

		// HAVING ...
		if (!sql.getHaving().isEmpty())
			buff.append(" HAVING ").append(sql.getHaving().toString(sql, parameters));

		return buff.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String insertSql(Sql<E> sql, List<Object> parameters) {
		StringBuilder buff = new StringBuilder();

		// INSERT INTO table name
		buff.append("INSERT INTO ").append(sql.entityInfo().tableName());

		// table alias
		if (!sql.tableAlias().isEmpty())
			buff.append(" ").append(sql.tableAlias());

		// ( column name, ...
		buff.append(" (");
		String[] delimiter = new String[] {""};
		sql.entityInfo().columnInfos().stream()
		// 1.2.0
			.filter(columnInfo -> columnInfo.insertable())
		////
			.forEach(columnInfo -> {
				buff.append(delimiter[0])
					.append(columnInfo.columnName());
				delimiter[0] = ", ";
			});

		// ) VALUES (value, ...)
		buff.append(") VALUES (");
		delimiter[0] = "";
		sql.columnInfoStream()
			.filter(columnInfo -> columnInfo.insertable())
			.forEach(columnInfo -> {
				// gets expression
				Expression expression = sql.getExpression(columnInfo.propertyName());
				if (expression.isEmpty())
					expression = columnInfo.insertExpression();

				buff.append(delimiter[0])
					.append(expression.toString(sql, parameters));
				delimiter[0] = ", ";
			});
		buff.append(")");

		return buff.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String updateSql(Sql<E> sql, List<Object> parameters) {
		StringBuilder buff = new StringBuilder();

		// UPDATE table name
		buff.append("UPDATE ").append(sql.entityInfo().tableName());

		// table alias
		if (!sql.tableAlias().isEmpty())
			buff.append(" ").append(sql.tableAlias());

		// SET column name =  value, ...
		buff.append(" SET ");
		String[] delimiter = new String[] {""};
		sql.selectedColumnInfoStream()
			.filter(columnInfo -> !columnInfo.isKey() && columnInfo.updatable())
			.forEach(columnInfo -> {
				// gets expression
				Expression expression = sql.getExpression(columnInfo.propertyName());
				if (expression.isEmpty())
					expression = columnInfo.updateExpression();

				buff.append(delimiter[0])
					.append(columnInfo.columnName())
					.append("=")
					.append(expression.toString(sql, parameters));
				delimiter[0] = ", ";
			});

		// WHERE ...
		if (sql.getWhere() != Condition.ALL)
			buff.append(" WHERE ").append(sql.getWhere().toString(sql, parameters));

		// ORDER BY ...
		if (!sql.getOrderBy().isEmpty())
			buff.append(' ').append(sql.getOrderBy().toString(sql, parameters));

		// LIMIT ...
		if (sql.getLimit() != Integer.MAX_VALUE)
			buff.append(" LIMIT ").append(sql.getLimit());

		return buff.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String deleteSql(Sql<E> sql, List<Object> parameters) {
		StringBuilder buff = new StringBuilder();

		// DELETE FROM table name
		buff.append("DELETE FROM ").append(sql.entityInfo().tableName());

		// table alias
		if (!sql.tableAlias().isEmpty())
			buff.append(" ").append(sql.tableAlias());

		// WHERE ...
		if (sql.getWhere() != Condition.ALL)
			buff.append(" WHERE ").append(sql.getWhere().toString(sql, parameters));

		// ORDER BY ...
		if (!sql.getOrderBy().isEmpty())
			buff.append(' ').append(sql.getOrderBy().toString(sql, parameters));

		// LIMIT ...
		if (sql.getLimit() != Integer.MAX_VALUE)
			buff.append(" LIMIT ").append(sql.getLimit());

		return buff.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, TypeConverter<?, ?>> typeConverterMap() {
		return typeConverterMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T convert(Object value, Class<T> type) {
		return TypeConverter.convert(typeConverterMap, value, type);
	}
}
