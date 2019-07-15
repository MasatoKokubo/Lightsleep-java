// SelectInsertUpdateSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.entity

import java.sql.Timestamp
import java.util.ArrayList

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.EntityCondition
import org.lightsleep.database.Standard
import org.lightsleep.entity.*

import spock.lang.*

// SelectInsertUpdateSpec
// @since 2.0.0
@Unroll
class SelectInsertUpdateSpec extends Specification {
	@SelectProperties([
		@SelectProperty(property='n1.n2.n3.c0', expression=''),
		@SelectProperty(property='n1.n2.n3.c2', expression='2+20'),
		@SelectProperty(property='n1.n2.n3.c3', expression='3+20'),
	])
	@InsertProperties([
		@InsertProperty(property='n1.n2.n3.c0', expression=''),
		@InsertProperty(property='n1.n2.n3.c2', expression='2-20'),
		@InsertProperty(property='n1.n2.n3.c3', expression='3-20'),
	])
	@UpdateProperties([
		@UpdateProperty(property='n1.n2.n3.c0', expression=''),
		@UpdateProperty(property='n1.n2.n3.c2', expression='2*20'),
		@UpdateProperty(property='n1.n2.n3.c3', expression='3*20'),
	])
	static class Entity1 {
		@Key
		int key = -1

		static class N1 {
			static class N2 {
				static class N3 {
					int c0 = 0

					@Select('1+10')
					@Insert('1-10')
					@Update('1*10')
					int c1 = 1

					int c2 = 2

					@Select('3+10')
					@Insert('3-10')
					@Update('3*10')
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

	@SelectProperty(property='c1', expression='1+10')
	@InsertProperty(property='c1', expression='1-10')
	@UpdateProperty(property='c1', expression='1*10')
	static class Entity3 {
		@Key
		int key = -1

		@Select('')
		@Insert('')
		@Update('')
		int c0 = 0
		int c1 = 1
	}

	@SelectProperty(property='c1', expression='1+20')
	@InsertProperty(property='c1', expression='1-20')
	@UpdateProperty(property='c1', expression='1*20')
	static class Entity4 extends Entity3 {
	}

	@SelectProperty(property='c1', expression='')
	@InsertProperty(property='c1', expression='')
	@UpdateProperty(property='c1', expression='')
	static class Entity5 extends Entity4 {
	}

	def "@#type #no"(String type, int no, Class<?> entityClass, String expectedSql) {
		DebugTrace.enter() // for Debugging
		setup:
			def entity = entityClass.getDeclaredConstructor().newInstance()
			def sql = new Sql<>(entityClass)
			sql.setEntity(entity).where(entity)

		when:
			def createdSql =
				type == 'Select' ? Standard.instance.selectSql(sql, []) :
				type == 'Insert' ? Standard.instance.insertSql(sql, []) :
				type == 'Update' ? Standard.instance.updateSql(sql, []) : ''
			DebugTrace.print('createdSql', createdSql) // for Debugging

		then:
			createdSql == expectedSql

		DebugTrace.leave() // for Debugging
		where:
			type    |no|entityClass|expectedSql
			'Select'|1 |Entity1    |'SELECT key, c0, 1+10 c1, 2+20 c2, 3+20 c3 FROM Entity1 WHERE key=-1'
			'Select'|2 |Entity2    |'SELECT key, c0, 1+10 c1, 2+20 c2, 3+20 c3 FROM Entity2 WHERE key=-1'
			'Select'|3 |Entity3    |'SELECT key, c0, 1+10 c1 FROM Entity3 WHERE key=-1'
			'Select'|4 |Entity4    |'SELECT key, c0, 1+20 c1 FROM Entity4 WHERE key=-1'
			'Select'|5 |Entity5    |'SELECT key, c0, c1 FROM Entity5 WHERE key=-1'

			'Insert'|1 |Entity1    |'INSERT INTO Entity1 (key, c0, c1, c2, c3) VALUES (-1, 0, 1-10, 2-20, 3-20)'
			'Insert'|2 |Entity2    |'INSERT INTO Entity2 (key, c0, c1, c2, c3) VALUES (-1, 0, 1-10, 2-20, 3-20)'
			'Insert'|3 |Entity3    |'INSERT INTO Entity3 (key, c0, c1) VALUES (-1, 0, 1-10)'
			'Insert'|4 |Entity4    |'INSERT INTO Entity4 (key, c0, c1) VALUES (-1, 0, 1-20)'
			'Insert'|5 |Entity5    |'INSERT INTO Entity5 (key, c0, c1) VALUES (-1, 0, 1)'

			'Update'|1 |Entity1    |'UPDATE Entity1 SET c0=0, c1=1*10, c2=2*20, c3=3*20 WHERE key=-1'
			'Update'|2 |Entity2    |'UPDATE Entity2 SET c0=0, c1=1*10, c2=2*20, c3=3*20 WHERE key=-1'
			'Update'|3 |Entity3    |'UPDATE Entity3 SET c0=0, c1=1*10 WHERE key=-1'
			'Update'|4 |Entity4    |'UPDATE Entity4 SET c0=0, c1=1*20 WHERE key=-1'
			'Update'|5 |Entity5    |'UPDATE Entity5 SET c0=0, c1=1 WHERE key=-1'
	}
}
