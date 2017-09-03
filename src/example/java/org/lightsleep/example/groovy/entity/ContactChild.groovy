// ContactChild.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity

abstract class ContactChild extends ContactChildKey {
	String label
	String content

	ContactChild() {
	}

	ContactChild(int contactId, short childIndex, String label, String content) {
		super(contactId, childIndex)
		this.label   = label
		this.content = content
	}
}
