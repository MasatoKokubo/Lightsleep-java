### NEW Features ###
* You can now generate the following SQLs.
    * `SELECT SQL` with `WITH` clauses  
        ```
        WITH W1(...) AS (
          SELECT ...
        )
        SELECT ... FROM W1 ...
        ```
    * Recursive `SELECT SQL`  
        ```
        WITH RECURSIVE W1(...) AS (
          SELECT ...
          UNION ALL 
          SELECT ...
        ) SELECT ... FROM W1 ...
        ```
    * `INSERT SQL` with subquery  
        ```
        INSERT INTO ... (...) SELECT ... FROM ...
        ```
    * Join subqueries  
        ```
        SELECT ... FROM ... INNER JOIN (SELECT ...) ...
        ```

### Added Methods ###
* `org.lightsleep.Sql` class
    * `String queryName()`
    * `Sql<E> innerJoin(Sql<?> joinSql, String tableAlias, Condition on)`
    * `Sql<E> innerJoin(Sql<?> joinSql, String tableAlias, String on, Object... arguments)`
    * `Sql<E> leftJoin(Sql<?> joinSql, String tableAlias, Condition on)`
    * `Sql<E> leftJoin(Sql<?> joinSql, String tableAlias, String on, Object... arguments)`
    * `Sql<E> rightJoin(Sql<?> joinSql, String tableAlias, Condition on)`
    * `Sql<E> rightJoin(Sql<?> joinSql, String tableAlias, String on, Object... arguments)`
    * `Sql<E> with(Sql<?>... withSqls)`
    * `List<Sql<?>> getWithSqls()`
    * `boolean isWithSql()`
    * `Sql<E> recursive(Sql<?> recursiveSql)`
    * `Sql<?> getRecursiveSql()`
    * `boolean isRecursiveSql()`
    * `boolean isInInsertFrom()`
    * `int insert()`

* `org.lightsleep.database.Standard` class
    * `<E> void appendInsertColumns(StringBuilder buff, Sql<E> sql)`
    * `<E> void appendInsertValues(StringBuilder buff, Sql<E> sql, List<Object> parameters)`
    * `<E> void appendUpdateColumnsAndValues(StringBuilder buff, Sql<E> sql, List<Object> parameters)`

* `org.lightsleep.helper.JoinInfo<JE>` class
    * `JoinInfo# (JoinType joinType, Sql<JE> joinSql, String tableAlias, Condition on)`
    * `Sql<JE> joinSql()`

* `org.lightsleep.helper.TypeConverter<ST, DT>` class
    * `static <ST, DT> TypeConverter<ST, DT> of(Class<ST> sourceType, Class<DT> destinType, Function<? super ST, ? extends DT> function)`
    * `static <ST, MT, DT> TypeConverter<ST, DT> of(Map<String, TypeConverter<?, ?>> typeConverterMap, Class<ST> sourceType, Class<MT> middleType, Class<DT> destinType)`
    * `static <ST, MT, DT> TypeConverter<ST, DT> of(Map<String, TypeConverter<?, ?>> typeConverterMap, Class<ST> sourceType, Class<MT> middleType, Class<DT> destinType, Function<? super MT, ? extends DT> function)`

### Deleted Interfaces ###
* `org.lightsleep.entity.Composite`
* `org.lightsleep.entity.PostLoad`
* `org.lightsleep.entity.PreStore`

### Deleted Methods ###
* `org.lightsleep.Sq` class
    * `Sql<E> setColumns(Set<String> propertyNames)`
    * `Sql<E> setColumns(Class<?> resultClass)`
    * `Sql<E> doIf(boolean condition, Consumer<Sql<E>> action, Consumer<Sql<E>> elseAction)`

### Methods with modified argument or return value ###
* `org.lightsleep.database.Database<ST, DT>` interface
    * `<E> String selectSql(Sql<E> sql, List<Object> parameters)`  
    -> `<E> CharSequence selectSql(Sql<E> sql, List<Object> parameters)`
    * `<E> String subSelectSql(Sql<E> sql, List<Object> parameters)`  
    -> `<E,OE> CharSequence subSelectSql(Sql<E> sql, Sql<OE> outerSql, List<Object> parameters)`
    * `<E> String subSelectSql(Sql<E> sql, Supplier<CharSequence> columnsSupplier, List<Object> parameters)`  
    -> `<E,OE> CharSequence subSelectSql(Sql<E> sql, Sql<OE> outerSql, Supplier<CharSequence> columnsSupplier, List<Object> parameters)`
    * `<E> String insertSql(Sql<E> sql, List<Object> parameters)`  
    -> `<E> CharSequence insertSql(Sql<E> sql, List<Object> parameters)`
    * `<E> String updateSql(Sql<E> sql, List<Object> parameters)`  
    -> `<E> CharSequence updateSql(Sql<E> sql, List<Object> parameters)`
    * `<E> String deleteSql(Sql<E> sql, List<Object> parameters)`  
    -> `<E> CharSequence deleteSql(Sql<E> sql, List<Object> parameters)`

