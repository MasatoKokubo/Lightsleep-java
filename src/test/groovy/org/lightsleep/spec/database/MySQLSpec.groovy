// MySQLSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.database

import org.debugtrace.DebugTrace
import org.lightsleep.component.*
import org.lightsleep.database.MySQL
import org.lightsleep.helper.*

import spock.lang.*

// MySQLSpec
@Unroll
class MySQLSpec extends Specification {
	@Shared map = MySQL.instance().typeConverterMap()

	// -> SqlString
	def "MySQLSpec -> SqlString"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, false   , SqlString.class).toString() == '0'
			TypeConverter.convert(map, true    , SqlString.class).toString() == '1'
			TypeConverter.convert(map, '\u0000', SqlString.class).toString() == "'\\0'"
			TypeConverter.convert(map, '\b'    , SqlString.class).toString() == "'\\b'"
			TypeConverter.convert(map, '\t'    , SqlString.class).toString() == "'\\t'"
			TypeConverter.convert(map, '\n'    , SqlString.class).toString() == "'\\n'"
			TypeConverter.convert(map, '\r'    , SqlString.class).toString() == "'\\r'"
			TypeConverter.convert(map, "'A'"   , SqlString.class).toString() == "'''A'''"
			TypeConverter.convert(map, '\\'    , SqlString.class).toString() == "'\\\\'"
	/**/DebugTrace.leave()
	}
}
