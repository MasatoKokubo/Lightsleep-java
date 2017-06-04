// PostgreSQLSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.database

import org.debugtrace.DebugTrace
import org.lightsleep.component.*
import org.lightsleep.database.PostgreSQL
import org.lightsleep.helper.*

import spock.lang.*

// PostgreSQLSpec
@Unroll
class PostgreSQLSpec extends Specification {
	@Shared map = PostgreSQL.instance().typeConverterMap()

	// -> SqlString
	def "-> SqlString"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, false     , SqlString.class).toString() == 'FALSE'
			TypeConverter.convert(map, true      , SqlString.class).toString() == 'TRUE'
			TypeConverter.convert(map, '\u0000'  , SqlString.class).toString() == "E'\\u0000'"
			TypeConverter.convert(map, '\b'      , SqlString.class).toString() == "E'\\b'"
			TypeConverter.convert(map, '\t'      , SqlString.class).toString() == "E'\\t'"
			TypeConverter.convert(map, '\n'      , SqlString.class).toString() == "E'\\n'"
			TypeConverter.convert(map, '\f'      , SqlString.class).toString() == "E'\\f'"
			TypeConverter.convert(map, '\r'      , SqlString.class).toString() == "E'\\r'"
			TypeConverter.convert(map, '\u001F'  , SqlString.class).toString() == "E'\\u001F'"
			TypeConverter.convert(map, '\u007F'  , SqlString.class).toString() == "E'\\u007F'"
			TypeConverter.convert(map, "'A'"     , SqlString.class).toString() == "'''A'''"
			TypeConverter.convert(map, '\\'      , SqlString.class).toString() == "E'\\\\'"
			TypeConverter.convert(map, '\u0001A\tB\n\u0002\r', SqlString.class).toString() == "E'\\u0001A\\tB\\n\\u0002\\r'"
			TypeConverter.convert(map, [(byte)0x7F , (byte)0x80 , (byte)0xFF] as byte[], SqlString.class).toString() == "E'\\\\x7F80FF'"
	/**/DebugTrace.leave()
	}
}
