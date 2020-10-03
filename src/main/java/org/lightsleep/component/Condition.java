// Condition.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.Collection;
import java.util.stream.Stream;

import org.lightsleep.Sql;

/**
 * A interface of condition for SQL.
 * @since 1.0.0
 * @author Masato Kokubo
 */
public interface Condition extends SqlComponent {
    /** The empty condition */
    static final Condition EMPTY = of("");

    /** The condition for all rows */
    static final Condition ALL = of("0=0");

    /**
     * Returns a new expression condition.
     *
     * @param content the content of the expression condition
     * @param arguments the arguments of the expression condition
     * @return a new expression condition
     *
     * @throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
     *
     * @see Expression#Expression(String, Object...)
     */
    static Condition of(String content, Object... arguments) {
        return new Expression(content, arguments);
    }

    /**
     * Returns a new entity condition.
     *
     * @param <K> the type of the entity
     * @param entity the entity of the entity condition
     * @return a new entity condition
     *
     * @throws NullPointerException if <b>entity</b> is <b>null</b>
     *
     * @see EntityCondition#EntityCondition(Object)
     */
    static <K> Condition of(K entity) {
        if (entity instanceof String)
            return new Expression((String)entity);

        return new EntityCondition<K>(entity);
    }

    /**
     * Returns a new subquery condition.
     *
     * @param <E> the type of the entity related to table of the outer sql.
     * @param <SE> the type of the entity related to table of the subquery.
     * @param content the content of the subquery condition.
     * @param outerSql the <b>Sql</b> object syntactically outer of <b>subSql</b>
     * @param subSql the <b>Sql</b> of the subquery condition.
     * @return a new subquery condition
     *
     * @throws NullPointerException if <b>content</b>, <b>outerSql</b> or <b>subSql</b> is <b>null</b>
     *
     * @see #of(Sql, Sql, String)
     * @see SubqueryCondition#SubqueryCondition(Expression, Sql, Sql)
     */
    static <E, SE> Condition of(String content, Sql<E> outerSql, Sql<SE> subSql) {
        return new SubqueryCondition<>(new Expression(content), outerSql, subSql);
    }

    /**
     * Returns a new subquery condition.
     *
     * @param <E> the type of the entity related to table of the outer sql.
     * @param <SE> the type of the entity related to table of the subquery.
     * @param outerSql the <b>Sql</b> object syntactically outer of <b>subSql</b>
     * @param subSql the <b>Sql</b> of the subquery condition.
     * @param content the content of the subquery condition.
     * @return a new subquery condition
     *
     * @throws NullPointerException if <b>outerSql</b>, <b>subSql</b> or <b>content</b> is <b>null</b>
     *
     * @since 3.1.0
     * @see #of(String, Sql, Sql)
     * @see SubqueryCondition#SubqueryCondition(Sql, Sql, Expression)
     */
    static <E, SE> Condition of(Sql<E> outerSql, Sql<SE> subSql, String content) {
        return new SubqueryCondition<>(outerSql, subSql, new Expression(content));
    }

    /**
     * Returns an optimized <b>(NOT this)</b>.
     *
     * @return optimized <b>(NOT this)</b>
     *
     * @see Not#Not(Condition)
     * @see LogicalCondition#optimized()
     */
    default Condition not() {
        return isEmpty() ? this : new Not(this).optimized();
    }

    /**
     * Returns an optimized (this AND <b>condition</b>).
     *
     * @param condition the condition
     * @return an optimized (this AND <b>condition</b>)
     *
     * @throws NullPointerException if <b>condition</b> is <b>null</b>
     *
     * @see And#And(Condition...)
     * @see LogicalCondition#optimized()
     */
    default Condition and(Condition condition) {
        return new And(this, condition).optimized();
    }

    /**
     * Returns an optimized (this AND the expression condition).
     *
     * @param content the content of the expression condition
     * @param arguments the arguments of the expression condition
     * @return an optimized (this AND the expression condition)
     *
     * @throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
     *
     * @see And#And(Condition...)
     * @see Expression#Expression(String, Object...)
     * @see LogicalCondition#optimized()
     */
    default Condition and(String content, Object... arguments) {
        return new And(this, new Expression(content, arguments)).optimized();
    }

    /**
     * Returns an optimized (this AND the entity condition).
     *
     * @param <K> the type of the entity
     * @param entity the entity of the entity condition
     * @return an optimized (this AND the entity condition)
     *
     * @throws NullPointerException if <b>entity</b> is <b>null</b>
     *
     * @since 3.1.0
     * @see And#And(Condition...)
     * @see EntityCondition#EntityCondition(Object)
     */
    default <K> Condition and(K entity) {
        return new And(this, entity instanceof String ? new Expression((String)entity) : new EntityCondition<K>(entity)).optimized();
    }

