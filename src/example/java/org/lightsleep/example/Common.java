// Common.java
// (C) 2016 Masato Kokubo

package org.lightsleep.example;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
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
		Contact contact = null;
		Phone phone = null;

		Calendar calendar = Calendar.getInstance();

		contact = new Contact();
		contact.id = 1; contact.familyName = "Apple"; contact.givenName  = "Akane";
		calendar.set(2001, 1-1, 1, 0, 0, 0); contact.birthday = new Date(calendar.getTimeInMillis());
		contacts.add(contact);

		contact = new Contact();
		contact.id = 2; contact.familyName = "Apple"; contact.givenName  = "Yukari";
		calendar.set(2001, 1-1, 2, 0, 0, 0); contact.birthday = new Date(calendar.getTimeInMillis());
		contacts.add(contact);

		contact = new Contact();
		contact.id = 3; contact.familyName = "Apple"; contact.givenName  = "Azusa";
		calendar.set(2001, 1-1, 3, 0, 0, 0); contact.birthday = new Date(calendar.getTimeInMillis());
		contacts.add(contact);

		contact = new Contact();
		contact.id = 4; contact.familyName = "Apple"; contact.givenName  = "Takane";
		calendar.set(2001, 1-1, 4, 0, 0, 0); contact.birthday = new Date(calendar.getTimeInMillis());
		contacts.add(contact);

		contact = new Contact();
		contact.id = 5; contact.familyName = "Apple"; contact.givenName  = "Haruka";
		calendar.set(2001, 1-1, 5, 0, 0, 0); contact.birthday = new Date(calendar.getTimeInMillis());
		contacts.add(contact);

		contact = new Contact();
		contact.id = 6; contact.familyName = "Orange"; contact.givenName  = "Setoka";
		calendar.set(2001, 2-1, 1, 0, 0, 0); contact.birthday = new Date(calendar.getTimeInMillis());
		contacts.add(contact);

		contact = new Contact();
		contact.id = 7; contact.familyName = "Orange"; contact.givenName  = "Harumi";
		calendar.set(2001, 2-1, 2, 0, 0, 0); contact.birthday = new Date(calendar.getTimeInMillis());
		contacts.add(contact);

		contact = new Contact();
		contact.id = 8; contact.familyName = "Orange"; contact.givenName  = "Mihaya";
		calendar.set(2001, 2-1, 3, 0, 0, 0); contact.birthday = new Date(calendar.getTimeInMillis());
		contacts.add(contact);

		contact = new Contact();
		contact.id = 9; contact.familyName = "Orange"; contact.givenName  = "Asumi";
		calendar.set(2001, 2-1, 4, 0, 0, 0); contact.birthday = new Date(calendar.getTimeInMillis());
		contacts.add(contact);

		contact = new Contact();
		contact.id = 10; contact.familyName = "Orange"; contact.givenName  = "Akemi";
		calendar.set(2001, 2-1, 5, 0, 0, 0); contact.birthday = new Date(calendar.getTimeInMillis());
		contacts.add(contact);


		phone = new Phone();
		phone.contactId = 1; phone.childIndex = 1; phone.label = "Main"; phone.content = "08000010001";
		phones.add(phone);

		phone = new Phone();
		phone.contactId = 1; phone.childIndex = 2; phone.label = "Sub" ; phone.content = "08000010002";
		phones.add(phone);

		phone = new Phone();
		phone.contactId = 3; phone.childIndex = 1; phone.label = "Main"; phone.content = "08000030001";
		phones.add(phone);

		phone = new Phone();
		phone.contactId = 4; phone.childIndex = 1; phone.label = "Main"; phone.content = "08000040001";
		phones.add(phone);

		phone = new Phone();
		phone.contactId = 4; phone.childIndex = 2; phone.label = "Sub" ; phone.content = "08000040002";
		phones.add(phone);


		phone = new Phone();
		phone.contactId = 6; phone.childIndex = 1; phone.label = "Main"; phone.content = "08000060001";
		phones.add(phone);

		phone = new Phone();
		phone.contactId = 6; phone.childIndex = 2; phone.label = "Sub"; phone.content = "08000060002";
		phones.add(phone);

		phone = new Phone();
		phone.contactId = 8; phone.childIndex = 1; phone.label = "Main"; phone.content = "08000080001";
		phones.add(phone);

		phone = new Phone();
		phone.contactId = 9; phone.childIndex = 1; phone.label = "Main"; phone.content = "08000090001";
		phones.add(phone);

		phone = new Phone();
		phone.contactId = 9; phone.childIndex = 2; phone.label = "Sub"; phone.content = "08000090002";
		phones.add(phone);


		phone = new Phone();
		phone.contactId = 11; phone.childIndex = 1; phone.label = "Main"; phone.content = "08000110001";
		phones.add(phone);

		phone = new Phone();
		phone.contactId = 12; phone.childIndex = 1; phone.label = "Main"; phone.content = "08000120001";
		phones.add(phone);


		Transaction.execute(connection -> {
			new Sql<>(Contact.class).insert(connection, contacts);
			new Sql<>(Phone  .class).insert(connection, phones  );
		});
	}
}
