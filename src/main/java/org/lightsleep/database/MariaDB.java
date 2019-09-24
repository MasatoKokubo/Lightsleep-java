// MariaDB.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;

import org.lightsleep.RuntimeSQLException;
import org.lightsleep.component.SqlString;
import org.lightsleep.helper.TypeConverter;
import org.lightsleep.helper.Utils;

/**
 * A database handler for
 * <a href="https://mariadb.org/" target="MariaDB">MariaDB</a>.
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
 *   <tr><td>Boolean</td><td rowspan="2">SqlString</td>
 *     <td>
 *       <b>new SqlString("0")</b> <span class="comment">if the source value is <b>false</b></span><br>
 *       <b>new SqlString("1")</b> <span class="comment">if the source value is <b>true</b></span>
 *     </td>
 *   </tr>
 *   <tr><td>String</td>
 *     <td>
 *       <b>new SqlString("'" + source + "'")</b><br>
 *       <span class="comment">Converts a single quote in the source string to two consecutive single quotes
 *       and converts control characters to escape sequences ( \0, \b, \t, \n, \r, \\ ).</span><br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>new SqlString(SqlString.PARAMETER, source)</b> <span class="comment">if the source string is too long</span>
 *     </td>
 *   </tr>
 * </table>
 *
 * @since 3.2.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class MariaDB extends Standard {
	/**
	 * The pattern string of passwords
	 */
	protected static final String PASSWORD_PATTERN =
		'['
		+ ASCII_CHARS
			.replace("&", "")
			.replace(":", "")
			.replace("[\\]", "\\[\\\\\\]")
			.replace("^", "\\^")
		+ "]*";

	/**
	 * The only instance of this class
	 */
	public static final MariaDB instance = new MariaDB();

	/**
	 * Constructs a new <b>MariaDB</b>.
	 */
	protected MariaDB() {
		// boolean -> 0, 1
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Boolean.class, SqlString.class, object -> new SqlString(object ? "1" : "0"))
		);

		// String.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, SqlString.class, object -> {
				if (object.length() > maxStringLiteralLength)
					return new SqlString(SqlString.PARAMETER, object); // SQL Parameter

				StringBuilder buff = new StringBuilder(object.length() + 2);
				buff.append('\'');
				for (char ch : object.toCharArray()) {
					switch (ch) {
					case '\u0000': buff.append("\\0" ); break; // 00 NUL
					case '\b'    : buff.append("\\b" ); break; // 07 BEL
					case '\t'    : buff.append("\\t" ); break; // 09 HT
					case '\n'    : buff.append("\\n" ); break; // 0A LF
					case '\r'    : buff.append("\\r" ); break; // 0D CR
					case '\''    : buff.append("''"  ); break;
					case '\\'    : buff.append("\\\\"); break;
					default      : buff.append(ch    ); break;
					}
				}
				buff.append('\'');
				return new SqlString(buff.toString());
			})
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportsOffsetLimit() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String maskPassword(String jdbcUrl) {
		return jdbcUrl.replaceAll("password *=" + PASSWORD_PATTERN, "password=" + PASSWORD_MASK);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getObject(Connection connection, ResultSet resultSet, String columnLabel) {
		Object object = super.getObject(connection, resultSet, columnLabel);

		if (object instanceof Time) {
			// Time (for get microseconds)
			try {
				object = resultSet.getObject(columnLabel, LocalTime.class);

				if (logger.isDebugEnabled())
					logger.debug("  -> MariaDB.getObject: columnLabel: " + columnLabel
						+ ", getted object: " + Utils.toLogString(object));
			}
			catch (SQLException e) {
				throw new RuntimeSQLException(e);
			}
		}

		return object;
	}
}
