// Condition.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.Objects;

import org.lightsleep.Sql;

/**
 * A interface of condition for SQL.
 * @since 1.0.0
 * @author Masato Kokubo
 */
public interface Condition extends SqlComponent {
	/** The empty condition */
// 1.8.2
//	static final Condition EMPTY = new And();
	static final Condition EMPTY = of("/* EMPTY CONDITION */");
////

	/** The condition for all rows */
	static final Condition ALL = of("0 = 0");

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.8.2
	 */
	@Override
	default boolean isEmpty() {
		return this == EMPTY;
	}

// 1.8.3
//	/**
//	 * Returns a new expression condition.
//	 *
//	 * @param content the content of the expression condition
//	 * @return a new expression condition
//	 *
//	 * @throws NullPointerException if <b>content</b> is null
//	 *
//	 * @see Expression
//	 */
//	static Condition of(String content) {
//		return new Expression(content);
//	}
////

	/**
	 * Returns a new expression condition.
	 *
	 * @param content the content of the expression condition
	 * @param arguments the arguments of the expression condition
	 * @return a new expression condition
	 *
	 * @throws NullPointerException if <b>content</b> or <b>arguments</b> is null
	 *
	 * @see Expression
	 */
	static Condition of(String content, Object... arguments) {
		return new Expression(content, arguments);
	}

	/**
	 * Returns a new entity condition.
	 *
	 * @param <E> the entity class
	 * @param entity the entity of the entity condition
	 * @return a new entity condition
	 *
	 * @throws NullPointerException if <b>entity</b> is null
	 */
	static <E> Condition of(E entity) {
	// 1.8.3
		// for Groovy
		if (entity instanceof String)
			return new Expression((String)entity);
	////
		return new EntityCondition<E>(entity);
	}

	/**
	 * Returns a new subquery condition.
	 *
	 * @param <E> the entity class related to table of the outer sql.
	 * @param <SE> the entity class related to table of the subquery.
	 * @param content the content of the subquery condition.
	 * @param outerSql the <b>Sql</b> object of the outer query
	 * @param subSql the <b>Sql</b> of the subquery condition.
	 * @return a new subquery condition
	 *
	 * @throws NullPointerException if <b>content</b>, <b>outerSql</b> or <b>subSql</b> is null
	 */
	static <E, SE> Condition of(String content, Sql<E> outerSql, Sql<SE> subSql) {
		return new SubqueryCondition<SE>(new Expression(content), outerSql, subSql);
	}

	/**
	 * Returns NOT(this).
	 *
	 * @return NOT(this)
	 */
	default Condition not() {
		return isEmpty() ? this : this instanceof Not ? ((Not)this).condition() : new Not(this);
	}

	/**
	 * Returns (this AND <b>condition</b>).
	 *
	 * @param condition the condition
	 * @return this AND <b>condition</b>
	 *
	 * @throws NullPointerException if <b>condition</b> is null
	 *
	 * @see And
	 */
	default Condition and(Condition condition) {
	//	if (condition == null) throw new NullPointerException("Condition.and: condition == null");
		Objects.requireNonNull(condition, "condition");

		return condition.isEmpty() ? this : isEmpty() ? condition : new And(this, condition);
	}

	/**
	 * Returns (this AND the expression condition).
	 *
	 * @param content the content of the expression condition
	 * @param arguments the arguments of the expression condition
	 * @return this AND the expression condition
	 *
	 * @throws NullPointerException if <b>content</b> or <b>arguments</b> is null
	 *
	 * @see And
	 * @see Expression
	 */
	default Condition and(String content, Object... arguments) {
		return and(of(content, arguments));
	}

	/**
	 * Returns (this AND the subquery condition).
	 *
	 * @param <E> the entity class related to table of the outer sql.
	 * @param <SE> the entity class related to table of the subquery.
	 * @param content the content of the subquery condition.
	 * @param outerSql the <b>Sql</b> object of the outer query
	 * @param subSql the <b>Sql</b> of the subquery condition.
	 * @return this OR the subquery condition
	 *
	 * @throws NullPointerException if <b>content</b>, <b>outerSql</b> or <b>subSql</b> is null
	 *
	 * @see And
	 * @see SubqueryCondition
	 */
	default <E, SE> Condition and(String content, Sql<E> outerSql, Sql<SE> subSql) {
		return and(of(content, outerSql, subSql));
	}

	/**
	 * Returns (this OR <b>condition</b>).
	 *
	 * @param condition the condition
	 * @return this OR <b>condition</b>
	 *
	 * @throws NullPointerException if <b>condition</b> is null
	 *
	 * @see Or
	 */
	default Condition or(Condition condition) {
	//	if (condition == null) throw new NullPointerException("Condition.and: condition == null");
		Objects.requireNonNull(condition, "condition");

		return condition.isEmpty() ? this : isEmpty() ? condition : new Or(this, condition);
	}

	/**
	 * Returns (this OR the expression condition).
	 *
	 * @param content the content of the expression condition
	 * @param arguments the arguments of the expression condition
	 * @return this OR the expression condition
	 *
	 * @throws NullPointerException if <b>content</b> or <b>arguments</b> is null
	 *
	 * @see Or
	 * @see Expression
	 */
	default Condition or(String content, Object... arguments) {
		return or(of(content, arguments));
	}

	/**
	 * Returns (this OR the subquery condition).
	 *
	 * @param <E> the entity class related to table of the outer sql.
	 * @param <SE> the entity class related to table of the subquery.
	 * @param content the content of the subquery condition.
	 * @param outerSql the <b>Sql</b> object of the outer query
	 * @param subSql the <b>Sql</b> of the subquery condition.
	 * @return this OR the subquery condition
	 *
	 * @throws NullPointerException if <b>content</b>, <b>outerSql</b> or <b>subSql</b> is null
	 *
	 * @see Or
	 * @see SubqueryCondition
	 */
	default <E, SE> Condition or(String content, Sql<E> outerSql, Sql<SE> subSql) {
		return or(of(content, outerSql, subSql));
	}
}
