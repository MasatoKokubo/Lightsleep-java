// OracleSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.database

import org.debugtrace.DebugTrace
import org.lightsleep.component.*
import org.lightsleep.database.Oracle
import static org.lightsleep.database.Standard.*
import org.lightsleep.helper.*

import spock.lang.*

// OracleSpec
@Unroll
class OracleSpec extends Specification {
	@Shared map = Oracle.instance.typeConverterMap()

	// -> SqlString
	def "Oracle -> SqlString"() {
		expect:
			TypeConverter.convert(map, false     , SqlString).toString() == '0'
			TypeConverter.convert(map, true      , SqlString).toString() == '1'
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
	}

	// maskPassword
	def "Oracle maskPassword"(String jdbcUrl, String result) {
		expect: Oracle.instance.maskPassword(jdbcUrl) == result

		where:
			jdbcUrl              |result
			''                   |''
			'/@'                 |'/' + PASSWORD_MASK + '@'
			'/ @'                |'/' + PASSWORD_MASK + '@'
			'/a@'                |'/' + PASSWORD_MASK + '@'
			'/ !"#$%&\'()*+,-./@'|'/' + PASSWORD_MASK + '@'
			'/;<=>?[\\]^_`(|)~@' |'/' + PASSWORD_MASK + '@'
			'/a@/a@bbb'          |'/' + PASSWORD_MASK + '@/' + PASSWORD_MASK + '@bbb'
	}
}
