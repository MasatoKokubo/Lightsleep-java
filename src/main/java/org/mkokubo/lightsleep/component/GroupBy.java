/*
	GroupBy.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mkokubo.lightsleep.Sql;

/**
	Configures GROUP BY of SQL.

	@since 1.0
	@author Masato Kokubo
*/
public class GroupBy implements SqlComponent {
	/** The empty <b>GroupBy</b> */
	public static final GroupBy EMPTY = new GroupBy();

	// The list of the <b>GroupBy</b> elements
	private List<Expression> elements = new ArrayList<>();

	/**
		Constructs a new <b>GroupBy</b>.
	*/
	public GroupBy() {
	}

	/**
		Adds an element of the <b>GroupBy</b>.

		@param expression an element to be added

		@return this object

		@throws NullPointerException if <b>expression</b> is <b>null</b>
	*/
	public GroupBy add(Expression expression) {
		GroupBy groupBy = this == EMPTY ? new GroupBy() : this;
		groupBy.elements.add(expression);
		return groupBy;
	}

	/**
		Returns a list of the elements of the <b>GroupBy</b>.

		@return a list of the elements
	*/
	public List<Expression> elements() {
		return Collections.unmodifiableList(elements);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isEmpty() {
		return elements.size() == 0;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public <E> String toString(Sql<E> sql, List<Object> parameters) {
		StringBuilder buff = new StringBuilder();
		if (elements.size() > 0) {
			buff.append("GROUP BY ");
			String[] delimiter = new String[] {""};
			elements.stream().forEach(element -> {
				buff.append(delimiter[0]).append(element.toString(sql, parameters));
				delimiter[0] = ", ";
			});
		}
		return buff.toString();
	}
}
