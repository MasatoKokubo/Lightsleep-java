// SQLiteSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.database

import org.debugtrace.DebugTrace
import org.lightsleep.component.*
import org.lightsleep.database.SQLite
import static org.lightsleep.database.Standard.*

import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import org.lightsleep.helper.*

import spock.lang.*

// SQLiteSpec
@Unroll
class SQLiteSpec extends Specification {
	@Shared map = SQLite.instance.typeConverterMap()

	@Shared CURRENT_MS = System.currentTimeMillis()
	@Shared UTIL_DATE = new java.util.Date(CURRENT_MS)
	@Shared  SQL_DATE = new           Date(CURRENT_MS)
	@Shared      TIME = new           Time(CURRENT_MS)
	@Shared TIMESTAMP = new      Timestamp(CURRENT_MS)

	@Shared      DATE_STRING = TypeConverter.convert(map,  SQL_DATE, String)
	@Shared      TIME_STRING = TypeConverter.convert(map,      TIME, String)
	@Shared TIMESTAMP_STRING = TypeConverter.convert(map, TIMESTAMP, String)

	// -> SqlString
	def "SQLite -> SqlString"() {
		expect:
			TypeConverter.convert(map, false    , SqlString).toString() == '0'
			TypeConverter.convert(map, true     , SqlString).toString() == '1'
			TypeConverter.convert(map, "'"      , SqlString).toString() == "''''"
			TypeConverter.convert(map, UTIL_DATE, SqlString).toString() == "'" + DATE_STRING      + "'"
			TypeConverter.convert(map, SQL_DATE , SqlString).toString() == "'" + DATE_STRING      + "'"
			TypeConverter.convert(map, TIME     , SqlString).toString() == "'" + TIME_STRING      + "'"
			TypeConverter.convert(map, TIMESTAMP, SqlString).toString() == "'" + TIMESTAMP_STRING + "'"
	}

	// maskPassword
	def "SQLite maskPassword"(String jdbcUrl) {
		expect: SQLite.instance.maskPassword(jdbcUrl) == jdbcUrl

		where:
			jdbcUrl << [
				''                            ,
				'passwor='                    ,
				'password ='                  ,
				'password  =a'                ,
				'password= !"#$%\'()*+,-./&'  ,
				'?password=;<=>?@[\\]^_`(|)~:',
				'?password=a&password=a:bbb'  ,
			]
	}
}
