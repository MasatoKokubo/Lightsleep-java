/*
	Dbcp.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import javax.sql.DataSource;

/**
	<a href="http://commons.apache.org/proper/commons-dbcp/" target="Apache">Apache Commons DBCP</a>
	を使用して <b>Connection</b> オブジェクトを取得します。

	@since 1.1.0
	@author Masato Kokubo
*/
public class Dbcp extends AbstractConnectionSupplier {
	/**
		<b>Dbcp</b> を構築します。<br>
		lightsleep.properties
		ファイルで指定された値を設定情報として使用します。
	*/
	public Dbcp() {
	}

	/**
		<b>Dbcp</b> を構築します。<br>
		lightsleep.properties および <i>&lt;<b>resourceName</b>&gt;</i>.properties
		ファイルで指定された値を設定情報として使用します。

		@param resourceName 追加のリソース名
	*/
	public Dbcp(String resourceName) {
	}

	/**
		{@inheritDoc}
	*/
	@Override
	protected DataSource getDataSource() {
		return null;
	}
}
