// Sql.java
// (C) 2016 Masato Kokubo

package org.lightsleep;

import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lightsleep.component.Condition;
import org.lightsleep.component.EntityCondition;
import org.lightsleep.component.Expression;
import org.lightsleep.component.GroupBy;
import org.lightsleep.component.OrderBy;
import org.lightsleep.component.SubqueryCondition;
import org.lightsleep.connection.ConnectionWrapper;
import org.lightsleep.entity.PostDelete;
import org.lightsleep.entity.PostInsert;
import org.lightsleep.entity.PostSelect;
import org.lightsleep.entity.PostUpdate;
import org.lightsleep.entity.PreDelete;
import org.lightsleep.entity.PreInsert;
import org.lightsleep.entity.PreUpdate;
import org.lightsleep.helper.Accessor;
import org.lightsleep.helper.ColumnInfo;
import org.lightsleep.helper.ConvertException;
import org.lightsleep.helper.EntityInfo;
import org.lightsleep.helper.JoinInfo;
import org.lightsleep.helper.Resource;
import org.lightsleep.helper.SqlColumnInfo;
import org.lightsleep.helper.SqlEntityInfo;
import org.lightsleep.helper.Utils;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

/**
 * The class to build and execute SQLs.
 *
 * <div class="exampleTitle"><span>Java Example</span></div>
 * <div class="exampleCode"><pre>
 * var contacts = new ArrayList&lt;&gt;();
 * Transaction.execute(conn -&gt;
 *     new <b>Sql</b>&lt;&gt;(Contact.class)
 *         .<b>where</b>("{lastName}={}", "Apple")
 *         .<b>connection</b>(conn)
 *         .<b>select</b>(contacts::add)
 * );
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 * List&lt;Contact&gt; contacts = []
 * Transaction.execute {
 *     new <b>Sql</b>&lt;&gt;(Contact)
 *         .<b>where</b>('{lastName}={}', 'Apple')
 *         .<b>connection</b>(it)
 *         .<b>select</b>({contacts &lt;&lt; it})
 * }
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Generated SQL</span></div>
 * <div class="exampleCode"><pre>
 * SELECT id, lastName, firstName, ... FROM Contact WHERE lastName='Apple'
 * </pre></div>
 *
 * @param <E> the type of the entity related to the main table
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
@SuppressWarnings("unchecked")
public class Sql<E> implements Cloneable, SqlEntityInfo<E> {
    /**
     * The wait value of forever
     * @since 1.9.0
     */
    public static final int FOREVER = Integer.MAX_VALUE;

    // The logger
    protected static final Logger logger = LoggerFactory.getLogger(Sql.class);

    // Class resources
    private static final Resource resource = new Resource(Sql.class);
    private static final String messageUnionCalled      = resource.getString("messageUnionCalled"); // since 3.1.0
    private static final String messageUnionAllCalled   = resource.getString("messageUnionAllCalled"); // since 3.1.0
    private static final String messageNoWhereCondition = resource.getString("messageNoWhereCondition");
    private static final String messageNoConnection     = resource.getString("messageNoConnection");

    private static final String messageSelected0Rows = resource.getString("messageSelected0Rows");
    private static final String messageSelectedRow   = resource.getString("messageSelectedRow");
    private static final String messageSelectedRows  = resource.getString("messageSelectedRows");

    private static final String messageNeedFromSql   = resource.getString("messageNeedFromSql"); // since 4.0.0
    private static final String messageInserted0Rows = resource.getString("messageInserted0Rows"); // since 3.2.0
    private static final String messageInsertedRow   = resource.getString("messageInsertedRow"); // since 3.2.0
    private static final String messageInsertedRows  = resource.getString("messageInsertedRows"); // since 3.2.0

    private static final String messageUpdated0Rows = resource.getString("messageUpdated0Rows");
    private static final String messageUpdatedRow   = resource.getString("messageUpdatedRow");
    private static final String messageUpdatedRows  = resource.getString("messageUpdatedRows");

    private static final String messageDeleted0Rows = resource.getString("messageDeleted0Rows"); // since 3.2.0
    private static final String messageDeletedRow   = resource.getString("messageDeletedRow"); // since 3.2.0
    private static final String messageDeletedRows  = resource.getString("messageDeletedRows"); // since 3.2.0

    protected static final String messageGet       = resource.getString("messageGet");
    protected static final String messageClose     = resource.getString("messageClose");
    protected static final String messageStart     = resource.getString("messageStart");
    protected static final String messageEnd       = resource.getString("messageEnd");
    protected static final String messageCommit    = resource.getString("messageCommit");
    protected static final String messageRollback  = resource.getString("messageRollback");

    // The entity information map
    private static final Map<Class<?>, EntityInfo<?>> entityInfoMap = new ConcurrentHashMap<>();

    private static int sqlNo = 1;

    // The entity information
    private transient final EntityInfo<E> entityInfo;

    // The table alias
    private String tableAlias = "";

    // The entity that are referenced from expressions
    private E entity;

    // With DISTINCT or not
    private boolean distinct = false;

    // The select columns
    private Set<String> columns = new HashSet<>();

    // The expression map (property name : expression)
    private final Map<String, Expression> expressionMap = new HashMap<>();

    // @since 3.1.0
    // Sql object used to generate <i>FROM</i> clause
    private Sql<?> fromSql = null;

    // The join informations
    private List<JoinInfo<?>> joinInfos = new ArrayList<>();

    // SQL entity information map
    private final Map<String, SqlEntityInfo<?>> sqlEntityInfoMap = new LinkedHashMap<>();

    // The WHERE condition
    private Condition where = Condition.EMPTY;

    // The GROUP BY info.
    private GroupBy groupBy = new GroupBy();

    // The HAVING condition
    private Condition having = Condition.EMPTY;

    // @since 3.1.0
    // Sql objects used to generate <i>UNION</i> SQL
    private List<Sql<?>> unionSqls = new ArrayList<>();

    // @since 3.1.0
    // Sql objects used to generate <i>UNION</i> SQL
    // Generates <i>UNION ALL</i> SQL if true, <i>UNION</i> SQL otherwise
    private boolean unionAll = false;

    // @since 4.0.0
    // Sql objects used to generate <i>WITH</i> clause
    private List<Sql<?>> withSqls = new ArrayList<>();

    // @since 4.0.0
    // 1 for the first withSql, 2 for the second withSql, ...
    private int withSqlIndex = 0;

    // @since 4.0.0
    // Sql objects used to generate RECURSIVE clause
    private Sql<?> recursiveSql = null;
 
    // @since 4.0.0
    // true if this is a recursive Sql object, false otherwise
    private boolean isRecursiveSql = false;

   // The ORDER BY information
    private OrderBy orderBy = new OrderBy();

    // The LIMIT value
    private int limit = Integer.MAX_VALUE;

    // The OFFSET value
    private int offset = 0;

    // Whether with FOR UPDATE
    private boolean forUpdate = false;

    // The WAIT time (sec)
    private int waitTime = FOREVER;

    // since 4.0.0
    // true if this is used in a <i>FROM</i> clause, false otherwise
    private boolean isInInsertFrom;

    // The connection wrapper @since 2.0.0
    private transient ConnectionWrapper connection;

    // The generated SQL @since 1.5.0
//4.0.0
//  private transient String generatedSql;
    private transient CharSequence generatedSql;