    /**
     * Returns an optimized (this AND the subquery condition).
     *
     * @param <E> the type of the entity related to table of the outer sql.
     * @param <SE> the type of the entity related to table of the subquery.
     * @param content the content of the subquery condition.
     * @param outerSql the <b>Sql</b> object syntactically outer of <b>subSql</b>
     * @param subSql the <b>Sql</b> of the subquery condition.
     * @return an optimized (this AND the subquery condition)
     *
     * @throws NullPointerException if <b>content</b>, <b>outerSql</b> or <b>subSql</b> is <b>null</b>
     *
     * @see #and(Sql, Sql, String)
     * @see And#And(Condition...)
     * @see SubqueryCondition#SubqueryCondition(Expression, Sql, Sql)
     * @see Expression#Expression(String, Object...)
     * @see LogicalCondition#optimized()
     */
    default <E, SE> Condition and(String content, Sql<E> outerSql, Sql<SE> subSql) {
        return new And(this, new SubqueryCondition<>(new Expression(content), outerSql, subSql)).optimized();
    }

    /**
     * Returns an optimized (this AND the subquery condition).
     *
     * @param <E> the type of the entity related to table of the outer sql.
     * @param <SE> the type of the entity related to table of the subquery.
     * @param outerSql the <b>Sql</b> object syntactically outer of <b>subSql</b>
     * @param subSql the <b>Sql</b> of the subquery condition.
     * @param content the content of the subquery condition.
     * @return an optimized (this AND the subquery condition)
     *
     * @throws NullPointerException if <b>outerSql</b>, <b>subSql</b> or <b>content</b> is <b>null</b>
     *
     * @since 3.1.0
     * @see #and(String, Sql, Sql)
     * @see And#And(Condition...)
     * @see SubqueryCondition#SubqueryCondition(Sql, Sql, Expression)
     * @see Expression#Expression(String, Object...)
     * @see LogicalCondition#optimized()
     */
    default <E, SE> Condition and(Sql<E> outerSql, Sql<SE> subSql, String content) {
        return new And(this, new SubqueryCondition<>(outerSql, subSql, new Expression(content))).optimized();
    }

    /**
     * Returns an optimized (this OR <b>condition</b>).
     *
     * @param condition the condition
     * @return optimized (this OR <b>condition</b>)
     *
     * @throws NullPointerException if <b>condition</b> is <b>null</b>
     *
     * @see Or#Or(Condition...)
     * @see LogicalCondition#optimized()
     */
    default Condition or(Condition condition) {
        return new Or(this, condition).optimized();
    }

    /**
     * Returns an optimized (this OR the expression condition).
     *
     * @param content the content of the expression condition
     * @param arguments the arguments of the expression condition
     * @return optimized (this OR the expression condition)
     *
     * @throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
     *
     * @see Or#Or(Condition...)
     * @see Expression#Expression(String, Object...)
     * @see LogicalCondition#optimized()
     */
    default Condition or(String content, Object... arguments) {
        return new Or(this, new Expression(content, arguments)).optimized();
    }

    /**
     * Returns an optimized (this OR the entity condition).
     *
     * @param <K> the type of the entity
     * @param entity the entity of the entity condition
     * @return an optimized (this OR the entity condition)
     *
     * @throws NullPointerException if <b>entity</b> is <b>null</b>
     *
     * @since 3.1.0
     * @see Or#Or(Condition...)
     * @see EntityCondition#EntityCondition(Object)
     */
    default <K> Condition or(K entity) {
        return new Or(this, entity instanceof String ? new Expression((String)entity) : new EntityCondition<K>(entity)).optimized();
    }

    /**
     * Returns an optimized (this OR the subquery condition).
     *
     * @param <E> the type of the entity related to table of the outer sql.
     * @param <SE> the type of the entity related to table of the subquery.
     * @param content the content of the subquery condition.
     * @param outerSql the <b>Sql</b> object syntactically outer of <b>subSql</b>
     * @param subSql the <b>Sql</b> of the subquery condition.
     * @return an optimized (this OR the subquery condition)
     *
     * @throws NullPointerException if <b>content</b>, <b>outerSql</b> or <b>subSql</b> is <b>null</b>
     *
     * @see #or(Sql, Sql, String)
     * @see Or#Or(Condition...)
     * @see SubqueryCondition#SubqueryCondition(Expression, Sql, Sql)
     * @see Expression#Expression(String, Object...)
     * @see LogicalCondition#optimized()
     */
    default <E, SE> Condition or(String content, Sql<E> outerSql, Sql<SE> subSql) {
        return new Or(this, new SubqueryCondition<>(new Expression(content), outerSql, subSql)).optimized();
    }

