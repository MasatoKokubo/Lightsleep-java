// Contact.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity

import java.sql.Date
import java.sql.Timestamp
import java.util.Calendar;

import org.lightsleep.entity.Insert
import org.lightsleep.entity.Key
import org.lightsleep.entity.NonUpdate
import org.lightsleep.entity.Update

class Contact {
	@Key
	int    id
	String givenName
	String familyName
	Date   birthday

	@Insert('0')
	@Update('{updateCount}+1')
	int updateCount

	@Insert('CURRENT_TIMESTAMP')
	@NonUpdate
	Timestamp createdTime

	@Insert('CURRENT_TIMESTAMP')
	@Update('CURRENT_TIMESTAMP')
	Timestamp updatedTime

	Contact() {
	}

	Contact(int id) {
		this.id = id
	}

	Contact(int id, String givenName, String familyName) {
		this(id)
		this.givenName  = givenName
		this.familyName = familyName
	}

	Contact(int id, String givenName, String familyName, int year, int month, int day) {
		this(id, givenName, familyName)
		setBirthday(year, month, day)
	}

	def void setBirthday(int year, int month, int day) {
		def calendar = Calendar.instance
		calendar.clear()
		calendar.set(year, month - 1, day, 0, 0, 0)
		birthday = new Date(calendar.timeInMillis)
	}
}
