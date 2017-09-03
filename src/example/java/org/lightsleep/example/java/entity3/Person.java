// Person.java
// (C) 2016 Masato Kokubo

package org.lightsleep.example.java.entity3;

import java.sql.Date;
import java.util.Calendar;

import org.lightsleep.entity.Column;
import org.lightsleep.entity.ColumnProperty;
import org.lightsleep.entity.ColumnTypeProperty;
import org.lightsleep.entity.InsertProperty;
import org.lightsleep.entity.Key;
import org.lightsleep.entity.NonInsertProperty;
import org.lightsleep.entity.NonUpdateProperty;
import org.lightsleep.entity.Table;
import org.lightsleep.entity.UpdateProperty;

// Person
@Table("Contact")
@ColumnProperty(property="name.first", column="firstName")
@ColumnProperty(property="name.last", column="lastName")
@ColumnProperty(property="birthday", column="birthday2")
@ColumnTypeProperty(property="birthday", type=Long.class)
@InsertProperty(property="updateCount", expression="0")
@UpdateProperty(property="updateCount", expression="{updateCount}+1")
@NonInsertProperty(property="createdTime")
@NonUpdateProperty(property="createdTime")
@NonInsertProperty(property="updatedTime")
@UpdateProperty(property="updatedTime", expression="CURRENT_TIMESTAMP")
public class Person extends PersonBase {
	public Person() {
	}

	public Person(int id) {
		super(id);
	}

	public Person(int id, String firstName, String lastName) {
		super(id);
		getName().setFirst(firstName);
		getName().setLast(lastName);
	}

	public Person(int id, String firstName, String lastName, int year, int month, int day) {
		this(id, firstName, lastName);
		setBirthday(year, month, day);
	}

	public void setBirthday(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month - 1, day, 0, 0, 0);
		setBirthday(new Date(calendar.getTimeInMillis()));
	}

	// ChildKey
	public static class ChildKey {
		@Key
		@Column("contactId")
		private int personId;

		@Key
		private short childIndex;

		public ChildKey() {
		}

		public ChildKey(int personId, short childIndex) {
			this.personId = personId ;
			this.childIndex = childIndex;
		}

		public int getPersonId() {
			return personId;
		}

		public void setPersonId(int personId) {
			this.personId = personId;
		}

		public short getChildIndex() {
			return childIndex;
		}

		public void setChildIndex(short childIndex) {
			this.childIndex = childIndex;
		}
	}

	// Child
	public static abstract class Child extends ChildKey {
		private String label;
		private String content;

		public Child() {
		}

		public Child(int personId, short childIndex, String label, String content) {
			super(personId, childIndex);
			this.label   = label  ;
			this.content = content;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}

	// Address
	@ColumnProperty(property="content", column="content0")
	public static class Address extends Child {
		private String postCode;
		private String content1;
		private String content2;
		private String content3;

		public Address() {
		}

		public Address(int personId, short childIndex, String label, String postCode, String content, String content1, String content2, String content3) {
			super(personId, childIndex, label, content);
			this.postCode = postCode;
			this.content1 = content1;
			this.content2 = content2;
			this.content3 = content3;
		}

		public String getPostCode() {
			return postCode;
		}

		public void setPostCode(String postCode) {
			this.postCode = postCode;
		}

		public String getContent1() {
			return content1;
		}

		public void setContent1(String content1) {
			this.content1 = content1;
		}

		public String getContent2() {
			return content2;
		}

		public void setContent2(String content2) {
			this.content2 = content2;
		}

		public String getContent3() {
			return content3;
		}

		public void setContent3(String content3) {
			this.content3 = content3;
		}
	}

	// Email
	public static class Email extends Child {
		public Email() {
		}

		public Email(int personId, short childIndex, String label, String content) {
			super(personId, childIndex, label, content);
		}
	}

	// Phone
	public static class Phone extends Child {
		public Phone() {
		}

		public Phone(int personId, short childIndex, String label, String content) {
			super(personId, childIndex, label, content);
		}
	}

	// Url
	public static class Url extends Child {
		public Url() {
		}

		public Url(int personId, short childIndex, String label, String content) {
			super(personId, childIndex, label, content);
		}
	}
}
