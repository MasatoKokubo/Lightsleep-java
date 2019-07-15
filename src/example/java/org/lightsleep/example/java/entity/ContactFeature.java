// ContactFeature.java
// (C) 2016 Masato Kokubo

package org.lightsleep.example.java.entity;

public class ContactFeature extends ContactFeatureKey {
	public String label;
	public String content;

	public ContactFeature() {
	}

	public ContactFeature(int contactId, short featureIndex, String label, String content) {
		super(contactId, featureIndex);
		this.label   = label  ;
		this.content = content;
	}
}
