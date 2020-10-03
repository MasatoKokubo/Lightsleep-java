// StandardSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.database

import java.lang.reflect.Array
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import org.debugtrace.DebugTrace
import org.lightsleep.database.Standard
import org.lightsleep.component.*
import org.lightsleep.helper.*
import org.lightsleep.test.type.*

import spock.lang.*

// StandardSpec
@Unroll
class StandardSpec extends Specification {
    @Shared map = Standard.instance.typeConverterMap()

    @Shared    bigByteMin  = new BigDecimal(   Byte.MIN_VALUE)
    @Shared    bigByteMax  = new BigDecimal(   Byte.MAX_VALUE)
    @Shared   bigShortMin  = new BigDecimal(  Short.MIN_VALUE)
    @Shared   bigShortMax  = new BigDecimal(  Short.MAX_VALUE)
    @Shared     bigIntMin  = new BigDecimal(Integer.MIN_VALUE)
    @Shared     bigIntMax  = new BigDecimal(Integer.MAX_VALUE)
    @Shared    bigLingMin  = new BigDecimal(   Long.MIN_VALUE)
    @Shared    bigLingMax  = new BigDecimal(   Long.MAX_VALUE)
    @Shared          big_0 = BigDecimal.ZERO
    @Shared bigM123_456789 = new BigDecimal(  '-123.456789'  )
    @Shared  big123_456789 = new BigDecimal(   '123.456789'  )

    @Shared DAY_MS = 24L * 60L * 60L * 1000L
    @Shared CURRENT_MS = System.currentTimeMillis()
    @Shared utilDate1        = new java.util.Date(CURRENT_MS - DAY_MS)
    @Shared utilDate2        = new java.util.Date(CURRENT_MS         )
    @Shared utilDate3        = new java.util.Date(CURRENT_MS + DAY_MS)
    @Shared  sqlDate1        = new           Date(CURRENT_MS - DAY_MS)
    @Shared  sqlDate2        = new           Date(CURRENT_MS         )
    @Shared  sqlDate3        = new           Date(CURRENT_MS + DAY_MS)
    @Shared      time1       = new           Time(CURRENT_MS - DAY_MS)
    @Shared      time2       = new           Time(CURRENT_MS         )
    @Shared      time3       = new           Time(CURRENT_MS + DAY_MS)
    @Shared timeStamp1       = new      Timestamp(CURRENT_MS - DAY_MS)
    @Shared timeStamp2       = new      Timestamp(CURRENT_MS         )
    @Shared timeStamp3       = new      Timestamp(CURRENT_MS + DAY_MS)
    @Shared localDateTime1   = LocalDateTime.of(2018, 8, 31, 23, 59, 59,  987_654_321)    // since 3.0.0
    @Shared localDateTime2   = LocalDateTime.of(2018, 9,  1,  0,  0,  0,            0)    // since 3.0.0
    @Shared localDateTime3   = LocalDateTime.of(2018, 9,  2,  1, 23, 45,  678_000_000)    // since 3.0.0
    @Shared localDate1       = localDateTime1.toLocalDate()                               // since 3.0.0
    @Shared localDate2       = localDateTime2.toLocalDate()                               // since 3.0.0
    @Shared localDate3       = localDateTime3.toLocalDate()                               // since 3.0.0
    @Shared localTime1       = localDateTime1.toLocalTime()                               // since 3.0.0
    @Shared localTime2       = localDateTime2.toLocalTime()                               // since 3.0.0
    @Shared localTime3       = localDateTime3.toLocalTime()                               // since 3.0.0
    @Shared offsetDateTime1  = localDateTime1.atOffset(ZoneOffset.ofHoursMinutes( 9, 30)) // since 3.0.0
    @Shared offsetDateTime2  = localDateTime2.atOffset(ZoneOffset.ofHoursMinutes( 0,  0)) // since 3.0.0
    @Shared offsetDateTime3  = localDateTime3.atOffset(ZoneOffset.ofHoursMinutes(-9,-30)) // since 3.0.0
    @Shared zonedDateTime1   = localDateTime1.atZone(ZoneId.of("Asia/Tokyo"         ))    // since 3.0.0
    @Shared zonedDateTime2   = localDateTime2.atZone(ZoneId.of("Europe/London"      ))    // since 3.0.0
    @Shared zonedDateTime3   = localDateTime3.atZone(ZoneId.of("America/Los_Angeles"))    // since 3.0.0
    @Shared instant1         = offsetDateTime1.toInstant()                                // since 3.0.0
    @Shared instant2         = offsetDateTime2.toInstant()                                // since 3.0.0
    @Shared instant3         = offsetDateTime3.toInstant()                                // since 3.0.0

