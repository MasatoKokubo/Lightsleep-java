/*
	HikariCP.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import javax.sql.DataSource;

/**
	<a href="http://brettwooldridge.github.io/HikariCP/" target="HikariCP">HikariCP JDBC Connection Pool</a>
	を使用してデータベース・コネクションを取得します。
	lightsleep.properties ファイルの以下のプロパティを参照します。<br>

	<div class="blankline">&nbsp;</div>

	<table class="additinal">
		<caption><span>lightsleep.properties の参照</span></caption>
		<tr><th>プロパティ名</th><th>内 容</th></tr>
		<tr><td>jdbcUrl  </td><td>接続するデータベースの URL</td></tr>
		<tr><td>username </td><td>データベースに接続する時のユーザー名</td></tr>
		<tr><td>password</td><td>データベースに接続する時のパスワード</td></tr>
		<tr>
			<td><i>その他のプロパティ名</i></td>
			<td>
				<a href="https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby" target="HikariCP">
					HikariCP のその他のプロパティ
				</a>
			</td>
		</tr>
	</table>

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

// 1.2.0
//	/**
//		<b>HikariCP</b> を構築します。<br>
//		lightsleep.properties および <i>&lt;<b>resourceName</b>&gt;</i>.properties
//		ファイルで指定された値を設定情報として使用します。
//
//		@param resourceName 追加のリソース名
//	*/
//	public HikariCP(String resourceName) {
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
