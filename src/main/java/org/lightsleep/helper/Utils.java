// Utils.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * There are utility methods.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class Utils {
    // A map to convert the primitive type to class type
    private static final Map<Class<?>, Class<?>> toClassMap = new LinkedHashMap<>();
    static {
        toClassMap.put(boolean.class, Boolean  .class);
        toClassMap.put(char   .class, Character.class);
        toClassMap.put(byte   .class, Byte     .class);
        toClassMap.put(short  .class, Short    .class);
        toClassMap.put(int    .class, Integer  .class);
        toClassMap.put(long   .class, Long     .class);
        toClassMap.put(float  .class, Float    .class);
        toClassMap.put(double .class, Double   .class);
    }

    // A map to convert the class type to primitive type
    private static final Map<Class<?>, Class<?>> toPrimitiveMap = new LinkedHashMap<>();
    static {
        toPrimitiveMap.put(Boolean  .class, boolean.class);
        toPrimitiveMap.put(Character.class, char   .class);
        toPrimitiveMap.put(Byte     .class, byte   .class);
        toPrimitiveMap.put(Short    .class, short  .class);
        toPrimitiveMap.put(Integer  .class, int    .class);
        toPrimitiveMap.put(Long     .class, long   .class);
        toPrimitiveMap.put(Float    .class, float  .class);
        toPrimitiveMap.put(Double   .class, double .class);
    }

    // Class resources
    private static int maxLogStringLength;
    private static int maxLogByteArrayLength;
    private static int maxLogArrayLength;
    private static int maxLogMapSize;
    static {
        initClass();
    }

    private static void initClass() {
        maxLogStringLength    = Resource.getGlobal().getInt("maxLogStringLength"   , 200);
        maxLogByteArrayLength = Resource.getGlobal().getInt("maxLogByteArrayLength", 200);
        maxLogArrayLength     = Resource.getGlobal().getInt("maxLogArrayLength"    , 100);
        maxLogMapSize         = Resource.getGlobal().getInt("maxLogMapSize"        , 100);
    }

    private static final DateTimeFormatter utilDateFormatter       = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSxxx");
    private static final DateTimeFormatter sqlDateFormatter        = DateTimeFormatter.ofPattern("yyyy-MM-ddxxx");
    private static final DateTimeFormatter timeFormatter           = DateTimeFormatter.ofPattern("HH:mm:ss.SSSxxx");
    private static final DateTimeFormatter timestampFormatter      = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSSxxx");
    private static final DateTimeFormatter localDateFormatter      = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter localTimeFormatter      = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSS");
    private static final DateTimeFormatter offsetTimeFormatter     = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSSSSxxx");
    private static final DateTimeFormatter localDateTimeFormatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
    private static final DateTimeFormatter offsetDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSSxxx");
    private static final DateTimeFormatter zonedDateTimeFormatter  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSSxxx VV");
    private static final DateTimeFormatter instantFormatter        = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSSX");

    /**
     * Converts the primitive type to the related class type.
     *
     * @param type the type (permit null)
     * @return the related class type if <b>type</b> is a primitive type, <b>type</b> otherwise (in the case of <b>type</b> == null)
     */
    public static Class<?> toClassType(Class<?> type) {
        return toClassMap.getOrDefault(type, type);
    }

    /**
     * Converts the class type to the related primitive type.
     *
     * @param type the type (permit null)
     * @return the related primitive type if <b>type</b> is a class type, <b>type</b> otherwise (in the case of <b>type</b> == null)
     */
    public static Class<?> toPrimitiveType(Class<?> type) {
        return toPrimitiveMap.getOrDefault(type, type);
    }

    /**
     * Returns a class name without the package name.
     *
     * @param clazz the class
     * @return a class name without the package name
     *
     * @throws NullPointerException if <b>clazz</b> is <b>null</b>
     */
    public static String nameWithoutPackage(Class<?> clazz) {
        String className = null;
        if (clazz.isArray()) {
            className = nameWithoutPackage(clazz.getComponentType()) + "[]";
        } else {
            className = clazz.getName();
            if (className.startsWith("java."))
                // gets the right from the last period
                className = className.substring(className.lastIndexOf('.') + 1);
        }

        return className;
    }

    /**
     * Returns a new array of <b>elementType</b>.<br>
     *
     * Store a new object of <b>elementType</b> for all of the elements of the array.
     *
     * @param <E> the type of the element of the array
     * @param elementType the type of the element of the array
     * @param length the length of the array
     * @return elementType a new array of <b>elementType</b>
     *
     * @throws NullPointerException if <b>elementType</b> is <b>null</b>
     * @throws IndexOutOfBoundsException if <b>length </b>&lt; 0
     * @throws RuntimeException if <b>InstantiationException</b> or <b>IllegalAccessException</b> has been thrown
     */
    @SuppressWarnings("unchecked")
    public static <E> E[] newArray(Class<E> elementType, int length) {
        E[] array = (E[])Array.newInstance(elementType, length);
        try {
            for (int index = 0; index < length; ++index)
                array[index] = elementType.getConstructor().newInstance();
        }
        catch (RuntimeException e) {throw e;}
        catch (Exception e) {throw new RuntimeException(e);}

        return array;
    }

    /**
     * Returns a string representation for the log output of the specified value.
     *
     * @param value the value
     * @return a string representation for the log output
     */
    public static String toLogString(boolean value) {
        return toLogString(Boolean.valueOf(value), Boolean.TYPE);
    }

    /**
     * Returns a string representation for the log output of the specified value.
     *
     * @param value the value
     * @return a string representation for the log output
     */
    public static String toLogString(char value) {
        return toLogString(Character.valueOf(value), Character.TYPE);
    }

    /**
     * Returns a string representation for the log output of the specified value.
     *
     * @param value the value
     * @return a string representation for the log output
     */
    public static String toLogString(byte value) {
        return toLogString(Byte.valueOf(value), Byte.TYPE);
    }

    /**
     * Returns a string representation for the log output of the specified value.
     *
     * @param value the value
     * @return a string representation for the log output
     */
    public static String toLogString(short value) {
        return toLogString(Short.valueOf(value), Short.TYPE);
    }

    /**
     * Returns a string representation for the log output of the specified value.
     *
     * @param value the value
     * @return a string representation for the log output
     */
    public static String toLogString(int value) {
        return toLogString(Integer.valueOf(value), Integer.TYPE);
    }

    /**
     * Returns a string representation for the log output of the specified value.
     *
     * @param value the value
     * @return a string representation for the log output
     */
    public static String toLogString(long value) {
        return toLogString(Long.valueOf(value), Long.TYPE);
    }

    /**
     * Returns a string representation for the log output of the specified value.
     *
     * @param value the value
     * @return a string representation for the log output
     */
    public static String toLogString(float value) {
        return toLogString(Float.valueOf(value), Float.TYPE);
    }

    /**
     * Returns a string representation for the log output of the specified value.
     *
     * @param value the value
     * @return a string representation for the log output
     */
    public static String toLogString(double value) {
        return toLogString(Double.valueOf(value), Double.TYPE);
    }

    /**
     * Returns a string representation for the log output of the specified value.
     *
     * @param value the value (permit null)
     * @return a string representation for the log output
     */
    public static String toLogString(Object value) {
        return toLogString(value, null);
    }

    /**
     * Returns a string representation for the log output of the specified value.
     *
     * @param object the object (permit null)
     * @param type the type of the object (permit null)
     * @return a string representation for the log output
     *
     * @throws NullPointerException if <b>type</b> is <b>null</b>
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
                appendType(buff.append('('), type, object).append(')');
                if (type == char[].class)
                    // String Array
                    appendChars(buff, ((char[])object));
                else if (type == byte[].class)
                    // Byte Array
                    appendBytes(buff, ((byte[])object));
                else
                    // etc. Array
                    appendArray(buff, object);

            } else if (object instanceof Boolean) {
                // Boolean
                if (type != Boolean.TYPE)
                    appendType(buff.append('('), type, object).append(')');
                buff.append(object);

            } else if (object instanceof Character) {
                // Character
                if (type != Character.TYPE)
                    appendType(buff.append('('), type, object).append(')');
                buff.append('\'');
                appendChar(buff, ((Character)object).charValue());
                buff.append('\'');

            } else if (object instanceof BigDecimal) {
                // BigDecimal
                appendType(buff.append('('), type, object).append(')')
                    .append(((BigDecimal)object).toPlainString());

            } else if (object instanceof Number) {
                // Number
                if (type != Integer.TYPE)
                    appendType(buff.append('('), type, object).append(')');
                buff.append(object);

            } else if (object instanceof java.util.Date) {
                // java.util.Date
                appendType(buff.append('('), type, object).append(')');
                Timestamp timestamp = object instanceof Timestamp ? (Timestamp)object : new Timestamp(((java.util.Date)object).getTime());
                ZonedDateTime zonedDateTime = timestamp.toLocalDateTime().atZone(ZoneId.systemDefault());
                if      (object instanceof Date     ) buff.append(zonedDateTime.format(sqlDateFormatter  )); // java.sql.Date
                else if (object instanceof Time     ) buff.append(zonedDateTime.format(timeFormatter     )); // Time
                else if (object instanceof Timestamp) buff.append(zonedDateTime.format(timestampFormatter)); // Timestamp
                else                                  buff.append(zonedDateTime.format(utilDateFormatter )); // java.util.Date

            } else if (object instanceof Temporal) {
                // Temporal
                appendType(buff.append('('), type, object).append(')');
                if      (object instanceof LocalDate     ) buff.append(((LocalDate     )object).format(localDateFormatter     )); // LocalDate
                else if (object instanceof LocalTime     ) buff.append(((LocalTime     )object).format(localTimeFormatter     )); // LocalTime
                else if (object instanceof OffsetTime    ) buff.append(((OffsetTime    )object).format(offsetTimeFormatter    )); // OffsetTime
                else if (object instanceof LocalDateTime ) buff.append(((LocalDateTime )object).format(localDateTimeFormatter )); // LocalDateTime
                else if (object instanceof OffsetDateTime) buff.append(((OffsetDateTime)object).format(offsetDateTimeFormatter)); // OffsetDateTime
                else if (object instanceof ZonedDateTime ) buff.append(((ZonedDateTime )object).format(zonedDateTimeFormatter )); // ZonedDateTime
                else if (object instanceof Instant) buff.append(((Instant)object).atOffset(ZoneOffset.ofHours(0)).format(instantFormatter)); // Instant
                else buff.append(object);

            } else if (object instanceof String) {
                // String
                appendString(buff, (String)object);

            } else if (object instanceof Class<?>) {
                // Class
                buff.append(((Class<?>)object).getName());

            } else if (object instanceof Iterable) {
                // Iterable
                appendType(buff.append('('), type, object).append(')');
                appendIterable(buff, (Iterable)object);

            } else if (object instanceof Map) {
                // Map
                appendType(buff.append('('), type, object).append(')');
                appendMap(buff, (Map<?,?>)object);

            } else {
                // etc.
                appendType(buff.append('('), type, object).append(')')
                    .append(object);
            }
        }

        return buff.toString();
    }

    /**
     * Appends a string representation of <b>value</b> to the string buffer.
     *
     * @param buff the string buffer
     * @param type the type of the value
     * @param value a value to be appended (permit null)
     * @return the string buffer
     *
     * @throws NullPointerException if <b>buff</b> or <b>type</b> is <b>null</b>
     */
    @SuppressWarnings("rawtypes")
    private static StringBuilder appendType(StringBuilder buff, Class<?> type, Object value) {
        Objects.requireNonNull(type, "type is null");

        long length = -1L;
        int  size   = -1;

        if (type.isArray()) {
            // Array
            appendType(buff, type.getComponentType(), null).append("[]");
            if (value != null)
                length = Array.getLength(value);
        } else {
            // Non Array
            String typeName = nameWithoutPackage(type);
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
     * Appends a string representation of a character to the string buffer.
     *
     * @param buff the string buffer
     * @param ch a character
     * @return the string buffer
     *
     * @throws NullPointerException if <b>buff</b> is <b>null</b>
     */
    private static StringBuilder appendChar(StringBuilder buff, char ch) {
        if (ch >= ' ' && ch != '\u007F') {
            if      (ch == '"' ) buff.append("\\\"");
            else if (ch == '\'') buff.append("\\'" );
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
     * Appends a string representation of a string to the string buffer.
     *
     * @param buff the string buffer
     * @param string the string
     * @return the string buffer
     *
     * @throws NullPointerException if <b>buff</b> or <b>string</b> is <b>null</b>
     */
    private static StringBuilder appendString(StringBuilder buff, String string) {
        buff.append('"');
        for (int index = 0; index < string.length(); ++index) {
            if (index >= maxLogStringLength) {
                buff.append("...");
                break;
            }
            appendChar(buff, string.charAt(index));
        }
        buff.append('"');

        return buff;
    }

    /**
     * Appends a string representation of characters to the string buffer.
     *
     * @param buff the string buffer
     * @param chars an array of characters
     * @return the string buffer
     *
     * @throws NullPointerException if <b>buff</b> or <b>chars</b> is <b>null</b>
     */
    private static StringBuilder appendChars(StringBuilder buff, char[] chars) {
        buff.append('"');
        for (int index = 0; index < chars.length; ++index) {
            if (index >= maxLogStringLength) {
                buff.append("...");
                break;
            }
            appendChar(buff, chars[index]);
        }
        buff.append('"');

        return buff;
    }

    /**
     * Appends a string representation of bytes to the string buffer.
     *
     * @param buff the string buffer
     * @param bytes an array of bytes
     * @return the string buffer
     *
     * @throws NullPointerException if <b>buff</b> or <b>bytes</b> is <b>null</b>
     */
    private static StringBuilder appendBytes(StringBuilder buff, byte[] bytes) {
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
     * Appends a string representation of an array to the string buffer.
     *
     * @param buff the string buffer
     * @param array an array
     * @return the string buffer
     *
     * @throws NullPointerException if <b>buff</b> or <b>array</b> is <b>null</b>
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
     * Appends a string representation of an iterable to the string buffer.
     *
     * @param buff the string buffer
     * @param iterable an <b>Iterable</b>
     * @return the string buffer
     *
     * @throws NullPointerException if <b>buff</b> or <b>iterable</b> is <b>null</b>
     */
    private static StringBuilder appendIterable(StringBuilder buff, Iterable<?> iterable) {
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
     * Appends a string representation of a map to the string buffer.
     *
     * @param buff the string buffer
     * @param map a <b>Map</b>
     * @return the string buffer
     *
     * @throws NullPointerException if <b>buff</b> or <b>map</b> is <b>null</b>
     */
    private static <K,V> StringBuilder appendMap(StringBuilder buff, Map<K,V> map) {
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

    /**
     * Returns annotations of the target class and its super class without Object class.
     *
     * @param <A> the annotation type
     * @param clazz the target class
     * @param annotationClass the annotation class
     * @return a list of annotations
     *
     * @throws NullPointerException if <b>clazz</b> or <b>annotationClass</b> is <b>null</b>
     *
     * @since 1.5.1
     */
    public static <A extends Annotation> List<A> getAnnotations(Class<?> clazz, Class<A> annotationClass) {
        List<A> annotations = new ArrayList<>();
        addAnnotations(annotations, clazz, annotationClass);
        return annotations;
    }

    // Adds annotations of the class and its superclasses to the annotation list.
    private static <A extends Annotation> void addAnnotations(List<A> annotations, Class<?> clazz, Class<A> annotationClass) {
        if (clazz == Object.class) return;

        // Adds annotations of superclasses to the annotation list.
        addAnnotations(annotations, clazz.getSuperclass(), annotationClass);

        Arrays.stream(clazz.getAnnotationsByType(annotationClass))
            .forEach(annotation -> annotations.add(annotation));
    }
}
