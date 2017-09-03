// StandardSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.database

import java.sql.Date
import java.sql.Time
import java.sql.Timestamp

import org.debugtrace.DebugTrace
import org.lightsleep.database.Standard
import org.lightsleep.component.*
import org.lightsleep.helper.*
import org.lightsleep.test.type.*

import spock.lang.*

// StandardSpec
@Unroll
class StandardSpec extends Specification {
	@Shared map = Standard.instance().typeConverterMap()

	@Shared    BIG_BYTE_MIN  = new BigDecimal(   Byte.MIN_VALUE)
	@Shared    BIG_BYTE_MAX  = new BigDecimal(   Byte.MAX_VALUE)
	@Shared   BIG_SHORT_MIN  = new BigDecimal(  Short.MIN_VALUE)
	@Shared   BIG_SHORT_MAX  = new BigDecimal(  Short.MAX_VALUE)
	@Shared     BIG_INT_MIN  = new BigDecimal(Integer.MIN_VALUE)
	@Shared     BIG_INT_MAX  = new BigDecimal(Integer.MAX_VALUE)
	@Shared    BIG_LONG_MIN  = new BigDecimal(   Long.MIN_VALUE)
	@Shared    BIG_LONG_MAX  = new BigDecimal(   Long.MAX_VALUE)
	@Shared            BIG_0 = BigDecimal.ZERO
	@Shared BIG_M_123_456789 = new BigDecimal(  '-123.456789'  )
	@Shared   BIG_123_456789 = new BigDecimal(   '123.456789'  )

	@Shared DAY_MS = 24L * 60L * 60L * 1000L
	@Shared CURRENT_MS = System.currentTimeMillis()
	@Shared UTIL_DATE1 = new java.util.Date(CURRENT_MS - DAY_MS)
	@Shared UTIL_DATE2 = new java.util.Date(CURRENT_MS         )
	@Shared UTIL_DATE3 = new java.util.Date(CURRENT_MS + DAY_MS)
	@Shared  SQL_DATE1 = new           Date(CURRENT_MS - DAY_MS)
	@Shared  SQL_DATE2 = new           Date(CURRENT_MS         )
	@Shared  SQL_DATE3 = new           Date(CURRENT_MS + DAY_MS)
	@Shared      TIME1 = new           Time(CURRENT_MS - DAY_MS)
	@Shared      TIME2 = new           Time(CURRENT_MS         )
	@Shared      TIME3 = new           Time(CURRENT_MS + DAY_MS)
	@Shared TIMESTAMP1 = new      Timestamp(CURRENT_MS - DAY_MS)
	@Shared TIMESTAMP2 = new      Timestamp(CURRENT_MS         )
	@Shared TIMESTAMP3 = new      Timestamp(CURRENT_MS + DAY_MS)

	@Shared      DATE1_STRING = TypeConverter.convert(map,  SQL_DATE1, String)
	@Shared      DATE2_STRING = TypeConverter.convert(map,  SQL_DATE2, String)
	@Shared      DATE3_STRING = TypeConverter.convert(map,  SQL_DATE3, String)
	@Shared      TIME1_STRING = TypeConverter.convert(map,      TIME1, String)
	@Shared      TIME2_STRING = TypeConverter.convert(map,      TIME2, String)
	@Shared      TIME3_STRING = TypeConverter.convert(map,      TIME3, String)
	@Shared TIMESTAMP1_STRING = TypeConverter.convert(map, TIMESTAMP1, String)
	@Shared TIMESTAMP2_STRING = TypeConverter.convert(map, TIMESTAMP2, String)
	@Shared TIMESTAMP3_STRING = TypeConverter.convert(map, TIMESTAMP3, String)

