// AbstractConnectionSupplier.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;


import org.lightsleep.database.Database;
import org.lightsleep.logger.Logger;

/**
 * 各種のコネクションラッパーのサプライヤクラスの共通部分を実装する抽象クラスです。
 *
 * @since 1.1.0
 * @author Masato Kokubo
 */
public abstract class AbstractConnectionSupplier implements ConnectionSupplier {
    /**
     * {@value}
     * @since 2.2.0
     */
    protected static final String URL = "url";

    /**
     * {@value}
     * @since 2.2.0
     */
    protected static final String URLS = "urls";

    /**
     * {@value}
     * @since 2.2.0
     */
    protected static final String JDBC_URL = "jdbcUrl";

    /**
     * {@value}
     * @since 2.2.0
     */
    protected static final String DATA_SOURCE = "dataSource";

    /**
     * {@value}
     * @since 2.2.0
     */
    protected static final String DATA_SOURCES = "dataSources";

    /**
     * {@value}
     * @since 2.2.0
     */
    protected static final String USER = "user";

    /**
     * {@value}
     * @since 2.2.0
     */
    protected static final String USERNAME = "username";

    /** ロガー */
    protected static final Logger logger = null;

    /** キー: url 文字列、値: ConnectionSupplier のマップ */
    protected static final Map<String, ConnectionSupplier> supplierMap = null;

    /**
     * <b>AbstractConnectionSupplier</b>を構築します。
     *
     * @param properties コネクション情報を含むプロパティ
     * @param modifier プロパティを修正するためのコンシューマー
     *
     * @since 2.1.0
     */
    protected AbstractConnectionSupplier(Properties properties, Consumer<Properties> modifier) {
    }

    /**
     * コネクションラッパーを返します。
     *
     * @return コネクションラッパー
     */
    @Override
    public ConnectionWrapper get() {
        return null;
    }


    /**
     * @since 2.1.0
     */
    @Override
    public Database getDatabase() {
        return null;
    }

    /**
     * @since 2.1.0
     */
    @Override
    public String getUrl() {
        return null;
    }

    /**
     * @since 2.1.0
     */
    @Override
    public String toString() {
        return null;
    }
}
