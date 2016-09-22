/*
	TomcatCP.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import javax.sql.DataSource;

/**
	<a href="http://people.apache.org/~fhanik/jdbc-pool/jdbc-pool.html" target="Apache">TomcatCP JDBC Connection Pool</a>
	を使用して <b>Connection</b> オブジェクトを取得します。

	@since 1.1.0
	@author Masato Kokubo
*/
public class TomcatCP extends AbstractConnectionSupplier {
	/**
		<b>TomcatCP</b> を構築します。<br>
		lightsleep.properties
		ファイルで指定された値を設定情報として使用します。
	*/
	public TomcatCP() {
	}

	/**
		<b>TomcatCP</b> を構築します。<br>
		lightsleep.properties および <i>&lt;<b>resourceName</b>&gt;</i>.properties
		ファイルで指定された値を設定情報として使用します。

		@param resourceName 追加のリソース名
	*/
	public TomcatCP(String resourceName) {
	}

	/**
		{@inheritDoc}
	*/
	@Override
	protected DataSource getDataSource() {
		return null;
	}
}
