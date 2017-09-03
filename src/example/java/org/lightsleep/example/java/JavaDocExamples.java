// JavaDocExamples.java
// (C) 2015 Masato Kokubo

package org.lightsleep.example.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.debugtrace.DebugTrace;
import org.lightsleep.*;
import org.lightsleep.component.Condition;
import org.lightsleep.database.SQLite;
import org.lightsleep.example.Common;
import org.lightsleep.example.java.entity.*;

/**
 * JavaDocExamples
 */
public class JavaDocExamples extends Common {
	public static void main(String[] args) {
		try {
			init1();
			init2();
			transaction();
			sql();
			sql_columns1();
			sql_columns2();
			sql_expression();
			sql_innerJoin();
			sql_leftJoin();
			sql_rightJoin();
			sql_where1();
			sql_where2();
			sql_where3();
			sql_where4();
			sql_and();
			sql_or();
			sql_orderBy();
			sql_asc();
			sql_desc();
			sql_limit();
			sql_offset();
			sql_doIf();
			sql_select1();
			sql_selectAs1();
			sql_select2();
			sql_select3();
			sql_select4();
			sql_select5();
			sql_select6();
			sql_selectAs2();
			sql_selectCount();
			sql_insert1();
			sql_insert2();
			sql_update1();
			sql_update2();
			sql_delete1();
			sql_delete2();
			sql_delete3();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Transaction
	private static void transaction() {
	/**/DebugTrace.enter();

 Transaction.execute(conn -> {
     Optional<Contact> contactOpt = new Sql<>(Contact.class)
         .where("{id}={}", 1)
         .connection(conn)
         .select();
     contactOpt.ifPresent(contact -> {
         contact.setBirthday(2017, 1, 1);
         new Sql<>(Contact.class)
         	.connection(conn)
            .update(contact);
     });
 });

	/**/DebugTrace.leave();
	}

	// Sql
	private static void sql() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class)
         .where("{lastName}={}", "Apple")
         .connection(conn)
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// columns 1
	private static void sql_columns1() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class)
         .columns("lastName", "firstName")
         .where("{lastName}={}", "Apple")
         .connection(conn)
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// columns 2
	private static void sql_columns2() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 List<Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C")
         .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
         .columns("C.id", "P.*")
         .connection(conn)
         .<Phone>select(contacts::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// expression
	private static void sql_expression() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class)
         .expression("firstName", "'['||{firstName}||']'")
         .where("{lastName}={}", "Orange")
         .connection(conn)
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// innerJoin
	private static void sql_innerJoin() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 List<Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C")
         .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
         .connection(conn)
         .<Phone>select(contacts::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// leftJoin
	private static void sql_leftJoin() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 List<Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C")
         .leftJoin(Phone.class, "P", "{P.contactId}={C.id}")
         .connection(conn)
         .<Phone>select(contacts::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// rightJoin
	private static void sql_rightJoin() {
		if (Sql.getDatabase() instanceof SQLite) return; // SQLite
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 List<Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C")
         .rightJoin(Phone.class, "P", "{P.contactId}={C.id}")
         .connection(conn)
         .<Phone>select(contacts::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// where 1
	private static void sql_where1() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class)
         .where("{birthday} IS NULL")
         .connection(conn).select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// where 2
	private static void sql_where2() {
	/**/DebugTrace.enter();

 int id = 1;
 Contact[] contact = new Contact[1];
 Transaction.execute(conn -> {
     contact[0] = new Sql<>(Contact.class)
         .where("{id}={}", id)
         .connection(conn).select().orElse(null);
 });

	/**/DebugTrace.leave();
	}

	// where 3
	private static void sql_where3() {
	/**/DebugTrace.enter();

 Contact[] contact = new Contact[1];
 Transaction.execute(conn -> {
     contact[0] = new Sql<>(Contact.class)
         .where(new ContactKey(2))
         .connection(conn).select().orElse(null);
 });

	/**/DebugTrace.leave();
	}

	// where 4
	private static void sql_where4() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C")
         .where("EXISTS",
              new Sql<>(Phone.class, "P")
                  .where("{P.contactId}={C.id}")
                  .and("{P.content} LIKE {}", "0800001%")
         )
         .connection(conn)
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// and
	private static void sql_and() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class)
         .where("{lastName}={}", "Apple")
         .and("{firstName}={}", "Akiyo")
         .connection(conn)
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// or
	private static void sql_or() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class)
         .where("{lastName}={}", "Apple")
         .or("{lastName}={}", "Orange")
         .connection(conn)
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// orderBy
	private static void sql_orderBy() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class)
         .orderBy("{lastName}")
         .orderBy("{firstName}")
         .connection(conn)
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// asc
	private static void sql_asc() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class)
         .orderBy("{id}").asc()
         .connection(conn)
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// desc
	private static void sql_desc() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class)
         .orderBy("{id}").desc()
         .connection(conn)
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// limit
	private static void sql_limit() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class)
         .limit(5)
         .connection(conn)
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// offset
	private static void sql_offset() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class)
         .limit(5).offset(5)
         .connection(conn)
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// doIf
	private static void sql_doIf() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C")
         .doIf(!(Sql.getDatabase() instanceof SQLite), Sql::forUpdate)
         .connection(conn)
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// select 1
	private static void sql_select1() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class)
         .connection(conn)
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// select As 1
 public static class ContactName {
     public String firstName;
     public String lastName;
 }
	private static void sql_selectAs1() {
	/**/DebugTrace.enter();

 List<ContactName> contactNames = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class)
         .connection(conn)
         .selectAs(ContactName.class, contactNames::add);
 });

 /**/DebugTrace.print("contactNames", contactNames);
	/**/DebugTrace.leave();
	}

	// select 2
	private static void sql_select2() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 List<Phone>   phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C")
         .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
         .connection(conn)
         .<Phone>select(contacts::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// select 3
	private static void sql_select3() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 List<Phone>   phones = new ArrayList<>();
 List<Email>   emails = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C")
         .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
         .innerJoin(Email.class, "E", "{E.contactId}={C.id}")
         .connection(conn)
         .<Phone, Email>select(contacts::add, phones::add, emails::add);
 });

	/**/DebugTrace.leave();
	}

	// select 4
	private static void sql_select4() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 List<Phone>   phones = new ArrayList<>();
 List<Email>   emails = new ArrayList<>();
 List<Address> addresses = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C")
         .innerJoin(  Phone.class, "P", "{P.contactId}={C.id}")
         .innerJoin(  Email.class, "E", "{E.contactId}={C.id}")
         .innerJoin(Address.class, "A", "{A.contactId}={C.id}")
         .connection(conn)
         .<Phone, Email, Address>select(
             contacts::add, phones::add, emails::add, addresses::add);
 });

	/**/DebugTrace.leave();
	}

	// select 5
	private static void sql_select5() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 List<Phone>   phones = new ArrayList<>();
 List<Email>   emails = new ArrayList<>();
 List<Address> addresses = new ArrayList<>();
 List<Url>     urls = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C")
         .innerJoin(  Phone.class, "P", "{P.contactId}={C.id}")
         .innerJoin(  Email.class, "E", "{E.contactId}={C.id}")
         .innerJoin(Address.class, "A", "{A.contactId}={C.id}")
         .innerJoin(    Url.class, "U", "{U.contactId}={C.id}")
         .connection(conn)
         .<Phone, Email, Address, Url>select(
             contacts::add, phones::add, emails::add,
             addresses::add, urls::add);
 });

	/**/DebugTrace.leave();
	}

	// select 6
	private static void sql_select6() {
	/**/DebugTrace.enter();

 Contact.Ex[] contact = new Contact.Ex[1];
 Transaction.execute(conn -> {
     contact[0] = new Sql<>(Contact.Ex.targetClass())
         .where("{id}={}", 1)
         .connection(conn)
         .select().orElse(null);
 });

	/**/DebugTrace.print("contact", contact[0]);
	/**/DebugTrace.leave();
	}

	// select As 2
	private static void sql_selectAs2() {
	/**/DebugTrace.enter();

 ContactName[] contactName = new ContactName[1];
 Transaction.execute(conn -> {
     contactName[0] = new Sql<>(Contact.class)
         .where("{id}={}", 1)
         .connection(conn)
         .selectAs(ContactName.class).orElse(null);
 });

 /**/DebugTrace.print("contactName", contactName[0]);
	/**/DebugTrace.leave();
	}

	// selectCount
	private static void sql_selectCount() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Contact.class)
         .connection(conn)
         .selectCount();
 });

	/**/DebugTrace.leave();
	}

	// insert 1
	private static void sql_insert1() {
	/**/DebugTrace.enter();

		Transaction.execute(conn -> {
			new Sql<>(Contact.class).where("{id}={}", 6)
				.connection(conn)
                .delete();
		});

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Contact.class)
         .connection(conn)
         .insert(new Contact(6, "Setoka", "Orange", 2001, 2, 1));
 });

	/**/DebugTrace.leave();
	}

	// insert 2
	private static void sql_insert2() {
	/**/DebugTrace.enter();

		Transaction.execute(conn -> {
			new Sql<>(Contact.class)
				.where("{id} IN {}", Arrays.asList(7, 8, 9))
				.connection(conn).delete();
		});

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Contact.class)
         .connection(conn)
         .insert(Arrays.asList(
             new Contact(7, "Harumi", "Orange", 2001, 2, 2),
             new Contact(8, "Mihaya", "Orange", 2001, 2, 3),
             new Contact(9, "Asumi" , "Orange", 2001, 2, 4)
         ));
 });

	/**/DebugTrace.leave();
	}

	// update 1
	private static void sql_update1() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Contact.class)
         .connection(conn)
         .update(new Contact(6, "Setoka", "Orange", 2017, 2, 1));
 });

	/**/DebugTrace.leave();
	}

	// update 2
	private static void sql_update2() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Contact.class)
         .connection(conn)
         .update(Arrays.asList(
             new Contact(7, "Harumi", "Orange", 2017, 2, 2),
             new Contact(8, "Mihaya", "Orange", 2017, 2, 3),
             new Contact(9, "Asumi" , "Orange", 2017, 2, 4)
         ));
 });

	/**/DebugTrace.leave();
	}

	// delete 1
	private static void sql_delete1() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Contact.class)
         .where(Condition.ALL)
         .connection(conn)
         .delete();
 });

	/**/DebugTrace.leave();
	}

	// delete 2
	private static void sql_delete2() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Contact.class)
         .connection(conn)
         .delete(new Contact(6));
 });

		Transaction.execute(conn -> {
			count[0] = new Sql<>(Contact.class)
				.connection(conn)
				.insert(new Contact(6, "Setoka", "Orange", 2001, 2, 1));
		});

	/**/DebugTrace.leave();
	}

	// delete 3
	private static void sql_delete3() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Contact.class)
         .connection(conn)
         .delete(Arrays.asList(new Contact(7), new Contact(8), new Contact(9)));
 });

	Transaction.execute(conn -> {
		new Sql<>(Contact.class)
			.connection(conn)
			.insert(Arrays.asList(
				new Contact(7, "Harumi", "Orange", 2001, 2, 2),
				new Contact(8, "Mihaya", "Orange", 2001, 2, 3),
				new Contact(9, "Asumi" , "Orange", 2001, 2, 4)
			));
	});

	/**/DebugTrace.leave();
	}

}
