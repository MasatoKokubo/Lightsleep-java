// Not.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.List;
import java.util.Objects;

import org.lightsleep.Sql;

/**
 * Configure the negative condition.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class Not implements Condition {
	private Condition condition;

	/**
	 * Constructs a new <b>Not</b>.
	 *
	 * @param condition a condition
	 */
	public Not(Condition condition) {
		this.condition = Objects.requireNonNull(condition, "condition");
	}

	/**
	 * Returns the condition.
	 *
	 * @return the condition
	 */
	public Condition condition() {
		return condition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return condition.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String toString(Sql<E> sql, List<Object> parameters) {
		return "NOT (" + condition.toString(sql, parameters) + ")";
	}
}
