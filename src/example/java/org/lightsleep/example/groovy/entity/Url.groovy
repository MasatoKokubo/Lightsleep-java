//  Url.groovy
//  (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity

import org.lightsleep.entity.Key

class Url {
	@Key
	int    contactId
	short  childIndex
	String label
	String content
}
