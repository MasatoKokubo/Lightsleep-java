// PersonBase.java
// (C) 2016 Masato Kokubo

package org.lightsleep.example.java.entity3;

import java.sql.Date;
import java.sql.Timestamp;

// PersonBase
public class PersonBase extends PersonKey {
	public static class Name {
		private String first;
		private String last;

		public String getFirst() {
			return first;
		}

		public void setFirst(String first) {
			this.first = first;
		}

		public String getLast() {
			return last;
		}

		public void setLast(String last) {
			this.last = last;
		}
	}

	private final Name name = new Name();
	private Date birthday;
	private int updateCount;
	private Timestamp createdTime;
	private Timestamp updatedTime;

	public PersonBase() {
	}

	public PersonBase(int id) {
		super(id);
	}

	public Name getName() {
		return name;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public int getUpdateCount() {
		return updateCount;
	}

	public void setUpdateCount(int updateCount) {
		this.updateCount = updateCount;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public Timestamp getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Timestamp updatedTime) {
		this.updatedTime = updatedTime;
	}
}
