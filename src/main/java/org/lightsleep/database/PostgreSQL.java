// PostgreSQL.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import org.lightsleep.component.SqlString;
import org.lightsleep.helper.TypeConverter;

/**
 * A database handler for
 * <a href="http://www.postgresql.org/" target="PostgreSQL">PostgreSQL</a>.
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
 *   <tr><td>String </td><td rowspan="2">SqlString</td><td><code>'...'</code><br>Converts control characters to escape sequence.<br><code>E'...'</code> if the converted string contains escape sequences<br><code>?</code> <i>(SQL parameter)</i> if the string is long</td></tr>
 *   <tr><td>byte[] </td><td><code>E'\\x...'</code><br><code>?</code> <i>(SQL parameter)</i> if the byte array is long</td></tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class PostgreSQL extends Standard {
	// The PostgreSQL instance
	private static final Database instance = new PostgreSQL();

	/**
	 * Returns the <b>PostgreSQL</b> instance.
	 *
	 * @return the <b>PostgreSQL</b> instance
	 */
	public static Database instance() {
		return instance;
	}

	/**
	 * Constructs a new <b>PostgreSQL</b>.
	 */
	protected PostgreSQL() {
	// 1.8.0
	//	/** boolean -> FALSE, TRUE */
	//	TypeConverter.put(typeConverterMap, booleanToSqlFalseTrueConverter);
	////

		// String.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, SqlString.class, object -> {
				if (object.length() > maxStringLiteralLength)
				// 1.7.0
				//	return SqlString.PARAMETER; // SQL Paramter
					return new SqlString(SqlString.PARAMETER, object); // SQL Paramter
				////
	
				boolean escaped = false;
				StringBuilder buff = new StringBuilder(object.length() + 2);
				buff.append('\'');
				for (char ch : object.toCharArray()) {
					switch (ch) {
					case '\b': buff.append("\\b" ); escaped = true; break; // 07 BEL
					case '\t': buff.append("\\t" ); escaped = true; break; // 09 HT
					case '\n': buff.append("\\n" ); escaped = true; break; // 0A LF
					case '\f': buff.append("\\f" ); escaped = true; break; // 0C FF
					case '\r': buff.append("\\r" ); escaped = true; break; // 0D CR
				// 1.2.0
				//	case '\'': buff.append("''"  ); escaped = true; break;
					case '\'': buff.append("''"  ); break;
				////
					case '\\': buff.append("\\\\"); escaped = true; break;
				// 1.9.0
				//	default  : buff.append(ch    ); break;
					default  :
						if (ch >= ' ' && ch != 0x7F)
							buff.append(ch);
						else {
							buff.append("\\u")
								.append(String.format("%04X", (int)ch));
							escaped = true;
						}
						break;
				////
					}
				}
				buff.append('\'');
				String string = buff.toString();
				if (escaped)
					string = 'E' + string;
				return new SqlString(string);
			})
		);

		// byte[].class -> SqlString.class
		TypeConverter.put(typeConverterMap,
	// 1.9.0
	//		new TypeConverter<>(byte[].class, SqlString.class, object -> {
	//			if (object.length > maxBinaryLiteralLength)
	//			// 1.7.0
	//			//	return SqlString.PARAMETER; // SQL Paramter
	//				return new SqlString(SqlString.PARAMETER, object); // SQL Paramter
	//			////
	//
	//			StringBuilder buff = new StringBuilder(object.length * 2 + 5);
	//			buff.append("E'\\\\x");
	//		// 1.7.0
	//		//	for (int b : object) {
	//		//		if (b < 0) b += 256;
	//		//		char ch = (char)('0' + b / 16);
	//		//		if (ch > '9') ch += 'A' - ('9' + 1);
	//		//		buff.append(ch);
	//		//
	//		//		ch = (char)('0' + b % 16);
	//		//		if (ch > '9') ch += 'A' - ('9' + 1);
	//		//		buff.append(ch);
	//		//	}
	//		//
	//		//	buff.append("'");
	//			for (int value : object) {
	//				value &= 0xFF;
	//				char ch = (char)((value >>> 4) + '0');
	//				if (ch > '9') ch += 'A' - ('9' + 1);
	//				buff.append(ch);
	//				ch = (char)((value & 0x0F) + '0');
	//				if (ch > '9') ch += 'A' - ('9' + 1);
	//				buff.append(ch);
	//			}
	//			buff.append('\'');
	//		////
	//			return new SqlString(buff.toString());
	//		})
			new TypeConverter<>(byte[].class, SqlString.class,
				TypeConverter.get(typeConverterMap, byte[].class, SqlString.class).function()
					.andThen(object ->
						object.parameters().length > 0
							? object
							: new SqlString("E'\\\\x" + object.content().substring(2)) // X'...' -> E'\\x...'
					)
			)
	////
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
