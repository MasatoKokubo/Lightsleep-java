// TypeConverterTest.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.helper

import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.time.DayOfWeek
import java.util.function.Function
import java.util.concurrent.ConcurrentHashMap

import org.debugtrace.DebugTrace
import org.lightsleep.helper.*

import spock.lang.*

// TypeConverterSpec
@Unroll
class TypeConverterSpec extends Specification {
	enum Size {
	//   0  1  2  3   4
		XS, S, M, L, XL

		static sizeMap = [
			(XS.ordinal()): XS,
			(S .ordinal()): S,
			(M .ordinal()): M,
			(L .ordinal()): L,
			(XL.ordinal()): XL
		]

		static Size valueOf(int ordinal) {
			return sizeMap.get(ordinal)
		}

		static int lastOrdinal() {
			return XL.ordinal()
		}
	}

	@Shared map = new ConcurrentHashMap<>(TypeConverter.typeConverterMap())

	def setupSpec() {
		// Integer -> Size
		TypeConverter.put(map,
			new TypeConverter<>(Integer.class, Size.class, {object ->
				Size size = Size.valueOf(object)
				if (size == null)
					throw new ConvertException(Integer.class, object, Size.class)
				return size
			})
		)

		// Byte -> Size
		TypeConverter.put(map,
			new TypeConverter<>(Byte.class, Size.class,
				TypeConverter.get(map, Byte.class, Integer.class).function()
				.andThen(TypeConverter.get(map, Integer.class, Size.class).function())
			)
		)

		// Short -> Size
		TypeConverter.put(map,
			new TypeConverter<>(Short.class, Size.class,
				TypeConverter.get(map, Short.class, Integer.class).function()
				.andThen(TypeConverter.get(map, Integer.class, Size.class).function())
			)
		)

		// Long -> Size
		TypeConverter.put(map,
			new TypeConverter<>(Long.class, Size.class,
				TypeConverter.get(map, Long.class, Integer.class).function()
				.andThen(TypeConverter.get(map, Integer.class, Size.class).function())
			)
		)
	}

	// key
	def "key"() {
		when:
			TypeConverter.key(null, null)

		then:
			thrown NullPointerException

		when:
			TypeConverter.key(String.class, null)

		then:
			thrown NullPointerException

	}

	// put
	def "put"() {
		when:
			TypeConverter.put(null, null)
		then:
			thrown NullPointerException

		when:
			TypeConverter.put(map, (TypeConverter<?, ?>)null)
		then:
			thrown NullPointerException

		when:
			TypeConverter.put(map, TypeConverter.get(map, String.class, Integer.class))
		then:
			noExceptionThrown()
	}

	// get
	def "get"() {
		when:
			TypeConverter.get(null, null, null)
		then:
			thrown NullPointerException

		expect:
			TypeConverter.get(map, Cloneable.class, Iterable.class) == null
			TypeConverter.get(map, String.class, Integer.class).sourceType() == String.class
			TypeConverter.get(map, String.class, Integer.class).destinType() == Integer.class
			TypeConverter.get(map, String.class, Integer.class).key() == 'String->Integer'
	}

	// equals
	def "equals"() {
		expect:
			TypeConverter.get(map, String.class, Integer.class) != 'AAA'
			TypeConverter.get(map, String.class, Integer.class) != TypeConverter.get(map, Long.class, Integer.class)
			TypeConverter.get(map, String.class, Integer.class) != TypeConverter.get(map, String.class, Long.class)
			TypeConverter.get(map, String.class, Integer.class) == TypeConverter.get(map, String.class, Integer.class)
			TypeConverter.get(map, String.class, Integer.class).hashCode() == TypeConverter.get(map, String.class, Integer.class).hashCode()
	}

	// convert
	def "convert"() {
		expect:
			TypeConverter.convert(map, null, Integer.class) == null
			TypeConverter.convert(map, 1, Integer.class) == 1

		when:
			TypeConverter.convert(map, '1', Date.class)
		then:
			thrown ConvertException
	}

	// constructor
	def "constructor"() {
		when:
			new TypeConverter<>(String.class, Integer.class, (Function<String, Integer>)null)

		then:
			thrown NullPointerException
	}

