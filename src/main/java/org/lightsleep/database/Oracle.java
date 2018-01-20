// Oracle.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import oracle.sql.TIMESTAMP;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import org.lightsleep.Sql;
import org.lightsleep.component.SqlString;
import org.lightsleep.helper.ConvertException;
import org.lightsleep.helper.TypeConverter;

/**
 * A database handler for
 * <a href="https://www.oracle.com/database/index.html" target="Oracle">Oracle Database</a>.
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
 *   <tr><td>Boolean</td><td rowspan="3">SqlString</td><td>false -&gt; <code>0</code><br>true -&gt; <code>1</code></td></tr>
 *   <tr><td>Time   </td><td><code>TO_TIMESTAMP('1970-01-01 HH:mm:ss','YYYY-MM-DD HH24:MI:SS.FF3')</code></td></tr>
 *   <tr><td>byte[]</td><td><code>?</code> <i>(SQL parameter)</i></td></tr>
 *   <tr><td rowspan="4">oracle.sql.TIMESTAMP</td><td>java.util.Date</td><td rowspan="4">Throws a ConvertException if SQLException is thrown when getting value.</td></tr>
 *   <tr>                                         <td>java.sql.Date     </td></tr>
 *   <tr>                                         <td>java.sql.Time     </td></tr>
 *   <tr>                                         <td>java.sql.Timestamp</td></tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class Oracle extends Standard {
	/**
	 * The only instance of this class
	 *
	 * @since 2.1.0
	 */
	public static final Oracle instance = new Oracle();

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
	 * Constructs a new <b>Oracle</b>.
	 */
	protected Oracle() {
		// boolean -> 0, 1
		TypeConverter.put(typeConverterMap, booleanToSql01Converter);

		// Time.class -> SqlString.class
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Time.class, SqlString.class, object ->
				new SqlString("TO_TIMESTAMP('1970-01-01 " + object + "','YYYY-MM-DD HH24:MI:SS.FF3')")
			)
		);

		// 1.7.0
		// byte[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(byte[].class, SqlString.class, object ->
				new SqlString(SqlString.PARAMETER, object))
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

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.9.0
	 */
	@Override
	protected <E> void appendForUpdate(StringBuilder buff, Sql<E> sql) {
		// FOR UPDATE
		if (sql.isForUpdate()) {
			buff.append(" FOR UPDATE");

			// NO WAIT
			if (sql.isNoWait())
				buff.append(" NOWAIT");

			// WAIT n
			else if (!sql.isWaitForever())
				buff.append(" WAIT ").append(sql.getWaitTime());
		}
	}

}
