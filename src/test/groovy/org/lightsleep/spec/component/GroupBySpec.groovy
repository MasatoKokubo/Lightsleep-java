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
	def "GroupBySpec empty"() {
	/**/DebugTrace.enter()

		expect:
			GroupBy.EMPTY.empty
			new GroupBy().empty
			GroupBy.EMPTY.toString(new Sql<>(Contact.class), new ArrayList<Object>()) == ''
			new GroupBy().toString(new Sql<>(Contact.class), new ArrayList<Object>()) == ''

	/**/DebugTrace.leave()
	}

	def "GroupBySpec GROUP BY A"() {
	/**/DebugTrace.enter()

		setup:
			def groupBy = GroupBy.EMPTY.add(new Expression('A'))
			def string = groupBy.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('groupBy', groupBy)
		/**/DebugTrace.print('string', string)

		expect:
			!groupBy.empty
			string == 'GROUP BY A'

	/**/DebugTrace.leave()
	}

	def "GroupBySpec GROUP BY A, B"() {
	/**/DebugTrace.enter()

		setup:
			def groupBy = GroupBy.EMPTY.add(new Expression('A')).add(new Expression('B'))
			def string = groupBy.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('groupBy', groupBy)
		/**/DebugTrace.print('string', string)

		expect:
			!groupBy.empty
			string == 'GROUP BY A, B'

	/**/DebugTrace.leave()
	}

	def "GroupBySpec equals clone"() {
	/**/DebugTrace.enter()

		setup:
			def groupBy1 = GroupBy.EMPTY.add('A {}', 1000).add('B {}', 1001)
			def groupBy2 = GroupBy.EMPTY.add('A {}', 500 + 500).add('B '+'{}', 1001)
			def groupBy3 = GroupBy.EMPTY.add('A {}', 1000).add('B {}', 1002)
		/**/DebugTrace.print('groupBy1', groupBy1)
		/**/DebugTrace.print('groupBy2', groupBy2)
		/**/DebugTrace.print('groupBy3', groupBy3)

		expect:
			GroupBy.EMPTY == GroupBy.EMPTY
			GroupBy.EMPTY == new GroupBy()
			groupBy1 != GroupBy.EMPTY
			groupBy1 == groupBy2
			groupBy1 != groupBy3
			groupBy1.clone() == groupBy1
			groupBy1.clone() == groupBy2.clone()

	/**/DebugTrace.leave()
	}

	def "GroupBySpec exception - add null"() {
	/**/DebugTrace.enter()

		when:
			GroupBy.EMPTY.add((Expression)null)

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}
}
