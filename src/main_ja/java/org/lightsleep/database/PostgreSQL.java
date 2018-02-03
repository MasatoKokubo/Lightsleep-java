// PostgreSQL.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

/**
 * <a href="http://www.postgresql.org/" target="PostgreSQL">PostgreSQL</a>
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
 *   <tr><td>String </td><td rowspan="2">SqlString</td><td><code>'...'</code><br>制御文字は エスケープ･シーケンスに変換<br>変換された文字列がエスケープ･シーケンスを含む場合はE'...'<br>長い文字列の場合は<code>?</code><i>(SQLパラメータ)</i></td></tr>
 *   <tr><td>byte[] </td><td><code>E'\\x...'</code><br>長いバイト配列の場合は<code>?</code><i>(SQLパラメータ)</i></td></tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class PostgreSQL extends Standard {
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
	public static final PostgreSQL instance = new PostgreSQL();

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
	 * <b>PostgreSQL</b>を構築します。
	 */
	protected PostgreSQL() {
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
