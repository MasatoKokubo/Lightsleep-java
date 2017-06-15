// SqlSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec

import java.sql.Connection
import java.util.function.Supplier

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.connection.*
import org.lightsleep.database.*
import org.lightsleep.helper.*
import org.lightsleep.test.entity.*

import spock.lang.*

// SqlSpec
@Unroll
class SqlSpec extends Specification {
	static databases = [
		Standard  .instance(),
		DB2       .instance(),
		MySQL     .instance(),
		Oracle    .instance(),
		PostgreSQL.instance(),
		SQLite    .instance(),
		SQLServer .instance()
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
	}

	// Sql.getDatabase()
	// Sql.setDatabase(Database)
	def "SqlSpec getDatabase setDatabase"() {
	/**/DebugTrace.enter()

		setup:
			def beforeDatabase = Sql.database
			def database = new TestDatabase()

		when:
			Sql.database = database

		then:
			Sql.database == database

		when:
			Sql.database = null

		then:
			thrown NullPointerException

		cleanup:
			Sql.database = beforeDatabase

	/**/DebugTrace.leave()
	}

	static class TestConnectionSupplier implements ConnectionSupplier {
		@Override public Connection get() {return null}
	}

	// Sql.getConnectionSupplier()
	// Sql.setConnectionSupplier(ConnectionSupplier)
	def "SqlSpec getConnectionSupplier setConnectionSupplier"() {
	/**/DebugTrace.enter()

		setup:
			def beforeConnectionSupplier = Sql.connectionSupplier
			def connectionSupplier = new TestConnectionSupplier()

		when:
			Sql.connectionSupplier = connectionSupplier

		then:
			Sql.connectionSupplier == connectionSupplier

		when:
			Sql.connectionSupplier = null

		then:
			thrown NullPointerException

		cleanup:
			Sql.setConnectionSupplier(beforeConnectionSupplier)

	/**/DebugTrace.leave()
	}

	// Sql.getEntityInfo(Class<E>)
	def "SqlSpec getEntityInfo - exception"() {
	/**/DebugTrace.enter()

		when:
			Sql.getEntityInfo(null)

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}

	// Sql.Sql(Class<E>)
	def "SqlSpec constructor - exception"() {
	/**/DebugTrace.enter()
		when:
			new Sql<>(null)

		then:
			thrown NullPointerException

		when:
			new Sql<>(Contact.class, null)

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}

	// Sql.entityInfo()
	def "SqlSpec entityInfo"() {
	/**/DebugTrace.enter()

		when:
			def entityInfo = new Sql<>(Contact.class).entityInfo()

		then:
			entityInfo.entityClass() == Contact.class

	/**/DebugTrace.leave()
	}

	// Sql.entityClass()
	def "SqlSpec entityClass"() {
	/**/DebugTrace.enter()

		when:
			def clazz = new Sql<>(Contact.class).entityClass()

		then:
			clazz == Contact.class

	/**/DebugTrace.leave()
	}

	// Sql.entity()
	def "SqlSpec entity"() {
	/**/DebugTrace.enter()

		when:
			def contact = new Sql<>(Contact.class).entity()

		then:
			contact == null

	/**/DebugTrace.leave()
	}

	// Sql.distinct()
	// Sql.isDistinct()
	def "SqlSpec distinct isDistinct - #databaseName"(Database database, String databaseName) {
	/**/DebugTrace.enter()
		expect:
			new Sql<>(Contact.class).isDistinct() == false
			new Sql<>(Contact.class).distinct().isDistinct()

		when:
			def selectSql = database.selectSql(new Sql<>(Contact.class).distinct(), new ArrayList<Object>())
		/**/DebugTrace.print(database.getClass().getSimpleName() + ': ', selectSql)

		then:
			selectSql.startsWith('SELECT DISTINCT id,')

	/**/DebugTrace.leave()

		where:
			database << databases
			databaseName = database.getClass().simpleName
	}

