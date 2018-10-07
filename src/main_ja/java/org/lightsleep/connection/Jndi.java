// Jndi.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.util.Properties;

import javax.sql.DataSource;

/**
 * <b>Jndi</b>は、JNDI (Java Naming and Directory Interface) API
 * で取得するデータソースを使用する場合に使用します。<br>
 * lightsleep.propertiesファイルの以下のプロパティを参照します。<br>
 *
 * <div class="blankline">&nbsp;</div>
 *
 * <table class="additional">
 *   <caption><span>lightsleep.propertiesの参照</span></caption>
 *   <tr><th>プロパティ名</th><th>内 容</th></tr>
 *   <tr><td>dataSource</td><td>データソースのリソース名</td></tr>
 * </table>
 *
 * @since 1.1.0
 * @author Masato Kokubo
 */
public class Jndi extends AbstractConnectionSupplier {
	/**
	 * <b>Jndi</b>を構築します。
	 *
	 * <p>
	 * lightsleep.propertiesファイルで指定された値を接続情報に使用します。
	 * </p>
	 *
	 * @see #Jndi(java.lang.String)
	 */
	public Jndi() {
		super(null, null);
	}

	/**
	 * <b>Jndi</b>を構築します。
	 *
	 * <p>
	 * <b>"java:/comp/env/" + dataSourceName</b>の文字列でデータソースを検索します。
	 * <b>dataSourceName</b>が<b>null</b>の場合、lightsleep.propertiesファイルで指定された値を使用します。
	 * </p>
	 *
	 * @param dataSourceName データソース名(null可)
	 */
	public Jndi(String dataSourceName) {
		super(null, null);
	}

	/**
	 * <b>Jndi</b>を構築します。
	 *
	 * @param properties コネクション情報を含むプロパティ
	 *
	 * @since 2.1.0
	 */
	public Jndi(Properties properties) {
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
