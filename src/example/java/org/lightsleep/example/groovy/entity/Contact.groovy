// Contact.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity

import java.time.LocalDate
import java.time.LocalDateTime

import org.lightsleep.database.Database
import org.lightsleep.entity.Insert
import org.lightsleep.entity.NonInsert
import org.lightsleep.entity.NonUpdate
import org.lightsleep.entity.Select
import org.lightsleep.entity.SelectProperty
import org.lightsleep.entity.Table
import org.lightsleep.entity.Update

// Contact
class Contact extends ContactKey {
	String firstName
	String lastName
	LocalDate birthday

	@Insert('0')
	@Update('{updateCount}+1')
	int updateCount

	@Insert('CURRENT_TIMESTAMP')
	@NonUpdate
	LocalDateTime createdTime

	@Insert('CURRENT_TIMESTAMP')
	@Update('CURRENT_TIMESTAMP')
	LocalDateTime updatedTime

	Contact() {
	}

	Contact(int id) {
		super(id);
	}

	Contact(int id, String firstName, String lastName) {
		this(id)
		this.firstName = firstName
		this.lastName = lastName
	}

	Contact(int id, String firstName, String lastName, int year, int month, int day) {
		this(id, firstName, lastName)
		setBirthday(year, month, day)
	}

	def void setBirthday(int year, int month, int day) {
		birthday = LocalDate.of(year, month, day);
	}

	// Contact.Ex
	@Table('super')
	static class Ex extends Contact {
		@Select("{firstName}||' '||{lastName}")
		@NonInsert @NonUpdate
		String fullName;

		// Contact.Ex.DB2
		@Table('super')
		static class DB2 extends Ex {}

		// Contact.Ex.MySQL
		@Table('super')
		@SelectProperty(property='fullName', expression="CONCAT({firstName},' ',{lastName})")
		static class MySQL extends Ex {}

		// Contact.Ex.Oracle
		@Table('super')
		static class Oracle extends Ex {}

		// Contact.Ex.PostgreSQL
		@Table('super')
		static class PostgreSQL extends Ex {}

		// Contact.Ex.SQLite
		@Table('super')
		static class SQLite extends Ex {}

		// Contact.Ex.SQLServer
		@Table('super')
		@SelectProperty(property='fullName', expression="{firstName}+' '+{lastName}")
		static class SQLServer extends Ex {}

		static Class<? extends Ex> targetClass(Database database) {
			try {
				return (Class<? extends Ex>)Class.forName(
					Ex.name + '$' + database.getClass().simpleName)
			}
			catch (ClassNotFoundException e) {
				throw new RuntimeException(e)
			}
		}
	}
}
