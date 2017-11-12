// KeyTableSpec.groovy
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

// KeyTableSpec
// @since 2.0.0
@Unroll
class KeyTableSpec extends Specification {
	@Table('Table1')
	@KeyProperties([
		@KeyProperty(property='n1.n2.n3.c0', value=false),
		@KeyProperty(property='n1.n2.n3.c1'),
		@KeyProperty(property='n1.n2.n3.c2', value=true),
		@KeyProperty(property='n1.n2.n3.c3', value=false),
//		@KeyProperty(property='n1.n2.n3.c4', value=false)
	])
	static class Entity1 {
		static class N1 {
			static class N2 {
				static class N3 {
					int c0 = 0

					@Key(false)
					int c1 = 1

					@Key(false)
					int c2 = 2

					@Key
					int c3 = 3

					@Key(true)
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

	@Table('Table3')
	@KeyProperties([
		@KeyProperty(property='c1'),
		@KeyProperty(property='c2', value=true),
		@KeyProperty(property='c3', value=false),
		@KeyProperty(property='c4', value=false)
	])
	static class Entity3 {
		@Key(false)
		int c0 = 0
		int c1 = 1
		int c2 = 2
		int c3 = 3
		int c4 = 4
	}

	@Table('super')
	@KeyProperties([
		@KeyProperty(property='c1', value=false),
		@KeyProperty(property='c2', value=false),
		@KeyProperty(property='c3'),
		@KeyProperty(property='c4', value=true)
	])
	static class Entity4 extends Entity3 {
	}

	def "@Key #method #no"(String method, int no, Class<?> entityClass, String expectedSql) {
	/**/DebugTrace.enter()
		setup:
			def entity = entityClass.getDeclaredConstructor().newInstance()
			def sql = new Sql<>(entityClass)
			sql.setEntity(entity).where(entity)

		when:
			def createdSql =
				method == 'select' ? Standard.instance.selectSql(sql, []) :
				method == 'insert' ? Standard.instance.insertSql(sql, []) :
				method == 'update' ? Standard.instance.updateSql(sql, []) :
				method == 'delete' ? Standard.instance.deleteSql(sql, []) : ''
		/**/DebugTrace.print('createdSql', createdSql)

		then:
			createdSql == expectedSql

	/**/DebugTrace.leave()
		where:
			method  |no|entityClass|expectedSql
			'select'|1 |Entity1    |'SELECT c0, c1, c2, c3, c4 FROM Table1 WHERE c1=1 AND c2=2 AND c4=4'
			'select'|2 |Entity2    |'SELECT c0, c1, c2, c3, c4 FROM Entity2 WHERE c1=1 AND c2=2 AND c4=4'
			'select'|3 |Entity3    |'SELECT c0, c1, c2, c3, c4 FROM Table3 WHERE c1=1 AND c2=2'
			'select'|4 |Entity4    |'SELECT c0, c1, c2, c3, c4 FROM Table3 WHERE c3=3 AND c4=4'

			'insert'|1 |Entity1    |'INSERT INTO Table1 (c0, c1, c2, c3, c4) VALUES (0, 1, 2, 3, 4)'
			'insert'|2 |Entity2    |'INSERT INTO Entity2 (c0, c1, c2, c3, c4) VALUES (0, 1, 2, 3, 4)'
			'insert'|3 |Entity3    |'INSERT INTO Table3 (c0, c1, c2, c3, c4) VALUES (0, 1, 2, 3, 4)'
			'insert'|4 |Entity4    |'INSERT INTO Table3 (c0, c1, c2, c3, c4) VALUES (0, 1, 2, 3, 4)'

			'update'|1 |Entity1    |'UPDATE Table1 SET c0=0, c3=3 WHERE c1=1 AND c2=2 AND c4=4'
			'update'|2 |Entity2    |'UPDATE Entity2 SET c0=0, c3=3 WHERE c1=1 AND c2=2 AND c4=4'
			'update'|3 |Entity3    |'UPDATE Table3 SET c0=0, c3=3, c4=4 WHERE c1=1 AND c2=2'
			'update'|4 |Entity4    |'UPDATE Table3 SET c0=0, c1=1, c2=2 WHERE c3=3 AND c4=4'

			'delete'|1 |Entity1    |'DELETE FROM Table1 WHERE c1=1 AND c2=2 AND c4=4'
			'delete'|2 |Entity2    |'DELETE FROM Entity2 WHERE c1=1 AND c2=2 AND c4=4'
			'delete'|3 |Entity3    |'DELETE FROM Table3 WHERE c1=1 AND c2=2'
			'delete'|4 |Entity4    |'DELETE FROM Table3 WHERE c3=3 AND c4=4'
	}
}
