/*
	HikariCP.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import javax.sql.DataSource;

/**
	<a href="http://brettwooldridge.github.io/HikariCP/" target="Apache">HikariCP JDBC Connection Pool</a>
	を使用して <b>Connection</b> オブジェクトを取得します。

	@since 1.1.0
	@author Masato Kokubo
*/
public class HikariCP extends AbstractConnectionSupplier {
	/**
		<b>HikariCP</b> を構築します。<br>
		lightsleep.properties
		ファイルで指定された値を設定情報として使用します。
	*/
	public HikariCP() {
	}

	/**
		<b>HikariCP</b> を構築します。<br>
		lightsleep.properties および <i>&lt;<b>resourceName</b>&gt;</i>.properties
		ファイルで指定された値を設定情報として使用します。

		@param resourceName 追加のリソース名
	*/
	public HikariCP(String resourceName) {
	}

	/**
		{@inheritDoc}
	*/
	@Override
	protected DataSource getDataSource() {
		return null;
	}
}