	// Sql.columns(String...)
	// Sql.getColumns()
	def "SqlSpec columns getColumns 1"() {
	/**/DebugTrace.enter()
		expect:
			new Sql<>(Contact.class).columns == [] as Set

			new Sql<>(Contact.class)
				.columns('name.family', 'name.given')
				.columns == ['name.family', 'name.given'] as Set

		when:
			new Sql<>(Contact.class).columns((String[])null)

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}

	// Sql.columns(String...)
	// Sql.columns()
	def "SqlSpec columns getColumns 2 - #databaseName"(Database database, String databaseName) {
	/**/DebugTrace.enter()

		setup:
			String selectSql = null

		when:
			selectSql = database.selectSql(
				new Sql<>(Contact.class)
					.columns('birthday')
					.columns('name.family', 'name.given')
				, new ArrayList<Object>())
		/**/DebugTrace.print(database.getClass().getSimpleName() + ': ', selectSql)

		then:
			selectSql.startsWith('SELECT familyName, givenName, birthday FROM Contact ')

		when:
			selectSql = database.selectSql(
				new Sql<>(Contact.class)
					.columns('*')
				, new ArrayList<Object>())
		/**/DebugTrace.print(database.getClass().getSimpleName() + ': ', selectSql)

		then:
			selectSql.startsWith('SELECT id, updateCount, created, updated, familyName, givenName, birthday, addressId FROM Contact ')

		when:
			selectSql = database.selectSql(
				new Sql<>(Contact.class, 'C')
					.innerJoin(Phone.class, 'P', '{P.contactId} = {C.id}')
					.columns('C.id', 'P.id')
				, new ArrayList<Object>())
		/**/DebugTrace.print(database.getClass().getSimpleName() + ': ', selectSql)

		then:
			selectSql.startsWith('SELECT C.id AS C_id, P.id AS P_id FROM Contact ')

		when:
			selectSql = database.selectSql(
				new Sql<>(Contact.class, 'C')
					.innerJoin(Phone.class, 'P', '{P.contactId} = {C.id}')
					.columns('P.*')
				, new ArrayList<Object>())
		/**/DebugTrace.print(database.getClass().getSimpleName() + ': ', selectSql)

		then:
			selectSql.indexOf(' P.id AS P_id, ') >= 0
			selectSql.indexOf(' P.contactId AS P_contactId, P.phoneNumber AS P_phoneNumber FROM Contact ') >= 0
			selectSql.indexOf('AS C_') == -1

	/**/DebugTrace.leave()

		where:
			database << databases
			databaseName = database.getClass().simpleName
	}

	// Sql.columns(String...)
	// Sql.getColumns()
	def "SqlSpec columns getColumns 3"() {
	/**/DebugTrace.enter()

		when:
			def sql = new Sql<>(Contact.class)
				.columns('birthday')
				.columns('name.family', 'name.given')
			sql.columns.clear()
		/**/DebugTrace.print('sql.columns', sql.columns)

		then:
			sql.columns == ['birthday', 'name.family', 'name.given'] as Set

		when:
			sql = new Sql<>(Contact.class)
				.setColumns(new LinkedHashSet<String>())
				.columns('birthday')
				.columns('name.family', 'name.given')
		/**/DebugTrace.print('sql.columns', sql.columns)

		then:
			sql.columns == ['birthday', 'name.family', 'name.given'] as Set

	/**/DebugTrace.leave()
	}

