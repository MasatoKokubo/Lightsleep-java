// OrSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.component

import java.util.stream.Stream

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.database.*
import org.lightsleep.test.entity.*

import spock.lang.*

// OrSpec
@Unroll
class OrSpec extends Specification {
	def "01 A OR EMPTY (array)"() {
	/**/DebugTrace.enter()

		when:
			def condition = new Or(Condition.of('A'), Condition.EMPTY)

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == 'A'

	/**/DebugTrace.leave()
	}

	def "02 (A OR B OR C) (array)"() {
	/**/DebugTrace.enter()

		when:
			def condition = new Or(Condition.of('A'), Condition.of('B'), Condition.of('C'))

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == '(A OR B OR C)'

	/**/DebugTrace.leave()
	}

	def "03 empty (list)"() {
	/**/DebugTrace.enter()

		when:
			List<Condition> conditions = []
			def condition = new Or(conditions)

		then:
			condition.empty

	/**/DebugTrace.leave()
	}

	def "04 A OR empty (list)"() {
	/**/DebugTrace.enter()

		when:
			def condition = new Or(Arrays.asList(Condition.of('A'), Condition.EMPTY))

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == 'A'

	/**/DebugTrace.leave()
	}

	def "05 (A OR B OR C) (list)"() {
	/**/DebugTrace.enter()

		when:
			def condition = new Or(Arrays.asList(Condition.of('A'), Condition.of('B'), Condition.of('C')))

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == '(A OR B OR C)'

	/**/DebugTrace.leave()
	}

	def "06 A OR empty (stream)"() {
	/**/DebugTrace.enter()

		when:
			def condition = new Or(Stream.of(Condition.of('A'), Condition.EMPTY))

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == 'A'

	/**/DebugTrace.leave()
	}

	def "07 (A OR B OR C) (stream)"() {
	/**/DebugTrace.enter()

		when:
			def condition = new Or(Stream.of(Condition.of('A'), Condition.of('B'), Condition.of('C')))

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == '(A OR B OR C)'

	/**/DebugTrace.leave()
	}
}
