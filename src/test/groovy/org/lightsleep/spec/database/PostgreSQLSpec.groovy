// PostgreSQLSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.database

import org.debugtrace.DebugTrace
import org.lightsleep.component.*
import org.lightsleep.database.PostgreSQL
import static org.lightsleep.database.Standard.*
import org.lightsleep.helper.*

import spock.lang.*

// PostgreSQLSpec
@Unroll
class PostgreSQLSpec extends Specification {
	@Shared map = PostgreSQL.instance.typeConverterMap()

	// -> SqlString
	def "PostgreSQL -> SqlString"() {
		expect:
			TypeConverter.convert(map, false     , SqlString).toString() == 'FALSE'
			TypeConverter.convert(map, true      , SqlString).toString() == 'TRUE'
			TypeConverter.convert(map, '\u0000'  , SqlString).toString() == "E'\\u0000'"
			TypeConverter.convert(map, '\b'      , SqlString).toString() == "E'\\b'"
			TypeConverter.convert(map, '\t'      , SqlString).toString() == "E'\\t'"
			TypeConverter.convert(map, '\n'      , SqlString).toString() == "E'\\n'"
			TypeConverter.convert(map, '\f'      , SqlString).toString() == "E'\\f'"
			TypeConverter.convert(map, '\r'      , SqlString).toString() == "E'\\r'"
			TypeConverter.convert(map, '\u001F'  , SqlString).toString() == "E'\\u001F'"
			TypeConverter.convert(map, '\u007F'  , SqlString).toString() == "E'\\u007F'"
			TypeConverter.convert(map, "'A'"     , SqlString).toString() == "'''A'''"
			TypeConverter.convert(map, '\\'      , SqlString).toString() == "E'\\\\'"
			TypeConverter.convert(map, '\u0001A\tB\n\u0002\r', SqlString).toString() == "E'\\u0001A\\tB\\n\\u0002\\r'"
			TypeConverter.convert(map, [(byte)0x7F , (byte)0x80 , (byte)0xFF] as byte[], SqlString).toString() == "E'\\\\x7F80FF'"
	}

	// maskPassword
	def "PostgreSQL maskPassword"(String jdbcUrl, String result) {
		expect: PostgreSQL.instance.maskPassword(jdbcUrl) == result

		where:
			jdbcUrl                     |result
			''                            |''
			'passwor='                    |'passwor='
			'password ='                  |'password=' + PASSWORD_MASK
			'password  =a'                |'password=' + PASSWORD_MASK
			'password= !"#$%\'()*+,-./&'  |'password=' + PASSWORD_MASK + '&'
			'?password=;<=>?@[\\]^_`(|)~:'|'?password=' + PASSWORD_MASK + ':'
			'?password=a&password=a:bbb'  |'?password=' + PASSWORD_MASK + '&password=' + PASSWORD_MASK + ':bbb'
	}
}
