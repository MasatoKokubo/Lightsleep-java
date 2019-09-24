// ExpressionSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.component

import java.sql.Date
import java.sql.Time
import java.sql.Timestamp

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.database.*
import org.lightsleep.helper.*
import org.lightsleep.test.entity.*

import spock.lang.*

// ExpressionSpec
@Unroll
class ExpressionSpec extends Specification {
	static databases = [
		Standard  .instance,
		Db2       .instance,
		MariaDB   .instance,
		MySQL     .instance,
		Oracle    .instance,
		PostgreSQL.instance,
		SQLite    .instance,
		SQLServer .instance,
	]

	@Shared TimeZone timeZone

	def setupSpec() {
		timeZone = TimeZone.default
		TimeZone.setDefault(TimeZone.getTimeZone("GMT-0"))
	}

	def cleanupSpec() {
		TimeZone.default = timeZone
	}

	def "ExpressionSpec normal"() {
		DebugTrace.enter() // for Debugging

		when: def expression = new Expression('\\{}, { }, {\t}, { \t}, {}', 1, 2, 3, null)
		then:
			expression.content() == '\\{}, { }, {\t}, { \t}, {}'
			expression.arguments().length == 4
			!expression.isEmpty()

		when: def string = expression.toString(Standard.instance, new Sql<>(Contact), new ArrayList<Object>())
		then: string == '{}, 1, 2, 3, NULL'

		DebugTrace.leave() // for Debugging
	}

	def "ExpressionSpec exception - less arguments"() {
		DebugTrace.enter() // for Debugging

		when:
			def expression = new Expression("{}, {}, {}, {}", 1.1, 1.2, 1.3)
			expression.toString(Standard.instance, new Sql<>(Contact), new ArrayList<Object>())

		then:
			def e = thrown MissingArgumentsException
			DebugTrace.print('e', e) // for Debugging
			e.message.indexOf(expression.content()) >= 0
			e.message.indexOf('' + expression.arguments().length) >= 0

		DebugTrace.leave() // for Debugging
	}

	def "ExpressionSpec exception - more arguments"() {
		DebugTrace.enter() // for Debugging

		when:
			def expression = new Expression("{}, {}", 1.1, 1.2, 1.3)
				expression.toString(Standard.instance, new Sql<>(Contact), new ArrayList<Object>())

		then:
			def e = thrown MissingArgumentsException
			DebugTrace.print('e', e) // for Debugging
			e.message.indexOf(expression.content()) >= 0
			e.message.indexOf('' + expression.arguments().length) >= 0

		DebugTrace.leave() // for Debugging
	}

	def "ExpressionSpec String argument"() {
		DebugTrace.enter() // for Debugging

		when:
			def expression = new Expression('{}', "AA'BB''CC")
			def string = expression.toString(Standard.instance, new Sql<>(Contact), new ArrayList<Object>())
			DebugTrace.print('string', string) // for Debugging

		then:
			string == "'AA''BB''''CC'"

		DebugTrace.leave() // for Debugging
	}

	def "ExpressionSpec Date argument - #databaseName"(Database database, String databaseName) {
		DebugTrace.enter() // for Debugging
		when:
			def expression = new Expression('{}', new Date(1 * 86400_000))
			def string = expression.toString(database, new Sql<>(Contact), new ArrayList<Object>())
			DebugTrace.print('string', string) // for Debugging

		then:
			if (database instanceof SQLite)
				assert string == "'1970-01-02'"

			else if (database instanceof SQLServer)
				assert string == "CAST('1970-01-02' AS DATE)"

			else
				assert string == "DATE'1970-01-02'"
		DebugTrace.leave() // for Debugging
		where:
			database << databases
			databaseName = database.getClass().simpleName
	}

