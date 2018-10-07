// EntityInfo.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

import java.util.List;

/**
 * エンティティクラスの情報を持ちます。
 *
 * @param <E> エンティティの型
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class EntityInfo<E> {
	/**
	 * <b>EntityInfo</b>オブジェクトを構築します。
	 *
	 * @param entityClass エンティティクラス
	 *
	 * @throws NullPointerException <b>entityClass</b>が<b>null</b>の場合
	 */
	public EntityInfo(Class<E> entityClass) {
	}

	/**
	 * エンティティクラスを返します。
	 *
	 * @return エンティティクラス
	 */
	public Class<E> entityClass() {
		return null;
	}

	/**
	 * アクセッサを返します。
	 *
	 * @return アクセッサ
	 */
	public Accessor<E> accessor() {
		return null;
	}

	/**
	 * テーブル名を返します。
	 *
	 * @return テーブル名
	 */
	public String tableName() {
		return null;
	}

	/**
	 * 指定のプロパティ名に関連するカラム情報を返します。
	 *
	 * @param propertyName プロパティ名
	 * @return カラム情報
	 *
	 * @throws NullPointerException <b>propertyName</b>が<b>null</b>の場合	 *
	 * @throws IllegalArgumentException プロパティ名に関連するカラム情報が見つからない場合
	 */
	public ColumnInfo getColumnInfo(String propertyName) {
		return null;
	}

	/**
	 * カラム情報のリストを返します。
	 *
	 * @return カラム情報のリスト
	 */
	public List<ColumnInfo> columnInfos() {
		return null;
	}

	/**
	 * キーを構成するカラム情報のリストを返します。
	 *
	 * @return キーを構成するカラム情報のリスト
	 */
	public List<ColumnInfo> keyColumnInfos() {
		return null;
	}
}
