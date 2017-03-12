// OrderBy.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lightsleep.Sql;
import org.lightsleep.helper.Resource;

/**
 * Configures ORDER BY of SQL.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class OrderBy implements SqlComponent {
	//  class resources
	private static final Resource resource = new Resource(OrderBy.class);
	private static final String messageNoOrderByElement = resource.get("messageNoOrderByElement");

	/** The empty <b>OrderBy</b> */
	public static final OrderBy EMPTY = new OrderBy();

	/**
	 * The element of <b>OrderBy</b>.
	 */
	public static class Element extends Expression {
		// The string of ascend
		private String order = " ASC";

		/**
		 * Constructs a new <b>Element</b>.
		 *
		 * @param content the content of the expression
		 * @param arguments arguments embedded in the expression
		 *
		 * @throws NullPointerException if <b>content</b> or <b>arguments</b> is null
		 */
		public Element(String content, Object... arguments) {
			super(content, arguments);
		}

		/**
		 * Sets to ascend.
		 *
		 * @return this object
		 */
		public Element asc() {
			order = " ASC";
			return this;
		}

		/**
		 * Sets to descend.
		 *
		 * @return this object
		 */
		public Element desc() {
			order = " DESC";
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <E> String toString(Sql<E> sql, List<Object> parameters) {
			return super.toString(sql, parameters) + order;
		}
	}

	// 
	private List<Element> elements = new ArrayList<>();

	/**
	 * Constructs a new <b>OrderBy</b>.
	 */
	public OrderBy() {
	}

	/**
	 * Adds an element of the <b>OrderBy</b>.
	 *
	 * @param element an element to be added
	 * @return this object
	 *
	 * @throws NullPointerException if <b>element</b> is null
	 */
	public OrderBy add(Element element) {
		if (element == null) throw new NullPointerException("OrderBy.add: element == null");

		OrderBy orderBy = this == EMPTY ? new OrderBy() : this;
		orderBy.elements.add(element);
		return orderBy;
	}

	/**
	 * Sets to ascend.
	 *
	 * @return this object
	 */
	public OrderBy asc() {
		if (elements.size() == 0) throw new IllegalStateException(messageNoOrderByElement);

		elements.get(elements.size() - 1).asc();
		return this;
	}

	/**
	 * Sets to descend.
	 *
	 * @return this object
	 */
	public OrderBy desc() {
		if (elements.size() == 0) throw new IllegalStateException(messageNoOrderByElement);

		elements.get(elements.size() - 1).desc();
		return this;
	}

	/**
	 * Returns a list of the elements of the <b>OrderBy</b>.
	 *
	 * @return a list of the elements
	 */
	public List<Element> elements() {
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
	public <E> String toString(Sql<E> sql, List<Object> parameters) {
		StringBuilder buff = new StringBuilder();
		if (elements.size() > 0) {
			buff.append("ORDER BY ");
			String[] delimiter = new String[] {""};
		// 1.5.1
		//	elements.stream().forEach(element -> {
			elements.forEach(element -> {
		////
				buff.append(delimiter[0]).append(element.toString(sql, parameters));
				delimiter[0] = ", ";
			});
		}
		return buff.toString();
	}
}
