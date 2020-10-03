// TypeConverterTest.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.helper

import java.sql.*
import java.time.*
import java.util.concurrent.ConcurrentHashMap
import org.debugtrace.DebugTrace // for Debugging
import org.lightsleep.helper.*

import spock.lang.*

// TypeConverterDateTimeSpec
// @since 3.0.0
@Unroll
class TypeConverterDateTimeSpec extends Specification {
    @Shared map = new ConcurrentHashMap<>(TypeConverter.typeConverterMap())

    @Shared TimeZone defaultTimeZone

    def setupSpec() {
        defaultTimeZone = TimeZone.getDefault();
    }

    def cleanupSpec() {
        TimeZone.setDefault(defaultTimeZone);
    }

    // -> Long
    def "TypeConverter #caseName"(String caseName, String timeZoneId, Closure beforeClosure, Closure expectedClosure) {
        DebugTrace.enter() // for Debugging
        DebugTrace.print('caseName', caseName) // for Debugging
        DebugTrace.print('timeZoneId', timeZoneId) // for Debugging
        setup:
            TimeZone.setDefault(TimeZone.getTimeZone(timeZoneId));

            def before = beforeClosure();
            DebugTrace.print('before', before) // for Debugging
            DebugTrace.print('before.class.name', before.getClass().name) // for Debugging
            def expected = expectedClosure();
            DebugTrace.print('expected', expected) // for Debugging

        when:
            def after = TypeConverter.convert(map, before, expected.getClass())
            DebugTrace.print('after', after) // for Debugging

        then:
            expected == after

        DebugTrace.leave() // for Debugging
        where:
            caseName|timeZoneId|beforeClosure|expectedClosure

        //  caseName                    |timeZoneId |before              dd               HH              mm            ss           SSS  offset         |expected dd       HH              mm            ss           SSS   offset
            'java.util.Date -> Long 1-1'|'GMT-09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L}
            'java.util.Date -> Long 1-2'|'GMT-09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L}
            'java.util.Date -> Long 1-3'|'GMT-09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L}
            'java.util.Date -> Long 2-1'|'GMT+00:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L}
            'java.util.Date -> Long 2-2'|'GMT+00:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L}
            'java.util.Date -> Long 2-3'|'GMT+00:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L}
            'java.util.Date -> Long 3-1'|'GMT+09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L}
            'java.util.Date -> Long 3-2'|'GMT+09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L}
            'java.util.Date -> Long 3-3'|'GMT+09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L}

        //  caseName                   |timeZoneId |before    dd               HH              mm            ss           SSS  offset         |expected dd       HH              mm            ss           SSS   offset
            'java.sql.Date -> Long 1-1'|'GMT-09:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L}
            'java.sql.Date -> Long 1-2'|'GMT-09:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L}
            'java.sql.Date -> Long 1-3'|'GMT-09:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L}
            'java.sql.Date -> Long 2-1'|'GMT+00:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L}
            'java.sql.Date -> Long 2-2'|'GMT+00:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L}
            'java.sql.Date -> Long 2-3'|'GMT+00:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L}
            'java.sql.Date -> Long 3-1'|'GMT+09:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L}
            'java.sql.Date -> Long 3-2'|'GMT+09:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L}
            'java.sql.Date -> Long 3-3'|'GMT+09:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L}

        //  caseName          |timeZoneId |before    HH              mm            ss           SSS  offset         |expected HH      mm            ss           SSS   offset
            'Time -> Long 1-1'|'GMT-09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{ 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L}
            'Time -> Long 1-2'|'GMT-09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L}
            'Time -> Long 1-3'|'GMT-09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L}
            'Time -> Long 2-1'|'GMT+00:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{ 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L}
            'Time -> Long 2-2'|'GMT+00:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L}
            'Time -> Long 2-3'|'GMT+00:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L}
            'Time -> Long 3-1'|'GMT+09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{ 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L}
            'Time -> Long 3-2'|'GMT+09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L}
            'Time -> Long 3-3'|'GMT+09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L}

        //  caseName               |timeZoneId |before          dd               HH              mm            ss          SSS    offset       |expected dd       HH              mm            ss           SSS   offset
            'Timestamp -> Long 1-1'|'GMT-09:00'|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L}
            'Timestamp -> Long 1-2'|'GMT-09:00'|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L}
            'Timestamp -> Long 1-3'|'GMT-09:00'|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L}
            'Timestamp -> Long 2-1'|'GMT+00:00'|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L}
            'Timestamp -> Long 2-2'|'GMT+00:00'|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L}
            'Timestamp -> Long 2-3'|'GMT+00:00'|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L}
            'Timestamp -> Long 3-1'|'GMT+09:00'|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L}
            'Timestamp -> Long 3-2'|'GMT+09:00'|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L}
            'Timestamp -> Long 3-3'|'GMT+09:00'|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L}

        //  caseName               |timeZoneId |before        yyyy  MM  dd  |expected dd      offset
            'LocalDate -> Long 1-1'|'GMT-09:00'|{LocalDate.of(1969, 12, 31)}|{-1L*86400_000L +9L*3600_000L}
            'LocalDate -> Long 1-2'|'GMT-09:00'|{LocalDate.of(1970,  1,  1)}|{ 0L*86400_000L +9L*3600_000L}
            'LocalDate -> Long 1-3'|'GMT-09:00'|{LocalDate.of(1970,  1,  2)}|{ 1L*86400_000L +9L*3600_000L}
            'LocalDate -> Long 2-1'|'GMT+00:00'|{LocalDate.of(1969, 12, 31)}|{-1L*86400_000L +0L*3600_000L}
            'LocalDate -> Long 2-2'|'GMT+00:00'|{LocalDate.of(1970,  1,  1)}|{ 0L*86400_000L +0L*3600_000L}
            'LocalDate -> Long 2-3'|'GMT+00:00'|{LocalDate.of(1970,  1,  2)}|{ 1L*86400_000L +0L*3600_000L}
            'LocalDate -> Long 3-1'|'GMT+09:00'|{LocalDate.of(1969, 12, 31)}|{-1L*86400_000L -9L*3600_000L}
            'LocalDate -> Long 3-2'|'GMT+09:00'|{LocalDate.of(1970,  1,  1)}|{ 0L*86400_000L -9L*3600_000L}
            'LocalDate -> Long 3-3'|'GMT+09:00'|{LocalDate.of(1970,  1,  2)}|{ 1L*86400_000L -9L*3600_000L}

        //  caseName               |timeZoneId |before        HH  mm  ss  nanoOfSecond |expected HH      mm            ss           SSS   offset
            'LocalTime -> Long 1-1'|'GMT-09:00'|{LocalTime.of( 0,  0,  0,           0)}|{ 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L}
            'LocalTime -> Long 1-2'|'GMT-09:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L}
            'LocalTime -> Long 1-3'|'GMT-09:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L}
            'LocalTime -> Long 2-1'|'GMT+00:00'|{LocalTime.of( 0,  0,  0,           0)}|{ 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L}
            'LocalTime -> Long 2-2'|'GMT+00:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L}
            'LocalTime -> Long 2-3'|'GMT+00:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L}
            'LocalTime -> Long 3-1'|'GMT+09:00'|{LocalTime.of( 0,  0,  0,           0)}|{ 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L}
            'LocalTime -> Long 3-2'|'GMT+09:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L}
            'LocalTime -> Long 3-3'|'GMT+09:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L}

        //  caseName                   |timeZoneId |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond |expected dd       HH              mm            ss           SSS   offset
            'LocalDateTime -> Long 1-1'|'GMT-09:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L}
            'LocalDateTime -> Long 1-2'|'GMT-09:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L}
            'LocalDateTime -> Long 1-3'|'GMT-09:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L}
            'LocalDateTime -> Long 2-1'|'GMT+00:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L}
            'LocalDateTime -> Long 2-2'|'GMT+00:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L}
            'LocalDateTime -> Long 2-3'|'GMT+00:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L}
            'LocalDateTime -> Long 3-1'|'GMT+09:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L}
            'LocalDateTime -> Long 3-2'|'GMT+09:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L}
            'LocalDateTime -> Long 3-3'|'GMT+09:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L}

        //  caseName                    |timeZoneId |before             yyyy  MM  dd  HH  mm  ss nanoOfSecond  ZoneOffset              |expected dd       HH              mm            ss           SSS   offset
            'OffsetDateTime -> Long 1-1'|'GMT-09:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L}
            'OffsetDateTime -> Long 1-2'|'GMT-09:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours(-9))}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L}
            'OffsetDateTime -> Long 1-3'|'GMT-09:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours(-9))}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L}
            'OffsetDateTime -> Long 2-1'|'GMT+00:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L}
            'OffsetDateTime -> Long 2-2'|'GMT+00:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 0))}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L}
            'OffsetDateTime -> Long 2-3'|'GMT+00:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 0))}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L}
            'OffsetDateTime -> Long 3-1'|'GMT+09:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L}
            'OffsetDateTime -> Long 3-2'|'GMT+09:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 9))}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L}
            'OffsetDateTime -> Long 3-3'|'GMT+09:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 9))}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L}

        //  caseName                   |timeZoneId |before            yyyy  MM  dd  HH  mm  ss nanoOfSecond  ZoneId                  |expected dd       HH              mm            ss           SSS   offset
            'ZonedDateTime -> Long 1-1'|'GMT-09:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT-09:00'))}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L}
            'ZonedDateTime -> Long 1-2'|'GMT-09:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT-09:00'))}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L}
            'ZonedDateTime -> Long 1-3'|'GMT-09:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT-09:00'))}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L}
            'ZonedDateTime -> Long 2-1'|'GMT+00:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+00:00'))}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L}
            'ZonedDateTime -> Long 2-2'|'GMT+00:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+00:00'))}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L}
            'ZonedDateTime -> Long 2-3'|'GMT+00:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+00:00'))}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L}
            'ZonedDateTime -> Long 3-1'|'GMT+09:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+09:00'))}|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L}
            'ZonedDateTime -> Long 3-2'|'GMT+09:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+09:00'))}|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L}
            'ZonedDateTime -> Long 3-3'|'GMT+09:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+09:00'))}|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L}

        //  caseName             |timeZoneId |before                                      |expected
            'Instant -> Long 1-1'|'GMT-09:00'|{Instant.EPOCH                             }|{                  0L}
            'Instant -> Long 1-2'|'GMT-09:00'|{Instant.ofEpochMilli(                  0L)}|{                  0L}
            'Instant -> Long 1-3'|'GMT-09:00'|{Instant.ofEpochMilli(               -999L)}|{               -999L}
            'Instant -> Long 1-4'|'GMT-09:00'|{Instant.ofEpochMilli(                999L)}|{                999L}
            'Instant -> Long 1-5'|'GMT-09:00'|{Instant.ofEpochMilli(-99_999_999_999_999L)}|{-99_999_999_999_999L}
            'Instant -> Long 1-6'|'GMT-09:00'|{Instant.ofEpochMilli( 99_999_999_999_999L)}|{ 99_999_999_999_999L}
            'Instant -> Long 2-1'|'GMT+00:00'|{Instant.EPOCH                             }|{                  0L}
            'Instant -> Long 2-2'|'GMT+00:00'|{Instant.ofEpochMilli(                  0L)}|{                  0L}
            'Instant -> Long 2-3'|'GMT+00:00'|{Instant.ofEpochMilli(               -999L)}|{               -999L}
            'Instant -> Long 2-4'|'GMT+00:00'|{Instant.ofEpochMilli(                999L)}|{                999L}
            'Instant -> Long 2-5'|'GMT+00:00'|{Instant.ofEpochMilli(-99_999_999_999_999L)}|{-99_999_999_999_999L}
            'Instant -> Long 2-6'|'GMT+00:00'|{Instant.ofEpochMilli( 99_999_999_999_999L)}|{ 99_999_999_999_999L}
            'Instant -> Long 3-1'|'GMT+09:00'|{Instant.EPOCH                             }|{                  0L}
            'Instant -> Long 3-2'|'GMT+09:00'|{Instant.ofEpochMilli(                  0L)}|{                  0L}
            'Instant -> Long 3-3'|'GMT+09:00'|{Instant.ofEpochMilli(               -999L)}|{               -999L}
            'Instant -> Long 3-4'|'GMT+09:00'|{Instant.ofEpochMilli(                999L)}|{                999L}
            'Instant -> Long 3-5'|'GMT+09:00'|{Instant.ofEpochMilli(-99_999_999_999_999L)}|{-99_999_999_999_999L}
            'Instant -> Long 3-6'|'GMT+09:00'|{Instant.ofEpochMilli( 99_999_999_999_999L)}|{ 99_999_999_999_999L}

        //  caseName                    |timeZoneId |before dd         HH              mm            ss           SSS   offset       |expected            dd               HH              mm            ss           SSS  offset
            'Long -> java.util.Date 1-1'|'GMT-09:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L}|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'Long -> java.util.Date 1-2'|'GMT-09:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L}|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'Long -> java.util.Date 1-3'|'GMT-09:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L}|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'Long -> java.util.Date 2-1'|'GMT+00:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L}|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'Long -> java.util.Date 2-2'|'GMT+00:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L}|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'Long -> java.util.Date 2-3'|'GMT+00:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L}|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'Long -> java.util.Date 3-1'|'GMT+09:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L}|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'Long -> java.util.Date 3-2'|'GMT+09:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L}|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'Long -> java.util.Date 3-3'|'GMT+09:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L}|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName                       |timeZoneId |before dd       HH            mm          ss         SSS  offset     |expected            dd               HH              mm            ss           SSS  offset
            'Integer -> java.util.Date 1-1'|'GMT-09:00'|{-1*86400_000 +  0*3600_000 +  0*60_000 +  0*1_000 +   0 +9*3600_000}|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'Integer -> java.util.Date 1-2'|'GMT-09:00'|{ 0*86400_000 + 12*3600_000 + 34*60_000 + 56*1_000 + 789 +9*3600_000}|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'Integer -> java.util.Date 1-3'|'GMT-09:00'|{ 1*86400_000 + 23*3600_000 + 59*60_000 + 59*1_000 + 999 +9*3600_000}|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'Integer -> java.util.Date 2-1'|'GMT+00:00'|{-1*86400_000 +  0*3600_000 +  0*60_000 +  0*1_000 +   0 +0*3600_000}|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'Integer -> java.util.Date 2-2'|'GMT+00:00'|{ 0*86400_000 + 12*3600_000 + 34*60_000 + 56*1_000 + 789 +0*3600_000}|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'Integer -> java.util.Date 2-3'|'GMT+00:00'|{ 1*86400_000 + 23*3600_000 + 59*60_000 + 59*1_000 + 999 +0*3600_000}|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'Integer -> java.util.Date 3-1'|'GMT+09:00'|{-1*86400_000 +  0*3600_000 +  0*60_000 +  0*1_000 +   0 -9*3600_000}|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'Integer -> java.util.Date 3-2'|'GMT+09:00'|{ 0*86400_000 + 12*3600_000 + 34*60_000 + 56*1_000 + 789 -9*3600_000}|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'Integer -> java.util.Date 3-3'|'GMT+09:00'|{ 1*86400_000 + 23*3600_000 + 59*60_000 + 59*1_000 + 999 -9*3600_000}|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName                          |timeZoneId |before          dd               HH              mm            ss           SSS   offset        |expected            dd               HH              mm            ss           SSS  offset
            'BigDecimal -> java.util.Date 1-1'|'GMT-09:00'|{new BigDecimal(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'BigDecimal -> java.util.Date 1-2'|'GMT-09:00'|{new BigDecimal( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'BigDecimal -> java.util.Date 1-3'|'GMT-09:00'|{new BigDecimal( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'BigDecimal -> java.util.Date 2-1'|'GMT+00:00'|{new BigDecimal(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'BigDecimal -> java.util.Date 2-2'|'GMT+00:00'|{new BigDecimal( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'BigDecimal -> java.util.Date 2-3'|'GMT+00:00'|{new BigDecimal( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'BigDecimal -> java.util.Date 3-1'|'GMT+09:00'|{new BigDecimal(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'BigDecimal -> java.util.Date 3-2'|'GMT+09:00'|{new BigDecimal( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'BigDecimal -> java.util.Date 3-3'|'GMT+09:00'|{new BigDecimal( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName                         |timeZoneId |before        yyyy  MM  dd  |expected            dd              offset
            'LocalDate -> java.util.Date 1-1'|'GMT-09:00'|{LocalDate.of(1969, 12, 31)}|{new java.util.Date(-1L*86400_000L +9L*3600_000L)}
            'LocalDate -> java.util.Date 1-2'|'GMT-09:00'|{LocalDate.of(1970,  1,  1)}|{new java.util.Date( 0L*86400_000L +9L*3600_000L)}
            'LocalDate -> java.util.Date 1-3'|'GMT-09:00'|{LocalDate.of(1970,  1,  2)}|{new java.util.Date( 1L*86400_000L +9L*3600_000L)}
            'LocalDate -> java.util.Date 2-1'|'GMT+00:00'|{LocalDate.of(1969, 12, 31)}|{new java.util.Date(-1L*86400_000L +0L*3600_000L)}
            'LocalDate -> java.util.Date 2-2'|'GMT+00:00'|{LocalDate.of(1970,  1,  1)}|{new java.util.Date( 0L*86400_000L +0L*3600_000L)}
            'LocalDate -> java.util.Date 2-3'|'GMT+00:00'|{LocalDate.of(1970,  1,  2)}|{new java.util.Date( 1L*86400_000L +0L*3600_000L)}
            'LocalDate -> java.util.Date 3-1'|'GMT+09:00'|{LocalDate.of(1969, 12, 31)}|{new java.util.Date(-1L*86400_000L -9L*3600_000L)}
            'LocalDate -> java.util.Date 3-2'|'GMT+09:00'|{LocalDate.of(1970,  1,  1)}|{new java.util.Date( 0L*86400_000L -9L*3600_000L)}
            'LocalDate -> java.util.Date 3-3'|'GMT+09:00'|{LocalDate.of(1970,  1,  2)}|{new java.util.Date( 1L*86400_000L -9L*3600_000L)}

        //  caseName                   |timeZoneId |before dd         HH              mm            ss           SSS   offset       |expected   dd               HH              mm            ss           SSS  offset
            'Long -> java.sql.Date 1-1'|'GMT-09:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L}|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'Long -> java.sql.Date 1-2'|'GMT-09:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L}|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'Long -> java.sql.Date 1-3'|'GMT-09:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L}|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'Long -> java.sql.Date 2-1'|'GMT+00:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L}|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'Long -> java.sql.Date 2-2'|'GMT+00:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L}|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'Long -> java.sql.Date 2-3'|'GMT+00:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L}|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'Long -> java.sql.Date 3-1'|'GMT+09:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L}|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'Long -> java.sql.Date 3-2'|'GMT+09:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L}|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'Long -> java.sql.Date 3-3'|'GMT+09:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L}|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName                      |timeZoneId |before dd       HH            mm          ss         SSS  offset     |expected   dd               HH              mm            ss           SSS  offset
            'Integer -> java.sql.Date 1-1'|'GMT-09:00'|{-1*86400_000 +  0*3600_000 +  0*60_000 +  0*1_000 +   0 +9*3600_000}|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'Integer -> java.sql.Date 1-2'|'GMT-09:00'|{ 0*86400_000 + 12*3600_000 + 34*60_000 + 56*1_000 + 789 +9*3600_000}|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'Integer -> java.sql.Date 1-3'|'GMT-09:00'|{ 1*86400_000 + 23*3600_000 + 59*60_000 + 59*1_000 + 999 +9*3600_000}|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'Integer -> java.sql.Date 2-1'|'GMT+00:00'|{-1*86400_000 +  0*3600_000 +  0*60_000 +  0*1_000 +   0 +0*3600_000}|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'Integer -> java.sql.Date 2-2'|'GMT+00:00'|{ 0*86400_000 + 12*3600_000 + 34*60_000 + 56*1_000 + 789 +0*3600_000}|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'Integer -> java.sql.Date 2-3'|'GMT+00:00'|{ 1*86400_000 + 23*3600_000 + 59*60_000 + 59*1_000 + 999 +0*3600_000}|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'Integer -> java.sql.Date 3-1'|'GMT+09:00'|{-1*86400_000 +  0*3600_000 +  0*60_000 +  0*1_000 +   0 -9*3600_000}|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'Integer -> java.sql.Date 3-2'|'GMT+09:00'|{ 0*86400_000 + 12*3600_000 + 34*60_000 + 56*1_000 + 789 -9*3600_000}|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'Integer -> java.sql.Date 3-3'|'GMT+09:00'|{ 1*86400_000 + 23*3600_000 + 59*60_000 + 59*1_000 + 999 -9*3600_000}|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName                         |timeZoneId |before          dd               HH              mm            ss           SSS   offset        |expected   dd               HH              mm            ss           SSS  offset
            'BigDecimal -> java.sql.Date 1-1'|'GMT-09:00'|{new BigDecimal(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'BigDecimal -> java.sql.Date 1-2'|'GMT-09:00'|{new BigDecimal( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'BigDecimal -> java.sql.Date 1-3'|'GMT-09:00'|{new BigDecimal( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'BigDecimal -> java.sql.Date 2-1'|'GMT+00:00'|{new BigDecimal(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'BigDecimal -> java.sql.Date 2-2'|'GMT+00:00'|{new BigDecimal( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'BigDecimal -> java.sql.Date 2-3'|'GMT+00:00'|{new BigDecimal( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'BigDecimal -> java.sql.Date 3-1'|'GMT+09:00'|{new BigDecimal(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'BigDecimal -> java.sql.Date 3-2'|'GMT+09:00'|{new BigDecimal( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'BigDecimal -> java.sql.Date 3-3'|'GMT+09:00'|{new BigDecimal( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName                             |timeZoneId |before              dd               HH              mm            ss           SSS  offset         |expected   dd               HH              mm            ss           SSS  offset
            'java.util.Date -> java.sql.Date 1-1'|'GMT-09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'java.util.Date -> java.sql.Date 1-2'|'GMT-09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'java.util.Date -> java.sql.Date 1-3'|'GMT-09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'java.util.Date -> java.sql.Date 2-1'|'GMT+00:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'java.util.Date -> java.sql.Date 2-2'|'GMT+00:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'java.util.Date -> java.sql.Date 2-3'|'GMT+00:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'java.util.Date -> java.sql.Date 3-1'|'GMT+09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'java.util.Date -> java.sql.Date 3-2'|'GMT+09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'java.util.Date -> java.sql.Date 3-3'|'GMT+09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName                   |timeZoneId |before    HH              mm            ss           SSS  offset         |expected    HH              mm            ss           SSS  offset
            'Time -> java.sql.Date 1-1'|'GMT-09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{new Date(- 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'Time -> java.sql.Date 1-2'|'GMT-09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{new Date( 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'Time -> java.sql.Date 1-3'|'GMT-09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{new Date( 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'Time -> java.sql.Date 2-1'|'GMT+00:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{new Date(- 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'Time -> java.sql.Date 2-2'|'GMT+00:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{new Date( 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'Time -> java.sql.Date 2-3'|'GMT+00:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{new Date( 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'Time -> java.sql.Date 3-1'|'GMT+09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{new Date(- 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'Time -> java.sql.Date 3-2'|'GMT+09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{new Date( 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'Time -> java.sql.Date 3-3'|'GMT+09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{new Date( 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName                        |timeZoneId |before                 dd               HH              mm            ss          offset                  nano seconds           |expected   dd               HH              mm            ss           SSS  offset
            'Timestamp -> java.sql.Date 1-1'|'GMT-09:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +9L*3600_000L); t.nanos =           0; return t}|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'Timestamp -> java.sql.Date 1-2'|'GMT-09:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +9L*3600_000L); t.nanos = 789_012_345; return t}|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'Timestamp -> java.sql.Date 1-3'|'GMT-09:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +9L*3600_000L); t.nanos = 999_999_999; return t}|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'Timestamp -> java.sql.Date 2-1'|'GMT+00:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +0L*3600_000L); t.nanos =           0; return t}|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'Timestamp -> java.sql.Date 2-2'|'GMT+00:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +0L*3600_000L); t.nanos = 789_012_345; return t}|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'Timestamp -> java.sql.Date 2-3'|'GMT+00:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +0L*3600_000L); t.nanos = 999_999_999; return t}|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'Timestamp -> java.sql.Date 3-1'|'GMT+09:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L -9L*3600_000L); t.nanos =           0; return t}|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'Timestamp -> java.sql.Date 3-2'|'GMT+09:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L -9L*3600_000L); t.nanos = 789_012_345; return t}|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'Timestamp -> java.sql.Date 3-3'|'GMT+09:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L -9L*3600_000L); t.nanos = 999_999_999; return t}|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName                        |timeZoneId |before        yyyy  MM  dd  |expected  dd              offset
            'LocalDate -> java.sql.Date 1-1'|'GMT-09:00'|{LocalDate.of(1969, 12, 31)}|{new Date(-1L*86400_000L +9L*3600_000L)}
            'LocalDate -> java.sql.Date 1-2'|'GMT-09:00'|{LocalDate.of(1970,  1,  1)}|{new Date( 0L*86400_000L +9L*3600_000L)}
            'LocalDate -> java.sql.Date 1-3'|'GMT-09:00'|{LocalDate.of(1970,  1,  2)}|{new Date( 1L*86400_000L +9L*3600_000L)}
            'LocalDate -> java.sql.Date 2-1'|'GMT+00:00'|{LocalDate.of(1969, 12, 31)}|{new Date(-1L*86400_000L +0L*3600_000L)}
            'LocalDate -> java.sql.Date 2-2'|'GMT+00:00'|{LocalDate.of(1970,  1,  1)}|{new Date( 0L*86400_000L +0L*3600_000L)}
            'LocalDate -> java.sql.Date 2-3'|'GMT+00:00'|{LocalDate.of(1970,  1,  2)}|{new Date( 1L*86400_000L +0L*3600_000L)}
            'LocalDate -> java.sql.Date 3-1'|'GMT+09:00'|{LocalDate.of(1969, 12, 31)}|{new Date(-1L*86400_000L -9L*3600_000L)}
            'LocalDate -> java.sql.Date 3-2'|'GMT+09:00'|{LocalDate.of(1970,  1,  1)}|{new Date( 0L*86400_000L -9L*3600_000L)}
            'LocalDate -> java.sql.Date 3-3'|'GMT+09:00'|{LocalDate.of(1970,  1,  2)}|{new Date( 1L*86400_000L -9L*3600_000L)}

        //  caseName          |timeZoneId |before HH         mm            ss           SSS   offset       |expected  HH              mm            ss           SSS   offset
            'Long -> Time 1-1'|'GMT-09:00'|{ 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'Long -> Time 1-2'|'GMT-09:00'|{12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'Long -> Time 1-3'|'GMT-09:00'|{23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'Long -> Time 2-1'|'GMT+00:00'|{ 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'Long -> Time 2-2'|'GMT+00:00'|{12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'Long -> Time 2-3'|'GMT+00:00'|{23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'Long -> Time 3-1'|'GMT+09:00'|{ 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'Long -> Time 3-2'|'GMT+09:00'|{12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'Long -> Time 3-3'|'GMT+09:00'|{23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName             |timeZoneId |before HH      mm          ss         SSS  offset     |expected  HH              mm            ss           SSS   offset
            'Integer -> Time 1-1'|'GMT-09:00'|{ 0*3600_000 +  0*60_000 +  0*1_000 +   0 +9*3600_000}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'Integer -> Time 1-2'|'GMT-09:00'|{12*3600_000 + 34*60_000 + 56*1_000 + 789 +9*3600_000}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'Integer -> Time 1-3'|'GMT-09:00'|{23*3600_000 + 59*60_000 + 59*1_000 + 999 +9*3600_000}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'Integer -> Time 2-1'|'GMT+00:00'|{ 0*3600_000 +  0*60_000 +  0*1_000 +   0 +0*3600_000}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'Integer -> Time 2-2'|'GMT+00:00'|{12*3600_000 + 34*60_000 + 56*1_000 + 789 +0*3600_000}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'Integer -> Time 2-3'|'GMT+00:00'|{23*3600_000 + 59*60_000 + 59*1_000 + 999 +0*3600_000}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'Integer -> Time 3-1'|'GMT+09:00'|{ 0*3600_000 +  0*60_000 +  0*1_000 +   0 -9*3600_000}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'Integer -> Time 3-2'|'GMT+09:00'|{12*3600_000 + 34*60_000 + 56*1_000 + 789 -9*3600_000}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'Integer -> Time 3-3'|'GMT+09:00'|{23*3600_000 + 59*60_000 + 59*1_000 + 999 -9*3600_000}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName                |timeZoneId |before          HH              mm            ss           SSS   offset        |expected  HH              mm            ss           SSS   offset
            'BigDecimal -> Time 1-1'|'GMT-09:00'|{new BigDecimal( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'BigDecimal -> Time 1-2'|'GMT-09:00'|{new BigDecimal(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'BigDecimal -> Time 1-3'|'GMT-09:00'|{new BigDecimal(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'BigDecimal -> Time 2-1'|'GMT+00:00'|{new BigDecimal( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'BigDecimal -> Time 2-2'|'GMT+00:00'|{new BigDecimal(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'BigDecimal -> Time 2-3'|'GMT+00:00'|{new BigDecimal(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'BigDecimal -> Time 3-1'|'GMT+09:00'|{new BigDecimal( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'BigDecimal -> Time 3-2'|'GMT+09:00'|{new BigDecimal(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'BigDecimal -> Time 3-3'|'GMT+09:00'|{new BigDecimal(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName                    |timeZoneId |before              HH              mm            ss           SSS  offset         |expected  HH              mm            ss           SSS   offset
            'java.util.Date -> Time 1-1'|'GMT-09:00'|{new java.util.Date( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'java.util.Date -> Time 1-2'|'GMT-09:00'|{new java.util.Date(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'java.util.Date -> Time 1-3'|'GMT-09:00'|{new java.util.Date(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'java.util.Date -> Time 2-1'|'GMT+00:00'|{new java.util.Date( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'java.util.Date -> Time 2-2'|'GMT+00:00'|{new java.util.Date(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'java.util.Date -> Time 2-3'|'GMT+00:00'|{new java.util.Date(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'java.util.Date -> Time 3-1'|'GMT+09:00'|{new java.util.Date( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'java.util.Date -> Time 3-2'|'GMT+09:00'|{new java.util.Date(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'java.util.Date -> Time 3-3'|'GMT+09:00'|{new java.util.Date(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName                   |timeZoneId |before             HH              mm            ss           SSS  offset         |expected  HH              mm            ss           SSS   offset
            'java.sql.Date -> Time 1-1'|'GMT-09:00'|{new java.sql.Date( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'java.sql.Date -> Time 1-2'|'GMT-09:00'|{new java.sql.Date(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'java.sql.Date -> Time 1-3'|'GMT-09:00'|{new java.sql.Date(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'java.sql.Date -> Time 2-1'|'GMT+00:00'|{new java.sql.Date( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'java.sql.Date -> Time 2-2'|'GMT+00:00'|{new java.sql.Date(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'java.sql.Date -> Time 2-3'|'GMT+00:00'|{new java.sql.Date(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'java.sql.Date -> Time 3-1'|'GMT+09:00'|{new java.sql.Date( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'java.sql.Date -> Time 3-2'|'GMT+09:00'|{new java.sql.Date(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'java.sql.Date -> Time 3-3'|'GMT+09:00'|{new java.sql.Date(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName               |timeZoneId |before         HH              mm            ss           SSS  offset         |expected  HH              mm            ss           SSS   offset
            'Timestamp -> Time 1-1'|'GMT-09:00'|{new Timestamp( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'Timestamp -> Time 1-2'|'GMT-09:00'|{new Timestamp(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'Timestamp -> Time 1-3'|'GMT-09:00'|{new Timestamp(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'Timestamp -> Time 2-1'|'GMT+00:00'|{new Timestamp( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'Timestamp -> Time 2-2'|'GMT+00:00'|{new Timestamp(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'Timestamp -> Time 2-3'|'GMT+00:00'|{new Timestamp(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'Timestamp -> Time 3-1'|'GMT+09:00'|{new Timestamp( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'Timestamp -> Time 3-2'|'GMT+09:00'|{new Timestamp(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'Timestamp -> Time 3-3'|'GMT+09:00'|{new Timestamp(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName               |timeZoneId |before        HH  mm  ss  nanoOfSecond |expected  HH              mm            ss           SSS   offset
            'LocalTime -> Time 1-1'|'GMT-09:00'|{LocalTime.of( 0,  0,  0,           0)}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'LocalTime -> Time 1-2'|'GMT-09:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'LocalTime -> Time 1-3'|'GMT-09:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'LocalTime -> Time 2-1'|'GMT+00:00'|{LocalTime.of( 0,  0,  0,           0)}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'LocalTime -> Time 2-2'|'GMT+00:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'LocalTime -> Time 2-3'|'GMT+00:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'LocalTime -> Time 3-1'|'GMT+09:00'|{LocalTime.of( 0,  0,  0,           0)}|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'LocalTime -> Time 3-2'|'GMT+09:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'LocalTime -> Time 3-3'|'GMT+09:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName               |timeZoneId |before dd         HH              mm            ss           SSS   offset       |expected       dd               HH              mm            ss           SSS   offset
            'Long -> Timestamp 1-1'|'GMT-09:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L}|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'Long -> Timestamp 1-2'|'GMT-09:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L}|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'Long -> Timestamp 1-3'|'GMT-09:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L}|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'Long -> Timestamp 2-1'|'GMT+00:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L}|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'Long -> Timestamp 2-2'|'GMT+00:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L}|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'Long -> Timestamp 2-3'|'GMT+00:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L}|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'Long -> Timestamp 3-1'|'GMT+09:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L}|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'Long -> Timestamp 3-2'|'GMT+09:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L}|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'Long -> Timestamp 3-3'|'GMT+09:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L}|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName                  |timeZoneId |before dd       HH            mm          ss         SSS  offset     |expected       dd               HH              mm            ss           SSS   offset
            'Integer -> Timestamp 1-1'|'GMT-09:00'|{-1*86400_000 +  0*3600_000 +  0*60_000 +  0*1_000 +   0 +9*3600_000}|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'Integer -> Timestamp 1-2'|'GMT-09:00'|{ 0*86400_000 + 12*3600_000 + 34*60_000 + 56*1_000 + 789 +9*3600_000}|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'Integer -> Timestamp 1-3'|'GMT-09:00'|{ 1*86400_000 + 23*3600_000 + 59*60_000 + 59*1_000 + 999 +9*3600_000}|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'Integer -> Timestamp 2-1'|'GMT+00:00'|{-1*86400_000 +  0*3600_000 +  0*60_000 +  0*1_000 +   0 +0*3600_000}|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'Integer -> Timestamp 2-2'|'GMT+00:00'|{ 0*86400_000 + 12*3600_000 + 34*60_000 + 56*1_000 + 789 +0*3600_000}|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'Integer -> Timestamp 2-3'|'GMT+00:00'|{ 1*86400_000 + 23*3600_000 + 59*60_000 + 59*1_000 + 999 +0*3600_000}|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'Integer -> Timestamp 3-1'|'GMT+09:00'|{-1*86400_000 +  0*3600_000 +  0*60_000 +  0*1_000 +   0 -9*3600_000}|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'Integer -> Timestamp 3-2'|'GMT+09:00'|{ 0*86400_000 + 12*3600_000 + 34*60_000 + 56*1_000 + 789 -9*3600_000}|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'Integer -> Timestamp 3-3'|'GMT+09:00'|{ 1*86400_000 + 23*3600_000 + 59*60_000 + 59*1_000 + 999 -9*3600_000}|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName                     |timeZoneId |before          dd               HH              mm            ss           SSS   offset        |expected       dd               HH              mm            ss           SSS   offset
            'BigDecimal -> Timestamp 1-1'|'GMT-09:00'|{new BigDecimal(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'BigDecimal -> Timestamp 1-2'|'GMT-09:00'|{new BigDecimal( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'BigDecimal -> Timestamp 1-3'|'GMT-09:00'|{new BigDecimal( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'BigDecimal -> Timestamp 2-1'|'GMT+00:00'|{new BigDecimal(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'BigDecimal -> Timestamp 2-2'|'GMT+00:00'|{new BigDecimal( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'BigDecimal -> Timestamp 2-3'|'GMT+00:00'|{new BigDecimal( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'BigDecimal -> Timestamp 3-1'|'GMT+09:00'|{new BigDecimal(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'BigDecimal -> Timestamp 3-2'|'GMT+09:00'|{new BigDecimal( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'BigDecimal -> Timestamp 3-3'|'GMT+09:00'|{new BigDecimal( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName                         |timeZoneId |before              dd               HH              mm            ss           SSS  offset         |expected       dd               HH              mm            ss           SSS   offset
            'java.util.Date -> Timestamp 1-1'|'GMT-09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'java.util.Date -> Timestamp 1-2'|'GMT-09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'java.util.Date -> Timestamp 1-3'|'GMT-09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'java.util.Date -> Timestamp 2-1'|'GMT+00:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'java.util.Date -> Timestamp 2-2'|'GMT+00:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'java.util.Date -> Timestamp 2-3'|'GMT+00:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'java.util.Date -> Timestamp 3-1'|'GMT+09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'java.util.Date -> Timestamp 3-2'|'GMT+09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'java.util.Date -> Timestamp 3-3'|'GMT+09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName                        |timeZoneId |before    dd               HH              mm            ss           SSS  offset         |expected       dd               HH              mm            ss           SSS   offset
            'java.sql.Date -> Timestamp 1-1'|'GMT-09:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'java.sql.Date -> Timestamp 1-2'|'GMT-09:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'java.sql.Date -> Timestamp 1-3'|'GMT-09:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'java.sql.Date -> Timestamp 2-1'|'GMT+00:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'java.sql.Date -> Timestamp 2-2'|'GMT+00:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'java.sql.Date -> Timestamp 2-3'|'GMT+00:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'java.sql.Date -> Timestamp 3-1'|'GMT+09:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'java.sql.Date -> Timestamp 3-2'|'GMT+09:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'java.sql.Date -> Timestamp 3-3'|'GMT+09:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName               |timeZoneId |before    HH              mm            ss           SSS  offset         |expected       HH              mm            ss           SSS   offset
            'Time -> Timestamp 1-1'|'GMT-09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{new Timestamp( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}
            'Time -> Timestamp 1-2'|'GMT-09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{new Timestamp(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}
            'Time -> Timestamp 1-3'|'GMT-09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{new Timestamp(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}
            'Time -> Timestamp 2-1'|'GMT+00:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{new Timestamp( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}
            'Time -> Timestamp 2-2'|'GMT+00:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{new Timestamp(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}
            'Time -> Timestamp 2-3'|'GMT+00:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{new Timestamp(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}
            'Time -> Timestamp 3-1'|'GMT+09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{new Timestamp( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}
            'Time -> Timestamp 3-2'|'GMT+09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{new Timestamp(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}
            'Time -> Timestamp 3-3'|'GMT+09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{new Timestamp(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}

        //  caseName                        |timeZoneId |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond |expected               dd               HH              mm            ss          offset                  nano seconds
            'LocalDateTime -> Timestamp 1-1'|'GMT-09:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +9L*3600_000L); t.nanos =           0; return t}
            'LocalDateTime -> Timestamp 1-2'|'GMT-09:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +9L*3600_000L); t.nanos = 789_012_345; return t}
            'LocalDateTime -> Timestamp 1-3'|'GMT-09:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +9L*3600_000L); t.nanos = 999_999_999; return t}
            'LocalDateTime -> Timestamp 2-1'|'GMT+00:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +0L*3600_000L); t.nanos =           0; return t}
            'LocalDateTime -> Timestamp 2-2'|'GMT+00:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +0L*3600_000L); t.nanos = 789_012_345; return t}
            'LocalDateTime -> Timestamp 2-3'|'GMT+00:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +0L*3600_000L); t.nanos = 999_999_999; return t}
            'LocalDateTime -> Timestamp 3-1'|'GMT+09:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L -9L*3600_000L); t.nanos =           0; return t}
            'LocalDateTime -> Timestamp 3-2'|'GMT+09:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L -9L*3600_000L); t.nanos = 789_012_345; return t}
            'LocalDateTime -> Timestamp 3-3'|'GMT+09:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L -9L*3600_000L); t.nanos = 999_999_999; return t}

        //  caseName                         |timeZoneId |before             yyyy  MM  dd  HH  mm  ss nanoOfSecond  ZoneOffset              |expected               dd               HH              mm            ss          offset                  nano seconds
            'OffsetDateTime -> Timestamp 1-1'|'GMT-09:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +9L*3600_000L); t.nanos =           0; return t}
            'OffsetDateTime -> Timestamp 1-2'|'GMT-09:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours(-9))}|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +9L*3600_000L); t.nanos = 789_012_345; return t}
            'OffsetDateTime -> Timestamp 1-3'|'GMT-09:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours(-9))}|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +9L*3600_000L); t.nanos = 999_999_999; return t}
            'OffsetDateTime -> Timestamp 2-1'|'GMT+00:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +0L*3600_000L); t.nanos =           0; return t}
            'OffsetDateTime -> Timestamp 2-2'|'GMT+00:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 0))}|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +0L*3600_000L); t.nanos = 789_012_345; return t}
            'OffsetDateTime -> Timestamp 2-3'|'GMT+00:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 0))}|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +0L*3600_000L); t.nanos = 999_999_999; return t}
            'OffsetDateTime -> Timestamp 3-1'|'GMT+09:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L -9L*3600_000L); t.nanos =           0; return t}
            'OffsetDateTime -> Timestamp 3-2'|'GMT+09:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 9))}|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L -9L*3600_000L); t.nanos = 789_012_345; return t}
            'OffsetDateTime -> Timestamp 3-3'|'GMT+09:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 9))}|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L -9L*3600_000L); t.nanos = 999_999_999; return t}

        //  caseName                       timeZoneId |before            yyyy  MM  dd  HH  mm  ss nanoOfSecond  ZoneId                    |expected               dd               HH              mm            ss          offset                  nano seconds
            'ZonedDateTime -> Timestamp 1-1'|'GMT-09:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT-09:00'))}|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +9L*3600_000L); t.nanos =           0; return t}
            'ZonedDateTime -> Timestamp 1-2'|'GMT-09:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT-09:00'))}|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +9L*3600_000L); t.nanos = 789_012_345; return t}
            'ZonedDateTime -> Timestamp 1-3'|'GMT-09:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT-09:00'))}|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +9L*3600_000L); t.nanos = 999_999_999; return t}
            'ZonedDateTime -> Timestamp 2-1'|'GMT+00:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+00:00'))}|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +0L*3600_000L); t.nanos =           0; return t}
            'ZonedDateTime -> Timestamp 2-2'|'GMT+00:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+00:00'))}|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +0L*3600_000L); t.nanos = 789_012_345; return t}
            'ZonedDateTime -> Timestamp 2-3'|'GMT+00:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+00:00'))}|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +0L*3600_000L); t.nanos = 999_999_999; return t}
            'ZonedDateTime -> Timestamp 3-1'|'GMT+09:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+09:00'))}|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L -9L*3600_000L); t.nanos =           0; return t}
            'ZonedDateTime -> Timestamp 3-2'|'GMT+09:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+09:00'))}|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L -9L*3600_000L); t.nanos = 789_012_345; return t}
            'ZonedDateTime -> Timestamp 3-3'|'GMT+09:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+09:00'))}|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L -9L*3600_000L); t.nanos = 999_999_999; return t}

        //  caseName                  |timeZoneId |before                 dd           HH          mm        ss  offset     nano seconds  |expected               dd               HH              mm            ss          offset                  nano seconds
            'Instant -> Timestamp 1-1'|'GMT-09:00'|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L +9L*3600L,           0L)}|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +9L*3600_000L); t.nanos =           0; return t}
            'Instant -> Timestamp 1-2'|'GMT-09:00'|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L +9L*3600L, 789_012_345L)}|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +9L*3600_000L); t.nanos = 789_012_345; return t}
            'Instant -> Timestamp 1-3'|'GMT-09:00'|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L +9L*3600L, 999_999_999L)}|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +9L*3600_000L); t.nanos = 999_999_999; return t}
            'Instant -> Timestamp 2-1'|'GMT+00:00'|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L +0L*3600L,           0L)}|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +0L*3600_000L); t.nanos =           0; return t}
            'Instant -> Timestamp 2-2'|'GMT+00:00'|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L +0L*3600L, 789_012_345L)}|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +0L*3600_000L); t.nanos = 789_012_345; return t}
            'Instant -> Timestamp 2-3'|'GMT+00:00'|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L +0L*3600L, 999_999_999L)}|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +0L*3600_000L); t.nanos = 999_999_999; return t}
            'Instant -> Timestamp 3-1'|'GMT+09:00'|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L -9L*3600L,           0L)}|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L -9L*3600_000L); t.nanos =           0; return t}
            'Instant -> Timestamp 3-2'|'GMT+09:00'|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L -9L*3600L, 789_012_345L)}|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L -9L*3600_000L); t.nanos = 789_012_345; return t}
            'Instant -> Timestamp 3-3'|'GMT+09:00'|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L -9L*3600L, 999_999_999L)}|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L -9L*3600_000L); t.nanos = 999_999_999; return t}

        //  caseName               |timeZoneId |before dd        offset       |expected      yyyy  MM  dd
            'Long -> LocalDate 1-1'|'GMT-09:00'|{-1L*86400_000L +9L*3600_000L}|{LocalDate.of(1969, 12, 31)}
            'Long -> LocalDate 1-2'|'GMT-09:00'|{ 0L*86400_000L +9L*3600_000L}|{LocalDate.of(1970,  1,  1)}
            'Long -> LocalDate 1-3'|'GMT-09:00'|{ 1L*86400_000L +9L*3600_000L}|{LocalDate.of(1970,  1,  2)}
            'Long -> LocalDate 2-1'|'GMT+00:00'|{-1L*86400_000L +0L*3600_000L}|{LocalDate.of(1969, 12, 31)}
            'Long -> LocalDate 2-2'|'GMT+00:00'|{ 0L*86400_000L +0L*3600_000L}|{LocalDate.of(1970,  1,  1)}
            'Long -> LocalDate 2-3'|'GMT+00:00'|{ 1L*86400_000L +0L*3600_000L}|{LocalDate.of(1970,  1,  2)}
            'Long -> LocalDate 3-1'|'GMT+09:00'|{-1L*86400_000L -9L*3600_000L}|{LocalDate.of(1969, 12, 31)}
            'Long -> LocalDate 3-2'|'GMT+09:00'|{ 0L*86400_000L -9L*3600_000L}|{LocalDate.of(1970,  1,  1)}
            'Long -> LocalDate 3-3'|'GMT+09:00'|{ 1L*86400_000L -9L*3600_000L}|{LocalDate.of(1970,  1,  2)}

        //  caseName                         |timeZoneId |before              dd               HH              mm            ss           SSS  offset         |expected      yyyy  MM  dd
            'java.util.Date -> LocalDate 1-1'|'GMT-09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{LocalDate.of(1969, 12, 31)}
            'java.util.Date -> LocalDate 1-2'|'GMT-09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{LocalDate.of(1970,  1,  1)}
            'java.util.Date -> LocalDate 1-3'|'GMT-09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{LocalDate.of(1970,  1,  2)}
            'java.util.Date -> LocalDate 2-1'|'GMT+00:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{LocalDate.of(1969, 12, 31)}
            'java.util.Date -> LocalDate 2-2'|'GMT+00:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{LocalDate.of(1970,  1,  1)}
            'java.util.Date -> LocalDate 2-3'|'GMT+00:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{LocalDate.of(1970,  1,  2)}
            'java.util.Date -> LocalDate 3-1'|'GMT+09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{LocalDate.of(1969, 12, 31)}
            'java.util.Date -> LocalDate 3-2'|'GMT+09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{LocalDate.of(1970,  1,  1)}
            'java.util.Date -> LocalDate 3-3'|'GMT+09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{LocalDate.of(1970,  1,  2)}

        //  caseName                        |timeZoneId |before    dd               HH              mm            ss           SSS  offset         |expected      yyyy  MM  dd
            'java.sql.Date -> LocalDate 1-1'|'GMT-09:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{LocalDate.of(1969, 12, 31)}
            'java.sql.Date -> LocalDate 1-2'|'GMT-09:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{LocalDate.of(1970,  1,  1)}
            'java.sql.Date -> LocalDate 1-3'|'GMT-09:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{LocalDate.of(1970,  1,  2)}
            'java.sql.Date -> LocalDate 2-1'|'GMT+00:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{LocalDate.of(1969, 12, 31)}
            'java.sql.Date -> LocalDate 2-2'|'GMT+00:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{LocalDate.of(1970,  1,  1)}
            'java.sql.Date -> LocalDate 2-3'|'GMT+00:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{LocalDate.of(1970,  1,  2)}
            'java.sql.Date -> LocalDate 3-1'|'GMT+09:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{LocalDate.of(1969, 12, 31)}
            'java.sql.Date -> LocalDate 3-2'|'GMT+09:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{LocalDate.of(1970,  1,  1)}
            'java.sql.Date -> LocalDate 3-3'|'GMT+09:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{LocalDate.of(1970,  1,  2)}

        //  caseName                    |timeZoneId |before          dd               HH              mm            ss          SSS  offset         |expected      yyyy  MM  dd
            'Timestamp -> LocalDate 1-1'|'GMT-09:00'|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{LocalDate.of(1969, 12, 31)}
            'Timestamp -> LocalDate 1-2'|'GMT-09:00'|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{LocalDate.of(1970,  1,  1)}
            'Timestamp -> LocalDate 1-3'|'GMT-09:00'|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{LocalDate.of(1970,  1,  2)}
            'Timestamp -> LocalDate 2-1'|'GMT+00:00'|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{LocalDate.of(1969, 12, 31)}
            'Timestamp -> LocalDate 2-2'|'GMT+00:00'|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{LocalDate.of(1970,  1,  1)}
            'Timestamp -> LocalDate 2-3'|'GMT+00:00'|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{LocalDate.of(1970,  1,  2)}
            'Timestamp -> LocalDate 3-1'|'GMT+09:00'|{new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{LocalDate.of(1969, 12, 31)}
            'Timestamp -> LocalDate 3-2'|'GMT+09:00'|{new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{LocalDate.of(1970,  1,  1)}
            'Timestamp -> LocalDate 3-3'|'GMT+09:00'|{new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{LocalDate.of(1970,  1,  2)}

        //  caseName                  |timeZoneId |before                 dd           HH          mm        ss  offset     nano seconds  |expected      yyyy  MM  dd
            'Instant -> LocalDate 1-1'|'GMT-09:00'|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L +9L*3600L,           0L)}|{LocalDate.of(1969, 12, 31)}
            'Instant -> LocalDate 1-2'|'GMT-09:00'|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L +9L*3600L, 789_012_345L)}|{LocalDate.of(1970,  1,  1)}
            'Instant -> LocalDate 1-3'|'GMT-09:00'|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L +9L*3600L, 999_999_999L)}|{LocalDate.of(1970,  1,  2)}
            'Instant -> LocalDate 2-1'|'GMT+00:00'|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L +0L*3600L,           0L)}|{LocalDate.of(1969, 12, 31)}
            'Instant -> LocalDate 2-2'|'GMT+00:00'|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L +0L*3600L, 789_012_345L)}|{LocalDate.of(1970,  1,  1)}
            'Instant -> LocalDate 2-3'|'GMT+00:00'|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L +0L*3600L, 999_999_999L)}|{LocalDate.of(1970,  1,  2)}
            'Instant -> LocalDate 3-1'|'GMT+09:00'|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L -9L*3600L,           0L)}|{LocalDate.of(1969, 12, 31)}
            'Instant -> LocalDate 3-2'|'GMT+09:00'|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L -9L*3600L, 789_012_345L)}|{LocalDate.of(1970,  1,  1)}
            'Instant -> LocalDate 3-3'|'GMT+09:00'|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L -9L*3600L, 999_999_999L)}|{LocalDate.of(1970,  1,  2)}

        //  caseName                        |timeZoneId |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond |expected      yyyy  MM  dd
            'LocalDateTime -> LocalDate 1-1'|'GMT-09:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{LocalDate.of(1969, 12, 31)}
            'LocalDateTime -> LocalDate 1-2'|'GMT-09:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{LocalDate.of(1970,  1,  1)}
            'LocalDateTime -> LocalDate 1-3'|'GMT-09:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{LocalDate.of(1970,  1,  2)}
            'LocalDateTime -> LocalDate 2-1'|'GMT+00:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{LocalDate.of(1969, 12, 31)}
            'LocalDateTime -> LocalDate 2-2'|'GMT+00:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{LocalDate.of(1970,  1,  1)}
            'LocalDateTime -> LocalDate 2-3'|'GMT+00:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{LocalDate.of(1970,  1,  2)}
            'LocalDateTime -> LocalDate 3-1'|'GMT+09:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{LocalDate.of(1969, 12, 31)}
            'LocalDateTime -> LocalDate 3-2'|'GMT+09:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{LocalDate.of(1970,  1,  1)}
            'LocalDateTime -> LocalDate 3-3'|'GMT+09:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{LocalDate.of(1970,  1,  2)}

        //  caseName                         |timeZoneId |before             yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneOffset              |expected      yyyy  MM  dd
            'OffsetDateTime -> LocalDate 1-1'|'GMT-09:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}|{LocalDate.of(1969, 12, 31)}
            'OffsetDateTime -> LocalDate 1-2'|'GMT-09:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours(-9))}|{LocalDate.of(1970,  1,  1)}
            'OffsetDateTime -> LocalDate 1-3'|'GMT-09:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours(-9))}|{LocalDate.of(1970,  1,  2)}
            'OffsetDateTime -> LocalDate 2-1'|'GMT+00:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}|{LocalDate.of(1969, 12, 31)}
            'OffsetDateTime -> LocalDate 2-2'|'GMT+00:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 0))}|{LocalDate.of(1970,  1,  1)}
            'OffsetDateTime -> LocalDate 2-3'|'GMT+00:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 0))}|{LocalDate.of(1970,  1,  2)}
            'OffsetDateTime -> LocalDate 3-1'|'GMT+09:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}|{LocalDate.of(1969, 12, 31)}
            'OffsetDateTime -> LocalDate 3-2'|'GMT+09:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 9))}|{LocalDate.of(1970,  1,  1)}
            'OffsetDateTime -> LocalDate 3-3'|'GMT+09:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 9))}|{LocalDate.of(1970,  1,  2)}

        //  caseName                        |timeZoneId |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneId                  |expected      yyyy  MM  dd
            'ZonedDateTime -> LocalDate 1-1'|'GMT-09:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT-09:00'))}|{LocalDate.of(1969, 12, 31)}
            'ZonedDateTime -> LocalDate 1-2'|'GMT-09:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT-09:00'))}|{LocalDate.of(1970,  1,  1)}
            'ZonedDateTime -> LocalDate 1-3'|'GMT-09:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT-09:00'))}|{LocalDate.of(1970,  1,  2)}
            'ZonedDateTime -> LocalDate 2-1'|'GMT+00:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+00:00'))}|{LocalDate.of(1969, 12, 31)}
            'ZonedDateTime -> LocalDate 2-2'|'GMT+00:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+00:00'))}|{LocalDate.of(1970,  1,  1)}
            'ZonedDateTime -> LocalDate 2-3'|'GMT+00:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+00:00'))}|{LocalDate.of(1970,  1,  2)}
            'ZonedDateTime -> LocalDate 3-1'|'GMT+09:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+09:00'))}|{LocalDate.of(1969, 12, 31)}
            'ZonedDateTime -> LocalDate 3-2'|'GMT+09:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+09:00'))}|{LocalDate.of(1970,  1,  1)}
            'ZonedDateTime -> LocalDate 3-3'|'GMT+09:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+09:00'))}|{LocalDate.of(1970,  1,  2)}

        //  caseName               |timeZoneId |before HH        mm            ss           SSS   offset       |expected      HH  mm  ss  nanoOfSecond
            'Long -> LocalTime 1-1'|'GMT-09:00'|{ 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L}|{LocalTime.of( 0,  0,  0,           0)}
            'Long -> LocalTime 1-2'|'GMT-09:00'|{12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L}|{LocalTime.of(12, 34, 56, 789_000_000)}
            'Long -> LocalTime 1-3'|'GMT-09:00'|{23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L}|{LocalTime.of(23, 59, 59, 999_000_000)}
            'Long -> LocalTime 2-1'|'GMT+00:00'|{ 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L}|{LocalTime.of( 0,  0,  0,           0)}
            'Long -> LocalTime 2-2'|'GMT+00:00'|{12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L}|{LocalTime.of(12, 34, 56, 789_000_000)}
            'Long -> LocalTime 2-3'|'GMT+00:00'|{23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L}|{LocalTime.of(23, 59, 59, 999_000_000)}
            'Long -> LocalTime 3-1'|'GMT+09:00'|{ 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L}|{LocalTime.of( 0,  0,  0,           0)}
            'Long -> LocalTime 3-2'|'GMT+09:00'|{12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L}|{LocalTime.of(12, 34, 56, 789_000_000)}
            'Long -> LocalTime 3-3'|'GMT+09:00'|{23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L}|{LocalTime.of(23, 59, 59, 999_000_000)}

        //  caseName                         |timeZoneId |before              HH              mm            ss           SSS   offset        |expected      HH  mm  ss  nanoOfSecond
            'java.util.Date -> LocalTime 1-1'|'GMT-09:00'|{new java.util.Date( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{LocalTime.of( 0,  0,  0,           0)}
            'java.util.Date -> LocalTime 1-2'|'GMT-09:00'|{new java.util.Date(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{LocalTime.of(12, 34, 56, 789_000_000)}
            'java.util.Date -> LocalTime 1-3'|'GMT-09:00'|{new java.util.Date(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{LocalTime.of(23, 59, 59, 999_000_000)}
            'java.util.Date -> LocalTime 2-1'|'GMT+00:00'|{new java.util.Date( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{LocalTime.of( 0,  0,  0,           0)}
            'java.util.Date -> LocalTime 2-2'|'GMT+00:00'|{new java.util.Date(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{LocalTime.of(12, 34, 56, 789_000_000)}
            'java.util.Date -> LocalTime 2-3'|'GMT+00:00'|{new java.util.Date(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{LocalTime.of(23, 59, 59, 999_000_000)}
            'java.util.Date -> LocalTime 3-1'|'GMT+09:00'|{new java.util.Date( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{LocalTime.of( 0,  0,  0,           0)}
            'java.util.Date -> LocalTime 3-2'|'GMT+09:00'|{new java.util.Date(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{LocalTime.of(12, 34, 56, 789_000_000)}
            'java.util.Date -> LocalTime 3-3'|'GMT+09:00'|{new java.util.Date(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{LocalTime.of(23, 59, 59, 999_000_000)}

        //  caseName                        |timeZoneId |before    HH              mm            ss           SSS   offset        |expected      HH  mm  ss  nanoOfSecond
            'java.sql.Date -> LocalTime 1-1'|'GMT-09:00'|{new Date( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{LocalTime.of( 0,  0,  0,           0)}
            'java.sql.Date -> LocalTime 1-2'|'GMT-09:00'|{new Date(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{LocalTime.of(12, 34, 56, 789_000_000)}
            'java.sql.Date -> LocalTime 1-3'|'GMT-09:00'|{new Date(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{LocalTime.of(23, 59, 59, 999_000_000)}
            'java.sql.Date -> LocalTime 2-1'|'GMT+00:00'|{new Date( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{LocalTime.of( 0,  0,  0,           0)}
            'java.sql.Date -> LocalTime 2-2'|'GMT+00:00'|{new Date(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{LocalTime.of(12, 34, 56, 789_000_000)}
            'java.sql.Date -> LocalTime 2-3'|'GMT+00:00'|{new Date(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{LocalTime.of(23, 59, 59, 999_000_000)}
            'java.sql.Date -> LocalTime 3-1'|'GMT+09:00'|{new Date( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{LocalTime.of( 0,  0,  0,           0)}
            'java.sql.Date -> LocalTime 3-2'|'GMT+09:00'|{new Date(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{LocalTime.of(12, 34, 56, 789_000_000)}
            'java.sql.Date -> LocalTime 3-3'|'GMT+09:00'|{new Date(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{LocalTime.of(23, 59, 59, 999_000_000)}

        //  caseName               |timeZoneId |before    HH              mm            ss           SSS   offset        |expected      HH  mm  ss  nanoOfSecond
            'Time -> LocalTime 1-1'|'GMT-09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{LocalTime.of( 0,  0,  0,           0)}
            'Time -> LocalTime 1-2'|'GMT-09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{LocalTime.of(12, 34, 56, 789_000_000)}
            'Time -> LocalTime 1-3'|'GMT-09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{LocalTime.of(23, 59, 59, 999_000_000)}
            'Time -> LocalTime 2-1'|'GMT+00:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{LocalTime.of( 0,  0,  0,           0)}
            'Time -> LocalTime 2-2'|'GMT+00:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{LocalTime.of(12, 34, 56, 789_000_000)}
            'Time -> LocalTime 2-3'|'GMT+00:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{LocalTime.of(23, 59, 59, 999_000_000)}
            'Time -> LocalTime 3-1'|'GMT+09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{LocalTime.of( 0,  0,  0,           0)}
            'Time -> LocalTime 3-2'|'GMT+09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{LocalTime.of(12, 34, 56, 789_000_000)}
            'Time -> LocalTime 3-3'|'GMT+09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{LocalTime.of(23, 59, 59, 999_000_000)}

        //  caseName                    |timeZoneId |before                 HH              mm            ss          offset                  nano seconds           |expected      HH  mm  ss  nanoOfSecond
            'Timestamp -> LocalTime 1-1'|'GMT-09:00'|{def t = new Timestamp( 0L*3600_000L +  0L*60_000L +  0L*1_000L +9L*3600_000L); t.nanos =           0; return t}|{LocalTime.of( 0,  0,  0,           0)}
            'Timestamp -> LocalTime 1-2'|'GMT-09:00'|{def t = new Timestamp(12L*3600_000L + 34L*60_000L + 56L*1_000L +9L*3600_000L); t.nanos = 789_012_345; return t}|{LocalTime.of(12, 34, 56, 789_012_345)}
            'Timestamp -> LocalTime 1-3'|'GMT-09:00'|{def t = new Timestamp(23L*3600_000L + 59L*60_000L + 59L*1_000L +9L*3600_000L); t.nanos = 999_999_999; return t}|{LocalTime.of(23, 59, 59, 999_999_999)}
            'Timestamp -> LocalTime 2-1'|'GMT+00:00'|{def t = new Timestamp( 0L*3600_000L +  0L*60_000L +  0L*1_000L +0L*3600_000L); t.nanos =           0; return t}|{LocalTime.of( 0,  0,  0,           0)}
            'Timestamp -> LocalTime 2-2'|'GMT+00:00'|{def t = new Timestamp(12L*3600_000L + 34L*60_000L + 56L*1_000L +0L*3600_000L); t.nanos = 789_012_345; return t}|{LocalTime.of(12, 34, 56, 789_012_345)}
            'Timestamp -> LocalTime 2-3'|'GMT+00:00'|{def t = new Timestamp(23L*3600_000L + 59L*60_000L + 59L*1_000L +0L*3600_000L); t.nanos = 999_999_999; return t}|{LocalTime.of(23, 59, 59, 999_999_999)}
            'Timestamp -> LocalTime 3-1'|'GMT+09:00'|{def t = new Timestamp( 0L*3600_000L +  0L*60_000L +  0L*1_000L -9L*3600_000L); t.nanos =           0; return t}|{LocalTime.of( 0,  0,  0,           0)}
            'Timestamp -> LocalTime 3-2'|'GMT+09:00'|{def t = new Timestamp(12L*3600_000L + 34L*60_000L + 56L*1_000L -9L*3600_000L); t.nanos = 789_012_345; return t}|{LocalTime.of(12, 34, 56, 789_012_345)}
            'Timestamp -> LocalTime 3-3'|'GMT+09:00'|{def t = new Timestamp(23L*3600_000L + 59L*60_000L + 59L*1_000L -9L*3600_000L); t.nanos = 999_999_999; return t}|{LocalTime.of(23, 59, 59, 999_999_999)}

        //  caseName                  |timeZoneId |before                 HH          mm        ss  offset     nano seconds  |expected      HH  mm  ss  nanoOfSecond
            'Instant -> LocalTime 1-1'|'GMT-09:00'|{Instant.ofEpochSecond( 0L*3600L +  0L*60L +  0L +9L*3600L,           0L)}|{LocalTime.of( 0,  0,  0,           0)}
            'Instant -> LocalTime 1-2'|'GMT-09:00'|{Instant.ofEpochSecond(12L*3600L + 34L*60L + 56L +9L*3600L, 789_012_345L)}|{LocalTime.of(12, 34, 56, 789_012_345)}
            'Instant -> LocalTime 1-3'|'GMT-09:00'|{Instant.ofEpochSecond(23L*3600L + 59L*60L + 59L +9L*3600L, 999_999_999L)}|{LocalTime.of(23, 59, 59, 999_999_999)}
            'Instant -> LocalTime 2-1'|'GMT+00:00'|{Instant.ofEpochSecond( 0L*3600L +  0L*60L +  0L +0L*3600L,           0L)}|{LocalTime.of( 0,  0,  0,           0)}
            'Instant -> LocalTime 2-2'|'GMT+00:00'|{Instant.ofEpochSecond(12L*3600L + 34L*60L + 56L +0L*3600L, 789_012_345L)}|{LocalTime.of(12, 34, 56, 789_012_345)}
            'Instant -> LocalTime 2-3'|'GMT+00:00'|{Instant.ofEpochSecond(23L*3600L + 59L*60L + 59L +0L*3600L, 999_999_999L)}|{LocalTime.of(23, 59, 59, 999_999_999)}
            'Instant -> LocalTime 3-1'|'GMT+09:00'|{Instant.ofEpochSecond( 0L*3600L +  0L*60L +  0L -9L*3600L,           0L)}|{LocalTime.of( 0,  0,  0,           0)}
            'Instant -> LocalTime 3-2'|'GMT+09:00'|{Instant.ofEpochSecond(12L*3600L + 34L*60L + 56L -9L*3600L, 789_012_345L)}|{LocalTime.of(12, 34, 56, 789_012_345)}
            'Instant -> LocalTime 3-3'|'GMT+09:00'|{Instant.ofEpochSecond(23L*3600L + 59L*60L + 59L -9L*3600L, 999_999_999L)}|{LocalTime.of(23, 59, 59, 999_999_999)}

        //  caseName                        |timeZoneId |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond |expected      HH  mm  ss  nanoOfSecond
            'LocalDateTime -> LocalTime 1-1'|'GMT-09:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{LocalTime.of( 0,  0,  0,           0)}
            'LocalDateTime -> LocalTime 1-2'|'GMT-09:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{LocalTime.of(12, 34, 56, 789_012_345)}
            'LocalDateTime -> LocalTime 1-3'|'GMT-09:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{LocalTime.of(23, 59, 59, 999_999_999)}
            'LocalDateTime -> LocalTime 2-1'|'GMT+00:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{LocalTime.of( 0,  0,  0,           0)}
            'LocalDateTime -> LocalTime 2-2'|'GMT+00:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{LocalTime.of(12, 34, 56, 789_012_345)}
            'LocalDateTime -> LocalTime 2-3'|'GMT+00:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{LocalTime.of(23, 59, 59, 999_999_999)}
            'LocalDateTime -> LocalTime 3-1'|'GMT+09:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{LocalTime.of( 0,  0,  0,           0)}
            'LocalDateTime -> LocalTime 3-2'|'GMT+09:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{LocalTime.of(12, 34, 56, 789_012_345)}
            'LocalDateTime -> LocalTime 3-3'|'GMT+09:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{LocalTime.of(23, 59, 59, 999_999_999)}

        //  caseName                         |timeZoneId |before             yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneOffset              |expected      HH  mm  ss  nanoOfSecond
            'OffsetDateTime -> LocalTime 1-1'|'GMT-09:00'|{OffsetDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}|{LocalTime.of( 0,  0,  0,           0)}
            'OffsetDateTime -> LocalTime 1-2'|'GMT-09:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours(-9))}|{LocalTime.of(12, 34, 56, 789_012_345)}
            'OffsetDateTime -> LocalTime 1-3'|'GMT-09:00'|{OffsetDateTime.of(1970,  1,  1, 23, 59, 59, 999_999_999, ZoneOffset.ofHours(-9))}|{LocalTime.of(23, 59, 59, 999_999_999)}
            'OffsetDateTime -> LocalTime 2-1'|'GMT+00:00'|{OffsetDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}|{LocalTime.of( 0,  0,  0,           0)}
            'OffsetDateTime -> LocalTime 2-2'|'GMT+00:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 0))}|{LocalTime.of(12, 34, 56, 789_012_345)}
            'OffsetDateTime -> LocalTime 2-3'|'GMT+00:00'|{OffsetDateTime.of(1970,  1,  1, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 0))}|{LocalTime.of(23, 59, 59, 999_999_999)}
            'OffsetDateTime -> LocalTime 3-1'|'GMT+09:00'|{OffsetDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}|{LocalTime.of( 0,  0,  0,           0)}
            'OffsetDateTime -> LocalTime 3-2'|'GMT+09:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 9))}|{LocalTime.of(12, 34, 56, 789_012_345)}
            'OffsetDateTime -> LocalTime 3-3'|'GMT+09:00'|{OffsetDateTime.of(1970,  1,  1, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 9))}|{LocalTime.of(23, 59, 59, 999_999_999)}

        //  caseName                        |timeZoneId |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneId                  |expected      HH  mm  ss  nanoOfSecond
            'ZonedDateTime -> LocalTime 1-1'|'GMT-09:00'|{ZonedDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneId.of('GMT-09:00'))}|{LocalTime.of( 0,  0,  0,           0)}
            'ZonedDateTime -> LocalTime 1-2'|'GMT-09:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT-09:00'))}|{LocalTime.of(12, 34, 56, 789_012_345)}
            'ZonedDateTime -> LocalTime 1-3'|'GMT-09:00'|{ZonedDateTime.of(1970,  1,  1, 23, 59, 59, 999_999_999, ZoneId.of('GMT-09:00'))}|{LocalTime.of(23, 59, 59, 999_999_999)}
            'ZonedDateTime -> LocalTime 2-1'|'GMT+00:00'|{ZonedDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneId.of('GMT+00:00'))}|{LocalTime.of( 0,  0,  0,           0)}
            'ZonedDateTime -> LocalTime 2-2'|'GMT+00:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+00:00'))}|{LocalTime.of(12, 34, 56, 789_012_345)}
            'ZonedDateTime -> LocalTime 2-3'|'GMT+00:00'|{ZonedDateTime.of(1970,  1,  1, 23, 59, 59, 999_999_999, ZoneId.of('GMT+00:00'))}|{LocalTime.of(23, 59, 59, 999_999_999)}
            'ZonedDateTime -> LocalTime 3-1'|'GMT+09:00'|{ZonedDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneId.of('GMT+09:00'))}|{LocalTime.of( 0,  0,  0,           0)}
            'ZonedDateTime -> LocalTime 3-2'|'GMT+09:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+09:00'))}|{LocalTime.of(12, 34, 56, 789_012_345)}
            'ZonedDateTime -> LocalTime 3-3'|'GMT+09:00'|{ZonedDateTime.of(1970,  1,  1, 23, 59, 59, 999_999_999, ZoneId.of('GMT+09:00'))}|{LocalTime.of(23, 59, 59, 999_999_999)}

        //  caseName                   |timeZoneId |before dd         HH              mm            ss           SSS   offset       |expected          yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'Long -> LocalDateTime 1-1'|'GMT-09:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'Long -> LocalDateTime 1-2'|'GMT-09:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'Long -> LocalDateTime 1-3'|'GMT-09:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000)}
            'Long -> LocalDateTime 2-1'|'GMT+00:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'Long -> LocalDateTime 2-2'|'GMT+00:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'Long -> LocalDateTime 2-3'|'GMT+00:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000)}
            'Long -> LocalDateTime 3-1'|'GMT+09:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'Long -> LocalDateTime 3-2'|'GMT+09:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'Long -> LocalDateTime 3-3'|'GMT+09:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000)}

        //  caseName                             |timeZoneId |before              dd               HH              mm            ss           SSS  offset         |expected          yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'java.util.Date -> LocalDateTime 1-1'|'GMT-09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'java.util.Date -> LocalDateTime 1-2'|'GMT-09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'java.util.Date -> LocalDateTime 1-3'|'GMT-09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000)}
            'java.util.Date -> LocalDateTime 2-1'|'GMT+00:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'java.util.Date -> LocalDateTime 2-2'|'GMT+00:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'java.util.Date -> LocalDateTime 2-3'|'GMT+00:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000)}
            'java.util.Date -> LocalDateTime 3-1'|'GMT+09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'java.util.Date -> LocalDateTime 3-2'|'GMT+09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'java.util.Date -> LocalDateTime 3-3'|'GMT+09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000)}

        //  caseName                            |timeZoneId |before    dd               HH              mm            ss           SSS  offset         |expected          yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'java.sql.Date -> LocalDateTime 1-1'|'GMT-09:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'java.sql.Date -> LocalDateTime 1-2'|'GMT-09:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'java.sql.Date -> LocalDateTime 1-3'|'GMT-09:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000)}
            'java.sql.Date -> LocalDateTime 2-1'|'GMT+00:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'java.sql.Date -> LocalDateTime 2-2'|'GMT+00:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'java.sql.Date -> LocalDateTime 2-3'|'GMT+00:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000)}
            'java.sql.Date -> LocalDateTime 3-1'|'GMT+09:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'java.sql.Date -> LocalDateTime 3-2'|'GMT+09:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'java.sql.Date -> LocalDateTime 3-3'|'GMT+09:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000)}

        //  caseName                   |timeZoneId |before    HH              mm            ss           SSS  offset         |expected          yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'Time -> LocalDateTime 1-1'|'GMT-09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{LocalDateTime.of(1970,  1,  1,  0,  0,  0,           0)}
            'Time -> LocalDateTime 1-2'|'GMT-09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'Time -> LocalDateTime 1-3'|'GMT-09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 23, 59, 59, 999_000_000)}
            'Time -> LocalDateTime 2-1'|'GMT+00:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{LocalDateTime.of(1970,  1,  1,  0,  0,  0,           0)}
            'Time -> LocalDateTime 2-2'|'GMT+00:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'Time -> LocalDateTime 2-3'|'GMT+00:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 23, 59, 59, 999_000_000)}
            'Time -> LocalDateTime 3-1'|'GMT+09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{LocalDateTime.of(1970,  1,  1,  0,  0,  0,           0)}
            'Time -> LocalDateTime 3-2'|'GMT+09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'Time -> LocalDateTime 3-3'|'GMT+09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 23, 59, 59, 999_000_000)}

        //  caseName                        |timeZoneId |before                 dd               HH              mm            ss          offset                  nano seconds           |expected          yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'Timestamp -> LocalDateTime 1-1'|'GMT-09:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +9L*3600_000L); t.nanos =           0; return t}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'Timestamp -> LocalDateTime 1-2'|'GMT-09:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +9L*3600_000L); t.nanos = 789_012_345; return t}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'Timestamp -> LocalDateTime 1-3'|'GMT-09:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +9L*3600_000L); t.nanos = 999_999_999; return t}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}
            'Timestamp -> LocalDateTime 2-1'|'GMT+00:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +0L*3600_000L); t.nanos =           0; return t}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'Timestamp -> LocalDateTime 2-2'|'GMT+00:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +0L*3600_000L); t.nanos = 789_012_345; return t}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'Timestamp -> LocalDateTime 2-3'|'GMT+00:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +0L*3600_000L); t.nanos = 999_999_999; return t}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}
            'Timestamp -> LocalDateTime 3-1'|'GMT+09:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L -9L*3600_000L); t.nanos =           0; return t}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'Timestamp -> LocalDateTime 3-2'|'GMT+09:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L -9L*3600_000L); t.nanos = 789_012_345; return t}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'Timestamp -> LocalDateTime 3-3'|'GMT+09:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L -9L*3600_000L); t.nanos = 999_999_999; return t}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}

        //  caseName                      |timeZoneId |before                 dd           HH          mm        ss  offset     nano seconds  |expected          yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'Instant -> LocalDateTime 1-1'|'GMT-09:00'|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L +9L*3600L,           0L)}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'Instant -> LocalDateTime 1-2'|'GMT-09:00'|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L +9L*3600L, 789_012_345L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'Instant -> LocalDateTime 1-3'|'GMT-09:00'|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L +9L*3600L, 999_999_999L)}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}
            'Instant -> LocalDateTime 2-1'|'GMT+00:00'|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L +0L*3600L,           0L)}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'Instant -> LocalDateTime 2-2'|'GMT+00:00'|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L +0L*3600L, 789_012_345L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'Instant -> LocalDateTime 2-3'|'GMT+00:00'|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L +0L*3600L, 999_999_999L)}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}
            'Instant -> LocalDateTime 3-1'|'GMT+09:00'|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L -9L*3600L,           0L)}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'Instant -> LocalDateTime 3-2'|'GMT+09:00'|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L -9L*3600L, 789_012_345L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'Instant -> LocalDateTime 3-3'|'GMT+09:00'|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L -9L*3600L, 999_999_999L)}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}

        //  caseName                        |timeZoneId |expected      yyyy  MM  dd  |expected          yyyy  MM  dd HH mm ss
            'LocalDate -> LocalDateTime 1-1'|'GMT-09:00'|{LocalDate.of(1969, 12, 31)}|{LocalDateTime.of(1969, 12, 31, 0, 0, 0)}
            'LocalDate -> LocalDateTime 1-2'|'GMT-09:00'|{LocalDate.of(1970,  1,  1)}|{LocalDateTime.of(1970,  1,  1, 0, 0, 0)}
            'LocalDate -> LocalDateTime 1-3'|'GMT-09:00'|{LocalDate.of(1970,  1,  2)}|{LocalDateTime.of(1970,  1,  2, 0, 0, 0)}
            'LocalDate -> LocalDateTime 2-1'|'GMT+00:00'|{LocalDate.of(1969, 12, 31)}|{LocalDateTime.of(1969, 12, 31, 0, 0, 0)}
            'LocalDate -> LocalDateTime 2-2'|'GMT+00:00'|{LocalDate.of(1970,  1,  1)}|{LocalDateTime.of(1970,  1,  1, 0, 0, 0)}
            'LocalDate -> LocalDateTime 2-3'|'GMT+00:00'|{LocalDate.of(1970,  1,  2)}|{LocalDateTime.of(1970,  1,  2, 0, 0, 0)}
            'LocalDate -> LocalDateTime 3-1'|'GMT+09:00'|{LocalDate.of(1969, 12, 31)}|{LocalDateTime.of(1969, 12, 31, 0, 0, 0)}
            'LocalDate -> LocalDateTime 3-2'|'GMT+09:00'|{LocalDate.of(1970,  1,  1)}|{LocalDateTime.of(1970,  1,  1, 0, 0, 0)}
            'LocalDate -> LocalDateTime 3-3'|'GMT+09:00'|{LocalDate.of(1970,  1,  2)}|{LocalDateTime.of(1970,  1,  2, 0, 0, 0)}

        //  caseName                        |timeZoneId |expected      HH  mm  ss  nanoOfSecond |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'LocalTime -> LocalDateTime 1-1'|'GMT-09:00'|{LocalTime.of( 0,  0,  0,           0)}|{LocalDateTime.of(1970,  1,  1,  0,  0,  0,           0)}
            'LocalTime -> LocalDateTime 1-2'|'GMT-09:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'LocalTime -> LocalDateTime 1-3'|'GMT-09:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{LocalDateTime.of(1970,  1,  1, 23, 59, 59, 999_999_999)}
            'LocalTime -> LocalDateTime 2-1'|'GMT+00:00'|{LocalTime.of( 0,  0,  0,           0)}|{LocalDateTime.of(1970,  1,  1,  0,  0,  0,           0)}
            'LocalTime -> LocalDateTime 2-2'|'GMT+00:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'LocalTime -> LocalDateTime 2-3'|'GMT+00:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{LocalDateTime.of(1970,  1,  1, 23, 59, 59, 999_999_999)}
            'LocalTime -> LocalDateTime 3-1'|'GMT+09:00'|{LocalTime.of( 0,  0,  0,           0)}|{LocalDateTime.of(1970,  1,  1,  0,  0,  0,           0)}
            'LocalTime -> LocalDateTime 3-2'|'GMT+09:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'LocalTime -> LocalDateTime 3-3'|'GMT+09:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{LocalDateTime.of(1970,  1,  1, 23, 59, 59, 999_999_999)}

        //  caseName                             |timeZoneId |before             yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneOffset              |expected          yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'OffsetDateTime -> LocalDateTime 1-1'|'GMT-09:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'OffsetDateTime -> LocalDateTime 1-2'|'GMT-09:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours(-9))}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'OffsetDateTime -> LocalDateTime 1-3'|'GMT-09:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours(-9))}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}
            'OffsetDateTime -> LocalDateTime 2-1'|'GMT+00:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'OffsetDateTime -> LocalDateTime 2-2'|'GMT+00:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 0))}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'OffsetDateTime -> LocalDateTime 2-3'|'GMT+00:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 0))}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}
            'OffsetDateTime -> LocalDateTime 3-1'|'GMT+09:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'OffsetDateTime -> LocalDateTime 3-2'|'GMT+09:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 9))}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'OffsetDateTime -> LocalDateTime 3-3'|'GMT+09:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 9))}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}

        //  caseName                            |timeZoneId |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneId                  |expected          yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'ZonedDateTime -> LocalDateTime 1-1'|'GMT-09:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT-09:00'))}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'ZonedDateTime -> LocalDateTime 1-2'|'GMT-09:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT-09:00'))}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'ZonedDateTime -> LocalDateTime 1-3'|'GMT-09:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT-09:00'))}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}
            'ZonedDateTime -> LocalDateTime 2-1'|'GMT+00:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+00:00'))}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'ZonedDateTime -> LocalDateTime 2-2'|'GMT+00:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+00:00'))}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'ZonedDateTime -> LocalDateTime 2-3'|'GMT+00:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+00:00'))}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}
            'ZonedDateTime -> LocalDateTime 3-1'|'GMT+09:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+09:00'))}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'ZonedDateTime -> LocalDateTime 3-2'|'GMT+09:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+09:00'))}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'ZonedDateTime -> LocalDateTime 3-3'|'GMT+09:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+09:00'))}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}

        //  caseName                    |timeZoneId |before dd         HH              mm            ss           SSS   offset        |expected          yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'Long -> OffsetDateTime 1-1'|'GMT-09:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}
            'Long -> OffsetDateTime 1-2'|'GMT-09:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneOffset.ofHours(-9))}
            'Long -> OffsetDateTime 1-3'|'GMT-09:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneOffset.ofHours(-9))}
            'Long -> OffsetDateTime 2-1'|'GMT+00:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}
            'Long -> OffsetDateTime 2-2'|'GMT+00:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneOffset.ofHours( 0))}
            'Long -> OffsetDateTime 2-3'|'GMT+00:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneOffset.ofHours( 0))}
            'Long -> OffsetDateTime 3-1'|'GMT+09:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}
            'Long -> OffsetDateTime 3-2'|'GMT+09:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneOffset.ofHours( 9))}
            'Long -> OffsetDateTime 3-3'|'GMT+09:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneOffset.ofHours( 9))}

        //  caseName                              |timeZoneId |before              dd               HH              mm            ss           SSS  offset         |expected          yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'java.util.Date -> OffsetDateTime 1-1'|'GMT-09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}
            'java.util.Date -> OffsetDateTime 1-2'|'GMT-09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneOffset.ofHours(-9))}
            'java.util.Date -> OffsetDateTime 1-3'|'GMT-09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneOffset.ofHours(-9))}
            'java.util.Date -> OffsetDateTime 2-1'|'GMT+00:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}
            'java.util.Date -> OffsetDateTime 2-2'|'GMT+00:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneOffset.ofHours( 0))}
            'java.util.Date -> OffsetDateTime 2-3'|'GMT+00:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneOffset.ofHours( 0))}
            'java.util.Date -> OffsetDateTime 3-1'|'GMT+09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}
            'java.util.Date -> OffsetDateTime 3-2'|'GMT+09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneOffset.ofHours( 9))}
            'java.util.Date -> OffsetDateTime 3-3'|'GMT+09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneOffset.ofHours( 9))}

        //  caseName                             |timeZoneId |before    dd               HH              mm            ss           SSS  offset         |expected          yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'java.sql.Date -> OffsetDateTime 1-1'|'GMT-09:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}
            'java.sql.Date -> OffsetDateTime 1-2'|'GMT-09:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneOffset.ofHours(-9))}
            'java.sql.Date -> OffsetDateTime 1-3'|'GMT-09:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneOffset.ofHours(-9))}
            'java.sql.Date -> OffsetDateTime 2-1'|'GMT+00:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}
            'java.sql.Date -> OffsetDateTime 2-2'|'GMT+00:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneOffset.ofHours( 0))}
            'java.sql.Date -> OffsetDateTime 2-3'|'GMT+00:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneOffset.ofHours( 0))}
            'java.sql.Date -> OffsetDateTime 3-1'|'GMT+09:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}
            'java.sql.Date -> OffsetDateTime 3-2'|'GMT+09:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneOffset.ofHours( 9))}
            'java.sql.Date -> OffsetDateTime 3-3'|'GMT+09:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneOffset.ofHours( 9))}

        //  caseName                    |timeZoneId |before    HH              mm            ss           SSS  offset         |before             yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneOffset
            'Time -> OffsetDateTime 1-1'|'GMT-09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{OffsetDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}
            'Time -> OffsetDateTime 1-2'|'GMT-09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneOffset.ofHours(-9))}
            'Time -> OffsetDateTime 1-3'|'GMT-09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{OffsetDateTime.of(1970,  1,  1, 23, 59, 59, 999_000_000, ZoneOffset.ofHours(-9))}
            'Time -> OffsetDateTime 2-1'|'GMT+00:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{OffsetDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}
            'Time -> OffsetDateTime 2-2'|'GMT+00:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneOffset.ofHours( 0))}
            'Time -> OffsetDateTime 2-3'|'GMT+00:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{OffsetDateTime.of(1970,  1,  1, 23, 59, 59, 999_000_000, ZoneOffset.ofHours( 0))}
            'Time -> OffsetDateTime 3-1'|'GMT+09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{OffsetDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}
            'Time -> OffsetDateTime 3-2'|'GMT+09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneOffset.ofHours( 9))}
            'Time -> OffsetDateTime 3-3'|'GMT+09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{OffsetDateTime.of(1970,  1,  1, 23, 59, 59, 999_000_000, ZoneOffset.ofHours( 9))}

        //  caseName                         |timeZoneId |before                 dd               HH              mm            ss          offset                  nano seconds           |before             yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneOffset
            'Timestamp -> OffsetDateTime 1-1'|'GMT-09:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +9L*3600_000L); t.nanos =           0; return t}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}
            'Timestamp -> OffsetDateTime 1-2'|'GMT-09:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +9L*3600_000L); t.nanos = 789_012_345; return t}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours(-9))}
            'Timestamp -> OffsetDateTime 1-3'|'GMT-09:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +9L*3600_000L); t.nanos = 999_999_999; return t}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours(-9))}
            'Timestamp -> OffsetDateTime 2-1'|'GMT+00:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +0L*3600_000L); t.nanos =           0; return t}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}
            'Timestamp -> OffsetDateTime 2-2'|'GMT+00:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +0L*3600_000L); t.nanos = 789_012_345; return t}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 0))}
            'Timestamp -> OffsetDateTime 2-3'|'GMT+00:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +0L*3600_000L); t.nanos = 999_999_999; return t}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 0))}
            'Timestamp -> OffsetDateTime 3-1'|'GMT+09:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L -9L*3600_000L); t.nanos =           0; return t}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}
            'Timestamp -> OffsetDateTime 3-2'|'GMT+09:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L -9L*3600_000L); t.nanos = 789_012_345; return t}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 9))}
            'Timestamp -> OffsetDateTime 3-3'|'GMT+09:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L -9L*3600_000L); t.nanos = 999_999_999; return t}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 9))}

        //  caseName                         |timeZoneId |expected      yyyy  MM  dd  |expected           yyyy  MM  dd HH mm ss  nanoOfSecond
            'LocalDate -> OffsetDateTime 1-1'|'GMT-09:00'|{LocalDate.of(1969, 12, 31)}|{OffsetDateTime.of(1969, 12, 31, 0, 0, 0, 0, ZoneOffset.ofHours(-9))}
            'LocalDate -> OffsetDateTime 1-2'|'GMT-09:00'|{LocalDate.of(1970,  1,  1)}|{OffsetDateTime.of(1970,  1,  1, 0, 0, 0, 0, ZoneOffset.ofHours(-9))}
            'LocalDate -> OffsetDateTime 1-3'|'GMT-09:00'|{LocalDate.of(1970,  1,  2)}|{OffsetDateTime.of(1970,  1,  2, 0, 0, 0, 0, ZoneOffset.ofHours(-9))}
            'LocalDate -> OffsetDateTime 2-1'|'GMT+00:00'|{LocalDate.of(1969, 12, 31)}|{OffsetDateTime.of(1969, 12, 31, 0, 0, 0, 0, ZoneOffset.ofHours( 0))}
            'LocalDate -> OffsetDateTime 2-2'|'GMT+00:00'|{LocalDate.of(1970,  1,  1)}|{OffsetDateTime.of(1970,  1,  1, 0, 0, 0, 0, ZoneOffset.ofHours( 0))}
            'LocalDate -> OffsetDateTime 2-3'|'GMT+00:00'|{LocalDate.of(1970,  1,  2)}|{OffsetDateTime.of(1970,  1,  2, 0, 0, 0, 0, ZoneOffset.ofHours( 0))}
            'LocalDate -> OffsetDateTime 3-1'|'GMT+09:00'|{LocalDate.of(1969, 12, 31)}|{OffsetDateTime.of(1969, 12, 31, 0, 0, 0, 0, ZoneOffset.ofHours( 9))}
            'LocalDate -> OffsetDateTime 3-2'|'GMT+09:00'|{LocalDate.of(1970,  1,  1)}|{OffsetDateTime.of(1970,  1,  1, 0, 0, 0, 0, ZoneOffset.ofHours( 9))}
            'LocalDate -> OffsetDateTime 3-3'|'GMT+09:00'|{LocalDate.of(1970,  1,  2)}|{OffsetDateTime.of(1970,  1,  2, 0, 0, 0, 0, ZoneOffset.ofHours( 9))}

        //  caseName                         |timeZoneId |expected      HH  mm  ss  nanoOfSecond |before             yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'LocalTime -> OffsetDateTime 1-1'|'GMT-09:00'|{LocalTime.of( 0,  0,  0,           0)}|{OffsetDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}
            'LocalTime -> OffsetDateTime 1-2'|'GMT-09:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours(-9))}
            'LocalTime -> OffsetDateTime 1-3'|'GMT-09:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{OffsetDateTime.of(1970,  1,  1, 23, 59, 59, 999_999_999, ZoneOffset.ofHours(-9))}
            'LocalTime -> OffsetDateTime 2-1'|'GMT+00:00'|{LocalTime.of( 0,  0,  0,           0)}|{OffsetDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}
            'LocalTime -> OffsetDateTime 2-2'|'GMT+00:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 0))}
            'LocalTime -> OffsetDateTime 2-3'|'GMT+00:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{OffsetDateTime.of(1970,  1,  1, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 0))}
            'LocalTime -> OffsetDateTime 3-1'|'GMT+09:00'|{LocalTime.of( 0,  0,  0,           0)}|{OffsetDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}
            'LocalTime -> OffsetDateTime 3-2'|'GMT+09:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 9))}
            'LocalTime -> OffsetDateTime 3-3'|'GMT+09:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{OffsetDateTime.of(1970,  1,  1, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 9))}

        //  caseName                             |timeZoneId |expected          yyyy  MM  dd  HH  mm  ss  nanoOfSecond |before             yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneOffset
            'LocalDateTime -> OffsetDateTime 1-1'|'GMT-09:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}
            'LocalDateTime -> OffsetDateTime 1-2'|'GMT-09:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours(-9))}
            'LocalDateTime -> OffsetDateTime 1-3'|'GMT-09:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours(-9))}
            'LocalDateTime -> OffsetDateTime 2-1'|'GMT+00:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}
            'LocalDateTime -> OffsetDateTime 2-2'|'GMT+00:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 0))}
            'LocalDateTime -> OffsetDateTime 2-3'|'GMT+00:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 0))}
            'LocalDateTime -> OffsetDateTime 3-1'|'GMT+09:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}
            'LocalDateTime -> OffsetDateTime 3-2'|'GMT+09:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 9))}
            'LocalDateTime -> OffsetDateTime 3-3'|'GMT+09:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 9))}

        //  caseName                             |timeZoneId |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneId                  |before             yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneOffset
            'ZonedDateTime -> OffsetDateTime 1-1'|'GMT-09:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT-09:00'))}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}
            'ZonedDateTime -> OffsetDateTime 1-2'|'GMT-09:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT-09:00'))}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours(-9))}
            'ZonedDateTime -> OffsetDateTime 1-3'|'GMT-09:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT-09:00'))}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours(-9))}
            'ZonedDateTime -> OffsetDateTime 2-1'|'GMT+00:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+00:00'))}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}
            'ZonedDateTime -> OffsetDateTime 2-2'|'GMT+00:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+00:00'))}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 0))}
            'ZonedDateTime -> OffsetDateTime 2-3'|'GMT+00:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+00:00'))}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 0))}
            'ZonedDateTime -> OffsetDateTime 3-1'|'GMT+09:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+09:00'))}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}
            'ZonedDateTime -> OffsetDateTime 3-2'|'GMT+09:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+09:00'))}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 9))}
            'ZonedDateTime -> OffsetDateTime 3-3'|'GMT+09:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+09:00'))}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 9))}

        //  caseName                       |timeZoneId |before                 dd           HH          mm        ss  offset     nano seconds  |before             yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneOffset
            'Instant -> OffsetDateTime 1-1'|'GMT-09:00'|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L +9L*3600L,           0L)}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}
            'Instant -> OffsetDateTime 1-2'|'GMT-09:00'|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L +9L*3600L, 789_012_345L)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours(-9))}
            'Instant -> OffsetDateTime 1-3'|'GMT-09:00'|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L +9L*3600L, 999_999_999L)}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours(-9))}
            'Instant -> OffsetDateTime 2-1'|'GMT+00:00'|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L +0L*3600L,           0L)}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}
            'Instant -> OffsetDateTime 2-2'|'GMT+00:00'|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L +0L*3600L, 789_012_345L)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 0))}
            'Instant -> OffsetDateTime 2-3'|'GMT+00:00'|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L +0L*3600L, 999_999_999L)}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 0))}
            'Instant -> OffsetDateTime 3-1'|'GMT+09:00'|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L -9L*3600L,           0L)}|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}
            'Instant -> OffsetDateTime 3-2'|'GMT+09:00'|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L -9L*3600L, 789_012_345L)}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 9))}
            'Instant -> OffsetDateTime 3-3'|'GMT+09:00'|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L -9L*3600L, 999_999_999L)}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 9))}

        //  caseName                   |timeZoneId |before dd         HH              mm            ss           SSS   offset       |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneId
            'Long -> ZonedDateTime 1-1'|'GMT-09:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT-09:00'))}
            'Long -> ZonedDateTime 1-2'|'GMT-09:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneId.of('GMT-09:00'))}
            'Long -> ZonedDateTime 1-3'|'GMT-09:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneId.of('GMT-09:00'))}
            'Long -> ZonedDateTime 2-1'|'GMT+00:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+00:00'))}
            'Long -> ZonedDateTime 2-2'|'GMT+00:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneId.of('GMT+00:00'))}
            'Long -> ZonedDateTime 2-3'|'GMT+00:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneId.of('GMT+00:00'))}
            'Long -> ZonedDateTime 3-1'|'GMT+09:00'|{-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+09:00'))}
            'Long -> ZonedDateTime 3-2'|'GMT+09:00'|{ 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneId.of('GMT+09:00'))}
            'Long -> ZonedDateTime 3-3'|'GMT+09:00'|{ 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneId.of('GMT+09:00'))}

        //  caseName                             |timeZoneId |before              dd               HH              mm            ss           SSS  offset         |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneId
            'java.util.Date -> ZonedDateTime 1-1'|'GMT-09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT-09:00'))}
            'java.util.Date -> ZonedDateTime 1-2'|'GMT-09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneId.of('GMT-09:00'))}
            'java.util.Date -> ZonedDateTime 1-3'|'GMT-09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneId.of('GMT-09:00'))}
            'java.util.Date -> ZonedDateTime 2-1'|'GMT+00:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+00:00'))}
            'java.util.Date -> ZonedDateTime 2-2'|'GMT+00:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneId.of('GMT+00:00'))}
            'java.util.Date -> ZonedDateTime 2-3'|'GMT+00:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneId.of('GMT+00:00'))}
            'java.util.Date -> ZonedDateTime 3-1'|'GMT+09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+09:00'))}
            'java.util.Date -> ZonedDateTime 3-2'|'GMT+09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneId.of('GMT+09:00'))}
            'java.util.Date -> ZonedDateTime 3-3'|'GMT+09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneId.of('GMT+09:00'))}

        //  caseName                            |timeZoneId |before    dd               HH              mm            ss           SSS  offset         |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneId
            'java.sql.Date -> ZonedDateTime 1-1'|'GMT-09:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT-09:00'))}
            'java.sql.Date -> ZonedDateTime 1-2'|'GMT-09:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneId.of('GMT-09:00'))}
            'java.sql.Date -> ZonedDateTime 1-3'|'GMT-09:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneId.of('GMT-09:00'))}
            'java.sql.Date -> ZonedDateTime 2-1'|'GMT+00:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+00:00'))}
            'java.sql.Date -> ZonedDateTime 2-2'|'GMT+00:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneId.of('GMT+00:00'))}
            'java.sql.Date -> ZonedDateTime 2-3'|'GMT+00:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneId.of('GMT+00:00'))}
            'java.sql.Date -> ZonedDateTime 3-1'|'GMT+09:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+09:00'))}
            'java.sql.Date -> ZonedDateTime 3-2'|'GMT+09:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneId.of('GMT+09:00'))}
            'java.sql.Date -> ZonedDateTime 3-3'|'GMT+09:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000, ZoneId.of('GMT+09:00'))}

        //  caseName                   |timeZoneId |before    HH              mm            ss           SSS  offset         |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneId
            'Time -> ZonedDateTime 1-1'|'GMT-09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{ZonedDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneId.of('GMT-09:00'))}
            'Time -> ZonedDateTime 1-2'|'GMT-09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneId.of('GMT-09:00'))}
            'Time -> ZonedDateTime 1-3'|'GMT-09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{ZonedDateTime.of(1970,  1,  1, 23, 59, 59, 999_000_000, ZoneId.of('GMT-09:00'))}
            'Time -> ZonedDateTime 2-1'|'GMT+00:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{ZonedDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneId.of('GMT+00:00'))}
            'Time -> ZonedDateTime 2-2'|'GMT+00:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneId.of('GMT+00:00'))}
            'Time -> ZonedDateTime 2-3'|'GMT+00:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{ZonedDateTime.of(1970,  1,  1, 23, 59, 59, 999_000_000, ZoneId.of('GMT+00:00'))}
            'Time -> ZonedDateTime 3-1'|'GMT+09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{ZonedDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneId.of('GMT+09:00'))}
            'Time -> ZonedDateTime 3-2'|'GMT+09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000, ZoneId.of('GMT+09:00'))}
            'Time -> ZonedDateTime 3-3'|'GMT+09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{ZonedDateTime.of(1970,  1,  1, 23, 59, 59, 999_000_000, ZoneId.of('GMT+09:00'))}

        //  caseName                        |timeZoneId |before                 dd               HH              mm            ss          offset                  nano seconds           |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneId
            'Timestamp -> ZonedDateTime 1-1'|'GMT-09:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +9L*3600_000L); t.nanos =           0; return t}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT-09:00'))}
            'Timestamp -> ZonedDateTime 1-2'|'GMT-09:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +9L*3600_000L); t.nanos = 789_012_345; return t}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT-09:00'))}
            'Timestamp -> ZonedDateTime 1-3'|'GMT-09:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +9L*3600_000L); t.nanos = 999_999_999; return t}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT-09:00'))}
            'Timestamp -> ZonedDateTime 2-1'|'GMT+00:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +0L*3600_000L); t.nanos =           0; return t}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+00:00'))}
            'Timestamp -> ZonedDateTime 2-2'|'GMT+00:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +0L*3600_000L); t.nanos = 789_012_345; return t}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+00:00'))}
            'Timestamp -> ZonedDateTime 2-3'|'GMT+00:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +0L*3600_000L); t.nanos = 999_999_999; return t}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+00:00'))}
            'Timestamp -> ZonedDateTime 3-1'|'GMT+09:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L -9L*3600_000L); t.nanos =           0; return t}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+09:00'))}
            'Timestamp -> ZonedDateTime 3-2'|'GMT+09:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L -9L*3600_000L); t.nanos = 789_012_345; return t}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+09:00'))}
            'Timestamp -> ZonedDateTime 3-3'|'GMT+09:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L -9L*3600_000L); t.nanos = 999_999_999; return t}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+09:00'))}

        //  caseName                        |timeZoneId |expected      yyyy  MM  dd  |expected           yyyy  MM  dd HH mm ss  nanoOfSecond
            'LocalDate -> ZonedDateTime 1-1'|'GMT-09:00'|{LocalDate.of(1969, 12, 31)}|{ZonedDateTime.of(1969, 12, 31, 0, 0, 0, 0, ZoneId.of('GMT-09:00'))}
            'LocalDate -> ZonedDateTime 1-2'|'GMT-09:00'|{LocalDate.of(1970,  1,  1)}|{ZonedDateTime.of(1970,  1,  1, 0, 0, 0, 0, ZoneId.of('GMT-09:00'))}
            'LocalDate -> ZonedDateTime 1-3'|'GMT-09:00'|{LocalDate.of(1970,  1,  2)}|{ZonedDateTime.of(1970,  1,  2, 0, 0, 0, 0, ZoneId.of('GMT-09:00'))}
            'LocalDate -> ZonedDateTime 2-1'|'GMT+00:00'|{LocalDate.of(1969, 12, 31)}|{ZonedDateTime.of(1969, 12, 31, 0, 0, 0, 0, ZoneId.of('GMT+00:00'))}
            'LocalDate -> ZonedDateTime 2-2'|'GMT+00:00'|{LocalDate.of(1970,  1,  1)}|{ZonedDateTime.of(1970,  1,  1, 0, 0, 0, 0, ZoneId.of('GMT+00:00'))}
            'LocalDate -> ZonedDateTime 2-3'|'GMT+00:00'|{LocalDate.of(1970,  1,  2)}|{ZonedDateTime.of(1970,  1,  2, 0, 0, 0, 0, ZoneId.of('GMT+00:00'))}
            'LocalDate -> ZonedDateTime 3-1'|'GMT+09:00'|{LocalDate.of(1969, 12, 31)}|{ZonedDateTime.of(1969, 12, 31, 0, 0, 0, 0, ZoneId.of('GMT+09:00'))}
            'LocalDate -> ZonedDateTime 3-2'|'GMT+09:00'|{LocalDate.of(1970,  1,  1)}|{ZonedDateTime.of(1970,  1,  1, 0, 0, 0, 0, ZoneId.of('GMT+09:00'))}
            'LocalDate -> ZonedDateTime 3-3'|'GMT+09:00'|{LocalDate.of(1970,  1,  2)}|{ZonedDateTime.of(1970,  1,  2, 0, 0, 0, 0, ZoneId.of('GMT+09:00'))}

        //  caseName                        |timeZoneId |expected      HH  mm  ss  nanoOfSecond |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'LocalTime -> ZonedDateTime 1-1'|'GMT-09:00'|{LocalTime.of( 0,  0,  0,           0)}|{ZonedDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneId.of('GMT-09:00'))}
            'LocalTime -> ZonedDateTime 1-2'|'GMT-09:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT-09:00'))}
            'LocalTime -> ZonedDateTime 1-3'|'GMT-09:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{ZonedDateTime.of(1970,  1,  1, 23, 59, 59, 999_999_999, ZoneId.of('GMT-09:00'))}
            'LocalTime -> ZonedDateTime 2-1'|'GMT+00:00'|{LocalTime.of( 0,  0,  0,           0)}|{ZonedDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneId.of('GMT+00:00'))}
            'LocalTime -> ZonedDateTime 2-2'|'GMT+00:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+00:00'))}
            'LocalTime -> ZonedDateTime 2-3'|'GMT+00:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{ZonedDateTime.of(1970,  1,  1, 23, 59, 59, 999_999_999, ZoneId.of('GMT+00:00'))}
            'LocalTime -> ZonedDateTime 3-1'|'GMT+09:00'|{LocalTime.of( 0,  0,  0,           0)}|{ZonedDateTime.of(1970,  1,  1,  0,  0,  0,           0, ZoneId.of('GMT+09:00'))}
            'LocalTime -> ZonedDateTime 3-2'|'GMT+09:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+09:00'))}
            'LocalTime -> ZonedDateTime 3-3'|'GMT+09:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{ZonedDateTime.of(1970,  1,  1, 23, 59, 59, 999_999_999, ZoneId.of('GMT+09:00'))}

        //  caseName                            |timeZoneId |expected          yyyy  MM  dd  HH  mm  ss  nanoOfSecond |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneId
            'LocalDateTime -> ZonedDateTime 1-1'|'GMT-09:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT-09:00'))}
            'LocalDateTime -> ZonedDateTime 1-2'|'GMT-09:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT-09:00'))}
            'LocalDateTime -> ZonedDateTime 1-3'|'GMT-09:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT-09:00'))}
            'LocalDateTime -> ZonedDateTime 2-1'|'GMT+00:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+00:00'))}
            'LocalDateTime -> ZonedDateTime 2-2'|'GMT+00:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+00:00'))}
            'LocalDateTime -> ZonedDateTime 2-3'|'GMT+00:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+00:00'))}
            'LocalDateTime -> ZonedDateTime 3-1'|'GMT+09:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+09:00'))}
            'LocalDateTime -> ZonedDateTime 3-2'|'GMT+09:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+09:00'))}
            'LocalDateTime -> ZonedDateTime 3-3'|'GMT+09:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+09:00'))}

        //  caseName                             |timeZoneId |before             yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneOffset              |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneId
            'OffsetDateTime -> ZonedDateTime 1-1'|'GMT-09:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}
            'OffsetDateTime -> ZonedDateTime 1-2'|'GMT-09:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours(-9))}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours(-9))}
            'OffsetDateTime -> ZonedDateTime 1-3'|'GMT-09:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours(-9))}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours(-9))}
            'OffsetDateTime -> ZonedDateTime 2-1'|'GMT+00:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}
            'OffsetDateTime -> ZonedDateTime 2-2'|'GMT+00:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 0))}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 0))}
            'OffsetDateTime -> ZonedDateTime 2-3'|'GMT+00:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 0))}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 0))}
            'OffsetDateTime -> ZonedDateTime 3-1'|'GMT+09:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}
            'OffsetDateTime -> ZonedDateTime 3-2'|'GMT+09:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 9))}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 9))}
            'OffsetDateTime -> ZonedDateTime 3-3'|'GMT+09:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 9))}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 9))}

        //  caseName                      |timeZoneId |before                 dd           HH          mm        ss  offset     nano seconds  |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneId
            'Instant -> ZonedDateTime 1-1'|'GMT-09:00'|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L +9L*3600L,           0L)}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT-09:00'))}
            'Instant -> ZonedDateTime 1-2'|'GMT-09:00'|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L +9L*3600L, 789_012_345L)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT-09:00'))}
            'Instant -> ZonedDateTime 1-3'|'GMT-09:00'|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L +9L*3600L, 999_999_999L)}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT-09:00'))}
            'Instant -> ZonedDateTime 2-1'|'GMT+00:00'|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L +0L*3600L,           0L)}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+00:00'))}
            'Instant -> ZonedDateTime 2-2'|'GMT+00:00'|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L +0L*3600L, 789_012_345L)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+00:00'))}
            'Instant -> ZonedDateTime 2-3'|'GMT+00:00'|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L +0L*3600L, 999_999_999L)}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+00:00'))}
            'Instant -> ZonedDateTime 3-1'|'GMT+09:00'|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L -9L*3600L,           0L)}|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+09:00'))}
            'Instant -> ZonedDateTime 3-2'|'GMT+09:00'|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L -9L*3600L, 789_012_345L)}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+09:00'))}
            'Instant -> ZonedDateTime 3-3'|'GMT+09:00'|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L -9L*3600L, 999_999_999L)}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+09:00'))}

        //    caseName             |timeZoneId |before                |expected
            'Long -> Instant 1-1'|'GMT-09:00'|{                  0L}|{Instant.EPOCH                             }
            'Long -> Instant 1-2'|'GMT-09:00'|{                  0L}|{Instant.ofEpochMilli(                  0L)}
            'Long -> Instant 1-3'|'GMT-09:00'|{               -999L}|{Instant.ofEpochMilli(               -999L)}
            'Long -> Instant 1-4'|'GMT-09:00'|{                999L}|{Instant.ofEpochMilli(                999L)}
            'Long -> Instant 1-5'|'GMT-09:00'|{-99_999_999_999_999L}|{Instant.ofEpochMilli(-99_999_999_999_999L)}
            'Long -> Instant 1-6'|'GMT-09:00'|{ 99_999_999_999_999L}|{Instant.ofEpochMilli( 99_999_999_999_999L)}
            'Long -> Instant 2-1'|'GMT+00:00'|{                  0L}|{Instant.EPOCH                             }
            'Long -> Instant 2-2'|'GMT+00:00'|{                  0L}|{Instant.ofEpochMilli(                  0L)}
            'Long -> Instant 2-3'|'GMT+00:00'|{               -999L}|{Instant.ofEpochMilli(               -999L)}
            'Long -> Instant 2-4'|'GMT+00:00'|{                999L}|{Instant.ofEpochMilli(                999L)}
            'Long -> Instant 2-5'|'GMT+00:00'|{-99_999_999_999_999L}|{Instant.ofEpochMilli(-99_999_999_999_999L)}
            'Long -> Instant 2-6'|'GMT+00:00'|{ 99_999_999_999_999L}|{Instant.ofEpochMilli( 99_999_999_999_999L)}
            'Long -> Instant 3-1'|'GMT+09:00'|{                  0L}|{Instant.EPOCH                             }
            'Long -> Instant 3-2'|'GMT+09:00'|{                  0L}|{Instant.ofEpochMilli(                  0L)}
            'Long -> Instant 3-3'|'GMT+09:00'|{               -999L}|{Instant.ofEpochMilli(               -999L)}
            'Long -> Instant 3-4'|'GMT+09:00'|{                999L}|{Instant.ofEpochMilli(                999L)}
            'Long -> Instant 3-5'|'GMT+09:00'|{-99_999_999_999_999L}|{Instant.ofEpochMilli(-99_999_999_999_999L)}
            'Long -> Instant 3-6'|'GMT+09:00'|{ 99_999_999_999_999L}|{Instant.ofEpochMilli( 99_999_999_999_999L)}

        //  caseName                       |timeZoneId |before              dd               HH              mm            ss           SSS  offset         |expected          yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'java.util.Date -> Instant 1-1'|'GMT-09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'java.util.Date -> Instant 1-2'|'GMT-09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'java.util.Date -> Instant 1-3'|'GMT-09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000)}
            'java.util.Date -> Instant 2-1'|'GMT+00:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'java.util.Date -> Instant 2-2'|'GMT+00:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'java.util.Date -> Instant 2-3'|'GMT+00:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000)}
            'java.util.Date -> Instant 3-1'|'GMT+09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'java.util.Date -> Instant 3-2'|'GMT+09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'java.util.Date -> Instant 3-3'|'GMT+09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000)}

        //  caseName                      |timeZoneId |before    dd               HH              mm            ss           SSS  offset         |expected          yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'java.sql.Date -> Instant 1-1'|'GMT-09:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'java.sql.Date -> Instant 1-2'|'GMT-09:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'java.sql.Date -> Instant 1-3'|'GMT-09:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000)}
            'java.sql.Date -> Instant 2-1'|'GMT+00:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'java.sql.Date -> Instant 2-2'|'GMT+00:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'java.sql.Date -> Instant 2-3'|'GMT+00:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000)}
            'java.sql.Date -> Instant 3-1'|'GMT+09:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'java.sql.Date -> Instant 3-2'|'GMT+09:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_000_000)}
            'java.sql.Date -> Instant 3-3'|'GMT+09:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_000_000)}

        //  caseName             |timeZoneId |before    HH              mm            ss           SSS   |expected              HH              mm            ss           SSS
            'Time -> Instant 1-1'|'GMT-09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L)}|{Instant.ofEpochMilli( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L)}
            'Time -> Instant 1-2'|'GMT-09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L)}|{Instant.ofEpochMilli(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L)}
            'Time -> Instant 1-3'|'GMT-09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L)}|{Instant.ofEpochMilli(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L)}
            'Time -> Instant 2-1'|'GMT+00:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L)}|{Instant.ofEpochMilli( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L)}
            'Time -> Instant 2-2'|'GMT+00:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L)}|{Instant.ofEpochMilli(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L)}
            'Time -> Instant 2-3'|'GMT+00:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L)}|{Instant.ofEpochMilli(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L)}
            'Time -> Instant 3-1'|'GMT+09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L)}|{Instant.ofEpochMilli( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L)}
            'Time -> Instant 3-2'|'GMT+09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L)}|{Instant.ofEpochMilli(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L)}
            'Time -> Instant 3-3'|'GMT+09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L)}|{Instant.ofEpochMilli(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L)}

        //  caseName                  |timeZoneId |before                 dd               HH              mm            ss                    nano seconds           |expected               dd           HH          mm        ss   nano seconds
            'Timestamp -> Instant 1-1'|'GMT-09:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L); t.nanos =           0; return t}|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L,           0L)}
            'Timestamp -> Instant 1-2'|'GMT-09:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L); t.nanos = 789_012_345; return t}|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L, 789_012_345L)}
            'Timestamp -> Instant 1-3'|'GMT-09:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L); t.nanos = 999_999_999; return t}|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L, 999_999_999L)}
            'Timestamp -> Instant 2-1'|'GMT+00:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L); t.nanos =           0; return t}|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L,           0L)}
            'Timestamp -> Instant 2-2'|'GMT+00:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L); t.nanos = 789_012_345; return t}|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L, 789_012_345L)}
            'Timestamp -> Instant 2-3'|'GMT+00:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L); t.nanos = 999_999_999; return t}|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L, 999_999_999L)}
            'Timestamp -> Instant 3-1'|'GMT+09:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L); t.nanos =           0; return t}|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L,           0L)}
            'Timestamp -> Instant 3-2'|'GMT+09:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L); t.nanos = 789_012_345; return t}|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L, 789_012_345L)}
            'Timestamp -> Instant 3-3'|'GMT+09:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L); t.nanos = 999_999_999; return t}|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L, 999_999_999L)}

        //  caseName                  |timeZoneId |before        yyyy  MM  dd  |expected               dd          offset
            'LocalDate -> Instant 1-1'|'GMT-09:00'|{LocalDate.of(1969, 12, 31)}|{Instant.ofEpochSecond(-1L*86400L +9L*3600L)}
            'LocalDate -> Instant 1-2'|'GMT-09:00'|{LocalDate.of(1970,  1,  1)}|{Instant.ofEpochSecond( 0L*86400L +9L*3600L)}
            'LocalDate -> Instant 1-3'|'GMT-09:00'|{LocalDate.of(1970,  1,  2)}|{Instant.ofEpochSecond( 1L*86400L +9L*3600L)}
            'LocalDate -> Instant 2-1'|'GMT+00:00'|{LocalDate.of(1969, 12, 31)}|{Instant.ofEpochSecond(-1L*86400L +0L*3600L)}
            'LocalDate -> Instant 2-2'|'GMT+00:00'|{LocalDate.of(1970,  1,  1)}|{Instant.ofEpochSecond( 0L*86400L +0L*3600L)}
            'LocalDate -> Instant 2-3'|'GMT+00:00'|{LocalDate.of(1970,  1,  2)}|{Instant.ofEpochSecond( 1L*86400L +0L*3600L)}
            'LocalDate -> Instant 3-1'|'GMT+09:00'|{LocalDate.of(1969, 12, 31)}|{Instant.ofEpochSecond(-1L*86400L -9L*3600L)}
            'LocalDate -> Instant 3-2'|'GMT+09:00'|{LocalDate.of(1970,  1,  1)}|{Instant.ofEpochSecond( 0L*86400L -9L*3600L)}
            'LocalDate -> Instant 3-3'|'GMT+09:00'|{LocalDate.of(1970,  1,  2)}|{Instant.ofEpochSecond( 1L*86400L -9L*3600L)}

        //  caseName                  |timeZoneId |before        HH  mm  ss  nanoOfSecond |expected               HH          mm        ss   offset    nano seconds
            'LocalTime -> Instant 1-1'|'GMT-09:00'|{LocalTime.of( 0,  0,  0,           0)}|{Instant.ofEpochSecond( 0L*3600L +  0L*60L +  0L +9L*3600L,           0L)}
            'LocalTime -> Instant 1-2'|'GMT-09:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{Instant.ofEpochSecond(12L*3600L + 34L*60L + 56L +9L*3600L, 789_012_345L)}
            'LocalTime -> Instant 1-3'|'GMT-09:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{Instant.ofEpochSecond(23L*3600L + 59L*60L + 59L +9L*3600L, 999_999_999L)}
            'LocalTime -> Instant 2-1'|'GMT+00:00'|{LocalTime.of( 0,  0,  0,           0)}|{Instant.ofEpochSecond( 0L*3600L +  0L*60L +  0L +0L*3600L,           0L)}
            'LocalTime -> Instant 2-2'|'GMT+00:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{Instant.ofEpochSecond(12L*3600L + 34L*60L + 56L +0L*3600L, 789_012_345L)}
            'LocalTime -> Instant 2-3'|'GMT+00:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{Instant.ofEpochSecond(23L*3600L + 59L*60L + 59L +0L*3600L, 999_999_999L)}
            'LocalTime -> Instant 3-1'|'GMT+09:00'|{LocalTime.of( 0,  0,  0,           0)}|{Instant.ofEpochSecond( 0L*3600L +  0L*60L +  0L -9L*3600L,           0L)}
            'LocalTime -> Instant 3-2'|'GMT+09:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{Instant.ofEpochSecond(12L*3600L + 34L*60L + 56L -9L*3600L, 789_012_345L)}
            'LocalTime -> Instant 3-3'|'GMT+09:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{Instant.ofEpochSecond(23L*3600L + 59L*60L + 59L -9L*3600L, 999_999_999L)}

        //  caseName                      |timeZoneId |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond |expected               dd           HH          mm        ss   offset    nano seconds
            'LocalDateTime -> Instant 1-1'|'GMT-09:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L +9L*3600L,           0L)}
            'LocalDateTime -> Instant 1-2'|'GMT-09:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L +9L*3600L, 789_012_345L)}
            'LocalDateTime -> Instant 1-3'|'GMT-09:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L +9L*3600L, 999_999_999L)}
            'LocalDateTime -> Instant 2-1'|'GMT+00:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L +0L*3600L,           0L)}
            'LocalDateTime -> Instant 2-2'|'GMT+00:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L +0L*3600L, 789_012_345L)}
            'LocalDateTime -> Instant 2-3'|'GMT+00:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L +0L*3600L, 999_999_999L)}
            'LocalDateTime -> Instant 3-1'|'GMT+09:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L -9L*3600L,           0L)}
            'LocalDateTime -> Instant 3-2'|'GMT+09:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L -9L*3600L, 789_012_345L)}
            'LocalDateTime -> Instant 3-3'|'GMT+09:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L -9L*3600L, 999_999_999L)}

        //  caseName                       |timeZoneId |before             yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneOffset              |expected               dd           HH          mm        ss   offset    nano seconds
            'OffsetDateTime -> Instant 1-1'|'GMT-09:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L +9L*3600L,           0L)}
            'OffsetDateTime -> Instant 1-2'|'GMT-09:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours(-9))}|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L +9L*3600L, 789_012_345L)}
            'OffsetDateTime -> Instant 1-3'|'GMT-09:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours(-9))}|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L +9L*3600L, 999_999_999L)}
            'OffsetDateTime -> Instant 2-1'|'GMT+00:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L +0L*3600L,           0L)}
            'OffsetDateTime -> Instant 2-2'|'GMT+00:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 0))}|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L +0L*3600L, 789_012_345L)}
            'OffsetDateTime -> Instant 2-3'|'GMT+00:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 0))}|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L +0L*3600L, 999_999_999L)}
            'OffsetDateTime -> Instant 3-1'|'GMT+09:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L -9L*3600L,           0L)}
            'OffsetDateTime -> Instant 3-2'|'GMT+09:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 9))}|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L -9L*3600L, 789_012_345L)}
            'OffsetDateTime -> Instant 3-3'|'GMT+09:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 9))}|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L -9L*3600L, 999_999_999L)}

        //  caseName                      |timeZoneId |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneId                  |expected               dd           HH          mm        ss   offset    nano seconds
            'ZonedDateTime -> Instant 1-1'|'GMT-09:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT-09:00'))}|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L +9L*3600L,           0L)}
            'ZonedDateTime -> Instant 1-2'|'GMT-09:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT-09:00'))}|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L +9L*3600L, 789_012_345L)}
            'ZonedDateTime -> Instant 1-3'|'GMT-09:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT-09:00'))}|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L +9L*3600L, 999_999_999L)}
            'ZonedDateTime -> Instant 2-1'|'GMT+00:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+00:00'))}|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L +0L*3600L,           0L)}
            'ZonedDateTime -> Instant 2-2'|'GMT+00:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+00:00'))}|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L +0L*3600L, 789_012_345L)}
            'ZonedDateTime -> Instant 2-3'|'GMT+00:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+00:00'))}|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L +0L*3600L, 999_999_999L)}
            'ZonedDateTime -> Instant 3-1'|'GMT+09:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+09:00'))}|{Instant.ofEpochSecond(-1L*86400L +  0L*3600L +  0L*60L +  0L -9L*3600L,           0L)}
            'ZonedDateTime -> Instant 3-2'|'GMT+09:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+09:00'))}|{Instant.ofEpochSecond( 0L*86400L + 12L*3600L + 34L*60L + 56L -9L*3600L, 789_012_345L)}
            'ZonedDateTime -> Instant 3-3'|'GMT+09:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+09:00'))}|{Instant.ofEpochSecond( 1L*86400L + 23L*3600L + 59L*60L + 59L -9L*3600L, 999_999_999L)}

        //  caseName                 |timeZoneId |before                           |before                 dd               HH              mm            ss          offset                  nano seconds
            'String -> Timestanp 1-1'|'GMT-09:00'|{'1969-12-31 00:00:00'          }|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +9L*3600_000L); t.nanos =           0; return t}
            'String -> Timestanp 1-2'|'GMT-09:00'|{'1970-01-01 12:34:56.789012345'}|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +9L*3600_000L); t.nanos = 789_012_345; return t}
            'String -> Timestanp 1-3'|'GMT-09:00'|{'1970-01-02 23:59:59.999999999'}|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +9L*3600_000L); t.nanos = 999_999_999; return t}
            'String -> Timestanp 2-1'|'GMT+00:00'|{'1969-12-31 00:00:00'          }|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +0L*3600_000L); t.nanos =           0; return t}
            'String -> Timestanp 2-2'|'GMT+00:00'|{'1970-01-01 12:34:56.789012345'}|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +0L*3600_000L); t.nanos = 789_012_345; return t}
            'String -> Timestanp 2-3'|'GMT+00:00'|{'1970-01-02 23:59:59.999999999'}|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +0L*3600_000L); t.nanos = 999_999_999; return t}
            'String -> Timestanp 3-1'|'GMT+09:00'|{'1969-12-31 00:00:00'          }|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L -9L*3600_000L); t.nanos =           0; return t}
            'String -> Timestanp 3-2'|'GMT+09:00'|{'1970-01-01 12:34:56.789012345'}|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L -9L*3600_000L); t.nanos = 789_012_345; return t}
            'String -> Timestanp 3-3'|'GMT+09:00'|{'1970-01-02 23:59:59.999999999'}|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L -9L*3600_000L); t.nanos = 999_999_999; return t}

        //  caseName                 |timeZoneId |before        |expected      yyyy  MM  dd
            'String -> LocalDate 1-1'|'GMT-09:00'|{'1969-12-31'}|{LocalDate.of(1969, 12, 31)}
            'String -> LocalDate 1-2'|'GMT-09:00'|{'1970-01-01'}|{LocalDate.of(1970,  1,  1)}
            'String -> LocalDate 1-3'|'GMT-09:00'|{'1970-01-02'}|{LocalDate.of(1970,  1,  2)}
            'String -> LocalDate 2-1'|'GMT+00:00'|{'1969-12-31'}|{LocalDate.of(1969, 12, 31)}
            'String -> LocalDate 2-2'|'GMT+00:00'|{'1970-01-01'}|{LocalDate.of(1970,  1,  1)}
            'String -> LocalDate 2-3'|'GMT+00:00'|{'1970-01-02'}|{LocalDate.of(1970,  1,  2)}
            'String -> LocalDate 3-1'|'GMT+09:00'|{'1969-12-31'}|{LocalDate.of(1969, 12, 31)}
            'String -> LocalDate 3-2'|'GMT+09:00'|{'1970-01-01'}|{LocalDate.of(1970,  1,  1)}
            'String -> LocalDate 3-3'|'GMT+09:00'|{'1970-01-02'}|{LocalDate.of(1970,  1,  2)}

        //  caseName                     |timeZoneId |before                           |expected          yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'String -> LocalDateTime 1-1'|'GMT-09:00'|{'1969-12-31 00:00:00'          }|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'String -> LocalDateTime 1-2'|'GMT-09:00'|{'1970-01-01 12:34:56.789012345'}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'String -> LocalDateTime 1-3'|'GMT-09:00'|{'1970-01-02 23:59:59.999999999'}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}
            'String -> LocalDateTime 2-1'|'GMT+00:00'|{'1969-12-31 00:00:00'          }|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'String -> LocalDateTime 2-2'|'GMT+00:00'|{'1970-01-01 12:34:56.789012345'}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'String -> LocalDateTime 2-3'|'GMT+00:00'|{'1970-01-02 23:59:59.999999999'}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}
            'String -> LocalDateTime 3-1'|'GMT+09:00'|{'1969-12-31 00:00:00'          }|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}
            'String -> LocalDateTime 3-2'|'GMT+09:00'|{'1970-01-01 12:34:56.789012345'}|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}
            'String -> LocalDateTime 3-3'|'GMT+09:00'|{'1970-01-02 23:59:59.999999999'}|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}

        //  caseName                      |timeZoneId |before                                 |expected           yyyy  MM  dd  HH  mm  ss  nanoOfSecond
            'String -> OffsetDateTime 1-1'|'GMT-09:00'|{'1969-12-31 00:00:00-09:00'          }|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}
            'String -> OffsetDateTime 1-2'|'GMT-09:00'|{'1970-01-01 12:34:56.789012345-09:00'}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours(-9))}
            'String -> OffsetDateTime 1-3'|'GMT-09:00'|{'1970-01-02 23:59:59.999999999-09:00'}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours(-9))}
            'String -> OffsetDateTime 2-1'|'GMT+00:00'|{'1969-12-31 00:00:00+00:00'          }|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}
            'String -> OffsetDateTime 2-2'|'GMT+00:00'|{'1970-01-01 12:34:56.789012345+00:00'}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 0))}
            'String -> OffsetDateTime 2-3'|'GMT+00:00'|{'1970-01-02 23:59:59.999999999+00:00'}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 0))}
            'String -> OffsetDateTime 3-1'|'GMT+09:00'|{'1969-12-31 00:00:00+09:00'          }|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}
            'String -> OffsetDateTime 3-2'|'GMT+09:00'|{'1970-01-01 12:34:56.789012345+09:00'}|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 9))}
            'String -> OffsetDateTime 3-3'|'GMT+09:00'|{'1970-01-02 23:59:59.999999999+09:00'}|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 9))}

        //  caseName                     |timeZoneId |before                                 |expected          yyyy  MM  dd  HH  mm  ss  nanoOfSecond ZoneId
            'String -> ZonedDateTime 1-1'|'GMT-09:00'|{'1969-12-31 00:00:00-09:00'          }|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}
            'String -> ZonedDateTime 1-2'|'GMT-09:00'|{'1970-01-01 12:34:56.789012345-09:00'}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours(-9))}
            'String -> ZonedDateTime 1-3'|'GMT-09:00'|{'1970-01-02 23:59:59.999999999-09:00'}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours(-9))}
            'String -> ZonedDateTime 2-1'|'GMT+00:00'|{'1969-12-31 00:00:00+00:00'          }|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}
            'String -> ZonedDateTime 2-2'|'GMT+00:00'|{'1970-01-01 12:34:56.789012345+00:00'}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 0))}
            'String -> ZonedDateTime 2-3'|'GMT+00:00'|{'1970-01-02 23:59:59.999999999+00:00'}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 0))}
            'String -> ZonedDateTime 3-1'|'GMT+09:00'|{'1969-12-31 00:00:00+09:00'          }|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}
            'String -> ZonedDateTime 3-2'|'GMT+09:00'|{'1970-01-01 12:34:56.789012345+09:00'}|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 9))}
            'String -> ZonedDateTime 3-3'|'GMT+09:00'|{'1970-01-02 23:59:59.999999999+09:00'}|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 9))}

        //    caseName               |timeZoneId |before                           |expected
            'String -> Instant 1-1'|'GMT-09:00'|{'1969-12-31 15:00:00.000-09:00'}|{Instant.EPOCH              }
            'String -> Instant 1-3'|'GMT-09:00'|{'1969-12-31 14:59:59.001-09:00'}|{Instant.ofEpochMilli(-999L)}
            'String -> Instant 1-4'|'GMT-09:00'|{'1969-12-31 15:00:00.999-09:00'}|{Instant.ofEpochMilli( 999L)}
            'String -> Instant 2-1'|'GMT+00:00'|{'1970-01-01 00:00:00.000+00:00'}|{Instant.EPOCH              }
            'String -> Instant 2-3'|'GMT+00:00'|{'1969-12-31 23:59:59.001+00:00'}|{Instant.ofEpochMilli(-999L)}
            'String -> Instant 2-4'|'GMT+00:00'|{'1970-01-01 00:00:00.999+00:00'}|{Instant.ofEpochMilli( 999L)}
            'String -> Instant 3-1'|'GMT+09:00'|{'1970-01-01 09:00:00.000+09:00'}|{Instant.EPOCH              }
            'String -> Instant 3-3'|'GMT+09:00'|{'1970-01-01 08:59:59.001+09:00'}|{Instant.ofEpochMilli(-999L)}
            'String -> Instant 3-4'|'GMT+09:00'|{'1970-01-01 09:00:00.999+09:00'}|{Instant.ofEpochMilli( 999L)}

        //  caseName                      |timeZoneId |before              dd               HH              mm            ss           SSS  offset         |expected
            'java.util.Date -> String 1-1'|'GMT-09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{'1969-12-31'}
            'java.util.Date -> String 1-2'|'GMT-09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{'1970-01-01'}
            'java.util.Date -> String 1-3'|'GMT-09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{'1970-01-02'}
            'java.util.Date -> String 2-1'|'GMT+00:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{'1969-12-31'}
            'java.util.Date -> String 2-2'|'GMT+00:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{'1970-01-01'}
            'java.util.Date -> String 2-3'|'GMT+00:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{'1970-01-02'}
            'java.util.Date -> String 3-1'|'GMT+09:00'|{new java.util.Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{'1969-12-31'}
            'java.util.Date -> String 3-2'|'GMT+09:00'|{new java.util.Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{'1970-01-01'}
            'java.util.Date -> String 3-3'|'GMT+09:00'|{new java.util.Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{'1970-01-02'}

        //  caseName                     |timeZoneId |before    dd               HH              mm            ss           SSS  offset         |expected
            'java.sql.Date -> String 1-1'|'GMT-09:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{'1969-12-31'}
            'java.sql.Date -> String 1-2'|'GMT-09:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{'1970-01-01'}
            'java.sql.Date -> String 1-3'|'GMT-09:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{'1970-01-02'}
            'java.sql.Date -> String 2-1'|'GMT+00:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{'1969-12-31'}
            'java.sql.Date -> String 2-2'|'GMT+00:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{'1970-01-01'}
            'java.sql.Date -> String 2-3'|'GMT+00:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{'1970-01-02'}
            'java.sql.Date -> String 3-1'|'GMT+09:00'|{new Date(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{'1969-12-31'}
            'java.sql.Date -> String 3-2'|'GMT+09:00'|{new Date( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{'1970-01-01'}
            'java.sql.Date -> String 3-3'|'GMT+09:00'|{new Date( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{'1970-01-02'}

        //  caseName            |timeZoneId |before    HH              mm            ss           SSS  offset         |expected
            'Time -> String 1-1'|'GMT-09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +9L*3600_000L)}|{'00:00:00'    }
            'Time -> String 1-2'|'GMT-09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +9L*3600_000L)}|{'12:34:56.789'}
            'Time -> String 1-3'|'GMT-09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +9L*3600_000L)}|{'23:59:59.999'}
            'Time -> String 2-1'|'GMT+00:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L +0L*3600_000L)}|{'00:00:00'    }
            'Time -> String 2-2'|'GMT+00:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L +0L*3600_000L)}|{'12:34:56.789'}
            'Time -> String 2-3'|'GMT+00:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L +0L*3600_000L)}|{'23:59:59.999'}
            'Time -> String 3-1'|'GMT+09:00'|{new Time( 0L*3600_000L +  0L*60_000L +  0L*1_000L +   0L -9L*3600_000L)}|{'00:00:00'    }
            'Time -> String 3-2'|'GMT+09:00'|{new Time(12L*3600_000L + 34L*60_000L + 56L*1_000L + 789L -9L*3600_000L)}|{'12:34:56.789'}
            'Time -> String 3-3'|'GMT+09:00'|{new Time(23L*3600_000L + 59L*60_000L + 59L*1_000L + 999L -9L*3600_000L)}|{'23:59:59.999'}

        //  caseName                 |timeZoneId |before                 dd               HH              mm            ss         offset                    nano seconds          |expected
            'Timestamp -> String 1-1'|'GMT-09:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +9L*3600_000L); t.nanos =           0; return t}|{'1969-12-31 00:00:00'          }
            'Timestamp -> String 1-2'|'GMT-09:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +9L*3600_000L); t.nanos = 789_012_345; return t}|{'1970-01-01 12:34:56.789012345'}
            'Timestamp -> String 1-3'|'GMT-09:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +9L*3600_000L); t.nanos = 999_999_999; return t}|{'1970-01-02 23:59:59.999999999'}
            'Timestamp -> String 2-1'|'GMT+00:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L +0L*3600_000L); t.nanos =           0; return t}|{'1969-12-31 00:00:00'          }
            'Timestamp -> String 2-2'|'GMT+00:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L +0L*3600_000L); t.nanos = 789_012_345; return t}|{'1970-01-01 12:34:56.789012345'}
            'Timestamp -> String 2-3'|'GMT+00:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L +0L*3600_000L); t.nanos = 999_999_999; return t}|{'1970-01-02 23:59:59.999999999'}
            'Timestamp -> String 3-1'|'GMT+09:00'|{def t = new Timestamp(-1L*86400_000L +  0L*3600_000L +  0L*60_000L +  0L*1_000L -9L*3600_000L); t.nanos =           0; return t}|{'1969-12-31 00:00:00'          }
            'Timestamp -> String 3-2'|'GMT+09:00'|{def t = new Timestamp( 0L*86400_000L + 12L*3600_000L + 34L*60_000L + 56L*1_000L -9L*3600_000L); t.nanos = 789_012_345; return t}|{'1970-01-01 12:34:56.789012345'}
            'Timestamp -> String 3-3'|'GMT+09:00'|{def t = new Timestamp( 1L*86400_000L + 23L*3600_000L + 59L*60_000L + 59L*1_000L -9L*3600_000L); t.nanos = 999_999_999; return t}|{'1970-01-02 23:59:59.999999999'}

        //  caseName                 |timeZoneId |before        yyyy  MM  dd  |expected
            'LocalDate -> String 1-1'|'GMT-09:00'|{LocalDate.of(1969, 12, 31)}|{'1969-12-31'}
            'LocalDate -> String 1-2'|'GMT-09:00'|{LocalDate.of(1970,  1,  1)}|{'1970-01-01'}
            'LocalDate -> String 1-3'|'GMT-09:00'|{LocalDate.of(1970,  1,  2)}|{'1970-01-02'}
            'LocalDate -> String 2-1'|'GMT+00:00'|{LocalDate.of(1969, 12, 31)}|{'1969-12-31'}
            'LocalDate -> String 2-2'|'GMT+00:00'|{LocalDate.of(1970,  1,  1)}|{'1970-01-01'}
            'LocalDate -> String 2-3'|'GMT+00:00'|{LocalDate.of(1970,  1,  2)}|{'1970-01-02'}
            'LocalDate -> String 3-1'|'GMT+09:00'|{LocalDate.of(1969, 12, 31)}|{'1969-12-31'}
            'LocalDate -> String 3-2'|'GMT+09:00'|{LocalDate.of(1970,  1,  1)}|{'1970-01-01'}
            'LocalDate -> String 3-3'|'GMT+09:00'|{LocalDate.of(1970,  1,  2)}|{'1970-01-02'}

        //  caseName                 |timeZoneId |before        HH  mm  ss  nanoOfSecond |expected
            'LocalTime -> String 1-1'|'GMT-09:00'|{LocalTime.of( 0,  0,  0,           0)}|{'00:00:00'          }
            'LocalTime -> String 1-2'|'GMT-09:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{'12:34:56.789012345'}
            'LocalTime -> String 1-3'|'GMT-09:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{'23:59:59.999999999'}
            'LocalTime -> String 2-1'|'GMT+00:00'|{LocalTime.of( 0,  0,  0,           0)}|{'00:00:00'          }
            'LocalTime -> String 2-2'|'GMT+00:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{'12:34:56.789012345'}
            'LocalTime -> String 2-3'|'GMT+00:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{'23:59:59.999999999'}
            'LocalTime -> String 3-1'|'GMT+09:00'|{LocalTime.of( 0,  0,  0,           0)}|{'00:00:00'          }
            'LocalTime -> String 3-2'|'GMT+09:00'|{LocalTime.of(12, 34, 56, 789_012_345)}|{'12:34:56.789012345'}
            'LocalTime -> String 3-3'|'GMT+09:00'|{LocalTime.of(23, 59, 59, 999_999_999)}|{'23:59:59.999999999'}

        //  caseName                     |timeZoneId |before            yyyy  MM  dd  HH  mm  ss  nanoOfSecond |expected
            'LocalDateTime -> String 1-1'|'GMT-09:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{'1969-12-31 00:00:00'          }
            'LocalDateTime -> String 1-2'|'GMT-09:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{'1970-01-01 12:34:56.789012345'}
            'LocalDateTime -> String 1-3'|'GMT-09:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{'1970-01-02 23:59:59.999999999'}
            'LocalDateTime -> String 2-1'|'GMT+00:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{'1969-12-31 00:00:00'          }
            'LocalDateTime -> String 2-2'|'GMT+00:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{'1970-01-01 12:34:56.789012345'}
            'LocalDateTime -> String 2-3'|'GMT+00:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{'1970-01-02 23:59:59.999999999'}
            'LocalDateTime -> String 3-1'|'GMT+09:00'|{LocalDateTime.of(1969, 12, 31,  0,  0,  0,           0)}|{'1969-12-31 00:00:00'          }
            'LocalDateTime -> String 3-2'|'GMT+09:00'|{LocalDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345)}|{'1970-01-01 12:34:56.789012345'}
            'LocalDateTime -> String 3-3'|'GMT+09:00'|{LocalDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999)}|{'1970-01-02 23:59:59.999999999'}

        //  caseName                      |timeZoneId |before             yyyy  MM  dd  HH  mm  ss nanoOfSecond  ZoneOffset              |expected
            'OffsetDateTime -> String 1-1'|'GMT-09:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours(-9))}|{'1969-12-31 00:00:00-09:00'          }
            'OffsetDateTime -> String 1-2'|'GMT-09:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours(-9))}|{'1970-01-01 12:34:56.789012345-09:00'}
            'OffsetDateTime -> String 1-3'|'GMT-09:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours(-9))}|{'1970-01-02 23:59:59.999999999-09:00'}
            'OffsetDateTime -> String 2-1'|'GMT+00:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 0))}|{'1969-12-31 00:00:00+00:00'          }
            'OffsetDateTime -> String 2-2'|'GMT+00:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 0))}|{'1970-01-01 12:34:56.789012345+00:00'}
            'OffsetDateTime -> String 2-3'|'GMT+00:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 0))}|{'1970-01-02 23:59:59.999999999+00:00'}
            'OffsetDateTime -> String 3-1'|'GMT+09:00'|{OffsetDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneOffset.ofHours( 9))}|{'1969-12-31 00:00:00+09:00'          }
            'OffsetDateTime -> String 3-2'|'GMT+09:00'|{OffsetDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneOffset.ofHours( 9))}|{'1970-01-01 12:34:56.789012345+09:00'}
            'OffsetDateTime -> String 3-3'|'GMT+09:00'|{OffsetDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneOffset.ofHours( 9))}|{'1970-01-02 23:59:59.999999999+09:00'}

        //  caseName                     |timeZoneId |before            yyyy  MM  dd  HH  mm  ss nanoOfSecond  ZoneId                  |expected
            'ZonedDateTime -> String 1-1'|'GMT-09:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT-09:00'))}|{'1969-12-31 00:00:00 GMT-09:00'          }
            'ZonedDateTime -> String 1-2'|'GMT-09:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT-09:00'))}|{'1970-01-01 12:34:56.789012345 GMT-09:00'}
            'ZonedDateTime -> String 1-3'|'GMT-09:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT-09:00'))}|{'1970-01-02 23:59:59.999999999 GMT-09:00'}
            'ZonedDateTime -> String 2-1'|'GMT+00:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+00:00'))}|{'1969-12-31 00:00:00 GMT'          }
            'ZonedDateTime -> String 2-2'|'GMT+00:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+00:00'))}|{'1970-01-01 12:34:56.789012345 GMT'}
            'ZonedDateTime -> String 2-3'|'GMT+00:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+00:00'))}|{'1970-01-02 23:59:59.999999999 GMT'}
            'ZonedDateTime -> String 3-1'|'GMT+09:00'|{ZonedDateTime.of(1969, 12, 31,  0,  0,  0,           0, ZoneId.of('GMT+09:00'))}|{'1969-12-31 00:00:00 GMT+09:00'          }
            'ZonedDateTime -> String 3-2'|'GMT+09:00'|{ZonedDateTime.of(1970,  1,  1, 12, 34, 56, 789_012_345, ZoneId.of('GMT+09:00'))}|{'1970-01-01 12:34:56.789012345 GMT+09:00'}
            'ZonedDateTime -> String 3-3'|'GMT+09:00'|{ZonedDateTime.of(1970,  1,  2, 23, 59, 59, 999_999_999, ZoneId.of('GMT+09:00'))}|{'1970-01-02 23:59:59.999999999 GMT+09:00'}

        //    caseName               |timeZoneId |before                       |expected
            'Instant -> String 1-1'|'GMT-09:00'|{Instant.EPOCH              }|{'1969-12-31 15:00:00-09:00'    }
            'Instant -> String 1-2'|'GMT-09:00'|{Instant.ofEpochMilli(   0L)}|{'1969-12-31 15:00:00-09:00'    }
            'Instant -> String 1-3'|'GMT-09:00'|{Instant.ofEpochMilli(-999L)}|{'1969-12-31 14:59:59.001-09:00'}
            'Instant -> String 1-4'|'GMT-09:00'|{Instant.ofEpochMilli( 999L)}|{'1969-12-31 15:00:00.999-09:00'}
            'Instant -> String 2-1'|'GMT+00:00'|{Instant.EPOCH              }|{'1970-01-01 00:00:00+00:00'    }
            'Instant -> String 2-2'|'GMT+00:00'|{Instant.ofEpochMilli(   0L)}|{'1970-01-01 00:00:00+00:00'    }
            'Instant -> String 2-3'|'GMT+00:00'|{Instant.ofEpochMilli(-999L)}|{'1969-12-31 23:59:59.001+00:00'}
            'Instant -> String 2-4'|'GMT+00:00'|{Instant.ofEpochMilli( 999L)}|{'1970-01-01 00:00:00.999+00:00'}
            'Instant -> String 3-1'|'GMT+09:00'|{Instant.EPOCH              }|{'1970-01-01 09:00:00+09:00'    }
            'Instant -> String 3-2'|'GMT+09:00'|{Instant.ofEpochMilli(   0L)}|{'1970-01-01 09:00:00+09:00'    }
            'Instant -> String 3-3'|'GMT+09:00'|{Instant.ofEpochMilli(-999L)}|{'1970-01-01 08:59:59.001+09:00'}
            'Instant -> String 3-4'|'GMT+09:00'|{Instant.ofEpochMilli( 999L)}|{'1970-01-01 09:00:00.999+09:00'}

    }
}
