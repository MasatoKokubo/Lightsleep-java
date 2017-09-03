// PersonBase.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity3

import java.sql.Date
import java.sql.Timestamp

// PersonBase
class PersonBase extends PersonKey {
	static class Name {
		String first
		String last
	}
	final Name name = new Name()
	Date birthday
	int updateCount
	Timestamp createdTime
	Timestamp updatedTime

	PersonBase() {
	}

	PersonBase(int id) {
		super(id)
	}
}