	def "ExpressionSpec Time argument - #databaseName"(Database database, String databaseName) {
		DebugTrace.enter() // for Debugging
		when:
			def expression = new Expression('{}', new Time(1 * 3600_000 + 2 * 60_000 + 3 * 1000))
			def string = expression.toString(database, new Sql<>(Contact), new ArrayList<Object>())
			DebugTrace.print('string', string) // for Debugging

		then:
			if (database instanceof Oracle)
			//	assert string == "TO_TIMESTAMP('1970-01-01 01:02:03','YYYY-MM-DD HH24:MI:SS.FF3')"
				assert string == "TO_TIMESTAMP('1970-01-01 01:02:03','YYYY-MM-DD HH24:MI:SS')"
				
			else if (database instanceof SQLite)
				assert string == "'01:02:03'"

			else if (database instanceof SQLServer)
				assert string == "CAST('01:02:03' AS TIME)"

			else
				assert string == "TIME'01:02:03'"

		DebugTrace.leave() // for Debugging
		where:
			database << databases
			databaseName = database.getClass().simpleName
	}

//	def "ExpressionSpec Timestamp argument - #databaseName"(Database database, String databaseName) {
//	/**/DebugTrace.enter()
//
//		when:
//			def expression = new Expression('{}', new Timestamp(1 * 86400_000 + 23 * 3600_000 + 58 * 60_000 + 59 * 1000))
//			def string = expression.toString(database, new Sql<>(Contact), new ArrayList<Object>())
//		/**/DebugTrace.print('string', string)
//
//		then:
//			if (database instanceof SQLite)
//				assert string == "'1970-01-02 23:58:59.000'"
//
//			else if (database instanceof SQLServer)
//				assert string == "CAST('1970-01-02 23:58:59.0' AS DATETIME2)"
//
//			else
//				assert string == "TIMESTAMP'1970-01-02 23:58:59.000'"
//
//	/**/DebugTrace.leave()
//		where:
//			database << databases
//			databaseName = database.getClass().simpleName
//	}

	def "ExpressionSpec property reference 1"() {
		DebugTrace.enter() // for Debugging

		when:
			def sql = new Sql<>(Contact)
			def contact = new Contact()
			contact.name.last = 'Apple'
			contact.name.first = 'Akane'
			sql.setEntity(contact)

			def expression = new Expression(" {#name.first}||' '||{ # name . last } ")
			def string = expression.toString(Standard.instance, sql, new ArrayList<Object>())
			DebugTrace.print('string', string) // for Debugging

		then: string == " 'Akane'||' '||'Apple' "

		DebugTrace.leave() // for Debugging
	}

	def "ExpressionSpec property reference 2"() {
		DebugTrace.enter() // for Debugging

		when:
			def contact = new Contact()
			contact.name.last = "La'st"
			def sql = new Sql<>(Contact).setEntity(contact)
			def expression = new Expression("{ name.last } = { # name.last }")
			def string = expression.toString(Standard.instance, sql, new ArrayList<Object>())
			DebugTrace.print('string', string) // for Debugging

		then: string == "lastName = 'La''st'"

		DebugTrace.leave() // for Debugging
	}

	def "ExpressionSpec property reference 3"() {
		DebugTrace.enter() // for Debugging

		when:
			def expression = new Expression('{C.name.last}')
			def string = expression.toString(Standard.instance, new Sql<>(Contact, 'C'), new ArrayList<Object>())
			DebugTrace.print('string', string) // for Debugging

		then: string == 'C.lastName'

		DebugTrace.leave() // for Debugging
	}

	def "ExpressionSpec property reference 4"() {
		DebugTrace.enter() // for Debugging

		when:
			def expression = new Expression('{C_name.last}')
			def string = expression.toString(Standard.instance, new Sql<>(Contact, 'C'), new ArrayList<Object>())
			DebugTrace.print('string', string) // for Debugging

		then: string == 'C_lastName'

		DebugTrace.leave() // for Debugging
	}

	@Ignore // Groovy converts byte[] to Byte[] when passing to variable length argument methods.
	def "ExpressionSpec byte[] argument - #databaseName"(Database database, String databaseName) {
		DebugTrace.enter() // for Debugging
		when:
			def bytes = (Object)(byte[])[1, 2, 3]
			DebugTrace.print('bytes', bytes) // for Debugging
			def expression = new Expression('{}', bytes)
			def paramerters = new ArrayList<Object>()
			def string = expression.toString(database, new Sql<>(Contact), paramerters)
			DebugTrace.print('string', string) // for Debugging
			DebugTrace.print('paramerters', paramerters) // for Debugging

		then:
			if (database instanceof Standard || database instanceof MariaDB || database instanceof MySQL) {
				assert string == "X'010203'"
				assert paramerters.size() == 0

			} else if (database instanceof PostgreSQL) {
				assert string == "E'\\\\x010203'"
				assert paramerters.size() == 0

			} else {
				assert string == "?"
				assert paramerters.size() == 1
			}

		DebugTrace.leave() // for Debugging
		where:
			database << databases
			databaseName = database.getClass().simpleName
	}