    @Shared           date1String = TypeConverter.convert(map, sqlDate1       , String)
    @Shared           date2String = TypeConverter.convert(map, sqlDate2       , String)
    @Shared           date3String = TypeConverter.convert(map, sqlDate3       , String)
    @Shared           time1String = TypeConverter.convert(map, time1          , String)
    @Shared           time2String = TypeConverter.convert(map, time2          , String)
    @Shared           time3String = TypeConverter.convert(map, time3          , String)
    @Shared      timeStamp1String = TypeConverter.convert(map, timeStamp1     , String)
    @Shared      timeStamp2String = TypeConverter.convert(map, timeStamp2     , String)
    @Shared      timeStamp3String = TypeConverter.convert(map, timeStamp3     , String)
    @Shared  localDateTime1String = TypeConverter.convert(map, localDateTime1 , String) // since 3.0.0
    @Shared  localDateTime2String = TypeConverter.convert(map, localDateTime2 , String) // since 3.0.0
    @Shared  localDateTime3String = TypeConverter.convert(map, localDateTime3 , String) // since 3.0.0
    @Shared      localDate1String = TypeConverter.convert(map, localDate1     , String) // since 3.0.0
    @Shared      localDate2String = TypeConverter.convert(map, localDate2     , String) // since 3.0.0
    @Shared      localDate3String = TypeConverter.convert(map, localDate3     , String) // since 3.0.0
    @Shared      localTime1String = TypeConverter.convert(map, localTime1     , String) // since 3.0.0
    @Shared      localTime2String = TypeConverter.convert(map, localTime2     , String) // since 3.0.0
    @Shared      localTime3String = TypeConverter.convert(map, localTime3     , String) // since 3.0.0
    @Shared offsetDateTime1String = TypeConverter.convert(map, offsetDateTime1, String) // since 3.0.0
    @Shared offsetDateTime2String = TypeConverter.convert(map, offsetDateTime2, String) // since 3.0.0
    @Shared offsetDateTime3String = TypeConverter.convert(map, offsetDateTime3, String) // since 3.0.0
    @Shared  zonedDateTime1String = TypeConverter.convert(map, zonedDateTime1 , String) // since 3.0.0
    @Shared  zonedDateTime2String = TypeConverter.convert(map, zonedDateTime2 , String) // since 3.0.0
    @Shared  zonedDateTime3String = TypeConverter.convert(map, zonedDateTime3 , String) // since 3.0.0
    @Shared        instant1String = TypeConverter.convert(map, instant1       , String) // since 3.0.0
    @Shared        instant2String = TypeConverter.convert(map, instant2       , String) // since 3.0.0
    @Shared        instant3String = TypeConverter.convert(map, instant3       , String) // since 3.0.0

