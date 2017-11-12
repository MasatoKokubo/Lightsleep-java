// HikariCP.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.util.Properties;
import java.util.function.Consumer;
import javax.sql.DataSource;

import org.lightsleep.helper.Resource;

/**
 * <a href="http://brettwooldridge.github.io/HikariCP/" target="HikariCP">HikariCP JDBC Connection Pool</a>
 * を使用してコネクション･ラッパーを取得します。
 * lightsleep.propertiesファイルの以下のプロパティを参照します。<br>
 *
 * <div class="blankline">&nbsp;</div>
 *
 * <table class="additional">
 *   <caption><span>lightsleep.propertiesの参照</span></caption>
 *   <tr><th>プロパティ名</th><th>内 容</th></tr>
 *   <tr><td>jdbcUrl  </td><td>接続するデータベースの URL</td></tr>
 *   <tr><td>username </td><td>データベースに接続する時のユーザー名</td></tr>
 *   <tr><td>password</td><td>データベースに接続する時のパスワード</td></tr>
 *   <tr>
 *     <td><i>その他のプロパティ名</i></td>
 *     <td>
 *       <a href="https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby" target="HikariCP">
 *         HikariCP のその他のプロパティ
 *       </a>
 *     </td>
 *   </tr>
 * </table>
 *
 * @since 1.1.0
 * @author Masato Kokubo
 */
public class HikariCP extends AbstractConnectionSupplier {
	/**
	 * <b>HikariCP</b>を構築します。
	 *
	 * <p>
	 * lightsleep.propertiesファイルで指定された値をコネクション情報として使用します。
	 * </p>
	 */
	public HikariCP() {
		super(Resource.getGlobal().getProperties(), modifier -> {});
	}

	/**
	 * <b>HikariCP</b>を構築します。
	 *
	 * <p>
	 * lightsleep.propertiesファイルで指定された値をコネクション情報として使用します。
	 * </p>
	 *
	 * @param modifier propertiesを変更するコンシューマー
	 *
	 * @since 1.5.0
	 */
	public HikariCP(Consumer<Properties> modifier) {
		super(Resource.getGlobal().getProperties(), modifier);
	}

	/**
	 * <b>HikariCP</b>を構築します。
	 *
	 * @param properties コネクション情報を含むプロパティ
	 *
	 * @since 2.1.0
	 */
	public HikariCP(Properties properties) {
		super(properties, modifier -> {});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataSource getDataSource() {
		return null;
	}
}
