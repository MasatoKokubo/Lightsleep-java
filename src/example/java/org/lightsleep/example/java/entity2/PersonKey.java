// PersonKey.java
// (C) 2016 Masato Kokubo

package org.lightsleep.example.java.entity2;

import org.lightsleep.entity.Key;

// PersonKey
public class PersonKey {
	@Key
	public int id;

	public PersonKey() {
	}

	public PersonKey(int id) {
		this.id = id;
	}
}
