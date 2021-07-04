### **Added Features** ###
* Generates SELECT SQL using **subqueries** in the **FROM clause**.
* Generates **UNION SQL**

### **Specification Change** ###
* Changed the specification when calling the `Sql#columns(String ...)` method multiple times.  
    **Prior to this version:** Columns of the argument value are accumulated.  
    **This version:** Replaced by the columns of the argument value.

### **Added Methods** ###
* `org.lightsleep.Sql` Class
    * `Sql<E> columns(Collection<String> propertyNames)`
    * `<RE> Sql<E> columns(Class<RE> resultClass)`
    * `Sql<E> from(Sql<?> fromSql)`
    * `Sql<?> getFrom()`
    * `<SE> Sql<E> where(Sql<SE> subSql, String content)`
    * `<SE> Sql<E> and(Sql<SE> subSql, String content)`
    * `<SE> Sql<E> or(Sql<SE> subSql, String content)`
    * `<SE> Sql<E> having(Sql<SE> subSql, String content)`
    * `<UE> Sql<E> union(Sql<UE> unionSql)`
    * `<UE> Sql<E> unionAll(Sql<UE> unionSql)`
    * `List<Sql<?>> getUnionSqls()`
    * `boolean isUnionAll()`

* `org.lightsleep.Condition` Interface
    * `static <E, SE> Condition of(Sql<E> outerSql, Sql<SE> subSql, String content)`
    * `default <K> Condition and(K entity)`
    * `default <E, SE> Condition and(Sql<E> outerSql, Sql<SE> subSql, String content)`
    * `default <K> Condition or(K entity)`
    * `default <E, SE> Condition or(Sql<E> outerSql, Sql<SE> subSql, String content)`

* `org.lightsleep.SubqueryCondition` Class
    * `<E> SubqueryCondition(Sql<E> outerSql, Sql<SE> subSql, Expression expression)`

### **Deprecated Methods** ###
* `org.lightsleep.Sql` Class
    * `setColumns(Set<String> propertyNames)`
    * `setColumns(Class<?> resultClass)`
