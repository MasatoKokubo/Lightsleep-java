// SelectSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec

import java.sql.Date

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.connection.*
import org.lightsleep.database.*
import org.lightsleep.entity.*
import org.lightsleep.test.entity.*
import org.lightsleep.test.entity.Product.Size

import spock.lang.*

// SelectSpec
@Unroll
class SelectSpec extends Specification {
	static connectionSupplierClasses = [
		C3p0.class,
		Dbcp.class,
		HikariCP.class,
		TomcatCP.class,
		Jdbc.class
	]

	@Shared connectionSupplier

	/**
	 * Inserts data for tests.
	 */
	def setupSpec() {
	/**/DebugTrace.enter()
		connectionSupplier = ConnectionSpec.getConnectionSupplier(Jdbc.class)

		Transaction.execute(connectionSupplier) {
			new Sql<>(Contact .class).where(Condition.ALL).delete(it)
			new Sql<>(Address .class).where(Condition.ALL).delete(it)
			new Sql<>(Phone   .class).where(Condition.ALL).delete(it)
			new Sql<>(Product .class).where(Condition.ALL).delete(it)
			new Sql<>(Sale    .class).where(Condition.ALL).delete(it)
			new Sql<>(SaleItem.class).where(Condition.ALL).delete(it)
		}

		insertContacts()
		insertProducts()
		insertSales   ()
	/**/DebugTrace.leave()
	}

