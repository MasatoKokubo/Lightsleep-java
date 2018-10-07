// DateAndTimeSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec


import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.connection.*
import org.lightsleep.database.*
import org.lightsleep.test.entity.*
import org.lightsleep.test.exception.*

import spock.lang.*

// DateAndTimeSpec
@Unroll
class DateAndTimeSpec extends SpecCommon {
	@Shared TimeZone defaultTimeZone

	def setupSpec() {
		DebugTrace.enter() // for Debugging
		defaultTimeZone = TimeZone.getDefault();
		deleteAllTables()
		DebugTrace.leave() // for Debugging
	}

	def cleanup() {
		DebugTrace.enter() // for Debugging
		TimeZone.setDefault(defaultTimeZone);

		if (connectionSupplier.database instanceof MySQL) {
			Transaction.execute(connectionSupplier) {
				new Sql<>(Object).connection(it)
					.executeUpdate("SET GLOBAL time_zone='${defaultTimeZone.getID()}'")
			}
		}
		DebugTrace.leave() // for Debugging
	}

	def "DateAndTimeSpec java.sql.Xxx #dateAndTimeClass #id #timeZoneId #year-#month-#day #hour:#minute:#second #nanosecond"(
		Class<? extends DateAndTime> dateAndTimeClass,
		int id, String timeZoneId, int year, int month, int day, int hour, int minute, int second, int nanosecond) {
		DebugTrace.enter() // for Debugging
		DebugTrace.print('dateAndTimeClass', dateAndTimeClass) // for Debugging
		DebugTrace.print('id', id) // for Debugging
		DebugTrace.print('timeZoneId', timeZoneId) // for Debugging
		DebugTrace.print('year', year) // for Debugging
		DebugTrace.print('month', month) // for Debugging
		DebugTrace.print('day', id) // for Debugging
		DebugTrace.print('hour', hour) // for Debugging
		DebugTrace.print('minute', minute) // for Debugging
		DebugTrace.print('second', second) // for Debugging
		DebugTrace.print('nanosecond', nanosecond) // for Debugging
		DebugTrace.print('(int)(nanosecond / 1000_000)', (int)(nanosecond / 1000_000)) // for Debugging
		DebugTrace.print('connectionSupplier.database.class.name', connectionSupplier.database.getClass().name) // for Debugging
		setup:
			// set timeZone
			TimeZone.setDefault(TimeZone.getTimeZone(timeZoneId));
			DebugTrace.print('ZoneId.systemDefault', ZoneId.systemDefault()) // for Debugging

			if (connectionSupplier.database instanceof MySQL) {
				Transaction.execute(connectionSupplier) {
					new Sql<>(Object).connection(it)
						.executeUpdate("SET GLOBAL time_zone='${timeZoneId}'")
				}
			}

			// Get a subclass corresponding to the target DBMS if exists
			try {
				dateAndTimeClass = Class.forName("${dateAndTimeClass.name}\$${connectionSupplier.database.getClass().simpleName}");
				DebugTrace.print('2 dateAndTimeClass', dateAndTimeClass) // for Debugging
			}
			catch (Exception e) {
				DebugTrace.print('e', e) // for Debugging
			}

			// milli seconds of Time
			def timeMilli = (int)(nanosecond / 1000_000)
			DebugTrace.print('timeMilli', timeMilli) // for Debugging

			// truncated milli seconds of Time
			def truncatedTimeMilli =
				connectionSupplier.database instanceof DB2        ? 0 :
				connectionSupplier.database instanceof MySQL      ? timeMilli :
				connectionSupplier.database instanceof Oracle     ? 0 :
				connectionSupplier.database instanceof PostgreSQL ? timeMilli :
				connectionSupplier.database instanceof SQLite     ? timeMilli :
				connectionSupplier.database instanceof SQLServer  ? timeMilli : timeMilli;
			DebugTrace.print('truncatedTimeMilli', truncatedTimeMilli) // for Debugging

			// truncated nano seconds of Time
			def truncatedTimeNano =
				connectionSupplier.database instanceof DB2        ? 0 :
				connectionSupplier.database instanceof MySQL      ? nanosecond - nanosecond % 1000 :
				connectionSupplier.database instanceof Oracle     ? 0 :
				connectionSupplier.database instanceof PostgreSQL ? nanosecond - nanosecond % 1000 :
				connectionSupplier.database instanceof SQLite     ? nanosecond :
			//	connectionSupplier.database instanceof SQLServer  ? nanosecond - nanosecond % 100 : nanosecond
				connectionSupplier.database instanceof SQLServer  ? nanosecond - nanosecond % 1000_000 : nanosecond
			DebugTrace.print('truncatedTimeNano', truncatedTimeNano) // for Debugging

			// truncated nano seconds of Timestamp
			def truncatedNano =
				connectionSupplier.database instanceof DB2        ? nanosecond :
				connectionSupplier.database instanceof MySQL      ? nanosecond - nanosecond % 1000 :
				connectionSupplier.database instanceof Oracle     ? nanosecond :
				connectionSupplier.database instanceof PostgreSQL ? nanosecond - nanosecond % 1000 :
				connectionSupplier.database instanceof SQLite     ? nanosecond :
				connectionSupplier.database instanceof SQLServer  ? nanosecond - nanosecond % 100 : nanosecond
			DebugTrace.print('truncatedNano', truncatedNano) // for Debugging

			// a Calendar for the Date
			def dayCal = Calendar.instance
			dayCal.set(year, month-1, day, 0, 0, 0)
			dayCal.set(Calendar.MILLISECOND , 0)
			DebugTrace.print('dayCal', dayCal) // for Debugging

			// a Calendar for the Time
			def timeCal = Calendar.instance
			timeCal.set(1970, 1-1, 1, hour, minute, second)
			timeCal.set(Calendar.MILLISECOND , timeMilli)
			DebugTrace.print('timeCal', timeCal) // for Debugging

			// a Calendar for the truncated Time
			def truncatedTimeCal = Calendar.instance
			truncatedTimeCal.set(1970, 1-1, 1, hour, minute, second)
			truncatedTimeCal.set(Calendar.MILLISECOND , truncatedTimeMilli)
			DebugTrace.print('truncatedTimeCal', truncatedTimeCal) // for Debugging

			// a Calendar for the Timestamp
			def timestampCal = Calendar.instance
			timestampCal.set(year, month-1, day, hour, minute, second)
			timestampCal.set(Calendar.MILLISECOND , timeMilli)
			DebugTrace.print('timestampCal', timestampCal) // for Debugging

			def dateAndTime1 = dateAndTimeClass.getConstructor().newInstance()

			dateAndTime1.id = id
			if (dateAndTime1 instanceof DateAndTime.JavaSql) {
				// DateAndTime.JavaSql
				def entity1 = (DateAndTime.JavaSql)dateAndTime1
				entity1.dateValue         = new Date     (dayCal.timeInMillis)
				entity1.timeValue         = new Time     (truncatedTimeCal.timeInMillis)
				entity1.timestampValue    = new Timestamp(timestampCal.timeInMillis); entity1.timestampValue.nanos = truncatedNano
				entity1.timestampTZValue  = entity1.timestampValue
				if (connectionSupplier.database instanceof Oracle)
					entity1.timestampLTZValue = entity1.timestampValue

			} else if (dateAndTime1 instanceof DateAndTime.JavaLong) {
				// DateAndTime.JavaLong
				def entity1 = (DateAndTime.JavaLong)dateAndTime1
				entity1.timestampValue    = timestampCal.timeInMillis
				entity1.timestampTZValue  = entity1.timestampValue
				if (connectionSupplier.database instanceof Oracle)
					entity1.timestampLTZValue = entity1.timestampValue

			} else if (dateAndTime1 instanceof DateAndTime.Local) {
				// DateAndTime.Local
				def entity1 = (DateAndTime.Local)dateAndTime1
				entity1.dateValue         = LocalDate.of(year, month, day)
				entity1.timeValue         = LocalTime.of(hour, minute, second, truncatedTimeNano)
				entity1.timestampValue    = LocalDateTime.of(year, month, day, hour, minute, second, truncatedNano)
				entity1.timestampTZValue  = entity1.timestampValue
				if (connectionSupplier.database instanceof Oracle)
					entity1.timestampLTZValue = entity1.timestampValue

			} else if (dateAndTime1 instanceof DateAndTime.Offset) {
				// DateAndTime.Offset
				def entity1 = (DateAndTime.Offset)dateAndTime1
				entity1.timestampValue    = ZonedDateTime.of(year, month, day, hour, minute, second, truncatedNano, ZoneId.systemDefault()).toOffsetDateTime()
				entity1.timestampTZValue  = entity1.timestampValue
				if (connectionSupplier.database instanceof Oracle)
					entity1.timestampLTZValue = entity1.timestampValue

			} else if (dateAndTime1 instanceof DateAndTime.Zoned) {
				// DateAndTime.Zoned
				def entity1 = (DateAndTime.Zoned)dateAndTime1
				entity1.timestampValue    = ZonedDateTime.of(year, month, day, hour, minute, second, truncatedNano, ZoneId.systemDefault())
				entity1.timestampTZValue  = entity1.timestampValue
				if (connectionSupplier.database instanceof SQLServer)
					entity1.timestampTZValue  = entity1.timestampTZValue.toOffsetDateTime().toZonedDateTime()
				if (connectionSupplier.database instanceof Oracle)
					entity1.timestampLTZValue = entity1.timestampValue

			} else if (dateAndTime1 instanceof DateAndTime.Instant) {
				// DateAndTime.Instant
				def entity1 = (DateAndTime.Instant)dateAndTime1
				entity1.timestampValue    = ZonedDateTime.of(year, month, day, hour, minute, second, truncatedNano, ZoneId.systemDefault()).toInstant()
				entity1.timestampTZValue  = entity1.timestampValue
				if (connectionSupplier.database instanceof Oracle)
					entity1.timestampLTZValue = entity1.timestampValue

			} else
				assert false;

			DebugTrace.print('dateAndTime1', dateAndTime1) // for Debugging

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(dateAndTimeClass).connection(it).delete(dateAndTime1)
				new Sql<>(dateAndTimeClass).connection(it).insert(dateAndTime1)
			}