    def setupSpec() {
        DebugTrace.print('bigByteMin    ', bigByteMin    ) // for Debugging
        DebugTrace.print('bigByteMax    ', bigByteMax    ) // for Debugging
        DebugTrace.print('bigShortMin   ', bigShortMin   ) // for Debugging
        DebugTrace.print('bigShortMax   ', bigShortMax   ) // for Debugging
        DebugTrace.print('bigIntMin     ', bigIntMin     ) // for Debugging
        DebugTrace.print('bigIntMax     ', bigIntMax     ) // for Debugging
        DebugTrace.print('bigLingMin    ', bigLingMin    ) // for Debugging
        DebugTrace.print('bigLingMax    ', bigLingMax    ) // for Debugging
        DebugTrace.print('bigM123_456789', bigM123_456789) // for Debugging
        DebugTrace.print('big123_456789 ', big123_456789 ) // for Debugging

        DebugTrace.print('utilDate1      ', utilDate1      ) // for Debugging
        DebugTrace.print('utilDate2      ', utilDate2      ) // for Debugging
        DebugTrace.print('utilDate3      ', utilDate3      ) // for Debugging
        DebugTrace.print('sqlDate1       ', sqlDate1       ) // for Debugging
        DebugTrace.print('sqlDate2       ', sqlDate2       ) // for Debugging
        DebugTrace.print('sqlDate3       ', sqlDate3       ) // for Debugging
        DebugTrace.print('time1          ', time1          ) // for Debugging
        DebugTrace.print('time2          ', time2          ) // for Debugging
        DebugTrace.print('time3          ', time3          ) // for Debugging
        DebugTrace.print('timeStamp1     ', timeStamp1     ) // for Debugging
        DebugTrace.print('timeStamp2     ', timeStamp2     ) // for Debugging
        DebugTrace.print('timeStamp3     ', timeStamp3     ) // for Debugging
        DebugTrace.print('localDateTime1 ', localDateTime1 ) // for Debugging
        DebugTrace.print('localDateTime2 ', localDateTime2 ) // for Debugging
        DebugTrace.print('localDateTime3 ', localDateTime3 ) // for Debugging
        DebugTrace.print('localDate1     ', localDate1     ) // for Debugging
        DebugTrace.print('localDate2     ', localDate2     ) // for Debugging
        DebugTrace.print('localDate3     ', localDate3     ) // for Debugging
        DebugTrace.print('localTime1     ', localTime1     ) // for Debugging
        DebugTrace.print('localTime2     ', localTime2     ) // for Debugging
        DebugTrace.print('localTime3     ', localTime3     ) // for Debugging
        DebugTrace.print('offsetDateTime1', offsetDateTime1) // for Debugging
        DebugTrace.print('offsetDateTime2', offsetDateTime2) // for Debugging
        DebugTrace.print('offsetDateTime3', offsetDateTime3) // for Debugging
        DebugTrace.print('zonedDateTime1 ', zonedDateTime1 ) // for Debugging
        DebugTrace.print('zonedDateTime2 ', zonedDateTime2 ) // for Debugging
        DebugTrace.print('zonedDateTime3 ', zonedDateTime3 ) // for Debugging
        DebugTrace.print('instant1       ', instant1       ) // for Debugging
        DebugTrace.print('instant2       ', instant2       ) // for Debugging
        DebugTrace.print('instant3       ', instant3       ) // for Debugging

        DebugTrace.print('          date1String',           date1String) // for Debugging
        DebugTrace.print('          date2String',           date2String) // for Debugging
        DebugTrace.print('          date3String',           date3String) // for Debugging
        DebugTrace.print('          time1String',           time1String) // for Debugging
        DebugTrace.print('          time2String',           time2String) // for Debugging
        DebugTrace.print('          time3String',           time3String) // for Debugging
        DebugTrace.print('     timeStamp1String',      timeStamp1String) // for Debugging
        DebugTrace.print('     timeStamp2String',      timeStamp2String) // for Debugging
        DebugTrace.print('     timeStamp3String',      timeStamp3String) // for Debugging
        DebugTrace.print(' localDateTime1String',  localDateTime1String) // for Debugging
        DebugTrace.print(' localDateTime2String',  localDateTime2String) // for Debugging
        DebugTrace.print(' localDateTime3String',  localDateTime3String) // for Debugging
        DebugTrace.print('     localDate1String',      localDate1String) // for Debugging
        DebugTrace.print('     localDate2String',      localDate2String) // for Debugging
        DebugTrace.print('     localDate3String',      localDate3String) // for Debugging
        DebugTrace.print('     localTime1String',      localTime1String) // for Debugging
        DebugTrace.print('     localTime2String',      localTime2String) // for Debugging
        DebugTrace.print('     localTime3String',      localTime3String) // for Debugging
        DebugTrace.print('offsetDateTime1String', offsetDateTime1String) // for Debugging
        DebugTrace.print('offsetDateTime2String', offsetDateTime2String) // for Debugging
        DebugTrace.print('offsetDateTime3String', offsetDateTime3String) // for Debugging
        DebugTrace.print(' zonedDateTime1String',  zonedDateTime1String) // for Debugging
        DebugTrace.print(' zonedDateTime2String',  zonedDateTime2String) // for Debugging
        DebugTrace.print(' zonedDateTime3String',  zonedDateTime3String) // for Debugging
        DebugTrace.print('       instant1String',        instant1String) // for Debugging
        DebugTrace.print('       instant2String',        instant2String) // for Debugging
        DebugTrace.print('       instant3String',        instant3String) // for Debugging
    }

