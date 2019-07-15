// PersonBase.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity3

import java.sql.Date
import java.sql.Timestamp

// PersonBase
public class PersonBase extends PersonKey {
	public static class Name {
		private String first
		private String last
	}
	private final Name name = new Name()
	private Date birthday
	private int updateCount
	private Timestamp createdTime
	private Timestamp updatedTime

	public PersonBase() {
	}

	public PersonBase(int id) {
		super(id)
	}
}
