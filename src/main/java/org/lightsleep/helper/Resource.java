/*
	Resource.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.helper;

import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.lightsleep.helper.ConvertException;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

/**
	Treats the resource file.

	@since 1.0
	@author Masato Kokubo
*/
public class Resource {
	/** The global <b>Resource</b> */
	public static final Resource globalResource = new Resource(System.getProperty("lightsleep.resource", "lightsleep"));

	// The base name
	private final String baseName;

	// The resource bundle
	private ResourceBundle resourceBundle;

	// The map of property name and value
	private final Map<String, Object> objectMap = new LinkedHashMap<String, Object>();

	/**
		Constructs a new <b>Resource</b>.

		@param baseName the base name
	*/
	public Resource(String baseName) {
		this.baseName = baseName;
		try {
			resourceBundle = ResourceBundle.getBundle(baseName);
		}
		catch (MissingResourceException e) {
			LoggerFactory.getLogger(Resource.class).error(e.getMessage());
		}
	}

	/**
		Constructs a new <b>Resource</b>.

		@param clazz the class which name is used as base name
	*/
	public Resource(Class<?> clazz) {
		this(clazz.getName());
	}

	/**
		Returns a string associated with <b>key</b>.

		@param key the key

		@return the string associated with <b>key</b>

		@throws NullPointerException if <b>key</b> is null
		@throws MissingResourceException if can not find the resource fils or can not find the string associated with <b>key</b>
	*/
	public String get(String key) {
		if (key == null) throw new NullPointerException("Resource.get: key == null");

		String string = null;

		MissingResourceException e = null;

		if (resourceBundle != null) {
			// gets from the resource bundle
			try {
				string = resourceBundle.getString(key).trim();
			}
			catch (MissingResourceException e2) {
				e = e2;
			}
		}

		if (string == null)
			throw e != null ? e : new MissingResourceException("Resource.get", baseName, key);

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
						? get(refKey, null) : null;
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

	/**
		Returns a string associated with <b>key</b>.<br>
		If can not find in the resource file, returns <b>defaultValue</b>

		@param key the key
		@param defaultValue the default value (permit <b>null</b>)

		@return the string associated with <b>key</b>

		@throws NullPointerException if <b>key</b> is null
	*/
	public String get(String key, String defaultValue) {
		String string = defaultValue;

		try {
			string = get(key);
		}
		catch (MissingResourceException e) {}

		return string;
	}

	/**
		Converts a string associated with <b>key</b> to <b>objectType</b> and returns it.<br>

		You can also specify an array type as <b>objectType</b>.<br>
		Ex.:<br>
		<code>
			values.length = 4 (or values.size = 4)<br>
			values.0 = A<br>
			values.1 = B<br>
			values.2 = C<br>
			values.3 = D<br>
		</code>

		@param <T> the object type

		@param key the key
		@param objectType the object type (other than primitive types)

		@return an object converted from a string associated with <b>key</b>

		@throws NullPointerException if <b>key</b> or <b>objectType</b> is <b>null</b>
		@throws MissingResourceException if can not find the resource fils or can not find the string associated with <b>key</b>
		@throws ConvertException if can not convert to <b>objectType</b>
	*/
	public <T> T get(Class<T> objectType, String key) {
		if (objectType == null) throw new NullPointerException("Resource.get: key = " + key + ", objectType == null");
		if (objectType.isPrimitive()) throw new IllegalArgumentException("Resource.get: key = " + key + ", objectType.isPrimitive() = true");
		if (key == null) throw new NullPointerException("Resource.get: key = null, objectType = " + objectType);

		// gets from the map
		T object = objectType.cast(objectMap.get(key));

		if (object == null) {
			if (objectType.isArray()) {
				// is Array
				int length = getLength(key);
				Class<?> componentType = objectType.getComponentType();
				if (componentType.isPrimitive()) {
					// Array of Primitive Type
					Object array = Array.newInstance(componentType, length);
					for (int index = 0; index < length; ++index) {
						String indexedKey = toIndexedKey(key, index);
						Object value = get(Utils.toClassType(componentType), indexedKey);
						if (value == null) continue;

						if      (componentType == boolean.class) Array.setBoolean(array, index, (Boolean  )value);
						else if (componentType == char   .class) Array.setChar   (array, index, (Character)value);
						else if (componentType == byte   .class) Array.setByte   (array, index, (Byte     )value);
						else if (componentType == short  .class) Array.setShort  (array, index, (Short    )value);
						else if (componentType == int    .class) Array.setInt    (array, index, (Integer  )value);
						else if (componentType == long   .class) Array.setLong   (array, index, (Long     )value);
						else if (componentType == float  .class) Array.setFloat  (array, index, (Float    )value);
						else if (componentType == double .class) Array.setDouble (array, index, (Double   )value);
					}
					object = objectType.cast(array);
				} else {
					// Array of Object Type
					Object[] objects = Utils.newArray(componentType, length);
					for (int index = 0; index < objects.length; ++index)
						objects[index] = get(componentType, toIndexedKey(key, index));

					object = objectType.cast(objects);
				}
			} else {
				// Non Array
				String string = get(key);
				object = objectType == String.class
					? objectType.cast(string)
					: TypeConverter.convert(TypeConverter.typeConverterMap, string, objectType);
			}

			// puts to the map
			if (objectType != String.class) {
				objectMap.put(key, object);
				Logger logger = LoggerFactory.getLogger(Resource.class);
				if (logger.isInfoEnabled())
					logger.info("Resource: objectMap.put: baseName=" + baseName + ", key=" + key + ", null -> " + object);
			}
		}

		return object;
	}

	/**
		Converts a string associated with <b>key</b> to <b>objectType</b> and returns it.<br>
		If can not find in the resource file or can not convert to the <b>objectType</b>,
		returns <b>defaultObject</b>.

		@see #get(java.lang.Class, java.lang.String)

		@param <T> the object type

		@param key the key
		@param objectType the object type (other than primitive types)
		@param defaultObject the default object (permit <b>null</b>)

		@return an object converted from a string associated with <b>key</b> or <b>defaultObject</b>

		@throws NullPointerException if key or objectType is <b>null</b>
	*/
	public <T> T get(Class<T> objectType, String key, T defaultObject) {
		T object = defaultObject;
		try {
			object = get(objectType, key);
		}
		catch (MissingResourceException e) {}
		catch (IllegalArgumentException e) {}
		catch (ConvertException e) {}
		return object;
	}

	/**
		Puts <b>object</b> with <b>key</b> to the map.

		@param <T> the object type

		@param objectType the object type
		@param key the key
		@param object the object to be put

		@return the previous value associated with <b>key</b> (or <b>null</b>)

		@throws NullPointerException if <b>objectType</b>, <b>key</b> or <b>object</b> is <b>null</b>
	*/
	public synchronized <T> T put(Class<T> objectType, String key, T object) {
		T beforeObject = objectType.cast(objectMap.put(key, object));
		Logger logger = LoggerFactory.getLogger(Resource.class);
		if (logger.isInfoEnabled())
			logger.info("Resource: objectMap.put: baseName=" + baseName + ", key=" + key + ", " + beforeObject + " -> " + object);

		return beforeObject;
	}

	/**
		Removes the object associated with <b>key</b> from the map.

		@param <T> the object type

		@param objectType the object type to be deleted
		@param key the key

		@return the removed object (or <b>null</b>)

		@throws NullPointerException if <b>key</b>, <b>objectType</b> is <b>null</b>
	*/
	public synchronized <T> T remove(Class<T> objectType, String key) {
		T beforeObject = objectType.cast(objectMap.remove(key));
		Logger logger = LoggerFactory.getLogger(Resource.class);
		if (logger.isInfoEnabled())
			logger.info("Resource: objectMap.remove: baseName=" + baseName + ", key=" + key + ", " + beforeObject);

		return beforeObject;
	}

	/**
		Returns the size of the array associated with <b>key</b>.<br>
		Array size is defined as <b><i>key</i>.length</b> or <b><i>key</i>.size</b>
		in properties file.

		@param key the key

		@return the size of the array

		@throws NullPointerException if <b>key</b> is null
		@throws MissingResourceException if can not find the resource fils or can not find the string associated with <b>key</b>
		@throws ConvertException if can not convert the size of the array associated with <b>key</b> to a number
	*/
	private int getLength(String key) {
		int length = get(Integer.class, key + ".length", -1);
		if (length == -1) {
			length = get(Integer.class, key + ".size", -1);
			if (length == -1)
				throw new MissingResourceException("Resource.getLength", baseName, key + ".length or " + key + ".size");
		}

		return length;
	}

	/**
		Appends <b>index</b> to <b>key</b>.

		@param key the key
		@param index the index

		@return a key appended <b>index</b>

		@throws NullPointerException if <b>key</b> is <b>null</b>
	*/
	private String toIndexedKey(String key, int index) {
		return key + "." + index;
	}

	/**
		Returns this as a <b>Properties</b>.

		@since 1.1.0

		@return Properties a Properties object
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

	/**
		Returns a <b>Properties</b> associated with <b>baseKey</b>.<br>
		Ex.)<br>
		<br>

		When the following have been defined in the properties file<br>
		<div class="sampleCode">
			baseKey.key1 = vakue1<br>
			baseKey.key2 = vakue2<br>
			baseKey.key3 = value3<br>
		</div>
		<br>

		A <b>Properties</b> object returnd by <b>getProperties("baseKey")</b> is as follows.

		<div class="sampleCode">
			{"key1":"value1", "key2":"value2", "key3":"value3"}
		</div>

		@param baseKey the base key

		@return Properties a Properties object

		@throws NullPointerException if <b>baseKey</b> is <b>null</b>
	*/
	public Properties getProperties(String baseKey) {
		if (baseKey == null) throw new NullPointerException("Resource.getProperties: baseKey == null");

		Properties properties = new Properties();

		if (resourceBundle != null) {
			Enumeration<String> keys = resourceBundle.getKeys();

			baseKey = baseKey + ".";
			int baseKeyLen = baseKey.length();

			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				if (key.startsWith(baseKey) && key.length() > baseKeyLen) {
					// key that starts with baseKey + '.'
					String newKey = key.substring(baseKeyLen);
					String value = get(key);
					properties.setProperty(newKey, value);
				}
			}
		}
		return properties;
	}
}
