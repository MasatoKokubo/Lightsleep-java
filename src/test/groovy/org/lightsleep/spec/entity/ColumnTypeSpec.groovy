// ColumnTypeSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.entity

import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.util.ArrayList

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.database.*
import org.lightsleep.entity.*

import spock.lang.*

// ColumnTypeSpec
// @since 2.0.0
@Unroll
class ColumnTypeSpec extends Specification {
	@Shared Database beforeDatabase = Sql.database
	@Shared def beforeTimeZone = TimeZone.getDefault()

	def setupSpec() {
		Sql.database = Standard.instance()
		TimeZone.setDefault(TimeZone.getTimeZone("GMT0"))
	}

	def cleanupSpec() {
		TimeZone.setDefault(beforeTimeZone)
		Sql.database = beforeDatabase
	}

	@ColumnTypeProperties([
		@ColumnTypeProperty(property='n1.n2.n3.c0', type=Void),
		@ColumnTypeProperty(property='n1.n2.n3.c2', type=Date),
		@ColumnTypeProperty(property='n1.n2.n3.c3', type=String),
	])
	static class Entity1 {
		@Key
		int key = -1

		static class N1 {
			static class N2 {
				static class N3 {
					int c0 = 0

					@ColumnType(String)
					int c1 = 1

					int c2 = 2

					@ColumnType(Time)
					int c3 = 3

					@ColumnType(Timestamp)
					int c4 = 4
				}
				N3 n3 = new N3()
			}
			N2 n2 = new N2()
		}
		N1 n1 = new N1()
	}

	static class Entity2 extends Entity1 {
	}

	@ColumnTypeProperties([
		@ColumnTypeProperty(property='c1', type=String),
		@ColumnTypeProperty(property='c2', type=String),
		@ColumnTypeProperty(property='c3', type=String),
		@ColumnTypeProperty(property='c4', type=String)
	])
	static class Entity3 {
		@Key
		int key = -1

		@ColumnType(Void)
		int c0 = 0
		int c1 = 1
		int c2 = 2
		int c3 = 3
		int c4 = 4
	}

	@ColumnTypeProperties([
		@ColumnTypeProperty(property='c2', type=Date),
		@ColumnTypeProperty(property='c3', type=Time),
		@ColumnTypeProperty(property='c4', type=Timestamp)
	])
	static class Entity4 extends Entity3 {
	}

	@ColumnTypeProperties([
		@ColumnTypeProperty(property='c1', type=Void),
		@ColumnTypeProperty(property='c2', type=Void),
		@ColumnTypeProperty(property='c3', type=Void),
		@ColumnTypeProperty(property='c4', type=Void)
	])
	static class Entity5 extends Entity4 {
	}

	def "@ColumnType #method #no"(String method, int no, Class<?> entityClass, String expectedSql) {
	/**/DebugTrace.enter()
		setup:
			def entity = entityClass.getDeclaredConstructor().newInstance()
			def sql = new Sql<>(entityClass)
			sql.setEntity(entity).where(entity)

		when:
			def createdSql =
				method == 'select' ? Sql.database.selectSql(sql, []) :
				method == 'insert' ? Sql.database.insertSql(sql, []) :
				method == 'update' ? Sql.database.updateSql(sql, []) : ''
		/**/DebugTrace.print("createdSql", createdSql)

		then:
			createdSql == expectedSql

	/**/DebugTrace.leave()
		where:
			method  |no|entityClass|expectedSql
			'select'|1 |Entity1    |"SELECT key, c0, c1, c2, c3, c4 FROM Entity1 WHERE key=-1"
			'select'|2 |Entity2    |"SELECT key, c0, c1, c2, c3, c4 FROM Entity2 WHERE key=-1"
			'select'|3 |Entity3    |"SELECT key, c0, c1, c2, c3, c4 FROM Entity3 WHERE key=-1"
			'select'|4 |Entity4    |"SELECT key, c0, c1, c2, c3, c4 FROM Entity4 WHERE key=-1"
			'select'|5 |Entity5    |"SELECT key, c0, c1, c2, c3, c4 FROM Entity5 WHERE key=-1"
			'insert'|1 |Entity1    |"INSERT INTO Entity1 (key, c0, c1, c2, c3, c4) VALUES (-1, 0, '1', DATE'1970-01-01', '3', TIMESTAMP'1970-01-01 00:00:00.004')"
			'insert'|2 |Entity2    |"INSERT INTO Entity2 (key, c0, c1, c2, c3, c4) VALUES (-1, 0, '1', DATE'1970-01-01', '3', TIMESTAMP'1970-01-01 00:00:00.004')"
			'insert'|3 |Entity3    |"INSERT INTO Entity3 (key, c0, c1, c2, c3, c4) VALUES (-1, 0, '1', '2', '3', '4')"
			'insert'|4 |Entity4    |"INSERT INTO Entity4 (key, c0, c1, c2, c3, c4) VALUES (-1, 0, '1', DATE'1970-01-01', TIME'00:00:00', TIMESTAMP'1970-01-01 00:00:00.004')"
			'insert'|5 |Entity5    |"INSERT INTO Entity5 (key, c0, c1, c2, c3, c4) VALUES (-1, 0, 1, 2, 3, 4)"
			'update'|1 |Entity1    |"UPDATE Entity1 SET c0=0, c1='1', c2=DATE'1970-01-01', c3='3', c4=TIMESTAMP'1970-01-01 00:00:00.004' WHERE key=-1"
			'update'|2 |Entity2    |"UPDATE Entity2 SET c0=0, c1='1', c2=DATE'1970-01-01', c3='3', c4=TIMESTAMP'1970-01-01 00:00:00.004' WHERE key=-1"
			'update'|3 |Entity3    |"UPDATE Entity3 SET c0=0, c1='1', c2='2', c3='3', c4='4' WHERE key=-1"
			'update'|4 |Entity4    |"UPDATE Entity4 SET c0=0, c1='1', c2=DATE'1970-01-01', c3=TIME'00:00:00', c4=TIMESTAMP'1970-01-01 00:00:00.004' WHERE key=-1"
			'update'|5 |Entity5    |"UPDATE Entity5 SET c0=0, c1=1, c2=2, c3=3, c4=4 WHERE key=-1"
	}
}
