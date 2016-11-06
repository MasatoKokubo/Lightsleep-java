/*
	Transaction.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep;

import java.sql.Connection;
import java.sql.SQLException;

/**
	トランザクションを実行するための関数型インターフェースです。

	<div class="sampleTitle"><span>使用例</span></div>
<div class="sampleCode"><pre>
<b>Transaction.execute(connection -&gt; {</b>
    new Sql&lt;&gt;(Person.class)
        .update(connection, person);
<b>});</b>
</pre></div>

	@since 1.0
	@author Masato Kokubo
*/
@FunctionalInterface
public interface Transaction {
	/**
		トランザクションの本体をこのメソッドに記述します。

		@param connection データベース・コネクション

		@throws RuntimeSQLException データベースのアクセス中に <b>SQLException</b> がスローされた場合
	*/
	void executeBody(Connection connection);

	/**
		以下の順でトランザクションを実行します。<br>
		<br>
		<ol>
			<li><b>Sql.connectionSupplier</b> をコールしてデータベース・コネクションを取得</li>
			<li><b>transaction.executeBody</b> メソッドを実行</li>
			<li>トランザクションをコミット</li>
			<li>データベース・コネクションをクローズ</li>
		</ol>
		<br>

		トランザクションの本体の実行中に例外がスローされた場合、コミットではなくロールバックを行います。<br>
		<br>

		<b>transaction</b> にラムダ式でトランザクションの実体を記述してください。

		@param transaction <b>Transaction</b> オブジェクト

		@throws RuntimeSQLException データベースのアクセス中に <b>SQLException</b> がスローされた場合
	*/
	static void execute(Transaction transaction) {
	}

	/**
		コネクションが自動コミットでなければ、トランザクションをコミットします。

		@param connection データベース・コネクション

		@throws RuntimeSQLException データベース・アクセス・エラーが発生した場合
	*/
	static void commit(Connection connection) {
	}

	/**
		コネクションが自動コミットでなければ、トランザクションをロールバックします。

		@param connection データベース・コネクション

		@throws RuntimeSQLException データベースのアクセス中に <b>SQLException</b> がスローされた場合
	*/
	static void rollback(Connection connection) {
	}
}
