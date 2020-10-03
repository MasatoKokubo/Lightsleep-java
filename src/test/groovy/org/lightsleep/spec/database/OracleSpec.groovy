// OracleSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.database

import java.sql.Time
import java.time.LocalTime

import org.debugtrace.DebugTrace
import org.lightsleep.component.*
import org.lightsleep.database.*
import org.lightsleep.helper.*

import spock.lang.*

// OracleSpec
@Unroll
class OracleSpec extends Specification {
    @Shared map = Oracle.instance.typeConverterMap()
    @Shared TimeZone defaultTimeZone

    def setupSpec() {
        DebugTrace.enter() // for Debugging
        defaultTimeZone = TimeZone.getDefault();
        DebugTrace.print('defaultTimeZone', defaultTimeZone) // for Debugging
        TimeZone.setDefault(TimeZone.getTimeZone('GMT+00:00'));
        DebugTrace.print('TimeZone.getDefault()', TimeZone.getDefault()) // for Debugging
        DebugTrace.leave() // for Debugging
    }

    def cleanupSpec() {
        DebugTrace.enter() // for Debugging
        TimeZone.setDefault(defaultTimeZone);
        DebugTrace.leave() // for Debugging
    }

    // -> SqlString
    def "Oracle #title -> SqlString"(String title, Object sourceValue, String expectedString) {
        DebugTrace.enter() // for Debugging
        DebugTrace.print('title', title) // for Debugging
        DebugTrace.print('sourceValue', sourceValue) // for Debugging
        DebugTrace.print('expectedString', expectedString) // for Debugging
        setup:

        when:
            def convertedValue = TypeConverter.convert(map, sourceValue, SqlString).toString()
            DebugTrace.print('convertedValue', convertedValue) // for Debugging

        then:
            expectedString == convertedValue
        DebugTrace.leave() // for Debugging

        where:
            title|sourceValue|expectedString

        //    title               |sourceValue|expectedString
            'Boolean false     '|false      |'0'
            'Boolean true      '|true       |'1'
            'String \\u0000    '|'\u0000'   |"''||CHR(0)"
            'String \\b        '|'\b'       |"''||CHR(8)"
            'String \\t        '|'\t'       |"''||CHR(9)"
            'String \\n        '|'\n'       |"''||CHR(10)"
            'String \\f        '|'\f'       |"''||CHR(12)"
            'String \\r        '|'\r'       |"''||CHR(13)"
            'String \\u001F    '|'\u001F'   |"''||CHR(31)"
            'String \\u007F    '|'\u007F'   |"''||CHR(127)"
            'String \'A\'      '|"'A'"      |"'''A'''"
            'String \\         '|'\\'       |"'\\'"
            'String A\\tB\\n\\r'|'A\tB\n\r' |"'A'||CHR(9)||'B'||CHR(10)||CHR(13)"

        //    title       |sourceValue    |expectedString
            'Time    0'|new Time(   0)|"TO_TIMESTAMP('1970-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS')"
            'Time 1000'|new Time(1000)|"TO_TIMESTAMP('1970-01-01 00:00:01','YYYY-MM-DD HH24:MI:SS')"
            'Time 2000'|new Time(2000)|"TO_TIMESTAMP('1970-01-01 00:00:02','YYYY-MM-DD HH24:MI:SS')"

        //    title               |sourceValue           |expectedString
            'LocalTime 00:00:00'|LocalTime.of(0,0,0, 0)|"TO_TIMESTAMP('1970-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS')"
            'LocalTime 00:00:01'|LocalTime.of(0,0,1, 0)|"TO_TIMESTAMP('1970-01-01 00:00:01','YYYY-MM-DD HH24:MI:SS')"
            'LocalTime 00:00:02'|LocalTime.of(0,0,2, 0)|"TO_TIMESTAMP('1970-01-01 00:00:02','YYYY-MM-DD HH24:MI:SS')"
    }

    // maskPassword
    def "Oracle maskPassword"(String jdbcUrl, String result) {
        expect: Oracle.instance.maskPassword(jdbcUrl) == result

        where:
            jdbcUrl              |result
            ''                   |''
            '/@'                 |'/' + Standard.PASSWORD_MASK + '@'
            '/ @'                |'/' + Standard.PASSWORD_MASK + '@'
            '/a@'                |'/' + Standard.PASSWORD_MASK + '@'
            '/ !"#$%&\'()*+,-./@'|'/' + Standard.PASSWORD_MASK + '@'
            '/;<=>?[\\]^_`(|)~@' |'/' + Standard.PASSWORD_MASK + '@'
            '/a@/a@bbb'          |'/' + Standard.PASSWORD_MASK + '@/' + Standard.PASSWORD_MASK + '@bbb'
    }
}
