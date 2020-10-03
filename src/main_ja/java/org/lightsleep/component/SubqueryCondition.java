// SubQueryCondition.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.List;

import org.lightsleep.Sql;
import org.lightsleep.database.Database;

/**
 * サブクエリを使用する条件を構成します。
 *
 * @param <SE> サブクエリの対象テーブルに対応するエンティティの型
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class SubqueryCondition<SE> implements Condition {
    /**
     * <b>SubqueryCondition</b>を構築します。
     *
     * @param <E> 外側のクエリの対象テーブルに対応するエンティティの型
     * @param expression サブクエリの SELECT 文の左部分の式
     * @param outerSql 構文上<b>subSql</b>の外側にある<b>Sql</b>オブジェクト
     * @param subSql サブクエリ生成用の<b>Sql</b>オブジェクト
     *
     * @throws NullPointerException <b>expression</b>, <b>outerSql</b>または<b>subSql</b>が<b>null</b>の場合
     */
    public <E> SubqueryCondition(Expression expression, Sql<E> outerSql, Sql<SE> subSql) {
    }

    /**
     * <b>SubqueryCondition</b>を構築します。
     *
     * @param <E> 外側のクエリの対象テーブルに対応するエンティティの型
     * @param outerSql 構文上<b>subSql</b>の外側にある<b>Sql</b>オブジェクト
     * @param subSql サブクエリ生成用の<b>Sql</b>オブジェクト
     * @param expression サブクエリの SELECT 文の右部分の式
     *
     * @throws NullPointerException <b>outerSql</b>, <b>subSql</b>または<b>expression</b>が<b>null</b>の場合
     */
    public <E> SubqueryCondition(Sql<E> outerSql, Sql<SE> subSql, Expression expression) {
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public <E> String toString(Database database, Sql<E> sql, List<Object> parameters) {
        return null;
    }
}
