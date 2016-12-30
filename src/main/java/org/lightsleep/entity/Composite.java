/*
	Composite.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.sql.Connection;

/**
	If an entity class implements this interface,
	<b>select</b>, <b>insert</b>,
	<b>update</b> or <b>delete</b> method of <b>Sql</b> class calls
	<b>postSelect</b>, <b>postInsert</b>,
	<b>postUpdate</b> or <b>postDelete</b> method of the entity class
	after the execution of each execute SQL.<br>

	However if <b>update</b> or <b>delete</b> method dose not have entity parameter, dose not call.

	If an entity is enclose another entity, by implementing this interface,
	You can perform SQL processing to the enclosed entity in conjunction the entity which encloses.

	<div class="sampleTitle"><span>Example of use</span></div>
<div class="sampleCode"><pre>
public class Contact implements PreInsert {
 {@literal @}Key public int id;
    ...
}

public class Phone {
 {@literal @}Key public int contactId;
 {@literal @}Key public short childIndex;
    ...
}

{@literal @}Table("super")
public class ContactComposite extends Contact implements <b>Composite</b> {
 {@literal @}NonColumn
  public final List&lt;Phone&gt; phones = new ArrayList&lt;&gt;();

 {@literal @}Override
  <b>public void postSelect(Connection connection)</b> {
    if (id &gt; 0) {
      new Sql&lt;&gt;(Phone.class)
        .where("{contactId}={}", id)
        .orderBy("{childIndex}")
        .select(connection, phones::add);
    }
  }

 {@literal @}Override
  <b>public int postInsert(Connection connection)</b> {
    short[] childIndex = new short[1];
    <i>// Inserts phones</i>
    childIndex[0] = 1;
    phones.forEach(phone -&gt; {
      phone.contactId = id;
      phone.childIndex = childIndex[0]++;
    });
    int count = new Sql&lt;&gt;(Phone.class).insert(connection, phones);
    return count;
  }

 {@literal @}Override
  <b>public int postUpdate(Connection connection)</b> {
    <i>// Deletes and inserts phones</i>
    int count = postDelete(connection);
    count += postInsert(connection);
    return count;
  }

 {@literal @}Override
  <b>public int postDelete(Connection connection)</b> {
    <i>// Deletes phones</i>
    int count = new Sql&lt;&gt;(Phone.class)
      .where("{contactId}={}", id)
      .delete(connection);
    return count;
  }
</pre></div>

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
		<b>postInsert</b> is executed after a row has been inserted.

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
