// JavaDocExamples2.groovy
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
import org.lightsleep.entity.*
import org.lightsleep.example.Common
import org.lightsleep.example.groovy.entity2.*
//import org.lightsleep.example.groovy.entity3.*

/**
 * JavaDocExamples2
 */
public class JavaDocExamples2 extends Common {
	public static void main(String[] args) {
		def examples = new JavaDocExamples2()
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
     def personOpt = new Sql<>(Person).connection(it)
         .where('{id} = {}', 1)
         .select()
     personOpt.ifPresent {Person person ->
         person.setBirthday(2017, 1, 1)
         new Sql<>(Person).connection(it)
             .update(person)
     }
 }

	/**/DebugTrace.leave()
	}

	// Sql
	void sql() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person).connection(it)
         .where('{name.last} = {}', 'Apple')
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// columns 1
	void sql_columns1() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person).connection(it)
         .columns('name.last', 'name.first')
         .where('{name.last} = {}', 'Apple')
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// columns 2
	void sql_columns2() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 List<Person.Phone> phones = []
 Transaction.execute {
     new Sql<>(Person, 'C').connection(it)
         .innerJoin(Person.Phone, 'P', '{P.personId} = {C.id}')
         .columns('C.id', 'P.*')
         .<Person.Phone>select({persons << it}, {phones << it})
 }

	/**/DebugTrace.leave()
	}

	// expression
	void sql_expression() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person).connection(it)
         .expression('name.first', "'['||{name.first}||']'")
         .where('{name.last} = {}', 'Orange')
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// innerJoin
	void sql_innerJoin() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 List<Person.Phone> phones = []
 Transaction.execute {
     new Sql<>(Person, 'C').connection(it)
         .innerJoin(Person.Phone, 'P', '{P.personId} = {C.id}')
         .<Person.Phone>select({persons << it}, {phones << it})
 }

	/**/DebugTrace.leave()
	}

	// leftJoin
	void sql_leftJoin() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 List<Person.Phone> phones = []
 Transaction.execute {
     new Sql<>(Person, 'C').connection(it)
         .leftJoin(Person.Phone, 'P', '{P.personId} = {C.id}')
         .<Person.Phone>select({persons << it}, {phones << it})
 }

	/**/DebugTrace.leave()
	}

	// rightJoin
	void sql_rightJoin() {
		if (ConnectionSupplier.find().database instanceof SQLite) return // SQLite dose not support RIGHT JOIN
	/**/DebugTrace.enter()

 List<Person> persons = []
 List<Person.Phone> phones = []
 Transaction.execute {
     new Sql<>(Person, 'C').connection(it)
         .rightJoin(Person.Phone, 'P', '{P.personId} = {C.id}')
         .<Person.Phone>select({persons << it}, {phones << it})
 }

	/**/DebugTrace.leave()
	}

	// where 1
	void sql_where1() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person).connection(it)
         .where('{birthday} IS NULL')
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// where 2
	void sql_where2() {
	/**/DebugTrace.enter()

 int id = 1
 Person person
 Transaction.execute {
     person = new Sql<>(Person).connection(it)
         .where('{id} = {}', id)
         .select().orElse(null)
 }

	/**/DebugTrace.leave()
	}

	// where 3
	void sql_where3() {
	/**/DebugTrace.enter()

 Person person
 Transaction.execute {
     Person key = new Person()
     person = new Sql<>(Person).connection(it)
         .where(new PersonKey(2))
         .select().orElse(null)
 }

	/**/DebugTrace.leave()
	}

	// where 4
	void sql_where4() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person, 'C').connection(it)
         .where('EXISTS',
              new Sql<>(Person.Phone, 'P')
                  .where('{P.personId} = {C.id}')
                  .and('{P.content} LIKE {}', '0800001%')
         )
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// and
	void sql_and() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person).connection(it)
         .where('{name.last} = {}', 'Apple')
         .and('{name.first} = {}', 'Akiyo')
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// or
	void sql_or() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person).connection(it)
         .where('{name.last} = {}', 'Apple')
         .or('{name.last} = {}', 'Orange')
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// orderBy
	void sql_orderBy() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person).connection(it)
         .orderBy('{name.last}')
         .orderBy('{name.first}')
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// asc
	void sql_asc() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person).connection(it)
         .orderBy('{id}').asc()
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// desc
	void sql_desc() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person).connection(it)
         .orderBy('{id}').desc()
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// limit
	void sql_limit() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person).connection(it)
         .limit(5)
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// offset
	void sql_offset() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person).connection(it)
         .limit(5).offset(5)
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// doIf
	void sql_doIf() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     def isSQLite = it.database instanceof SQLite
     new Sql<>(Person, 'C').connection(it)
         .doIf(!isSQLite) {it.forUpdate} // SQLite dose not support FOR UPDATE
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// select 1
	void sql_select1() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person).connection(it)
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// select As 1
	@ColumnProperties([
		@ColumnProperty(property="name.first", column="firstName"),
		@ColumnProperty(property="name.last", column="lastName")
	])
	public static class PersonName {
		public final Person.Name name = new Person.Name();
	}
	private static void sql_selectAs1() {
	/**/DebugTrace.enter();

 List<PersonName> personNames = []
 Transaction.execute {
     new Sql<>(Person).connection(it)
         .selectAs(PersonName, {personNames << it})
 }

 /**/DebugTrace.print("personNames", personNames);
	/**/DebugTrace.leave();
	}

	// select 2
	void sql_select2() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 List<Person.Phone>   phones = []
 Transaction.execute {
     new Sql<>(Person, 'C').connection(it)
         .innerJoin(Person.Phone, 'P', '{P.personId} = {C.id}')
         .select({persons << it}, {phones << it})
 }

	/**/DebugTrace.leave()
	}

	// select 3
	void sql_select3() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 List<Person.Phone>   phones = []
 List<Person.Email>   emails = []
 Transaction.execute {
     new Sql<>(Person, 'C').connection(it)
         .innerJoin(Person.Phone, 'P', '{P.personId} = {C.id}')
         .innerJoin(Person.Email, 'E', '{E.personId} = {C.id}')
         .select({persons << it}, {phones << it}, {emails << it})
 }

	/**/DebugTrace.leave()
	}

	// select 4
	void sql_select4() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 List<Person.Phone>   phones = []
 List<Person.Email>   emails = []
 List<Person.Address> addresses = []
 Transaction.execute {
     new Sql<>(Person, 'C').connection(it)
         .innerJoin(Person.Phone  , 'P', '{P.personId} = {C.id}')
         .innerJoin(Person.Email  , 'E', '{E.personId} = {C.id}')
         .innerJoin(Person.Address, 'A', '{A.personId} = {C.id}')
         .select(
             {persons << it}, {phones << it}, {emails << it}, {addresses << it})
 }

	/**/DebugTrace.leave()
	}

	// select 5
	void sql_select5() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 List<Person.Phone>   phones = []
 List<Person.Email>   emails = []
 List<Person.Address> addresses = []
 List<Person.Url>     urls = []
 Transaction.execute {
     new Sql<>(Person, 'C').connection(it)
         .innerJoin(Person.Phone  , 'P', '{P.personId} = {C.id}')
         .innerJoin(Person.Email  , 'E', '{E.personId} = {C.id}')
         .innerJoin(Person.Address, 'A', '{A.personId} = {C.id}')
         .innerJoin(Person.Url    , 'U', '{U.personId} = {C.id}')
         .select(
             {persons << it}, {phones << it}, {emails << it},
             {addresses << it}, {urls << it})
 }

	/**/DebugTrace.leave()
	}

	// select 6
	void sql_select6() {
	/**/DebugTrace.enter()

 Person.Ex person
 Transaction.execute {
     person = new Sql<>(Person.Ex.targetClass(it.database)).connection(it)
         .where('{id} = {}', 1)
         .select().orElse(null)
 }

	/**/DebugTrace.print("person", person)
	/**/DebugTrace.leave()
	}

	// select As 2
	void sql_selectAs2() {
	/**/DebugTrace.enter()

 PersonName personName
 Transaction.execute {
     personName = new Sql<>(Person).connection(it)
         .where('{id}={}', 1)
         .selectAs(PersonName).orElse(null)
 }

/**/DebugTrace.print("personName", personName);
	/**/DebugTrace.leave()
	}

	// selectCount
	void sql_selectCount() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Person).connection(it)
         .selectCount()
 }

	/**/DebugTrace.leave()
	}

	// insert 1
	void sql_insert1() {
	/**/DebugTrace.enter()

		Transaction.execute {
			new Sql<>(Person).connection(it)
				.where('{id} = {}', 6)
				.delete()
		}

 int count
 Transaction.execute {
     count = new Sql<>(Person).connection(it)
         .insert(new Person(6, 'Setoka', 'Orange', 2001, 2, 1))
 }

	/**/DebugTrace.leave()
	}

	// insert 2
	void sql_insert2() {
	/**/DebugTrace.enter()

		Transaction.execute {
			new Sql<>(Person).connection(it)
				.where('{id} IN {}', [7, 8, 9])
				.delete()
		}

 int count
 Transaction.execute {
     count = new Sql<>(Person).connection(it)
         .insert([
             new Person(7, 'Harumi', 'Orange', 2001, 2, 2),
             new Person(8, 'Mihaya', 'Orange', 2001, 2, 3),
             new Person(9, 'Asumi' , 'Orange', 2001, 2, 4)
         ])
 }

	/**/DebugTrace.leave()
	}

	// update 1
	void sql_update1() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Person).connection(it)
         .update(new Person(6, 'Setoka', 'Orange', 2017, 2, 1))
 }

	/**/DebugTrace.leave()
	}

	// update 2
	void sql_update2() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Person).connection(it)
         .update([
             new Person(7, 'Harumi', 'Orange', 2017, 2, 2),
             new Person(8, 'Mihaya', 'Orange', 2017, 2, 3),
             new Person(9, 'Asumi' , 'Orange', 2017, 2, 4)
         ])
 }

	/**/DebugTrace.leave()
	}

	// delete 1
	void sql_delete1() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Person).connection(it)
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
     count = new Sql<>(Person).connection(it)
         .delete(new Person(6))
 }

		Transaction.execute {
			count = new Sql<>(Person).connection(it)
				.insert(new Person(6, 'Setoka', 'Orange', 2001, 2, 1))
		}

	/**/DebugTrace.leave()
	}

	// delete 3
	void sql_delete3() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Person).connection(it)
         .delete([new Person(7), new Person(8), new Person(9)])
 }

	/**/DebugTrace.print('count', count)

	Transaction.execute {
		new Sql<>(Person).connection(it)
			.insert([
				new Person(7, 'Harumi', 'Orange', 2001, 2, 2),
				new Person(8, 'Mihaya', 'Orange', 2001, 2, 3),
				new Person(9, 'Asumi' , 'Orange', 2001, 2, 4)
			])
	}

	/**/DebugTrace.leave()
	}

}
