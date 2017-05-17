// Example.java
// (C) 2016 Masato Kokubo

package org.lightsleep.example.java;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.lightsleep.*;
import org.lightsleep.component.*;
import org.lightsleep.database.*;
import org.lightsleep.example.Common;
import org.lightsleep.example.java.entity.*;
import org.lightsleep.logger.*;


public class Example extends Common {
	static final Logger logger = LoggerFactory.getLogger(Example.class);

	public static void main(String[] args) {
	    try {
	        init1();
	        example4();

	        init1();
	        init2();
	        readme();
	        example5_1_1();
	        example5_1_2();
	        example5_1_3();
	        example5_1_4();
	        example5_1_5();
	        example5_1_6();
	        example5_1_7();
	        example5_1_8();
	        example5_1_9();
	        example5_1_10();
	        example5_1_11();
	        example5_1_12();
	        example5_1_13();
	        example5_1_14();
	        example5_1_15();

	        init1();
	        example5_2_1();
	        example5_2_2();

	        example5_3_1();
	        example5_3_2();
	        example5_3_3();
	        example5_3_4();

	        example5_4_1();
	        example5_4_2();
	        example5_4_3();
	        example5_4_4();
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	// README
	private static void readme() {
		logger.info("readme");

		List<Contact> contacts = new ArrayList<Contact>();
		Transaction.execute(connection ->
		    new Sql<>(Contact.class)
		        .where("{familyName}={}", "Apple")
		        .or   ("{familyName}={}", "Orange")
		        .orderBy("{familyName}")
		        .orderBy("{givenName}")
		        .select(connection, contacts::add)
		);
	}

	// ### 4. Transaction
	private static void example4() {
		logger.info("example4");

	    Contact contact = new Contact();
	    contact.id = 1;
	    contact.familyName = "Apple";
	    contact.givenName  = "Akane";

	    // トランザクション定義例
	    Transaction.execute(connection -> {
	        // トランザクション内容開始
	        new Sql<>(Contact.class).insert(connection, contact);

	        // トランザクション内容終了
	    });
	}

	// #### 5-1-1. SELECT 1 row with an Expression condition
	private static void example5_1_1() {
		logger.info("example5_1_1");

	    Transaction.execute(connection -> {
	        Optional<Contact> contactOpt = new Sql<>(Contact.class)
	            .where("{id}={}", 1)
	            .select(connection);
	    });
	}

	// #### 5-1-2. SELECT 1 row with an Entity condition
	private static void example5_1_2() {
		logger.info("example5_1_2");

	    Contact contact = new Contact();
	    contact.id = 1;
	    Transaction.execute(connection -> {
	        Optional<Contact> contactOpt = new Sql<>(Contact.class)
	            .where(contact)
	            .select(connection);
	    });
	}

	// #### 5-1-3. SELECT multiple rows with an Expression condition
	private static void example5_1_3() {
		logger.info("example5_1_3");

	    List<Contact> contacts = new ArrayList<>();
	    Transaction.execute(connection ->
	        new Sql<>(Contact.class)
	            .where("{familyName}={}", "Apple")
	            .select(connection, contacts::add)
	    );
	}

	// #### 5-1-4. SELECT with a Subquery condition
	private static void example5_1_4() {
		logger.info("example5_1_4");

	    List<Contact> contacts = new ArrayList<>();
	    Transaction.execute(connection ->
	        new Sql<>(Contact.class, "C")
	            .where("EXISTS",
	                new Sql<>(Phone.class, "P")
	                    .where("{P.contactId}={C.id}")
	            )
	            .select(connection, contacts::add)
	    );
	}

	// #### 5-1-5. SELECT with Expression conditions (AND)
	private static void example5_1_5() {
		logger.info("example5_1_5");

	    List<Contact> contacts = new ArrayList<>();
	    Transaction.execute(connection ->
	        new Sql<>(Contact.class)
	            .where("{familyName}={}", "Apple")
	            .and  ("{givenName}={}", "Akane")
	            .select(connection, contacts::add)
	    );
	}

	// #### 5-1-6. SELECT with Expression Condition (OR)
	private static void example5_1_6() {
		logger.info("example5_1_6");

	    List<Contact> contacts = new ArrayList<>();
	    Transaction.execute(connection ->
	        new Sql<>(Contact.class)
	            .where("{familyName}={}", "Apple")
	            .or   ("{familyName}={}", "Orange")
	            .select(connection, contacts::add)
	    );
	}

	// #### 5-1-7. SELECT with Expression conditions A AND B OR C AND D
	private static void example5_1_7() {
		logger.info("example5_1_7");

	    List<Contact> contacts = new ArrayList<>();
	    Transaction.execute(connection ->
	        new Sql<>(Contact.class)
	            .where(Condition
	                .of ("{familyName}={}", "Apple")
	                .and("{givenName}={}", "Akane")
	            )
	            .or(Condition
	                .of ("{familyName}={}", "Orange")
	                .and("{givenName}={}", "Setoka")
	            )
	            .select(connection, contacts::add)
	    );
	}

	// #### 5-1-8. SELECT with selection of columns
	private static void example5_1_8() {
		logger.info("example5_1_8");

	    List<Contact> contacts = new ArrayList<>();
	    Transaction.execute(connection ->
	        new Sql<>(Contact.class)
	            .where("{familyName}={}", "Apple")
	            .columns("familyName", "givenName")
	            .select(connection, contacts::add)
	    );
	}

	// #### 5-1-9. SELECT with GROUP BY and HAVING
	private static void example5_1_9() {
		logger.info("example5_1_9");

	    List<Contact> contacts = new ArrayList<>();
	    Transaction.execute(connection ->
	        new Sql<>(Contact.class, "C")
	            .columns("familyName")
	            .groupBy("{familyName}")
	            .having("COUNT({familyName})>=2")
	            .select(connection, contacts::add)
	    );
	}

	// #### 5-1-10. SELECT with ORDER BY, OFFSET and LIMIT
	private static void example5_1_10() {
		logger.info("example5_1_10");

	    List<Contact> contacts = new ArrayList<>();
	    Transaction.execute(connection ->
	        new Sql<>(Contact.class)
	            .orderBy("{familyName}")
	            .orderBy("{givenName}")
	            .orderBy("{id}")
	            .offset(10).limit(5)
	            .select(connection, contacts::add)
	    );
	}

	// #### 5-1-11. SELECT with FOR UPDATE
	private static void example5_1_11() {
		if (Sql.getDatabase() instanceof SQLite) return;
		logger.info("example5_1_11");

		Transaction.execute(connection -> {
		    Optional<Contact> contactOpt = new Sql<>(Contact.class)
		        .where("{id}={}", 1)
		        .forUpdate()
		        .select(connection);
		});
	}

	// #### 5-1-12. SELECT with INNER JOIN
	private static void example5_1_12() {
		logger.info("example5_1_12");

	    List<Contact> contacts = new ArrayList<>();
	    List<Phone> phones = new ArrayList<>();
	    Transaction.execute(connection ->
	        new Sql<>(Contact.class, "C")
	            .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
	            .where("{C.id}={}", 1)
	            .<Phone>select(connection, contacts::add, phones::add)
	    );
	}

	// #### 5-1-13. SELECT with LEFT OUTER JOIN
	private static void example5_1_13() {
		logger.info("example5_1_13");

	    List<Contact> contacts = new ArrayList<>();
	    List<Phone> phones = new ArrayList<>();
	    Transaction.execute(connection ->
	        new Sql<>(Contact.class, "C")
	            .leftJoin(Phone.class, "P", "{P.contactId}={C.id}")
	            .where("{C.familyName}={}", "Apple")
	            .<Phone>select(connection, contacts::add, phones::add)
	    );
	}

	// #### 5-1-14. SELECT with RIGHT OUTER JOIN
	private static void example5_1_14() {
		if (Sql.getDatabase() instanceof SQLite) return;
		logger.info("example5_1_14");

	    List<Contact> contacts = new ArrayList<>();
	    List<Phone> phones = new ArrayList<>();
	    Transaction.execute(connection ->
	        new Sql<>(Contact.class, "C")
	            .rightJoin(Phone.class, "P", "{P.contactId}={C.id}")
	            .where("{P.label}={}", "Main")
	            .<Phone>select(connection, contacts::add, phones::add)
	    );
	}

	// #### 5-1-15. SELECT COUNT(*)
	private static void example5_1_15() {
		logger.info("example5_1_15");

	    int[] rowCount = new int[1];
	    Transaction.execute(connection ->
	        rowCount[0] = new Sql<>(Contact.class)
	            .where("familyName={}", "Apple")
	            .selectCount(connection)
	    );
	}

	// #### 5-2-1. INSERT 1 row
	private static void example5_2_1() {
		logger.info("example5_2_1");

	    Contact contact = new Contact();
	    contact.id = 1;
	    contact.familyName = "Apple";
	    contact.givenName = "Akane";
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(2001, 1-1, 1, 0, 0, 0);
	    contact.birthday = new Date(calendar.getTimeInMillis());

	    Transaction.execute(connection ->
	        new Sql<>(Contact.class).insert(connection, contact));
	}

	// #### 5-2-2. INSERT multiple rows
	private static void example5_2_2() {
		logger.info("example5_2_2");

	    List<Contact> contacts = new ArrayList<>();

	    Contact contact = new Contact();
	    contact.id = 2; contact.familyName = "Apple"; contact.givenName = "Yukari";
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(2001, 1-1, 2, 0, 0, 0);
	    contact.birthday = new Date(calendar.getTimeInMillis());
	    contacts.add(contact);

	    contact = new Contact();
	    contact.id = 3; contact.familyName = "Apple"; contact.givenName = "Azusa";
	    calendar = Calendar.getInstance();
	    calendar.set(2001, 1-1, 3, 0, 0, 0);
	    contact.birthday = new Date(calendar.getTimeInMillis());
	    contacts.add(contact);

	    Transaction.execute(connection ->
	        new Sql<>(Contact.class).insert(connection, contacts));
	}

	// #### 5-3-1. UPDATE 1 row
	private static void example5_3_1() {
		logger.info("example5_3_1");

	    Transaction.execute(connection ->
	        new Sql<>(Contact.class)
	            .where("{id}={}", 1)
	            .select(connection)
	            .ifPresent(contact -> {
	                contact.givenName = "Akiyo";
	                new Sql<>(Contact.class).update(connection, contact);
	            })
	    );
	}

	// #### 5-3-2. UPDATE multiple rows
	private static void example5_3_2() {
		logger.info("example5_3_2");

	    Transaction.execute(connection -> {
	        List<Contact> contacts = new ArrayList<>();
	        new Sql<>(Contact.class)
	            .where("{familyName}={}", "Apple")
	            .select(connection, contact -> {
	                contact.familyName = "Apfel";
	                contacts.add(contact);
	            });
	        new Sql<>(Contact.class).update(connection, contacts);
	    });
	}

	// #### 5-3-3. UPDATE with a Condition and selection of columns
	private static void example5_3_3() {
		logger.info("example5_3_3");

	    Contact contact = new Contact();
	    contact.familyName = "Pomme";
	    Transaction.execute(connection ->
	        new Sql<>(Contact.class)
	            .where("{familyName}={}", "Apfel")
	            .columns("familyName")
	            .update(connection, contact)
	    );
	}

	// #### 5-3-4. UPDATE all rows
	private static void example5_3_4() {
		logger.info("example5_3_4");

	    Contact contact = new Contact();
	    Transaction.execute(connection ->
	        new Sql<>(Contact.class)
	            .where(Condition.ALL)
	            .columns("birthday")
	            .update(connection, contact)
	    );
	}

	// #### 5-4-1. DELETE 1 row
	private static void example5_4_1() {
		logger.info("example5_4_1");

	    Transaction.execute(connection ->
	        new Sql<>(Contact.class)
	            .where("{id}={}", 1)
	            .select(connection)
	            .ifPresent(contact ->
	                new Sql<>(Contact.class).delete(connection, contact))
	    );
	}

	// #### 5-4-2. DELETE multiple rows
	private static void example5_4_2() {
		logger.info("example5_4_2");

	    Transaction.execute(connection -> {
	        List<Contact> contacts = new ArrayList<>();
	        new Sql<>(Contact.class)
	            .where("{familyName}={}", "Pomme")
	            .select(connection, contacts::add);
	        new Sql<>(Contact.class).delete(connection, contacts);
	    });
	}

	// #### 5-4-3. DELETE with a Condition
	private static void example5_4_3() {
		logger.info("example5_4_3");

	    Transaction.execute(connection ->
	        new Sql<>(Contact.class)
	            .where("{familyName}={}", "Orange")
	            .delete(connection)
	    );
	}

	// #### 5-4-4. DELETE all rows
	private static void example5_4_4() {
		logger.info("example5_4_4");

	    Transaction.execute(connection ->
	        new Sql<>(Phone.class)
	            .where(Condition.ALL)
	            .delete(connection)
	    );
	}
}
