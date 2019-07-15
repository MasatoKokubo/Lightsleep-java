// ManualExamples.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy

import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.connection.*
import org.lightsleep.database.*
import org.lightsleep.example.Common
import org.lightsleep.example.groovy.entity.*
import org.lightsleep.logger.*

public class ManualExamples extends Common {
    private static final Logger logger = LoggerFactory.getLogger(ManualExamples)

    public static void main (String[] args) {
        def examples = new ManualExamples()
        try {
            examples.init1()
            examples.example4()

            examples.init1()
            examples.init2()
            examples.readme()
            examples.example5_1_1()
            examples.example5_1_2()
            examples.example5_1_3()
            examples.example5_1_4()
            examples.example5_1_5()
            examples.example5_1_6()
            examples.example5_1_7()
            examples.example5_1_8()
            examples.example5_1_9()
            examples.example5_1_10()
            examples.example5_1_11()
            examples.example5_1_12()
            examples.example5_1_13()
            examples.example5_1_14()
            examples.example5_1_15()
			examples.example5_1_16()
			examples.example5_1_17()
			
            examples.init1()
            examples.example5_2_1()
            examples.example5_2_2()

            examples.example5_3_1()
            examples.example5_3_2()
            examples.example5_3_3()
            examples.example5_3_4()

            examples.example5_4_1()
            examples.example5_4_2()
            examples.example5_4_3()
            examples.example5_4_4()
        }
        catch (Exception e) {
            e.printStackTrace()
        }
    }

    // README
    private static void readme() {
        logger.info('README')

        def contacts = []
        Transaction.execute {
            new Sql<>(Contact)
                .where('{lastName}={}', 'Apple')
                .or   ('{lastName}={}', 'Orange')
                .orderBy('{lastName}')
                .orderBy('{firstName}')
                .connection(it)
                .select({contacts << it})
        }
    }

    // ### 4. Transaction
    void example4() {
        logger.info('example4')

        def contact = new Contact(1, 'Akane', 'Apple')

        // An example of transaction
        Transaction.execute {
            // Start of transaction body
            new Sql<>(Contact)
                .connection(it)
                .insert(contact)

            // End of transaction body
        }
    }

    // #### 5-1-1. SELECT 1 row with an Expression condition
    void example5_1_1() {
        logger.info('example5_1_1')

        Transaction.execute {
            def contactOpt = new Sql<>(Contact)
                .where('{id}={}', 1)
                .connection(it)
                .select()
        }
    }

    // #### 5-1-2. SELECT 1 row with an Entity condition
    void example5_1_2() {
        logger.info('example5_1_2')

        def contact = new Contact(1)
        Transaction.execute {
            def contactOpt = new Sql<>(Contact)
                .where(contact)
                .connection(it)
                .select()
        }
    }

    // #### 5-1-3. SELECT multiple rows with an Expression condition
    void example5_1_3() {
        logger.info('example5_1_3')

        def contacts = []
        Transaction.execute {
            new Sql<>(Contact)
                .where('{lastName}={}', 'Apple')
                .connection(it)
                .select({contacts << it})
        }
    }

    // #### 5-1-4. SELECT with a Subquery condition
    void example5_1_4() {
        logger.info('example5_1_4')

        def contacts = []
        Transaction.execute {
            new Sql<>(Contact, 'C')
                .where('EXISTS',
                    new Sql<>(Phone, 'P')
                        .where('{P.contactId}={C.id}')
                )
                .connection(it)
                .select({contacts << it})
        }
    }

    // #### 5-1-5. SELECT with Expression conditions (AND)
    void example5_1_5() {
        logger.info('example5_1_5')

        def contacts = []
        Transaction.execute {
            new Sql<>(Contact)
                .where('{lastName}={}', 'Apple')
                .and  ('{firstName}={}', 'Akane')
                .connection(it)
                .select({contacts << it})
        }
    }

    // #### 5-1-6. SELECT with Expression Condition (OR)
    void example5_1_6() {
        logger.info('example5_1_6')

        def contacts = []
        Transaction.execute {
            new Sql<>(Contact)
                .where('{lastName}={}', 'Apple')
                .or   ('{lastName}={}', 'Orange')
                .connection(it)
                .select({contacts << it})
        }
    }

    // #### 5-1-7. SELECT with Expression conditions A AND B OR C AND D
    void example5_1_7() {
        logger.info('example5_1_7')

        def contacts = []
        Transaction.execute {
            new Sql<>(Contact)
                .where(Condition
                    .of ('{lastName}={}', 'Apple')
                    .and('{firstName}={}', 'Akane')
                )
                .or(Condition
                    .of ('{lastName}={}', 'Orange')
                    .and('{firstName}={}', 'Setoka')
                )
                .connection(it)
                .select({contacts << it})
        }

    }

