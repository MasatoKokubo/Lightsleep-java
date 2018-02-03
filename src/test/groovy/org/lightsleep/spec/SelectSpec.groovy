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
class SelectSpec extends SpecCommon {
	@Shared Calendar birthdayStart
	@Shared Calendar saleDateStart

	/**
	 * Inserts data for tests.
	 */
	def setupSpec() {
	/**/DebugTrace.enter()
		deleteAllTables()

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
				new Sql<>(Contact).connection(it).insert(contact)
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
		// 0 ~ 99
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
				new Sql<>(Product).connection(it).insert(product)
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
				Contact contact = new Sql<>(Contact).connection(it)
					.where('{name.last} = {}', 'Last' + (((index / 10) as int) % 10))
					  .and('{name.first} = {}', 'First' + (index % 10))
					.select().orElse(null)

				// id(Last0, First0), id(Last0, First1), ... id(Last0, First9)
				// id(Last1, First0), id(Last1, First1), ... id(Last1, First9)
				//    ...
				// id(Last9, First0), id(Last9, First1), ... id(Last9, First9)
				sale.contactId = contact.id

				// 2017-05-05, 017-05-06, ...
				sale.saleDate = new Date(calendar.timeInMillis)
				calendar.add(Calendar.DAY_OF_MONTH, 1)

				sale.taxRate = 8

				// 0 ~ 9
				(0..<10).each {index2 ->
					SaleItem item = new SaleItem()

					Product product = new Sql<>(Product).connection(it)
						.where('{productName} = {}', 'Product' + ((index + index2) % 100))
						.select().orElse(null)

					item.productId = product.id

					// 10, 11, 12, 13, 14
					item.quantity  = (short)(10 + index2 % 5)

					sale.items.add(item)
				}
			}

