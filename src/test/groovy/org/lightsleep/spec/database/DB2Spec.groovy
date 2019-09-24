// Db2Spec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.database

import org.debugtrace.DebugTrace
import org.lightsleep.component.*
import org.lightsleep.database.*
import org.lightsleep.helper.*

import spock.lang.*

// Db2Spec
@Unroll
class Db2Spec extends Specification {
	@Shared map = Db2.instance.typeConverterMap()

	// -> SqlString
	def "Db2 #title -> SqlString"(String title, Object sourceValue, String expectedString) {
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
			'Boolean false     '|false                |'FALSE'
			'Boolean true      '|true                 |'TRUE'
			'String \\u0000    '|'\u0000'             |"''||CHR(0)"
			'String \\b        '|'\b'                 |"''||CHR(8)"
			'String \\t        '|'\t'                 |"''||CHR(9)"
			'String \\n        '|'\n'                 |"''||CHR(10)"
			'String \\f        '|'\f'                 |"''||CHR(12)"
			'String \\r        '|'\r'                 |"''||CHR(13)"
			'String \\u001F    '|'\u001F'             |"''||CHR(31)"
			'String \\u007F    '|'\u007F'             |"''||CHR(127)"
			'String \'A\'      '|"'A'"                |"'''A'''"
			'String \\         '|'\\'                 |"'\\'"
			'String A\\tB\\n\\r'|'A\tB\n\r'           |"'A'||CHR(9)||'B'||CHR(10)||CHR(13)"
			'byte[] {0,1,-2,-1}'|[0,1,-2,-1] as byte[]|"BX'0001FEFF'"
	}

	// maskPassword
	def "Db2 maskPassword"(String jdbcUrl, String result) {
		expect: Db2.instance.maskPassword(jdbcUrl) == result

		where:
			jdbcUrl                      |result
			''                           |''
			'passwor='                   |'passwor='
			'password ='                 |'password=' + Standard.PASSWORD_MASK
			'password  =a'               |'password=' + Standard.PASSWORD_MASK
			'password= !"#$%&\'()*+,-./;'|'password=' + Standard.PASSWORD_MASK + ';'
			':password=<=>?@[\\]^_`(|)~:'|':password=' + Standard.PASSWORD_MASK + ':'
			':password=a;password=a:bbb' |':password=' + Standard.PASSWORD_MASK + ';password=' + Standard.PASSWORD_MASK + ':bbb'
	}
}
