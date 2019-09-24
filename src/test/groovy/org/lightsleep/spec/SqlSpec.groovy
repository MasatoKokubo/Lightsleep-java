// SqlSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec

import java.sql.Connection
import java.sql.ResultSet
import java.util.function.Supplier

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.connection.*
import org.lightsleep.database.*
import org.lightsleep.entity.*
import org.lightsleep.helper.*
import org.lightsleep.test.entity.*

import spock.lang.*

// SqlSpec
@Unroll
class SqlSpec extends Specification {
	static databases = [
		Standard  .instance,
		Db2       .instance,
		MySQL     .instance,
		Oracle    .instance,
		PostgreSQL.instance,
		SQLite    .instance,
		SQLServer .instance,
	]

	static class TestDatabase implements Database {
		@Override public <E> String selectSql(Sql<E> sql, List<Object> parameters) {return ''}
		@Override public <E> String subSelectSql(Sql<E> sql, List<Object> parameters) {return ''}
		@Override public <E> String subSelectSql(Sql<E> sql, Supplier<CharSequence> columnsSupplier, List<Object> parameters) {return ''}
		@Override public <E> String insertSql(Sql<E> sql, List<Object> parameters) {return ''}
		@Override public <E> String updateSql(Sql<E> sql, List<Object> parameters) {return ''}
		@Override public <E> String deleteSql(Sql<E> sql, List<Object> parameters) {return ''}
		@Override public Map<String, TypeConverter<?, ?>> typeConverterMap() {return null}
		@Override public <T> T convert(Object value, Class<T> type) {return null}
		@Override public String maskPassword(String jdbcUrl) {return jdbcUrl}
		@Override public Object getObject(Connection connection, ResultSet resultSet, String columnLabel) {return null} // since 3.0.0
	}

	// Sql.getEntityInfo(Class<E>)
	def "SqlSpec getEntityInfo - NullPointerException"() {
		DebugTrace.enter() // for Debugging
		when: Sql.getEntityInfo(null)
		then: def e = thrown NullPointerException
			DebugTrace.print("e", e)
		DebugTrace.leave() // for Debugging
	}

	// Sql.Sql(Class<E>), Sql.Sql(Sql)
	def "SqlSpec constructor - NullPointerException"() {
		DebugTrace.enter() // for Debugging
		when: new Sql<>(null as Class<Contact>)
		then: def e = thrown NullPointerException
			DebugTrace.print("e", e)

		when: new Sql<>(Contact, null)
		then: e = thrown NullPointerException
			DebugTrace.print("e", e)
		DebugTrace.leave() // for Debugging
	}

	// Sql.entityInfo()
	def "SqlSpec entityInfo"() {
		DebugTrace.enter() // for Debugging
		when: def entityInfo = new Sql<>(Contact).entityInfo()
		then: entityInfo.entityClass() == Contact
		DebugTrace.leave() // for Debugging
	}

	// Sql.entityClass()
	def "SqlSpec entityClass"() {
		DebugTrace.enter() // for Debugging
		when: def clazz = new Sql<>(Contact).entityClass()
		then: clazz == Contact
		DebugTrace.leave() // for Debugging
	}

	// Sql.entity()
	def "SqlSpec entity"() {
		DebugTrace.enter() // for Debugging
		when: def contact = new Sql<>(Contact).entity()
		then: contact == null
		DebugTrace.leave() // for Debugging
	}

	// Sql.distinct()
	// Sql.isDistinct()
	def "SqlSpec distinct isDistinct - #databaseName"(Database database, String databaseName) {
		DebugTrace.enter() // for Debugging
		expect:
			new Sql<>(Contact).isDistinct() == false
			new Sql<>(Contact).distinct().isDistinct()

		when: def selectSql = database.selectSql(new Sql<>(Contact).distinct(), [])
			DebugTrace.print(database.getClass().simpleName + ': ', selectSql) // for Debugging
		then: selectSql.startsWith('SELECT DISTINCT id,')

		DebugTrace.leave() // for Debugging
		where:
			database << databases
			databaseName = database.getClass().simpleName
	}

