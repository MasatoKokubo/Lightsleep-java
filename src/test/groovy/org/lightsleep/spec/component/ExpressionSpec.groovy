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
import org.lightsleep.test.entity.*

import spock.lang.*

// ExpressionSpec
@Unroll
class ExpressionSpec extends Specification {
	static databases = [
		Standard  .instance(),
		MySQL     .instance(),
		Oracle    .instance(),
		PostgreSQL.instance(),
		SQLite    .instance(),
		SQLServer .instance()
	]

	@Shared TimeZone timeZone

	def setupSpec() {
		timeZone = TimeZone.default
		TimeZone.setDefault(TimeZone.getTimeZone("GMT-0"))
	}

	def cleanupSpec() {
		TimeZone.default = timeZone
	}

	def "01 normal"() {
	/**/DebugTrace.enter()

		when:
			def expression = new Expression('\\{}, { }, {\t}, { \t}, {}', 1, 2, 3, null)

		then:
			expression.content() == '\\{}, { }, {\t}, { \t}, {}'
			expression.arguments().length == 4
			!expression.isEmpty()

		when:
			def string = expression.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == '{}, 1, 2, 3, NULL'

	/**/DebugTrace.leave()
	}

	def "02 exception - more placement"() {
	/**/DebugTrace.enter()

		when:
			def expression = new Expression("{}, {}, {}, {}", 1.1, 1.2, 1.3)
			expression.toString(new Sql<>(Contact.class), new ArrayList<Object>())

		then:
			def e = thrown IllegalArgumentException
			e.message.indexOf(expression.content()) >= 0
			e.message.indexOf('' + expression.arguments().length) >= 0

	/**/DebugTrace.leave()
	}

	def "03 exception - more argument"() {
	/**/DebugTrace.enter()

		when:
			def expression = new Expression("{}, {}", 1.1, 1.2, 1.3)
				expression.toString(new Sql<>(Contact.class), new ArrayList<Object>())

		then:
			def e = thrown IllegalArgumentException
			e.message.indexOf(expression.content()) >= 0
			e.message.indexOf('' + expression.arguments().length) >= 0

	/**/DebugTrace.leave()
	}

	def "04 String argument"() {
	/**/DebugTrace.enter()

		when:
			def expression = new Expression('{}', "AA'BB''CC")
			def string = expression.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == "'AA''BB''''CC'"

	/**/DebugTrace.leave()
	}

	def "05 Date argument - #databaseName"(Database database, String databaseName) {
	/**/DebugTrace.enter()
		setup:
			def beforeDatabase = Sql.database
			Sql.database = database

		when:
			def expression = new Expression('{}', new Date(1 * 86400_000))
			def string = expression.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			if (Sql.database instanceof SQLite)
				assert string == "'1970-01-02'"

			else if (Sql.database instanceof SQLServer)
				assert string == "CAST('1970-01-02' AS DATE)"

			else
				assert string == "DATE'1970-01-02'"

		cleanup:
			Sql.database = beforeDatabase ?: Standard.instance()

	/**/DebugTrace.leave()
		where:
			database << databases
			databaseName = database.getClass().simpleName
	}

