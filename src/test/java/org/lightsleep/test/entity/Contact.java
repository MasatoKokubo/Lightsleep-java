// Contact.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

import java.sql.Date;

/**
 * The entity of contact table.
 */
public class Contact extends Common {
	/** Person Name */
	public final PersonName name = new PersonName();

	/** Birthday */
	public Date birthday;

	/** Address Identifier */
	public int addressId;
}
