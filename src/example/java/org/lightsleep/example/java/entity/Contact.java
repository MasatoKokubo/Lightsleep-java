// Contact.java
// (C) 2016 Masato Kokubo

package org.lightsleep.example.java.entity;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import org.lightsleep.entity.Column;
import org.lightsleep.entity.Insert;
import org.lightsleep.entity.Key;
import org.lightsleep.entity.NonColumn;
import org.lightsleep.entity.NonUpdate;
import org.lightsleep.entity.Update;

public class Contact {
	@Key
	public int id;

	@Column("firstName")
	public String givenName;

	@Column("lastName")
	public String familyName;

	public Date birthday;

	@NonColumn
	public Date birthday2;

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
		this.id = id;
	}

	public Contact(int id, String givenName, String familyName) {
		this(id);
		this.givenName  = givenName ;
		this.familyName = familyName;
	}

	public Contact(int id, String givenName, String familyName, int year, int month, int day) {
		this(id, givenName, familyName);
		setBirthday(year, month, day);
	}

	public void setBirthday(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month - 1, day, 0, 0, 0);
		birthday = new Date(calendar.getTimeInMillis());
	}
}
