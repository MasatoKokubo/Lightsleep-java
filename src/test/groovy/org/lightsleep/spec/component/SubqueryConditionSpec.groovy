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
	/**/DebugTrace.enter()

		when:
			def condition = Condition.of("EXISTS", new Sql<>(Contact.class, "C"),
				new Sql<>(Address.class, "A").where("{A.postCode}={}", "1234567"))

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print("string", string)

		then:
			string == "EXISTS (SELECT * FROM Address A WHERE A.postCode='1234567')"

	/**/DebugTrace.leave()
	}

	def "SqlStringSpec 02 AND subquery"() {
	/**/DebugTrace.enter()

		when:
			def condition = Condition.EMPTY.and("EXISTS", new Sql<>(Contact.class, "C"),
				new Sql<>(Address.class, "A").where("{A.postCode}={}", "1234567"))

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print("string", string)

		then:
			string == "EXISTS (SELECT * FROM Address A WHERE A.postCode='1234567')"

	/**/DebugTrace.leave()
	}

	def "SqlStringSpec 03 OR subquery"() {
	/**/DebugTrace.enter()

		when:
			def condition = Condition.EMPTY.or("EXISTS", new Sql<>(Contact.class, "C"),
				new Sql<>(Address.class, "A").where("{A.postCode}={}", "1234567"))

		then:
			!condition.empty

		when:
			def string = condition.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print("string", string)

		then:
			string == "EXISTS (SELECT * FROM Address A WHERE A.postCode='1234567')"

	/**/DebugTrace.leave()
	}

	def "SqlStringSpec 04 exception - null content"() {
	/**/DebugTrace.enter()

		when:
			new SubqueryCondition<>(null, new Sql<>(Contact.class), new Sql<>(Contact.class))

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}

	def "SqlStringSpec 05 exception - null argument [0]"() {
	/**/DebugTrace.enter()

		when:
			new SubqueryCondition<>(new Expression(""), null, new Sql<>(Contact.class))

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}

	def "SqlStringSpec 06 exception - null argument [1]"() {
	/**/DebugTrace.enter()

		when:
			new SubqueryCondition<>(new Expression(""), new Sql<>(Contact.class), null)

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}
}
