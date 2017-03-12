/*
	PreInsert.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.sql.Connection;

/**
	エンティティ・クラスがこのインターフェースを実装している場合、
	<b>Sql</b> クラスの <b>insert</b> メソッドで、
	INSERT SQL 実行前に <b>preInsert</b> メソッドがコールされます。<br>

	<b>preInsert</b> メソッドでは、プライマリー・キーの採番の実装等を行います。

	<div class="sampleTitle"><span>Example of use</span></div>
<div class="sampleCode"><pre>
public class Contact implements <b>PreInsert</b> {

 {@literal @}Override
  <b>public int preInsert(Connection connection)</b> {
    <i>// 挿入前に ID を採番</i>
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
		<b>preInsert</b> は行が挿入される前に実行されます。

		@param connection データベース・コネクション

		@return 挿入された行数

		@throws NullPointerException <b>connection</b> が null の場合
	*/
	int preInsert(Connection connection);
}
