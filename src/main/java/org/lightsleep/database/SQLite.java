// SQLite.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.function.Function;

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
 *   <caption><span>Additional contents of the TypeConverter map</span></caption>
 *   <tr><th colspan="2">Key: Data Types</th><th rowspan="2">Value: Conversion Function</th></tr>
 *   <tr><th>Source</th><th>Destination</th></tr>
 *
 *   <tr><td>Boolean       </td><td rowspan="13">SqlString</td>
 *     <td>
 *       <b>new SqlString("0")</b> <span class="comment">if the source value is <b>false</b></span><br>
 *       <b>new SqlString("1")</b> <span class="comment">if the source value is <b>true</b></span>
 *     </td>
 *   </tr>
 *   <tr><td>String        </td>
 *     <td>
 *       <b>new SqlString("'" + source + "'")</b><br>
 *       <span class="comment">Converts a single quote in the source string to two consecutive single quotes.</span><br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>new SqlString(SqlString.PARAMETER, source)</b> <span class="comment">if the source string is too long</span>
 *     </td>
 *   </tr>
 *   <tr><td>java.util.Date</td>
 *     <td rowspan="10">
 *       (<b>java.util.Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>LocalDate</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Time</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>LocalTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Timestamp</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>LocalDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>OffsetDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>ZonedDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Instant</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b>) <img src="../../../../images/arrow-right.gif" alt="->"> <b>new SqlString("'" + string + "'")</b>
 *     </td>
 *   </tr>
 *   <tr><td>Date          </td></tr>
 *   <tr><td>LocalDate     </td></tr>
 *   <tr><td>Time          </td></tr>
 *   <tr><td>LocalTime     </td></tr>
 *   <tr><td>Timestamp     </td></tr>
 *   <tr><td>LocalDateTime </td></tr>
 *   <tr><td>OffsetDateTime</td></tr>
 *   <tr><td>ZonedDateTime </td></tr>
 *   <tr><td>Instant       </td></tr>
 *   <tr><td>byte[]</td><td><b>new SqlString(SqlString.PARAMETER, source)</b></td></tr>
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
	 * Constructs a new <b>SQLite</b>.
	 */
	protected SQLite() {
		// boolean -> 0, 1
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Boolean.class, SqlString.class, object -> new SqlString(object ? "1" : "0"))
		);

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

		Function<String, SqlString> toSimpleSqlString = string -> new SqlString('\'' + string + '\'');

		// java.util.Date -> String -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.util.Date.class, SqlString.class, 
				TypeConverter.get(typeConverterMap, java.util.Date.class, String.class).function(),
				toSimpleSqlString
			)
		);

		// java.sql.Date -> String -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Date.class, SqlString.class,
				TypeConverter.get(typeConverterMap, Date.class, String.class).function(),
				toSimpleSqlString
			)
		);

		// Time -> String -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Time.class, SqlString.class,
				TypeConverter.get(typeConverterMap, Time.class, String.class).function(),
				toSimpleSqlString
			)
		);

		// Timestamp -> String -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Timestamp.class, SqlString.class,
				TypeConverter.get(typeConverterMap, Timestamp.class, String.class).function(),
				toSimpleSqlString
			)
		);

		// LocalDate -> String -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(LocalDate.class, SqlString.class,
				TypeConverter.get(typeConverterMap, LocalDate.class, String.class).function(),
				toSimpleSqlString
			)
		);

		// LocalTime -> String -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(LocalTime.class, SqlString.class,
				TypeConverter.get(typeConverterMap, LocalTime.class, String.class).function(),
				toSimpleSqlString
			)
		);

		// LocalDateTime -> String -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(LocalDateTime.class, SqlString.class,
				TypeConverter.get(typeConverterMap, LocalDateTime.class, String.class).function(),
				toSimpleSqlString
			)
		);

		// OffsetDateTime -> String -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(OffsetDateTime.class, SqlString.class,
				TypeConverter.get(typeConverterMap, OffsetDateTime.class, String.class).function(),
				toSimpleSqlString
			)
		);

		// ZonedDateTime -> String -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(ZonedDateTime.class, SqlString.class, 
				TypeConverter.get(typeConverterMap, ZonedDateTime.class, String.class).function(),
				toSimpleSqlString
			)
		);

		// Instant -> String -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Instant.class, SqlString.class,
				TypeConverter.get(typeConverterMap, Instant.class, String.class).function(),
				toSimpleSqlString
			)
		);

	// 3.0.1
	//	// byte[] -> SqlString
	//	TypeConverter.put(typeConverterMap,
	//		new TypeConverter<>(byte[].class, SqlString.class, object ->
	//			new SqlString(SqlString.PARAMETER, object))
	//	);
	////
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
