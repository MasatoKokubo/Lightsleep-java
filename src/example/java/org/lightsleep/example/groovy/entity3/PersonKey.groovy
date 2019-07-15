// PersonKey.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity3

import org.lightsleep.entity.Key

// PersonKey
public class PersonKey {
	@Key
	private int id

	public PersonKey() {
	}

	public PersonKey(int id) {
		this.id = id
	}
}
