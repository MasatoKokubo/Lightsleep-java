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
	static final Condition ALL = of("0 = 0");

	/**
	 * Returns a new expression condition.
	 *
	 * @param content the content of the expression condition
	 * @param arguments the arguments of the expression condition
	 * @return a new expression condition
	 *
	 * @throws NullPointerException if <b>content</b> or <b>arguments</b> is null
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
	 * @throws NullPointerException if <b>entity</b> is null
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
	 * @param outerSql the <b>Sql</b> object of the outer query
	 * @param subSql the <b>Sql</b> of the subquery condition.
	 * @return a new subquery condition
	 *
	 * @throws NullPointerException if <b>content</b>, <b>outerSql</b> or <b>subSql</b> is null
	 *
	 * @see SubqueryCondition#SubqueryCondition(Expression, Sql, Sql)
	 */
	static <E, SE> Condition of(String content, Sql<E> outerSql, Sql<SE> subSql) {
		return new SubqueryCondition<>(new Expression(content), outerSql, subSql);
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
	 * @throws NullPointerException if <b>condition</b> is null
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
	 * @throws NullPointerException if <b>content</b> or <b>arguments</b> is null
	 *
	 * @see And#And(Condition...)
	 * @see Expression#Expression(String, Object...)
	 * @see LogicalCondition#optimized()
	 */
	default Condition and(String content, Object... arguments) {
		return new And(this, new Expression(content, arguments)).optimized();
	}

	/**
	 * Returns an optimized (this AND the subquery condition).
	 *
	 * @param <E> the type of the entity related to table of the outer sql.
	 * @param <SE> the type of the entity related to table of the subquery.
	 * @param content the content of the subquery condition.
	 * @param outerSql the <b>Sql</b> object of the outer query
	 * @param subSql the <b>Sql</b> of the subquery condition.
	 * @return an optimized (this AND the subquery condition)
	 *
	 * @throws NullPointerException if <b>content</b>, <b>outerSql</b> or <b>subSql</b> is null
	 *
	 * @see And#And(Condition...)
	 * @see SubqueryCondition#SubqueryCondition(Expression, Sql, Sql)
	 * @see Expression#Expression(String, Object...)
	 * @see LogicalCondition#optimized()
	 */
	default <E, SE> Condition and(String content, Sql<E> outerSql, Sql<SE> subSql) {
		return new And(this, new SubqueryCondition<>(new Expression(content), outerSql, subSql)).optimized();
	}

	/**
	 * Returns an optimized (this OR <b>condition</b>).
	 *
	 * @param condition the condition
	 * @return optimized (this OR <b>condition</b>)
	 *
	 * @throws NullPointerException if <b>condition</b> is null
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
	 * @throws NullPointerException if <b>content</b> or <b>arguments</b> is null
	 *
	 * @see Or#Or(Condition...)
	 * @see Expression#Expression(String, Object...)
	 * @see LogicalCondition#optimized()
	 */
	default Condition or(String content, Object... arguments) {
		return new Or(this, new Expression(content, arguments)).optimized();
	}

	/**
	 * Returns an optimized (this OR the subquery condition).
	 *
	 * @param <E> the type of the entity related to table of the outer sql.
	 * @param <SE> the type of the entity related to table of the subquery.
	 * @param content the content of the subquery condition.
	 * @param outerSql the <b>Sql</b> object of the outer query
	 * @param subSql the <b>Sql</b> of the subquery condition.
	 * @return an optimized (this OR the subquery condition)
	 *
	 * @throws NullPointerException if <b>content</b>, <b>outerSql</b> or <b>subSql</b> is null
	 *
	 * @see Or#Or(Condition...)
	 * @see SubqueryCondition#SubqueryCondition(Expression, Sql, Sql)
	 * @see Expression#Expression(String, Object...)
	 * @see LogicalCondition#optimized()
	 */
	default <E, SE> Condition or(String content, Sql<E> outerSql, Sql<SE> subSql) {
		return new Or(this, new SubqueryCondition<>(new Expression(content), outerSql, subSql)).optimized();
	}

	/**
	 * Returns an optimized <b>new And(conditions)</b>.
	 *
	 * @param conditions a stream of conditions
	 * @return an optimized <b>new And(conditions)</b>
	 *
	 * @throws NullPointerException if <b>conditions</b> or any element of <b>conditions</b> is null
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
	 * @throws NullPointerException if <b>conditions</b> or any element of <b>conditions</b> is null
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
	 * @throws NullPointerException if <b>conditions</b> or any element of <b>conditions</b> is null
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
	 * @throws NullPointerException if <b>conditions</b> or any element of <b>conditions</b> is null
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
	 * @throws NullPointerException if <b>conditions</b> or any element of <b>conditions</b> is null
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
	 * @throws NullPointerException if <b>conditions</b> or any element of <b>conditions</b> is null
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
