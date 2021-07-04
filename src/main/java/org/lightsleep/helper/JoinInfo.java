// JoinInfo.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

import java.util.Objects;

import org.lightsleep.Sql;
import org.lightsleep.component.Condition;

/**
 * Has the information of join tables and conditions.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class JoinInfo<JE> implements SqlEntityInfo<JE> {
    /**
     * The join type
     */
    public enum JoinType {
        /** INNER JOIN */
        INNER(" INNER JOIN "),

        /** LEFT OUTER JOIN */
        LEFT(" LEFT OUTER JOIN "),

        /** RIGHT OUTER JOIN */
        RIGHT(" RIGHT OUTER JOIN ");

        // the SQL string
        private final String sql;

        /**
         * Constructs a new <b>JoinType</b>.
         *
         * @param sql the SQL string
         */
        private JoinType(String sql) {
            this.sql = sql;
        }

        /**
         * Returns the SQL string
         *
         * @return the SQL string
         */
        public String sql() {
            return sql;
        }
    }

    // The type type
    private final JoinType joinType;

    private final Sql<JE> joinSql;

    // The entity information
    private final EntityInfo<JE> entityInfo;

    // The table alias
    private final String tableAlias;

    // The join condition
    private final Condition on;

    /**
     * Constructs a new <b>JoinInfo</b> that joins the table.
     *
     * @param joinType the join type
     * @param entityInfo entity information of the table to join
     * @param tableAlias the alias of the joined table
     * @param on the join condition
     *
     * @throws NullPointerException if <b>joinType</b>, <b>entityInfo</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
     */
    public JoinInfo(JoinType joinType, EntityInfo<JE> entityInfo, String tableAlias, Condition on) {
        this.joinType   = Objects.requireNonNull(joinType  , "joinType is null");
        this.joinSql    = null;
        this.entityInfo = Objects.requireNonNull(entityInfo, "entityInfo is null");
        this.tableAlias = Objects.requireNonNull(tableAlias, "tableAlias is null");
        this.on         = Objects.requireNonNull(on        , "on is null");
    }

    /**
     * Constructs a new <b>JoinInfo</b> that joins the subquery.
     *
     * @param joinType the join type
     * @param joinSql the <b>Sql</b> of the subquery to join
     * @param tableAlias the alias of the joined table
     * @param on the join condition
     *
     * @throws NullPointerException if <b>joinType</b>, <b>joinSql</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
     *
     * @since 4.0.0
     */
    public JoinInfo(JoinType joinType, Sql<JE> joinSql, String tableAlias, Condition on) {
        this.joinType   = Objects.requireNonNull(joinType  , "joinType is null");
        this.joinSql    = Objects.requireNonNull(joinSql   , "joinSql is null");;
        this.entityInfo = joinSql.entityInfo();
        this.tableAlias = Objects.requireNonNull(tableAlias, "tableAlias is null");
        this.on         = Objects.requireNonNull(on        , "on is null");
    }

    /**
     * Returns the join type.
     *
     * @return the join type
     */
    public JoinType joinType() {
        return joinType;
    }

    /**
     * Returns the <b>Sql</b> object for the joined table.
     *
     * @return the <b>Sql</b> object for the joined table (<b>null</b> if joins a table)
     *
     * @since 4.0.0
     */
    public Sql<JE> joinSql() {
        return joinSql;
    }

    @Override
    public EntityInfo<JE> entityInfo() {
        return entityInfo;
    }

    @Override
    public String tableAlias() {
        return tableAlias;
    }

    @Override
    public JE entity() {
        return null;
    }

    /**
     * Returns the join condition.
     *
     * @return the join condition
     */
    public Condition on() {
        return on;
    }
}
