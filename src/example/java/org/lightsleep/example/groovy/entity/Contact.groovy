// Contact.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity

import java.sql.Date
import java.sql.Timestamp

import org.lightsleep.entity.Insert
import org.lightsleep.entity.Key
import org.lightsleep.entity.NonUpdate
import org.lightsleep.entity.Update

class Contact {
	@Key
	int    id
	String familyName
	String givenName
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
}
