// GroupBySpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.component

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.database.*
import org.lightsleep.test.entity.*

import spock.lang.*

// GroupBySpec
class GroupBySpec extends Specification {
	def "01 empty"() {
	/**/DebugTrace.enter()

		when:
			def groupBy = new GroupBy()
		/**/DebugTrace.print('groupBy', groupBy)

		then:
			groupBy.empty

		when:
			def string = groupBy.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == ''

	/**/DebugTrace.leave()
	}

	def "02 GROUP BY A"() {
	/**/DebugTrace.enter()

		when:
			def groupBy = GroupBy.EMPTY.add(new Expression('A'))
		/**/DebugTrace.print('groupBy', groupBy)

		then:
			!groupBy.empty

		when:
			def string = groupBy.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == 'GROUP BY A'

	/**/DebugTrace.leave()
	}

	def "03 GROUP BY A, B"() {
	/**/DebugTrace.enter()

		when:
			def groupBy = GroupBy.EMPTY.add(new Expression('A')).add(new Expression('B'))
		/**/DebugTrace.print('groupBy', groupBy)

		then:
			!groupBy.empty

		when:
			def string = groupBy.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == 'GROUP BY A, B'

	/**/DebugTrace.leave()
	}
}
