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
	@Shared map = SQLServer.instance.typeConverterMap()

	// -> SqlString
	def "SQLServerSpec -> SqlString"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, false     , SqlString).toString() == '0'
			TypeConverter.convert(map, true      , SqlString).toString() == '1'
			TypeConverter.convert(map, '\u0000'  , SqlString).toString() == "''+CHAR(0)"
			TypeConverter.convert(map, '\b'      , SqlString).toString() == "''+CHAR(8)"
			TypeConverter.convert(map, '\t'      , SqlString).toString() == "''+CHAR(9)"
			TypeConverter.convert(map, '\n'      , SqlString).toString() == "''+CHAR(10)"
			TypeConverter.convert(map, '\f'      , SqlString).toString() == "''+CHAR(12)"
			TypeConverter.convert(map, '\r'      , SqlString).toString() == "''+CHAR(13)"
			TypeConverter.convert(map, '\u001F'  , SqlString).toString() == "''+CHAR(31)"
			TypeConverter.convert(map, '\u007F'  , SqlString).toString() == "''+CHAR(127)"
			TypeConverter.convert(map, "'A'"     , SqlString).toString() == "'''A'''"
			TypeConverter.convert(map, '\\'      , SqlString).toString() == "'\\'"
			TypeConverter.convert(map, 'A\tB\n\r', SqlString).toString() == "'A'+CHAR(9)+'B'+CHAR(10)+CHAR(13)"
	/**/DebugTrace.leave()
	}
}
