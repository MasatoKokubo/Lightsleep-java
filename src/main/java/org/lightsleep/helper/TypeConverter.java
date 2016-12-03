/*
	TypeConverter.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.helper;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import org.lightsleep.component.SqlString;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

/**
	A class to convert data types.<br>

	Following <b>TypeConverter</b> objects has been registered in <b>typeConverterMap</b>.<br>

	<table class="additinal">
		<caption><span>Registered TypeConverter objects</span></caption>
		<tr><th>Source Data Type</th><th>Destination Data Type</th></tr>

		<tr><td>Byte          </td><td rowspan="9">Boolean</td></tr>
		<tr><td>Short         </td></tr>
		<tr><td>Integer       </td></tr>
		<tr><td>Long          </td></tr>
		<tr><td>Float         </td></tr>
		<tr><td>Double        </td></tr>
		<tr><td>BigDecimal    </td></tr>
		<tr><td>Character     </td></tr>
		<tr><td>String        </td></tr>

		<tr><td>Boolean       </td><td rowspan="9">Byte</td></tr>
		<tr><td>Short         </td></tr>
		<tr><td>Integer       </td></tr>
		<tr><td>Long          </td></tr>
		<tr><td>Float         </td></tr>
		<tr><td>Double        </td></tr>
		<tr><td>BigDecimal    </td></tr>
		<tr><td>Character     </td></tr>
		<tr><td>String        </td></tr>

		<tr><td>Boolean       </td><td rowspan="9">Short</td></tr>
		<tr><td>Short         </td></tr>
		<tr><td>Integer       </td></tr>
		<tr><td>Long          </td></tr>
		<tr><td>Float         </td></tr>
		<tr><td>Double        </td></tr>
		<tr><td>BigDecimal    </td></tr>
		<tr><td>Character     </td></tr>
		<tr><td>String        </td></tr>

		<tr><td>Boolean       </td><td rowspan="9">Integer</td></tr>
		<tr><td>Short         </td></tr>
		<tr><td>Integer       </td></tr>
		<tr><td>Long          </td></tr>
		<tr><td>Float         </td></tr>
		<tr><td>Double        </td></tr>
		<tr><td>BigDecimal    </td></tr>
		<tr><td>Character     </td></tr>
		<tr><td>String        </td></tr>

		<tr><td>Boolean       </td><td rowspan="9">Long</td></tr>
		<tr><td>Short         </td></tr>
		<tr><td>Integer       </td></tr>
		<tr><td>Long          </td></tr>
		<tr><td>Float         </td></tr>
		<tr><td>Double        </td></tr>
		<tr><td>BigDecimal    </td></tr>
		<tr><td>Character     </td></tr>
		<tr><td>String        </td></tr>

		<tr><td>Boolean       </td><td rowspan="9">Float</td></tr>
		<tr><td>Short         </td></tr>
		<tr><td>Integer       </td></tr>
		<tr><td>Long          </td></tr>
		<tr><td>Float         </td></tr>
		<tr><td>Double        </td></tr>
		<tr><td>BigDecimal    </td></tr>
		<tr><td>Character     </td></tr>
		<tr><td>String        </td></tr>

		<tr><td>Boolean       </td><td rowspan="9">Double</td></tr>
		<tr><td>Short         </td></tr>
		<tr><td>Integer       </td></tr>
		<tr><td>Long          </td></tr>
		<tr><td>Float         </td></tr>
		<tr><td>Double        </td></tr>
		<tr><td>BigDecimal    </td></tr>
		<tr><td>Character     </td></tr>
		<tr><td>String        </td></tr>

		<tr><td>Boolean       </td><td rowspan="9">BigDecimal</td></tr>
		<tr><td>Short         </td></tr>
		<tr><td>Integer       </td></tr>
		<tr><td>Long          </td></tr>
		<tr><td>Float         </td></tr>
		<tr><td>Double        </td></tr>
		<tr><td>BigDecimal    </td></tr>
		<tr><td>Character     </td></tr>
		<tr><td>String        </td></tr>

		<tr><td>Boolean       </td><td rowspan="9">Character</td></tr>
		<tr><td>Short         </td></tr>
		<tr><td>Integer       </td></tr>
		<tr><td>Long          </td></tr>
		<tr><td>Float         </td></tr>
		<tr><td>Double        </td></tr>
		<tr><td>BigDecimal    </td></tr>
		<tr><td>Character     </td></tr>
		<tr><td>String        </td></tr>

		<tr><td>Object        </td><td rowspan="3">String</td></tr>
		<tr><td>BigDecimal    </td></tr>
		<tr><td>Timestamp     </td></tr>

		<tr><td>Long          </td><td rowspan="3">java.sql.Date</td></tr>
		<tr><td>java.util.Date</td></tr>
		<tr><td>String        </td></tr>

		<tr><td>Long          </td><td rowspan="3">Time</td></tr>
		<tr><td>java.util.Date</td></tr>
		<tr><td>String        </td></tr>

		<tr><td>Long          </td><td rowspan="3">Timestamp</td></tr>
		<tr><td>java.util.Date</td></tr>
		<tr><td>String        </td></tr>
	</table>

	@see org.lightsleep.database.Standard
	@see org.lightsleep.database.MySQL
	@see org.lightsleep.database.Oracle
	@see org.lightsleep.database.PostgreSQL
	@see org.lightsleep.database.SQLServer

	@since 1.0
	@author Masato Kokubo
*/
public class TypeConverter<ST, DT> {
	// The logger
	private static final Logger logger = LoggerFactory.getLogger(TypeConverter.class);

