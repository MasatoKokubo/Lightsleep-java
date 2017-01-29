// SqlString.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

/**
 * This class will be uses to convert the value into a SQL string.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class SqlString {
	/** The paramter string of SQL */
// 1.7.0
//	public static final SqlString PARAMETER = new SqlString("?");
	public static final String PARAMETER = "?";
////

// 1.7.0
	// Zero parameters
	private static final Object[] ZERO_PARAMETERS = new Object[0];
////

	// The string content
	private final String content;

// 1.7.0
	// The parameters
	private final Object[] parameters;
////

	/**
	 * Constructs a new <b>SqlString</b>.
	 *
	 * @param content the content of the <b>SqlString</b>
	 */
	public SqlString(String content) {
		this.content = content;
	// 1.7.0
		this.parameters = ZERO_PARAMETERS;
	////
	}

	/**
	 * Constructs a new <b>SqlString</b>.
	 *
	 * @param content the content of the <b>SqlString</b>
	 * @param parameters the parameters of the <b>SqlString</b>
	 *
	 * @since 1.7.0
	 */
	public SqlString(String content, Object... parameters) {
		this.content = content;
		this.parameters = parameters;
	}

	/**
	 * Returns the content.
	 *
	 * @return the content
	 */
	public String content() {
		return content;
	}

	/**
	 * Returns the parameters.
	 *
	 * @return the parameters
	 */
	public Object[] parameters() {
		return parameters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return content == null ? "NULL" : content;
	}
}