	// Sql.expression(String, Expression)
	// Sql.expression(String, String, Object...)
	// Sql.getExpression(String)
	def "SqlSpec expression getExpression"() {
	/**/DebugTrace.enter()
		setup:
			String selectSql = null
			String insertSql = null
			String updateSql = null

		expect:
			new Sql<>(Contact.class).getExpression('name.given') == Expression.EMPTY

			new Sql<>(Contact.class)
				.expression('name.given', new Expression('AAA'))
				.getExpression('name.given').content() == 'AAA'

			new Sql<>(Contact.class)
				.expression('name.given', 'BBB')
				.getExpression('name.given').content() == 'BBB'

		// SELECT SQL {propertyName}
		when:
			selectSql = Standard.instance().selectSql(
				new Sql<>(Contact.class).expression("name.given", "'['||{name.given}||']'"),
				new ArrayList<Object>())
		/**/DebugTrace.print('selectSql', selectSql)

		then:
			selectSql.indexOf("'['||givenName||']'") >= 0

		// SELECT SQL (with table alias) {propertyName}
		when:
			selectSql = Standard.instance().selectSql(
				new Sql<>(Contact.class, "C").expression("name.given", "'['||{name.given}||']'"),
				new ArrayList<Object>())
		/**/DebugTrace.print('selectSql', selectSql)

		then:
			selectSql.indexOf("'['||C.givenName||']'") >= 0

		// SELECT SQL (with table alias) {A.propertyName}
		when:
			selectSql = Standard.instance().selectSql(
				new Sql<>(Contact.class, "C").expression("C.name.family", "'['||{C.name.family}||']'"),
				new ArrayList<Object>())
		/**/DebugTrace.print('selectSql', selectSql)

		then:
			selectSql.indexOf("'['||C.familyName||']'") >= 0

		// INSERT SQL {#propertyName}
		when:
			Contact contact = new Contact()
			contact.name.family = "Apple"
			contact.name.given = "Yukari"
			insertSql = Standard.instance().insertSql(
				new Sql<>(Contact.class)
					.setEntity(contact)
					.expression("name.given", "'['||{#name.given}||']'"),
				new ArrayList<Object>())
		/**/DebugTrace.print('insertSql', insertSql)

		then:
			insertSql.indexOf("'['||'Yukari'||']'") >=0

		// INSERT SQL (with table alias) {#propertyName}
		when:
			insertSql = Standard.instance().insertSql(
				new Sql<>(Contact.class, "C")
					.setEntity(contact)
					.expression("name.family", "'['||{#name.family}||']'"),
				new ArrayList<Object>())
		/**/DebugTrace.print('insertSql', insertSql)

		then:
			insertSql.indexOf("'['||'Apple'||']'") >=0

		// UPDATE SQL {#propertyName}
		when:
			contact.name.family = "Orange"
			contact.name.given = "Harumi"
			updateSql = Standard.instance().updateSql(
				new Sql<>(Contact.class)
					.setEntity(contact)
					.expression("name.given", "'['||{#name.given}||']'"),
				new ArrayList<Object>())
		/**/DebugTrace.print('updateSql', updateSql)

		then:
			updateSql.indexOf("'['||'Harumi'||']'") >=0

		// UPDATE SQL (with table alias) {#propertyName}
		when:
			updateSql = Standard.instance().updateSql(
				new Sql<>(Contact.class, "C")
					.setEntity(contact)
					.expression("name.family", "'['||{#name.family}||']'"),
				new ArrayList<Object>())
		/**/DebugTrace.print('updateSql', updateSql)

		then:
			updateSql.indexOf("'['||'Orange'||']'") >=0

		when:
			new Sql<>(Contact.class).expression(null, new Expression('AAA'))
		then:
			thrown NullPointerException

		when:
			new Sql<>(Contact.class).expression('name.given', (Expression)null)
		then:
			thrown NullPointerException

		when:
			new Sql<>(Contact.class).expression(null, 'AAA')
		then:
			thrown NullPointerException

		when:
			new Sql<>(Contact.class).expression('name.given', null)
		then:
			thrown NullPointerException

		when:
			new Sql<>(Contact.class).getExpression(null)
		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}

