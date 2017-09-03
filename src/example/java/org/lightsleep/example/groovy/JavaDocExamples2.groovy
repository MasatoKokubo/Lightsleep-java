// JavaDocExamples2.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy

import java.util.ArrayList
import java.util.List
import java.util.Optional

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.Condition
import org.lightsleep.database.SQLite
import org.lightsleep.entity.ColumnProperties
import org.lightsleep.entity.ColumnProperty
import org.lightsleep.example.Common
import org.lightsleep.example.groovy.entity2.*
//import org.lightsleep.example.groovy.entity3.*

/**
 * JavaDocExamples2
 */
public class JavaDocExamples2 extends Common {
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
     def personOpt = new Sql<>(Person)
         .where('{id} = {}', 1)
         .connection(it)
         .select()
     personOpt.ifPresent {Person person ->
         person.setBirthday(2017, 1, 1)
         new Sql<>(Person)
             .connection(it)
             .update(person)
     }
 }

	/**/DebugTrace.leave()
	}

	// Sql
	static void sql() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person)
         .where('{name.last} = {}', 'Apple')
         .connection(it)
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// columns 1
	static void sql_columns1() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person)
         .columns('name.last', 'name.first')
         .where('{name.last} = {}', 'Apple')
         .connection(it)
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// columns 2
	static void sql_columns2() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 List<Person.Phone> phones = []
 Transaction.execute {
     new Sql<>(Person, 'C')
         .innerJoin(Person.Phone, 'P', '{P.personId} = {C.id}')
         .columns('C.id', 'P.*')
         .connection(it)
         .<Person.Phone>select({persons << it}, {phones << it})
 }

	/**/DebugTrace.leave()
	}

	// expression
	static void sql_expression() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person)
         .expression('name.first', "'['||{name.first}||']'")
         .where('{name.last} = {}', 'Orange')
         .connection(it)
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// innerJoin
	static void sql_innerJoin() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 List<Person.Phone> phones = []
 Transaction.execute {
     new Sql<>(Person, 'C')
         .innerJoin(Person.Phone, 'P', '{P.personId} = {C.id}')
         .connection(it)
         .<Person.Phone>select({persons << it}, {phones << it})
 }

	/**/DebugTrace.leave()
	}

	// leftJoin
	static void sql_leftJoin() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 List<Person.Phone> phones = []
 Transaction.execute {
     new Sql<>(Person, 'C')
         .leftJoin(Person.Phone, 'P', '{P.personId} = {C.id}')
         .connection(it)
         .<Person.Phone>select({persons << it}, {phones << it})
 }

	/**/DebugTrace.leave()
	}

	// rightJoin
	static void sql_rightJoin() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 List<Person.Phone> phones = []
 Transaction.execute {
     new Sql<>(Person, 'C')
         .rightJoin(Person.Phone, 'P', '{P.personId} = {C.id}')
         .connection(it)
         .<Person.Phone>select({persons << it}, {phones << it})
 }

	/**/DebugTrace.leave()
	}

	// where 1
	static void sql_where1() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person)
         .where('{birthday} IS NULL')
         .connection(it)
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// where 2
	static void sql_where2() {
	/**/DebugTrace.enter()

 int id = 1
 Person person
 Transaction.execute {
     person = new Sql<>(Person)
         .where('{id} = {}', id)
         .connection(it)
         .select().orElse(null)
 }

	/**/DebugTrace.leave()
	}

	// where 3
	static void sql_where3() {
	/**/DebugTrace.enter()

 Person person
 Transaction.execute {
     Person key = new Person()
     person = new Sql<>(Person)
         .where(new PersonKey(2))
         .connection(it)
         .select().orElse(null)
 }

	/**/DebugTrace.leave()
	}

	// where 4
	static void sql_where4() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person, 'C')
         .where('EXISTS',
              new Sql<>(Person.Phone, 'P')
                  .where('{P.personId} = {C.id}')
                  .and('{P.content} LIKE {}', '0800001%')
         )
         .connection(it)
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// and
	static void sql_and() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person)
         .where('{name.last} = {}', 'Apple')
         .and('{name.first} = {}', 'Akiyo')
         .connection(it)
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// or
	static void sql_or() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person)
         .where('{name.last} = {}', 'Apple')
         .or('{name.last} = {}', 'Orange')
         .connection(it)
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// orderBy
	static void sql_orderBy() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person)
         .orderBy('{name.last}')
         .orderBy('{name.first}')
         .connection(it)
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// asc
	static void sql_asc() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person)
         .orderBy('{id}').asc()
         .connection(it)
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// desc
	static void sql_desc() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person)
         .orderBy('{id}').desc()
         .connection(it)
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// limit
	static void sql_limit() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person)
         .limit(5)
         .connection(it)
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// offset
	static void sql_offset() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person)
         .limit(5).offset(5)
         .connection(it)
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// doIf
	static void sql_doIf() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person, 'C')
         .doIf(!(Sql.database instanceof SQLite)) {it.forUpdate}
         .connection(it)
         .select({persons << it})
 }

	/**/DebugTrace.leave()
	}

	// select 1
	static void sql_select1() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 Transaction.execute {
     new Sql<>(Person)
         .connection(it)
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
     new Sql<>(Person)
         .connection(it)
         .selectAs(PersonName, {personNames << it})
 }

 /**/DebugTrace.print("personNames", personNames);
	/**/DebugTrace.leave();
	}

	// select 2
	static void sql_select2() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 List<Person.Phone>   phones = []
 Transaction.execute {
     new Sql<>(Person, 'C')
         .innerJoin(Person.Phone, 'P', '{P.personId} = {C.id}')
         .connection(it)
         .select({persons << it}, {phones << it})
 }

	/**/DebugTrace.leave()
	}

	// select 3
	static void sql_select3() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 List<Person.Phone>   phones = []
 List<Person.Email>   emails = []
 Transaction.execute {
     new Sql<>(Person, 'C')
         .innerJoin(Person.Phone, 'P', '{P.personId} = {C.id}')
         .innerJoin(Person.Email, 'E', '{E.personId} = {C.id}')
         .connection(it)
         .select({persons << it}, {phones << it}, {emails << it})
 }

	/**/DebugTrace.leave()
	}

	// select 4
	static void sql_select4() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 List<Person.Phone>   phones = []
 List<Person.Email>   emails = []
 List<Person.Address> addresses = []
 Transaction.execute {
     new Sql<>(Person, 'C')
         .innerJoin(Person.Phone  , 'P', '{P.personId} = {C.id}')
         .innerJoin(Person.Email  , 'E', '{E.personId} = {C.id}')
         .innerJoin(Person.Address, 'A', '{A.personId} = {C.id}')
         .connection(it)
         .select(
             {persons << it}, {phones << it}, {emails << it}, {addresses << it})
 }

	/**/DebugTrace.leave()
	}

	// select 5
	static void sql_select5() {
	/**/DebugTrace.enter()

 List<Person> persons = []
 List<Person.Phone>   phones = []
 List<Person.Email>   emails = []
 List<Person.Address> addresses = []
 List<Person.Url>     urls = []
 Transaction.execute {
     new Sql<>(Person, 'C')
         .innerJoin(Person.Phone  , 'P', '{P.personId} = {C.id}')
         .innerJoin(Person.Email  , 'E', '{E.personId} = {C.id}')
         .innerJoin(Person.Address, 'A', '{A.personId} = {C.id}')
         .innerJoin(Person.Url    , 'U', '{U.personId} = {C.id}')
         .connection(it)
         .select(
             {persons << it}, {phones << it}, {emails << it},
             {addresses << it}, {urls << it})
 }

	/**/DebugTrace.leave()
	}

	// select 6
	static void sql_select6() {
	/**/DebugTrace.enter()

 Person.Ex person
 Transaction.execute {
     person = new Sql<>(Person.Ex.targetClass)
         .where('{id} = {}', 1)
         .connection(it)
         .select().orElse(null)
 }

	/**/DebugTrace.print("person", person)
	/**/DebugTrace.leave()
	}

	// select As 2
	static void sql_selectAs2() {
	/**/DebugTrace.enter()

 PersonName personName
 Transaction.execute {
     personName = new Sql<>(Person)
         .where('{id}={}', 1)
         .connection(it)
         .selectAs(PersonName).orElse(null)
 }

/**/DebugTrace.print("personName", personName);
	/**/DebugTrace.leave()
	}

	// selectCount
	static void sql_selectCount() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Person)
         .connection(it)
         .selectCount()
 }

	/**/DebugTrace.leave()
	}

	// insert 1
	static void sql_insert1() {
	/**/DebugTrace.enter()

		Transaction.execute {
			new Sql<>(Person).where('{id} = {}', 6)
			.connection(it)
			.delete()
		}

 int count
 Transaction.execute {
     count = new Sql<>(Person)
         .connection(it)
         .insert(new Person(6, 'Setoka', 'Orange', 2001, 2, 1))
 }

	/**/DebugTrace.leave()
	}

	// insert 2
	static void sql_insert2() {
	/**/DebugTrace.enter()

		Transaction.execute {
			new Sql<>(Person).where('{id} IN {}', [7, 8, 9])
			.connection(it)
			.delete()
		}

 int count
 Transaction.execute {
     count = new Sql<>(Person)
         .connection(it)
         .insert([
             new Person(7, 'Harumi', 'Orange', 2001, 2, 2),
             new Person(8, 'Mihaya', 'Orange', 2001, 2, 3),
             new Person(9, 'Asumi' , 'Orange', 2001, 2, 4)
         ])
 }

	/**/DebugTrace.leave()
	}

	// update 1
	static void sql_update1() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Person)
         .connection(it)
         .update(new Person(6, 'Setoka', 'Orange', 2017, 2, 1))
 }

	/**/DebugTrace.leave()
	}

	// update 2
	static void sql_update2() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Person)
         .connection(it)
         .update([
             new Person(7, 'Harumi', 'Orange', 2017, 2, 2),
             new Person(8, 'Mihaya', 'Orange', 2017, 2, 3),
             new Person(9, 'Asumi' , 'Orange', 2017, 2, 4)
         ])
 }

	/**/DebugTrace.leave()
	}

	// delete 1
	static void sql_delete1() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Person)
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
     count = new Sql<>(Person)
         .connection(it)
         .delete(new Person(6))
 }

		Transaction.execute {
			count = new Sql<>(Person)
				.connection(it)
				.insert(new Person(6, 'Setoka', 'Orange', 2001, 2, 1))
		}

	/**/DebugTrace.leave()
	}

	// delete 3
	static void sql_delete3() {
	/**/DebugTrace.enter()

 int count
 Transaction.execute {
     count = new Sql<>(Person)
         .connection(it)
         .delete([new Person(7), new Person(8), new Person(9)])
 }

	/**/DebugTrace.print('count', count)

	Transaction.execute {
		new Sql<>(Person)
			.connection(it)
			.insert([
				new Person(7, 'Harumi', 'Orange', 2001, 2, 2),
				new Person(8, 'Mihaya', 'Orange', 2001, 2, 3),
				new Person(9, 'Asumi' , 'Orange', 2001, 2, 4)
			])
	}

	/**/DebugTrace.leave()
	}

}