    // #### 5-1-8. SELECT with selection of columns
    void example5_1_8() {
        logger.info('example5_1_8')

        def contacts = []
        Transaction.execute {
            new Sql<>(Contact)
                .where('{lastName}={}', 'Apple')
                .columns('lastName', 'firstName')
                .connection(it)
                .select({contacts << it})
        }
    }

    // #### 5-1-9. SELECT with GROUP BY and HAVING
    void example5_1_9() {
        logger.info('example5_1_9')

        def contacts = []
        Transaction.execute {
            new Sql<>(Contact, 'C')
                .columns('lastName')
                .groupBy('{lastName}')
                .having('COUNT({lastName})>=2')
                .connection(it)
                .select({contacts << it})
        }
    }

    // #### 5-1-10. SELECT with ORDER BY, OFFSET and LIMIT
    void example5_1_10() {
        logger.info('example5_1_10')

        def contacts = []
        Transaction.execute {
            new Sql<>(Contact)
                .orderBy('{lastName}')
                .orderBy('{firstName}')
                .orderBy('{id}')
                .offset(10).limit(5)
                .connection(it)
                .select({contacts << it})
        }
    }

    // #### 5-1-11. SELECT with FOR UPDATE
    void example5_1_11() {
        if (ConnectionSupplier.find().database instanceof SQLite) return
        logger.info('example5_1_11')

        Transaction.execute {
            def contactOpt = new Sql<>(Contact)
                .where('{id}={}', 1)
                .forUpdate()
                .connection(it)
                .select()
        }
    }

    // #### 5-1-12. SELECT with INNER JOIN
    void example5_1_12() {
        logger.info('example5_1_12')

        def contacts = []
        def phones = []
        Transaction.execute {
            new Sql<>(Contact, 'C')
                .innerJoin(Phone, 'P', '{P.contactId}={C.id}')
                .where('{C.id}={}', 1)
                .connection(it)
                .select({contacts << it}, {phones << it})
        }
    }

    // #### 5-1-13. SELECT with LEFT OUTER JOIN
    void example5_1_13() {
        logger.info('example5_1_13')

        def contacts = []
        def phones = []
        Transaction.execute {
            new Sql<>(Contact, 'C')
                .leftJoin(Phone, 'P', '{P.contactId}={C.id}')
                .where('{C.lastName}={}', 'Apple')
                .connection(it)
                .select({contacts << it}, {phones << it})
        }
    }

    // #### 5-1-14. SELECT with RIGHT OUTER JOIN
    void example5_1_14() {
        if (ConnectionSupplier.find().database instanceof SQLite) return
        logger.info('example5_1_14')

        def contacts = []
        def phones = []
        Transaction.execute {
            new Sql<>(Contact, 'C')
                .rightJoin(Phone, 'P', '{P.contactId}={C.id}')
                .where('{P.label}={}', 'Main')
                .connection(it)
                .select({contacts << it}, {phones << it})
        }
    }

    // #### 5-1-15. SELECT COUNT(*)
    void example5_1_15() {
        logger.info('example5_1_15')

        def count = 0
        Transaction.execute {
            count = new Sql<>(Contact)
                .where('{lastName}={}', 'Apple')
                .connection(it)
                .selectCount()
        }
    }

    // #### 5-1-16. SELECT FROM Clause Subquery
    void example5_1_16() {
        logger.info('example5_1_16')

        def contacts = []
        Transaction.execute {
            def contactClass = Contact.Ex.targetClass(it.database)
            new Sql<>(contactClass)
                .from(new Sql<>(contactClass))
                .where('{fullName}={}', 'Akane Apple')
                .orderBy('{fullName}')
                .connection(it)
                .select({contacts << it})
        }

        logger.info('  row count=' + contacts.size())
    }

