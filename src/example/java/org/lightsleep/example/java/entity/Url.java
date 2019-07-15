//  Url.java
//  (C) 2016 Masato Kokubo

package org.lightsleep.example.java.entity;

public class Url extends ContactFeature {
	public Url() {
	}

	public Url(int contactId, short featureIndex, String label, String content) {
		super(contactId, featureIndex, label, content);
	}
}
