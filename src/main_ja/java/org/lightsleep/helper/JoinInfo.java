// JoinInfo.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

import org.lightsleep.Sql;
import org.lightsleep.component.Condition;

/**
 * 結合テーブルおよび条件の情報を持ちます。
 *
 * @param <JE> 結合するテーブルに対応するエンティティの型
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class JoinInfo<JE> implements SqlEntityInfo<JE> {
    /**
     * 結合タイプ
     */
    public enum JoinType {
        /** INNER JOIN */
        INNER(" INNER JOIN "),

        /** LEFT OUTER JOIN */
        LEFT(" LEFT OUTER JOIN "),

        /** RIGHT OUTER JOIN */
        RIGHT(" RIGHT OUTER JOIN ");

        private JoinType(String sql) {
        }

        /**
         * SQL文字列を返します。
         *
         * @return SQL文字列
         */
        public String sql() {
            return null;
        }
    }

    /**
     * テーブルを結合する<b>JoinInfo</b>を構築します。
     *
     * @param joinType 結合タイプ
     * @param entityInfo 結合するテーブルのエンティティ情報
     * @param tableAlias 結合テーブルの別名
     * @param on 結合条件
     *
     * @throws NullPointerException <b>joinType</b>, <b>entityInfo</b>, <b>tableAlias</b> または <b>on</b>が<b>null</b>の場合
     */
    public JoinInfo(JoinType joinType, EntityInfo<JE> entityInfo, String tableAlias, Condition on) {
    }

    /**
     * サブクエリを結合する<b>JoinInfo</b>を構築します。
     *
     * @param joinType 結合タイプ
     * @param joinSql 結合するサブクエリの<b>Sql</b>オブジェクト
     * @param tableAlias 結合テーブルの別名
     * @param on 結合条件
     *
     * @throws NullPointerException if <b>joinType</b>, <b>joinSql</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
     *
     * @since 4.0.0
     */
    public JoinInfo(JoinType joinType, Sql<JE> joinSql, String tableAlias, Condition on) {
    }

    /**
     * 結合タイプを返します。
     *
     * @return 結合タイプ
     */
    public JoinType joinType() {
        return null;
    }

    /**
     * 結合するサブクエリの<b>Sql</b>オブジェクトを返します。
     *
     * @return 結合するサブクエリの<b>Sql</b>オブジェクト (テーブルを結合する場合は<b>null</b>)
     *
     * @since 4.0.0
     */
    public Sql<JE> joinSql() {
        return null;
    }

    @Override
    public EntityInfo<JE> entityInfo() {
        return null;
    }

    @Override
    public String tableAlias() {
        return null;
    }

    @Override
    public JE entity() {
        return null;
    }

    /**
     * 結合条件を返します。
     *
     * @return 結合条件
     */
    public Condition on() {
        return null;
    }
}