	// 3.1.0
	// Sql tableAlias from, union
	def "SqlSpec tableAlias from, union"() {
		DebugTrace.enter() // for Debugging
		def sql = null as Sql<Contact>

		// tableAlias from(from)
		when:
			sql = new Sql<>(Contact).from(new Sql<>(Contact).from(new Sql<>(Contact)))
		then:
			sql.tableAlias()           == ''
			sql.from.tableAlias()      == ''
			sql.from.from.tableAlias() == ''

		// tableAlias from(from)
		when:
			sql = new Sql<>(Contact, 'C').from(new Sql<>(Contact).from(new Sql<>(Contact)))
		then:
			sql.tableAlias()           == 'C'
			sql.from.tableAlias()      == 'C'
			sql.from.from.tableAlias() == 'C'

		// from tableAlias(from)
		when:
			sql = new Sql<>(Contact).from(new Sql<>(Contact, 'C').from(new Sql<>(Contact)))
		then:
			sql.tableAlias()           == 'C'
			sql.from.tableAlias()      == 'C'
			sql.from.from.tableAlias() == 'C'

		// from(from tableAlias)
		when:
			sql = new Sql<>(Contact).from(new Sql<>(Contact).from(new Sql<>(Contact, 'C')))
		then:
			sql.tableAlias()           == 'C'
			sql.from.tableAlias()      == 'C'
			sql.from.from.tableAlias() == 'C'

		// union(union)
		when:
			sql = new Sql<>(Contact)
				.union(new Sql<>(Contact)
					.union(new Sql<>(Contact))
					.union(new Sql<>(Contact))
				)
				.union(new Sql<>(Contact)
					.unionAll(new Sql<>(Contact))
					.unionAll(new Sql<>(Contact))
				)
		then:
			sql.tableAlias()                           == ''
			sql.unionSqls[0].tableAlias()              == ''
			sql.unionSqls[0].unionSqls[0].tableAlias() == ''
			sql.unionSqls[0].unionSqls[1].tableAlias() == ''
			sql.unionSqls[1].tableAlias()              == ''
			sql.unionSqls[1].unionSqls[0].tableAlias() == ''
			sql.unionSqls[1].unionSqls[1].tableAlias() == ''

		// tableAlias union(union)
		when:
			sql = new Sql<>(Contact, 'C')
				.union(new Sql<>(Contact)
					.union(new Sql<>(Contact))
					.union(new Sql<>(Contact))
				)
				.union(new Sql<>(Contact)
					.unionAll(new Sql<>(Contact))
					.unionAll(new Sql<>(Contact))
				)
		then:
			sql.tableAlias()                           == 'C'
			sql.unionSqls[0].tableAlias()              == 'C'
			sql.unionSqls[0].unionSqls[0].tableAlias() == 'C'
			sql.unionSqls[0].unionSqls[1].tableAlias() == 'C'
			sql.unionSqls[1].tableAlias()              == 'C'
			sql.unionSqls[1].unionSqls[0].tableAlias() == 'C'
			sql.unionSqls[1].unionSqls[1].tableAlias() == 'C'

		// union tableAlias(union)
		when:
			sql = new Sql<>(Contact)
				.union(new Sql<>(Contact)
					.unionAll(new Sql<>(Contact))
					.unionAll(new Sql<>(Contact))
					.unionAll(new Sql<>(Contact))
				)
				.union(new Sql<>(Contact, 'C')
					.unionAll(new Sql<>(Contact))
					.unionAll(new Sql<>(Contact))
					.unionAll(new Sql<>(Contact))
				)
				.union(new Sql<>(Contact)
					.unionAll(new Sql<>(Contact))
					.unionAll(new Sql<>(Contact))
					.unionAll(new Sql<>(Contact))
				)
		then:
			sql.tableAlias()                           == 'C'
			sql.unionSqls[0].tableAlias()              == 'C'
			sql.unionSqls[0].unionSqls[0].tableAlias() == 'C'
			sql.unionSqls[0].unionSqls[1].tableAlias() == 'C'
			sql.unionSqls[0].unionSqls[2].tableAlias() == 'C'
			sql.unionSqls[1].tableAlias()              == 'C'
			sql.unionSqls[1].unionSqls[0].tableAlias() == 'C'
			sql.unionSqls[1].unionSqls[1].tableAlias() == 'C'
			sql.unionSqls[1].unionSqls[2].tableAlias() == 'C'
			sql.unionSqls[2].tableAlias()              == 'C'
			sql.unionSqls[2].unionSqls[0].tableAlias() == 'C'
			sql.unionSqls[2].unionSqls[1].tableAlias() == 'C'
			sql.unionSqls[2].unionSqls[2].tableAlias() == 'C'

		// union tableAlias(union)
		when:
			sql = new Sql<>(Contact)
				.union(new Sql<>(Contact)
					.unionAll(new Sql<>(Contact))
					.unionAll(new Sql<>(Contact))
					.unionAll(new Sql<>(Contact))
				)
				.union(new Sql<>(Contact)
					.unionAll(new Sql<>(Contact))
					.unionAll(new Sql<>(Contact, 'C'))
					.unionAll(new Sql<>(Contact))
				)
				.union(new Sql<>(Contact)
					.unionAll(new Sql<>(Contact))
					.unionAll(new Sql<>(Contact))
					.unionAll(new Sql<>(Contact))
				)
		then:
			sql.tableAlias()                           == 'C'
			sql.unionSqls[0].tableAlias()              == 'C'
			sql.unionSqls[0].unionSqls[0].tableAlias() == 'C'
			sql.unionSqls[0].unionSqls[1].tableAlias() == 'C'
			sql.unionSqls[0].unionSqls[2].tableAlias() == 'C'
			sql.unionSqls[1].tableAlias()              == 'C'
			sql.unionSqls[1].unionSqls[0].tableAlias() == 'C'
			sql.unionSqls[1].unionSqls[1].tableAlias() == 'C'
			sql.unionSqls[1].unionSqls[2].tableAlias() == 'C'
			sql.unionSqls[2].tableAlias()              == 'C'
			sql.unionSqls[2].unionSqls[0].tableAlias() == 'C'
			sql.unionSqls[2].unionSqls[1].tableAlias() == 'C'
			sql.unionSqls[2].unionSqls[2].tableAlias() == 'C'

		DebugTrace.leave() // for Debugging
	}

	// 3.1.0
	// Sql.columns(String...)
	// Sql.getColumns()
	def "SqlSpec columns from getColumns"() {
		DebugTrace.enter() // for Debugging
		def sql = null as Sql<Contact>

		// getColumns()
		when:
			sql = new Sql<>(Contact)
		then:
			sql.columns == [] as Set

		// columns
		// from(from)
		when:
			sql = new Sql<>(Contact)
				.columns('name.first', 'name.last')
				.from(new Sql<>(Contact).from(new Sql<>(Contact)))
		then:
			sql.columns           == ['name.first', 'name.last'] as Set
			sql.from.columns      == ['name.first', 'name.last'] as Set
			sql.from.from.columns == ['name.first', 'name.last'] as Set

		// from(from)
		// columns
		when:
			sql = new Sql<>(Contact)
				.from(new Sql<>(Contact).from(new Sql<>(Contact)))
				.columns('name.first', 'name.last')
		then:
			sql.columns           == ['name.first', 'name.last'] as Set
			sql.from.columns      == ['name.first', 'name.last'] as Set
			sql.from.from.columns == ['name.first', 'name.last'] as Set

		// from(columns, from)
		when:
			sql = new Sql<>(Contact)
				.from(new Sql<>(Contact)
					.columns('name.first', 'name.last')
					.from(new Sql<>(Contact))
				)
		then:
			sql.columns           == ['name.first', 'name.last'] as Set
			sql.from.columns      == ['name.first', 'name.last'] as Set
			sql.from.from.columns == ['name.first', 'name.last'] as Set

		// from(from, columns)
		when:
			sql = new Sql<>(Contact)
				.from(new Sql<>(Contact)
					.from(new Sql<>(Contact))
					.columns('name.first', 'name.last')
				)
		then:
			sql.columns           == ['name.first', 'name.last'] as Set
			sql.from.columns      == ['name.first', 'name.last'] as Set
			sql.from.from.columns == ['name.first', 'name.last'] as Set

		// from(from(columns)
		when:
			sql = new Sql<>(Contact)
				.from(new Sql<>(Contact)
					.from(new Sql<>(Contact)
						.columns('name.first', 'name.last')
					)
				)
		then:
			sql.columns           == ['name.first', 'name.last'] as Set
			sql.from.columns      == ['name.first', 'name.last'] as Set
			sql.from.from.columns == ['name.first', 'name.last'] as Set

		DebugTrace.leave() // for Debugging
	}

