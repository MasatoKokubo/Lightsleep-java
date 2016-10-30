/*
	Jdbc.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import javax.sql.DataSource;

/**
	<b>JdbcConnection</b> は、<b>java.sql.DriverManager</b> クラスから直接
	データベース・コネクションを取得する場合に使用します。<br>
	lightsleep.properties ファイルの以下のプロパティを参照します。<br>

	<div class="blankline">&nbsp;</div>

	<table class="additinal">
		<caption>lightsleep.properties の参照</caption>
		<tr><th>プロパティ名</th><th>内 容</th></tr>
		<tr><td>url     </td><td>接続するデータベースの URL</td></tr>
		<tr><td>user    </td><td>データベースに接続する時のユーザー名</td></tr>
		<tr><td>password</td><td>データベースに接続する時のパスワード</td></tr>
		<tr>
			<td><i>その他のプロパティ名</i></td>
			<td><b>DriverManager</b> からデータベース・コネクションを取得する時に使用するその他のプロパティ</td>
		</tr>
	</table>

	@since 1.1.0
	@author Masato Kokubo
*/
public class Jdbc extends AbstractConnectionSupplier {
	/**
		<b>Jdbc</b> を構築します。<br>
		lightsleep.properties
		ファイルで指定された値を設定情報として使用します。
	*/
	public Jdbc() {
	}

// 1.2.0
//	/**
//		<b>Jdbc</b> を構築します。<br>
//		lightsleep.properties および <i>&lt;<b>resourceName</b>&gt;</i>.properties
//		ファイルで指定された値を設定情報として使用します。
//
//		@param resourceName 追加のリソース名
//	*/
//	public Jdbc(String resourceName) {
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
