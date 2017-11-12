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
import org.lightsleep.connection.ConnectionSupplier;
import org.lightsleep.database.SQLite;
import org.lightsleep.example.Common;
import org.lightsleep.example.java.entity.*;

/**
 * JavaDocExamples
 */
public class JavaDocExamples extends Common {
	public static void main(String[] args) {
		JavaDocExamples examples = new JavaDocExamples();
		try {
			examples.init1();
			examples.init2();
			examples.transaction();
			examples.sql();
			examples.sql_columns1();
			examples.sql_columns2();
			examples.sql_expression();
			examples.sql_innerJoin();
			examples.sql_leftJoin();
			examples.sql_rightJoin();
			examples.sql_where1();
			examples.sql_where2();
			examples.sql_where3();
			examples.sql_where4();
			examples.sql_and();
			examples.sql_or();
			examples.sql_orderBy();
			examples.sql_asc();
			examples.sql_desc();
			examples.sql_limit();
			examples.sql_offset();
			examples.sql_doIf();
			examples.sql_select1();
			examples.sql_selectAs1();
			examples.sql_select2();
			examples.sql_select3();
			examples.sql_select4();
			examples.sql_select5();
			examples.sql_select6();
			examples.sql_selectAs2();
			examples.sql_selectCount();
			examples.sql_insert1();
			examples.sql_insert2();
			examples.sql_update1();
			examples.sql_update2();
			examples.sql_delete1();
			examples.sql_delete2();
			examples.sql_delete3();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Transaction
	private void transaction() {
	/**/DebugTrace.enter();

 Transaction.execute(conn -> {
     Optional<Contact> contactOpt = new Sql<>(Contact.class).connection(conn)
         .where("{id}={}", 1)
         .select();
     contactOpt.ifPresent(contact -> {
         contact.setBirthday(2017, 1, 1);
         new Sql<>(Contact.class).connection(conn)
            .update(contact);
     });
 });

	/**/DebugTrace.leave();
	}

	// Sql
	private void sql() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class).connection(conn)
         .where("{lastName}={}", "Apple")
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// columns 1
	private void sql_columns1() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class).connection(conn)
         .columns("lastName", "firstName")
         .where("{lastName}={}", "Apple")
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// columns 2
	private void sql_columns2() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 List<Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C").connection(conn)
         .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
         .columns("C.id", "P.*")
         .<Phone>select(contacts::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// expression
	private void sql_expression() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class).connection(conn)
         .expression("firstName", "'['||{firstName}||']'")
         .where("{lastName}={}", "Orange")
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// innerJoin
	private void sql_innerJoin() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 List<Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C").connection(conn)
         .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
         .<Phone>select(contacts::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// leftJoin
	private void sql_leftJoin() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 List<Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C").connection(conn)
         .leftJoin(Phone.class, "P", "{P.contactId}={C.id}")
         .<Phone>select(contacts::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// rightJoin
	private void sql_rightJoin() {
		if (ConnectionSupplier.find().getDatabase() instanceof SQLite) return; // SQLite dose not support RIGHT JOIN
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 List<Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C").connection(conn)
         .rightJoin(Phone.class, "P", "{P.contactId}={C.id}")
         .<Phone>select(contacts::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// where 1
	private void sql_where1() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class).connection(conn)
         .where("{birthday} IS NULL")
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// where 2
	private void sql_where2() {
	/**/DebugTrace.enter();

 int id = 1;
 Contact[] contact = new Contact[1];
 Transaction.execute(conn -> {
     contact[0] = new Sql<>(Contact.class).connection(conn)
         .where("{id}={}", id)
         .select().orElse(null);
 });

	/**/DebugTrace.leave();
	}

	// where 3
	private void sql_where3() {
	/**/DebugTrace.enter();

 Contact[] contact = new Contact[1];
 Transaction.execute(conn -> {
     contact[0] = new Sql<>(Contact.class).connection(conn)
         .where(new ContactKey(2))
         .select().orElse(null);
 });

	/**/DebugTrace.leave();
	}

	// where 4
	private void sql_where4() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C").connection(conn)
         .where("EXISTS",
              new Sql<>(Phone.class, "P")
                  .where("{P.contactId}={C.id}")
                  .and("{P.content} LIKE {}", "0800001%")
         )
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// and
	private void sql_and() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class).connection(conn)
         .where("{lastName}={}", "Apple")
         .and("{firstName}={}", "Akiyo")
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// or
	private void sql_or() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class).connection(conn)
         .where("{lastName}={}", "Apple")
         .or("{lastName}={}", "Orange")
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// orderBy
	private void sql_orderBy() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class).connection(conn)
         .orderBy("{lastName}")
         .orderBy("{firstName}")
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// asc
	private void sql_asc() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class).connection(conn)
         .orderBy("{id}").asc()
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// desc
	private void sql_desc() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class).connection(conn)
         .orderBy("{id}").desc()
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// limit
	private void sql_limit() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class).connection(conn)
         .limit(5)
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// offset
	private void sql_offset() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class).connection(conn)
         .limit(5).offset(5)
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// doIf
	private void sql_doIf() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C").connection(conn)
         .doIf(!(conn.getDatabase() instanceof SQLite), Sql::forUpdate) // SQLite dose not support FOR UPDATE
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// select 1
	private void sql_select1() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class).connection(conn)
         .select(contacts::add);
 });

	/**/DebugTrace.leave();
	}

	// select As 1
 public static class ContactName {
     public String firstName;
     public String lastName;
 }
	private void sql_selectAs1() {
	/**/DebugTrace.enter();

 List<ContactName> contactNames = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class).connection(conn)
         .selectAs(ContactName.class, contactNames::add);
 });

 /**/DebugTrace.print("contactNames", contactNames);
	/**/DebugTrace.leave();
	}

	// select 2
	private void sql_select2() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 List<Phone>   phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C").connection(conn)
         .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
         .<Phone>select(contacts::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// select 3
	private void sql_select3() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 List<Phone>   phones = new ArrayList<>();
 List<Email>   emails = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C").connection(conn)
         .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
         .innerJoin(Email.class, "E", "{E.contactId}={C.id}")
         .<Phone, Email>select(contacts::add, phones::add, emails::add);
 });

	/**/DebugTrace.leave();
	}

	// select 4
	private void sql_select4() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 List<Phone>   phones = new ArrayList<>();
 List<Email>   emails = new ArrayList<>();
 List<Address> addresses = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C").connection(conn)
         .innerJoin(  Phone.class, "P", "{P.contactId}={C.id}")
         .innerJoin(  Email.class, "E", "{E.contactId}={C.id}")
         .innerJoin(Address.class, "A", "{A.contactId}={C.id}")
         .<Phone, Email, Address>select(
             contacts::add, phones::add, emails::add, addresses::add);
 });

	/**/DebugTrace.leave();
	}

	// select 5
	private void sql_select5() {
	/**/DebugTrace.enter();

 List<Contact> contacts = new ArrayList<>();
 List<Phone>   phones = new ArrayList<>();
 List<Email>   emails = new ArrayList<>();
 List<Address> addresses = new ArrayList<>();
 List<Url>     urls = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Contact.class, "C").connection(conn)
         .innerJoin(  Phone.class, "P", "{P.contactId}={C.id}")
         .innerJoin(  Email.class, "E", "{E.contactId}={C.id}")
         .innerJoin(Address.class, "A", "{A.contactId}={C.id}")
         .innerJoin(    Url.class, "U", "{U.contactId}={C.id}")
         .<Phone, Email, Address, Url>select(
             contacts::add, phones::add, emails::add,
             addresses::add, urls::add);
 });

	/**/DebugTrace.leave();
	}

	// select 6
	private void sql_select6() {
	/**/DebugTrace.enter();

 Contact.Ex[] contact = new Contact.Ex[1];
 Transaction.execute(conn -> {
     contact[0] = new Sql<>(Contact.Ex.targetClass(conn.getDatabase())).connection(conn)
         .where("{id}={}", 1)
         .select().orElse(null);
 });

	/**/DebugTrace.print("contact", contact[0]);
	/**/DebugTrace.leave();
	}

	// select As 2
	private void sql_selectAs2() {
	/**/DebugTrace.enter();

 ContactName[] contactName = new ContactName[1];
 Transaction.execute(conn -> {
     contactName[0] = new Sql<>(Contact.class).connection(conn)
         .where("{id}={}", 1)
         .selectAs(ContactName.class).orElse(null);
 });

 /**/DebugTrace.print("contactName", contactName[0]);
	/**/DebugTrace.leave();
	}

	// selectCount
	private void sql_selectCount() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Contact.class).connection(conn)
         .selectCount();
 });

	/**/DebugTrace.leave();
	}

	// insert 1
	private void sql_insert1() {
	/**/DebugTrace.enter();

		Transaction.execute(conn -> {
			new Sql<>(Contact.class).connection(conn)
				.where("{id}={}", 6)
                .delete();
		});

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Contact.class).connection(conn)
         .insert(new Contact(6, "Setoka", "Orange", 2001, 2, 1));
 });

	/**/DebugTrace.leave();
	}

	// insert 2
	private void sql_insert2() {
	/**/DebugTrace.enter();

		Transaction.execute(conn -> {
			new Sql<>(Contact.class).connection(conn)
				.where("{id} IN {}", Arrays.asList(7, 8, 9))
				.delete();
		});

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Contact.class).connection(conn)
         .insert(Arrays.asList(
             new Contact(7, "Harumi", "Orange", 2001, 2, 2),
             new Contact(8, "Mihaya", "Orange", 2001, 2, 3),
             new Contact(9, "Asumi" , "Orange", 2001, 2, 4)
         ));
 });

	/**/DebugTrace.leave();
	}

	// update 1
	private void sql_update1() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Contact.class).connection(conn)
         .update(new Contact(6, "Setoka", "Orange", 2017, 2, 1));
 });

	/**/DebugTrace.leave();
	}

	// update 2
	private void sql_update2() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Contact.class).connection(conn)
         .update(Arrays.asList(
             new Contact(7, "Harumi", "Orange", 2017, 2, 2),
             new Contact(8, "Mihaya", "Orange", 2017, 2, 3),
             new Contact(9, "Asumi" , "Orange", 2017, 2, 4)
         ));
 });

	/**/DebugTrace.leave();
	}

	// delete 1
	private void sql_delete1() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Contact.class).connection(conn)
         .where(Condition.ALL)
         .delete();
 });

	/**/DebugTrace.leave();
	}

	// delete 2
	private void sql_delete2() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Contact.class).connection(conn)
         .delete(new Contact(6));
 });

		Transaction.execute(conn -> {
			count[0] = new Sql<>(Contact.class).connection(conn)
				.insert(new Contact(6, "Setoka", "Orange", 2001, 2, 1));
		});

	/**/DebugTrace.leave();
	}

	// delete 3
	private void sql_delete3() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Contact.class).connection(conn)
         .delete(Arrays.asList(new Contact(7), new Contact(8), new Contact(9)));
 });

	Transaction.execute(conn -> {
		new Sql<>(Contact.class).connection(conn)
			.insert(Arrays.asList(
				new Contact(7, "Harumi", "Orange", 2001, 2, 2),
				new Contact(8, "Mihaya", "Orange", 2001, 2, 3),
				new Contact(9, "Asumi" , "Orange", 2001, 2, 4)
			));
	});

	/**/DebugTrace.leave();
	}

}