	// Sql.doIf(boolean, Consumer)
	// Sql.doIf(boolean, Consumer, Consumer)
	def "SqlSpec doIf"() {
	/**/DebugTrace.enter()
		expect:
			new Sql<>(Contact.class)
				.limit(4)
				.doIf(true, {it.limit(1)})
				.limit == 1

			new Sql<>(Contact.class)
				.limit(4)
				.doIf(false, {it.limit(1)})
				.limit == 4

			new Sql<>(Contact.class)
				.limit(4)
				.doIf(true, {it.limit(1)}, {it.limit(2)})
				.limit == 1

			new Sql<>(Contact.class)
				.limit(4)
				.doIf(false, {it.limit(1)}, {it.limit(2)})
				.limit == 2

		when:
			new Sql<>(Contact.class).doIf(true, null)

		then:
			thrown NullPointerException

		when:
			new Sql<>(Contact.class).doIf(true, {it.limit(1)}, null)

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}


	// Sql.innerJoin(Class<JE>, String, Condition)
	// Sql.innerJoin(Class<JE>, String, Condition, Object...)
	// Sql.leftJoin(Class<JE>, String, Condition)
	// Sql.leftJoin(Class<JE>, String, Condition, Object...)
	// Sql.rightJoin(Class<JE>, String, Condition)
	// Sql.rightJoin(Class<JE>, String, Condition, Object...)
	def "SqlSpec innerJoin leftJoin rightJoin"() {
	/**/DebugTrace.enter()
		setup:
			List<JoinInfo<?>> joinInfos = null

		when:
			joinInfos = new Sql<>(Contact.class, 'C')
				.innerJoin(Address.class, 'A', '{A.id} = {C.addressId}')
				.getJoinInfos()

		then:
			joinInfos.size() == 1

			joinInfos[0].joinType()   == JoinInfo.JoinType.INNER
			joinInfos[0].entityInfo() == Sql.getEntityInfo(Address.class)
			joinInfos[0].tableAlias() == 'A'
			joinInfos[0].on()         instanceof Expression

		when:
			joinInfos = new Sql<>(Contact.class, 'C')
				.innerJoin(Address.class, 'A', '{A.id} = {C.addressId}')
				.leftJoin (Phone  .class, 'P', '{P.contactId} = {C.id}')
				.getJoinInfos()

		then:
			joinInfos.size() == 2

			joinInfos[0].joinType()   == JoinInfo.JoinType.INNER
			joinInfos[0].entityInfo() == Sql.getEntityInfo(Address.class)
			joinInfos[0].tableAlias() == 'A'
			joinInfos[0].on()         instanceof Expression

			joinInfos[1].joinType()   == JoinInfo.JoinType.LEFT
			joinInfos[1].entityInfo() == Sql.getEntityInfo(Phone.class)
			joinInfos[1].tableAlias() == 'P'
			joinInfos[1].on()         instanceof Expression

		when:
			joinInfos = new Sql<>(Contact.class, 'C')
				.innerJoin(Address.class, 'A1', '{A1.id} = {P.addressId}')
				.leftJoin (Phone  .class, 'P', '{P.contactId} = {C.id}')
				.rightJoin(Address.class, 'A2', '{A2.id} = {P.addressId}')
				.getJoinInfos()

		then:
			joinInfos.size() == 3

			joinInfos[0].joinType()   == JoinInfo.JoinType.INNER
			joinInfos[0].entityInfo() == Sql.getEntityInfo(Address.class)
			joinInfos[0].tableAlias() == 'A1'
			joinInfos[0].on()         instanceof Expression

			joinInfos[1].joinType()   == JoinInfo.JoinType.LEFT
			joinInfos[1].entityInfo() == Sql.getEntityInfo(Phone.class)
			joinInfos[1].tableAlias() == 'P'
			joinInfos[1].on()         instanceof Expression

			joinInfos[2].joinType()   == JoinInfo.JoinType.RIGHT
			joinInfos[2].entityInfo() == Sql.getEntityInfo(Address.class)
			joinInfos[2].tableAlias() == 'A2'
			joinInfos[2].on()         instanceof Expression

		when:
			joinInfos = new Sql<>(Contact.class, 'C')
				.innerJoin(Address.class, 'A1', Condition.of('{A1.id} = {P.addressId}'))
				.leftJoin (Phone  .class, 'P1', Condition.of('{P1.contactId} = {C.id}'))
				.innerJoin(Address.class, 'A2', Condition.of('{A2.id} = {P.addressId}'))
				.rightJoin(Phone  .class, 'P2', Condition.of('{P2.contactId} = {C.id}'))
				.getJoinInfos()

		then:
			joinInfos.size() == 4

			joinInfos[0].joinType()   == JoinInfo.JoinType.INNER
			joinInfos[0].entityInfo() == Sql.getEntityInfo(Address.class)
			joinInfos[0].tableAlias() == 'A1'
			joinInfos[0].on()         instanceof Expression

			joinInfos[1].joinType()   == JoinInfo.JoinType.LEFT
			joinInfos[1].entityInfo() == Sql.getEntityInfo(Phone.class)
			joinInfos[1].tableAlias() == 'P1'
			joinInfos[1].on()         instanceof Expression

			joinInfos[2].joinType()   == JoinInfo.JoinType.INNER
			joinInfos[2].entityInfo() == Sql.getEntityInfo(Address.class)
			joinInfos[2].tableAlias() == 'A2'
			joinInfos[2].on()         instanceof Expression

			joinInfos[3].joinType()   == JoinInfo.JoinType.RIGHT
			joinInfos[3].entityInfo() == Sql.getEntityInfo(Phone.class)
			joinInfos[3].tableAlias() == 'P2'
			joinInfos[3].on()         instanceof Expression

	/**/DebugTrace.leave()
	}

