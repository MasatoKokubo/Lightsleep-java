// Accessor.java
// (C) 2016 Masato Kokubo

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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.lightsleep.entity.NonColumn;
import org.lightsleep.entity.NonColumnProperty;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

/**
 * Gets and sets value for fields of objects.
 *
 * @param <T> The type of target object.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class Accessor<T> {
	// The logger
	private static final Logger logger = LoggerFactory.getLogger(Accessor.class);

	// Class Resources
	private static final Resource resource = new Resource(Accessor.class);
	private static final String messageMissingProperty          = resource.getString("messageMissingProperty");
	private static final String messageMissingGetMethod         = resource.getString("messageMissingGetMethod");
	private static final String messageMissingSetMethod         = resource.getString("messageMissingSetMethod");
	private static final String messagePropertyExceedsMaxNest   = resource.getString("messagePropertyExceedsMaxNest");
	private static final String messageIntermediateObjectIsNull = resource.getString("messageIntermediateObjectIsNull");

	// Maximum nesting level of property
	private static final int MAX_NEST = 8;

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

	private final Set<String> nonColumnSet = new HashSet<>();

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
		valueTypes.add(java.util.Date.class);
	}

	/**
	 * Constructs a new <b>Accessor</b>.
	 *
	 * @param objectClass the class of access target object
	 *
	 * @throws NullPointerException if <b>objectClass</b> is null
	 * @throws IllegalArgumentException if <b>objectClass</b> is interface
	 */
	public Accessor(Class<T> objectClass) {
		this.objectClass = Objects.requireNonNull(objectClass, "objectClass");
	// 2.2.0
		if (objectClass.isInterface())
			throw new IllegalArgumentException("objectClass: " + objectClass);
	////

		// @NonColumnProperty, @NonColumnProperties
		List<NonColumnProperty> nonColumnProperties = Utils.getAnnotations(objectClass, NonColumnProperty.class);
		nonColumnProperties.forEach(annotation -> {
			if (annotation.value())
				nonColumnSet.add(annotation.property());
			else
				nonColumnSet.remove(annotation.property());
		});

		putToMaps(objectClass, "", null, 0);

		propertyNames = fieldMap.keySet().stream().collect(Collectors.toList());

		valuePropertyNames = fieldMap.entrySet().stream()
			.filter(entry -> {
				Class<?> fieldType = getComponentType(entry.getValue().getType());
				return valueTypes.contains(fieldType) || fieldType.isEnum();
			})
			.map(Map.Entry::getKey)
			.collect(Collectors.toList());

		if (logger.isDebugEnabled()) {
			String logHeader = objectClass.getName() + '.';
			logger.debug("");
			for (int index = 0; index < propertyNames.size(); ++index)
				logger.debug(logHeader + "propertyNames." + index + ": " + objectClass.getName() + " / " + propertyNames.get(index));

			for (int index = 0; index < valuePropertyNames.size(); ++index)
				logger.debug(logHeader + "valuePropertyNames." + index + ": " + objectClass.getName() + " / " + valuePropertyNames.get(index));
		}
	}

	/**
	 * Puts to maps.
	 *
	 * @param objectClass the class of target object
	 * @param basePropertyName the base property name
	 * @param subGetter the getter of the base property
	 * @param nestCount the nest count of the property
	 */
	private void putToMaps(Class<?> objectClass, String basePropertyName, Function<T, Object> subGetter, int nestCount) {
		Class<?> superClass = objectClass.getSuperclass();
		if (superClass != null && superClass != Object.class)
			putToMaps(superClass, basePropertyName, subGetter, nestCount);

		Field[] fields = objectClass.getDeclaredFields();
		for (Field field : fields) {
			// @NonColumn
			NonColumn nonColumn = field.getAnnotation(NonColumn.class);
			if (nonColumn != null && nonColumn.value()) continue;

			int modifier = field.getModifiers();
			if (Modifier.isStatic(modifier)) continue; // static

			String fieldName = field.getName();
			if (fieldName.equals("metaClass")) continue; // When defined in Groovy
			Class<?> fieldType = field.getType();
			String propertyName = basePropertyName + fieldName;
			if (nonColumnSet.contains(propertyName)) continue;

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
							throw new RuntimeException(field.toString(), e);
						}
					}
					: object -> {
						try {
							return field.get(subGetter.apply(object));
						}
						catch (IllegalAccessException e) {
							throw new RuntimeException(field.toString(), e);
						}
					};

				setter = subGetter == null
					? (object, value) -> {
						try {
							field.set(object, value);
						}
						catch (IllegalAccessException e) {
							throw new RuntimeException(field.toString(), e);
						}
					}
					:  (object, value) -> {
						try {
							field.set(subGetter.apply(object), value);
						}
						catch (IllegalAccessException e) {
							throw new RuntimeException(field.toString(), e);
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
								throw new RuntimeException(getterMethod.toString(), e);
							}
						}
						: object -> {
							try {
								Object subObject = subGetter.apply(object);
								if (subObject == null) {
									logger.error(MessageFormat.format(
										messageIntermediateObjectIsNull, this.objectClass.getName(), basePropertyName));
									return null;
								} else
									return getterMethod.invoke(subObject);
							}
							catch (IllegalAccessException | InvocationTargetException e) {
								throw new RuntimeException(getterMethod.toString(), e);
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
								throw new RuntimeException(setterMethod.toString(), e);
							}
						}
						: (object, value) -> {
							try {
								Object subObject = subGetter.apply(object);
								if (subObject == null)
									logger.error(MessageFormat.format(
										messageIntermediateObjectIsNull, this.objectClass.getName(), basePropertyName));
								else
									setterMethod.invoke(subObject, value);
							}
							catch (IllegalAccessException | InvocationTargetException e) {
								throw new RuntimeException(setterMethod.toString(), e);
							}
						};
				}
			}

			// put to getterMap
			if (getter != null) {
				getterMap.put(propertyName, getter);

				if (nestCount >= MAX_NEST)
					throw new IllegalArgumentException(
						MessageFormat.format(messagePropertyExceedsMaxNest, this.objectClass.getName(), propertyName, MAX_NEST));

				if (!valueTypes.contains(fieldType) && !fieldType.isEnum())
					putToMaps(fieldType, basePropertyName + fieldName + '.', getter, nestCount + 1);
			}

			// put to setterMap
			if (setter != null) {
				setterMap.put(propertyName, setter);
			}
		}
	}

	/**
	 * Returns the getter <b>Method</b>.
	 *
	 * @param objectClass the class of target object
	 * @param fieldName the name of the field
	 * @param fieldType the type of the field
	 * @return the getter <b>Method</b> (null if not found)
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
	 * Returns the setter <b>Method</b>.
	 *
	 * @param objectClass the class of target object
	 * @param fieldName the name of the field
	 * @param fieldType the type of the field
	 * @return the getter <b>Method</b> (null if not found)
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
	 * Returns the component type of <b>type</b> if it is an array, <b>type</b> otherwise.
	 *
	 * @param type a type
	 * @return the component type of <b>type</b> or <b>type</b>.
	 */
	private static Class<?> getComponentType(Class<?> type) {
		return type.isArray() ? getComponentType(type.getComponentType()) : type;
	}

	/**
	 * Returns a list of property names of accessible all fields.<br>
	 * If not nested, the property name is the same as the field name.
	 * Otherwise, it is the name that connected each field with a period.
	 * (e.g. <b>name.first</b>)
	 *
	 * @return a list of property names
	 */
	public List<String> propertyNames() {
		return propertyNames;
	}

	/**
	 * Returns a list of property names of the accessible value type of the field.
	 * If not nested, the property name is the same as the field name.
	 * Otherwise, it is the name that connected each field with a period.
	 * (e.g. <b>name.first</b>)<br>
	 *
	 * Value type is one of the following.<br>
	 *
	 * <div class="blankline">&nbsp;</div>
	 *
	 * <div class="code indent">
	 *   boolean, char, byte, short, int, long, float, double,<br>
	 *   Boolean, Character, Byte, Short, Integer, Long, Float, Double, BigInteger, BigDecimal,<br>
	 *   String, java.util.Date, java.sql.Date, Time, Timestamp
	 *  </div>
	 *
	 * @return the list of the property names
	 */
	public List<String> valuePropertyNames() {
		return valuePropertyNames;
	}

	/**
	 * Returns the <b>Field</b> object of the fields that are specified by <b>propertyName</b>.
	 *
	 * @param propertyName a property name
	 * @return the <b>Field</b> object
	 *
	 * @throws MissingPropertyException if the field that are specified by <b>propertyName</b> dose not exist
	 */
	public Field getField(String propertyName) {
		Field field = fieldMap.get(propertyName);
		if (field == null)
			// Not found
			throw new MissingPropertyException(
				MessageFormat.format(messageMissingProperty, objectClass.getName(), propertyName));

		return field;
	}

	/**
	 * Returns the type of the field that are specified by <b>propertyName</b>.
	 *
	 * @param propertyName a property name
	 * @return the type
	 *
	 * @throws MissingPropertyException if the field that are specified by <b>propertyName</b> dose not exist
	 */
	public Class<?> getType(String propertyName) {
		return getField(propertyName).getType();
	}

	/**
	 * Returns a value of the field of the specified object.<br>
	 * If the field is <b>public</b>, gets the value directly,
	 * otherwise uses the <b>public</b> getting method related to the the field.<br>
	 * If the field name is <b>foo</b>, getting method is one of the following.<br>
	 * <ul>
	 *   <li><b>foo()   </b></li>
	 *   <li><b>getFoo()</b></li>
	 *   <li><b>isFoo() </b></li>
	 * </ul>
	 *
	 * @param object an object
	 * @param propertyName the property name of the field
	 * @return a value (might be null)
	 *
	 * @throws NullPointerException if <b>object</b> is null
	 * @throws MissingPropertyException if the get method for the field that are specified by <b>propertyName</b> dose not exist
	 * @throws RuntimeException if <b>IllegalAccessException</b> was thrown
	 */
	public Object getValue(T object, String propertyName) {
		Objects.requireNonNull(object, () -> "object: null, propertyName: " + propertyName);

		Function<T, Object> getter = getterMap.get(propertyName);
		if (getter == null)
			// Not found
			throw new MissingPropertyException(
				MessageFormat.format(messageMissingGetMethod, objectClass.getName(), propertyName));

		Object value = getter.apply(object);
		return value;
	}

	/**
	 * Sets a value to the field of the specified object.<br>
	 * If the field is <b>public</b>, sets the value directly,
	 * otherwise uses the <b>public</b> setting method related to the the field.<br>
	 * If the field name is <b>foo</b>, setting method is one of the following.<br>
	 * <ul>
	 *   <li><b>foo()   </b></li>
	 *   <li><b>setFoo()</b></li>
	 * </ul>
	 *
	 * @param object an object
	 * @param propertyName the property name of the field
	 * @param value a value to be set the field (permit null)
	 *
	 * @throws NullPointerException if <b>object</b> is null
	 * @throws MissingPropertyException if the set method for the field that are specified by <b>propertyName</b> does not exist
	 * @throws RuntimeException if <b>IllegalAccessException</b> or <b>InvocationTargetException</b> was thrown
	 */
	public void setValue(T object, String propertyName, Object value) {
		Objects.requireNonNull(object, () -> "object: null, propertyName: " + propertyName);

		BiConsumer<T, Object> setter = setterMap.get(propertyName);
		if (setter == null)
			// Not found
			throw new MissingPropertyException(
				MessageFormat.format(messageMissingSetMethod, objectClass.getName(), propertyName));

		if (value == null) {
			Field field = getField(propertyName);
			if (field.getType().isPrimitive()) {
				logger.info("setValue: (" + field.getType().getName() + ")" + propertyName + " <- null");
				return;
			}
		}

		setter.accept(object, value);
	}
}
