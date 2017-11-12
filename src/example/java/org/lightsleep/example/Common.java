// Common.java
// (C) 2016 Masato Kokubo

package org.lightsleep.example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lightsleep.Sql;
import org.lightsleep.Transaction;
import org.lightsleep.component.*;
import org.lightsleep.connection.C3p0;
import org.lightsleep.connection.ConnectionSupplier;
import org.lightsleep.connection.Dbcp;
import org.lightsleep.connection.HikariCP;
import org.lightsleep.connection.Jdbc;
import org.lightsleep.connection.TomcatCP;
import org.lightsleep.example.java.entity.*;
import org.lightsleep.helper.Resource;
import org.lightsleep.logger.LoggerFactory;

// Common
public class Common {
	static {
		System.getProperties().setProperty("lightsleep.resource", "example/lightsleep");
	}

	public Common () {
		LoggerFactory.getLogger(Common.class).info(getClass().getName());

	}

	// init1
	protected void init1() {
		Transaction.execute(conn -> {
			new Sql<>(Contact.class).where(Condition.ALL).connection(conn).delete();
			new Sql<>(Phone  .class).where(Condition.ALL).connection(conn).delete();
			new Sql<>(Email  .class).where(Condition.ALL).connection(conn).delete();
			new Sql<>(Url    .class).where(Condition.ALL).connection(conn).delete();
			new Sql<>(Address.class).where(Condition.ALL).connection(conn).delete();
		});
	}

	// init2
	protected void init2() {
		List<Contact> persons  = new ArrayList<>();
		List<Phone>   phones    = new ArrayList<>();
		List<Address> addresses = new ArrayList<>();
		List<Email>   emails    = new ArrayList<>();
		List<Url>     urls      = new ArrayList<>();

		persons.add(new Contact( 1, "Akane" , "Apple" , 2001, 1, 1));
		persons.add(new Contact( 2, "Yukari", "Apple" , 2001, 1, 2));
		persons.add(new Contact( 3, "Azusa" , "Apple" , 2001, 1, 3));
		persons.add(new Contact( 4, "Takane", "Apple" , 2001, 1, 4));
		persons.add(new Contact( 5, "Haruka", "Apple"             ));
		persons.add(new Contact( 6, "Setoka", "Orange", 2001, 2, 1));
		persons.add(new Contact( 7, "Harumi", "Orange", 2001, 2, 2));
		persons.add(new Contact( 8, "Mihaya", "Orange", 2001, 2, 3));
		persons.add(new Contact( 9, "Asumi" , "Orange", 2001, 2, 4));
		persons.add(new Contact(10, "Akemi" , "Orange"            ));

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

		addresses.add(new Address( 1, (short)1, "Main", "1310045", "Tokyo", "Sumida-ku", "1-1-13 Oshiage", null));
		addresses.add(new Address( 2, (short)1, "Main", "1310045", "Tokyo", "Sumida-ku", "1-1-13 Oshiage", null));
		addresses.add(new Address( 3, (short)1, "Main", "1310045", "Tokyo", "Sumida-ku", "1-1-13 Oshiage", null));
		addresses.add(new Address( 4, (short)1, "Main", "1310045", "Tokyo", "Sumida-ku", "1-1-13 Oshiage", null));
		addresses.add(new Address( 5, (short)1, "Main", "1310045", "Tokyo", "Sumida-ku", "1-1-13 Oshiage", null));
		addresses.add(new Address( 6, (short)1, "Main", "1310045", "Tokyo", "Sumida-ku", "1-1-14 Oshiage", null));
		addresses.add(new Address( 7, (short)1, "Main", "1310045", "Tokyo", "Sumida-ku", "1-1-14 Oshiage", null));
		addresses.add(new Address( 8, (short)1, "Main", "1310045", "Tokyo", "Sumida-ku", "1-1-14 Oshiage", null));
		addresses.add(new Address( 9, (short)1, "Main", "1310045", "Tokyo", "Sumida-ku", "1-1-14 Oshiage", null));
		addresses.add(new Address(10, (short)1, "Main", "1310045", "Tokyo", "Sumida-ku", "1-1-14 Oshiage", null));

		emails.add(new Email( 1, (short)1, "Main", "akane@apple.lightsleep.org"));
		emails.add(new Email( 2, (short)1, "Main", "yukari@apple.lightsleep.org"));
		emails.add(new Email( 3, (short)1, "Main", "azusa@apple.lightsleep.org"));
		emails.add(new Email( 4, (short)1, "Main", "takane@apple.lightsleep.org"));
		emails.add(new Email( 5, (short)1, "Main", "haruka@apple.lightsleep.org"));
		emails.add(new Email( 6, (short)1, "Main", "setoka@orange.lightsleep.org"));
		emails.add(new Email( 7, (short)1, "Main", "harumi@orange.lightsleep.org"));
		emails.add(new Email( 8, (short)1, "Main", "mihaya@orange.lightsleep.org"));
		emails.add(new Email( 9, (short)1, "Main", "asumi@orange.lightsleep.org"));
		emails.add(new Email(10, (short)1, "Main", "akemi@orange.lightsleep.org"));

		urls.add(new Url( 1, (short)1, "Main", "akane.apple.lightsleep.org"));
		urls.add(new Url( 2, (short)1, "Main", "yukari.apple.lightsleep.org"));
		urls.add(new Url( 3, (short)1, "Main", "azusa.apple.lightsleep.org"));
		urls.add(new Url( 4, (short)1, "Main", "takane.apple.lightsleep.org"));
		urls.add(new Url( 5, (short)1, "Main", "haruka.apple.lightsleep.org"));
		urls.add(new Url( 6, (short)1, "Main", "setoka.orange.lightsleep.org"));
		urls.add(new Url( 7, (short)1, "Main", "harumi.orange.lightsleep.org"));
		urls.add(new Url( 8, (short)1, "Main", "mihaya.orange.lightsleep.org"));
		urls.add(new Url( 9, (short)1, "Main", "asumi.orange.lightsleep.org"));
		urls.add(new Url(10, (short)1, "Main", "akemi.orange.lightsleep.org"));

		Transaction.execute(conn -> {
			new Sql<>(Contact.class).connection(conn).insert(persons );
			new Sql<>(Phone  .class).connection(conn).insert(phones   );
			new Sql<>(Address.class).connection(conn).insert(addresses);
			new Sql<>(Email  .class).connection(conn).insert(emails   );
			new Sql<>(Url    .class).connection(conn).insert(urls     );
		});
	}
}
