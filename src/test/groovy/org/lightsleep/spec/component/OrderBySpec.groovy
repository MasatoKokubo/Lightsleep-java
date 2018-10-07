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
		DebugTrace.enter() // for Debugging

		expect:
			new OrderBy().empty
			new OrderBy().toString(Standard.instance, new Sql<>(Contact), new ArrayList<Object>()) == ''

		DebugTrace.leave() // for Debugging
	}

	def "OrderBySpec ORDER BY A ASC"() {
		DebugTrace.enter() // for Debugging

		setup:
			def orderBy = new OrderBy().add(new OrderBy.Element('A'))
			def string = orderBy.toString(Standard.instance, new Sql<>(Contact), new ArrayList<Object>())
			DebugTrace.print('orderBy', orderBy) // for Debugging
			DebugTrace.print('string', string) // for Debugging

		expect:
			!orderBy.empty
			string == 'ORDER BY A ASC'

		DebugTrace.leave() // for Debugging
	}

	def "OrderBySpec ORDER BY A ASC, B DESC"() {
		DebugTrace.enter() // for Debugging

		setup:
			def orderBy = new OrderBy()
				.add(new OrderBy.Element('A').asc())
				.add(new OrderBy.Element('B').desc())
			def string = orderBy.toString(Standard.instance, new Sql<>(Contact), new ArrayList<Object>())
			DebugTrace.print('orderBy', orderBy) // for Debugging
			DebugTrace.print('string', string) // for Debugging

		expect:
			!orderBy.empty
			string == 'ORDER BY A ASC, B DESC'

		DebugTrace.leave() // for Debugging
	}

	def "OrderBySpec ORDER BY A DESC, B ASC"() {
		DebugTrace.enter() // for Debugging

		setup:
			def orderBy = new OrderBy()
				.add(new OrderBy.Element('A')).desc()
				.add(new OrderBy.Element('B')).asc()
			def string = orderBy.toString(Standard.instance, new Sql<>(Contact), new ArrayList<Object>())
			DebugTrace.print('orderBy', orderBy) // for Debugging
			DebugTrace.print('string', string) // for Debugging

		expect:
			!orderBy.empty
			string == 'ORDER BY A DESC, B ASC'

		DebugTrace.leave() // for Debugging
	}

	def "OrderBySpec equals clone"() {
		DebugTrace.enter() // for Debugging

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
			DebugTrace.print('orderBy1', orderBy1) // for Debugging
			DebugTrace.print('orderBy2', orderBy2) // for Debugging
			DebugTrace.print('orderBy3', orderBy3) // for Debugging

		expect:
			orderBy1 != new OrderBy()
			orderBy1 == orderBy2
			orderBy1 != orderBy3
			orderBy1.clone() == orderBy1
			orderBy1.clone() == orderBy2.clone()

		DebugTrace.leave() // for Debugging
	}

	def "OrderBySpec exception - add null"() {
		DebugTrace.enter() // for Debugging

		when: new OrderBy().add((OrderBy.Element)null)
		then: thrown NullPointerException

		DebugTrace.leave() // for Debugging
	}

	def "OrderBySpec exception - empty ASC / empty DESC"() {
		DebugTrace.enter() // for Debugging

		when: new OrderBy().asc()
		then: thrown IllegalStateException

		when: new OrderBy().desc()
		then: thrown IllegalStateException

		DebugTrace.leave() // for Debugging
	}
}