	def setupSpec() {
	/**/DebugTrace.print('   BIG_BYTE_MIN ',    BIG_BYTE_MIN )
	/**/DebugTrace.print('   BIG_BYTE_MAX ',    BIG_BYTE_MAX )
	/**/DebugTrace.print('  BIG_SHORT_MIN ',   BIG_SHORT_MIN )
	/**/DebugTrace.print('  BIG_SHORT_MAX ',   BIG_SHORT_MAX )
	/**/DebugTrace.print('    BIG_INT_MIN ',     BIG_INT_MIN )
	/**/DebugTrace.print('    BIG_INT_MAX ',     BIG_INT_MAX )
	/**/DebugTrace.print('   BIG_LONG_MIN ',    BIG_LONG_MIN )
	/**/DebugTrace.print('   BIG_LONG_MAX ',    BIG_LONG_MAX )
	/**/DebugTrace.print('BIG_M_123_456789', BIG_M_123_456789)
	/**/DebugTrace.print('  BIG_123_456789',   BIG_123_456789)

	/**/DebugTrace.print('UTIL_DATE1       ', UTIL_DATE1       )
	/**/DebugTrace.print('UTIL_DATE2       ', UTIL_DATE2       )
	/**/DebugTrace.print('UTIL_DATE3       ', UTIL_DATE3       )
	/**/DebugTrace.print(' SQL_DATE1       ',  SQL_DATE1       )
	/**/DebugTrace.print(' SQL_DATE2       ',  SQL_DATE2       )
	/**/DebugTrace.print(' SQL_DATE3       ',  SQL_DATE3       )
	/**/DebugTrace.print('     TIME1       ',      TIME1       )
	/**/DebugTrace.print('     TIME2       ',      TIME2       )
	/**/DebugTrace.print('     TIME3       ',      TIME3       )
	/**/DebugTrace.print('TIMESTAMP1       ', TIMESTAMP1       )
	/**/DebugTrace.print('TIMESTAMP2       ', TIMESTAMP2       )
	/**/DebugTrace.print('TIMESTAMP3       ', TIMESTAMP3       )
	/**/DebugTrace.print('     DATE1_STRING',      DATE1_STRING)
	/**/DebugTrace.print('     DATE2_STRING',      DATE2_STRING)
	/**/DebugTrace.print('     DATE3_STRING',      DATE3_STRING)
	/**/DebugTrace.print('     TIME1_STRING',      TIME1_STRING)
	/**/DebugTrace.print('     TIME2_STRING',      TIME2_STRING)
	/**/DebugTrace.print('     TIME3_STRING',      TIME3_STRING)
	/**/DebugTrace.print('TIMESTAMP1_STRING', TIMESTAMP1_STRING)
	/**/DebugTrace.print('TIMESTAMP2_STRING', TIMESTAMP2_STRING)
	/**/DebugTrace.print('TIMESTAMP3_STRING', TIMESTAMP3_STRING)
	}