			DateAndTime dateAndTime2
			Transaction.execute(connectionSupplier) {
				dateAndTime2 = new Sql<>(dateAndTimeClass).connection(it)
					.where('{id}={}', dateAndTime1.id)
					.select().orElseThrow({new NotFoundException()})
			}
			DebugTrace.print('dateAndTime2', dateAndTime2) // for Debugging

		then:
			dateAndTime1.equals(dateAndTime2)

		DebugTrace.leave() // for Debugging
		where:
			dateAndTimeClass    |id      |timeZoneId           |year|month|day|hour|minute|second|nanosecond

			DateAndTime.JavaSql |1245_1_1|'Pacific/Chatham'    |2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.JavaLong|1245_1_2|'Pacific/Chatham'    |2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Local   |1245_1_3|'Pacific/Chatham'    |2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Offset  |1245_1_4|'Pacific/Chatham'    |2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Zoned   |1245_1_5|'Pacific/Chatham'    |2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Instant |1245_1_6|'Pacific/Chatham'    |2018|   1 |  1|  0 |   1  |   2  |123_456_789

			DateAndTime.JavaSql |1245_8_1|'Pacific/Chatham'    |2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.JavaLong|1245_8_2|'Pacific/Chatham'    |2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Local   |1245_8_3|'Pacific/Chatham'    |2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Offset  |1245_8_4|'Pacific/Chatham'    |2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Zoned   |1245_8_5|'Pacific/Chatham'    |2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Instant |1245_8_6|'Pacific/Chatham'    |2018|   8 |  1|  0 |   1  |   2  |123_456_789

