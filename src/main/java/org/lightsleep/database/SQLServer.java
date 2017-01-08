// SQLServer.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.function.Supplier;

import org.lightsleep.Sql;
import org.lightsleep.component.Condition;
import org.lightsleep.component.SqlString;
import org.lightsleep.helper.TypeConverter;

/**
 * A database handler for
 * <a href="https://www.microsoft.com/ja-jp/server-cloud/products-SQL-Server-2014.aspx" target="SQL Server">Microsoft SQL Server</a>.<br>
 *
 * The object of this class has a <b>TypeConverter</b> map
 * with the following additional <b>TypeConverter</b> to
 * {@linkplain org.lightsleep.helper.TypeConverter#typeConverterMap}.
 *
 * <table class="additional">
 *   <caption><span>Registered TypeConverter objects</span></caption>
 *   <tr><th>Source data type</th><th>Destination data type</th></tr>
 *   <tr><td>boolean</td><td>{@linkplain org.lightsleep.component.SqlString} (0, 1)</td></tr>
 *   <tr><td>String </td><td>{@linkplain org.lightsleep.component.SqlString}</td></tr>
 * </table>

 * @since 1.0.0
 * @author Masato Kokubo
 */
public class SQLServer extends Standard {
	// The SQLServer instance
	private static final Database instance = new SQLServer();

	/**
	 * Returns the <b>SQLServer</b> instance.
	 *
	 * @return the <b>SQLServer</b> instance
	 */
	public static Database instance() {
		return instance;
	}

	/**
	 * Constructs a new <b>SQLServer</b>.
	 */
	protected SQLServer() {
		// boolean -> 0, 1
		TypeConverter.put(typeConverterMap, booleanToSql01Converter);

		// String.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, SqlString.class, object -> {
				if (object.length() > maxStringLiteralLength)
					return SqlString.PARAMETER; // SQL Paramter

				StringBuilder buff = new StringBuilder(object.length() + 2);
				buff.append('\'');

				char[] chars = object.toCharArray();
				boolean inLiteral = true;
				for (int index = 0; index < chars.length; ++index) {
					char ch = chars[index];
					if (ch >= ' ' && ch != '\u007F') {
						// Literal representation
						if (!inLiteral) {
							// Outside of the literal
							buff.append("+'");
							inLiteral = true;
						}
						if (ch == '\'') buff.append("''");
						else buff.append(ch);
					} else {
						// Functional representation
						if (inLiteral) {
							// Inside of the literal
							buff.append('\'');
							inLiteral = false;
						}
						buff.append("+CHAR(").append((int)ch).append(')');
					}
				}

				if (inLiteral)
					buff.append('\'');

				return new SqlString(buff.toString());
			})
		);

		// Date.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<Date, SqlString>(Date.class, SqlString.class, object ->
				new SqlString("CAST('" + object + "' AS DATE)"))
		);

		// Time.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<Time, SqlString>(Time.class, SqlString.class, object ->
				new SqlString("CAST('" + object + "' AS TIME)"))
		);

		// Timestamp.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<Timestamp, SqlString>(Timestamp.class, SqlString.class, object ->
				new SqlString("CAST('" + object + "' AS DATETIME2)"))
		);
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
		buff.append(' ').append(sql.getOrderBy().toString(sql, parameters));

		return buff.toString();
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

		// the column names, ...
		buff.append(columnsSupplier.get());

		// FROM table name
		buff.append(" FROM ").append(sql.entityInfo().tableName());

		// table alias
		if (!sql.tableAlias().isEmpty())
			buff.append(" ").append(sql.tableAlias());

		// FOR UPDATE
		if (sql.isForUpdate()) {
			// NO WAIT
			if (sql.isNoWait())
				buff.append(" WITH (ROWLOCK,UPDLOCK,NOWAIT)");
			else
				buff.append(" WITH (ROWLOCK,UPDLOCK)");
		}

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
}
