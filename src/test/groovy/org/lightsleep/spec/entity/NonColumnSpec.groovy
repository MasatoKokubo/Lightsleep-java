// NonColumnSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.entity

import java.sql.Timestamp
import java.util.ArrayList

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.EntityCondition
import org.lightsleep.entity.*

import spock.lang.*

// NonColumnSpec
// @since 2.0.0
@Unroll
class NonColumnSpec extends Specification {
	@NonColumnProperties([
		@NonColumnProperty(property='n1.n2.n3.c0', value=false),
		@NonColumnProperty(property='n1.n2.n3.c1'),
		@NonColumnProperty(property='n1.n2.n3.c2', value=true),
		@NonColumnProperty(property='n1.n2.n3.c3', value=false),
		@NonColumnProperty(property='n1.n2.n3.c4', value=false)
	])
	static class Entity1 {
		@Key
		int key = -1

		static class N1 {
			static class N2 {
				static class N3 {
					int c0 = 0

					@NonColumn(false)
					int c1 = 1

					@NonColumn(false)
					int c2 = 2

					@NonColumn
					int c3 = 3

					@NonColumn(true)
					int c4 = 4
				}
				N3 n3 = new N3()
			}
			N2 n2 = new N2()
		}
		N1 n1 = new N1()
	}

	@NonSelectProperties([
		@NonSelectProperty(property='c1', value=false),
		@NonSelectProperty(property='c2', value=false),
		@NonSelectProperty(property='c3', value=false),
		@NonSelectProperty(property='c4', value=false)
	])
	@NonInsertProperties([
		@NonInsertProperty(property='c1', value=false),
		@NonInsertProperty(property='c2', value=false),
		@NonInsertProperty(property='c3', value=false),
		@NonInsertProperty(property='c4', value=false)
	])
	@NonUpdateProperties([
		@NonUpdateProperty(property='c1', value=false),
		@NonUpdateProperty(property='c2', value=false),
		@NonUpdateProperty(property='c3', value=false),
		@NonUpdateProperty(property='c4', value=false)
	])
	static class Entity2 extends Entity1 {
	}

	@NonColumnProperties([
		@NonColumnProperty(property='c1'),
		@NonColumnProperty(property='c2', value=true),
		@NonColumnProperty(property='c3', value=false),
		@NonColumnProperty(property='c4', value=false)
	])
	static class Entity3 {
		@Key
		int key = -1

		@NonColumn(false)
		int c0 = 0
		int c1 = 1
		int c2 = 2
		int c3 = 3
		int c4 = 4
	}

	@NonColumnProperties([
		@NonColumnProperty(property='c1', value=false),
		@NonColumnProperty(property='c2', value=false),
		@NonColumnProperty(property='c3'),
		@NonColumnProperty(property='c4', value=true)
	])
	@NonSelectProperties([
		@NonSelectProperty(property='c1', value=false),
		@NonSelectProperty(property='c2', value=false),
		@NonSelectProperty(property='c3', value=false),
		@NonSelectProperty(property='c4', value=false)
	])
	@NonInsertProperties([
		@NonInsertProperty(property='c1', value=false),
		@NonInsertProperty(property='c2', value=false),
		@NonInsertProperty(property='c3', value=false),
		@NonInsertProperty(property='c4', value=false)
	])
	@NonUpdateProperties([
		@NonUpdateProperty(property='c1', value=false),
		@NonUpdateProperty(property='c2', value=false),
		@NonUpdateProperty(property='c3', value=false),
		@NonUpdateProperty(property='c4', value=false)
	])
	static class Entity4 extends Entity3 {
	}

	def "@NonColumn #method #no"(String method, int no, Class<?> entityClass, String expectedSql) {
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
			'select'|1 |Entity1    |'SELECT key, c0 FROM Entity1 WHERE key=-1'
			'select'|2 |Entity2    |'SELECT key, c0 FROM Entity2 WHERE key=-1'
			'select'|3 |Entity3    |'SELECT key, c0, c3, c4 FROM Entity3 WHERE key=-1'
			'select'|4 |Entity4    |'SELECT key, c0, c1, c2 FROM Entity4 WHERE key=-1'

			'insert'|1 |Entity1    |'INSERT INTO Entity1 (key, c0) VALUES (-1, 0)'
			'insert'|2 |Entity2    |'INSERT INTO Entity2 (key, c0) VALUES (-1, 0)'
			'insert'|3 |Entity3    |'INSERT INTO Entity3 (key, c0, c3, c4) VALUES (-1, 0, 3, 4)'
			'insert'|4 |Entity4    |'INSERT INTO Entity4 (key, c0, c1, c2) VALUES (-1, 0, 1, 2)'

			'update'|1 |Entity1    |'UPDATE Entity1 SET c0=0 WHERE key=-1'
			'update'|2 |Entity2    |'UPDATE Entity2 SET c0=0 WHERE key=-1'
			'update'|3 |Entity3    |'UPDATE Entity3 SET c0=0, c3=3, c4=4 WHERE key=-1'
			'update'|4 |Entity4    |'UPDATE Entity4 SET c0=0, c1=1, c2=2 WHERE key=-1'
	}
}
