// Standard.java
// (C) 2016 Masato Kokubo

package org.lightsleep.database;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.lightsleep.Sql;
import org.lightsleep.component.Condition;
import org.lightsleep.component.Expression;
import org.lightsleep.component.SqlString;
import org.lightsleep.helper.ColumnInfo;
import org.lightsleep.helper.ConvertException;
import org.lightsleep.helper.Resource;
import org.lightsleep.helper.TypeConverter;
import org.lightsleep.helper.Utils;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

/**
 * A database handler that does not depend on the particular DBMS.
 *
 * <p>
 * The object of this class has a <b>TypeConverter</b> map
 * with the following additional <b>TypeConverter</b> to
 * {@linkplain org.lightsleep.helper.TypeConverter#typeConverterMap}.
 * </p>
 *
 * <table class="additional">
 *   <caption><span>Additional contents of the TypeConverter map</span></caption>
 *   <tr><th colspan="2">Key: Data Types</th><th rowspan="2">Value: Conversion Function</th></tr>
 *   <tr><th>Source</th><th>Destination</th></tr>
 *
 *   <tr><td>Clob</td>
 *     <td>String</td><td rowspan="2">
 *       <div class="warning">
 *         Throws a <b>ConvertException</b> if the source length exceeds <b>Integer.MAX_VALUE</b>
 *         or <b>SQLException</b> is thrown when getting content.
 *       </div>
 *     </td>
 *   </tr>
 *   <tr><td>Blob</td><td>byte[]</td></tr>
 *   <tr><td rowspan="19">java.sql.Array</td><td>boolean[]</td>
 *     <td rowspan="19">Converts each element of the <b>Array</b> to the element type of the destination using <b>TypeConverter</b>.
 *     </td>
 *   </tr>
 *   <tr><td>byte[]          </td></tr>
 *   <tr><td>short[]         </td></tr>
 *   <tr><td>int[]           </td></tr>
 *   <tr><td>long[]          </td></tr>
 *   <tr><td>float[]         </td></tr>
 *   <tr><td>double[]        </td></tr>
 *   <tr><td>BigDecimal[]    </td></tr>
 *   <tr><td>String[]        </td></tr>
 *   <tr><td>java.util.Date[]</td></tr>
 *   <tr><td>Date[]          </td></tr>
 *   <tr><td>Time[]          </td></tr>
 *   <tr><td>Timestamp[]     </td></tr>
 *   <tr><td>LocalDateTime[] </td></tr>
 *   <tr><td>LocalDate[]     </td></tr>
 *   <tr><td>LocalTime[]     </td></tr>
 *   <tr><td>OffsetDateTime[]</td></tr>
 *   <tr><td>ZonedDateTime[] </td></tr>
 *   <tr><td>Instant[]       </td></tr>
 *   <tr>
 *     <td>
 *       Object<br>
 *       <span class="comment">(Boolean, Byte,<br>Short, Integer,<br>Long, Float,<br>Double, Character<br>, Enum, ...)</span>
 *     </td>
 *     <td rowspan="37">SqlString</td><td><b>new SqlString(source.toString())</b></td>
 *   </tr>
 *   <tr><td>Boolean        </td>
 *     <td>
 *       <b>new SqlString("FALSE")</b> <span class="comment">if the source value is <b>false</b></span><br>
 *       <b>new SqlString("TRUE")</b> <span class="comment">if the source value is <b>true</b></span>
 *     </td>
 *   </tr>
 *   <tr><td>BigDecimal     </td><td><b>new SqlString(object.toPlainString())</b></td></tr>
 *   <tr><td>String         </td>
 *     <td>
 *       <b>new SqlString("'" + source + "'")</b><br>
 *       <span class="comment">Converts a single quote in the source string to two consecutive single quotes
 *       and converts control characters to</span> <b>'...'||CHR(character code)||'...'</b>.<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>new SqlString(SqlString.PARAMETER, source)</b> <span class="comment">if the source string is too long</span>
 *     </td>
 *   </tr>
 *   <tr><td>Character      </td><td><b>Character</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>SqlString</b></td></tr>
 *   <tr><td>java.util.Date </td>
 *     <td rowspan="3">
 *       (<b>java.util.Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Date</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b>) <img src="../../../../images/arrow-right.gif" alt="->"> <b>new SqlString("DATE'" + string + '\'')</b>
 *     </td>
 *   </tr>
 *   <tr><td>Date           </td></tr>
 *   <tr><td>LocalDate      </td></tr>
 *   <tr><td>Time           </td>
 *     <td rowspan="2">
 *       (<b>Time</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>LocalTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b>) <img src="../../../../images/arrow-right.gif" alt="->"> <b>new SqlString("TIME'" + string + '\'')</b>
 *     </td>
 *   </tr>
 *   <tr><td>LocalTime      </td></tr>
 *   <tr><td>Timestamp      </td>
 *     <td rowspan="5">
 *       (<b>Timestamp</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>LocalDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>OffsetDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>ZonedDateTime</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b> or<br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>Instant</b> <img src="../../../../images/arrow-right.gif" alt="->"> <b>String</b>) <img src="../../../../images/arrow-right.gif" alt="->"> <b>new SqlString("TIMESTAMP'" + string + '\'')</b>
 *     </td>
 *   </tr>
 *   <tr><td>LocalDateTime  </td></tr>
 *   <tr><td>OffsetDateTime </td></tr>
 *   <tr><td>ZonedDateTime  </td></tr>
 *   <tr><td>Instant        </td></tr>
 *   <tr><td>byte[]         </td>
 *     <td>
 *       <b>new SqlString("X'" + hexadecimal string + "'")</b><br>
 *       <div class="blankline">&nbsp;</div>
 *       <b>new SqlString(SqlString.PARAMETER, source)</b> <span class="comment">if the source byte array is too long</span>
 *     </td>
 *   </tr>
 *   <tr><td>boolean[]      </td>
 *     <td rowspan="20">
 *       <b>new SqlString("ARRAY[a,b,c,...]")</b><br>
 *       <div class="comment">The <b>a,b,c,...</b> are elements of the source array each converted to <b>SqlString</b> using <b>TypeConverter</b>.</div>
 *     </td>
 *   </tr>
 *   <tr><td>char[]          </td></tr>
 *   <tr><td>byte[][]        </td></tr>
 *   <tr><td>short[]         </td></tr>
 *   <tr><td>int[]           </td></tr>
 *   <tr><td>long[]          </td></tr>
 *   <tr><td>float[]         </td></tr>
 *   <tr><td>double[]        </td></tr>
 *   <tr><td>BigDecimal[]    </td></tr>
 *   <tr><td>String[]        </td></tr>
 *   <tr><td>java.util.Date[]</td></tr>
 *   <tr><td>java.sql.Date[] </td></tr>
 *   <tr><td>Time[]          </td></tr>
 *   <tr><td>Timestamp[]     </td></tr>
 *   <tr><td>LocalDateTime[] </td></tr>
 *   <tr><td>LocalDate[]     </td></tr>
 *   <tr><td>LocalTime[]     </td></tr>
 *   <tr><td>OffsetDateTime[]</td></tr>
 *   <tr><td>ZonedDateTime[] </td></tr>
 *   <tr><td>Instant[]       </td></tr>
 *   <tr><td>Iterable        </td>
 *     <td>
 *       <b>new SqlString("(a,b,c,...)")</b><br>
 *       <div class="comment">The <b>a,b,c,...</b> are elements of the source each converted to <b>SqlString</b> using <b>TypeConverter</b>.</div>
 *     </td>
 *   </tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 * @see org.lightsleep.helper.TypeConverter
 */