	// 3.1.0
	// Sql.columns(String...)
	// Sql.getColumns()
	def "SqlSpec columns union(), unionAll, getColumns"() {
		DebugTrace.enter() // for Debugging
		def sql = null as Sql<Contact>

		// union(union(from), union(from))
		// columns
		// union(unionAll(from), unionAll(from))
		when:
			sql = new Sql<>(Contact)
				.union(new Sql<>(Contact)
					.union(new Sql<>(Contact).from(new Sql<>(Contact)))
					.union(new Sql<>(Contact).from(new Sql<>(Contact)))
				)
				.columns('name.first', 'name.last')
				.union(new Sql<>(Contact)
					.unionAll(new Sql<>(Contact).from(new Sql<>(Contact)))
					.unionAll(new Sql<>(Contact).from(new Sql<>(Contact)))
				)
		then:
			sql.columns                                == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].columns                   == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].unionSqls[0].columns      == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].unionSqls[0].from.columns == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].unionSqls[1].columns      == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].unionSqls[1].from.columns == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].columns                   == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].unionSqls[0].columns      == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].unionSqls[0].from.columns == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].unionSqls[1].columns      == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].unionSqls[1].from.columns == ['name.first', 'name.last'] as Set

		// union(union(from), columns, union)(from)
		// union(unionAll(from), unionAll(from))
		when:
			sql = new Sql<>(Contact)
				.union(new Sql<>(Contact)
					.union(new Sql<>(Contact).from(new Sql<>(Contact)))
					.columns('name.first', 'name.last')
					.union(new Sql<>(Contact).from(new Sql<>(Contact)))
				)
				.union(new Sql<>(Contact)
					.unionAll(new Sql<>(Contact).from(new Sql<>(Contact)))
					.unionAll(new Sql<>(Contact).from(new Sql<>(Contact)))
				)
		then:
			sql.columns                                == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].columns                   == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].unionSqls[0].columns      == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].unionSqls[0].from.columns == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].unionSqls[1].columns      == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].unionSqls[1].from.columns == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].columns                   == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].unionSqls[0].columns      == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].unionSqls[0].from.columns == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].unionSqls[1].columns      == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].unionSqls[1].from.columns == ['name.first', 'name.last'] as Set

		// union(union(from), union)(from)
		// union(unionAll(from), unionAll(from, column))
		when:
			sql = new Sql<>(Contact)
				.union(new Sql<>(Contact)
					.union(new Sql<>(Contact).from(new Sql<>(Contact)))
					.union(new Sql<>(Contact).from(new Sql<>(Contact)))
				)
				.union(new Sql<>(Contact)
					.unionAll(new Sql<>(Contact).from(new Sql<>(Contact)))
					.unionAll(new Sql<>(Contact).from(new Sql<>(Contact)).columns('name.first', 'name.last'))
				)
		then:
			sql.columns                                == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].columns                   == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].unionSqls[0].columns      == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].unionSqls[0].from.columns == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].unionSqls[1].columns      == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].unionSqls[1].from.columns == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].columns                   == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].unionSqls[0].columns      == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].unionSqls[0].from.columns == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].unionSqls[1].columns      == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].unionSqls[1].from.columns == ['name.first', 'name.last'] as Set

		// union(union(from), union)(from)
		// union(unionAll(from), unionAll(from(column)))
		when:
			sql = new Sql<>(Contact)
				.union(new Sql<>(Contact)
					.union(new Sql<>(Contact).from(new Sql<>(Contact)))
					.union(new Sql<>(Contact).from(new Sql<>(Contact)))
				)
				.union(new Sql<>(Contact)
					.unionAll(new Sql<>(Contact).from(new Sql<>(Contact)))
					.unionAll(new Sql<>(Contact).from(new Sql<>(Contact).columns('name.first', 'name.last')))
				)
		then:
			sql.columns                                == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].columns                   == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].unionSqls[0].columns      == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].unionSqls[0].from.columns == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].unionSqls[1].columns      == ['name.first', 'name.last'] as Set
			sql.unionSqls[0].unionSqls[1].from.columns == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].columns                   == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].unionSqls[0].columns      == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].unionSqls[0].from.columns == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].unionSqls[1].columns      == ['name.first', 'name.last'] as Set
			sql.unionSqls[1].unionSqls[1].from.columns == ['name.first', 'name.last'] as Set

		DebugTrace.leave() // for Debugging
	}

	// 3.1.0
	// Sql.columns from - NullPointerException
	def "SqlSpec columns - NullPointerException"() {
		DebugTrace.enter() // for Debugging
		// Sql.columns(String...)
		when: new Sql<>(Contact).columns(null as String[])
		then: def e = thrown NullPointerException
			DebugTrace.print("e", e)

		// Sql.columns(null as Collection)
		when: new Sql<>(Contact).columns(null as Collection<String>)
		then: e = thrown NullPointerException
			DebugTrace.print("e", e)
	}

	// 3.1.0
	// Sql.columns from - IllegalStateException
	def "SqlSpec columns from - IllegalStateException"() {
		DebugTrace.enter() // for Debugging
		// Sql.columns, from(columns)
		when: new Sql<>(Contact)
			.columns('first', 'last')
			.from(new Sql<>(Contact).columns('first'))
		then: def e = thrown IllegalStateException
			DebugTrace.print("e", e)

		// Sql.from(columns) columns
		when: new Sql<>(Contact)
			.from(new Sql<>(Contact).columns('first'))
			.columns('first', 'last')
		then: e = thrown IllegalStateException
			DebugTrace.print("e", e)

		// Sql.columns from(from(columns))
		when: new Sql<>(Contact)
			.columns('first', 'last')
			.from(new Sql<>(Contact)
				.from(new Sql<>(Contact).columns('first'))
			)
		then: e = thrown IllegalStateException
			DebugTrace.print("e", e)

		// Sql.from(from(columns)) columns
		when: new Sql<>(Contact)
			.from(new Sql<>(Contact)
				.from(new Sql<>(Contact).columns('first'))
			)
			.columns('first', 'last')
		then: e = thrown IllegalStateException
			DebugTrace.print("e", e)

		// Sql.from(columns from(columns))
		when: new Sql<>(Contact)
			.from(new Sql<>(Contact)
				.columns('first', 'last')
				.from(new Sql<>(Contact).columns('first'))
			)
		then: e = thrown IllegalStateException
			DebugTrace.print("e", e)

		// Sql.from(from(columns) columns)
		when: new Sql<>(Contact)
			.from(new Sql<>(Contact)
				.from(new Sql<>(Contact).columns('first'))
				.columns('first', 'last')
			)
		then: e = thrown IllegalStateException
			DebugTrace.print("e", e)

		DebugTrace.leave() // for Debugging
	}

	// 3.1.0
	// Sql.columns union IllegalStateException
	def "SqlSpec columns union - IllegalStateException"() {
		DebugTrace.enter() // for Debugging

		// columns, union(columns)
		when: new Sql<>(Contact)
			.columns('first', 'last')
			.union(new Sql<>(Contact))
			.union(new Sql<>(Contact).columns('first'))
		then: def e = thrown IllegalStateException
			DebugTrace.print("e", e)

		// union(columns) columns
		when: new Sql<>(Contact)
			.union(new Sql<>(Contact).columns('first'))
			.union(new Sql<>(Contact))
			.columns('first', 'last')
		then: e = thrown IllegalStateException
			DebugTrace.print("e", e)

		// columns, union(from(columns))
		when: new Sql<>(Contact)
			.columns('first', 'last')
			.union(new Sql<>(Contact).from(new Sql<>(Contact)))
			.union(new Sql<>(Contact).from(new Sql<>(Contact).columns('first')))
		then: e = thrown IllegalStateException
			DebugTrace.print("e", e)

		// union(from(columns)), columns
		when: new Sql<>(Contact)
			.union(new Sql<>(Contact).from(new Sql<>(Contact).columns('first')))
			.union(new Sql<>(Contact).from(new Sql<>(Contact)))
			.columns('first', 'last')
		then: e = thrown IllegalStateException
			DebugTrace.print("e", e)

		DebugTrace.leave() // for Debugging
	}

	// Sql.columns(String...)
	// Sql.columns(Collection)
	// Sql.columns()
	def "SqlSpec columns getColumns - #databaseName"(Database database, String databaseName) {
		DebugTrace.enter() // for Debugging

		setup:
			String selectSql = null

		// Sql.columns(String...)
		when:
			selectSql = database.selectSql(
				new Sql<>(Contact)
					.columns('name.first', 'name.last', 'birthday')
				, [])
			DebugTrace.print(database.getClass().simpleName + ': ', selectSql) // for Debugging

		then:
			selectSql.startsWith('SELECT firstName, lastName, birthday FROM Contact ')

		// Sql.columns(Collection)
		when:
			selectSql = database.selectSql(
				new Sql<>(Contact)
					.columns(['name.first', 'name.last', 'birthday'])
				, [])
			DebugTrace.print(database.getClass().simpleName + ': ', selectSql) // for Debugging

		then:
			selectSql.startsWith('SELECT firstName, lastName, birthday FROM Contact ')

		// Sql.columns(String...)
		when:
			selectSql = database.selectSql(
				new Sql<>(Contact)
					.columns('*')
				, [])
			DebugTrace.print(database.getClass().simpleName + ': ', selectSql) // for Debugging

		then:
			selectSql.startsWith('SELECT id, updateCount, created, updated, firstName, lastName, birthday, addressId FROM Contact ')

		// Sql.columns(String...)
		when:
			selectSql = database.selectSql(
				new Sql<>(Contact, 'C')
					.innerJoin(Phone, 'P', '{P.contactId} = {C.id}')
					.columns('C.id', 'P.id')
				, [])
			DebugTrace.print(database.getClass().simpleName + ': ', selectSql) // for Debugging

		then:
			selectSql.startsWith('SELECT C.id C_id, P.id P_id FROM Contact ')

		// Sql.columns(String...)
		when:
			selectSql = database.selectSql(
				new Sql<>(Contact, 'C')
					.innerJoin(Phone, 'P', '{P.contactId} = {C.id}')
					.columns('P.*')
				, [])
			DebugTrace.print(database.getClass().simpleName + ': ', selectSql) // for Debugging

		then:
			selectSql.indexOf(' P.id P_id, ') >= 0
			selectSql.indexOf(' P.contactId P_contactId, P.phoneNumber P_phoneNumber FROM Contact ') >= 0
			selectSql.indexOf(' C_') == -1

		DebugTrace.leave() // for Debugging
		where:
			database << databases
			databaseName = database.getClass().simpleName
	}

	// Sql.getColumns()
	// Sql.setColumns(Set)
	def "SqlSpec setColumns(Set)"() {
		DebugTrace.enter() // for Debugging

		// Sql.setColumns(Set)
		// Sql.columns(String...)
		when:
			def sql = new Sql<>(Contact)
				.setColumns(new LinkedHashSet<String>())
				.columns('name.first', 'name.last', 'birthday')
		then: sql.columns == ['name.first', 'name.last', 'birthday'] as Set

		// Sql.setColumns(Set)
		when: sql = new Sql<>(Contact).setColumns(['name.first', 'name.last'] as Set)
		then: sql.columns == ['name.first', 'name.last'] as Set

		DebugTrace.leave() // for Debugging
	}

	@NonColumnProperties([
		@NonColumnProperty(property='id'),
		@NonColumnProperty(property='updateCount'),
		@NonColumnProperty(property='created'),
		@NonColumnProperty(property='updated'),
		@NonColumnProperty(property='name.last'),
		@NonColumnProperty(property='name.first'),
		@NonColumnProperty(property='addressId'),
	])
	class ContactBirthday extends Contact {
	}
	class ContactName {
		final PersonName name = new PersonName()
	}
	class Nothing {
	}

	// Sql.setColumns(Class)
	def "SqlSpec setColumns(Class)"() {
		DebugTrace.enter() // for Debugging
		expect:
			new Sql<>(Contact).setColumns(ContactBirthday).columns == ['birthday'] as Set
			new Sql<>(Contact).setColumns(ContactName).columns == ['name.first', 'name.last'] as Set
			new Sql<>(Contact).setColumns(Nothing).columns.empty
			new Sql<>(Contact, 'C').setColumns(ContactBirthday).columns == ['C.birthday'] as Set
			new Sql<>(Contact, 'C').setColumns(ContactName).columns == ['C.name.last', 'C.name.first'] as Set
			new Sql<>(Contact, 'C').setColumns(Nothing).columns.empty
		DebugTrace.leave() // for Debugging
	}

	// Sql.expression(String, Expression)
	// Sql.expression(String, String, Object...)
	// Sql.getExpression(String)
	def "SqlSpec expression getExpression"() {
		DebugTrace.enter() // for Debugging
		setup:
			String selectSql = null
			String insertSql = null
			String updateSql = null

		expect:
			new Sql<>(Contact).getExpression('name.first') == Expression.EMPTY

			new Sql<>(Contact)
				.expression('name.first', new Expression('AAA'))
				.getExpression('name.first').content() == 'AAA'

			new Sql<>(Contact)
				.expression('name.first', 'BBB')
				.getExpression('name.first').content() == 'BBB'

			new Sql<>(Contact)
				.expression('name.first', new Expression('AAA'))
				.expression('name.first', Expression.EMPTY)
				.getExpression('name.first').empty

			new Sql<>(Contact)
				.expression('name.first', 'BBB')
				.expression('name.first', Expression.EMPTY)
				.getExpression('name.first').empty

		// SELECT SQL {propertyName}
		when:
			selectSql = Standard.instance.selectSql(
				new Sql<>(Contact).expression("name.first", "'['||{name.first}||']'"),
				[])
			DebugTrace.print('selectSql', selectSql) // for Debugging

		then:
			selectSql.indexOf("'['||firstName||']'") >= 0

		// SELECT SQL (with table alias) {propertyName}
		when:
			selectSql = Standard.instance.selectSql(
				new Sql<>(Contact, "C").expression("name.first", "'['||{name.first}||']'"),
				[])
			DebugTrace.print('selectSql', selectSql) // for Debugging

		then:
			selectSql.indexOf("'['||C.firstName||']'") >= 0

		// SELECT SQL (with table alias) {A.propertyName}
		when:
			selectSql = Standard.instance.selectSql(
				new Sql<>(Contact, "C").expression("C.name.last", "'['||{C.name.last}||']'"),
				[])
			DebugTrace.print('selectSql', selectSql) // for Debugging

		then:
			selectSql.indexOf("'['||C.lastName||']'") >= 0

		// INSERT SQL {#propertyName}
		when:
			Contact contact = new Contact()
			contact.name.last = "Apple"
			contact.name.first = "Yukari"
			insertSql = Standard.instance.insertSql(
				new Sql<>(Contact)
					.setEntity(contact)
					.expression("name.first", "'['||{#name.first}||']'"),
				[])
			DebugTrace.print('insertSql', insertSql) // for Debugging

		then:
			insertSql.indexOf("'['||'Yukari'||']'") >=0

		// INSERT SQL (with table alias) {#propertyName}
		when:
			insertSql = Standard.instance.insertSql(
				new Sql<>(Contact, "C")
					.setEntity(contact)
					.expression("name.last", "'['||{#name.last}||']'"),
				[])
			DebugTrace.print('insertSql', insertSql) // for Debugging

		then:
			insertSql.indexOf("'['||'Apple'||']'") >=0

		// UPDATE SQL {#propertyName}
		when:
			contact.name.last = "Orange"
			contact.name.first = "Harumi"
			updateSql = Standard.instance.updateSql(
				new Sql<>(Contact)
					.setEntity(contact)
					.expression("name.first", "'['||{#name.first}||']'"),
				[])
			DebugTrace.print('updateSql', updateSql) // for Debugging

		then:
			updateSql.indexOf("'['||'Harumi'||']'") >=0

		// UPDATE SQL (with table alias) {#propertyName}
		when:
			updateSql = Standard.instance.updateSql(
				new Sql<>(Contact, "C")
					.setEntity(contact)
					.expression("name.last", "'['||{#name.last}||']'"),
				[])
			DebugTrace.print('updateSql', updateSql) // for Debugging

		then:
			updateSql.indexOf("'['||'Orange'||']'") >=0

		DebugTrace.leave() // for Debugging
	}


	// Sql.expression(String, Expression)
	// Sql.expression(String, String, Object...)
	def "SqlSpec NullPointerException #caseNo"(
		String caseNo, String property, Class<?> expressionType, Object expression, Class<? extends Exception> exception) {
		DebugTrace.enter() // for Debugging
		when:
			if (expressionType == Expression)
				new Sql<>(Contact).expression(property, (Expression)expression)

			else if (expressionType == String)
				new Sql<>(Contact).expression(property, (String)expression)

		then:
			def e = thrown exception
			DebugTrace.print("e", e)

		DebugTrace.leave() // for Debugging
		where:
			caseNo|property    |expressionType|expression           |exception
			'1-1' |null        |Expression    |new Expression('AAA')|NullPointerException
			'1-4' |'name.first'|Expression    |null                 |NullPointerException
			'2-1' |null        |String        |'AAA'                |NullPointerException
			'2-4' |'name.first'|String        |null                 |NullPointerException
	}

	// Sql.getExpression(String)
	def "SqlSpec getExpression - NullPointerException #caseNo"(
		String caseNo, String property, Class<? extends Exception> exception) {
		DebugTrace.enter() // for Debugging
		when: new Sql<>(Contact).getExpression(property)
		then: def e = thrown exception
			DebugTrace.print("e", e)

		DebugTrace.leave() // for Debugging
		where:
			caseNo|property|exception
			'1-1' |null    |NullPointerException
	}

	// Sql.doAlways(Consumer)
	// Sql.doIf(boolean, Consumer)
	// Sql.doNotIf(boolean, Consumer)
	// Sql.doElse(Consumer)
	// Sql.doIf(boolean, Consumer, Consumer)
	def "SqlSpec doAlways, doIf, doNotIf, doElse"() {
		DebugTrace.enter() // for Debugging
		expect:
			new Sql<>(Contact)
				.limit(0)
				.doAlways({it.limit(1)})
				.limit == 1

			new Sql<>(Contact)
				.limit(0)
				.doIf(true) {it.limit(1)}
				.limit == 1

			new Sql<>(Contact)
				.limit(0)
				.doIf(false) {it.limit(1)}
				.limit == 0

			new Sql<>(Contact)
				.limit(0)
				.doIf(true) {it.limit(1)}
				.doElse {it.limit(2)}
				.limit == 1

			new Sql<>(Contact)
				.limit(0)
				.doIf(false) {it.limit(1)}
				.doElse {it.limit(2)}
				.limit == 2

			new Sql<>(Contact)
				.limit(0)
				.doElse {it.limit(1)}
				.doIf(false) {it.limit(2)}
				.offset(0)
				.doElse {it.limit(3)}
				.doElse {it.limit(4)}
				.limit == 3

			new Sql<>(Contact)
				.limit(0)
				.doNotIf(true) {it.limit(1)}
				.limit == 0

			new Sql<>(Contact)
				.limit(0)
				.doNotIf(false) {it.limit(1)}
				.limit == 1

			new Sql<>(Contact)
				.limit(0)
				.doNotIf(true) {it.limit(1)}
				.doElse {it.limit(2)}
				.limit == 2

			new Sql<>(Contact)
				.limit(0)
				.doNotIf(false) {it.limit(1)}
				.doElse {it.limit(2)}
				.limit == 1

			new Sql<>(Contact)
				.limit(0)
				.doElse {it.limit(1)}
				.doNotIf(true) {it.limit(2)}
				.offset(0)
				.doElse {it.limit(3)}
				.doElse {it.limit(4)}
				.limit == 3

			new Sql<>(Contact)
				.limit(0)
				.doIf(true, {it.limit(1)}, {it.limit(2)})
				.limit == 1

			new Sql<>(Contact)
				.limit(0)
				.doIf(false, {it.limit(1)}, {it.limit(2)})
				.limit == 2

		when: new Sql<>(Contact).doAlways(null)
		then: def e = thrown NullPointerException
			DebugTrace.print("e", e)

		when: new Sql<>(Contact).doIf(true, null)
		then: e = thrown NullPointerException
			DebugTrace.print("e", e)

		when: new Sql<>(Contact).doIf(true, null, {})
		then: e = thrown NullPointerException
			DebugTrace.print("e", e)

		when: new Sql<>(Contact).doIf(false, {}, null)
		then: e = thrown NullPointerException
			DebugTrace.print("e", e)

		when: new Sql<>(Contact).doIf(false, {}).doElse(null)
		then: e = thrown NullPointerException
			DebugTrace.print("e", e)

		DebugTrace.leave() // for Debugging
	}


	// Sql.innerJoin(Class<JE>, String, Condition)
	// Sql.innerJoin(Class<JE>, String, Condition, Object...)
	// Sql.leftJoin(Class<JE>, String, Condition)
	// Sql.leftJoin(Class<JE>, String, Condition, Object...)
	// Sql.rightJoin(Class<JE>, String, Condition)
	// Sql.rightJoin(Class<JE>, String, Condition, Object...)
	def "SqlSpec innerJoin leftJoin rightJoin"() {
		DebugTrace.enter() // for Debugging
		setup:
			List<JoinInfo<?>> joinInfos = null

		when:
			joinInfos = new Sql<>(Contact, 'C')
				.innerJoin(Address, 'A', '{A.id} = {C.addressId}')
				.getJoinInfos()

		then:
			joinInfos.size() == 1

			joinInfos[0].joinType()   == JoinInfo.JoinType.INNER
			joinInfos[0].entityInfo() == Sql.getEntityInfo(Address)
			joinInfos[0].tableAlias() == 'A'
			joinInfos[0].on()         instanceof Expression

		when:
			joinInfos = new Sql<>(Contact, 'C')
				.innerJoin(Address, 'A', '{A.id} = {C.addressId}')
				.leftJoin (Phone  , 'P', '{P.contactId} = {C.id}')
				.getJoinInfos()

		then:
			joinInfos.size() == 2

			joinInfos[0].joinType()   == JoinInfo.JoinType.INNER
			joinInfos[0].entityInfo() == Sql.getEntityInfo(Address)
			joinInfos[0].tableAlias() == 'A'
			joinInfos[0].on()         instanceof Expression

			joinInfos[1].joinType()   == JoinInfo.JoinType.LEFT
			joinInfos[1].entityInfo() == Sql.getEntityInfo(Phone)
			joinInfos[1].tableAlias() == 'P'
			joinInfos[1].on()         instanceof Expression

		when:
			joinInfos = new Sql<>(Contact, 'C')
				.innerJoin(Address, 'A1', '{A1.id} = {P.addressId}')
				.leftJoin (Phone  , 'P', '{P.contactId} = {C.id}')
				.rightJoin(Address, 'A2', '{A2.id} = {P.addressId}')
				.getJoinInfos()

		then:
			joinInfos.size() == 3

			joinInfos[0].joinType()   == JoinInfo.JoinType.INNER
			joinInfos[0].entityInfo() == Sql.getEntityInfo(Address)
			joinInfos[0].tableAlias() == 'A1'
			joinInfos[0].on()         instanceof Expression

			joinInfos[1].joinType()   == JoinInfo.JoinType.LEFT
			joinInfos[1].entityInfo() == Sql.getEntityInfo(Phone)
			joinInfos[1].tableAlias() == 'P'
			joinInfos[1].on()         instanceof Expression

			joinInfos[2].joinType()   == JoinInfo.JoinType.RIGHT
			joinInfos[2].entityInfo() == Sql.getEntityInfo(Address)
			joinInfos[2].tableAlias() == 'A2'
			joinInfos[2].on()         instanceof Expression

		when:
			joinInfos = new Sql<>(Contact, 'C')
				.innerJoin(Address, 'A1', Condition.of('{A1.id} = {P.addressId}'))
				.leftJoin (Phone  , 'P1', Condition.of('{P1.contactId} = {C.id}'))
				.innerJoin(Address, 'A2', Condition.of('{A2.id} = {P.addressId}'))
				.rightJoin(Phone  , 'P2', Condition.of('{P2.contactId} = {C.id}'))
				.getJoinInfos()

		then:
			joinInfos.size() == 4

			joinInfos[0].joinType()   == JoinInfo.JoinType.INNER
			joinInfos[0].entityInfo() == Sql.getEntityInfo(Address)
			joinInfos[0].tableAlias() == 'A1'
			joinInfos[0].on()         instanceof Expression

			joinInfos[1].joinType()   == JoinInfo.JoinType.LEFT
			joinInfos[1].entityInfo() == Sql.getEntityInfo(Phone)
			joinInfos[1].tableAlias() == 'P1'
			joinInfos[1].on()         instanceof Expression

			joinInfos[2].joinType()   == JoinInfo.JoinType.INNER
			joinInfos[2].entityInfo() == Sql.getEntityInfo(Address)
			joinInfos[2].tableAlias() == 'A2'
			joinInfos[2].on()         instanceof Expression

			joinInfos[3].joinType()   == JoinInfo.JoinType.RIGHT
			joinInfos[3].entityInfo() == Sql.getEntityInfo(Phone)
			joinInfos[3].tableAlias() == 'P2'
			joinInfos[3].on()         instanceof Expression

		DebugTrace.leave() // for Debugging
	}

	// Sql.where(Condition)
	// Sql.where(String, Object...)
	// Sql.where(E)
	// Sql.where(String, Sql<?>)
	// Sql.where(Sql<?>, String)
	// Sql.where()
	def "SqlSpec where getWhere"() {
		DebugTrace.enter() // for Debugging
		expect:
			new Sql<>(Contact).where == Condition.EMPTY
			new Sql<>(Contact).where(Condition.ALL).where == Condition.ALL
			new Sql<>(Contact).where("{id} = 1").where instanceof Expression
			new Sql<>(Contact).where(new Contact()).where instanceof EntityCondition
			new Sql<>(Contact).where('', new Sql<>(Address)).where instanceof SubqueryCondition
			new Sql<>(Contact).where(new Sql<>(Address), '').where instanceof SubqueryCondition // since 3.1.0

		when: new Sql<>(Contact).where((Condition)null)
		then: def e = thrown NullPointerException
			DebugTrace.print("e", e)
		DebugTrace.leave() // for Debugging
	}

	// Sql.and(Condition)
	// Sql.and(String, Object...)
	// Sql.and(String, Sql<?>)
	// Sql.and(Sql<?>, String)
	// Sql.or(Condition)
	// Sql.or(String, Object...)
	// Sql.or(String, Sql<?>)
	// Sql.or(Sql<?>, String)
	def "SqlSpec and or"() {
		DebugTrace.enter() // for Debugging
		expect:
			new Sql<>(Contact)
				.where('A')
				.and(Condition.of('B'))
				.and('C')
				.and('D', new Sql<>(Address))
				.and(new Sql<>(Address), 'E') // since 3.1.0
				.where instanceof And

			new Sql<>(Contact)
				.where('A')
				.or(Condition.of('B'))
				.or('C')
				.or('D', new Sql<>(Address))
				.or(new Sql<>(Address), 'E') // since 3.1.0
				.where instanceof Or

			new Sql<>(Contact)
				.having('A')
				.and(Condition.of('B'))
				.and('C', 1, 2, 3)
				.and('D', new Sql<>(Address))
				.and(new Sql<>(Address), 'E') // since 3.1.0
				.having instanceof And

			new Sql<>(Contact)
				.having(Condition.of('A'))
				.or(Condition.of('B'))
				.or('C', 1, 2, 3)
				.or('D', new Sql<>(Address))
				.or(new Sql<>(Address), 'E') // since 3.1.0
				.having instanceof Or

		when: new Sql<>(Contact).where('A').and((Condition)null)
		then: def e = thrown NullPointerException
			DebugTrace.print("e", e)

		when: new Sql<>(Contact).where('A').or((Condition)null)
		then: e = thrown NullPointerException
			DebugTrace.print("e", e)

		DebugTrace.leave() // for Debugging
	}

	// Sql.groupBy(String, Object...)
	// Sql.getGroupBy()
	def "SqlSpec groupBy setGroupBy getGroupBy"() {
		DebugTrace.enter() // for Debugging

		expect:
			new Sql<>(Contact).groupBy == new GroupBy()
			new Sql<>(Contact).groupBy('A').groupBy != new GroupBy()

		when:
			def sql = new Sql<>(Contact)
			sql.groupBy.add('A')

		then:
			sql.groupBy == new GroupBy().add('A')

		expect:
			sql.setGroupBy(new GroupBy().add('A')).groupBy == new GroupBy().add('A')
			sql.setGroupBy(new GroupBy().add('A').add('B')).groupBy == new GroupBy().add('A').add('B')

		DebugTrace.leave() // for Debugging
	}

	// Sql.having(Condition)
	// Sql.having(String, Object...)
	// Sql.having(String, Sql<SE>)
	// Sql.having(Sql<SE>, String)
	// Sql.getHaving()
	def 'SqlSpec having, getHaving'() {
		DebugTrace.enter() // for Debugging
		expect:
			new Sql<>(Contact).having == Condition.EMPTY
			new Sql<>(Contact).having(Condition.ALL).having ==  Condition.ALL
			new Sql<>(Contact).having("{id} = 1").having  instanceof Expression
			new Sql<>(Contact).having('', new Sql<>(Address)).having instanceof SubqueryCondition
			new Sql<>(Contact).having(new Sql<>(Address), '').having instanceof SubqueryCondition // 3.1.0

		when:
			new Sql<>(Contact).having((Condition)null)

		then:
			def e = thrown NullPointerException
			DebugTrace.print("e", e)

		DebugTrace.leave() // for Debugging
	}

	// 3.1.0
	// Sql.union(Sql<UE>)
	// Sql.unionAll(Sql<UE>)
	// Sql.getUnionSqls()
	// Sql.isUnionAll()
	def 'SqlSpec union, unionAll, getUnionSqls, isUnionAll'() {
		DebugTrace.enter() // for Debugging
		expect:
			new Sql<>(Contact).unionSqls.empty

			new Sql<>(Contact)
				.union(new Sql<>(Contact))
				.unionSqls.size() == 1

			new Sql<>(Contact)
				.unionAll(new Sql<>(Contact))
				.unionSqls.size() == 1

			new Sql<>(Contact)
				.union(new Sql<>(Contact))
				.union(new Sql<>(Contact))
				.unionSqls.size() == 2

			new Sql<>(Contact)
				.unionAll(new Sql<>(Contact))
				.unionAll(new Sql<>(Contact))
				.unionSqls.size() == 2

			new Sql<>(Contact).unionAll == false

			new Sql<>(Contact)
				.union(new Sql<>(Contact))
				.unionAll == false

			new Sql<>(Contact)
				.unionAll(new Sql<>(Contact))
				.unionAll == true
		DebugTrace.leave() // for Debugging
	}

	// 3.1.0
	// Sql.union(Sql<UE>)
	// Sql.unionAll(Sql<UE>)
	// Exception
	def 'SqlSpec union, unionAll - exception'() {
		DebugTrace.enter() // for Debugging
		when:
			new Sql<>(Contact)
				.union(new Sql<>(Contact))
				.unionAll(new Sql<>(Contact))

		then:
			def e = thrown IllegalStateException
			DebugTrace.print("e", e)

		when:
			new Sql<>(Contact)
				.unionAll(new Sql<>(Contact))
				.union(new Sql<>(Contact))

		then:
			e = thrown IllegalStateException
			DebugTrace.print("e", e) 

		DebugTrace.leave() // for Debugging
	}

	// Sql.orderBy(String, Object...)
	// Sql.getOrderBy()
	def "SqlSpec orderBy setOrderBy getOrderBy"() {
		DebugTrace.enter() // for Debugging

		expect:
			new Sql<>(Contact).orderBy == new OrderBy()
			new Sql<>(Contact).orderBy('A').asc().desc().orderBy != new OrderBy()

		when:
			def sql = new Sql<>(Contact)
			sql.orderBy.add('A')

		then:
			sql.orderBy == new OrderBy().add('A')

		expect:
			sql.setOrderBy(new OrderBy().add('A')).orderBy == new OrderBy().add('A')
			sql.setOrderBy(new OrderBy().add('A').asc()).orderBy == new OrderBy().add('A')
			sql.setOrderBy(new OrderBy().add('A').desc()).orderBy == new OrderBy().add('A').desc()
			sql.setOrderBy(new OrderBy().add('A')).desc().orderBy == new OrderBy().add('A').desc()

		DebugTrace.leave() // for Debugging
	}

	// Sql.limit(int)
	// Sql.getLimit()
	// Sql.offset(int)
	// Sql.getOffset()
	def "SqlSpec limit getLimit offset getOffset"() {
		DebugTrace.enter() // for Debugging

		expect:
			new Sql<>(Contact).limit == Integer.MAX_VALUE
			new Sql<>(Contact).limit(100).limit == 100
			new Sql<>(Contact).offset == 0
			new Sql<>(Contact).offset(100).offset == 100

		DebugTrace.leave() // for Debugging
	}

	// Sql.forUpdate()
	// Sql.isForUpdate()
	// Sql.noWait()
	// Sql.wait(waitTime)
	// Sql.getWaitTime()
	// Sql.isNoWait()
	// Sql.isWaitForever()
	def "SqlSpec forUpdate isForUpdate noWait wait getWaitTime isNoWait isWaitForever"() {
		DebugTrace.enter() // for Debugging

		expect:
			new Sql<>(Contact).forUpdate == false
			new Sql<>(Contact).forUpdate().forUpdate

			new Sql<>(Contact).noWait == false
			new Sql<>(Contact).waitForever
			new Sql<>(Contact).waitTime == Sql.FOREVER

			new Sql<>(Contact).noWait().noWait
			new Sql<>(Contact).noWait().waitForever == false
			new Sql<>(Contact).noWait().waitTime == 0

			new Sql<>(Contact).wait(0).noWait
			new Sql<>(Contact).wait(0).waitForever == false
			new Sql<>(Contact).wait(0).waitTime == 0

			new Sql<>(Contact).wait(10).noWait == false
			new Sql<>(Contact).wait(10).waitForever == false
			new Sql<>(Contact).wait(10).waitTime == 10

			new Sql<>(Contact).wait(Sql.FOREVER).noWait == false
			new Sql<>(Contact).wait(Sql.FOREVER).waitForever
			new Sql<>(Contact).wait(Sql.FOREVER).waitTime == Sql.FOREVER

		DebugTrace.leave() // for Debugging
	}

	// Sql.addSqlEntityInfo getSqlEntityInfo
	def "SqlSpec addSqlEntityInfo getSqlEntityInfo"() {
		DebugTrace.enter() // for Debugging

		expect:
			new Sql<>(Contact, 'C')
				.innerJoin(Address, 'A', '{A.addressId} = {P.addressId}')
				.getSqlEntityInfo('A').tableAlias() == 'A'

			new Sql<>(Contact, 'C')
				.innerJoin(Address, 'A', '{A.addressId} = {P.addressId}')
				.getSqlEntityInfo('B') == null

			new Sql<>(Contact, 'C')
				.innerJoin(Address, 'A', '{A.addressId} = {A.addressId}')
				.getSqlEntityInfo('C').getClass() == Sql

		when:
			def parameters = []
			def sql = Standard.instance.selectSql(
				new Sql<>(Address, 'A')
					.innerJoin(Contact, 'C', '{C.addressId} = {A.id}')
					.where("EXISTS", new Sql(Phone, 'P').where('{P.contactId}={C.id}')),
				[])

		then:
			sql.indexOf('P.contactId=C.id') >= 0

		DebugTrace.leave() // for Debugging
	}

	// Sql.updateSql with JOIN
	def "SqlSpec updateSql with JOIN - #databaseName"(Database database, String databaseName) {
		DebugTrace.enter() // for Debugging

		when:
			Contact contact = new Contact()
			contact.id = 1
			contact.name.last = 'Apple'
			contact.name.first = 'Chiyuki'

			String updateSql = database.updateSql(
					new Sql<>(Contact, 'C')
						.innerJoin(Phone, 'P', '{P.contactId} = {C.id}')
						.where(contact)
						.setEntity(contact)
						.columns('name.first', 'updateCount')
				, [])
			DebugTrace.print(database.getClass().simpleName + ': ', updateSql) // for Debugging

		then:
			if (database instanceof SQLServer)
				assert updateSql == "UPDATE Contact SET updateCount=updateCount+1, firstName='Chiyuki' FROM Contact C INNER JOIN Phone P ON P.contactId = C.id WHERE C.id=1"
			else
				assert updateSql == "UPDATE Contact C INNER JOIN Phone P ON P.contactId = C.id SET C.updateCount=C.updateCount+1, C.firstName='Chiyuki' WHERE C.id=1"

		DebugTrace.leave() // for Debugging
		where:
			database << databases
			databaseName = database.getClass().simpleName
	}

	// Sql.deleteSql with JOIN
	def "SqlSpec deleteSql with JOIN - #databaseName"(Database database, String databaseName) {
		DebugTrace.enter() // for Debugging

		setup:
			String deleteSql = null

		when:
			deleteSql = database.deleteSql(
				new Sql<>(Contact)
					.where(Condition.ALL)
				, [])
			DebugTrace.print(database.getClass().simpleName + ": ", deleteSql) // for Debugging

		then:
			deleteSql == "DELETE FROM Contact"

		when:
			deleteSql = database.deleteSql(
				new Sql<>(Contact)
					.innerJoin(Phone, "P", "{P.contactId} = {id}")
					.where("{P.phoneNumber} LIKE {}", "080%")
				, [])
			DebugTrace.print(database.getClass().simpleName + ": ", deleteSql) // for Debugging

		then:
			deleteSql == "DELETE Contact FROM Contact INNER JOIN Phone P ON P.contactId = id WHERE P.phoneNumber LIKE '080%'"

		when:
			deleteSql = database.deleteSql(
				new Sql<>(Contact, "C")
					.innerJoin(Phone, "P", "{P.contactId} = {C.id}")
					.where("{P.phoneNumber} LIKE {}", "080%")
				, [])
			DebugTrace.print(database.getClass().simpleName + ": ", deleteSql) // for Debugging

		then:
			deleteSql == "DELETE C FROM Contact C INNER JOIN Phone P ON P.contactId = C.id WHERE P.phoneNumber LIKE '080%'"

		DebugTrace.leave() // for Debugging
		where:
			database << databases
			databaseName = database.getClass().simpleName
	}
}
