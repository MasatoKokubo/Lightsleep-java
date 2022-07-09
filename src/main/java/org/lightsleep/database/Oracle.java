// Oracle.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;

import org.lightsleep.RuntimeSQLException;
import org.lightsleep.Sql;
import org.lightsleep.component.SqlString;
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
 *   <tr><td>byte[]</td><td><b>new SqlString(SqlString.PARAMETER, source)</b></td></tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class Oracle extends Standard {
    /**
     * The pattern string of passwords
     *
     * @since 2.2.0
     */
    protected static final String PASSWORD_PATTERN =
        '['
        + ASCII_CHARS
            .replace(":", "")
            .replace("@", "")
            .replace("[\\]", "\\[\\\\\\]")
            .replace("^", "\\^")
        + "]*";

    /**
     * The only instance of this class
     *
     * @since 2.1.0
     */
    public static final Oracle instance = new Oracle();

    /**
     * Constructs a new <b>Oracle</b>.
     */
    protected Oracle() {
        // boolean -> 0, 1
        TypeConverter.put(typeConverterMap,
            new TypeConverter<>(Boolean.class, SqlString.class, object -> new SqlString(object ? "1" : "0"))
        );

        Function<String, SqlString> toTimeSqlString =
            string -> new SqlString("TO_TIMESTAMP('1970-01-01 " + string + "','YYYY-MM-DD HH24:MI:SS')");

        // Time -> String -> SqlString (since 3.0.0)
        TypeConverter.put(typeConverterMap,
            TypeConverter.of(typeConverterMap, Time.class, String.class, SqlString.class, toTimeSqlString)
        );

        // LocalTime -> String -> SqlString (since 3.0.0)
        TypeConverter.put(typeConverterMap,
            TypeConverter.of(typeConverterMap, LocalTime.class, String.class, SqlString.class, toTimeSqlString)
        );

        // byte[] -> SqlString (since 1.7.0)
        TypeConverter.put(typeConverterMap,
            new TypeConverter<>(byte[].class, SqlString.class, object ->
                new SqlString(SqlString.PARAMETER, object))
        );
    }

    @Override
    protected <E> CharSequence withSelectSql(Sql<E> sql, List<Object> parameters) {
        return onlyWithSelectSql(sql, parameters);
    }

    /**
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

    /**
     * @since 2.2.0
     */
    @Override
    public String maskPassword(String jdbcUrl) {
        return jdbcUrl.replaceAll('/' + PASSWORD_PATTERN + '@', '/' + PASSWORD_MASK + '@');
    }

    /**
     * @since 3.0.0
     */
    @Override
    public Object getObject(Connection connection, ResultSet resultSet, String columnLabel) {
        Object object = super.getObject(connection, resultSet, columnLabel);

        if (object instanceof oracle.sql.Datum) {
            try {
                if (object instanceof oracle.sql.TIMESTAMP)
                    // oracle.sql.TIMESTAMP
                    object = ((oracle.sql.TIMESTAMP)object).timestampValue();

                else if (object instanceof oracle.sql.TIMESTAMPLTZ)
                    // oracle.sql.TIMESTAMPLTZ
                    object = ((oracle.sql.TIMESTAMPLTZ)object).timestampValue(connection);

                else if (object instanceof oracle.sql.TIMESTAMPTZ) {
                    // oracle.sql.TIMESTAMPTZ
                    LocalDateTime localDateTime = ((oracle.sql.TIMESTAMPTZ)object).timestampValue(connection).toLocalDateTime();
                    ZoneId zoneId = ((oracle.sql.TIMESTAMPTZ)object).getTimeZone().toZoneId();
                    object = ZonedDateTime.of(localDateTime, zoneId);
                }
            }
            catch (SQLException e) {
                throw new RuntimeSQLException(e);
            }
        }

        return object;
    }
}
