// Person.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity2

import java.sql.Date
import java.sql.Timestamp
import java.util.Calendar
import org.lightsleep.database.Database
import org.lightsleep.entity.Column
import org.lightsleep.entity.ColumnProperty
import org.lightsleep.entity.ColumnType
import org.lightsleep.entity.Key
import org.lightsleep.entity.NonInsert
import org.lightsleep.entity.NonUpdate
import org.lightsleep.entity.Select
import org.lightsleep.entity.SelectProperty
import org.lightsleep.entity.Table
import org.lightsleep.entity.Update

// Person
@Table('Contact')
class Person extends PersonKey {
	static class Name {
		@Column('firstName')
		String first

		@Column('lastName')
		String last
	}

	final Name name = new Name()

	@Column('birthday2')
	@ColumnType(Long)
	Date birthday

	@NonInsert
	@Update('{updateCount}+1')
	int updateCount

	@NonInsert
	@NonUpdate
	Timestamp createdTime

	@NonInsert
	@Update('CURRENT_TIMESTAMP')
	Timestamp updatedTime

	Person() {
	}

	Person(int id) {
		super(id)
	}

	Person(int id, String firstName, String lastName) {
		super(id)
		name.first = firstName
		name.last  = lastName
	}

	Person(int id, String firstName, String lastName, int year, int month, int day) {
		this(id, firstName, lastName)
		setBirthday(year, month, day)
	}

	void setBirthday(int year, int month, int day) {
		Calendar calendar = Calendar.instance
		calendar.clear()
		calendar.set(year, month - 1, day, 0, 0, 0)
		birthday = new Date(calendar.timeInMillis)
	}

	// Person.Ex
	@Table('super')
	static class Ex extends Person {
		@Select("{name.first}||' '||{name.last}")
		@NonInsert @NonUpdate
		String fullName;

		// Person.Ex.DB2
		@Table('super')
		static class DB2 extends Ex {}

		// Person.Ex.MySQL
		@Table('super')
		@SelectProperty(property='fullName', expression="CONCAT({name.first},' ',{name.last})")
		static class MySQL extends Ex {}

		// Person.Ex.Oracle
		@Table('super')
		static class Oracle extends Ex {}

		// Person.Ex.PostgreSQL
		@Table('super')
		static class PostgreSQL extends Ex {}

		// Person.Ex.SQLite
		@Table('super')
		static class SQLite extends Ex {}

		// Person.Ex.SQLServer
		@Table('super')
		@SelectProperty(property='fullName', expression="{name.first}+' '+{name.last}")
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

	// Person.ChildKey
	static class ChildKey {
		@Key
		@Column('contactId')
		int personId

		@Key
		short childIndex

		ChildKey() {
		}

		ChildKey(int personId, short childIndex) {
			this.personId = personId 
			this.childIndex = childIndex
		}
	}

	// Person.Child
	static abstract class Child extends ChildKey {
		String label
		String content

		Child() {
		}

		Child(int personId, short childIndex, String label, String content) {
			super(personId, childIndex)
			this.label = label
			this.content = content
		}
	}

	// Person.Address
	@ColumnProperty(property='content', column='content0')
	static class Address extends Child {
		String postCode
		String content1
		String content2
		String content3

		Address() {
		}

		Address(int personId, short childIndex, String label, String postCode, String content, String content1, String content2, String content3) {
			super(personId, childIndex, label, content)
			this.postCode = postCode
			this.content1 = content1
			this.content2 = content2
			this.content3 = content3
		}
	}

	// Person.Email
	static class Email extends Child {
		Email() {
		}

		Email(int personId, short childIndex, String label, String content) {
			super(personId, childIndex, label, content)
		}
	}

	// Person.Phone
	static class Phone extends Child {
		Phone() {
		}

		Phone(int personId, short childIndex, String label, String content) {
			super(personId, childIndex, label, content)
		}
	}

	// Person.Url
	static class Url extends Child {
		Url() {
		}

		Url(int personId, short childIndex, String label, String content) {
			super(personId, childIndex, label, content)
		}
	}
}
