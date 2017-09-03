// ManualExamples.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy

import java.sql.Date
import java.util.ArrayList
import java.util.Calendar
import java.util.List
import java.util.Optional

import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.database.*
import org.lightsleep.example.Common
import org.lightsleep.example.groovy.entity.*
import org.lightsleep.logger.*

class ManualExamples extends Common {
	static final Logger logger = LoggerFactory.getLogger(ManualExamples)

	static void main (String[] args) {
			try {
			init1()
			example4()

			init1()
			init2()
			readme()
			example5_1_1()
			example5_1_2()
			example5_1_3()
			example5_1_4()
			example5_1_5()
			example5_1_6()
			example5_1_7()
			example5_1_8()
			example5_1_9()
			example5_1_10()
			example5_1_11()
			example5_1_12()
			example5_1_13()
			example5_1_14()
			example5_1_15()

			init1()
			example5_2_1()
			example5_2_2()

			example5_3_1()
			example5_3_2()
			example5_3_3()
			example5_3_4()

			example5_4_1()
			example5_4_2()
			example5_4_3()
			example5_4_4()
		}
		catch (Exception e) {
			e.printStackTrace()
		}
	}

	// README
	private static void readme() {
		logger.info("README")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact).connection(it)
		        .where('{lastName}={}', 'Apple')
		        .or   ('{lastName}={}', 'Orange')
		        .orderBy('{lastName}')
		        .orderBy('{firstName}')
		        .select({contacts << it})
		}
	}

	// ### 4. Transaction
	static void example4() {
		logger.info("example4")

	    def contact = new Contact(1, "Akane", "Apple")

	    // An example of transaction
	    Transaction.execute {
	        // Start of transaction body
	        new Sql<>(Contact).connection(it)
	            .insert(contact)

	        // End of transaction body
	    }
	}

	// #### 5-1-1. SELECT 1 row with an Expression condition
	static void example5_1_1() {
		logger.info("example5_1_1")

		Transaction.execute {
		    def contactOpt = new Sql<>(Contact).connection(it)
		        .where('{id}={}', 1)
		        .select()
		}
	}

	// #### 5-1-2. SELECT 1 row with an Entity condition
	static void example5_1_2() {
		logger.info("example5_1_2")

	    def contact = new Contact(1)
	    Transaction.execute {
	        def contactOpt = new Sql<>(Contact).connection(it)
	            .where(contact)
	            .select()
	    }
	}

	// #### 5-1-3. SELECT multiple rows with an Expression condition
	static void example5_1_3() {
		logger.info("example5_1_3")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact).connection(it)
		        .where('{lastName}={}', 'Apple')
		        .select({contacts << it})
		}
	}

	// #### 5-1-4. SELECT with a Subquery condition
	static void example5_1_4() {
		logger.info("example5_1_4")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact, 'C').connection(it)
		        .where('EXISTS',
		            new Sql<>(Phone, 'P')
		                .where('{P.contactId}={C.id}')
		        )
		        .select({contacts << it})
		}
	}

	// #### 5-1-5. SELECT with Expression conditions (AND)
	static void example5_1_5() {
		logger.info("example5_1_5")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact).connection(it)
		        .where('{lastName}={}', 'Apple')
		        .and  ('{firstName}={}', 'Akane')
		        .select({contacts << it})
		}
	}

	// #### 5-1-6. SELECT with Expression Condition (OR)
	static void example5_1_6() {
		logger.info("example5_1_6")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact).connection(it)
		        .where('{lastName}={}', 'Apple')
		        .or   ('{lastName}={}', 'Orange')
		        .select({contacts << it})
		}
	}

	// #### 5-1-7. SELECT with Expression conditions A AND B OR C AND D
	static void example5_1_7() {
		logger.info("example5_1_7")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact).connection(it)
		        .where(Condition
		            .of ('{lastName}={}', 'Apple')
		            .and('{firstName}={}', 'Akane')
		        )
		        .or(Condition
		            .of ('{lastName}={}', 'Orange')
		            .and('{firstName}={}', 'Setoka')
		        )
		        .select({contacts << it})
		}

	}

	// #### 5-1-8. SELECT with selection of columns
	static void example5_1_8() {
		logger.info("example5_1_8")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact).connection(it)
		        .where('{lastName}={}', 'Apple')
		        .columns('lastName', 'firstName')
		        .select({contacts << it})
		}
	}

	// #### 5-1-9. SELECT with GROUP BY and HAVING
	static void example5_1_9() {
		logger.info("example5_1_9")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact, 'C').connection(it)
		        .columns('lastName')
		        .groupBy('{lastName}')
		        .having('COUNT({lastName})>=2')
		        .select({contacts << it})
		}
	}

	// #### 5-1-10. SELECT with ORDER BY, OFFSET and LIMIT
	static void example5_1_10() {
		logger.info("example5_1_10")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact).connection(it)
		        .orderBy('{lastName}')
		        .orderBy('{firstName}')
		        .orderBy('{id}')
		        .offset(10).limit(5)
		        .select({contacts << it})
		}
	}

	// #### 5-1-11. SELECT with FOR UPDATE
	static void example5_1_11() {
		if (Sql.getDatabase() instanceof SQLite) return
		logger.info("example5_1_11")

		Transaction.execute {
		    def contactOpt = new Sql<>(Contact).connection(it)
		        .where('{id}={}', 1)
		        .forUpdate()
		        .select()
		}
	}

	// #### 5-1-12. SELECT with INNER JOIN
	static void example5_1_12() {
		logger.info("example5_1_12")

		def contacts = []
		def phones = []
		Transaction.execute {
		    new Sql<>(Contact, 'C').connection(it)
		        .innerJoin(Phone, 'P', '{P.contactId}={C.id}')
		        .where('{C.id}={}', 1)
		        .select({contacts << it}, {phones << it})
		}
	}

	// #### 5-1-13. SELECT with LEFT OUTER JOIN
	static void example5_1_13() {
		logger.info("example5_1_13")

		def contacts = []
		def phones = []
		Transaction.execute {
		    new Sql<>(Contact, 'C').connection(it)
		        .leftJoin(Phone, 'P', '{P.contactId}={C.id}')
		        .where('{C.lastName}={}', 'Apple')
		        .select({contacts << it}, {phones << it})
		}
	}

	// #### 5-1-14. SELECT with RIGHT OUTER JOIN
	static void example5_1_14() {
		if (Sql.getDatabase() instanceof SQLite) return
		logger.info("example5_1_14")

		def contacts = []
		def phones = []
		Transaction.execute {
		    new Sql<>(Contact, 'C').connection(it)
		        .rightJoin(Phone, 'P', '{P.contactId}={C.id}')
		        .where('{P.label}={}', 'Main')
		        .select({contacts << it}, {phones << it})
		}
	}

	// #### 5-1-15. SELECT COUNT(*)
	static void example5_1_15() {
		logger.info("example5_1_15")

		def count = 0
		Transaction.execute {
		    count = new Sql<>(Contact).connection(it)
		        .where('{lastName}={}', 'Apple')
		        .selectCount()
		}
	}

	// #### 5-2-1. INSERT 1 row
	static void example5_2_1() {
		logger.info("example5_2_1")

		Transaction.execute {
		    new Sql<>(Contact).connection(it)
		        .insert(new Contact(1, "Akane", "Apple", 2001, 1, 1))
		}
	}

	// #### 5-2-2. INSERT multiple rows
	static void example5_2_2() {
		logger.info("example5_2_2")

	    Transaction.execute {
	        new Sql<>(Contact).connection(it)
		        .insert([
		            new Contact(2, "Yukari", "Apple", 2001, 1, 2),
		            new Contact(3, "Azusa", "Apple", 2001, 1, 3)
		        ])
	    }
	}

	// #### 5-3-1. UPDATE 1 row
	static void example5_3_1() {
		logger.info("example5_3_1")

		Transaction.execute {conn ->
		    new Sql<>(Contact).connection(conn)
		        .where('{id}={}', 1)
		        .select()
		        .ifPresent {Contact contact ->
		            contact.firstName = 'Akiyo'
		            new Sql<>(Contact).connection(conn)
		                .update(contact)
		        }
		}
	}

	// #### 5-3-2. UPDATE multiple rows
	static void example5_3_2() {
		logger.info("example5_3_2")

		Transaction.execute {
		    def contacts = []
		    new Sql<>(Contact).connection(it)
		        .where('{lastName}={}', 'Apple')
		        .select({Contact contact ->
		            contact.lastName = 'Apfel'
		            contacts << contact
		        })
		    new Sql<>(Contact).connection(it)
	            .update(contacts)
		}
	}

	// #### 5-3-3. UPDATE with a Condition and selection of columns
	static void example5_3_3() {
		logger.info("example5_3_3")

		def contact = new Contact()
		contact.lastName = 'Pomme'
		Transaction.execute {
		    new Sql<>(Contact).connection(it)
		        .where('{lastName}={}', 'Apfel')
		        .columns('lastName')
		        .update(contact)
		}
	}

	// #### 5-3-4. UPDATE all rows
	static void example5_3_4() {
		logger.info("example5_3_4")

		def contact = new Contact()
		Transaction.execute {
		    new Sql<>(Contact).connection(it)
		        .where(Condition.ALL)
		        .columns('birthday')
		        .update(contact)
		}
	}

	// #### 5-4-1. DELETE 1 row
	static void example5_4_1() {
		logger.info("example5_4_1")

		Transaction.execute {conn ->
		    new Sql<>(Contact).connection(conn)
		        .where('{id}={}', 1)
		        .select()
		        .ifPresent {contact ->
		            new Sql<>(Contact).connection(conn)
	                    .delete(contact)
		        }
		}
	}

	// #### 5-4-2. DELETE multiple rows
	static void example5_4_2() {
		logger.info("example5_4_2")

		Transaction.execute {
		    def contacts = []
		    new Sql<>(Contact).connection(it)
		        .where('{lastName}={}', 'Pomme')
		        .select({contacts << it})
		    new Sql<>(Contact).connection(it)
	            .delete(contacts)
		}
	}

	// #### 5-4-3. DELETE with a Condition
	static void example5_4_3() {
		logger.info("example5_4_3")

		Transaction.execute {
		    new Sql<>(Contact).connection(it)
		        .where('{lastName}={}', 'Orange')
		        .delete()
		}
	}

	// #### 5-4-4. DELETE all rows
	static void example5_4_4() {
		logger.info("example5_4_4")

		Transaction.execute {
		    new Sql<>(Phone).connection(it)
		        .where(Condition.ALL)
		        .delete()
		}
	}
}