	// Array -> byte[]
	def "StandardSpec Array -> byte[]"() {
	/**/DebugTrace.enter()
		setup:
			def byteMinZeroMax = [Byte.MIN_VALUE, 0, Byte.MAX_VALUE] as byte[]

		expect:
			TypeConverter.convert(map, new TestArray([      Byte.MIN_VALUE, 0 ,       Byte.MAX_VALUE] as byte   []), byte[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([      Byte.MIN_VALUE, 0 ,       Byte.MAX_VALUE] as short  []), byte[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([      Byte.MIN_VALUE, 0 ,       Byte.MAX_VALUE] as int    []), byte[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([(long)Byte.MIN_VALUE, 0L, (long)Byte.MAX_VALUE] as long   []), byte[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([      Byte.MIN_VALUE, 0 ,       Byte.MAX_VALUE] as Byte   []), byte[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([      Byte.MIN_VALUE, 0 ,       Byte.MAX_VALUE] as Short  []), byte[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([(int )Byte.MIN_VALUE, 0 , (int )Byte.MAX_VALUE] as Integer[]), byte[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([(long)Byte.MIN_VALUE, 0L, (long)Byte.MAX_VALUE] as Long   []), byte[]) == byteMinZeroMax
	/**/DebugTrace.leave()
	}

	// Array -> short[]
	def "StandardSpec Array -> short[]"() {
	/**/DebugTrace.enter()
		setup:
			def byteMinZeroMax  = [ Byte.MIN_VALUE, 0,  Byte.MAX_VALUE] as short[]
			def shortMinZeroMax = [Short.MIN_VALUE, 0, Short.MAX_VALUE] as short[]

		expect:
			TypeConverter.convert(map, new TestArray([       Byte.MIN_VALUE, 0 ,        Byte.MAX_VALUE] as byte   []), short[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([      Short.MIN_VALUE, 0 ,       Short.MAX_VALUE] as short  []), short[]) == shortMinZeroMax
			TypeConverter.convert(map, new TestArray([      Short.MIN_VALUE, 0 ,       Short.MAX_VALUE] as int    []), short[]) == shortMinZeroMax
			TypeConverter.convert(map, new TestArray([(long)Short.MIN_VALUE, 0L, (long)Short.MAX_VALUE] as long   []), short[]) == shortMinZeroMax
			TypeConverter.convert(map, new TestArray([       Byte.MIN_VALUE, 0 ,        Byte.MAX_VALUE] as Byte   []), short[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([      Short.MIN_VALUE, 0 ,       Short.MAX_VALUE] as Short  []), short[]) == shortMinZeroMax
			TypeConverter.convert(map, new TestArray([(int )Short.MIN_VALUE, 0 , (int )Short.MAX_VALUE] as Integer[]), short[]) == shortMinZeroMax
			TypeConverter.convert(map, new TestArray([(long)Short.MIN_VALUE, 0L, (long)Short.MAX_VALUE] as Long   []), short[]) == shortMinZeroMax
	/**/DebugTrace.leave()
	}

	// Array -> int[]
	def "StandardSpec Array -> int[]"() {
	/**/DebugTrace.enter()
		setup:
			def byteMinZeroMax  = [   Byte.MIN_VALUE, 0,    Byte.MAX_VALUE] as int[]
			def shortMinZeroMax = [  Short.MIN_VALUE, 0,   Short.MAX_VALUE] as int[]
			def intMinZeroMax   = [Integer.MIN_VALUE, 0, Integer.MAX_VALUE] as int[]

		expect:
			TypeConverter.convert(map, new TestArray([         Byte.MIN_VALUE, 0 ,          Byte.MAX_VALUE] as    byte[]), int[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([        Short.MIN_VALUE, 0 ,         Short.MAX_VALUE] as   short[]), int[]) == shortMinZeroMax
			TypeConverter.convert(map, new TestArray([      Integer.MIN_VALUE, 0 ,       Integer.MAX_VALUE] as     int[]), int[]) == intMinZeroMax
			TypeConverter.convert(map, new TestArray([(long)Integer.MIN_VALUE, 0L, (long)Integer.MAX_VALUE] as    long[]), int[]) == intMinZeroMax
			TypeConverter.convert(map, new TestArray([         Byte.MIN_VALUE, 0 ,          Byte.MAX_VALUE] as    Byte[]), int[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([        Short.MIN_VALUE, 0 ,         Short.MAX_VALUE] as   Short[]), int[]) == shortMinZeroMax
			TypeConverter.convert(map, new TestArray([      Integer.MIN_VALUE, 0 ,       Integer.MAX_VALUE] as Integer[]), int[]) == intMinZeroMax
			TypeConverter.convert(map, new TestArray([(long)Integer.MIN_VALUE, 0L, (long)Integer.MAX_VALUE] as    Long[]), int[]) == intMinZeroMax
	/**/DebugTrace.leave()
	}

	// Array -> long[]
	def "StandardSpec Array -> long[]"() {
	/**/DebugTrace.enter()
		setup:
			def byteMinZeroMax  = [   Byte.MIN_VALUE, 0,    Byte.MAX_VALUE] as long[]
			def shortMinZeroMax = [  Short.MIN_VALUE, 0,   Short.MAX_VALUE] as long[]
			def intMinZeroMax   = [Integer.MIN_VALUE, 0, Integer.MAX_VALUE] as long[]
			def longMinZeroMax  = [   Long.MIN_VALUE, 0,    Long.MAX_VALUE] as long[]

		expect:
			TypeConverter.convert(map, new TestArray([   Byte.MIN_VALUE, 0 ,    Byte.MAX_VALUE] as    byte[]), long[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([  Short.MIN_VALUE, 0 ,   Short.MAX_VALUE] as   short[]), long[]) == shortMinZeroMax
			TypeConverter.convert(map, new TestArray([Integer.MIN_VALUE, 0 , Integer.MAX_VALUE] as     int[]), long[]) == intMinZeroMax
			TypeConverter.convert(map, new TestArray([   Long.MIN_VALUE, 0L,    Long.MAX_VALUE] as    long[]), long[]) == longMinZeroMax
			TypeConverter.convert(map, new TestArray([   Byte.MIN_VALUE, 0 ,    Byte.MAX_VALUE] as    Byte[]), long[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([  Short.MIN_VALUE, 0 ,   Short.MAX_VALUE] as   Short[]), long[]) == shortMinZeroMax
			TypeConverter.convert(map, new TestArray([Integer.MIN_VALUE, 0 , Integer.MAX_VALUE] as Integer[]), long[]) == intMinZeroMax
			TypeConverter.convert(map, new TestArray([   Long.MIN_VALUE, 0L,    Long.MAX_VALUE] as    Long[]), long[]) == longMinZeroMax
	/**/DebugTrace.leave()
	}

	// Array -> float[]
	def "StandardSpec Array -> float[]"() {
	/**/DebugTrace.enter()
		setup:
			def byteMinZeroMax  = [   Byte.MIN_VALUE, 0   ,    Byte.MAX_VALUE] as float[]
			def shortMinZeroMax = [  Short.MIN_VALUE, 0   ,   Short.MAX_VALUE] as float[]
			def intMinZeroMax   = [Integer.MIN_VALUE, 0   , Integer.MAX_VALUE] as float[]
			def longMinZeroMax  = [   Long.MIN_VALUE, 0   ,    Long.MAX_VALUE] as float[]
			def float123456     = [        -123.456F, 0.0F,          123.456F] as float[]

		expect:
			TypeConverter.convert(map, new TestArray([   Byte.MIN_VALUE, 0   ,    Byte.MAX_VALUE] as    byte[]), float[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([  Short.MIN_VALUE, 0   ,   Short.MAX_VALUE] as   short[]), float[]) == shortMinZeroMax
			TypeConverter.convert(map, new TestArray([Integer.MIN_VALUE, 0   , Integer.MAX_VALUE] as     int[]), float[]) == intMinZeroMax
			TypeConverter.convert(map, new TestArray([   Long.MIN_VALUE, 0L  ,    Long.MAX_VALUE] as    long[]), float[]) == longMinZeroMax
			TypeConverter.convert(map, new TestArray([        -123.456F, 0.0F,          123.456F] as   float[]), float[]) == float123456
			TypeConverter.convert(map, new TestArray([        -123.456D, 0.0D,          123.456D] as  double[]), float[]) == float123456
			TypeConverter.convert(map, new TestArray([   Byte.MIN_VALUE, 0   ,    Byte.MAX_VALUE] as    Byte[]), float[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([  Short.MIN_VALUE, 0   ,   Short.MAX_VALUE] as   Short[]), float[]) == shortMinZeroMax
			TypeConverter.convert(map, new TestArray([Integer.MIN_VALUE, 0   , Integer.MAX_VALUE] as Integer[]), float[]) == intMinZeroMax
			TypeConverter.convert(map, new TestArray([   Long.MIN_VALUE, 0L  ,    Long.MAX_VALUE] as    Long[]), float[]) == longMinZeroMax
			TypeConverter.convert(map, new TestArray([        -123.456F, 0.0F,          123.456F] as   Float[]), float[]) == float123456
			TypeConverter.convert(map, new TestArray([        -123.456D, 0.0D,          123.456D] as  Double[]), float[]) == float123456

	/**/DebugTrace.leave()
	}

	// Array -> double[]
	def "StandardSpec Array -> double[]"() {
	/**/DebugTrace.enter()
		setup:
			def byteMinZeroMax  = [   Byte.MIN_VALUE, 0   ,    Byte.MAX_VALUE] as double[]
			def shortMinZeroMax = [  Short.MIN_VALUE, 0   ,   Short.MAX_VALUE] as double[]
			def intMinZeroMax   = [Integer.MIN_VALUE, 0   , Integer.MAX_VALUE] as double[]
			def longMinZeroMax  = [   Long.MIN_VALUE, 0   ,    Long.MAX_VALUE] as double[]
			def flost123456     = [        -123.456F, 0.0F,          123.456F] as double[]
			def double123456789 = [     -123.456789D, 0.0D,       123.456789D] as double[]

		expect:
			TypeConverter.convert(map, new TestArray([   Byte.MIN_VALUE, 0   ,    Byte.MAX_VALUE] as    byte[]), double[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([  Short.MIN_VALUE, 0   ,   Short.MAX_VALUE] as   short[]), double[]) == shortMinZeroMax
			TypeConverter.convert(map, new TestArray([Integer.MIN_VALUE, 0   , Integer.MAX_VALUE] as     int[]), double[]) == intMinZeroMax
			TypeConverter.convert(map, new TestArray([   Long.MIN_VALUE, 0L  ,    Long.MAX_VALUE] as    long[]), double[]) == longMinZeroMax
			TypeConverter.convert(map, new TestArray([     -123.456F   , 0.0F,       123.456F   ] as   float[]), double[]) == flost123456
			TypeConverter.convert(map, new TestArray([     -123.456789D, 0.0D,       123.456789D] as  double[]), double[]) == double123456789
			TypeConverter.convert(map, new TestArray([   Byte.MIN_VALUE, 0   ,    Byte.MAX_VALUE] as    Byte[]), double[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([  Short.MIN_VALUE, 0   ,   Short.MAX_VALUE] as   Short[]), double[]) == shortMinZeroMax
			TypeConverter.convert(map, new TestArray([Integer.MIN_VALUE, 0   , Integer.MAX_VALUE] as Integer[]), double[]) == intMinZeroMax
			TypeConverter.convert(map, new TestArray([   Long.MIN_VALUE, 0L  ,    Long.MAX_VALUE] as    Long[]), double[]) == longMinZeroMax
			TypeConverter.convert(map, new TestArray([     -123.456F   , 0.0F,       123.456F   ] as   Float[]), double[]) == flost123456
			TypeConverter.convert(map, new TestArray([     -123.456789D, 0.0D,       123.456789D] as  Double[]), double[]) == double123456789

	/**/DebugTrace.leave()
	}


	// Array -> BigDecimal[]
	def "StandardSpec Array -> BigDecimal[]"() {
	/**/DebugTrace.enter()
		setup:
			def byteMinZeroMax  = [BIG_BYTE_MIN    , BIG_0, BIG_BYTE_MAX  ] as BigDecimal[]
			def shortMinZeroMax = [BIG_SHORT_MIN   , BIG_0, BIG_SHORT_MAX ] as BigDecimal[]
			def intMinZeroMax   = [BIG_INT_MIN     , BIG_0, BIG_INT_MAX   ] as BigDecimal[]
			def longMinZeroMax  = [BIG_LONG_MIN    , BIG_0, BIG_LONG_MAX  ] as BigDecimal[]
			def big123456789    = [BIG_M_123_456789, BIG_0, BIG_123_456789] as BigDecimal[]

		expect:
			TypeConverter.convert(map, new TestArray([   Byte.MIN_VALUE, 0    ,    Byte.MAX_VALUE] as       byte[]), BigDecimal[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([  Short.MIN_VALUE, 0    ,   Short.MAX_VALUE] as      short[]), BigDecimal[]) == shortMinZeroMax
			TypeConverter.convert(map, new TestArray([Integer.MIN_VALUE, 0    , Integer.MAX_VALUE] as        int[]), BigDecimal[]) == intMinZeroMax
			TypeConverter.convert(map, new TestArray([   Long.MIN_VALUE, 0L   ,    Long.MAX_VALUE] as       long[]), BigDecimal[]) == longMinZeroMax
			TypeConverter.convert(map, new TestArray([   Byte.MIN_VALUE, 0    ,    Byte.MAX_VALUE] as       Byte[]), BigDecimal[]) == byteMinZeroMax
			TypeConverter.convert(map, new TestArray([  Short.MIN_VALUE, 0    ,   Short.MAX_VALUE] as      Short[]), BigDecimal[]) == shortMinZeroMax
			TypeConverter.convert(map, new TestArray([Integer.MIN_VALUE, 0    , Integer.MAX_VALUE] as    Integer[]), BigDecimal[]) == intMinZeroMax
			TypeConverter.convert(map, new TestArray([   Long.MIN_VALUE, 0L   ,    Long.MAX_VALUE] as       Long[]), BigDecimal[]) == longMinZeroMax
			TypeConverter.convert(map, new TestArray([ BIG_M_123_456789, BIG_0,    BIG_123_456789] as BigDecimal[]), BigDecimal[]) == big123456789

	/**/DebugTrace.leave()
	}

	// Array -> String[]
	def "StandardSpec Array -> String[]"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new TestArray(['A'  , 'B'  , 'C'  ] as      char[]), String[]) == ['A'  , 'B'  , 'C'  ] as String[]
			TypeConverter.convert(map, new TestArray(['A'  , 'B'  , 'C'  ] as Character[]), String[]) == ['A'  , 'B'  , 'C'  ] as String[]
			TypeConverter.convert(map, new TestArray(['ABC', 'abc', '123'] as    String[]), String[]) == ['ABC', 'abc', '123'] as String[]
	/**/DebugTrace.leave()
	}


	// Array -> java.util.Date[]
	def "StandardSpec Array -> java.util.Date[]"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new TestArray([UTIL_DATE1, UTIL_DATE2, UTIL_DATE3] as java.util.Date[]), java.util.Date[]) == [UTIL_DATE1, UTIL_DATE2, UTIL_DATE3] as java.util.Date[]
			TypeConverter.convert(map, new TestArray([ SQL_DATE1,  SQL_DATE2,  SQL_DATE3] as           Date[]), java.util.Date[]) == [ SQL_DATE1,  SQL_DATE2,  SQL_DATE3] as java.util.Date[]
			TypeConverter.convert(map, new TestArray([     TIME1,      TIME2,      TIME3] as           Time[]), java.util.Date[]) == [     TIME1,      TIME2,      TIME3] as java.util.Date[]
			TypeConverter.convert(map, new TestArray([TIMESTAMP1, TIMESTAMP2, TIMESTAMP3] as      Timestamp[]), java.util.Date[]) == [TIMESTAMP1, TIMESTAMP2, TIMESTAMP3] as java.util.Date[]
	/**/DebugTrace.leave()
	}

	// Array -> java.sql.Date[]
	def "StandardSpec Array -> java.sql.Date[]"() {
	/**/DebugTrace.enter()
		setup:
			def date123 = [SQL_DATE1, SQL_DATE2, SQL_DATE3] as Date[]

		expect:
			TypeConverter.convert(map, new TestArray([UTIL_DATE1, UTIL_DATE2, UTIL_DATE3] as java.util.Date[]), Date[]) == date123
			TypeConverter.convert(map, new TestArray([ SQL_DATE1,  SQL_DATE2,  SQL_DATE3] as           Date[]), Date[]) == date123
			TypeConverter.convert(map, new TestArray([     TIME1,      TIME2,      TIME3] as           Time[]), Date[]) == date123
			TypeConverter.convert(map, new TestArray([TIMESTAMP1, TIMESTAMP2, TIMESTAMP3] as      Timestamp[]), Date[]) == date123
	/**/DebugTrace.leave()
	}

	// Array -> Time[]
	def "StandardSpec Array -> Time[]"() {
	/**/DebugTrace.enter()
		setup:
			def time123 = [TIME1, TIME2, TIME3] as Time[]

		expect:
			TypeConverter.convert(map, new TestArray([UTIL_DATE1, UTIL_DATE2, UTIL_DATE3] as java.util.Date[]), Time[]) == time123
			TypeConverter.convert(map, new TestArray([ SQL_DATE1,  SQL_DATE2,  SQL_DATE3] as           Date[]), Time[]) == time123
			TypeConverter.convert(map, new TestArray([     TIME1,      TIME2,      TIME3] as           Time[]), Time[]) == time123
			TypeConverter.convert(map, new TestArray([TIMESTAMP1, TIMESTAMP2, TIMESTAMP3] as      Timestamp[]), Time[]) == time123
	/**/DebugTrace.leave()
	}

	// Array -> Timestamp[]
	def "StandardSpec Array -> Timestamp[]"() {
	/**/DebugTrace.enter()
		setup:
			def timestamp123 = [TIMESTAMP1, TIMESTAMP2, TIMESTAMP3] as Timestamp[]

		expect:
			TypeConverter.convert(map, new TestArray([UTIL_DATE1, UTIL_DATE2, UTIL_DATE3] as java.util.Date[]), Timestamp[]) == timestamp123
			TypeConverter.convert(map, new TestArray([ SQL_DATE1,  SQL_DATE2,  SQL_DATE3] as           Date[]), Timestamp[]) == timestamp123
			TypeConverter.convert(map, new TestArray([     TIME1,      TIME2,      TIME3] as           Time[]), Timestamp[]) == timestamp123
			TypeConverter.convert(map, new TestArray([TIMESTAMP1, TIMESTAMP2, TIMESTAMP3] as      Timestamp[]), Timestamp[]) == timestamp123
	/**/DebugTrace.leave()
	}

	enum Size {XS, S, M, L, XL}

	// -> SqlString
	def "StandardSpec -> SqlString"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, 0                       , SqlString).toString() == '0'
			TypeConverter.convert(map, false                   , SqlString).toString() == 'FALSE'
			TypeConverter.convert(map, true                    , SqlString).toString() == 'TRUE'
			TypeConverter.convert(map, 'A' as char             , SqlString).toString() == "'A'"
			TypeConverter.convert(map, 'ABC'                   , SqlString).toString() == "'ABC'"
			TypeConverter.convert(map, new BigDecimal('123.45'), SqlString).toString() == '123.45'
			TypeConverter.convert(map, 'ABC'                   , SqlString).toString() == "'ABC'"
			TypeConverter.convert(map, UTIL_DATE1              , SqlString).toString() == "DATE'"      + DATE1_STRING      + "'"
			TypeConverter.convert(map, SQL_DATE1               , SqlString).toString() == "DATE'"      + DATE1_STRING      + "'"
			TypeConverter.convert(map, TIME1                   , SqlString).toString() == "TIME'"      + TIME1_STRING      + "'"
			TypeConverter.convert(map, TIMESTAMP1              , SqlString).toString() == "TIMESTAMP'" + TIMESTAMP1_STRING + "'"
			TypeConverter.convert(map, Size.XS                 , SqlString).toString() == "'XS'"
			TypeConverter.convert(map, Size.M                  , SqlString).toString() == "'M'"
			TypeConverter.convert(map, Size.XL                 , SqlString).toString() == "'XL'"

			TypeConverter.convert(map, [true , false, true ] as boolean[], SqlString).toString() == 'ARRAY[TRUE,FALSE,TRUE]'
			TypeConverter.convert(map, ['A'  , 'B'  , 'C'  ] as    char[], SqlString).toString() == "ARRAY['A','B','C']"
			TypeConverter.convert(map, [0x7F , 0x80 ,  0xFF] as    byte[], SqlString).toString() == "X'7F80FF'"
			TypeConverter.convert(map, [-1   ,  0   ,  1   ] as   short[], SqlString).toString() == 'ARRAY[-1,0,1]'
			TypeConverter.convert(map, [-1   ,  0   ,  1   ] as     int[], SqlString).toString() == 'ARRAY[-1,0,1]'
			TypeConverter.convert(map, [-1L  ,  0L  ,  1L  ] as    long[], SqlString).toString() == 'ARRAY[-1,0,1]'
			TypeConverter.convert(map, [-1.5F,  0.0F,  1.5F] as   float[], SqlString).toString() == 'ARRAY[-1.5,0.0,1.5]'
			TypeConverter.convert(map, [-1.5D,  0.0D,  1.5D] as  double[], SqlString).toString() == 'ARRAY[-1.5,0.0,1.5]'
			TypeConverter.convert(map, ['ABC', 'abc', '123'] as  String[], SqlString).toString() == "ARRAY['ABC','abc','123']"

			TypeConverter.convert(map, [new BigDecimal('-123.456'), BigDecimal.ZERO, new BigDecimal('123.456')] as BigDecimal[], SqlString).toString() ==
				'ARRAY[-123.456,0,123.456]'

			TypeConverter.convert(map, [UTIL_DATE1, UTIL_DATE2, UTIL_DATE3] as java.util.Date[], SqlString).toString() ==
				"ARRAY[DATE'" + DATE1_STRING + "',DATE'" + DATE2_STRING + "',DATE'" + DATE3_STRING + "']"

			TypeConverter.convert(map, [SQL_DATE1, SQL_DATE2, SQL_DATE3] as Date[], SqlString).toString() ==
				"ARRAY[DATE'" + DATE1_STRING + "',DATE'" + DATE2_STRING + "',DATE'" + DATE3_STRING + "']"

			TypeConverter.convert(map, [TIME1, TIME2, TIME3] as Time[], SqlString).toString() ==
				"ARRAY[TIME'" + TIME1_STRING + "',TIME'" + TIME2_STRING + "',TIME'" + TIME3_STRING + "']"

			TypeConverter.convert(map, [TIMESTAMP1, TIMESTAMP2, TIMESTAMP3] as Timestamp[], SqlString).toString() ==
				"ARRAY[TIMESTAMP'" + TIMESTAMP1_STRING + "',TIMESTAMP'" + TIMESTAMP2_STRING + "',TIMESTAMP'" + TIMESTAMP3_STRING + "']"

			TypeConverter.convert(map, ['ABC', 123, false, 1.2D], SqlString).toString() == "('ABC',123,FALSE,1.2)"
	/**/DebugTrace.leave()
	}

	// long String -> SqlString
	def "StandardSpec long String -> SqlString"() {
	/**/DebugTrace.enter()
		when:
		/**/DebugTrace.print('maxStringLiteralLength', Standard.maxStringLiteralLength)
			def buff = new StringBuilder(Standard.maxStringLiteralLength + 1)
			for (int index = 0; index < Standard.maxStringLiteralLength; ++index)
				buff.append((char)((char)'A' + (index % 26)))
		/**/DebugTrace.print('buff', buff)

		then:
			TypeConverter.convert(map, buff.toString(), SqlString).toString().startsWith("'ABCDEFGH")

		when: buff.append('+')
		then: TypeConverter.convert(map, buff.toString(), SqlString).toString() == '?'

	/**/DebugTrace.leave()
	}

	// long byte[] -> SqlString
	def "StandardSpec long byte[] -> SqlString"() {
	/**/DebugTrace.enter()
		when:
		/**/DebugTrace.print('maxBinaryLiteralLength', Standard.maxBinaryLiteralLength)
			def bytes = new byte[Standard.maxBinaryLiteralLength]
			for (def index = 0; index < bytes.length; ++index)
				bytes[index] = (byte)(index - 128)
		/**/DebugTrace.print('bytes', bytes)

		then: TypeConverter.convert(map, bytes, SqlString).toString().startsWith("X'8081828384858687")

		when:
			bytes = new byte[Standard.maxBinaryLiteralLength + 1]
			for (def index = 0; index < bytes.length; ++index)
				bytes[index] = (byte)(index - 128)
		/**/DebugTrace.print('bytes', bytes)

		then: TypeConverter.convert(map, bytes, SqlString).toString() == '?'
	/**/DebugTrace.leave()
	}

	// long String[] -> SqlString
	def "StandardSpec long String[] -> SqlString"() {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('maxStringLiteralLength', Standard.maxStringLiteralLength)
		when:
			def buff = new StringBuilder(Standard.maxStringLiteralLength + 1)
			for (def index = 0; index < Standard.maxStringLiteralLength; ++index)
				buff.append((char)(('A' as char) + (index % 26)))
			def strings = ['A', buff.toString(), 'B', buff.toString()] as String[]
			def string = TypeConverter.convert(map, strings, SqlString).toString()
		/**/DebugTrace.print('strings', strings)
		/**/DebugTrace.print('string', string)

		then:
			string.startsWith("ARRAY['A','ABCDEFGH")
			string.endsWith("']")

		when:
			buff.append('+')
			strings = ['A', buff.toString(), 'B', buff.toString()] as String[]
			string = TypeConverter.convert(map, strings, SqlString).toString()
		/**/DebugTrace.print('strings', strings)
		/**/DebugTrace.print('string', string)

		then: string == "ARRAY['A',?,'B',?]"

	/**/DebugTrace.leave()
	}

	// long byte[][] -> SqlString
	def "StandardSpec long byte[][] -> SqlString"() {
	/**/DebugTrace.enter()
		when:
		/**/DebugTrace.print('maxStringLiteralLength', Standard.maxStringLiteralLength)
			def bytes = new byte[Standard.maxBinaryLiteralLength]
			for (def index = 0; index < bytes.length; ++index)
				bytes[index] = (byte)(index - 128)
			def bytesArray = [[(byte)0x80, (byte)0x7F] as byte[], bytes, [(byte)0x81, (byte)0x7E] as byte[], bytes] as byte[][]
			def string = TypeConverter.convert(map, bytesArray, SqlString).toString()
		/**/DebugTrace.print('bytesArray', bytesArray)
		/**/DebugTrace.print('string', string)

		then:
			string.startsWith("ARRAY[X'807F',X'8081828384858687")
			string.endsWith("']")

		when:
			bytes = new byte[Standard.maxBinaryLiteralLength + 1]
			for (def index = 0; index < bytes.length; ++index)
				bytes[index] = (byte)(index - 128)
			bytesArray = [[(byte)0x80, (byte)0x7F] as byte[], bytes, [(byte)0x81, (byte)0x7E] as byte[], bytes] as byte[][]
			string = TypeConverter.convert(map, bytesArray, SqlString).toString()
		/**/DebugTrace.print('bytesArray', bytesArray)
		/**/DebugTrace.print('string', string)

		then: string == "ARRAY[X'807F',?,X'817E',?]"

	/**/DebugTrace.leave()
	}

}
