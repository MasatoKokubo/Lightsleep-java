// ManualExamples.java
// (C) 2016 Masato Kokubo

package org.lightsleep.example.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.lightsleep.*;
import org.lightsleep.component.*;
import org.lightsleep.connection.ConnectionSupplier;
import org.lightsleep.database.*;
import org.lightsleep.example.Common;
import org.lightsleep.example.java.entity.*;
import org.lightsleep.logger.*;


public class ManualExamples extends Common {
    private static final Logger logger = LoggerFactory.getLogger(ManualExamples.class);

    public static void main(String[] args) {
        ManualExamples examples = new ManualExamples();
        try {
            examples.init1();
            examples.example4();

            examples.init1();
            examples.init2();
            examples.readme();
            examples.example5_1_1();
            examples.example5_1_2();
            examples.example5_1_3();
            examples.example5_1_4();
            examples.example5_1_5();
            examples.example5_1_6();
            examples.example5_1_7();
            examples.example5_1_8();
            examples.example5_1_9();
            examples.example5_1_10();
            examples.example5_1_11();
            examples.example5_1_12();
            examples.example5_1_13();
            examples.example5_1_14();
            examples.example5_1_15();
            examples.example5_1_16();
            examples.example5_1_17();

            examples.init1();
            examples.example5_2_1();
            examples.example5_2_2();

            examples.example5_3_1();
            examples.example5_3_2();
            examples.example5_3_3();
            examples.example5_3_4();

            examples.example5_4_1();
            examples.example5_4_2();
            examples.example5_4_3();
            examples.example5_4_4();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // README
    private void readme() {
        logger.info("README");

        List<Contact> contacts = new ArrayList<Contact>();
        Transaction.execute(conn ->
            new Sql<>(Contact.class)
                .where("{lastName}={}", "Apple")
                .or   ("{lastName}={}", "Orange")
                .orderBy("{lastName}")
                .orderBy("{firstName}")
                .connection(conn)
                .select(contacts::add)
        );
    }

    // ### 4. Transaction
    private void example4() {
        logger.info("example4");

        Contact contact = new Contact(1, "Akane", "Apple");

        // An example of transaction
        Transaction.execute(conn -> {
            // Start of transaction body
            new Sql<>(Contact.class)
                .connection(conn)
                .insert(contact);

            // End of transaction body
        });
    }

    // #### 5-1-1. SELECT 1 row with an Expression condition
    @SuppressWarnings("unused")
    private void example5_1_1() {
        logger.info("example5_1_1");

        Transaction.execute(conn -> {
            Optional<Contact> contactOpt = new Sql<>(Contact.class)
                .where("{id}={}", 1)
                .connection(conn)
                .select();
        });
    }

    // #### 5-1-2. SELECT 1 row with an Entity condition
    @SuppressWarnings("unused")
    private void example5_1_2() {
        logger.info("example5_1_2");

        Transaction.execute(conn -> {
            Optional<Contact> contactOpt = new Sql<>(Contact.class)
                .where(new ContactKey(1))
                .connection(conn)
                .select();
        });
    }

    // #### 5-1-3. SELECT multiple rows with an Expression condition
    private void example5_1_3() {
        logger.info("example5_1_3");

        List<Contact> contacts = new ArrayList<>();
        Transaction.execute(conn ->
            new Sql<>(Contact.class)
                .where("{lastName}={}", "Apple")
                .connection(conn)
                .select(contacts::add)
        );

        logger.info("  row count=" + contacts.size());
    }

    // #### 5-1-4. SELECT with a Subquery condition
    private void example5_1_4() {
        logger.info("example5_1_4");

        List<Contact> contacts = new ArrayList<>();
        Transaction.execute(conn ->
            new Sql<>(Contact.class, "C")
                .where("EXISTS",
                    new Sql<>(Phone.class, "P")
                        .where("{P.contactId}={C.id}")
                )
                .connection(conn)
                .select(contacts::add)
        );

        logger.info("  row count=" + contacts.size());
    }

    // #### 5-1-5. SELECT with Expression conditions (AND)
    private void example5_1_5() {
        logger.info("example5_1_5");

        List<Contact> contacts = new ArrayList<>();
        Transaction.execute(conn ->
            new Sql<>(Contact.class)
                .where("{lastName}={}", "Apple")
                .and  ("{firstName}={}", "Akane")
                .connection(conn)
                .select(contacts::add)
        );

        logger.info("  row count=" + contacts.size());
    }

    // #### 5-1-6. SELECT with Expression Condition (OR)
    private void example5_1_6() {
        logger.info("example5_1_6");

        List<Contact> contacts = new ArrayList<>();
        Transaction.execute(conn ->
            new Sql<>(Contact.class)
                .where("{lastName}={}", "Apple")
                .or   ("{lastName}={}", "Orange")
                .connection(conn)
                .select(contacts::add)
        );

        logger.info("  row count=" + contacts.size());
    }

    // #### 5-1-7. SELECT with Expression conditions A AND B OR C AND D
    private void example5_1_7() {
        logger.info("example5_1_7");

        List<Contact> contacts = new ArrayList<>();
        Transaction.execute(conn ->
            new Sql<>(Contact.class)
                .where(Condition
                    .of ("{lastName}={}", "Apple")
                    .and("{firstName}={}", "Akane")
                )
                .or(Condition
                    .of ("{lastName}={}", "Orange")
                    .and("{firstName}={}", "Setoka")
                )
                .connection(conn)
                .select(contacts::add)
        );

        logger.info("  row count=" + contacts.size());
    }

    // #### 5-1-8. SELECT with selection of columns
    private void example5_1_8() {
        logger.info("example5_1_8");

        List<Contact> contacts = new ArrayList<>();
        Transaction.execute(conn ->
            new Sql<>(Contact.class)
                .where("{lastName}={}", "Apple")
                .columns("lastName", "firstName")
                .connection(conn)
                .select(contacts::add)
        );

        logger.info("  row count=" + contacts.size());
    }

    // #### 5-1-9. SELECT with GROUP BY and HAVING
    private void example5_1_9() {
        logger.info("example5_1_9");

        List<Contact> contacts = new ArrayList<>();
        Transaction.execute(conn ->
            new Sql<>(Contact.class, "C")
                .columns("lastName")
                .groupBy("{lastName}")
                .having("COUNT({lastName})>=2")
                .connection(conn)
                .select(contacts::add)
        );

        logger.info("  row count=" + contacts.size());
    }

    // #### 5-1-10. SELECT with ORDER BY, OFFSET and LIMIT
    private void example5_1_10() {
        logger.info("example5_1_10");

        List<Contact> contacts = new ArrayList<>();
        Transaction.execute(conn ->
            new Sql<>(Contact.class)
                .orderBy("{lastName}")
                .orderBy("{firstName}")
                .orderBy("{id}")
                .offset(10).limit(5)
                .connection(conn)
                .select(contacts::add)
        );

        logger.info("  row count=" + contacts.size());
    }

    // #### 5-1-11. SELECT with FOR UPDATE
    @SuppressWarnings("unused")
    private void example5_1_11() {
        if (ConnectionSupplier.find().getDatabase() instanceof SQLite) return;
        logger.info("example5_1_11");

        Transaction.execute(conn -> {
            Optional<Contact> contactOpt = new Sql<>(Contact.class)
                .where("{id}={}", 1)
                .forUpdate()
                .connection(conn)
                .select();
        });
    }

    // #### 5-1-12. SELECT with INNER JOIN
    private void example5_1_12() {
        logger.info("example5_1_12");

        List<Contact> contacts = new ArrayList<>();
        List<Phone> phones = new ArrayList<>();
        Transaction.execute(conn ->
            new Sql<>(Contact.class, "C")
                .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
                .where("{C.id}={}", 1)
                .connection(conn)
                .<Phone>select(contacts::add, phones::add)
        );

        logger.info("  row count=" + contacts.size());
    }

    // #### 5-1-13. SELECT with LEFT OUTER JOIN
    private void example5_1_13() {
        logger.info("example5_1_13");

        List<Contact> contacts = new ArrayList<>();
        List<Phone> phones = new ArrayList<>();
        Transaction.execute(conn ->
            new Sql<>(Contact.class, "C")
                .leftJoin(Phone.class, "P", "{P.contactId}={C.id}")
                .where("{C.lastName}={}", "Apple")
                .connection(conn)
                .<Phone>select(contacts::add, phones::add)
        );

        logger.info("  row count=" + contacts.size());
    }

    // #### 5-1-14. SELECT with RIGHT OUTER JOIN
    private void example5_1_14() {
        if (ConnectionSupplier.find().getDatabase() instanceof SQLite) return;
        logger.info("example5_1_14");

        List<Contact> contacts = new ArrayList<>();
        List<Phone> phones = new ArrayList<>();
        Transaction.execute(conn ->
            new Sql<>(Contact.class, "C")
                .rightJoin(Phone.class, "P", "{P.contactId}={C.id}")
                .where("{P.label}={}", "Main")
                .connection(conn)
                .<Phone>select(contacts::add, phones::add)
        );

        logger.info("  row count=" + contacts.size());
    }

    // #### 5-1-15. SELECT COUNT(*)
    private void example5_1_15() {
        logger.info("example5_1_15");

        int[] count = new int[1];
        Transaction.execute(conn ->
            count[0] = new Sql<>(Contact.class)
                .where("{lastName}={}", "Apple")
                .connection(conn)
                .selectCount()
        );

        logger.info("  count=" + count[0]);
    }

    // #### 5-1-16. SELECT FROM Clause Subquery
    private void example5_1_16() {
        logger.info("example5_1_16");

        List<Contact> contacts = new ArrayList<>();
        Transaction.execute(conn -> {
            Class<? extends Contact.Ex> contactClass = Contact.Ex.targetClass(conn.getDatabase());
            new Sql<>(contactClass)
                .from(new Sql<>(contactClass))
                .where("{fullName}={}", "Akane Apple")
                .orderBy("{fullName}")
                .connection(conn)
                .select(contacts::add);
        });

        logger.info("  row count=" + contacts.size());
    }

    // #### 5-1-17. SELECT UNION, UNION ALL
    private void example5_1_17() {
        logger.info("example5_1_17");

        List<ContactFeature> features = new ArrayList<>();
        String targetFirstName = "Setoka";
        String targetLastName = "Orange";
        Transaction.execute(conn -> {
            new Sql<>(ContactFeature.class, "F")
                .columns(ContactFeature.class)
                .unionAll(new Sql<>(Address.class)
                    .innerJoin(Contact.class, "C", "{C.id}={F.contactId}")
                    .where("{C.firstName}={}", targetFirstName)
                    .and("{C.lastName}={}", targetLastName)
	                .and("{F.featureIndex}={}", 1)
                )
                .unionAll(new Sql<>(Email.class)
                    .innerJoin(Contact.class, "C", "{C.id}={F.contactId}")
                    .where("{C.firstName}={}", targetFirstName)
                    .and("{C.lastName}={}", targetLastName)
	                .and("{F.featureIndex}={}", 1)
                )
                .unionAll(new Sql<>(Phone.class)
                    .innerJoin(Contact.class, "C", "{C.id}={F.contactId}")
                    .where("{C.firstName}={}", targetFirstName)
                    .and("{C.lastName}={}", targetLastName)
	                .and("{F.featureIndex}={}", 1)
                )
                .unionAll(new Sql<>(Url.class)
                    .innerJoin(Contact.class, "C", "{C.id}={F.contactId}")
                    .where("{C.firstName}={}", targetFirstName)
                    .and("{C.lastName}={}", targetLastName)
	                .and("{F.featureIndex}={}", 1)
                )
                .orderBy("{F_label}")
                .connection(conn)
                .select(features::add);
        });

        logger.info("  row count=" + features.size());
    }

    // #### 5-2-1. INSERT 1 row
    private void example5_2_1() {
        logger.info("example5_2_1");

        Transaction.execute(conn ->
            new Sql<>(Contact.class)
                .connection(conn)
                .insert(new Contact(1, "Akane", "Apple", 2001, 1, 1))
        );
    }

    // #### 5-2-2. INSERT multiple rows
    private void example5_2_2() {
        logger.info("example5_2_2");

        Transaction.execute(conn ->
            new Sql<>(Contact.class)
                .connection(conn)
                .insert(Arrays.asList(
                    new Contact(2, "Yukari", "Apple", 2001, 1, 2),
                    new Contact(3, "Azusa", "Apple", 2001, 1, 3)
                ))
        );
    }

    // #### 5-3-1. UPDATE 1 row
    private void example5_3_1() {
        logger.info("example5_3_1");

        Transaction.execute(conn ->
            new Sql<>(Contact.class)
                .where("{id}={}", 1)
                .connection(conn)
                .select()
                .ifPresent(contact -> {
                    contact.firstName = "Akiyo";
                    new Sql<>(Contact.class)
                        .connection(conn)
                        .update(contact);
                })
        );
    }

    // #### 5-3-2. UPDATE multiple rows
    private void example5_3_2() {
        logger.info("example5_3_2");

        Transaction.execute(conn -> {
            List<Contact> contacts = new ArrayList<>();
            new Sql<>(Contact.class)
                .where("{lastName}={}", "Apple")
                .connection(conn)
                .select(contact -> {
                    contact.lastName = "Apfel";
                    contacts.add(contact);
                });
            new Sql<>(Contact.class)
                .connection(conn)
                .update(contacts);
        });
    }

    // #### 5-3-3. UPDATE with a Condition and selection of columns
    private void example5_3_3() {
        logger.info("example5_3_3");

        Contact contact = new Contact();
        contact.lastName = "Pomme";
        Transaction.execute(conn ->
            new Sql<>(Contact.class)
                .where("{lastName}={}", "Apfel")
                .columns("lastName")
                .connection(conn)
                .update(contact)
        );
    }

    // #### 5-3-4. UPDATE all rows
    private void example5_3_4() {
        logger.info("example5_3_4");

        Contact contact = new Contact();
        Transaction.execute(conn ->
            new Sql<>(Contact.class)
                .where(Condition.ALL)
                .columns("birthday")
                .connection(conn)
                .update(contact)
        );
    }

    // #### 5-4-1. DELETE 1 row
    private void example5_4_1() {
        logger.info("example5_4_1");

        Transaction.execute(conn ->
            new Sql<>(Contact.class)
                .where("{id}={}", 1)
                .connection(conn)
                .select()
                .ifPresent(contact ->
                    new Sql<>(Contact.class)
                        .connection(conn)
                        .delete(contact)
                )
        );
    }

    // #### 5-4-2. DELETE multiple rows
    private void example5_4_2() {
        logger.info("example5_4_2");

        Transaction.execute(conn -> {
            List<Contact> contacts = new ArrayList<>();
            new Sql<>(Contact.class)
                .where("{lastName}={}", "Pomme")
                .connection(conn)
                .select(contacts::add);
            new Sql<>(Contact.class)
                .connection(conn)
                .delete(contacts);
        });
    }

    // #### 5-4-3. DELETE with a Condition
    private void example5_4_3() {
        logger.info("example5_4_3");

        Transaction.execute(conn ->
            new Sql<>(Contact.class)
                .where("{lastName}={}", "Orange")
                .connection(conn)
                .delete()
        );
    }

    // #### 5-4-4. DELETE all rows
    private void example5_4_4() {
        logger.info("example5_4_4");

        Transaction.execute(conn ->
            new Sql<>(Phone.class)
                .where(Condition.ALL)
                .connection(conn)
                .delete()
        );
    }
}
