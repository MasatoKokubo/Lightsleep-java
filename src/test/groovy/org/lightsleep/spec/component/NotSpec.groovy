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
    def "NotSpec 01 NOT empty"() {
        DebugTrace.enter() // for Debugging

        when: def condition = Condition.EMPTY.not()
        then:
            condition.empty
            condition.getClass() != Not

        DebugTrace.leave() // for Debugging
    }

    def "NotSpec 02 NOT A = B"() {
        DebugTrace.enter() // for Debugging

        when: def condition = Condition.of('A = B').not()
        then: !condition.empty

        when: def string = condition.toString(Standard.instance, new Sql<>(Contact), new ArrayList<Object>())
        then: string == 'NOT(A = B)'

        DebugTrace.leave() // for Debugging
    }

    def "NotSpec 03 NOT NOT A = B"() {
        DebugTrace.enter() // for Debugging

        when: def condition = Condition.of('A = B').not().not()
        then: !condition.empty

        when: def string = condition.toString(Standard.instance, new Sql<>(Contact), new ArrayList<Object>())
        then: string == 'A = B'

        DebugTrace.leave() // for Debugging
    }

    def "NotSpec 04 exception - null argument"() {
        DebugTrace.enter() // for Debugging

        when: new Not(null)
        then: thrown NullPointerException

        DebugTrace.leave() // for Debugging
    }
}
