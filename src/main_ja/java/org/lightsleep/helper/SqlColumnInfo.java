// SqlColumnInfo.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

/**
 * テーブル別名とカラム情報を持ちます。
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class SqlColumnInfo {
	/**
	 * <b>SqlColumnInfo</b> を構築します。
	 *
	 * @param tableAlias テーブル別名
	 * @param columnInfo カラム情報
	 *
	 * @throws NullPointerException <b>tableAlias</b> または <b>columnInfo</b> が null の場合
	 */
	public SqlColumnInfo(String tableAlias, ColumnInfo columnInfo) {
	}

	/**
	 * テーブル別名を返します。
	 *
	 * @return テーブル別名
	 */
	public String tableAlias() {
		return null;
	}

	/**
	 * カラム情報を返します。
	 *
	 * @return カラム情報
	 */
	public ColumnInfo columnInfo() {
		return null;
	}

	/**
	 * 指定の名前がこのカラム情報のプロパティ名とマッチするかどうかを返します。<br>
	 *
	 * 名前に <b>'.'</b> が含まれる場合、<b>'.'</b> の左側がテーブル別名と比較され、右側がプロパティ名と比較されます。<br>
	 * 含まれない場合、名前全体がプロパティ名と比較されます。<br>
	 *
	 * テーブル別名部とこのインスタンスのテーブル別名が等しくかつ
	 * プロパティ部とカラム情報のプロパティ名が等しい場合にマッチします。<br>
	 * 
	 * @param name 名前
	 * @return マッチするな <b>true</b>、そうでなければ <b>false</b>
	 *
	 * @throws NullPointerException <b>name</b> が null の場合
	 */
	public boolean matches(String name) {
		return false;
	}
}
