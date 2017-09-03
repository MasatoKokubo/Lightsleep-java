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
	/** First Name */
	@Column("firstName")
	public String first;

	/** Last Name */
	@Column("lastName")
	public String last;
}
