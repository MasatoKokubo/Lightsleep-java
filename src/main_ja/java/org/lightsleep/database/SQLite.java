// SQLite.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import org.lightsleep.Sql;

/**
 * <a href="https://www.sqlite.org/index.html" target="SQLite">SQLite</a>
 * 用のデータベース・ハンドラーです。<br>
 *
 * スーパークラスで追加された <b>TypeConverter</b> オブジェクトに以下を追加するか置き換えます。<br>
 * <br>
 *
 * <table class="additional">
 *   <caption><span>追加される TypeConverter オブジェクト</span></caption>
 *   <tr><th>変換元データ型</th><th>変換先データ型</th><th>変換内容</th></tr>
 *   <tr><td>Boolean       </td><td rowspan="6">SqlString</td><td>false ➔ <code>0</code><br>true ➔ <code>1</code></td></tr>
 *   <tr><td>java.util.Date</td><td rowspan="2"><code>'yyyy-MM-dd'</code></td></tr>
 *   <tr><td>java.sql.Date </td></tr>
 *   <tr><td>Time          </td><td><code>'HH:mm:ss'</code></td></tr>
 *   <tr><td>Timestamp     </td><td><code>'yyyy-MM-dd HH:mm:ss.SSS'</code></td></tr>
 *   <tr><td>byte[]        </td><td><code>?</code> <i>(SQLパラメータ)</i></td></tr>
 * </table>

 * @since 1.7.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class SQLite extends Standard {
	/**
	 * <b>SQLite</b> オブジェクトを返します。
	 *
	 * @return SQLite オブジェクト
	 */
	public static Database instance() {
		return null;
	}

	/**
	 * <b>SQLite</b> を構築します。
	 */
	protected SQLite() {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.8.2
	 */
	@Override
	public boolean supportsOffsetLimit() {
		return true;
	}
}
