// OrderBySpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.component

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.database.*
import org.lightsleep.test.entity.*

import spock.lang.*

// OrderBySpec
class OrderBySpec extends Specification {
	def "OrderBySpec empty"() {
	/**/DebugTrace.enter()

		expect:
			new OrderBy().empty
			new OrderBy().toString(new Sql<>(Contact), new ArrayList<Object>()) == ''

	/**/DebugTrace.leave()
	}

	def "OrderBySpec ORDER BY A ASC"() {
	/**/DebugTrace.enter()

		setup:
			def orderBy = new OrderBy().add(new OrderBy.Element('A'))
			def string = orderBy.toString(new Sql<>(Contact), new ArrayList<Object>())
		/**/DebugTrace.print('orderBy', orderBy)
		/**/DebugTrace.print('string', string)

		expect:
			!orderBy.empty
			string == 'ORDER BY A ASC'

	/**/DebugTrace.leave()
	}

	def "OrderBySpec ORDER BY A ASC, B DESC"() {
	/**/DebugTrace.enter()

		setup:
			def orderBy = new OrderBy()
				.add(new OrderBy.Element('A').asc())
				.add(new OrderBy.Element('B').desc())
			def string = orderBy.toString(new Sql<>(Contact), new ArrayList<Object>())
		/**/DebugTrace.print('orderBy', orderBy)
		/**/DebugTrace.print('string', string)

		expect:
			!orderBy.empty
			string == 'ORDER BY A ASC, B DESC'

	/**/DebugTrace.leave()
	}

	def "OrderBySpec ORDER BY A DESC, B ASC"() {
	/**/DebugTrace.enter()

		setup:
			def orderBy = new OrderBy()
				.add(new OrderBy.Element('A')).desc()
				.add(new OrderBy.Element('B')).asc()
			def string = orderBy.toString(new Sql<>(Contact), new ArrayList<Object>())
		/**/DebugTrace.print('orderBy', orderBy)
		/**/DebugTrace.print('string', string)

		expect:
			!orderBy.empty
			string == 'ORDER BY A DESC, B ASC'

	/**/DebugTrace.leave()
	}

	def "OrderBySpec equals clone"() {
	/**/DebugTrace.enter()

		setup:
			def orderBy1 = new OrderBy()
				.add(new OrderBy.Element('A {}', 1000)).desc()
				.add(new OrderBy.Element('B {}', 1001).asc()).asc()
			def orderBy2 = new OrderBy()
				.add(new OrderBy.Element('A {}', 500 + 500).desc())
				.add(new OrderBy.Element('B '+'{}', 1001)).asc()
			def orderBy3 = new OrderBy()
				.add(new OrderBy.Element('A {}', 1000)).desc()
				.add(new OrderBy.Element('B {}', 1001).asc()).desc()
		/**/DebugTrace.print('orderBy1', orderBy1)
		/**/DebugTrace.print('orderBy2', orderBy2)
		/**/DebugTrace.print('orderBy3', orderBy3)

		expect:
			orderBy1 != new OrderBy()
			orderBy1 == orderBy2
			orderBy1 != orderBy3
			orderBy1.clone() == orderBy1
			orderBy1.clone() == orderBy2.clone()

	/**/DebugTrace.leave()
	}

	def "OrderBySpec exception - add null"() {
	/**/DebugTrace.enter()

		when: new OrderBy().add((OrderBy.Element)null)
		then: thrown NullPointerException

	/**/DebugTrace.leave()
	}

	def "OrderBySpec exception - empty ASC / empty DESC"() {
	/**/DebugTrace.enter()

		when: new OrderBy().asc()
		then: thrown IllegalStateException

		when: new OrderBy().desc()
		then: thrown IllegalStateException

	/**/DebugTrace.leave()
	}
}
