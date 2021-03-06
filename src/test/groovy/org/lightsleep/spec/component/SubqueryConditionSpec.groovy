// SubqueryConditionSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.component

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.database.*
import org.lightsleep.test.entity.*

import spock.lang.*

// SubqueryConditionSpec
@Unroll
class SubqueryConditionSpec extends Specification {
    // SubqueryCondition
    def "SqlStringSpec 01 of subquery"() {
        DebugTrace.enter() // for Debugging

        when:def condition = Condition.of("EXISTS", new Sql<>(Contact, "C"),
                new Sql<>(Address, "A").where("{A.postCode}={}", "1234567"))
        then: !condition.empty

        when: def string = condition.toString(Standard.instance, new Sql<>(Contact), new ArrayList<Object>())
        then: string == "EXISTS (SELECT * FROM Address A WHERE A.postCode='1234567')"

        DebugTrace.leave() // for Debugging
    }

    def "SqlStringSpec 02 AND subquery"() {
        DebugTrace.enter() // for Debugging

        when: def condition = Condition.EMPTY.and("EXISTS", new Sql<>(Contact, "C"),
                new Sql<>(Address, "A").where("{A.postCode}={}", "1234567"))
        then: !condition.empty

        when: def string = condition.toString(Standard.instance, new Sql<>(Contact), new ArrayList<Object>())
        then: string == "EXISTS (SELECT * FROM Address A WHERE A.postCode='1234567')"

        DebugTrace.leave() // for Debugging
    }

    def "SqlStringSpec 03 OR subquery"() {
        DebugTrace.enter() // for Debugging

        when: def condition = Condition.EMPTY.or("EXISTS", new Sql<>(Contact, "C"),
                new Sql<>(Address, "A").where("{A.postCode}={}", "1234567"))
        then: !condition.empty

        when: def string = condition.toString(Standard.instance, new Sql<>(Contact), new ArrayList<Object>())
        then: string == "EXISTS (SELECT * FROM Address A WHERE A.postCode='1234567')"

        DebugTrace.leave() // for Debugging
    }

    def "SqlStringSpec 04 exception - null content"() {
        DebugTrace.enter() // for Debugging

        when: new SubqueryCondition<>(null, new Sql<>(Contact), new Sql<>(Contact))
        then: thrown NullPointerException

        DebugTrace.leave() // for Debugging
    }

    def "SqlStringSpec 05 exception - null argument [0]"() {
        DebugTrace.enter() // for Debugging

        when: new SubqueryCondition<>(new Expression(""), null, new Sql<>(Contact))
        then: thrown NullPointerException

        DebugTrace.leave() // for Debugging
    }

    def "SqlStringSpec 06 exception - null argument [1]"() {
        DebugTrace.enter() // for Debugging

        when: new SubqueryCondition<>(new Expression(""), new Sql<>(Contact), null)
        then: thrown NullPointerException

        DebugTrace.leave() // for Debugging
    }
}
