// SQLite.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.lightsleep.Sql;
import org.lightsleep.component.SqlString;
import org.lightsleep.helper.TypeConverter;

/**
 * A database handler for
 * <a href="https://www.sqlite.org/" target="SQLite">SQLite</a>.
 *
 * <p>
 * The object of this class has a <b>TypeConverter</b> map
 * with the following additional <b>TypeConverter</b> to
 * {@linkplain Standard#typeConverterMap}.
 * </p>
 *
 * <table class="additional">
 *   <caption><span>Registered TypeConverter objects</span></caption>
 *   <tr><th>Source data type</th><th>Destination data type</th><th>Conversion Contents</th></tr>
 *   <tr><td>Boolean       </td><td rowspan="7">SqlString</td><td>false -&gt; <code>0</code><br>true -&gt; <code>1</code></td></tr>
 *   <tr><td>String        </td><td><code>'...'</code><br><code>?</code> <i>(SQL parameter)</i> if the string is long</td></tr>
 *   <tr><td>java.util.Date</td><td rowspan="2"><code>'yyyy-MM-dd'</code></td></tr>
 *   <tr><td>java.sql.Date </td></tr>
 *   <tr><td>Time          </td><td><code>'HH:mm:ss'</code></td></tr>
 *   <tr><td>Timestamp     </td><td><code>'yyyy-MM-dd HH:mm:ss.SSS'</code></td></tr>
 *   <tr><td>byte[]        </td><td><code>?</code> <i>(SQL parameter)</i></td></tr>
 * </table>
 *
 * @since 1.7.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class SQLite extends Standard {
	// The SQLite instance
	private static final Database instance = new SQLite();

	/**
	 * Returns the <b>SQLite</b> instance.
	 *
	 * @return the <b>SQLite</b> instance
	 */
	public static Database instance() {
		return instance;
	}

	/**
	 * Constructs a new <b>SQLite</b>.
	 */
	protected SQLite() {
		// boolean -> 0, 1
		TypeConverter.put(typeConverterMap, booleanToSql01Converter);

	// 1.9.0
		// String -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, SqlString.class, object -> {
				if (object.length() > maxStringLiteralLength)
					return new SqlString(SqlString.PARAMETER, object); // SQL Parameter

				StringBuilder buff = new StringBuilder(object.length() + 2);
				buff.append('\'');
				for (char ch : object.toCharArray()) {
					if (ch == '\'')
						buff.append(ch);
					buff.append(ch);
				}
				buff.append('\'');

				return new SqlString(buff.toString());
			})
		);
	////

		// java.util.Date -> String -> SqlString
		TypeConverter.put(typeConverterMap,
		// 1.8.0
		//	new TypeConverter<>(java.util.Date.class, SqlString.class,
		//		TypeConverter.get(typeConverterMap, java.util.Date.class, String.class).function()
		//		.andThen(object -> new SqlString('\'' + object + '\''))
		//	)
			new TypeConverter<>(
				TypeConverter.get(typeConverterMap, java.util.Date.class, String.class),
				TypeConverter.get(typeConverterMap, String.class, SqlString.class)
			)
		);

		// java.sql.Date -> String -> SqlString
		TypeConverter.put(typeConverterMap,
		// 1.8.0
		//	new TypeConverter<>(Date.class, SqlString.class,
		//		TypeConverter.get(typeConverterMap, Date.class, String.class).function()
		//		.andThen(object -> new SqlString('\''+ object + '\''))
		//	)
			new TypeConverter<>(
				TypeConverter.get(typeConverterMap, Date.class, String.class),
				TypeConverter.get(typeConverterMap, String.class, SqlString.class)
			)
		);

		// Time -> String -> SqlString
		TypeConverter.put(typeConverterMap,
		// 1.8.0
		//	new TypeConverter<>(Time.class, SqlString.class,
		//		TypeConverter.get(typeConverterMap, Time.class, String.class).function()
		//		.andThen(object -> new SqlString('\'' + object + '\''))
		//	)
			new TypeConverter<>(
				TypeConverter.get(typeConverterMap, Time.class, String.class),
				TypeConverter.get(typeConverterMap, String.class, SqlString.class)
			)
		);

		// Timestamp -> String -> SqlString
		TypeConverter.put(typeConverterMap,
		// 1.8.0
		//	new TypeConverter<>(Timestamp.class, SqlString.class,
		//		TypeConverter.get(typeConverterMap, Timestamp.class, String.class).function()
		//		.andThen(object -> new SqlString('\'' + object + '\''))
		//	)
			new TypeConverter<>(
				TypeConverter.get(typeConverterMap, Timestamp.class, String.class),
				TypeConverter.get(typeConverterMap, String.class, SqlString.class)
			)
		);

		// byte[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(byte[].class, SqlString.class, object ->
				new SqlString(SqlString.PARAMETER, object))
		);
	}

// 1.8.2
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public <E> String selectSql(Sql<E> sql, List<Object> parameters) {
//		StringBuilder buff = new StringBuilder();
//
//		// SELECT ... FROM ... WHERE ... GROUP BY ... HAVING ...
//		buff.append(subSelectSql(sql, parameters));
//
//		// ORDER BY ...
//		if (!sql.getOrderBy().isEmpty())
//			buff.append(' ').append(sql.getOrderBy().toString(sql, parameters));
//
//		if (supportsOffsetLimit()) {
//			// LIMIT ...
//			if (sql.getLimit() != Integer.MAX_VALUE)
//				buff.append(" LIMIT ").append(sql.getLimit());
//
//			// OFFSET ...
//			if (sql.getOffset() != 0)
//				buff.append(" OFFSET ").append(sql.getOffset());
//		}
//
//		// FOR UPDATE
//		if (sql.isForUpdate()) {
//			buff.append(" /* dose not support FOR UPDATE */");
//
//			// NO WAIT
//			if (sql.isNoWait())
//				buff.append(" /* dose not support NO WAIT */");
//		}
//
//		return buff.toString();
//	}
////

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.9.0
	 */
	@Override
	protected <E> void appendForUpdate(StringBuilder buff, Sql<E> sql) {
		// FOR UPDATE
		if (sql.isForUpdate())
			throw new UnsupportedOperationException("forUpdate");
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.8.2
	 */
	@Override
	public boolean supportsOffsetLimit() {
		return true;
	}
}
