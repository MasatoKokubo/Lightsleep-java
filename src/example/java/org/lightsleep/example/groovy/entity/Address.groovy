//  Address.groovy
//  (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity

import org.lightsleep.entity.ColumnProperty

@ColumnProperty(property="content", column="content0")
class Address extends ContactChild {
	String postCode
	String content1
	String content2
	String content3

	Address() {
	}

	Address(int contactId, short childIndex, String label, String postCode, String content, String content1, String content2, String content3) {
		super(contactId, childIndex, label, content)
		this.postCode = postCode
		this.content1 = content1
		this.content2 = content2
		this.content3 = content3
	}
}
