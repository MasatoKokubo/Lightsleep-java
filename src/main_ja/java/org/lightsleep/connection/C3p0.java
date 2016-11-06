/*
	C3p0.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import javax.sql.DataSource;

/**
	<a href="http://www.mchange.com/projects/c3p0/" target="c3p0">c3p0 JDBC Connection Pool</a>
	を使用してデータベース・コネクションを取得します。
	lightsleep.properties ファイルの以下のプロパティを参照します。<br>

	<div class="blankline">&nbsp;</div>

	<table class="additinal">
		<caption><span>lightsleep.properties の参照</span></caption>
		<tr><th>プロパティ名</th><th>内 容</th></tr>
		<tr><td>url     </td><td>接続するデータベースの URL</td></tr>
		<tr><td>user    </td><td>データベースに接続する時のユーザー名</td></tr>
		<tr><td>password</td><td>データベースに接続する時のパスワード</td></tr>
	</table>

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

// 1.2.0
//	/**
//		<b>C3p0</b> を構築します。<br>
//		lightsleep.properties, <i>&lt;<b>resourceName</b>&gt;</i>.properties
//		および (c3p0.properties または c3p0-config.xml)
//		ファイルで指定された値を設定情報として使用します。
//
//		@param resourceName 追加のリソース名
//	*/
//	public C3p0(String resourceName) {
//	}
////

	/**
		{@inheritDoc}
	*/
	@Override
	protected DataSource getDataSource() {
		return null;
	}
}
