// Resource.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.lightsleep.Sql;
import org.lightsleep.logger.LoggerFactory;

/**
 *	Treats the resource file.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class Resource {
// 1.8.6
	// A converter for string values
	private static final Function<String, String> stringConverter = string -> {
			if (string != null) {
				StringBuilder buff = new StringBuilder(string.length());
				boolean escape = false;
				for (int index = 0; index < string.length(); ++index) {
					char ch = string.charAt(index);
					if (escape) {
						if      (ch == 't' ) buff.append('\t'); // 09 HT
						else if (ch == 'n' ) buff.append('\n'); // 0A LF
						else if (ch == 'r' ) buff.append('\r'); // 0D CR
						else if (ch == 's' ) buff.append(' ' ); // 20 SPACE
						else if (ch == '\\') buff.append('\\');
						else                 buff.append(ch);
						escape = false;
					} else {
						if (ch == '\\')
							escape = true;
						else
							buff.append(ch);
					}
				}
				string = buff.toString();
			}
			return string;
		};

	// A ResourceBundle.Control
	private static final ResourceBundle.Control control = new ResourceBundle.Control() {
		@Override
		public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
				throws IllegalAccessException, InstantiationException, IOException {
			String bundleName = toBundleName(baseName, locale);
			String resourceName = toResourceName(bundleName, "properties");

			try (InputStream inStream = loader.getResourceAsStream(resourceName);
				InputStreamReader streamReader = new InputStreamReader(inStream, "UTF-8");
				BufferedReader reader = new BufferedReader(streamReader)) {
				return new PropertyResourceBundle(reader);
			}
		}
	};
////

	/** The global <b>Resource</b> */
// 1.8.6
//	public static final Resource globalResource = new Resource(System.getProperty("lightsleep.resource", "lightsleep"));
	public static final Resource globalResource;
	static {
		Resource resource = new Resource(System.getProperty("lightsleep.resource", "lightsleep"));
		if (resource == null)
			resource = new Resource(Sql.class.getPackage().getName() + ".lightsleep");
		globalResource = resource;
	}
////

	// The base name
	private final String baseName;

	// The resource bundle
	private ResourceBundle resourceBundle;

// 1.8.6
//	// The map of property name and value
//	private final Map<String, Object> objectMap = new LinkedHashMap<String, Object>();
////

	/**
	 * Constructs a new <b>Resource</b>.
	 *
	 * @param baseName the base name
	 */
	public Resource(String baseName) {
		this.baseName = baseName;
		try {
		// 1.8.6
		//	resourceBundle = ResourceBundle.getBundle(baseName);
			resourceBundle = ResourceBundle.getBundle(baseName, control);
		////
		}
		catch (MissingResourceException e) {
		// 1.8.6
			if (globalResource != null)
		////
				LoggerFactory.getLogger(Resource.class).error(e.getMessage());
		}
	}

	/**
	 * Constructs a new <b>Resource</b>.
	 *
	 * @param clazz the class which name is used as base name
	 */
	public Resource(Class<?> clazz) {
		this(clazz.getName());
	}

	/**
	 * Returns a string associated with <b>keypropertyKeyb>.
	 *
	 * @param propertyKey the key of resource property
	 * @return the string value of resource property
	 *
	 * @throws NullPointerException if <b>propertyKey</b> is null
	 * @throws MissingResourceException if the property dose not found
	 */
// 1.8.6
//	public String get(String propertyKey) {
	private String get(String propertyKey) {
////
		Objects.requireNonNull(propertyKey, "propertyKey");

		String string = null;

		MissingResourceException e = null;

		if (resourceBundle != null) {
			// gets from the resource bundle
			try {
				string = resourceBundle.getString(propertyKey).trim();
			}
			catch (MissingResourceException e2) {
				e = e2;
			}
		}

		if (string == null)
			throw e != null ? e : new MissingResourceException("Resource.get", baseName, propertyKey);

		// resolve '{}' references
		boolean inKey = false;
		StringBuilder buff = new StringBuilder();
		StringBuilder keyBuff = new StringBuilder();
		for (int index = 0; index < string.length(); ++index) {
			char ch = string.charAt(index);
			if (!inKey) {
				// not in {}
				if (ch == '{')
					inKey = true;
				else
					buff.append(ch);
			} else {
				// in {}
				if (ch == '}') {
					String refKey = keyBuff.toString().trim();
					// dose not convert '{0}, ...', because it used by parameter reference
					String value = refKey.length() > 0 && (refKey.charAt(0) < '0' || refKey.charAt(0) > '9')
						? getString(refKey, null) : null;
					if (value == null)
						// can not find
						// add '{...}' directly to the buffer
						buff.append('{').append(keyBuff).append('}');
					else
						// found
						buff.append(value);
					inKey = false;
					keyBuff.setLength(0);
				} else {
					keyBuff.append(ch);
				}
			}
		}
		string = buff.toString();

		return string;
	}

