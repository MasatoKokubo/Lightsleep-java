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
	static def connectionSupplierClasses = [
		C3p0,
		Dbcp,
		HikariCP,
		TomcatCP,
		Jdbc
	]

	@Shared ConnectionSupplier connectionSupplier
	@Shared Calendar birthdayStart
	@Shared Calendar saleDateStart

	/**
	 * Inserts data for tests.
	 */
	def setupSpec() {
	/**/DebugTrace.enter()
		connectionSupplier = ConnectionSpec.getConnectionSupplier(Jdbc)

		Transaction.execute(connectionSupplier) {
			new Sql<>(Contact ).where(Condition.ALL).delete(it)
			new Sql<>(Address ).where(Condition.ALL).delete(it)
			new Sql<>(Phone   ).where(Condition.ALL).delete(it)
			new Sql<>(Product ).where(Condition.ALL).delete(it)
			new Sql<>(Sale    ).where(Condition.ALL).delete(it)
			new Sql<>(SaleItem).where(Condition.ALL).delete(it)
		}

		// 2001-01-01
		birthdayStart = Calendar.instance
		birthdayStart.clear(); birthdayStart.set(2001, 1-1, 1, 0, 0, 0)

		// 2017-05-05
		saleDateStart = Calendar.instance
		saleDateStart.clear(); saleDateStart.set(2017, 5-1, 5, 0, 0, 0)

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
		Calendar calendar = birthdayStart.clone()

		// 0 ~ 99
		(0..<100).each {index ->
			ContactComposite contact = new ContactComposite()

			// Last0, Last0, ..., Last0,
			// Last1, Last1, ..., Last1,
			//   ...,
			// Last9, Last9, ..., Last9
			contact.name.last  = 'Last' + ((index / 10) as int)

			// First0, First1, ..., First9,
			// First0, First1, ..., First9,
			//   ...,
			// First0, First1, ..., First9
			contact.name.first = 'First' + (index % 10)

			// 2001-01-01, 2001-01-02, ...
			contact.birthday = new Date(calendar.timeInMillis)
			calendar.add(Calendar.DAY_OF_MONTH, 1)

			contact.address.postCode = '1310045'
			contact.address.address1 = 'Tokyo'
			contact.address.address2 = 'Sumida-ku'

			// 0-0-0 Oshiage ..., 0-0-9 Oshiage,
			// 0-1-0 Oshiage ..., 0-1-9 Oshiage,
			//   ...,
			// 0-9-0 Oshiage ..., 0-9-9 Oshiage
			contact.address.address3 =  ((index / 100) as int) + '-' + (((index / 10) as int) % 10) + '-' + (index % 10) + ' Oshiage'

			// index =  0 : []
			// index =  1 : [09000010000]
			// index =  2 : [09000020000, 09000020001]
			// index =  3 : [09000030000, 09000030001, 09000030002]
			//   ...
			// index =  9 : [09000090000, 09000090001, 09000090002, ..., 09000090009]
			//   ...
			// index = 99 : [09000990000, 09000990001, 09000990002, ..., 09000990009]
			(0..<(index % 10)).each {index2 ->
				Phone phone = new Phone()
				phone.phoneNumber = '0' + (90_0000_0000L + index * 10000 + index2)
				contact.phones.add(phone)
			}

			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact).insert(it, contact)
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

			// Product0, Product1, ... Product99
			product.productName = 'Product' + index

			// 1000, 1010, ... 1990
			product.price = 1000 + index * 10

			// XS, S, M, L, XL, XS, S, M, L, XL, ....
			product.productSize =
				sizeIndex == 0 ? Size.XS :
				sizeIndex == 1 ? Size.S  :
				sizeIndex == 2 ? Size.M  :
				sizeIndex == 3 ? Size.L  :
				sizeIndex == 4 ? Size.XL : null

			// Beige, Black, ..., Yellow, Beige, Black, ..., Yellow, ...
			product.color = colors[colorIndex]

			Transaction.execute(connectionSupplier) {
				new Sql<>(Product).insert(it, product)
			}
		}
	/**/DebugTrace.leave()
	}

	/**
	 * Insert to Sale table.
	 */
	def insertSales() {
	/**/DebugTrace.enter()
		Calendar calendar = saleDateStart.clone()

		// 0 ~ 99
		(0..<100).each {index ->
			SaleComposite sale = new SaleComposite()

			Transaction.execute(connectionSupplier) {
				Contact contact = new Sql<>(Contact)
					.where('{name.last} = {}', 'Last' + (((index / 10) as int) % 10))
					  .and('{name.first} = {}', 'First' + (index % 10))
					.select(it).orElse(null)

				// id(Last0, First0), id(Last0, First1), ... id(Last0, First9)
				// id(Last1, First0), id(Last1, First1), ... id(Last1, First9)
				//    ...
				// id(Last9, First0), id(Last9, First1), ... id(Last9, First9)
				sale.contactId = contact.id

				// 2017-05-05, 2017-05-06, ...
				sale.saleDate = new Date(calendar.timeInMillis)
				calendar.add(Calendar.DAY_OF_MONTH, 1)

				// 8%
				sale.taxRate = 8

				// 0 ~ 9
				(0..<10).each {index2 ->
					SaleItem item = new SaleItem()

					Product product = new Sql<>(Product)
						.where('{productName} = {}', 'Product' + ((index + index2) % 100))
						.select(it).orElse(null)

					item.productId = product.id
					item.quantity  = (short)(10 + index2 % 5)

					sale.items.add(item)
				}
			}

			Transaction.execute(connectionSupplier) {
				new Sql<>(Sale).insert(it, sale)
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
	def "SelectSpec select - exception"() {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('select - exception')

		setup:
			List<Contact> contacts = []
			List<Address> addresses = []

		// select(Connection connection, Consumer<? super E> consumer,
		//                               Consumer<? super JE1> consumer1)
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact, 'C')
					.<Address>select(it, {contacts << it}, {addresses << it})
			}

		then:
			thrown IllegalStateException

		// select(Connection connection, Consumer<? super E> consumer,
		//                               Consumer<? super JE1> consumer1,
		//                               Consumer<? super JE2> consumer2)
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact, 'C')
					.innerJoin(Address, 'A', '{A.adressId} = {P.adressId}')
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
				new Sql<>(Contact, 'C')
					.innerJoin(Address, 'A1', '{A1.adressId} = {P.adressId}')
					.innerJoin(Address, 'A2', '{A2.adressId} = {P.adressId}')
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
				new Sql<>(Contact, 'C')
					.innerJoin(Address, 'A1', '{A1.adressId} = {P.adressId}')
					.innerJoin(Address, 'A2', '{A2.adressId} = {P.adressId}')
					.innerJoin(Address, 'A3', '{A3.adressId} = {P.adressId}')
					.<Address, Address, Address, Address>select(it, {contacts << it}, {addresses << it}, {addresses << it}, {addresses << it}, {addresses << it})
			}

		then:
			thrown IllegalStateException


	/**/DebugTrace.leave()
	}

	// select(Connection connection)
	def "SelectSpec select1 #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('select1')
	/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)

		setup:
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact).select(it)
			}

		then:
			thrown ManyRowsException

		when:
			Contact contact = null
			Transaction.execute(connectionSupplier) {
				contact = new Sql<>(Contact).where('0<>0').select(it).orElse(null)
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
	def "SelectSpec select MAX #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('select MAX')
	/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)

		setup:
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			Contact contact = null

		when:
			Transaction.execute(connectionSupplier) {
				contact = new Sql<>(Contact) // without table alias
					.expression('id', 'MAX({id})') // without table alias
					.columns('id')                 // without table alias
					.select(it).orElse(null)
			}

		then:
			contact != null
			contact.id != 0

		when:
			Transaction.execute(connectionSupplier) {
				contact = new Sql<>(Contact, 'C')// with table alias
					.expression('id', 'MAX({C.id})')   // with table alias
					.columns('C.id')                   // with table alias
					.select(it).orElse(null)
			}

		then:
			contact != null
			contact.id != 0

		when:
			Transaction.execute(connectionSupplier) {
				contact = new Sql<>(Contact, 'C') // with table alias
					.expression('id', 'MAX({id})')      // without table alias
					.columns('C.id')                    // with table alias
					.select(it).orElse(null)
			}

		then:
			contact != null
			contact.id != 0

		when:
			Transaction.execute(connectionSupplier) {
				contact = new Sql<>(Contact, 'C') // with table alias
					.expression('id', 'MAX({C.id})')    // with table alias
					.columns('id')                      // without table alias
					.select(it).orElse(null)
			}

		then:
			contact != null
			contact.id != 0

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / where A and B or C and D
	def "SelectSpec where A and B or C and D #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('where A and B or C and D')

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			List<Contact> contacts = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact)
					.where('{name.last} = {}', 'Last0')
					.and  ('{name.first} = {}' , 'First0')
					.or(Condition
						.of ('{name.last} = {}', 'Last1')
						.and('{name.first} = {}' , 'First1')
					)
					.orderBy('{name.last}')
					.orderBy('{name.first}')
					.select(it, {contacts << it})
			}

		then:
			contacts.size() == 2
			contacts[0].name.last == 'Last0'
			contacts[0].name.first  == 'First0'
			contacts[1].name.last == 'Last1'
			contacts[1].name.first  == 'First1'

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / where (A or B) and (C or D)
	def "SelectSpec where (A or B) and (C or D) #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('where (A or B) and (C or D)')

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			List<Contact> contacts = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact)
					.where('{name.last} = {}', 'Last0')
					.or   ('{name.last} = {}', 'Last1')
					.and(Condition
						.of('{name.first} = {}', 'First0')
						.or('{name.first} = {}', 'First1')
					)
					.orderBy('{name.last}')
					.orderBy('{name.first}')
					.select(it, {contacts << it})
			}

		then:
			contacts.size() == 4
			contacts[0].name.last  == 'Last0'
			contacts[0].name.first == 'First0'
			contacts[1].name.last  == 'Last0'
			contacts[1].name.first == 'First1'
			contacts[2].name.last  == 'Last1'
			contacts[2].name.first == 'First0'
			contacts[3].name.last  == 'Last1'
			contacts[3].name.first == 'First1'

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// selectCount(Connection connection)
	def "SelectSpec selectCount #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('selectCount')
	/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)

		setup:
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			def count = 0

		when:
			Transaction.execute(connectionSupplier) {
				count = new Sql<>(Phone, 'P')
					.innerJoin(Contact, 'C', '{C.id} = {P.contactId}')
					.where('{P.phoneNumber} LIKE {}', '090____0003')
						.and('{C.name.last} = {}', 'Last1')
					.selectCount(it)
			}

		then:
			count == 6 // 4, 5, 6, 7, 8, 9

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / limit, offset
	def "SelectSpec limit, offset #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('limit, offset')

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			List<Contact> contacts = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact)
					.where('{name.last} IN {}', ['Last0', 'Last1', 'Last2', 'Last3', 'Last4'])
					  .and('{name.first} IN {}', ['First5', 'First6', 'First7', 'First8', 'First9'])
					.orderBy('{name.last}').desc()
					.orderBy('{name.first}').desc()
					.offset(5).limit(15)
					.select(it, {contacts << it})
			}

		then:
			contacts.size() == 15
			contacts[ 0].name.last  == 'Last3'
			contacts[ 0].name.first == 'First9'
			contacts[14].name.last  == 'Last1'
			contacts[14].name.first == 'First5'

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / innerJoin
	def "SelectSpec innerJoin #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('innerJoin')

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			List<Phone> phones = []
			List<Contact> contacts = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Phone, 'P')
					.innerJoin(Contact, 'C', '{C.id} = {P.contactId}')
					.where('{P.phoneNumber} LIKE {}', '090____0003')
						.and('{C.name.last} = {}', 'Last5')
					.orderBy('{P.phoneNumber}').desc()
					.columns('P.phoneId', 'P.phoneNumber', 'C.id')
					.<Contact>select(it,
						{phones << it}, {contacts << it}
					)
			}

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
	def "SelectSpec innerJoin2 #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('innerJoin')

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			List<SaleItem> saleItems = []
			List<Sale    > sales     = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(SaleItem, 'SI')
					.innerJoin(Sale, 'S', '{S.id} = {SI.saleId}')
					.where('{S.saleDate} = {}', new Date(saleDateStart.timeInMillis))
					.<Sale>select(it,
						{saleItems << it}, {sales << it}
					)
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
	def "SelectSpec innerJoin innerJoin #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('innerJoin innerJoin')

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			List<SaleItem> saleItems = []
			List<Sale    > sales     = []
			List<Contact > contacts  = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(SaleItem, 'SI')
					.innerJoin(Sale   , 'S', '{S.id} = {SI.saleId}')
					.innerJoin(Contact, 'C', '{C.id} = {S.contactId}')
					.where("{C.birthday} = {}", new Date(birthdayStart.timeInMillis))
					.<Sale, Contact>select(it,
						{saleItems << it}, {sales << it}, {contacts << it}
					)
			}

		then:
			sales   .size() == saleItems .size()
			contacts.size() == saleItems .size()

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / innerJoin x 3
	def "SelectSpec innerJoin innerJoin innerJoin #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('innerJoin innerJoin innerJoin')

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			List<SaleItem> saleItems = []
			List<Sale    > sales     = []
			List<Contact > contacts  = []
			List<Address > addresses = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(SaleItem, 'SI')
					.innerJoin(Sale   , 'S', '{S.id} = {SI.saleId}')
					.innerJoin(Contact, 'C', '{C.id} = {S.contactId}')
					.innerJoin(Address, 'A', '{A.id} = {C.addressId}')
					.where('{A.address3} LIKE {}', '%0-0-0')
					.<Sale, Contact, Address>select(it,
						{saleItems << it}, {sales << it},
						{contacts << it}, {addresses << it}
					)
			}

		then:
			sales    .size() == saleItems .size()
			contacts .size() == saleItems .size()
			addresses.size() == saleItems .size()

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / innerJoin x 3 + leftJoin
	def "SelectSpec innerJoin innerJoin innerJoin leftJoin #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('innerJoin innerJoin innerJoin leftJoin')

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
				new Sql<>(SaleItem, 'SI')
					.innerJoin(Sale   , 'S', '{S.id} = {SI.saleId}')
					.innerJoin(Contact, 'C', '{C.id} = {S.contactId}')
					.innerJoin(Address, 'A', '{A.id} = {C.addressId}')
					.leftJoin (Phone  , 'P', '{P.contactId} = {C.id}')
					.where('{P.phoneNumber} = {}', '09000010000')
					.<Sale, Contact, Address, Phone>select(it,
						{saleItems << it}, {sales << it},
						{contacts << it}, {addresses << it},
						{phones << it}
					)
			}

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

	@Table('super')
	public static class ContactPhoneCount extends Phone {
		@Select('COUNT({id})')
		public int count
	}

	// select(Connection connection) / gourpBy
	def "SelectSpec gourpBy #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('gourpBy')

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			List<ContactPhoneCount> phoneCounts = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactPhoneCount, 'P')
					.innerJoin(Contact, 'C', '{C.id} = {P.contactId}')
					.columns('P.contactId', 'P.count')
					.where('{C.name.first} LIKE {}', '%4')
						.or('{C.name.first} LIKE {}', '%5')
						.or('{C.name.first} LIKE {}', '%6')
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
	def "SelectSpec forUpdate #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
		if (Sql.database instanceof SQLite) return
	/**/DebugTrace.enter()
	/**/DebugTrace.print('forUpdate')

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			Contact contact0 = null

		when:
			Transaction.execute(connectionSupplier) {
				contact0 = new Sql<>(Contact)
					.limit(1)
					.orderBy('{id}')
					.select(it).orElse(null)
			}
			
		then:
			assert contact0 != null

		when:
			def threads = new Thread[10]
			(0..<threads.length).each {index ->
				threads[index] = new Thread({
					Transaction.execute(connectionSupplier) {
						def myIndex = index
						Contact contact = new Sql<>(Contact)
							.where('{id} = {}', contact0.id)
							.forUpdate()
							.select(it).orElse(null)
						assert contact != null

						contact.name.first  += myIndex

						if (myIndex == 0)
							Thread.sleep(1000L)

						new Sql<>(Contact).update(it, contact)
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
				contact1 = new Sql<>(Contact)
					.where('{id} = {}', contact0.id)
					.select(it).orElse(null)
			/**/DebugTrace.print('3 contact1', contact1)
				assert contact1 != null
				assert contact1.name.first.indexOf('0') >= 0
				assert contact1.name.first.indexOf('1') >= 0
				assert contact1.name.first.indexOf('2') >= 0
				assert contact1.name.first.indexOf('3') >= 0
				assert contact1.name.first.indexOf('4') >= 0
				assert contact1.name.first.indexOf('5') >= 0
				assert contact1.name.first.indexOf('6') >= 0
				assert contact1.name.first.indexOf('7') >= 0
				assert contact1.name.first.indexOf('8') >= 0
				assert contact1.name.first.indexOf('9') >= 0

				new Sql<>(Contact).update(it, contact1)
			}

		then:
			true

		cleanup:
			if (contact0 != null)
				Transaction.execute(connectionSupplier) {new Sql<>(Contact).update(it, contact0)}

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / forUpdate noWait
	def "SelectSpec forUpdate noWait #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
		if (Sql.database instanceof DB2) return
		if (Sql.database instanceof MySQL) return
		if (Sql.database instanceof PostgreSQL) return
		if (Sql.database instanceof SQLite) return
	/**/DebugTrace.enter()
	/**/DebugTrace.print('forUpdate noWait')

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			Contact contact0 = null

		when:
			Transaction.execute(connectionSupplier) {
				contact0 = new Sql<>(Contact)
					.limit(1)
					.orderBy('{id}')
					.select(it).orElse(null)
			}
			
		then:
			assert contact0 != null

		when:
			def threads = new Thread[10]
			(0..<threads.length).each {index ->
				threads[index] = new Thread({
					Transaction.execute(connectionSupplier) {
						try {
							def myIndex = index
							Contact contact = new Sql<>(Contact)
								.where('{id} = {}', contact0.id)
								.forUpdate().noWait()
								.select(it).orElse(null)

							contact.name.first  += myIndex

							if (myIndex == 0)
								Thread.sleep(1500L)

							new Sql<>(Contact).update(it, contact)
						}
						catch (RuntimeSQLException e) {
						/**/DebugTrace.print('e', e)
						}
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
				contact1 = new Sql<>(Contact)
					.where('{id} = {}', contact0.id)
					.select(it).orElse(null)
				assert contact1 != null
				assert contact1.name.first.indexOf('0') >= 0
				assert contact1.name.first.indexOf('1') == -1
				assert contact1.name.first.indexOf('2') == -1
				assert contact1.name.first.indexOf('3') == -1
				assert contact1.name.first.indexOf('4') == -1
				assert contact1.name.first.indexOf('5') == -1
				assert contact1.name.first.indexOf('6') == -1
				assert contact1.name.first.indexOf('7') == -1
				assert contact1.name.first.indexOf('8') == -1
				assert contact1.name.first.indexOf('9') == -1

				new Sql<>(Contact).update(it, contact1)
			}

		then:
			true

		cleanup:
			if (contact0 != null)
				Transaction.execute(connectionSupplier) {new Sql<>(Contact).update(it, contact0)}

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / forUpdate wait N
	def "SelectSpec forUpdate wait N #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
		if (Sql.database instanceof DB2) return
		if (Sql.database instanceof MySQL) return
		if (Sql.database instanceof PostgreSQL) return
		if (Sql.database instanceof SQLite) return
		if (Sql.database instanceof SQLServer) return
	/**/DebugTrace.enter()
	/**/DebugTrace.print('forUpdate wait N')

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			Contact contact0 = null

		when:
			Transaction.execute(connectionSupplier) {
				contact0 = new Sql<>(Contact)
					.limit(1)
					.orderBy('{id}')
					.select(it).orElse(null)
			}
			
		then:
			assert contact0 != null

		when:
			def threads = new Thread[10]
			(0..<threads.length).each {index ->
				threads[index] = new Thread({
					Transaction.execute(connectionSupplier) {
						try {
							def myIndex = index
							Contact contact = new Sql<>(Contact)
								.where('{id} = {}', contact0.id)
								.forUpdate().wait(1) // wait 1000ms
								.select(it).orElse(null)

							contact.name.first  += myIndex

							if (myIndex == 0)
								Thread.sleep(1550L)

						/**/DebugTrace.print('2 myIndex', myIndex)
							new Sql<>(Contact).update(it, contact)
						}
						catch (RuntimeSQLException e) {
						/**/DebugTrace.print('e', e)
						}
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
				contact1 = new Sql<>(Contact)
					.where('{id} = {}', contact0.id)
					.select(it).orElse(null)
			/**/DebugTrace.print('3 contact1', contact1)
				assert contact1 != null
				assert contact1.name.first.indexOf('0') >= 0  //   0~1550ms
				assert contact1.name.first.indexOf('1') == -1 // 100~1100ms
				assert contact1.name.first.indexOf('2') == -1 // 200~1200ms
				assert contact1.name.first.indexOf('3') == -1 // 300~1300ms
				assert contact1.name.first.indexOf('4') == -1 // 400~1400ms
				assert contact1.name.first.indexOf('5') >= 0 || // 500~1500ms
				       contact1.name.first.indexOf('6') >= 0 || // 600~1600ms
				       contact1.name.first.indexOf('7') >= 0  // 700~1700ms
				assert contact1.name.first.indexOf('8') == -1 // 800~1800ms
				assert contact1.name.first.indexOf('9') == -1 // 900~1900ms

				new Sql<>(Contact).update(it, contact1)
			}

		then:
			true

		cleanup:
			if (contact0 != null)
				Transaction.execute(connectionSupplier) {new Sql<>(Contact).update(it, contact0)}

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << connectionSupplierClasses
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / forUpdate - exception
	def "SelectSpec forUpdate - exception #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
		if (Sql.database instanceof DB2) return
		if (Sql.database instanceof MySQL) return
		if (Sql.database instanceof Oracle) return
		if (Sql.database instanceof PostgreSQL) return
		if (Sql.database instanceof SQLServer) return
	/**/DebugTrace.enter()
	/**/DebugTrace.print('forUpdate - exception')

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			Contact contact0 = null

		when:
			Transaction.execute(connectionSupplier) {
				contact0 = new Sql<>(Contact)
					.limit(1)
					.orderBy('{id}')
					.select(it).orElse(null)
			}
		/**/DebugTrace.print('1 contact0', contact0)
			
		then:
			assert contact0 != null

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact)
					.where('{id} = {}', contact0.id)
					.forUpdate()
					.select(it)
			}

		then:
			def e =thrown UnsupportedOperationException
			e.message.indexOf('forUpdate') >= 0

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass = Jdbc
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / forUpdate noWait - exception
	def "SelectSpec forUpdate noWait - exception #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
		if (Sql.database instanceof Oracle) return
		if (Sql.database instanceof SQLite) return
		if (Sql.database instanceof SQLServer) return
	/**/DebugTrace.enter()
	/**/DebugTrace.print('forUpdate noWait - exception')

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			Contact contact0 = null

		when:
			Transaction.execute(connectionSupplier) {
				contact0 = new Sql<>(Contact)
					.limit(1)
					.orderBy('{id}')
					.select(it).orElse(null)
			}
		/**/DebugTrace.print('1 contact0', contact0)
			
		then:
			assert contact0 != null

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact)
					.where('{id} = {}', contact0.id)
					.forUpdate().noWait()
					.select(it)
			}

		then:
			def e = thrown UnsupportedOperationException
			e.message.indexOf('noWait') >= 0

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass = Jdbc
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// select(Connection connection) / forUpdate wait N - exception
	def "SelectSpec forUpdate wait N - exception #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
		if (Sql.database instanceof Oracle) return
		if (Sql.database instanceof SQLite) return
	/**/DebugTrace.enter()
	/**/DebugTrace.print('forUpdate noWait N - exception')

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)
			Contact contact0 = null

		when:
			Transaction.execute(connectionSupplier) {
				contact0 = new Sql<>(Contact)
					.limit(1)
					.orderBy('{id}')
					.select(it).orElse(null)
			}
		/**/DebugTrace.print('1 contact0', contact0)
			
		then:
			assert contact0 != null

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact)
					.where('{id} = {}', contact0.id)
					.forUpdate().wait(5)
					.select(it)
			}

		then:
			def e = thrown UnsupportedOperationException
			e.message.indexOf('wait N') >= 0

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass = Jdbc
			connectionSupplierName = connectionSupplierClass.simpleName
	}

	// exceptionTest
	def "SelectSpec exception - ManyRowsException"() {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('exception - ManyRowsException')

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact)
					.where('{name.last} LIKE {}', 'Last2%')
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

	@Table('super')
	@SelectProperty(property = 'fullName', expression = "{name.first}||' '||{name.last}")
	static class ContactFnDB2 extends ContactFn {}

	@Table('super')
	@SelectProperty(property = 'fullName', expression = "CONCAT({name.first},' ',{name.last})")
	static class ContactFnMySQL extends ContactFn {}

	@Table('super')
	@SelectProperty(property = 'fullName', expression = "{name.first}||' '||{name.last}")
	static class ContactFnOracle extends ContactFn {}

	@Table('super')
	@SelectProperty(property = 'fullName', expression = "{name.first}||' '||{name.last}")
	static class ContactFnPostgreSQL extends ContactFn {}

	@Table('super')
	@SelectProperty(property = 'fullName', expression = "{name.first}||' '||{name.last}")
	static class ContactFnSQLite extends ContactFn {}

	@Table('super')
	@SelectProperty(property = 'fullName', expression = "{name.first}+' '+{name.last}")
	static class ContactFnSQLServer extends ContactFn {}

	// extendsClassTest
	def "SelectSpec extends class #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('extends Class')

		setup:
		/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
			def connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)

		when:
			Class<? extends ContactFn> contactClass =
				Sql.database instanceof DB2        ? ContactFnDB2        :
				Sql.database instanceof MySQL      ? ContactFnMySQL      :
				Sql.database instanceof Oracle     ? ContactFnOracle     :
				Sql.database instanceof PostgreSQL ? ContactFnPostgreSQL :
				Sql.database instanceof SQLite     ? ContactFnSQLite     :
				Sql.database instanceof SQLServer  ? ContactFnSQLServer  : null

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
