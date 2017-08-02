// MySQL.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import org.lightsleep.component.SqlString;
import org.lightsleep.helper.TypeConverter;

/**
 * A database handler for
 * <a href="http://www.mysql.com/" target="MySQL">MySQL</a>.
 *
 * <p>
 * The object of this class has a <b>TypeConverter</b> map
 * with the following additional <b>TypeConverter</b> to
 * {@linkplain Standard#typeConverterMap}.
 * </p>
 *
 * <table class="additional">
 *   <caption><span>Registered TypeConverter objects</span></caption>
 *   <tr><th>Source Data Type</th><th>Destination Data Type</th><th>Conversion Contents</th></tr>
 *   <tr><td>Boolean</td><td rowspan="2">SqlString</td><td>false -&gt; <code>0</code><br>true -&gt; <code>1</code></td></tr>
 *   <tr><td>String </td><td><code>'...'</code><br>Converts control characters to escape sequence.<br><code>?</code> <i>(SQL parameter)</i> if the string is long</td></tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class MySQL extends Standard {
	// The MySQL instance
	private static final Database instance = new MySQL();

	/**
	 * Returns the <b>MySQL</b> instance.
	 *
	 * @return the <b>MySQL</b> instance
	 */
	public static Database instance() {
		return instance;
	}

	/**
	 * Constructs a new <b>MySQL</b>.
	 */
	protected MySQL() {
		// boolean -> 0, 1
		TypeConverter.put(typeConverterMap, booleanToSql01Converter);

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
}