	// Byte -> Boolean
	def "Byte -> Boolean"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, (byte)-1, Boolean.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, (byte)0, Boolean.class) == false
			TypeConverter.convert(map, (byte)1, Boolean.class) == true

		when:
			TypeConverter.convert(map, (byte)2, Boolean.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Short -> Boolean
	def "Short -> Boolean"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, (short)-1, Boolean.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, (short)0, Boolean.class) == false
			TypeConverter.convert(map, (short)1, Boolean.class) == true

		when:
			TypeConverter.convert(map, (short)2, Boolean.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Integer -> Boolean
	def "Integer -> Boolean"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -1, Boolean.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, 0, Boolean.class) == false
			TypeConverter.convert(map, 1, Boolean.class) == true

		when:
			TypeConverter.convert(map, 2, Boolean.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Long -> Boolean
	def "Long -> Boolean"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -1L, Boolean.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, 0L, Boolean.class) == false
			TypeConverter.convert(map, 1L, Boolean.class) == true

		when:
			TypeConverter.convert(map, 2L, Boolean.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Float -> Boolean
	def "Float -> Boolean"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -1.0F, Boolean.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, 0.0F, Boolean.class) == false
			TypeConverter.convert(map, 1.0F, Boolean.class) == true

		when:
			TypeConverter.convert(map, 2.0F, Boolean.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Double -> Boolean
	def "Double -> Boolean"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -1.0D, Boolean.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, 0.0D, Boolean.class) == false
			TypeConverter.convert(map, 1.0D, Boolean.class) == true

		when:
			TypeConverter.convert(map, 2.0D, Boolean.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// BigDecimal -> Boolean
	def "BigDecimal -> Boolean"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, BigDecimal.valueOf(-1L), Boolean.class)
		then:
			thrown ConvertException

			TypeConverter.convert(map, BigDecimal.valueOf(0L), Boolean.class) == false
			TypeConverter.convert(map, BigDecimal.valueOf(1L), Boolean.class) == true

		when:
			TypeConverter.convert(map, BigDecimal.valueOf(2L), Boolean.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Character -> Boolean
	def "Character -> Boolean"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (char)'0', Boolean.class) == false
			TypeConverter.convert(map, (char)'1', Boolean.class) == true

		when:
			TypeConverter.convert(map, (char)'2', Boolean.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// String -> Boolean
	def "String -> Boolean"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, '', Boolean.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, '0', Boolean.class) == false
			TypeConverter.convert(map, '1', Boolean.class) == true

		when:
			TypeConverter.convert(map, '2', Boolean.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Boolean -> Byte
	def "Boolean -> Byte"() {
	/**/DebugTrace.enter()
			TypeConverter.convert(map, false, Byte.class) == (byte)0
			TypeConverter.convert(map, true , Byte.class) == (byte)1
	/**/DebugTrace.leave()
	}

	// Short -> Byte
	def "Short -> Byte"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, (short)-129, Byte.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, (short)-128, Byte.class) == (byte)-128
			TypeConverter.convert(map, (short) 127, Byte.class) == (byte) 127

		when:
			TypeConverter.convert(map, (short)128, Byte.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Integer -> Byte
	def "Integer -> Byte"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -129, Byte.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, -128, Byte.class) == (byte)-128
			TypeConverter.convert(map,  127, Byte.class) == (byte) 127

		when:
			TypeConverter.convert(map, 128, Byte.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Long -> Byte
	def "Long -> Byte"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -129L, Byte.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, -128L, Byte.class) == (byte)-128
			TypeConverter.convert(map,  127L, Byte.class) == (byte) 127

		when:
			TypeConverter.convert(map, 128L, Byte.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Float -> Byte
	def "Float -> Byte"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -129.0F, Byte.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, -128.0F, Byte.class) == (byte)-128
			TypeConverter.convert(map,  127.0F, Byte.class) == (byte) 127

		when:
			TypeConverter.convert(map, 128.0F, Byte.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Double -> Byte
	def "Double -> Byte"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -129.0D, Byte.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, -128.0D, Byte.class) == (byte)-128
			TypeConverter.convert(map,  127.0D, Byte.class) == (byte) 127

		when:
			TypeConverter.convert(map, 128.0D, Byte.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// BigDecimal -> Byte
	def "BigDecimal -> Byte"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, BigDecimal.valueOf(-129L), Byte.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, BigDecimal.valueOf(-128L), Byte.class) == (byte)-128
			TypeConverter.convert(map, BigDecimal.valueOf( 127L), Byte.class) == (byte) 127

		when:
			TypeConverter.convert(map, BigDecimal.valueOf(128L), Byte.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Character -> Byte
	def "Character -> Byte"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, (char)'\uFF7F', Byte.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, (char)'\uFF80', Byte.class) == (byte)-128
			TypeConverter.convert(map, (char)'\u007F', Byte.class) == (byte) 127

		when:
			TypeConverter.convert(map, (char)'\u0080', Byte.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// String -> Byte
	def "String -> Byte"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, '', Byte.class)
		then:
			thrown ConvertException

		when:
			TypeConverter.convert(map, '-129', Byte.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, '-128', Byte.class) == (byte)-128
			TypeConverter.convert(map,  '127', Byte.class) == (byte) 127

		when:
			TypeConverter.convert(map, '128', Byte.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Boolean -> Short
	def "Boolean -> Short"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, false, Short.class) == (short)0
			TypeConverter.convert(map, true , Short.class) == (short)1
	/**/DebugTrace.leave()
	}

	// Byte -> Short
	def "Byte -> Short"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (byte)-128, Short.class) == (short)-128
			TypeConverter.convert(map, (byte) 127, Short.class) == (short) 127
	/**/DebugTrace.leave()
	}

	// Integer -> Short
	def "Integer -> Short"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -32769, Short.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, -32768, Short.class) == (short)-32768
			TypeConverter.convert(map,  32767, Short.class) == (short) 32767

		when:
			TypeConverter.convert(map, 32768, Short.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Long -> Short
	def "Long -> Short"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -32769L, Short.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, -32768L, Short.class) == (short)-32768
			TypeConverter.convert(map,  32767L, Short.class) == (short) 32767

		when:
			TypeConverter.convert(map, 32768L, Short.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Float -> Short
	def "Float -> Short"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -32769.0F, Short.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, -32768.0F, Short.class) == (short)-32768
			TypeConverter.convert(map,  32767.0F, Short.class) == (short) 32767

		when:
			TypeConverter.convert(map, 32768.0F, Short.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Double -> Short
	def "Double -> Short"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -32769.0D, Short.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, -32768.0D, Short.class) == (short)-32768
			TypeConverter.convert(map,  32767.0D, Short.class) == (short) 32767

		when:
			TypeConverter.convert(map, 32768.0D, Short.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	def "BigDecimal -> Short"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, BigDecimal.valueOf(-32769L), Short.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, BigDecimal.valueOf(-32768L), Short.class) == (short)-32768
			TypeConverter.convert(map, BigDecimal.valueOf( 32767L), Short.class) == (short) 32767

		when:
			TypeConverter.convert(map, BigDecimal.valueOf(32768L), Short.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Character -> Short
	def "Character -> Short"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (char)'\u8000', Short.class) == (short)-32768
			TypeConverter.convert(map, (char)'\u7FFF', Short.class) == (short) 32767
	/**/DebugTrace.leave()
	}

	// String -> Short
	def "String -> Short"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, '', Short.class)
		then:
			thrown ConvertException

		when:
			TypeConverter.convert(map, '-32769', Short.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, '-32768', Short.class) == (short)-32768
			TypeConverter.convert(map,  '32767', Short.class) == (short) 32767

		when:
			TypeConverter.convert(map, '32768', Short.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Boolean -> Integer
	def "Boolean -> Integer"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, false, Integer.class) == 0
			TypeConverter.convert(map, true , Integer.class) == 1
	/**/DebugTrace.leave()
	}

	// Byte -> Integer
	def "Byte -> Integer"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (byte)-128, Integer.class) == -128
			TypeConverter.convert(map, (byte) 127, Integer.class) ==  127
	/**/DebugTrace.leave()
	}

	// Short -> Integer
	def "Short -> Integer"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (short)-32768, Integer.class) == -32768
			TypeConverter.convert(map, (short) 32767, Integer.class) ==  32767
	/**/DebugTrace.leave()
	}

	// Long -> Integer
	def "Long -> Integer"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -2147483649L, Integer.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, -2147483648L, Integer.class) == -2147483648
			TypeConverter.convert(map,  2147483647L, Integer.class) ==  2147483647

		when:
			TypeConverter.convert(map, 2147483648L, Integer.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Float -> Integer
	def "Float -> Integer"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -2147484000.0F, Integer.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, -2147483648.0F, Integer.class) == -2147483648
			TypeConverter.convert(map,  2147483647.0F, Integer.class) ==  2147483647

		when:
			TypeConverter.convert(map, 2147484000.0F, Integer.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Double -> Integer
	def "Double -> Integer"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -2147483649.0D, Integer.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, -2147483648.0D, Integer.class) == -2147483648
			TypeConverter.convert(map,  2147483647.0D, Integer.class) ==  2147483647

		when:
			TypeConverter.convert(map, 2147483648.0D, Integer.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// BigDecimal -> Integer
	def "BigDecimal -> Integer"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, BigDecimal.valueOf(-2147483649L), Integer.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, BigDecimal.valueOf(-2147483648L), Integer.class) == -2147483648
			TypeConverter.convert(map, BigDecimal.valueOf( 2147483647L), Integer.class) ==  2147483647

		when:
			TypeConverter.convert(map, BigDecimal.valueOf(2147483648L), Integer.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Character -> Integer
	def "Character -> Integer"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (char)'\u0000', Integer.class) ==     0
			TypeConverter.convert(map, (char)'\u7FFF', Integer.class) == 32767
	/**/DebugTrace.leave()
	}

	// String -> Integer
	def "String -> Integer"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, '', Integer.class)
		then:
			thrown ConvertException

		when:
			TypeConverter.convert(map, '-2147483649', Integer.class)
		then:
			thrown ConvertException

			TypeConverter.convert(map, '-2147483648', Integer.class) == -2147483648
			TypeConverter.convert(map,  '2147483647', Integer.class) ==  2147483647

		when:
			TypeConverter.convert(map, '2147483648', Integer.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// java.util.Date -> Integer
	def "utilDate -> Integer"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new java.util.Date((( 0*60+ 0)*60+ 0)*1000L    ), Integer.class) == (( 0*60+ 0)*60+ 0)*1000
			TypeConverter.convert(map, new java.util.Date(((23*60+59)*60+59)*1000L+999), Integer.class) == ((23*60+59)*60+59)*1000+999

			TypeConverter.convert(map, new java.sql.Date ((( 0*60+ 0)*60+ 0)*1000L    ), Integer.class) == (( 0*60+ 0)*60+ 0)*1000
			TypeConverter.convert(map, new java.sql.Date (((23*60+59)*60+59)*1000L+999), Integer.class) == ((23*60+59)*60+59)*1000+999

			TypeConverter.convert(map, new Time          ((( 0*60+ 0)*60+ 0)*1000L    ), Integer.class) == (( 0*60+ 0)*60+ 0)*1000
			TypeConverter.convert(map, new Time          (((23*60+59)*60+59)*1000L+999), Integer.class) == ((23*60+59)*60+59)*1000+999

			TypeConverter.convert(map, new Timestamp     ((( 0*60+ 0)*60+ 0)*1000L    ), Integer.class) == (( 0*60+ 0)*60+ 0)*1000
			TypeConverter.convert(map, new Timestamp     (((23*60+59)*60+59)*1000L+999), Integer.class) == ((23*60+59)*60+59)*1000+999
	/**/DebugTrace.leave()
	}

	// Boolean -> Long
	def "Boolean -> Long"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, false, Long.class) == 0L
			TypeConverter.convert(map, true , Long.class) == 1L
	/**/DebugTrace.leave()
	}

	// Byte -> Long
	def "Byte -> Long"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (byte)-128, Long.class) == -128L
			TypeConverter.convert(map, (byte) 127, Long.class) ==  127L
	/**/DebugTrace.leave()
	}

	// Short -> Long
	def "Short -> Long"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (short)-32768, Long.class) == -32768L
			TypeConverter.convert(map, (short) 32767, Long.class) ==  32767L
	/**/DebugTrace.leave()
	}

	// Integer -> Long
	def "Integer -> Long"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, -2147483648, Long.class) == -2147483648L
			TypeConverter.convert(map,  2147483647, Long.class) ==  2147483647L
	/**/DebugTrace.leave()
	}

	// Float -> Long
	def "Float -> Long"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -9223373000000000000.0F, Long.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, -9223372036854775808.0F, Long.class) == -9223372036854775808L
			TypeConverter.convert(map,  9223372036854775807.0F, Long.class) ==  9223372036854775807L

		when:
			TypeConverter.convert(map, 9223373000000000000.0F, Long.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Double -> Long
	def "Double -> Long"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -9223372036854780000.0D, Long.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, -9223372036854775808.0D, Long.class) == -9223372036854775808L
			TypeConverter.convert(map,  9223372036854775807.0D, Long.class) ==  9223372036854775807L

		when:
			TypeConverter.convert(map, 9223372036854780000.0D, Long.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// BigDecimal -> Long
	def "BigDecimal -> Long"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, new BigDecimal('-9223372036854775809'), Long.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, BigDecimal.valueOf(-9223372036854775808L), Long.class) == -9223372036854775808L
			TypeConverter.convert(map, BigDecimal.valueOf( 9223372036854775807L), Long.class) ==  9223372036854775807L

		when:
			TypeConverter.convert(map, new BigDecimal('9223372036854775808'), Long.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Character -> Long
	def "Character -> Long"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (char)'\u0000', Long.class) ==     0L
			TypeConverter.convert(map, (char)'\u7FFF', Long.class) == 32767L
	/**/DebugTrace.leave()
	}

	// String -> Long
	def "String -> Long"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, '', Long.class)
		then:
			thrown ConvertException

		when:
			TypeConverter.convert(map, '-9223372036854775809', Long.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, '-9223372036854775808', Long.class) == -9223372036854775808L
			TypeConverter.convert(map,  '9223372036854775807', Long.class) ==  9223372036854775807L

		when:
			TypeConverter.convert(map, '9223372036854775808', Long.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// java.util.Date -> Long
	def "utilDate -> Long"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new java.util.Date((( 0*60+ 0)*60+ 0)*1000L    ), Long.class) == (( 0*60+ 0)*60+ 0)*1000L
			TypeConverter.convert(map, new java.util.Date(((23*60+59)*60+59)*1000L+999), Long.class) == ((23*60+59)*60+59)*1000L+999

			TypeConverter.convert(map, new java.sql.Date ((( 0*60+ 0)*60+ 0)*1000L    ), Long.class) == (( 0*60+ 0)*60+ 0)*1000L
			TypeConverter.convert(map, new java.sql.Date (((23*60+59)*60+59)*1000L+999), Long.class) == ((23*60+59)*60+59)*1000L+999

			TypeConverter.convert(map, new Time          ((( 0*60+ 0)*60+ 0)*1000L    ), Long.class) == (( 0*60+ 0)*60+ 0)*1000L
			TypeConverter.convert(map, new Time          (((23*60+59)*60+59)*1000L+999), Long.class) == ((23*60+59)*60+59)*1000L+999

			TypeConverter.convert(map, new Timestamp     ((( 0*60+ 0)*60+ 0)*1000L    ), Long.class) == (( 0*60+ 0)*60+ 0)*1000L
			TypeConverter.convert(map, new Timestamp     (((23*60+59)*60+59)*1000L+999), Long.class) == ((23*60+59)*60+59)*1000L+999
	/**/DebugTrace.leave()
	}

	// Boolean -> Float
	def "Boolean -> Float"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, false, Float.class) == 0.0F
			TypeConverter.convert(map, true , Float.class) == 1.0F
	/**/DebugTrace.leave()
	}

	// Byte -> Float
	def "Byte -> Float"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (byte)-128, Float.class) == -128.0F
			TypeConverter.convert(map, (byte) 127, Float.class) ==  127.0F
	/**/DebugTrace.leave()
	}

	// Short -> Float
	def "Short -> Float"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (short)-32768, Float.class) == -32768.0F
			TypeConverter.convert(map, (short) 32767, Float.class) ==  32767.0F
	/**/DebugTrace.leave()
	}

	// Integer -> Float
	def "Integer -> Float"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, -2147483000, Float.class) == -2147483000.0F
			TypeConverter.convert(map,  2147483000, Float.class) ==  2147483000.0F
	/**/DebugTrace.leave()
	}

	// Long -> Float
	def "Long -> Float"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, -9223372000000000000L, Float.class) == -9223372000000000000.0F
			TypeConverter.convert(map,  9223372000000000000L, Float.class) ==  9223372000000000000.0F
	/**/DebugTrace.leave()
	}

	// Double -> Float
	def "Double -> Float"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, -1234567.0D, Float.class) == -1234567.0F
			TypeConverter.convert(map,  1234567.0D, Float.class) ==  1234567.0F
	/**/DebugTrace.leave()
	}

	// BigDecimal -> Float
	def "BigDecimal -> Float"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new BigDecimal('-1234.125'), Float.class) == -1234.125F
			TypeConverter.convert(map, new BigDecimal( '1234.125'), Float.class) ==  1234.125F
	/**/DebugTrace.leave()
	}

	// Character -> Float
	def "Character -> Float"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (char)'\u0000', Float.class) ==     0.0F
			TypeConverter.convert(map, (char)'\u7FFF', Float.class) == 32767.0F
	/**/DebugTrace.leave()
	}

	// String -> Float
	def "String -> Float"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, '', Float.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, '-1234.125', Float.class) == -1234.125F
			TypeConverter.convert(map,  '1234.125', Float.class) ==  1234.125F
	/**/DebugTrace.leave()
	}

	// Boolean -> Double
	def "Boolean -> Double"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, false, Double.class) == 0.0
			TypeConverter.convert(map, true , Double.class) == 1.0
	/**/DebugTrace.leave()
	}

	// Byte -> Double
	def "Byte -> Double"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (byte)-128, Double.class) == -128.0
			TypeConverter.convert(map, (byte) 127, Double.class) ==  127.0
	/**/DebugTrace.leave()
	}

	// Short -> Double
	def "Short -> Double"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (short)-32768, Double.class) == -32768.0
			TypeConverter.convert(map, (short) 32767, Double.class) ==  32767.0
	/**/DebugTrace.leave()
	}

	// Integer -> Double
	def "Integer -> Double"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, -2147483648, Double.class) == -2147483648.0
			TypeConverter.convert(map,  2147483647, Double.class) ==  2147483647.0
	/**/DebugTrace.leave()
	}

	// Long -> Double
	def "Long -> Double"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, -9223372036854770000L, Double.class) == -9223372036854770000.0
			TypeConverter.convert(map,  9223372036854770000L, Double.class) ==  9223372036854770000.0
	/**/DebugTrace.leave()
	}

	// Float -> Double
	def "Float -> Double"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, -1234567.0F, Double.class) == -1234567.0
			TypeConverter.convert(map,  1234567.0F, Double.class) ==  1234567.0
	/**/DebugTrace.leave()
	}

	// BigDecimal -> Double
	def "BigDecimal -> Double"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new BigDecimal('-12345678901.0625'), Double.class) == -12345678901.0625
			TypeConverter.convert(map, new BigDecimal( '12345678901.0625'), Double.class) ==  12345678901.0625
	/**/DebugTrace.leave()
	}

	// Character -> Double
	def "Character -> Double"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (char)'\u0000', Double.class) ==     0.0
			TypeConverter.convert(map, (char)'\u7FFF', Double.class) == 32767.0
	/**/DebugTrace.leave()
	}

	// String -> Double
	def "String -> Double"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, '', Double.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, '-12345678901.0625', Double.class) == -12345678901.0625
			TypeConverter.convert(map,  '12345678901.0625', Double.class) ==  12345678901.0625
	/**/DebugTrace.leave()
	}

	// Boolean -> BigDecimal
	def "Boolean -> BigDecimal"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, false, BigDecimal.class) == BigDecimal.ZERO
			TypeConverter.convert(map, true , BigDecimal.class) == BigDecimal.ONE
	/**/DebugTrace.leave()
	}

	// Byte -> BigDecimal
	def "Byte -> BigDecimal"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (byte)-128, BigDecimal.class) == BigDecimal.valueOf(-128L)
			TypeConverter.convert(map, (byte) 127, BigDecimal.class) == BigDecimal.valueOf( 127L)
	/**/DebugTrace.leave()
	}

	// Short -> BigDecimal
	def "Short -> BigDecimal"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (short)-32768, BigDecimal.class) == BigDecimal.valueOf(-32768L)
			TypeConverter.convert(map, (short) 32767, BigDecimal.class) == BigDecimal.valueOf( 32767L)
	/**/DebugTrace.leave()
	}

	// Integer -> BigDecimal
	def "Integer -> BigDecimal"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, -2147483648, BigDecimal.class) == BigDecimal.valueOf(-2147483648L)
			TypeConverter.convert(map,  2147483647, BigDecimal.class) == BigDecimal.valueOf( 2147483647L)
	/**/DebugTrace.leave()
	}

	// Long -> BigDecimal
	def "Long -> BigDecimal"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, -9223372036854775808L, BigDecimal.class) == BigDecimal.valueOf(-9223372036854775808L)
			TypeConverter.convert(map,  9223372036854775807L, BigDecimal.class) == BigDecimal.valueOf( 9223372036854775807L)
	/**/DebugTrace.leave()
	}

	// Float -> BigDecimal
	def "Float -> BigDecimal"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, -1234.125F, BigDecimal.class) == new BigDecimal('-1234.125')
			TypeConverter.convert(map,  1234.125F, BigDecimal.class) == new BigDecimal( '1234.125')
	/**/DebugTrace.leave()
	}

	// Double -> BigDecimal
	def "Double -> BigDecimal"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, -12345678901.0625D, BigDecimal.class) == new BigDecimal('-12345678901.0625')
			TypeConverter.convert(map,  12345678901.0625D, BigDecimal.class) == new BigDecimal( '12345678901.0625')
	/**/DebugTrace.leave()
	}

	// Character -> BigDecimal
	def "Character -> BigDecimal"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (char)'\u0000', BigDecimal.class) == BigDecimal.ZERO
			TypeConverter.convert(map, (char)'\u7FFF', BigDecimal.class) == BigDecimal.valueOf(32767L)
	/**/DebugTrace.leave()
	}

	// String -> BigDecimal
	def "String -> BigDecimal"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, '', BigDecimal.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, '-12345678901234567890.1234567890', BigDecimal.class) == new BigDecimal('-12345678901234567890.1234567890')
			TypeConverter.convert(map,  '12345678901234567890.1234567890', BigDecimal.class) == new BigDecimal( '12345678901234567890.1234567890')
	/**/DebugTrace.leave()
	}

	// Boolean -> Character
	def "Boolean -> Character"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, false, Character.class) == (char)'0'
			TypeConverter.convert(map, true , Character.class) == (char)'1'
	/**/DebugTrace.leave()
	}

	// Byte -> Character
	def "Byte -> Character"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (byte)-128, Character.class) == (char)'\uFF80'
			TypeConverter.convert(map, (byte) 127, Character.class) == (char)'\u007F'
	/**/DebugTrace.leave()
	}

	// Short -> Character
	def "Short -> Character"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (short)-32768, Character.class) == (char)'\u8000'
			TypeConverter.convert(map, (short) 32767, Character.class) == (char)'\u7FFF'
	/**/DebugTrace.leave()
	}

	// Integer -> Character
	def "Integer -> Character"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -1, Character.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map,      0, Character.class) == (char)'\u0000'
			TypeConverter.convert(map,  32767, Character.class) == (char)'\u7FFF'

		when:
			TypeConverter.convert(map, 65536, Character.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Long -> Character
	def "Long -> Character"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -1L, Character.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map,    -0L, Character.class) == (char)'\u0000'
			TypeConverter.convert(map, 32767L, Character.class) == (char)'\u7FFF'

		when:
			TypeConverter.convert(map, 65536L, Character.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Float -> Character
	def "Float -> Character"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -1.0F, Character.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map,    -0.0F, Character.class) == (char)'\u0000'
			TypeConverter.convert(map, 32767.0F, Character.class) == (char)'\u7FFF'

		when:
			TypeConverter.convert(map, 65536.0F, Character.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Double -> Character
	def "Double -> Character"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -1.0D, Character.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map,    -0.0D, Character.class) == (char)'\u0000'
			TypeConverter.convert(map, 32767.0D, Character.class) == (char)'\u7FFF'

		when:
			TypeConverter.convert(map, 65536.0D, Character.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// BigDecimal -> Character
	def "BigDecimal -> Character"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, BigDecimal.valueOf(-1L), Character.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, BigDecimal.valueOf(   -0L), Character.class) == (char)'\u0000'
			TypeConverter.convert(map, BigDecimal.valueOf(32767L), Character.class) == (char)'\u7FFF'

		when:
			TypeConverter.convert(map, BigDecimal.valueOf(65536L), Character.class)
		then:
			thrown ConvertException

		when:
			TypeConverter.convert(map, new BigDecimal('0.1'), Character.class)
		then:
			thrown ConvertException

	/**/DebugTrace.leave()
	}

	// String -> Character
	def "String -> Character"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, '', Character.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, '\u0000', Character.class) == (char)'\u0000'
			TypeConverter.convert(map, '\\'    , Character.class) == (char)'\\'
			TypeConverter.convert(map, 'A'     , Character.class) == (char)'A'
			TypeConverter.convert(map, '漢'    , Character.class) == (char)'漢'

		when:
			TypeConverter.convert(map, 'AA', Character.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	static class Foo {
		@Override
		public String toString() {return 'aFoo'}
	}

	// Object -> String
	def "Object -> String"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new Foo(), String.class) == 'aFoo'
	/**/DebugTrace.leave()
	}

	// Boolean -> String
	def "Boolean -> String"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, false, String.class) == 'false'
			TypeConverter.convert(map, true , String.class) == 'true'
	/**/DebugTrace.leave()
	}

	// Byte -> String
	def "Byte -> String"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (byte)-128, String.class) == '-128'
			TypeConverter.convert(map, (byte) 127, String.class) ==  '127'
	/**/DebugTrace.leave()
	}

	// Short -> String
	def "Short -> String"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (short)-32768, String.class) == '-32768'
			TypeConverter.convert(map, (short) 32767, String.class) ==  '32767'
	/**/DebugTrace.leave()
	}

	// Integer -> String
	def "Integer -> String"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, -2147483648, String.class) == '-2147483648'
			TypeConverter.convert(map,  2147483647, String.class) ==  '2147483647'
	/**/DebugTrace.leave()
	}

	// Long -> String
	def "Long -> String"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, -9223372036854775808L, String.class) == '-9223372036854775808'
			TypeConverter.convert(map,  9223372036854775807L, String.class) ==  '9223372036854775807'
	/**/DebugTrace.leave()
	}

	// Float -> String
	def "Float -> String"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, -1234.125F, String.class) == '-1234.125'
			TypeConverter.convert(map,  1234.125F, String.class) ==  '1234.125'
	/**/DebugTrace.leave()
	}

	// Double -> String
	def "Double -> String"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, -12345678901.0625D, String.class) == '-1.23456789010625E10'
			TypeConverter.convert(map,  12345678901.0625D, String.class) ==  '1.23456789010625E10'
	/**/DebugTrace.leave()
	}

	// BigDecimal -> String
	def "BigDecimal -> String"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new BigDecimal('-12345678901234567890.1234567890'), String.class) == '-12345678901234567890.1234567890'
			TypeConverter.convert(map, new BigDecimal( '12345678901234567890.1234567890'), String.class) ==  '12345678901234567890.1234567890'
	/**/DebugTrace.leave()
	}

	// Character -> String
	def "Character -> String"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, '\u0000', String.class) == '\u0000'
			TypeConverter.convert(map, '\\', String.class) == '\\'
			TypeConverter.convert(map, 'A' , String.class) == 'A'
			TypeConverter.convert(map, '漢', String.class) == '漢'
	/**/DebugTrace.leave()
	}

	// java.util.Date -> String
	def "utilDate -> String"() {
	/**/DebugTrace.enter()
		setup:
			def timeZone = TimeZone.getDefault()

		when:
			TimeZone.setDefault(TimeZone.getTimeZone('GMT-6'))

		then:
			TypeConverter.convert(map, new java.util.Date((( 0*60+ 0)*60+ 0)*1000L), String.class) == '1969-12-31'
			TypeConverter.convert(map, new java.util.Date(((23*60+59)*60+59)*1000L), String.class) == '1970-01-01'

		when:
			TimeZone.setDefault(TimeZone.getTimeZone('GMT'))

		then:
			TypeConverter.convert(map, new java.util.Date((( 0*60+ 0)*60+ 0)*1000L), String.class) == '1970-01-01'
			TypeConverter.convert(map, new java.util.Date(((23*60+59)*60+59)*1000L), String.class) == '1970-01-01'

		when:
			TimeZone.setDefault(TimeZone.getTimeZone('GMT+6'))

		then:
			TypeConverter.convert(map, new java.util.Date((( 0*60+ 0)*60+ 0)*1000L), String.class) == '1970-01-01'
			TypeConverter.convert(map, new java.util.Date(((23*60+59)*60+59)*1000L), String.class) == '1970-01-02'

		cleanup:
			TimeZone.setDefault(timeZone)
	/**/DebugTrace.leave()
	}

	// java.sql.Date -> String
	def "sqlDate -> String"() {
	/**/DebugTrace.enter()
		setup:
			def timeZone = TimeZone.getDefault()

		when:
			TimeZone.setDefault(TimeZone.getTimeZone('GMT-6'))

		then:
			TypeConverter.convert(map, new Date((( 0*60+ 0)*60+ 0)*1000L), String.class) == '1969-12-31'
			TypeConverter.convert(map, new Date(((23*60+59)*60+59)*1000L), String.class) == '1970-01-01'

		when:
			TimeZone.setDefault(TimeZone.getTimeZone('GMT'))

		then:
			TypeConverter.convert(map, new Date((( 0*60+ 0)*60+ 0)*1000L), String.class) == '1970-01-01'
			TypeConverter.convert(map, new Date(((23*60+59)*60+59)*1000L), String.class) == '1970-01-01'

		when:
			TimeZone.setDefault(TimeZone.getTimeZone('GMT+6'))

		then:
			TypeConverter.convert(map, new Date((( 0*60+ 0)*60+ 0)*1000L), String.class) == '1970-01-01'
			TypeConverter.convert(map, new Date(((23*60+59)*60+59)*1000L), String.class) == '1970-01-02'

		cleanup:
			TimeZone.setDefault(timeZone)
	/**/DebugTrace.leave()
	}

	// Time -> String
	def "Time -> String"() {
	/**/DebugTrace.enter()
		setup:
			def timeZone = TimeZone.getDefault()

		when:
			TimeZone.setDefault(TimeZone.getTimeZone('GMT-6'))

		then:
			TypeConverter.convert(map, new Time((( 0*60+ 0)*60+ 0)*1000L), String.class) == '18:00:00'
			TypeConverter.convert(map, new Time(((23*60+59)*60+59)*1000L), String.class) == '17:59:59'

		when:
			TimeZone.setDefault(TimeZone.getTimeZone('GMT'))

		then:
			TypeConverter.convert(map, new Time((( 0*60+ 0)*60+ 0)*1000L), String.class) == '00:00:00'
			TypeConverter.convert(map, new Time(((23*60+59)*60+59)*1000L), String.class) == '23:59:59'

		when:
			TimeZone.setDefault(TimeZone.getTimeZone('GMT+6'))

		then:
			TypeConverter.convert(map, new Time((( 0*60+ 0)*60+ 0)*1000L), String.class) == '06:00:00'
			TypeConverter.convert(map, new Time(((23*60+59)*60+59)*1000L), String.class) == '05:59:59'

		cleanup:
			TimeZone.setDefault(timeZone)
	/**/DebugTrace.leave()
	}

	// Timestamp -> String
	def "Timestamp -> String"() {
	/**/DebugTrace.enter()
		setup:
			def timeZone = TimeZone.getDefault()

		when:
			TimeZone.setDefault(TimeZone.getTimeZone('GMT-6'))

		then:
			TypeConverter.convert(map, new Timestamp((( 0*60+ 0)*60+ 0)*1000L    ), String.class) == '1969-12-31 18:00:00.000'
			TypeConverter.convert(map, new Timestamp(((23*60+59)*60+59)*1000L+900), String.class) == '1970-01-01 17:59:59.900'
			TypeConverter.convert(map, new Timestamp(((23*60+59)*60+59)*1000L+990), String.class) == '1970-01-01 17:59:59.990'
			TypeConverter.convert(map, new Timestamp(((23*60+59)*60+59)*1000L+999), String.class) == '1970-01-01 17:59:59.999'

		when:
			TimeZone.setDefault(TimeZone.getTimeZone('GMT'))

		then:
			TypeConverter.convert(map, new Timestamp((( 0*60+ 0)*60+ 0)*1000L    ), String.class) == '1970-01-01 00:00:00.000'
			TypeConverter.convert(map, new Timestamp(((23*60+59)*60+59)*1000L+999), String.class) == '1970-01-01 23:59:59.999'

		when:
			TimeZone.setDefault(TimeZone.getTimeZone('GMT+6'))

		then:
			TypeConverter.convert(map, new Timestamp((( 0*60+ 0)*60+ 0)*1000L    ), String.class) == '1970-01-01 06:00:00.000'
			TypeConverter.convert(map, new Timestamp(((23*60+59)*60+59)*1000L+999), String.class) == '1970-01-02 05:59:59.999'

		cleanup:
			TimeZone.setDefault(timeZone)
	/**/DebugTrace.leave()
	}

	// Long -> java.util.Date
	def "Long -> java.util.Date"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (( 0*60+ 0)*60+ 0)*1000L    , java.util.Date.class) == new java.util.Date((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, ((23*60+59)*60+59)*1000L+999, java.util.Date.class) == new java.util.Date(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// Integer -> java.util.Date (since 1.8.0)
	def "Integer -> java.util.Date"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (( 0*60+ 0)*60+ 0)*1000    , java.util.Date.class) == new java.util.Date((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, ((23*60+59)*60+59)*1000+999, java.util.Date.class) == new java.util.Date(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// java.sql.Date -> java.util.Date (cast) (since 1.4.0)
	def "sqlDate -> java.util.Date"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new Date((( 0*60+ 0)*60+ 0)*1000L    ), java.util.Date.class) == new Date((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, new Date(((23*60+59)*60+59)*1000L+999), java.util.Date.class) == new Date(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// Time -> java.util.Date (cast)
	def "Time -> java.util.Date"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new Time((( 0*60+ 0)*60+ 0)*1000L    ), java.util.Date.class) == new Time((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, new Time(((23*60+59)*60+59)*1000L+999), java.util.Date.class) == new Time(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// Timestamp -> java.util.Date (cast)
	def "Timestamp -> java.util.Date"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new Timestamp((( 0*60+ 0)*60+ 0)*1000L    ), java.util.Date.class) == new Timestamp((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, new Timestamp(((23*60+59)*60+59)*1000L+999), java.util.Date.class) == new Timestamp(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// String -> java.util.Date
	def "String -> java.util.Date"() {
	/**/DebugTrace.enter()
		setup:
			def timeZone = TimeZone.getDefault()

		when:
			TimeZone.setDefault(TimeZone.getTimeZone('GMT'))

		then:
			TypeConverter.convert(map, '1969-12-31', java.util.Date.class) == new java.util.Date(-1*24*60*60*1000L)
			TypeConverter.convert(map, '1970-01-01', java.util.Date.class) == new java.util.Date( 0*24*60*60*1000L)
			TypeConverter.convert(map, '1970-01-02', java.util.Date.class) == new java.util.Date( 1*24*60*60*1000L)

		when:
			TypeConverter.convert(map, '1970-01-XX', java.util.Date.class)
		then:
			thrown ConvertException

		cleanup:
			TimeZone.setDefault(timeZone)
	/**/DebugTrace.leave()
	}

	// Long -> java.sql.Date (since 1.8.0)
	def "Long -> java.sql.Date"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (( 0*60+ 0)*60+ 0)*1000L    , Date.class) == new Date((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, ((23*60+59)*60+59)*1000L+999, Date.class) == new Date(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// Integer -> java.sql.Date
	def "Integer -> java.sql.Date"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (( 0*60+ 0)*60+ 0)*1000    , Date.class) == new Date((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, ((23*60+59)*60+59)*1000+999, Date.class) == new Date(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// Time -> java.sql.Date
	def "Time -> java.sql.Date"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new Time((( 0*60+ 0)*60+ 0)*1000L    ), Date.class) == new Date((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, new Time(((23*60+59)*60+59)*1000L+999), Date.class) == new Date(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// Timestamp -> java.sql.Date
	def "Timestamp -> java.sql.Date"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new Timestamp((( 0*60+ 0)*60+ 0)*1000L    ), Date.class) == new Date((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, new Timestamp(((23*60+59)*60+59)*1000L+999), Date.class) == new Date(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// String -> java.sql.Date
	def "String -> java.sql.Date"() {
	/**/DebugTrace.enter()
		setup:
			def timeZone = TimeZone.getDefault()

		when:
			TimeZone.setDefault(TimeZone.getTimeZone('GMT'))

		then:
			TypeConverter.convert(map, '1969-12-31', Date.class) == new Date(-1*24*60*60*1000L)
			TypeConverter.convert(map, '1970-01-01', Date.class) == new Date( 0*24*60*60*1000L)
			TypeConverter.convert(map, '1970-01-02', Date.class) == new Date( 1*24*60*60*1000L)

		when:
			TypeConverter.convert(map, '1970-01-XX', Date.class)
		then:
			thrown ConvertException

		TimeZone.setDefault(timeZone)
	/**/DebugTrace.leave()
	}

	// Long -> Time
	def "Long -> Time"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (( 0*60+ 0)*60+ 0)*1000L    , Time.class) == new Time((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, ((23*60+59)*60+59)*1000L+999, Time.class) == new Time(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// Integer -> Time (since 1.8.0)
	def "Integer -> Time"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (( 0*60+ 0)*60+ 0)*1000    , Time.class) == new Time((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, ((23*60+59)*60+59)*1000+999, Time.class) == new Time(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// java.util.Date -> Time
	def "utilDate -> Time"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new java.util.Date((( 0*60+ 0)*60+ 0)*1000L    ), Time.class) == new Time((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, new java.util.Date(((23*60+59)*60+59)*1000L+999), Time.class) == new Time(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// java.sql.Date -> Time
	def "sqlDate -> Time"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new Date((( 0*60+ 0)*60+ 0)*1000L    ), Time.class) == new Time((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, new Date(((23*60+59)*60+59)*1000L+999), Time.class) == new Time(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// Timestamp -> Time
	def "Timestamp -> Time"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new Timestamp((( 0*60+ 0)*60+ 0)*1000L    ), Time.class) == new Time((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, new Timestamp(((23*60+59)*60+59)*1000L+999), Time.class) == new Time(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// String -> Time
	def "String -> Time"() {
	/**/DebugTrace.enter()
		setup:
			def timeZone = TimeZone.getDefault()

		when:
			TimeZone.setDefault(TimeZone.getTimeZone('GMT'))

		then:
			TypeConverter.convert(map, '00:00:00', Time.class) == new Time((( 0*60+ 0)*60+ 0)*1000L)
			TypeConverter.convert(map, '23:59:59', Time.class) == new Time(((23*60+59)*60+59)*1000L)

		when:
			TypeConverter.convert(map, '23:59:AA', Time.class)
		then:
			thrown ConvertException

		cleanup:
			TimeZone.setDefault(timeZone)
	/**/DebugTrace.leave()
	}

	// Long -> Timestamp
	def "Long -> Timestamp"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (( 0*60+ 0)*60+ 0)*1000L    , Timestamp.class) == new Timestamp((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, ((23*60+59)*60+59)*1000L+999, Timestamp.class) == new Timestamp(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// Integer -> Timestamp (since 1.8.0)
	def "Integer -> Timestamp"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, (( 0*60+ 0)*60+ 0)*1000    , Timestamp.class) == new Timestamp((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, ((23*60+59)*60+59)*1000+999, Timestamp.class) == new Timestamp(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// java.util.Date -> Timestamp
	def "utilDate -> Timestamp"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new java.util.Date((( 0*60+ 0)*60+ 0)*1000L    ), Timestamp.class) == new Timestamp((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, new java.util.Date(((23*60+59)*60+59)*1000L+999), Timestamp.class) == new Timestamp(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// java.sql.Date -> Timestamp
	def "sqlDate -> Timestamp"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new Date((( 0*60+ 0)*60+ 0)*1000L    ), Timestamp.class) == new Timestamp((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, new Date(((23*60+59)*60+59)*1000L+999), Timestamp.class) == new Timestamp(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// Time -> Timestamp
	def "Time -> Timestamp"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, new Time((( 0*60+ 0)*60+ 0)*1000L    ), Timestamp.class) == new Timestamp((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, new Time(((23*60+59)*60+59)*1000L+999), Timestamp.class) == new Timestamp(((23*60+59)*60+59)*1000L+999)
	/**/DebugTrace.leave()
	}

	// String -> Timestamp
	def "String -> Timestamp"() {
	/**/DebugTrace.enter()
		setup:
			def timeZone = TimeZone.getDefault()

		when:
			TimeZone.setDefault(TimeZone.getTimeZone('GMT'))

		then:
			TypeConverter.convert(map, '1970-01-01 00:00:00.000', Timestamp.class) == new Timestamp((( 0*60+ 0)*60+ 0)*1000L    )
			TypeConverter.convert(map, '1970-01-01 23:59:59.999', Timestamp.class) == new Timestamp(((23*60+59)*60+59)*1000L+999)

		when:
			TypeConverter.convert(map, '1970-01-31 00:00:XX', Timestamp.class)
		then:
			thrown ConvertException

		cleanup:
			TimeZone.setDefault(timeZone)
	/**/DebugTrace.leave()
	}

	// Enum -> String
	def "Enum -> String"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, Character.UnicodeScript.HIRAGANA, String.class) == 'HIRAGANA'
			TypeConverter.convert(map, Thread.State.RUNNABLE, String.class) == 'RUNNABLE'
			TypeConverter.convert(map, DayOfWeek.SUNDAY, String.class) == 'SUNDAY'
	/**/DebugTrace.leave()
	}

	// Enum -> Byte (since 1.4.0)
	def "Enum -> Byte"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, Size.XS, Byte.class) == (byte)0
			TypeConverter.convert(map, Size.S , Byte.class) == (byte)1
			TypeConverter.convert(map, Size.M , Byte.class) == (byte)2
			TypeConverter.convert(map, Size.L , Byte.class) == (byte)3
			TypeConverter.convert(map, Size.XL, Byte.class) == (byte)4
	/**/DebugTrace.leave()
	}

	// Enum -> Short (since 1.4.0)
	def "Enum -> Short"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, Size.XS, Short.class) == (short)0
			TypeConverter.convert(map, Size.S , Short.class) == (short)1
			TypeConverter.convert(map, Size.M , Short.class) == (short)2
			TypeConverter.convert(map, Size.L , Short.class) == (short)3
			TypeConverter.convert(map, Size.XL, Short.class) == (short)4
	/**/DebugTrace.leave()
	}

	// Enum -> Integer (since 1.4.0)
	def "Enum -> Integer"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, Size.XS, Integer.class) == 0
			TypeConverter.convert(map, Size.S , Integer.class) == 1
			TypeConverter.convert(map, Size.M , Integer.class) == 2
			TypeConverter.convert(map, Size.L , Integer.class) == 3
			TypeConverter.convert(map, Size.XL, Integer.class) == 4
	/**/DebugTrace.leave()
	}

	// Enum -> Long (since 1.4.0)
	def "Enum -> Long"() {
	/**/DebugTrace.enter()
		expect:
			TypeConverter.convert(map, Size.XS, Long.class) == 0L
			TypeConverter.convert(map, Size.S , Long.class) == 1L
			TypeConverter.convert(map, Size.M , Long.class) == 2L
			TypeConverter.convert(map, Size.L , Long.class) == 3L
			TypeConverter.convert(map, Size.XL, Long.class) == 4L
	/**/DebugTrace.leave()
	}

	// Byte -> Size (since 1.4.0)
	def "Byte -> Size"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, (byte)-1, Size.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, (byte)0, Size.class) == Size.XS
			TypeConverter.convert(map, (byte)1, Size.class) == Size.S
			TypeConverter.convert(map, (byte)2, Size.class) == Size.M
			TypeConverter.convert(map, (byte)3, Size.class) == Size.L
			TypeConverter.convert(map, (byte)4, Size.class) == Size.XL

		when:
			TypeConverter.convert(map, (byte)(Size.lastOrdinal() + 1), Size.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Short -> Size (since 1.4.0)
	def "Short -> Size"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, (short)-1, Size.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, (short)0, Size.class) == Size.XS
			TypeConverter.convert(map, (short)1, Size.class) == Size.S
			TypeConverter.convert(map, (short)2, Size.class) == Size.M
			TypeConverter.convert(map, (short)3, Size.class) == Size.L
			TypeConverter.convert(map, (short)4, Size.class) == Size.XL

		when:
			TypeConverter.convert(map, (short)(Size.lastOrdinal() + 1), Size.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Integer -> Size (since 1.4.0)
	def "Integer -> Size"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -1, Size.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, 0, Size.class) == Size.XS
			TypeConverter.convert(map, 1, Size.class) == Size.S
			TypeConverter.convert(map, 2, Size.class) == Size.M
			TypeConverter.convert(map, 3, Size.class) == Size.L
			TypeConverter.convert(map, 4, Size.class) == Size.XL

		when:
			TypeConverter.convert(map, Size.lastOrdinal() + 1, Size.class)
		then:
			thrown ConvertException
	/**/DebugTrace.leave()
	}

	// Long -> Size (since 1.4.0)
	def "Long -> Size"() {
	/**/DebugTrace.enter()
		when:
			TypeConverter.convert(map, -1L, Size.class)
		then:
			thrown ConvertException

		expect:
			TypeConverter.convert(map, 0L, Size.class) == Size.XS
			TypeConverter.convert(map, 1L, Size.class) == Size.S
			TypeConverter.convert(map, 2L, Size.class) == Size.M
			TypeConverter.convert(map, 3L, Size.class) == Size.L
			TypeConverter.convert(map, 4L, Size.class) == Size.XL

		when:
			TypeConverter.convert(map, Size.lastOrdinal() + 1L, Size.class)

		then:
			thrown ConvertException

	/**/DebugTrace.leave()
	}

}
