// PersonKey.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity3

import org.lightsleep.entity.Key

// PersonKey
class PersonKey {
	@Key
	int id

	PersonKey() {
	}

	PersonKey(int id) {
		this.id = id
	}
}