	// Well known classes
	private static final Set<Class<?>> wellKnownClasses = new HashSet<>();
	static {
		wellKnownClasses.add(Boolean.class);
		wellKnownClasses.add(Character.class);
		wellKnownClasses.add(Byte.class);
		wellKnownClasses.add(Short.class);
		wellKnownClasses.add(Integer.class);
		wellKnownClasses.add(Long.class);
		wellKnownClasses.add(Float.class);
		wellKnownClasses.add(Double.class);
		wellKnownClasses.add(BigDecimal.class);
		wellKnownClasses.add(String.class);
		wellKnownClasses.add(Date.class);
		wellKnownClasses.add(Time.class);
		wellKnownClasses.add(Timestamp.class);
		wellKnownClasses.add(BigDecimal[].class);
		wellKnownClasses.add(String[].class);
		wellKnownClasses.add(Date[].class);
		wellKnownClasses.add(Time[].class);
		wellKnownClasses.add(Timestamp[].class);
		wellKnownClasses.add(Clob.class);
		wellKnownClasses.add(Blob.class);
		wellKnownClasses.add(Enum.class);
		wellKnownClasses.add(Array.class);
		wellKnownClasses.add(Iterable.class);
		wellKnownClasses.add(ArrayList.class);
		wellKnownClasses.add(LinkedList.class);
		wellKnownClasses.add(HashSet.class);
		wellKnownClasses.add(LinkedHashSet.class);
		wellKnownClasses.add(TreeSet.class);
		wellKnownClasses.add(SqlString.class);
	}

	// The string of Timestamp format
	private static final String timestampFormatString = "yyyy-MM-dd HH:mm:ss.SSS";

	// The source data type
	private final Class<ST> sourceType;

	// The destination data type
	private final Class<DT> destinType;

	// The function for converting
	private final Function<ST, DT> function;

	// The key when stored in the map
	private final String key;

	// The hash code of this object
	private final int hashCode;

	/**
		Creates and returns a key of the map from the destination and source data type.

		@param sourceType the source data type
		@param destinType the destination data type

		@return the key

		@throws NullPointerException <b>sourceType</b> or <b>destinType</b> is null
	*/
	public static String key(Class<?> sourceType, Class<?> destinType) {
		if (sourceType == null) throw new NullPointerException("TypeConverter.key: sourceType == null");
		if (destinType == null) throw new NullPointerException("TypeConverter.key: destinType == null");

		sourceType = Utils.toClassType(sourceType);
		destinType = Utils.toClassType(destinType);

		String sourceTypeName = wellKnownClasses.contains(sourceType)
			? sourceType.getSimpleName() : sourceType.getCanonicalName();
		String destinTypeName = wellKnownClasses.contains(destinType)
			? destinType.getSimpleName() : destinType.getCanonicalName();

		String key = sourceTypeName + "->" + destinTypeName;
		return key;
	}