    // #### 5-1-17. SELECT UNION, UNION ALL
    void example5_1_17() {
        logger.info('example5_1_17')

        def features = []
        def targetFirstName = 'Setoka'
        def targetLastName = 'Orange'
        Transaction.execute {
            new Sql<>(ContactFeature, 'F')
                .columns(ContactFeature)
                .unionAll(new Sql<>(Address)
                    .innerJoin(Contact, 'C', '{C.id}={F.contactId}')
                    .where('{C.firstName}={}', targetFirstName)
                    .and('{C.lastName}={}', targetLastName)
                    .and('{F.featureIndex}={}', 1)
                )
                .unionAll(new Sql<>(Email)
                    .innerJoin(Contact, 'C', '{C.id}={F.contactId}')
                    .where('{C.firstName}={}', targetFirstName)
                    .and('{C.lastName}={}', targetLastName)
                    .and('{F.featureIndex}={}', 1)
                )
                .unionAll(new Sql<>(Phone)
                    .innerJoin(Contact, 'C', '{C.id}={F.contactId}')
                    .where('{C.firstName}={}', targetFirstName)
                    .and('{C.lastName}={}', targetLastName)
                    .and('{F.featureIndex}={}', 1)
                )
                .unionAll(new Sql<>(Url)
                    .innerJoin(Contact, 'C', '{C.id}={F.contactId}')
                    .where('{C.firstName}={}', targetFirstName)
                    .and('{C.lastName}={}', targetLastName)
                    .and('{F.featureIndex}={}', 1)
                )
                .orderBy('{F_label}')
                .connection(it)
                .select({features << it})
        }

        logger.info('  row count=' + features.size())
    }

    // #### 5-2-1. INSERT 1 row
    void example5_2_1() {
        logger.info('example5_2_1')

        Transaction.execute {
            new Sql<>(Contact)
                .connection(it)
                .insert(new Contact(1, 'Akane', 'Apple', 2001, 1, 1))
        }
    }

    // #### 5-2-2. INSERT multiple rows
    void example5_2_2() {
        logger.info('example5_2_2')

        Transaction.execute {
            new Sql<>(Contact)
                .connection(it)
                .insert([
                    new Contact(2, 'Yukari', 'Apple', 2001, 1, 2),
                    new Contact(3, 'Azusa', 'Apple', 2001, 1, 3)
                ])
        }
    }

    // #### 5-3-1. UPDATE 1 row
    void example5_3_1() {
        logger.info('example5_3_1')

        Transaction.execute {
            new Sql<>(Contact)
                .where('{id}={}', 1)
                .connection(it)
                .select()
                .ifPresent {Contact contact ->
                    contact.firstName = 'Akiyo'
                    new Sql<>(Contact)
                        .connection(it)
                        .update(contact)
                }
        }
    }

    // #### 5-3-2. UPDATE multiple rows
    void example5_3_2() {
        logger.info('example5_3_2')

        Transaction.execute {
            def contacts = []
            new Sql<>(Contact)
                .where('{lastName}={}', 'Apple')
                .connection(it)
                .select({Contact contact ->
                    contact.lastName = 'Apfel'
                    contacts << contact
                })
            new Sql<>(Contact)
                .connection(it)
                .update(contacts)
        }
    }

    // #### 5-3-3. UPDATE with a Condition and selection of columns
    void example5_3_3() {
        logger.info('example5_3_3')

        def contact = new Contact()
        contact.lastName = 'Pomme'
        Transaction.execute {
            new Sql<>(Contact)
                .where('{lastName}={}', 'Apfel')
                .columns('lastName')
                .connection(it)
                .update(contact)
        }
    }

    // #### 5-3-4. UPDATE all rows
    void example5_3_4() {
        logger.info('example5_3_4')

        def contact = new Contact()
        Transaction.execute {
            new Sql<>(Contact)
                .where(Condition.ALL)
                .columns('birthday')
                .connection(it)
                .update(contact)
        }
    }

    // #### 5-4-1. DELETE 1 row
    void example5_4_1() {
        logger.info('example5_4_1')

        Transaction.execute {
            new Sql<>(Contact)
                .where('{id}={}', 1)
                .connection(it)
                .select()
                .ifPresent {contact ->
                    new Sql<>(Contact)
                        .connection(it)
                        .delete(contact)
                }
        }
    }

    // #### 5-4-2. DELETE multiple rows
    void example5_4_2() {
        logger.info('example5_4_2')

        Transaction.execute {
            def contacts = []
            new Sql<>(Contact)
                .where('{lastName}={}', 'Pomme')
                .connection(it)
                .select({contacts << it})
            new Sql<>(Contact)
                .connection(it)
                .delete(contacts)
        }
    }

    // #### 5-4-3. DELETE with a Condition
    void example5_4_3() {
        logger.info('example5_4_3')

        Transaction.execute {
            new Sql<>(Contact)
                .where('{lastName}={}', 'Orange')
                .connection(it)
                .delete()
        }
    }

    // #### 5-4-4. DELETE all rows
    void example5_4_4() {
        logger.info('example5_4_4')

        Transaction.execute {
            new Sql<>(Phone)
                .where(Condition.ALL)
                .connection(it)
                .delete()
        }
    }
}
