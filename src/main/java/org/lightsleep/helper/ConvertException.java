// ConvertException.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

import java.text.MessageFormat;

import org.lightsleep.helper.Resource;
import org.lightsleep.helper.Utils;

/**
 * If it fails to data conversion in the method of TypeConverter class,
 * this exception will be thrown.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
@SuppressWarnings("serial")
public class ConvertException extends RuntimeException {
	// Class resources
	private static final Resource resource = new Resource(ConvertException.class);
// 1.9.0
//	private static final String messageCantConvert     = resource.get("messageCantConvert");
//	private static final String messageCantConvert_e   = resource.get("messageCantConvert_e");
//	private static final String messageCantConvert_d   = resource.get("messageCantConvert_d");
//	private static final String messageCantConvert_d_e = resource.get("messageCantConvert_d_e");
	private static final String messageCantConvert     = resource.getString("messageCantConvert");
	private static final String messageCantConvert_e   = resource.getString("messageCantConvert_e");
	private static final String messageCantConvert_d   = resource.getString("messageCantConvert_d");
	private static final String messageCantConvert_d_e = resource.getString("messageCantConvert_d_e");
////

	// The source data type
	private Class<?> sourceType;

	// The destination data type
	private Class<?> destinType;

	// The source object
	private Object source;

	// The destination object
	private Object destin;

	/**
	 * Constructs a new <b>ConvertException</b>.
	 */
	public ConvertException() {
	}

	/**
	 * Constructs a new <b>ConvertException</b>.
	 *
	 * @param message the detail message
	 */
	public ConvertException(String message) {
		super(message);
	}

	/**
	 * Constructs a new <b>ConvertException</b>.
	 *
	 * @param message the detail message
	 * @param cause the cause
	 */
	public ConvertException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new <b>ConvertException</b>.
	 *
	 * @param cause the cause
	 */
	public ConvertException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new <b>ConvertException</b>.
	 *
	 * @param sourceType the source data type
	 * @param source the source object (permit null)
	 * @param destinType the destination data type
	 */
	public ConvertException(Class<?> sourceType, Object source, Class<?> destinType) {
		this(sourceType, source, destinType, null);
	}

	/**
	 * Constructs a new <b>ConvertException</b>.
	 *
	 * @param sourceType the source data type
	 * @param source the source object (permit null)
	 * @param destinType the destination data type
	 * @param destin the destination object (permit null)
	 */
	public ConvertException(Class<?> sourceType, Object source, Class<?> destinType, Object destin) {
		super(toString(sourceType, source, destinType, destin, null));
		this.sourceType = sourceType;
		this.source     = source;
		this.destinType = destinType;
		this.destin     = destin;
	}

	/**
	 * Constructs a new <b>ConvertException</b>.
	 *
	 * @param sourceType the source data type
	 * @param source the source object (permit null)
	 * @param destinType the destination data type
	 * @param destin the destination object (permit null)
	 * @param cause the cause
	 */
	public ConvertException(Class<?> sourceType, Object source, Class<?> destinType, Object destin, Throwable cause) {
		super(toString(sourceType, source, destinType, destin, cause), cause);
		this.sourceType = sourceType;
		this.source     = source;
		this.destinType = destinType;
		this.destin     = destin;
	}

	/**
	 * Returns a string representation of the exception contents.
	 *
	 * @param sourceType the source data type
	 * @param source the source object (permit null)
	 * @param destinType the destination data type
	 * @param destin the destination object (permit null)
	 * @param cause the cause
	 */
	private static String toString(Class<?> sourceType, Object source, Class<?> destinType, Object destin, Throwable cause) {
		String sourceTypeName = sourceType.getCanonicalName();
		String destinTypeName = destinType.getCanonicalName();
		String sourceString = Utils.toLogString(source);

		return 
			destin == null
			? cause == null
				? MessageFormat.format(messageCantConvert,
					sourceTypeName, sourceString, destinTypeName)

				: MessageFormat.format(messageCantConvert_e,
					sourceTypeName, sourceString, destinTypeName, cause)

			: cause == null
				? MessageFormat.format(messageCantConvert_d,
					sourceTypeName, sourceString, destinTypeName, Utils.toLogString(destin))

				: MessageFormat.format(messageCantConvert_d_e,
					sourceTypeName, sourceString, destinTypeName, Utils.toLogString(destin), cause)
			;
	}

	/**
	 * Returns the source data type.
	 *
	 * @return the source data type
	 */
	public Class<?> sourceType() {
		return sourceType;
	}

	/**
	 * Returns the destination data type.
	 *
	 * @return the destination data type
	 */
	public Class<?> destinType() {
		return destinType;
	}

	/**
	 * Returns the source object.
	 *
	 * @return the source object
	 */
	public Object sourceObject() {
		return source;
	}

	/**
	 * Returns the destination object.
	 *
	 * @return the destination object
	 */
	public Object destinObject() {
		return destin;
	}
}
