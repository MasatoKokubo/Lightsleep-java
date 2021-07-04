1. **Improvement**
    * Supported the following data types. You can use them as a field type for entity classes.
        * `java.time.LocalDate`
        * `java.time.LocalTime`
        * `java.time.LocalDateTime`
        * `java.time.OffsetDateTime`
        * `java.time.ZonedDateTime`
        * `java.time.Instant`

1. **Added** methods and constructors
    * `org.lightsleep.Sql` Class
        * `doNotIf(boolean condition, Consumer<Sql<E>> action)`
        * `doElse(Consumer<Sql<E>> elseAction)`
        * `executeUpdate(String sql)`

    * `org.lightsleep.database.Database` Interface and classes implementing it.
        * `getObject(Connection connection, ResultSet resultSet, String columnLabel)`

    * `org.lightsleep.helper.ConvertException` Class
        * `ConvertException(Class<?> sourceType, Object source, Class<?> destinType, Throwable cause)`

    * `org.lightsleep.helper.TypeConverter` Class
        * `TypeConverter(Class<ST> sourceType, Class<DT> destinType, Function<? super ST, MT> function1, Function<? super MT, ? extends DT> function2)`
        * `TypeConverter(Class<ST> sourceType, Class<DT> destinType,Function<? super ST, ? extends MT1> function1, Function<? super MT1, ? extends MT2> function2, Function<? super MT2, ? extends DT> function3)`
        * `TypeConverter(Class<ST> sourceType, Class<DT> destinType, Function<? super ST, MT1> function1, Function<? super MT1, ? extends MT2> function2, Function<? super MT2, ? extends MT3> function3, Function<? super MT3, ? extends DT> function4)`

1. **Deprecated** method
    * `org.lightsleep.Sql` Class
        * `doIf(boolean condition, Consumer<Sql<E>> action, Consumer<Sql<E>> elseAction)`

1. **Deleted** methods and constructor
    * `org.lightsleep.Sql` Class

        * `select(ConnectionWrapper connection, Consumer<? super E> consumer)`
        * `select(ConnectionWrapper connection, Consumer<? super E> consumer, Consumer<? super JE1> consumer1)`
        * `select(ConnectionWrapper connection, Consumer<? super  E > consumer, Consumer<? super JE1> consumer1, Consumer<? super JE2> consumer2)`
        * `select(ConnectionWrapper connection, Consumer<? super E> consumer, Consumer<? super JE1> consumer1, Consumer<? super JE2> consumer2, Consumer<? super JE3> consumer3)`
        * `select(ConnectionWrapper connection, Consumer<? super E> consumer, Consumer<? super JE1> consumer1, Consumer<? super JE2> consumer2, Consumer<? super JE3> consumer3, Consumer<? super JE4> consumer4)`
        * `select(ConnectionWrapper connection)`
        * `selectCount(ConnectionWrapper connection)`
        * `insert(ConnectionWrapper connection, E entity)`
        * `insert(ConnectionWrapper connection, Iterable<? extends E> entities)`
        * `update(ConnectionWrapper connection, E entity)`
        * `update(ConnectionWrapper connection, Iterable<? extends E> entities)`
        * `delete(ConnectionWrapper connection)`
        * `delete(ConnectionWrapper connection, E entity)`
        * `delete(ConnectionWrapper connection, Iterable<? extends E> entities)`

    * `org.lightsleep.database.DB2`, `MySQL`, `Oracle`, `PostgreSQL`, `SQLite`, `SQLServer` and `Standard` Class
        * `instance()`

    * `org.lightsleep.helper.TypeConverter` Class
        * `TypeConverter(TypeConverter<ST, MT> typeConverter1, TypeConverter<MT, DT> typeConverter2)`