    // Array ->
    def "Standard #title"(String title, Object sourceValues, Class<?> destinType) {
        DebugTrace.enter() // for Debugging
        DebugTrace.print('title', title) // for Debugging
        DebugTrace.print('sourceValues', sourceValues) // for Debugging
        DebugTrace.print('destinType', destinType) // for Debugging
        setup:
            def destinComponentType = destinType.componentType
            DebugTrace.print('destinComponentType', destinComponentType) // for Debugging
            def sourceValue = new TestArray(sourceValues)
            DebugTrace.print('sourceValue', sourceValue) // for Debugging
            def expectedValue = Array.newInstance(destinComponentType, sourceValues.size())
            DebugTrace.print('1 expectedValue', expectedValue) // for Debugging
            sourceValues.eachWithIndex {value, index->
                if (destinComponentType.primitive)
                    expectedValue[index] = TypeConverter.convert(map, value, Utils.toClassType(destinComponentType))
                else
                    expectedValue[index] = TypeConverter.convert(map, value, destinComponentType)
            }
            DebugTrace.print('2 expectedValue', expectedValue) // for Debugging

        when:
            def destinValue = TypeConverter.convert(map, sourceValue, destinType)
            DebugTrace.print('destinValue', destinValue) // for Debugging

        then:
            expectedValue.getClass() == destinValue.getClass()
            expectedValue == destinValue
        DebugTrace.leave() // for Debugging

        where:
            title|sourceValues|destinType

        //    title                     |sourceValues                                                 |destinType
            'Array(byte   ) -> byte[]'|[      Byte.MIN_VALUE, 0 ,       Byte.MAX_VALUE] as byte   []|byte[]
            'Array(short  ) -> byte[]'|[      Byte.MIN_VALUE, 0 ,       Byte.MAX_VALUE] as short  []|byte[]
            'Array(int    ) -> byte[]'|[      Byte.MIN_VALUE, 0 ,       Byte.MAX_VALUE] as int    []|byte[]
            'Array(long   ) -> byte[]'|[(long)Byte.MIN_VALUE, 0L, (long)Byte.MAX_VALUE] as long   []|byte[]
            'Array(Byte   ) -> byte[]'|[      Byte.MIN_VALUE, 0 ,       Byte.MAX_VALUE] as Byte   []|byte[]
            'Array(Short  ) -> byte[]'|[      Byte.MIN_VALUE, 0 ,       Byte.MAX_VALUE] as Short  []|byte[]
            'Array(Integer) -> byte[]'|[(int )Byte.MIN_VALUE, 0 , (int )Byte.MAX_VALUE] as Integer[]|byte[]
            'Array(Long   ) -> byte[]'|[(long)Byte.MIN_VALUE, 0L, (long)Byte.MAX_VALUE] as Long   []|byte[]

        //    title                      |sourceValues                                                   |destinType
            'Array(byte   ) -> short[]'|[       Byte.MIN_VALUE, 0 ,        Byte.MAX_VALUE] as byte   []|short[]
            'Array(short  ) -> short[]'|[      Short.MIN_VALUE, 0 ,       Short.MAX_VALUE] as short  []|short[]
            'Array(int    ) -> short[]'|[      Short.MIN_VALUE, 0 ,       Short.MAX_VALUE] as int    []|short[]
            'Array(long   ) -> short[]'|[(long)Short.MIN_VALUE, 0L, (long)Short.MAX_VALUE] as long   []|short[]
            'Array(Byte   ) -> short[]'|[       Byte.MIN_VALUE, 0 ,        Byte.MAX_VALUE] as Byte   []|short[]
            'Array(Short  ) -> short[]'|[      Short.MIN_VALUE, 0 ,       Short.MAX_VALUE] as Short  []|short[]
            'Array(Integer) -> short[]'|[(int )Short.MIN_VALUE, 0 , (int )Short.MAX_VALUE] as Integer[]|short[]
            'Array(Long   ) -> short[]'|[(long)Short.MIN_VALUE, 0L, (long)Short.MAX_VALUE] as Long   []|short[]

        //    title                    |sourceValues                                                       |destinType
            'Array(byte   ) -> int[]'|[         Byte.MIN_VALUE, 0 ,          Byte.MAX_VALUE] as    byte[]|int[]
            'Array(short  ) -> int[]'|[        Short.MIN_VALUE, 0 ,         Short.MAX_VALUE] as   short[]|int[]
            'Array(int    ) -> int[]'|[      Integer.MIN_VALUE, 0 ,       Integer.MAX_VALUE] as     int[]|int[]
            'Array(long   ) -> int[]'|[(long)Integer.MIN_VALUE, 0L, (long)Integer.MAX_VALUE] as    long[]|int[]
            'Array(Byte   ) -> int[]'|[         Byte.MIN_VALUE, 0 ,          Byte.MAX_VALUE] as    Byte[]|int[]
            'Array(Short  ) -> int[]'|[        Short.MIN_VALUE, 0 ,         Short.MAX_VALUE] as   Short[]|int[]
            'Array(Integer) -> int[]'|[      Integer.MIN_VALUE, 0 ,       Integer.MAX_VALUE] as Integer[]|int[]
            'Array(Long   ) -> int[]'|[(long)Integer.MIN_VALUE, 0L, (long)Integer.MAX_VALUE] as    Long[]|int[]

        //    title                     |sourceValues                                           |destinType
            'Array(byte   ) -> long[]'|[   Byte.MIN_VALUE, 0 ,    Byte.MAX_VALUE] as    byte[]|long[]
            'Array(short  ) -> long[]'|[  Short.MIN_VALUE, 0 ,   Short.MAX_VALUE] as   short[]|long[]
            'Array(int    ) -> long[]'|[Integer.MIN_VALUE, 0 , Integer.MAX_VALUE] as     int[]|long[]
            'Array(long   ) -> long[]'|[   Long.MIN_VALUE, 0L,    Long.MAX_VALUE] as    long[]|long[]
            'Array(Byte   ) -> long[]'|[   Byte.MIN_VALUE, 0 ,    Byte.MAX_VALUE] as    Byte[]|long[]
            'Array(Short  ) -> long[]'|[  Short.MIN_VALUE, 0 ,   Short.MAX_VALUE] as   Short[]|long[]
            'Array(Integer) -> long[]'|[Integer.MIN_VALUE, 0 , Integer.MAX_VALUE] as Integer[]|long[]
            'Array(Long   ) -> long[]'|[   Long.MIN_VALUE, 0L,    Long.MAX_VALUE] as    Long[]|long[]

        //    title                      |sourceValues                                             |destinType
            'Array(   byte) -> float[]'|[   Byte.MIN_VALUE, 0   ,    Byte.MAX_VALUE] as    byte[]|float[]
            'Array(  short) -> float[]'|[  Short.MIN_VALUE, 0   ,   Short.MAX_VALUE] as   short[]|float[]
            'Array(    int) -> float[]'|[Integer.MIN_VALUE, 0   , Integer.MAX_VALUE] as     int[]|float[]
            'Array(   long) -> float[]'|[   Long.MIN_VALUE, 0L  ,    Long.MAX_VALUE] as    long[]|float[]
            'Array(  float) -> float[]'|[        -123.456F, 0.0F,          123.456F] as   float[]|float[]
            'Array( double) -> float[]'|[        -123.456D, 0.0D,          123.456D] as  double[]|float[]
            'Array(   Byte) -> float[]'|[   Byte.MIN_VALUE, 0   ,    Byte.MAX_VALUE] as    Byte[]|float[]
            'Array(  Short) -> float[]'|[  Short.MIN_VALUE, 0   ,   Short.MAX_VALUE] as   Short[]|float[]
            'Array(Integer) -> float[]'|[Integer.MIN_VALUE, 0   , Integer.MAX_VALUE] as Integer[]|float[]
            'Array(   Long) -> float[]'|[   Long.MIN_VALUE, 0L  ,    Long.MAX_VALUE] as    Long[]|float[]
            'Array(  Float) -> float[]'|[        -123.456F, 0.0F,          123.456F] as   Float[]|float[]
            'Array( Double) -> float[]'|[        -123.456D, 0.0D,          123.456D] as  Double[]|float[]

        //    title                       |sourceValues                                             |destinType
            'Array(   byte) -> double[]'|[   Byte.MIN_VALUE, 0   ,    Byte.MAX_VALUE] as    byte[]|double[]
            'Array(  short) -> double[]'|[  Short.MIN_VALUE, 0   ,   Short.MAX_VALUE] as   short[]|double[]
            'Array(    int) -> double[]'|[Integer.MIN_VALUE, 0   , Integer.MAX_VALUE] as     int[]|double[]
            'Array(   long) -> double[]'|[   Long.MIN_VALUE, 0L  ,    Long.MAX_VALUE] as    long[]|double[]
            'Array(  float) -> double[]'|[     -123.456F   , 0.0F,       123.456F   ] as   float[]|double[]
            'Array( double) -> double[]'|[     -123.456789D, 0.0D,       123.456789D] as  double[]|double[]
            'Array(   Byte) -> double[]'|[   Byte.MIN_VALUE, 0   ,    Byte.MAX_VALUE] as    Byte[]|double[]
            'Array(  Short) -> double[]'|[  Short.MIN_VALUE, 0   ,   Short.MAX_VALUE] as   Short[]|double[]
            'Array(Integer) -> double[]'|[Integer.MIN_VALUE, 0   , Integer.MAX_VALUE] as Integer[]|double[]
            'Array(   Long) -> double[]'|[   Long.MIN_VALUE, 0L  ,    Long.MAX_VALUE] as    Long[]|double[]
            'Array(  Float) -> double[]'|[     -123.456F   , 0.0F,       123.456F   ] as   Float[]|double[]
            'Array( Double) -> double[]'|[     -123.456789D, 0.0D,       123.456789D] as  Double[]|double[]

        //    title                              |sourceValues                                                 |destinType
            'Array(      byte) -> BigDecimal[]'|[   Byte.MIN_VALUE, 0    ,    Byte.MAX_VALUE] as       byte[]|BigDecimal[]
            'Array(     short) -> BigDecimal[]'|[  Short.MIN_VALUE, 0    ,   Short.MAX_VALUE] as      short[]|BigDecimal[]
            'Array(       int) -> BigDecimal[]'|[Integer.MIN_VALUE, 0    , Integer.MAX_VALUE] as        int[]|BigDecimal[]
            'Array(      long) -> BigDecimal[]'|[   Long.MIN_VALUE, 0L   ,    Long.MAX_VALUE] as       long[]|BigDecimal[]
            'Array(      Byte) -> BigDecimal[]'|[   Byte.MIN_VALUE, 0    ,    Byte.MAX_VALUE] as       Byte[]|BigDecimal[]
            'Array(     Short) -> BigDecimal[]'|[  Short.MIN_VALUE, 0    ,   Short.MAX_VALUE] as      Short[]|BigDecimal[]
            'Array(   Integer) -> BigDecimal[]'|[Integer.MIN_VALUE, 0    , Integer.MAX_VALUE] as    Integer[]|BigDecimal[]
            'Array(      Long) -> BigDecimal[]'|[   Long.MIN_VALUE, 0L   ,    Long.MAX_VALUE] as       Long[]|BigDecimal[]
            'Array(BigDecimal) -> BigDecimal[]'|[   bigM123_456789, big_0,     big123_456789] as BigDecimal[]|BigDecimal[]

        //    title                         |sourceValues                        |destinType
            'Array(     char) -> String[]'|['A'  , 'B'  , 'C'  ] as      char[]|String[]
            'Array(Character) -> String[]'|['A'  , 'B'  , 'C'  ] as Character[]|String[]
            'Array(   String) -> String[]'|['ABC', 'abc', '123'] as    String[]|String[]

        //    title                                 |sourceValues                                       |destinType
//            'Array(Date) -> java.util.Date[]'     |[  sqlDate1,   sqlDate2,   sqlDate3] as Date     []|java.util.Date[]
            'Array(Date) -> Date[]'               |[  sqlDate1,   sqlDate2,   sqlDate3] as Date     []|Date          []
            'Array(Date) -> LocalDate[]'          |[  sqlDate1,   sqlDate2,   sqlDate3] as Date     []|LocalDate     []
            'Array(Time) -> Time[]'               |[     time1,      time2,      time3] as Time     []|Time          []
            'Array(Time) -> LocalTime[]'          |[     time1,      time2,      time3] as Time     []|LocalTime     []
            'Array(Timestamp) -> Timestamp[]'     |[timeStamp1, timeStamp2, timeStamp3] as Timestamp[]|Timestamp     []
            'Array(Timestamp) -> LocalDateTime[]' |[timeStamp1, timeStamp2, timeStamp3] as Timestamp[]|LocalDateTime []
            'Array(Timestamp) -> OffsetDateTime[]'|[timeStamp1, timeStamp2, timeStamp3] as Timestamp[]|OffsetDateTime[]
            'Array(Timestamp) -> ZonedDateTime[]' |[timeStamp1, timeStamp2, timeStamp3] as Timestamp[]|ZonedDateTime []
            'Array(Timestamp) -> Instant[]'       |[timeStamp1, timeStamp2, timeStamp3] as Timestamp[]|Instant       []
    }

