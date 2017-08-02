//  Phone.java
//  (C) 2016 Masato Kokubo

package org.lightsleep.example.java.entity;

import org.lightsleep.entity.Key;

public class Phone {
	@Key
	public int    contactId;
	public short  childIndex;
	public String label;
	public String content;

	public Phone() {
	}

	public Phone(int contactId, short childIndex, String label, String content) {
		this.contactId  = contactId ;
		this.childIndex = childIndex;
		this.label      = label     ;
		this.content    = content   ;
	}
}
