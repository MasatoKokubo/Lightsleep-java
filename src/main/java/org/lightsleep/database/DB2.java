// DB2.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import org.lightsleep.Sql;
import org.lightsleep.component.SqlString;
import org.lightsleep.helper.TypeConverter;

/**
 * A database handler for
 * <a href="https://www.ibm.com/us-en/marketplace/db2-express-c" target="DB2">DB2</a>.
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
 *   <tr><td>byte[]</td><td>SqlString</td><td><code>BX'...'</code><br><code>?</code> <i>(SQL parameter)</i> if the byte array is long</td></tr>
 * </table>
 *
 * @since 1.9.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class DB2 extends Standard {
	// The DB2 instance
	private static final Database instance = new DB2();

	/**
	 * Returns the <b>DB2</b> instance.
	 *
	 * @return the <b>DB2</b> instance
	 */
	public static Database instance() {
		return instance;
	}

	/**
	 * Constructs a new <b>DB2</b>.
	 */
	protected DB2() {
		// byte[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(byte[].class, SqlString.class,
				TypeConverter.get(typeConverterMap, byte[].class, SqlString.class).function()
					.andThen(object ->
						object.parameters().length > 0
							? object
							: new SqlString('B' + object.content()) // X'...' -> BX'...'
					)
			)
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected <E> void appendForUpdate(StringBuilder buff, Sql<E> sql) {
		// FOR UPDATE
		if (sql.isForUpdate()) {
			buff.append(" FOR UPDATE WITH RS");

			// NO WAIT
			if (sql.isNoWait())
				throw new UnsupportedOperationException("noWait");

			// WAIT n
			else if (!sql.isWaitForever())
				throw new UnsupportedOperationException("wait N");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportsOffsetLimit() {
		return true;
	}
}