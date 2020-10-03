// OrderBy.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.lightsleep.Sql;
import org.lightsleep.database.Database;
import org.lightsleep.helper.Resource;

/**
 * Configures ORDER BY of SQL.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class OrderBy implements SqlComponent, Cloneable {
    //  class resources
    private static final Resource resource = new Resource(OrderBy.class);
    private static final String messageNoOrderByElement = resource.getString("messageNoOrderByElement");

    /**
     * The element of <b>OrderBy</b>.
     */
    public static class Element extends Expression {
        private static final String ASC  = " ASC";
        private static final String DESC = " DESC";

        // The string of ascend
        private String order = ASC;

        /**
         * Constructs a new <b>Element</b>.
         *
         * @param content the content of the expression
         * @param arguments arguments embedded in the expression
         *
         * @throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
         */
        public Element(String content, Object... arguments) {
            super(content, arguments);
        }

        /**
         * Sets in ascending order.
         *
         * @return this object
         */
        public Element asc() {
            order = ASC;
            return this;
        }

        /**
         * Sets in descending order.
         *
         * @return this object
         */
        public Element desc() {
            order = DESC;
            return this;
        }

        @Override
        public <E> String toString(Database database, Sql<E> sql, List<Object> parameters) {
            return super.toString(database, sql, parameters) + order;
        }

        /**
         * @since 1.9.1
         */
        @Override
        public int hashCode() {
            return 31 * super.hashCode() + order.hashCode();
        }

        /**
         * @since 1.9.1
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!super.equals(obj)) return false;
            if (getClass() != obj.getClass()) return false;
            Element other = (Element)obj;
            if (!order.equals(other.order)) return false;
            return true;
        }
    }

    // Elements
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
     * @throws NullPointerException if <b>element</b> is <b>null</b>
     */
    public OrderBy add(Element element) {
        elements.add(Objects.requireNonNull(element, "element is null"));
        return this;
    }

    /**
     * Adds an element of the <b>OrderBy</b>.
     *
     * @param content the content of the expression
     * @param arguments the arguments of the expression
     * @return this object
     *
     * @throws NullPointerException <b>content</b> or <b>arguments</b> is <b>null</b>
     *
     * @since 1.9.1
     */
    public OrderBy add(String content, Object... arguments) {
        return add(new Element(content, arguments));
    }

    /**
     * Sets the element of the last added in ascending order.
     *
     * @return this object
     *
     * @throws IllegalStateException if there is no element
     */
    public OrderBy asc() {
        if (elements.size() == 0) throw new IllegalStateException(messageNoOrderByElement);

        elements.get(elements.size() - 1).asc();
        return this;
    }

    /**
     * Sets the element of the last added in descending order.
     *
     * @return this object
     *
     * @throws IllegalStateException if there is no element
     */
    public OrderBy desc() {
        if (elements.size() == 0) throw new IllegalStateException(messageNoOrderByElement);

        elements.get(elements.size() - 1).desc();
        return this;
    }

    /**
     * Returns an unmodifiable list of the elements of the <b>OrderBy</b>.
     *
     * @return an unmodifiable list of the elements
     */
    public List<Element> elements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public boolean isEmpty() {
        return elements.size() == 0;
    }

    @Override
    public <E> String toString(Database database, Sql<E> sql, List<Object> parameters) {
        StringBuilder buff = new StringBuilder();
        if (elements.size() > 0) {
            buff.append("ORDER BY ");
            String[] delimiter = new String[] {""};
            elements.forEach(element -> {
                buff.append(delimiter[0]).append(element.toString(database, sql, parameters));
                delimiter[0] = ", ";
            });
        }
        return buff.toString();
    }

    /**
     * @since 1.9.1
     */
    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    /**
     * @since 1.9.1
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        OrderBy other = (OrderBy)obj;
        if (!elements.equals(other.elements)) return false;
        return true;
    }

    /**
     * @since 1.9.1
     */
    @Override
    public OrderBy clone() {
        OrderBy orderBy = new OrderBy();
        orderBy.elements.addAll(elements);
        return orderBy;
    }
}
