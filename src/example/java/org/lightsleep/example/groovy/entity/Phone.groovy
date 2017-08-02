//  Phone.groovy
//  (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity

import org.lightsleep.entity.Key

class Phone {
	@Key
	int    contactId
	short  childIndex
	String label
	String content

	Phone() {
	}

	Phone(int contactId, short childIndex, String label, String content) {
		this.contactId  = contactId
		this.childIndex = childIndex
		this.label      = label
		this.content    = content
	}
}
