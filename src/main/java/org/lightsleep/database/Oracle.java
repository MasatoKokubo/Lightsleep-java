/*
	Oracle.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.database;

import oracle.sql.TIMESTAMP;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import org.lightsleep.component.SqlString;
import org.lightsleep.helper.ConvertException;
import org.lightsleep.helper.TypeConverter;

/**
	A database handler for
	<a href="https://www.oracle.com/database/index.html" target="Oracle">Oracle Database</a>.<br>

	The object of this class has a <b>TypeConverter</b> map
	with the following additional <b>TypeConverter</b> to
	{@linkplain org.lightsleep.helper.TypeConverter#typeConverterMap}.

	<table class="additinal">
		<caption><span>Registered TypeConverter objects</span></caption>
		<tr><th>Source data type</th><th>Destination data type</th></tr>
		<tr><td>boolean       </td><td>{@linkplain org.lightsleep.component.SqlString} (0, 1)</td></tr>
		<tr><td>String        </td><td rowspan="2">{@linkplain org.lightsleep.component.SqlString}</td></tr>
		<tr><td>Time          </td></tr>
		<tr><td rowspan="4">oracle.sql.TIMESTAMP</td><td>java.util.Date<br><i>(since 1.4.0)</i></td></tr>
		<tr>                                         <td>java.sql.Date     </td></tr>
		<tr>                                         <td>java.sql.Time     </td></tr>
		<tr>                                         <td>java.sql.Timestamp</td></tr>
	</table>

	@since 1.0.0
	@author Masato Kokubo
*/
public class Oracle extends Standard {
	// The Oracle instance
	private static final Database instance = new Oracle();

	/**
		Returns the Oracle instance.

		@return the Oracle instance
	*/
	public static Database instance() {
		return instance;
	}

	/**
		Constructs a new <b>Oracle</b>.
	*/
	protected Oracle() {
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
							buff.append("||'");
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
						buff.append("||CHR(").append((int)ch).append(')');
					}
				}

				if (inLiteral)
					buff.append('\'');

				return new SqlString(buff.toString());
			})
		);


		// Time.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Time.class, SqlString.class, object ->
				new SqlString("TO_TIMESTAMP('1970-01-01 " + object + "','YYYY-MM-DD HH24:MI:SS.FF3')")
			)
		);

		// oracle.sql.TIMESTAMP -> java.util.Date (since 1.4.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(TIMESTAMP.class, java.util.Date.class, object -> {
				try {
					return new java.util.Date(object.dateValue().getTime());
				}
				catch (SQLException e) {
					throw new ConvertException(e);
				}
			})
		);

		// oracle.sql.TIMESTAMP -> java.sql.Date
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(TIMESTAMP.class, Date.class, object -> {
				try {
					return object.dateValue();
				}
				catch (SQLException e) {
					throw new ConvertException(e);
				}
			})
		);

		// oracle.sql.TIMESTAMP -> java.sql.Time
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(TIMESTAMP.class, Time.class, object -> {
				try {
					return object.timeValue();
				}
				catch (SQLException e) {
					throw new ConvertException(e);
				}
			})
		);

		// oracle.sql.TIMESTAMP -> java.sql.Timestamp
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(TIMESTAMP.class, Timestamp.class, object -> {
				try {
					return object.timestampValue();
				}
				catch (SQLException e) {
					throw new ConvertException(e);
				}
			})
		);
	}
}
