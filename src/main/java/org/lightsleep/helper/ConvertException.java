// ConvertException.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

import java.text.MessageFormat;
import java.util.Optional;

/**
 * This exception is thrown if it fails to data conversion in the method of TypeConverter class.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
@SuppressWarnings("serial")
public class ConvertException extends RuntimeException {
    // Class resources
    private static final Resource resource = new Resource(ConvertException.class);
    private static final String messageNotFound        = resource.getString("messageNotFound"); // 4.0.1
    private static final String messageCantConvert     = resource.getString("messageCantConvert");
    private static final String messageCantConvert_e   = resource.getString("messageCantConvert_e");
    private static final String messageCantConvert_d   = resource.getString("messageCantConvert_d");
    private static final String messageCantConvert_d_e = resource.getString("messageCantConvert_d_e");

    // The source data type
    private Class<?> sourceType;

    // The destination data type
    private Class<?> destinType;

    // The source object
    private Optional<Object> source = Optional.empty();

    // The destination object
    private Optional<Object> destin = Optional.empty();

    /**
     * Constructs a new <b>ConvertException</b>.
     */
    public ConvertException() {
    }

    /**
     * Constructs a new <b>ConvertException</b> with the specified detail message.
     *
     * @param message the detail message
     */
    public ConvertException(String message) {
        super(message);
    }

    /**
     * Constructs a new <b>ConvertException</b> with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new <b>ConvertException</b> with the specified cause.
     *
     * @param cause the cause
     */
    public ConvertException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new <b>ConvertException</b>.
     *
     * @param sourceType the source data type
     * @param destinType the destination data type
     * since 4.0.1
     */
    public ConvertException(Class<?> sourceType, Class<?> destinType) {
        this(sourceType, Optional.empty(), destinType, Optional.empty(), null);
    }

    /**
     * Constructs a new <b>ConvertException</b>.
     *
     * @param sourceType the source data type
     * @param source the source object
     * @param destinType the destination data type
     */
    public ConvertException(Class<?> sourceType, Object source, Class<?> destinType) {
        this(sourceType, Optional.of(source), destinType, Optional.empty(), null);
    }

    /**
     * Constructs a new <b>ConvertException</b>.
     *
     * @param sourceType the source data type
     * @param source the source object
     * @param destinType the destination data type
     * @param destin the destination object
     */
    public ConvertException(Class<?> sourceType, Object source, Class<?> destinType, Object destin) {
        this(sourceType, Optional.of(source), destinType, Optional.of(destin), null);
    }

    /**
     * Constructs a new <b>ConvertException</b>.
     *
     * @param sourceType the source data type
     * @param source the source object
     * @param destinType the destination data type
     * @param cause the cause
     *
     * @since 3.0.0
     */
    public ConvertException(Class<?> sourceType, Object source, Class<?> destinType, Throwable cause) {
        this(sourceType, Optional.of(source), destinType, Optional.empty(), cause);
    }

    /**
     * Constructs a new <b>ConvertException</b>.
     *
     * @param sourceType the source data type
     * @param source the source object
     * @param destinType the destination data type
     * @param destin the destination object
     * @param cause the cause
     */
    public ConvertException(Class<?> sourceType, Object source, Class<?> destinType, Object destin, Throwable cause) {
        this(sourceType, Optional.of(source), destinType, Optional.of(destin), cause);
    }

    /**
     * Constructs a new <b>ConvertException</b>.
     *
     * @param sourceType the source data type
     * @param source the source object
     * @param destinType the destination data type
     * @param destin the destination object
     * @param cause the cause
     */
    private ConvertException(Class<?> sourceType, Optional<Object> source,
        Class<?> destinType, Optional<Object> destin, Throwable cause) {
        super(toString(sourceType, source, destinType, destin, cause), cause);
        this.sourceType = sourceType;
        this.source     = source;
        this.destinType = destinType;
        this.destin     = destin;
    }

    /**
     * Returns a string representation of the exception contents.
     *
     * @param sourceType the source data type
     * @param source the source object
     * @param destinType the destination data type
     * @param destin the destination object
     * @param cause the cause
     */
    private static String toString(Class<?> sourceType, Optional<Object> source,
        Class<?> destinType, Optional<Object> destin, Throwable cause) {
        String sourceTypeName = sourceType.getCanonicalName();
        String destinTypeName = destinType.getCanonicalName();
        String sourceString = source.isPresent() ? Utils.toLogString(source.get()) : null;
        String destinString = destin.isPresent() ? Utils.toLogString(destin.get()) : null;

        return 
            !source.isPresent()
                ? MessageFormat.format(messageNotFound, sourceTypeName, destinTypeName)
                : !destin.isPresent()
                    ? cause == null
                        ? MessageFormat.format(messageCantConvert,
                            sourceTypeName, sourceString, destinTypeName)
                        : MessageFormat.format(messageCantConvert_e,
                            sourceTypeName, sourceString, destinTypeName, cause)
                    : cause == null
                        ? MessageFormat.format(messageCantConvert_d,
                            sourceTypeName, sourceString, destinTypeName, destinString)
                        : MessageFormat.format(messageCantConvert_d_e,
                            sourceTypeName, sourceString, destinTypeName, destinString, cause)
                    ;
    }

    /**
     * Returns the source data type.
     *
     * @return the source data type
     */
    public Class<?> sourceType() {
        return sourceType;
    }

    /**
     * Returns the destination data type.
     *
     * @return the destination data type
     */
    public Class<?> destinType() {
        return destinType;
    }

    /**
     * Returns the source object.
     *
     * @return the source object
     */
    public Object sourceObject() {
        return source.orElse(null);
    }

    /**
     * Returns the destination object.
     *
     * @return the destination object
     */
    public Object destinObject() {
        return destin.orElse(null);
    }
}
