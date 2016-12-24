/*
	ManyRowsException.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep;

/**
	この例外は、単一行を取得するメソッドにおいて複数行が検索された場合にスローされます。

	@since 1.0

	@author Masato Kokubo
*/
@SuppressWarnings("serial")
public class ManyRowsException extends RuntimeException {
	/**
		<b>ManyRowsException</b> を構築します。
	*/
	public ManyRowsException() {
	}

	/**
		<b>ManyRowsException</b> を構築します。

		@param message 詳細メッセージ

		@since 1.5.0
	*/
	public ManyRowsException(String message) {
	}
}
