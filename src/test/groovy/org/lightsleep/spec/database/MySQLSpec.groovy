// MySQLSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.database

import org.debugtrace.DebugTrace
import org.lightsleep.component.*
import org.lightsleep.database.MySQL
import static org.lightsleep.database.Standard.*
import org.lightsleep.helper.*

import spock.lang.*

// MySQLSpec
@Unroll
class MySQLSpec extends Specification {
	@Shared map = MySQL.instance.typeConverterMap()

	// -> SqlString
	def "MySQL -> SqlString"() {
		expect:
			TypeConverter.convert(map, false   , SqlString).toString() == '0'
			TypeConverter.convert(map, true    , SqlString).toString() == '1'
			TypeConverter.convert(map, '\u0000', SqlString).toString() == "'\\0'"
			TypeConverter.convert(map, '\b'    , SqlString).toString() == "'\\b'"
			TypeConverter.convert(map, '\t'    , SqlString).toString() == "'\\t'"
			TypeConverter.convert(map, '\n'    , SqlString).toString() == "'\\n'"
			TypeConverter.convert(map, '\r'    , SqlString).toString() == "'\\r'"
			TypeConverter.convert(map, "'A'"   , SqlString).toString() == "'''A'''"
			TypeConverter.convert(map, '\\'    , SqlString).toString() == "'\\\\'"
	}

	// maskPassword
	def "MySQL maskPassword"(String jdbcUrl, String result) {
		expect: MySQL.instance.maskPassword(jdbcUrl) == result

		where:
			jdbcUrl                       |result
			''                            |''
			'passwor='                    |'passwor='
			'password ='                  |'password=' + PASSWORD_MASK
			'password  =a'                |'password=' + PASSWORD_MASK
			'password= !"#$%\'()*+,-./&'  |'password=' + PASSWORD_MASK + '&'
			'?password=;<=>?@[\\]^_`(|)~:'|'?password=' + PASSWORD_MASK + ':'
			'?password=a&password=a:bbb'  |'?password=' + PASSWORD_MASK + '&password=' + PASSWORD_MASK + ':bbb'
	}
}
