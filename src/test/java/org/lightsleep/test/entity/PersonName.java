// PersonName.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

import org.lightsleep.entity.Column;

/**
 * Properties of person name.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class PersonName {
	/** Family Name */
	@Column("familyName")
	public String family;

	/** Given Name */
	@Column("givenName")
	public String given;
}