    /**
     * Returns an optimized (this OR the subquery condition).
     *
     * @param <E> the type of the entity related to table of the outer sql.
     * @param <SE> the type of the entity related to table of the subquery.
     * @param outerSql the <b>Sql</b> object syntactically outer of <b>subSql</b>
     * @param subSql the <b>Sql</b> of the subquery condition.
     * @param content the content of the subquery condition.
     * @return an optimized (this OR the subquery condition)
     *
     * @throws NullPointerException if <b>content</b>, <b>outerSql</b> or <b>subSql</b> is <b>null</b>
     *
     * @since 3.1.0
     * @see #or(String, Sql, Sql)
     * @see Or#Or(Condition...)
     * @see SubqueryCondition#SubqueryCondition(Expression, Sql, Sql)
     * @see Expression#Expression(String, Object...)
     * @see LogicalCondition#optimized()
     */
    default <E, SE> Condition or(Sql<E> outerSql, Sql<SE> subSql, String content) {
        return new Or(this, new SubqueryCondition<>(outerSql, subSql, new Expression(content))).optimized();
    }

    /**
     * Returns an optimized <b>new And(conditions)</b>.
     *
     * @param conditions a stream of conditions
     * @return an optimized <b>new And(conditions)</b>
     *
     * @throws NullPointerException if <b>conditions</b> or any element of <b>conditions</b> is <b>null</b>
     *
     * @since 1.8.8
     *
     * @see And#And(Stream)
     * @see LogicalCondition#optimized()
     */
    static Condition and(Stream<Condition> conditions) {
        return new And(conditions).optimized();
    }

    /**
     * Returns <b>new And(conditions).optimized()</b>.
     *
     * @param conditions a collection of conditions
     * @return an optimized <b>new And(conditions)</b>
     *
     * @throws NullPointerException if <b>conditions</b> or any element of <b>conditions</b> is <b>null</b>
     *
     * @since 1.8.8
     *
     * @see And#And(Collection)
     * @see LogicalCondition#optimized()
     */
    static Condition and(Collection<Condition> conditions) {
        return new And(conditions).optimized();
    }

    /**
     * Returns an optimized <b>new And(conditions)</b>.
     *
     * @param conditions an array of conditions
     * @return an optimized <b>new And(conditions)</b>
     *
     * @throws NullPointerException if <b>conditions</b> or any element of <b>conditions</b> is <b>null</b>
     *
     * @since 1.8.8
     *
     * @see And#And(Condition...)
     * @see LogicalCondition#optimized()
     */
    static Condition and(Condition... conditions) {
        return new And(conditions).optimized();
    }

    /**
     * Returns an optimized <b>new Or(conditions)</b>.
     *
     * @param conditions a stream of conditions
     * @return an optimized <b>new Or(conditions)</b>
     *
     * @throws NullPointerException if <b>conditions</b> or any element of <b>conditions</b> is <b>null</b>
     *
     * @since 1.8.8
     *
     * @see Or#Or(Stream)
     * @see LogicalCondition#optimized()
     */
    static Condition or(Stream<Condition> conditions) {
        return new Or(conditions).optimized();
    }

    /**
     * Returns an optimized <b>new Or(conditions)</b>.
     *
     * @param conditions a liat of conditions
     * @return an optimized <b>new Or(conditions)</b>
     *
     * @throws NullPointerException if <b>conditions</b> or any element of <b>conditions</b> is <b>null</b>
     *
     * @since 1.8.8
     *
     * @see Or#Or(Collection)
     * @see LogicalCondition#optimized()
     */
    static Condition or(Collection<Condition> conditions) {
        return new Or(conditions).optimized();
    }

    /**
     * Returns an optimized <b>new Or(conditions)</b>.
     *
     * @param conditions an array of conditions
     * @return an optimized <b>new Or(conditions)</b>
     *
     * @throws NullPointerException if <b>conditions</b> or any element of <b>conditions</b> is <b>null</b>
     *
     * @since 1.8.8
     *
     * @see Or#Or(Condition...)
     * @see LogicalCondition#optimized()
     */
    static Condition or(Condition... conditions) {
        return new Or(conditions).optimized();
    }
}