////

    // For storing doIf method condition @since 3.0.0
    private transient Boolean doIfCondition;

    /**
     * Returns the entity information related to the specified entity class.
     *
     * @param <E> the type of the entity related to the main table
     * @param entityClass the entity class
     * @return the information of the entity
     *
     * @throws NullPointerException if <b>entityClass</b> is <b>null</b>
     *
     * @see #entityInfo()
     */
    public static <E> EntityInfo<E> getEntityInfo(Class<E> entityClass) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        return (EntityInfo<E>)entityInfoMap.computeIfAbsent(entityClass, key -> new EntityInfo<>(key));
    }

    /**
     * Constructs a new <b>Sql</b>.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     *     new Sql&lt;&gt;(Contact.class)
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     *     new Sql&lt;&gt;(Contact)
     * </pre></div>
     *
     * @throws NullPointerException if <b>entityClass</b> is <b>null</b>
     *
     * @param entityClass the entity class
     */
    public Sql(Class<E> entityClass) {
        this(entityClass, "");
    }

    /**
     * Constructs a new <b>Sql</b>.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     *     new Sql&lt;&gt;(Contact.class, "C")
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     *     new Sql&lt;&gt;(Contact, 'C')
     * </pre></div>
     *
     * @param entityClass the entity class
     * @param tableAlias the table alias
     *
     * @throws NullPointerException if <b>entityClass</b> or <b>tableAlias</b> is <b>null</b>
     */
    public Sql(Class<E> entityClass, String tableAlias) {
        entityInfo = getEntityInfo(Objects.requireNonNull(entityClass, "entityClass is null"));
        tableAlias(tableAlias);
    }

    @Override
    public Sql<E> clone() {
        Sql<E> sql = new Sql<>(entityClass(), tableAlias);

        sql.entity         = entity;
        sql.distinct       = distinct;
        sql.columns  .addAll(columns);
        sql.fromSql        = fromSql;
        sql.joinInfos.addAll(joinInfos);
        sql.where          = where;
        sql.groupBy        = groupBy.clone();
        sql.having         = having;
        sql.withSqls .addAll(withSqls); // since 4.0.0
        sql.withSqlIndex   = withSqlIndex;
        sql.isRecursiveSql = isRecursiveSql;
        sql.unionSqls.addAll(unionSqls);
        sql.unionAll       = unionAll;
        sql.orderBy        = orderBy.clone();
        sql.limit          = limit;
        sql.offset         = offset;
        sql.forUpdate      = forUpdate;
        sql.waitTime       = waitTime;
        sql.isInInsertFrom = isInInsertFrom; // since 4.0.0
        sql.connection     = connection;
        sql.generatedSql   = generatedSql;

        expressionMap.entrySet()
            .forEach(entry -> sql.expressionMap.put(entry.getKey(), entry.getValue()));
        sqlEntityInfoMap.entrySet()
            .forEach(entry -> sql.sqlEntityInfoMap.put(entry.getKey(), entry.getValue()));

        return sql;
    }

    /**
     * @see #getEntityInfo(Class)
     */
    @Override
    public EntityInfo<E> entityInfo() {
        return entityInfo;
    }

    /**
     * Returns the entity class.
     *
     * @return the entity class
     */
    public Class<E> entityClass() {
        return entityInfo.entityClass();
    }

    /**
     * Sets the table alias.
     *
     * @param tableAlias the table alias
     * @return this object
     *
     * @since 3.1.0
     */
    private Sql<E> tableAlias(String tableAlias) {
        sqlEntityInfoMap.remove(this.tableAlias);
        this.tableAlias = Objects.requireNonNull(tableAlias, "tableAlias");
        addSqlEntityInfo(this);
        return this;
    }

    /**
     * @see #Sql(Class, String)
     */
    @Override
    public String tableAlias() {
        return tableAlias;
    }

    /**
     * Returns the subquery name in the <i>WITH</i> clause.
     * <p>
     * Returns an empty string ("") if this object is not used in the <i>WITH</i> clause;
     * Otherwise, if the constructor argument has a table alias, returns it,
     * a name based on the order of <i>WITH</i> clauses (W1, W2, W3, ...) otherwise.
     * </p>
     *
     * @return the subquery name in the <i>WITH</i> clause
     *
     * @since 4.0.0
     */
    public String queryName() {
        return withSqlIndex <= 0 ? "" : !tableAlias.isEmpty() ? tableAlias : "W" + withSqlIndex;
    }

    /**
     * @see #setEntity(Object)
     */
    @Override
    public E entity() {
        return entity;
    }

    /**
     * Sets the entity that is referenced in expressions.
     *
     * @param entity an entity
     * @return this object
     *
     * @see #entity()
     */
    public Sql<E> setEntity(E entity) {
        this.entity = entity;
        return this;
    }

    /**
     * Appends <i>DISTINCT</i> to the <i>SELECT</i> SQL.
     *
     * @return this object
     *
     * @see #isDistinct()
     */
    public Sql<E> distinct() {
        distinct = true;
        return this;
    }

    /**
     * Returns <b>true</b> if appends <i>DISTINCT</i> to <i>SELECT</i> SQL, <b>false</b> otherwise.
     *
     * @return <b>true</b> if appends <i>DISTINCT</i> to <i>SELECT</i> SQL, <b>false</b> otherwise
     *
     * @see #distinct()
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * Specifies target columns for <i>SELECT</i> and <i>UPDATE</i> SQL.
     *
     * <p>
     * Also sets them if they are not set in <b>Sql</b> objects set by the <b>from</b>, <b>union</b> or <b>unionAll</b> methods,
     * </p>
     *
     * <p>
     * You can also be specified <i>"*"</i> or <i>"&lt;table alias&gt;.*"</i>.
     * If this method is not invoked it will be in the same as <i>"*"</i> is specified.
     * </p>
     *
     * <div class="exampleTitle"><span>Java Example 1</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class)
     *         .<b>columns("lastName", "firstName")</b>
     *         .where("{lastName}={}", "Apple")
     *         .connection(conn)
     *         .select(contacts::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example 1</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact)
     *         .<b>columns('lastName', 'firstName')</b>
     *         .where('{lastName}={}', 'Apple')
     *         .connection(it)
     *         .select({contacts &lt;&lt; it})
     * }
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Java Example 2</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * var phones = new ArrayList&lt;Phone&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class, "C")
     *         .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
     *         .<b>columns("C.id", "P.*")</b>
     *         .connection(conn)
     *         .&lt;Phone&gt;select(contacts::add, phones::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example 2</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * List&lt;Phone&gt; phones = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact, 'C')
     *         .innerJoin(Phone, 'P', '{P.contactId}={C.id}')
     *         .<b>columns('C.id', 'P.*')</b>
     *         .connection(it)
     *         .&lt;Phone&gt;select({contacts &lt;&lt; it}, {phones &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @param propertyNames an array of property names related to the columns
     * @return this object
     *
     * @throws NullPointerException if <b>propertyNames</b> or any element of <b>propertyNames</b> is <b>null</b>
     *
     * @see #columns(Collection)
     * @see #columns(Class)
     * @see #getColumns()
     */
    public Sql<E> columns(String... propertyNames) {
        return columns(Arrays.stream(Objects.requireNonNull(propertyNames, "propertyNames is null")));
    }

    /**
     * Specifies target columns for <i>SELECT</i> and <i>UPDATE</i> SQL.
     *
     * <p>
     * Also sets them if they are not set in <b>Sql</b> objects set by the <b>from</b>, <b>union</b> or <b>unionAll</b> methods,
     * </p>
     *
     * <p>
     * You can also be specified <i>"*"</i> or <i>"&lt;table alias&gt;.*"</i>.
     * If this method is not invoked it will be in the same as <i>"*"</i> is specified.
     * </p>
     *
     * @param propertyNames a collection of property names related to the columns
     * @return this object
     *
     * @throws NullPointerException if <b>propertyNames</b> or any element of <b>propertyNames</b> is <b>null</b>
     *
     * @since 3.1.0
     * @see #columns(String...)
     * @see #columns(Class)
     * @see #getColumns()
     */
    public Sql<E> columns(Collection<String> propertyNames) {
        return columns(Objects.requireNonNull(propertyNames, "propertyNames is null").stream());
    }

    /**
     * Specifies target columns for <i>SELECT</i> and <i>UPDATE</i> SQL.
     *
     * @param <RE> the type of the result entity
     * @param resultClass a entity class containing set of property names to specify
     * @return this object
     *
     * @since 3.1.0
     * @see #columns(String...)
     * @see #columns(Collection)
     * @see #getColumns()
     */
    public <RE> Sql<E> columns(Class<RE> resultClass) {
        List<String> propertyNames = getEntityInfo(resultClass).accessor().valuePropertyNames();
        return tableAlias.isEmpty()
            ? columns(propertyNames)
            : columns(
                propertyNames.stream()
                    .map(propertyName -> tableAlias + '.' + propertyName));
    }

    /**
     * Specifies target columns for <i>SELECT</i> and <i>UPDATE</i> SQL.
     *
     * <p>
     * Also sets them if they are not set in <b>Sql</b> objects set by the <b>from</b>, <b>union</b> or <b>unionAll</b> methods,
     * </p>
     *
     * <p>
     * You can also be specified <i>"*"</i> or <i>"&lt;table alias&gt;.*"</i>.
     * If this method is not invoked it will be in the same as <i>"*"</i> is specified.
     * </p>
     *
     * @param propertyNamesStream a stream of property names related to the columns
     * @return this object
     *
     * @throws NullPointerException if <b>propertyNames</b> or any element of <b>propertyNames</b> is <b>null</b>
     *
     * @since 3.1.0
     * @see #columns(String...)
     * @see #columns(Collection)
     * @see #columns(Class)
     * @see #getColumns()
     */
    private Sql<E> columns(Stream<String> propertyNamesStream) {
        columns.clear();
        propertyNamesStream.forEach(columns::add);

        // synchronize columns with fromSql
        synchronizeColumns();

        return this;
    }

    /**
     * Returns property names related target columns for <i>SELECT</i> and <i>UPDATE</i> SQL.
     *
     * @return a set of property names
     *
     * @see #columns(String...)
     * @see #columns(Collection)
     * @see #columns(Class)
     */
    public Set<String> getColumns() {
        return columns;
    }

    /**
     * Associates the property with the specified expression.
     *
     * <p>
     * If <b>expression</b> is empty, releases the previous association of <b>propertyName</b>.
     * </p>
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class)
     *         .<b>expression("firstName", "'['||{firstName}||']'")</b>
     *         .where("{lastName}={}", "Orange")
     *         .connection(conn)
     *         .select(contacts::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact)
     *         .<b>expression('firstName', "'['||{firstName}||']'")</b>
     *         .where('{lastName}={}', 'Orange')
     *         .connection(it)
     *         .select({contacts &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @param propertyName the property name
     * @param expression the expression
     * @return this object
     *
     * @throws NullPointerException if <b>propertyName</b> or <b>expression</b> is <b>null</b>
     *
     * @see #getExpression(String)
     */
    public Sql<E> expression(String propertyName, Expression expression) {
        Objects.requireNonNull(propertyName, "propertyName is null");
        Objects.requireNonNull(expression, "expression is null");

        if (expression.isEmpty())
            expressionMap.remove(propertyName);
        else
            expressionMap.put(propertyName, expression);

        return this;
    }

    /**
     * Associates the property with the specified expression.
     *
     * <p>
     * If the expression is empty, releases the previous association of <b>propertyName</b>.
     * </p>
     *
     * @param propertyName the property name
     * @param content the content of the expression
     * @param arguments arguments of the expression
     * @return this object
     *
     * @throws NullPointerException if <b>propertyName</b>, <b>content</b> or <b>arguments</b> is <b>null</b>
     *
     * @see #getExpression(String)
     * @see Expression#Expression(String, Object...)
     */
    public Sql<E> expression(String propertyName, String content, Object... arguments) {
        return expression(propertyName, new Expression(content, arguments));
    }

    /**
     * Returns the expression associated <b>propertyName</b> or <b>Expression.EMPTY</b> if not associated.
     *
     * @param propertyName the property name
     * @return the expression associated <b>propertyName</b> or <b>Expression.EMPTY</b>
     *
     * @throws NullPointerException if <b>propertyName</b> is <b>null</b>
     *
     * @see #expression(String, Expression)
     * @see #expression(String, String, Object...)
     */
    public Expression getExpression(String propertyName) {
        Objects.requireNonNull(propertyName, "propertyName is null");

        return expressionMap.getOrDefault(propertyName, Expression.EMPTY);
    }

    /**
     * Specifies the <i>FROM</i> clause of <i>SELECT</i> SQL as a subquery.
     *
     * @param fromSql <b>Sql</b> object to generate the <i>FROM</i> clause
     * @return this object
     *
     * @throws NullPointerException if <b>fromSql</b> is <b>null</b>
     *
     * @since 3.1.0
     */
    public Sql<E> from(Sql<?> fromSql) {
        this.fromSql = Objects.requireNonNull(fromSql, "fromSql is null");

        // synchronize table aliases with fromSql
        synchronizeTableAliases();

        // synchronize columns with fromSql
        synchronizeColumns();

        if (fromSql.where.isEmpty())
            fromSql.where = Condition.ALL;

        return this;
    }

    /**
     * Returns <b>Sql</b> object to generate a <i>FROM</i> clause of <i>SELECT</i> SQL or <b>null</b> if not specified.
     *
     * @return <b>Sql</b> object to generate a <i>FROM</i> clause of <i>SELECT</i> SQL or <b>null</b>
     *
     * @since 3.1.0
     */
    public Sql<?> getFrom() {
        return fromSql;
    }

    /**
     * Add the information of the table that join with <i>INNER JOIN</i>.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * var phones = new ArrayList&lt;Phone&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class, "C")
     *         .<b>innerJoin(Phone.class, "P", "{P.contactId}={C.id}")</b>
     *         .connection(conn)
     *         .&lt;Phone&gt;select(contacts::add, phones::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * List&lt;Phone&gt; phones = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact, 'C')
     *         .<b>innerJoin(Phone, 'P', '{P.contactId}={C.id}')</b>
     *         .connection(it)
     *         .&lt;Phone&gt;select({contacts &lt;&lt; it}, {phones &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @param entityClass the entity class related to the table to join
     * @param tableAlias an alias of the table to join
     * @param on the join condition
     * @return this object
     *
     * @throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
     *
     * @see #getJoinInfos()
     * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
     */
    public Sql<E> innerJoin(Class<?> entityClass, String tableAlias, Condition on) {
        return join(JoinInfo.JoinType.INNER, entityClass, tableAlias, on);
    }

    /**
     * Add the information of the table that join with <i>INNER JOIN</i>.
     *
     * @param entityClass the entity class related to the table to join
     * @param tableAlias an alias of the table to join
     * @param on the join condition
     * @param arguments the arguments of the join condition
     * @return this object
     *
     * @throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b>, <b>on</b> or <b>arguments</b> is <b>null</b>
     *
     * @see #getJoinInfos()
     * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
     */
    public Sql<E> innerJoin(Class<?> entityClass, String tableAlias, String on, Object... arguments) {
        return join(JoinInfo.JoinType.INNER, entityClass, tableAlias, Condition.of(on, arguments));
    }

    /**
     * Add the information of the table that join with <i>LEFT OUTER JOIN</i>.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * var phones = new ArrayList&lt;Phone&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class, "C")
     *         .<b>leftJoin(Phone.class, "P", "{P.contactId}={C.id}")</b>
     *         .connection(conn)
     *         .&lt;Phone&gt;select(contacts::add, phones::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * List&lt;Phone&gt; phones = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact, 'C')
     *         .<b>leftJoin(Phone, 'P', '{P.contactId}={C.id}')</b>
     *         .connection(it)
     *         .&lt;Phone&gt;select({contacts &lt;&lt; it}, {phones &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @param entityClass the entity class related to the table to join
     * @param tableAlias an alias of the table to join
     * @param on the join condition
     * @return this object
     *
     * @throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
     *
     * @see #getJoinInfos()
     * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
     */
    public Sql<E> leftJoin(Class<?> entityClass, String tableAlias, Condition on) {
        return join(JoinInfo.JoinType.LEFT, entityClass, tableAlias, on);
    }

    /**
     * Add the information of the table that join with <i>LEFT OUTER JOIN</i>.
     *
     * @param entityClass the entity class related to the table to join
     * @param tableAlias an alias of the table to join
     * @param on the join condition
     * @param arguments the arguments of the join condition
     * @return this object
     *
     * @throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b>, <b>on</b> or <b>arguments</b> is <b>null</b>
     *
     * @see #getJoinInfos()
     * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
     */
    public Sql<E> leftJoin(Class<?> entityClass, String tableAlias, String on, Object... arguments) {
        return join(JoinInfo.JoinType.LEFT, entityClass, tableAlias, Condition.of(on, arguments));
    }

    /**
     * Add the information of the table that join with <i>RIGHT OUTER JOIN</i>.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * var phones = new ArrayList&lt;Phone&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class, "C")
     *         .<b>rightJoin(Phone.class, "P", "{P.contactId}={C.id}")</b>
     *         .connection(conn)
     *         .&lt;Phone&gt;select(contacts::add, phones::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * List&lt;Phone&gt; phones = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact, 'C')
     *         .<b>rightJoin(Phone, 'P', '{P.contactId}={C.id}')</b>
     *         .connection(it)
     *         .&lt;Phone&gt;select({contacts &lt;&lt; it}, {phones &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @param entityClass the entity class related to the table to join
     * @param tableAlias an alias of the table to join
     * @param on the join condition
     * @return this object
     *
     * @throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
     *
     * @see #getJoinInfos()
     * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
     */
    public Sql<E> rightJoin(Class<?> entityClass, String tableAlias, Condition on) {
        return join(JoinInfo.JoinType.RIGHT, entityClass, tableAlias, on);
    }

    /**
     * Add the information of the table that join with <i>RIGHT OUTER JOIN</i>.
     *
     * @param entityClass the entity class related to the table to join
     * @param tableAlias an alias of the table to join
     * @param on the join condition
     * @param arguments the arguments of the join condition
     * @return this object
     *
     * @throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b>, <b>on</b> or <b>arguments</b> is <b>null</b>
     *
     * @see #getJoinInfos()
     * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
     */
    public Sql<E> rightJoin(Class<?> entityClass, String tableAlias, String on, Object... arguments) {
        return join(JoinInfo.JoinType.RIGHT, entityClass, tableAlias, Condition.of(on, arguments));
    }

    /**
     * Add the information of the table that join with
      *   <i>INNER JOIN</i>, <i>LEFT OUTER JOIN</i> or <i>RIGHT OUTER JOIN</i>.
     *
     * @param joinType the join type
     * @param entityClass the entity class related to the table to join
     * @param tableAlias an alias of the table to join
     * @param on the join condition
     * @return this object
     *
     * @throws NullPointerException if <b>joinType</b>, <b>entityClass</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
     */
    private Sql<E> join(JoinInfo.JoinType joinType, Class<?> entityClass, String tableAlias, Condition on) {
        EntityInfo<?> entityInfo = getEntityInfo(entityClass);
        JoinInfo<?> joinInfo = new JoinInfo<>(joinType, entityInfo, tableAlias, on);
        addSqlEntityInfo(joinInfo);
        joinInfos.add(joinInfo);
        return this;
    }

    /**
     * Add the information of the table that join with <i>INNER JOIN</i>.
     *
     * @param joinSql the <b>Sql</b> object to join
     * @param tableAlias an alias of the table to join
     * @param on the join condition
     * @return this object
     *
     * @throws NullPointerException if <b>joinSql</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
     *
     * @see #getJoinInfos()
     * @see JoinInfo#JoinInfo(JoinInfo.JoinType, Sql, String, Condition)
     * @since 4.0.0
     */
    public Sql<E> innerJoin(Sql<?> joinSql, String tableAlias, Condition on) {
        return join(JoinInfo.JoinType.INNER, joinSql, tableAlias, on);
    }

    /**
     * Add the subquery to join with <i>INNER JOIN</i>.
     *
     * @param joinSql the <b>Sql</b> object to join
     * @param tableAlias an alias of the table to join
     * @param on the join condition
     * @param arguments the arguments of the join condition
     * @return this object
     *
     * @throws NullPointerException if <b>joinSql</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
     *
     * @see #getJoinInfos()
     * @see JoinInfo#JoinInfo(JoinInfo.JoinType, Sql, String, Condition)
     * @since 4.0.0
     */
    public Sql<E> innerJoin(Sql<?> joinSql, String tableAlias, String on, Object... arguments) {
        return join(JoinInfo.JoinType.INNER, joinSql, tableAlias, Condition.of(on, arguments));
    }

    /**
     * Add the subquery to join with <i>LEFT OUTER JOIN</i>.
     *
     * @param joinSql the <b>Sql</b> object to join
     * @param tableAlias an alias of the table to join
     * @param on the join condition
     * @return this object
     *
     * @throws NullPointerException if <b>joinSql</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
     *
     * @see #getJoinInfos()
     * @see JoinInfo#JoinInfo(JoinInfo.JoinType, Sql, String, Condition)
     * @since 4.0.0
     */
    public Sql<E> leftJoin(Sql<?> joinSql, String tableAlias, Condition on) {
        return join(JoinInfo.JoinType.LEFT, joinSql, tableAlias, on);
    }

    /**
     * Add the subquery to join with <i>LEFT OUTER JOIN</i>.
     *
     * @param joinSql the <b>Sql</b> object to join
     * @param tableAlias an alias of the table to join
     * @param on the join condition
     * @param arguments the arguments of the join condition
     * @return this object
     *
     * @throws NullPointerException if <b>joinSql</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
     *
     * @see #getJoinInfos()
     * @see JoinInfo#JoinInfo(JoinInfo.JoinType, Sql, String, Condition)
     * @since 4.0.0
     */
    public Sql<E> leftJoin(Sql<?> joinSql, String tableAlias, String on, Object... arguments) {
        return join(JoinInfo.JoinType.LEFT, joinSql, tableAlias, Condition.of(on, arguments));
    }

    /**
     * Add the subquery to join with <i>RIGHT OUTER JOIN</i>.
     *
     * @param joinSql the <b>Sql</b> object to join
     * @param tableAlias an alias of the table to join
     * @param on the join condition
     * @return this object
     *
     * @throws NullPointerException if <b>joinSql</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
     *
     * @see #getJoinInfos()
     * @see JoinInfo#JoinInfo(JoinInfo.JoinType, Sql, String, Condition)
     * @since 4.0.0
     */
    public Sql<E> rightJoin(Sql<?> joinSql, String tableAlias, Condition on) {
        return join(JoinInfo.JoinType.RIGHT, joinSql, tableAlias, on);
    }

    /**
     * Add the subquery to join with <i>RIGHT OUTER JOIN</i>.
     *
     * @param joinSql the <b>Sql</b> object to join
     * @param tableAlias an alias of the table to join
     * @param on the join condition
     * @param arguments the arguments of the join condition
     * @return this object
     *
     * @throws NullPointerException if <b>joinSql</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
     *
     * @see #getJoinInfos()
     * @see JoinInfo#JoinInfo(JoinInfo.JoinType, Sql, String, Condition)
     * @since 4.0.0
     */
    public Sql<E> rightJoin(Sql<?> joinSql, String tableAlias, String on, Object... arguments) {
        return join(JoinInfo.JoinType.RIGHT, joinSql, tableAlias, Condition.of(on, arguments));
    }

    /**
     * Add the subquery to join with
     *   <i>INNER JOIN</i>, <i>LEFT OUTER JOIN</i> or <i>RIGHT OUTER JOIN</i>.
     *
     * @param joinType the join type
     * @param joinSql the <b>Sql</b> object to join
     * @param tableAlias an alias of the table to join
     * @param on the join condition
     * @return this object
     *
     * @throws NullPointerException if <b>joinType</b>, <b>joinSql</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
     * @since 4.0.0
     */
    private Sql<E> join(JoinInfo.JoinType joinType, Sql<?> joinSql, String tableAlias, Condition on) {
        JoinInfo<?> joinInfo = new JoinInfo<>(joinType, joinSql, tableAlias, on);
        addSqlEntityInfo(joinInfo);
        joinInfos.add(joinInfo);
        return this;
    }

    /**
     * Returns a list of join information that was added.
     *
     * @return a list of join information
     *
     * @see #innerJoin(Class, String, Condition)
     * @see #innerJoin(Class, String, String, Object...)
     * @see #leftJoin(Class, String, Condition)
     * @see #leftJoin(Class, String, String, Object...)
     * @see #rightJoin(Class, String, Condition)
     * @see #rightJoin(Class, String, String, Object...)
     */
    public List<JoinInfo<?>> getJoinInfos() {
        return joinInfos;
    }

    /**
     * Specifies the condition of the <i>WHERE</i> clause.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class)
     *         .<b>where("{birthday} IS NULL")</b>
     *         .connection(conn)
     *         .select(contacts::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact)
     *         .<b>where('{birthday} IS NULL')</b>
     *         .connection(it)
     *         .select({contacts &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @param condition a condition
     * @return this object
     *
     * @throws NullPointerException if <b>condition</b> is <b>null</b>
     *
     * @see #getWhere()
     */
    public Sql<E> where(Condition condition) {
        where = Objects.requireNonNull(condition, "condition is null");
        return this;
    }

    /**
     * Specifies the condition of the <i>WHERE</i> clause by an <b>Expression</b>.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * int id = 1;
     * var contact = new Contact[1];
     * Transaction.execute(conn -&gt;
     *     contact[0] = new Sql&lt;&gt;(Contact.class)
     *         .<b>where("{id}={}", id)</b>
     *         .connection(conn).select().orElse(null)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * int id = 1
     * Contact contact
     * Transaction.execute {
     *     contact = new Sql&lt;&gt;(Contact)
     *         .<b>where('{id}={}', id)</b>
     *         .connection(it)
     *         .select().orElse(null)
     * }
     * </pre></div>
     *
     * @param content the content of the <b>Expression</b>
     * @param arguments arguments of the <b>Expression</b>
     * @return this object
     *
     * @throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
     *
     * @see #getWhere()
     * @see Condition#of(String, Object...)
     */
    public Sql<E> where(String content, Object... arguments) {
        where = Condition.of(content, arguments);
        return this;
    }

    /**
     * Specifies the condition of the <i>WHERE</i> clause by an <b>EntityCondition</b>.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contact = new Contact[1];
     * Transaction.execute(conn -&gt;
     *     contact[0] = new Sql&lt;&gt;(Contact.class)
     *         .<b>where(new ContactKey(2))</b>
     *         .connection(conn)
     *         .select().orElse(null)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * Contact contact
     * Transaction.execute {
     *     Contact key = new Contact()
     *     key.id = 2
     *     contact = new Sql&lt;&gt;(Contact)
     *         .<b>where(key)</b>
     *         .connection(it)
     *         .select().orElse(null)
     * }
     * </pre></div>
     *
     * @param <K> the type of the entity
     * @param entity the entity of the EntityCondition
     * @return this object
     *
     * @throws NullPointerException if <b>entity</b> is <b>null</b>
     *
     * @see #getWhere()
     * @see Condition#of(Object)
     */
    public <K> Sql<E> where(K entity) {
        where = Condition.of(entity);
        return this;
    }

    /**
     * Specifies the condition of the <i>WHERE</i> clause by a SubqueryCondition.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class, "C")
     *         .<b>where("EXISTS",
     *              new Sql&lt;&gt;(Phone.class, "P")
     *                  .where("{P.contactId}={C.id}")
     *                  .and("{P.content} LIKE {}", "0800001%")
     *         )</b>
     *         .connection(conn)
     *         .select(contacts::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact, 'C')
     *         .<b>where('EXISTS',
     *              new Sql&lt;&gt;(Phone, 'P')
     *                  .where('{P.contactId}={C.id}')
     *                  .and('{P.content} LIKE {}', '0800001%')
     *         )</b>
     *         .connection(it)
     *         .select({contacts &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @param <SE> the type of the entity related to the subquery
     * @param content the left part from the <i>SELECT</i> statement of a subquery
     * @param subSql the <b>Sql</b> object for the subquery
     * @return this object
     *
     * @throws NullPointerException if <b>content</b> or <b>subSql</b> is <b>null</b>
     *
     * @see #where(Sql, String)
     * @see #getWhere()
     * @see Condition#of(String, Sql, Sql)
     * @see SubqueryCondition#SubqueryCondition(Expression, Sql, Sql)
     */
    public <SE> Sql<E> where(String content, Sql<SE> subSql) {
        where = Condition.of(content, this, subSql);
        return this;
    }

    /**
     * Specifies the condition of the <i>WHERE</i> clause by a SubqueryCondition.
     *
     * @param <SE> the type of the entity related to the subquery
     * @param subSql the <b>Sql</b> object for the subquery
     * @param content the right part from the <i>SELECT</i> statement of a subquery
     * @return this object
     *
     * @throws NullPointerException if <b>subSql</b> or <b>content</b> is <b>null</b>
     *
     * @since 3.1.0
     * @see #where(String, Sql)
     * @see #getWhere()
     * @see Condition#of(Sql, Sql, String)
     * @see SubqueryCondition#SubqueryCondition(Sql, Sql, Expression)
     */
    public <SE> Sql<E> where(Sql<SE> subSql, String content) {
        where = Condition.of(this, subSql, content);
        return this;
    }

    /**
     * Returns the condition of the <i>WHERE</i> clause that was specified.
     *
     * @return the condition of the <i>WHERE</i> clause
     *
     * @see #where(Condition)
     * @see #where(Object)
     * @see #where(String, Object...)
     * @see #where(String, Sql)
     */
    public Condition getWhere() {
        return where;
    }

    /**
     * Adds the condition using <i>AND</i> to the condition of the <i>HAVING</i> clause
     * if after you invoke <b>having</b> method, 
     * to the condition of the <i>WHERE</i> clause otherwise.
     *
     * @param condition a condition
     * @return this object
     *
     * @throws NullPointerException if <b>condition</b> is <b>null</b>
     *
     * @see #and(String, Object...)
     * @see #and(String, Sql)
     * @see #and(Sql, String)
     * @see Condition#and(Condition)
     */
    public Sql<E> and(Condition condition) {
        Objects.requireNonNull(condition, "condition is null");

        if (having.isEmpty())
            where = where.and(condition);
        else
            having = having.and(condition);
        return this;
    }

    /**
     * Adds a <b>Expression</b> condition using <i>AND</i> to the condition of the <i>HAVING</i> clause
     * if after you invoke <b>having</b> method, 
     * to the condition of the <i>WHERE</i> clause otherwise.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class)
     *         .where("{lastName}={}", "Apple")
     *         .<b>and("{firstName}={}", "Akiyo")</b>
     *         .connection(conn)
     *         .select(contacts::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact)
     *         .where('{lastName}={}', 'Apple')
     *         .<b>and('{firstName}={}', 'Akiyo')</b>
     *         .connection(it)
     *         .select({contacts &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @param content the content of the <b>Expression</b>
     * @param arguments the arguments of the <b>Expression</b>
     * @return this object
     *
     * @throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
     *
     * @see #and(Condition)
     * @see #and(String, Sql)
     * @see #and(Sql, String)
     * @see Condition#and(Condition)
     * @see Condition#of(String, Object...)
     */
    public Sql<E> and(String content, Object... arguments) {
        return and(Condition.of(content, arguments));
    }

    /**
     * Adds a <b>SubqueryCondition</b> using <i>AND</i> to the condition of the <i>HAVING</i> clause
     * if after you invoke <b>having</b> method, 
     * to the condition of the <i>WHERE</i> clause otherwise.
     *
     * @param <SE> the type of the entity related to the subquery
     * @param content the left part from the <i>SELECT</i> statement of the subquery
     * @param subSql the <b>Sql</b> object for the subquery
     * @return this object
     *
     * @throws NullPointerException if <b>content</b> or <b>subSql</b> is <b>null</b>
     *
     * @see #and(Condition)
     * @see #and(String, Object...)
     * @see #and(Sql, String)
     * @see Condition#and(String, Sql, Sql)
     * @see Condition#and(Condition)
     * @see Condition#of(String, Sql, Sql)
     * @see SubqueryCondition#SubqueryCondition(Expression, Sql, Sql)
     */
    public <SE> Sql<E> and(String content, Sql<SE> subSql) {
        return and(Condition.of(content, this, subSql));
    }

    /**
     * Adds a <b>SubqueryCondition</b> using <i>AND</i> to the condition of the <i>HAVING</i> clause
     * if after you invoke <b>having</b> method, 
     * to the condition of the <i>WHERE</i> clause otherwise.
     *
     * @param <SE> the type of the entity related to the subquery
     * @param subSql the <b>Sql</b> object for the subquery
     * @param content the right part from the <i>SELECT</i> statement of the subquery
     * @return this object
     *
     * @throws NullPointerException if <b>subSql</b> or <b>content</b> is <b>null</b>
     *
     * @since 3.1.0
     * @see #and(Condition)
     * @see #and(String, Object...)
     * @see #and(String, Sql)
     * @see Condition#and(Condition)
     * @see Condition#of(Sql, Sql, String)
     * @see SubqueryCondition#SubqueryCondition(Sql, Sql, Expression)
     */
    public <SE> Sql<E> and(Sql<SE> subSql, String content) {
        return and(Condition.of(this, subSql, content));
    }

    /**
     * Adds the condition using <i>OR</i> to the condition of the <i>HAVING</i> clause
     * if after you invoke <b>having</b> method, 
     * to the condition of the <i>WHERE</i> clause otherwise.
     *
     * @param condition a condition
     * @return this object
     *
     * @throws NullPointerException if <b>condition</b> is <b>null</b>
     *
     * @see #or(String, Object...)
     * @see #or(String, Sql)
     * @see #or(Sql, String)
     * @see Condition#or(Condition)
     */
    public Sql<E> or(Condition condition) {
        Objects.requireNonNull(condition, "condition is null");

        if (having.isEmpty())
            where = where.or(condition);
        else
            having = having.or(condition);
        return this;
    }

    /**
     * Adds a <b>Expression</b> condition using <i>OR</i> to the condition of the <i>HAVING</i> clause
     * if after you invoke <b>having</b> method, 
     * to the condition of the <i>WHERE</i> clause otherwise.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class)
     *         .where("{lastName}={}", "Apple")
     *         .<b>or("{lastName}={}", "Orange")</b>
     *         .connection(conn)
     *         .select(contacts::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact)
     *         .where('{lastName}={}', 'Apple')
     *         .<b>or('{lastName}={}', 'Orange')</b>
     *         .connection(it)
     *         .select({contacts &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @param content the content of the <b>Expression</b>
     * @param arguments arguments of the <b>Expression</b>
     * @return this object
     *
     * @throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
     *
     * @see #or(Condition)
     * @see #or(String, Sql)
     * @see #or(Sql, String)
     * @see Condition#or(String, Object...)
     */
    public Sql<E> or(String content, Object... arguments) {
        return or(Condition.of(content, arguments));
    }

    /**
     * Adds a <b>SubqueryCondition</b> using <i>OR</i> to the condition of the <i>HAVING</i> clause
     * if after you invoke <b>having</b> method, 
     * to the condition of the <i>WHERE</i> clause otherwise.
     *
     * @param <SE> the type of the entity related to the subquery
     * @param content the left part from the <i>SELECT</i> statement of the subquery
     * @param subSql the <b>Sql</b> object for the subquery
     * @return this object
     *
     * @throws NullPointerException if <b>content</b> or <b>subSql</b> is <b>null</b>
     *
     * @see #or(Condition)
     * @see #or(String, Object...)
     * @see #or(Sql, String)
     * @see Condition#or(String, Sql, Sql)
     * @see Condition#or(Condition)
     * @see Condition#of(String, Sql, Sql)
     * @see SubqueryCondition#SubqueryCondition(Expression, Sql, Sql)
     */
    public <SE> Sql<E> or(String content, Sql<SE> subSql) {
        return or(Condition.of(content, this, subSql));
    }

    /**
     * Adds a <b>SubqueryCondition</b> using <i>OR</i> to the condition of the <i>HAVING</i> clause
     * if after you invoke <b>having</b> method, 
     * to the condition of the <i>WHERE</i> clause otherwise.
     *
     * @param <SE> the type of the entity related to the subquery
     * @param content the left part from the <i>SELECT</i> statement of the subquery
     * @param subSql the <b>Sql</b> object for the subquery
     * @return this object
     *
     * @throws NullPointerException if <b>subSql</b> or <b>content</b> is <b>null</b>
     *
     * @since 3.1.0
     * @see #or(Condition)
     * @see #or(String, Object...)
     * @see #or(String, Sql)
     * @see Condition#or(Sql, Sql, String)
     * @see Condition#or(Condition)
     * @see Condition#of(Sql, Sql, String)
     * @see SubqueryCondition#SubqueryCondition(Sql, Sql, Expression)
     */
    public <SE> Sql<E> or(Sql<SE> subSql, String content) {
        return or(Condition.of(this, subSql, content));
    }

    /**
     * Specifies an element of the <i>GROUP BY</i> clause.
     *
     * @param content the content of the <b>Expression</b>
     * @param arguments arguments of the <b>Expression</b>
     * @return this object
     *
     * @throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
     *
     * @see #getGroupBy()
     * @see #setGroupBy(GroupBy)
     * @see GroupBy
     * @see Expression#Expression(String, Object...)
     */
    public Sql<E> groupBy(String content, Object... arguments) {
        groupBy = groupBy.add(new Expression(content, arguments));
        return this;
    }

    /**
     * Sets the contents of the <i>GROUP BY</i> clause.
     *
     * @param groupBy the contents of the <i>GROUP BY</i> clause
     * @return this object
     *
     * @since 1.9.1
     * @see #groupBy(String, Object...)
     * @see #getGroupBy()
     */
    public Sql<E> setGroupBy(GroupBy groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    /**
     * Returns the contents of the <i>GROUP BY</i> clause.
     *
     * @return the contents of the <i>GROUP BY</i> clause
     *
     * @see #groupBy(String, Object...)
     * @see #setGroupBy(GroupBy)
     */
    public GroupBy getGroupBy() {
        return groupBy;
    }

    /**
     * Specifies the condition of the <i>HAVING</i> clause.
     *
     * @param condition the condition
     * @return this object
     *
     * @throws NullPointerException if <b>condition</b> is <b>null</b>
     *
     * @see #getHaving()
     */
    public Sql<E> having(Condition condition) {
        having = Objects.requireNonNull(condition, "condition is null");
        return this;
    }

    /**
     * Specifies the condition of the <i>HAVING</i> clause by an <b>Expression</b>.
     *
     * @param content the content of the <b>Expression</b>
     * @param arguments arguments of the <b>Expression</b>
     * @return this object
     *
     * @throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
     *
     * @see #getHaving()
     * @see Condition#of(String, Object...)
     */
    public Sql<E> having(String content, Object... arguments) {
        having = Condition.of(content, arguments);
        return this;
    }

    /**
     * Specifies the condition of the <i>HAVING</i> clause by a <b>SubqueryCondition</b>.
     *
     * @param <SE> the type of the entity related to the subquery
     * @param content the left part from the <i>SELECT</i> statement of the subquery
     * @param subSql the <b>Sql</b> object for the subquery
     * @return this object
     *
     * @throws NullPointerException if <b>content</b> or <b>subSql</b> is <b>null</b>
     *
     * @see #having(Sql, String)
     * @see #getHaving()
     * @see Condition#of(String, Sql, Sql)
     */
    public <SE> Sql<E> having(String content, Sql<SE> subSql) {
        having = Condition.of(content, this, subSql);
        return this;
    }

    /**
     * Specifies the condition of the <i>HAVING</i> clause by a <b>SubqueryCondition</b>.
     *
     * @param <SE> the type of the entity related to the subquery
     * @param subSql the <b>Sql</b> object for the subquery
     * @param content the right part from the <i>SELECT</i> statement of the subquery
     * @return this object
     *
     * @throws NullPointerException if <b>content</b> or <b>subSql</b> is <b>null</b>
     *
     * @since 3.1.0
     * @see #having(String, Sql)
     * @see #getHaving()
     * @see Condition#of(Sql, Sql, String)
     */
    public <SE> Sql<E> having(Sql<SE> subSql, String content) {
        having = Condition.of(this, subSql, content);
        return this;
    }

    /**
     * Returns the condition of the <i>HAVING</i> clause that was specified.
     *
     * @return the condition of the <i>HAVING</i> clause
     *
     * @see #having(Condition)
     * @see #having(String, Object...)
     * @see #having(String, Sql)
     */
    public Condition getHaving() {
        return having;
    }

    /**
     * Specifies <i>WITH</i> clauses.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var nodes = new ArrayList&lt;Node&gt;();
     * var nodeSql = new Sql&lt;&gt;(Node.class)
     *    .where("{name} LIKE {}", "%-%");
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Node.class)
     *         .<b>with(nodeSql)</b>
     *         .from(nodeSql)
     *         .connection(conn)
     *         .select(nodes::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Node&gt; nodes = []
     * def nodeSql = new Sql&lt;&gt;(Node)
     *    .where('{name} LIKE {}', '%-%')
     * Transaction.execute {
     *     new Sql&lt;&gt;(Node)
     *         .<b>with(nodeSql)</b>
     *         .from(nodeSql)
     *         .connection(it)
     *         .select({nodes &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @param withSqls <b>Sql</b> objects for <i>WITH</i> clauses
     * @return this object
     *
     * @since 4.0.0
     * @see #getWithSqls()
     * @see #isWithSql()
     * @see #recursive(Sql)
     * @see #getRecursiveSql()
     * @see #isRecursiveSql()
     */
    public Sql<E> with(Sql<?>... withSqls) {
        Objects.requireNonNull(withSqls, "withSqls");
        this.withSqls.clear();
        for (Sql<?> withSql : withSqls) {
            this.withSqls.add(withSql);
            withSql.withSqlIndex = this.withSqls.size();

            if (withSql.recursiveSql != null)
                withSql.recursiveSql.addSqlEntityInfo(newSqlEntityInfo(withSql.entityClass(), withSql.queryName()));

            // synchronize columns with recursiveSql
            withSql.synchronizeColumns();

            if (withSql.where.isEmpty())
                withSql.where = Condition.ALL;
        }
        return this;
    }

    /**
     * Returns <b>Sql</b> objects for <i>WITH</i> clauses.
     *
     * @return <b>Sql</b> objects for <i>WITH</i> clauses
     *
     * @since 4.0.0
     * @see #with(Sql...)
     * @see #isWithSql()
     * @see #recursive(Sql)
     * @see #getRecursiveSql()
     * @see #isRecursiveSql()
     */
    public List<Sql<?>> getWithSqls() {
        return Collections.unmodifiableList(withSqls);
    }

    /**
     * Returns <b>true</b> if this object is used in <i>WITH</i> clause, <b>false</b> otherwise.
     *
     * @return <b>true</b> if this object is used in <i>WITH</i> clause, <b>false</b> otherwise
     *
     * @since 4.0.0
     * @see #with(Sql...)
     * @see #getWithSqls()
     * @see #recursive(Sql)
     * @see #getRecursiveSql()
     * @see #isRecursiveSql()
     */
    public boolean isWithSql() {
        return withSqlIndex > 0;
    }

    /**
     * Specifies <i>RECURSIVE</i> SQL.
     * 
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var nodes = new ArrayList&lt;Node&gt;();
     * var nodeSql = new Sql&lt;&gt;(Node.class).where(rootNode)
     *     .<b>recursive(new Sql&lt;&gt;(Node.class, "node").where("{node.parentId}={W1.id}"))</b>;
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Node.class)
     *         .with(nodeSql)
     *         .from(nodeSql)
     *         .connection(conn)
     *         .select(nodes::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Node&gt; nodes = []
     * def nodeSql = new Sql&lt;&gt;(Node).where(rootNode)
     *     .<b>recursive(new Sql&lt;&gt;(Node, 'node').where('{node.parentId}={W1.id}'))</b>
     * Transaction.execute {
     *     new Sql&lt;&gt;(Node)
     *         .with(nodeSql)
     *         .from(nodeSql)
     *         .connection(it)
     *         .select({nodes &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @param recursiveSql the <b>Sql</b> object for <i>RECURSIVE</i> SQL
     * @return this object
     *
     * @since 4.0.0
     * @see #with(Sql...)
     * @see #getWithSqls()
     * @see #isWithSql()
     * @see #getRecursiveSql()
     * @see #isRecursiveSql()
     */
    public Sql<E> recursive(Sql<?> recursiveSql) {
        this.recursiveSql = Objects.requireNonNull(recursiveSql, "recursiveSql");
        recursiveSql.isRecursiveSql = true;
        return this;
    }

    /**
     * Returns the <b>Sql</b> object for <i>RECURSIVE</i> SQL.
     * 
     * @return the <b>Sql</b> object for <i>RECURSIVE</i> SQL
     *
     * @since 4.0.0
     * @see #with(Sql...)
     * @see #getWithSqls()
     * @see #isWithSql()
     * @see #recursive(Sql)
     * @see #isRecursiveSql()
     */
    public Sql<?> getRecursiveSql() {
        return recursiveSql;
    }

    /**
     * Returns <b>true</b> if this object is used in <i>RECURSIVE</i> SQL, <b>false</b> otherwise.
     *
     * @return <b>true</b> if this object is used in <i>RECURSIVE</i> SQL, <b>false</b> otherwise
     *
     * @since 4.0.0
     * @see #with(Sql...)
     * @see #getWithSqls()
     * @see #isWithSql()
     * @see #recursive(Sql)
     * @see #getRecursiveSql()
     */
    public boolean isRecursiveSql() {
        return isRecursiveSql;
    }

    /**
     * Adds the <b>Sql</b> object that generate a <i>SELECT</i> SQL for a <i>UNION</i> SQL component.
     *
     * @param unionSql the <b>Sql</b> object that generate a <i>SELECT</i> SQL for a <i>UNION</i> SQL component
     * @return this object
     *
     * @throws IllegalStateException <b>unionAll</b> method has already been invoked
     *
     * @since 3.1.0
     * @see #unionAll(Sql)
     */
    public Sql<E> union(Sql<?> unionSql) {
        return unionOrUnionAll(unionSql, false);
    }

    /**
     * Adds the <b>Sql</b> object that generate a <i>SELECT</i> SQL for a <i>UNION ALL</i> SQL component.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var leaves = new ArrayList&lt;Leaf&gt;();
     * Transaction.execute(conn -&gt;
     *    new Sql&lt;&gt;(Leaf.class)
     *        .<b>unionAll(new Sql&lt;&gt;(LeafA.class).where("{parentId}={}", rootNode.getId()))</b>
     *        .<b>unionAll(new Sql&lt;&gt;(LeafB.class).where("{parentId}={}", rootNode.getId()))</b>
     *        .connection(conn)
     *        .select(leaves::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Leaf&gt; leaves = []
     * Transaction.execute {
     *    new Sql&lt;&gt;(Leaf)
     *        .<b>unionAll(new Sql&lt;&gt;(LeafA).where("{parentId}={}", rootNode.id))</b>
     *        .<b>unionAll(new Sql&lt;&gt;(LeafB).where("{parentId}={}", rootNode.id))</b>
     *        .connection(it)
     *        .select({leaves &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @param unionSql the <b>Sql</b> object that generate a <i>SELECT</i> SQL for a <i>UNION ALL</i> SQL component
     * @return this object
     *
     * @throws IllegalStateException <b>union</b> method has already been invoked
     *
     * @since 3.1.0
     * @see #union(Sql)
     */
    public Sql<E> unionAll(Sql<?> unionSql) {
        return unionOrUnionAll(unionSql, true);
    }

    /**
     * Adds the <b>Sql</b> object that generate a <i>SELECT</i> SQL for a UNION or <i>UNION ALL</i> SQL component.
     *
     * @param unionSql the <b>Sql</b> object that generate a <i>SELECT</i> SQL for a UNION or <i>UNION ALL</i> SQL component
     * @param unionAll generate <i>UNION ALL</i> SQL if <b>true</b>, <i>UNION</i> SQL otherwise
     * @return this object
     *
     * @throws IllegalStateException if both <b>union</b> and <b>unionAll</b> are invoked.
     *
     * @since 3.1.0
     */
    private Sql<E> unionOrUnionAll(Sql<?> unionSql, boolean unionAll) {
        Objects.requireNonNull(unionSql, "unionSql is null");
        if (unionSqls.size() == 0) {
            this.unionAll = unionAll;
        } else {
            if (this.unionAll != unionAll)
                throw new IllegalStateException(
                    MessageFormat.format(unionAll ? messageUnionCalled : messageUnionAllCalled,
                        entityInfo.entityClass().getName()));
        }

    // 4.0.0
        if (unionSql.isWithSql() && unionSql.fromSql == null)
            // uses WITH clause
            unionSql = unionSql.clone().from(unionSql).where(Condition.ALL);
    ////

        unionSqls.add(unionSql);

        // synchronize table aliases with unionSqls
        synchronizeTableAliases();

        // synchronize columns with unionSqls
        synchronizeColumns();

    // 4.0.0
        if (unionSql.where.isEmpty())
            unionSql.where = Condition.ALL;
    ////

        return this;
    }

    /**
     * Returns a list of <b>Sql</b> objects that generate <i>SELECT</i> SQL for a UNION or <i>UNION ALL</i> SQL component.
     *
     * @return all Sql objects to generate UNION or <i>UNION ALL</i> SQL
     *
     * @since 3.1.0
     */
    public List<Sql<?>> getUnionSqls() {
    // 4.0.0
    //  return unionSqls;
        return Collections.unmodifiableList(unionSqls);
    ////
    }

    /**
     * Returns <b>true</b> if generates <i>UNION ALL</i> SQL, <b>false</b> if generates <i>UNION</i> SQL.
     *
     * @return <b>true</b> if generates <i>UNION ALL</i> SQL, <b>false</b> if generates <i>UNION</i> SQL
     *
     * @since 3.1.0
     */
    public boolean isUnionAll() {
        return unionAll;
    }

    /**
     * Specifies an element of the <i>ORDER BY</i> clause.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class)
     *         .<b>orderBy("{lastName}")</b>
     *         .<b>orderBy("{firstName}")</b>
     *         .connection(conn)
     *         .select(contacts::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact)
     *         .<b>orderBy('{lastName}')</b>
     *         .<b>orderBy('{firstName}')</b>
     *         .connection(it)
     *         .select({contacts &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @param content the content of the <b>OrderBy.Element</b>
     * @param arguments the arguments of the <b>OrderBy.Element</b>
     * @return this object
     *
     * @throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
     *
     * @see #asc()
     * @see #desc()
     * @see #setOrderBy(OrderBy)
     * @see #getOrderBy()
     * @see OrderBy#add(OrderBy.Element)
     * @see OrderBy.Element#Element(String, Object...)
     */
    public Sql<E> orderBy(String content, Object... arguments) {
        orderBy = orderBy.add(new OrderBy.Element(content, arguments));
        return this;
    }

    /**
     * Sets the element of the last specified <i>ORDER BY</i> clause in ascending order.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class)
     *         .orderBy("{id}")<b>.asc()</b>
     *         .connection(conn)
     *         .select(contacts::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact)
     *         .orderBy('{id}')<b>.asc()</b>
     *         .connection(it)
     *         .select({contacts &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @return this object
     *
     * @see #orderBy(String, Object...)
     * @see #desc()
     * @see #getOrderBy()
     * @see OrderBy#asc
     */
    public Sql<E> asc() {
        orderBy.asc();
        return this;
    }

    /**
     * Sets the element of the last specified <i>ORDER BY</i> clause in descending order.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class)
     *         .orderBy("{id}")<b>.desc()</b>
     *         .connection(conn)
     *         .select(contacts::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact)
     *         .orderBy('{id}')<b>.desc()</b>
     *         .connection(it)
     *         .select({contacts &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @return this object
     *
     * @see #orderBy(java.lang.String, java.lang.Object...)
     * @see #asc()
     * @see #getOrderBy()
     * @see OrderBy#desc
     */
    public Sql<E> desc() {
        orderBy.desc();
        return this;
    }

    /**
     * Sets the contents of the <i>ORDER BY</i> clause.
     *
     * @param orderBy the contents of the <i>ORDER BY</i> clause
     *
     * @return this object
     *
     * @since 1.9.1
     * @see #orderBy(java.lang.String, java.lang.Object...)
     * @see #getOrderBy()
     */
    public Sql<E> setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    /**
     * Returns the contents of the <i>ORDER BY</i> clause that was specified.
     *
     * @return the contents of the <i>ORDER BY</i> clause
     *
     * @see #orderBy(java.lang.String, java.lang.Object...)
     * @see #setOrderBy(OrderBy)
     */
    public OrderBy getOrderBy() {
        return orderBy;
    }

    /**
     * Specifies the <i>LIMIT</i> value.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class)
     *         .<b>limit(5)</b>
     *         .connection(conn)
     *         .select(contacts::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact)
     *         .<b>limit(5)</b>
     *         .connection(it)
     *         .select({contacts &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @param limit the <i>LIMIT</i> value
     * @return this object
     *
     * @see #getLimit()
     */
    public Sql<E> limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Returns the <i>LIMIT</i> value that was specified.
     *
     * @return the <i>LIMIT</i> value
     *
     * @see #limit(int)
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Specifies the <i>OFFSET</i> value.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class)
     *         .limit(5)<b>.offset(5)</b>
     *         .connection(conn)
     *         .select(contacts::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact)
     *         .limit(5)<b>.offset(5)</b>
     *         .connection(it)
     *         .select({contacts &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @param offset the <i>OFFSET</i> value
     * @return this object
     *
     * @see #getOffset()
     */
    public Sql<E> offset(int offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Returns the <i>OFFSET</i> value that was specified.
     *
     * @return the <i>OFFSET</i> value
     *
     * @see #offset(int)
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Specifies that appends <i>FOR UPDATE</i> to <i>SELECT</i> SQL.
     *
     * @return this object
     *
     * @see #isForUpdate()
     */
    public Sql<E> forUpdate() {
        forUpdate = true;
        return this;
    }

    /**
     * Returns <b>true</b> if appends <i>FOR UPDATE</i>, <b>false</b> otherwise.
     *
     * @return <b>true</b> if appends <i>FOR UPDATE</i>, <b>false</b> otherwise
     *
     * @see #forUpdate()
     */
    public boolean isForUpdate() {
        return forUpdate;
    }

    /**
     * Specifies that appends <i>NOWAIT</i> to <i>SELECT</i> SQL.
     *
     * @return this object
     *
     * @see #wait()
     * @see #getWaitTime()
     * @see #isNoWait()
     * @see #isWaitForever()
     */
    public Sql<E> noWait() {
        waitTime = 0;
        return this;
    }

    /**
     * Specifies that append <i>WAIT n</i> to <i>SELECT</i> SQL.
     *
     * @param waitTime the wait time (second)
     * @return this object
     *
     * @since 1.9.0
     * @see #noWait()
     * @see #getWaitTime()
     * @see #isNoWait()
     * @see #isWaitForever()
     */
    public Sql<E> wait(int waitTime) {
        this.waitTime = waitTime;
        return this;
    }

    /**
     * Returns the argument of <b>WAIT</b> of <i>SELECT</i> SQL.
     *
     * @return the wait time (seccond)
     *
     * @since 1.9.0
     * @see #noWait()
     * @see #wait(int)
     * @see #isNoWait()
     * @see #isWaitForever()
     */
    public int getWaitTime() {
        return waitTime;
    }

    /**
     * Returns <b>true</b> if appends <i>NOWAIT</i> to <i>SELECT</i> SQL, <b>false</b> otherwise.
     *
     * @return <b>true</b> if appends <i>NOWAIT</i> to <i>SELECT</i> SQL, <b>false</b> otherwise
     *
     * @see #noWait()
     * @see #wait(int)
     * @see #getWaitTime()
     * @see #isWaitForever()
     */
    public boolean isNoWait() {
        return waitTime == 0;
    }

    /**
     * Returns <b>true</b> if appends neither <i>NOWAIT</i> nor <i>WAIT n</i> to <i>SELECT</i> SQL, <b>false</b> otherwise.
     *
     * @return <b>true</b> if appends neither <i>NOWAIT</i> nor <i>WAIT n</i> to <i>SELECT</i> SQL, <b>false</b> otherwise
     *
     * @since 1.9.0
     * @see #noWait()
     * @see #wait(int)
     * @see #getWaitTime()
     * @see #isNoWait()
     */
    public boolean isWaitForever() {
        return waitTime == FOREVER;
    }

    /**
     * Returns <b>true</b> if this is used in <i>FROM</i> clause of <i>INSERT</i> SQL, <b>false</b> otherwise.
     *
     * @return <b>true</b> if this is used in <i>FROM</i> clause of <i>INSERT</i> SQL, <b>false</b> otherwise
     *
     * @since 4.0.0
     */
    public boolean isInInsertFrom() {
        return isInInsertFrom;
    }

    /**
     * Specifies the connection wrapper used by select, insert, update and delete methods.
     *
     * <p>
     * <span class="simpleTagLabel">Caution:</span>
     * Call this method before using methods for accessing database
     * (select, insert, update and delete) added in version 2.0.0.
     * </p>
     *
     * @param connection the connection wrapper
     * @return this object
     *
     * @since 2.0.0
     * @see #getConnection()
     */
    public Sql<E> connection(ConnectionWrapper connection) {
        this.connection = Objects.requireNonNull(connection, "connection is null");
        return this;
    }

    /**
     * Returns the connection wrapper.
     *
     * @return the connection wrapper
     *
     * @since 2.1.0
     * @see #connection(ConnectionWrapper)
     */
    public ConnectionWrapper getConnection() {
        return connection;
    }

    /**
     * Returns the last generated SQL.
     *
     * @return the last generated SQL
     *
     * @since 1.8.4
     */
    public String generatedSql() {
    // 4.0.0
    //  return generatedSql;
        return generatedSql.toString();
    ////
    }

    /**
     * Executes <b>action</b>.
     *
     * @param action the action to be executed
     * @return this object
     *
     * @since 2.0.0
     * @see #doIf(boolean, Consumer)
     */
    public Sql<E> doAlways(Consumer<Sql<E>> action) {
        Objects.requireNonNull(action, "action is null").accept(this);

        return this;
    }

    /**
     * Executes <b>action</b> if <b>condition</b> is <b>true</b>.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class, "C")
     *         .connection(conn)
     *         .<b>doIf(!(conn.getDatabase() instanceof SQLite), Sql::forUpdate)</b>
     *         .select(contacts::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact, 'C')
     *         .connection(it)
     *         .<b>doIf(!(conn.database instanceof SQLite)) {it.forUpdate}</b>
     *         .select({contacts &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @param condition the condition of execution
     * @param action the action
     * @return this object
     *
     * @see #doNotIf(boolean, Consumer)
     * @see #doElse(Consumer)
     * @see #doAlways(Consumer)
     */
    public Sql<E> doIf(boolean condition, Consumer<Sql<E>> action) {
        doIfCondition = condition;
        if (condition)
            Objects.requireNonNull(action, "action is null").accept(this);

        return this;
    }

    /**
     * Executes <b>action</b> if <b>condition</b>  is <b>false</b>.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class, "C")
     *         .connection(conn)
     *         .<b>doNotIf(conn.getDatabase() instanceof SQLite, Sql::forUpdate)</b>
     *         .select(contacts::add)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact, 'C')
     *         .connection(it)
     *         .<b>doNotIf(conn.database instanceof SQLite) {it.forUpdate}</b>
     *         .select({contacts &lt;&lt; it})
     * }
     * </pre></div>
     *
     * @param condition the condition of execution
     * @param action the action
     * @return this object
     *
     * @since 3.0.0
     * @see #doIf(boolean, Consumer)
     * @see #doElse(Consumer)
     * @see #doAlways(Consumer)
     */
    public Sql<E> doNotIf(boolean condition, Consumer<Sql<E>> action) {
        return doIf(!condition, action);
    }

    /**
     * Executes <b>elseAction</b> if the condition of <b>doIf(boolean, Consumer)</b> executed before this is <b>false</b>.
     *
     * @param elseAction the action
     * @return this object
     *
     * @since 3.0.0
     * @see #doIf(boolean, Consumer)
     * @see #doNotIf(boolean, Consumer)
     * @see #doAlways(Consumer)
     */
    public Sql<E> doElse(Consumer<Sql<E>> elseAction) {
        if (doIfCondition != null && !doIfCondition) {
            doIfCondition = null;
            Objects.requireNonNull(elseAction, "elseAction is null").accept(this);
        }

        return this;
    }

    /**
     * Returns the <b>SqlEntityInfo</b> object related to the specified table alias.
     *
     * <p>
     * <i>This method is used internally.</i>
     * </p>
     *
     * @param tableAlias the table alias
     * @return the <b>SqlEntityInfo</b> object
     *
     * @throws NullPointerException if <b>tableAlias</b> is <b>null</b>
     */
    public SqlEntityInfo<?> getSqlEntityInfo(String tableAlias) {
        return sqlEntityInfoMap.get(tableAlias);
    }

    /**
     * Adds the <b>SqlEntityInfo</b> object.
     *
     * <p>
     * <i>This method is used internally.</i>
     * </p>
     *
     * @param sqlEntityInfo the <b>SqlEntityInfo</b> object
     */
    public void addSqlEntityInfo(SqlEntityInfo<?> sqlEntityInfo) {
        sqlEntityInfoMap.putIfAbsent(sqlEntityInfo.tableAlias(), sqlEntityInfo);

        if (sqlEntityInfo instanceof Sql) {
            ((Sql<?>)sqlEntityInfo).sqlEntityInfoMap.values().stream()
                .filter(sqlEntityInfo2 -> sqlEntityInfo2 != sqlEntityInfo)
                .forEach(this::addSqlEntityInfo);
        }
    }

    /**
     * Generates and executes a <i>SELECT</i> SQL that joins no tables.
     *
     * <p>
     * Adds following string to <b>columns</b> set
     * if <b>columns</b> method is not invoked and
     * <b>innerJoin</b>, <b>leftJoin</b> or <b>rightJoin</b> method invoked.<br>
     * </p>
     *
     * <ul class="code" style="list-style-type:none">
     *   <li>"&lt;Main Table Alias&gt;.*"</li>
     * </ul>
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class)
     *         .connection(conn)
     *         .<b>select(contacts::add)</b>
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact)
     *         .connection(it)
     *         .<b>select({contacts &lt;&lt; it})</b>
     * }
     * </pre></div>
     *
     * <p>
     * <span class="simpleTagLabel">Caution:</span>
     * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before invoking this method.
     * </p>
     *
     * @param consumer a consumer of the entities created from the <b>ResultSet</b>
     *
     * @throws NullPointerException if <b>consumer</b> is <b>null</b>
     * @throws IllegalStateException if a <b>ConnectionWrapper</b> is not set
     * @throws IllegalStateException if a <i>SELECT</i> SQL without columns was generated
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     *
     * @since 2.0.0
     * @see #selectAs(Class, Consumer)
     */
    public void select(Consumer<? super E> consumer) {
        selectAs(entityInfo.entityClass(), consumer);
    }

    /**
     * Generates and executes a <i>SELECT</i> SQL that joins no tables.
     *
     * <p>
     * Adds following string to <b>columns</b> set
     * if <b>columns</b> method is not invoked and
     * <b>innerJoin</b>, <b>leftJoin</b> or <b>rightJoin</b> method invoked.<br>
     * </p>
     *
     * <ul class="code" style="list-style-type:none">
     *   <li>"&lt;Main Table Alias&gt;.*"</li>
     * </ul>
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contactNames = new ArrayList&lt;ContactName&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class)
     *         .connection(conn)
     *         .<b>selectAs(ContactName.class, contactNames::add)</b>
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;ContactName&gt; contactNames = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact)
     *         .connection(it)
     *         .<b>selectAs(ContactName, {contactNames &lt;&lt; it})</b>
     * }
     * </pre></div>
     *
     * <p>
     * <span class="simpleTagLabel">Caution:</span>
     * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before invoking this method.
     * </p>
     *
     * @param <RE> the type of the result entity
     * @param resultClass the class of the argumrnt of <b>consumer</b>
     * @param consumer a consumer of the entities created from the <b>ResultSet</b>
     *
     * @throws NullPointerException if <b>resultClass</b> or <b>consumer</b> is <b>null</b>
     * @throws IllegalStateException if a <b>ConnectionWrapper</b> is not set
     * @throws IllegalStateException if a <i>SELECT</i> SQL without columns was generated
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     *
     * @since 2.0.0
     * @see #select(Consumer)
     */
    public <RE> void selectAs(Class<RE> resultClass, Consumer<? super RE> consumer) {
        Objects.requireNonNull(resultClass, "resultClass is null");
        Objects.requireNonNull(consumer, "consumer is null");
        if (connection == null)
            throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));

        Sql<E> sql = where.isEmpty() ? clone().where(Condition.ALL) : this;

        if (sql.columns.isEmpty()) {
            if (sql == this) sql = clone();

            if (resultClass == sql.entityInfo.entityClass()) {
                if (sql.joinInfos.size() > 0)
                    sql.columns.add(tableAlias + ".*");
            } else {
                sql.columns(resultClass);
            }
        }

        List<Object> parameters = new ArrayList<>();
        generatedSql = connection.getDatabase().selectSql(sql, parameters);

        SqlEntityInfo<RE> sqlEntityInfo = resultClass == sql.entityInfo.entityClass()
            ? (SqlEntityInfo<RE>)sql
            : newSqlEntityInfo(resultClass, sql.tableAlias);

        sql.executeQuery(generatedSql, parameters, sql.getRowConsumer(sqlEntityInfo, consumer));
    }

    /**
     * Returns a new <b>SqlEntityInfo<b>.
     *
     * @param entityClass the entity class
     * @param tableAlias the table alias
     * @return a new <b>SqlEntityInfo<b>
     *
     * @since 2.0.0
     */
    private <T> SqlEntityInfo<T> newSqlEntityInfo(Class<T> entityClass, String tableAlias) {
        return new SqlEntityInfo<T>() {
            @Override
            public String tableAlias() {
                return tableAlias;
            }

            @Override
            public EntityInfo<T> entityInfo() {
                return getEntityInfo(entityClass);
            }

            @Override
            public T entity() {
                try {
                    return entityClass.getConstructor().newInstance();
                }
                catch (RuntimeException  e) {
                    throw e;
                }
                catch (Exception  e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    /**
     * Generates and executes a <i>SELECT</i> SQL that joins one table.
     *
     * <p>
     * Adds following strings to <b>columns</b> set
     * if <b>columns</b> method is not invoked and
     * <b>innerJoin</b>, <b>leftJoin</b> or <b>rightJoin</b> method invoked more than once.
     * </p>
     *
     * <ul class="code" style="list-style-type:none">
     *   <li>"&lt;Main Table Alias&gt;.*"</li>
     *   <li>"&lt;Joined Table Alias&gt;.*"</li>
     * </ul>
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * var phones   = new ArrayList&lt;Phone&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class, "C")
     *         .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
     *         .connection(conn)
     *         .<b>&lt;Phone&gt;select(contacts::add, phones::add)</b>
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * List&lt;Phone&gt;   phones = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact, 'C')
     *         .innerJoin(Phone, 'P', '{P.contactId}={C.id}')
     *         .connection(it)
     *         .<b>select({contacts &lt;&lt; it}, {phones &lt;&lt; it})</b>
     * }
     * </pre></div>
     *
     * <p>
     * <span class="simpleTagLabel">Caution:</span>
     * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before invoking this method.
     * </p>
     *
     * @param <JE1> the type of the entity related to the joined table
     * @param consumer a consumer of the entities related to the main table created from the <b>ResultSet</b>
     * @param consumer1 a consumer of the entities related to the joined table created from the <b>ResultSet</b>
     *
     * @throws NullPointerException if <b>consumer</b> or <b>consumer1</b> is <b>null</b>
     * @throws IllegalStateException if joinInfo information is less than 1
     * @throws IllegalStateException if a <b>ConnectionWrapper</b> is not set
     * @throws IllegalStateException if a <i>SELECT</i> SQL without columns was generated
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     *
     * @since 2.0.0
     */
    public <JE1> void select(
        Consumer<? super E> consumer,
        Consumer<? super JE1> consumer1) {
        if (joinInfos.size() < 1) throw new IllegalStateException("joinInfos.size < 1");
        if (connection == null)
            throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));

        Sql<E> sql = where.isEmpty() ? clone().where(Condition.ALL) : this;

        if (sql.columns.isEmpty() && sql.joinInfos.size() > 1) {
            if (sql == this) sql = clone();
            sql.columns.add(sql.tableAlias + ".*");
            sql.columns.add(sql.joinInfos.get(0).tableAlias() + ".*");
        }

        List<Object> parameters = new ArrayList<>();
        generatedSql = connection.getDatabase().selectSql(sql, parameters);

        sql.executeQuery(generatedSql, parameters,
            sql.getRowConsumer(sql, consumer)
            .andThen(sql.getRowConsumer((JoinInfo<JE1>)sql.joinInfos.get(0), consumer1))
        );
    }

    /**
     * Generates and executes a <i>SELECT</i> SQL that joins two tables.
     *
     * <p>
     * Adds following strings to <b>columns</b> set
     * if <b>columns</b> method is not invoked and
     * <b>innerJoin</b>, <b>leftJoin</b> or <b>rightJoin</b> method invoked more than twice.
     * </p>
     *
     * <ul class="code" style="list-style-type:none">
     *   <li>"&lt;Main Table Alias&gt;.*"</li>
     *   <li>"&lt;1st Joined Table Alias&gt;.*"</li>
     *   <li>"&lt;2nd Joined Table Alias&gt;.*"</li>
     * </ul>
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts = new ArrayList&lt;Contact&gt;();
     * var phones   = new ArrayList&lt;Phone&gt;();
     * var emails   = new ArrayList&lt;Email&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class, "C")
     *         .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
     *         .innerJoin(Email.class, "E", "{E.contactId}={C.id}")
     *         .connection(conn)
     *         .<b>&lt;Phone, Email&gt;select(contacts::add, phones::add, emails::add)</b>
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * List&lt;Phone&gt;   phones = []
     * List&lt;Email&gt;   emails = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact, 'C')
     *         .innerJoin(Phone, 'P', '{P.contactId}={C.id}')
     *         .innerJoin(Email, 'E', '{E.contactId}={C.id}')
     *         .connection(it)
     *         .<b>select({contacts &lt;&lt; it}, {phones &lt;&lt; it}, {emails &lt;&lt; it})</b>
     * }
     * </pre></div>
     *
     * <p>
     * <span class="simpleTagLabel">Caution:</span>
     * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before invoking this method.
     * </p>
     *
     * @param <JE1> the type of the entity related to the 1st joined table
     * @param <JE2> the type of the entity related to the 2nd joined table
     * @param consumer a consumer of the entities related to the main table created from the <b>ResultSet</b>
     * @param consumer1 a consumer of the entities related to the 1st joined table created from the <b>ResultSet</b>
     * @param consumer2 a consumer of the entities related to the 2nd joined table created from the <b>ResultSet</b>
     *
     * @throws NullPointerException if <b>consumer</b>, <b>consumer1</b> or <b>consumer2</b> is <b>null</b>
     * @throws IllegalStateException if join information is less than 2
     * @throws IllegalStateException if a <b>ConnectionWrapper</b> is not set
     * @throws IllegalStateException if a <i>SELECT</i> SQL without columns was generated
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     *
     * @since 2.0.0
     */
    public <JE1, JE2> void select(
        Consumer<? super E> consumer,
        Consumer<? super JE1> consumer1,
        Consumer<? super JE2> consumer2) {
        if (joinInfos.size() < 2) throw new IllegalStateException("joinInfos.size < 2");
        if (connection == null)
            throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));

        Sql<E> sql = where.isEmpty() ? clone().where(Condition.ALL) : this;

        if (sql.columns.isEmpty() && sql.joinInfos.size() > 2) {
            if (sql == this) sql = clone();
            sql.columns.add(sql.tableAlias + ".*");
            sql.columns.add(sql.joinInfos.get(0).tableAlias() + ".*");
            sql.columns.add(sql.joinInfos.get(1).tableAlias() + ".*");
        }

        List<Object> parameters = new ArrayList<>();
        generatedSql = connection.getDatabase().selectSql(sql, parameters);

        sql.executeQuery(generatedSql, parameters,
            sql.getRowConsumer(sql, consumer)
            .andThen(sql.getRowConsumer((JoinInfo<JE1>)sql.joinInfos.get(0), consumer1))
            .andThen(sql.getRowConsumer((JoinInfo<JE2>)sql.joinInfos.get(1), consumer2))
        );
    }

    /**
     * Generates and executes a <i>SELECT</i> SQL that joins three tables.
     *
     * <p>
     * Adds following strings to <b>columns</b> set
     * if <b>columns</b> method is not invoked and
     * <b>innerJoin</b>, <b>leftJoin</b> or <b>rightJoin</b> method invoked more than 3 times.
     * </p>
     *
     * <ul class="code" style="list-style-type:none">
     *   <li>"&lt;Main Table Alias&gt;.*"</li>
     *   <li>"&lt;1st Joined Table Alias&gt;.*"</li>
     *   <li>"&lt;2nd Joined Table Alias&gt;.*"</li>
     *   <li>"&lt;3rd Joined Table Alias&gt;.*"</li>
     * </ul>
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts  = new ArrayList&lt;Contact&gt;();
     * var phones    = new ArrayList&lt;Phone&gt;();
     * var emails    = new ArrayList&lt;Email&gt;();
     * var addresses = new ArrayList&lt;Address&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class, "C")
     *         .innerJoin(  Phone.class, "P", "{P.contactId}={C.id}")
     *         .innerJoin(  Email.class, "E", "{E.contactId}={C.id}")
     *         .innerJoin(Address.class, "A", "{A.contactId}={C.id}")
     *         .connection(conn)
     *         .<b>&lt;Phone, Email, Address&gt;select(</b>
     *             <b>contacts::add, phones::add, emails::add, addresses::add)</b>
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * List&lt;Phone&gt;   phones = []
     * List&lt;Email&gt;   emails = []
     * List&lt;Address&gt; addresses = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact, 'C')
     *         .innerJoin(Phone  , 'P', '{P.contactId}={C.id}')
     *         .innerJoin(Email  , 'E', '{E.contactId}={C.id}')
     *         .innerJoin(Address, 'A', '{A.contactId}={C.id}')
     *         .connection(it)
     *         .<b>select(</b>
     *             <b>{contacts &lt;&lt; it}, {phones &lt;&lt; it}, {emails &lt;&lt; it}, {addresses &lt;&lt; it})</b>
     * }
     * </pre></div>
     *
     * <p>
     * <span class="simpleTagLabel">Caution:</span>
     * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before invoking this method.
     * </p>
     *
     * @param <JE1> the type of the entity related to the 1st joined table
     * @param <JE2> the type of the entity related to the 2nd joined table
     * @param <JE3> the type of the entity related to the 3rd joined table
     * @param consumer a consumer of the entities related to the main table created from the <b>ResultSet</b>
     * @param consumer1 a consumer of the entities related to the 1st joined table created from the <b>ResultSet</b>
     * @param consumer2 a consumer of the entities related to the 2nd joined table created from the <b>ResultSet</b>
     * @param consumer3 a consumer of the entities related to the 3rd joined table created from the <b>ResultSet</b>
     *
     * @throws NullPointerException if <b>consumer</b>, <b>consumer1</b>, <b>consumer2</b> or <b>consumer3</b> is <b>null</b>
     * @throws IllegalStateException if join information is less than 3
     * @throws IllegalStateException if a <b>ConnectionWrapper</b> is not set
     * @throws IllegalStateException if a <i>SELECT</i> SQL without columns was generated
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     *
     * @since 2.0.0
     */
    public <JE1, JE2, JE3> void select(
        Consumer<? super E> consumer,
        Consumer<? super JE1> consumer1,
        Consumer<? super JE2> consumer2,
        Consumer<? super JE3> consumer3) {
        if (joinInfos.size() < 3) throw new IllegalStateException("joinInfos.size < 3");
        if (connection == null)
            throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));

        Sql<E> sql = where.isEmpty() ? clone().where(Condition.ALL) : this;

        if (columns.isEmpty() && joinInfos.size() > 3) {
            if (sql == this) sql = clone();
            sql.columns.add(sql.tableAlias + ".*");
            sql.columns.add(sql.joinInfos.get(0).tableAlias() + ".*");
            sql.columns.add(sql.joinInfos.get(1).tableAlias() + ".*");
            sql.columns.add(sql.joinInfos.get(2).tableAlias() + ".*");
        }

        List<Object> parameters = new ArrayList<>();
        generatedSql = connection.getDatabase().selectSql(sql, parameters);

        sql.executeQuery(generatedSql, parameters,
            sql.getRowConsumer(sql, consumer)
            .andThen(sql.getRowConsumer((JoinInfo<JE1>)sql.joinInfos.get(0), consumer1))
            .andThen(sql.getRowConsumer((JoinInfo<JE2>)sql.joinInfos.get(1), consumer2))
            .andThen(sql.getRowConsumer((JoinInfo<JE3>)sql.joinInfos.get(2), consumer3))
        );
    }

    /**
     * Generates and executes a <i>SELECT</i> SQL that joins four tables.
     *
     * <p>
     * Adds following strings to <b>columns</b> set
     * if <b>columns</b> method is not invoked and
     * <b>innerJoin</b>, <b>leftJoin</b> or <b>rightJoin</b> method invoked more than 4 times.
     * </p>
     *
     * <ul class="code" style="list-style-type:none">
     *   <li>"&lt;Main Table Alias&gt;.*"</li>
     *   <li>"&lt;1st Joined Table Alias&gt;.*"</li>
     *   <li>"&lt;2nd Joined Table Alias&gt;.*"</li>
     *   <li>"&lt;3rd Joined Table Alias&gt;.*"</li>
     *   <li>"&lt;4th Joined Table Alias&gt;.*"</li>
     * </ul>
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contacts  = new ArrayList&lt;Contact&gt;();
     * var phones    = new ArrayList&lt;Phone&gt;();
     * var emails    = new ArrayList&lt;Email&gt;();
     * var addresses = new ArrayList&lt;Address&gt;();
     * var urls      = new ArrayList&lt;Url&gt;();
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class, "C")
     *         .innerJoin(  Phone.class, "P", "{P.contactId}={C.id}")
     *         .innerJoin(  Email.class, "E", "{E.contactId}={C.id}")
     *         .innerJoin(Address.class, "A", "{A.contactId}={C.id}")
     *         .innerJoin(    Url.class, "U", "{U.contactId}={C.id}")
     *         .connection(conn)
     *         .<b>&lt;Phone, Email, Address, Url&gt;select(</b>
     *             <b>contacts::add, phones::add, emails::add,</b>
     *             <b>addresses::add, urls::add)</b>
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * List&lt;Contact&gt; contacts = []
     * List&lt;Phone&gt;   phones = []
     * List&lt;Email&gt;   emails = []
     * List&lt;Address&gt; addresses = []
     * List&lt;Url&gt;     urls = []
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact, 'C')
     *         .innerJoin(Phone  , 'P', '{P.contactId}={C.id}')
     *         .innerJoin(Email  , 'E', '{E.contactId}={C.id}')
     *         .innerJoin(Address, 'A', '{A.contactId}={C.id}')
     *         .innerJoin(Url    , 'U', '{U.contactId}={C.id}')
     *         .connection(it)
     *         .<b>select(</b>
     *             <b>{contacts &lt;&lt; it}, {phones &lt;&lt; it}, {emails &lt;&lt; it},</b>
     *             <b>{addresses &lt;&lt; it}, {urls &lt;&lt; it})</b>
     * }
     * </pre></div>
     *
     * <p>
     * <span class="simpleTagLabel">Caution:</span>
     * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before invoking this method.
     * </p>
     *
     * @param <JE1> the type of the entity related to the 1st joined table
     * @param <JE2> the type of the entity related to the 2nd joined table
     * @param <JE3> the type of the entity related to the 3rd joined table
     * @param <JE4> the type of the entity related to the 4th joined table
     * @param consumer a consumer of the entities related to the main table created from the <b>ResultSet</b>
     * @param consumer1 a consumer of the entities related to the 1st join table created from the <b>ResultSet</b>
     * @param consumer2 a consumer of the entities related to the 2nd join table created from the <b>ResultSet</b>
     * @param consumer3 a consumer of the entities related to the 3rd join table created from the <b>ResultSet</b>
     * @param consumer4 a consumer of the entities related to the 4th join table created from the <b>ResultSet</b>
     *
     * @throws NullPointerException if <b>consumer</b>, <b>consumer1</b>, <b>consumer2</b>, <b>consumer3</b> or <b>consumer4</b> is <b>null</b>
     * @throws IllegalStateException if join information is less than 4
     * @throws IllegalStateException if a <b>ConnectionWrapper</b> is not set
     * @throws IllegalStateException if a <i>SELECT</i> SQL without columns was generated
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     *
     * @since 2.0.0
     */
    public <JE1, JE2, JE3, JE4> void select(
        Consumer<? super  E > consumer,
        Consumer<? super JE1> consumer1,
        Consumer<? super JE2> consumer2,
        Consumer<? super JE3> consumer3,
        Consumer<? super JE4> consumer4) {
        if (joinInfos.size() < 4) throw new IllegalStateException("joinInfos.size < 4");
        if (connection == null)
            throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));

        Sql<E> sql = where.isEmpty() ? clone().where(Condition.ALL) : this;

        if (sql.columns.isEmpty() && sql.joinInfos.size() > 4) {
            if (sql == this) sql = clone();
            sql.columns.add(sql.tableAlias + ".*");
            sql.columns.add(sql.joinInfos.get(0).tableAlias() + ".*");
            sql.columns.add(sql.joinInfos.get(1).tableAlias() + ".*");
            sql.columns.add(sql.joinInfos.get(2).tableAlias() + ".*");
            sql.columns.add(sql.joinInfos.get(3).tableAlias() + ".*");
        }

        List<Object> parameters = new ArrayList<>();
        generatedSql = connection.getDatabase().selectSql(sql, parameters);

        sql.executeQuery(generatedSql, parameters,
            sql.getRowConsumer(sql, consumer)
            .andThen(sql.getRowConsumer((JoinInfo<JE1>)sql.joinInfos.get(0), consumer1))
            .andThen(sql.getRowConsumer((JoinInfo<JE2>)sql.joinInfos.get(1), consumer2))
            .andThen(sql.getRowConsumer((JoinInfo<JE3>)sql.joinInfos.get(2), consumer3))
            .andThen(sql.getRowConsumer((JoinInfo<JE4>)sql.joinInfos.get(3), consumer4))
        );
    }

    /**
     * Generates and executes a <i>SELECT</i> SQL
     * and returns an <b>Optional</b> of the entity if searched, <b>Optional.empty()</b> otherwise.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contact = new Contact[1];
     * Transaction.execute(conn -&gt;
     *     contact[0] = new Sql&lt;&gt;(Contact.class)
     *         .where("{id}={}", 1)
     *         .connection(conn)
     *         .<b>select()</b>.orElse(null)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * Contact contact
     * Transaction.execute {
     *     contact = new Sql&lt;&gt;(Contact)
     *         .where('{id}={}', 1)
     *         .connection(it)
     *         .<b>select()</b>.orElse(null)
     * }
     * </pre></div>
     *
     * <p>
     * <span class="simpleTagLabel">Caution:</span>
     * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before invoking this method.
     * </p>
     *
     * @return an <b>Optional</b> of the entity if searched, <b>Optional.empty()</b> otherwise
     *
     * @throws IllegalStateException if a <b>ConnectionWrapper</b> is not set
     * @throws IllegalStateException if a <i>SELECT</i> SQL without columns was generated
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     * @throws ManyRowsException if more than one row searched
     *
     * @since 2.0.0
     * @see #selectAs(Class)
     */
    public Optional<E> select() {
        return selectAs(entityInfo.entityClass());
    }

    /**
     * Generates and executes a <i>SELECT</i> SQL
     * and returns an <b>Optional</b> of the entity if searched, <b>Optional.empty()</b> otherwise.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var contactName = new ContactName[1];
     * Transaction.execute(conn -&gt;
     *     contactName[0] = new Sql&lt;&gt;(Contact.class)
     *         .where("{id}={}", 1)
     *         .connection(conn)
     *         .<b>selectAs(ContactName.class)</b>.orElse(null)
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * ContactName contactName
     * Transaction.execute {
     *     contactName = new Sql&lt;&gt;(Contact)
     *         .where('{id}={}', 1)
     *         .connection(it)
     *         .<b>selectAs(ContactName)</b>.orElse(null)
     * }
     * </pre></div>
     *
     * <p>
     * <span class="simpleTagLabel">Caution:</span>
     * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before invoking this method.
     * </p>
     *
     * @param <RE> the type of the result entity
     * @param resultClass the class of entity to as a return value
     * @return an <b>Optional</b> of the entity if searched, <b>Optional.empty()</b> otherwise
     *
     * @throws NullPointerException <b>resultClass</b> is <b>null</b>
     * @throws IllegalStateException if a <b>ConnectionWrapper</b> is not set
     * @throws IllegalStateException if a <i>SELECT</i> SQL without columns was generated
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     * @throws ManyRowsException if more than one row searched
     *
     * @since 2.0.0
     * @see #select()
     */
    public <RE> Optional<RE> selectAs(Class<RE> resultClass) {
        List<RE> entities = new ArrayList<>();
        selectAs(resultClass, entity -> {
            if (entities.size() > 0)
            // 4.0.0
            //  throw new ManyRowsException(generatedSql);
                throw new ManyRowsException(generatedSql.toString());
            ////
            entities.add(entity);
        });
        return entities.isEmpty() ? Optional.empty() : Optional.of(entities.get(0));
    }

    /**
     * Generates and executes a <i>SELECT COUNT(*)</i> SQL and returns the result.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var count = new int[1];
     * Transaction.execute(conn -&gt;
     *     count[0] = new Sql&lt;&gt;(Contact.class)
     *         .connection(conn)
     *         .<b>selectCount()</b>
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * int count
     * Transaction.execute {
     *     count = new Sql&lt;&gt;(Contact)
     *         .connection(it)
     *         .<b>selectCount()</b>
     * }
     * </pre></div>
     *
     * <p>
     * <span class="simpleTagLabel">Caution:</span>
     * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before invoking this method.
     * </p>
     *
     * @return the number of selected rows
     *
     * @throws IllegalStateException if a <b>ConnectionWrapper</b> is not set
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     *
     * @since 2.0.0
     */
    public int selectCount() {
        if (connection == null)
            throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));

        Sql<E> sql = where.isEmpty() ? clone().where(Condition.ALL) : this;

        List<Object> parameters = new ArrayList<>();
    // 4.0.0
    //  String sqlString = connection.getDatabase().subSelectSql(sql, null, () -> "COUNT(*)", parameters);
        CharSequence sqlString = connection.getDatabase().subSelectSql(sql, null, () -> "COUNT(*)", parameters);
    ////

        int[] count = new int[1];
        executeQuery(sqlString, parameters, resultSet -> {
            try {
                count[0] = resultSet.getInt(1);
            }
            catch (SQLException e) {throw new RuntimeSQLException(e);}
        });
        return count[0];
    }

    /**
     * Generates and executes an <i>INSERT</i> SQL with a FROM subquery.
     *
     * @return the number of rows inserted
     *
     * @throws IllegalStateException if a <b>ConnectionWrapper</b> is not set
     * @throws IllegalStateException a FROM subquery is not set
     *
     * @since 4.0.0
     * @see #from(Sql)
     */
    public int insert() {
        if (connection == null)
            throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));

        if (fromSql == null)
            throw new IllegalStateException(messageNeedFromSql);
        fromSql = fromSql.clone();
        fromSql.isInInsertFrom = true;

        List<Object> parameters = new ArrayList<>();
        generatedSql = connection.getDatabase().insertSql(this, parameters);
        int count = executeUpdate(generatedSql, parameters);

        return count;
    }

    /**
     * Generates and executes an <i>INSERT</i> SQL for the specified entity.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class)
     *         .connection(conn)
     *         .<b>insert(new Contact("Setoka", "Orange", LocalDate.of(2001, 2, 1)))</b>
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact)
     *         .connection(it)
     *         .<b>insert(new Contact('Setoka', 'Orange', LocalDate.of(2001, 2, 1)))</b>
     * }
     * </pre></div>
     *
     * <p>
     * <span class="simpleTagLabel">Caution:</span>
     * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before invoking this method.
     * </p>
     *
     * @param entity the entity to be inserted
     * @return the number of rows inserted
     *
     * @throws NullPointerException if <b>entity</b> is <b>null</b>
     * @throws IllegalStateException if a <b>ConnectionWrapper</b> is not set
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     *
     * @since 2.0.0
     */
    public int insert(E entity) {
        Objects.requireNonNull(entity, "entity is null");

        if (connection == null)
            throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));
    
    // 4.0.0
    //    if (entity instanceof PreStore)
    //        ((PreStore)entity).preStore();
    ////
    
        Sql<E> sql = clone().setEntity(entity);
    
        // before INSERT
        if (entity instanceof PreInsert)
            ((PreInsert)entity).preInsert(connection);
    
        List<Object> parameters = new ArrayList<>();
        generatedSql = connection.getDatabase().insertSql(sql, parameters);
        int count = sql.executeUpdate(generatedSql, parameters);
    
        // after INSERT
        if (entity instanceof PostInsert)
            ((PostInsert)entity).postInsert(connection);
    
        return count;
    }

    /**
     * Generates and executes <i>INSERT</i> SQLs for each element of entities.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class)
     *         .connection(conn)
     *         .<b>insert(Arrays.asList(</b>
     *             <b>new Contact("Harumi", "Orange", LocalDate.of(2001, 2, 2)),</b>
     *             <b>new Contact("Mihaya", "Orange", LocalDate.of(2001, 2, 3)),</b>
     *             <b>new Contact("Asumi" , "Orange", LocalDate.of(2001, 2, 4))</b>
     *         <b>))</b>
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact)
     *         .connection(it)
     *         .<b>insert([</b>
     *             <b>new Contact('Harumi', 'Orange', LocalDate.of(2001, 2, 2)),</b>
     *             <b>new Contact('Mihaya', 'Orange', LocalDate.of(2001, 2, 3)),</b>
     *             <b>new Contact('Asumi' , 'Orange', LocalDate.of(2001, 2, 4))</b>
     *         <b>])</b>
     * }
     * </pre></div>
     *
     * <p>
     * <span class="simpleTagLabel">Caution:</span>
     * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before invoking this method.
     * </p>
     *
     * @param entities an <b>Iterable</b> of entities
     * @return the number of rows inserted
     *
     * @throws NullPointerException if <b>entities</b> or any element of <b>entities</b> is <b>null</b>
     * @throws IllegalStateException if a <b>ConnectionWrapper</b> is not set
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     *
     * @since 2.0.0
     */
    public int insert(Iterable<? extends E> entities) {
        int[] count = new int[1];
        Objects.requireNonNull(entities, "entities is null")
            .forEach(entity -> count[0] += insert(entity));
        return count[0];
    }

    /**
     * Generates and executes an <i>UPDATE</i> SQL for the specified entity.
     *
     * <p>
     * If the condition of the <i>WHERE</i> clause is specified, updates by the condition.<br>
     * To update all rows of the target table, specify <b>Condition.ALL</b> to <i>WHERE</i> conditions.
     * </p>
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * Transaction.execute(conn -&gt; {
     *     var contact = new Contact[1];
     *     new Sql&lt;&gt;(Contact.class)
     *         .where("{firstName}={}", "Setoka")
     *         .and("{lastName}={}", "Orange")
     *         .connection(conn)
     *         .select(it -&gt; contact[0] = it);
     *
     *     contact[0].birthday = LocalDate.of(2017, 2, 1);
     *     new Sql&lt;&gt;(Contact.class)
     *         .columns("birthday")
     *         .connection(conn)
     *         .<b>update(contact[0])</b>;
     * });
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * Transaction.execute {
     *     Contact contact
     *     new Sql&lt;&gt;(Contact)
     *         .where('{firstName}={}', 'Setoka')
     *         .and('{lastName}={}', 'Orange')
     *         .connection(it)
     *         .select({contact = it})
     *
     *     contact.birthday = LocalDate.of(2017, 2, 1);
     *     new Sql&lt;&gt;(Contact)
     *         .columns('birthday')
     *         .connection(it)
     *         .<b>update(contact)</b>
     * }
     * </pre></div>
     *
     * <p>
     * <span class="simpleTagLabel">Caution:</span>
     * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before invoking this method.
     * </p>
     *
     * @param entity the entity to be updated
     * @return the number of rows updated
     *
     * @throws NullPointerException if <b>entity</b> is <b>null</b>
     * @throws IllegalStateException if a <b>ConnectionWrapper</b> is not set
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     *
     * @since 2.0.0
     */
    public int update(E entity) {
        Objects.requireNonNull(entity, "entity is null");

        if (connection == null)
            throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));

    // 4.0.0
    //    if (entity instanceof PreStore)
    //        ((PreStore)entity).preStore();
    ////

        Sql<E> sql = clone().setEntity(entity);
        if (sql.where.isEmpty())
            sql.where = Condition.of(entity);

        // before UPDATE
        if (entity instanceof PreUpdate)
            ((PreUpdate)entity).preUpdate(connection);

        List<Object> parameters = new ArrayList<>();
        generatedSql = connection.getDatabase().updateSql(sql, parameters);
        int count = sql.executeUpdate(generatedSql, parameters);

        // after UPDATE
        if (sql.where instanceof EntityCondition && entity instanceof PostUpdate)
            ((PostUpdate)entity).postUpdate(connection);

        return count;
    }

    /**
     * Generates and executes <i>UPDATE</i> SQLs for each element of <b>entities</b>.
     *
     * <p>
     * Even if the condition of the <i>WHERE</i> clause is specified,
     * <b>new EntityCondition(entity)</b> will be specified for each entity.
     * </p>
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * Transaction.execute(conn -&gt; {
     *     var contacts = new ArrayList&lt;Contact&gt;();
     *     new Sql&lt;&gt;(Contact.class)
     *         .where(Condition
     *             .of("{firstName}={}", "Setoka")
     *             .or("{firstName}={}", "Mihaya")
     *             .or("{firstName}={}", "Asumi")
     *         )
     *         .and("{lastName}={}", "Orange")
     *         .orderBy("{birthday}")
     *         .connection(conn)
     *         .select(contact -&gt; contacts.add(contact));
     *
     *         contacts.get(0).birthday = LocalDate.of(2017, 2, 2);
     *         contacts.get(1).birthday = LocalDate.of(2017, 2, 3);
     *         contacts.get(2).birthday = LocalDate.of(2017, 2, 4);
     *         new Sql&lt;&gt;(Contact.class)
     *             .columns("birthday")
     *             .connection(conn)
     *             .<b>update(contacts)</b>;
     * });
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * Transaction.execute {
     *     List&lt;Contact&gt; contacts = []
     *     new Sql&lt;&gt;(Contact)
     *         .where(Condition
     *             .of('{firstName}={}', 'Setoka')
     *             .or('{firstName}={}', 'Mihaya')
     *             .or('{firstName}={}', 'Asumi')
     *         )
     *         .and('{lastName}={}', 'Orange')
     *         .orderBy('{birthday}')
     *         .connection(it)
     *         .select({contacts &lt;&lt; it})
     *
     *         contacts[0].birthday = LocalDate.of(2017, 2, 2)
     *         contacts[1].birthday = LocalDate.of(2017, 2, 3)
     *         contacts[2].birthday = LocalDate.of(2017, 2, 4)
     *         new Sql&lt;&gt;(Contact)
     *             .columns('birthday')
     *             .connection(it)
     *             .<b>update(contacts)</b>
     * }
     * </pre></div>
     *
     * <p>
     * <span class="simpleTagLabel">Caution:</span>
     * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before invoking this method.
     * </p>
     *
     * @param entities an <b>Iterable</b> of entities
     * @return the number of rows updated
     *
     * @throws NullPointerException if <b>entities</b> or any element of <b>entities</b> is <b>null</b>
     * @throws IllegalStateException if a <b>ConnectionWrapper</b> is not set
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     *
     * @since 2.0.0
     */
    public int update(Iterable<? extends E> entities) {
        int[] count = new int[1];
        Objects.requireNonNull(entities, "entities is null")
            .forEach(entity -> count[0] += update(entity));
        return count[0];
    }

    /**
     * Generates and executes a <i>DELETE</i> SQL.
     *
     * <p>
     * If the <B>WHERE</b> condition is not specified, dose not delete.<br>
     * To delete all rows of the target table, specify <b>Condition.ALL</b> to <i>WHERE</i> conditions.
     * </p>
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class)
     *         .where(Condition.ALL)
     *         .connection(conn)
     *         .<b>delete()</b>
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact)
     *         .where(Condition.ALL)
     *         .connection(it)
     *         .<b>delete()</b>
     * }
     * </pre></div>
     *
     * <p>
     * <span class="simpleTagLabel">Caution:</span>
     * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before invoking this method.
     * </p>
     *
     * @return the number of rows deleted
     *
     * @throws IllegalStateException if a <b>ConnectionWrapper</b> is not set
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     *
     * @see org.lightsleep.component.Condition#ALL
     * @since 2.0.0
     */
    public int delete() {
        if (connection == null)
            throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));

        if (where.isEmpty()) {
            logger.warn(MessageFormat.format(messageNoWhereCondition, entityInfo.entityClass().getName()));
            return 0;
        }

        List<Object> parameters = new ArrayList<>();
    // 4.0.0
    //  String sqlString = connection.getDatabase().deleteSql(this, parameters);
        CharSequence sqlString = connection.getDatabase().deleteSql(this, parameters);
    ////
        return executeUpdate(sqlString, parameters);
    }

    /**
     * Generates and executes a <i>DELETE</i> SQL for the specified entity.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * Transaction.execute(conn -&gt;
     *     new Sql&lt;&gt;(Contact.class)
     *         .connection(conn)
     *         .<b>delete(new ContactKey(contacts.get(6-1).id))</b>
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * Transaction.execute {
     *     new Sql&lt;&gt;(Contact)
     *         .connection(it)
     *         .<b>delete(new ContactKey(contacts[6-1].id))</b>
     * }
     * </pre></div>
     *
     * <p>
     * <span class="simpleTagLabel">Caution:</span>
     * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before invoking this method.
     * </p>
     *
     * @param entity the entity to be deleted
     * @return the number of rows deleted
     *
     * @throws NullPointerException if <b>entity</b> is <b>null</b>
     * @throws IllegalStateException if a <b>ConnectionWrapper</b> is not set
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     *
     * @since 2.0.0
     */
    public int delete(E entity) {
        Objects.requireNonNull(entity, "entity is null");

        if (connection == null)
            throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));

        Sql<E> sql = clone().where(Condition.of(entity));

        // before DELETE
        if (entity instanceof PreDelete)
            ((PreDelete)entity).preDelete(connection);

        List<Object> parameters = new ArrayList<>();
        generatedSql = connection.getDatabase().deleteSql(sql, parameters);
        int count = sql.executeUpdate(generatedSql, parameters);

        // after DELETE
        if (entity instanceof PostDelete)
            ((PostDelete)entity).postDelete(connection);

        return count;
    }

    /**
     * Generates and executes <i>DELETE</i> SQLs for each element of entities.
     *
     * <div class="exampleTitle"><span>Java Example</span></div>
     * <div class="exampleCode"><pre>
     * var count = new int[1];
     * Transaction.execute(conn -&gt;
     *     count[0] = new Sql&lt;&gt;(Contact.class)
     *         .connection(conn)
     *         .<b>delete(Arrays.asList(
     *             new ContactKey(contacts.get(7-1).id),
     *             new ContactKey(contacts.get(8-1).id),
     *             new ContactKey(contacts.get(9-1).id)
     *         ))</b>
     * );
     * </pre></div>
     *
     * <div class="exampleTitle"><span>Groovy Example</span></div>
     * <div class="exampleCode"><pre>
     * int count
     * Transaction.execute {
     *     count = new Sql&lt;&gt;(Contact)
     *         .connection(it)
     *         .<b>delete([
     *             new ContactKey(contacts[7-1].id),
     *             new ContactKey(contacts[8-1].id),
     *             new ContactKey(contacts[9-1].id)
     *         ])</b>
     * }
     * </pre></div>
     *
     * <p>
     * <span class="simpleTagLabel">Caution:</span>
     * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before invoking this method.
     * </p>
     *
     * @param entities an <b>Iterable</b> of entities
     * @return the number of rows deleted
     *
     * @throws NullPointerException if <b>entities</b> or any element of <b>entities</b> is <b>null</b>
     * @throws IllegalStateException if a <b>ConnectionWrapper</b> is not set
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     *
     * @since 2.0.0
     */
    public int delete(Iterable<? extends E> entities) {
        int[] count = new int[1];
        Objects.requireNonNull(entities, "entities is null")
            .forEach(entity -> count[0] += delete(entity));
        return count[0];
    }

    /** The time format  */
    private static DecimalFormat timeFormat = new DecimalFormat();
    static {
        timeFormat.setMinimumFractionDigits(0);
        timeFormat.setMaximumFractionDigits(3);
    }

    /**
     * Returns a row consumer.
     *
     * @param connection the connection wrapper
     * @param sqlEntityInfo the <b>SqlEntityInfo</b> object
     * @param consumer the consumer
     *
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     * @throws RuntimeException InstantiationException, IllegalAccessException
     */
    private <T> Consumer<ResultSet> getRowConsumer(SqlEntityInfo<T> sqlEntityInfo, Consumer<? super T> consumer) {
        return resultSet -> {
            EntityInfo<T> entityInfo = sqlEntityInfo.entityInfo();
            Accessor<T> accessor = entityInfo.accessor();
            String tableAlias = sqlEntityInfo.tableAlias();
            try {
                // Create an entity object
                T entity = entityInfo.entityClass().getConstructor().newInstance();

                //  Column loop
                sqlEntityInfo.selectedSqlColumnInfoStream(columns)
                    .filter(sqlColumnInfo -> sqlColumnInfo.columnInfo().selectable())
                    .forEach(sqlColumnInfo -> {
                        ColumnInfo columnInfo = sqlColumnInfo.columnInfo();
                        String columnAlias = columnInfo.getColumnAlias(tableAlias);

                        Object value = connection.getDatabase().getObject(connection.getConnection(), resultSet, columnAlias);

                        Class<?> destinType = Utils.toClassType(accessor.getType(columnInfo.propertyName()));
                        Object convertedValue = null;
                        try {
                            convertedValue = connection.getDatabase().convert(value, destinType);
                        }
                        catch (ConvertException e) {
                            if (columnInfo.columnType() == null)
                                throw e;

                            logger.debug(() -> e.toString());
                            value = connection.getDatabase().convert(value, columnInfo.columnType());
                            convertedValue = connection.getDatabase().convert(value, destinType);
                        }
                        entityInfo.accessor().setValue(entity, columnInfo.propertyName(), convertedValue);
                    });

                // After get
            // 4.0.0
            //    if (entity instanceof PostLoad)
            //        ((PostLoad)entity).postLoad();
            ////

                if (entity instanceof PostSelect)
                    ((PostSelect)entity).postSelect(connection);

                // Consumes the entity
                consumer.accept(entity);
            }
            catch (RuntimeException e) {throw e;}
            catch (Exception e) {throw new RuntimeException(e);}
        };
    }


    /**
     * Executes the <i>SELECT</i> SQL.
     *
     * @param connection the connection wrapper
     * @param sql the SQL
     * @param parameters the parameters of SQL
     * @param consumer the consumer for the <b>ResultSet</b> object
     *
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     */
// 4.0.0
//  private void executeQuery(String sql, List<Object> parameters, Consumer<ResultSet> consumer) {
    private void executeQuery(CharSequence sql, List<Object> parameters, Consumer<ResultSet> consumer) {
////
        Objects.requireNonNull(sql, "sql");
        Objects.requireNonNull(parameters, "parameters is null");
        Objects.requireNonNull(consumer, "consumer is null");
        if (connection == null)
            throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));

        int sqlNo = Sql.sqlNo++;
        if (logger.isInfoEnabled())
            logger.info('#' + Integer.toUnsignedString(sqlNo) + ' '
                + connection.toString() + ' ' + sql);

        // Prepares SQL
    // 4.0.0
    //  try (PreparedStatement statement = connection.prepareStatement(sql)) {
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
    ////
            //  Sets the parameter values
            for (int index = 0; index < parameters.size(); ++index) {
                Object parameter = parameters.get(index);
                if  (logger.isDebugEnabled())
                    logger.debug("  parameters[" + index + "]: " + Utils.toLogString(parameter));

                if (parameter instanceof Reader)
                    statement.setCharacterStream(index + 1, (Reader)parameter);
                else
                    statement.setObject(index + 1, parameter);
            }

            // Executes SQL
            long execTimeBefore = System.nanoTime(); // Time of before execution
            ResultSet resultSet = statement.executeQuery();
            long execTimeAfter = System.nanoTime(); // Time of after execution

            int resultSetType = resultSet.getType();

            //  for offset
            int rowOffset = getOffset();
            int rowLimit = getLimit();
            if (rowOffset > 0 && !connection.getDatabase().supportsOffsetLimit()) {
                //  Offset value was specified and cannot create SQL using 'OFFSET'
                if (resultSetType == ResultSet.TYPE_FORWARD_ONLY) {
                    //  Skip rows for offset value
                    for (int index = 0; index < rowOffset; ++index) {
                        if (!resultSet.next())
                            break;
                    }
                    logger.debug(() -> "  resultSet.next() * " + rowOffset);
                } else {
                    // Specifies absolute row offset
                    boolean absoluteResult = resultSet.absolute(rowOffset);
                    logger.debug(() -> "  resultSet.absolute(" + rowOffset + ")=" + absoluteResult);
                }
            }

            // Loop for row
            long getTimeBefore = System.nanoTime(); // Time of before get rows
            int rowCount = 0;
            while (rowCount < rowLimit) {
                if (!resultSet.next())
                    break;
                ++rowCount;

                consumer.accept(resultSet);
            }
            long getTimeAfter = System.nanoTime(); // Time of after get rows

            // Logging for the results
            if (logger.isInfoEnabled()) {
                double execTime = (execTimeAfter - execTimeBefore) / 1_000_000.0;
                double getTime  = (getTimeAfter  - getTimeBefore ) / 1_000_000.0;
                String sqlNoStr = "#" + Integer.toUnsignedString(sqlNo) + ' ';
                switch (rowCount) {
                case 0:
                    logger.info(sqlNoStr + MessageFormat.format(messageSelected0Rows,
                        timeFormat.format(execTime) + timeFormat.format(getTime)));
                    break;
                case 1:
                    logger.info(sqlNoStr + MessageFormat.format(messageSelectedRow,
                        timeFormat.format(execTime), timeFormat.format(getTime)));
                    break;
                default:
                    logger.info(sqlNoStr + MessageFormat.format(messageSelectedRows, rowCount,
                        timeFormat.format(execTime), timeFormat.format(getTime),
                        timeFormat.format(getTime / rowCount)));
                    break;
                }
            }
        }
        catch (SQLException e) {throw new RuntimeSQLException(e);}
    }

    /**
     * Executes the SQL.
     *
     * @param sql the SQL
     *
     * @throws NullPointerException if <b>entity</b> is <b>null</b>
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     *
     * @since 3.0.0
     */
    public void executeUpdate(String sql) {
        executeUpdate(sql, Collections.emptyList());
    }

    /**
     * Executes the SQL which is <i>INSERT</i>, <i>UPDATE</i> or <i>DELETE</i> SQL.
     *
     * @param sql the SQL
     * @param parameters the parameters of SQL
     *
     * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
     */
