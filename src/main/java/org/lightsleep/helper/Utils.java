/*
	Utils.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.helper;

import java.math.BigInteger;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
	There are utility methods.

	@since 1.0
	@author Masato Kokubo
*/
public class Utils {
	// Class resources
	private static final int maxLogStringLength    = Resource.globalResource.get(Integer.class, "maxLogStringLength"   , 200);
	private static final int maxLogByteArrayLength = Resource.globalResource.get(Integer.class, "maxLogByteArrayLength", 200);
	private static final int maxLogArrayLength     = Resource.globalResource.get(Integer.class, "maxLogArrayLength"    , 100);
	private static final int maxLogMapSize         = Resource.globalResource.get(Integer.class, "maxLogMapSize"        , 100);

	// A map to convert the primitive type to class type
	private static final Map<Class<?>, Class<?>> toClassMap = new LinkedHashMap<>();

	// A map to convert the class type to primitive type
	private static final Map<Class<?>, Class<?>> toPrimitiveMap = new LinkedHashMap<>();

	static {
		toClassMap.put(boolean.class, Boolean  .class);
		toClassMap.put(char   .class, Character.class);
		toClassMap.put(byte   .class, Byte     .class);
		toClassMap.put(short  .class, Short    .class);
		toClassMap.put(int    .class, Integer  .class);
		toClassMap.put(long   .class, Long     .class);
		toClassMap.put(float  .class, Float    .class);
		toClassMap.put(double .class, Double   .class);

		toPrimitiveMap.put(Boolean  .class, boolean.class);
		toPrimitiveMap.put(Character.class, char   .class);
		toPrimitiveMap.put(Byte     .class, byte   .class);
		toPrimitiveMap.put(Short    .class, short  .class);
		toPrimitiveMap.put(Integer  .class, int    .class);
		toPrimitiveMap.put(Long     .class, long   .class);
		toPrimitiveMap.put(Float    .class, float  .class);
		toPrimitiveMap.put(Double   .class, double .class);
	}

	/**
		Converts the primitive type to the corresponding class type.

		@param type the type (permit <b>null</b>)

		@return the corresponding class type if <b>type</b> is a primitive type, <b>type</b> otherwise (in the case of <b>type</b> == <b>null</b>)
	*/
	public static Class<?> toClassType(Class<?> type) {
		return toClassMap.getOrDefault(type, type);
	}

	/**
		Converts the class type to the corresponding primitive type.

		@param type the type (permit <b>null</b>)

		@return the corresponding primitive type if <b>type</b> is a class type, <b>type</b> otherwise (in the case of <b>type</b> == <b>null</b>)
	*/
	public static Class<?> toPrimitiveType(Class<?> type) {
		return toPrimitiveMap.getOrDefault(type, type);
	}

	// the set of the value classes
	private static final Set<Class<?>> valueClasses = new HashSet<>();
	static {
		valueClasses.add(boolean          .class);
		valueClasses.add(char             .class);
		valueClasses.add(byte             .class);
		valueClasses.add(short            .class);
		valueClasses.add(int              .class);
		valueClasses.add(long             .class);
		valueClasses.add(float            .class);
		valueClasses.add(double           .class);
		valueClasses.add(Boolean          .class);
		valueClasses.add(Character        .class);
		valueClasses.add(Byte             .class);
		valueClasses.add(Short            .class);
		valueClasses.add(Integer          .class);
		valueClasses.add(Long             .class);
		valueClasses.add(Float            .class);
		valueClasses.add(Double           .class);
		valueClasses.add(BigInteger       .class);
		valueClasses.add(BigDecimal       .class);
		valueClasses.add(String           .class);
		valueClasses.add(java.util.Date   .class);
		valueClasses.add(Date             .class);
		valueClasses.add(Time             .class);
		valueClasses.add(Timestamp        .class);
	}

	/**
		Returns whether <b>type</b> is value type.<br>
		Value type is one of the following.<br>

		<div class="blankline">&nbsp;</div>

		<div class="code indent">
			boolean, char, byte, short, int, long, float, double,<br>
			Boolean, Character, Byte, Short, Integer, Long, Float, Double, BigInteger, BigDecimal,<br>
			String, java.util.Date, java.sql.Date, Time, Timestamp
		</div>

		@param type the type (permit <b>null</b>)

		@return <b>type</b> if <b>type</b> is value type, <b>false</b> otherwise

		@throws NullPointerException if <b>type</b> is <b>null</b>
	*/
	public static boolean isValueType(Class<?> type) {
		return valueClasses.contains(type);
	}

