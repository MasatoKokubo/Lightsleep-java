// Database.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.lightsleep.Sql;
import org.lightsleep.helper.TypeConverter;

/**
 * SQLを生成するためのインタフェースです。
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public interface Database {
    /**
     * SELECT SQLで<b>OFFSET</b>と<b>OFFSET</b>をサポートしているかどうかを返します。
     *
     * @return <b>OFFSET</b>と<b>OFFSET</b>をサポートしている場合は<b>true</b>、そうでなければ<b>false</b>
     */
    default boolean supportsOffsetLimit() {
        return false;
    }

    /**
     * SELECT SQLを作成して返します。
     *
     * @param <E> エンティティの型
     * @param sql SQLの生成情報を含む<b>Sql</b>オブジェクト
     * @param parameters SQLのパラメータを格納するリスト
     * @return SELECT SQLを含む<b>CharSequence</b>
     */
    public <E> CharSequence selectSql(Sql<E> sql, List<Object> parameters);

    /**
     * <b>OFFSET</b>/<b>LIMIT</b>,
     * <b>FOR UPDATE</b>,
     * <b>ORDER BY</b>
     * を除いた SELECT SQLを作成して返します。
     *
     * @param <E> エンティティの型
     * @param <OE> <b>outerSql</b>のエンティティの型
     * @param sql SQLの生成情報を含む<b>Sql</b>オブジェクト
     * @param outerSql 構文上<b>sql</b>の外側にある<b>Sql</b>オブジェクト
     * @param parameters SQLのパラメータを格納するリスト
     *
     * @return SELECT SQLを含む<b>CharSequence</b>
     */
    public <E, OE> CharSequence subSelectSql(Sql<E> sql, Sql<OE> outerSql, List<Object> parameters);

    /**
     * <b>OFFSET</b>/<b>LIMIT</b>,
     * <b>FOR UPDATE</b>,
     * <b>ORDER BY</b>
     * を除いた SELECT SQLを作成して返します。
     *
     * @param <E> エンティティの型
     * @param <OE> <b>outerSql</b>のエンティティの型
     * @param sql SQLの生成情報を含む<b>Sql</b>オブジェクト
     * @param outerSql 構文上<b>sql</b>の外側にある<b>Sql</b>オブジェクト
     * @param columnsSupplier カラム列文字列のサプライヤ
     * @param parameters SQLのパラメータを格納するリスト
     * @return SELECT SQLを含む<b>CharSequence</b>
     */
    public <E, OE> CharSequence subSelectSql(Sql<E> sql, Sql<OE> outerSql, Supplier<CharSequence> columnsSupplier, List<Object> parameters);

    /**
     * INSERT SQLを作成して返します。
     *
     * @param <E> エンティティの型
     * @param sql SQLの生成情報を含む<b>Sql</b>オブジェクト
     * @param parameters SQLのパラメータを格納するリスト
     * @return INSERT SQLを含む<b>CharSequence</b>
     */
    public <E> CharSequence insertSql(Sql<E> sql, List<Object> parameters);

    /**
     * UPDATE SQLを作成して返します。
     *
     * @param <E> エンティティの型
     * @param sql SQLの生成情報を含む<b>Sql</b>オブジェクト
     * @param parameters SQLのパラメータを格納するリスト
     * @return UPDATE SQLを含む<b>CharSequence</b>
     */
    public <E> CharSequence updateSql(Sql<E> sql, List<Object> parameters);

    /**
     * DELETE SQLを作成して返します。
     *
     * @param <E> エンティティの型
     * @param sql SQLの生成情報を含む<b>Sql</b>オブジェクト
     * @param parameters SQLのパラメータを格納するリスト
     * @return DELETE SQLを含む<b>CharSequence</b>
     */
    public <E> CharSequence deleteSql(Sql<E> sql, List<Object> parameters);

    /**
     * <b>TypeConverter</b>マップを返します。
     *
     * @return <b>TypeConverter</b>マップ
     */
    public Map<String, TypeConverter<?, ?>> typeConverterMap();

    /**
     * オブジェクトを指定の型に変換します。
     *
     * @param <T> 変換先の型
     * @param value 変換するオブジェクト
     * @param type 変換先の型のクラスオブジェクト
     * @return 変換されたオブジェクト
     */
    public <T> T convert(Object value, Class<T> type);

    /**
     * JDBC URLのパスワードをマスクします。.
     *
     * @param jdbcUrl JDBC URL
     * @return パスワードをマスクしたJDBC URL
     *
     * @since 2.2.0
     */
    default String maskPassword(String jdbcUrl) {
        return null;
    }

    /**
     * <b>resultSet</b>から値を取得して返します。
     *
     * @param connection <b>Connection</b>オブジェクト
     * @param resultSet the <b>ResultSet</b>オブジェクト
     * @param columnLabel the label カラムのラベル
     * @return the column value
     *
     * @since 3.0.0
     */
    default Object getObject(Connection connection, ResultSet resultSet, String columnLabel) {
        return null;
    }

    /**
     * <b>jdbcUrl</b>に関連するデータベースハンドラを返します。
     *
     * @param jdbcUrl JDBC URL
     * @return <b>jdbcUrl</b>に関連するデータベースハンドラ
     *
     * @throws IllegalArgumentException <b>jdbcUrl</b>がデータベースハンドラを識別する文字列を含んでいない場合
     *
     * @since 2.1.0
     */
    @SuppressWarnings("unchecked")
    public static Database getInstance(String jdbcUrl) {
        return null;
    }
}
