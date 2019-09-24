// LogicalCondition.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lightsleep.Sql;
import org.lightsleep.database.Database;

/**
 * The abstract superclass of <b>And</b> and <b>Or</b>.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public abstract class LogicalCondition implements Condition {
	/** Logical operator. */
	protected enum Operator {
		/** AND */
		AND(" AND "),

		/** OR */
		OR (" OR ");

		private final String sql;

		private Operator(String sql) {
			this.sql = sql;
		}

		/**
		 * Returns a SQL string representation of this object.
		 *
		 * @return a SQL string representation
		 */
		public String sql() {
			return sql;
		}
	}

	// The operator (Operator.AND or Operator.OR)
	private final Operator operator;

	// The list of the conditions
	private final List<Condition> conditions;

	/**
	 * Constructs an <b>And</b> consisting of the conditions.
	 *
	 * @param operator the operator
	 * @param conditionStream the stream of conditions
	 *
	 * @throws NullPointerException <b>operator</b>, <b>conditionStream</b> or any of <b>conditions</b> is <b>null</b>
	 */
	public LogicalCondition(Operator operator, Stream<Condition> conditionStream) {
		this.operator = Objects.requireNonNull(operator, "operator is null");
		conditions = Objects.requireNonNull(conditionStream, "conditionStream is null")
			.map(condition -> Objects.requireNonNull(condition, "an element of conditions is null"))
			.flatMap(condition -> condition instanceof LogicalCondition && ((LogicalCondition)condition).operator == operator
				? ((LogicalCondition)condition).conditions().stream()
				: Stream.of(condition))
			.filter(condition -> !condition.isEmpty())
			.collect(Collectors.toList());
	}

	/**
	 * Returns an unmodifiable list of the conditions.
	 *
	 * @return an unmodifiable list of the conditions
	 */
	public List<Condition> conditions() {
		return Collections.unmodifiableList(conditions);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return conditions.isEmpty();
	}

	/**
	 * Optimizes and returns the components of this condition.<br>
	 * In particular,
	 * returns <b>Condition.EMPTY</b> if <b>conditions.isEmpty()</b>,
	 * <b>conditions.get(0)</b> if <b>conditions.size() == 1</b>,
	 * <b>this</b> otherwise.
	 *
	 * @return condition that is optimizing the components of this condition
	 *
	 * @since 1.8.8
	 */
	public Condition optimized() {
		if (conditions.isEmpty()) return Condition.EMPTY;
		if (conditions.size() == 1) return conditions.get(0);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String toString(Database database, Sql<E> sql, List<Object> parameters) {
		return String.join(operator.sql(),
			conditions.stream()
				.map(condition ->
					operator == Operator.AND && condition instanceof Or
					? '(' + condition.toString(database, sql, parameters) + ')'
					: condition.toString(database, sql, parameters)
				)
				.toArray(String[]::new)
		);
	}
}