    enum Size {XS, S, M, L, XL}

    // -> SqlString
    def "Standard #title -> SqlString"(String title, Object sourceValue, String expectedString) {
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

        //    title           |sourceValue             |expectedString
            'int           '|0                       |"0"
            'boolean false '|false                   |"FALSE"
            'boolean true  '|true                    |"TRUE"
            'char          '|'A' as char             |"'A'"
            'String 1      '|'ABC'                   |"'ABC'"
            'String 2      '|"'A'B'C'"               |"'''A''B''C'''"
            'String 3      '|"A\nB\tC"               |"'A'||CHR(10)||'B'||CHR(9)||'C'"
            'String 4      '|"\rA\nB\tC\f"           |"''||CHR(13)||'A'||CHR(10)||'B'||CHR(9)||'C'||CHR(12)"
            'BigDecimal    '|new BigDecimal('123.45')|"123.45"
            'java.uitl.Date'|utilDate1               |"DATE'"      + date1String           + "'"
            'Date          '|sqlDate1                |"DATE'"      + date1String           + "'"
            'Time          '|time1                   |"TIME'"      + time1String           + "'"
            'Timestamp     '|timeStamp1              |"TIMESTAMP'" + timeStamp1String      + "'"
            'LocalDateTime '|localDateTime1          |"TIMESTAMP'" + localDateTime1String  + "'"
            'LocalDate     '|localDate1              |"DATE'"      + localDate1String      + "'"
            'LocalTime     '|localTime1              |"TIME'"      + localTime1String      + "'"
            'OffsetDateTime'|offsetDateTime1         |"TIMESTAMP'" + offsetDateTime1String + "'"
            'ZonedDateTime '|zonedDateTime1          |"TIMESTAMP'" + zonedDateTime1String  + "'"
            'Instant       '|instant1                |"TIMESTAMP'" + instant1String        + "'"
            'enum XS       '|Size.XS                 |"'XS'"
            'enum M        '|Size.M                  |"'M'"
            'enum XL       '|Size.XL                 |"'XL'"

        //    title      |sourceValue                       |expectedString
            'boolean[]'|[true , false, true ] as boolean[]|'ARRAY[TRUE,FALSE,TRUE]'
            'char[]   '|['A'  , 'B'  , 'C'  ] as    char[]|"ARRAY['A','B','C']"
            'byte[]   '|[0x7F , 0x80 ,  0xFF] as    byte[]|"X'7F80FF'"
            'short[]  '|[-1   ,  0   ,  1   ] as   short[]|'ARRAY[-1,0,1]'
            'int[]    '|[-1   ,  0   ,  1   ] as     int[]|'ARRAY[-1,0,1]'
            'long[]   '|[-1L  ,  0L  ,  1L  ] as    long[]|'ARRAY[-1,0,1]'
            'float[]  '|[-1.5F,  0.0F,  1.5F] as   float[]|'ARRAY[-1.5,0.0,1.5]'
            'double[] '|[-1.5D,  0.0D,  1.5D] as  double[]|'ARRAY[-1.5,0.0,1.5]'
            'String[] '|['ABC', 'abc', '123'] as  String[]|"ARRAY['ABC','abc','123']"

        //    title         |sourceValue                                                                             |expectedString
            'BigDecimal[]'|[new BigDecimal('-123.456'), BigDecimal.ZERO, new BigDecimal('123.456')] as BigDecimal[]|'ARRAY[-123.456,0,123.456]'

        //    title             |sourceValue                                                            |expectedString
            'java.uitl.Date[]'|[utilDate1      , utilDate2      , utilDate3      ] as java.util.Date[]|"ARRAY[DATE'"      + date1String           + "',DATE'"      + date2String           + "',DATE'"      + date3String           + "']"
            'Date[]          '|[sqlDate1       , sqlDate2       , sqlDate3       ] as Date[]          |"ARRAY[DATE'"      + date1String           + "',DATE'"      + date2String           + "',DATE'"      + date3String           + "']"
            'Time[]          '|[time1          , time2          , time3          ] as Time[]          |"ARRAY[TIME'"      + time1String           + "',TIME'"      + time2String           + "',TIME'"      + time3String           + "']"
            'Timestamp[]     '|[timeStamp1     , timeStamp2     , timeStamp3     ] as Timestamp[]     |"ARRAY[TIMESTAMP'" + timeStamp1String      + "',TIMESTAMP'" + timeStamp2String      + "',TIMESTAMP'" + timeStamp3String      + "']"
            'LocalDateTime[] '|[localDateTime1 , localDateTime2 , localDateTime3 ] as LocalDateTime[] |"ARRAY[TIMESTAMP'" + localDateTime1String  + "',TIMESTAMP'" + localDateTime2String  + "',TIMESTAMP'" + localDateTime3String  + "']"
            'LocalDate[]     '|[localDate1     , localDate2     , localDate3     ] as LocalDate[]     |"ARRAY[DATE'"      + localDate1String      + "',DATE'"      + localDate2String      + "',DATE'"      + localDate3String      + "']"
            'LocalTime[]     '|[localTime1     , localTime2     , localTime3     ] as LocalTime[]     |"ARRAY[TIME'"      + localTime1String      + "',TIME'"      + localTime2String      + "',TIME'"      + localTime3String      + "']"
            'OffsetDateTime[]'|[offsetDateTime1, offsetDateTime2, offsetDateTime3] as OffsetDateTime[]|"ARRAY[TIMESTAMP'" + offsetDateTime1String + "',TIMESTAMP'" + offsetDateTime2String + "',TIMESTAMP'" + offsetDateTime3String + "']"
            'ZonedDateTime[] '|[zonedDateTime1 , zonedDateTime2 , zonedDateTime3 ] as ZonedDateTime[] |"ARRAY[TIMESTAMP'" + zonedDateTime1String  + "',TIMESTAMP'" + zonedDateTime2String  + "',TIMESTAMP'" + zonedDateTime3String  + "']"
            'Instant[]       '|[instant1       , instant2       , instant3       ] as Instant[]       |"ARRAY[TIMESTAMP'" + instant1String        + "',TIMESTAMP'" + instant2String        + "',TIMESTAMP'" + instant3String        + "']"

        //    title |sourceValue              |expectedString
            'List'|['ABC', 123, false, 1.2D]|"('ABC',123,FALSE,1.2)"
    }

