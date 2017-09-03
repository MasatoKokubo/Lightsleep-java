//  Url.groovy
//  (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity

import org.lightsleep.entity.Key

class Url extends ContactChild {
	Url() {
	}

	Url(int contactId, short childIndex, String label, String content) {
		super(contactId, childIndex, label, content);
	}
}
