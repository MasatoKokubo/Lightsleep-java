// PostgreSQL.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

/**
 * <a href="http://www.postgresql.org/" target="PostgreSQL">PostgreSQL</a>
 * 用のデータベース・ハンドラーです。<br>
 *
 * スーパークラスで追加された <b>TypeConverter</b> オブジェクトに以下を追加するか置き換えます。<br>
 * <br>
 *
 * <table class="additional">
 *   <caption><span>追加される TypeConverter オブジェクト</span></caption>
 *   <tr><th>変換元データ型</th><th>変換先データ型</th><th>変換内容</th></tr>
 *   <tr><td>String </td><td rowspan="2">SqlString</td><td><code>'...'</code><br>制御文字は エスケープ・シーケンスに変換<br>長い場合は <code>?</code> <i>(SQLパラメータ)</i></td></tr>
 *   <tr><td>byte[] </td><td><code>E'\\x...'</code><br>長い場合は <code>?</code> <i>(SQLパラメータ)</i></td></tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class PostgreSQL extends Standard {
	/**
	 * <b>PostgreSQL</b> オブジェクトを返します。
	 *
	 * @return PostgreSQL オブジェクト
	 */
	public static Database instance() {
		return null;
	}

	/**
	 * <b>PostgreSQL</b> を構築します。
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
}
