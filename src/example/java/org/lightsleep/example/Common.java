// Common.java
// (C) 2016 Masato Kokubo

package org.lightsleep.example;

import java.util.ArrayList;
import java.util.List;

import org.lightsleep.Sql;
import org.lightsleep.Transaction;
import org.lightsleep.component.*;
import org.lightsleep.example.java.entity.*;


// Common
public class Common {
	// init1
	protected static void init1() {
		Transaction.execute(connection -> {
			new Sql<>(Contact.class).where(Condition.ALL).delete(connection);
			new Sql<>(Phone  .class).where(Condition.ALL).delete(connection);
			new Sql<>(Email  .class).where(Condition.ALL).delete(connection);
			new Sql<>(Url    .class).where(Condition.ALL).delete(connection);
			new Sql<>(Address.class).where(Condition.ALL).delete(connection);
		});
	}

	// init2
	protected static void init2() {
		List<Contact> contacts = new ArrayList<>();
		List<Phone> phones = new ArrayList<>();

		contacts.add(new Contact( 1, "Akane" , "Apple" , 2001, 1, 1));
		contacts.add(new Contact( 2, "Yukari", "Apple" , 2001, 1, 2));
		contacts.add(new Contact( 3, "Azusa" , "Apple" , 2001, 1, 3));
		contacts.add(new Contact( 4, "Takane", "Apple" , 2001, 1, 4));
		contacts.add(new Contact( 5, "Haruka", "Apple"             ));
		contacts.add(new Contact( 6, "Setoka", "Orange", 2001, 2, 1));
		contacts.add(new Contact( 7, "Harumi", "Orange", 2001, 2, 2));
		contacts.add(new Contact( 8, "Mihaya", "Orange", 2001, 2, 3));
		contacts.add(new Contact( 9, "Asumi" , "Orange", 2001, 2, 4));
		contacts.add(new Contact(10, "Akemi" , "Orange"            ));

		phones.add(new Phone( 1, (short)1, "Main", "08000010001"));
		phones.add(new Phone( 1, (short)2, "Sub" , "08000010002"));
		phones.add(new Phone( 3, (short)1, "Main", "08000030001"));
		phones.add(new Phone( 4, (short)1, "Main", "08000040001"));
		phones.add(new Phone( 4, (short)2, "Sub" , "08000040002"));
		phones.add(new Phone( 6, (short)1, "Main", "08000060001"));
		phones.add(new Phone( 6, (short)2, "Sub" , "08000060002"));
		phones.add(new Phone( 8, (short)1, "Main", "08000080001"));
		phones.add(new Phone( 9, (short)1, "Main", "08000090001"));
		phones.add(new Phone( 9, (short)2, "Sub" , "08000090002"));
		phones.add(new Phone(11, (short)1, "Main", "08000110001"));
		phones.add(new Phone(12, (short)1, "Main", "08000120001"));

		Transaction.execute(connection -> {
			new Sql<>(Contact.class).insert(connection, contacts);
			new Sql<>(Phone  .class).insert(connection, phones  );
		});
	}
}
