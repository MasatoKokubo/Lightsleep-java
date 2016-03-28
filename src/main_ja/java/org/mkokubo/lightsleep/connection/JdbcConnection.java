/*
	JdbcConnection.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.connection;

import java.sql.Connection;

import org.mkokubo.lightsleep.RuntimeSQLException;

/**
	<b>JdbcConnection</b> は、<b>java.sql.DriverManager</b> クラスから直接
	<b>Connection</b> オブジェクトを取得する場合に使用します。<br>
	lightsleep.properties ファイルの以下のプロパティを参照します。<br>

	<div class="blankline">&nbsp;</div>

	<table class="additinal">
		<caption>lightsleep.properties の参照</caption>
		<tr><th>プロパティ名</th><th>内 容</th></tr>
		<tr><td>JdbcConnection.driver  </td><td>JDBCドライバのクラス名</td></tr>
		<tr><td>JdbcConnection.url     </td><td>接続するデータベースの URL</td></tr>
		<tr><td>JdbcConnection.user    </td><td>データベースに接続する時のユーザー名</td></tr>
		<tr><td>JdbcConnection.password</td><td>データベースに接続する時のパスワード</td></tr>
		<tr>
			<td>JdbcConnection.<i>プロパティ名</i></td>
			<td><b>DriverManager</b> からコネクションを取得する時に使用するその他のプロパティ</td>
		</tr>
	</table>

	@since 1.0.0
	@author Masato Kokubo
*/
public class JdbcConnection implements ConnectionSupplier {
	/**
		<b>JdbcConnection</b> を構築します。<br>
		lightsleep.properties ファイルで指定された値を接続情報として使用します。

		@see #JdbcConnection(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	*/
	public JdbcConnection() {
	}

	/**
		<b>JdbcConnection</b> を構築します。<br>
		<b>driver</b>, <b>url</b>, <b>user</b>, <b>password</b> が
		<b>null</b> の場合、lightsleep.properties ファイルで指定されたそれぞれの値を使用します。

		@param driver JDBCドライバのクラス名
		@param url 接続するデータベースの URL
		@param user データベースに接続する時のユーザー名
		@param password データベースに接続する時のパスワード
	*/
	public JdbcConnection(String driver, String url, String user, String password) {
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
