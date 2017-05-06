// Common.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

import java.sql.Connection;
import java.sql.Timestamp;

import org.lightsleep.entity.*;

/**
 * Common properties of entities.
 */
public abstract class Common implements PreInsert {
	/** Identifier */
	@Key
	public int id;

	/** Update count */
	@Insert("0")
	@Update("{updateCount}+1")
	public int updateCount;

    /** Created Timestamp */
	@Insert("CURRENT_TIMESTAMP")
	@NonUpdate
	public Timestamp created;

	/** Updated Timestamp */
	@Insert("CURRENT_TIMESTAMP")
	@Update("CURRENT_TIMESTAMP")
	public Timestamp updated;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int preInsert(Connection connection) {
		id = Numbering.getNewId(connection, getClass());
		return 0;
	}
}
