// PostgreSQL.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

/**
 * <a href="http://www.postgresql.org/" target="PostgreSQL">PostgreSQL</a>
 * 用のデータベース・ハンドラーです。<br>
 * 
 * このクラスのオブジェクトは、
 * {@linkplain org.lightsleep.helper.TypeConverter#typeConverterMap}
 * の内容に加え以下の <b>TypeConverter</b> を追加した <b>TypeConverter</b> マップを持ちます。
 * 
 * <table class="additional">
 * 	<caption><span>登録されている TypeConverter オブジェクト</span></caption>
 *   <tr><th>変換元データ型</th><th>変換先データ型</th><th>変換フォーマット</th></tr>
 *   <tr><td>String </td><td rowspan="2">{@linkplain org.lightsleep.component.SqlString}</td>
                         <td>長い場合は <i>SQL パラメータ (?)</i>、そうでなければ '...' (エスケープ・シーケンスを含む場合あり)</td></tr>
 *   <tr><td>byte[] </td><td>長い場合は <i>SQL パラメータ (?)</i>、そうでなければ E'\\x...'</td></tr>
 * </table>
 * 
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class PostgreSQL extends Standard {
	/**
		<b>PostgreSQL</b> オブジェクトを返します。

		@return PostgreSQL オブジェクト
	*/
	public static Database instance() {
		return null;
	}

	/**
		<b>PostgreSQL</b> を構築します。
	*/
	protected PostgreSQL() {
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean supportsOffsetLimit() {
		return true;
	}
}
