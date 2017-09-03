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
import org.lightsleep.entity.ColumnProperty;
import org.lightsleep.example.Common;
import org.lightsleep.example.java.entity2.*;
//import org.lightsleep.example.java.entity3.*;

/**
 * JavaDocExamples2
 */
public class JavaDocExamples2 extends Common {
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
     Optional<Person> personOpt = new Sql<>(Person.class)
         .where("{id}={}", 1)
         .connection(conn)
         .select();
     personOpt.ifPresent(person -> {
         person.setBirthday(2017, 1, 1);
         new Sql<>(Person.class)
         	.connection(conn)
            .update(person);
     });
 });

	/**/DebugTrace.leave();
	}

	// Sql
	private static void sql() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class)
         .where("{name.last}={}", "Apple")
         .connection(conn)
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// columns 1
	private static void sql_columns1() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class)
         .columns("name.last", "name.first")
         .where("{name.last}={}", "Apple")
         .connection(conn)
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// columns 2
	private static void sql_columns2() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 List<Person.Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C")
         .innerJoin(Person.Phone.class, "P", "{P.personId}={C.id}")
         .columns("C.id", "P.*")
         .connection(conn)
         .<Person.Phone>select(persons::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// expression
	private static void sql_expression() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class)
         .expression("name.first", "'['||{name.first}||']'")
         .where("{name.last}={}", "Orange")
         .connection(conn)
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// innerJoin
	private static void sql_innerJoin() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 List<Person.Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C")
         .innerJoin(Person.Phone.class, "P", "{P.personId}={C.id}")
         .connection(conn)
         .<Person.Phone>select(persons::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// leftJoin
	private static void sql_leftJoin() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 List<Person.Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C")
         .leftJoin(Person.Phone.class, "P", "{P.personId}={C.id}")
         .connection(conn)
         .<Person.Phone>select(persons::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// rightJoin
	private static void sql_rightJoin() {
		if (Sql.getDatabase() instanceof SQLite) return; // SQLite
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 List<Person.Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C")
         .rightJoin(Person.Phone.class, "P", "{P.personId}={C.id}")
         .connection(conn)
         .<Person.Phone>select(persons::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// where 1
	private static void sql_where1() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class)
         .where("{birthday} IS NULL")
         .connection(conn).select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// where 2
	private static void sql_where2() {
	/**/DebugTrace.enter();

 int id = 1;
 Person[] person = new Person[1];
 Transaction.execute(conn -> {
     person[0] = new Sql<>(Person.class)
         .where("{id}={}", id)
         .connection(conn).select().orElse(null);
 });

	/**/DebugTrace.leave();
	}

	// where 3
	private static void sql_where3() {
	/**/DebugTrace.enter();

 Person[] person = new Person[1];
 Transaction.execute(conn -> {
     person[0] = new Sql<>(Person.class)
         .where(new PersonKey(2))
         .connection(conn).select().orElse(null);
 });

	/**/DebugTrace.leave();
	}

	// where 4
	private static void sql_where4() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C")
         .where("EXISTS",
              new Sql<>(Person.Phone.class, "P")
                  .where("{P.personId}={C.id}")
                  .and("{P.content} LIKE {}", "0800001%")
         )
         .connection(conn)
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// and
	private static void sql_and() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class)
         .where("{name.last}={}", "Apple")
         .and("{name.first}={}", "Akiyo")
         .connection(conn)
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// or
	private static void sql_or() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class)
         .where("{name.last}={}", "Apple")
         .or("{name.last}={}", "Orange")
         .connection(conn)
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// orderBy
	private static void sql_orderBy() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class)
         .orderBy("{name.last}")
         .orderBy("{name.first}")
         .connection(conn)
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// asc
	private static void sql_asc() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class)
         .orderBy("{id}").asc()
         .connection(conn)
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// desc
	private static void sql_desc() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class)
         .orderBy("{id}").desc()
         .connection(conn)
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// limit
	private static void sql_limit() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class)
         .limit(5)
         .connection(conn)
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// offset
	private static void sql_offset() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class)
         .limit(5).offset(5)
         .connection(conn)
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// doIf
	private static void sql_doIf() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C")
         .doIf(!(Sql.getDatabase() instanceof SQLite), Sql::forUpdate)
         .connection(conn)
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// select 1
	private static void sql_select1() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class)
         .connection(conn)
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// select As 1
	@ColumnProperty(property="name.first", column="firstName")
	@ColumnProperty(property="name.last", column="lastName")
	public static class PersonName {
		public final Person.Name name = new Person.Name();
	}
	private static void sql_selectAs1() {
	/**/DebugTrace.enter();

 List<PersonName> personNames = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class)
         .connection(conn)
         .selectAs(PersonName.class, personNames::add);
 });

 /**/DebugTrace.print("personNames", personNames);
	/**/DebugTrace.leave();
	}

	// select 2
	private static void sql_select2() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 List<Person.Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C")
         .innerJoin(Person.Phone.class, "P", "{P.personId}={C.id}")
         .connection(conn)
         .<Person.Phone>select(persons::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// select 3
	private static void sql_select3() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 List<Person.Phone> phones = new ArrayList<>();
 List<Person.Email> emails = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C")
         .innerJoin(Person.Phone.class, "P", "{P.personId}={C.id}")
         .innerJoin(Person.Email.class, "E", "{E.personId}={C.id}")
         .connection(conn)
         .<Person.Phone, Person.Email>select(persons::add, phones::add, emails::add);
 });

	/**/DebugTrace.leave();
	}

	// select 4
	private static void sql_select4() {
	/**/DebugTrace.enter();

 List<Person>  persons = new ArrayList<>();
 List<Person.Phone>   phones = new ArrayList<>();
 List<Person.Email>   emails = new ArrayList<>();
 List<Person.Address> addresses = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C")
         .innerJoin(Person.Phone.class  , "P", "{P.personId}={C.id}")
         .innerJoin(Person.Email.class  , "E", "{E.personId}={C.id}")
         .innerJoin(Person.Address.class, "A", "{A.personId}={C.id}")
         .connection(conn)
         .<Person.Phone, Person.Email, Person.Address>select(
             persons::add, phones::add, emails::add, addresses::add);
 });

	/**/DebugTrace.leave();
	}

	// select 5
	private static void sql_select5() {
	/**/DebugTrace.enter();

 List<Person>  persons = new ArrayList<>();
 List<Person.Phone>   phones = new ArrayList<>();
 List<Person.Email>   emails = new ArrayList<>();
 List<Person.Address> addresses = new ArrayList<>();
 List<Person.Url>     urls = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C")
         .innerJoin(Person.Phone.class  , "P", "{P.personId}={C.id}")
         .innerJoin(Person.Email.class  , "E", "{E.personId}={C.id}")
         .innerJoin(Person.Address.class, "A", "{A.personId}={C.id}")
         .innerJoin(Person.Url.class    , "U", "{U.personId}={C.id}")
         .connection(conn)
         .<Person.Phone, Person.Email, Person.Address, Person.Url>select(
             persons::add, phones::add, emails::add,
             addresses::add, urls::add);
 });

	/**/DebugTrace.leave();
	}

	// select 6
	private static void sql_select6() {
	/**/DebugTrace.enter();

 Person.Ex[] person = new Person.Ex[1];
 Transaction.execute(conn -> {
     person[0] = new Sql<>(Person.Ex.targetClass())
         .where("{id}={}", 1)
         .connection(conn)
         .select().orElse(null);
 });

	/**/DebugTrace.print("person", person[0]);
	/**/DebugTrace.leave();
	}

	// select As 2
	private static void sql_selectAs2() {
	/**/DebugTrace.enter();

 PersonName[] personName = new PersonName[1];
 Transaction.execute(conn -> {
     personName[0] = new Sql<>(Person.class)
         .where("{id}={}", 1)
         .connection(conn)
         .selectAs(PersonName.class).orElse(null);
 });

/**/DebugTrace.print("personName", personName[0]);
	/**/DebugTrace.leave();
	}

	// selectCount
	private static void sql_selectCount() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Person.class)
         .connection(conn)
         .selectCount();
 });

	/**/DebugTrace.leave();
	}

	// insert 1
	private static void sql_insert1() {
	/**/DebugTrace.enter();

		Transaction.execute(conn -> {
			new Sql<>(Person.class).where("{id}={}", 6)
				.connection(conn)
                .delete();
		});

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Person.class)
         .connection(conn)
         .insert(new Person(6, "Setoka", "Orange", 2001, 2, 1));
 });

	/**/DebugTrace.leave();
	}

	// insert 2
	private static void sql_insert2() {
	/**/DebugTrace.enter();

		Transaction.execute(conn -> {
			new Sql<>(Person.class)
				.where("{id} IN {}", Arrays.asList(7, 8, 9))
				.connection(conn).delete();
		});

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Person.class)
         .connection(conn)
         .insert(Arrays.asList(
             new Person(7, "Harumi", "Orange", 2001, 2, 2),
             new Person(8, "Mihaya", "Orange", 2001, 2, 3),
             new Person(9, "Asumi" , "Orange", 2001, 2, 4)
         ));
 });

	/**/DebugTrace.leave();
	}

	// update 1
	private static void sql_update1() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Person.class)
         .connection(conn)
         .update(new Person(6, "Setoka", "Orange", 2017, 2, 1));
 });

	/**/DebugTrace.leave();
	}

	// update 2
	private static void sql_update2() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Person.class)
         .connection(conn)
         .update(Arrays.asList(
             new Person(7, "Harumi", "Orange", 2017, 2, 2),
             new Person(8, "Mihaya", "Orange", 2017, 2, 3),
             new Person(9, "Asumi" , "Orange", 2017, 2, 4)
         ));
 });

	/**/DebugTrace.leave();
	}

	// delete 1
	private static void sql_delete1() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Person.class)
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
     count[0] = new Sql<>(Person.class)
         .connection(conn)
         .delete(new Person(6));
 });

		Transaction.execute(conn -> {
			count[0] = new Sql<>(Person.class)
				.connection(conn)
				.insert(new Person(6, "Setoka", "Orange", 2001, 2, 1));
		});

	/**/DebugTrace.leave();
	}

	// delete 3
	private static void sql_delete3() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Person.class)
         .connection(conn)
         .delete(Arrays.asList(new Person(7), new Person(8), new Person(9)));
 });

	Transaction.execute(conn -> {
		new Sql<>(Person.class)
			.connection(conn)
			.insert(Arrays.asList(
				new Person(7, "Harumi", "Orange", 2001, 2, 2),
				new Person(8, "Mihaya", "Orange", 2001, 2, 3),
				new Person(9, "Asumi" , "Orange", 2001, 2, 4)
			));
	});

	/**/DebugTrace.leave();
	}

}
