// SQLServerSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.database

import java.sql.*
import java.time.*

import org.debugtrace.DebugTrace
import org.lightsleep.component.*
import org.lightsleep.database.*
import org.lightsleep.helper.*

import spock.lang.*

// SQLServerSpec
@Unroll
class SQLServerSpec extends Specification {
    @Shared map = SQLServer.instance.typeConverterMap()
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
    def "SQLServer #title -> SqlString"(String title, Object sourceValue, String expectedString) {
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
            'String \\u0000    '|'\u0000'   |"''+CHAR(0)"
            'String \\b        '|'\b'       |"''+CHAR(8)"
            'String \\t        '|'\t'       |"''+CHAR(9)"
            'String \\n        '|'\n'       |"''+CHAR(10)"
            'String \\f        '|'\f'       |"''+CHAR(12)"
            'String \\r        '|'\r'       |"''+CHAR(13)"
            'String \\u001F    '|'\u001F'   |"''+CHAR(31)"
            'String \\u007F    '|'\u007F'   |"''+CHAR(127)"
            'String \'A\'      '|"'A'"      |"'''A'''"
            'String \\         '|'\\'       |"'\\'"
            'String A\\tB\\n\\r'|'A\tB\n\r' |"'A'+CHAR(9)+'B'+CHAR(10)+CHAR(13)"
            'String nchar 1    '|'¢'                |"N'¢'"
            'String nchar 2    '|'ABC¢'             |"N'ABC¢'"
            'String nchar 3    '|'ABC¢\r\n¢ABC\nABC'|"N'ABC¢'+CHAR(13)+CHAR(10)+N'¢ABC'+CHAR(10)+'ABC'"

        //    title           |sourceValue                                       |expectedString
            'java.utl.Date '|new java.util.Date(0L)                            |"CAST('1970-01-01' AS DATE)"
            'Date          '|new Date(0L)                                      |"CAST('1970-01-01' AS DATE)"
            'Time          '|new Time(12*60*60*1000L+34*60*1000L+56*1000L+789L)|"CAST('12:34:56.789' AS TIME)"


        //    title                           |sourceValue                                           |expectedString
            'Timestamp 00:00:00.000_000_000'|{def t = new Timestamp(0L); t.nanos =   0; return t}()|"CAST('1970-01-01 00:00:00' AS DATETIME2)"
            'Timestamp 00:00:00.000_000_100'|{def t = new Timestamp(0L); t.nanos = 100; return t}()|"CAST('1970-01-01 00:00:00.0000001' AS DATETIME2)"
            'Timestamp 00:00:00.000_000_200'|{def t = new Timestamp(0L); t.nanos = 200; return t}()|"CAST('1970-01-01 00:00:00.0000002' AS DATETIME2)"

        //    title                           |sourceValue                   |expectedString
            'LocalTime 00:00:00.000_000_000'|LocalTime.of(0,0,0,         0)|"CAST('00:00:00' AS TIME)"
            'LocalTime 00:00:00.001_000_000'|LocalTime.of(0,0,0, 1_000_000)|"CAST('00:00:00.001' AS TIME)"
            'LocalTime 00:00:00.002_000_000'|LocalTime.of(0,0,0, 2_000_000)|"CAST('00:00:00.002' AS TIME)"

        //    title                               |sourceValue                           |expectedString
            'LocalDateTime 00:00:00.000_000_000'|LocalDateTime.of(2019,1,1, 0,0,0,   0)|"CAST('2019-01-01 00:00:00' AS DATETIME2)"
            'LocalDateTime 00:00:00.000_000_100'|LocalDateTime.of(2019,1,1, 0,0,0, 100)|"CAST('2019-01-01 00:00:00.0000001' AS DATETIME2)"
            'LocalDateTime 00:00:00.000_000_200'|LocalDateTime.of(2019,1,1, 0,0,0, 200)|"CAST('2019-01-01 00:00:00.0000002' AS DATETIME2)"

        //    title                                |sourceValue                                                   |expectedString
            'OffsetDateTime 00:00:00.000_000_000'|OffsetDateTime.of(2019,1,1, 0,0,0,   0, ZoneOffset.ofHours(0))|"CAST('2019-01-01 00:00:00+00:00' AS DATETIMEOFFSET)"
            'OffsetDateTime 00:00:00.000_000_100'|OffsetDateTime.of(2019,1,1, 0,0,0, 100, ZoneOffset.ofHours(0))|"CAST('2019-01-01 00:00:00.0000001+00:00' AS DATETIMEOFFSET)"
            'OffsetDateTime 00:00:00.000_000_200'|OffsetDateTime.of(2019,1,1, 0,0,0, 200, ZoneOffset.ofHours(0))|"CAST('2019-01-01 00:00:00.0000002+00:00' AS DATETIMEOFFSET)"

        //    title                               |sourceValue                                             |expectedString
            'ZonedDateTime 00:00:00.000_000_000'|ZonedDateTime.of(2019,1,1, 0,0,0,   0, ZoneId.of('GMT'))|"CAST('2019-01-01 00:00:00 GMT' AS DATETIMEOFFSET)"
            'ZonedDateTime 00:00:00.000_000_100'|ZonedDateTime.of(2019,1,1, 0,0,0, 100, ZoneId.of('GMT'))|"CAST('2019-01-01 00:00:00.0000001 GMT' AS DATETIMEOFFSET)"
            'ZonedDateTime 00:00:00.000_000_200'|ZonedDateTime.of(2019,1,1, 0,0,0, 200, ZoneId.of('GMT'))|"CAST('2019-01-01 00:00:00.0000002 GMT' AS DATETIMEOFFSET)"

        //    title                         |sourceValue                  |expectedString
            'Instant 00:00:00.000_000_000'|Instant.ofEpochSecond(0,   0)|"CAST('1970-01-01 00:00:00+00:00' AS DATETIMEOFFSET)"
            'Instant 00:00:00.000_000_100'|Instant.ofEpochSecond(0, 100)|"CAST('1970-01-01 00:00:00.0000001+00:00' AS DATETIMEOFFSET)"
            'Instant 00:00:00.000_000_200'|Instant.ofEpochSecond(0, 200)|"CAST('1970-01-01 00:00:00.0000002+00:00' AS DATETIMEOFFSET)"
    }

    // maskPassword
    def "SQLServer maskPassword"(String jdbcUrl, String result) {
        expect: SQLServer.instance.maskPassword(jdbcUrl) == result

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
