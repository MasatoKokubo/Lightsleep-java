/*
	Resource.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.helper;

import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;

/**
	リソースファイルを扱います。

	@since 1.0.0
	@author Masato Kokubo
*/
public class Resource {
	/**
		<b>Resource</b> を構築します。

		@param baseName 基底名
	*/
	public Resource(String baseName) {
	}

	/**
		<b>clazz</b> のクラス名を基底名として <b>Resource</b> を構築します。

		@param clazz クラス名
	*/
	public Resource(Class<?> clazz) {
	}

	/**
		指定のキーに関連する文字列を返します。

		@param key キー

		@return 文字列

		@throws NullPointerException <b>key</b> が <b>null</b> の場合
		@throws MissingResourceException リソースファイルが見つからない場合か指定のキーの文字列が見つからない場合
	*/
	protected String get(String key) {
		return null;
	}

	/**
		指定のキーに関連する文字列を返します。
		リソースファイルが見つからない場合か指定のキーの文字列が見つからない場合は、<b>defaultValue</b> を返します。

		@param key キー
		@param defaultValue デフォルト値 (<b>null</b> 可)

		@return 文字列リソースまたはデフォルト値

		@throws NullPointerException <b>key</b> が <b>null</b> の場合
	*/
	protected String get(String key, String defaultValue) {
		return null;
	}

	/**
		指定のキーに関連する文字列を指定の型のオブジェクトに変換して返します。<br>
		型は配列型を指定する事もできます。<br>
		例:<br>
		<code>
			values.length = 4 (or values.size = 4)<br>
			values.0 = A<br>
			values.1 = B<br>
			values.2 = C<br>
			values.3 = D<br>
		</code>

		@param <T> オブジェクトの型

		@param key キー
		@param objectType 変換する型 (プリミティブ型以外)

		@return リソースファイルから取得したプロパティ値

		@throws NullPointerException <b>key</b> または <b>objectType</b> が <b>null</b> の場合
		@throws MissingResourceException リソースファイルが見つからない場合か指定のキーの値が見つからない場合
		@throws ConvertException 指定の型のオブジェクトに変換できない場合
	*/
	public <T> T get(String key, Class<T> objectType) {
		return null;
	}

	/**
		指定のキーに関連する文字列を指定の型のオブジェクトに変換して返します。<br>
		リソースファイルに値が見つからない
		(または指定のオブジェクト型のオブジェクトに変換できない) 場合は、<b>defaultObject</b> を返します。

		@param <T> オブジェクトの型

		@param key キー
		@param objectType 取得するオブジェクトの型 (プリミティブ型以外)
		@param defaultObject デフォルトオブジェクト (<b>null</b> 可)

		@return リソースファイルから取得したプロパティ値またはデフォルトオブジェクト

		@throws NullPointerException <b>key</b> または <b>objectType</b> が <b>null</b> の場合
	*/
	public <T> T get(String key, Class<T> objectType, T defaultObject) {
		return null;
	}

	/**
		指定のキーで指定のオブジェクトをマップに登録します。

		@param <T> オブジェクトの型

		@param key キー
		@param objectType 登録するオブジェクトの型
		@param object 登録するオブジェクト

		@return 登録する以前のオブジェクト (なければ <b>null</b>)

		@throws NullPointerException <b>key</b> または <b>objectType</b> が <b>null</b> の場合
	*/
	public synchronized <T> T put(String key, Class<T> objectType, T object) {
		return null;
	}

	/**
		指定のキーのオブジェクトをマップから削除します。

		@param <T> オブジェクトの型

		@param key キー
		@param objectType 削除するオブジェクトの型

		@return 削除したオブジェクト (なければ  <b>null</b>)

		@throws NullPointerException <b>key</b> または <b>objectType</b> が <b>null</b> の場合
	*/
	public synchronized <T> T remove(String key, Class<T> objectType) {
		return null;
	}


	/**
		自身を <b>Properties</b> として返します。

		@since 1.1.0

		@return Properties オブジェクト
	*/
	public Properties getProperties() {
		return null;
	}

	/**
		指定されたベースキーの <b>Properties</b> を返します。
		例)<br>
		<br>

		プロパティファイルで以下が定義されていた場合に<br>
		<div class="sampleCode">
			baseKey.key1 = vakue1<br>
			baseKey.key2 = vakue2<br>
			baseKey.key3 = value3<br>
		</div>
		<br>

		<b>getProperties("baseKey")</b> で返される <b>Properties</b> オブジェクトは以下になります。<br>

		<div class="sampleCode">
			{"key1":"value1", "key2":"value2", "key3":"value3"}
		</div>

		@param baseKey ベースキー

		@return Properties オブジェクト

		@throws NullPointerException <b>baseKey</b> が <b>null</b> の場合
	*/
	public Properties getProperties(String baseKey) {
		return null;
	}
}
