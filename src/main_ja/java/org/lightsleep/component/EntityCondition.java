// EntityCondition.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.List;

import org.lightsleep.Sql;
import org.lightsleep.database.Database;

/**
 * エンティティのプライマリーキーの値を使用して条件を構成します。
 *
 * @param <E> エンティティの型
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class EntityCondition<E> implements Condition {
    /**
     * EntityConditionを構築します。
     *
     * @param entity エンティティ
     *
     * @throws NullPointerException <b>entity</b>が<b>null</b>の場合
     */
    public EntityCondition(E entity) {
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public <T> String toString(Database database, Sql<T> sql, List<Object> parameters) {
        return null;
    }
}
