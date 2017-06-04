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
class InsertUpdateDeleteSpec extends Specification {
	static connectionSupplierClasses = [
		C3p0.class,
		Dbcp.class,
		HikariCP.class,
		TomcatCP.class,
		Jdbc.class
	]

	@Shared connectionSupplier

	/**
	 * Deletes test data.
	 */
	def setupSpec() {
	/**/DebugTrace.enter()
		connectionSupplier = ConnectionSpec.getConnectionSupplier(Jdbc.class)

		Transaction.execute(connectionSupplier) {
			new Sql<>(Contact.class).where(Condition.ALL).delete(it)
			new Sql<>(Address.class).where(Condition.ALL).delete(it)
			new Sql<>(Phone  .class).where(Condition.ALL).delete(it)
		}
	/**/DebugTrace.leave()
	}

	/**
	 * Test methos.
	 *   Sql.insert(Connection it, E entity)
	 *   Sql.update(Connection it, E entity)
	 *   Sql.delete(Connection it, E entity)
	 * Normal case
	*/
	def "insert update delete - 1 row - #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
		def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)

		setup:
			// Deletes rows of previous test.
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact.class).where(Condition.ALL).delete(it)
				new Sql<>(Phone  .class).where(Condition.ALL).delete(it)
				new Sql<>(Address.class).where(Condition.ALL).delete(it)
			}

			ContactComposite contact2 = null

		when:
			// Makes test data.
		/**/DebugTrace.print('Makes test data.')
			def contacts = makeTestData(null, 1, 1)
			def contact = contacts.get(0)

			// Inserts a row and gets a row.
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite.class).insert(it, contact)
				contact2 = new Sql<>(ContactComposite.class).where(contact).select(it).orElse(null)
			}

		then:
			// Confirms insert result
			assertTestData(contact2, contact, 0, 0)

		when:
			// Updates test data.
			makeTestData(contacts, 2, -1)

			// Updates a row and gets a row.
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite.class).update(it, contact)
				contact2 = new Sql<>(ContactComposite.class).where(contact).select(it).orElse(null)
			}

		then:
			// Confirms update result.
			assertTestData(contact2, contact, 1, 1)

		when:
			// Deletes a row and tries to get a row.
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite.class).delete(it, contact)
				contact2 = new Sql<>(ContactComposite.class).where(contact).select(it).orElse(null)
			}

		then:
			// Confirms delete result.
			assert contact2 == null

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	/**
	 * Test methos.
	 *   Sql.insert(Connection it, Collection<? extends E> entities)
	 *   Sql.update(Connection it, Collection<? extends E> entities)
	 *   Sql.delete(Connection it, Collection<? extends E> entities)
	 * Normal case
	 */
	def "insert update delete - multiple rows - #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)

		setup:
			ConnectionSupplier connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)

			// Deletes rows of previous test.
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact.class).where(Condition.ALL).delete(it)
				new Sql<>(Phone  .class).where(Condition.ALL).delete(it)
				new Sql<>(Address.class).where(Condition.ALL).delete(it)
			}

			List<ContactComposite> contacts2 = new ArrayList<>()

		when:
			// Makes test data.
		/**/DebugTrace.print('Makes test data.')
			List<ContactComposite> contacts = makeTestData(null, 1, 4)

			// Inserts rows and gets rows.
			Transaction.execute(connectionSupplier) {
			/**/DebugTrace.print('Inserts rows.')
				new Sql<>(ContactComposite.class).insert(it, contacts)

			/**/DebugTrace.print('Gets rows.')
				new Sql<>(ContactComposite.class)
					.where('{name.family} LIKE {}', 'Family%')
					.orderBy('{id}')
					.select(it, {contacts2 << it})
			}
		/**/DebugTrace.print('0 contacts2', contacts2)

		then:
			// Confirms insert result
			assertTestData(contacts2, contacts, 0, 0)

		when:
			// Updates test data.
			makeTestData(contacts, 5, -1)

			// Updates rows and gets rows.
			contacts2.clear()
			Transaction.execute(connectionSupplier) {
			/**/DebugTrace.print('Updates rows.')
				new Sql<>(ContactComposite.class).update(it, contacts)

			/**/DebugTrace.print('Gets rows.')
				new Sql<>(ContactComposite.class)
					.where('{name.family} LIKE {}', 'Family%')
					.orderBy('{id}')
					.select(it, {contacts2 << it})
			}
		/**/DebugTrace.print('1 contacts2', contacts2)

		then:
			// Confirms update result.
			assertTestData(contacts2, contacts, 1, 1)

		when:
			// Updates rows at a time and gets rows.
			contacts.get(0).name.given = 'FirstXXX'
			contacts2.clear()
			Transaction.execute(connectionSupplier) {
			/**/DebugTrace.print('Updates rows at a time.')
				new Sql<>(ContactComposite.class)
					.columns('name.given', 'updateCount', 'updated')
					.where(Condition.ALL)
					.update(it, contacts.get(0))

			/**/DebugTrace.print('Gets rows.')
				new Sql<>(ContactComposite.class)
					.where('{name.family} LIKE {}', 'Family%')
					.orderBy('{id}')
					.select(it, {contacts2 << it})
			}
		/**/DebugTrace.print('2 contacts2', contacts2)

		then:
			// Confirms update result.
			contacts.forEach {it.name.given = 'FirstXXX'}
			assertTestData(contacts2, contacts, 2, 1)

		when:
			// Deletes rows.
			contacts2.clear()
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite.class).delete(it, contacts)
				new Sql<>(ContactComposite.class)
					.where('{name.family} LIKE {}', 'Family%')
					.orderBy('{id}')
					.select(it, {contacts2 << it})
			}
		/**/DebugTrace.print('3 contacts2', contacts2)

		then:
			// Confirms delete result.
			assert contacts2.size() == 0

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	/**
	 * Test methos.
	 *   Sql.update(Connection it)
	 *   Sql.delete(Connection it)
	 * Normal case
	 */
	def "update delete - with condition - #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
		if (Sql.getDatabase() instanceof DB2) return
		if (Sql.getDatabase() instanceof Oracle) return
		if (Sql.getDatabase() instanceof PostgreSQL) return
		if (Sql.getDatabase() instanceof SQLite) return
	/**/DebugTrace.enter()
	/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)

		setup:
			ConnectionSupplier connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)

			// Deletes rows of previous test.
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact.class).where(Condition.ALL).delete(it)
				new Sql<>(Phone  .class).where(Condition.ALL).delete(it)
				new Sql<>(Address.class).where(Condition.ALL).delete(it)
			}

			List<ContactComposite> contacts2 = new ArrayList<>()

		when:
			// Makes test data.
		/**/DebugTrace.print('Makes test data.')
			List<ContactComposite> contacts = makeTestData(null, 1, 10)

			// Inserts rows and gets rows.
			Transaction.execute(connectionSupplier) {
			/**/DebugTrace.print('Inserts rows.')
				new Sql<>(ContactComposite.class).insert(it, contacts)

			/**/DebugTrace.print('Gets rows.')
				new Sql<>(ContactComposite.class)
					.where('{name.family} LIKE {}', 'Family%')
					.orderBy('{id}')
					.select(it, {contacts2 << it})
			}
		/**/DebugTrace.print('0 contacts2', contacts2)

		then:
			// Confirms insert result
			assertTestData(contacts2, contacts, 0, 0)

		when:
			// Updates rows and gets test data.
			List<ContactComposite> contacts3 = new ArrayList<>()
			Transaction.execute(connectionSupplier) {
			/**/DebugTrace.print('Updates rows.')
				Contact contact = new Contact()
				contact.name.given = '_Given_'
				new Sql<>(Contact.class, 'C')
					.innerJoin(Phone.class, 'P', '{P.contactId} = {C.id}')
					.where('{P.phoneNumber} = {}', '09000000010')
					.columns('name.given')
					.doIf(Sql.database instanceof MySQL) {
						// MySQL
						it.expression('name.given', "CONCAT('<',{name.given},'>')")
					}
					.doIf(Sql.database instanceof SQLServer) {
						// SQLServer
						it.expression('name.given', "'<'+{name.given}+'>'")
					}
					.update(it, contact)

			/**/DebugTrace.print('Gets rows.')
				new Sql<>(ContactComposite.class)
					.where('{name.given} LIKE {}', '<Given%>')
					.orderBy('{id}')
					.select(it, {contacts3 << it})
			}
		/**/DebugTrace.print('contacts3', contacts3)

		then:
			// Confirms update result.
			contacts3.size() == 1

		when:
			// Deletes rows and gets test data.
			List<ContactComposite> contacts4 = new ArrayList<>()
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact.class, 'C')
					.innerJoin(Phone.class, 'P', '{P.contactId} = {C.id}')
					.where('{P.phoneNumber} = {}', '09000000020')
					.delete(it)

			/**/DebugTrace.print('Gets rows.')
				new Sql<>(ContactComposite.class)
					.orderBy('{id}')
					.select(it, {contacts4 << it})
			}
		/**/DebugTrace.print('contacts4', contacts4)

		then:
			// Confirms delete result.
			contacts4.size() == contacts.size() - 1

		when:
			// Deletes rows and gets test data.
			List<ContactComposite> contacts5 = new ArrayList<>()
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact.class)
					.innerJoin(Phone.class, 'P', '{P.contactId} = Contact.{id}')
					.where('{P.phoneNumber} = {}', '09000000030')
					.delete(it)

			/**/DebugTrace.print('Gets rows.')
				new Sql<>(ContactComposite.class)
					.orderBy('{id}')
					.select(it, {contacts5 << it})
			}
		/**/DebugTrace.print('contacts5', contacts5)

		then:
			// Confirms delete result.
			assert contacts5.size() == contacts4.size() - 1

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
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
	def "insert update delete - exception"() {
	/**/DebugTrace.enter()

		// insert
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite.class).insert(it, (ContactComposite)null)
			}

		then:
			thrown NullPointerException

		// insert
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite.class).insert(it, (Iterable<ContactComposite>)null)
			}

		then:
			thrown NullPointerException

		// update
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite.class).update(it, (ContactComposite)null)
			}

		then:
			thrown NullPointerException

		// update
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite.class).update(it, (Iterable<ContactComposite>)null)
			}

		then:
			thrown NullPointerException

		// delete / No condition
		when:
			def count = -1
			Transaction.execute(connectionSupplier) {
				count = new Sql<>(ContactComposite.class).delete(it)
			}

		then:
			count == 0

		// delete
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite.class).delete(it, (ContactComposite)null)
			}

		then:
			thrown NullPointerException

		// delete
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactComposite.class).delete(it, (Iterable<ContactComposite>)null)
			}

		then:
			thrown NullPointerException

	/**/DebugTrace.leave()
	}


	def "Optimistic lock #connectionSupplierName" (
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)

		setup:
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)

			// Deletes rows of previous test.
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact.class).where(Condition.ALL).delete(it)
			}

			// Insert
			Calendar calendar = Calendar.getInstance()
			def contact1 = new Contact()
			contact1.name.family = "Apple"
			contact1.name.given  = "Akane"
			calendar.set(2001, 1-1, 1, 0, 0, 0)
			contact1.birthday = new Date(calendar.timeInMillis)
		/**/DebugTrace.print('1 contact1', contact1)

			Transaction.execute(connectionSupplier) {
				def count = new Sql<>(Contact.class).insert(it, contact1)
			/**/DebugTrace.print('inserted count', count)
				assert count == 1
			}
		/**/DebugTrace.print('2 contact1', contact1)

			// Update
			Contact contact2 = null
			Transaction.execute(connectionSupplier) {
				contact2 = new Sql<>(Contact.class).where(contact1).select(it).orElse(null)
			/**/DebugTrace.print('1 contact2', contact2)
				assert contact2 != null

				calendar.set(2001, 1-1, 2, 0, 0, 0)
				contact2.birthday = new Date(calendar.timeInMillis)
			/**/DebugTrace.print('2 contact2', contact2)
				def count = new Sql<>(Contact.class).update(it, contact2)
			/**/DebugTrace.print('updated count', count)
				assert count == 1
			}

		when:
			Contact contact1t = null
			Transaction.execute(connectionSupplier) {
				contact1t = new Sql<>(Contact.class)
					.where(contact1)
					.doIf(!(Sql.database instanceof SQLite)) {Sql sql -> sql.forUpdate()}
					.select(it).orElse(null)
			/**/DebugTrace.print('contact1t', contact1t)
				if (contact1t == null)
					throw new DeletedException()
				if (contact1.updateCount != contact1t.updateCount)
					throw new UpdateException()
			}

		then:
			// Optimistic lock error
			thrown UpdateException

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
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

			contact.name.family  = 'Family'  + (index + offset)
			contact.name.given = 'Given' + (index + offset)

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
		assert after.name.family         == before.name.family
		assert after.name.given          == before.name.given
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
