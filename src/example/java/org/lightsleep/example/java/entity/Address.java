//  Address.java
//  (C) 2016 Masato Kokubo

package org.lightsleep.example.java.entity;

import org.lightsleep.entity.Key;

public class Address {
	@Key
	public int    contactId;
	public short  childIndex;
	public String label;
	public String postCode;
	public String content0;
	public String content1;
	public String content2;
	public String content3;
}
