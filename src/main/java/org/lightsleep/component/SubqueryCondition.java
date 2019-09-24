// SubQueryCondition.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.List;
import java.util.Objects;

import org.lightsleep.Sql;
import org.lightsleep.database.Database;

/**
 * Configure a condition to use a subquery.
 *
 * @param <SE> the type of the entity of the subquery
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class SubqueryCondition<SE> implements Condition {
// 3.1.0
//	// The expression
//	private final Expression expression;
	// The left expression of the Subquery
	private final Expression leftExpression;

	// The right expression of the Subquery
	private final Expression rightExpression;
////

	// The Sql object related to the subquery
	private final Sql<SE> subSql;

	/**
	 * Constructs a new <b>SubqueryCondition</b>.
	 *
	 * @param <E> the type of the entity related to table of the outer sql.
	 * @param expression the expression of the left part from the SELECT statement of the subquery
	 * @param outerSql the <b>Sql</b> object of the outer query
	 * @param subSql the <b>Sql</b> object for the subquery
	 *
	 * @throws NullPointerException if <b>expression</b>, <b>outerSql</b> or <b>subSql</b> is <b>null</b>
	 */
	public <E> SubqueryCondition(Expression expression, Sql<E> outerSql, Sql<SE> subSql) {
	// 3.1.0
	//	this.expression = Objects.requireNonNull(expression, "expression");
		leftExpression = Objects.requireNonNull(expression, "expression is null");
		rightExpression = Expression.EMPTY;
	////
		this.subSql = Objects.requireNonNull(subSql, "subSql is null");
	// 3.1.0
		if (subSql.getWhere().isEmpty())
			subSql.where(Condition.ALL);
	////
		subSql.addSqlEntityInfo(Objects.requireNonNull(outerSql, "outerSql is null"));
	}

	/**
	 * Constructs a new <b>SubqueryCondition</b>.
	 *
	 * @param <E> the type of the entity related to table of the outer sql.
	 * @param outerSql the <b>Sql</b> object of the outer query
	 * @param subSql the <b>Sql</b> object for the subquery
	 * @param expression the expression of the right part from the SELECT statement of the subquery
	 *
	 * @throws NullPointerException if <b>expression</b>, <b>outerSql</b> or <b>subSql</b> is <b>null</b>
	 * @since 3.1.0
	 */
	public <E> SubqueryCondition(Sql<E> outerSql, Sql<SE> subSql, Expression expression) {
		leftExpression = Expression.EMPTY;
		rightExpression = Objects.requireNonNull(expression, "expression is null");
		this.subSql = Objects.requireNonNull(subSql, "subSql is null");
		if (subSql.getWhere().isEmpty())
			subSql.where(Condition.ALL);
		subSql.addSqlEntityInfo(Objects.requireNonNull(outerSql, "outerSql is null"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String toString(Database database, Sql<E> sql, List<Object> parameters) {
		Objects.requireNonNull(database, "database is null");
		StringBuilder buff = new StringBuilder();

	// 3.1.0
	//	buff.append(expression.toString(database, sql, parameters));
	//	buff.append(" (")
	//		.append(database.subSelectSql(subSql, () -> "*", parameters))
	//		.append(')');
		buff.append(leftExpression.toString(database, sql, parameters));
		buff.append(leftExpression.isEmpty() ? "(" : " (")
			.append(subSql.getColumns().isEmpty()
				? database.subSelectSql(subSql, () -> "*", parameters)
				: database.subSelectSql(subSql, parameters)
			)
			.append(rightExpression.isEmpty() ? ")" : ") ");
		buff.append(rightExpression.toString(database, sql, parameters));
	////
		return buff.toString();
	}
}
