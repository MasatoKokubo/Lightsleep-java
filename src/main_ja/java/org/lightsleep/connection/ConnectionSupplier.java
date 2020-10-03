// ConnectionSupplier.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.util.Properties;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.lightsleep.database.Database;

/**
 * コネクションのサプライヤのインタフェースです。
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public interface ConnectionSupplier extends Supplier<ConnectionWrapper> {
    /**
     * このオブジェクトに関連する<b>Database</b>オブジェクトを返します。
     *
     * @return <b>Database</b>オブジェクト
     *
     * @since 2.1.0
     */
    Database getDatabase();

    /**
     * このオブジェクトに関連するデータソースを返します。
     *
     * @return データソース
     *
     * @since 2.1.0
     */
    DataSource getDataSource();

    /**
     * このオブジェクトに関連するJDBC URLを返します。
     *
     * @return JDBC URL
     *
     * @since 2.1.0
     */
    String getUrl();

    /**
     * <b>ConnectionSupplier</b>オブジェクトを作成します。
     *
     * @param supplierName クラス名
     * @param properties データベースの接続情報を含むプロパティ
     * @return 作成された<b>ConnectionSupplier</b>オブジェクト
     *
     * @throws RuntimeException クラスが見つからないかオブジェクトが作成できない場合
     *
     * @since 2.1.0
     */
    static ConnectionSupplier of(String supplierName, Properties properties) {
        return null;
    }

    /**
     * 指定された<b>urlWords</b>のすべての単語を含むurlに関連する<b>ConnectionSupplier</b>オブジェクトを見つけます。
     *
     * @param urlWords url の中の単語の配列
     * @return 見つかった<b>ConnectionSupplier</b>オブジェクト
     *
     * @throws IllegalArgumentException <b>ConnectionSupplier</b>オブジェクトが見つからないか複数見つかった場合
     *
     * @since 2.1.0
     */
    static ConnectionSupplier find(String... urlWords) {
        return null;
    }
}
