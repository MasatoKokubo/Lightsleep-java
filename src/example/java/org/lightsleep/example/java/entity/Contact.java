// Contact.java
// (C) 2016 Masato Kokubo

package org.lightsleep.example.java.entity;

import java.sql.Date;
import java.sql.Timestamp;

import org.lightsleep.entity.Insert;
import org.lightsleep.entity.Key;
import org.lightsleep.entity.NonUpdate;
import org.lightsleep.entity.Update;

public class Contact {
	@Key
	public int    id;
	public String familyName;
	public String givenName;
	public Date   birthday;

	@Insert("0")
	@Update("{updateCount}+1")
	public int updateCount;

	@Insert("CURRENT_TIMESTAMP")
	@NonUpdate
	public Timestamp createdTime;

	@Insert("CURRENT_TIMESTAMP")
	@Update("CURRENT_TIMESTAMP")
	public Timestamp updatedTime;
}
