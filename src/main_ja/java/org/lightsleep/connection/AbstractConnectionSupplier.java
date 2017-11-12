// AbstractConnectionSupplier.java
// (C) 2016 Masato Kokubo

package org.lightsleep.connection;

import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;


import org.lightsleep.database.Database;
import org.lightsleep.logger.Logger;

/**
 * 各種のコネクション･ラッパーのサプライヤ･クラスの共通部分を実装する抽象クラスです。
 *
 * @since 1.1.0
 * @author Masato Kokubo
 */
public abstract class AbstractConnectionSupplier implements ConnectionSupplier {
	/** ロガー */
	protected static final Logger logger = null;

	/** キー: url 文字列、値: ConnectionSupplier のマップ */
	protected static final Map<String, ConnectionSupplier> supplierMap = null;

	/** プロパティ */
	protected Properties properties = null;

// 2.1.0
//	/**
//	 * <b>AbstractConnectionSupplier</b>を構築します。
//	 * lightsleep.propertiesファイルの設定値を接続情報として使用します。
//	 */
//	public AbstractConnectionSupplier() {
//	}
//
//	/**
//	 * <b>AbstractConnectionSupplier</b>を構築します。
//	 * lightsleep.propertiesファイルの設定値を接続情報として使用します。
//	 *
//	 * @param modifier propertiesを変更するコンシューマー
//	 *
//	 * @since 1.5.0
//	 */
//	public AbstractConnectionSupplier(Consumer<Properties> modifier) {
//	}
////

	/**
	 * <b>AbstractConnectionSupplier</b>を構築します。
	 *
	 * @param properties コネクション情報を含むプロパティ
	 * @param modifier プロパティを修正するためのコンシューマー
	 *
	 * @since 2.1.0
	 */
	protected AbstractConnectionSupplier(Properties properties, Consumer<Properties> modifier) {
	}

// 2.1.0
//	/**
//	 * データソースを返します。
//	 *
//	 * @return データソース
//	 */
//	protected abstract DataSource getDataSource();
////

	/**
	 * コネクション･ラッパーを返します。
	 *
	 * @return コネクション･ラッパー
	 */
	@Override
	public ConnectionWrapper get() {
		return null;
	}


	/**
	 * {@inheritDoc}
	 *
	 * @since 2.1.0
	 */
	@Override
	public Database getDatabase() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 2.1.0
	 */
	@Override
	public String getUrl() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 2.1.0
	 */
	@Override
	public String toString() {
		return null;
	}
}
