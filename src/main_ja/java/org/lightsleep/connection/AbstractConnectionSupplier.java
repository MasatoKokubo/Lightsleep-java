/*
	AbstractConnectionSupplier.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.lightsleep.helper.Resource;
import org.lightsleep.logger.Logger;

/**
	The abstract connection supplier

	@since 1.1.0
	@author Masato Kokubo
*/
public abstract class AbstractConnectionSupplier implements ConnectionSupplier {
	/** ロガー */
	protected static final Logger logger = null;

	/** プロパティ */
	protected Properties properties = null;

	/**
		<b>AbstractConnectionSupplier</b> を構築します。
		lightsleep.properties ファイルの設定値を接続情報として使用します。
	*/
	public AbstractConnectionSupplier() {
	}

	/**
		<b>AbstractConnectionSupplier</b> を構築します。
		lightsleep.properties および <i>&lt;<b>resourceName</b>&gt;</i>.properties ファイル
		の設定値を接続情報として使用します。

		@param resourceName 追加のリソース名
	*/
	public AbstractConnectionSupplier(String resourceName) {
	}

	/**
		データソースを返します。

		@return データソース
	*/
	protected abstract DataSource getDataSource();

	/**
		データベース・コネクションを返します。

		@return データベース・コネクション
	*/
	@Override
	public Connection get() {
		return null;
	}
}
