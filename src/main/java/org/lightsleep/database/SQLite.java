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
	/**
	 * The only instance of this class
	 *
	 * @since 2.1.0
	 */
	public static final SQLite instance = new SQLite();

	/**
	 * Returns the only instance of this class.
	 *
	 * <p>
	 * @deprecated As of release 2.1.0, instead use {@link #instance}
	 * </p>
	 *
	 * @return the only instance of this class
	 */
	@Deprecated
	public static Database instance() {
		return instance;
	}

	/**
	 * Constructs a new <b>SQLite</b>.
	 */
	protected SQLite() {
		// boolean -> 0, 1
		TypeConverter.put(typeConverterMap, booleanToSql01Converter);

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

		// java.util.Date -> String -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(
				TypeConverter.get(typeConverterMap, java.util.Date.class, String.class),
				TypeConverter.get(typeConverterMap, String.class, SqlString.class)
			)
		);

		// java.sql.Date -> String -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(
				TypeConverter.get(typeConverterMap, Date.class, String.class),
				TypeConverter.get(typeConverterMap, String.class, SqlString.class)
			)
		);

		// Time -> String -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(
				TypeConverter.get(typeConverterMap, Time.class, String.class),
				TypeConverter.get(typeConverterMap, String.class, SqlString.class)
			)
		);

		// Timestamp -> String -> SqlString
		TypeConverter.put(typeConverterMap,
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

	/**
	 * {@inheritDoc}
	 *
	 * @since 2.2.0
	 */
	@Override
	public String maskPassword(String jdbcUrl) {
		return jdbcUrl;
	}
}
