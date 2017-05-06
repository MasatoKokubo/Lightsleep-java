// SQLServerSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.database

import org.debugtrace.DebugTrace
import org.lightsleep.component.*
import org.lightsleep.database.SQLServer
import org.lightsleep.helper.*

import spock.lang.*

// SQLServerSpec
@Unroll
class SQLServerSpec extends Specification {
	@Shared map = SQLServer.instance().typeConverterMap()

	// -> SqlString
	def "-> SqlString"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, false     , SqlString.class).toString() == '0'
			TypeConverter.convert(map, true      , SqlString.class).toString() == '1'
			TypeConverter.convert(map, '\u0000'  , SqlString.class).toString() == "''+CHAR(0)"
			TypeConverter.convert(map, '\b'      , SqlString.class).toString() == "''+CHAR(8)"
			TypeConverter.convert(map, '\t'      , SqlString.class).toString() == "''+CHAR(9)"
			TypeConverter.convert(map, '\n'      , SqlString.class).toString() == "''+CHAR(10)"
			TypeConverter.convert(map, '\f'      , SqlString.class).toString() == "''+CHAR(12)"
			TypeConverter.convert(map, '\r'      , SqlString.class).toString() == "''+CHAR(13)"
			TypeConverter.convert(map, '\u001F'  , SqlString.class).toString() == "''+CHAR(31)"
			TypeConverter.convert(map, '\u007F'  , SqlString.class).toString() == "''+CHAR(127)"
			TypeConverter.convert(map, "'A'"     , SqlString.class).toString() == "'''A'''"
			TypeConverter.convert(map, '\\'      , SqlString.class).toString() == "'\\'"
			TypeConverter.convert(map, 'A\tB\n\r', SqlString.class).toString() == "'A'+CHAR(9)+'B'+CHAR(10)+CHAR(13)"
	/**/DebugTrace.leave()
	}
}
