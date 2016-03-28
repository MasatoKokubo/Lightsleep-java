/*
	Standard.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.database;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.mkokubo.lightsleep.helper.ColumnInfo;
import org.mkokubo.lightsleep.helper.ConvertException;
import org.mkokubo.lightsleep.helper.Resource;
import org.mkokubo.lightsleep.Sql;
import org.mkokubo.lightsleep.component.Condition;
import org.mkokubo.lightsleep.component.SqlString;
import org.mkokubo.lightsleep.helper.TypeConverter;

/**
	A database handler that does not depend on the particular DBMS.

	The object of this class has a <b>TypeConverter</b> map
	with the following additional <b>TypeConverter</b> to
	{@linkplain org.mkokubo.lightsleep.helper.TypeConverter#typeConverterMap}.

	<table class="additinal">
		<caption>TypeConverter objects that are registered</caption>
		<tr><th>Source data type</th><th>Destination data type</th></tr>
		<tr><td>boolean       </td><td>{@linkplain org.mkokubo.lightsleep.component.SqlString} (FALSE, TRUE)</td></tr>
		<tr><td>Byte          </td><td rowspan="15">{@linkplain org.mkokubo.lightsleep.component.SqlString}</td></tr>
		<tr><td>Short         </td></tr>
		<tr><td>Integer       </td></tr>
		<tr><td>Long          </td></tr>
		<tr><td>Float         </td></tr>
		<tr><td>Double        </td></tr>
		<tr><td>BigInteger    </td></tr>
		<tr><td>BigDecimal    </td></tr>
		<tr><td>String        </td></tr>
		<tr><td>Character     </td></tr>
		<tr><td>java.sql.Date </td></tr>
		<tr><td>Time          </td></tr>
		<tr><td>Timestamp     </td></tr>
		<tr><td>byte[]        </td></tr>
		<tr><td>Enum          </td></tr>
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
	protected Standard() {
		// boolean -> FALSE, TRUE
		TypeConverter.put(typeConverterMap, booleanToSqlFalseTrueConverter);

		// Byte.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Byte.class, SqlString.class, object -> new SqlString(String.valueOf(object)))
		);

		// Short.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Short.class, SqlString.class, object -> new SqlString(String.valueOf(object)))
		);

		// Integer.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Integer.class, SqlString.class, object -> new SqlString(String.valueOf(object)))
		);

		// Long.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Long.class, SqlString.class, object -> new SqlString(String.valueOf(object)))
		);

		// Float.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Float.class, SqlString.class, object -> new SqlString(String.valueOf(object)))
		);

		// Double.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Double.class, SqlString.class, object -> new SqlString(String.valueOf(object)))
		);

		// BigInteger.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigInteger.class, SqlString.class, object -> new SqlString(object.toString()))
		);

		// BigDecimal.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigDecimal.class, SqlString.class, object -> new SqlString(object.toPlainString()))
		);

		// String.class -> SqlString.class
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

		// Character.class -> String.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Character.class, SqlString.class,
				TypeConverter.get(typeConverterMap, Character.class, String.class).function()
				.andThen(TypeConverter.get(typeConverterMap, String.class, SqlString.class).function())
			)
		);

		// Date.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Date.class, SqlString.class, object -> new SqlString("DATE'" + object + '\''))
		);

		// Time.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Time.class, SqlString.class, object -> new SqlString("TIME'" + object + '\''))
		);

		// Timestamp.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Timestamp.class, SqlString.class, object ->
				new SqlString("TIMESTAMP'" + object + '\''))
		);

		// byte[].class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(byte[].class, SqlString.class, object -> new SqlString("?"))
		);

		// Enum -> String.class -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Enum.class, SqlString.class,
				TypeConverter.get(typeConverterMap, Enum.class, String.class).function()
				.andThen(TypeConverter.get(typeConverterMap, String.class, SqlString.class).function())
			)
		);
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

		if (isEnableOffset()) {
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
				.filter(sqlColumnInfo -> sqlColumnInfo.columnInfo().selectable())
				.forEach(sqlColumnInfo -> {
					buff.append(delimiter[0]);
					delimiter[0] = ", ";

					ColumnInfo columnInfo = sqlColumnInfo.columnInfo();
					String tableAlias  = sqlColumnInfo.tableAlias();
					String columnName  = columnInfo.getColumnName (tableAlias);
					String columnAlias = columnInfo.getColumnAlias(tableAlias);

					if (columnInfo.selectExpression().isEmpty()) {
						if (!sql.getGroupBy().isEmpty()) buff.append("MIN(");

						// No expression ->  column name
						buff.append(columnName);

						if (!sql.getGroupBy().isEmpty()) buff.append(")");

						//  column alias
						if (!columnAlias.equals(columnName))
							buff.append(" AS ").append(columnAlias);

					} else {
						// Given expression
						buff.append(columnInfo.selectExpression().toString(sql, parameters));

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
				buff.append(delimiter[0])
					.append(columnInfo.insertExpression().toString(sql, parameters));
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

		// SET column name =  value, ...
		buff.append(" SET ");
		String[] delimiter = new String[] {""};
		sql.selectedColumnInfoStream()
			.filter(columnInfo -> !columnInfo.isKey() && columnInfo.updatable())
			.forEach(columnInfo -> {
				buff.append(delimiter[0])
					.append(columnInfo.columnName())
					.append(" = ")
					.append(columnInfo.updateExpression().toString(sql, parameters));
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
