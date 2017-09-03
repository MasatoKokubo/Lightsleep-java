// ContactChildKey.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity

import org.lightsleep.entity.Key

class ContactChildKey {
	@Key
	int   contactId
	@Key
	short childIndex

	ContactChildKey() {
	}

	ContactChildKey(int contactId, short childIndex) {
		this.contactId  = contactId
		this.childIndex = childIndex
	}
}
