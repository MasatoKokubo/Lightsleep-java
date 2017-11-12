// JavaDocExamples.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy

import java.util.ArrayList
import java.util.List
import java.util.Optional

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.connection.*
import org.lightsleep.database.*
import org.lightsleep.example.Common
import org.lightsleep.example.groovy.entity.*

/**
 * JavaDocExamples
 */
public class JavaDocExamples extends Common {
	public static void main(String[] args) {
		def examples = new JavaDocExamples()
		try {
			examples.init1()
			examples.init2()
			examples.transaction()
			examples.sql()
			examples.sql_columns1()
			examples.sql_columns2()
			examples.sql_expression()
			examples.sql_innerJoin()
			examples.sql_leftJoin()
			examples.sql_rightJoin()
			examples.sql_where1()
			examples.sql_where2()
			examples.sql_where3()
			examples.sql_where4()
			examples.sql_and()
			examples.sql_or()
			examples.sql_orderBy()
			examples.sql_asc()
			examples.sql_desc()
			examples.sql_limit()
			examples.sql_offset()
			examples.sql_doIf()
			examples.sql_select1()
			examples.sql_selectAs1()
			examples.sql_select2()
			examples.sql_select3()
			examples.sql_select4()
			examples.sql_select5()
			examples.sql_select6()
			examples.sql_selectAs2()
			examples.sql_selectCount()
			examples.sql_insert1()
			examples.sql_insert2()
			examples.sql_update1()
			examples.sql_update2()
			examples.sql_delete1()
			examples.sql_delete2()
			examples.sql_delete3()
		}
		catch (Exception e) {
			e.printStackTrace()
		}
	}

	// Transaction
	void transaction() {
	/**/DebugTrace.enter()

 Transaction.execute {
     def contactOpt = new Sql<>(Contact)
         .where('{id} = {}', 1)
         .connection(it)
         .select()
     contactOpt.ifPresent {Contact contact ->
         contact.setBirthday(2017, 1, 1)
         new Sql<>(Contact)
             .connection(it)
             .update(contact)
     }
 }

	/**/DebugTrace.leave()
	}

	// Sql
	void sql() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 Transaction.execute {
     new Sql<>(Contact)
         .where('{lastName} = {}', 'Apple')
         .connection(it)
         .select({contacts << it})
 }

