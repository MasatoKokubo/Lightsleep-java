// SQLite.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import org.lightsleep.Sql;
import org.lightsleep.component.SqlString;
import org.lightsleep.helper.TypeConverter;

/**
 * <a href="https://www.sqlite.org/index.html" target="SQLite">SQLite</a>
 * 用のデータベース・ハンドラーです。<br>
 *
 * このクラスのオブジェクトは、
 * {@linkplain Standard#typeConverterMap}.
 * に以下の <b>TypeConverter</b> を追加した <b>TypeConverter</b> マップを持ちます。
 *
 * <table class="additional">
 *   <caption><span>登録されている TypeConverter オブジェクト</span></caption>
 *   <tr><th>変換元データ型</th><th>変換先データ型</th><th>変換フォーマット</th></tr>
 *   <tr><td>boolean       </td><td rowspan="6">{@linkplain org.lightsleep.component.SqlString}</td><td>0 か 1</td></tr>
 *   <tr><td>java.util.Date</td><td rowspan="2">'yyyy-MM-dd'</td></tr>
 *   <tr><td>java.sql.Date </td></tr>
 *   <tr><td>Time          </td><td>'HH:mm:ss'</td></tr>
 *   <tr><td>Timestamp     </td><td>'yyyy-MM-dd HH:mm:ss.SSS'</td></tr>
 *   <tr><td>byte[]        </td><td>常に <i>SQL パラメータ (?)</i></td></tr>
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
	 */
	@Override
	public <E> String selectSql(Sql<E> sql, List<Object> parameters) {
		return null;
	}
}
