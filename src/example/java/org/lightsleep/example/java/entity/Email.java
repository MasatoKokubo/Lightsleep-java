//  Email.java
//  (C) 2016 Masato Kokubo

package org.lightsleep.example.java.entity;

public class Email extends ContactFeature {
	public Email() {
	}

	public Email(int contactId, short featureIndex, String label, String content) {
		super(contactId, featureIndex, label, content);
	}
}
