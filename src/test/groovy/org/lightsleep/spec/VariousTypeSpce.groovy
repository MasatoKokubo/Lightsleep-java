// VariousTypeSpce.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.IOException
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.connection.*
import org.lightsleep.database.*
import org.lightsleep.test.entity.*
import org.lightsleep.test.exception.*

import spock.lang.*

// VariousTypeSpce
@Unroll
class VariousTypeSpce extends Specification {
	@Shared connectionSupplier

	def setupSpec() {
		connectionSupplier = ConnectionSpec.getConnectionSupplier(Jdbc)
	}

	def "VariousTypeSpce various types"() {
	/**/DebugTrace.enter()

		setup:
			Calendar dayCal = Calendar.instance
			dayCal.set(Calendar.HOUR_OF_DAY, 0)
			dayCal.set(Calendar.MINUTE     , 0)
			dayCal.set(Calendar.SECOND     , 0)
			dayCal.set(Calendar.MILLISECOND, 0)

			Calendar timeCal = Calendar.instance
			timeCal.set(Calendar.YEAR        , 1970)
			timeCal.set(Calendar.MONTH       , 1-1)
			timeCal.set(Calendar.DAY_OF_MONTH, 1)
			timeCal.set(Calendar.HOUR_OF_DAY , 0)
			timeCal.set(Calendar.MILLISECOND , 0)

			Calendar timestampCal = Calendar.instance
			timestampCal.set(Calendar.MILLISECOND, 0)

			Various various1 = Sql.database instanceof PostgreSQL ? new Various.PostgreSQL() : new Various()

			various1.id               = 1

			various1.booleanPValue    = true
			various1.char1PValue      = 'あ' as char
			various1.tinyIntPValue    = 0x7F
			various1.smallIntPValue   = 0x7FFF
			various1.intPValue        = 0x7FFF_FFFF
			various1.bigIntPValue     = 0x7FFF_FFFF_FFFF_FFFFL
			various1.floatPValue      = 123.123F
			various1.doublePValue     = 1232456.123456

			various1.booleanValue     = true
			various1.char1Value       = 'い' as char
			if (Sql.database instanceof SQLServer)
				various1.tinyIntValue = 0x00
			else
				various1.tinyIntValue = -0x80
			various1.smallIntValue    = -0x8000
			various1.intValue         = -0x8000_0000
			various1.bigIntValue      = -0x8000_0000_0000_0000L
			various1.floatValue       = -123.123F
			various1.doubleValue      = -123456.123456

			various1.decimalValue     = new BigDecimal('1234567890.99')
			various1.dateValue        = new Date     (dayCal .timeInMillis)           ; dayCal      .add(Calendar.DAY_OF_MONTH, 1)
			various1.timeValue        = new Time     (timeCal.timeInMillis)           ; timeCal     .add(Calendar.HOUR_OF_DAY,  23) // 00:??:??
			various1.timeTZValue      = new Time     (timeCal.timeInMillis)           ; timeCal     .add(Calendar.HOUR_OF_DAY, -23) // 23:??:??
			various1.dateTimeValue    = new Timestamp(timestampCal.timeInMillis +   0); timestampCal.add(Calendar.HOUR_OF_DAY,  1)
			various1.timestampValue   = new Timestamp(timestampCal.timeInMillis +   9); timestampCal.add(Calendar.HOUR_OF_DAY,  1)
			various1.timestampTZValue = new Timestamp(timestampCal.timeInMillis + 999); timestampCal.add(Calendar.HOUR_OF_DAY,  1)
			various1.longDate         = new Date     (dayCal .timeInMillis)
			various1.longTime         = new Time     (timeCal.timeInMillis)
			various1.longTimestamp    = new Timestamp(timestampCal.timeInMillis +  99)
			various1.charValue        = "0123456789_\b\t\n\f\r'\\"
			various1.varCharValue     = "0123456789_\b\t\n\f\r'\\"
			various1.binaryValue      = [0x00, 0x01, 0x7F, 0x80, 0xFE, 0xFF] as byte[]
			various1.varBinaryValue   = [0x00, 0x01, 0x7E, 0x7F, 0x80, 0x81, 0xFE, 0xFF] as byte[]
			various1.textValue        = readTextFile  (new File('testdata/text.html'))
			various1.blobValue        = readBinaryFile(new File('testdata/blob.jpg'))

			if (various1 instanceof Various.PostgreSQL) {
				Various.PostgreSQL various1p = (Various.PostgreSQL)various1

				various1p.jsonValue        = '{"x": 123456, "y": -123456}'
				various1p.jsonbValue       = '{"x": 234567, "y": -234567}'
				various1p.booleans         = [false, true, false, true] as boolean[]
			//	various1p.shorts           = [-1, 0, 1, 2] as short[]
				various1p.shortList        = [(short)-1, (short)0, (short)1, (short)2]
				various1p.ints             = [-1   , 0   , 1   , 2   ] as int[]   
				various1p.longs            = [-1L  , 0L  , 1L  , 2L  ] as long[]  
				various1p.floats           = [-1.1F, 0.0F, 1.1F, 2.2F] as float[] 
				various1p.doubles          = [-1.1D, 0.0D, 1.1D, 2.2D] as double[]
				various1p.decimals         = [new BigDecimal('-2.22'), new BigDecimal('-1.11'), new BigDecimal('1.11'), new BigDecimal('2.22')] as BigDecimal[]
				various1p.texts            = [
					'ABC', readTextFile(new File('README.md')),
					'EFG', readTextFile(new File('README_ja.md')),
					'HIJ', readTextFile(new File('Tutorial.md')),
					'KLM', readTextFile(new File('Tutorial_ja.md'))
				] as String[]
				various1p.dates            = new Date[4]
				various1p.dates     [0]    = new Date(dayCal .timeInMillis); dayCal.add(Calendar.DAY_OF_MONTH, 1)
				various1p.dates     [1]    = new Date(dayCal .timeInMillis); dayCal.add(Calendar.DAY_OF_MONTH, 1)
				various1p.dates     [2]    = new Date(dayCal .timeInMillis); dayCal.add(Calendar.DAY_OF_MONTH, 1)
				various1p.dates     [3]    = new Date(dayCal .timeInMillis); dayCal.add(Calendar.DAY_OF_MONTH, 1)
				various1p.times            = new Time [4]
				various1p.times     [0]    = new Time(timeCal.timeInMillis); timeCal.add(Calendar.HOUR_OF_DAY,   7) // 00:??:??
				various1p.times     [1]    = new Time(timeCal.timeInMillis); timeCal.add(Calendar.HOUR_OF_DAY,   8) // 07:??:??
				various1p.times     [2]    = new Time(timeCal.timeInMillis); timeCal.add(Calendar.HOUR_OF_DAY,   8) // 15:??:??
				various1p.times     [3]    = new Time(timeCal.timeInMillis); timeCal.add(Calendar.HOUR_OF_DAY, -23) // 23:??:??
				various1p.timestamps       = new Timestamp [4]
				various1p.timestamps[0]    = new Timestamp(timestampCal.timeInMillis +   0); timestampCal.add(Calendar.HOUR_OF_DAY, 1)
				various1p.timestamps[1]    = new Timestamp(timestampCal.timeInMillis +   9); timestampCal.add(Calendar.HOUR_OF_DAY, 1)
				various1p.timestamps[2]    = new Timestamp(timestampCal.timeInMillis +  99); timestampCal.add(Calendar.HOUR_OF_DAY, 1)
				various1p.timestamps[3]    = new Timestamp(timestampCal.timeInMillis + 999); timestampCal.add(Calendar.HOUR_OF_DAY, 1)
			}

		when:
			Transaction.execute(connectionSupplier) {
				if (various1 instanceof Various.PostgreSQL) {
					new Sql<>(Various.PostgreSQL).delete(it, various1 as Various.PostgreSQL)
					new Sql<>(Various.PostgreSQL).insert(it, various1 as Various.PostgreSQL)
				} else {
					new Sql<>(Various).delete(it, various1)
					new Sql<>(Various).insert(it, various1)
				}
			}

			Various various2
			Transaction.execute(connectionSupplier) {
				various2 = new Sql<>(various1.getClass())
					.where('{id}={}', various1.id)
					.select(it).orElseThrow({new NotFoundException()})
			}

		then:
			various2.id                == various1.id

			various2.booleanPValue     == various1.booleanPValue
			various2.char1PValue       == various1.char1PValue
			various2.tinyIntPValue     == various1.tinyIntPValue
			various2.smallIntPValue    == various1.smallIntPValue
			various2.intPValue         == various1.intPValue
			various2.bigIntPValue      == various1.bigIntPValue
			various2.floatPValue       == various1.floatPValue
			various2.doublePValue      == various1.doublePValue

			various2.booleanValue      == various1.booleanValue
			various2.char1Value        == various1.char1Value
			various2.tinyIntValue      == various1.tinyIntValue
			various2.smallIntValue     == various1.smallIntValue
			various2.intValue          == various1.intValue
			various2.bigIntValue       == various1.bigIntValue
			various2.floatValue        == various1.floatValue
			various2.doubleValue       == various1.doubleValue
			various2.decimalValue      == various1.decimalValue

			various2.dateValue         == various1.dateValue
			various2.timeValue         == various1.timeValue
			various2.timeTZValue       == various1.timeTZValue
			various2.dateTimeValue     == various1.dateTimeValue
			various2.timestampValue    == various1.timestampValue
			various2.timestampTZValue  == various1.timestampTZValue
			various2.longDate          == various1.longDate
			various2.longTime          == various1.longTime
			various2.longTimestamp     == various1.longTimestamp
			various2.charValue?.trim() == various1.charValue
			various2.varCharValue      == various1.varCharValue
			various2.binaryValue       == (various1.binaryValue == null ? null : Arrays.copyOf(various1.binaryValue, various2.binaryValue.length))
			various2.varBinaryValue    == various1.varBinaryValue
			various2.textValue         == various1.textValue
			various2.blobValue         == various1.blobValue

			if (various2 instanceof Various.PostgreSQL) {
				Various.PostgreSQL various1p = various1 as Various.PostgreSQL
				Various.PostgreSQL various2p = various2 as Various.PostgreSQL

				various2p.jsonValue  == various1p.jsonValue
				various2p.jsonbValue == various1p.jsonbValue
				various2p.booleans   == various1p.booleans
			//	various2p.shorts     == various1p.shorts
				various2p.shortList  == various1p.shortList
				various2p.ints       == various1p.ints
				various2p.longs      == various1p.longs
				various2p.floats     == various1p.floats
				various2p.doubles    == various1p.doubles
				various2p.decimals   == various1p.decimals
				various2p.texts      == various1p.texts
				various2p.dates      == various1p.dates
				various2p.times      == various1p.times
				various2p.timestamps == various1p.timestamps
				various2p.timestamps == various1p.timestamps
				various2p.jsonX      ==  123456
				various2p.jsonY      == -123456
				various2p.jsonbX     ==  234567
				various2p.jsonbY     == -234567
			}

	/**/DebugTrace.leave()
	}

