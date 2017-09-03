// NonSelectInsertUpdateSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.entity

import java.sql.Timestamp
import java.util.ArrayList

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.EntityCondition
import org.lightsleep.entity.*

import spock.lang.*

// NonSelectInsertUpdateSpec
// @since 2.0.0
@Unroll
class NonSelectInsertUpdateSpec extends Specification {
	@NonSelectProperties([
		@NonSelectProperty(property='n1.n2.n3.c0', value=false),
		@NonSelectProperty(property='n1.n2.n3.c1'),
		@NonSelectProperty(property='n1.n2.n3.c2', value=true),
		@NonSelectProperty(property='n1.n2.n3.c3', value=false),
	])
	@NonInsertProperties([
		@NonInsertProperty(property='n1.n2.n3.c0', value=false),
		@NonInsertProperty(property='n1.n2.n3.c1'),
		@NonInsertProperty(property='n1.n2.n3.c2', value=true),
		@NonInsertProperty(property='n1.n2.n3.c3', value=false),
	])
	@NonUpdateProperties([
		@NonUpdateProperty(property='n1.n2.n3.c0', value=false),
		@NonUpdateProperty(property='n1.n2.n3.c1'),
		@NonUpdateProperty(property='n1.n2.n3.c2', value=true),
		@NonUpdateProperty(property='n1.n2.n3.c3', value=false),
	])
	static class Entity1 {
		@Key
		int key = -1

		static class N1 {
			static class N2 {
				static class N3 {
					int c0 = 0

					@NonSelect(false)
					@NonInsert(false)
					@NonUpdate(false)
					int c1 = 1

					@NonSelect(false)
					@NonInsert(false)
					@NonUpdate(false)
					int c2 = 2

					@NonSelect
					@NonInsert
					@NonUpdate
					int c3 = 3

					@NonSelect(true)
					@NonInsert(true)
					@NonUpdate(true)
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

	@NonSelectProperties([
		@NonSelectProperty(property='c1'),
		@NonSelectProperty(property='c2', value=true),
		@NonSelectProperty(property='c3', value=false),
		@NonSelectProperty(property='c4', value=false)
	])
	@NonInsertProperties([
		@NonInsertProperty(property='c1'),
		@NonInsertProperty(property='c2', value=true),
		@NonInsertProperty(property='c3', value=false),
		@NonInsertProperty(property='c4', value=false)
	])
	@NonUpdateProperties([
		@NonUpdateProperty(property='c1'),
		@NonUpdateProperty(property='c2', value=true),
		@NonUpdateProperty(property='c3', value=false),
		@NonUpdateProperty(property='c4', value=false)
	])
	static class Entity3 {
		@Key
		int key = -1

		@NonSelect(false)
		@NonInsert(false)
		@NonUpdate(false)
		int c0 = 0
		int c1 = 1
		int c2 = 2
		int c3 = 3
		int c4 = 4
	}

	@NonSelectProperties([
		@NonSelectProperty(property='c1', value=false),
		@NonSelectProperty(property='c2', value=false),
		@NonSelectProperty(property='c3'),
		@NonSelectProperty(property='c4', value=true)
	])
	@NonInsertProperties([
		@NonInsertProperty(property='c1', value=false),
		@NonInsertProperty(property='c2', value=false),
		@NonInsertProperty(property='c3'),
		@NonInsertProperty(property='c4', value=true)
	])
	@NonUpdateProperties([
		@NonUpdateProperty(property='c1', value=false),
		@NonUpdateProperty(property='c2', value=false),
		@NonUpdateProperty(property='c3'),
		@NonUpdateProperty(property='c4', value=true)
	])
	static class Entity4 extends Entity3 {
	}

	def "@Non#type #no"(String type, int no, Class<?> entityClass, String expectedSql) {
	/**/DebugTrace.enter()
		setup:
			def entity = entityClass.getDeclaredConstructor().newInstance()
			def sql = new Sql<>(entityClass)
			sql.setEntity(entity).where(entity)

		when:
			def createdSql =
				type == 'Select' ? Sql.database.selectSql(sql, []) :
				type == 'Insert' ? Sql.database.insertSql(sql, []) :
				type == 'Update' ? Sql.database.updateSql(sql, []) : ''
		/**/DebugTrace.print('createdSql', createdSql)

		then:
			createdSql == expectedSql

	/**/DebugTrace.leave()
		where:
			type    |no|entityClass|expectedSql
			'Select'|1 |Entity1    |'SELECT key, c0, c3 FROM Entity1 WHERE key=-1'
			'Select'|2 |Entity2    |'SELECT key, c0, c3 FROM Entity2 WHERE key=-1'
			'Select'|3 |Entity3    |'SELECT key, c0, c3, c4 FROM Entity3 WHERE key=-1'
			'Select'|4 |Entity4    |'SELECT key, c0, c1, c2 FROM Entity4 WHERE key=-1'

			'Insert'|1 |Entity1    |'INSERT INTO Entity1 (key, c0, c3) VALUES (-1, 0, 3)'
			'Insert'|2 |Entity2    |'INSERT INTO Entity2 (key, c0, c3) VALUES (-1, 0, 3)'
			'Insert'|3 |Entity3    |'INSERT INTO Entity3 (key, c0, c3, c4) VALUES (-1, 0, 3, 4)'
			'Insert'|4 |Entity4    |'INSERT INTO Entity4 (key, c0, c1, c2) VALUES (-1, 0, 1, 2)'

			'Update'|1 |Entity1    |'UPDATE Entity1 SET c0=0, c3=3 WHERE key=-1'
			'Update'|2 |Entity2    |'UPDATE Entity2 SET c0=0, c3=3 WHERE key=-1'
			'Update'|3 |Entity3    |'UPDATE Entity3 SET c0=0, c3=3, c4=4 WHERE key=-1'
			'Update'|4 |Entity4    |'UPDATE Entity4 SET c0=0, c1=1, c2=2 WHERE key=-1'
	}
}
