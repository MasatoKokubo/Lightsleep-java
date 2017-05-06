// AndSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.component

import java.util.stream.Stream

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.database.*
import org.lightsleep.test.entity.*

import spock.lang.*

// AndSpec
@Unroll
class AndSpec extends Specification {
	def "01 A AND EMPTY (array)"() {
	/**/DebugTrace.enter()

		when:
			def condition = new And(Condition.of('A'), Condition.EMPTY)

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == 'A'

	/**/DebugTrace.leave()
	}

	def "02 (A AND B AND C) (array)"() {
	/**/DebugTrace.enter()

		when:
			def condition = new And(Condition.of('A'), Condition.of('B'), Condition.of('C'))

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == '(A AND B AND C)'

	/**/DebugTrace.leave()
	}

	def "03 empty (list)"() {
	/**/DebugTrace.enter()

		when:
			def conditions = new ArrayList<Condition>()
			def condition = new And(conditions)

		then:
			condition.empty

	/**/DebugTrace.leave()
	}

	def "04 A AND empty (list)"() {
	/**/DebugTrace.enter()

		when:
			def condition = new And(Arrays.asList(Condition.of('A'), Condition.EMPTY))

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == 'A'

	/**/DebugTrace.leave()
	}

	def "05 (A AND B AND C) (list)"() {
	/**/DebugTrace.enter()

		when:
			def condition = new And(Arrays.asList(Condition.of('A'), Condition.of('B'), Condition.of('C')))

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == '(A AND B AND C)'

	/**/DebugTrace.leave()
	}

	def "06 A AND empty (stream)"() {
	/**/DebugTrace.enter()

		when:
			def condition = new And(Stream.of(Condition.of('A'), Condition.EMPTY))

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == 'A'

	/**/DebugTrace.leave()
	}

	def "07 (A AND B AND C) (stream)"() {
	/**/DebugTrace.enter()

		when:
			def condition = new And(Stream.of(Condition.of('A'), Condition.of('B'), Condition.of('C')))

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == '(A AND B AND C)'

	/**/DebugTrace.leave()
	}
}
