// SQLServer.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.function.Supplier;

import org.lightsleep.Sql;
import org.lightsleep.component.Expression;
import org.lightsleep.component.SqlString;
import org.lightsleep.helper.TypeConverter;

/**
 * A database handler for
 * <a href="https://www.microsoft.com/ja-jp/server-cloud/products-SQL-Server-2014.aspx" target="SQL Server">Microsoft SQL Server</a>.<br>
 *
 * The object of this class has a <b>TypeConverter</b> map
 * with the following additional <b>TypeConverter</b> to
 * {@linkplain Standard#typeConverterMap}.
 *
 * <table class="additional">
 *   <caption><span>Registered TypeConverter objects</span></caption>
 *   <tr><th>Source data type</th><th>Destination data type</th><th>Conversion Contents</th></tr>
 *   <tr><td>Boolean      </td><td rowspan="6">SqlString</td><td>false -&gt; <code>0</code><br>true -&gt; <code>1</code></td></tr>
 *   <tr><td>java.sql.Date</td><td><code>CAST('yyyy:MM:dd' AS DATE)</code></td></tr>
 *   <tr><td>Time         </td><td><code>CAST('HH:mm:ss' AS DATE)</code></td></tr>
 *   <tr><td>Timestamp    </td><td><code>CAST('yyyy-MM-dd HH:mm:ss.SSS' AS DATETIME2)</code></td></tr>
 *   <tr><td>String       </td><td><code>'...'</code><br>Converts control character to <code>'...'+CHAR(n)+'...'</code>.<br><code>?</code> <i>(SQL parameter)</i> if long</td></tr>
 *   <tr><td>byte[]</td><td><code>?</code> <i>(SQL parameter)</i></td></tr>
 * </table>

 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
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
				// 1.7.0
				//	return SqlString.PARAMETER; // SQL Paramter
					return new SqlString(SqlString.PARAMETER, object); // SQL Paramter
				////

				StringBuilder buff = new StringBuilder(object.length() + 2);
				buff.append('\'');

				char[] chars = object.toCharArray();
				boolean inLiteral = true;
			// 1.7.0
			//	for (int index = 0; index < chars.length; ++index) {
			//		char ch = chars[index];
				for (char ch : chars) {
			////
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

		// 1.7.0
		// byte[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(byte[].class, SqlString.class, object ->
				new SqlString(SqlString.PARAMETER, object))
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
	// 1.8.2
	//	buff.append(' ').append(sql.getOrderBy().toString(sql, parameters));
		appendsOrderBy(buff, sql, parameters);
	////

		return buff.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String subSelectSql(Sql<E> sql, Supplier<CharSequence> columnsSupplier, List<Object> parameters) {
		StringBuilder buff = new StringBuilder();

	// 1.8.2
	//	// SELECT
	//	buff.append("SELECT ");
	//
	//	// DISTINCT
	//	if (sql.isDistinct())
	//		buff.append("DISTINCT ");
	//
	//	// the column names, ...
	//	buff.append(columnsSupplier.get());
	//
	//	// FROM table name
	//	buff.append(" FROM ").append(sql.entityInfo().tableName());
	//
	//	// table alias
	//	if (!sql.tableAlias().isEmpty())
	//		buff.append(" ").append(sql.tableAlias());
	//
	//	// FOR UPDATE
	//	if (sql.isForUpdate()) {
	//		// NO WAIT
	//		if (sql.isNoWait())
	//			buff.append(" WITH (ROWLOCK,UPDLOCK,NOWAIT)");
	//		else
	//			buff.append(" WITH (ROWLOCK,UPDLOCK)");
	//	}
	//
	//	// INNER / OUTER JOIN ...
	//	if (!sql.getJoinInfos().isEmpty()) {
	//	// 1.5.1
	//	//	sql.getJoinInfos().stream()
	//		sql.getJoinInfos()
	//	////
	//			.forEach(joinInfo -> {
	//				// INNER/OUTER JOIN table name
	//				buff.append(joinInfo.joinType().sql()).append(joinInfo.entityInfo().tableName());
	//
	//				// table alias
	//				if (!joinInfo.tableAlias().isEmpty())
	//					buff.append(" ").append(joinInfo.tableAlias());
	//
	//				// ON ...
	//				if (!joinInfo.on().isEmpty())
	//					buff.append(" ON ").append(joinInfo.on().toString(sql, parameters));
	//			});
	//	}
	//
	//	// WHERE ...
	//	if (!sql.getWhere().isEmpty() && sql.getWhere() != Condition.ALL)
	//		buff.append(" WHERE ").append(sql.getWhere().toString(sql, parameters));
	//
	//	// GROUP BY ...
	//	if (!sql.getGroupBy().isEmpty())
	//		buff.append(' ').append(sql.getGroupBy().toString(sql, parameters));
	//
	//	// HAVING ...
	//	if (!sql.getHaving().isEmpty())
	//		buff.append(" HAVING ").append(sql.getHaving().toString(sql, parameters));
		// SELECT
		buff.append("SELECT");
	
		// DISTINCT
		appendsDistinct(buff, sql);
	
		// the column names, ...
		buff.append(' ').append(columnsSupplier.get());
	
		// FROM
		buff.append(" FROM");

		// main table name and alias
		appendsMainTable(buff, sql);

		// FOR UPDATE
		appendsForUpdate(buff, sql);
	
		// INNER / OUTER JOIN ...
		appendsJoinTables(buff, sql, parameters);
	
		// WHERE ...
		appendsWhere(buff, sql, parameters);
	
		// GROUP BY ...
		appendsGroupBy(buff, sql, parameters);
	
		// HAVING ...
		appendsHaving(buff, sql, parameters);
	////

		return buff.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.8.4
	 */
	@Override
	public <E> String updateSql(Sql<E> sql, List<Object> parameters) {
		if (sql.getJoinInfos().size() == 0)
			return super.updateSql(sql, parameters);

		StringBuilder buff = new StringBuilder();

		Sql<E> sql2 = new Sql<>(sql.entityInfo().entityClass())
			.setColumns(sql.getColumns())
			.setEntity(sql.entity());

		// Sets expressions to sql2 from sql.
		sql.columnInfoStream().forEach(columnInfo -> {
			String propertyName = columnInfo.getPropertyName("");
			Expression expression = sql.getExpression(propertyName);
			if (!expression.isEmpty())
				sql2.expression(propertyName, expression);
		});

		// UPDATE table name
		buff.append("UPDATE");

		// main table name and alias
		appendsMainTable(buff, sql2);

		// SET column name =  value, ...
		appendsUpdateColumnsAndValues(buff, sql2, parameters);

		// FROM
		buff.append(" FROM");

		// main table name and alias
		appendsMainTable(buff, sql);

		// INNER / OUTER JOIN ...
		appendsJoinTables(buff, sql, parameters);

		// WHERE ...
		appendsWhere(buff, sql, parameters);

		// ORDER BY ...
		appendsOrderBy(buff, sql, parameters);

		return buff.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.8.2
	 */
	@Override
	protected <E> void appendsForUpdate(StringBuilder buff, Sql<E> sql) {
		// FOR UPDATE
		if (sql.isForUpdate()) {
			// NO WAIT
			if (sql.isNoWait())
				buff.append(" WITH (ROWLOCK,UPDLOCK,NOWAIT)");
			else
				buff.append(" WITH (ROWLOCK,UPDLOCK)");
		}
	}
}
