// ColumnSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.entity

import java.sql.Timestamp
import java.util.ArrayList

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.EntityCondition
import org.lightsleep.entity.*

import spock.lang.*

// ColumnSpec
// @since 2.0.0
@Unroll
class ColumnSpec extends Specification {
	@ColumnProperties([
		@ColumnProperty(property='n1.n2.n3.c0', column=''),
		@ColumnProperty(property='n1.n2.n3.c2', column='C2'),
		@ColumnProperty(property='n1.n2.n3.c3', column='C3'),
	])
	static class Entity1 {
		@Key
		int key = -1

		static class N1 {
			static class N2 {
				static class N3 {
					int c0 = 0

					@Column('C1')
					int c1 = 1

					int c2 = 2

					@Column('C3')
					int c3 = 3
				}
				N3 n3 = new N3()
			}
			N2 n2 = new N2()
		}
		N1 n1 = new N1()
	}

	static class Entity2 extends Entity1 {
	}

	@ColumnProperty(property='c1', column='C1')
	static class Entity3 {
		@Key
		int key = -1

		@Column('')
		int c0 = 0
		int c1 = 1
	}

	@ColumnProperty(property='c1', column='CC1')
	static class Entity4 extends Entity3 {
	}

	@ColumnProperty(property='c1', column='')
	static class Entity5 extends Entity4 {
	}

	def "@Column #method #no"(String method, int no, Class<?> entityClass, String expectedSql) {
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
		/**/DebugTrace.print('createdSql', createdSql)

		then:
			createdSql == expectedSql

	/**/DebugTrace.leave()
		where:
			method  |no|entityClass|expectedSql
			'select'|1 |Entity1    |'SELECT key, c0, C1, C2, C3 FROM Entity1 WHERE key=-1'
			'select'|2 |Entity2    |'SELECT key, c0, C1, C2, C3 FROM Entity2 WHERE key=-1'
			'select'|3 |Entity3    |'SELECT key, c0, C1 FROM Entity3 WHERE key=-1'
			'select'|4 |Entity4    |'SELECT key, c0, CC1 FROM Entity4 WHERE key=-1'
			'select'|5 |Entity5    |'SELECT key, c0, c1 FROM Entity5 WHERE key=-1'

			'insert'|1 |Entity1    |'INSERT INTO Entity1 (key, c0, C1, C2, C3) VALUES (-1, 0, 1, 2, 3)'
			'insert'|2 |Entity2    |'INSERT INTO Entity2 (key, c0, C1, C2, C3) VALUES (-1, 0, 1, 2, 3)'
			'insert'|3 |Entity3    |'INSERT INTO Entity3 (key, c0, C1) VALUES (-1, 0, 1)'
			'insert'|4 |Entity4    |'INSERT INTO Entity4 (key, c0, CC1) VALUES (-1, 0, 1)'
			'insert'|5 |Entity5    |'INSERT INTO Entity5 (key, c0, c1) VALUES (-1, 0, 1)'

			'update'|1 |Entity1    |'UPDATE Entity1 SET c0=0, C1=1, C2=2, C3=3 WHERE key=-1'
			'update'|2 |Entity2    |'UPDATE Entity2 SET c0=0, C1=1, C2=2, C3=3 WHERE key=-1'
			'update'|3 |Entity3    |'UPDATE Entity3 SET c0=0, C1=1 WHERE key=-1'
			'update'|4 |Entity4    |'UPDATE Entity4 SET c0=0, CC1=1 WHERE key=-1'
			'update'|5 |Entity5    |'UPDATE Entity5 SET c0=0, c1=1 WHERE key=-1'
	}
}
