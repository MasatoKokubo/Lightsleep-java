/*
	TypeConverter.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.helper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.mkokubo.lightsleep.logger.Logger;
import org.mkokubo.lightsleep.logger.LoggerFactory;

/**
	A class to convert data types.<br>

	Following <b>TypeConverter</b> objects has been registered in <b>typeConverterMap</b>.<br>

	<table class="additinal">
		<caption>TypeConverter objects that are registered</caption>
		<tr><th>Source Data Type</th><th>Destination Data Type</th></tr>

		<tr><td>Byte             </td><td rowspan="10">Boolean</td></tr>
		<tr><td>Short            </td></tr>
		<tr><td>Integer          </td></tr>
		<tr><td>Long             </td></tr>
		<tr><td>Float            </td></tr>
		<tr><td>Double           </td></tr>
		<tr><td>BigInteger       </td></tr>
		<tr><td>BigDecimal       </td></tr>
		<tr><td>Character        </td></tr>
		<tr><td>String           </td></tr>

		<tr><td>Boolean          </td><td rowspan="10">Byte</td></tr>
		<tr><td>Short            </td></tr>
		<tr><td>Integer          </td></tr>
		<tr><td>Long             </td></tr>
		<tr><td>Float            </td></tr>
		<tr><td>Double           </td></tr>
		<tr><td>BigInteger       </td></tr>
		<tr><td>BigDecimal       </td></tr>
		<tr><td>Character        </td></tr>
		<tr><td>String           </td></tr>

		<tr><td>Boolean          </td><td rowspan="10">Short</td></tr>
		<tr><td>Short            </td></tr>
		<tr><td>Integer          </td></tr>
		<tr><td>Long             </td></tr>
		<tr><td>Float            </td></tr>
		<tr><td>Double           </td></tr>
		<tr><td>BigInteger       </td></tr>
		<tr><td>BigDecimal       </td></tr>
		<tr><td>Character        </td></tr>
		<tr><td>String           </td></tr>

		<tr><td>Boolean          </td><td rowspan="10">Integer</td></tr>
		<tr><td>Short            </td></tr>
		<tr><td>Integer          </td></tr>
		<tr><td>Long             </td></tr>
		<tr><td>Float            </td></tr>
		<tr><td>Double           </td></tr>
		<tr><td>BigInteger       </td></tr>
		<tr><td>BigDecimal       </td></tr>
		<tr><td>Character        </td></tr>
		<tr><td>String           </td></tr>

		<tr><td>Boolean          </td><td rowspan="10">Long</td></tr>
		<tr><td>Short            </td></tr>
		<tr><td>Integer          </td></tr>
		<tr><td>Long             </td></tr>
		<tr><td>Float            </td></tr>
		<tr><td>Double           </td></tr>
		<tr><td>BigInteger       </td></tr>
		<tr><td>BigDecimal       </td></tr>
		<tr><td>Character        </td></tr>
		<tr><td>String           </td></tr>

		<tr><td>Boolean          </td><td rowspan="10">Float</td></tr>
		<tr><td>Short            </td></tr>
		<tr><td>Integer          </td></tr>
		<tr><td>Long             </td></tr>
		<tr><td>Float            </td></tr>
		<tr><td>Double           </td></tr>
		<tr><td>BigInteger       </td></tr>
		<tr><td>BigDecimal       </td></tr>
		<tr><td>Character        </td></tr>
		<tr><td>String           </td></tr>

		<tr><td>Boolean          </td><td rowspan="10">Double</td></tr>
		<tr><td>Short            </td></tr>
		<tr><td>Integer          </td></tr>
		<tr><td>Long             </td></tr>
		<tr><td>Float            </td></tr>
		<tr><td>Double           </td></tr>
		<tr><td>BigInteger       </td></tr>
		<tr><td>BigDecimal       </td></tr>
		<tr><td>Character        </td></tr>
		<tr><td>String           </td></tr>

		<tr><td>Boolean          </td><td rowspan="10">BigInteger</td></tr>
		<tr><td>Short            </td></tr>
		<tr><td>Integer          </td></tr>
		<tr><td>Long             </td></tr>
		<tr><td>Float            </td></tr>
		<tr><td>Double           </td></tr>
		<tr><td>BigInteger       </td></tr>
		<tr><td>BigDecimal       </td></tr>
		<tr><td>Character        </td></tr>
		<tr><td>String           </td></tr>

		<tr><td>Boolean          </td><td rowspan="10">BigDecimal</td></tr>
		<tr><td>Short            </td></tr>
		<tr><td>Integer          </td></tr>
		<tr><td>Long             </td></tr>
		<tr><td>Float            </td></tr>
		<tr><td>Double           </td></tr>
		<tr><td>BigInteger       </td></tr>
		<tr><td>BigDecimal       </td></tr>
		<tr><td>Character        </td></tr>
		<tr><td>String           </td></tr>

		<tr><td>Boolean          </td><td rowspan="10">Character</td></tr>
		<tr><td>Short            </td></tr>
		<tr><td>Integer          </td></tr>
		<tr><td>Long             </td></tr>
		<tr><td>Float            </td></tr>
		<tr><td>Double           </td></tr>
		<tr><td>BigInteger       </td></tr>
		<tr><td>BigDecimal       </td></tr>
		<tr><td>Character        </td></tr>
		<tr><td>String           </td></tr>

		<tr><td>Object           </td><td rowspan="16">String</td></tr>
		<tr><td>Boolean          </td></tr>
		<tr><td>Byte             </td></tr>
		<tr><td>Short            </td></tr>
		<tr><td>Integer          </td></tr>
		<tr><td>Long             </td></tr>
		<tr><td>Float            </td></tr>
		<tr><td>Double           </td></tr>
		<tr><td>BigInteger       </td></tr>
		<tr><td>BigDecimal       </td></tr>
		<tr><td>Character        </td></tr>
		<tr><td>java.sql.Date    </td></tr>
		<tr><td>Time             </td></tr>
		<tr><td>Timestamp        </td></tr>
		<tr><td>Clob             </td></tr>
		<tr><td>Enum             </td></tr>

		<tr><td>Blob             </td><td>byte[]</td></tr>

		<tr><td>Long             </td><td rowspan="4">java.sql.Date</td></tr>
		<tr><td>Time             </td></tr>
		<tr><td>Timestamp        </td></tr>
		<tr><td>String           </td></tr>

		<tr><td>Long             </td><td rowspan="4">Time</td></tr>
		<tr><td>java.sql.Date    </td></tr>
		<tr><td>Timestamp        </td></tr>
		<tr><td>String           </td></tr>

		<tr><td>Long             </td><td rowspan="4">Timestamp</td></tr>
		<tr><td>java.sql.Date    </td></tr>
		<tr><td>Time             </td></tr>
		<tr><td>String           </td></tr>
	</table>

	@see org.mkokubo.lightsleep.database.Standard
	@see org.mkokubo.lightsleep.database.MySQL
	@see org.mkokubo.lightsleep.database.Oracle
	@see org.mkokubo.lightsleep.database.PostgreSQL
	@see org.mkokubo.lightsleep.database.SQLServer

	@since 1.0
	@author Masato Kokubo
*/
public class TypeConverter<ST, DT> {
	// The logger
	private static final Logger logger = LoggerFactory.getLogger(TypeConverter.class);

