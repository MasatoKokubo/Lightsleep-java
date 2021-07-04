// PostgreSQL.java
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
 * <a href="http://www.postgresql.org/" target="PostgreSQL">PostgreSQL</a>.
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
 *   <tr><td>String </td><td rowspan="2">SqlString</td>
 *     <td>
 *       <b>new SqlString("'" + source + "'")</b><br>
 *       <span class="comment">Converts a single quote in the source string to two consecutive single quotes
 *       and converts control characters to escape sequences ( \b, \t, \n, \f, \r, \\ ).</span><br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>new SqlString("E'" + source + "'")</b> <span class="comment">if the converted string contains escape sequences</span><br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>new SqlString(SqlString.PARAMETER, source)</b> <span class="comment">if the source string is too long</span>
 *     </td>
 *   </tr>
 *   <tr><td>byte[] </td>
 *     <td>
 *       <b>new SqlString("E'\\x" + hexadecimal string + "'")</b><br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>new SqlString(SqlString.PARAMETER, source)</b> <span class="comment">if the source byte array is too long</span>
 *     </td>
 *   </tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class PostgreSQL extends Standard {
    /**
     * The pattern string of passwords
     *
     * @since 2.2.0
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
     *
     * @since 2.1.0
     */
    public static final PostgreSQL instance = new PostgreSQL();

    /**
     * Constructs a new <b>PostgreSQL</b>.
     */
    protected PostgreSQL() {
        // String.class -> SqlString.class
        TypeConverter.put(typeConverterMap,
            new TypeConverter<>(String.class, SqlString.class, object -> {
                if (object.length() > maxStringLiteralLength)
                    return new SqlString(SqlString.PARAMETER, object); // SQL Parameter
    
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
                    case '\'': buff.append("''"  ); break;
                    case '\\': buff.append("\\\\"); escaped = true; break;
                    default  :
                        if (ch >= ' ' && ch != 0x7F)
                            buff.append(ch);
                        else {
                            buff.append("\\u")
                                .append(String.format("%04X", (int)ch));
                            escaped = true;
                        }
                        break;
                    }
                }
                if (escaped)
                    buff.insert(0, 'E');
                buff.append('\'');
                return new SqlString(buff.toString());
            })
        );

        // Character -> String -> SqlString
        TypeConverter.put(typeConverterMap,
            TypeConverter.of(typeConverterMap, Character.class, String.class, SqlString.class)
        );

        // byte[].class -> SqlString.class
        TypeConverter.put(typeConverterMap,
            TypeConverter.of(typeConverterMap, byte[].class, SqlString.class, SqlString.class,
                object -> object.parameters().length > 0
                    ? object : new SqlString("E'\\\\x" + object.content().substring(2)) // X'...' -> E'\\x...'
            )
        );
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

    /**
     * @since 3.0.0
     */
    @Override
    public Object getObject(Connection connection, ResultSet resultSet, String columnLabel) {
        Object object = super.getObject(connection, resultSet, columnLabel);

        if (object instanceof Time) {
            // Time (for get microseconds)
            try {
                object = resultSet.getObject(columnLabel, LocalTime.class);

                if (logger.isDebugEnabled())
                    logger.debug("  -> PostgreSQL.getObject: columnLabel: " + columnLabel
                        + ", getted object: " + Utils.toLogString(object));
            }
            catch (SQLException e) {
                throw new RuntimeSQLException(e);
            }
        }

        return object;
    }
}
