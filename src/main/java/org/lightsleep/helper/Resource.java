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

import org.lightsleep.logger.LoggerFactory;

/**
 *	Treats the resource file.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class Resource {
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

	/** The global <b>Resource</b> */
// 2.0.0
//	public static final Resource globalResource;
//	static {
//		Resource resource = new Resource(System.getProperty("lightsleep.resource", "lightsleep"));
//		if (resource == null)
//			resource = new Resource(Sql.class.getPackage().getName() + ".lightsleep");
//		globalResource = resource;
//	}
// 2.1.0
//	public static final Resource globalResource = new Resource(System.getProperty("lightsleep.resource", "lightsleep"));
	private static Resource global;
	static {
		initClass();
	}

	private static void initClass() {
		global = new Resource(System.getProperty("lightsleep.resource", "lightsleep"));
	}
////

	/**
	 * Returns the <b>Resource</b> created based on lightsleep.properties.
	 *
	 * @return the <b>Resource</b> created based on lightsleep.properties
	 *
	 * @since 2.1.0
	 */
	public static Resource getGlobal() {
		return global;
	}

	// The base name
	private final String baseName;

	// The resource bundle
	private ResourceBundle resourceBundle;

	/**
	 * Constructs a new <b>Resource</b>.
	 *
	 * @param baseName the base name
	 */
	public Resource(String baseName) {
		this.baseName = baseName;
		try {
			resourceBundle = ResourceBundle.getBundle(baseName, control);
		}
		catch (MissingResourceException e) {
			if (global != null)
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
	 * Returns a string related to <b>propertyKey<b>.
	 *
	 * @param propertyKey the key of resource property
	 * @return the string value of resource property
	 *
	 * @throws NullPointerException if <b>propertyKey</b> is null
	 * @throws MissingResourceException if the property dose not found
	 */
	private String get(String propertyKey) {
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
