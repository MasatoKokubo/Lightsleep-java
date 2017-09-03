// DB2Spec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.database

import org.debugtrace.DebugTrace
import org.lightsleep.component.*
import org.lightsleep.database.DB2
import org.lightsleep.helper.*

import spock.lang.*

// DB2Spec
@Unroll
class DB2Spec extends Specification {
	@Shared map = DB2.instance().typeConverterMap()

	// -> SqlString
	def "DB2Spec -> SqlString"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, false     , SqlString).toString() == 'FALSE'
			TypeConverter.convert(map, true      , SqlString).toString() == 'TRUE'
			TypeConverter.convert(map, '\u0000'  , SqlString).toString() == "''||CHR(0)"
			TypeConverter.convert(map, '\b'      , SqlString).toString() == "''||CHR(8)"
			TypeConverter.convert(map, '\t'      , SqlString).toString() == "''||CHR(9)"
			TypeConverter.convert(map, '\n'      , SqlString).toString() == "''||CHR(10)"
			TypeConverter.convert(map, '\f'      , SqlString).toString() == "''||CHR(12)"
			TypeConverter.convert(map, '\r'      , SqlString).toString() == "''||CHR(13)"
			TypeConverter.convert(map, '\u001F'  , SqlString).toString() == "''||CHR(31)"
			TypeConverter.convert(map, '\u007F'  , SqlString).toString() == "''||CHR(127)"
			TypeConverter.convert(map, "'A'"     , SqlString).toString() == "'''A'''"
			TypeConverter.convert(map, '\\'      , SqlString).toString() == "'\\'"
			TypeConverter.convert(map, 'A\tB\n\r', SqlString).toString() == "'A'||CHR(9)||'B'||CHR(10)||CHR(13)"
	/**/DebugTrace.leave()
	}
}
