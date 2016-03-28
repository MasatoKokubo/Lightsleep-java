/*
	JndiConnection.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.connection;

import java.sql.Connection;

import org.mkokubo.lightsleep.RuntimeSQLException;

/**
	<b>JndiConnection</b> は、JNDI (Java Naming and Directory Interface) API
	で取得するデータソースを使用する場合に使用します。<br>
	lightsleep.properties ファイルの以下のプロパティを参照します。<br>

	<div class="blankline">&nbsp;</div>

	<table class="additinal">
		<caption>lightsleep.properties の参照</caption>
		<tr><th>プロパティ名</th><th>内 容</th></tr>
		<tr><td>JndiConnection.dataSource</td><td>データソースのリソース名</td></tr>
	</table>

	@since 1.0.0
	@author Masato Kokubo
*/
public class JndiConnection implements ConnectionSupplier {
	/**
		<b>JndiConnection</b> を構築します。<br>
		lightsleep.properties ファイルで指定された値を接続情報に使用します。

		@see #JndiConnection(java.lang.String)
	*/
	public JndiConnection() {
	}

	/**
		<b>JndiConnection</b> を構築します。<br>
		<b>"java:/comp/env/" + dataSourceName</b> の文字列でデータソースを検索します。
		<b>dataSourceName</b> が <b>null</b> の場合、
		lightsleep.properties ファイルで指定された値を使用します。

		@param dataSourceName データソース名 (<b>null</b> 可)
	*/
	public JndiConnection(String dataSourceName) {
	}

	/**
		{@inheritDoc}

		@throws RuntimeSQLException データベース・アクセス・エラーが発生した場合
	*/
	@Override
	public Connection get() {
		return null;
	}
}
