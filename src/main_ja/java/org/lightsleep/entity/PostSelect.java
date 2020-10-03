// PostSelect.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

import org.lightsleep.connection.ConnectionWrapper;

/**
 * エンティティクラスがこのインターフェースを実装している場合、
 * SELECT SQLを実行してエンティティを取得した後に<b>postSelect</b>メソッドが呼び出されます。
 *
 * @since 3.2.0
 * @author Masato Kokubo
 */
public interface PostSelect {
    /**
     * SELECT SQLを実行してエンティティを取得した後に呼び出されます。
     *
     * @param connection コネクションラッパー
     * @return した取得した行数
     *
     * @throws NullPointerException <b>connection</b>がnullの場合
     */
    int postSelect(ConnectionWrapper connection);
}
