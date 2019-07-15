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
public class Person extends PersonBase {
	public Person() {
	}

	public Person(int id) {
		super(id)
	}

	public Person(int id, String firstName, String lastName) {
		super(id)
		name.first = firstName
		name.last  = lastName
	}

	public Person(int id, String firstName, String lastName, int year, int month, int day) {
		this(id, firstName, lastName)
		setBirthday(year, month, day)
	}

	public void setBirthday(int year, int month, int day) {
		Calendar calendar = Calendar.instance
		calendar.clear()
		calendar.set(year, month - 1, day, 0, 0, 0)
		birthday = new Date(calendar.timeInMillis)
	}

	// FeatureKey
	public static class FeatureKey {
		@Key
		@Column('contactId')
		private int personId

		@Key
		private short featureIndex

		public FeatureKey() {
		}

		public FeatureKey(int personId, short featureIndex) {
			this.personId = personId 
			this.featureIndex = featureIndex
		}
	}

	// Feature
	public static abstract class Feature extends FeatureKey {
		private String label
		private String content

		Feature() {
		}

		Feature(int personId, short featureIndex, String label, String content) {
			super(personId, featureIndex)
			this.label   = label  
			this.content = content
		}
	}

	// Address
	public static class Address extends Feature {
		private String postCode
		private String content1
		private String content2
		private String content3

		Address() {
		}

		Address(int personId, short featureIndex, String label, String postCode, String content, String content1, String content2, String content3) {
			super(personId, featureIndex, label, content)
			this.postCode = postCode
			this.content1 = content1
			this.content2 = content2
			this.content3 = content3
		}
	}

	// Email
	public static class Email extends Feature {
		public Email() {
		}

		public Email(int personId, short featureIndex, String label, String content) {
			super(personId, featureIndex, label, content)
		}
	}

	// Phone
	public static class Phone extends Feature {
		public Phone() {
		}

		public Phone(int personId, short featureIndex, String label, String content) {
			super(personId, featureIndex, label, content)
		}
	}

	// Url
	public static class Url extends Feature {
		public Url() {
		}

		public Url(int personId, short featureIndex, String label, String content) {
			super(personId, featureIndex, label, content)
		}
	}
}
