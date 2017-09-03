//  Url.java
//  (C) 2016 Masato Kokubo

package org.lightsleep.example.java.entity;

public class Url extends ContactChild {
	public Url() {
	}

	public Url(int contactId, short childIndex, String label, String content) {
		super(contactId, childIndex, label, content);
	}
}