	def "VariousTypeSpce number types"() {
	/**/DebugTrace.enter()

		setup:
			VariousInteger various1 = new VariousInteger()

			various1.id               = 2

			various1.tinyIntPValue    = 0x7F
			various1.smallIntPValue   = 0x7FFF
			various1.intPValue        = 0x7FFF_FFFF
			various1.bigIntPValue     = 0x7FFF_FFFF
			various1.floatPValue      = 123_123
			various1.doublePValue     = 1_232_456_123

			if (Sql.database instanceof SQLServer)
				various1.tinyIntValue = 0x00
			else
				various1.tinyIntValue = -0x80
			various1.smallIntValue    = -0x8000
			various1.intValue         = -0x8000_0000
			various1.bigIntValue      = -0x8000_0000
			various1.floatValue       = -123_123
			various1.doubleValue      = -123_456_123

			various1.decimalValue     = 1_234_567_890

		when:
			VariousInteger various2
			Transaction.execute(connectionSupplier) {
				new Sql<>(VariousInteger).delete(it, various1)
				new Sql<>(VariousInteger).insert(it, various1)
				various2 = new Sql<>(VariousInteger)
					.where(various1)
					.select(it).orElseThrow({new NotFoundException()})
			}

		then:
			various2.id               == various1.id

			various2.tinyIntPValue    == various1.tinyIntPValue
			various2.smallIntPValue   == various1.smallIntPValue
			various2.intPValue        == various1.intPValue
			various2.bigIntPValue     == various1.bigIntPValue
			various2.floatPValue      == various1.floatPValue
			various2.doublePValue     == various1.doublePValue

			various2.tinyIntValue     == various1.tinyIntValue
			various2.smallIntValue    == various1.smallIntValue
			various2.intValue         == various1.intValue
			various2.bigIntValue      == various1.bigIntValue
			various2.floatValue       == various1.floatValue
			various2.doubleValue      == various1.doubleValue

			various2.decimalValue     == various1.decimalValue

	/**/DebugTrace.leave()
	}

