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
import org.lightsleep.entity.ColumnProperty;
import org.lightsleep.example.Common;
import org.lightsleep.example.java.entity2.*;
//import org.lightsleep.example.java.entity3.*;

/**
 * JavaDocExamples2
 */
public class JavaDocExamples2 extends Common {
	public static void main(String[] args) {
		JavaDocExamples2 examples = new JavaDocExamples2();
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
     Optional<Person> personOpt = new Sql<>(Person.class).connection(conn)
         .where("{id}={}", 1)
         .select();
     personOpt.ifPresent(person -> {
         person.setBirthday(2017, 1, 1);
         new Sql<>(Person.class).connection(conn)
            .update(person);
     });
 });

	/**/DebugTrace.leave();
	}

	// Sql
	private void sql() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class).connection(conn)
         .where("{name.last}={}", "Apple")
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// columns 1
	private void sql_columns1() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class).connection(conn)
         .columns("name.last", "name.first")
         .where("{name.last}={}", "Apple")
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// columns 2
	private void sql_columns2() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 List<Person.Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C").connection(conn)
         .innerJoin(Person.Phone.class, "P", "{P.personId}={C.id}")
         .columns("C.id", "P.*")
         .<Person.Phone>select(persons::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// expression
	private void sql_expression() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class).connection(conn)
         .expression("name.first", "'['||{name.first}||']'")
         .where("{name.last}={}", "Orange")
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// innerJoin
	private void sql_innerJoin() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 List<Person.Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C").connection(conn)
         .innerJoin(Person.Phone.class, "P", "{P.personId}={C.id}")
         .<Person.Phone>select(persons::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// leftJoin
	private void sql_leftJoin() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 List<Person.Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C").connection(conn)
         .leftJoin(Person.Phone.class, "P", "{P.personId}={C.id}")
         .<Person.Phone>select(persons::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// rightJoin
	private void sql_rightJoin() {
	// 2.1.0
	//	if (Sql.getDatabase() instanceof SQLite) return; // SQLite dose not support RIGHT JOIN
		if (ConnectionSupplier.find().getDatabase() instanceof SQLite) return; // SQLite dose not support RIGHT JOIN
	////
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 List<Person.Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C").connection(conn)
         .rightJoin(Person.Phone.class, "P", "{P.personId}={C.id}")
         .<Person.Phone>select(persons::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// where 1
	private void sql_where1() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class).connection(conn)
         .where("{birthday} IS NULL")
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// where 2
	private void sql_where2() {
	/**/DebugTrace.enter();

 int id = 1;
 Person[] person = new Person[1];
 Transaction.execute(conn -> {
     person[0] = new Sql<>(Person.class).connection(conn)
         .where("{id}={}", id)
         .select().orElse(null);
 });

	/**/DebugTrace.leave();
	}

	// where 3
	private void sql_where3() {
	/**/DebugTrace.enter();

 Person[] person = new Person[1];
 Transaction.execute(conn -> {
     person[0] = new Sql<>(Person.class).connection(conn)
         .where(new PersonKey(2))
         .select().orElse(null);
 });

	/**/DebugTrace.leave();
	}

	// where 4
	private void sql_where4() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C").connection(conn)
         .where("EXISTS",
              new Sql<>(Person.Phone.class, "P")
                  .where("{P.personId}={C.id}")
                  .and("{P.content} LIKE {}", "0800001%")
         )
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// and
	private void sql_and() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class).connection(conn)
         .where("{name.last}={}", "Apple")
         .and("{name.first}={}", "Akiyo")
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// or
	private void sql_or() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class).connection(conn)
         .where("{name.last}={}", "Apple")
         .or("{name.last}={}", "Orange")
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// orderBy
	private void sql_orderBy() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class).connection(conn)
         .orderBy("{name.last}")
         .orderBy("{name.first}")
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// asc
	private void sql_asc() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class).connection(conn)
         .orderBy("{id}").asc()
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// desc
	private void sql_desc() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class).connection(conn)
         .orderBy("{id}").desc()
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// limit
	private void sql_limit() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class).connection(conn)
         .limit(5)
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// offset
	private void sql_offset() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class).connection(conn)
         .limit(5).offset(5)
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// doIf
	private void sql_doIf() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C").connection(conn)
     // 2.1.0
     //  .doIf(!(Sql.getDatabase() instanceof SQLite), Sql::forUpdate) // SQLite dose not support FOR UPDATE
         .doIf(!(conn.getDatabase() instanceof SQLite), Sql::forUpdate) // SQLite dose not support FOR UPDATE
     ////
         .select(persons::add);
 });

	/**/DebugTrace.leave();
	}

	// select 1
	private void sql_select1() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class).connection(conn)
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
	private void sql_selectAs1() {
	/**/DebugTrace.enter();

 List<PersonName> personNames = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class).connection(conn)
         .selectAs(PersonName.class, personNames::add);
 });

 /**/DebugTrace.print("personNames", personNames);
	/**/DebugTrace.leave();
	}

	// select 2
	private void sql_select2() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 List<Person.Phone> phones = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C").connection(conn)
         .innerJoin(Person.Phone.class, "P", "{P.personId}={C.id}")
         .<Person.Phone>select(persons::add, phones::add);
 });

	/**/DebugTrace.leave();
	}

	// select 3
	private void sql_select3() {
	/**/DebugTrace.enter();

 List<Person> persons = new ArrayList<>();
 List<Person.Phone> phones = new ArrayList<>();
 List<Person.Email> emails = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C").connection(conn)
         .innerJoin(Person.Phone.class, "P", "{P.personId}={C.id}")
         .innerJoin(Person.Email.class, "E", "{E.personId}={C.id}")
         .<Person.Phone, Person.Email>select(persons::add, phones::add, emails::add);
 });

	/**/DebugTrace.leave();
	}

	// select 4
	private void sql_select4() {
	/**/DebugTrace.enter();

 List<Person>  persons = new ArrayList<>();
 List<Person.Phone>   phones = new ArrayList<>();
 List<Person.Email>   emails = new ArrayList<>();
 List<Person.Address> addresses = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C").connection(conn)
         .innerJoin(Person.Phone.class  , "P", "{P.personId}={C.id}")
         .innerJoin(Person.Email.class  , "E", "{E.personId}={C.id}")
         .innerJoin(Person.Address.class, "A", "{A.personId}={C.id}")
         .<Person.Phone, Person.Email, Person.Address>select(
             persons::add, phones::add, emails::add, addresses::add);
 });

	/**/DebugTrace.leave();
	}

	// select 5
	private void sql_select5() {
	/**/DebugTrace.enter();

 List<Person>  persons = new ArrayList<>();
 List<Person.Phone>   phones = new ArrayList<>();
 List<Person.Email>   emails = new ArrayList<>();
 List<Person.Address> addresses = new ArrayList<>();
 List<Person.Url>     urls = new ArrayList<>();
 Transaction.execute(conn -> {
     new Sql<>(Person.class, "C").connection(conn)
         .innerJoin(Person.Phone.class  , "P", "{P.personId}={C.id}")
         .innerJoin(Person.Email.class  , "E", "{E.personId}={C.id}")
         .innerJoin(Person.Address.class, "A", "{A.personId}={C.id}")
         .innerJoin(Person.Url.class    , "U", "{U.personId}={C.id}")
         .<Person.Phone, Person.Email, Person.Address, Person.Url>select(
             persons::add, phones::add, emails::add,
             addresses::add, urls::add);
 });

	/**/DebugTrace.leave();
	}

	// select 6
	private void sql_select6() {
	/**/DebugTrace.enter();

 Person.Ex[] person = new Person.Ex[1];
 Transaction.execute(conn -> {
     person[0] = new Sql<>(Person.Ex.targetClass(conn.getDatabase())).connection(conn)
         .where("{id}={}", 1)
         .select().orElse(null);
 });

	/**/DebugTrace.print("person", person[0]);
	/**/DebugTrace.leave();
	}

	// select As 2
	private void sql_selectAs2() {
	/**/DebugTrace.enter();

 PersonName[] personName = new PersonName[1];
 Transaction.execute(conn -> {
     personName[0] = new Sql<>(Person.class).connection(conn)
         .where("{id}={}", 1)
         .selectAs(PersonName.class).orElse(null);
 });

/**/DebugTrace.print("personName", personName[0]);
	/**/DebugTrace.leave();
	}

	// selectCount
	private void sql_selectCount() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Person.class).connection(conn)
         .selectCount();
 });

	/**/DebugTrace.leave();
	}

	// insert 1
	private void sql_insert1() {
	/**/DebugTrace.enter();

		Transaction.execute(conn -> {
			new Sql<>(Person.class).where("{id}={}", 6).connection(conn)
                .delete();
		});

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Person.class).connection(conn)
         .insert(new Person(6, "Setoka", "Orange", 2001, 2, 1));
 });

	/**/DebugTrace.leave();
	}

	// insert 2
	private void sql_insert2() {
	/**/DebugTrace.enter();

		Transaction.execute(conn -> {
			new Sql<>(Person.class).connection(conn)
				.where("{id} IN {}", Arrays.asList(7, 8, 9))
				.delete();
		});

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Person.class).connection(conn)
         .insert(Arrays.asList(
             new Person(7, "Harumi", "Orange", 2001, 2, 2),
             new Person(8, "Mihaya", "Orange", 2001, 2, 3),
             new Person(9, "Asumi" , "Orange", 2001, 2, 4)
         ));
 });

	/**/DebugTrace.leave();
	}

	// update 1
	private void sql_update1() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Person.class).connection(conn)
         .update(new Person(6, "Setoka", "Orange", 2017, 2, 1));
 });

	/**/DebugTrace.leave();
	}

	// update 2
	private void sql_update2() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Person.class).connection(conn)
         .update(Arrays.asList(
             new Person(7, "Harumi", "Orange", 2017, 2, 2),
             new Person(8, "Mihaya", "Orange", 2017, 2, 3),
             new Person(9, "Asumi" , "Orange", 2017, 2, 4)
         ));
 });

	/**/DebugTrace.leave();
	}

	// delete 1
	private void sql_delete1() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Person.class).connection(conn)
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
     count[0] = new Sql<>(Person.class).connection(conn)
         .delete(new Person(6));
 });

		Transaction.execute(conn -> {
			count[0] = new Sql<>(Person.class).connection(conn)
				.insert(new Person(6, "Setoka", "Orange", 2001, 2, 1));
		});

	/**/DebugTrace.leave();
	}

	// delete 3
	private void sql_delete3() {
	/**/DebugTrace.enter();

 int[] count = new int[1];
 Transaction.execute(conn -> {
     count[0] = new Sql<>(Person.class).connection(conn)
         .delete(Arrays.asList(new Person(7), new Person(8), new Person(9)));
 });

	Transaction.execute(conn -> {
		new Sql<>(Person.class).connection(conn)
			.insert(Arrays.asList(
				new Person(7, "Harumi", "Orange", 2001, 2, 2),
				new Person(8, "Mihaya", "Orange", 2001, 2, 3),
				new Person(9, "Asumi" , "Orange", 2001, 2, 4)
			));
	});

	/**/DebugTrace.leave();
	}

}
