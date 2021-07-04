// Db2.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.util.List;

import org.lightsleep.Sql;
import org.lightsleep.component.SqlString;
import org.lightsleep.helper.TypeConverter;

/**
 * A database handler for
 * <a href="https://www.ibm.com/us-en/marketplace/db2-express-c" target="Db2">Db2</a>.
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
 *   <tr><td>byte[]</td><td>SqlString</td>
 *     <td>
 *       <b>new SqlString("BX'" + hexadecimal string + "'")</b><br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>new SqlString(SqlString.PARAMETER, source)</b> <span class="comment">if the source byte array is too long</span>
 *     </td>
 *   </tr>
 * </table>
 *
 * @since 1.9.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class Db2 extends Standard {
    /**
     * The pattern string of passwords
     *
     * @since 2.2.0
     */
    protected static final String PASSWORD_PATTERN =
        '['
        + ASCII_CHARS
            .replace(":;", "")
            .replace("[\\]", "\\[\\\\\\]")
            .replace("^", "\\^")
        + "]*";

    /**
     * The only instance of this class
     *
     * @since 2.1.0
     */
    public static final Db2 instance = new Db2();

    /**
     * Constructs a new <b>Db2</b>.
     */
    protected Db2() {
        // byte[] -> SqlString
        TypeConverter.put(typeConverterMap,
            TypeConverter.of(typeConverterMap, byte[].class, SqlString.class, SqlString.class,
                object -> object.parameters().length > 0
                    ? object : new SqlString('B' + object.content()) // X'...' -> BX'...')
            )
        );
    }

    @Override
    protected <E> CharSequence withSelectSql(Sql<E> sql, List<Object> parameters) {
        return onlyWithSelectSql(sql, parameters);
    }

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

    @Override
    public boolean supportsOffsetLimit() {
        return true;
    }

    /**
     * @since 2.2.0
     */
    @Override
    public String maskPassword(String jdbcUrl) {
        return jdbcUrl.replaceAll("password *=" + PASSWORD_PATTERN, "password=" + PASSWORD_MASK);
    }
}