    // long String -> SqlString
    def "Standard long String -> SqlString"() {
        DebugTrace.enter() // for Debugging
        when:
            DebugTrace.print('maxStringLiteralLength', Standard.instance.maxStringLiteralLength) // for Debugging
            def buff = new StringBuilder(Standard.instance.maxStringLiteralLength + 1)
            for (int index = 0; index < Standard.instance.maxStringLiteralLength; ++index)
                buff.append((char)((char)'A' + (index % 26)))
            DebugTrace.print('buff', buff) // for Debugging

        then:
            TypeConverter.convert(map, buff.toString(), SqlString).toString().startsWith("'ABCDEFGH")

        when: buff.append('+')
        then: TypeConverter.convert(map, buff.toString(), SqlString).toString() == '?'

        DebugTrace.leave() // for Debugging
    }

    // long byte[] -> SqlString
    def "Standard long byte[] -> SqlString"() {
        DebugTrace.enter() // for Debugging
        when:
            DebugTrace.print('maxBinaryLiteralLength', Standard.instance.maxBinaryLiteralLength) // for Debugging
            def bytes = new byte[Standard.instance.maxBinaryLiteralLength]
            for (def index = 0; index < bytes.length; ++index)
                bytes[index] = (byte)(index - 128)
            DebugTrace.print('bytes', bytes) // for Debugging

        then: TypeConverter.convert(map, bytes, SqlString).toString().startsWith("X'8081828384858687")

        when:
            bytes = new byte[Standard.instance.maxBinaryLiteralLength + 1]
            for (def index = 0; index < bytes.length; ++index)
                bytes[index] = (byte)(index - 128)
            DebugTrace.print('bytes', bytes) // for Debugging

        then: TypeConverter.convert(map, bytes, SqlString).toString() == '?'
        DebugTrace.leave() // for Debugging
    }