	/**
		Returns a class name without the package name.

		@param clazz the class

		@return a class name without the package name

		@throws NullPointerException if <b>clazz</b> is <b>null</b>
	*/
	public static String nameWithoutPackage(Class<?> clazz) {
		String className = null;
		if (clazz.isArray()) {
			className = nameWithoutPackage(clazz.getComponentType()) + "[]";
		} else {
			className = clazz.getName();

			// 最後の . より右
			className = className.substring(className.lastIndexOf('.') + 1);
		}

		return className;
	}

	/**
		Returns a new array of <b>elementType</b>.<br>

		Store a new object of <b>elementType</b> for all of the elements of the array.

		@param <E> the element type of the array

		@param elementType the element type of the array
		@param length the length of the array

		@return elementType a new array of <b>elementType</b>

		@throws NullPointerException if <b>elementType</b> is <b>null</b>
		@throws IndexOutOfBoundsException if <b>length </b>&lt; 0
		@throws RuntimeException if <b>InstantiationException</b> or <b>IllegalAccessException</b> has been thrown
	*/
	@SuppressWarnings("unchecked")
	public static <E> E[] newArray(Class<E> elementType, int length) {
		E[] array = (E[])Array.newInstance(elementType, length);
		try {
			for (int index = 0; index < length; ++index)
			// 1.3.0 for Java 9
			//	array[index] = elementType.newInstance();
				array[index] = elementType.getConstructor().newInstance();
			////
		}
	// 1.3.0 for Java 9
	//	catch (InstantiationException | IllegalAccessException e) {
	//		throw new RuntimeException(e);
	//	}
		catch (RuntimeException e) {throw e;}
		catch (Exception e) {throw new RuntimeException(e);}
	////

		return array;
	}

	/**
		Returns a string representation for the log output of the specified value.

		@param value the value

		@return a string representation for the log output
	*/
	public static String toLogString(boolean value) {
		return toLogString(Boolean.valueOf(value), Boolean.TYPE);
	}

	/**
		Returns a string representation for the log output of the specified value.

		@param value the value

		@return a string representation for the log output
	*/
	public static String toLogString(char value) {
		return toLogString(Character.valueOf(value), Character.TYPE);
	}

	/**
		Returns a string representation for the log output of the specified value.

		@param value the value

		@return a string representation for the log output
	*/
	public static String toLogString(byte value) {
		return toLogString(Byte.valueOf(value), Byte.TYPE);
	}

	/**
		Returns a string representation for the log output of the specified value.

		@param value the value

		@return a string representation for the log output
	*/
	public static String toLogString(short value) {
		return toLogString(Short.valueOf(value), Short.TYPE);
	}

	/**
		Returns a string representation for the log output of the specified value.

		@param value the value

		@return a string representation for the log output
	*/
	public static String toLogString(int value) {
		return toLogString(Integer.valueOf(value), Integer.TYPE);
	}

	/**
		Returns a string representation for the log output of the specified value.

		@param value the value

		@return a string representation for the log output
	*/
	public static String toLogString(long value) {
		return toLogString(Long.valueOf(value), Long.TYPE);
	}

	/**
		Returns a string representation for the log output of the specified value.

		@param value the value

		@return a string representation for the log output
	*/
	public static String toLogString(float value) {
		return toLogString(Float.valueOf(value), Float.TYPE);
	}

	/**
		Returns a string representation for the log output of the specified value.

		@param value the value

		@return a string representation for the log output
	*/
	public static String toLogString(double value) {
		return toLogString(Double.valueOf(value), Double.TYPE);
	}

	/**
		Returns a string representation for the log output of the specified value.

		@param value the value (permit <b>null</b>)

		@return a string representation for the log output
	*/
	public static String toLogString(Object value) {
		return toLogString(value, null);
	}

