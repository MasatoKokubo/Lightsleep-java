// JavaDocExamples.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy

import java.util.ArrayList
import java.util.List
import java.util.Optional

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.Condition
import org.lightsleep.database.SQLite
import org.lightsleep.example.Common
import org.lightsleep.example.groovy.entity.*

/**
 * JavaDocExamples
 */
public class JavaDocExamples extends Common {
	public static void main(String[] args) {
		try {
			init1()
			init2()
			transaction()
			sql()
			sql_columns1()
			sql_columns2()
			sql_expression()
			sql_innerJoin()
			sql_leftJoin()
			sql_rightJoin()
			sql_where1()
			sql_where2()
			sql_where3()
			sql_where4()
			sql_and()
			sql_or()
			sql_orderBy()
			sql_asc()
			sql_desc()
			sql_limit()
			sql_offset()
			sql_doIf()
			sql_select1()
			sql_selectAs1()
			sql_select2()
			sql_select3()
			sql_select4()
			sql_select5()
			sql_select6()
			sql_selectAs2()
			sql_selectCount()
			sql_insert1()
			sql_insert2()
			sql_update1()
			sql_update2()
			sql_delete1()
			sql_delete2()
			sql_delete3()
		}
		catch (Exception e) {
			e.printStackTrace()
		}
	}

