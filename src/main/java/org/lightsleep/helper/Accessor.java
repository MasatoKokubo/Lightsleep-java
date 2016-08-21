/*
	Accessor.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.helper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.lightsleep.entity.NonColumn;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

/**
	Gets and sets value for fields of objects.

	@param <T> The type of target object.

	@since 1.0.0
	@author Masato Kokubo
*/
public class Accessor<T> {
	// The logger
	private static final Logger logger = LoggerFactory.getLogger(Accessor.class);

	// Class Resources
	private static final Resource resource = new Resource(Accessor.class);
	private static final String messagePropertyIsNotFound       = resource.get("messagePropertyIsNotFound");
	private static final String messageIntermediateObjectIsNull = resource.get("messageIntermediateObjectIsNull");

	// The target class
	private final Class<T> objectClass;

	// A map (property name -> Field)
	private final Map<String, Field> fieldMap = new LinkedHashMap<>();

	// A map (property name -> getter Function)
	private final Map<String, Function<T, Object>> getterMap = new LinkedHashMap<>();

	// A map (property name -> setter BiConsumer)
	private final Map<String, BiConsumer<T, Object>> setterMap = new LinkedHashMap<>();

	// The list of property names of accessible all fields
	private final List<String> propertyNames;

	// The list of the property names of the properties to be getting and setting the value
	private final List<String> valuePropertyNames;

	// Prefixes of getter methods
	private static final String[] getterPrefixes = new String[]{"", "get", "is"};

	// Prefixes of setter methods
	private static final String[] setterPrefixes = new String[]{"", "set"};

	// Value types
	private static final Set<Class<?>> valueTypes = new LinkedHashSet<>();
	static {
		valueTypes.add(boolean   .class);
		valueTypes.add(char      .class);
		valueTypes.add(byte      .class);
		valueTypes.add(short     .class);
		valueTypes.add(int       .class);
		valueTypes.add(long      .class);
		valueTypes.add(float     .class);
		valueTypes.add(double    .class);
		valueTypes.add(Boolean   .class);
		valueTypes.add(Character .class);
		valueTypes.add(Byte      .class);
		valueTypes.add(Short     .class);
		valueTypes.add(Integer   .class);
		valueTypes.add(Long      .class);
		valueTypes.add(Float     .class);
		valueTypes.add(Double    .class);
		valueTypes.add(BigInteger.class);
		valueTypes.add(BigDecimal.class);
		valueTypes.add(String    .class);
		valueTypes.add(Date      .class);
		valueTypes.add(Time      .class);
		valueTypes.add(Timestamp .class);
	}

	/**
		Constructs a new <b>Accessor</b>.

		@param objectClass the class of access target object
	*/
	public Accessor(Class<T> objectClass) {
		if (objectClass == null) throw new NullPointerException("Accessor.<init>: objectClass == null");

		this.objectClass = objectClass;

		putToMaps(objectClass, "", null);

		propertyNames = fieldMap.keySet().stream().collect(Collectors.toList());

		valuePropertyNames = fieldMap.entrySet().stream()
			.filter(entry -> {
				Class<?> fieldType = getComponentType(entry.getValue().getType());
				return valueTypes.contains(fieldType) || fieldType.isEnum();
			})
			.map(entry -> entry.getKey())
			.collect(Collectors.toList());

		if (logger.isDebugEnabled()) {
			logger.debug("");
			for (int index = 0; index < propertyNames.size(); ++index)
				logger.debug("Accessor: propertyNames." + index + ": " + objectClass.getName() + " / " + propertyNames.get(index));

			logger.debug("");
			for (int index = 0; index < valuePropertyNames.size(); ++index)
				logger.debug("Accessor: valuePropertyNames." + index + ": " + objectClass.getName() + " / " + valuePropertyNames.get(index));

			logger.debug("");
		}
	}