	/**
		Puts all the <b>typeConverters</b> in the <b>typeConverterMap</b>.

		@param typeConverterMap the <b>TypeConverter</b> map
		@param typeConverters an array of <b>TypeConverter</b> objects

		@throws NullPointerException <b>typeConverterMap</b>, <b>typeConverters</b> or any of <b>typeConverters</b> is null
	*/
	public static void put(Map<String, TypeConverter<?, ?>> typeConverterMap, TypeConverter<?, ?>... typeConverters) {
		if (typeConverterMap == null) throw new NullPointerException("TypeConverter.put: typeConverterMap == null");
		if (typeConverters == null) throw new NullPointerException("TypeConverter.put: typeConverters == null");

		Arrays.stream(typeConverters).forEach(typeConverter -> {
			if (typeConverter == null) throw new NullPointerException("TypeConverter.put: typeConverters[...] == null");

			boolean overwrite = typeConverterMap.containsKey(typeConverter.key);

			typeConverterMap.put(typeConverter.key, typeConverter);

			logger.debug(() -> "TypeConverter.put: " + typeConverter + (overwrite ? " (overwrite)" : ""));
		});
	}

	/**
		Finds and returns a <b>TypeConverter</b>
		to convert <b>sourceType</b> to <b>destinType</b> in <b>typeConverterMap</b>.<br>

		If can not find <b>TypeConverter</b> to match with <b>sourceType</b> and <b>destinType</b>,
		finds a <b>TypeConverter</b> to match
		with interfaces of <b>sourceType</b> and <b>destinType</b>.<br>

		If still can not find,
		finds a <b>TypeConverter</b> to match
		with super classes of <b>sourceType</b> and <b>destinType</b>.<br>

		If still can not find, returns <b>null</b><br>.

		If found with in the super class or interface,
		puts them in <b>typeConverterMap</b> to be found directly next time.

		@param <ST> the source data type
		@param <DT> the destination data type

		@param typeConverterMap the <b>TypeConverter</b> map
		@param sourceType the source data type
		@param destinType the destination data type

		@return a <b>TypeConverter</b>

		@throws NullPointerException <b>typeConverterMap</b>, s<b>ourceType</b> or <b>destinType</b> is <b>null</b>
	*/
	@SuppressWarnings("unchecked")
	public static <ST, DT> TypeConverter<ST, DT> get(Map<String, TypeConverter<?, ?>> typeConverterMap,
			Class<ST> sourceType, Class<DT> destinType) {
		if (typeConverterMap == null) throw new NullPointerException("TypeConverter.put: typeConverterMap == null");

		String key = TypeConverter.key(sourceType, destinType);
		TypeConverter<ST, DT> typeConverter = (TypeConverter<ST, DT>)typeConverterMap.get(key);

		if (typeConverter == null) {
			// can not find
			TypeConverter<ST, DT> typeConverter2 = search(typeConverterMap, sourceType, destinType);

			if (typeConverter2 != null) {
				// found
				TypeConverter<ST, DT> typeConverter3 = new TypeConverter<>(sourceType, destinType, typeConverter2.function());
				typeConverterMap.put(key, typeConverter3);

				logger.info(() -> "TypeConverter.put: " + typeConverter3 + " (key: " + key + ")");

				typeConverter = typeConverter3;
			}
		}

		if (typeConverter == null) {
			logger.error("TypeConverter.get: search("+ TypeConverter.key(sourceType, destinType) + ") = null"
				+ ", sourceType: " + sourceType.getCanonicalName()
				+ ", destinType: " + destinType.getCanonicalName()
				);
		}

		return typeConverter;
	}

