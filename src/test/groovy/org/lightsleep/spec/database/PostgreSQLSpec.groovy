// PostgreSQLSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.database

import org.debugtrace.DebugTrace
import org.lightsleep.component.*
import org.lightsleep.database.*
import org.lightsleep.helper.*

import spock.lang.*

// PostgreSQLSpec
@Unroll
class PostgreSQLSpec extends Specification {
	@Shared map = PostgreSQL.instance.typeConverterMap()

	// -> SqlString
	def "PostgreSQL #title -> SqlString"(String title, Object sourceValue, String expectedString) {
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

		//	title                        |sourceValue           |expectedString
			'Boolean false              '|false                 |'FALSE'
			'Boolean true               '|true                  |'TRUE'
			'String \\u0000             '|'\u0000'              |"E'\\u0000'"
			'String \\b                 '|'\b'                  |"E'\\b'"
			'String \\t                 '|'\t'                  |"E'\\t'"
			'String \\n                 '|'\n'                  |"E'\\n'"
			'String \\f                 '|'\f'                  |"E'\\f'"
			'String \\r                 '|'\r'                  |"E'\\r'"
			'String \\u001F             '|'\u001F'              |"E'\\u001F'"
			'String \\u007F             '|'\u007F'              |"E'\\u007F'"
			'String \'A\'               '|"'A'"                 |"'''A'''"
			'String \\                  '|'\\'                  |"E'\\\\'"
			'String \u0001A\tB\n\u0002\r'|'\u0001A\tB\n\u0002\r'|"E'\\u0001A\\tB\\n\\u0002\\r'"
			'byte[] {0,1,-2,-1}         '|[0,1,-2,-1] as byte[] |"E'\\\\x0001FEFF'"
	}

	// maskPassword
	def "PostgreSQL maskPassword"(String jdbcUrl, String result) {
		expect: PostgreSQL.instance.maskPassword(jdbcUrl) == result

		where:
			jdbcUrl                       |result
			''                            |''
			'passwor='                    |'passwor='
			'password ='                  |'password=' + Standard.PASSWORD_MASK
			'password  =a'                |'password=' + Standard.PASSWORD_MASK
			'password= !"#$%\'()*+,-./&'  |'password=' + Standard.PASSWORD_MASK + '&'
			'?password=;<=>?@[\\]^_`(|)~:'|'?password=' + Standard.PASSWORD_MASK + ':'
			'?password=a&password=a:bbb'  |'?password=' + Standard.PASSWORD_MASK + '&password=' + Standard.PASSWORD_MASK + ':bbb'
	}
}
