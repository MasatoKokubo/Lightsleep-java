/*
	Composite.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep;

import java.sql.Connection;

/**
	If an entity class implements this interface,
	<b>select</b>, <b>insert</b>,
	<b>update</b> or <b>delete</b> method of <b>Sql</b> class calls
	<b>postSelect</b>, <b>postInsert</b>,
	<b>postUpdate</b> or <b>postDelete</b> method of the entity class
	after the execution of each execute SQL.<br>

	However if update or delete method dose not have entity parameter, dose not call.

	If an entity is enclose another entity, by implementing this interface,
	You can perform SQL processing to the enclosed entity in conjunction the entity which encloses.

	@since 1.0.0
	@author Masato Kokubo
*/
public interface Composite {
	/**
		<b>postSelect</b> is executed after select a row and set it to the entity.

		@param connection the database connection

		@throws NullPointerException if <b>connection</b> is <b>null</b>
	*/
	void postSelect(Connection connection);

	/**
		<b>postUpdate</b> is executed after a row has been inserted.

		@param connection the database connection

		@return the number of inserted rows

		@throws NullPointerException if <b>connection</b> is <b>null</b>
	*/
	int postInsert(Connection connection);

	/**
		<b>postUpdate</b> is executed after a row has been updated.

		@param connection the database connection

		@return the number of updated rows

		@throws NullPointerException if <b>connection</b> is <b>null</b>
	*/
	int postUpdate(Connection connection);

	/**
		<b>postDelete</b> is executed after a row has been deleted.

		@param connection the database connection

		@return the number of deleted rows

		@throws NullPointerException if <b>connection</b> is <b>null</b>
	*/
	int postDelete(Connection connection);
}