	/**
		Puts to maps.

		@param objectClass the class of target object
		@param basePropertyName the base property name
		@param subGetter the getter of the base property
	*/
	private void putToMaps(Class<?> objectClass, String basePropertyName, Function<T, Object> subGetter) {
		Class<?> superClass = objectClass.getSuperclass();
		if (superClass != null && superClass != Object.class)
			putToMaps(superClass, basePropertyName, subGetter);

		Field[] fields = objectClass.getDeclaredFields();
		for (Field field : fields) {
			// @NonColumn
			if (field.getAnnotation(NonColumn.class) != null) continue;

			int modifier = field.getModifiers();
			if (Modifier.isStatic(modifier)) continue; // static

			String fieldName = field.getName();
			Class<?> fieldType = field.getType();
			String propertyName = basePropertyName + fieldName;

			// put to fieldMap
			fieldMap.put(propertyName, field);

			Function<T, Object> getter = null;
			BiConsumer<T, Object> setter = null;

			if (Modifier.isPublic(modifier)) {
				// public field
				getter = subGetter == null
					? object -> {
						try {
							return field.get(object);
						}
						catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}
					: object -> {
						try {
							return field.get(subGetter.apply(object));
						}
						catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					};

				setter = subGetter == null
					? (object, value) -> {
						try {
							field.set(object, value);
						}
						catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}
					:  (object, value) -> {
						try {
							field.set(subGetter.apply(object), value);
						}
						catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					};

			} else {
				// non public field
				Method getterMethod = getGetterMethod(objectClass, fieldName, fieldType);
				Method setterMethod = getSetterMethod(objectClass, fieldName, fieldType);

				if (getterMethod != null) {
					getter = subGetter == null
						? object -> {
							try {
								return getterMethod.invoke(object);
							}
							catch (IllegalAccessException | InvocationTargetException e) {
								throw new RuntimeException(e);
							}
						}
						: object -> {
							try {
								Object subObject = subGetter.apply(object);
								if (subObject == null) {
									logger.error(MessageFormat.format(
										messageIntermediateObjectIsNull, objectClass.getName(), basePropertyName));
									return null;
								} else
									return getterMethod.invoke(subObject);
							}
							catch (IllegalAccessException | InvocationTargetException e) {
								throw new RuntimeException(e);
							}
						};
				}

				if (setterMethod != null) {
					setter = subGetter == null
						? (object, value) -> {
							try {
								setterMethod.invoke(object, value);
							}
							catch (IllegalAccessException | InvocationTargetException e) {
								throw new RuntimeException(e);
							}
						}
						: (object, value) -> {
							try {
								Object subObject = subGetter.apply(object);
								if (subObject == null)
									logger.error(MessageFormat.format(
										messageIntermediateObjectIsNull, objectClass.getName(), basePropertyName));
								else
									setterMethod.invoke(subObject, value);
							}
							catch (IllegalAccessException | InvocationTargetException e) {
								throw new RuntimeException(e);
							}
						};
				}
			}

			// put to getterMap
			if (getter != null) {
				getterMap.put(propertyName, getter);

				if (!valueTypes.contains(fieldType) && !fieldType.isEnum())
					putToMaps(fieldType, fieldName + '.', getter);
			}

			// put to setterMap
			if (setter != null) {
				setterMap.put(propertyName, setter);
			}
		}
	}

	/**
		Returns the getter <b>Method</b>.

		@param objectClass the class of target object
		@param fieldName the name of the field
		@param fieldType the type of the field

		@return the getter <b>Method</b> (<b>null</b> if not found)
	*/
	private Method getGetterMethod(Class<?> objectClass, String fieldName, Class<?> fieldType) {
		Method getterMethod = null;

		for (String getterPrefix : getterPrefixes) {
			String getterName = getterPrefix.isEmpty()
				? fieldName
				: getterPrefix + fieldName.substring(0, 1).toUpperCase(Locale.ENGLISH) + fieldName.substring(1);
			try {
				getterMethod = objectClass.getMethod(getterName);
				if (getterMethod.getReturnType() == fieldType)
					break;

				getterMethod = null;
			}
			catch (NoSuchMethodException e) {
			}
		}

		return getterMethod;
	}

	/**
		Returns the setter <b>Method</b>.

		@param objectClass the class of target object
		@param fieldName the name of the field
		@param fieldType the type of the field

		@return the getter <b>Method</b> (<b>null</b> if not found)
	*/
	private Method getSetterMethod(Class<?> objectClass, String fieldName, Class<?> fieldType) {
		Method setterMethod = null;

		for (String setterPrefix : setterPrefixes) {
			String setterName = setterPrefix.isEmpty()
				? fieldName
				: setterPrefix + fieldName.substring(0, 1).toUpperCase(Locale.ENGLISH) + fieldName.substring(1);
			try {
				setterMethod = objectClass.getMethod(setterName, fieldType);
				break;
			}
			catch (NoSuchMethodException e) {
			}
		}

		return setterMethod;
	}

	/**
		Returns the component type of <b>type</b> if it is an array, <b>type</b> otherwise.

		@param type a type

		@return the component type of <b>type</b> or <b>type</b>.
	*/
	private static Class<?> getComponentType(Class<?> type) {
		return type.isArray() ? getComponentType(type.getComponentType()) : type;
	}

