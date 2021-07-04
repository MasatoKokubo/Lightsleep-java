// Database.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.lightsleep.RuntimeSQLException;
import org.lightsleep.Sql;
import org.lightsleep.helper.TypeConverter;
import org.lightsleep.helper.Utils;

/**
 * An interface to generate SQLs.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public interface Database {
    /**
     * Returns whether support <b>OFFSET</b> and <b>LIMIT</b> in the SELECT SQL.
     *
     * @return <b>true</b> if support <b>OFFSET</b> and <b>LIMIT</b>, <b>false</b> otherwise
     */
    default boolean supportsOffsetLimit() {
        return false;
    }

    /**
     * Creates and returns a SELECT SQL.
     *
     * @param <E> the type of the entity
     * @param sql the <b>Sql</b> object that contains SQL generation information that contains SQL generation information
     * @param parameters the list to add the parameters of the SQL
     * @return a <b>CharSequence</b> including SELECT SQL
     *
     * @throws IllegalStateException if SELECT SQL without columns was generated
     */
    <E> CharSequence selectSql(Sql<E> sql, List<Object> parameters);

    /**
     * Creates and returns a SELECT SQL excluding
     * <b>OFFSET</b>/<b>LIMIT</b>,
     * <b>FOR UPDATE</b> and
     * <b>ORDER BY</b>.
     *
     * @param <E> the type of the entity
     * @param <OE> the type of the entity of <b>outerSql</b>
     * @param sql the <b>Sql</b> object that contains SQL generation information
     * @param outerSql the <b>Sql</b> object syntactically outer of <b>sql</b>
     * @param parameters the list to add the parameters of the SQL
     * @return a <b>CharSequence</b> including SELECT SQL
     *
     * @throws IllegalStateException if SELECT SQL without columns was generated
     */
    <E, OE> CharSequence subSelectSql(Sql<E> sql, Sql<OE> outerSql, List<Object> parameters);

    /**
     * Creates and returns a SELECT SQL excluding
     * <b>OFFSET</b>/<b>LIMIT</b>,
     * <b>FOR UPDATE</b> and
     * <b>ORDER BY</b>.
     *
     * @param <E> the type of the entity
     * @param <OE> the type of the entity of <b>outerSql</b>
     * @param sql the <b>Sql</b> object that contains SQL generation information
     * @param outerSql the <b>Sql</b> object syntactically outer of <b>sql</b>
     * @param columnsSupplier a Supplier of the columns string
     * @param parameters the list to add the parameters of the SQL
     * @return a <b>CharSequence</b> including SELECT SQL
     */
    <E, OE> CharSequence subSelectSql(Sql<E> sql, Sql<OE> outerSql, Supplier<CharSequence> columnsSupplier, List<Object> parameters);

    /**
     * Creates and returns a INSERT SQL.
     *
     * @param <E> the type of the entity
     * @param sql the <b>Sql</b> object that contains SQL generation information
     * @param parameters the list to add the parameters of the SQL
     * @return a <b>CharSequence</b> including INSERT SQL
     */
     <E> CharSequence insertSql(Sql<E> sql, List<Object> parameters);

    /**
     * Creates and returns a UPDATE SQL.
     *
     * @param <E> the type of the entity
     * @param sql the <b>Sql</b> object that contains SQL generation information
     * @param parameters the list to add the parameters of the SQL
     * @return a <b>CharSequence</b> including INSERT SQL
     */
    <E> CharSequence updateSql(Sql<E> sql, List<Object> parameters);

    /**
     * Creates and returns a DELETE SQL.
     *
     * @param <E> the type of the entity
     * @param sql the <b>Sql</b> object that contains SQL generation information
     * @param parameters the list to add the parameters of the SQL
     * @return a <b>CharSequence</b> including INSERT SQL
     */
    <E> CharSequence deleteSql(Sql<E> sql, List<Object> parameters);

    /**
     * Returns the <b>TypeConverter</b> map.
     *
     * @return the <b>TypeConverter</b> map
     */
    Map<String, TypeConverter<?, ?>> typeConverterMap();

    /**
     * Converts the object to the specified type.
     *
     * @param <T> the destination type
     * @param value an object to be converted
     * @param type the class object of the destination type
     * @return a converted object
     */
    <T> T convert(Object value, Class<T> type);

    /**
     * Masks the password of the JDBC URL.
     *
     * @param jdbcUrl a JDBC URL
     * @return the JDBC URL masked the password
     *
     * @since 2.2.0
     */
    default String maskPassword(String jdbcUrl) {
        return SQLServer.instance.maskPassword(MySQL.instance.maskPassword(jdbcUrl));
    }

    /**
     * Gets the value from the resultSet and returns it.
     *
     * @param connection the <b>Connection</b> object
     * @param resultSet the <b>ResultSet</b> object
     * @param columnLabel the label for the column
     * @return the column value
     *
     * @throws NullPointerException if <b>connection</b>, <b>resultSet</b> or <b>columnLabel</b> is <b>null</b>
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     *
     * @since 3.0.0
     */
    default Object getObject(Connection connection, ResultSet resultSet, String columnLabel) {
        try {
            Object object = resultSet.getObject(columnLabel);

            if (Standard.logger.isDebugEnabled())
                Standard.logger.debug("Database.getObject: columnLabel: " + columnLabel
                    + ", getted object: " + Utils.toLogString(object));

            return object;
        }
        catch (SQLException e) {
            throw new RuntimeSQLException(e);
        }
    }

    /**
     * Returns a database handler related to <b>jdbcUrl</b>.
     *
     * @param jdbcUrl a JDBC URL
     * @return the database handler related to the JDBC URL
     *
     * @throws IllegalArgumentException if <b>jdbcUrl</b> does not contain a string that can identify a <b>Database</b> class
     *
     * @since 2.1.0
     */
    @SuppressWarnings("unchecked")
    static Database getInstance(String jdbcUrl) {
        Objects.requireNonNull(jdbcUrl, "jdbcUrl is null");
        String[] words = jdbcUrl.split(":");
        for (String word : words) {
            if (word.equals("jdbc")) continue;
            if (!word.matches("[a-z0-9]+")) continue;

            Class<? extends Database> anchorClass;
            try {
                String anchorClassName = Database.class.getPackage().getName() + ".anchor." + word;
                anchorClass = (Class<? extends Database>)Class.forName(anchorClassName);
            } catch (ClassNotFoundException e) {
                continue;
            }

            try {
                Class<? extends Database> databaseClass = (Class<? extends Database>)anchorClass.getSuperclass();
                Database database = (Database)databaseClass.getField("instance").get(null);
                return database;

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        throw new IllegalArgumentException(jdbcUrl);
    }
}
