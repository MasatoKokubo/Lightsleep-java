// Example.groovy
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

class Example extends Common {
	static final Logger logger = LoggerFactory.getLogger(Example.class)

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
		logger.info("readme")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact.class)
		        .where('{familyName}={}', 'Apple')
		        .or   ('{familyName}={}', 'Orange')
		        .orderBy('{familyName}')
		        .orderBy('{givenName}')
		        .select(it, {contacts << it})
		}
	}

	// ### 4. Transaction
	static void example4() {
		logger.info("example4")

		def contact = new Contact()
		contact.id = 1
		contact.familyName = 'Apple'
		contact.givenName  = 'Akane'

		// トランザクション定義例
		Transaction.execute {
		    // トランザクション内容開始
		    new Sql<>(Contact.class).insert(it, contact)

		    // トランザクション内容終了
		}
	}

	// #### 5-1-1. SELECT 1 row with an Expression condition
	static void example5_1_1() {
		logger.info("example5_1_1")

		Transaction.execute {
		    def contactOpt = new Sql<>(Contact.class)
		        .where('{id}={}', 1)
		        .select(it)
		}
	}

	// #### 5-1-2. SELECT 1 row with an Entity condition
	static void example5_1_2() {
		logger.info("example5_1_2")

		def contact = new Contact()
		contact.id = 1
		Transaction.execute {
		    def contactOpt = new Sql<>(Contact.class)
		        .where(contact)
		        .select(it)
		}
	}

	// #### 5-1-3. SELECT multiple rows with an Expression condition
	static void example5_1_3() {
		logger.info("example5_1_3")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact.class)
		        .where('{familyName}={}', 'Apple')
		        .select(it, {contacts << it})
		}
	}

	// #### 5-1-4. SELECT with a Subquery condition
	static void example5_1_4() {
		logger.info("example5_1_4")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact.class, 'C')
		        .where('EXISTS',
		            new Sql<>(Phone.class, 'P')
		                .where('{P.contactId}={C.id}')
		        )
		        .select(it, {contacts << it})
		}
	}

	// #### 5-1-5. SELECT with Expression conditions (AND)
	static void example5_1_5() {
		logger.info("example5_1_5")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact.class)
		        .where('{familyName}={}', 'Apple')
		        .and  ('{givenName}={}', 'Akane')
		        .select(it, {contacts << it})
		}
	}

	// #### 5-1-6. SELECT with Expression Condition (OR)
	static void example5_1_6() {
		logger.info("example5_1_6")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact.class)
		        .where('{familyName}={}', 'Apple')
		        .or   ('{familyName}={}', 'Orange')
		        .select(it, {contacts << it})
		}
	}

	// #### 5-1-7. SELECT with Expression conditions A AND B OR C AND D
	static void example5_1_7() {
		logger.info("example5_1_7")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact.class)
		        .where(Condition
		            .of ('{familyName}={}', 'Apple')
		            .and('{givenName}={}', 'Akane')
		        )
		        .or(Condition
		            .of ('{familyName}={}', 'Orange')
		            .and('{givenName}={}', 'Setoka')
		        )
		        .select(it, {contacts << it})
		}

	}

	// #### 5-1-8. SELECT with selection of columns
	static void example5_1_8() {
		logger.info("example5_1_8")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact.class)
		        .where('{familyName}={}', 'Apple')
		        .columns('familyName', 'givenName')
		        .select(it, {contacts << it})
		}
	}

	// #### 5-1-9. SELECT with GROUP BY and HAVING
	static void example5_1_9() {
		logger.info("example5_1_9")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact.class, 'C')
		        .columns('familyName')
		        .groupBy('{familyName}')
		        .having('COUNT({familyName})>=2')
		        .select(it, {contacts << it})
		}
	}

	// #### 5-1-10. SELECT with ORDER BY, OFFSET and LIMIT
	static void example5_1_10() {
		logger.info("example5_1_10")

		def contacts = []
		Transaction.execute {
		    new Sql<>(Contact.class)
		        .orderBy('{familyName}')
		        .orderBy('{givenName}')
		        .orderBy('{id}')
		        .offset(10).limit(5)
		        .select(it, {contacts << it})
		}
	}

	// #### 5-1-11. SELECT with FOR UPDATE
	static void example5_1_11() {
		if (Sql.getDatabase() instanceof SQLite) return
		logger.info("example5_1_11")

		Transaction.execute {
		    def contactOpt = new Sql<>(Contact.class)
		        .where('{id}={}', 1)
		        .forUpdate()
		        .select(it)
		}
	}

	// #### 5-1-12. SELECT with INNER JOIN
	static void example5_1_12() {
		logger.info("example5_1_12")

		def contacts = []
		def phones = []
		Transaction.execute {
		    new Sql<>(Contact.class, 'C')
		        .innerJoin(Phone.class, 'P', '{P.contactId}={C.id}')
		        .where('{C.id}={}', 1)
		        .select(it, {contacts << it}, {phones << it})
		}
	}

	// #### 5-1-13. SELECT with LEFT OUTER JOIN
	static void example5_1_13() {
		logger.info("example5_1_13")

		def contacts = []
		def phones = []
		Transaction.execute {
		    new Sql<>(Contact.class, 'C')
		        .leftJoin(Phone.class, 'P', '{P.contactId}={C.id}')
		        .where('{C.familyName}={}', 'Apple')
		        .select(it, {contacts << it}, {phones << it})
		}
	}

	// #### 5-1-14. SELECT with RIGHT OUTER JOIN
	static void example5_1_14() {
		if (Sql.getDatabase() instanceof SQLite) return
		logger.info("example5_1_14")

		def contacts = []
		def phones = []
		Transaction.execute {
		    new Sql<>(Contact.class, 'C')
		        .rightJoin(Phone.class, 'P', '{P.contactId}={C.id}')
		        .where('{P.label}={}', 'Main')
		        .select(it, {contacts << it}, {phones << it})
		}
	}

	// #### 5-1-15. SELECT COUNT(*)
	static void example5_1_15() {
		logger.info("example5_1_15")

		def rowCount = 0
		Transaction.execute {
		    rowCount = new Sql<>(Contact.class)
		        .where('familyName={}', 'Apple')
		        .selectCount(it)
		}
	}

	// #### 5-2-1. INSERT 1 row
	static void example5_2_1() {
		logger.info("example5_2_1")

		def contact = new Contact()
		contact.id = 1
		contact.familyName = 'Apple'
		contact.givenName = 'Akane'
		Calendar calendar = Calendar.instance
		calendar.set(2001, 1-1, 1, 0, 0, 0)
		contact.birthday = new Date(calendar.timeInMillis)

		Transaction.execute {
		    new Sql<>(Contact.class).insert(it, contact)
		}
	}

	// #### 5-2-2. INSERT multiple rows
	static void example5_2_2() {
		logger.info("example5_2_2")

		def contacts = []

		def contact = new Contact()
		contact.id = 2; contact.familyName = 'Apple'; contact.givenName = 'Yukari'
		def calendar = Calendar.instance
		calendar.set(2001, 1-1, 2, 0, 0, 0)
		contact.birthday = new Date(calendar.timeInMillis)
		contacts << contact

		contact = new Contact()
		contact.id = 3; contact.familyName = 'Apple'; contact.givenName = 'Azusa'
		calendar = Calendar.instance
		calendar.set(2001, 1-1, 3, 0, 0, 0)
		contact.birthday = new Date(calendar.timeInMillis)
		contacts << contact

		Transaction.execute {
		    new Sql<>(Contact.class).insert(it, contacts)
		}
	}

	// #### 5-3-1. UPDATE 1 row
	static void example5_3_1() {
		logger.info("example5_3_1")

		Transaction.execute {
		    new Sql<>(Contact.class)
		        .where('{id}={}', 1)
		        .select(it)
		        .ifPresent {Contact contact ->
		            contact.givenName = 'Akiyo'
		            new Sql<>(Contact.class).update(it, contact)
		        }
		}
	}

	// #### 5-3-2. UPDATE multiple rows
	static void example5_3_2() {
		logger.info("example5_3_2")

		Transaction.execute {
		    def contacts = []
		    new Sql<>(Contact.class)
		        .where('{familyName}={}', 'Apple')
		        .select(it, {Contact contact ->
		            contact.familyName = 'Apfel'
		            contacts << contact
		        })
		    new Sql<>(Contact.class).update(it, contacts)
		}
	}

	// #### 5-3-3. UPDATE with a Condition and selection of columns
	static void example5_3_3() {
		logger.info("example5_3_3")

		def contact = new Contact()
		contact.familyName = 'Pomme'
		Transaction.execute {
		    new Sql<>(Contact.class)
		        .where('{familyName}={}', 'Apfel')
		        .columns('familyName')
		        .update(it, contact)
		}
	}

	// #### 5-3-4. UPDATE all rows
	static void example5_3_4() {
		logger.info("example5_3_4")

		def contact = new Contact()
		Transaction.execute {
		    new Sql<>(Contact.class)
		        .where(Condition.ALL)
		        .columns('birthday')
		        .update(it, contact)
		}
	}

	// #### 5-4-1. DELETE 1 row
	static void example5_4_1() {
		logger.info("example5_4_1")

		Transaction.execute {
		    new Sql<>(Contact.class)
		        .where('{id}={}', 1)
		        .select(it)
		        .ifPresent {contact ->
		            new Sql<>(Contact.class).delete(it, contact)
		        }
		}
	}

	// #### 5-4-2. DELETE multiple rows
	static void example5_4_2() {
		logger.info("example5_4_2")

		Transaction.execute {
		    def contacts = []
		    new Sql<>(Contact.class)
		        .where('{familyName}={}', 'Pomme')
		        .select(it, {contacts << it})
		    new Sql<>(Contact.class).delete(it, contacts)
		}
	}

	// #### 5-4-3. DELETE with a Condition
	static void example5_4_3() {
		logger.info("example5_4_3")

		Transaction.execute {
		    new Sql<>(Contact.class)
		        .where('{familyName}={}', 'Orange')
		        .delete(it)
		}
	}

	// #### 5-4-4. DELETE all rows
	static void example5_4_4() {
		logger.info("example5_4_4")

		Transaction.execute {
		    new Sql<>(Phone.class)
		        .where(Condition.ALL)
		        .delete(it)
		}
	}
}
