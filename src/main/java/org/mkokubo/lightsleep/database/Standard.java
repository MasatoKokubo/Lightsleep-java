/*
	Standard.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.database;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.mkokubo.lightsleep.helper.ColumnInfo;
import org.mkokubo.lightsleep.helper.ConvertException;
import org.mkokubo.lightsleep.helper.Resource;
import org.mkokubo.lightsleep.Sql;
import org.mkokubo.lightsleep.component.Condition;
import org.mkokubo.lightsleep.component.Expression;
import org.mkokubo.lightsleep.component.SqlString;
import org.mkokubo.lightsleep.helper.TypeConverter;
import org.mkokubo.lightsleep.helper.Utils;

/**
	A database handler that does not depend on the particular DBMS.

	The object of this class has a <b>TypeConverter</b> map
	with the following additional <b>TypeConverter</b> to
	{@linkplain org.mkokubo.lightsleep.helper.TypeConverter#typeConverterMap}.

	<table class="additinal">
		<caption>TypeConverter objects that are registered</caption>
		<tr><th>Source data type</th><th>Destination data type</th></tr>

		<tr><td>Clob          </td><td>String</td></tr>

		<tr><td>Blob          </td><td>byte[]</td></tr>

		<tr><td rowspan="11">java.sql.Array</td><td>boolean[]   </td></tr>
		<tr>                                    <td>byte[]      </td></tr>
		<tr>                                    <td>short[]     </td></tr>
		<tr>                                    <td>int[]       </td></tr>
		<tr>                                    <td>long[]      </td></tr>
		<tr>                                    <td>double[]    </td></tr>
		<tr>                                    <td>BigDecimal[]</td></tr>
		<tr>                                    <td>String[]    </td></tr>
		<tr>                                    <td>Date[]      </td></tr>
		<tr>                                    <td>Time[]      </td></tr>
		<tr>                                    <td>Timestamp[] </td></tr>

		<tr><td>Boolean        </td><td>{@linkplain org.mkokubo.lightsleep.component.SqlString} (FALSE, TRUE)</td></tr>

		<tr><td>Object         </td><td rowspan="22">{@linkplain org.mkokubo.lightsleep.component.SqlString}</td></tr>
		<tr><td>Character      </td></tr>
		<tr><td>BigDecimal     </td></tr>
		<tr><td>String         </td></tr>
		<tr><td>java.sql.Date  </td></tr>
		<tr><td>Time           </td></tr>
		<tr><td>Timestamp      </td></tr>
		<tr><td>Enum           </td></tr>
		<tr><td>boolean[]      </td></tr>
		<tr><td>char[]         </td></tr>
		<tr><td>byte[]         </td></tr>
		<tr><td>short[]        </td></tr>
		<tr><td>int[]          </td></tr>
		<tr><td>long[]         </td></tr>
		<tr><td>float[]        </td></tr>
		<tr><td>double[]       </td></tr>
		<tr><td>BigDecimal[]   </td></tr>
		<tr><td>String[]       </td></tr>
		<tr><td>java.sql.Date[]</td></tr>
		<tr><td>Time[]         </td></tr>
		<tr><td>Timestamp[]    </td></tr>
		<tr><td>Iterable       </td></tr>
	</table>

	@since 1.0.0
	@author Masato Kokubo
*/
public class Standard implements Database {
	/**
		The maximum length of string literal when creates SQL.<br>
		If the string literal exceeds this length, it generated as SQL parameters (?).<br>
		The value of <b>maxStringLiteralLength</b> of lightsleep.properties has been set.
		(if undefined, 128)
	*/
	protected static final int maxStringLiteralLength = Resource.globalResource.get(Integer.class, "maxStringLiteralLength", 128);

	/**
		The maximum length of binary literal when creates SQL.<br>
		If the binary literal exceeds this length, it generated as SQL parameters (?).<br>
		The value of <b>maxBinaryLiteralLength</b> of lightsleep.properties has been set.
		(if undefined, 128)
	*/
	protected static final int maxBinaryLiteralLength = Resource.globalResource.get(Integer.class, "maxBinaryLiteralLength", 128);

	/**
		<b>TypeConverter</b> object to convert
		from <b>boolean</b> to <b>SqlString</b> (FALSE, TRUE)
	*/
	public static final TypeConverter<Boolean, SqlString> booleanToSqlFalseTrueConverter =
		new TypeConverter<>(Boolean.class, SqlString.class, object -> new SqlString(object ? "TRUE" : "FALSE"));

	/**
		<b>TypeConverter</b> object to convert
		from <b>Boolean</b> to <b>SqlString</b> (0, 1)
	*/
	public static final TypeConverter<Boolean, SqlString> booleanToSql01Converter =
		new TypeConverter<>(Boolean.class, SqlString.class, object -> new SqlString(object ? "1" : "0"));

	/**
		<b>TypeConverter</b> object to convert
		from <b>Boolean</b> to <b>SqlString</b> ('0', '1')
	*/
	public static final TypeConverter<Boolean, SqlString> booleanToSqlChar01Converter =
		new TypeConverter<>(Boolean.class, SqlString.class, object -> new SqlString(object ? "'1'" : "'0'"));