			Transaction.execute(connectionSupplier) {
				new Sql<>(Sale).connection(it).insert(sale)
			}
		}
	/**/DebugTrace.leave()
	}

	// select(Consumer<? super E> consumer,
	//                               Consumer<? super JE1> consumer1)
	//
	// select(Consumer<? super E> consumer,
	//                               Consumer<? super JE1> consumer1,
	//                               Consumer<? super JE2> consumer2)
	//
	// select(Consumer<? super E> consumer,
	//                               Consumer<? super JE1> consumer1,
	//                               Consumer<? super JE2> consumer2,
	//                               Consumer<? super JE3> consumer3)
	//
	// select(Consumer<? super E> consumer,
	//                               Consumer<? super JE1> consumer1,
	//                               Consumer<? super JE2> consumer2,
	//                               Consumer<? super JE3> consumer3,
	//                               Consumer<? super JE4> consumer4)
	//
	// select()
	def "SelectSpec select - exception"() {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('select - exception')

		setup:
			List<Contact> contacts = []
			List<Address> addresses = []

		// select(Consumer<? super E> consumer,
		//                               Consumer<? super JE1> consumer1)
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact, 'C').connection(it)
					.<Address>select({contacts << it}, {addresses << it})
			}

		then:
			thrown IllegalStateException

		// select(Consumer<? super E> consumer,
		//                               Consumer<? super JE1> consumer1,
		//                               Consumer<? super JE2> consumer2)
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact, 'C').connection(it)
					.innerJoin(Address, 'A', '{A.adressId} = {P.adressId}')
					.<Address, Address>select({contacts << it}, {addresses << it}, {addresses << it})
			}

		then:
			thrown IllegalStateException

		// select(Consumer<? super E> consumer,
		//                               Consumer<? super JE1> consumer1,
		//                               Consumer<? super JE2> consumer2,
		//                               Consumer<? super JE3> consumer3)
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact, 'C').connection(it)
					.innerJoin(Address, 'A1', '{A1.adressId} = {P.adressId}')
					.innerJoin(Address, 'A2', '{A2.adressId} = {P.adressId}')
					.<Address, Address, Address>select({contacts << it}, {addresses << it}, {addresses << it}, {addresses << it})
			}

		then:
			thrown IllegalStateException

		// select(Consumer<? super E> consumer,
		//                               Consumer<? super JE1> consumer1,
		//                               Consumer<? super JE2> consumer2,
		//                               Consumer<? super JE3> consumer3,
		//                               Consumer<? super JE4> consumer4)
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact, 'C').connection(it)
					.innerJoin(Address, 'A1', '{A1.adressId} = {P.adressId}')
					.innerJoin(Address, 'A2', '{A2.adressId} = {P.adressId}')
					.innerJoin(Address, 'A3', '{A3.adressId} = {P.adressId}')
					.<Address, Address, Address, Address>select({contacts << it}, {addresses << it}, {addresses << it}, {addresses << it}, {addresses << it})
			}

		then:
			thrown IllegalStateException


	/**/DebugTrace.leave()
	}

	// since 2.2.0
	// selectAs(Class<R> resultClass, Consumer<? super R> consumer) - exception
	// selectAs(Class<R> resultClass) - exception
	public static interface Id {
		public int getId();
		public int setId(int id);
	}
	def "SelectSpec selectAs - exception #connectionSupplier #comment"(
		ConnectionSupplier connectionSupplier, Class<?> resultClass, Class<? extends Exception> exception, String comment) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('selectAs')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
	/**/DebugTrace.print('resultClass', resultClass)
	/**/DebugTrace.print('comment', comment)
		setup:
			List<Id> ids = []
			List<Common> commons = []
			Exception e = null

		// selectAs(Class<R> resultClass, Consumer<? super R> consumer)
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact).connection(it).selectAs(resultClass, {ids << it})
			}
		then:
			thrown exception

		// selectAs(Class<R> resultClass)
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact).connection(it).selectAs(resultClass)
			}
		then:
			thrown exception

	/**/DebugTrace.leave()
		where:
			connectionSupplier = connectionSuppliers[0]
			resultClass |exception               |comment
			null        |NullPointerException    |'null'
			Id          |IllegalArgumentException|'interface'
			Common      |IllegalArgumentException|'abstract class'
			Product.Size|IllegalArgumentException|'enum class'
			int         |IllegalArgumentException|'primitive'
			Table       |IllegalArgumentException|'annotation class'
			Contact[]   |IllegalArgumentException|'array class'
	}

	// select()
	def "SelectSpec select1 #connectionSupplier"(ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('select1')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact).connection(it).select()
			}

		then:
			thrown ManyRowsException

		when:
			Contact contact = null
			Transaction.execute(connectionSupplier) {
				contact = new Sql<>(Contact).connection(it).where('0<>0').select().orElse(null)
			}

		then:
			contact == null

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	// select()
	// SELECT MAX(...)
	def "SelectSpec select MAX #connectionSupplier"(ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('select MAX')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			Contact contact = null

		when:
			Transaction.execute(connectionSupplier) {
				contact = new Sql<>(Contact).connection(it) // without table alias
					.expression('id', 'MAX({id})') // without table alias
					.columns('id')                 // without table alias
					.select().orElse(null)
			}
			/**/DebugTrace.print('contact', contact)

		then:
			contact != null
			contact.id != 0

		when:
			Transaction.execute(connectionSupplier) {
				contact = new Sql<>(Contact, 'C').connection(it)// with table alias
					.expression('id', 'MAX({C.id})')   // with table alias
					.columns('C.id')                   // with table alias
					.select().orElse(null)
			}
		/**/DebugTrace.print('contact', contact)

		then:
			contact != null
			contact.id != 0

		when:
			Transaction.execute(connectionSupplier) {
				contact = new Sql<>(Contact, 'C').connection(it) // with table alias
					.expression('id', 'MAX({id})')      // without table alias
					.columns('C.id')                    // with table alias
					.select().orElse(null)
			}
		/**/DebugTrace.print('contact', contact)

		then:
			contact != null
			contact.id != 0

		when:
			Transaction.execute(connectionSupplier) {
				contact = new Sql<>(Contact, 'C').connection(it) // with table alias
					.expression('id', 'MAX({C.id})')    // with table alias
					.columns('id')                      // without table alias
					.select().orElse(null)
			}
		/**/DebugTrace.print('contact', contact)

		then:
			contact != null
			contact.id != 0

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	// select() / where A and B or C and D
	def "SelectSpec where A and B or C and D #connectionSupplier"(ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('where A and B or C and D')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			List<Contact> contacts = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact).connection(it)
					.where('{name.last} = {}', 'Last0')
					.and  ('{name.first} = {}' , 'First0')
					.or(Condition
						.of ('{name.last} = {}', 'Last1')
						.and('{name.first} = {}' , 'First1')
					)
					.orderBy('{name.last}')
					.orderBy('{name.first}')
					.select({contacts << it})
			}

		then:
			contacts.size() == 2
			contacts[0].name.last == 'Last0'
			contacts[0].name.first  == 'First0'
			contacts[1].name.last == 'Last1'
			contacts[1].name.first  == 'First1'

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	// select() / where (A or B) and (C or D)
	def "SelectSpec where (A or B) and (C or D) #connectionSupplier"(ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('where (A or B) and (C or D)')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			List<Contact> contacts = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact).connection(it)
					.where('{name.last} = {}', 'Last0')
					.or   ('{name.last} = {}', 'Last1')
					.and(Condition
						.of('{name.first} = {}', 'First0')
						.or('{name.first} = {}', 'First1')
					)
					.orderBy('{name.last}')
					.orderBy('{name.first}')
					.select({contacts << it})
			}

		then:
			contacts.size() == 4
			contacts[0].name.last == 'Last0'
			contacts[0].name.first  == 'First0'
			contacts[1].name.last == 'Last0'
			contacts[1].name.first  == 'First1'
			contacts[2].name.last == 'Last1'
			contacts[2].name.first  == 'First0'
			contacts[3].name.last == 'Last1'
			contacts[3].name.first  == 'First1'

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	// selectCount()
	def "SelectSpec selectCount #connectionSupplier"(ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('selectCount')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			def count = 0

		when:
			Transaction.execute(connectionSupplier) {
				count = new Sql<>(Contact).connection(it).selectCount()
			}

		then:
			count == 100

		when:
			Transaction.execute(connectionSupplier) {
				count = new Sql<>(Phone, 'P').connection(it)
					.innerJoin(Contact, 'C', '{C.id} = {P.contactId}')
					.where('{P.phoneNumber} LIKE {}', '090____0003')
						.and('{C.name.last} = {}', 'Last1')
					.selectCount()
			}

		then:
			count == 6 // 4, 5, 6, 7, 8, 9

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	// select() / limit, offset
	def "SelectSpec limit, offset #connectionSupplier"(ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('limit, offset')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			List<Contact> contacts = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact).connection(it)
					.where('{name.last} IN {}', ['Last0', 'Last1', 'Last2', 'Last3', 'Last4'])
					  .and('{name.first} IN {}', ['First5', 'First6', 'First7', 'First8', 'First9'])
					.orderBy('{name.last}').desc()
					.orderBy('{name.first}').desc()
					.offset(5).limit(15)
					.select({contacts << it})
			}

		then:
			contacts.size() == 15
			contacts[ 0].name.last  == 'Last3'
			contacts[ 0].name.first == 'First9'
			contacts[14].name.last  == 'Last1'
			contacts[14].name.first == 'First5'

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	// select() / innerJoin
	def "SelectSpec innerJoin #connectionSupplier"(ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('innerJoin')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			List<Phone> phones = []
			List<Contact> contacts = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Phone, 'P').connection(it)
					.innerJoin(Contact, 'C', '{C.id} = {P.contactId}')
					.where('{P.phoneNumber} LIKE {}', '090____0003')
						.and('{C.name.last} = {}', 'Last5')
					.orderBy('{P.phoneNumber}').desc()
					.columns('P.phoneId', 'P.phoneNumber', 'C.id')
					.<Contact>select({phones << it}, {contacts << it})
			}
		/**/DebugTrace.print('phones .size', phones .size())
		/**/DebugTrace.print('contacts.size', contacts.size())

		then:
			phones  .size() == 6 // 4, 5, 6, 7, 8, 9
			contacts.size() == 6 // 4, 5, 6, 7, 8, 9 
			phones[0].phoneNumber == '09000590003'
			phones[5].phoneNumber == '09000540003'

		when:
			phones = []
			def sql = new Sql<>(Phone, 'P')
				.innerJoin(Contact, 'C', '{C.id} = {P.contactId}')
				.where('{P.phoneNumber} LIKE {}', '090____0006')
					.and('{C.name.last} = {}', 'Last5')
				.orderBy('{P.phoneNumber}')
			Transaction.execute(connectionSupplier) {
				sql.connection(it).select({phones << it})
			}

		then:
			// No 'C_xxxx' columns
		/**/DebugTrace.print('sql.generatedSql', sql.generatedSql)
			sql.generatedSql.indexOf('P_phoneNumber FROM') >= 0

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	// select() / innerJoin
	def "SelectSpec innerJoin2 #connectionSupplier"(ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('innerJoin')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			List<SaleItem> saleItems = []
			List<Sale    > sales     = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(SaleItem, 'SI').connection(it)
					.innerJoin(Sale, 'S', '{S.id} = {SI.saleId}')
					.where('{S.saleDate} = {}', new Date(saleDateStart.timeInMillis))
					.<Sale>select(
						{saleItems << it}, {sales << it}
					)
			}
		/**/DebugTrace.print('sales.size', sales.size())

		then:
			sales.size() == saleItems.size()

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	// select() / innerJoin x 2
	def "SelectSpec innerJoin innerJoin #connectionSupplier"(ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('innerJoin innerJoin')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			List<SaleItem> saleItems = []
			List<Sale    > sales     = []
			List<Contact > contacts  = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(SaleItem, 'SI').connection(it)
					.innerJoin(Sale   , 'S', '{S.id} = {SI.saleId}')
					.innerJoin(Contact, 'C', '{C.id} = {S.contactId}')
					.where('{C.birthday} = {}', new Date(birthdayStart.timeInMillis))
					.<Sale, Contact>select(
						{saleItems << it}, {sales << it}, {contacts << it}
					)
			}
		/**/DebugTrace.print('sales.size', sales.size())

		then:
			sales   .size() == saleItems .size()
			contacts.size() == saleItems .size()

		when:
			saleItems = []
			sales     = []
			def sql = new Sql<>(SaleItem, 'SI')
				.innerJoin(Sale   , 'S', '{S.id} = {SI.saleId}')
				.innerJoin(Contact, 'C', '{C.id} = {S.contactId}')
				.limit(1)
			Transaction.execute(connectionSupplier) {
				sql.connection(it)
					.<Sale>select({saleItems << it}, {sales << it})
			}
		/**/DebugTrace.print('sales.size', sales.size())

		then:
			// No 'C_xxxx' columns
		/**/DebugTrace.print('sql.generatedSql', sql.generatedSql)
			sql.generatedSql.indexOf('S_taxRate FROM') >= 0

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	// select() / innerJoin x 3
	def "SelectSpec innerJoin x 3 #connectionSupplier"(ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('innerJoin x 3')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			List<SaleItem> saleItems = []
			List<Sale    > sales     = []
			List<Contact > contacts  = []
			List<Address > addresses = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(SaleItem, 'SI').connection(it)
					.innerJoin(Sale   , 'S', '{S.id} = {SI.saleId}')
					.innerJoin(Contact, 'C', '{C.id} = {S.contactId}')
					.innerJoin(Address, 'A', '{A.id} = {C.addressId}')
					.where('{A.address3} LIKE {}', '%0-0-0')
					.<Sale, Contact, Address>select(
						{saleItems << it}, {sales << it},
						{contacts << it}, {addresses << it}
					)
			}
		/**/DebugTrace.print('sales.size', sales.size())

		then:
			sales    .size() == saleItems .size()
			contacts .size() == saleItems .size()
			addresses.size() == saleItems .size()

		when:
			saleItems = []
			sales     = []
			contacts  = []
			def sql = new Sql<>(SaleItem, 'SI')
				.innerJoin(Sale   , 'S', '{S.id} = {SI.saleId}')
				.innerJoin(Contact, 'C', '{C.id} = {S.contactId}')
				.innerJoin(Address, 'A', '{A.id} = {C.addressId}')
				.limit(1)
			Transaction.execute(connectionSupplier) {
				sql.connection(it)
					.<Sale, Contact>select(
						{saleItems << it}, {sales << it}, {contacts << it}
					)
			}
		/**/DebugTrace.print('sales.size', sales.size())

		then:
			// No 'A_xxxx' columns
		/**/DebugTrace.print('sql.generatedSql', sql.generatedSql)
			sql.generatedSql.indexOf('C_addressId FROM') >= 0

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	// select() / innerJoin x 3 + leftJoin
	def "SelectSpec innerJoin x 3  leftJoin #connectionSupplier"(ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('innerJoin x 3 leftJoin')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			List<SaleItem> saleItems = []
			List<Sale    > sales     = []
			List<Contact > contacts  = []
			List<Address > addresses = []
			List<Phone   > phones    = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(SaleItem, 'SI').connection(it)
					.innerJoin(Sale   , 'S', '{S.id} = {SI.saleId}')
					.innerJoin(Contact, 'C', '{C.id} = {S.contactId}')
					.innerJoin(Address, 'A', '{A.id} = {C.addressId}')
					.leftJoin (Phone  , 'P', '{P.contactId} = {C.id}')
					.where('{P.phoneNumber} = {}', '09000010000')
					.<Sale, Contact, Address, Phone>select(
						{saleItems << it}, {sales << it},
						{contacts << it}, {addresses << it},
						{phones << it}
					)
			}
		/**/DebugTrace.print('sales.size', sales.size())

		then:
			sales    .size() == saleItems .size()
			contacts .size() == saleItems .size()
			addresses.size() == saleItems .size()
			phones   .size() == saleItems .size()

		when:
			saleItems = []
			sales     = []
			contacts  = []
			addresses = []
			def sql = new Sql<>(SaleItem, 'SI')
				.innerJoin(Sale   , 'S', '{S.id} = {SI.saleId}')
				.innerJoin(Contact, 'C', '{C.id} = {S.contactId}')
				.innerJoin(Address, 'A', '{A.id} = {C.addressId}')
				.leftJoin (Phone  , 'P', '{P.contactId} = {C.id}')
				.limit(1)
			Transaction.execute(connectionSupplier) {
				sql.connection(it)
					.<Sale, Contact, Address, Phone>select(
						{saleItems << it}, {sales << it},
						{contacts << it}, {addresses << it}
					)
			}

		then:
			// No 'P_xxxx' columns
		/**/DebugTrace.print('sql.generatedSql', sql.generatedSql)
			sql.generatedSql.indexOf('A_address4 FROM') >= 0

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	// select() / innerJoin x 3 + leftJoin x 2
	def "SelectSpec innerJoin x 3  leftJoin x 2 #connectionSupplier"(ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('innerJoin x 3 leftJoin x 2')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			List<SaleItem> saleItems = []
			List<Sale    > sales     = []
			List<Contact > contacts  = []
			List<Address > addresses = []
			List<Phone   > phones    = []

		when:
			def sql = new Sql<>(SaleItem, 'SI')
				.innerJoin(Sale   , 'S', '{S.id} = {SI.saleId}')
				.innerJoin(Contact, 'C', '{C.id} = {S.contactId}')
				.innerJoin(Address, 'A', '{A.id} = {C.addressId}')
				.leftJoin (Phone  , 'P1', '{P1.contactId} = {C.id}')
				.leftJoin (Phone  , 'P2', '{P2.contactId} = {C.id}')
				.limit(1)
			Transaction.execute(connectionSupplier) {
				sql.connection(it)
					.<Sale, Contact, Address, Phone>select(
						{saleItems << it}, {sales << it},
						{contacts << it}, {addresses << it},
						{phones << it}
					)
			}

		then:
			// No 'P2_xxxx' columns
		/**/DebugTrace.print('sql.generatedSql', sql.generatedSql)
			sql.generatedSql.indexOf('P1_phoneNumber FROM') >= 0

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	@Table('super')
	public static class ContactPhoneCount extends Phone {
		@Select('COUNT({id})')
		public int count
	}

	// select() / gourpBy
	def "SelectSpec gourpBy #connectionSupplier"(ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('gourpBy')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			List<ContactPhoneCount> phoneCounts = []

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(ContactPhoneCount, 'P').connection(it)
					.innerJoin(Contact, 'C', '{C.id} = {P.contactId}')
					.columns('P.contactId', 'P.count')
					.where('{C.name.first} LIKE {}', '%4')
						.or('{C.name.first} LIKE {}', '%5')
						.or('{C.name.first} LIKE {}', '%6')
					.groupBy('{P.contactId}')
					.having('COUNT({P.id}) >= {}', 6)
					.select({phoneCounts << it})
			}

		then:
			phoneCounts.size() == 10
			phoneCounts[0].count == 6

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	// select() / forUpdate
	def "SelectSpec forUpdate #connectionSupplier"(ConnectionSupplier connectionSupplier) {
		if (connectionSupplier.database instanceof SQLite) return
	/**/DebugTrace.enter()
	/**/DebugTrace.print('forUpdate')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			Contact contact0 = null

		when:
			Transaction.execute(connectionSupplier) {
				contact0 = new Sql<>(Contact).connection(it)
					.limit(1)
					.orderBy('{id}')
					.select().orElse(null)
				Contact contact = new Sql<>(Contact).connection(it)
					.where(contact0)
					.select().orElse(null)
				contact.name.first = '-'
				new Sql<>(Contact).connection(it)
					.columns('name.first', 'updateCount', 'updated')
					.update(contact)
			}
			
		then:
			assert contact0 != null

		when:
			def threads = new Thread[10]
			(0..<threads.length).each {index ->
				threads[index] = new Thread({
					Transaction.execute(connectionSupplier) {
						def myIndex = index
						Contact contact = new Sql<>(Contact).connection(it)
							.where('{id} = {}', contact0.id)
							.forUpdate()
							.select().orElse(null)
						assert contact != null

						contact.name.first  += myIndex

						if (myIndex == 0)
							Thread.sleep(1500L) // sleep 1500ms

						new Sql<>(Contact).connection(it)
							.columns('name.first', 'updateCount', 'updated')
							.update(contact)
					}
				})
				threads[index].start()
				Thread.sleep(100L) // sleep 100ms
			}

		then:
			true

		when:
			// Wait for all threads to finish.
			(0..<threads.length).each {threads[it].join()}

			Contact contact1 = null
			Transaction.execute(connectionSupplier) {
				contact1 = new Sql<>(Contact).connection(it)
					.where('{id} = {}', contact0.id)
					.select().orElse(null)
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
			}

		then:
			true

		cleanup:
			if (contact0 != null)
				Transaction.execute(connectionSupplier) {
					new Sql<>(Contact).connection(it)
					.columns('name.first', 'updateCount', 'updated')
					.update(contact0)
				}

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	// select() / forUpdate noWait
	def "SelectSpec forUpdate noWait #connectionSupplier"(ConnectionSupplier connectionSupplier) {
		if (connectionSupplier.database instanceof DB2) return
		if (connectionSupplier.database instanceof MySQL) return
		if (connectionSupplier.database instanceof PostgreSQL) return
		if (connectionSupplier.database instanceof SQLite) return
	/**/DebugTrace.enter()
	/**/DebugTrace.print('forUpdate noWait')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			Contact contact0 = null

		when:
			Transaction.execute(connectionSupplier) {
				contact0 = new Sql<>(Contact).connection(it)
					.limit(1)
					.orderBy('{id}')
					.select().orElse(null)
				Contact contact = new Sql<>(Contact).connection(it)
					.where(contact0)
					.select().orElse(null)
				contact.name.first = '-'
				new Sql<>(Contact).connection(it)
					.columns('name.first', 'updateCount', 'updated')
					.update(contact)
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
							Contact contact = new Sql<>(Contact).connection(it)
								.where('{id} = {}', contact0.id)
								.forUpdate().noWait()
								.select().orElse(null)

							contact.name.first  += myIndex

							if (myIndex == 0)
								Thread.sleep(1500L) // sleep 1500ms

							new Sql<>(Contact).connection(it)
								.columns('name.first', 'updateCount', 'updated')
								.update(contact)
						}
						catch (RuntimeSQLException e) {
						/**/DebugTrace.print('e', e)
						}
					}
				})
				threads[index].start()
				Thread.sleep(100L) // sleep 100ms
			}

		then:
			true

		when:
			// Wait for all threads to finish.
			(0..<threads.length).each {threads[it].join()}

			Contact contact1 = null
			Transaction.execute(connectionSupplier) {
				contact1 = new Sql<>(Contact).connection(it)
					.where('{id} = {}', contact0.id)
					.select().orElse(null)
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
			}

		then:
			true

		cleanup:
			if (contact0 != null)
				Transaction.execute(connectionSupplier) {
					new Sql<>(Contact).connection(it)
					.columns('name.first', 'updateCount', 'updated')
					.update(contact0)
				}

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	// select() / forUpdate wait N
	def "SelectSpec forUpdate wait N #connectionSupplier"(ConnectionSupplier connectionSupplier) {
		if (connectionSupplier.database instanceof DB2) return
		if (connectionSupplier.database instanceof MySQL) return
		if (connectionSupplier.database instanceof PostgreSQL) return
		if (connectionSupplier.database instanceof SQLite) return
		if (connectionSupplier.database instanceof SQLServer) return
	/**/DebugTrace.enter()
	/**/DebugTrace.print('forUpdate wait N')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			Contact contact0 = null

		when:
			Transaction.execute(connectionSupplier) {
				contact0 = new Sql<>(Contact).connection(it)
					.limit(1)
					.orderBy('{id}')
					.select().orElse(null)
				Contact contact = new Sql<>(Contact).connection(it)
					.where(contact0)
					.select().orElse(null)
				contact.name.first = '-'
				new Sql<>(Contact).connection(it)
					.columns('name.first', 'updateCount', 'updated')
					.update(contact)
			}
			
		then:
			assert contact0 != null

		when:
			def threads = new Thread[10]
			(0..<threads.length).each {index ->
				threads[index] = new Thread({
					Transaction.execute(connectionSupplier) {
						def myIndex = index
						try {
							Contact contact = new Sql<>(Contact).connection(it)
								.where('{id} = {}', contact0.id)
								.forUpdate().wait(1) // wait 1000ms
								.select().orElse(null)

							contact.name.first  += myIndex

							Thread.sleep(1550L) // sleep 1550ms

							new Sql<>(Contact).connection(it)
								.columns('name.first', 'updateCount', 'updated')
								.update(contact)
						}
						catch (RuntimeSQLException e) {
						/**/DebugTrace.print('e', e)
						}
					}
				})
				threads[index].start()
				Thread.sleep(100L) // sleep 100ms
			}

		then:
			true

		when:
			// Wait for all threads to finish.
			(0..<threads.length).each {threads[it].join()}

			Contact contact1 = null
			Transaction.execute(connectionSupplier) {
				contact1 = new Sql<>(Contact).connection(it)
					.where('{id} = {}', contact0.id)
					.select().orElse(null)
				assert contact1 != null
				assert contact1.name.first.indexOf('0') >= 0    //   0~1550ms
				assert contact1.name.first.indexOf('1') == -1   // 100~1100ms
				assert contact1.name.first.indexOf('2') == -1   // 200~1200ms
				assert contact1.name.first.indexOf('3') == -1   // 300~1300ms
				assert contact1.name.first.indexOf('4') == -1   // 400~1400ms
				assert contact1.name.first.indexOf('5') >= 0 || // 500~1500ms
				       contact1.name.first.indexOf('6') >= 0 || // 600~1600ms
				       contact1.name.first.indexOf('7') >= 0    // 700~1700ms
				assert contact1.name.first.indexOf('8') == -1   // 800~1800ms
				assert contact1.name.first.indexOf('9') == -1   // 900~1900ms
			}

		then:
			true

		cleanup:
			if (contact0 != null)
				Transaction.execute(connectionSupplier) {
					new Sql<>(Contact).connection(it)
					.columns('name.first', 'updateCount', 'updated')
					.update(contact0)
				}
			Thread.sleep(1000)

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	// select() / forUpdate - exception
	def "SelectSpec forUpdate - exception #connectionSupplier"(ConnectionSupplier connectionSupplier) {
		if (connectionSupplier.database instanceof DB2) return
		if (connectionSupplier.database instanceof MySQL) return
		if (connectionSupplier.database instanceof Oracle) return
		if (connectionSupplier.database instanceof PostgreSQL) return
		if (connectionSupplier.database instanceof SQLServer) return
	/**/DebugTrace.enter()
	/**/DebugTrace.print('forUpdate - exception')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			Contact contact0 = null

		when:
			Transaction.execute(connectionSupplier) {
				contact0 = new Sql<>(Contact).connection(it)
					.limit(1)
					.orderBy('{id}')
					.select().orElse(null)
			}
		/**/DebugTrace.print('1 contact0', contact0)
			
		then:
			assert contact0 != null

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact).connection(it)
					.where('{id} = {}', contact0.id)
					.forUpdate()
					.select()
			}

		then:
			def e = thrown UnsupportedOperationException
			e.message.indexOf('forUpdate') >= 0

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSupplier
	}

	// select() / forUpdate noWait - exception
	def "SelectSpec forUpdate noWait - exception #connectionSupplier"(ConnectionSupplier connectionSupplier) {
		if (connectionSupplier.database instanceof Oracle) return
		if (connectionSupplier.database instanceof SQLite) return
		if (connectionSupplier.database instanceof SQLServer) return
	/**/DebugTrace.enter()
	/**/DebugTrace.print('forUpdate noWait - exception')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			Contact contact0 = null

		when:
			Transaction.execute(connectionSupplier) {
				contact0 = new Sql<>(Contact).connection(it)
					.limit(1)
					.orderBy('{id}')
					.select().orElse(null)
			}
		/**/DebugTrace.print('1 contact0', contact0)
			
		then:
			assert contact0 != null

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact).connection(it)
					.where('{id} = {}', contact0.id)
					.forUpdate().noWait()
					.select()
			}

		then:
			def e = thrown UnsupportedOperationException
			e.message.indexOf('noWait') >= 0

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSupplier
	}

	// select() / forUpdate wait N - exception
	def "SelectSpec forUpdate wait N - exception #connectionSupplier"(ConnectionSupplier connectionSupplier) {
		if (connectionSupplier.database instanceof Oracle) return
		if (connectionSupplier.database instanceof SQLite) return
	/**/DebugTrace.enter()
	/**/DebugTrace.print('forUpdate noWait N - exception')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			Contact contact0 = null

		when:
			Transaction.execute(connectionSupplier) {
				contact0 = new Sql<>(Contact).connection(it)
					.limit(1)
					.orderBy('{id}')
					.select().orElse(null)
			}
		/**/DebugTrace.print('1 contact0', contact0)
			
		then:
			assert contact0 != null

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact).connection(it)
					.where('{id} = {}', contact0.id)
					.forUpdate().wait(5)
					.select()
			}

		then:
			def e = thrown UnsupportedOperationException
			e.message.indexOf('wait N') >= 0

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSupplier
	}

	// exceptionTest
	def "SelectSpec exception - ManyRowsException"() {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('exception - ManyRowsException')

		when:
			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact).connection(it)
					.where('{name.last} LIKE {}', 'Last2%')
					.select()
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
	def "SelectSpec extends class #connectionSupplier"(ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('extends Class')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		when:
			Class<? extends ContactFn> contactClass =
				connectionSupplier.database instanceof DB2        ? ContactFnDB2        :
				connectionSupplier.database instanceof MySQL      ? ContactFnMySQL      :
				connectionSupplier.database instanceof Oracle     ? ContactFnOracle     :
				connectionSupplier.database instanceof PostgreSQL ? ContactFnPostgreSQL :
				connectionSupplier.database instanceof SQLite     ? ContactFnSQLite     :
				connectionSupplier.database instanceof SQLServer  ? ContactFnSQLServer  : null

		then:
			contactClass != null

		when:
			ContactFn contact = null
			Transaction.execute(connectionSupplier) {
				contact = new Sql<>(contactClass).connection(it)
					.columns('fullName')
					.limit(1)
					.select().orElse(null)
			}
		/**/DebugTrace.print('contact', contact)

		then:
			contact != null

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}

	static class ContactName {
		final PersonName name = new PersonName()
	}

	// selectAs
	def "SelectSpec selectAs #connectionSupplier"(ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('selectBy')
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			List<Contact> contacts = []
			List<ContactName> contactNames = []

		// selectAs(ContactName, Consumer)
		when:
			def sql1 = new Sql<>(Contact).orderBy('{id}').limit(5)
			def sql2 = new Sql<>(Contact).orderBy('{id}').limit(5)
			Transaction.execute(connectionSupplier) {
				sql1.connection(it).select({contacts << it})
				sql2.connection(it).selectAs(ContactName,  {contactNames  << it})
			}

		then:
		/**/DebugTrace.print('contacts', contacts)
		/**/DebugTrace.print('contactNames', contactNames)
			contactNames*.name*.first == contacts*.name*.first
			contactNames*.name*.last == contacts*.name*.last
			sql2.generatedSql.indexOf('SELECT firstName, lastName FROM') == 0

		// selectAs(ContactName)
		when:
			Optional<Contact> contactOpt = null
			Optional<ContactName> contactNameOpt = null
			sql1 = new Sql<>(Contact).orderBy('{id}').limit(1)
			sql2 = new Sql<>(Contact).orderBy('{id}').limit(1)
			Transaction.execute(connectionSupplier) {
				contactOpt     = sql1.connection(it).select()
				contactNameOpt = sql2.connection(it).selectAs(ContactName)
			}

		then:
		/**/DebugTrace.print('contactOpt', contactOpt)
		/**/DebugTrace.print('contactNameOpt', contactNameOpt)
			contactNameOpt.get().name.first == contactOpt.get().name.first
			contactNameOpt.get().name.last == contactOpt.get().name.last
			sql2.generatedSql.indexOf('SELECT firstName, lastName FROM') == 0

	/**/DebugTrace.leave()
		where:
			connectionSupplier << connectionSuppliers
	}
}