	// Sql.where(Condition)
	// Sql.where(String, Object...)
	// Sql.where(E)
	// Sql.where(String, Sql<SE>)
	// Sql.where()
	def "SqlSpec where getWhere"() {
	/**/DebugTrace.enter()
		expect:
			new Sql<>(Contact.class).where == Condition.EMPTY
			new Sql<>(Contact.class).where(Condition.ALL).where == Condition.ALL
			new Sql<>(Contact.class).where("{id} = 1").where instanceof Expression
			new Sql<>(Contact.class).where(new Contact()).where instanceof EntityCondition
			new Sql<>(Contact.class).where('', new Sql<>(Address.class)).where instanceof SubqueryCondition

		when:
			new Sql<>(Contact.class).where((Condition)null)

		then:
			thrown NullPointerException

		when:

		then:
	/**/DebugTrace.leave()
	}

	// Sql.and(Condition)
	// Sql.and(String, Object...)
	// Sql.and(String, Sql<SE>)
	// Sql.or(Condition)
	// Sql.or(String, Object...)
	// Sql.or(String, Sql<SE>)
	def "SqlSpec and or"() {
	/**/DebugTrace.enter()
		expect:
			new Sql<>(Contact.class)
				.where('A')
				.and(Condition.of('B'))
				.and('C')
				.and('D', new Sql<>(Address.class))
				.where instanceof And

			new Sql<>(Contact.class)
				.where('A')
				.or(Condition.of('B'))
				.or('C')
				.or('D', new Sql<>(Address.class))
				.where instanceof Or

			new Sql<>(Contact.class)
				.having('A')
				.and(Condition.of('B'))
				.and('C', 1, 2, 3)
				.and('D', new Sql<>(Address.class))
				.having instanceof And

			new Sql<>(Contact.class)
				.having(Condition.of('A'))
				.or(Condition.of('B'))
				.or('C', 1, 2, 3)
				.or('D', new Sql<>(Address.class))
				.having instanceof Or

		when:
			new Sql<>(Contact.class).where('A').and((Condition)null)

		then:
			thrown NullPointerException

		when:
			new Sql<>(Contact.class).where('A').or((Condition)null)

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}

	// Sql.groupBy(String, Object...)
	// Sql.getGroupBy()
	def "SqlSpec groupBy setGroupBy getGroupBy"() {
	/**/DebugTrace.enter()

		expect:
			new Sql<>(Contact.class).groupBy == new GroupBy()
			new Sql<>(Contact.class).groupBy('A').groupBy != new GroupBy()

		when:
			def sql = new Sql<>(Contact.class)
			sql.groupBy.add('A')

		then:
			sql.groupBy == new GroupBy()

		expect:
			sql.setGroupBy(new GroupBy().add('A')).groupBy == new GroupBy().add('A')
			sql.setGroupBy(new GroupBy().add('A').add('B')).groupBy == new GroupBy().add('A').add('B')

	/**/DebugTrace.leave()
	}

