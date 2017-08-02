//  Url.java
//  (C) 2016 Masato Kokubo

package org.lightsleep.example.java.entity;

import org.lightsleep.entity.Key;

public class Url {
	@Key
	public int    contactId;
	public short  childIndex;
	public String label;
	public String content;
}