// 1.8.6
//	/**
//	 * Returns a string associated with <b>propertyKey</b>.<br>
//	 * If can not find in the resource file, returns <b>defaultValue</b>
//	 *
//	 * @param propertyKey the key
//	 * @param defaultValue the default value (permit null)
//	 * @return the string associated with <b>propertyKey</b>
//	 *
//	 * @throws NullPointerException if <b>propertyKey</b> is null
//	 */
//	public String get(String propertyKey, String defaultValue) {
//		String string = defaultValue;
//
//		try {
//			string = get(propertyKey);
//		}
//		catch (MissingResourceException e) {}
//
//		return string;
//	}
//
//	/**
//	 * Converts a string associated with <b>key</b> to <b>objectType</b> and returns it.<br>
//	 *
//	 * You can also specify an array type as <b>objectType</b>.<br>
//	 * Ex.:<br>
//	 * <code>
//	 *   values.length = 4 (or values.size = 4)<br>
//	 *   values.0 = A<br>
//	 *   values.1 = B<br>
//	 *   values.2 = C<br>
//	 *   values.3 = D<br>
//	 * </code>
//	 *
//	 * @param <T> the object type
//	 * @param key the key
//	 * @param objectType the object type (other than primitive types)
//	 * @return an object converted from a string associated with <b>key</b>
//	 *
//	 * @throws NullPointerException if <b>key</b> or <b>objectType</b> is null
//	 * @throws MissingResourceException if the property dose not found
//	 * @throws ConvertException if can not convert to <b>objectType</b>
//	 */
//	public <T> T get(Class<T> objectType, String key) {
//		Objects.requireNonNull(objectType, "objectType");
//		if (objectType.isPrimitive()) throw new IllegalArgumentException("key: " + key + ", objectType.isPrimitive() = true");
//		Objects.requireNonNull(key, "key");
//
//		// gets from the map
//		T object = objectType.cast(objectMap.get(key));
//
//		if (object == null) {
//			if (objectType.isArray()) {
//				// is Array
//				int length = getLength(key);
//				Class<?> componentType = objectType.getComponentType();
//				if (componentType.isPrimitive()) {
//					// Array of Primitive Type
//					Object array = Array.newInstance(componentType, length);
//					for (int index = 0; index < length; ++index) {
//						String indexedKey = toIndexedKey(key, index);
//						Object value = get(Utils.toClassType(componentType), indexedKey);
//						if (value == null) continue;
//
//						if      (componentType == boolean.class) Array.setBoolean(array, index, (Boolean  )value);
//						else if (componentType == char   .class) Array.setChar   (array, index, (Character)value);
//						else if (componentType == byte   .class) Array.setByte   (array, index, (Byte     )value);
//						else if (componentType == short  .class) Array.setShort  (array, index, (Short    )value);
//						else if (componentType == int    .class) Array.setInt    (array, index, (Integer  )value);
//						else if (componentType == long   .class) Array.setLong   (array, index, (Long     )value);
//						else if (componentType == float  .class) Array.setFloat  (array, index, (Float    )value);
//						else if (componentType == double .class) Array.setDouble (array, index, (Double   )value);
//					}
//					object = objectType.cast(array);
//				} else {
//					// Array of Object Type
//					Object[] objects = Utils.newArray(componentType, length);
//					for (int index = 0; index < objects.length; ++index)
//						objects[index] = get(componentType, toIndexedKey(key, index));
//
//					object = objectType.cast(objects);
//				}
//			} else {
//				// Non Array
//				String string = get(key);
//				object = objectType == String.class
//					? objectType.cast(string)
//				// 1.8.1
//				//	: TypeConverter.convert(TypeConverter.typeConverterMap, string, objectType);
//					: TypeConverter.convert(TypeConverter.typeConverterMap(), string, objectType);
//				////
//			}
//
//			// puts to the map
//			if (objectType != String.class) {
//				objectMap.put(key, object);
//				Logger logger = LoggerFactory.getLogger(Resource.class);
//				if (logger.isInfoEnabled())
//					logger.info("Resource: objectMap.put: baseName=" + baseName + ", key=" + key + ", null -> " + object);
//			}
//		}
//
//		return object;
//	}
//
//	/**
//	 * Converts a string associated with <b>key</b> to <b>objectType</b> and returns it.<br>
//	 * If can not find in the resource file or can not convert to the <b>objectType</b>,
//	 * Returns <b>defaultObject</b>.
//	 *
//	 * @param <T> the object type
//	 * @param key the key
//	 * @param objectType the object type (other than primitive types)
//	 * @param defaultObject the default object (permit null)
//	 * @return an object converted from a string associated with <b>key</b> or <b>defaultObject</b>
//	 *
//	 * @throws NullPointerException if key or objectType is null
//	 *
//	 * @see #get(java.lang.Class, java.lang.String)
//	 */
//	public <T> T get(Class<T> objectType, String key, T defaultObject) {
//		T object = defaultObject;
//		try {
//			object = get(objectType, key);
//		}
//		catch (MissingResourceException e) {}
//		catch (IllegalArgumentException e) {}
//		catch (ConvertException e) {}
//		return object;
//	}
//
//	/**
//	 * Puts <b>object</b> with <b>key</b> to the map.
//	 *
//	 * @param <T> the object type
//	 * @param objectType the object type
//	 * @param key the key
//	 * @param object the object to be put
//	 * @return the previous value associated with <b>key</b> (or null)
//	 *
//	 * @throws NullPointerException if <b>objectType</b>, <b>key</b> or <b>object</b> is null
//	 */
//	public synchronized <T> T put(Class<T> objectType, String key, T object) {
//		T beforeObject = objectType.cast(objectMap.put(key, object));
//		Logger logger = LoggerFactory.getLogger(Resource.class);
//		if (logger.isInfoEnabled())
//			logger.info("Resource: objectMap.put: baseName=" + baseName + ", key=" + key + ", " + beforeObject + " -> " + object);
//
//		return beforeObject;
//	}
//
//	/**
//	 * Removes the object associated with <b>key</b> from the map.
//	 *
//	 * @param <T> the object type
//	 * @param objectType the object type to be deleted
//	 * @param key the key
//	 * @return the removed object (or null)
//	 *
//	 * @throws NullPointerException if <b>key</b>, <b>objectType</b> is null
//	 */
//	public synchronized <T> T remove(Class<T> objectType, String key) {
//		T beforeObject = objectType.cast(objectMap.remove(key));
//		Logger logger = LoggerFactory.getLogger(Resource.class);
//		if (logger.isInfoEnabled())
//			logger.info("Resource: objectMap.remove: baseName=" + baseName + ", key=" + key + ", " + beforeObject);
//
//		return beforeObject;
//	}
//
//	/**
//	 * Returns the size of the array associated with <b>key</b>.<br>
//	 * Array size is defined as <b><i>key</i>.length</b> or <b><i>key</i>.size</b>
//	 * in properties file.
//	 *
//	 * @param key the key
//	 * @return the size of the array
//	 *
//	 * @throws NullPointerException if <b>key</b> is null
//	 * @throws MissingResourceException if can not find the resource fils or can not find the string associated with <b>key</b>
//	 * @throws ConvertException if can not convert the size of the array associated with <b>key</b> to a number
//	 */
//	private int getLength(String key) {
//		int length = get(Integer.class, key + ".length", -1);
//		if (length == -1) {
//			length = get(Integer.class, key + ".size", -1);
//			if (length == -1)
//				throw new MissingResourceException("Resource.getLength", baseName, key + ".length or " + key + ".size");
//		}
//
//		return length;
//	}
//
//	/**
//	 * Appends <b>index</b> to <b>key</b>.
//	 *
//	 * @param key the key
//	 * @param index the index
//	 * @return a key appended <b>index</b>
//	 *
//	 * @throws NullPointerException if <b>key</b> is null
//	 */
//	private String toIndexedKey(String key, int index) {
//		return key + "." + index;
//	}
////

	/**
	 * Returns this as a <b>Properties</b>.
	 *
	 * @return Properties a Properties object
	 *
	 * @since 1.1.0
	 */
	public Properties getProperties() {
		Properties properties = new Properties();

		if (resourceBundle != null) {
			Enumeration<String> keys = resourceBundle.getKeys();

			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				properties.setProperty(key, get(key));
			}
		}
		return properties;
	}