    // long String[] -> SqlString
    def "Standard long String[] -> SqlString"() {
        DebugTrace.enter() // for Debugging
        DebugTrace.print('maxStringLiteralLength', Standard.instance.maxStringLiteralLength) // for Debugging
        when:
            def buff = new StringBuilder(Standard.instance.maxStringLiteralLength + 1)
            for (def index = 0; index < Standard.instance.maxStringLiteralLength; ++index)
                buff.append((char)(('A' as char) + (index % 26)))
            def strings = ['A', buff.toString(), 'B', buff.toString()] as String[]
            def string = TypeConverter.convert(map, strings, SqlString).toString()
            DebugTrace.print('strings', strings) // for Debugging
            DebugTrace.print('string', string) // for Debugging

        then:
            string.startsWith("ARRAY['A','ABCDEFGH")
            string.endsWith("']")

        when:
            buff.append('+')
            strings = ['A', buff.toString(), 'B', buff.toString()] as String[]
            string = TypeConverter.convert(map, strings, SqlString).toString()
            DebugTrace.print('strings', strings) // for Debugging
            DebugTrace.print('string', string) // for Debugging

        then: string == "ARRAY['A',?,'B',?]"

        DebugTrace.leave() // for Debugging
    }

    // long byte[][] -> SqlString
    def "Standard long byte[][] -> SqlString"() {
        DebugTrace.enter() // for Debugging
        when:
            DebugTrace.print('maxStringLiteralLength', Standard.instance.maxStringLiteralLength) // for Debugging
            def bytes = new byte[Standard.instance.maxBinaryLiteralLength]
            for (def index = 0; index < bytes.length; ++index)
                bytes[index] = (byte)(index - 128)
            def bytesArray = [[(byte)0x80, (byte)0x7F] as byte[], bytes, [(byte)0x81, (byte)0x7E] as byte[], bytes] as byte[][]
            def string = TypeConverter.convert(map, bytesArray, SqlString).toString()
            DebugTrace.print('bytesArray', bytesArray) // for Debugging
            DebugTrace.print('string', string) // for Debugging

        then:
            string.startsWith("ARRAY[X'807F',X'8081828384858687")
            string.endsWith("']")

        when:
            bytes = new byte[Standard.instance.maxBinaryLiteralLength + 1]
            for (def index = 0; index < bytes.length; ++index)
                bytes[index] = (byte)(index - 128)
            bytesArray = [[(byte)0x80, (byte)0x7F] as byte[], bytes, [(byte)0x81, (byte)0x7E] as byte[], bytes] as byte[][]
            string = TypeConverter.convert(map, bytesArray, SqlString).toString()
            DebugTrace.print('bytesArray', bytesArray) // for Debugging
            DebugTrace.print('string', string) // for Debugging

        then: string == "ARRAY[X'807F',?,X'817E',?]"

        DebugTrace.leave() // for Debugging
    }

    // maskPassword
    def "Standard maskPassword"(String jdbcUrl, String result) {
        expect: Standard.instance.maskPassword(jdbcUrl) == result

        where:
            jdbcUrl                       |result
            ''                            |''
            'passwor='                    |'passwor='
            'password ='                  |'password=' + Standard.PASSWORD_MASK
            'password  =a'                |'password=' + Standard.PASSWORD_MASK

            'password= !"#$%\'()*+,-./;'  |'password=' + Standard.PASSWORD_MASK
            ':password=<=>?[\\]^_`(|)~:'  |':password=' + Standard.PASSWORD_MASK + ':'

            'password= !"#$%\'()*+,-./&'  |'password=' + Standard.PASSWORD_MASK
            '?password=;<=>?[\\]^_`(|)~:' |'?password=' + Standard.PASSWORD_MASK + ':'
    }
}
