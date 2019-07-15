//  Phone.groovy
//  (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity

import org.lightsleep.entity.Key

public class Phone extends ContactFeature {
	public Phone() {
	}

	public Phone(int contactId, short featureIndex, String label, String content) {
		super(contactId, featureIndex, label, content);
	}
}