	def "ExpressionSpec equals"() {
		DebugTrace.enter() // for Debugging

		setup:
			def expression1 = new Expression('A {}', 1000)
			def expression2 = new Expression('A ' + '{}', 500 + 500)
			def expression3 = new Expression('B {}', 1000)
			def expression4 = new Expression('A {}', 1001)
			def expression5 = new Expression('A {}')
			def expression6 = new Expression('A {}', 1000, 1001)
			DebugTrace.print('expression1', expression1) // for Debugging
			DebugTrace.print('expression2', expression2) // for Debugging
			DebugTrace.print('expression3', expression3) // for Debugging
			DebugTrace.print('expression4', expression4) // for Debugging
			DebugTrace.print('expression5', expression5) // for Debugging
			DebugTrace.print('expression6', expression6) // for Debugging

		expect:
			Expression.EMPTY == Expression.EMPTY
			Expression.EMPTY == new Expression("")
			expression1 != Expression.EMPTY
			expression1 == expression2
			expression1 != expression3
			expression1 != expression4
			expression1 != expression5
			expression1 != expression6

		DebugTrace.leave() // for Debugging
	}

	def "ExpressionSpec exception - missing property 1"() {
		DebugTrace.enter() // for Debugging

		when:
			def expression = new Expression('{P.name.last}')
			expression.toString(Standard.instance, new Sql<>(Contact, 'C'), new ArrayList<Object>())

		then:
			def e = thrown MissingPropertyException
			DebugTrace.print('e', e) // for Debugging
			DebugTrace.print('e', e) // for Debugging
			e.message.indexOf(Contact.class.name) >= 0
			e.message.indexOf('name.last') >= 0
			e.message.indexOf('P.name.last') >= 0

		DebugTrace.leave() // for Debugging
	}

	def "ExpressionSpec exception - missing property 2"() {
		DebugTrace.enter() // for Debugging

		when:
			def expression = new Expression("{P_name.last}")
			expression.toString(Standard.instance, new Sql<>(Contact, 'C'), new ArrayList<Object>())

		then:
			def e = thrown MissingPropertyException
			DebugTrace.print('e', e) // for Debugging
			e.message.indexOf(Contact.class.name) >= 0
			e.message.indexOf('P_name.last') >= 0
			e.message.indexOf('name.last') >= 0

		DebugTrace.leave() // for Debugging
	}

	def "ExpressionSpec exception - missing property 3"() {
		DebugTrace.enter() // for Debugging

		when:
			def expression = new Expression("{last}")
			expression.toString(Standard.instance, new Sql<>(Contact, 'C'), new ArrayList<Object>())

		then:
			def e = thrown MissingPropertyException
			DebugTrace.print('e', e) // for Debugging
			e.message.indexOf(Contact.class.name) >= 0
			e.message.indexOf('last') >= 0

		DebugTrace.leave() // for Debugging
	}

	def "ExpressionSpec exception - missing property 4"() {
		DebugTrace.enter() // for Debugging

		when:
			def expression = new Expression("{C.last}")
			expression.toString(Standard.instance, new Sql<>(Contact, 'C'), new ArrayList<Object>())

		then:
			def e = thrown MissingPropertyException
			DebugTrace.print('e', e) // for Debugging
			e.message.indexOf(Contact.class.name) >= 0
			e.message.indexOf('C.last') >= 0
			e.message.indexOf('last') >= 0

		DebugTrace.leave() // for Debugging
	}

	def "ExpressionSpec exception - missing property 5"() {
		DebugTrace.enter() // for Debugging

		when:
			def expression = new Expression("{C_last}")
			expression.toString(Standard.instance, new Sql<>(Contact, 'C'), new ArrayList<Object>())

		then:
			def e = thrown MissingPropertyException
			DebugTrace.print('e', e) // for Debugging
			e.message.indexOf(Contact.class.name) >= 0
			e.message.indexOf('C_last') >= 0
			e.message.indexOf('last') >= 0

		DebugTrace.leave() // for Debugging
	}

	def "ExpressionSpec exception - [content] argument is null 1"() {
		DebugTrace.enter() // for Debugging

		when: new Expression(null)
		then: thrown NullPointerException

		DebugTrace.leave() // for Debugging
	}

	def "ExpressionSpec exception - [arguments] argument is null 2"() {
		DebugTrace.enter() // for Debugging

		when: new Expression('', (Object[])null)
		then: thrown NullPointerException

		DebugTrace.leave() // for Debugging
	}

	def "ExpressionSpec exception - illegal value reference"() {
		DebugTrace.enter() // for Debugging

		when:
			def expression = new Expression("{name.last} = {#name.last}")
			expression.toString(Standard.instance, new Sql<>(Contact), new ArrayList<Object>())

		then: thrown NullPointerException

		DebugTrace.leave() // for Debugging
	}
}
