//  Url.groovy
//  (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity

import org.lightsleep.entity.Key

public class Url extends ContactFeature {
	public Url() {
	}

	public Url(int contactId, short featureIndex, String label, String content) {
		super(contactId, featureIndex, label, content);
	}
}