	/**
		Finds and returns a <b>TypeConverter</b>
		to convert <b>sourceType</b> to <b>destinType</b> in <b>typeConverterMap</b>.<br>

		If can not find <b>TypeConverter</b> to match with <b>sourceType</b> and <b>destinType</b>,
		finds a <b>TypeConverter</b> to match
		with interfaces of <b>sourceType</b> and <b>destinType</b>.<br>

		If still can not find,
		finds a <b>TypeConverter</b> to match
		with super classes of <b>sourceType</b> and <b>destinType</b>.<br>

		If still can not find, returns <b>null</b><br>.

		@param <ST> the source data type
		@param <DT> the destination data type

		@param typeConverterMap the <b>TypeConverter</b> map
		@param sourceType the source data type
		@param destinType the destination data type

		@return a <b>TypeConverter</b>

		@throws NullPointerException <b>typeConverterMap</b>, s<b>ourceType</b> or <b>destinType</b> is <b>null</b>
	*/
	@SuppressWarnings("unchecked")
	private static <ST, DT> TypeConverter<ST, DT> search(
			Map<String, TypeConverter<?, ?>> typeConverterMap,
			Class<? super ST> sourceType, Class<? extends DT> destinType) {
		logger.debug(() ->
			"TypeConverter.search: sourceType: " + Utils.toLogString(sourceType)
			+ ", destinType: " + Utils.toLogString(destinType));

		String key = TypeConverter.key(sourceType, destinType);
		TypeConverter<ST, DT> typeConverter = (TypeConverter<ST, DT>)typeConverterMap.get(key);

		if (typeConverter == null) {
			// can not find
			// trys with interfaces of the source class
			Class<? super ST>[] sourceInterfaces = (Class<? super ST>[])sourceType.getInterfaces();
			for (Class<? super ST> sourceInterface : sourceInterfaces) {
				typeConverter = search(typeConverterMap, sourceInterface, destinType);
				if (typeConverter != null)
					break;
			}
		}

		if (typeConverter == null) {
			// can not find
			// trys with super classes of the source class
			Class<? super ST> sourceSuperType = sourceType.getSuperclass();
			if (sourceSuperType != null)
				typeConverter = search(typeConverterMap, sourceSuperType, destinType);
		}

		return typeConverter;
	}

	/**
		If <b>source == null</b>, returns <b>null</b><br>

		Otherwise if <b>destinType.isInstance(source)</b>,
		returns <b>source</b> without converting.

		Otherwise if found a <b>TypeConverter</b>,
		returns an object converted the source by the converter.

		@param <ST> the source data type
		@param <DT> the destination data type

		@param typeConverterMap the <b>TypeConverter</b> map
		@param source a source object (permit <b>null</b>)
		@param destinType a destination type (other than primitive types)

		@return a converted object (might be <b>null</b>)

		@throws NullPointerException if <b>typeConverterMap</b> or <b>destinType</b> is <b>null</b>
		@throws ConvertException if can not find the converter or the accuracy is lowered in the conversion
	*/
	@SuppressWarnings("unchecked")
	public static <ST, DT> DT convert(Map<String, TypeConverter<?, ?>> typeConverterMap,
			ST source, Class<DT> destinType) {
		if (logger.isDebugEnabled()) {
			logger.debug("TypeConverter.convert:");
			logger.debug("    source: " + Utils.toLogString(source)
				+ ", destinType: " + Utils.toLogString(destinType));
		}

		DT destin = null;
		if (source != null) {
			if (destinType.isInstance(source)) {
				destin = destinType.cast(source);
			} else {
				Class<ST> sourceType  = (Class<ST>)source.getClass();

				TypeConverter<ST, DT> typeConverter = get(typeConverterMap, sourceType, destinType);

				if (typeConverter == null)
					throw new ConvertException(sourceType, source, destinType);

				destin = typeConverter.apply(source);
				logger.debug(() -> "    typeConverter: " + typeConverter);
			}
		}

		DT destinForLog = destin;
		logger.debug(() -> "    result: " + Utils.toLogString(destinForLog));

		return destin;
	}

	/**
		Constructs a new <b>TypeConverter</b>.

		@param sourceType the source data type
		@param destinType the destination data type
		@param function the function for converting

		@throws NullPointerException if <b>sourceType</b>, <b>destinType</b> or <b>function</b> is <b>null</b>
	*/
	public TypeConverter(Class<ST> sourceType, Class<DT> destinType, Function<ST, DT> function) {
		if (function == null) throw new NullPointerException("TypeConverter.<init>: function == null");

		this.sourceType = sourceType;
		this.destinType = destinType;
		this.function = function;
		key = key(sourceType, destinType);
		hashCode = key.hashCode();
	}

	/**
		Returns the source data type.

		@return the source data type
	*/
	public Class<ST> sourceType() {
		return sourceType;
	}

	/**
		Returns the destination data type.

		@return the destination data type
	*/
	public Class<DT> destinType() {
		return destinType;
	}

	/**
		Returns the function for converting.

		@return the function for converting
	*/
	public Function<ST, DT> function() {
		return function;
	}

	/**
		Returns the key.

		@return the key
	*/
	public String key() {
		return key;
	}

