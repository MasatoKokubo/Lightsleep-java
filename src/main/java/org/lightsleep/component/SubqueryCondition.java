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
	// The expression
	private final Expression expression;

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
	 * @throws NullPointerException if <b>expression</b>, <b>outerSql</b> or <b>subSql</b> is null
	 */
	public <E> SubqueryCondition(Expression expression, Sql<E> outerSql, Sql<SE> subSql) {
		this.expression = Objects.requireNonNull(expression, "expression");
		this.subSql = Objects.requireNonNull(subSql, "subSql");
		subSql.addSqlEntityInfo(Objects.requireNonNull(outerSql, "outerSql"));
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
// 2.1.0
//	public <E> String toString(Sql<E> sql, List<Object> parameters) {
	public <E> String toString(Database database, Sql<E> sql, List<Object> parameters) {
		Objects.requireNonNull(database, "database");
////
		StringBuilder buff = new StringBuilder();

	// 2.1.0
	//	buff.append(expression.toString(sql, parameters));
		buff.append(expression.toString(database, sql, parameters));
	////
		buff.append(" (")
		// 2.1.0
		//	.append(Sql.getDatabase().subSelectSql(subSql, () -> "*", parameters))
			.append(database.subSelectSql(subSql, () -> "*", parameters))
		////
			.append(')');
		return buff.toString();
	}
}
