// MySQL.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import org.lightsleep.Sql;

/**
 * <a href="http://www.mysql.com/" target="MySQL">MySQL</a>
 * 用のデータベース･ハンドラです。
 *
 * <p>
 * このクラスのオブジェクトは、{@linkplain Standard#typeConverterMap}
 * に以下の<b>TypeConverter</b>オブジェクトを追加した<b>TypeConverter</b>マップを持ちます。
 * </p>
 *
 * <table class="additional">
 *   <caption><span>追加されるTypeConverterオブジェクト</span></caption>
 *   <tr><th>変換元データ型</th><th>変換先データ型</th><th>変換内容</th></tr>
 *   <tr><td>Boolean</td><td rowspan="2">SqlString</td><td>false ➔ <code>0</code><br>true ➔ <code>1</code></td></tr>
 *   <tr><td>String </td><td><code>'...'</code><br>制御文字はエスケープ･シーケンスに変換<br>長い文字列の場合は<code>?</code><i>(SQLパラメータ)</i></td></tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class MySQL extends Standard {
	/**
	 * パスワードのパターン文字列
	 *
	 * @since 2.2.0
	 */
	protected static final String PASSWORD_PATTERN = "";

	/**
	 * このクラスの唯一のインスタンス
	 *
	 * @since 2.1.0
	 */
	public static final MySQL instance = new MySQL();

	/**
	 * このクラスの唯一のインスタンスを返します。
	 *
	 * <p>
	 * @deprecated リリース 2.1.0 より。代わりに{@link #instance}を使用してください。
	 * </p>
	 *
	 * @return このクラスの唯一のインスタンス
	 */
// 2.1.0
	@Deprecated
////
	public static Database instance() {
		return null;
	}

	/**
	 * <b>MySQL</b>を構築します。
	 */
	protected MySQL() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportsOffsetLimit() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 2.2.0
	 */
	@Override
	public String maskPassword(String jdbcUrl) {
		return null;
	}
}
