/*
	Composite.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep;

import java.sql.Connection;

/**
	エンティティ・クラスがこのインターフェースを実装している場合、
	<b>Sql</b> クラスの
	<b>select</b>, <b>insert</b>,
	<b>update</b> または <b>delete</b> メソッドで、
	各 SQL の実行後にエンティティ・クラスの <b>postSelect</b>, <b>postInsert</b>,
	<b>postUpdate</b> または <b>postDelete</b> メソッドがコールされます。<br>

	ただし <b>update</b>, <b>delete</b>
	メソッドで、エンティティが引数にない場合は、コールされません。<br>

	エンティティが他のエンティティを内包する場合、このインターフェースを実装する事で、
	内包するエンティティへの SQL 処理を連動して行う事ができるようになります。

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
