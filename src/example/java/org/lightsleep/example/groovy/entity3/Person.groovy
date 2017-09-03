// Person.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity3

import java.sql.Date
import java.util.Calendar

import org.lightsleep.entity.Column
import org.lightsleep.entity.ColumnProperties
import org.lightsleep.entity.ColumnProperty
import org.lightsleep.entity.ColumnTypeProperty
import org.lightsleep.entity.InsertProperty
import org.lightsleep.entity.Key
import org.lightsleep.entity.NonInsertProperties
import org.lightsleep.entity.NonInsertProperty
import org.lightsleep.entity.NonUpdateProperty
import org.lightsleep.entity.Table
import org.lightsleep.entity.UpdateProperties
import org.lightsleep.entity.UpdateProperty

// Person
@Table('Contact')
@ColumnProperties([
	@ColumnProperty(property='name.first', column='firstName'),
	@ColumnProperty(property='name.last', column='lastName'),
	@ColumnProperty(property='birthday', column='birthday2')
])
@ColumnTypeProperty(property='birthday', type=Long)
@InsertProperty(property='updateCount', expression='0')
@UpdateProperties([
	@UpdateProperty(property='updateCount', expression='{updateCount}+1'),
	@UpdateProperty(property='updatedTime', expression='CURRENT_TIMESTAMP')
])
@NonInsertProperties([
	@NonInsertProperty(property='createdTime'),
	@NonInsertProperty(property='updatedTime')
])
@NonUpdateProperty(property='createdTime')
class Person extends PersonBase {
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

	// ChildKey
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

	// Child
	static abstract class Child extends ChildKey {
		String label
		String content

		Child() {
		}

		Child(int personId, short childIndex, String label, String content) {
			super(personId, childIndex)
			this.label   = label  
			this.content = content
		}
	}

	// Address
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

	// Email
	static class Email extends Child {
		Email() {
		}

		Email(int personId, short childIndex, String label, String content) {
			super(personId, childIndex, label, content)
		}
	}

	// Phone
	static class Phone extends Child {
		Phone() {
		}

		Phone(int personId, short childIndex, String label, String content) {
			super(personId, childIndex, label, content)
		}
	}

	// Url
	static class Url extends Child {
		Url() {
		}

		Url(int personId, short childIndex, String label, String content) {
			super(personId, childIndex, label, content)
		}
	}
}