public class Standard implements Database {
	// Class Resources
	private static final Resource resource = new Resource(Standard.class);
	private static final String messageSelectSqlWithoutColumns = resource.getString("messageSelectSqlWithoutColumns");

	// The logger
	protected static final Logger logger = LoggerFactory.getLogger(Database.class);

	/**
	 * Maximum length of string literal when generates SQL.
	 *
	 * <p>
	 * If the string literal exceeds this length, it generated as SQL parameters (?).<br>
	 * The value of <b>maxStringLiteralLength</b> of lightsleep.properties has been set.
	 * (if undefined, 128)
	 * </p>
	 */
	public final int maxStringLiteralLength = Resource.getGlobal().getInt("maxStringLiteralLength", 128);

	/**
	 * Maximum length of binary literal when generates SQL.
	 *
	 * <p>
	 * If the binary literal exceeds this length, it generated as SQL parameters (?).<br>
	 * The value of <b>maxBinaryLiteralLength</b> of lightsleep.properties has been set.
	 * (if undefined, 128)
	 * </p>
	 */
	public final int maxBinaryLiteralLength = Resource.getGlobal().getInt("maxBinaryLiteralLength", 128);

	/**
	 * <b>TypeConverter</b> object to convert
	 * from <b>Boolean</b> to <b>SqlString</b> (0 or 1)
	 */
// 3.0.0
//	protected static final TypeConverter<Boolean, SqlString> booleanToSql01Converter =
//		new TypeConverter<>(Boolean.class, SqlString.class, object -> new SqlString(object ? "1" : "0"));
////

