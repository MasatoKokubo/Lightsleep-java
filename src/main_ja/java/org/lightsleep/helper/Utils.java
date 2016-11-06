/*
	Utils.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.helper;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
	ユーティリティメソッドがあります。

	@since 1.0

	@author Masato Kokubo
*/
public class Utils {
	/**
		プリミティブ型をクラス型に変換します。

		@param type 型 (<b>null</b> 可)

		@return type がプリミティブ型なら対応するクラス型、そうでなければ <b>type</b> (<b>type</b> == <b>null</b> の場合も)
	*/
	public static Class<?> toClassType(Class<?> type) {
		return null;
	}

	/**
		クラス型をプリミティブ型に変換します。

		@param type 型 (<b>null</b> 可)

		@return type に対応するプリミティブ型があればそのプリミティブ型、なければ <b>nul</b>l
	*/
	public static Class<?> toPrimitiveType(Class<?> type) {
		return null;
	}

	/**
		<b>type</b> が値型かどうかを返します。<br>
		値型は以下のいずれかです。<br>

		<div class="blankline">&nbsp;</div>

		<div class="code indent">
			boolean, char, byte, short, int, long, float, double,<br>
			Boolean, Character, Byte, Short, Integer, Long, Float, Double, BigInteger, BigDecimal,<br>
			String, java.util.Date, java.sql.Date, Time, Timestamp
		</div>

		@param type 型 (<b>null</b> 可)

		@return <b>type</b> が値型なら <b>true</b>、そうでなければ <b>false</b>

		@throws NullPointerException <b>type</b> が <b>null</b> の場合
	*/
	public static boolean isValueType(Class<?> type) {
		return false;
	}

	/**
		パッケージなしのクラス名を返します。

		@param clazz クラス

		@return パッケージなしのクラス

		@throws NullPointerException <b>clazz</b> が <b>null</b> の場合
	*/
	public static String nameWithoutPackage(Class<?> clazz) {
		return null;
	}

	/**
		配列オブジェクトを作成します。
		配列のすべての要素に指定の要素型のオブジェクトを作成して格納します。

		@param <E> 配列の要素型

		@param elementType 配列の要素型クラス
		@param length 配列のサイズ

		@return elementType 型の配列

		@throws NullPointerException <b>elementType</b> が <b>null</b> の場合
		@throws IndexOutOfBoundsException <b>length </b>&lt; 0 の場合
		@throws RuntimeException インスタンスの生成が失敗した場合 (<b>InstantiationException)</b> かコンストラクタにアクセスできない場合 (<b>IllegalAccessException</b>)
	*/
	public static <E> E[] newArray(Class<E> elementType, int length) {
		return null;
	}

	/**
		指定の値のログ出力用の文字列表現を返します。

		@param value 値

		@return ログ出力用の文字列表現
	*/
	public static String toLogString(boolean value) {
		return null;
	}

	/**
		指定の値のログ出力用の文字列表現を返します。

		@param value 値

		@return ログ出力用の文字列表現
	*/
	public static String toLogString(char value) {
		return null;
	}

	/**
		指定の値のログ出力用の文字列表現を返します。

		@param value 値

		@return ログ出力用の文字列表現
	*/
	public static String toLogString(byte value) {
		return null;
	}

	/**
		指定の値のログ出力用の文字列表現を返します。

		@param value 値

		@return ログ出力用の文字列表現
	*/
	public static String toLogString(short value) {
		return null;
	}

	/**
		指定の値のログ出力用の文字列表現を返します。

		@param value 値

		@return ログ出力用の文字列表現
	*/
	public static String toLogString(int value) {
		return null;
	}

	/**
		指定の値のログ出力用の文字列表現を返します。

		@param value 値

		@return ログ出力用の文字列表現
	*/
	public static String toLogString(long value) {
		return null;
	}

	/**
		指定の値のログ出力用の文字列表現を返します。

		@param value 値

		@return ログ出力用の文字列表現
	*/
	public static String toLogString(float value) {
		return null;
	}

	/**
		指定の値のログ出力用の文字列表現を返します。

		@param value 値

		@return ログ出力用の文字列表現
	*/
	public static String toLogString(double value) {
		return null;
	}

	/**
		指定のオブジェクトのグ出力用の文字列表現を返します。

		@param value 値 (<b>null</b> 可)

		@return ログ出力用の文字列表現
	*/
	public static String toLogString(Object value) {
		return null;
	}
}