	/**
		<b>TypeConverter</b> object to convert
		from <b>Boolean</b> to <b>SqlString</b> ('N', 'Y')
	*/
	public static final TypeConverter<Boolean, SqlString> booleanToSqlNYConverter =
		new TypeConverter<>(Boolean.class, SqlString.class, object -> new SqlString(object ? "'Y'" : "'N'"));

	/**
		<b>TypeConverter</b> object to convert
		from <b>String</b> ('N', 'Y') to <b>Boolean</b>
	*/
	public static final TypeConverter<String, Boolean> stringNYToBooleanConverter =
		new TypeConverter<>(String.class, Boolean.class, object -> {
			if      ("N".equals(object)) return false;
			else if ("Y".equals(object)) return true;
			else throw new ConvertException(String.class, object, Boolean.class, null);
		});

	// The Standard instance
	private static final Database instance = new Standard();

	/**
		Returns the <b>Standard</b> instance.

		@return the <b>Standard</b> instance
	*/
	public static Database instance() {
		return instance;
	}

	// The TypeConverter map
	protected final Map<String, TypeConverter<?, ?>> typeConverterMap = new LinkedHashMap<>(TypeConverter.typeConverterMap);

	/**
		Constructs a new <b>Standard</b>.
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

	// * -> SqlString
		// Object -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Object.class, SqlString.class, object -> new SqlString(object.toString()))
		);

		// Boolean -> SqlString(FALSE, TRUE)
		TypeConverter.put(typeConverterMap, booleanToSqlFalseTrueConverter);

		// BigDecimal -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigDecimal.class, SqlString.class, object -> new SqlString(object.toPlainString()))
		);

		// String -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, SqlString.class, object -> {
				if (object.length() > maxStringLiteralLength)
					return SqlString.PARAMETER; // SQL Paramter

				StringBuilder buff = new StringBuilder(object.length() + 2);
				buff.append('\'');
				char[] chars = object.toCharArray();
				for (int index = 0; index < chars.length; ++index) {
					char ch = chars[index];
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
			new TypeConverter<>(Character.class, SqlString.class,
				TypeConverter.get(typeConverterMap, Character.class, String.class).function()
				.andThen(TypeConverter.get(typeConverterMap, String.class, SqlString.class).function())
			)
		);

		// Date -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Date.class, SqlString.class, object -> new SqlString("DATE'" + object + '\''))
		);

		// Time -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Time.class, SqlString.class, object -> new SqlString("TIME'" + object + '\''))
		);

		// Timestamp -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Timestamp.class, SqlString.class, object ->
				new SqlString("TIMESTAMP'" + object + '\''))
		);

		// Enum -> String -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Enum.class, SqlString.class,
				TypeConverter.get(typeConverterMap, Enum.class, String.class).function()
				.andThen(TypeConverter.get(typeConverterMap, String.class, SqlString.class).function())
			)
		);

		// boolean[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(boolean[].class, SqlString.class, object ->
				toSqlString(object, boolean.class))
		);

		// char[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(char[].class, SqlString.class, object ->
				toSqlString(object, char.class))
		);

		// byte[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(byte[].class, SqlString.class, object -> new SqlString("?"))
		);

		// short[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(short[].class, SqlString.class, object ->
				toSqlString(object, short.class))
		);

		// int[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(int[].class, SqlString.class, object ->
				toSqlString(object, int.class))
		);

		// long[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(long[].class, SqlString.class, object ->
				toSqlString(object, long.class))
		);

		// float[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(float[].class, SqlString.class, object ->
				toSqlString(object, float.class))
		);

		// double[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(double[].class, SqlString.class, object ->
				toSqlString(object, double.class))
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
							function = (Function<Object, SqlString>)
								TypeConverter.get(typeConverterMap, elementType, SqlString.class).function();
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
		Function<CT, SqlString> function =
			TypeConverter.get(typeConverterMap, componentType, SqlString.class).function();
		StringBuilder buff = new StringBuilder("ARRAY[");
		for (int index = 0; index < Array.getLength(array); ++ index) {
			if (index > 0) buff.append(",");
			buff.append(function.apply((CT)Array.get(array, index)).content());
		}
		buff.append(']');
		return new SqlString(buff.toString());
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public <E> String selectSql(Sql<E> sql, List<Object> parameters) {
		StringBuilder buff = new StringBuilder();

		// SELECT ... FROM ... WHERE ... GROUP BY ... HAVING ...
		buff.append(subSelectSql(sql, parameters));

		// ORDER BY ...
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
				buff.append(" /* NO WAIT (unsupported) */");
		}

		return buff.toString();
	}

	/**
		{@inheritDoc}
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
		{@inheritDoc}
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
			sql.getJoinInfos().stream()
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
		{@inheritDoc}
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
		{@inheritDoc}
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

		return buff.toString();
	}

	/**
		{@inheritDoc}
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

		return buff.toString();
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public Map<String, TypeConverter<?, ?>> typeConverterMap() {
		return typeConverterMap;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public <T> T convert(Object value, Class<T> type) {
		return TypeConverter.convert(typeConverterMap, value, type);
	}
}
