/*
	SqlString.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.component;

/**
	This class will be uses to convert the value into a SQL string.

	@since 1.0.0
	@author Masato Kokubo
*/
public class SqlString {
	/** The paramter string of SQL */
	public static final SqlString PARAMETER = new SqlString("?");

	// The string content
	private final String content;

	/**
		Constructs a new <b>SqlString</b>.

		@param content the content of the <b>SqlString</b>
	*/
	public SqlString(String content) {
		this.content = content;
	}

	/**
		Returns the content.

		@return the content
	*/
	public String content() {
		return content;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public String toString() {
		return content == null ? "NULL" : content;
	}
}