	/**
		Returns a list of property names of accessible all fields.<br>
		If not nested, the property name is the same as the field name.
		Otherwise, it is the name that connected each field with a period.
		(e.g. <b>name.first</b>)

		@return a list of property names
	*/
	public List<String> propertyNames() {
		return propertyNames;
	}

	/**
		Returns a list of property names of the accessible value type of the field.
		If not nested, the property name is the same as the field name.
		Otherwise, it is the name that connected each field with a period.
		(e.g. <b>name.first</b>)<br>

		Value type is one of the following.<br>

		<div class="blankline">&nbsp;</div>

		<div class="code indent">
			boolean, char, byte, short, int, long, float, double,<br>
			Boolean, Character, Byte, Short, Integer, Long, Float, Double, BigInteger, BigDecimal,<br>
			String, java.util.Date, java.sql.Date, Time, Timestamp
		</div>

		@return the list of the property names
	*/
	public List<String> valuePropertyNames() {
		return valuePropertyNames;
	}

	/**
		Returns the <b>Field</b> object of the fields that are specified by <b>propertyName</b>.

		@param propertyName a property name

		@return the <b>Field</b> object

		@throws IllegalArgumentException if the field that are specified by <b>propertyName</b> is not found
	*/
	public Field getField(String propertyName) {
		Field field = fieldMap.get(propertyName);
		if (field == null)
			// Not found
			throw new IllegalArgumentException(
				MessageFormat.format(messagePropertyIsNotFound, propertyName, objectClass.getName()));

		return field;
	}

	/**
		Returns the type of the field that are specified by <b>propertyName</b>.

		@param propertyName a property name

		@return the type

		@throws IllegalArgumentException if the field that are specified by <b>propertyName</b> is not found
	*/
	public Class<?> getType(String propertyName) {
		return getField(propertyName).getType();
	}

	/**
		Returns a value of the field of the specified object.<br>
		If the field is <b>public</b>, gets the value directly,
		otherwise uses the <b>public</b> getting method associated with the the field.<br>
		If the field name is <b>foo</b>, getting method is one of the following.<br>
		<ul>
			<li><b>foo()   </b></li>
			<li><b>getFoo()</b></li>
			<li><b>isFoo() </b></li>
		</ul>

		@param object an object
		@param propertyName the property name of the field

		@return a value (might be <b>null</b>)

		@throws NullPointerException if <b>object</b> is <b>null</b>
		@throws IllegalArgumentException if the field that are specified by <b>propertyName</b> is not found
		@throws RuntimeException if <b>IllegalAccessException</b> was thrown
	*/
	public Object getValue(T object, String propertyName) {
		if (object == null) throw new NullPointerException("Accessor.getValue: object = null, propertyName = " + propertyName);

		Function<T, Object> getter = getterMap.get(propertyName);
		if (getter == null)
			// Not found
			throw new IllegalArgumentException(
				MessageFormat.format(messagePropertyIsNotFound, propertyName, objectClass.getName()));

		Object value = getter.apply(object);
		return value;
	}

	/**
		Sets a value to the field of the specified object.<br>
		If the field is <b>public</b>, sets the value directly,
		otherwise uses the <b>public</b> setting method associated with the the field.<br>
		If the field name is <b>foo</b>, setting method is one of the following.<br>
		<ul>
			<li><b>foo()   </b></li>
			<li><b>setFoo()</b></li>
		</ul>

		@param object an object
		@param propertyName the property name of the field
		@param value a value to be set the field (permit <b>null</b>)

		@throws NullPointerException if <b>object</b> is <b>null</b>
		@throws IllegalArgumentException if the field that are specified by <b>propertyName</b> is not found
		@throws RuntimeException if <b>IllegalAccessException</b> or <b>InvocationTargetException</b> was thrown
	*/
	public void setValue(T object, String propertyName, Object value) {
		if (object == null) throw new NullPointerException("Accessor.setValue: object = null, propertyName = " + propertyName);

		BiConsumer<T, Object> setter = setterMap.get(propertyName);
		if (setter == null)
			// Not found
			throw new IllegalArgumentException(
				MessageFormat.format(messagePropertyIsNotFound, propertyName, objectClass.getName()));

		if (value == null) {
			Field field = getField(propertyName);
			if (field.getType().isPrimitive()) {
				logger.warn("Accessor.setValue: (" + field.getType().getName() + ")" + propertyName + " <- null");
				return;
			}
		}

		setter.accept(object, value);
	}
}