	/**
		Returns a string representation for the log output of the specified value.

		@param object the object (permit <b>null</b>)
		@param type the type of the object (permit <b>null</b>)

		@return  log 出力用の文字列表現
	*/
	@SuppressWarnings("rawtypes")
	private static String toLogString(Object object, Class<?> type) {
		StringBuilder buff = new StringBuilder();

		if (object == null) {
			// null
			buff.append(object);
		} else {
			if (type == null)
				type = object.getClass();

			// object type
			if (type.isArray()) {
				// Array
				append(buff.append('('), type, object).append(')');
				if (type == char[].class)
					// String Array
					append(buff, ((char[])object));
				else if (type == byte[].class)
					// Byte Array
					append(buff, ((byte[])object));
				else
					// etc. Array
					appendArray(buff, object);

			} else if (object instanceof Boolean) {
				// Boolean
				if (type != Boolean.TYPE)
					append(buff.append('('), type, object).append(')');
				buff.append(object);

			} else if (object instanceof Character) {
				// Character
				if (type != Character.TYPE)
					append(buff.append('('), type, object).append(')');
				buff.append('\'');
				append(buff, ((Character)object).charValue());
				buff.append('\'');

			} else if (object instanceof BigDecimal) {
				// BigDecimal
				if (type != Integer.TYPE)
					append(buff.append('('), type, object).append(')');
				buff.append(((BigDecimal)object).toPlainString());

			} else if (object instanceof Number) {
				// Number
				if (type != Integer.TYPE)
					append(buff.append('('), type, object).append(')');
				buff.append(object);

			} else if (object instanceof java.util.Date) {
				// java.util.Date
				if (type == java.util.Date.class)
					append(buff.append('('), type, object).append(')')
						.append(new Timestamp(((java.util.Date)object).getTime()));
				else
					buff.append(object);

			} else if (object instanceof String) {
				// String
				append(buff, (String)object);

			} else if (object instanceof Iterable) {
				// Iterable
				append(buff.append('('), type, object).append(')');
				append(buff, (Iterable)object);

			} else if (object instanceof Map) {
				// Map
				append(buff.append('('), type, object).append(')');
				append(buff, (Map<?,?>)object);

			} else {
				// etc.
				buff.append(object);
			}
		}

		return buff.toString();
	}

	/**
		Appends a string representation of <b>value</b> to the string buffer.

		@param buff the string buffer
		@param type the type of the value
		@param value a value to be appended (permit <b>null</b>)

		@return the string buffer

		@throws NullPointerException if type is <b>null</b>
	*/
	@SuppressWarnings("rawtypes")
	private static StringBuilder append(StringBuilder buff, Class<?> type, Object value) {
		if (type == null)
			throw new NullPointerException("Utils.append(StringBuilder, Class, Object): type == null");

		long length = -1L;
		int  size   = -1;

		if (type.isArray()) {
			// Array
			append(buff, type.getComponentType(), null).append("[]");
			if (value != null)
				length = Array.getLength(value);
		} else {
			// Non Array
			String typeName = Utils.nameWithoutPackage(type);
			if (typeName.equals("Date")) type.getName();

			if (value != null) {
				if      (value instanceof String    ) length = ((String    )value).length();
				else if (value instanceof Collection) size   = ((Collection)value).size  ();
				else if (value instanceof Map       ) size   = ((Map       )value).size  ();
			}
			buff.append(typeName);
		}

		if (length != -1L)
			buff.append(" length=").append(length);
		if (size != -1)
			buff.append(" size=").append(size);

		return buff;
	}

	/**
		Appends a string representation of a character to the string buffer.

		@param buff the string buffer
		@param ch a character

		@return the string buffer
	*/
	private static StringBuilder append(StringBuilder buff, char ch) {
		if (ch >= ' ' && ch != '\u007F') {
			if      (ch == '"' ) buff.append("\\\"");
			else if (ch == '\'') buff.append("\\'" );
			else if (ch == '/' ) buff.append("\\/" );
			else if (ch == '\\') buff.append("\\\\");
			else buff.append(ch);
		} else {
			if      (ch == '\b') buff.append("\\b" ); // 07 BEL
			else if (ch == '\t') buff.append("\\t" ); // 09 HT
			else if (ch == '\n') buff.append("\\n" ); // 0A LF
			else if (ch == '\f') buff.append("\\f" ); // 0C FF
			else if (ch == '\r') buff.append("\\r" ); // 0D CR
			else {
				String hexString = "000" + Integer.toHexString((int)ch);
				buff.append("\\u").append(hexString.substring(hexString.length() - 4));
			}
		}
		return buff;
	}

