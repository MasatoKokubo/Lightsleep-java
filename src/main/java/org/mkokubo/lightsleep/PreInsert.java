/*
	PreInsert.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep;

import java.sql.Connection;

/**
	If an entity implements this interface,
	<b>insert</b> method of Sql class calls <b>preInsert</b> method
	of the entity before INSERT SQL execution.<br>

	In <b>preInsert,</b> do the implementation of the numbering of the primary key or etc.

	@since 1.0.0
	@author Masato Kokubo
*/
public interface PreInsert {
	/**
		<b>preInsert</b> is executed before a row is inserted.

		@param connection the database connection

		@return the number of inserted rows

		@throws NullPointerException if <b>connection</b> is <b>null</b>
	*/
	int preInsert(Connection connection);
}
