// NotSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.component

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.database.*
import org.lightsleep.test.entity.*

import spock.lang.*

// NotSpec
@Unroll
class NotSpec extends Specification {
	def "01 NOT empty"() {
	/**/DebugTrace.enter()

		when:
			def condition = Condition.EMPTY.not()
		/**/DebugTrace.print('condition', condition)

		then:
			condition.empty
			condition.getClass() != Not.class

	/**/DebugTrace.leave()
	}

	def "02 NOT A = B"() {
	/**/DebugTrace.enter()

		when:
			def condition = Condition.of('A = B').not()
		/**/DebugTrace.print('condition', condition)

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == 'NOT(A = B)'

	/**/DebugTrace.leave()
	}

	def "03 NOT NOT A = B"() {
	/**/DebugTrace.enter()

		when:
			def condition = Condition.of('A = B').not().not()
		/**/DebugTrace.print('condition', condition)

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == 'A = B'

	/**/DebugTrace.leave()
	}

	def "04 exception - null argument"() {
	/**/DebugTrace.enter()

		when:
			new Not(null)

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}
}
