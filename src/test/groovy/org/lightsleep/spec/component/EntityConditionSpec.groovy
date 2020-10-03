// EntityConditionSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.component

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.database.*
import org.lightsleep.test.entity.*

import spock.lang.*

// EntityConditionSpec
@Unroll
class EntityConditionSpec extends Specification {
    def "EntityConditionSpec 01 normal"() {
        DebugTrace.enter() // for Debugging

        when:
            def contact = new Contact()
            contact.id = 1
            def condition = Condition.of(contact)

        then: !condition.empty

        when: def string = condition.toString(Standard.instance, new Sql<>(Contact), new ArrayList<Object>())
        then: string == 'id=1'

        DebugTrace.leave() // for Debugging
    }

    def "EntityConditionSpec 02 exception - null argument"() {
        DebugTrace.enter() // for Debugging

        when: Condition.of((Contact)null)
        then: thrown NullPointerException

        DebugTrace.leave() // for Debugging
    }

    static class Entity {
    }

    def "EntityConditionSpec 03 exception - entity without key property"() {
        DebugTrace.enter() // for Debugging

        when: Condition.of(new Entity())
        then: thrown IllegalArgumentException

        DebugTrace.leave() // for Debugging
    }
}
