// DB2.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import org.lightsleep.Sql;
import org.lightsleep.component.SqlString;
import org.lightsleep.helper.TypeConverter;

/**
 * <a href="https://www.ibm.com/us-en/marketplace/db2-express-c" target="DB2">DB2</a>
 * 用のデータベース･ハンドラです。
 *
 * <p>
 * このクラスのオブジェクトは、{@linkplain Standard#typeConverterMap}
 * に以下の<b>TypeConverter</b>オブジェクトを追加した<b>TypeConverter</b>マップを持ちます。
 * </p>
 *
 * <table class="additional">
 *   <caption><span>Registered TypeConverter objects</span></caption>
 *   <tr><th>Source Data Type</th><th>Destination Data Type</th><th>Conversion Contents</th></tr>
 *   <tr><td>byte[] </td><td>SqlString</td><td><code>BX'...'</code><br>長いバイト配列の場合は<code>?</code><i>(SQLパラメータ)</i></td></tr>
 * </table>
 *
 * @since 1.9.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 * @see org.lightsleep.database.Standard
 */
public class DB2 extends Standard {
	/**
	 * このクラスの唯一のインスタンス
	 *
	 * @since 2.1.0
	 */
	public static final DB2 instance = new DB2();

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
	 * <b>DB2</b>を構築します。
	 */
	protected DB2() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected <E> void appendForUpdate(StringBuilder buff, Sql<E> sql) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportsOffsetLimit() {
		return true;
	}
}
