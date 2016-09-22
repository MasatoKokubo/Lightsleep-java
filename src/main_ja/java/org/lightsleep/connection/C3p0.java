/*
	C3p0.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import javax.sql.DataSource;

/**
	<a href="http://www.mchange.com/projects/c3p0/" target="c3p0">c3p0</a>
	を使用して <b>Connection</b> オブジェクトを取得します。

	@since 1.1.0
	@author Masato Kokubo
*/
public class C3p0 extends AbstractConnectionSupplier {
	/**
		<b>C3p0</b> を構築します。<br>
		lightsleep.properties および (c3p0.properties または c3p0-config.xml)
		ファイルで指定された値を設定情報として使用します。
	*/
	public C3p0() {
	}

	/**
		<b>C3p0</b> を構築します。<br>
		lightsleep.properties, <i>&lt;<b>resourceName</b>&gt;</i>.properties
		および (c3p0.properties または c3p0-config.xml)
		ファイルで指定された値を設定情報として使用します。

		@param resourceName 追加のリソース名
	*/
	public C3p0(String resourceName) {
	}

	/**
		{@inheritDoc}
	*/
	@Override
	protected DataSource getDataSource() {
		return null;
	}
}
