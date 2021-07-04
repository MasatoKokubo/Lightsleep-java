// SQLServer.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.lightsleep.Sql;
import org.lightsleep.component.Expression;
import org.lightsleep.component.SqlString;
import org.lightsleep.helper.TypeConverter;
import org.lightsleep.helper.Utils;

/**
 * A database handler for
 * <a href="https://www.microsoft.com/ja-jp/server-cloud/products-SQL-Server-2014.aspx" target="SQL Server">Microsoft SQL Server</a>.
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
 *       <span class="comment">Converts a single quote in the source string to two consecutive single quotes
 *       and converts control characters to </span><b>'...'+CHAR(character code)+'...'</b>.<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>new SqlString(SqlString.PARAMETER, source)</b> <span class="comment">if the source string is too long</span>
 *     </td>
 *   </tr>
 *   <tr><td>java.util.Date</td>
 *     <td rowspan="3">
 *       (<b>java.util.Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>LocalDate</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b>) <img src="../../../../images/arrow-right.gif" alt="->"> <b>new SqlString("CAST('" + string + "' AS DATE)")</b>
 *     </td>
 *   </tr>
 *   <tr><td>Date          </td>
 *   <tr><td>LocalDate     </td></tr>
 *   <tr><td>Time          </td>
 *     <td>
 *       <b>Time</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> <img src="../../../../images/arrow-right.gif" alt="->"><br>
 *       <b>new SqlString("CAST('" + string + "' AS TIME)")</b>
 *     </td>
 *   </tr>
 *   <tr><td>LocalTime     </td>
 *     <td>
 *       <b>LocalTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> <img src="../../../../images/arrow-right.gif" alt="->"><br>
 *       <b>new SqlString("CAST('" + string + "' AS TIME)")</b>
 *     </td>
 *   </tr>
 *   <tr><td>Timestamp     </td>
 *     <td rowspan="2">
 *       (<b>Timestamp</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>LocalDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b>) <img src="../../../../images/arrow-right.gif" alt="->"> <b>new SqlString("CAST('" + string + "' AS DATETIME2)")</b>
 *     </td>
 *   </tr>
 *   <tr><td>LocalDateTime </td></tr>
 *   <tr><td>OffsetDateTime</td>
 *     <td rowspan="3">
 *       (<b>OffsetDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>ZonedDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Instant</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b>) <img src="../../../../images/arrow-right.gif" alt="->"> <b>new SqlString("CAST('" + string + "' AS DATETIMEOFFSET)")</b>
 *     </td>
 *   </tr>
 *   <tr><td>ZonedDateTime </td></tr>
 *   <tr><td>Instant       </td></tr>
 *   <tr><td>byte[]</td><td><b>new SqlString(SqlString.PARAMETER, source)</b></td></tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class SQLServer extends Standard {
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
    public static final SQLServer instance = new SQLServer();

    /**
     * Constructs a new <b>SQLServer</b>.
     */
    protected SQLServer() {
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
                int literalIndex = buff.length();
                buff.append('\'');
                boolean hasNChar = false;

                for (char ch : object.toCharArray()) {
                    if (ch >= ' ' && ch != '\u007F') {
                        // Literal representation
                        if (ch >= '\u0080')
                            hasNChar = true;
                        if (literalIndex < 0) {
                            // Outside of the literal
                            buff.append('+');
                            literalIndex = buff.length();
                            buff.append('\'');
                        }
                        if (ch == '\'') buff.append("''");
                        else buff.append(ch);
                    } else {
                        // Functional representation
                        if (literalIndex >= 0) {
                            // Inside of the literal
                            if (hasNChar) {
                                buff.insert(literalIndex, 'N');
                                hasNChar = false;
                            }
                            // Inside of the literal
                            buff.append('\'');
                            literalIndex = -1;
                        }
                        buff.append("+CHAR(").append((int)ch).append(')');
                    }
                }

                if (literalIndex >= 0) {
                    if (hasNChar)
                        buff.insert(literalIndex, 'N');
                    buff.append('\'');
                }

                return new SqlString(buff.toString());
            })
        );

        // Character -> String -> SqlString
        TypeConverter.put(typeConverterMap,
            TypeConverter.of(typeConverterMap, Character.class, String.class, SqlString.class)
        );

        Function<String, SqlString> toDateSqlString         = string -> new SqlString("CAST('" + string + "' AS DATE)");
        Function<String, SqlString> toTimeSqlString         = string -> new SqlString("CAST('" + string + "' AS TIME)");
        Function<String, SqlString> toTimestampSqlString    = string -> new SqlString("CAST('" + string + "' AS DATETIME2)");
        Function<String, SqlString> toTimestampWTZSqlString = string -> new SqlString("CAST('" + string + "' AS DATETIMEOFFSET)");

        // java.util.Date -> String -> SqlString (since 3.0.0)
        TypeConverter.put(typeConverterMap,
            TypeConverter.of(typeConverterMap, java.util.Date.class, String.class, SqlString.class, toDateSqlString)
        );

        // java.sql.Date -> String -> SqlString
        TypeConverter.put(typeConverterMap,
            TypeConverter.of(typeConverterMap, Date.class, String.class, SqlString.class, toDateSqlString)
        );

        // Time -> String -> SqlString
        TypeConverter.put(typeConverterMap,
            TypeConverter.of(typeConverterMap, Time.class, String.class, SqlString.class, toTimeSqlString)
        );

        // Timestamp -> String -> SqlString
        TypeConverter.put(typeConverterMap,
            TypeConverter.of(typeConverterMap, Timestamp.class, String.class, SqlString.class, toTimestampSqlString)
        );

        // LocalDate -> String -> SqlString (since 3.0.0)
        TypeConverter.put(typeConverterMap,
            TypeConverter.of(typeConverterMap, LocalDate.class, String.class, SqlString.class, toDateSqlString)
        );

        // LocalTime -> String -> SqlString (since 3.0.0)
        TypeConverter.put(typeConverterMap,
            TypeConverter.of(typeConverterMap, LocalTime.class, String.class, SqlString.class, toTimeSqlString)
        );

        // LocalDateTime -> String -> SqlString (since 3.0.0)
        TypeConverter.put(typeConverterMap,
            TypeConverter.of(typeConverterMap, LocalDateTime.class, String.class, SqlString.class, toTimestampSqlString)
        );

        // OffsetDateTime -> String -> SqlString (since 3.0.0)
        TypeConverter.put(typeConverterMap,
            TypeConverter.of(typeConverterMap, OffsetDateTime.class, String.class, SqlString.class, toTimestampWTZSqlString)
        );

        // ZonedDateTime -> String -> SqlString (since 3.0.0)
        TypeConverter.put(typeConverterMap,
            TypeConverter.of(typeConverterMap, ZonedDateTime.class, String.class, SqlString.class, toTimestampWTZSqlString)
        );

        // Instant -> String -> SqlString (since 3.0.0)
        TypeConverter.put(typeConverterMap,
            TypeConverter.of(typeConverterMap, Instant.class, String.class, SqlString.class, toTimestampWTZSqlString)
        );

        // byte[] -> SqlString (since 1.7.0)
        TypeConverter.put(typeConverterMap,
            new TypeConverter<>(byte[].class, SqlString.class, object -> new SqlString(SqlString.PARAMETER, object))
        );
    }

    @Override
    public <E> CharSequence selectSql(Sql<E> sql, List<Object> parameters) {
        StringBuilder buff = new StringBuilder();

       // SELECT ... FROM ... WHERE ... GROUP BY ... HAVING ...
        buff.append(subSelectSql(sql, null, parameters));

        // ORDER BY ...
        appendOrderBy(buff, sql, parameters);

        return buff;
    }

    @Override
    protected <E> CharSequence withSelectSql(Sql<E> sql, List<Object> parameters) {
        return onlyWithSelectSql(sql, parameters);
    }

    @Override
    public <E, OE> CharSequence subSelectSql(Sql<E> sql, Sql<OE> outerSql, Supplier<CharSequence> columnsSupplier, List<Object> parameters) {
        StringBuilder buff = new StringBuilder();

        // SELECT
        buff.append("SELECT ");

        // DISTINCT
        appendDistinct(buff, sql);

        // the column names, ...
        buff.append(columnsSupplier.get());

        // FROM
        buff.append(" FROM ");

        // main table
        appendFrom(buff, sql, outerSql, parameters);

        // FOR UPDATE
        appendForUpdate(buff, sql);

        // INNER / OUTER JOIN ...
        appendJoinTables(buff, sql, parameters);

        // WHERE ...
        appendWhere(buff, sql, parameters);

        // GROUP BY ...
        appendGroupBy(buff, sql, parameters);

        // HAVING ...
        appendHaving(buff, sql, parameters);

        return buff;
    }

    /**
     * @since 1.8.4
     */
    @Override
    public <E> CharSequence updateSql(Sql<E> sql, List<Object> parameters) {
        if (sql.getJoinInfos().size() == 0)
            return super.updateSql(sql, parameters);

        StringBuilder buff = new StringBuilder();

        Sql<E> sql2 = new Sql<>(sql.entityInfo().entityClass())
            .columns(sql.getColumns())
            .setEntity(sql.entity());

        // Sets expressions to sql2 from sql.
        sql.columnInfoStream().forEach(columnInfo -> {
            String propertyName = columnInfo.getPropertyName("");
            Expression expression = sql.getExpression(propertyName);
            if (!expression.isEmpty())
                sql2.expression(propertyName, expression);
        });

        // UPDATE table name
        buff.append("UPDATE ");

        // main table name and alias
        appendMainTable(buff, sql2);

        // SET column name =  value, ...
        appendUpdateColumnsAndValues(buff, sql2, parameters);

        // FROM
        buff.append(" FROM ");

        // main table name and alias
        appendMainTable(buff, sql);

        // INNER / OUTER JOIN ...
        appendJoinTables(buff, sql, parameters);

        // WHERE ...
        appendWhere(buff, sql, parameters);

        // ORDER BY ...
        appendOrderBy(buff, sql, parameters);

        return buff;
    }

    /**
     * @since 1.8.2
     */
    @Override
    protected <E> void appendForUpdate(StringBuilder buff, Sql<E> sql) {
        // FOR UPDATE
        if (sql.isForUpdate()) {
            // NO WAIT
            if (sql.isNoWait())
                buff.append(" WITH (ROWLOCK,UPDLOCK,NOWAIT)");
            // WAIT
            else if (sql.isWaitForever())
                buff.append(" WITH (ROWLOCK,UPDLOCK)");
            // WAIT n
            else
                throw new UnsupportedOperationException("wait N");
        }
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

        if (object instanceof microsoft.sql.DateTimeOffset) {
            // microsoft.sql.DateTimeOffset
            LocalDateTime localDateTime = ((microsoft.sql.DateTimeOffset)object).getTimestamp().toLocalDateTime();
            ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(((microsoft.sql.DateTimeOffset)object).getMinutesOffset() * 60);
            object = OffsetDateTime.of(localDateTime, zoneOffset);

            if (logger.isDebugEnabled())
                logger.debug("  -> SQLServer.getObject: columnLabel: " + columnLabel
                    + ", getted object: " + Utils.toLogString(object));
        }

        return object;
    }
}
