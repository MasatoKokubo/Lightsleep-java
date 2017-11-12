// InsertUpdateDeleteSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec


import java.sql.Date

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.connection.*
import org.lightsleep.database.*
import org.lightsleep.test.entity.*
import org.lightsleep.test.exception.DeletedException
import org.lightsleep.test.exception.UpdateException
import spock.lang.*

// InsertUpdateDeleteSpec
@Unroll
class InsertUpdateDeleteSpec extends SpecCommon {
	def setup() {
		deleteAllTables()
	}

	/**
	 * Test methos.
	 *   Sql.insert(Connection it, E entity)
	 *   Sql.update(Connection it, E entity)
	 *   Sql.delete(Connection it, E entity)
	 * Normal case
	*/
	def "InsertUpdateDeleteSpec insert update delete - 1 row - #connectionSupplier"(
		ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('connectionSupplier.class', connectionSupplier.getClass().name)
	/**/DebugTrace.print('1 connectionSupplier', connectionSupplier.toString())
		setup:
			ContactComposite contact2 = null

		when:
			// Make test data.
			def contacts = makeTestData(null, 1, 1)
			def contact = contacts.get(0)

			// Insert a row and gets a row.
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite).connection(it).insert(contact)
				contact2 = new Sql<>(ContactComposite).connection(it).where(contact).select().orElse(null)
			}

		then:
			// Confirm inserted result
			assertTestData(contact2, contact, 0, 0)

		when:
			// Update test data.
			makeTestData(contacts, 2, -1)

			// Update a row and gets a row.
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite).connection(it).update(contact)
				contact2 = new Sql<>(ContactComposite).connection(it).where(contact).select().orElse(null)
			}

		then:
			// Confirm update result.
			assertTestData(contact2, contact, 1, 1)

		when:
			// Deletes a row and tries to get a row.
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite).connection(it).delete(contact)
				contact2 = new Sql<>(ContactComposite).connection(it).where(contact).select().orElse(null)
			}

		then:
			// Confirm delete result.
			assert contact2 == null

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	/**
	 * Test methos.
	 *   Sql.insert(Connection it, Collection<? extends E> entities)
	 *   Sql.update(Connection it, Collection<? extends E> entities)
	 *   Sql.delete(Connection it, Collection<? extends E> entities)
	 * Normal case
	 */
	def "InsertUpdateDeleteSpec insert update delete - multiple rows - #connectionSupplier"(
		ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			List<ContactComposite> contacts2 = new ArrayList<>()

		when:
			// Make test data.
			List<ContactComposite> contacts = makeTestData(null, 1, 4)

			// Insert rows and gets rows.
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite).connection(it).insert(contacts)

				new Sql<>(ContactComposite).connection(it)
					.where('{name.last} LIKE {}', 'Last%')
					.orderBy('{id}')
					.select({contacts2 << it})
			}

		then:
			// Confirm inserted result
			assertTestData(contacts2, contacts, 0, 0)

		when:
			// Update test data.
			makeTestData(contacts, 5, -1)

			// Update rows and gets rows.
			contacts2.clear()
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite).connection(it).update(contacts)

				new Sql<>(ContactComposite).connection(it)
					.where('{name.last} LIKE {}', 'Last%')
					.orderBy('{id}')
					.select({contacts2 << it})
			}

		then:
			// Confirm update result.
			assertTestData(contacts2, contacts, 1, 1)

		when:
			// Update rows at a time and gets rows.
			contacts.get(0).name.first = 'FirstXXX'
			contacts2.clear()
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite).connection(it)
					.columns('name.first', 'updateCount', 'updated')
					.where(Condition.ALL)
					.update(contacts.get(0))

				new Sql<>(ContactComposite).connection(it)
					.where('{name.last} LIKE {}', 'Last%')
					.orderBy('{id}')
					.select({contacts2 << it})
			}

		then:
			// Confirm update result.
			contacts.forEach {it.name.first = 'FirstXXX'}
			assertTestData(contacts2, contacts, 2, 1)

		when:
			// Deletes rows.
			contacts2.clear()
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite).connection(it).delete(contacts)
				new Sql<>(ContactComposite).connection(it)
					.where('{name.last} LIKE {}', 'Last%')
					.orderBy('{id}')
					.select({contacts2 << it})
			}

		then:
			// Confirm delete result.
			assert contacts2.size() == 0

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	/**
	 * Test methos.
	 *   Sql.update(Connection it)
	 *   Sql.delete(Connection it)
	 * Normal case
	 */
	def "InsertUpdateDeleteSpec update delete - with condition - #connectionSupplier"(
		ConnectionSupplier connectionSupplier) {
		if (connectionSupplier.database instanceof DB2) return
		if (connectionSupplier.database instanceof Oracle) return
		if (connectionSupplier.database instanceof PostgreSQL) return
		if (connectionSupplier.database instanceof SQLite) return
	/**/DebugTrace.enter()
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			List<ContactComposite> contacts2 = new ArrayList<>()

		when:
			// Make test data.
			List<ContactComposite> contacts = makeTestData(null, 1, 10)

			// Insert rows and gets rows.
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite).connection(it).insert(contacts)

				new Sql<>(ContactComposite).connection(it)
					.where('{name.last} LIKE {}', 'Last%')
					.orderBy('{id}')
					.select({contacts2 << it})
			}

		then:
			// Confirm inserted result
			assertTestData(contacts2, contacts, 0, 0)

		when:
			// Update rows and gets test data.
			List<ContactComposite> contacts3 = new ArrayList<>()
			Transaction.execute(connectionSupplier) {
				Contact contact = new Contact()
				contact.name.first = '_First_'
				new Sql<>(Contact, 'C').connection(it)
					.innerJoin(Phone, 'P', '{P.contactId} = {C.id}')
					.where('{P.phoneNumber} = {}', '09000000010')
					.columns('name.first')
					.doIf(it.database instanceof MySQL) {
						// MySQL
						it.expression('name.first', "CONCAT('<',{name.first},'>')")
					}
					.doIf(it.database instanceof SQLServer) {
						// SQLServer
						it.expression('name.first', "'<'+{name.first}+'>'")
					}
					.update(contact)

				new Sql<>(ContactComposite).connection(it)
					.where('{name.first} LIKE {}', '<First%>')
					.orderBy('{id}')
					.select({contacts3 << it})
			}

		then:
			// Confirm update result.
			contacts3.size() == 1

		when:
			// Deletes rows and gets test data.
			List<ContactComposite> contacts4 = new ArrayList<>()
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact, 'C').connection(it)
					.innerJoin(Phone, 'P', '{P.contactId} = {C.id}')
					.where('{P.phoneNumber} = {}', '09000000020')
					.delete()

				new Sql<>(ContactComposite).connection(it)
					.orderBy('{id}')
					.select({contacts4 << it})
			}

		then:
			// Confirm delete result.
			contacts4.size() == contacts.size() - 1

		when:
			// Deletes rows and gets test data.
			List<ContactComposite> contacts5 = new ArrayList<>()
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact).connection(it)
					.innerJoin(Phone, 'P', '{P.contactId} = Contact.{id}')
					.where('{P.phoneNumber} = {}', '09000000030')
					.delete()

				new Sql<>(ContactComposite).connection(it)
					.orderBy('{id}')
					.select({contacts5 << it})
			}

		then:
			// Confirm delete result.
			assert contacts5.size() == contacts4.size() - 1

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	/**
	 * Test methos.
	 *   Sql.insert(Connection it, E entity)
	 *   Sql.insert(Connection it, Stream<? extends E> entityStream)
	 *   Sql.insert(Connection it, Collection<? extends E> entities)
	 * Error case / Illegal argument
	 *
	 * Test methos.
	 *   Sql.update(Connection it, E entity)
	 *   Sql.update(Connection it, Stream<? extends E> entityStream)
	 *   Sql.update(Connection it, Collection<? extends E> entities)
	 * Error case / Illegal argument
	 *
	 * Test methos.
	 *   Sql.delete(Connection it, E entity)
	 *   Sql.delete(Connection it, Stream<? extends E> entityStream)
	 *   Sql.delete(Connection it, Collection<? extends E> entities)
	 *  Error case / No condition
	 *  Error case / Illegal argument
	 */
	def "InsertUpdateDeleteSpec insert update delete - exception"() {
	/**/DebugTrace.enter()
		// insert
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite).connection(it).insert((ContactComposite)null)
			}
		then: thrown NullPointerException

		// insert
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite).connection(it).insert((Iterable<ContactComposite>)null)
			}
		then: thrown NullPointerException

		// update
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite).connection(it).update((ContactComposite)null)
			}
		then: thrown NullPointerException

		// update
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite).connection(it).update((Iterable<ContactComposite>)null)
			}
		then: thrown NullPointerException

		// delete / No condition
		when:
			def count = -1
			Transaction.execute(connectionSupplier) {
				count = new Sql<>(ContactComposite).connection(it).delete()
			}
		then: count == 0

		// delete
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite).connection(it).delete((ContactComposite)null)
			}
		then: thrown NullPointerException

		// delete
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite).connection(it).delete((Iterable<ContactComposite>)null)
			}
		then: thrown NullPointerException

	/**/DebugTrace.leave()
	}

	def "InsertUpdateDeleteSpec optimistic lock #connectionSupplier" (
		ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			// Insert
			Calendar calendar = Calendar.getInstance()
			def contact1 = new Contact()
			contact1.name.last = "Apple"
			contact1.name.first = "Akane"
			calendar.set(2001, 1-1, 1, 0, 0, 0)
			contact1.birthday = new Date(calendar.timeInMillis)

			Transaction.execute(connectionSupplier) {
				def count = new Sql<>(Contact).connection(it).insert(contact1)
				assert count == 1
			}

			// Update
			Contact contact2 = null
			Transaction.execute(connectionSupplier) {
				contact2 = new Sql<>(Contact).connection(it).where(contact1).select().orElse(null)
				assert contact2 != null

				calendar.set(2001, 1-1, 2, 0, 0, 0)
				contact2.birthday = new Date(calendar.timeInMillis)
				def count = new Sql<>(Contact).connection(it).update(contact2)
				assert count == 1
			}

		when:
			Contact contact1t = null
			Transaction.execute(connectionSupplier) {
				contact1t = new Sql<>(Contact).connection(it)
					.where(contact1)
					.doIf(!(it.database instanceof SQLite)) {Sql sql -> sql.forUpdate()}
					.select().orElse(null)
				if (contact1t == null)
					throw new DeletedException()
				if (contact1.updateCount != contact1t.updateCount)
					throw new UpdateException()
			}

		then:
			// optimistic lock error
			thrown UpdateException

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	/**
	 * Creates data to insert in the tests.
	 */
	static List<ContactComposite> makeTestData(List<ContactComposite> contacts, int offset, int count) {
		def init = false
		if (contacts == null) {
			init = true
			contacts = []
		} else
			count = contacts.size()

		(0..<count).each {index ->
			if (init)
				contacts.add(new ContactComposite())
			def contact = contacts.get(index)

			contact.name.last  = 'Last'  + (index + offset)
			contact.name.first = 'First' + (index + offset)

			def calendar = Calendar.getInstance()
			calendar.setTimeInMillis(0L)
			calendar.set(2015, 12-1, (index + offset) + index, 0, 0, 0)
			contact.birthday = new Date(calendar.getTimeInMillis())

			contact.address.postCode = '12345'   + (index + offset)
			contact.address.address1 = 'Address' + index + '_' + (offset + 0)
			contact.address.address2 = 'Address' + index + '_' + (offset + 1)
			contact.address.address3 = 'Address' + index + '_' + (offset + 2)

			int count2 = init ? 2 : contact.phones.size()
			(0..<count2).each {index2 ->
				if (init)
					contact.phones.add(new Phone())
				def phone = contact.phones.get(index2)

				phone.phoneNumber = '0' + (90_0000_0000L + (index + offset) * 10 + index2)
			}
		}

		return contacts
	}

	/**
	 * Validates a test result.
	 */
	static void assertTestData(ContactComposite after, ContactComposite before, int updateCount1, int updateCount2) {
		assert after.id                  == before.id
		assert after.name.last           == before.name.last
		assert after.name.first          == before.name.first
		assert after.birthday            == before.birthday
		assert after.updateCount         == updateCount1
		assert after.created             != null
		assert after.updated             != null
		assert after.address.postCode    == before.address.postCode
		assert after.address.address1    == before.address.address1
		assert after.address.address2    == before.address.address2
		assert after.address.address3    == before.address.address3
		assert after.address.updateCount == updateCount2
		assert after.address.created     != null
		assert after.address.updated     != null

		assert after.phones.size() == before.phones.size()
		(0..<after.phones.size()).each {
			assert after.phones[it].phoneNumber == before.phones[it].phoneNumber
			assert after.phones[it].updateCount == updateCount2
			assert after.phones[it].created     != null
			assert after.phones[it].updated     != null
		}
	}

	/**
	 * Validates test results.
	 */
	static void assertTestData(List<ContactComposite> afters, List<ContactComposite> befores, int updateCount1, int updateCount2) {
		assert afters.size() == befores.size()

		(0..<afters.size()).each {assertTestData(afters[it], befores[it], updateCount1, updateCount2)}
	}
}