	/**/DebugTrace.leave()
	}

	// columns 1
	void sql_columns1() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 Transaction.execute {
     new Sql<>(Contact)
         .columns('lastName', 'firstName')
         .where('{lastName} = {}', 'Apple')
         .connection(it)
         .select({contacts << it})
 }

	/**/DebugTrace.leave()
	}

	// columns 2
	void sql_columns2() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 List<Phone> phones = []
 Transaction.execute {
     new Sql<>(Contact, 'C')
         .innerJoin(Phone, 'P', '{P.contactId} = {C.id}')
         .columns('C.id', 'P.*')
         .connection(it)
         .<Phone>select({contacts << it}, {phones << it})
 }

	/**/DebugTrace.leave()
	}

	// expression
	void sql_expression() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 Transaction.execute {
     new Sql<>(Contact)
         .expression('firstName', "'['||{firstName}||']'")
         .where('{lastName} = {}', 'Orange')
         .connection(it)
         .select({contacts << it})
 }

	/**/DebugTrace.leave()
	}

	// innerJoin
	void sql_innerJoin() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 List<Phone> phones = []
 Transaction.execute {
     new Sql<>(Contact, 'C')
         .innerJoin(Phone, 'P', '{P.contactId} = {C.id}')
         .connection(it)
         .<Phone>select({contacts << it}, {phones << it})
 }

	/**/DebugTrace.leave()
	}

	// leftJoin
	void sql_leftJoin() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 List<Phone> phones = []
 Transaction.execute {
     new Sql<>(Contact, 'C')
         .leftJoin(Phone, 'P', '{P.contactId} = {C.id}')
         .connection(it)
         .<Phone>select({contacts << it}, {phones << it})
 }

	/**/DebugTrace.leave()
	}

	// rightJoin
	void sql_rightJoin() {
		if (ConnectionSupplier.find().database instanceof SQLite) return // SQLite dose not support RIGHT JOIN
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 List<Phone> phones = []
 Transaction.execute {
     new Sql<>(Contact, 'C')
         .rightJoin(Phone, 'P', '{P.contactId} = {C.id}')
         .connection(it)
         .<Phone>select({contacts << it}, {phones << it})
 }

	/**/DebugTrace.leave()
	}

	// where 1
	void sql_where1() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 Transaction.execute {
     new Sql<>(Contact)
         .where('{birthday} IS NULL')
         .connection(it)
         .select({contacts << it})
 }

	/**/DebugTrace.leave()
	}

	// where 2
	void sql_where2() {
	/**/DebugTrace.enter()

 int id = 1
 Contact contact
 Transaction.execute {
     contact = new Sql<>(Contact)
         .where('{id} = {}', id)
         .connection(it)
         .select().orElse(null)
 }

	/**/DebugTrace.leave()
	}

	// where 3
	void sql_where3() {
	/**/DebugTrace.enter()

 Contact contact
 Transaction.execute {
     Contact key = new Contact()
     contact = new Sql<>(Contact)
         .where(new ContactKey(2))
         .connection(it)
         .select().orElse(null)
 }

	/**/DebugTrace.leave()
	}

	// where 4
	void sql_where4() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 Transaction.execute {
     new Sql<>(Contact, 'C')
         .where('EXISTS',
              new Sql<>(Phone, 'P')
                  .where('{P.contactId} = {C.id}')
                  .and('{P.content} LIKE {}', '0800001%')
         )
         .connection(it)
         .select({contacts << it})
 }

	/**/DebugTrace.leave()
	}

	// and
	void sql_and() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 Transaction.execute {
     new Sql<>(Contact)
         .where('{lastName} = {}', 'Apple')
         .and('{firstName} = {}', 'Akiyo')
         .connection(it)
         .select({contacts << it})
 }

	/**/DebugTrace.leave()
	}

	// or
	void sql_or() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 Transaction.execute {
     new Sql<>(Contact)
         .where('{lastName} = {}', 'Apple')
         .or('{lastName} = {}', 'Orange')
         .connection(it)
         .select({contacts << it})
 }

	/**/DebugTrace.leave()
	}

	// orderBy
	void sql_orderBy() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 Transaction.execute {
     new Sql<>(Contact)
         .orderBy('{lastName}')
         .orderBy('{firstName}')
         .connection(it)
         .select({contacts << it})
 }

	/**/DebugTrace.leave()
	}

	// asc
	void sql_asc() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 Transaction.execute {
     new Sql<>(Contact)
         .orderBy('{id}').asc()
         .connection(it)
         .select({contacts << it})
 }

	/**/DebugTrace.leave()
	}

	// desc
	void sql_desc() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 Transaction.execute {
     new Sql<>(Contact)
         .orderBy('{id}').desc()
         .connection(it)
         .select({contacts << it})
 }

	/**/DebugTrace.leave()
	}

	// limit
	void sql_limit() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 Transaction.execute {
     new Sql<>(Contact)
         .limit(5)
         .connection(it)
         .select({contacts << it})
 }

	/**/DebugTrace.leave()
	}

	// offset
	void sql_offset() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 Transaction.execute {
     new Sql<>(Contact)
         .limit(5).offset(5)
         .connection(it)
         .select({contacts << it})
 }

	/**/DebugTrace.leave()
	}

	// doIf
	void sql_doIf() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 Transaction.execute {
 /**/DebugTrace.print("it", it)
     def isSQLite = it.database instanceof SQLite
     new Sql<>(Contact, 'C').connection(it)
         .doIf(!isSQLite) {it.forUpdate} // SQLite dose not support FOR UPDATE
         .select({contacts << it})
 }

	/**/DebugTrace.leave()
	}

	// select 1
	void sql_select1() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 Transaction.execute {
     new Sql<>(Contact).connection(it)
         .select({contacts << it})
 }

	/**/DebugTrace.leave()
	}

	// select As 1
 static class ContactName {
     String firstName
     String lastName
 }
	private static void sql_selectAs1() {
	/**/DebugTrace.enter();
 List<ContactName> contactNames = []
 Transaction.execute {
     new Sql<>(Contact).connection(it)
         .selectAs(ContactName, {contactNames << it})
 }

 /**/DebugTrace.print("contactNames", contactNames);
	/**/DebugTrace.leave();
	}

	// select 2
	void sql_select2() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 List<Phone>   phones = []
 Transaction.execute {
     new Sql<>(Contact, 'C').connection(it)
         .innerJoin(Phone, 'P', '{P.contactId} = {C.id}')
         .select({contacts << it}, {phones << it})
 }

	/**/DebugTrace.leave()
	}

	// select 3
	void sql_select3() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 List<Phone>   phones = []
 List<Email>   emails = []
 Transaction.execute {
     new Sql<>(Contact, 'C').connection(it)
         .innerJoin(Phone, 'P', '{P.contactId} = {C.id}')
         .innerJoin(Email, 'E', '{E.contactId} = {C.id}')
         .select({contacts << it}, {phones << it}, {emails << it})
 }

	/**/DebugTrace.leave()
	}

	// select 4
	void sql_select4() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 List<Phone>   phones = []
 List<Email>   emails = []
 List<Address> addresses = []
 Transaction.execute {
     new Sql<>(Contact, 'C').connection(it)
         .innerJoin(Phone  , 'P', '{P.contactId} = {C.id}')
         .innerJoin(Email  , 'E', '{E.contactId} = {C.id}')
         .innerJoin(Address, 'A', '{A.contactId} = {C.id}')
         .select(
             {contacts << it}, {phones << it}, {emails << it}, {addresses << it})
 }

	/**/DebugTrace.leave()
	}

	// select 5
	void sql_select5() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 List<Phone>   phones = []
 List<Email>   emails = []
 List<Address> addresses = []
 List<Url>     urls = []
 Transaction.execute {
     new Sql<>(Contact, 'C').connection(it)
         .innerJoin(Phone  , 'P', '{P.contactId} = {C.id}')
         .innerJoin(Email  , 'E', '{E.contactId} = {C.id}')
         .innerJoin(Address, 'A', '{A.contactId} = {C.id}')
         .innerJoin(Url    , 'U', '{U.contactId} = {C.id}')
         .select(
             {contacts << it}, {phones << it}, {emails << it},
             {addresses << it}, {urls << it})
 }

	/**/DebugTrace.leave()
	}

	// select 6
	void sql_select6() {
	/**/DebugTrace.enter()

 Contact.Ex contact
 Transaction.execute {
     contact = new Sql<>(Contact.Ex.targetClass(it.database)).connection(it)
         .where('{id} = {}', 1)
         .select().orElse(null)
 }

	/**/DebugTrace.print("contact", contact)
	/**/DebugTrace.leave()
	}

	// select As 2
	void sql_selectAs2() {
	/**/DebugTrace.enter()

 ContactName contactName
 Transaction.execute {
     contactName = new Sql<>(Contact).connection(it)
         .where('{id}={}', 1)
         .selectAs(ContactName).orElse(null)
 }

/**/DebugTrace.print("contactName", contactName);
	/**/DebugTrace.leave()
	}

	// selectCount
	void sql_selectCount() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Contact).connection(it)
         .selectCount()
 }

	/**/DebugTrace.leave()
	}

	// insert 1
	void sql_insert1() {
	/**/DebugTrace.enter()

		Transaction.execute {
			new Sql<>(Contact).connection(it)
			.where('{id} = {}', 6)
			.delete()
		}

 int count
 Transaction.execute {
     count = new Sql<>(Contact).connection(it)
         .insert(new Contact(6, 'Setoka', 'Orange', 2001, 2, 1))
 }

	/**/DebugTrace.leave()
	}

	// insert 2
	void sql_insert2() {
	/**/DebugTrace.enter()

		Transaction.execute {
			new Sql<>(Contact).connection(it)
				.where('{id} IN {}', [7, 8, 9])
				.delete()
		}

 int count
 Transaction.execute {
     count = new Sql<>(Contact).connection(it)
         .insert([
             new Contact(7, 'Harumi', 'Orange', 2001, 2, 2),
             new Contact(8, 'Mihaya', 'Orange', 2001, 2, 3),
             new Contact(9, 'Asumi' , 'Orange', 2001, 2, 4)
         ])
 }

	/**/DebugTrace.leave()
	}

	// update 1
	void sql_update1() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Contact).connection(it)
         .update(new Contact(6, 'Setoka', 'Orange', 2017, 2, 1))
 }

	/**/DebugTrace.leave()
	}

	// update 2
	void sql_update2() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Contact).connection(it)
         .update([
             new Contact(7, 'Harumi', 'Orange', 2017, 2, 2),
             new Contact(8, 'Mihaya', 'Orange', 2017, 2, 3),
             new Contact(9, 'Asumi' , 'Orange', 2017, 2, 4)
         ])
 }

	/**/DebugTrace.leave()
	}

	// delete 1
	void sql_delete1() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Contact).connection(it)
         .where(Condition.ALL)
         .delete()
 }

	/**/DebugTrace.leave()
	}

	// delete 2
	void sql_delete2() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Contact).connection(it)
         .delete(new Contact(6))
 }

		Transaction.execute {
			count = new Sql<>(Contact).connection(it)
				.insert(new Contact(6, 'Setoka', 'Orange', 2001, 2, 1))
		}

	/**/DebugTrace.leave()
	}

	// delete 3
	void sql_delete3() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Contact).connection(it)
         .delete([new Contact(7), new Contact(8), new Contact(9)])
 }

	/**/DebugTrace.print('count', count)

	Transaction.execute {
		new Sql<>(Contact).connection(it)
			.insert([
				new Contact(7, 'Harumi', 'Orange', 2001, 2, 2),
				new Contact(8, 'Mihaya', 'Orange', 2001, 2, 3),
				new Contact(9, 'Asumi' , 'Orange', 2001, 2, 4)
			])
	}

	/**/DebugTrace.leave()
	}

}