// 4.0.0
//  private int executeUpdate(String sql, List<Object> parameters) {
    private int executeUpdate(CharSequence sql, List<Object> parameters) {
////
        Objects.requireNonNull(sql, "sql is null");
        Objects.requireNonNull(parameters, "parameters is null");
        if (connection == null)
            throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));

        int sqlNo = Sql.sqlNo++;
        if (logger.isInfoEnabled())
            logger.info('#' + Integer.toUnsignedString(sqlNo) + ' '
            //    + connection.getDatabase().getClass().getSimpleName() + ": " + sql);
                + connection.toString() + ' '  + sql);
            ////

        // Prepares SQL
    // 4.0.0
    //  try (PreparedStatement statement = connection.prepareStatement(sql)) {
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
    ////
            //  Sets the parameter values
            for (int index = 0; index < parameters.size(); ++index) {
                Object parameter = parameters.get(index);
                if  (logger.isDebugEnabled())
                    logger.debug("  parameters[" + index + "]: " + Utils.toLogString(parameter));

                if (parameter instanceof Reader)
                    statement.setCharacterStream(index + 1, (Reader)parameter);
                else
                    statement.setObject(index + 1, parameter);
            }

            // Executes SQL
            long execTimeBefore = System.nanoTime(); // Time of before execution
            int rowCount = statement.executeUpdate();
            long execTimeAfter = System.nanoTime(); // Time of after execution

            // Logging for the results
            if (logger.isInfoEnabled()) {
                double execTime = (execTimeAfter - execTimeBefore) / 1_000_000.0;
                String sqlNoStr = "#" + Integer.toUnsignedString(sqlNo) + ' ';

            // 4.0.0
                if (sql.toString().startsWith("INSERT ")) {
            ////
                    switch (rowCount) {
                    case 0:
                        logger.info(sqlNoStr + MessageFormat.format(messageInserted0Rows, timeFormat.format(execTime)));
                        break;
                    case 1:
                        logger.info(sqlNoStr + MessageFormat.format(messageInsertedRow, timeFormat.format(execTime)));
                        break;
                    default:
                        logger.info(sqlNoStr + MessageFormat.format(messageInsertedRows, rowCount, timeFormat.format(execTime)));
                        break;
                    }
            // 4.0.0
            //  } else if (sql.startsWith("DELETE ")) {
                } else if (sql.toString().startsWith("DELETE ")) {
            ////
                    switch (rowCount) {
                    case 0:
                        logger.info(sqlNoStr + MessageFormat.format(messageDeleted0Rows, timeFormat.format(execTime)));
                        break;
                    case 1:
                        logger.info(sqlNoStr + MessageFormat.format(messageDeletedRow, timeFormat.format(execTime)));
                        break;
                    default:
                        logger.info(sqlNoStr + MessageFormat.format(messageDeletedRows, rowCount, timeFormat.format(execTime)));
                        break;
                    }
                } else {
                    switch (rowCount) {
                    case 0:
                        logger.info(sqlNoStr + MessageFormat.format(messageUpdated0Rows, timeFormat.format(execTime)));
                        break;
                    case 1:
                        logger.info(sqlNoStr + MessageFormat.format(messageUpdatedRow, timeFormat.format(execTime)));
                        break;
                    default:
                        logger.info(sqlNoStr + MessageFormat.format(messageUpdatedRows, rowCount, timeFormat.format(execTime)));
                        break;
                    }
                }
            }

            return rowCount;
        }
        catch (SQLException e) {throw new RuntimeSQLException(e);}
    }

    /**
     * Returns a <b>ColumnInfo</b> stream of the main table.
     *
     * <p>
     * <i>This method is used internally.</i>
     * </p>
     *
     * @return a <b>ColumnInfo</b> stream
     */
    public Stream<ColumnInfo> columnInfoStream() {
        return entityInfo.columnInfos().stream();
    }

    /**
     * Returns a <b>SqlColumnInfo</b> stream with selected columns of the main table.
     *
     * <p>
     * <i>This method is used internally.</i>
     * </p>
     *
     * @return a <b>SqlColumnInfo</b> stream
     */
    public Stream<SqlColumnInfo> selectedSqlColumnInfoStream() {
        return selectedSqlColumnInfoStream(columns);
    }

    /**
     * Returns a <b>SqlColumnInfo</b> stream with selected columns
     * of the main table and the joined tables.
     *
     * <p>
     * <i>This method is used internally.</i>
     * </p>
     *
     * @return a <b>SqlColumnInfo</b> stream
     */
    public Stream<SqlColumnInfo> selectedJoinSqlColumnInfoStream() {
        return Stream.concat(Stream.of(this), joinInfos.stream())
            .flatMap(sqlEntityInfo -> sqlEntityInfo.selectedSqlColumnInfoStream(columns));
    }

    /**
     * Synchronizes table aliases with from Sql and union Sqls.
     *
     * @since 3.1.0
     */
    private void synchronizeTableAliases() {
        // synchronize table aliases with fromSql
    // 4.0.0
    //  if (fromSql != null) {
        if (fromSql != null && !fromSql.isWithSql()) {
    ////
            if (!tableAlias.isEmpty()) {
                if (!fromSql.tableAlias.isEmpty()) {
                    if (!tableAlias.equals(fromSql.tableAlias))
                        throw new IllegalStateException(
                            "tableAlias(" + tableAlias + ") <> fromSql.tableAlias(" + fromSql.tableAlias + ")");
                } else {
                    // tableAlias -> fromSql.tableAlias
                    fromSql.tableAlias(tableAlias);
                    fromSql.synchronizeTableAliases();
                }
            } else {
                if (!fromSql.tableAlias.isEmpty()) {
                    // tableAlias <- fromSql.tableAlias
                    tableAlias(fromSql.tableAlias);
                    synchronizeTableAliases();
                }
            }
        }

        // synchronize table aliases with unionSqls
        int index = 0;
        for (Sql<?> unionSql : unionSqls) {
        // 4.0.0
            if (unionSql.isWithSql()) continue;

        ////
            if (!tableAlias.isEmpty()) {
                if (!unionSql.tableAlias.isEmpty()) {
                    if (!tableAlias.equals(unionSql.tableAlias))
                        throw new IllegalStateException(
                            "tableAlias(" + tableAlias + ") <> unionSqls[" + index + "].tableAlias(" + unionSql.tableAlias + ")");
                } else {
                    // tableAlias -> unionSql.tableAlias
                    unionSql.tableAlias(tableAlias);
                    unionSql.synchronizeTableAliases();
                }
            } else {
                if (!unionSql.tableAlias.isEmpty()) {
                    // tableAlias <- unionSql.tableAlias
                    tableAlias(unionSql.tableAlias);
                    synchronizeTableAliases();
                }
            }
            ++index;
        }
    }

    /**
     * Synchronize <b>columns</b> with <b>fromSql</b>, <b>unionSqls</b> and <b>recursiveSql</b>.
     *
     * @since 3.1.0
     */
    private void synchronizeColumns() {
        // synchronize columns with fromSql
        if (fromSql != null) {
            if (!columns.isEmpty()) {
                if (!fromSql.columns.isEmpty()) {
                    if (!equals(columns, fromSql.columns))
                        throw new IllegalStateException(
                            "columns(" + toString(columns) + ") <> fromSql.columns(" + toString(fromSql.columns) + ")");
                } else {
                    // column -> fromSql.columns
                    fromSql.columns(columns);
                    fromSql.synchronizeColumns();
                }
            } else {
                if (!fromSql.columns.isEmpty()) {
                    // columns <- fromSql.column
                    columns(fromSql.columns);
                    synchronizeColumns();
                }
            }
        }

        // synchronize columns with unionSqls
        int index = 0;
        for (Sql<?> unionSql : unionSqls) {
            if (!columns.isEmpty()) {
                if (!unionSql.columns.isEmpty()) {
                    if (!equals(columns, unionSql.columns))
                        throw new IllegalStateException(
                            "columns(" + toString(columns) + ") <> unionSqls[" + index + "].columns(" + toString(unionSql.columns) + ")");
                } else {
                    // column -> unionSql.columns
                    unionSql.columns(columns);
                    unionSql.synchronizeColumns();
                }
            } else {
                if (!unionSql.columns.isEmpty()) {
                    // columns <- unionSql.column
                    columns(unionSql.columns);
                    synchronizeColumns();
                }
            }
            ++index;
        }

    // 4.0.0
        // synchronize columns with recursiveSql
        if (recursiveSql != null) {
            if (!columns.isEmpty()) {
                if (!recursiveSql.columns.isEmpty()) {
                    if (!equals(columns, recursiveSql.columns))
                        throw new IllegalStateException(
                            "columns(" + toString(columns) + ") <> recursiveSql.columns(" + toString(recursiveSql.columns) + ")");
                } else {
                    // column -> recursiveSql.columns
                    recursiveSql.columns(columns);
                    recursiveSql.synchronizeColumns();
                }
            } else {
                if (!recursiveSql.columns.isEmpty()) {
                    // columns <- recursiveSql.column
                    columns(recursiveSql.columns);
                    synchronizeColumns();
                }
            }
        }
    ////
    }

    /**
     * Returns <b>true</b> if <b>set1</b> equals <b>set2</b>, <b>false</b> otherwise.
     *
     * @param set1 the set 1
     * @param set2 the set 2
     * @return <b>true</b> if <b>set1</b> equals <b>set2</b>, <b>false</b> otherwise
     *
     * @since 3.1.0
     */
    private static <T> boolean equals(Set<T> set1, Set<T> set2) {
        if (set1.size() != set2.size())
            return false;

        Set<T> set = new HashSet<T>();
        set.addAll(set1);
        set.addAll(set2);
        return set.size() == set1.size();
    }

    /**
     * Returns a string representation of <b>set</b>.
     *
     * @param set the set
     * @return a string representation of <b>set</b>
     *
     * @since 3.1.0
     */
    private static <T> String toString(Set<T> set) {
        return set.stream()
            .map(element -> element.toString())
            .collect(Collectors.joining(", ", "[", "]"));
    }
}