	/**
		Convers the data type of the value.

		@param value a source object

		@return a converted object
	*/
	public DT apply(ST value) {
		return function.apply(value);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean equals(Object object) {
		return object instanceof TypeConverter
			&& sourceType == ((TypeConverter<?, ?>)object).sourceType
			&& destinType == ((TypeConverter<?, ?>)object).destinType;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public int hashCode() {
		return hashCode;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public String toString() {
		return key;
	}

	/**
		A <b>TypeConverter</b> map
		that is used in the conversion of when storing values retrieved from the database in the field.<br>
	*/
	public static final Map<String, TypeConverter<?, ?>> typeConverterMap = new LinkedHashMap<>();
	static {
	// * -> Boolean
		// Byte -> Boolean
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Byte.class, Boolean.class, object -> {
				byte value = object;
				if (value == (byte)0) return false;
				if (value == (byte)1) return true;
				throw new ConvertException(Byte.class, object, Boolean.class);
			})
		);

		// Short -> Boolean
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Short.class, Boolean.class, object -> {
				short value = object;
				if (value == (short)0) return false;
				if (value == (short)1) return true;
				throw new ConvertException(Short.class, object, Boolean.class);
			})
		);

		// Integer -> Boolean
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Integer.class, Boolean.class, object -> {
				int value = object;
				if (value == 0) return false;
				if (value == 1) return true;
				throw new ConvertException(Integer.class, object, Boolean.class);
			})
		);

		// Long -> Boolean
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Long.class, Boolean.class, object -> {
				long value = object;
				if (value == 0L) return false;
				if (value == 1L) return true;
				throw new ConvertException(Long.class, object, Boolean.class);
			})
		);

		// Float -> Boolean
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Float.class, Boolean.class, object -> {
				float value = object;
				if (value == 0.0F) return false;
				if (value == 1.0F) return true;
				throw new ConvertException(Float.class, object, Boolean.class);
			})
		);

		// Double -> Boolean
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Double.class, Boolean.class, object -> {
				double value = object;
				if (value == 0.0D) return false;
				if (value == 1.0D) return true;
				throw new ConvertException(Double.class, object, Boolean.class);
			})
		);

		// BigDecimal -> Boolean
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigDecimal.class, Boolean.class, object -> {
				BigDecimal bigDecimal = object;
				if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) return false;
				if (bigDecimal.compareTo(BigDecimal.ONE ) == 0) return true;
				throw new ConvertException(BigDecimal.class, object, Boolean.class);
			})
		);

		// Character -> Boolean
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Character.class, Boolean.class, object -> {
				char value = object;
				if (value == '0') return false;
				if (value == '1') return true;
				throw new ConvertException(Character.class, object, Boolean.class);
			})
		);

		// String -> Boolean
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, Boolean.class, object -> {
				if ("0".equals(object)) return false;
				if ("1".equals(object)) return true;
				throw new ConvertException(String.class, object, Boolean.class);
			})
		);

	// * -> Byte
		// Boolean -> Byte
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Boolean.class, Byte.class, object -> object ? (byte)1 : (byte)0)
		);

		// Short -> Byte
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Short.class, Byte.class, object -> {
				short value = object;
				if ((short)(byte)value != value)
					throw new ConvertException(Short.class, object, Byte.class, (byte)value);
				return (byte)value;
			})
		);

		// Integer -> Byte
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Integer.class, Byte.class, object -> {
				int value = object;
				if ((int)(byte)value != value)
					throw new ConvertException(Integer.class, object, Byte.class, (byte)value);
				return (byte)value;
			})
		);

		// Long -> Byte
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Long.class, Byte.class, object -> {
				long value = object;
				if ((long)(byte)value != value)
					throw new ConvertException(Long.class, object, Byte.class, (byte)value);
				return (byte)value;
			})
		);

		// Float -> Byte
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Float.class, Byte.class, object -> {
				float value = object;
				if ((float)(byte)value != value)
					throw new ConvertException(Float.class, object, Byte.class, (byte)value);
				return (byte)value;
			})
		);

		// Double -> Byte
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Double.class, Byte.class, object -> {
				double value = object;
				if ((double)(byte)value != value)
					throw new ConvertException(Double.class, object, Byte.class, (byte)value);
				return (byte)value;
			})
		);

		// BigDecimal -> Byte
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigDecimal.class, Byte.class, object -> {
				try {
					return object.byteValueExact();
				}
				catch (ArithmeticException e) {
					throw new ConvertException(BigDecimal.class, object, Byte.class, null, e);
				}
			})
		);

		// Character -> Byte
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Character.class, Byte.class, object -> {
				char value = object;
				if ((char)(byte)value != value)
					throw new ConvertException(Character.class, object, Byte.class, (byte)value);
				return (byte)value;
			})
		);

		// String -> Byte
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, Byte.class, object -> {
				try {
					return Byte.valueOf(object);
				}
				catch (NumberFormatException e) {
					throw new ConvertException(String.class, object, Byte.class, null, e);
				}
			})
		);

	// * -> Short
		// Boolean -> Short
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Boolean.class, Short.class, object -> object ? (short)1 : (short)0)
		);

		// Byte -> Short
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Byte.class, Short.class, object -> (short)(byte)object)
		);

		// Integer -> Short
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Integer.class, Short.class, object -> {
				int value = object;
				if ((int)(short)value != value)
					throw new ConvertException(Integer.class, object, Short.class);
				return (short)value;
			})
		);

		// Long -> Short
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Long.class, Short.class, object -> {
				long value = object;
				if ((long)(short)value != value)
					throw new ConvertException(Long.class, object, Short.class);
				return (short)value;
			})
		);

		// Float -> Short
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Float.class, Short.class, object -> {
				float value = object;
				if ((float)(short)value != value)
					throw new ConvertException(Float.class, object, Short.class);
				return (short)value;
			})
		);

		// Double -> Short
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Double.class, Short.class, object -> {
				double value = object;
				if ((double)(short)value != value)
					throw new ConvertException(Double.class, object, Short.class);
				return (short)value;
			})
		);

		// BigDecimal -> Short
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigDecimal.class, Short.class, object -> {
				try {
					return object.shortValueExact();
				}
				catch (ArithmeticException e) {
					throw new ConvertException(BigDecimal.class, object, Short.class, null, e);
				}
			})
		);

		// Character -> Short
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Character.class, Short.class, object -> (short)(char)object)
		);

		// String -> Short
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, Short.class, object -> {
				try {
					return Short.valueOf(object);
				}
				catch (NumberFormatException e) {
					throw new ConvertException(String.class, object, Short.class, null, e);
				}
			})
		);

	// * -> Integer
		// Boolean -> Integer
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Boolean.class, Integer.class, object -> object ? 1 : 0)
		);

		// Byte -> Integer
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Byte.class, Integer.class, object -> (int)(byte)object)
		);

		// Short -> Integer
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Short.class, Integer.class, object -> (int)(short)object)
		);

		// Long -> Integer
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Long.class, Integer.class, object -> {
				long value = object;
				if ((long)(int)value != value)
					throw new ConvertException(Long.class, object, Integer.class, (int)value);
				return (int)value;
			})
		);

		// Float -> Integer
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Float.class, Integer.class, object -> {
				float value = object;
				if ((float)(int)value != value)
					throw new ConvertException(Float.class, object, Integer.class, (int)value);
				return (int)value;
			})
		);

		// Double -> Integer
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Double.class, Integer.class, object -> {
				double value = object;
				if ((double)(int)value != value)
					throw new ConvertException(Double.class, object, Integer.class, (int)value);
				return (int)value;
			})
		);

		// BigDecimal -> Integer
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigDecimal.class, Integer.class, object -> {
				try {
					return object.intValueExact();
				}
				catch (ArithmeticException e) {
					throw new ConvertException(BigDecimal.class, object, Integer.class, null, e);
				}
			})
		);

		// Character -> Integer
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Character.class, Integer.class, object -> (int)(char)object)
		);

		// String -> Integer
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, Integer.class, object -> {
				try {
					return Integer.valueOf(object);
				}
				catch (NumberFormatException e) {
					throw new ConvertException(String.class, object, Integer.class, null, e);
				}
			})
		);

	// * -> Long
		// Boolean -> Long
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Boolean.class, Long.class, object -> object ? 1L : 0L)
		);

		// Byte -> Long
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Byte.class, Long.class, object -> (long)(byte)object)
		);

		// Short -> Long
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Short.class, Long.class, object -> (long)(short)object)
		);

		// Integer -> Long
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Integer.class, Long.class, object -> (long)(int)object)
		);

		// Float -> Long
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Float.class, Long.class, object -> {
				float value = object;
				if ((float)(long)value != value)
					throw new ConvertException(Float.class, object, Long.class, (long)value);
				return (long)value;
			})
		);

		// Double -> Long
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Double.class, Long.class, object -> {
				double value = object;
				if ((double)(long)value != value)
					throw new ConvertException(Double.class, object, Long.class, (long)value);
				return (long)value;
			})
		);

		// BigDecimal -> Long
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigDecimal.class, Long.class, object -> {
				try {
					return object.longValueExact();
				}
				catch (ArithmeticException e) {
					throw new ConvertException(BigDecimal.class, object, Long.class, null, e);
				}
			})
		);

		// Character -> Long
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Character.class, Long.class, object -> (long)(char)object)
		);

		// String -> Long
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, Long.class, object -> {
				try {
					return Long.valueOf(object);
				}
				catch (NumberFormatException e) {
					throw new ConvertException(String.class, object, Long.class, null, e);
				}
			})
		);

	// * -> Float
		// Boolean -> Float
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Boolean.class, Float.class, object -> object ? 1.0F : 0.0F)
		);

		// Byte -> Float
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Byte.class, Float.class, object -> (float)(byte)object)
		);

		// Short -> Float
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Short.class, Float.class, object -> (float)(short)object)
		);

		// Integer -> Float
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Integer.class, Float.class, object -> (float)(int)object)
		);

		// Long -> Float
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Long.class, Float.class, object -> (float)(long)object)
		);

		// Double -> Float
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Double.class, Float.class, object -> (float)(double)object)
		);

		// BigDecimal -> Float
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigDecimal.class, Float.class, object -> object.floatValue())
		);

		// Character -> Float
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Character.class, Float.class, object -> (float)(char)object)
		);

		// String -> Float
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, Float.class, object -> {
				try {
					return Float.valueOf(object);
				}
				catch (NumberFormatException e) {
					throw new ConvertException(String.class, object, Float.class, null, e);
				}
			})
		);

	// * -> Double
		// Boolean -> Double
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Boolean.class, Double.class, object -> object ? 1.0 : 0.0)
		);

		// Byte -> Double
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Byte.class, Double.class, object -> (double)(byte)object)
		);

		// Short -> Double
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Short.class, Double.class, object -> (double)(short)object)
		);

		// Integer -> Double
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Integer.class, Double.class, object -> (double)(int)object)
		);

		// Long -> Double
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Long.class, Double.class, object -> (double)(long)object)
		);

		// Float -> Double
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Float.class, Double.class, object -> (double)(float)object)
		);

		// BigDecimal -> Double
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigDecimal.class, Double.class, object -> object.doubleValue())
		);

		// Character -> Double
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Character.class, Double.class, object -> (double)(char)object)
		);

		// String -> Double
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, Double.class, object -> {
				try {
					return Double.valueOf(object);
				}
				catch (NumberFormatException e) {
					throw new ConvertException(String.class, object, Double.class, null, e);
				}
			})
		);

	// * -> BigDecimal
		// Boolean -> BigDecimal
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Boolean.class, BigDecimal.class, object -> object ? BigDecimal.ONE : BigDecimal.ZERO)
		);

		// Byte -> BigDecimal
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Byte.class, BigDecimal.class, object -> BigDecimal.valueOf((long)(byte)object))
		);

		// Short -> BigDecimal
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Short.class, BigDecimal.class, object -> BigDecimal.valueOf((long)(short)object))
		);

		// Integer -> BigDecimal
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Integer.class, BigDecimal.class, object -> BigDecimal.valueOf((long)(int)object))
		);

		// Long -> BigDecimal
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Long.class, BigDecimal.class, object -> BigDecimal.valueOf(object))
		);

		// Float -> BigDecimal
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Float.class, BigDecimal.class, object -> BigDecimal.valueOf((double)(float)object))
		);

		// Double -> BigDecimal
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Double.class, BigDecimal.class, object -> BigDecimal.valueOf(object))
		);

		// Character -> BigDecimal
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Character.class, BigDecimal.class, object -> BigDecimal.valueOf((long)(char)object))
		);

		// String -> BigDecimal
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, BigDecimal.class, object -> {
				try {
					return new BigDecimal(object);
				} catch (NumberFormatException e) {
					throw new ConvertException(String.class, object, BigDecimal.class, null, e);
				}
			})
		);

	// * -> Character
		// Boolean -> Character
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Boolean.class, Character.class, object -> object ? '1' : '0')
		);

		// Byte -> Character
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Byte.class, Character.class, object -> (char)(byte)object)
		);

		// Short -> Character
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Short.class, Character.class, object -> (char)(short)object)
		);

		// Integer -> Character
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Integer.class, Character.class, object -> {
				int value = object;
				if ((int)(char)value != value)
					throw new ConvertException(Integer.class, object, Character.class, (char)value);
				return (char)value;
			})
		);

		// Long -> Character
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Long.class, Character.class, object -> {
				long value = object;
				if ((long)(char)value != value)
					throw new ConvertException(Long.class, object, Character.class, (char)value);
				return (char)value;
			})
		);

		// Float -> Character
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Float.class, Character.class, object -> {
				float value = object;
				if ((float)(char)value != value)
					throw new ConvertException(Float.class, object, Character.class, (char)value);
				return (char)value;
			})
		);

		// Double -> Character
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Double.class, Character.class, object -> {
				double value = object;
				if ((double)(char)value != value)
					throw new ConvertException(Double.class, object, Character.class, (char)value);
				return (char)value;
			})
		);

		// BigDecimal -> Character
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigDecimal.class, Character.class, object -> {
				try {
					int value = object.intValueExact();
					if (value < 0 || value > (int)'\uFFFF')
						throw new ConvertException(BigDecimal.class, object, Character.class);
					return (char)value;
				}
				catch (ArithmeticException e) {
					throw new ConvertException(BigDecimal.class, object, Character.class, null, e);
				}
			})
		);

		// String -> Character
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, Character.class, object -> {
				if (object.length() != 1)
					throw new ConvertException(String.class, object, Character.class);
				return object.charAt(0);
			})
		);

	// * -> String
		// Object -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Object.class, String.class, object -> object.toString())
		);

		// BigDecimal -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigDecimal.class, String.class, object -> object.toPlainString())
		);

		// Timestamp -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Timestamp.class, String.class, object ->
				new SimpleDateFormat(timestampFormatString).format(object))
		);

	// * -> Date
		// Long -> Date
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Long.class, Date.class, object -> new Date(object))
		);

		// java.util.Date -> java.sql.Date
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.util.Date.class, Date.class, object -> new Date(object.getTime()))
		);

		// String -> Date
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, Date.class, object -> {
				try {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					return new Date(format.parse(object).getTime());
				}
				catch (ParseException e) {
					throw new ConvertException(String.class, object, Date.class, e);
				}
			})
		);

	// * -> Time
		// Long -> Time
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Long.class, Time.class, object -> new Time(object))
		);

		// java.util.Date -> Time
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.util.Date.class, Time.class, object -> new Time(object.getTime()))
		);

		// String -> Time
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, Time.class, object -> {
				try {
					SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
					return new Time(format.parse(object).getTime());
				}
				catch (ParseException e) {
					throw new ConvertException(String.class, object, Time.class, e);
				}
			})
		);

	// * -> Timestamp
		// Long -> Timestamp
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Long.class, Timestamp.class, object -> new Timestamp(object))
		);

		// java.util.Date -> Timestamp
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.util.Date.class, Timestamp.class, object -> new Timestamp(object.getTime()))
		);

		// String -> Timestamp
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, Timestamp.class, object -> {
				try {
					return new Timestamp(new SimpleDateFormat(timestampFormatString).parse(object).getTime());
				}
				catch (ParseException e) {
					throw new ConvertException(String.class, object, Timestamp.class, e);
				}
			})
		);

	// Enum -> *

		// Enum -> Integer (since 1.4.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Enum.class, Integer.class, object -> object.ordinal())
		);

		// Enum -> Byte (since 1.4.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Enum.class, Byte.class,
				TypeConverter.get(typeConverterMap, Enum.class, Integer.class).function()
				.andThen(TypeConverter.get(typeConverterMap, Integer.class, Byte.class).function())
			)
		);

		// Enum -> Short (since 1.4.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Enum.class, Short.class,
				TypeConverter.get(typeConverterMap, Enum.class, Integer.class).function()
				.andThen(TypeConverter.get(typeConverterMap, Integer.class, Short.class).function())
			)
		);

		// Enum -> Long (since 1.4.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Enum.class, Long.class,
				TypeConverter.get(typeConverterMap, Enum.class, Integer.class).function()
				.andThen(TypeConverter.get(typeConverterMap, Integer.class, Long.class).function())
			)
		);

	}
}
