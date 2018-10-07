// SqlStringSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.component

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.database.*
import org.lightsleep.test.entity.*

import spock.lang.*

// SqlStringSpec
@Unroll
class SqlStringSpec extends Specification {
	def "SqlStringSpec 01 A"() {
		DebugTrace.enter() // for Debugging

		when: SqlString sqlString = new SqlString("A")
		then:
			sqlString.content() == 'A'
			sqlString.toString() == 'A'

		DebugTrace.leave() // for Debugging
	}

	def "SqlStringSpec 02 null"() {
		DebugTrace.enter() // for Debugging

		when: SqlString sqlString = new SqlString(null)
		then:
			sqlString.content() == null
			sqlString.toString() == 'NULL'

		DebugTrace.leave() // for Debugging
	}
}
