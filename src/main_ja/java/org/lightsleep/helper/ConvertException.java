// ConvertException.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

/**
 * <b>TypeConverter</b> クラスのメソッドでデータ変換に失敗した場合にこの例外がスローされます。
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
@SuppressWarnings("serial")
public class ConvertException extends RuntimeException {
	/**
	 * 新規 <b>ConvertException</b> を構築します。
	 */
	public ConvertException() {
	}

	/**
	 * 指定の詳細メッセージで新規 <b>ConvertException</b> を構築します。
	 *
	 * @param message 詳細メッセージ
	 */
	public ConvertException(String message) {
	}

	/**
	 * 指定の詳細メッセージと原因で新規 <b>ConvertException</b> を構築します。
	 *
	 * @param message 詳細メッセージ
	 * @param cause 原因 (null 可)
	 */
	public ConvertException(String message, Throwable cause) {
	}

	/**
	 * 指定の原因で新規 <b>ConvertException</b> を構築します。
	 *
	 * @param cause 原因 (null 可)
	 */
	public ConvertException(Throwable cause) {
	}

	/**
	 * 新規 <b>ConvertException</b> を構築します。
	 *
	 * @param sourceType 変換元型
	 * @param source 変換元オブジェクト (null 可)
	 * @param destinType 変換先型
	 */
	public ConvertException(Class<?> sourceType, Object source, Class<?> destinType) {
	}

	/**
	 * 新規 <b>ConvertException</b> を構築します。
	 *
	 * @param sourceType 変換元型
	 * @param source 変換元オブジェクト (null 可)
	 * @param destinType 変換先型
	 * @param destin 変換先オブジェクト (null 可)
	 */
	public ConvertException(Class<?> sourceType, Object source, Class<?> destinType, Object destin) {
	}

	/**
	 * 新規 <b>ConvertException</b> を構築します。
	 *
	 * @param sourceType 変換元型
	 * @param source 変換元オブジェクト (null 可)
	 * @param destinType 変換先型
	 * @param destin 変換先オブジェクト (null 可)
	 * @param cause 原因 (null 可)
	 */
	public ConvertException(Class<?> sourceType, Object source, Class<?> destinType, Object destin, Throwable cause) {
	}

	/**
	 * 変換元データ型を返します。
	 *
	 * @return 変換元データ型
	 */
	public Class<?> sourceType() {
		return null;
	}

	/**
	 * 変換先データ型を返します。
	 *
	 * @return 変換先データ型
	 */
	public Class<?> destinType() {
		return null;
	}

	/**
	 * 変換元オブジェクトを返します。
	 *
	 * @return 変換元オブジェクト (null 有)
	 */
	public Object sourceObject() {
		return null;
	}

	/**
	 * 変換先オブジェクトを返します。
	 *
	 * @return 変換先オブジェクト (null 有)
	 */
	public Object destinObject() {
		return null;
	}
}
