//  Email.java
//  (C) 2016 Masato Kokubo

package org.lightsleep.example.java.entity;

public class Email extends ContactChild {
	public Email() {
	}

	public Email(int contactId, short childIndex, String label, String content) {
		super(contactId, childIndex, label, content);
	}
}