	/**
	 * The ASCII characters without controle charactes
	 *
	 * @since 2.2.0
	 */
	protected static final String ASCII_CHARS =
		  " !\"#$%&'()*+,-./"
		+ "0123456789:;<=>?"
		+ "@ABCDEFGHUJKLMNO"
		+ "PQRSTUVWXYZ[\\]^_"
		+ "`abcdefghijklmno"
		+ "pqrstuvwxyz(|)~";

	/**
	 * The pattern string of passwords
	 *
	 * @since 2.2.0
	 */
	protected static final String PASSWORD_MASK = "xxxx";

	/**
	 * The only instance of this class
	 *
	 * @since 2.1.0
	 */
	public static final Standard instance = new Standard();

	/**
	 * <b>TypeConverter</b> map used for the following data type conversion
	 * <ul>
	 *   <li>When generating SQL</li>
	 *   <li>When storing the value obtained by SELECT SQL in the entity</li>
	 * </ul>
	 */
	protected final Map<String, TypeConverter<?, ?>> typeConverterMap = new ConcurrentHashMap<>(TypeConverter.typeConverterMap());

	/**
	 * Constructs a new <b>Standard</b>.
	 */
	@SuppressWarnings("unchecked")
	protected Standard() {
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

	// java.sql.Array -> *[]
		// java.sql.Array -> boolean[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, boolean[].class, object -> toArray(object, boolean[].class, boolean.class))
		);

