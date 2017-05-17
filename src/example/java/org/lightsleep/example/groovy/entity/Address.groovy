//  Address.groovy
//  (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity

import org.lightsleep.entity.Key

class Address {
	@Key
	int	  contactId
	short  childIndex
	String label
	String postCode
	String content0
	String content1
	String content2
	String content3
}
