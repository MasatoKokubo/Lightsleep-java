// GroupBy.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.lightsleep.Sql;
import org.lightsleep.database.Database;

/**
 * Configures GROUP BY of SQL.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class GroupBy implements SqlComponent, Cloneable {
	// The list of the <b>GroupBy</b> elements
	private List<Expression> elements = new ArrayList<>();

	/**
	 * Constructs a new <b>GroupBy</b>.
	 */
	public GroupBy() {
	}

	/**
	 * Adds an element of the <b>GroupBy</b>.
	 *
	 * @param expression an element to be added
	 * @return this object
	 *
	 * @throws NullPointerException if <b>expression</b> is <b>null</b>
	 */
	public GroupBy add(Expression expression) {
		elements.add(Objects.requireNonNull(expression, "expression is null"));
		return this;
	}

	/**
	 * Adds an element of the <b>GroupBy</b>.
	 *
	 * @param content the content of the expression
	 * @param arguments the arguments of the expression
	 * @return this object
	 *
	 * @throws NullPointerException <b>content</b> or <b>arguments</b> is <b>null</b>
	 *
	 * @since 1.9.1
	 */
	public GroupBy add(String content, Object... arguments) {
		return add(new Expression(content, arguments));
	}

	/**
	 * Returns an unmodifiable list of the elements of the <b>GroupBy</b>.
	 *
	 * @return an unmodifiable list of the elements
	 */
	public List<Expression> elements() {
		return Collections.unmodifiableList(elements);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return elements.size() == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String toString(Database database, Sql<E> sql, List<Object> parameters) {
		StringBuilder buff = new StringBuilder();
		if (elements.size() > 0) {
			buff.append("GROUP BY ");
			String[] delimiter = new String[] {""};
			elements.forEach(element -> {
				buff.append(delimiter[0]).append(element.toString(database, sql, parameters));
				delimiter[0] = ", ";
			});
		}
		return buff.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.9.1
	 */
	@Override
	public int hashCode() {
		return elements.hashCode();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.9.1
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		GroupBy other = (GroupBy)obj;
		if (!elements.equals(other.elements)) return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.9.1
	 */
	@Override
	public GroupBy clone() {
		GroupBy groupBy = new GroupBy();
		groupBy.elements.addAll(elements);
		return groupBy;
	}
}
