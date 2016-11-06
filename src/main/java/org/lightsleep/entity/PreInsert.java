/*
	PreInsert.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.sql.Connection;

/**
	If an entity class implements this interface,
	<b>insert</b> method of Sql class calls <b>preInsert</b> method
	of the entity before INSERT SQL execution.<br>

	In <b>preInsert method,</b> do the implementation of the numbering of the primary key or etc.

	<div class="sampleTitle"><span>Example of use</span></div>
<div class="sampleCode"><pre>
public class Contact implements <b>PreInsert</b> {
 {@literal @}Key public int id;
    ...

 {@literal @}Override
  <b>public int preInsert(Connection connection)</b> {
    <i>// Numbering of ID before INSERT</i>
    id = NextId.getNewId(connection, Contact.class);
    return 0;
  }
}
</pre></div>

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
