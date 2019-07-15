// ConditionSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.component

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.database.*
import org.lightsleep.entity.*
import spock.lang.*

// ConditionSpec
@Unroll
class ConditionSpec extends Specification {
	static class Entity {
		@Key
		int id
	}

	def "ConditionSpec isEmpty"() {
		expect:
			Condition.EMPTY.empty
	}

	def "ConditionSpec of"() {
		expect:
			Condition.of('').empty
			Condition.of('A') instanceof Expression
			Condition.of('A', 1) instanceof Expression
			Condition.of('{}', 'A') instanceof Expression
			Condition.of(new Entity()) instanceof EntityCondition
			Condition.of('A', new Sql<>(Entity), new Sql<>(Entity)) instanceof SubqueryCondition
			Condition.of(new Sql<>(Entity), new Sql<>(Entity), 'A') instanceof SubqueryCondition // 3.1.0

	}

	def "ConditionSpec not"() {
		setup:
			def sql = new Sql<>(Entity)

		expect:
			Condition.EMPTY.not() == Condition.EMPTY
			Condition.of('A').not() instanceof Not
			Condition.of('A', 1).not() instanceof Not
			Condition.of('{}', 'A').not() instanceof Not
			Condition.of(new Entity()).not() instanceof Not
			Condition.of('A', new Sql<>(Entity), new Sql<>(Entity)).not() instanceof Not
			Condition.of(new Sql<>(Entity), new Sql<>(Entity), 'A').not() instanceof Not // 3.1.0

			Condition.EMPTY.not().not() == Condition.EMPTY
			Condition.of('A').not().not() instanceof Expression
			Condition.of('A', 1).not().not() instanceof Expression
			Condition.of('{}', 'A').not().not() instanceof Expression
			Condition.of(new Entity()).not().not() instanceof EntityCondition
			Condition.of('A', new Sql<>(Entity), new Sql<>(Entity)).not().not() instanceof SubqueryCondition
			Condition.of(new Sql<>(Entity), new Sql<>(Entity), 'A').not().not() instanceof SubqueryCondition // 3.1.0

		when:
			def condition1 = Condition.of('A')
			def condition2 = condition1.not()

		then:
			condition1.toString(Standard.instance, sql, []) == 'A'
			condition2.toString(Standard.instance, sql, []) == 'NOT(A)'
	}

	def "ConditionSpec and"() {
		setup:
			def sql = new Sql<>(Entity)

		expect:
			Condition.of('A').and('A') instanceof And
			Condition.of('A', 1).and('A', 1) instanceof And
			Condition.of('{}', 'A').and('{}', 'A') instanceof And
			Condition.of(new Entity()).and(new Entity()) instanceof And
			Condition.of('A', new Sql<>(Entity), new Sql<>(Entity)).and('A', new Sql<>(Entity), new Sql<>(Entity)) instanceof And
			Condition.of(new Sql<>(Entity), new Sql<>(Entity), 'A').and(new Sql<>(Entity), new Sql<>(Entity), 'A') instanceof And // 3.1.0

			Condition.EMPTY.and(Condition.EMPTY) == Condition.EMPTY
			Condition.EMPTY.and('A') instanceof Expression
			Condition.EMPTY.and('A', 1) instanceof Expression
			Condition.EMPTY.and('{}', 'A') instanceof Expression
			Condition.EMPTY.and(new Entity()) instanceof EntityCondition
			Condition.EMPTY.and('A', new Sql<>(Entity), new Sql<>(Entity)) instanceof SubqueryCondition
			Condition.EMPTY.and(new Sql<>(Entity), new Sql<>(Entity), 'A') instanceof SubqueryCondition // 3.1.0

			Condition.of('A').and(Condition.EMPTY) instanceof Expression
			Condition.of('A', 1).and(Condition.EMPTY) instanceof Expression
			Condition.of('{}', 'A').and(Condition.EMPTY) instanceof Expression
			Condition.of(new Entity()).and(Condition.EMPTY) instanceof EntityCondition
			Condition.of('A', new Sql<>(Entity), new Sql<>(Entity)).and(Condition.EMPTY) instanceof SubqueryCondition
			Condition.of(new Sql<>(Entity), new Sql<>(Entity), 'A').and(Condition.EMPTY) instanceof SubqueryCondition // 3.1.0

		when:
			def condition1 = Condition.of('A')
			def condition2 = condition1.and('B')
			def condition3 = Condition.of('C')
			def condition4 = condition3.and('D')
			def condition5 = condition2.or(condition4)

		then:
			condition1.toString(Standard.instance, sql, []) == 'A'
			condition2.toString(Standard.instance, sql, []) == 'A AND B'
			condition3.toString(Standard.instance, sql, []) == 'C'
			condition4.toString(Standard.instance, sql, []) == 'C AND D'
			condition5.toString(Standard.instance, sql, []) == 'A AND B OR C AND D'
	}

	def "ConditionSpec or"() {
		setup:
			def sql = new Sql<>(Entity)

		expect:
			Condition.of('A').or('A') instanceof Or
			Condition.of('A', 1).or('A', 1) instanceof Or
			Condition.of('{}', 'A').or('{}', 'A') instanceof Or
			Condition.of(new Entity()).or(new Entity()) instanceof Or
			Condition.of('', new Sql<>(Entity), new Sql<>(Entity)).or('', new Sql<>(Entity), new Sql<>(Entity)) instanceof Or
			Condition.of(new Sql<>(Entity), new Sql<>(Entity), '').or(new Sql<>(Entity), new Sql<>(Entity), '') instanceof Or // 3.1.0

			Condition.EMPTY.or(Condition.EMPTY) == Condition.EMPTY
			Condition.EMPTY.or('A') instanceof Expression
			Condition.EMPTY.or('A', 1) instanceof Expression
			Condition.EMPTY.or('{}', 'A') instanceof Expression
			Condition.EMPTY.or(new Entity()) instanceof EntityCondition
			Condition.EMPTY.or('A', new Sql<>(Entity), new Sql<>(Entity)) instanceof SubqueryCondition
			Condition.EMPTY.or(new Sql<>(Entity), new Sql<>(Entity), 'A') instanceof SubqueryCondition // 3.1.0

			Condition.of('A').or(Condition.EMPTY) instanceof Expression
			Condition.of('A', 1).or(Condition.EMPTY) instanceof Expression
			Condition.of('{}', 'A').or(Condition.EMPTY) instanceof Expression
			Condition.of(new Entity()).or(Condition.EMPTY) instanceof EntityCondition
			Condition.of('A', new Sql<>(Entity), new Sql<>(Entity)).or(Condition.EMPTY) instanceof SubqueryCondition
			Condition.of(new Sql<>(Entity), new Sql<>(Entity), 'A').or(Condition.EMPTY) instanceof SubqueryCondition // 3.1.0

		when:
			def condition1 = Condition.of('A')
			def condition2 = condition1.or('B')
			def condition3 = Condition.of('C')
			def condition4 = condition3.or('D')
			def condition5 = condition2.and(condition4)

		then:
			condition1.toString(Standard.instance, sql, []) == 'A'
			condition2.toString(Standard.instance, sql, []) == 'A OR B'
			condition3.toString(Standard.instance, sql, []) == 'C'
			condition4.toString(Standard.instance, sql, []) == 'C OR D'
			condition5.toString(Standard.instance, sql, []) == '(A OR B) AND (C OR D)'
	}
}
