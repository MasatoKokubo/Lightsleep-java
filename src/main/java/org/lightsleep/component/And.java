// And.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * Configures AND of conditions.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class And extends LogicalCondition {
	/**
	 * Constructs an empty <b>And</b>.
	 */
	public And() {
		super(Operator.AND);
	}

	/**
	 * Constructs an <b>And</b> consisting of the conditions.
	 *
	 * @param conditionStream the stream of conditions
	 *
	 * @throws NullPointerException <b>conditionStream</b> or any of <b>conditionStream</b> is <b>null</b>
	 */
	public And(Stream<Condition> conditionStream) {
		super(Operator.AND, conditionStream);
	}

	/**
	 * Constructs an <b>And</b> consisting of the conditions.
	 *
	 * @param conditions the collection of conditions
	 *
	 * @throws NullPointerException <b>conditions</b> or any of <b>conditions</b> is <b>null</b>
	 */
	public And(Collection<Condition> conditions) {
		super(Operator.AND, conditions.stream());
	}

	/**
	 * Constructs an <b>And</b> consisting of the conditions.
	 *
	 * @param conditions the array of conditions
	 *
	 * @throws NullPointerException <b>conditions</b> or any of <b>conditions</b> is <b>null</b>
	 */
	public And(Condition... conditions) {
		super(Operator.AND, Arrays.stream(conditions));
	}
}
