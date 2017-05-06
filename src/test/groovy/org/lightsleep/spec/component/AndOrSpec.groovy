// AndOrSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.component

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.database.*
import org.lightsleep.test.entity.*

import spock.lang.*

// AndOrSpec
@Unroll
class AndOrSpec extends Specification {
	def "01 (((A OR B) AND (C OR D)) OR ((E OR F) AND (G OR H)))"() {
	/**/DebugTrace.enter()

		when:
			def condition = new Or(
				new And(
					Condition.EMPTY.or(Condition.of('A')).or(Condition.of('B')).or(Condition.EMPTY),
					Condition.of('C').or(Condition.of('D'))
				),
				new And(
					Condition.of('E').or(Condition.of('F')),
					Condition.of('G').or(Condition.of('H'))
				)
			)

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == '(((A OR B) AND (C OR D)) OR ((E OR F) AND (G OR H)))'

	/**/DebugTrace.leave()
	}

	def "02 (A AND B AND C AND D AND E AND F AND G AND H)"() {
	/**/DebugTrace.enter()

		when:
			def condition = new And(
				new And(
					Condition.EMPTY.or(Condition.of('A')).and(Condition.of('B')).and(Condition.EMPTY),
					Condition.of('C').and(Condition.of('D'))
				),
				new And(
					Condition.of('E').and(Condition.of('F')),
					Condition.of('G').and(Condition.of('H'))
				)
			)

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == '(A AND B AND C AND D AND E AND F AND G AND H)'

	/**/DebugTrace.leave()
	}

	def "03 (A OR B OR C OR D OR E OR F OR G OR H)"() {
 	/**/DebugTrace.enter()

		when:
			def condition = new Or(
				new Or(
					Condition.of('A').or(Condition.of('B')),
					Condition.of('C').or(Condition.of('D'))
				),
				new Or(
					Condition.of('E').or(Condition.of('F')),
					Condition.of('G').or(Condition.of('H'))
				)
			)

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == '(A OR B OR C OR D OR E OR F OR G OR H)'

	/**/DebugTrace.leave()
	}

	def "04 (A AND B AND C AND D)"() {
	/**/DebugTrace.enter()

		when:
			def condition = Condition.of('A').and('B').and('C').and('D')

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == '(A AND B AND C AND D)'

	/**/DebugTrace.leave()
	}

	def "05 (A OR B OR C OR D)"() {
	/**/DebugTrace.enter()

		when:
			def condition = Condition.of('A').or('B').or('C').or('D')

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == '(A OR B OR C OR D)'

	/**/DebugTrace.leave()
	}

	def "06 (((A OR B) AND C) OR D)"() {
	/**/DebugTrace.enter()

		when:
			def condition = Condition.of('A').or('B').and('C').or('D')

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == '(((A OR B) AND C) OR D)'

	/**/DebugTrace.leave()
	}

	def "07 exception - AND null"() {
	/**/DebugTrace.enter()

		when:
			Condition.EMPTY.and(null)

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}

	def "08  exception - OR null"() {
	/**/DebugTrace.enter()

		when:
			Condition.EMPTY.or(null)

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}
}
