// C3p0.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.util.Properties;
import java.util.function.Consumer;
import javax.sql.DataSource;

/**
 * <a href="http://www.mchange.com/projects/c3p0/" target="c3p0">c3p0 JDBC Connection Pool</a>
 * を使用してコネクション･ラッパーを取得します。
 * lightsleep.propertiesファイルの以下のプロパティを参照します。<br>
 *
 * <div class="blankline">&nbsp;</div>
 *
 * <table class="additional">
 *   <caption><span>lightsleep.propertiesの参照</span></caption>
 *   <tr><th>プロパティ名</th><th>内 容</th></tr>
 *   <tr><td>url     </td><td>接続するデータベースの URL</td></tr>
 *   <tr><td>user    </td><td>データベースに接続する時のユーザー名</td></tr>
 *   <tr><td>password</td><td>データベースに接続する時のパスワード</td></tr>
 * </table>
 *
 * @since 1.1.0
 * @author Masato Kokubo
 */
public class C3p0 extends AbstractConnectionSupplier {
	/**
	 * <b>C3p0</b>を構築します。
	 *
	 * <p>
	 * lightsleep.propertiesおよび(c3p0.properties または c3p0-config.xml)ファイルで指定された値をコネクション情報として使用します。
	 * </p>
	 */
	public C3p0() {
		super(null, null);
	}

	/**
	 * <b>C3p0</b>を構築します。
	 *
	 * <p>
	 * lightsleep.propertiesおよび(c3p0.properties または c3p0-config.xml)ファイルで指定された値をコネクション情報として使用します。
	 * </p>
	 *
	 * @param modifier propertiesを変更するコンシューマー
	 *
	 * @since 1.5.0
	 */
	public C3p0(Consumer<Properties> modifier) {
		super(null, null);
	}

	/**
	 * <b>C3p0</b>を構築します。
	 *
	 * @param properties コネクション情報を含むプロパティ
	 *
	 * @since 2.1.0
	 */
	public C3p0(Properties properties) {
		super(null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataSource getDataSource() {
		return null;
	}
}
