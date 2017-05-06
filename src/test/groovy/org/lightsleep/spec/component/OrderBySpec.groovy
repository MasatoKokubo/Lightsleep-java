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
	def "01 empty"() {
	/**/DebugTrace.enter()

		when:
			def orderBy = new OrderBy()
		/**/DebugTrace.print('orderBy', orderBy)

		then:
			orderBy.empty

		when:
			def string = orderBy.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == ''

	/**/DebugTrace.leave()
	}

	def "02 ORDER BY A ASC"() {
	/**/DebugTrace.enter()

		when:
			def orderBy = OrderBy.EMPTY.add(new OrderBy.Element('A'))
		/**/DebugTrace.print('orderBy', orderBy)

		then:
			!orderBy.empty

			def string = orderBy.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == 'ORDER BY A ASC'

	/**/DebugTrace.leave()
	}

	def "03 ORDER BY A ASC, B DESC"() {
	/**/DebugTrace.enter()

		when:
			def orderBy = OrderBy.EMPTY.add(new OrderBy.Element('A').asc()).add(new OrderBy.Element('B').desc())
		/**/DebugTrace.print('orderBy', orderBy)

		then:
			!orderBy.empty

		when:
			def string = orderBy.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == 'ORDER BY A ASC, B DESC'

	/**/DebugTrace.leave()
	}

	def "04 ORDER BY A DESC, B ASC"() {
	/**/DebugTrace.enter()

		when:
			def orderBy = OrderBy.EMPTY.add(new OrderBy.Element('A')).desc().add(new OrderBy.Element('B')).asc()
		/**/DebugTrace.print('orderBy', orderBy)

		then:
			!orderBy.empty

		when:
			def string = orderBy.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == 'ORDER BY A DESC, B ASC'

	/**/DebugTrace.leave()
	}

	def "05 exception - add null"() {
	/**/DebugTrace.enter()

		when:
			OrderBy.EMPTY.add((OrderBy.Element)null)

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}

	def "06 exception - empty ASC"() {
	/**/DebugTrace.enter()

		when:
			OrderBy.EMPTY.asc()

		then:
			thrown IllegalStateException

	/**/DebugTrace.leave()
	}

	def "07 exception - empty DESC"() {
	/**/DebugTrace.enter()

		when:
			OrderBy.EMPTY.desc()

		then:
			thrown IllegalStateException

	/**/DebugTrace.leave()
	}
}
