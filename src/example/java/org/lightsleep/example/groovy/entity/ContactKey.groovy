// ContactKey.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity

import org.lightsleep.entity.Key

class ContactKey {
	@Key
	int id

	ContactKey() {
	}

	ContactKey(int id) {
		this.id = id
	}
}