	/**
	 * Inserts to Contact table.
	 */
	def insertContacts() {
	/**/DebugTrace.enter()
		Calendar calendar = Calendar.getInstance()
		calendar.setTimeInMillis(0L)
		calendar.set(2001, 1-1, 1, 0, 0, 0)

		(0..<100).each {index ->
			ContactComposite contact = new ContactComposite()

			contact.name.family  = 'Family' + ((index / 10) as int)
			contact.name.given = 'Given' + (index % 10)

			contact.birthday = new Date(calendar.timeInMillis)
			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1)

			contact.address.postCode = '1310045'
			contact.address.address1 = 'Tokyo'
			contact.address.address2 = 'Sumida-ku'
			contact.address.address3 = 'Oshiue' + ((index / 100) as int) + '-' + (((index / 10) as int) % 10) + '-' + (index % 10)

			(0..<(index % 10)).each {index2 ->
				Phone phone = new Phone()
				phone.phoneNumber = '0' + (90_0000_0000L + index * 10000 + index2)
				contact.phones.add(phone)
			}

			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact.class).insert(it, contact)
			}
		}
	/**/DebugTrace.leave()
	}

	// Colors
	@Shared colors = [
		'Beige',
		'Black',
		'Blue',
		'Gray',
		'Green',
		'Pink',
		'Purple',
		'Rose',
		'Turquoise Blue',
		'White',
		'Yellow',
	]

	/**
	 * Inserts to Product table.
	 */
	def insertProducts() {
	/**/DebugTrace.enter()
		(0..<100).each {index ->
			Product product = new Product()

			int sizeIndex = index % 5
			int colorIndex = index % colors.size()

			product.productName = 'Product' + index
			product.price       = 1000 + index * 10
			product.productSize        =
				sizeIndex == 0 ? Size.XS :
				sizeIndex == 1 ? Size.S  :
				sizeIndex == 2 ? Size.M  :
				sizeIndex == 3 ? Size.L  :
				sizeIndex == 4 ? Size.XL : null
			product.color       = colors[colorIndex]

			Transaction.execute(connectionSupplier) {
				new Sql<>(Product.class).insert(it, product)
			}
		}
	/**/DebugTrace.leave()
	}

	/**
	 * Insert to Sale table.
	 */
	def insertSales() {
	/**/DebugTrace.enter()
		Calendar calendar = Calendar.getInstance()
		calendar.setTimeInMillis(0L)
		calendar.set(2017, 5-1, 5, 0, 0, 0)

		(0..<100).each {index ->
			SaleComposite sale = new SaleComposite()

			int index1 = index
			Transaction.execute(connectionSupplier) {
				Contact contact = new Sql<>(Contact.class)
					.where('{name.family} = {}', 'Family' + (((index1 / 10) as int) % 10))
					  .and('{name.given} = {}', 'Given' + (index1 % 10))
					.select(it).orElse(null)

				sale.contactId = contact.id

				sale.saleDate = new Date(calendar.timeInMillis)
				calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1)

				sale.taxRate = 8

				(0..<10).each {index2 ->
					SaleItem item = new SaleItem()

					Product product = new Sql<>(Product.class)
						.where('{productName} = {}', 'Product' + ((index1 + index2) % 100))
						.select(it).orElse(null)

					item.productId = product.id
					item.quantity  = (short)(10 + index2 % 5)

					sale.items.add(item)
				}
			}

			Transaction.execute(connectionSupplier) {
				new Sql<>(Sale.class).insert(it, sale)
			}
		}
	/**/DebugTrace.leave()
	}

	// select(Connection connection, Consumer<? super E> consumer,
	//                               Consumer<? super JE1> consumer1)
	//
	// select(Connection connection, Consumer<? super E> consumer,
	//                               Consumer<? super JE1> consumer1,
	//                               Consumer<? super JE2> consumer2)
	//
	// select(Connection connection, Consumer<? super E> consumer,
	//                               Consumer<? super JE1> consumer1,
	//                               Consumer<? super JE2> consumer2,
	//                               Consumer<? super JE3> consumer3)
	//
	// select(Connection connection, Consumer<? super E> consumer,
	//                               Consumer<? super JE1> consumer1,
	//                               Consumer<? super JE2> consumer2,
	//                               Consumer<? super JE3> consumer3,
	//                               Consumer<? super JE4> consumer4)
	//
	// select(Connection connection)
	def "select - exception"() {
	/**/DebugTrace.enter()

		setup:
			List<Contact> contacts = []
			List<Address> addresses = []

		// select(Connection connection, Consumer<? super E> consumer,
		//                               Consumer<? super JE1> consumer1)
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact.class, 'C')
					.<Address>select(it, {contacts << it}, {addresses << it})
			}

		then:
			thrown IllegalStateException

		// select(Connection connection, Consumer<? super E> consumer,
		//                               Consumer<? super JE1> consumer1,
		//                               Consumer<? super JE2> consumer2)
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact.class, 'C')
					.innerJoin(Address.class, 'A', '{A.adressId} = {P.adressId}')
					.<Address, Address>select(it, {contacts << it}, {addresses << it}, {addresses << it})
			}

		then:
			thrown IllegalStateException

		// select(Connection connection, Consumer<? super E> consumer,
		//                               Consumer<? super JE1> consumer1,
		//                               Consumer<? super JE2> consumer2,
		//                               Consumer<? super JE3> consumer3)
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact.class, 'C')
					.innerJoin(Address.class, 'A1', '{A1.adressId} = {P.adressId}')
					.innerJoin(Address.class, 'A2', '{A2.adressId} = {P.adressId}')
					.<Address, Address, Address>select(it, {contacts << it}, {addresses << it}, {addresses << it}, {addresses << it})
			}

		then:
			thrown IllegalStateException

		// select(Connection connection, Consumer<? super E> consumer,
		//                               Consumer<? super JE1> consumer1,
		//                               Consumer<? super JE2> consumer2,
		//                               Consumer<? super JE3> consumer3,
		//                               Consumer<? super JE4> consumer4)
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact.class, 'C')
					.innerJoin(Address.class, 'A1', '{A1.adressId} = {P.adressId}')
					.innerJoin(Address.class, 'A2', '{A2.adressId} = {P.adressId}')
					.innerJoin(Address.class, 'A3', '{A3.adressId} = {P.adressId}')
					.<Address, Address, Address, Address>select(it, {contacts << it}, {addresses << it}, {addresses << it}, {addresses << it}, {addresses << it})
			}

		then:
			thrown IllegalStateException


	/**/DebugTrace.leave()
	}

	// select(Connection connection)
	def "select1 #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)

		setup:
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact.class).select(it)
			}

		then:
			thrown ManyRowsException

		when:
			Contact contact = null
			Transaction.execute(connectionSupplier) {
				contact = new Sql<>(Contact.class).where('0<>0').select(it).orElse(null)
			}

		then:
			contact == null

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection)
	// SELECT MAX(...)
	def "select MAX #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)

		setup:
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			Contact contact = null

		when:
			Transaction.execute(connectionSupplier) {
				contact = new Sql<>(Contact.class) // without table alias
					.expression('id', 'MAX({id})')         // without table alias
					.columns('id')                         // without table alias
					.select(it).orElse(null)
			}
			/**/DebugTrace.print('contact', contact)

		then:
			contact != null
			contact.id != 0

		when:
			Transaction.execute(connectionSupplier) {
				contact = new Sql<>(Contact.class, 'C')// with table alias
					.expression('id', 'MAX({C.id})')           // with table alias
					.columns('C.id')                           // with table alias
					.select(it).orElse(null)
			}
		/**/DebugTrace.print('contact', contact)

		then:
			contact != null
			contact.id != 0

		when:
			Transaction.execute(connectionSupplier) {
				contact = new Sql<>(Contact.class, 'C') // with table alias
					.expression('id', 'MAX({id})')              // without table alias
					.columns('C.id')                            // with table alias
					.select(it).orElse(null)
			}
		/**/DebugTrace.print('contact', contact)

		then:
			contact != null
			contact.id != 0

		when:
			Transaction.execute(connectionSupplier) {
				contact = new Sql<>(Contact.class, 'C') // with table alias
					.expression('id', 'MAX({C.id})')            // with table alias
					.columns('id')                              // without table alias
					.select(it).orElse(null)
			}
		/**/DebugTrace.print('contact', contact)

		then:
			contact != null
			contact.id != 0

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// selectCount(Connection connection)
	def "selectCount #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)

		setup:
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			def count = 0

		when:
			Transaction.execute(connectionSupplier) {
				count = new Sql<>(Phone.class, 'P')
					.innerJoin(Contact.class, 'C', '{C.id} = {P.contactId}')
					.where('{P.phoneNumber} LIKE {}', '090____0003')
						.and('{C.name.family} = {}', 'Family1')
					.selectCount(it)
			}
	/**/DebugTrace.print('count', count)

		then:
			count == 6 // 4, 5, 6, 7, 8, 9

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / limit, offset
	def "limit, offset #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			List<Contact> contacts = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact.class)
					.where('{name.family } IN ({},{},{},{},{})', 'Family0', 'Family1', 'Family2', 'Family3', 'Family4')
					  .and('{name.given} IN ({},{},{},{},{})', 'Given5', 'Given6', 'Given7', 'Given8', 'Given9')
					.orderBy('{name.family}').desc()
					.orderBy('{name.given}').desc()
					.offset(5).limit(15)
					.select(it, {contacts << it})
			}

		then:
			contacts.size() == 15
			contacts[ 0].name.family == 'Family3'
			contacts[ 0].name.given  == 'Given9'
			contacts[14].name.family == 'Family1'
			contacts[14].name.given  == 'Given5'

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / innerJoin
	def "joinTest #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			List<Phone> phones = []
			List<Contact> contacts = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Phone.class, 'P')
					.innerJoin(Contact.class, 'C', '{C.id} = {P.contactId}')
					.where('{P.phoneNumber} LIKE {}', '090____0003')
						.and('{C.name.family} = {}', 'Family5')
					.orderBy('{P.phoneNumber}').desc()
					.columns('P.phoneId', 'P.phoneNumber', 'C.id')
					.<Contact>select(it, {phones << it}, {contacts << it})
			}
		/**/DebugTrace.print('phones .size', phones .size())
		/**/DebugTrace.print('contacts.size', contacts.size())

		then:
			phones  .size() == 6 // 4, 5, 6, 7, 8, 9
			contacts.size() == 6 // 4, 5, 6, 7, 8, 9 
			phones[0].phoneNumber == '09000590003'
			phones[5].phoneNumber == '09000540003'

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / innerJoin
	def "joinTest1 #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			List<SaleItem> saleItems = []
			List<Sale    > sales     = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(SaleItem.class, 'SI')
					.innerJoin(Sale.class, 'S', '{S.id} = {SI.saleId}')
					.limit(100)
					.<Sale>select(it, {saleItems << it}, {sales << it})
			}
		/**/DebugTrace.print('sales.size', sales.size())

		then:
			sales.size() == saleItems.size()

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / innerJoin x 2
	def "joinTest2 #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			List<SaleItem> saleItems = []
			List<Sale    > sales     = []
			List<Contact > contacts  = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(SaleItem.class, 'SI')
					.innerJoin(Sale   .class, 'S', '{S.id} = {SI.saleId}')
					.innerJoin(Contact.class, 'C', '{C.id} = {S.contactId}')
					.limit(100)
					.<Sale, Contact>select(it, {saleItems << it}, {sales << it}, {contacts << it})
			}
		/**/DebugTrace.print('sales.size', sales.size())

		then:
			sales   .size() == saleItems .size()
			contacts.size() == saleItems .size()

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / innerJoin x 3
	def "joinTest3 #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			List<SaleItem> saleItems = []
			List<Sale    > sales     = []
			List<Contact > contacts  = []
			List<Address > addresses = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(SaleItem.class, 'SI')
					.innerJoin(Sale   .class, 'S', '{S.id} = {SI.saleId}')
					.innerJoin(Contact.class, 'C', '{C.id} = {S.contactId}')
					.innerJoin(Address.class, 'A', '{A.id} = {C.addressId}')
					.limit(100)
					.<Sale, Contact, Address>select(it, {saleItems << it}, {sales << it}, {contacts << it}, {addresses << it})
			}
		/**/DebugTrace.print('sales.size', sales.size())

		then:
			sales    .size() == saleItems .size()
			contacts .size() == saleItems .size()
			addresses.size() == saleItems .size()

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / innerJoin x 4
	def "joinTest4 #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			List<SaleItem> saleItems = []
			List<Sale    > sales     = []
			List<Contact > contacts  = []
			List<Address > addresses = []
			List<Phone   > phones    = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(SaleItem.class, 'SI')
					.innerJoin(Sale   .class, 'S', '{S.id} = {SI.saleId}')
					.innerJoin(Contact.class, 'C', '{C.id} = {S.contactId}')
					.innerJoin(Address.class, 'A', '{A.id} = {C.addressId}')
					.leftJoin (Phone  .class, 'P', '{P.contactId} = {C.id}')
					.limit(100)
					.<Sale, Contact, Address, Phone>select(it, {saleItems << it}, {sales << it}, {contacts << it}, {addresses << it}, {phones << it})
			}
		/**/DebugTrace.print('sales.size', sales.size())

		then:
			sales    .size() == saleItems .size()
			contacts .size() == saleItems .size()
			addresses.size() == saleItems .size()
			phones   .size() == saleItems .size()

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / innerJoin x 4
	def "select join x 4 #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			List<SaleItem> saleItems = []
			List<Sale    > sales     = []

		when:
			Sql<SaleItem> sql =  new Sql<>(SaleItem.class)
			sql .innerJoin(Sale   .class, 'S', '{S.id} = {saleId}')
				.innerJoin(Contact.class, 'C', '{C.id} = {S.contactId}')
				.innerJoin(Address.class, 'A', '{A.id} = {C.addressId}')
				.leftJoin (Phone  .class, 'P', '{P.contactId} = {C.id}')
				.limit(100)
			Transaction.execute(connectionSupplier) {
				sql.<Sale>select(it, {saleItems << it}, {sales << it})
			}
		/**/DebugTrace.print('saleItems.size  ', saleItems.size())
		/**/DebugTrace.print('sales.size      ', sales.size())
		/**/DebugTrace.print('sql.generatedSql', sql.generatedSql())
		/**/DebugTrace.print('sql.columns     ', sql.getColumns())

		then:
			sql.generatedSql().indexOf('itemIndex'    ) >= 0
			sql.generatedSql().indexOf('S.updateCount') >= 0
			sql.generatedSql().indexOf('C.updateCount') == -1
			sql.generatedSql().indexOf('A.updateCount') == -1
			sql.generatedSql().indexOf('P.updateCount') == -1
			new ArrayList<String>(sql.getColumns()) == ['.*', 'S.*']

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	@Table('super')
	public static class ContactPhoneCount extends Phone {
		@Select('COUNT({id})')
		public int count
	}

	// select(Connection connection) / gourpBy
	def "gourpBy #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			List<ContactPhoneCount> phoneCounts = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactPhoneCount.class, 'P')
					.innerJoin(Contact.class, 'C', '{C.id} = {P.contactId}')
					.columns('P.contactId', 'P.count')
					.where('{C.name.given} LIKE {}', '%4')
						.or('{C.name.given} LIKE {}', '%5')
						.or('{C.name.given} LIKE {}', '%6')
					.groupBy('{P.contactId}')
					.having('COUNT({P.id}) >= {}', 6)
					.select(it, {phoneCounts << it})
			}

		then:
			phoneCounts.size() == 10
			phoneCounts[0].count == 6

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / forUpdate
	def "forUpdate #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
		if (Sql.getDatabase() instanceof SQLite) return
	/**/DebugTrace.enter()

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			Contact contact0 = null

		when:
			Transaction.execute(connectionSupplier) {
				contact0 = new Sql<>(Contact.class)
					.limit(1)
					.orderBy('{id}')
					.select(it).orElse(null)
			}
		/**/DebugTrace.print('1 contact0', contact0)
			
		then:
			assert contact0 != null

		when:
			def threads = new Thread[10]
			(0..<threads.length).each {index ->
				threads[index] = new Thread({
					Transaction.execute(connectionSupplier) {
						def myIndex = index
					/**/DebugTrace.print('1 myIndex', myIndex)
						Contact contact = new Sql<>(Contact.class)
							.where('{id} = {}', contact0.id)
							.forUpdate()
							.select(it).orElse(null)
						assert contact != null
					/**/DebugTrace.print('2-' + myIndex + ' contact', contact)

						contact.name.given  += myIndex
					/**/DebugTrace.print('contact.name.given' , contact.name.given )

						if (myIndex == 0)
							Thread.sleep(1000L)

					/**/DebugTrace.print('2 myIndex', myIndex)
						new Sql<>(Contact.class).update(it, contact)
					}
				})
				threads[index].start()
				Thread.sleep(100L)
			}

		then:
			true

		when:
			// Waits for all threads to finish.
			(0..<threads.length).each {threads[it].join()}

			Contact contact1 = null
			Transaction.execute(connectionSupplier) {
				contact1 = new Sql<>(Contact.class)
					.where('{id} = {}', contact0.id)
					.select(it).orElse(null)
			/**/DebugTrace.print('3 contact1', contact1)
				assert contact1 != null
				assert contact1.name.given.indexOf('0') >= 0
				assert contact1.name.given.indexOf('1') >= 0
				assert contact1.name.given.indexOf('2') >= 0
				assert contact1.name.given.indexOf('3') >= 0
				assert contact1.name.given.indexOf('4') >= 0
				assert contact1.name.given.indexOf('5') >= 0
				assert contact1.name.given.indexOf('6') >= 0
				assert contact1.name.given.indexOf('7') >= 0
				assert contact1.name.given.indexOf('8') >= 0
				assert contact1.name.given.indexOf('9') >= 0

				new Sql<>(Contact.class).update(it, contact1)
			}

		then:
			true

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// exceptionTest
	def "exception"() {
	/**/DebugTrace.enter()

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact.class)
					.where('{name.family} LIKE {}', 'Family2%')
					.select(it)
			}

		then:
			def e = thrown ManyRowsException
			e.message.indexOf('WHERE') >= 0

	/**/DebugTrace.leave()
	}

	@Table('super')
	public static class ContactFn extends Contact {
		public String fullName
	}

	@SelectProperty(property = 'fullName', expression = "CONCAT({name.given},' ',{name.family})")
	static class ContactFnMyMySQL extends ContactFn {}

	@SelectProperty(property = 'fullName', expression = "{name.given}||' '||{name.family}")
	static class ContactFnOracle extends ContactFn {}

	@SelectProperty(property = 'fullName', expression = "{name.given}||' '||{name.family}")
	static class ContactFnPostgreSQL extends ContactFn {}

	@SelectProperty(property = 'fullName', expression = "{name.given}||' '||{name.family}")
	static class ContactFnSQLite extends ContactFn {}

	@SelectProperty(property = 'fullName', expression = "{name.given}+' '+{name.family}")
	static class ContactFnSQLServer extends ContactFn {}

	// extendsClassTest
	def "extends Class #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)

		when:
			Class<? extends ContactFn> contactClass =
				Sql.database instanceof MySQL      ? ContactFnMyMySQL   .class :
				Sql.database instanceof Oracle     ? ContactFnOracle    .class :
				Sql.database instanceof PostgreSQL ? ContactFnPostgreSQL.class :
				Sql.database instanceof SQLite     ? ContactFnSQLite    .class :
				Sql.database instanceof SQLServer  ? ContactFnSQLServer .class : null

		then:
			contactClass != null

		when:
			ContactFn contact = null
			Transaction.execute(connectionSupplier) {
				contact = new Sql<>(contactClass)
					.columns('fullName')
					.limit(1)
					.select(it).orElse(null)
			}
		/**/DebugTrace.print('contact', contact)

		then:
			contact != null

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}
}