	// Sql.having(Condition)
	// Sql.having(String, Object...)
	// Sql.having(String, Sql<SE>)
	// Sql.getHaving()
	def 'SqlSpec having, getHaving'() {
	/**/DebugTrace.enter()
		expect:
			new Sql<>(Contact.class).having == Condition.EMPTY
			new Sql<>(Contact.class).having(Condition.ALL).having ==  Condition.ALL
			new Sql<>(Contact.class).having("{id} = 1").having  instanceof Expression
			new Sql<>(Contact.class).having('', new Sql<>(Address.class)).having instanceof SubqueryCondition

		when:
			new Sql<>(Contact.class).having((Condition)null)

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}

	// Sql.orderBy(String, Object...)
	// Sql.getOrderBy()
	def "SqlSpec orderBy setOrderBy getOrderBy"() {
	/**/DebugTrace.enter()

		expect:
			new Sql<>(Contact.class).orderBy == new OrderBy()
			new Sql<>(Contact.class).orderBy('A').asc().desc().orderBy != new OrderBy()

		when:
			def sql = new Sql<>(Contact.class)
			sql.orderBy.add('A')

		then:
			sql.orderBy == new OrderBy()

		expect:
			sql.setOrderBy(new OrderBy().add('A')).orderBy == new OrderBy().add('A')
			sql.setOrderBy(new OrderBy().add('A').asc()).orderBy == new OrderBy().add('A')
			sql.setOrderBy(new OrderBy().add('A').desc()).orderBy == new OrderBy().add('A').desc()
			sql.setOrderBy(new OrderBy().add('A')).desc().orderBy == new OrderBy().add('A').desc()

	/**/DebugTrace.leave()
	}

	// Sql.limit(int)
	// Sql.getLimit()
	// Sql.offset(int)
	// Sql.getOffset()
	def "SqlSpec limit getLimit offset getOffset"() {
	/**/DebugTrace.enter()

		expect:
			new Sql<>(Contact.class).limit == Integer.MAX_VALUE
			new Sql<>(Contact.class).limit(100).limit == 100
			new Sql<>(Contact.class).offset == 0
			new Sql<>(Contact.class).offset(100).offset == 100

	/**/DebugTrace.leave()
	}

	// Sql.forUpdate()
	// Sql.isForUpdate()
	// Sql.noWait()
	// Sql.wait(waitTime)
	// Sql.getWaitTime()
	// Sql.isNoWait()
	// Sql.isWaitForever()
	def "SqlSpec forUpdate isForUpdate noWait wait getWaitTime isNoWait isWaitForever"() {
	/**/DebugTrace.enter()

		expect:
			new Sql<>(Contact.class).forUpdate == false
			new Sql<>(Contact.class).forUpdate().forUpdate

			new Sql<>(Contact.class).noWait == false
			new Sql<>(Contact.class).waitForever
			new Sql<>(Contact.class).waitTime == Sql.FOREVER

			new Sql<>(Contact.class).noWait().noWait
			new Sql<>(Contact.class).noWait().waitForever == false
			new Sql<>(Contact.class).noWait().waitTime == 0

			new Sql<>(Contact.class).wait(0).noWait
			new Sql<>(Contact.class).wait(0).waitForever == false
			new Sql<>(Contact.class).wait(0).waitTime == 0

			new Sql<>(Contact.class).wait(10).noWait == false
			new Sql<>(Contact.class).wait(10).waitForever == false
			new Sql<>(Contact.class).wait(10).waitTime == 10

			new Sql<>(Contact.class).wait(Sql.FOREVER).noWait == false
			new Sql<>(Contact.class).wait(Sql.FOREVER).waitForever
			new Sql<>(Contact.class).wait(Sql.FOREVER).waitTime == Sql.FOREVER

	/**/DebugTrace.leave()
	}

