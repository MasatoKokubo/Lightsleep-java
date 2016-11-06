/*
	Composite.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.sql.Connection;

/**
	エンティティ・クラスがこのインターフェースを実装している場合、
	<b>Sql</b> クラスの
	<b>select</b>, <b>insert</b>,
	<b>update</b> または <b>delete</b> メソッドで、
	各 SQL の実行後にエンティティ・クラスの <b>postSelect</b>, <b>postInsert</b>,
	<b>postUpdate</b> または <b>postDelete</b> メソッドがコールされます。<br>

	ただし <b>update</b>, <b>delete</b>
	メソッドで、引数にエンティティがない場合は、コールされません。<br>

	エンティティが他のエンティティを内包する場合、このインターフェースを実装する事で、
	内包するエンティティへの SQL 処理を連動して行う事ができるようになります。

	<div class="sampleTitle"><span>使用例</span></div>
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
    <i>// phones を挿入</i>
    childIndex[0] = 1;
    phones.stream().forEach(phone -&gt; {
      phone.contactId = id;
      phone.childIndex = childIndex[0]++;
    });
    int count = new Sql&lt;&gt;(Phone.class).insert(connection, phones);
    return count;
  }

 {@literal @}Override
  <b>public int postUpdate(Connection connection)</b> {
    <i>// phones を削除して挿入</i>
    int count = postDelete(connection);
    count += postInsert(connection);
    return count;
  }

 {@literal @}Override
  <b>public int postDelete(Connection connection)</b> {
    <i>// phones を削除</i>
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
		<b>postSelect</b> は行を SELECT しエンティティに値が格納された後に実行されます。

		@param connection データベース・コネクション

		@throws NullPointerException <b>connection</b> が <b>null</b> の場合
	*/
	void postSelect(Connection connection);

	/**
		<b>postInsert</b> は行の挿入後に実行されます。

		@param connection データベース・コネクション

		@return 挿入された行数

		@throws NullPointerException <b>connection</b> が <b>null</b> の場合
	*/
	int postInsert(Connection connection);

	/**
		<b>postUpdate</b> は行の更新後に実行されます。

		@param connection データベース・コネクション

		@return 更新された行数

		@throws NullPointerException <b>connection</b> が <b>null</b> の場合
	*/
	int postUpdate(Connection connection);

	/**
		<b>postDelete</b> は行の削除後に実行されます。

		@param connection データベース・コネクション

		@return 削除された行数

		@throws NullPointerException <b>connection</b> が <b>null</b> の場合
	*/
	int postDelete(Connection connection);
}
