// ContactChild.java
// (C) 2016 Masato Kokubo

package org.lightsleep.example.java.entity;

import org.lightsleep.entity.Key;

public class ContactChildKey {
	@Key
	public int contactId;

	@Key
	public short childIndex;

	public ContactChildKey() {
	}

	public ContactChildKey(int contactId, short childIndex) {
		this.contactId = contactId ;
		this.childIndex = childIndex;
	}
}
