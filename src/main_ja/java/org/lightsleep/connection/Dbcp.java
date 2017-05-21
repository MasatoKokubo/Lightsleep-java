// Dbcp.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.util.Properties;
import java.util.function.Consumer;
import javax.sql.DataSource;

/**
 * <a href="http://commons.apache.org/proper/commons-dbcp/" target="Apache">Apache Commons DBCP 2</a>
 * を使用してデータベース・コネクションを取得します。
 * lightsleep.properties ファイルの以下のプロパティを参照します。<br>
 *
 * <div class="blankline">&nbsp;</div>
 *
 * <table class="additional">
 *   <caption><span>lightsleep.properties の参照</span></caption>
 *   <tr><th>プロパティ名</th><th>内 容</th></tr>
 *   <tr><td>url     </td><td>接続するデータベースの URL</td></tr>
 *   <tr><td>username</td><td>データベースに接続する時のユーザー名</td></tr>
 *   <tr><td>password</td><td>データベースに接続する時のパスワード</td></tr>
 *   <tr>
 *     <td><i>その他のプロパティ名</i></td>
 *     <td>
 *       <a href="http://commons.apache.org/proper/commons-dbcp/configuration.html" target="Apache">
 *       DBCP 2 のその他のプロパティ
 *       </a>
 *     </td>
 *   </tr>
 * </table>
 *
 * @since 1.1.0
 * @author Masato Kokubo
 */
public class Dbcp extends AbstractConnectionSupplier {
	/**
	 * <b>Dbcp</b> を構築します。<br>
	 * lightsleep.properties
	 * ファイルで指定された値を設定情報として使用します。
	 */
	public Dbcp() {
	}

	/**
	 * <b>Dbcp</b> を構築します。<br>
	 * lightsleep.properties
	 * ファイルで指定された値を設定情報として使用します。
	 *
	 * @param modifier properties を変更するコンシューマー
	 *
	 * @since 1.5.0
	 */
	public Dbcp(Consumer<Properties> modifier) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataSource getDataSource() {
		return null;
	}
}
