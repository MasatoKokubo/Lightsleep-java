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
import org.lightsleep.entity.Composite;
import org.lightsleep.entity.PostLoad;
import org.lightsleep.entity.PreInsert;
import org.lightsleep.entity.PreStore;
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
 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
 * Transaction.execute(conn -&gt; {
 *     new <b>Sql</b>&lt;&gt;(Contact.class)
 *         <b>.where</b>("{lastName}={}", "Apple")
 *         <b>.connection</b>(conn)
 *         <b>.select</b>(contacts::add);
 * });
 * </pre></div>
 *
 * <div class="exampleTitle"><span>Groovy Example</span></div>
 * <div class="exampleCode"><pre>
 * List&lt;Contact&gt; contacts = []
 * Transaction.execute {
 *     new <b>Sql</b>&lt;&gt;(Contact)
 *         <b>.where</b>('{lastName}={}', 'Apple')
 *         <b>.connection</b>(it)
 *         <b>.select</b>({contacts &lt;&lt; it})
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
	private static final String messageSelect0Rows = resource.getString("messageSelect0Rows");
	private static final String messageSelectRow   = resource.getString("messageSelectRow");
	private static final String messageSelectRows  = resource.getString("messageSelectRows");
	private static final String messageUpdate0Rows = resource.getString("messageUpdate0Rows");
	private static final String messageUpdateRow   = resource.getString("messageUpdateRow");
	private static final String messageUpdateRows  = resource.getString("messageUpdateRows");
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

// 3.1.0
	private Sql<?> fromSql = null;
////

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

// 3.1.0
	private List<Sql<?>> unionSqls = new ArrayList<>();

	private boolean unionAll = false;
////

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

	// The connection wrapper @since 2.0.0
	private transient ConnectionWrapper connection;

	// The generated SQL @since 1.5.0
	private transient String generatedSql;

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
		Objects.requireNonNull(entityClass, "entityClass");
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
	 * @param entityClass an entity class
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
	 * @param entityClass an entity class
	 * @param tableAlias a table alias
	 *
	 * @throws NullPointerException if <b>entityClass</b> or <b>tableAlias</b> is <b>null</b>
	 */
	public Sql(Class<E> entityClass, String tableAlias) {
		entityInfo = getEntityInfo(Objects.requireNonNull(entityClass, "entityClass"));
	// 3.1.0
	//	this.tableAlias = Objects.requireNonNull(tableAlias, "tableAlias");
	//	addSqlEntityInfo(this);
		tableAlias(tableAlias);
	////
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Sql<E> clone() {
		Sql<E> sql = new Sql<>(entityClass(), tableAlias());

		sql.entity       = entity;
		sql.distinct     = distinct;
	// 3.1.0
	//	sql.columns      = getColumns();
	//	sql.joinInfos    = getJoinInfos();
		sql.columns  .addAll(columns);
		sql.fromSql      = fromSql;
		sql.joinInfos.addAll(joinInfos);
	////
		sql.where        = where;
	// 3.1.0
	//	sql.groupBy      = getGroupBy();
		sql.groupBy      = groupBy.clone();
	////
		sql.having       = having;
	// 3.1.0
	//	sql.orderBy      = getOrderBy();
		sql.unionSqls.addAll(unionSqls);
		sql.unionAll     = unionAll;
		sql.orderBy      = orderBy.clone();
	////
		sql.limit        = limit;
		sql.offset       = offset;
		sql.forUpdate    = forUpdate;
		sql.waitTime     = waitTime;
		sql.connection   = connection;
		sql.generatedSql = generatedSql;

		expressionMap.entrySet()
			.forEach(entry -> sql.expressionMap.put(entry.getKey(), entry.getValue()));
		sqlEntityInfoMap.entrySet()
			.forEach(entry -> sql.sqlEntityInfoMap.put(entry.getKey(), entry.getValue()));

		return sql;
	}

	/**
	 * {@inheritDoc}
	 *
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
	 * {@inheritDoc}
	 *
	 * @see #Sql(Class, String)
	 */
	@Override
	public String tableAlias() {
		return tableAlias;
	}

	/**
	 * {@inheritDoc}
	 *
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
	 * Specifies that appends <b>DISTINCT</b> to SELECT SQL.
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
	 * Returns whether to append <b>DISTINCT</b> to SELECT SQL.
	 *
	 * @return <b>true</b> if appends <b>DISTINCT</b>, <b>false</b> otherwise
	 *
	 * @see #distinct()
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * Sets target columns of generated SELECT and UPDATE SQL.
	 *
	 * <p>
	 * Also sets them if they are not set in <b>Sql</b> objects set by the <b>from</b>, <b>union</b> or <b>unionAll</b> methods,
	 * </p>
	 *
	 * <p>
	 * You can also be specified <b>"*"</b> or <b>"<i>&lt;table alias&gt;</i>.*"</b>.
	 * If this method is not called it will be in the same as <b>"*"</b> is specified.
	 * </p>
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         <b>.columns("lastName", "firstName")</b>
	 *         .where("{lastName}={}", "Apple")
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         <b>.columns('lastName', 'firstName')</b>
	 *         .where('{lastName}={}', 'Apple')
	 *         .connection(it)
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * List&lt;Phone&gt; phones = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
	 *         <b>.columns("C.id", "P.*")</b>
	 *         .connection(conn)
	 *         .&lt;Phone&gt;select(contacts::add, phones::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * List&lt;Phone&gt; phones = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         .innerJoin(Phone, 'P', '{P.contactId}={C.id}')
	 *         <b>.columns('C.id', 'P.*')</b>
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
	// 3.1.0
	//	Arrays.stream(Objects.requireNonNull(propertyNames, "propertyNames"))
	//		.forEach(this.columns::add);
	//	return this;
		return columns(Arrays.stream(Objects.requireNonNull(propertyNames, "propertyNames")));
	////
	}

	/**
	 * Sets target columns of generated SELECT and UPDATE SQL.
	 *
	 * <p>
	 * Also sets them if they are not set in <b>Sql</b> objects set by the <b>from</b>, <b>union</b> or <b>unionAll</b> methods,
	 * </p>
	 *
	 * <p>
	 * You can also be specified <b>"*"</b> or <b>"<i>&lt;table alias&gt;</i>.*"</b>.
	 * If this method is not called it will be in the same as <b>"*"</b> is specified.
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
		return columns(Objects.requireNonNull(propertyNames, "propertyNames").stream());
	}

	/**
	 * Sets target columns of generated SELECT and UPDATE SQL.
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
	 * Sets target columns of generated SELECT and UPDATE SQL.
	 *
	 * <p>
	 * Also sets them if they are not set in <b>Sql</b> objects set by the <b>from</b>, <b>union</b> or <b>unionAll</b> methods,
	 * </p>
	 *
	 * <p>
	 * You can also be specified <b>"*"</b> or <b>"<i>&lt;table alias&gt;</i>.*"</b>.
	 * If this method is not called it will be in the same as <b>"*"</b> is specified.
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

		// synchronize columns with from Sql and union Sql
		synchronizeColumns();

		return this;
	}

	/**
	 * Returns property names related target columns of generated SELECT and UPDATE SQL.
	 *
	 * @return a set of property names
	 *
	 * @see #columns(String...)
	 * @see #columns(Collection)
	 * @see #columns(Class)
	 */
	public Set<String> getColumns() {
	// 3.1.0
	//	try {
	//		return (Set<String>)columns.getClass().getMethod("clone").invoke(columns);
	//	}
	//	catch (Exception e) {
	//		throw new RuntimeException(e);
	//	}
		return columns;
	////
	}

	/**
	 * Sets target columns of generated SELECT and UPDATE SQL.
	 *
	 * <p>
	 * @deprecated As of release 3.1.0,
	 * instead use {@link #columns(Collection)}
	 * </p>
	 *
	 * @param propertyNames a set of property names
	 * @return this object
	 *
	 * @throws NullPointerException if <b>propertyNames</b> is <b>null</b>
	 *
	 * @since 1.8.4
	 * @see #columns(Collection)
	 */
	@Deprecated
	public Sql<E> setColumns(Set<String> propertyNames) {
	// 3.1.0
	//	this.columns = Objects.requireNonNull(propertyNames, "propertyNames");
	//	return this;
		return columns(propertyNames);
	////
	}

	/**
	 * Sets target columns of generated SELECT and UPDATE SQL.
	 *
	 * <p>
	 * @deprecated As of release 3.1.0,
	 * instead use {@link #columns(Class)}
	 * </p>
	 *
	 * @param resultClass a entity class containing set of property names to specify
	 * @return this object
	 *
	 * @since 2.0.0
	 * @see #columns(Class)
	 */
	@Deprecated
	public Sql<E> setColumns(Class<?> resultClass) {
	// 3.1.0
	//	List<String> propertyNames = getEntityInfo(resultClass).accessor().valuePropertyNames();
	//	if (!tableAlias.isEmpty())
	//		propertyNames = propertyNames.stream()
	//			.map(propertyName -> tableAlias + '.' + propertyName)
	//			.collect(Collectors.toList());
	//
	//	columns = new HashSet<String>(propertyNames);
	//
	//	return this;
		return columns(resultClass);
	////
	}

	/**
	 * Associates <b>expression</b> with the column related to <b>propertyName</b>.
	 *
	 * <p>
	 * If <b>expression</b> is empty, releases the previous association of <b>propertyName</b>.
	 * </p>
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         <b>.expression("firstName", "'['||{firstName}||']'")</b>
	 *         .where("{lastName}={}", "Orange")
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         <b>.expression('firstName', "'['||{firstName}||']'")</b>
	 *         .where('{lastName}={}', 'Orange')
	 *         .connection(it)
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param propertyName the property name
	 * @param expression an expression
	 * @return this object
	 *
	 * @throws NullPointerException if <b>propertyName</b> or <b>expression</b> is <b>null</b>
	 *
	 * @see #getExpression(String)
	 */
	public Sql<E> expression(String propertyName, Expression expression) {
		Objects.requireNonNull(propertyName, "propertyName");
		Objects.requireNonNull(expression, "expression");

		if (expression.isEmpty())
			expressionMap.remove(propertyName);
		else
			expressionMap.put(propertyName, expression);

		return this;
	}

	/**
	 * Associates the expression to the column related to <b>propertyName</b>.
	 *
	 * <p>
	 * If the expression is empty, releases the previous association of <b>propertyName</b>.
	 * </p>
	 *
	 * @param propertyName the property name
	 * @param content a content of the expression
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
		Objects.requireNonNull(propertyName, "propertyName");

		return expressionMap.getOrDefault(propertyName, Expression.EMPTY);
	}

	/**
	 * Specifies the FROM clause of SELECT SQL as a subquery.
	 *
	 * @param fromSql <b>Sql</b> object to generate the FROM clause
	 * @return this object
	 *
	 * @throws NullPointerException if <b>fromSql</b> is <b>null</b>
	 *
	 * @since 3.1.0
	 */
	public Sql<E> from(Sql<?> fromSql) {
		this.fromSql = Objects.requireNonNull(fromSql);

		// synchronize table aliases with from Sql and union Sqls
		synchronizeTableAliases();

		// synchronize columns with from Sql and union Sqls
		synchronizeColumns();

		if (fromSql.where.isEmpty())
			fromSql.where = Condition.ALL;

		return this;
	}

	/**
	 * Returns <b>Sql</b> object to generate a FROM clause of SELECT SQL or <b>null </b> if not specified.
	 *
	 * @return <b>Sql</b> object to generate a FROM clause of SELECT SQL or <b>null</b>
	 *
	 * @since 3.1.0
	 */
	public Sql<?> getFrom() {
		return fromSql;
	}

	/**
	 * Add the information of the table that join with <b>INNER JOIN</b>.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * List&lt;Phone&gt; phones = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         <b>.innerJoin(Phone.class, "P", "{P.contactId}={C.id}")</b>
	 *         .connection(conn)
	 *         .&lt;Phone&gt;select(contacts::add, phones::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * List&lt;Phone&gt; phones = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         <b>.innerJoin(Phone, 'P', '{P.contactId}={C.id}')</b>
	 *         .connection(it)
	 *         .&lt;Phone&gt;select({contacts &lt;&lt; it}, {phones &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param <JE> the type of the entity related to the table to join
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
	public <JE> Sql<E> innerJoin(Class<JE> entityClass, String tableAlias, Condition on) {
		return join(JoinInfo.JoinType.INNER, entityClass, tableAlias, on);
	}

	/**
	 * Add the information of the table that join with <b>INNER JOIN</b>.
	 *
	 * @param <JE> the type of the entity related to the table to join
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
	public <JE> Sql<E> innerJoin(Class<JE> entityClass, String tableAlias, String on, Object... arguments) {
		return join(JoinInfo.JoinType.INNER, entityClass, tableAlias, Condition.of(on, arguments));
	}

	/**
	 * Add the information of the table that join with <b>LEFT OUTER JOIN</b>.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * List&lt;Phone&gt; phones = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         <b>.leftJoin(Phone.class, "P", "{P.contactId}={C.id}")</b>
	 *         .connection(conn)
	 *         .&lt;Phone&gt;select(contacts::add, phones::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * List&lt;Phone&gt; phones = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         <b>.leftJoin(Phone, 'P', '{P.contactId}={C.id}')</b>
	 *         .connection(it)
	 *         .&lt;Phone&gt;select({contacts &lt;&lt; it}, {phones &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param <JE> the type of the entity related to the table to join
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
	public <JE> Sql<E> leftJoin(Class<JE> entityClass, String tableAlias, Condition on) {
		return join(JoinInfo.JoinType.LEFT, entityClass, tableAlias, on);
	}

	/**
	 * Add the information of the table that join with <b>LEFT OUTER JOIN</b>.
	 *
	 * @param <JE> the type of the entity related to the table to join
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
	public <JE> Sql<E> leftJoin(Class<JE> entityClass, String tableAlias, String on, Object... arguments) {
		return join(JoinInfo.JoinType.LEFT, entityClass, tableAlias, Condition.of(on, arguments));
	}

	/**
	 * Add the information of the table that join with <b>RIGHT OUTER JOIN</b>.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * List&lt;Phone&gt; phones = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         <b>.rightJoin(Phone.class, "P", "{P.contactId}={C.id}")</b>
	 *         .connection(conn)
	 *         .&lt;Phone&gt;select(contacts::add, phones::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * List&lt;Phone&gt; phones = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         <b>.rightJoin(Phone, 'P', '{P.contactId}={C.id}')</b>
	 *         .connection(it)
	 *         .&lt;Phone&gt;select({contacts &lt;&lt; it}, {phones &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param <JE> the type of the entity related to the table to join
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
	public <JE> Sql<E> rightJoin(Class<JE> entityClass, String tableAlias, Condition on) {
		return join(JoinInfo.JoinType.RIGHT, entityClass, tableAlias, on);
	}

	/**
	 * Add the information of the table that join with <b>RIGHT OUTER JOIN</b>.
	 *
	 * @param <JE> the type of the entity related to the table to join
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
	public <JE> Sql<E> rightJoin(Class<JE> entityClass, String tableAlias, String on, Object... arguments) {
		return join(JoinInfo.JoinType.RIGHT, entityClass, tableAlias, Condition.of(on, arguments));
	}

	/**
	 * Add the information of the table that join with
 	 *   <b>INNER JOIN</b>, <b>LEFT OUTER JOIN</b> or <b>RIGHT OUTER JOIN</b>.
	 *
	 * @param <JE> the type of the entity related to the table to join
	 * @param joinType the join type
	 * @param entityClass the entity class related to the table to join
	 * @param tableAlias an alias of the table to join
	 * @param on the join condition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>joinType</b>, <b>entityClass</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
	 */
	private <JE> Sql<E> join(JoinInfo.JoinType joinType, Class<JE> entityClass, String tableAlias, Condition on) {
		EntityInfo<JE> entityInfo = getEntityInfo(entityClass);
		JoinInfo<JE> joinInfo = new JoinInfo<>(joinType, entityInfo, tableAlias, on);
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
	// 3.1.0
	//	return new ArrayList<>(joinInfos);
		return joinInfos;
	////
	}

	/**
	 * Specifies the condition of the <b>WHERE</b> clause.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         <b>.where("{birthday} IS NULL")</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         <b>.where('{birthday} IS NULL')</b>
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
		where = Objects.requireNonNull(condition, "condition");
		return this;
	}

	/**
	 * Specifies the condition of the <b>WHERE</b> clause by an <b>Expression</b>.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int id = 1;
	 * Contact[] contact = new Contact[1];
	 * Transaction.execute(conn -&gt; {
	 *     contact[0] = new Sql&lt;&gt;(Contact.class)
	 *         <b>.where("{id}={}", id)</b>
	 *         .connection(conn).select().orElse(null);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int id = 1
	 * Contact contact
	 * Transaction.execute {
	 *     contact = new Sql&lt;&gt;(Contact)
	 *         <b>.where('{id}={}', id)</b>
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
	 * Specifies the condition of the <b>WHERE</b> clause by an <b>EntityCondition</b>.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * Contact[] contact = new Contact[1];
	 * Transaction.execute(conn -&gt; {
	 *     contact[0] = new Sql&lt;&gt;(Contact.class)
	 *         <b>.where(new ContactKey(2))</b>
	 *         .connection(conn)
	 *         .select().orElse(null);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * Contact contact
	 * Transaction.execute {
	 *     Contact key = new Contact()
	 *     key.id = 2
	 *     contact = new Sql&lt;&gt;(Contact)
	 *         <b>.where(key)</b>
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
	 * Specifies the condition of the <b>WHERE</b> clause by a SubqueryCondition.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         <b>.where("EXISTS",</b>
	 *              <b>new Sql&lt;&gt;(Phone.class, "P")</b>
	 *                  <b>.where("{P.contactId}={C.id}")</b>
	 *                  <b>.and("{P.content} LIKE {}", "0800001%")</b>
	 *         <b>)</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         <b>.where('EXISTS',</b>
	 *              <b>new Sql&lt;&gt;(Phone, 'P')</b>
	 *                  <b>.where('{P.contactId}={C.id}')</b>
	 *                  <b>.and('{P.content} LIKE {}', '0800001%')</b>
	 *         <b>)</b>
	 *         .connection(it)
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param <SE> the type of the entity related to the subquery
	 * @param content the left part from the SELECT statement of a subquery
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
	 * Specifies the condition of the <b>WHERE</b> clause by a SubqueryCondition.
	 *
	 * @param <SE> the type of the entity related to the subquery
	 * @param subSql the <b>Sql</b> object for the subquery
	 * @param content the right part from the SELECT statement of a subquery
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
	 * Returns the condition of the <b>WHERE</b> clause that was specified.
	 *
	 * @return the condition of the <b>WHERE</b> clause
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
	 * Adds the condition using <b>AND</b> to the condition of the <b>HAVING</b> clause
	 * if after you invoke <b>having</b> method, 
	 * to the condition of the <b>WHERE</b> clause otherwise.
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
		Objects.requireNonNull(condition, "condition");

		if (having.isEmpty())
			where = where.and(condition);
		else
			having = having.and(condition);
		return this;
	}

	/**
	 * Adds a <b>Expression</b> condition using <b>AND</b> to the condition of the <b>HAVING</b> clause
	 * if after you invoke <b>having</b> method, 
	 * to the condition of the <b>WHERE</b> clause otherwise.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .where("{lastName}={}", "Apple")
	 *         <b>.and("{firstName}={}", "Akiyo")</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         .where('{lastName}={}', 'Apple')
	 *         <b>.and('{firstName}={}', 'Akiyo')</b>
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
	 * Adds a <b>SubqueryCondition</b> using <b>AND</b> to the condition of the <b>HAVING</b> clause
	 * if after you invoke <b>having</b> method, 
	 * to the condition of the <b>WHERE</b> clause otherwise.
	 *
	 * @param <SE> the type of the entity related to the subquery
	 * @param content the left part from the SELECT statement of the subquery
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
	 * Adds a <b>SubqueryCondition</b> using <b>AND</b> to the condition of the <b>HAVING</b> clause
	 * if after you invoke <b>having</b> method, 
	 * to the condition of the <b>WHERE</b> clause otherwise.
	 *
	 * @param <SE> the type of the entity related to the subquery
	 * @param subSql the <b>Sql</b> object for the subquery
	 * @param content the right part from the SELECT statement of the subquery
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
	 * Adds the condition using <b>OR</b> to the condition of the <b>HAVING</b> clause
	 * if after you invoke <b>having</b> method, 
	 * to the condition of the <b>WHERE</b> clause otherwise.
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
		Objects.requireNonNull(condition, "condition");

		if (having.isEmpty())
			where = where.or(condition);
		else
			having = having.or(condition);
		return this;
	}

	/**
	 * Adds a <b>Expression</b> condition using <b>OR</b> to the condition of the <b>HAVING</b> clause
	 * if after you invoke <b>having</b> method, 
	 * to the condition of the <b>WHERE</b> clause otherwise.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .where("{lastName}={}", "Apple")
	 *         <b>.or("{lastName}={}", "Orange")</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         .where('{lastName}={}', 'Apple')
	 *         <b>.or('{lastName}={}', 'Orange')</b>
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
	 * Adds a <b>SubqueryCondition</b> using <b>OR</b> to the condition of the <b>HAVING</b> clause
	 * if after you invoke <b>having</b> method, 
	 * to the condition of the <b>WHERE</b> clause otherwise.
	 *
	 * @param <SE> the type of the entity related to the subquery
	 * @param content the left part from the SELECT statement of the subquery
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
	 * Adds a <b>SubqueryCondition</b> using <b>OR</b> to the condition of the <b>HAVING</b> clause
	 * if after you invoke <b>having</b> method, 
	 * to the condition of the <b>WHERE</b> clause otherwise.
	 *
	 * @param <SE> the type of the entity related to the subquery
	 * @param content the left part from the SELECT statement of the subquery
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
	 * Specifies an element of the <b>GROUP BY</b> clause.
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
	 * Sets the contents of the <b>GROUP BY</b> clause.
	 *
	 * @param groupBy the contents of the <b>GROUP BY</b> clause
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
	 * Returns the contents of the <b>GROUP BY</b> clause.
	 *
	 * @return the contents of the <b>GROUP BY</b> clause
	 *
	 * @see #groupBy(String, Object...)
	 * @see #setGroupBy(GroupBy)
	 */
	public GroupBy getGroupBy() {
	// 3.1.0
	//	return groupBy.clone();
		return groupBy;
	////
	}

	/**
	 * Specifies the condition of the <b>HAVING</b> clause.
	 *
	 * @param condition the condition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>condition</b> is <b>null</b>
	 *
	 * @see #getHaving()
	 */
	public Sql<E> having(Condition condition) {
		having = Objects.requireNonNull(condition, "condition");
		return this;
	}

	/**
	 * Specifies the condition of the <b>HAVING</b> clause by an <b>Expression</b>.
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
	 * Specifies the condition of the <b>HAVING</b> clause by a <b>SubqueryCondition</b>.
	 *
	 * @param <SE> the type of the entity related to the subquery
	 * @param content the left part from the SELECT statement of the subquery
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
	 * Specifies the condition of the <b>HAVING</b> clause by a <b>SubqueryCondition</b>.
	 *
	 * @param <SE> the type of the entity related to the subquery
	 * @param subSql the <b>Sql</b> object for the subquery
	 * @param content the right part from the SELECT statement of the subquery
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
	 * Returns the condition of the <b>HAVING</b> clause that was specified.
	 *
	 * @return the condition of the <b>HAVING</b> clause
	 *
	 * @see #having(Condition)
	 * @see #having(String, Object...)
	 * @see #having(String, Sql)
	 */
	public Condition getHaving() {
		return having;
	}

	/**
	 * Adds the <b>Sql</b> object that generate a SELECT SQL for a UNION SQL component.
	 *
	 * @param <UE> the type of the entity related to the UNION SQL component
	 * @param unionSql the <b>Sql</b> object that generate a SELECT SQL for a UNION SQL component
	 * @return this object
	 *
	 * @throws IllegalStateException <b>unionAll</b> method has already been called
	 *
	 * @since 3.1.0
	 * @see #unionAll(Sql)
	 */
	public <UE> Sql<E> union(Sql<UE> unionSql) {
		return unionOrUnionAll(unionSql, false);
	}

	/**
	 * Adds the <b>Sql</b> object that generate a SELECT SQL for a UNION ALL SQL component.
	 *
	 * @param <UE> the type of the entity related to the UNION SQL component
	 * @param unionSql the <b>Sql</b> object that generate a SELECT SQL for a UNION ALL SQL component
	 * @return this object
	 *
	 * @throws IllegalStateException <b>union</b> method has already been called
	 *
	 * @since 3.1.0
	 * @see #union(Sql)
	 */
	public <UE> Sql<E> unionAll(Sql<UE> unionSql) {
		return unionOrUnionAll(unionSql, true);
	}

	/**
	 * Adds the <b>Sql</b> object that generate a SELECT SQL for a UNION or UNION ALL SQL component.
	 *
	 * @param <UE> the type of the entity related to the UNION sql component
	 * @param unionSql the <b>Sql</b> object that generatw a SELECT SQL for a UNION or UNION ALL SQL component
	 * @return this object
	 *
	 * @throws IllegalStateException if both <b>union</b> and <b>unionAll</b> are called.
	 *
	 * @since 3.1.0
	 */
	private <UE> Sql<E> unionOrUnionAll(Sql<UE> unionSql, boolean unionAll) {
		if (unionSqls.size() == 0) {
			this.unionAll = unionAll;
		} else {
			if (this.unionAll != unionAll)
				throw new IllegalStateException(
					MessageFormat.format(unionAll ? messageUnionCalled : messageUnionAllCalled,
						entityInfo.entityClass().getName()));
		}

		unionSqls.add(Objects.requireNonNull(unionSql, "unionSql"));

		// synchronize table aliases with from Sql and union Sqls
		synchronizeTableAliases();

		// synchronize columns with from Sql and union Sqls
		synchronizeColumns();

		return this;
	}

	/**
	 * Returns a list of <b>Sql</b> objects that generate SELECT SQL for a UNION or UNION ALL SQL component.
	 *
	 * @return all Sql objects to generate UNION or UNION ALL SQL
	 *
	 * @since 3.1.0
	 */
	public List<Sql<?>> getUnionSqls() {
		return unionSqls;
	}

	/**
	 * Returns <b>true</b> if generates UNION ALL SQL, <b>false</b> if generates UNION SQL.
	 *
	 * @return <b>true</b> if generates UNION ALL SQL, <b>false</b> if generates UNION SQL
	 *
	 * @since 3.1.0
	 */
	public boolean isUnionAll() {
		return unionAll;
	}

	/**
	 * Specifies an element of the <b>ORDER BY</b> clause.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         <b>.orderBy("{lastName}")</b>
	 *         <b>.orderBy("{firstName}")</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         <b>.orderBy('{lastName}')</b>
	 *         <b>.orderBy('{firstName}')</b>
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
	 * Sets the element of the last specified <b>ORDER BY</b> clause in ascending order.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .orderBy("{id}")<b>.asc()</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
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
	 * Sets the element of the last specified <b>ORDER BY</b> clause in descending order.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .orderBy("{id}")<b>.desc()</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
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
	 * Sets the contents of the <b>ORDER BY</b> clause.
	 *
	 * @param orderBy the contents of the <b>ORDER BY</b> clause
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
	 * Returns the contents of the <b>ORDER BY</b> clause that was specified.
	 *
	 * @return the contents of the <b>ORDER BY</b> clause
	 *
	 * @see #orderBy(java.lang.String, java.lang.Object...)
	 * @see #setOrderBy(OrderBy)
	 */
	public OrderBy getOrderBy() {
	// 3.1.0
	//	return orderBy.clone();
		return orderBy;
	////
	}

	/**
	 * Specifies the <b>LIMIT</b> value.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         <b>.limit(5)</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         <b>.limit(5)</b>
	 *         .connection(it)
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param limit the <b>LIMIT</b> value
	 * @return this object
	 *
	 * @see #getLimit()
	 */
	public Sql<E> limit(int limit) {
		this.limit = limit;
		return this;
	}

	/**
	 * Returns the <b>LIMIT</b> value that was specified.
	 *
	 * @return the <b>LIMIT</b> value
	 *
	 * @see #limit(int)
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * Specifies the <b>OFFSET</b> value.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .limit(5)<b>.offset(5)</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
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
	 * @param offset the <b>OFFSET</b> value
	 * @return this object
	 *
	 * @see #getOffset()
	 */
	public Sql<E> offset(int offset) {
		this.offset = offset;
		return this;
	}

	/**
	 * Returns the <b>OFFSET</b> value that was specified.
	 *
	 * @return the <b>OFFSET</b> value
	 *
	 * @see #offset(int)
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Specifies that appends <b>FOR UPDATE</b> to SELECT SQL.
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
	 * Returns whether to append <b>FOR UPDATE</b> to SELECT SQL.
	 *
	 * @return <b>true</b> if appends <b>FOR UPDATE</b>, <b>false</b> otherwise
	 *
	 * @see #forUpdate()
	 */
	public boolean isForUpdate() {
		return forUpdate;
	}

	/**
	 * Specifies that appends <b>NO WAIT</b> to SELECT SQL.
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
	 * Specifies that append <b>WAIT n</b> to SELECT SQL.
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
	 * Returns the argument of <b>WAIT</b> of SELECT SQL.
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
	 * Returns whether to append <b>NOWAIT</b> to SELECT SQL.
	 *
	 * @return <b>true</b> if appends <b>NOWAIT</b>, <b>false</b> otherwise
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
	 * Returns whether to append neither <b>NOWAIT</b> nor <b>WAIT n</b> to SELECT SQL.
	 *
	 * @return <b>true</b> if appends neither <b>NOWAIT</b> nor <b>WAIT n</b>, <b>false</b> otherwise
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
		this.connection = Objects.requireNonNull(connection, "connection");
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
		return generatedSql;
	}

	/**
	 * Executes <b>action</b>.
	 *
	 * @param action an action to be executed
	 * @return this object
	 *
	 * @since 2.0.0
	 * @see #doIf(boolean, Consumer)
	 */
	public Sql<E> doAlways(Consumer<Sql<E>> action) {
		Objects.requireNonNull(action, "action").accept(this);

		return this;
	}

	/**
	 * Executes <b>action</b> if <b>condition</b> is <b>true</b>.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .connection(conn)
	 *         <b>.doIf(!(conn.getDatabase() instanceof SQLite), Sql::forUpdate)</b>
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         .connection(it)
	 *         <b>.doIf(!(conn.database instanceof SQLite)) {it.forUpdate}</b>
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param condition the condition of execution
	 * @param action an action
	 * @return this object
	 *
	 * @see #doNotIf(boolean, Consumer)
	 * @see #doElse(Consumer)
	 * @see #doAlways(Consumer)
	 */
	public Sql<E> doIf(boolean condition, Consumer<Sql<E>> action) {
	// 3.0.0
		doIfCondition = condition;
	////
		if (condition)
			Objects.requireNonNull(action, "action").accept(this);

		return this;
	}

	/**
	 * Executes <b>action</b> if <b>condition</b>  is <b>false</b>.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .connection(conn)
	 *         <b>.doNotIf(conn.getDatabase() instanceof SQLite, Sql::forUpdate)</b>
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         .connection(it)
	 *         <b>.doNotIf(conn.database instanceof SQLite) {it.forUpdate}</b>
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param condition the condition of execution
	 * @param action an action
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
	 * @param elseAction an action
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
			Objects.requireNonNull(elseAction, "elseAction").accept(this);
		}

		return this;
	}

	/**
	 * Executes <b>action</b> if <b>condition</b> is <b>true</b>, executes <b>elseAction</b> otherwise.
	 *
	 * <p>
	 * @deprecated As of release 3.0.0,
	 * instead use {@link #doIf(boolean, Consumer)} and {@link #doElse(Consumer)}
	 * </p>
	 *
	 * @param condition the condition of execution
	 * @param action an action to be executed if <b>condition</b> is <b>true</b>
	 * @param elseAction an action to be executed if <b>condition</b>  is <b>false</b>
	 * @return this object
	 */
	@Deprecated
	public Sql<E> doIf(boolean condition, Consumer<Sql<E>> action, Consumer<Sql<E>> elseAction) {
		Objects.requireNonNull(condition ? action : elseAction,
			() -> condition ? "action" : "elseAction").accept(this);

		return this;
	}

	/**
	 * Returns the <b>SqlEntityInfo</b> object related to the specified table alias.
	 *
	 * <p>
	 * <i>This method is used internally.</i>
	 * </p>
	 *
	 * @param tableAlias a table alias
	 * @return the SqlEntityInfo objcet
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
	 * @param sqlEntityInfo the SqlEntityInfo object
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
	 * Generates and executes a SELECT SQL that joins no tables.
	 *
	 * <p>
	 * Adds following string to <b>columns</b> set
	 * if <b>columns</b> method is not called and
	 * <b>innerJoin</b>, <b>leftJoin</b> or <b>rightJoin</b> method called.<br>
	 * </p>
	 *
	 * <ul class="code" style="list-style-type:none">
	 *   <li>"&lt;Main Table Alias&gt;.*"</li>
	 * </ul>
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.select(contacts::add)</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.select({contacts &lt;&lt; it})</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">Caution:</span>
	 * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before calling this method.
	 * </p>
	 *
	 * @param consumer a consumer of the entities created from the <b>ResultSet</b>
	 *
	 * @throws NullPointerException if <b>consumer</b> is <b>null</b>
	 * @throws IllegalStateException if a SELECT SQL without columns was generated
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 *
	 * @since 2.0.0
	 * @see #selectAs(Class, Consumer)
	 */
	public void select(Consumer<? super E> consumer) {
		selectAs(entityInfo.entityClass(), consumer);
	}

	/**
	 * Generates and executes a SELECT SQL that joins no tables.
	 *
	 * <p>
	 * Adds following string to <b>columns</b> set
	 * if <b>columns</b> method is not called and
	 * <b>innerJoin</b>, <b>leftJoin</b> or <b>rightJoin</b> method called.<br>
	 * </p>
	 *
	 * <ul class="code" style="list-style-type:none">
	 *   <li>"&lt;Main Table Alias&gt;.*"</li>
	 * </ul>
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;ContactName&gt; contactNames = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.selectAs(ContactName.class, contactNames::add)</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;ContactName&gt; contactNames = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.selectAs(ContactName, {contactNames &lt;&lt; it})</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">Caution:</span>
	 * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before calling this method.
	 * </p>
	 *
	 * @param <RE> the type of the result entity
	 * @param resultClass the class of the argumrnt of <b>consumer</b>
	 * @param consumer a consumer of the entities created from the <b>ResultSet</b>
	 *
	 * @throws NullPointerException if <b>resultClass</b> or <b>consumer</b> is <b>null</b>
	 * @throws IllegalStateException if a SELECT SQL without columns was generated
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 *
	 * @since 2.0.0
	 * @see #select(Consumer)
	 */
	public <RE> void selectAs(Class<RE> resultClass, Consumer<? super RE> consumer) {
		Objects.requireNonNull(resultClass, "resultClass");
		Objects.requireNonNull(consumer, "consumer");
	// 3.1.0
		if (connection == null)
			throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));
	////

		Sql<E> sql = this;

		if (sql.where.isEmpty()) {
			sql = clone();
			sql.where = Condition.ALL;
		}

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
	 * Generates and executes a SELECT SQL that joins one table.
	 *
	 * <p>
	 * Adds following strings to <b>columns</b> set
	 * if <b>columns</b> method is not called and
	 * <b>innerJoin</b>, <b>leftJoin</b> or <b>rightJoin</b> method called more than once.
	 * </p>
	 *
	 * <ul class="code" style="list-style-type:none">
	 *   <li>"&lt;Main Table Alias&gt;.*"</li>
	 *   <li>"&lt;Joined Table Alias&gt;.*"</li>
	 * </ul>
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * List&lt;Phone&gt;   phones = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
	 *         .connection(conn)
	 *         <b>.&lt;Phone&gt;select(contacts::add, phones::add)</b>;
	 * });
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
	 *         <b>.select({contacts &lt;&lt; it}, {phones &lt;&lt; it})</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">Caution:</span>
	 * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before calling this method.
	 * </p>
	 *
	 * @param <JE1> the type of the entity related to the joined table
	 * @param consumer a consumer of the entities related to the main table created from the <b>ResultSet</b>
	 * @param consumer1 a consumer of the entities related to the joined table created from the <b>ResultSet</b>
	 *
	 * @throws NullPointerException if <b>consumer</b> or <b>consumer1</b> is <b>null</b>
	 * @throws IllegalStateException if joinInfo information is less than 1
	 * @throws IllegalStateException if a SELECT SQL without columns was generated
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 *
	 * @since 2.0.0
	 */
	public <JE1> void select(
		Consumer<? super E> consumer,
		Consumer<? super JE1> consumer1) {
		if (joinInfos.size() < 1) throw new IllegalStateException("joinInfos.size < 1");
	// 3.1.0
		if (connection == null)
			throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));
	////


		Sql<E> sql = this;

		if (sql.where.isEmpty()) {
			sql = clone();
			sql.where = Condition.ALL;
		}

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
	 * Generates and executes a SELECT SQL that joins two tables.
	 *
	 * <p>
	 * Adds following strings to <b>columns</b> set
	 * if <b>columns</b> method is not called and
	 * <b>innerJoin</b>, <b>leftJoin</b> or <b>rightJoin</b> method called more than twice.
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
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * List&lt;Phone&gt;   phones = new ArrayList&lt;&gt;();
	 * List&lt;Email&gt;   emails = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
	 *         .innerJoin(Email.class, "E", "{E.contactId}={C.id}")
	 *         .connection(conn)
	 *         <b>.&lt;Phone, Email&gt;select(contacts::add, phones::add, emails::add)</b>;
	 * });
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
	 *         <b>.select({contacts &lt;&lt; it}, {phones &lt;&lt; it}, {emails &lt;&lt; it})</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">Caution:</span>
	 * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before calling this method.
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
	 * @throws IllegalStateException if a SELECT SQL without columns was generated
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 *
	 * @since 2.0.0
	 */
	public <JE1, JE2> void select(
		Consumer<? super E> consumer,
		Consumer<? super JE1> consumer1,
		Consumer<? super JE2> consumer2) {
		if (joinInfos.size() < 2) throw new IllegalStateException("joinInfos.size < 2");
	// 3.1.0
		if (connection == null)
			throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));
	////


		Sql<E> sql = this;

		if (sql.where.isEmpty()) {
			sql = clone();
			sql.where = Condition.ALL;
		}

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
	 * Generates and executes a SELECT SQL that joins three tables.
	 *
	 * <p>
	 * Adds following strings to <b>columns</b> set
	 * if <b>columns</b> method is not called and
	 * <b>innerJoin</b>, <b>leftJoin</b> or <b>rightJoin</b> method called more than 3 times.
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
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * List&lt;Phone&gt;   phones = new ArrayList&lt;&gt;();
	 * List&lt;Email&gt;   emails = new ArrayList&lt;&gt;();
	 * List&lt;Address&gt; addresses = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .innerJoin(  Phone.class, "P", "{P.contactId}={C.id}")
	 *         .innerJoin(  Email.class, "E", "{E.contactId}={C.id}")
	 *         .innerJoin(Address.class, "A", "{A.contactId}={C.id}")
	 *         .connection(conn)
	 *         <b>.&lt;Phone, Email, Address&gt;select(</b>
	 *             <b>contacts::add, phones::add, emails::add, addresses::add)</b>;
	 * });
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
	 *         <b>.select(</b>
	 *             <b>{contacts &lt;&lt; it}, {phones &lt;&lt; it}, {emails &lt;&lt; it}, {addresses &lt;&lt; it})</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">Caution:</span>
	 * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before calling this method.
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
	 * @throws IllegalStateException if a SELECT SQL without columns was generated
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
	// 3.1.0
		if (connection == null)
			throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));
	////


		Sql<E> sql = this;

		if (sql.where.isEmpty()) {
			sql = clone();
			sql.where = Condition.ALL;
		}

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
	 * Generates and executes a SELECT SQL that joins four tables.
	 *
	 * <p>
	 * Adds following strings to <b>columns</b> set
	 * if <b>columns</b> method is not called and
	 * <b>innerJoin</b>, <b>leftJoin</b> or <b>rightJoin</b> method called more than 4 times.
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
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * List&lt;Phone&gt;   phones = new ArrayList&lt;&gt;();
	 * List&lt;Email&gt;   emails = new ArrayList&lt;&gt;();
	 * List&lt;Address&gt; addresses = new ArrayList&lt;&gt;();
	 * List&lt;Url&gt;     urls = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .innerJoin(  Phone.class, "P", "{P.contactId}={C.id}")
	 *         .innerJoin(  Email.class, "E", "{E.contactId}={C.id}")
	 *         .innerJoin(Address.class, "A", "{A.contactId}={C.id}")
	 *         .innerJoin(    Url.class, "U", "{U.contactId}={C.id}")
	 *         .connection(conn)
	 *         <b>.&lt;Phone, Email, Address, Url&gt;select(</b>
	 *             <b>contacts::add, phones::add, emails::add,</b>
	 *             <b>addresses::add, urls::add)</b>;
	 * });
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
	 *         <b>.select(</b>
	 *             <b>{contacts &lt;&lt; it}, {phones &lt;&lt; it}, {emails &lt;&lt; it},</b>
	 *             <b>{addresses &lt;&lt; it}, {urls &lt;&lt; it})</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">Caution:</span>
	 * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before calling this method.
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
	 * @throws IllegalStateException if a SELECT SQL without columns was generated
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
	// 3.1.0
		if (connection == null)
			throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));
	////


		Sql<E> sql = this;

		if (sql.where.isEmpty()) {
			sql = clone();
			sql.where = Condition.ALL;
		}

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
	 * Generates and executes a SELECT SQL
	 * and returns an <b>Optional</b> of the entity if searched, <b>Optional.empty()</b> otherwise.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * Contact[] contact = new Contact[1];
	 * Transaction.execute(conn -&gt; {
	 *     contact[0] = new Sql&lt;&gt;(Contact.class)
	 *         .where("{id}={}", 1)
	 *         .connection(conn)
	 *         <b>.select()</b>.orElse(null);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * Contact contact
	 * Transaction.execute {
	 *     contact = new Sql&lt;&gt;(Contact)
	 *         .where('{id}={}', 1)
	 *         .connection(it)
	 *         <b>.select()</b>.orElse(null)
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">Caution:</span>
	 * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before calling this method.
	 * </p>
	 *
	 * @return an <b>Optional</b> of the entity if searched, <b>Optional.empty()</b> otherwise
	 *
	 * @throws IllegalStateException if a SELECT SQL without columns was generated
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
	 * Generates and executes a SELECT SQL
	 * and returns an <b>Optional</b> of the entity if searched, <b>Optional.empty()</b> otherwise.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * ContactName[] contactName = new ContactName[1];
	 * Transaction.execute(conn -&gt; {
	 *     contactName[0] = new Sql&lt;&gt;(Contact.class)
	 *         .where("{id}={}", 1)
	 *         .connection(conn)
	 *         <b>.selectAs(ContactName.class)</b>.orElse(null);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * ContactName contactName
	 * Transaction.execute {
	 *     contactName = new Sql&lt;&gt;(Contact)
	 *         .where('{id}={}', 1)
	 *         .connection(it)
	 *         <b>.selectAs(ContactName)</b>.orElse(null)
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">Caution:</span>
	 * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before calling this method.
	 * </p>
	 *
	 * @param <RE> the type of the result entity
	 * @param resultClass the class of entity to as a return value
	 * @return an <b>Optional</b> of the entity if searched, <b>Optional.empty()</b> otherwise
	 *
	 * @throws NullPointerException <b>resultClass</b> is <b>null</b>
	 * @throws IllegalStateException if a SELECT SQL without columns was generated
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
				throw new ManyRowsException(generatedSql);
			entities.add(entity);
		});
		return entities.isEmpty() ? Optional.empty() : Optional.of(entities.get(0));
	}

	/**
	 * Generates and executes a SELECT COUNT(*) SQL and returns the result.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int[] count = new int[1];
	 * Transaction.execute(conn -&gt; {
	 *     count[0] = new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.selectCount()</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int count
	 * Transaction.execute {
	 *     count = new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.selectCount()</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">Caution:</span>
	 * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before calling this method.
	 * </p>
	 *
	 * @return the number of selected rows
	 *
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 *
	 * @since 2.0.0
	 */
	public int selectCount() {
	// 3.1.0
		if (connection == null)
			throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));
	////

		Sql<E> sql = this;

		if (sql.where.isEmpty()) {
			sql = clone();
			sql.where = Condition.ALL;
		}

		List<Object> parameters = new ArrayList<>();
		String sqlString = connection.getDatabase().subSelectSql(sql, () -> "COUNT(*)", parameters);

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
	 * Generates and executes an INSERT SQL.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int[] count = new int[1];
	 * Transaction.execute(conn -&gt; {
	 *     count[0] = new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.insert(new Contact(6, "Setoka", "Orange", 2001, 2, 1))</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int count
	 * Transaction.execute {
	 *     count = new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.insert(new Contact(6, 'Setoka', 'Orange', 2001, 2, 1))</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">Caution:</span>
	 * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before calling this method.
	 * </p>
	 *
	 * @param entity the entity to be inserted
	 * @return the number of rows inserted
	 *
	 * @throws NullPointerException if <b>entity</b> is <b>null</b>
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 *
	 * @since 2.0.0
	 */
	public int insert(E entity) {
		Objects.requireNonNull(entity, "entity");
		if (connection == null)
			throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));

		if (entity instanceof PreStore)
			((PreStore)entity).preStore();

		int count = 0;

		// before INSERT
		if (entity instanceof PreInsert)
			count += ((PreInsert)entity).preInsert(connection);

		Sql<E> sql = clone();
		sql.entity = entity;

		List<Object> parameters = new ArrayList<>();
		generatedSql = connection.getDatabase().insertSql(sql, parameters);
		count += sql.executeUpdate(generatedSql, parameters);

		// after INSERT
		if (entity instanceof Composite)
			count += ((Composite)entity).postInsert(connection);

		return count;
	}

	/**
	 * Generates and executes INSERT SQLs for each element of entities.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int[] count = new int[1];
	 * Transaction.execute(conn -&gt; {
	 *     count[0] = new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.insert(Arrays.asList(</b>
	 *             <b>new Contact(7, "Harumi", "Orange", 2001, 2, 2),</b>
	 *             <b>new Contact(8, "Mihaya", "Orange", 2001, 2, 3),</b>
	 *             <b>new Contact(9, "Asumi" , "Orange", 2001, 2, 4)</b>
	 *         <b>))</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int count
	 * Transaction.execute {
	 *     count = new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.insert([</b>
	 *             <b>new Contact(7, 'Harumi', 'Orange', 2001, 2, 2),</b>
	 *             <b>new Contact(8, 'Mihaya', 'Orange', 2001, 2, 3),</b>
	 *             <b>new Contact(9, 'Asumi' , 'Orange', 2001, 2, 4)</b>
	 *         <b>])</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">Caution:</span>
	 * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before calling this method.
	 * </p>
	 *
	 * @param entities an <b>Iterable</b> of entities
	 * @return the number of rows inserted
	 *
	 * @throws NullPointerException if <b>entities</b> or any element of <b>entities</b> is <b>null</b>
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 *
	 * @since 2.0.0
	 */
	public int insert(Iterable<? extends E> entities) {
		int[] count = new int[1];
		Objects.requireNonNull(entities, "entities")
			.forEach(entity -> count[0] += insert(entity));
		return count[0];
	}

	/**
	 * Generates and executes an UPDATE SQL.
	 *
	 * <p>
	 * If the condition of the <b>WHERE</b> clause is specified, updates by the condition.<br>
	 * To update all rows of the target table, specify <b>Condition.ALL</b> to <b>WHERE</b> conditions.
	 * </p>
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int[] count = new int[1];
	 * Transaction.execute(conn -&gt; {
	 *     count[0] = new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.update(new Contact(6, "Setoka", "Orange", 2017, 2, 1))</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int count
	 * Transaction.execute {
	 *     count = new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.update(new Contact(6, 'Setoka', 'Orange', 2017, 2, 1))</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">Caution:</span>
	 * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before calling this method.
	 * </p>
	 *
	 * @param entity the entity to be updated
	 * @return the number of rows updated
	 *
	 * @throws NullPointerException if <b>entity</b> is <b>null</b>
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 *
	 * @since 2.0.0
	 */
	public int update(E entity) {
	// 3.1.0
		if (connection == null)
			throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));
	////

		this.entity = Objects.requireNonNull(entity, "entity");

		if (entity instanceof PreStore)
			((PreStore)entity).preStore();

		Sql<E> sql = this;

		if (sql.where.isEmpty()) {
			sql = clone();
			sql.where = Condition.of(entity);
		}

		List<Object> parameters = new ArrayList<>();
		generatedSql = connection.getDatabase().updateSql(sql, parameters);
		int count = sql.executeUpdate(generatedSql, parameters);

		// after UPDATE
		if (sql.where instanceof EntityCondition && entity instanceof Composite)
			count += ((Composite)entity).postUpdate(connection);

		return count;
	}

	/**
	 * Generates and executes UPDATE SQLs for each element of <b>entities</b>.
	 *
	 * <p>
	 * Even if the condition of the <b>WHERE</b> clause is specified,
	 * <b>new EntityCondition(entity)</b> will be specified for each entity.
	 * </p>
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int[] count = new int[1];
	 * Transaction.execute(conn -&gt; {
	 *     count[0] = new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.update(Arrays.asList(</b>
	 *             <b>new Contact(7, "Harumi", "Orange", 2017, 2, 2),</b>
	 *             <b>new Contact(8, "Mihaya", "Orange", 2017, 2, 3),</b>
	 *             <b>new Contact(9, "Asumi" , "Orange", 2017, 2, 4)</b>
	 *         <b>))</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int count
	 * Transaction.execute {
	 *     count = new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.update([</b>
	 *             <b>new Contact(7, 'Harumi', 'Orange', 2017, 2, 2),</b>
	 *             <b>new Contact(8, 'Mihaya', 'Orange', 2017, 2, 3),</b>
	 *             <b>new Contact(9, 'Asumi' , 'Orange', 2017, 2, 4)</b>
	 *         <b>])</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">Caution:</span>
	 * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before calling this method.
	 * </p>
	 *
	 * @param entities an <b>Iterable</b> of entities
	 * @return the number of rows updated
	 *
	 * @throws NullPointerException if <b>entities</b> or any element of <b>entities</b> is <b>null</b>
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 *
	 * @since 2.0.0
	 */
	public int update(Iterable<? extends E> entities) {
		Sql<E> sql = this;

		if (!sql.where.isEmpty()) {
			sql = clone();
			sql.where = Condition.EMPTY;
		}

		int[] count = new int[1];
		Objects.requireNonNull(entities, "entities")
			.forEach(entity -> count[0] += update(entity));
		return count[0];
	}

	/**
	 * Generates and executes a DELETE SQL.
	 *
	 * <p>
	 * If the <B>WHERE</b> condition is not specified, dose not delete.<br>
	 * To delete all rows of the target table, specify <b>Condition.ALL</b> to <b>WHERE</b> conditions.
	 * </p>
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int[] count = new int[1];
	 * Transaction.execute(conn -&gt; {
	 *     count[0] = new Sql&lt;&gt;(Contact.class)
	 *         .where(Condition.ALL)
	 *         .connection(conn)
	 *         <b>.delete()</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int count
	 * Transaction.execute {
	 *     count = new Sql&lt;&gt;(Contact)
	 *         .where(Condition.ALL)
	 *         .connection(it)
	 *         <b>.delete()</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">Caution:</span>
	 * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before calling this method.
	 * </p>
	 *
	 * @return the number of rows deleted
	 *
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 *
	 * @since 2.0.0
	 */
	public int delete() {
	// 3.1.0
		if (connection == null)
			throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));
	////

		if (where.isEmpty()) {
			logger.warn(MessageFormat.format(messageNoWhereCondition, entityInfo.entityClass().getName()));
			return 0;
		}

		List<Object> parameters = new ArrayList<>();
		String sqlString = connection.getDatabase().deleteSql(this, parameters);
		return executeUpdate(sqlString, parameters);
	}

	/**
	 * Generates and executes a DELETE SQL.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int[] count = new int[1];
	 * Transaction.execute(conn -&gt; {
	 *     count[0] = new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.delete(new Contact(6))</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int count
	 * Transaction.execute {
	 *     count = new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.delete(new Contact(6))</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">Caution:</span>
	 * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before calling this method.
	 * </p>
	 *
	 * @param entity the entity to be deleted
	 * @return the number of rows deleted
	 *
	 * @throws NullPointerException if <b>entity</b> is <b>null</b>
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 *
	 * @since 2.0.0
	 */
	public int delete(E entity) {
	// 3.1.0
		if (connection == null)
			throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));
	////

		Sql<E> sql = clone();
		sql.where = Condition.of(Objects.requireNonNull(entity, "entity"));

		List<Object> parameters = new ArrayList<>();
		generatedSql = connection.getDatabase().deleteSql(sql, parameters);
		int count = sql.executeUpdate(generatedSql, parameters);

		// after DELETE
		if (entity instanceof Composite)
			count += ((Composite)entity).postDelete(connection);

		return count;
	}

	/**
	 * Generates and executes DELETE SQLs for each element of entities.
	 *
	 * <div class="exampleTitle"><span>Java Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int[] count = new int[1];
	 * Transaction.execute(conn -&gt; {
	 *     count[0] = new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.delete(Arrays.asList(new Contact(7), new Contact(8), new Contact(9)))</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>Groovy Example</span></div>
	 * <div class="exampleCode"><pre>
	 * int count
	 * Transaction.execute {
	 *     count = new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.delete([new Contact(7), new Contact(8), new Contact(9)])</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">Caution:</span>
	 * Call {@link #connection(ConnectionWrapper)} method to specify the connection wrapper before calling this method.
	 * </p>
	 *
	 * @param entities an <b>Iterable</b> of entities
	 * @return the number of rows deleted
	 *
	 * @throws NullPointerException if <b>entities</b> or any element of <b>entities</b> is <b>null</b>
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 *
	 * @since 2.0.0
	 */
	public int delete(Iterable<? extends E> entities) {
		int[] count = new int[1];
		Objects.requireNonNull(entities, "entities")
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

					// 3.0.0
					//	try {
					//		Object value = resultSet.getObject(columnAlias);
						// Gets a value and convert type to store
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
					////
						entityInfo.accessor().setValue(entity, columnInfo.propertyName(), convertedValue);
					// 3.0.0
					//	}
					//	catch (SQLException e) {throw new RuntimeSQLException(e);}
					//
					});

				// After get
				if (entity instanceof PostLoad)
					((PostLoad)entity).postLoad();

				if (entity instanceof Composite)
					((Composite)entity).postSelect(connection);

				// Consumes the entity
				consumer.accept(entity);
			}
			catch (RuntimeException e) {throw e;}
			catch (Exception e) {throw new RuntimeException(e);}
		};
	}


	/**
	 * Executes the SELECT SQL.
	 *
	 * @param connection the connection wrapper
	 * @param sql the SQL
	 * @param parameters the parameters of SQL
	 * @param consumer the consumer for the <b>ResultSet</b> object
	 *
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 */
	private void executeQuery(String sql, List<Object> parameters, Consumer<ResultSet> consumer) {
		Objects.requireNonNull(sql, "sql");
		Objects.requireNonNull(parameters, "parameters");
		Objects.requireNonNull(consumer, "consumer");
		if (connection == null)
			throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));

		int sqlNo = Sql.sqlNo++;
		if (logger.isInfoEnabled())
			logger.info('#' + Integer.toUnsignedString(sqlNo) + ' '
				+ connection.toString() + ' ' + sql);

		// Prepares SQL
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
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
					logger.info(sqlNoStr + MessageFormat.format(messageSelect0Rows,
						timeFormat.format(execTime) + timeFormat.format(getTime)));
					break;
				case 1:
					logger.info(sqlNoStr + MessageFormat.format(messageSelectRow,
						timeFormat.format(execTime), timeFormat.format(getTime)));
					break;
				default:
					logger.info(sqlNoStr + MessageFormat.format(messageSelectRows, rowCount,
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
	 * Executes the SQL which is INSERT, UPDATE or DELETE SQL.
	 *
	 * @param sql the SQL
	 * @param parameters the parameters of SQL
	 *
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 */
	private int executeUpdate(String sql, List<Object> parameters) {
		Objects.requireNonNull(sql, "sql");
		Objects.requireNonNull(parameters, "parameters");
		if (connection == null)
			throw new IllegalStateException(MessageFormat.format(messageNoConnection, entityInfo.entityClass().getName()));

		int sqlNo = Sql.sqlNo++;
		if (logger.isInfoEnabled())
			logger.info('#' + Integer.toUnsignedString(sqlNo) + ' '
			//	+ connection.getDatabase().getClass().getSimpleName() + ": " + sql);
				+ connection.toString() + ' '  + sql);
			////

		// Prepares SQL
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
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
				switch (rowCount) {
				case 0:
					logger.info(sqlNoStr + MessageFormat.format(messageUpdate0Rows, timeFormat.format(execTime)));
					break;
				case 1:
					logger.info(sqlNoStr + MessageFormat.format(messageUpdateRow, timeFormat.format(execTime)));
					break;
				default:
					logger.info(sqlNoStr + MessageFormat.format(messageUpdateRows, rowCount, timeFormat.format(execTime)));
					break;
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
		// synchronize table aliases with from Sql
		if (fromSql != null) {
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

		// synchronize table aliases with union Sqls
		int index = 0;
		for (Sql<?> unionSql : unionSqls) {
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
	 * Synchronize columns with from Sql and union Sqls.
	 *
	 * @since 3.1.0
	 */
	private void synchronizeColumns() {
		// synchronize columns with from Sql
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

		// synchronize columns with union Sqls
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
	 * Returns a string representation of this object.
	 *
	 * @param set the set
	 * @return a string representation of this object
	 *
	 * @since 3.1.0
	 */
	private static <T> String toString(Set<T> set) {
		return set.stream()
			.map(element -> element.toString())
			.collect(Collectors.joining(", ", "[", "]"));
	}
}
