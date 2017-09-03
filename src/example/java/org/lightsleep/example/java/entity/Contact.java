// Contact.java
// (C) 2016 Masato Kokubo

package org.lightsleep.example.java.entity;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import org.lightsleep.Sql;
import org.lightsleep.entity.Insert;
import org.lightsleep.entity.NonInsert;
import org.lightsleep.entity.NonUpdate;
import org.lightsleep.entity.Select;
import org.lightsleep.entity.SelectProperty;
import org.lightsleep.entity.Table;
import org.lightsleep.entity.Update;

// Contact
public class Contact extends ContactKey {
	public String firstName;
	public String lastName;
	public Date birthday;

	@Insert("0")
	@Update("{updateCount}+1")
	public int updateCount;

	@Insert("CURRENT_TIMESTAMP")
	@NonUpdate
	public Timestamp createdTime;

	@Insert("CURRENT_TIMESTAMP")
	@Update("CURRENT_TIMESTAMP")
	public Timestamp updatedTime;

	public Contact() {
	}

	public Contact(int id) {
		super(id);
	}

	public Contact(int id, String firstName, String lastName) {
		super(id);
		this.firstName  = firstName ;
		this.lastName = lastName;
	}

	public Contact(int id, String firstName, String lastName, int year, int month, int day) {
		this(id, firstName, lastName);
		setBirthday(year, month, day);
	}

	public void setBirthday(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month - 1, day, 0, 0, 0);
		birthday = new Date(calendar.getTimeInMillis());
	}

	// Contact.Ex
	@Table("super")
	public static class Ex extends Contact {
		@Select("{firstName}||' '||{lastName}")
		@NonInsert  @NonUpdate
		public String fullName;

		// Contact.Ex.DB2
		@Table("super")
		public static class DB2 extends Ex {}

		// Contact.Ex.MySQL
		@Table("super")
		@SelectProperty(property="fullName", expression="CONCAT({firstName},' ',{lastName})")
		public static class MySQL extends Ex {}

		// Contact.Ex.Oracle
		@Table("super")
		public static class Oracle extends Ex {}

		// Contact.Ex.PostgreSQL
		@Table("super")
		public static class PostgreSQL extends Ex {}

		// Contact.Ex.SQLite
		@Table("super")
		public static class SQLite extends Ex {}

		// Contact.Ex.SQLServer
		@Table("super")
		@SelectProperty(property="fullName", expression="{firstName}+' '+{lastName}")
		public static class SQLServer extends Contact {}

		@SuppressWarnings("unchecked")
		public static Class<? extends Ex> targetClass() {
			try {
				return (Class<? extends Ex>)Class.forName(
					Ex.class.getName() + '$' + Sql.getDatabase().getClass().getSimpleName());
			}
			catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
