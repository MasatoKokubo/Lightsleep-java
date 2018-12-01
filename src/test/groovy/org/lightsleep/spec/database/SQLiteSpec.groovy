// SQLiteSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.database

import java.sql.*
import java.time.*

import org.debugtrace.DebugTrace
import org.lightsleep.component.*
import org.lightsleep.database.*
import org.lightsleep.helper.*

import spock.lang.*

// SQLiteSpec
@Unroll
class SQLiteSpec extends Specification {
	@Shared map = SQLite.instance.typeConverterMap()
	@Shared TimeZone defaultTimeZone

	def setupSpec() {
		DebugTrace.enter() // for Debugging
		defaultTimeZone = TimeZone.getDefault();
		DebugTrace.print('defaultTimeZone', defaultTimeZone) // for Debugging
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+00:00'));
		DebugTrace.print('TimeZone.getDefault()', TimeZone.getDefault()) // for Debugging
		DebugTrace.leave() // for Debugging
	}

	def cleanupSpec() {
		DebugTrace.enter() // for Debugging
		TimeZone.setDefault(defaultTimeZone);
		DebugTrace.leave() // for Debugging
	}

	// -> SqlString
	def "SQLite #title -> SqlString"(String title, Object sourceValue, String expectedString) {
		DebugTrace.enter() // for Debugging
		DebugTrace.print('title', title) // for Debugging
		DebugTrace.print('sourceValue', sourceValue) // for Debugging
		DebugTrace.print('expectedString', expectedString) // for Debugging
		when:
			def convertedValue = TypeConverter.convert(map, sourceValue, SqlString).toString()
			DebugTrace.print('convertedValue', convertedValue) // for Debugging

		then:
			expectedString == convertedValue
		DebugTrace.leave() // for Debugging

		where:
			title|sourceValue|expectedString

		//	title               |sourceValue          |expectedString
			'Boolean false'     |false                |'0'
			'Boolean true '     |true                 |'1'
			'String \'A\' '     |"'A'"                |"'''A'''"
			'byte[] {0,1,-2,-1}'|[0,1,-2,-1] as byte[]|"X'0001FEFF'" // since 3.0.1

		//	title           |sourceValue                                                                                  |expectedString
			'java.utl.Date '|new java.util.Date(0L)                                                                       |"'1970-01-01'"
			'Date          '|new Date(0L)                                                                                 |"'1970-01-01'"
			'Time          '|new Time(12*60*60*1000L+34*60*1000L+56*1000L+789L)                                           |"'12:34:56.789'"
			'Timestamp     '|{def t = new Timestamp(12*60*60*1000L+34*60*1000L+56*1000L); t.nanos = 789123456; return t}()|"'1970-01-01 12:34:56.789123456'"
			'LocalDate     '|LocalDate     .of(2019,1,1)                                                                  |"'2019-01-01'"
			'LocalTime     '|LocalTime     .of(          12,34,56, 789123456)                                             |"'12:34:56.789123456'"
			'LocalDateTime '|LocalDateTime .of(2019,1,1, 12,34,56, 789123456)                                             |"'2019-01-01 12:34:56.789123456'"
			'OffsetDateTime'|OffsetDateTime.of(2019,1,1, 12,34,56, 789123456, ZoneOffset.ofHours(0))                      |"'2019-01-01 12:34:56.789123456+00:00'"
			'ZonedDateTime '|ZonedDateTime .of(2019,1,1, 12,34,56, 789123456, ZoneId.of('GMT'))                           |"'2019-01-01 12:34:56.789123456 GMT'"
			'Instant       '|Instant.ofEpochSecond(12*60*60+34*60+56, 789123456)                                          |"'1970-01-01 12:34:56.789123456+00:00'"
	}
	// maskPassword
	def "SQLite maskPassword"(String jdbcUrl) {
		expect: SQLite.instance.maskPassword(jdbcUrl) == jdbcUrl

		where:
			jdbcUrl << [
				''                            ,
				'passwor='                    ,
				'password ='                  ,
				'password  =a'                ,
				'password= !"#$%\'()*+,-./&'  ,
				'?password=;<=>?@[\\]^_`(|)~:',
				'?password=a&password=a:bbb'  ,
			]
	}
}