		// java.sql.Array -> byte[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, byte[].class, object -> toArray(object, byte[].class, byte.class))
		);

		// java.sql.Array -> short[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, short[].class, object -> toArray(object, short[].class, short.class))
		);

		// java.sql.Array -> int[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, int[].class, object -> toArray(object, int[].class, int.class))
		);

		// java.sql.Array -> long[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, long[].class, object -> toArray(object, long[].class, long.class))
		);

		// java.sql.Array -> float[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, float[].class, object -> toArray(object, float[].class, float.class))
		);

		// java.sql.Array -> double[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, double[].class, object -> toArray(object, double[].class, double.class))
		);

		// java.sql.Array -> BigDecimal[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, BigDecimal[].class, object -> toArray(object, BigDecimal[].class, BigDecimal.class))
		);

		// java.sql.Array -> String[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, String[].class, object -> toArray(object, String[].class, String.class))
		);

		// java.sql.Array -> java.util.Date[] (since 1.4.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, java.util.Date[].class, object -> toArray(object, java.util.Date[].class, java.util.Date.class))
		);

		// java.sql.Array -> Date[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, Date[].class, object -> toArray(object, Date[].class, Date.class))
		);

		// java.sql.Array -> Time[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, Time[].class, object -> toArray(object, Time[].class, Time.class))
		);

		// java.sql.Array -> Timestamp[]
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, Timestamp[].class, object -> toArray(object, Timestamp[].class, Timestamp.class))
		);

		// java.sql.Array -> LocalDateTime[] (3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, LocalDateTime[].class, object -> toArray(object, LocalDateTime[].class, LocalDateTime.class))
		);

		// java.sql.Array -> LocalDate[] (3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, LocalDate[].class, object -> toArray(object, LocalDate[].class, LocalDate.class))
		);

		// java.sql.Array -> LocalTime[] (3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, LocalTime[].class, object -> toArray(object, LocalTime[].class, LocalTime.class))
		);

		// java.sql.Array -> OffsetDateTime[] (3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, OffsetDateTime[].class, object -> toArray(object, OffsetDateTime[].class, OffsetDateTime.class))
		);

		// java.sql.Array -> ZonedDateTime[] (3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, ZonedDateTime[].class, object -> toArray(object, ZonedDateTime[].class, ZonedDateTime.class))
		);

		// java.sql.Array -> Instant[] (3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.sql.Array.class, Instant[].class, object -> toArray(object, Instant[].class, Instant.class))
		);

	// * -> SqlString
		Function<String, SqlString> toDateSqlString      = string -> new SqlString("DATE'" + string + '\'');
		Function<String, SqlString> toTimeSqlString      = string -> new SqlString("TIME'" + string + '\'');
		Function<String, SqlString> toTimestampSqlString = string -> new SqlString("TIMESTAMP'" + string + '\'');

		// Object -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Object.class, SqlString.class, object -> new SqlString(object.toString()))
		);

		// Boolean -> SqlString(FALSE, TRUE)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Boolean.class, SqlString.class, object -> new SqlString(object ? "TRUE" : "FALSE"))
		);

		// BigDecimal -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigDecimal.class, SqlString.class, object -> new SqlString(object.toPlainString()))
		);

		// String -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String.class, SqlString.class, object -> {
				if (object.length() > maxStringLiteralLength)
					return new SqlString(SqlString.PARAMETER, object); // SQL Parameter

				StringBuilder buff = new StringBuilder(object.length() + 2);
				buff.append('\'');
				boolean inLiteral = true;

				for (char ch : object.toCharArray()) {
					if (ch >= ' ' && ch != '\u007F') {
						// Literal representation
						if (!inLiteral) {
							// Outside of the literal
							buff.append("||'");
							inLiteral = true;
						}
						if (ch == '\'') buff.append('\'');
						buff.append(ch);
					} else {
						// Functional representation
						if (inLiteral) {
							// Inside of the literal
							buff.append('\'');
							inLiteral = false;
						}
						buff.append("||CHR(").append((int)ch).append(')');
					}
				}

				if (inLiteral)
					buff.append('\'');

				return new SqlString(buff.toString());
			})
		);

		// Character -> String -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Character.class, SqlString.class,
				TypeConverter.get(typeConverterMap, Character.class, String.class).function(),
				TypeConverter.get(typeConverterMap, String.class, SqlString.class).function()
			)
		);

		// java.util.Date -> String -> SqlString (since 1.4.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.util.Date.class, SqlString.class,
				TypeConverter.get(typeConverterMap, java.util.Date.class, String.class).function(),
				toDateSqlString
			)
		);

		// java.sql.Date -> String -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Date.class, SqlString.class,
				TypeConverter.get(typeConverterMap, Date.class, String.class).function(),
				toDateSqlString
			)
		);

		// LocalDate -> String -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(LocalDate.class, SqlString.class,
				TypeConverter.get(typeConverterMap, LocalDate.class, String.class).function(),
				toDateSqlString
			)
		);

		// Time -> String -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Time.class, SqlString.class,
				TypeConverter.get(typeConverterMap, Time.class, String.class).function(),
				toTimeSqlString
			)
		);

		// LocalTime -> String -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(LocalTime.class, SqlString.class,
				TypeConverter.get(typeConverterMap, LocalTime.class, String.class).function(),
				toTimeSqlString
			)
		);

		// Timestamp -> String -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Timestamp.class, SqlString.class,
				TypeConverter.get(typeConverterMap, Timestamp.class, String.class).function(),
				toTimestampSqlString
			)
		);

		// LocalDateTime -> String -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(LocalDateTime.class, SqlString.class,
				TypeConverter.get(typeConverterMap, LocalDateTime.class, String.class).function(),
				toTimestampSqlString
			)
		);

		// OffsetDateTime -> String -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(OffsetDateTime.class, SqlString.class,
				TypeConverter.get(typeConverterMap, OffsetDateTime.class, String.class).function(),
				toTimestampSqlString
			)
		);

		// ZonedDateTime -> String -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(ZonedDateTime.class, SqlString.class,
				TypeConverter.get(typeConverterMap, ZonedDateTime.class, String.class).function(),
				toTimestampSqlString
			)
		);

		// Instant -> String -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Instant.class, SqlString.class,
				TypeConverter.get(typeConverterMap, Instant.class, String.class).function(),
				toTimestampSqlString
			)
		);

		// Enum -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Enum.class, SqlString.class, object -> new SqlString('\'' + object.toString() + '\''))
		);

		// boolean[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(boolean[].class, SqlString.class, object -> toSqlString(object, Boolean.class))
		);

		// char[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(char[].class, SqlString.class, object -> toSqlString(object, Character.class))
		);

		// byte[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(byte[].class, SqlString.class, object -> {
				if (object.length > maxBinaryLiteralLength)
					return new SqlString(SqlString.PARAMETER, object); // SQL Parameter

				StringBuilder buff = new StringBuilder(object.length * 2 + 3);
				buff.append("X'");
				for (int value : object) {
					value &= 0xFF;
					char ch = (char)((value >>> 4) + '0');
					if (ch > '9') ch += 'A' - ('9' + 1);
					buff.append(ch);
					ch = (char)((value & 0x0F) + '0');
					if (ch > '9') ch += 'A' - ('9' + 1);
					buff.append(ch);
				}
				buff.append('\'');

				return new SqlString(buff.toString());
			})
		);

		// byte[][] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(byte[][].class, SqlString.class, object -> toSqlString(object, byte[].class))
		);

		// short[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(short[].class, SqlString.class, object -> toSqlString(object, Short.class))
		);

		// int[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(int[].class, SqlString.class, object -> toSqlString(object, Integer.class))
		);

		// long[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(long[].class, SqlString.class, object -> toSqlString(object, Long.class))
		);

		// float[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(float[].class, SqlString.class, object -> toSqlString(object, Float.class))
		);

		// double[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(double[].class, SqlString.class, object -> toSqlString(object, Double.class))
		);

		// BigDecimal[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(BigDecimal[].class, SqlString.class, object -> toSqlString(object, BigDecimal.class))
		);

		// String[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(String[].class, SqlString.class, object -> toSqlString(object, String.class))
		);

		// java.util.Date[] -> SqlString (since 1.4.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(java.util.Date[].class, SqlString.class, object -> toSqlString(object, java.util.Date.class))
		);

		// Date[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Date[].class, SqlString.class, object -> toSqlString(object, Date.class))
		);

		// Time[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Time[].class, SqlString.class, object -> toSqlString(object, Time.class))
		);

		// Timestamp[] -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Timestamp[].class, SqlString.class, object -> toSqlString(object, Timestamp.class))
		);

		// LocalDateTime[] -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(LocalDateTime[].class, SqlString.class, object -> toSqlString(object, LocalDateTime.class))
		);

		// LocalDate[] -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(LocalDate[].class, SqlString.class, object -> toSqlString(object, LocalDate.class))
		);

		// LocalTime[] -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(LocalTime[].class, SqlString.class, object -> toSqlString(object, LocalTime.class))
		);

		// OffsetDateTime[] -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(OffsetDateTime[].class, SqlString.class, object -> toSqlString(object, OffsetDateTime.class))
		);

		// ZonedDateTime[] -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(ZonedDateTime[].class, SqlString.class, object -> toSqlString(object, ZonedDateTime.class))
		);

		// Instant[] -> SqlString (since 3.0.0)
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Instant[].class, SqlString.class, object -> toSqlString(object, Instant.class))
		);

		// Iterable -> SqlString
		TypeConverter.put(typeConverterMap,
			new TypeConverter<>(Iterable.class, SqlString.class, object -> {
				Iterator<Object> iterator = object.iterator();
				Class<?> beforeElementType = null;
				Function<Object, SqlString> function = null;

				StringBuilder buff = new StringBuilder("(");

				for (int index = 0; iterator.hasNext(); ++index) {
					if (index > 0) buff.append(",");
					Object element = iterator.next();
					if (element == null)
						buff.append("NULL");
					else {
						Class<?> elementType = element.getClass();
						if (elementType != beforeElementType) {
							TypeConverter<?, SqlString> typeConverter = TypeConverter.get(typeConverterMap, elementType, SqlString.class);
							if (typeConverter == null)
								throw new ConvertException(elementType, element, SqlString.class);

							function = (Function<Object, SqlString>)typeConverter.function();
							beforeElementType = elementType;
						}
						buff.append(function.apply(element).content());
					}
				}

				buff.append(')');
				return new SqlString(buff.toString());
			})
		);

	}

	/**
	 * Converts a java.sql.Array to an array.
	 *
	 * @param <AT> array type
	 * @param <CT> component type
	 * @param object an object to be converted
	 * @param arrayType the array type
	 * @param componentType the component type
	 * @return the converted array
	 */
	@SuppressWarnings("unchecked")
	protected <AT, CT> AT toArray(java.sql.Array object, Class<AT> arrayType, Class<CT> componentType) {
		try {
			Object array = object.getArray();
			if (arrayType.isInstance(array))
				return (AT)array;

			AT result = (AT)Array.newInstance(componentType, Array.getLength(array));
			TypeConverter<Object, CT> typeConverter = null;
			for (int index = 0; index < Array.getLength(result); ++index) {
				Object value = Array.get(array, index);
				CT convertedValue = null;
				if (value != null) {
					if (Utils.toClassType(componentType).isInstance(value))
						convertedValue = (CT)value;
					else {
						if (typeConverter == null)
							typeConverter = (TypeConverter<Object, CT>)TypeConverter.get(typeConverterMap, value.getClass(), componentType);

						if (typeConverter == null)
							throw new ConvertException(value.getClass(), value, componentType);

						convertedValue = typeConverter.function().apply(value);
					}
				}
				Array.set(result, index, convertedValue);
			}
			return result;
		}
		catch (Exception e) {
			throw new ConvertException(object.getClass(), object, arrayType, e);
		}
	}

	/**
	 * Converts an array object to a <b>SqlString</b>.
	 *
	 * @param <CT> component type
	 * @param array an array object to be converted
	 * @param componentType the component type
	 * @return the converted <b>SqlString</b>
	 */
	@SuppressWarnings("unchecked")
	protected <CT> SqlString toSqlString(Object array, Class<CT> componentType) {
		TypeConverter<CT, SqlString> typeConverter = TypeConverter.get(typeConverterMap, componentType, SqlString.class);
		if (typeConverter == null)
			throw new ConvertException(componentType, array, SqlString.class);

	// 3.0.0
	//	Function<CT, SqlString> function = typeConverter.function();
		Function<? super CT, ? extends SqlString> function = typeConverter.function();
	////
		StringBuilder buff = new StringBuilder("ARRAY[");
		List<Object> parameters = new ArrayList<>();
		for (int index = 0; index < Array.getLength(array); ++ index) {
			if (index > 0) buff.append(",");
			SqlString sqlString = function.apply((CT)Array.get(array, index));
			buff.append(sqlString.content());
			parameters.addAll(Arrays.asList(sqlString.parameters()));
		}
		buff.append(']');
		return new SqlString(buff.toString(), parameters.toArray());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String selectSql(Sql<E> sql, List<Object> parameters) {
		StringBuilder buff = new StringBuilder();

		// SELECT ... FROM ... WHERE ... GROUP BY ... HAVING ...
		buff.append(subSelectSql(sql, parameters));

		// ORDER BY ...
		appendOrderBy(buff, sql, parameters);

		if (supportsOffsetLimit()) {
			// LIMIT ...
			appendLimit(buff, sql);

			// OFFSET ...
			appendOffset(buff, sql);
		}

		// FOR UPDATE
		appendForUpdate(buff, sql);

		return buff.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String subSelectSql(Sql<E> sql, List<Object> parameters) {
		return subSelectSql(sql, () -> {
			//  column name, ...
			StringBuilder buff = new StringBuilder();
			String[] delimiter = new String[] {""};

			sql.selectedJoinSqlColumnInfoStream()
				.filter(sqlColumnInfo -> sqlColumnInfo.columnInfo().selectable())
				.forEach(sqlColumnInfo -> {
					buff.append(delimiter[0]);
					delimiter[0] = ", ";

					ColumnInfo columnInfo = sqlColumnInfo.columnInfo();
					String tableAlias   = sqlColumnInfo.tableAlias();
					String columnName   = columnInfo.getColumnName(tableAlias);
					String columnAlias  = columnInfo.getColumnAlias(tableAlias);

					// gets expression
					Expression expression = sql.getExpression(columnInfo.propertyName());
					if (expression.isEmpty())
						expression = sql.getExpression(columnInfo.getPropertyName(tableAlias));

					if (expression.isEmpty())
						expression = columnInfo.selectExpression();

					if (expression.isEmpty()) {
						if (!sql.getGroupBy().isEmpty()) buff.append("MIN(");

						// No expression ->  column name
						buff.append(columnName);

						if (!sql.getGroupBy().isEmpty()) buff.append(")");

						// column alias
						if (!columnAlias.equals(columnName))
							buff.append(" AS ").append(columnAlias);

					} else {
						// First expression
						buff.append(expression.toString(this, sql, parameters));

						// column alias
						buff.append(" AS ").append(columnAlias);
					}
				});
			if (buff.length() == 0)
				throw new IllegalStateException(MessageFormat.format(messageSelectSqlWithoutColumns,
					sql.entityClass().getName(),
					'[' + sql.getColumns().stream()
						.map(name -> '"' + name + '"')
						.collect(Collectors.joining(", ")) + ']'
				));
			return buff;
		}, parameters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String subSelectSql(Sql<E> sql, Supplier<CharSequence> columnsSupplier, List<Object> parameters) {
		StringBuilder buff = new StringBuilder();

		// SELECT
		buff.append("SELECT");

		// DISTINCT
		appendDistinct(buff, sql);

		//  column name, ...
		buff.append(' ').append(columnsSupplier.get());

		// FROM
		buff.append(" FROM");

		// main table name and alias
		appendMainTable(buff, sql);

		// INNER / OUTER JOIN ...
		appendJoinTables(buff, sql, parameters);

		// WHERE ...
		appendWhere(buff, sql, parameters);

		// GROUP BY ...
		appendGroupBy(buff, sql, parameters);

		// HAVING ...
		appendHaving(buff, sql, parameters);
	////

		return buff.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String insertSql(Sql<E> sql, List<Object> parameters) {
		StringBuilder buff = new StringBuilder();

		// INSERT INTO
		buff.append("INSERT INTO");

		// table name and alias
		appendMainTable(buff, sql);

		// (column name, ...) VALUES (value, ...)
		appendInsertColumnsAndValues(buff, sql, parameters);

		return buff.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String updateSql(Sql<E> sql, List<Object> parameters) {
		StringBuilder buff = new StringBuilder();

		// UPDATE table name
		buff.append("UPDATE");

		// main table name and alias
		appendMainTable(buff, sql);

		// INNER / OUTER JOIN ...
		appendJoinTables(buff, sql, parameters);

		// SET column name =  value, ...
		appendUpdateColumnsAndValues(buff, sql, parameters);

		// WHERE ...
		appendWhere(buff, sql, parameters);

		// ORDER BY ...
		appendOrderBy(buff, sql, parameters);

		// LIMIT ...
		appendLimit(buff, sql);

		return buff.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String deleteSql(Sql<E> sql, List<Object> parameters) {
		StringBuilder buff = new StringBuilder();

		// DELETE FROM
		buff.append("DELETE");
		if (sql.getJoinInfos().size() > 0)
			buff.append(' ')
				.append(sql.tableAlias().isEmpty() ? sql.entityInfo().tableName() : sql.tableAlias());
		buff.append(" FROM");

		// main table name and alias
		appendMainTable(buff, sql);

		// INNER / OUTER JOIN ...
		appendJoinTables(buff, sql, parameters);

		// WHERE ...
		appendWhere(buff, sql, parameters);

		// ORDER BY ...
		appendOrderBy(buff, sql, parameters);

		// LIMIT ...
		appendLimit(buff, sql);

		return buff.toString();
	}

	/**
	 * Appends DISTINCT to <b>buff</b>.
	 *
	 * @param <E> the type of the entity
	 * @param buff the string buffer to be appended
	 * @param sql a <b>Sql</b> object
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendDistinct(StringBuilder buff, Sql<E> sql) {
		// DISTINCT
		if (sql.isDistinct())
			buff.append(" DISTINCT");
	}

	/**
	 * Appends the main table name and alias to <b>buff</b>.
	 *
	 * @param <E> the type of the entity
	 * @param buff the string buffer to be appended
	 * @param sql a <b>Sql</b> object
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendMainTable(StringBuilder buff, Sql<E> sql) {
		// main table name
		buff.append(' ').append(sql.entityInfo().tableName());

		// table alias
		if (!sql.tableAlias().isEmpty())
			buff.append(' ').append(sql.tableAlias());
	}

	/**
	 * Appends the join table names and aliases to <b>buff</b>.
	 *
	 * @param <E> the type of the entity
	 * @param buff the string buffer to be appended
	 * @param sql a <b>Sql</b> object
	 * @param parameters a list to add the parameters of the SQL
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendJoinTables(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
		sql.getJoinInfos().forEach(joinInfo -> {
			// INNER/OUTER JOIN table name
			buff.append(joinInfo.joinType().sql()).append(joinInfo.entityInfo().tableName());

			// table alias
			if (!joinInfo.tableAlias().isEmpty())
				buff.append(' ').append(joinInfo.tableAlias());

			// ON ...
			if (!joinInfo.on().isEmpty())
				buff.append(" ON ").append(joinInfo.on().toString(this, sql, parameters));
		});
	}

	/**
	 * Appends INSERT column names and values to <b>buff</b>.
	 *
	 * @param <E> the type of the entity
	 * @param buff the string buffer to be appended
	 * @param sql a <b>Sql</b> object
	 * @param parameters a list to add the parameters of the SQL
	 *
	 * @since 1.8.4
	 */
	protected <E> void appendInsertColumnsAndValues(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
		// ( column name, ...
		buff.append(" (");
		String[] delimiter = new String[] {""};

		sql.columnInfoStream()
			.filter(ColumnInfo::insertable)
			.forEach(columnInfo -> {
				buff.append(delimiter[0]).append(columnInfo.columnName());
				delimiter[0] = ", ";
			});

		// ) VALUES (value, ...)
		buff.append(") VALUES (");
		delimiter[0] = "";

		sql.columnInfoStream()
			.filter(ColumnInfo::insertable)
			.forEach(columnInfo -> {
				String propertyName = columnInfo.propertyName();

				// gets expression
				Expression expression = sql.getExpression(propertyName);
				if (expression.isEmpty())
					expression = columnInfo.insertExpression();

				if (expression.isEmpty())
					expression = new Expression("{#" + propertyName + "}");

				buff.append(delimiter[0])
					.append(expression.toString(this, sql, parameters));
				delimiter[0] = ", ";
			});
		buff.append(")");
	}

	/**
	 * Appends UPDATE column names and values to <b>buff</b>.
	 *
	 * @param <E> the type of the entity
	 * @param buff the string buffer to be appended
	 * @param sql a <b>Sql</b> object
	 * @param parameters a list to add the parameters of the SQL
	 *
	 * @since 1.8.4
	 */
	protected <E> void appendUpdateColumnsAndValues(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
		// SET column name =  value, ...
		buff.append(" SET ");
		String[] delimiter = new String[] {""};

		sql.selectedSqlColumnInfoStream()
			.filter(sqlColumnInfo -> sqlColumnInfo.columnInfo().updatable())
			.forEach(sqlColumnInfo -> {
				ColumnInfo columnInfo = sqlColumnInfo.columnInfo();
				String tableAlias   = sqlColumnInfo.tableAlias();
				String propertyName = columnInfo.propertyName();
				String columnName   = columnInfo.getColumnName(tableAlias);

				// gets expression
				Expression expression = sql.getExpression(propertyName);
				if (expression.isEmpty())
					expression = columnInfo.updateExpression();

				if (expression.isEmpty())
					expression = new Expression("{#" + propertyName + "}");

				buff.append(delimiter[0])
					.append(columnName)
					.append("=")
					.append(expression.toString(this, sql, parameters));
				delimiter[0] = ", ";
			});
	}

	/**
	 * Appends WHERE clause to <b>buff</b>.
	 *
	 * @param <E> the type of the entity
	 * @param buff the string buffer to be appended
	 * @param sql a <b>Sql</b> object
	 * @param parameters a list to add the parameters of the SQL
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendWhere(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
		if (sql.getWhere() != Condition.ALL)
			buff.append(" WHERE ").append(sql.getWhere().toString(this, sql, parameters));
	}

	/**
	 * Appends GROUP BY clause to <b>buff</b>.
	 *
	 * @param <E> the type of the entity
	 * @param buff the string buffer to be appended
	 * @param sql a <b>Sql</b> object
	 * @param parameters a list to add the parameters of the SQL
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendGroupBy(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
		if (!sql.getGroupBy().isEmpty())
			buff.append(' ').append(sql.getGroupBy().toString(this, sql, parameters));
	}

	/**
	 * Appends HAVING clause to <b>buff</b>.
	 *
	 * @param <E> the type of the entity
	 * @param buff the string buffer to be appended
	 * @param sql a <b>Sql</b> object
	 * @param parameters a list to add the parameters of the SQL
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendHaving(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
		if (!sql.getHaving().isEmpty())
			buff.append(" HAVING ").append(sql.getHaving().toString(this, sql, parameters));
	}

	/**
	 * Appends ORDER BY clause to <b>buff</b>.
	 *
	 * @param <E> the type of the entity
	 * @param buff the string buffer to be appended
	 * @param sql a <b>Sql</b> object
	 * @param parameters a list to add the parameters of the SQL
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendOrderBy(StringBuilder buff, Sql<E> sql, List<Object> parameters) {
		if (!sql.getOrderBy().isEmpty())
			buff.append(' ').append(sql.getOrderBy().toString(this, sql, parameters));
	}

	/**
	 * Appends LIMIT clause to <b>buff</b>.
	 *
	 * @param <E> the type of the entity
	 * @param buff the string buffer to be appended
	 * @param sql a <b>Sql</b> object
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendLimit(StringBuilder buff, Sql<E> sql) {
		if (sql.getLimit() != Integer.MAX_VALUE)
			buff.append(" LIMIT ").append(sql.getLimit());
	}

	/**
	 * Appends OFFSET clause to <b>buff</b>.
	 *
	 * @param <E> the type of the entity
	 * @param buff the string buffer to be appended
	 * @param sql a <b>Sql</b> object
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendOffset(StringBuilder buff, Sql<E> sql) {
		if (sql.getOffset() != 0)
			buff.append(" OFFSET ").append(sql.getOffset());
	}

	/**
	 * Appends FOR UPDATE clause to <b>buff</b>.
	 *
	 * @param <E> the type of the entity
	 * @param buff the string buffer to be appended
	 * @param sql a <b>Sql</b> object
	 *
	 * @since 1.8.2
	 */
	protected <E> void appendForUpdate(StringBuilder buff, Sql<E> sql) {
		// FOR UPDATE
		if (sql.isForUpdate()) {
			buff.append(" FOR UPDATE");

			// NO WAIT
			if (sql.isNoWait())
				throw new UnsupportedOperationException("noWait");

			// WAIT n
			else if (!sql.isWaitForever())
				throw new UnsupportedOperationException("wait N");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, TypeConverter<?, ?>> typeConverterMap() {
		return typeConverterMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T convert(Object value, Class<T> type) {
		return TypeConverter.convert(typeConverterMap, value, type);
	}

// 3.0.0
//	/**
//	 * {@inheritDoc}
//	 *
//	 * @since 2.2.0
//	 */
//	@Override
//	public String maskPassword(String jdbcUrl) {
//		return SQLServer.instance.maskPassword(MySQL.instance.maskPassword(jdbcUrl));
//	}
////
}
