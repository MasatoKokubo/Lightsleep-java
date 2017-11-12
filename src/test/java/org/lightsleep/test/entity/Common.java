// Common.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

import java.sql.Timestamp;

import org.lightsleep.connection.ConnectionWrapper;
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
// 2.1.0
//	public int preInsert(Connection conn) {
	public int preInsert(ConnectionWrapper conn) {
////
		id = Numbering.getNewId(conn, getClass());
		return 0;
	}
}
