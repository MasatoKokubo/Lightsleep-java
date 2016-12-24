/*
	AbstractConnectionSupplier.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import java.sql.Connection;
import java.util.Properties;
import java.util.function.Consumer;

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
		lightsleep.properties ファイルの設定値を接続情報として使用します。

		@param modifier properties を変更するコンシューマー

		@since 1.5.0
	*/
	public AbstractConnectionSupplier(Consumer<Properties> modifier) {
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