	def "06 Time argument - #databaseName"(Database database, String databaseName) {
	/**/DebugTrace.enter()
		setup:
			def beforeDatabase = Sql.database
			Sql.database = database

		when:
			def expression = new Expression('{}', new Time(1 * 3600_000 + 2 * 60_000 + 3 * 1000))
			def string = expression.toString(new Sql<>(Contact.class), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			if (Sql.database instanceof Oracle)
				assert string == "TO_TIMESTAMP('1970-01-01 01:02:03','YYYY-MM-DD HH24:MI:SS.FF3')"

			else if (Sql.database instanceof SQLite)
				assert string == "'01:02:03'"

			else if (Sql.database instanceof SQLServer)
				assert string == "CAST('01:02:03' AS TIME)"

			else
				assert string == "TIME'01:02:03'"

		cleanup:
			Sql.database = beforeDatabase ?: Standard.instance()

	/**/DebugTrace.leave()
		where:
			database << databases
			databaseName = database.getClass().simpleName
	}

//	def "07 Timestamp argument - #databaseName"(Database database, String databaseName) {
//	/**/DebugTrace.enter()
//
//		setup:
//			def beforeDatabase = Sql.database
//			Sql.database = database
//
//		when:
//			def expression = new Expression('{}', new Timestamp(1 * 86400_000 + 23 * 3600_000 + 58 * 60_000 + 59 * 1000))
//			def string = expression.toString(new Sql<>(Contact.class), new ArrayList<Object>())
//		/**/DebugTrace.print('string', string)
//
//		then:
//			if (Sql.database instanceof SQLite)
//				assert string == "'1970-01-02 23:58:59.000'"
//
//			else if (Sql.database instanceof SQLServer)
//				assert string == "CAST('1970-01-02 23:58:59.0' AS DATETIME2)"
//
//			else
//				assert string == "TIMESTAMP'1970-01-02 23:58:59.000'"
//
//		cleanup:
//			Sql.database = beforeDatabase ?: Standard.instance()
//
//	/**/DebugTrace.leave()
//		where:
//			database << databases
//			databaseName = database.getClass().simpleName
//	}

	def "08 property reference"() {
	/**/DebugTrace.enter()

		when:
			def sql = new Sql<>(Contact.class)
			def contact = new Contact()
			contact.name.family = 'Apple'
			contact.name.given = 'Akane'
			sql.setEntity(contact)

			def expression = new Expression(" {#name.given}||' '||{ # name . family } ")
			def string = expression.toString(sql, new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == " 'Akane'||' '||'Apple' "

	/**/DebugTrace.leave()
	}

	def "09 property reference"() {
	/**/DebugTrace.enter()

		when:
			def contact = new Contact()
			contact.name.family = "La'st"
			def sql = new Sql<>(Contact.class).setEntity(contact)
			def expression = new Expression("{ name.family } = { # name.family }")
			def string = expression.toString(sql, new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == "familyName = 'La''st'"

	/**/DebugTrace.leave()
	}


	@Ignore // Groovy converts byte[] to Byte[] when passing to variable length argument methods.
	def "10 byte[] argument - #databaseName"(Database database, String databaseName) {
	/**/DebugTrace.enter()
		setup:
			def beforeDatabase = Sql.database
			Sql.database = database

		when:
			def bytes = (Object)(byte[])[1, 2, 3]
		/**/DebugTrace.print('bytes', bytes)
			def expression = new Expression('{}', bytes)
			def paramerters = new ArrayList<Object>()
			def string = expression.toString(new Sql<>(Contact.class), paramerters)
		/**/DebugTrace.print('string', string)
		/**/DebugTrace.print("paramerters", paramerters)

		then:
			if (Sql.database instanceof Standard || Sql.database instanceof MySQL) {
				assert string == "X'010203'"
				assert paramerters.size() == 0

			} else if (Sql.database instanceof PostgreSQL) {
				assert string == "E'\\\\x010203'"
				assert paramerters.size() == 0

			} else {
				assert string == "?"
				assert paramerters.size() == 1
			}

		cleanup:
			Sql.database = beforeDatabase ?: Standard.instance()

	/**/DebugTrace.leave()
		where:
			database << databases
			databaseName = database.getClass().simpleName
	}

	def "11 property reference"() {
	/**/DebugTrace.enter()

		when:
			def expression = new Expression('{C.name.family}')
			def string = expression.toString(new Sql<>(Contact.class, 'C'), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == 'C.familyName'

	/**/DebugTrace.leave()
	}

	def "12 property reference"() {
	/**/DebugTrace.enter()

		when:
			def expression = new Expression('{C_name.family}')
			def string = expression.toString(new Sql<>(Contact.class, 'C'), new ArrayList<Object>())
		/**/DebugTrace.print('string', string)

		then:
			string == 'C_familyName'

	/**/DebugTrace.leave()
	}

	def "13 exception - illegal property name"() {
	/**/DebugTrace.enter()

		when:
			def expression = new Expression('{P.name.family}')
			expression.toString(new Sql<>(Contact.class, 'C'), new ArrayList<Object>())

		then:
			thrown IllegalArgumentException

	/**/DebugTrace.leave()
	}

	def "14 exception - illegal property name"() {
	/**/DebugTrace.enter()

		when:
			def expression = new Expression("{P_name.family}")
			expression.toString(new Sql<>(Contact.class, 'C'), new ArrayList<Object>())

		then:
			thrown IllegalArgumentException

	/**/DebugTrace.leave()
	}

	def "15 exception - illegal property name"() {
	/**/DebugTrace.enter()

		when:
			def expression = new Expression("{family}")
			expression.toString(new Sql<>(Contact.class, 'C'), new ArrayList<Object>())

		then:
			thrown IllegalArgumentException

	/**/DebugTrace.leave()
	}

	def "16 exception - illegal property name"() {
	/**/DebugTrace.enter()

		when:
			def expression = new Expression("{C.family}")
			expression.toString(new Sql<>(Contact.class, 'C'), new ArrayList<Object>())

		then:
			def e = thrown IllegalArgumentException
			e.message.indexOf("C.family\", \"family") >= 0

	/**/DebugTrace.leave()
	}

	def "17 exception - illegal property name"() {
	/**/DebugTrace.enter()

		when:
			def expression = new Expression("{C_family}")
			expression.toString(new Sql<>(Contact.class, 'C'), new ArrayList<Object>())

		then:
			def e = thrown IllegalArgumentException
			e.message.indexOf("C_family\", \"family") >= 0

	/**/DebugTrace.leave()
	}

	def "18 exception - [content] argument is null"() {
	/**/DebugTrace.enter()

		when:
			new Expression(null)

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}

	def "19 exception - [arguments] argument is null"() {
	/**/DebugTrace.enter()

		when:
			new Expression('', (Object[])null)

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}

	def "20 exception - illegal value reference"() {
	/**/DebugTrace.enter()

		when:
			def expression = new Expression("{name.family} = {#name.family}")
			expression.toString(new Sql<>(Contact.class), new ArrayList<Object>())

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}
}
