//  Phone.java
//  (C) 2016 Masato Kokubo

package org.lightsleep.example.java.entity;

public class Phone extends ContactChild {
	public Phone() {
	}

	public Phone(int contactId, short childIndex, String label, String content) {
		super(contactId, childIndex, label, content);
	}
}