	// Transaction
	static void transaction() {
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
	static void sql() {
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
	static void sql_columns1() {
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
	static void sql_columns2() {
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
	static void sql_expression() {
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
	static void sql_innerJoin() {
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
	static void sql_leftJoin() {
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
	static void sql_rightJoin() {
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
	static void sql_where1() {
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
	static void sql_where2() {
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
	static void sql_where3() {
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
	static void sql_where4() {
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
	static void sql_and() {
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
	static void sql_or() {
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
	static void sql_orderBy() {
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
	static void sql_asc() {
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
	static void sql_desc() {
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
	static void sql_limit() {
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
	static void sql_offset() {
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
	static void sql_doIf() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 Transaction.execute {
     new Sql<>(Contact, 'C')
         .doIf(!(Sql.database instanceof SQLite)) {it.forUpdate}
         .connection(it)
         .select({contacts << it})
 }

	/**/DebugTrace.leave()
	}

	// select 1
	static void sql_select1() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 Transaction.execute {
     new Sql<>(Contact)
         .connection(it)
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
     new Sql<>(Contact)
         .connection(it)
         .selectAs(ContactName, {contactNames << it})
 }

 /**/DebugTrace.print("contactNames", contactNames);
	/**/DebugTrace.leave();
	}

	// select 2
	static void sql_select2() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 List<Phone>   phones = []
 Transaction.execute {
     new Sql<>(Contact, 'C')
         .innerJoin(Phone, 'P', '{P.contactId} = {C.id}')
         .connection(it)
         .select({contacts << it}, {phones << it})
 }

	/**/DebugTrace.leave()
	}

	// select 3
	static void sql_select3() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 List<Phone>   phones = []
 List<Email>   emails = []
 Transaction.execute {
     new Sql<>(Contact, 'C')
         .innerJoin(Phone, 'P', '{P.contactId} = {C.id}')
         .innerJoin(Email, 'E', '{E.contactId} = {C.id}')
         .connection(it)
         .select({contacts << it}, {phones << it}, {emails << it})
 }

	/**/DebugTrace.leave()
	}

	// select 4
	static void sql_select4() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 List<Phone>   phones = []
 List<Email>   emails = []
 List<Address> addresses = []
 Transaction.execute {
     new Sql<>(Contact, 'C')
         .innerJoin(Phone  , 'P', '{P.contactId} = {C.id}')
         .innerJoin(Email  , 'E', '{E.contactId} = {C.id}')
         .innerJoin(Address, 'A', '{A.contactId} = {C.id}')
         .connection(it)
         .select(
             {contacts << it}, {phones << it}, {emails << it}, {addresses << it})
 }

	/**/DebugTrace.leave()
	}

	// select 5
	static void sql_select5() {
	/**/DebugTrace.enter()

 List<Contact> contacts = []
 List<Phone>   phones = []
 List<Email>   emails = []
 List<Address> addresses = []
 List<Url>     urls = []
 Transaction.execute {
     new Sql<>(Contact, 'C')
         .innerJoin(Phone  , 'P', '{P.contactId} = {C.id}')
         .innerJoin(Email  , 'E', '{E.contactId} = {C.id}')
         .innerJoin(Address, 'A', '{A.contactId} = {C.id}')
         .innerJoin(Url    , 'U', '{U.contactId} = {C.id}')
         .connection(it)
         .select(
             {contacts << it}, {phones << it}, {emails << it},
             {addresses << it}, {urls << it})
 }

	/**/DebugTrace.leave()
	}

	// select 6
	static void sql_select6() {
	/**/DebugTrace.enter()

 Contact.Ex contact
 Transaction.execute {
     contact = new Sql<>(Contact.Ex.targetClass)
         .where('{id} = {}', 1)
         .connection(it)
         .select().orElse(null)
 }

	/**/DebugTrace.print("contact", contact)
	/**/DebugTrace.leave()
	}

	// select As 2
	static void sql_selectAs2() {
	/**/DebugTrace.enter()

 ContactName contactName
 Transaction.execute {
     contactName = new Sql<>(Contact)
         .where('{id}={}', 1)
         .connection(it)
         .selectAs(ContactName).orElse(null)
 }

/**/DebugTrace.print("contactName", contactName);
	/**/DebugTrace.leave()
	}

	// selectCount
	static void sql_selectCount() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Contact)
         .connection(it)
         .selectCount()
 }

	/**/DebugTrace.leave()
	}

	// insert 1
	static void sql_insert1() {
	/**/DebugTrace.enter()

		Transaction.execute {
			new Sql<>(Contact).where('{id} = {}', 6)
			.connection(it)
			.delete()
		}

 int count
 Transaction.execute {
     count = new Sql<>(Contact)
         .connection(it)
         .insert(new Contact(6, 'Setoka', 'Orange', 2001, 2, 1))
 }

	/**/DebugTrace.leave()
	}

	// insert 2
	static void sql_insert2() {
	/**/DebugTrace.enter()

		Transaction.execute {
			new Sql<>(Contact).where('{id} IN {}', [7, 8, 9])
			.connection(it)
			.delete()
		}

 int count
 Transaction.execute {
     count = new Sql<>(Contact)
         .connection(it)
         .insert([
             new Contact(7, 'Harumi', 'Orange', 2001, 2, 2),
             new Contact(8, 'Mihaya', 'Orange', 2001, 2, 3),
             new Contact(9, 'Asumi' , 'Orange', 2001, 2, 4)
         ])
 }

	/**/DebugTrace.leave()
	}

	// update 1
	static void sql_update1() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Contact)
         .connection(it)
         .update(new Contact(6, 'Setoka', 'Orange', 2017, 2, 1))
 }

	/**/DebugTrace.leave()
	}

	// update 2
	static void sql_update2() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Contact)
         .connection(it)
         .update([
             new Contact(7, 'Harumi', 'Orange', 2017, 2, 2),
             new Contact(8, 'Mihaya', 'Orange', 2017, 2, 3),
             new Contact(9, 'Asumi' , 'Orange', 2017, 2, 4)
         ])
 }

	/**/DebugTrace.leave()
	}

	// delete 1
	static void sql_delete1() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Contact)
         .where(Condition.ALL)
         .connection(it)
         .delete()
 }

	/**/DebugTrace.leave()
	}

	// delete 2
	static void sql_delete2() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Contact)
         .connection(it)
         .delete(new Contact(6))
 }

		Transaction.execute {
			count = new Sql<>(Contact)
				.connection(it)
				.insert(new Contact(6, 'Setoka', 'Orange', 2001, 2, 1))
		}

	/**/DebugTrace.leave()
	}

	// delete 3
	static void sql_delete3() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Contact)
         .connection(it)
         .delete([new Contact(7), new Contact(8), new Contact(9)])
 }

	/**/DebugTrace.print('count', count)

	Transaction.execute {
		new Sql<>(Contact)
			.connection(it)
			.insert([
				new Contact(7, 'Harumi', 'Orange', 2001, 2, 2),
				new Contact(8, 'Mihaya', 'Orange', 2001, 2, 3),
				new Contact(9, 'Asumi' , 'Orange', 2001, 2, 4)
			])
	}

	/**/DebugTrace.leave()
	}

}