	/**
		Appends a string representation of a string to the string buffer.

		@param buff the string buffer
		@param string 文字列

		@return the string buffer
	*/
	private static StringBuilder append(StringBuilder buff, String string) {
		buff.append('"');
		for (int index = 0; index < string.length(); ++index) {
			if (index >= maxLogStringLength) {
				buff.append("...");
				break;
			}
			append(buff, string.charAt(index));
		}
		buff.append('"');

		return buff;
	}

	/**
		Appends a string representation of characters to the string buffer.

		@param buff the string buffer
		@param chars an array of characters

		@return the string buffer
	*/
	private static StringBuilder append(StringBuilder buff, char[] chars) {
		buff.append('"');
		for (int index = 0; index < chars.length; ++index) {
			if (index >= maxLogStringLength) {
				buff.append("...");
				break;
			}
			append(buff, chars[index]);
		}
		buff.append('"');

		return buff;
	}

	/**
		Appends a string representation of bytes to the string buffer.

		@param buff the string buffer
		@param bytes an array of bytes

		@return the string buffer
	*/
	private static StringBuilder append(StringBuilder buff, byte[] bytes) {
		buff.append('[');
		String delimiter = "";
		for (int index = 0; index < bytes.length; ++index) {
			buff.append(delimiter);
			if (index >= maxLogByteArrayLength) {
				buff.append("...");
				break;
			}
			int value = bytes[index];
			if (value < 0) value += 256;
			char ch = (char)(value / 16 + '0');
			if (ch > '9') ch += 'A' - '9' - 1;
			buff.append(ch);
			ch = (char)(value % 16 + '0');
			if (ch > '9') ch += 'A' - '9' - 1;
			buff.append(ch);
			delimiter = ", ";
		}
		buff.append(']');

		return buff;
	}

	/**
		Appends a string representation of an array to the string buffer.

		@param buff the string buffer
		@param array an array

		@return the string buffer
	*/
	private static StringBuilder appendArray(StringBuilder buff, Object array) {
		Class<?> componentType = array.getClass().getComponentType();
		if (!componentType.isPrimitive())
			componentType = null;

		int length = Array.getLength(array);

		buff.append('[');
		String delimiter = "";
		for (int index = 0; index < length; ++index) {
			buff.append(delimiter);
			if (index >= maxLogArrayLength) {
				buff.append("...");
				break;
			}
			buff.append(toLogString(Array.get(array, index), componentType));
			delimiter = ", ";
		}
		buff.append(']');

		return buff;
	}

	/**
		Appends a string representation of an iterable to the string buffer.

		@param buff the string buffer
		@param iterable an <b>Iterable</b>

		@return the string buffer
	*/
	private static StringBuilder append(StringBuilder buff, Iterable<?> iterable) {
		Iterator<?> iter = iterable.iterator();
		buff.append('[');
		String delimiter = "";
		for (int index = 0; iter.hasNext(); ++index) {
			buff.append(delimiter);
			if (index >= maxLogArrayLength) {
				buff.append("...");
				break;
			}
			buff.append(toLogString(iter.next(), null));
			delimiter = ", ";
		}
		buff.append(']');

		return buff;
	}

	/**
		Appends a string representation of a map to the string buffer.

		@param buff the string buffer
		@param map a <b>Map</b>

		@return the string buffer
	*/
	private static <K,V> StringBuilder append(StringBuilder buff, Map<K,V> map) {
		Iterator<Map.Entry<K,V>> iter = map.entrySet().iterator();
		buff.append('[');
		String delimiter = "";
		for (int index = 0; iter.hasNext(); ++index) {
			if (index >= maxLogMapSize) {
				buff.append("...");
				break;
			}
			buff.append(delimiter);
			Map.Entry<K,V> entry = iter.next();
			buff.append(toLogString(entry.getKey())).append(':').append(toLogString(entry.getValue()));
			delimiter = ", ";
		}
		buff.append(']');

		return buff;
	}
}