// 1.8.6
//	/**
//	 * Returns a <b>Properties</b> associated with <b>baseKey</b>.<br>
//	 * Ex.)<br>
//	 * <br>
//	 *
//	 * When the following have been defined in the properties file<br>
//	 * <div class="sampleCode">
//	 *   baseKey.key1 = vakue1<br>
//	 *   baseKey.key2 = vakue2<br>
//	 *   baseKey.key3 = value3<br>
//	 * </div>
//	 * <br>
//	 *
//	 * A <b>Properties</b> object returnd by <b>getProperties("baseKey")</b> is as follows.
//	 *
//	 * <div class="sampleCode">
//	 *   {"key1":"value1", "key2":"value2", "key3":"value3"}
//	 * </div>
//	 *
//	 * @param baseKey the base key
//	 * @return Properties a Properties object
//	 *
//	 * @throws NullPointerException if <b>baseKey</b> is null
//	 */
//	public Properties getProperties(String baseKey) {
//		Objects.requireNonNull(baseKey, "baseKey");
//
//		Properties properties = new Properties();
//
//		if (resourceBundle != null) {
//			Enumeration<String> keys = resourceBundle.getKeys();
//
//			baseKey = baseKey + ".";
//			int baseKeyLen = baseKey.length();
//
//			while (keys.hasMoreElements()) {
//				String key = keys.nextElement();
//				if (key.startsWith(baseKey) && key.length() > baseKeyLen) {
//					// key that starts with baseKey + '.'
//					String newKey = key.substring(baseKeyLen);
//					String value = get(key);
//					properties.setProperty(newKey, value);
//				}
//			}
//		}
//		return properties;
//	}
////

	/**
	 * Returns the value of resource property.
	 * 
	 * @param <V> the type of value
	 * @param propertyKey the key of resource property
	 * @param valueConverter function to convert string to return value
	 * @return the value of resource property
	 *
	 * @throws NullPointerException if <b>propertyKey</b> or <b>valueConverter</b> is null
	 * @throws MissingResourceException if the property dose not found
	 *
	 * @since 1.8.6
	 */
	public <V> V getValue(String propertyKey, Function<String, V> valueConverter) {
		return valueConverter.apply(get(propertyKey));
	}

	/**
	 * Returns the value of resource property.
	 * 
	 * @param <V> the type of value
	 * @param propertyKey the key of resource property
	 * @param valueConverter the function to convert string to return type
	 * @param defaultValue the default value
	 * @return the value of resource property (or defaultValue if not found in properties file)
	 *
	 * @throws NullPointerException if <b>propertyKey</b> or <b>valueConverter</b> is null
	 *
	 * @since 1.8.6
	 */
	public <V> V getValue(String propertyKey, Function<String, V> valueConverter, V defaultValue) {
		try {
			return valueConverter.apply(get(propertyKey));
		}
		catch (MissingResourceException e) {
			return defaultValue;
		}
	}

	/**
	 * Returns the string of resource property.
	 * 
	 * @param propertyKey the key of resource property
	 * @return the string value of resource property (or defaultValue if not found in properties file)
	 *
	 * @throws NullPointerException if <b>propertyKey</b> is null
	 * @throws MissingResourceException if the property dose not found
	 *
	 * @since 1.8.6
	 */
	public String getString(String propertyKey) {
		return getValue(propertyKey, stringConverter);
	}

	/**
	 * Returns the string of resource property.
	 * 
	 * @param propertyKey the key of resource property
	 * @param defaultValue the default value
	 * @return the string value of resource property (or defaultValue if not found in properties file)
	 *
	 * @throws NullPointerException if <b>propertyKey</b> is null
	 *
	 * @since 1.8.6
	 */
	public String getString(String propertyKey, String defaultValue) {
		return getValue(propertyKey, stringConverter, defaultValue);
	}

	/**
	 * Returns the int value of resource property.
	 *
	 * @param propertyKey the key of resource property
	 * @return the int value of resource property
	 *
	 * @throws NullPointerException if <b>propertyKey</b> is null
	 * @throws MissingResourceException if the property dose not found
	 * @throws NumberFormatException if the value can not convert to int
	 *
	 * @since 1.8.6
	*/
	public int getInt(String propertyKey) {
		return getValue(propertyKey, Integer::parseInt);
	}

	/**
	 * Returns the int value of resource property.
	 *
	 * @param propertyKey the key of resource property
	 * @param defaultValue the default value
	 * @return the int value of resource property (or defaultValue if not found in properties file)
	 *
	 * @throws NullPointerException if <b>propertyKey</b> is null
	 * @throws NumberFormatException if the value can not convert to int
	 *
	 * @since 1.8.6
	 */
	public int getInt(String propertyKey, int defaultValue) {
		return getValue(propertyKey, Integer::parseInt, defaultValue);
	}

	/**
	 * Returns a list created from the resource property value if it is found,
	 * an empty list otherwise.
	 *
	 * @param <E> the type of elements of the list
	 * @param propertyKey the key of resource property
	 * @param valueConverter the function to convert string to element type
	 * @return a created list (or an empty list)
	 *
	 * @throws NullPointerException if <b>propertyKey</b> or <b>valueConverter</b> is null
	 *
	 * @since 1.8.6
	 */
	public <E> List<E> getList(String propertyKey, Function<String, E> valueConverter) {
		Objects.requireNonNull(valueConverter, "valueConverter");

		String propertyValue = getString(propertyKey, "");

		List<E> list = new ArrayList<>();

		Arrays.stream(propertyValue.split(","))
			.forEach(string -> {
				string = string.trim();
				if (!string.isEmpty())
					list.add(valueConverter.apply(string));
			});

		return list;
	}

	/**
	 * Returns a string list created from the resource property value if it is found,
	 * an empty list otherwise.
	 *
	 * @param propertyKey the key of resource property
	 * @return a created string list (or an empty list)
	 *
	 * @throws NullPointerException if <b>propertyKey</b> is null
	 *
	 * @since 1.8.6
	 */
	public List<String> getStringList(String propertyKey) {
		return getList(propertyKey, stringConverter);
	}
	/**
	 * Returns a map created from the resource property value if it is found,
	 * an empty map otherwise.
	 *
	 * @param <K> the type of keys of map
	 * @param <V> the type of values of the map
	 * @param propertyKey the key of resource property
	 * @param keyConverter the function that converts string to the key type of the map
	 * @param valueConverter the function that converts string to the value type of the map
	 * @return a created map (or an empty map)
	 *
	 * @throws NullPointerException if <b>propertyKey</b>, <b>keyConverter</b> or <b>valueConverter</b> is null
	 *
	 * @since 1.8.6
	 */
	public <K, V> Map<K, V> getMap(String propertyKey, Function<String, K> keyConverter, Function<String, V> valueConverter) {
		Objects.requireNonNull(keyConverter, "keyConverter");
		Objects.requireNonNull(valueConverter, "valueConverter");

		String propertyValue = getString(propertyKey, "");

		Map<K, V> map = new HashMap<>();

		Arrays.stream(propertyValue.split(","))
			.forEach(string -> {
				string = string.trim();
				if (!string.isEmpty()) {
					String[] keyValueStr = string.split(":");
					String keyStr   = keyValueStr.length == 2 ? keyValueStr[0].trim() : "";
					String valueStr = keyValueStr.length == 2 ? keyValueStr[1].trim() : "";
					if (!keyStr.isEmpty() && !valueStr.isEmpty())
						map.put(keyConverter.apply(keyStr), valueConverter.apply(valueStr));
				}
			});

		return map;
	}

	/**
	 * Returns a map  (key: Integer, value: String) created from the resource property value if it is found,
	 * an empty map otherwise.
	 *
	 * @param propertyKey the key of resource property
	 * @return a created map (or an empty map)
	 *
	 * @throws NullPointerException if <b>propertyKey</b> is null
	 * @throws NumberFormatException if the value can not convert to int
	 *
	 * @since 1.8.6
	 */
	public Map<Integer, String> getIntegerKeyMap(String propertyKey) {
		return getMap(propertyKey, Integer::parseInt, stringConverter);
	}

	/**
	 * Returns a map (key: String, value: String) created from the resource property value if it is found,
	 * an empty map otherwise.
	 *
	 * @param propertyKey the key of resource property
	 * @return a created map (or an empty map)
	 *
	 * @throws NullPointerException if <b>propertyKey</b> is null
	 *
	 * @since 1.8.6
	 */
	public Map<String, String> getStringKeyMap(String propertyKey) {
		return getMap(propertyKey, s -> s, stringConverter);
	}
}