	def "VariousTypeSpce string type"() {
	/**/DebugTrace.enter()

		setup:
			VariousString various1 = new VariousString()
			various1.id               = 3

			various1.char1PValue      = 'あ' as char
			various1.tinyIntPValue    = '' + (byte )0x7F
			various1.smallIntPValue   = '' + (short)0x7FFF
			various1.intPValue        = '' + (int  )0x7FFF_FFFF
			various1.bigIntPValue     = '' + (long )0x7FFF_FFFF_FFFF_FFFFL
			various1.floatPValue      = '' + 123.123F
			various1.doublePValue     = '' + 1232456.123456

			various1.char1Value       = 'い'
			if (Sql.database instanceof SQLServer)
				various1.tinyIntValue = '' + (byte)0x00
			else
				various1.tinyIntValue = '' + (byte )0x80
			various1.smallIntValue    = '' + (short)0x8000
			various1.intValue         = '' + (int  )0x8000_0000
			various1.bigIntValue      = '' + (long )(0x7FFF_FFFF_FFFF_FFFFL + 1)
			various1.floatValue       = '' + -123.123F
			various1.doubleValue      = '' + -123456.123456

			various1.decimalValue     = '' + new BigDecimal('1234567890.99')
			various1.charValue        = "0123456789_\b\t\n\f\r'\\"
			various1.varCharValue     = "0123456789_\b\t\n\f\r'\\"
			various1.textValue        = "0123456789_\b\t\n\f\r'\\"

		when:
			VariousString various2
			Transaction.execute(connectionSupplier) {
				new Sql<>(VariousString).delete(it, various1)
				new Sql<>(VariousString).insert(it, various1)
				various2 = new Sql<>(VariousString)
					.where(various1)
					.select(it).orElseThrow({throw new NotFoundException()})
			}

		then:
			various2.id               == various1.id

			various2.char1PValue      == various1.char1PValue
			various2.tinyIntPValue    == various1.tinyIntPValue
			various2.smallIntPValue   == various1.smallIntPValue
			various2.intPValue        == various1.intPValue
			various2.bigIntPValue     == various1.bigIntPValue
			various2.floatPValue      == various1.floatPValue
			various2.doublePValue     == various1.doublePValue

			various2.char1Value       == various1.char1Value
			various2.tinyIntValue     == various1.tinyIntValue
			various2.smallIntValue    == various1.smallIntValue
			various2.intValue         == various1.intValue
			various2.bigIntValue      == various1.bigIntValue
			various2.floatValue       == various1.floatValue
			various2.doubleValue      == various1.doubleValue

			if (Sql.database instanceof SQLite)
				new BigDecimal(various2.decimalValue) == new BigDecimal(various1.decimalValue)
			else
				various2.decimalValue == various1.decimalValue
			various2.charValue.trim() == various1.charValue
			various2.varCharValue     == various1.varCharValue
			various2.textValue        == various1.textValue

	/**/DebugTrace.leave()
	}

	private String readTextFile(File file) throws IOException {
		def buff = new StringBuilder()
		BufferedReader reader = null
		try {
			reader = new BufferedReader(new FileReader(file))
			String line
			while ((line = reader.readLine()) != null)
				buff.append(line).append('\n')
		}
		finally {
			if (reader != null)
				reader.close()
		}
		return buff.toString()
	}

	private byte[] readBinaryFile(File file) throws IOException {
		def result = new byte[(int)file.length()]
		def totalReadCount = 0
		def readCount = 0
		BufferedInputStream stream = null
		try {
			stream = new BufferedInputStream(new FileInputStream(file))
			while ((readCount = stream.read(result, totalReadCount, result.length - totalReadCount)) > 0)
				totalReadCount += readCount
		}
		finally {
			if (stream != null)
				stream.close()
		}
		return result
	}

}