	// Sql.getSqlEntityInfo(String)
	def "SqlSpec getSqlEntityInfo"() {
	/**/DebugTrace.enter()

		expect:
			new Sql<>(Contact.class, 'C')
				.innerJoin(Address.class, 'A', '{A.addressId} = {P.addressId}')
				.getSqlEntityInfo('A').tableAlias() == 'A'

			new Sql<>(Contact.class, 'C')
				.innerJoin(Address.class, 'A', '{A.addressId} = {P.addressId}')
				.getSqlEntityInfo('B') == null

			new Sql<>(Contact.class, 'C')
				.innerJoin(Address.class, 'A', '{A.addressId} = {A.addressId}')
				.getSqlEntityInfo('C').getClass() == Sql.class

	/**/DebugTrace.leave()
	}

	// Sql.updateSql with JOIN
	def "SqlSpec updateSql with JOIN - #databaseName"(Database database, String databaseName) {
	/**/DebugTrace.enter()

		when:
			Contact contact = new Contact()
			contact.id = 1
			contact.name.family = 'Apple'
			contact.name.given = 'Chiyuki'

			String updateSql = database.updateSql(
					new Sql<>(Contact.class, 'C')
						.innerJoin(Phone.class, 'P', '{P.contactId} = {C.id}')
						.where(contact)
						.setEntity(contact)
						.columns('name.given')
						.columns('updateCount')
				, new ArrayList<Object>())
		/**/DebugTrace.print(database.getClass().simpleName + ': ', updateSql)

		then:
			if (database instanceof SQLServer)
				assert updateSql == "UPDATE Contact SET updateCount=updateCount+1, givenName='Chiyuki' FROM Contact C INNER JOIN Phone P ON P.contactId = C.id WHERE C.id=1"
			else
				assert updateSql == "UPDATE Contact C INNER JOIN Phone P ON P.contactId = C.id SET C.updateCount=C.updateCount+1, C.givenName='Chiyuki' WHERE C.id=1"

	/**/DebugTrace.leave()
		where:
			database << databases
			databaseName = database.getClass().simpleName
	}

	// Sql.deleteSql with JOIN
	def "SqlSpec deleteSql with JOIN - #databaseName"(Database database, String databaseName) {
	/**/DebugTrace.enter()

		setup:
			String deleteSql = null

		when:
			deleteSql = database.deleteSql(
				new Sql<>(Contact.class)
					.where(Condition.ALL)
				, new ArrayList<Object>())
		/**/DebugTrace.print(database.getClass().getSimpleName() + ": ", deleteSql)

		then:
			deleteSql == "DELETE FROM Contact"

		when:
			deleteSql = database.deleteSql(
				new Sql<>(Contact.class)
					.innerJoin(Phone.class, "P", "{P.contactId} = {id}")
					.where("{P.phoneNumber} LIKE {}", "080%")
				, new ArrayList<Object>())
		/**/DebugTrace.print(database.getClass().getSimpleName() + ": ", deleteSql)

		then:
			deleteSql == "DELETE Contact FROM Contact INNER JOIN Phone P ON P.contactId = id WHERE P.phoneNumber LIKE '080%'"

		when:
			deleteSql = database.deleteSql(
				new Sql<>(Contact.class, "C")
					.innerJoin(Phone.class, "P", "{P.contactId} = {C.id}")
					.where("{P.phoneNumber} LIKE {}", "080%")
				, new ArrayList<Object>())
		/**/DebugTrace.print(database.getClass().getSimpleName() + ": ", deleteSql)

		then:
			deleteSql == "DELETE C FROM Contact C INNER JOIN Phone P ON P.contactId = C.id WHERE P.phoneNumber LIKE '080%'"

	/**/DebugTrace.leave()
		where:
			database << databases
			databaseName = database.getClass().simpleName
	}
}
