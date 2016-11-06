/*
	Condition.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.component;

import org.lightsleep.Sql;

/**
	A interface of condition for SQL.
	@since 1.0.0
	@author Masato Kokubo
*/
public interface Condition extends SqlComponent {
	/** The empty condition */
	static final Condition EMPTY = new And();

	/** The condition for all rows */
	static final Condition ALL = of("0 = 0");

	/**
		Returns a new expression condition.

		@see Expression

		@param content the content of the expression condition

		@return a new expression condition

		@throws NullPointerException if <b>content</b> is <b>null</b>
	*/
	static Condition of(String content) {
		return new Expression(content);
	}

	/**
		Returns a new expression condition.

		@see Expression

		@param content the content of the expression condition
		@param arguments the arguments of the expression condition

		@return a new expression condition

		@throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
	*/
	static Condition of(String content, Object... arguments) {
		return new Expression(content, arguments);
	}

	/**
		Returns a new entity condition.

		@param <E> the entity class

		@param entity the entity of the entity condition

		@return a new entity condition

		@throws NullPointerException if <b>entity</b> is <b>null</b>
	*/
	static <E> Condition of(E entity) {
		return new EntityCondition<E>(entity);
	}

	/**
		Returns a new subquery condition.

		@param <E> the entity class corresponding to table of the outer sql.
		@param <SE> the entity class corresponding to table of the subquery.

		@param content the content of the subquery condition.
		@param outerSql the <b>Sql</b> object of the outer query
		@param subSql the <b>Sql</b> of the subquery condition.

		@return a new subquery condition

		@throws NullPointerException if <b>content</b>, <b>outerSql</b> or <b>subSql</b> is <b>null</b>
	*/
	static <E, SE> Condition of(String content, Sql<E> outerSql, Sql<SE> subSql) {
		return new SubqueryCondition<SE>(new Expression(content), outerSql, subSql);
	}

	/**
		Returns NOT(this).

		@return NOT(this)
	*/
	default Condition not() {
		return isEmpty() ? this : this instanceof Not ? ((Not)this).condition() : new Not(this);
	}

	/**
		Returns (this AND <b>condition</b>).

		@see And

		@param condition the condition

		@return this AND <b>condition</b>

		@throws NullPointerException if <b>condition</b> is <b>null</b>
	*/
	default Condition and(Condition condition) {
		if (condition == null) throw new NullPointerException("Condition.and: condition == null");

		return condition.isEmpty() ? this : isEmpty() ? condition : new And(this, condition);
	}

	/**
		Returns (this AND the expression condition).

		@see And
		@see Expression

		@param content the content of the expression condition
		@param arguments the arguments of the expression condition

		@return this AND the expression condition

		@throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
	*/
	default Condition and(String content, Object... arguments) {
		return and(of(content, arguments));
	}

	/**
		Returns (this AND the subquery condition).

		@see And
		@see SubqueryCondition

		@param <E> the entity class corresponding to table of the outer sql.
		@param <SE> the entity class corresponding to table of the subquery.

		@param content the content of the subquery condition.
		@param outerSql the <b>Sql</b> object of the outer query
		@param subSql the <b>Sql</b> of the subquery condition.

		@return this OR the subquery condition

		@throws NullPointerException if <b>content</b>, <b>outerSql</b> or <b>subSql</b> is <b>null</b>
	*/
	default <E, SE> Condition and(String content, Sql<E> outerSql, Sql<SE> subSql) {
		return and(of(content, outerSql, subSql));
	}

	/**
		Returns (this OR <b>condition</b>).

		@see Or

		@param condition the condition

		@return this OR <b>condition</b>

		@throws NullPointerException if <b>condition</b> is <b>null</b>
	*/
	default Condition or(Condition condition) {
		if (condition == null) throw new NullPointerException("Condition.and: condition == null");

		return condition.isEmpty() ? this : isEmpty() ? condition : new Or(this, condition);
	}

	/**
		Returns (this OR the expression condition).

		@see Or
		@see Expression

		@param content the content of the expression condition
		@param arguments the arguments of the expression condition

		@return this OR the expression condition

		@throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
	*/
	default Condition or(String content, Object... arguments) {
		return or(of(content, arguments));
	}

	/**
		Returns (this OR the subquery condition).

		@see Or
		@see SubqueryCondition

		@param <E> the entity class corresponding to table of the outer sql.
		@param <SE> the entity class corresponding to table of the subquery.

		@param content the content of the subquery condition.
		@param outerSql the <b>Sql</b> object of the outer query
		@param subSql the <b>Sql</b> of the subquery condition.

		@return this OR the subquery condition

		@throws NullPointerException if <b>content</b>, <b>outerSql</b> or <b>subSql</b> is <b>null</b>
	*/
	default <E, SE> Condition or(String content, Sql<E> outerSql, Sql<SE> subSql) {
		return or(of(content, outerSql, subSql));
	}
}
