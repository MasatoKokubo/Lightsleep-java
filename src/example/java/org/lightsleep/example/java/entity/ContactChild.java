// ContactChild.java
// (C) 2016 Masato Kokubo

package org.lightsleep.example.java.entity;

public abstract class ContactChild extends ContactChildKey {
	public String label;
	public String content;

	public ContactChild() {
	}

	public ContactChild(int contactId, short childIndex, String label, String content) {
		super(contactId, childIndex);
		this.label   = label  ;
		this.content = content;
	}
}
