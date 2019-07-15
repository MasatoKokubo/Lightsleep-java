// ContactFeatureKey.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.example.groovy.entity

import org.lightsleep.entity.Key

public class ContactFeatureKey {
	@Key
	public int   contactId
	@Key
	public short featureIndex

	public ContactFeatureKey() {
	}

	public ContactFeatureKey(int contactId, short featureIndex) {
		this.contactId  = contactId
		this.featureIndex = featureIndex
	}
}
