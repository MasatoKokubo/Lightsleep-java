// ContactKey.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity

import org.lightsleep.entity.Key

public class ContactKey {
	@Key
	public int id

	public ContactKey() {
	}

	public ContactKey(int id) {
		this.id = id
	}
}