	// The source data type
	private final Class<ST> sourceType;

	// The destination data type
	private final Class<DT> destinType;

	// The function for converting
	private final Function<? super ST, ? extends DT> function;

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

		String key = sourceType.getCanonicalName() + "->" + destinType.getCanonicalName();
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

			logger.info(() -> "TypeConverter.put: " + typeConverter + (overwrite ? " (overwrite)" : ""));
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
			TypeConverter<? super ST, DT> typeConverter2 = search(typeConverterMap, sourceType, destinType);

			if (typeConverter2 != null) {
				// found
				TypeConverter<ST, DT> typeConverter3= new TypeConverter<>(sourceType, destinType, typeConverter2.function());
				typeConverterMap.put(key, typeConverter3);

				logger.info(() -> "TypeConverter.put: " + typeConverter3 + " (key: " + key + ")");

				typeConverter = typeConverter3;
			}
		}

		if (typeConverter == null) {
			logger.error("TypeConverter.get: search("+ TypeConverter.key(sourceType, destinType) + ") = null"
				+ ", sourceType: " + sourceType.getName()
				+ ", destinType: " + destinType.getName()
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
	private static <ST, DT> TypeConverter<? super ST, DT> search(
			Map<String, TypeConverter<?, ?>> typeConverterMap,
			Class<ST> sourceType, Class<DT> destinType) {
		logger.debug(() ->
			"TypeConverter.search: sourceType: " + Utils.toLogString(sourceType)
			+ ", destinType: " + Utils.toLogString(destinType));

		String key = TypeConverter.key(sourceType, destinType);
		TypeConverter<? super ST, DT> typeConverter = (TypeConverter<? super ST, DT>)typeConverterMap.get(key);

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
	public TypeConverter(Class<ST> sourceType, Class<DT> destinType, Function<? super ST, ? extends DT> function) {
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
	public Function<? super ST, ? extends DT> function() {
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

	// BigInteger.valueOf(Byte.MIN_VALUE)
	private static final BigInteger bigIntegerByteMin = BigInteger.valueOf(Byte.MIN_VALUE);

	// BigInteger.valueOf(Byte.MAX_VALUE)
	private static final BigInteger bigIntegerByteMax = BigInteger.valueOf(Byte.MAX_VALUE);

	// BigInteger.valueOf(Short.MIN_VALUE)
	private static final BigInteger bigIntegerShortMin = BigInteger.valueOf(Short.MIN_VALUE);

	// BigInteger.valueOf(Short.MAX_VALUE)
	private static final BigInteger bigIntegerShortMax = BigInteger.valueOf(Short.MAX_VALUE);

	// BigInteger.valueOf(Integer.MIN_VALUE)
	private static final BigInteger bigIntegerIntegerMin = BigInteger.valueOf(Integer.MIN_VALUE);

	// BigInteger.valueOf(Integer.MAX_VALUE)
	private static final BigInteger bigIntegerIntegerMax = BigInteger.valueOf(Integer.MAX_VALUE);

	// BigInteger.valueOf(Long.MIN_VALUE)
	private static final BigInteger bigIntegerLongMin = BigInteger.valueOf(Long.MIN_VALUE);

	// BigInteger.valueOf(Long.MAX_VALUE)
	private static final BigInteger bigIntegerLongMax = BigInteger.valueOf(Long.MAX_VALUE);

	// BigInteger.valueOf(Character.MIN_VALUE)
	private static final BigInteger bigIntegerCharacterMin = BigInteger.valueOf(Character.MIN_VALUE);

	// BigInteger.valueOf(Character.MAX_VALUE)
	private static final BigInteger bigIntegerCharacterMax = BigInteger.valueOf(Character.MAX_VALUE);

	private static final String timestampFormatString = "yyyy-MM-dd HH:mm:ss.SSS";

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

		// BigInteger -> Boolean
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigInteger.class, Boolean.class, object -> {
				BigInteger bigInteger = object;
				if (bigInteger.compareTo(BigInteger.ZERO) == 0) return false;
				if (bigInteger.compareTo(BigInteger.ONE ) == 0) return true;
				throw new ConvertException(BigInteger.class, object, Boolean.class);
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

		// BigInteger -> Byte
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigInteger.class, Byte.class, object -> {
				if (object.compareTo(bigIntegerByteMin) < 0 || object.compareTo(bigIntegerByteMax) > 0)
					throw new ConvertException(BigInteger.class, object, Byte.class);
				return (byte)object.intValue();
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

		// BigInteger -> Short
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigInteger.class, Short.class, object -> {
				if (object.compareTo(bigIntegerShortMin) < 0 || object.compareTo(bigIntegerShortMax) > 0)
					throw new ConvertException(BigInteger.class, object, Short.class);
				return (short)object.intValue();
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

		// BigInteger -> Integer
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigInteger.class, Integer.class, object -> {
				if (object.compareTo(bigIntegerIntegerMin) < 0 || object.compareTo(bigIntegerIntegerMax) > 0)
					throw new ConvertException(BigInteger.class, object, Integer.class);
				return object.intValue();
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

		// BigInteger -> Long
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigInteger.class, Long.class, object -> {
				if (object.compareTo(bigIntegerLongMin) < 0 || object.compareTo(bigIntegerLongMax) > 0)
					throw new ConvertException(BigInteger.class, object, Long.class);
				return object.longValue();
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

		// BigInteger -> Float
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigInteger.class, Float.class, object -> object.floatValue())
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

		// BigInteger -> Double
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigInteger.class, Double.class, object -> object.doubleValue())
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

	// * -> BigInteger
		// Boolean -> BigInteger
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Boolean.class, BigInteger.class, object -> object ? BigInteger.ONE : BigInteger.ZERO)
		);

		// Byte -> BigInteger
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Byte.class, BigInteger.class, object -> BigInteger.valueOf((long)(byte)object))
		);

		// Short -> BigInteger
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Short.class, BigInteger.class, object -> BigInteger.valueOf((long)(short)object))
		);

		// Integer -> BigInteger
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Integer.class, BigInteger.class, object -> BigInteger.valueOf((long)(int)object))
		);

		// Long -> BigInteger
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Long.class, BigInteger.class, object -> BigInteger.valueOf((long)object))
		);

		// Float -> BigInteger
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Float.class, BigInteger.class, object -> {
				try {
					return new BigInteger(new DecimalFormat("0.#").format((float)object));
				}
				catch (NumberFormatException e) {
					throw new ConvertException(Float.class, object, BigInteger.class, null, e);
				}
			})
		);

		// Double -> BigInteger
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Double.class, BigInteger.class, object -> {
				try {
					return new BigInteger(new DecimalFormat("0.#").format((double)object));
				}
				catch (NumberFormatException e) {
					throw new ConvertException(Double.class, object, BigInteger.class, null, e);
				}
			})
		);

		// BigDecimal -> BigInteger
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigDecimal.class, BigInteger.class, object -> {
				BigInteger bigInteger = object.toBigInteger();
				if (!new BigDecimal(bigInteger).equals(object))
					throw new ConvertException(BigDecimal.class, object, BigInteger.class, bigInteger);
				return bigInteger;
			})
		);

		// Character -> BigInteger
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Character.class, BigInteger.class, object -> BigInteger.valueOf((long)(char)object))
		);

		// String -> BigInteger
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, BigInteger.class, object -> {
				try {
					return new BigInteger(object);
				}
				catch (NumberFormatException e) {
					throw new ConvertException(String.class, object, BigInteger.class, null, e);
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

		// BigInteger -> BigDecimal
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigInteger.class, BigDecimal.class, object -> new BigDecimal(object))
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

		// BigInteger -> Character
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigInteger.class, Character.class, object -> {
				if (object.compareTo(bigIntegerCharacterMin) < 0 || object.compareTo(bigIntegerCharacterMax) > 0)
					throw new ConvertException(BigInteger.class, object, Character.class);
				return (char)object.intValue();
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

		// Boolean -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Boolean.class, String.class, object -> object ? "true" : "false")
		);

		// Byte -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Byte.class, String.class, object -> String.valueOf(object))
		);

		// Short -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Short.class, String.class, object -> String.valueOf(object))
		);

		// Integer -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Integer.class, String.class, object -> String.valueOf(object))
		);

		// Long -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Long.class, String.class, object -> String.valueOf(object))
		);

		// Float -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Float.class, String.class, object -> String.valueOf(object))
		);

		// Double -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Double.class, String.class, object -> String.valueOf(object))
		);

		// BigInteger -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigInteger.class, String.class, object -> object.toString())
		);

		// BigDecimal -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigDecimal.class, String.class, object -> object.toPlainString())
		);

		// Character -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Character.class, String.class, object -> object.toString())
		);

		// Date -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Date.class, String.class, object -> object.toString())
		);

		// Time -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Time.class, String.class, object -> object.toString())
		);

		// Timestamp -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Timestamp.class, String.class, object ->
				new SimpleDateFormat(timestampFormatString).format(object))
		);

		// Clob -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Clob.class, String.class, object -> {
				try {
					long length = object.length();
					if (length > Integer.MAX_VALUE)
						throw new ConvertException(Clob.class, "length=" + length, String.class);
					return object.getSubString(1L, (int)length);
				}
				catch (SQLException e) {
					throw new ConvertException(Clob.class, object, String.class, null, e);
				}
			})
		);

		// Enum -> String
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Enum.class, String.class, object -> object.toString())
		);


	// * -> byte[]
		// Blob -> byte[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Blob.class, byte[].class, object -> {
				try {
					long length = object.length();
					if (length > Integer.MAX_VALUE)
						throw new ConvertException(Blob.class, "length=" + length, byte[].class);
					return object.getBytes(1L, (int)length);
				}
				catch (SQLException e) {
					throw new ConvertException(Blob.class, object, byte[].class, null, e);
				}
			})
		);

	// * -> Date
		// Long -> Date
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Long.class, Date.class, object -> new Date(object))
		);

		// Time -> Date
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Time.class, Date.class, object -> new Date(object.getTime()))
		);

		// Timestamp -> Date
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Timestamp.class, Date.class, object -> new Date(object.getTime()))
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

		// Date -> Time
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Date.class, Time.class, object -> new Time(object.getTime()))
		);

		// Timestamp -> Time
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Timestamp.class, Time.class, object -> new Time(object.getTime()))
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

		// Date -> Timestamp
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Date.class, Timestamp.class, object -> new Timestamp(object.getTime()))
		);

		// Time -> Timestamp
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Time.class, Timestamp.class, object -> new Timestamp(object.getTime()))
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
	}
}
