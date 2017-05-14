// Or.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Configures OR of conditions.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class Or extends LogicalCondition {
// 1.8.6
//	/**
//	 * Constructs an empty <b>Or</b>.
//	 */
//	public Or() {
//		super(Operator.OR);
//	}
////

	/**
	 * Constructs an <b>Or</b> consisting of the conditions.
	 *
	 * @param conditionStream the stream of conditions
	 *
	 * @throws NullPointerException <b>conditionStream</b> or any of <b>conditionStream</b> is null
	 */
	public Or(Stream<Condition> conditionStream) {
		super(Operator.OR, conditionStream);
	}

	/**
	 * Constructs an <b>Or</b> consisting of the conditions.
	 *
	 * @param conditions the collection of conditions
	 *
	 * @throws NullPointerException <b>conditions</b> or any of <b>conditions</b> is null
	 */
	public Or(Collection<Condition> conditions) {
	// 1.8.7
	//	super(Operator.OR, conditions.stream());
		super(Operator.OR, Objects.requireNonNull(conditions, "Collection<Condition> conditions").stream());
	////
	}

	/**
	 * Constructs an <b>Or</b> consisting of the conditions.

	 * @param conditions the array of conditions

	 * @throws NullPointerException <b>conditions</b> or any of <b>conditions</b> is null
	 */
	public Or(Condition... conditions) {
	// 1.8.7
	//	super(Operator.OR, Arrays.stream(conditions));
		super(Operator.OR, Arrays.stream(Objects.requireNonNull(conditions, "Condition[] conditions")));
	////
	}
}