			DateAndTime.JavaSql | 900_1_1|'Asia/Tokyo'         |2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.JavaLong| 900_1_2|'Asia/Tokyo'         |2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Local   | 900_1_3|'Asia/Tokyo'         |2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Offset  | 900_1_4|'Asia/Tokyo'         |2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Zoned   | 900_1_5|'Asia/Tokyo'         |2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Instant | 900_1_6|'Asia/Tokyo'         |2018|   1 |  1|  0 |   1  |   2  |123_456_789

			DateAndTime.JavaSql | 900_8_1|'Asia/Tokyo'         |2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.JavaLong| 900_8_2|'Asia/Tokyo'         |2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Local   | 900_8_3|'Asia/Tokyo'         |2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Offset  | 900_8_4|'Asia/Tokyo'         |2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Zoned   | 900_8_5|'Asia/Tokyo'         |2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Instant | 900_8_6|'Asia/Tokyo'         |2018|   8 |  1|  0 |   1  |   2  |123_456_789

			DateAndTime.JavaSql |     1_1|'Europe/London'      |2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.JavaLong|     1_2|'Europe/London'      |2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Local   |     1_3|'Europe/London'      |2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Offset  |     1_4|'Europe/London'      |2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Zoned   |     1_5|'Europe/London'      |2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Instant |     1_6|'Europe/London'      |2018|   1 |  1|  0 |   1  |   2  |123_456_789

			DateAndTime.JavaSql |     8_1|'Europe/London'      |2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.JavaLong|     8_2|'Europe/London'      |2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Local   |     8_3|'Europe/London'      |2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Offset  |     8_4|'Europe/London'      |2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Zoned   |     8_5|'Europe/London'      |2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Instant |     8_6|'Europe/London'      |2018|   8 |  1|  0 |   1  |   2  |123_456_789

			DateAndTime.JavaSql |-800_1_1|'America/Los_Angeles'|2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.JavaLong|-800_1_2|'America/Los_Angeles'|2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Local   |-800_1_3|'America/Los_Angeles'|2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Offset  |-800_1_4|'America/Los_Angeles'|2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Zoned   |-800_1_5|'America/Los_Angeles'|2018|   1 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Instant |-800_1_6|'America/Los_Angeles'|2018|   1 |  1|  0 |   1  |   2  |123_456_789

			DateAndTime.JavaSql |-800_8_1|'America/Los_Angeles'|2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.JavaLong|-800_8_2|'America/Los_Angeles'|2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Local   |-800_8_3|'America/Los_Angeles'|2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Offset  |-800_8_4|'America/Los_Angeles'|2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Zoned   |-800_8_5|'America/Los_Angeles'|2018|   8 |  1|  0 |   1  |   2  |123_456_789
			DateAndTime.Instant |-800_8_6|'America/Los_Angeles'|2018|   8 |  1|  0 |   1  |   2  |123_456_789
	}
}
