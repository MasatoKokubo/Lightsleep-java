/*
	PreInsert.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.entity;

import java.sql.Connection;

/**
	エンティティ・クラスがこのインターフェースを実装している場合、
	<b>Sql</b> クラスの <b>insert</b> メソッドで、
	INSERT SQL 実行前に <b>preInsert</b> メソッドがコールされます。<br>

	<b>preInsert</b> メソッドでは、プライマリー・キーの採番の実装等を行います。

	@since 1.0.0
	@author Masato Kokubo
*/
public interface PreInsert {
	/**
		<b>preInsert</b> は行が挿入される前に実行されます。

		@param connection データベース・コネクション

		@return 挿入された行数

		@throws NullPointerException <b>connection</b> が <b>null</b> の場合
	*/
	int preInsert(Connection connection);
}
