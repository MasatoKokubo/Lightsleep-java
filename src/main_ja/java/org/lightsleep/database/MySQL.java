// MySQL.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

/**
 * <a href="http://www.mysql.com/" target="MySQL">MySQL</a>
 * 用のデータベース・ハンドラーです。<br>
 * 
 * このクラスのオブジェクトは、
 * {@linkplain Standard#typeConverterMap}.
 * に以下の <b>TypeConverter</b> を追加した <b>TypeConverter</b> マップを持ちます。
 * 
 * <table class="additional">
 *   <caption><span>登録されている TypeConverter オブジェクト</span></caption>
 *   <tr><th>変換元データ型</th><th>変換先データ型</th><th>変換フォーマット</th></tr>
 *   <tr><td>boolean</td><td rowspan="2">{@linkplain org.lightsleep.component.SqlString}</td><td>0 か 1</td></tr>
 *   <tr><td>String </td><td>長い場合は <i>SQL パラメータ (?)</i>、そうでなければ '...' (エスケープ・シーケンスを含む場合あり)</td></tr>
 * </table>
 * 
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class MySQL extends Standard {
	/**
		<b>MySQL</b> オブジェクトを返します。

		@return MySQL オブジェクト
	*/
	public static Database instance() {
		return null;
	}

	/**
		<b>MySQL</b> を構築します。
	*/
	protected MySQL() {
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean supportsOffsetLimit() {
		return true;
	}
}
