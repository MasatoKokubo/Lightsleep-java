//  Email.groovy
//  (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity

import org.lightsleep.entity.Key

class Email {
	@Key
	int    contactId
	short  childIndex
	String label
	String content
}
