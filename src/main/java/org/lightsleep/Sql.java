// Sql.java
// (C) 2016 Masato Kokubo

package org.lightsleep;

import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.lightsleep.component.Condition;
import org.lightsleep.component.EntityCondition;
import org.lightsleep.component.Expression;
import org.lightsleep.component.GroupBy;
import org.lightsleep.component.OrderBy;
import org.lightsleep.connection.ConnectionSupplier;
import org.lightsleep.connection.Jdbc;
import org.lightsleep.database.Database;
import org.lightsleep.database.Standard;
import org.lightsleep.entity.Composite;
import org.lightsleep.entity.PostLoad;
import org.lightsleep.entity.PreInsert;
import org.lightsleep.entity.PreStore;
import org.lightsleep.helper.Accessor;
import org.lightsleep.helper.ColumnInfo;
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
 * <div class="sampleTitle"><span>Example</span></div>
 * <div class="sampleCode"><pre>
 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
 * Transaction.execute(connection -&gt; {
 *     new <b>Sql</b>&lt;&gt;(Contact.class)
 *         .<b>where</b>("{familyName} = {}", "Apple")
 *         .<b>select</b>(connection, contacts::add);
 * });
 * </pre></div>
 *
 * <div class="sampleTitle"><span>SQL</span></div>
 * <div class="sampleCode"><pre>
 * SELECT id, familyName, givenName, ... FROM Contact WHERE familyName='Apple'
 * </pre></div>
 *
 * @param <E> the entity class related to the main table
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
@SuppressWarnings("unchecked")
public class Sql<E> implements SqlEntityInfo<E> {
	/**
	 * The wait value of forever
	 * @since 1.9.0
	 */
	public static final int FOREVER = Integer.MAX_VALUE;

	// The logger
	private static final Logger logger = LoggerFactory.getLogger(Sql.class);

	// The version resource
// 1.8.6
//	private static final String version = new Resource("lightsleep-version").get("version");
	private static final String version =
		new Resource(Sql.class.getPackage().getName() + ".lightsleep-version").getString("version");
////

	// Class resources
	private static final Resource resource = new Resource(Sql.class);
// 1.8.6
//	private static final String messageDatabaseHandler            = resource.get("messageDatabaseHandler");
//	private static final String messageDatabaseHandlerNotFound    = resource.get("messageDatabaseHandlerNotFound");
//	private static final String messageConnectionSupplier         = resource.get("messageConnectionSupplier");
//	private static final String messageConnectionSupplierNotFound = resource.get("messageConnectionSupplierNotFound");
//	private static final String messageNoWhereCondition           = resource.get("messageNoWhereCondition");
//	private static final String messageRows                       = resource.get("messageRows");
//	private static final String messageRowsSelect                 = resource.get("messageRowsSelect");
	private static final String messageDatabaseHandler            = resource.getString("messageDatabaseHandler");
	private static final String messageDatabaseHandlerNotFound    = resource.getString("messageDatabaseHandlerNotFound");
	private static final String messageConnectionSupplier         = resource.getString("messageConnectionSupplier");
	private static final String messageConnectionSupplierNotFound = resource.getString("messageConnectionSupplierNotFound");
	private static final String messageNoWhereCondition           = resource.getString("messageNoWhereCondition");
	private static final String messageRows                       = resource.getString("messageRows");
	private static final String messageRowsSelect                 = resource.getString("messageRowsSelect");
////

	// The entity information map
	private static final Map<Class<?>, EntityInfo<?>> entityInfoMap = new LinkedHashMap<>();

	// The entity information
	private final EntityInfo<E> entityInfo;

	// The table alias
	private String tableAlias = "";

	// The entity that are referenced from expressions
	private E entity;

	// With DISTINCT or not
	private boolean distinct = false;

	// The select columns
	private Set<String> columns = new HashSet<>();

	// The expression map (property name : expression)
	private Map<String, Expression> expressionMap = new HashMap<>();

	// The join informations
	private List<JoinInfo<?>> joinInfos = new ArrayList<>();

	// SQL entity information map
	private final Map<String, SqlEntityInfo<?>> sqlEntityInfoMap = new LinkedHashMap<>();

	// The WHERE condition
	private Condition where = Condition.EMPTY;

	// The GROUP BY info.
	private GroupBy groupBy = GroupBy.EMPTY;

	// The HAVING condition
	private Condition having = Condition.EMPTY;

	// The ORDER BY information
	private OrderBy orderBy = OrderBy.EMPTY;

	// The LIMIT value
	private int limit = Integer.MAX_VALUE;

	// The OFFSET value
	private int offset = 0;

	// Whether with FOR UPDATE
	private boolean forUpdate = false;

// 1.9.0
//	// Whether with NO WAIT
//	private boolean noWait = false;
	// The WAIT time (sec)
	private int waitTime = FOREVER;
////

	// The database handler
	private static Database database;

	// The connection supplier
	private static ConnectionSupplier connectionSupplier;

	// The generated SQL @since 1.5.0
	private String generatedSql;

	static {
		logger.info("Lightsleep " + version + " / logger: " + LoggerFactory.loggerClass.getName());
	}

	/**
	 * Returns the database handler.
	 *
	 * @return database the database handler
	 *
	 * @see #setDatabase(Database)
	 */
	public static Database getDatabase() {
		return database;
	}

	/**
	 * Sets the database handler.
	 *
	 * @param database the database handler
	 *
	 * @throws NullPointerException if <b>database</b> is null
	 *
	 * @see #getDatabase()
	 */
	public static void setDatabase(Database database) {
		Objects.requireNonNull(database, "database");

		Sql.database = database;
		logger.info(() -> MessageFormat.format(messageDatabaseHandler, Sql.database.getClass().getName()));
	}

	//  Initialize the database handler
	static {
	// 1.2.0
	//	String databaseName = Resource.globalResource.get("Database", "Standard");
	// 1.8.6
	//	String databaseName = Resource.globalResource.get(Database.class.getSimpleName(), Standard.class.getSimpleName());
		String databaseName = Resource.globalResource.getString(Database.class.getSimpleName(), Standard.class.getSimpleName());
	////
		if (databaseName.indexOf('.') < 0)
			databaseName = Database.class.getPackage().getName() + '.' + databaseName;

		Class<? extends Database> databaseClass;
		try {
			databaseClass = (Class<? extends Database>)Class.forName(databaseName);
		}
		catch (ClassNotFoundException e) {
			logger.error(MessageFormat.format(messageDatabaseHandlerNotFound, databaseName), e);
			databaseClass = Standard.class;
		}

		try {
			setDatabase((Database)databaseClass.getMethod("instance").invoke(null));
		}
		catch (Exception e) {
		// 1.9.0
		//	logger.error("", e);
			logger.error(e.toString(), e);
		////
		}
	}

	/**
	 * Returns the connection supplier.
	 *
	 * @return the connection supplier
	 *
	 * @see #setConnectionSupplier(ConnectionSupplier)
	 */
	public static ConnectionSupplier getConnectionSupplier() {
		return connectionSupplier;
	}

	/**
	 * Sets the connection supplier.
	 *
	 * @param supplier the connection supplier
	 *
	 * @throws NullPointerException if <b>supplier</b> is null
	 *
	 * @see #getConnectionSupplier()
	 */
	public static void setConnectionSupplier(ConnectionSupplier supplier) {
		Objects.requireNonNull(supplier, "supplier");

		connectionSupplier = supplier;
		logger.debug(() -> MessageFormat.format(messageConnectionSupplier, connectionSupplier.getClass().getName()));
	}

	// Initialize the connection supplier
	static {
	// 1.2.0
	//	String supplierName = Resource.globalResource.get("ConnectionSupplier", "Jdbc");
	// 1.8.6
	//	String supplierName = Resource.globalResource.get(ConnectionSupplier.class.getSimpleName(), Jdbc.class.getSimpleName());
		String supplierName = Resource.globalResource.getString(ConnectionSupplier.class.getSimpleName(), Jdbc.class.getSimpleName());
	////
		if (supplierName.indexOf('.') < 0)
			supplierName = ConnectionSupplier.class.getPackage().getName() + '.' + supplierName;

		Class<? extends ConnectionSupplier> supplierClass;
		try {
			supplierClass = (Class<? extends ConnectionSupplier>)Class.forName(supplierName);
		}
		catch (ClassNotFoundException e) {
			logger.error(MessageFormat.format(messageConnectionSupplierNotFound, supplierName), e);
			supplierClass = Jdbc.class;
		}

		try {
		// 1.3.0 for Java 9
			setConnectionSupplier(supplierClass.getConstructor().newInstance());
		////
		}
	// 1.3.0 for Java 9
	//	catch (InstantiationException | IllegalAccessException e) {
		catch (Exception e) {
	////
		// 1.9.0
		//	logger.error("", e);
			logger.error(e.toString(), e);
		////
		}
	}

	/**
	 * Returns the entity information related to the specified entity class.
	 *
	 * @param <E> the entity class related to the main table
	 * @param entityClass the entity class
	 * @return the entity information
	 *
	 * @throws NullPointerException if <b>entityClass</b> is null
	 *
	 * @see #entityInfo()
	 */
	public static <E> EntityInfo<E> getEntityInfo(Class<E> entityClass) {
		Objects.requireNonNull(entityClass, "entityClass");

		EntityInfo<E> entityInfo = (EntityInfo<E>)entityInfoMap.get(entityClass);

 		if (entityInfo == null) {
			synchronized(entityInfoMap) {
				// creates a new entity information and put it into the map
				entityInfo = new EntityInfo<>(entityClass);
				entityInfoMap.put(entityClass, entityInfo);
			}
		}

		return entityInfo;
	}

	/**
	 * Constructs a new <b>Sql</b>.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class)
	 * </pre></div>
	 *
	 * @throws NullPointerException if <b>entityClass</b> is null
	 *
	 * @param entityClass the entity class
	 */
	public Sql(Class<E> entityClass) {
		this(entityClass, "");
	}

	/**
	 * Constructs a new <b>Sql</b>.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 * </pre></div>
	 *
	 * @param entityClass the entity class
	 * @param tableAlias the table alias
	 *
	 * @throws NullPointerException if <b>entityClass</b> or <b>tableAlias</b> is null
	 */
	public Sql(Class<E> entityClass, String tableAlias) {
		Objects.requireNonNull(entityClass, "entityClass");
		Objects.requireNonNull(tableAlias, "tableAlias");

		entityInfo = getEntityInfo(entityClass);
		this.tableAlias = tableAlias;
		addSqlEntityInfo(this);
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
	 * @param entity the entity
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
	 * Specifies the columns used in SELECT and UPDATE SQL.
	 *
	 * <p>
	 * Specify the property names that related to the columns.
	 * You can also be specified <b>"*"</b> or <b>"<i>&lt;table alias&gt;</i>.*"</b>.
	 * If this method is not called it will be in the same manner as <b>"*"</b> is specified.
	 * </p>
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .<b>columns("familyName", "givenName")</b>
	 * </pre></div>
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .innerJoin(Phone.class, "P", "{P.contactId} = {C.id}")
	 *         .<b>columns("C.id", "P.*")</b>
	 * </pre></div>
	 *
	 * @param columns the array of the property names related to the columns
	 *
	 * @return this object
	 *
	 * @throws NullPointerException if <b>columns</b> or any of <b>columns</b> is null
	 *
	 * @see #getColumns()
	 * @see #setColumns(Set)
	 */
	public Sql<E> columns(String... columns) {
		Objects.requireNonNull(columns, "columns");

	// 1.8.2
	//	for (String column : columns)
	//		this.columns.add(column);
		Arrays.stream(columns).forEach(this.columns::add);
	////
		return this;
	}

	/**
	 * Returns a set of property names related to the columns used in SELECT and UPDATE SQL.
	 *
	 * @return a set of property names
	 *
	 * @see #columns(String...)
	 * @see #setColumns(Set)
	 */
	public Set<String> getColumns() {
	// 1.8.4
	//	return columns;
	//	return new HashSet<String>(columns);
		try {
			return (Set<String>)columns.getClass().getMethod("clone").invoke(columns);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	////
	}

	/**
	 * Sets the set of property names related to the columns used in SELECT and UPDATE SQL.
	 *
	 * @param columns the set of property names
	 * @return this object
	 *
	 * @throws NullPointerException if <b>columns</b> is null
	 *
	 * @since 1.8.4
	 *
	 * @see #columns(String...)
	 * @see #getColumns()
	 */
	public Sql<E> setColumns(Set<String> columns) {
		Objects.requireNonNull(columns, "columns");

		this.columns = columns;
		return this;
	}

	/**
	 * Associates <b>expression</b> to the column related to <b>propertyName</b>.
	 *
	 * <p>
	 * If <b>expression</b> is empty, releases the previous association of <b>propertyName</b>.
	 * </p>
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .<b>expression("birthday", "'['||{birthday}||']'")</b>
	 * </pre></div>
	 *
	 * @param propertyName the property name
	 * @param expression the expression
	 * @return this object
	 *
	 * @throws NullPointerException if <b>propertyName</b> or <b>expression</b> is null
	 *
	 * @see #getExpression(String)
	 */
	public Sql<E> expression(String propertyName, Expression expression) {
		Objects.requireNonNull(propertyName, "propertyName");
		Objects.requireNonNull(expression, "expression");

		if (expression.content().isEmpty())
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
	 * @param content the content of the expression
	 * @param arguments the arguments of the expression
	 * @return this object
	 *
	 * @throws NullPointerException if <b>propertyName</b>, <b>content</b> or <b>arguments</b> is null
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
	 * @throws NullPointerException if <b>propertyName</b> is null
	 *
	 * @see #expression(String, Expression)
	 * @see #expression(String, String, Object...)
	 */
	public Expression getExpression(String propertyName) {
		Objects.requireNonNull(propertyName, "propertyName");

		return expressionMap.getOrDefault(propertyName, Expression.EMPTY);
	}

	/**
	 * Add an information of the table that join with <b>INNER JOIN</b>.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .<b>innerJoin(Phone.class, "P", "{P.contactId} = {C.id}")</b>
	 *         .&lt;Phone&gt;select(connection, contacts::add, phones::add);
	 * </pre></div>
	 *
	 * @param <JE> the entity class that related to the table to join
	 * @param entityClass the entity class that related to the table to join
	 * @param tableAlias the alias of the table to join
	 * @param on the join condition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b> or <b>on</b> is null
	 *
	 * @see #getJoinInfos()
	 * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
	 */
	public <JE> Sql<E> innerJoin(Class<JE> entityClass, String tableAlias, Condition on) {
		return join(JoinInfo.JoinType.INNER, entityClass, tableAlias, on);
	}

	/**
	 * Add an information of the table that join with <b>INNER JOIN</b>.
	 *
	 * @param <JE> the entity class that related to the table to join
	 * @param entityClass the entity class that related to the table to join
	 * @param tableAlias the alias of the table to join
	 * @param on the join condition
	 * @param arguments the arguments of the join condition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b>, <b>on</b> or <b>arguments</b> is null
	 *
	 * @see #getJoinInfos()
	 * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
	 */
	public <JE> Sql<E> innerJoin(Class<JE> entityClass, String tableAlias, String on, Object... arguments) {
		return join(JoinInfo.JoinType.INNER, entityClass, tableAlias, Condition.of(on, arguments));
	}

	/**
	 * Add an information of the table that join with <b>LEFT OUTER JOIN</b>.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .<b>leftJoin(Phone.class, "P", "{P.contactId} = {C.id}")</b>
	 *         .&lt;Phone&gt;select(connection, contacts::add, phones::add);
	 * </pre></div>
	 *
	 * @param <JE> the entity class that related to the table to join
	 * @param entityClass the entity class that related to the table to join
	 * @param tableAlias the alias of the table to join
	 * @param on the join condition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b> or <b>on</b> is null
	 *
	 * @see #getJoinInfos()
	 * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
	 */
	public <JE> Sql<E> leftJoin(Class<JE> entityClass, String tableAlias, Condition on) {
		return join(JoinInfo.JoinType.LEFT, entityClass, tableAlias, on);
	}

	/**
	 * Add an information of the table that join with <b>LEFT OUTER JOIN</b>.
	 *
	 * @param <JE> the entity class that related to the table to join
	 * @param entityClass the entity class that related to the table to join
	 * @param tableAlias the alias of the table to join
	 * @param on the join condition
	 * @param arguments the arguments of the join condition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b>, <b>on</b> or <b>arguments</b> is null
	 *
	 * @see #getJoinInfos()
	 * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
	 */
	public <JE> Sql<E> leftJoin(Class<JE> entityClass, String tableAlias, String on, Object... arguments) {
		return join(JoinInfo.JoinType.LEFT, entityClass, tableAlias, Condition.of(on, arguments));
	}

	/**
	 * Add an information of the table that join with <b>RIGHT OUTER JOIN</b>.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .<b>rightJoin(Phone.class, "P", "{P.contactId} = {C.id}")</b>
	 *         .&lt;Phone&gt;select(connection, contacts::add, phones::add);
	 * </pre></div>
	 *
	 * @param <JE> the entity class that related to the table to join
	 * @param entityClass the entity class that related to the table to join
	 * @param tableAlias the alias of the table to join
	 * @param on the join condition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b> or <b>on</b> is null
	 *
	 * @see #getJoinInfos()
	 * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
	 */
	public <JE> Sql<E> rightJoin(Class<JE> entityClass, String tableAlias, Condition on) {
		return join(JoinInfo.JoinType.RIGHT, entityClass, tableAlias, on);
	}

	/**
	 * Add an information of the table that join with <b>RIGHT OUTER JOIN</b>.
	 *
	 * @param <JE> the entity class that related to the table to join
	 * @param entityClass the entity class that related to the table to join
	 * @param tableAlias the alias of the table to join
	 * @param on the join condition
	 * @param arguments the arguments of the join condition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b>, <b>on</b> or <b>arguments</b> is null
	 *
	 * @see #getJoinInfos()
	 * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
	 */
	public <JE> Sql<E> rightJoin(Class<JE> entityClass, String tableAlias, String on, Object... arguments) {
		return join(JoinInfo.JoinType.RIGHT, entityClass, tableAlias, Condition.of(on, arguments));
	}

	/**
	 * Add an information of the table that join with
 	 *   <b>INNER JOIN</b>, <b>LEFT OUTER JOIN</b> or <b>RIGHT OUTER JOIN</b>.
	 *
	 * @param <JE> the entity class that related to the table to join
	 * @param joinType the join type
	 * @param entityClass the entity class that related to the table to join
	 * @param tableAlias the alias of the table to join
	 * @param on the join condition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>joinType</b>, <b>entityClass</b>, <b>tableAlias</b> or <b>on</b> is null
	 */
	private <JE> Sql<E> join(JoinInfo.JoinType joinType, Class<JE> entityClass, String tableAlias, Condition on) {
		EntityInfo<JE> entityInfo = Sql.getEntityInfo(entityClass);
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
		return joinInfos;
	}

	/**
	 * Specifies the <b>WHERE</b> condition.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .<b>where("{birthday} IS NULL")</b>
	 *         .select(connection, contacts::add);
	 * </pre></div>
	 *
	 * @param condition the condition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>condition</b> is null
	 *
	 * @see #getWhere()
	 */
	public Sql<E> where(Condition condition) {
		Objects.requireNonNull(condition, "condition");

		where = condition;
		return this;
	}

	/**
	 * Specifies the <b>WHERE</b> condition by <b>Expression</b>.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .<b>where("{id} = {}", contactId)</b>
	 *         .select(connection).orElse(null);
	 * </pre></div>
	 *
	 * @param content the content of the <b>Expression</b>
	 * @param arguments the arguments of the <b>Expression</b>
	 * @return this object
	 *
	 * @throws NullPointerException if <b>content</b> or <b>arguments</b> is null
	 *
	 * @see #getWhere()
	 * @see Condition#of(String, Object...)
	 */
	public Sql<E> where(String content, Object... arguments) {
		where = Condition.of(content, arguments);
		return this;
	}

	/**
	 * Specifies the <b>WHERE</b> condition by EntityCondition.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     Contact contact = new Contact();
	 *     contact.familyName = "Apple";
	 *     contact.givenName = "Yukari";
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .<b>where(contact)</b>
	 *         .select(connection, contacts::add);
	 * </pre></div>
	 *
	 * @param entity the entity of the EntityCondition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>entity</b> is null
	 *
	 * @see #getWhere()
	 * @see Condition#of(Object)
	 */
	public Sql<E> where(E entity) {
		where = Condition.of(entity);
		return this;
	}

	/**
	 * Specifies the <b>WHERE</b> condition by SubqueryCondition.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .<b>where("EXISTS",</b>
	 *              <b>new Sql&lt;&gt;(Phone.class, "P")</b>
	 *                  <b>.where("{P.contactId} = {C.id}")</b>
	 *                  <b>.and("{P.content} LIKE {}", "080%")</b>
	 *         <b>)</b>
	 *         .select(connection, contacts::add);
	 * </pre></div>
	 *
	 * @param <SE> the entity class related to the subquery
	 * @param content the content of the SubqueryCondition
	 * @param subSql the Sql object of the SubqueryCondition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>content</b> or <b>subSql</b> is null
	 *
	 * @see #getWhere()
	 * @see Condition#of(String, Sql, Sql)
	 */
	public <SE> Sql<E> where(String content, Sql<SE> subSql) {
		where = Condition.of(content, this, subSql);
		return this;
	}

	/**
	 * Returns the <b>WHERE</b> condition that was specified.
	 *
	 * @return the <b>WHERE</b> condition
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
	 * Adds the condition using <b>AND</b> to the <b>HAVING</b> condition
	 * if after you call <b>having</b> method, 
	 * to the <b>WHERE</b> condition otherwise.
	 *
	 * @param condition the condition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>condition</b> is null
	 *
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
	 * Adds a <b>Expression</b> condition using <b>AND</b> to the <b>HAVING</b> condition
	 * if after you call <b>having</b> method, 
	 * to the <b>WHERE</b> condition otherwise.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .where("{familyName} = {}", "Apple")
	 *         .<b>and("{givenName} = {}", "Akiyo")</b>
	 *         .select(connection, contacts::add);
	 * </pre></div>
	 *
	 * @param content the content of the <b>Expression</b>
	 * @param arguments the arguments of the <b>Expression</b>
	 * @return this object
	 *
	 * @throws NullPointerException if <b>content</b> or <b>arguments</b> is null
	 *
	 * @see Condition#and(String, Object...)
	 */
	public Sql<E> and(String content, Object... arguments) {
		return and(Condition.of(content, arguments));
	}

	/**
	 * Adds a <b>SubqueryCondition</b> using <b>AND</b> to the <b>HAVING</b> condition
	 * if after you call <b>having</b> method, 
	 * to the <b>WHERE</b> condition otherwise.
	 *
	 * @param <SE> the entity class related to the subquery
	 * @param content the content of the SubqueryCondition
	 * @param subSql the Sql object of the SubqueryCondition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>content</b> or <b>subSql</b> is null
	 *
	 * @see Condition#and(String, Sql, Sql)
	 */
	public <SE> Sql<E> and(String content, Sql<SE> subSql) {
		return and(Condition.of(content, this, subSql));
	}

	/**
	 * Adds the condition using <b>OR</b> to the <b>HAVING</b> condition
	 * if after you call <b>having</b> method, 
	 * to the <b>WHERE</b> condition otherwise.
	 *
	 * @param condition the condition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>condition</b> is null
	 *
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
	 * Adds a <b>Expression</b> condition using <b>OR</b> to the <b>HAVING</b> condition
	 * if after you call <b>having</b> method, 
	 * to the <b>WHERE</b> condition otherwise.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .where("{familyName} = {}", "Apple")
	 *         .<b>or("{familyName} = {}", "Orange")</b>
	 *         .select(connection, contacts::add);
	 * </pre></div>
	 *
	 * @param content the content of the <b>Expression</b>
	 * @param arguments the arguments of the <b>Expression</b>
	 * @return this object
	 *
	 * @throws NullPointerException if <b>content</b> or <b>arguments</b> is null
	 *
	 * @see Condition#or(String, Object...)
	 */
	public Sql<E> or(String content, Object... arguments) {
		return or(Condition.of(content, arguments));
	}

	/**
	 * Adds a <b>SubqueryCondition</b> using <b>OR</b> to the <b>HAVING</b> condition
	 * if after you call <b>having</b> method, 
	 * to the <b>WHERE</b> condition otherwise.
	 *
	 * @param <SE> the entity class related to the subquery
	 * @param content the content of the SubqueryCondition
	 * @param subSql the Sql object of the SubqueryCondition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>content</b> or <b>subSql</b> is null
	 *
	 * @see Condition#or(String, Sql, Sql)
	 */
	public <SE> Sql<E> or(String content, Sql<SE> subSql) {
		return or(Condition.of(content, this, subSql));
	}

	/**
	 * Specifies the <b>GROUP BY</b> expression.
	 *
	 * @param content the content of the <b>Expression</b>
	 * @param arguments the arguments of the <b>Expression</b>
	 * @return this object
	 *
	 * @throws NullPointerException if <b>content</b> or <b>arguments</b> is null
	 *
	 * @see #getGroupBy()
	 * @see GroupBy
	 * @see Expression#Expression(String, Object...)
	 */
	public Sql<E> groupBy(String content, Object... arguments) {
		groupBy = groupBy.add(new Expression(content, arguments));
		return this;
	}

	/**
	 * Returns the <b>GROUP BY</b> information that was specified.
	 *
	 * @return the <b>GROUP BY</b> information
	 *
	 * @see #groupBy(String, Object...)
	 */
	public GroupBy getGroupBy() {
		return groupBy;
	}

	/**
	 * Specifies the <b>HAVING</b> condition.
	 *
	 * @param condition the condition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>condition</b> is null
	 *
	 * @see #getHaving()
	 */
	public Sql<E> having(Condition condition) {
		Objects.requireNonNull(condition, "condition");

		having = condition;
		return this;
	}

	/**
	 * Specifies the <b>HAVING</b> condition by <b>Expression</b>.
	 *
	 * @param content the content of the <b>Expression</b>
	 * @param arguments the arguments of the <b>Expression</b>
	 * @return this object
	 *
	 * @throws NullPointerException if <b>content</b> or <b>arguments</b> is null
	 *
	 * @see #getHaving()
	 * @see Condition#of(String, Object...)
	 */
	public Sql<E> having(String content, Object... arguments) {
		having = Condition.of(content, arguments);
		return this;
	}

	/**
	 * Specifies the <b>HAVING</b> condition by <b>SubqueryCondition</b>.
	 *
	 * @param <SE> the entity class related to the subquery
	 * @param content the content of the SubqueryCondition
	 * @param subSql the Sql object of the SubqueryCondition
	 * @return this object
	 *
	 * @throws NullPointerException if <b>content</b> or <b>subSql</b> is null
	 *
	 * @see #getHaving()
	 * @see Condition#of(String, Sql, Sql)
	 */
	public <SE> Sql<E> having(String content, Sql<SE> subSql) {
		having = Condition.of(content, this, subSql);
		return this;
	}

	/**
	 * Returns the <b>HAVING</b> condition that was specified.
	 *
	 * @return the <b>HAVING</b> condition
	 *
	 * @see #having(Condition)
	 * @see #having(String, Object...)
	 * @see #having(String, Sql)
	 */
	public Condition getHaving() {
		return having;
	}

	/**
	 * Specifies the <b>ORDER BY</b> expression.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .<b>orderBy("{familyName}")</b>
	 *         .select(connection, contacts::add);
	 * </pre></div>
	 *
	 * @param content the content of the <b>Expression</b>
	 * @param arguments the arguments of the <b>Expression</b>
	 * @return this object
	 *
	 * @throws NullPointerException if <b>content</b> or <b>arguments</b> is null
	 *
	 * @see #asc()
	 * @see #desc()
	 * @see #getOrderBy()
	 * @see OrderBy#add(OrderBy.Element)
	 * @see OrderBy.Element#Element(String, Object...)
	 */
	public Sql<E> orderBy(String content, Object... arguments) {
		orderBy = orderBy.add(new OrderBy.Element(content, arguments));
		return this;
	}

	/**
	 * Specifies the sort order to ascend.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .orderBy("{id}").<b>asc()</b>
	 *         .select(connection, contacts::add);
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
	 * Specifies the sort order to descend.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .orderBy("{id}").<b>desc()</b>
	 *         .select(connection, contacts::add);
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
	 * Returns the <b>ORDER BY</b> information that was specified.
	 *
	 * @return the <b>ORDER BY</b> information
	 *
	 * @see #orderBy(java.lang.String, java.lang.Object...)
	 * @see #asc()
	 * @see #desc()
	 */
	public OrderBy getOrderBy() {
		return orderBy;
	}

	/**
	 * Specifies the <b>LIMIT</b> value.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .<b>limit(10)</b>
	 *         .select(connection, contacts::add);
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
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .limit(10).<b>offset(100)</b>
	 *         .select(connection, contacts::add);
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
	// 1.9.0
	//	noWait = true;
		waitTime = 0;
	////
		return this;
	}

	/**
	 * Specifies that append <b>WAIT n</b> to SELECT SQL.
	 *
	 * @param waitTime the wait time (second)
	 * @return this object
	 *
	 * @since 1.9.0
	 *
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
	 *
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
	// 1.9.0
	//	return noWait;
		return waitTime == 0;
	////
	}

	/**
	 * Returns whether to append neither <b>NOWAIT</b> nor <b>WAIT n</b> to SELECT SQL.
	 *
	 * @return <b>true</b> if appends neither <b>NOWAIT</b> nor <b>WAIT n</b>, <b>false</b> otherwise
	 *
	 * @since 1.9.0
	 *
	 * @see #noWait()
	 * @see #wait(int)
	 * @see #getWaitTime()
	 * @see #isNoWait()
	 */
	public boolean isWaitForever() {
		return waitTime == FOREVER;
	}

	/**
	 * Executes <b>action</b> if <b>condition</b> is true.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .<b>doIf(!(Sql.getDatabase() instanceof SQLite), Sql::forUpdate)</b>
	 *         .select(connection, contacts::add);
	 * </pre></div>
	 *
	 * @param condition the condition
	 * @param action the action that is executed if <b>condition</b> is <b>true</b>
	 * @return this object
	 */
	public Sql<E> doIf(boolean condition, Consumer<Sql<E>> action) {
		Objects.requireNonNull(action, "action");

		if (condition)
			action.accept(this);

		return this;
	}

	/**
	 * Executes <b>action</b> if <b>condition</b> is true, <b>elseAction</b> otherwise.
	 *
	 * @param condition the condition
	 * @param action the action that is executed if <b>condition</b> is true
	 * @param elseAction the action that is executed if <b>condition</b> is false
	 * @return this object
	 */
	public Sql<E> doIf(boolean condition, Consumer<Sql<E>> action, Consumer<Sql<E>> elseAction) {
		Objects.requireNonNull(action, "action");
		Objects.requireNonNull(elseAction, "elseAction");

		if (condition)
			action.accept(this);
		else
			elseAction.accept(this);

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
	 * @throws NullPointerException if <b>tableAlias</b> is null
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
		String tableAlias = sqlEntityInfo.tableAlias();
		if (!sqlEntityInfoMap.containsKey(tableAlias))
			sqlEntityInfoMap.put(tableAlias, sqlEntityInfo);

		if (sqlEntityInfo instanceof Sql) {
			((Sql<?>)sqlEntityInfo).sqlEntityInfoMap.values().stream()
				.filter(sqlEntityInfo2 -> sqlEntityInfo2 != sqlEntityInfo)
			// 1.8.2
			//	.forEach(sqlEntityInfo2 -> addSqlEntityInfo(sqlEntityInfo2));
				.forEach(this::addSqlEntityInfo);
			////
		}
	}

	/**
	 * Generates and executes a SELECT SQL that joins no tables.
	 *
	 * <p>
	 * Adds following string to <b>columns</b> set
	 * if <b>columns</b> method is not called and
	 * <b>innerJoin</b>, <b>leftJoin</b> or <b>rightJoin</b> method called. <i>(since 1.8.4)</i><br>
	 * </p>
	 *
	 * <ul class="code" style="list-style-type:none">
	 *   <li>"&lt;Main Table Alias&gt;.*"</li>
	 * </ul>
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .<b>select(connection, contacts::add)</b>;
	 * </pre></div>
	 *
	 * @param connection the database connection
	 * @param consumer the consumer of the entities generated from retrieved rows
	 *
	 * @throws NullPointerException if <b>connection</b> or consumer is null
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 */
	public void select(Connection connection, Consumer<? super E> consumer) {
	// 1.8.2
		if (where.isEmpty())
			where = Condition.ALL;
	////
	// 1.8.4
		if (columns.size() == 0 && joinInfos.size() > 0)
			columns.add(tableAlias + ".*");
	////
		List<Object> parameters = new ArrayList<>();
		String sql = getDatabase().selectSql(this, parameters);

		executeQuery(connection, sql, parameters, getRowConsumer(connection, this, consumer));
	}

	/**
	 * Generates and executes a SELECT SQL that joins one table.
	 *
	 * <p>
	 * Adds following strings to <b>columns</b> set
	 * if <b>columns</b> method is not called and
	 * <b>innerJoin</b>, <b>leftJoin</b> or <b>rightJoin</b> method called more than once. <i>(since 1.8.4)</i>
	 * </p>
	 *
	 * <ul class="code" style="list-style-type:none">
	 *   <li>"&lt;Main Table Alias&gt;.*"</li>
	 *   <li>"&lt;Joined Table Alias&gt;.*"</li>
	 * </ul>
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 *     List&lt;Phone&gt; phones = new ArrayList&lt;&gt;();
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .innerJoin(Phone.class, "P", "{P.contactId} = {C.id}")
	 *         .<b>&lt;Phone&gt;select(connection, contacts::add, phones::add)</b>;
	 * </pre></div>
	 *
	 * @param <JE1> the entity class related to the joined table
	 * @param connection the database connection
	 * @param consumer the consumer of the entities related to the main table generated from retrieved rows
	 * @param consumer1 the consumer of the entities related to the joined table generated from retrieved rows
	 *
	 * @throws NullPointerException if <b>connection</b>, <b>consumer</b> or consumer1 is null
	 * @throws IllegalStateException if joinInfo information is less than 1
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 */
	public <JE1> void select(Connection connection,
		Consumer<? super E> consumer,
		Consumer<? super JE1> consumer1) {
	//	if (joinInfos.size() < 1) throw new IllegalStateException("Sql.select: joinInfos.size < 1");
		if (joinInfos.size() < 1) throw new IllegalStateException("joinInfos.size < 1");

	// 1.8.2
		if (where.isEmpty())
			where = Condition.ALL;
	////
	// 1.8.4
		if (columns.size() == 0 && joinInfos.size() > 1) {
			columns.add(tableAlias + ".*");
			columns.add(joinInfos.get(0).tableAlias() + ".*");
		}
	////
		List<Object> parameters = new ArrayList<>();
		String sql = getDatabase().selectSql(this, parameters);

	// 1.8.3
	//	executeQuery(connection, sql, parameters, resultSet -> {
	//		getRowConsumer(connection,  this                          , consumer ).accept(resultSet);
	//		getRowConsumer(connection, (JoinInfo<JE1>)joinInfos.get(0), consumer1).accept(resultSet);
	//	});
		executeQuery(connection, sql, parameters,
			getRowConsumer(connection, this, consumer)
			.andThen(getRowConsumer(connection, (JoinInfo<JE1>)joinInfos.get(0), consumer1))
		);
	////
	}

	/**
	 * Generates and executes a SELECT SQL that joins two tables.
	 *
	 * <p>
	 * Adds following strings to <b>columns</b> set
	 * if <b>columns</b> method is not called and
	 * <b>innerJoin</b>, <b>leftJoin</b> or <b>rightJoin</b> method called more than twice. <i>(since 1.8.4)</i>
	 * </p>
	 *
	 * <ul class="code" style="list-style-type:none">
	 *   <li>"&lt;Main Table Alias&gt;.*"</li>
	 *   <li>"&lt;1st Joined Table Alias&gt;.*"</li>
	 *   <li>"&lt;2nd Joined Table Alias&gt;.*"</li>
	 * </ul>
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 *     List&lt;Phone&gt; phones = new ArrayList&lt;&gt;();
	 *     List&lt;Email&gt; emails = new ArrayList&lt;&gt;();
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .innerJoin(Phone.class, "P", "{P.contactId} = {C.id}")
	 *         .innerJoin(Email.class, "E", "{E.contactId} = {C.id}")
	 *         .<b>&lt;Phone, Email&gt;select(connection, contacts::add, phones::add, emails::add)</b>;
	 * </pre></div>
	 *
	 * @param <JE1> the entity class related to the 1st joined table
	 * @param <JE2> the entity class related to the 2nd joined table
	 * @param connection the database connection
	 * @param consumer the consumer of the entities related to the main table generated from retrieved rows
	 * @param consumer1 the consumer of the entities related to the 1st joined table generated from retrieved rows
	 * @param consumer2 the consumer of the entities related to the 2nd joined table generated from retrieved rows
	 *
	 * @throws NullPointerException if <b>connection</b>, <b>consumer</b>, <b>consumer1</b> or <b>consumer2</b> is null
	 * @throws IllegalStateException if join information is less than 2
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 */
	public <JE1, JE2> void select(Connection connection,
		Consumer<? super  E > consumer,
		Consumer<? super JE1> consumer1,
		Consumer<? super JE2> consumer2) {
	//	if (joinInfos.size() < 2) throw new IllegalStateException("Sql.select: joinInfos.size < 2");
		if (joinInfos.size() < 2) throw new IllegalStateException("joinInfos.size < 2");

	// 1.8.2
		if (where.isEmpty())
			where = Condition.ALL;
	////
	// 1.8.4
		if (columns.size() == 0 && joinInfos.size() > 2) {
			columns.add(tableAlias + ".*");
			columns.add(joinInfos.get(0).tableAlias() + ".*");
			columns.add(joinInfos.get(1).tableAlias() + ".*");
		}
	////
		List<Object> parameters = new ArrayList<>();
		String sql = getDatabase().selectSql(this, parameters);

	// 1.8.3
	//	executeQuery(connection, sql, parameters, resultSet -> {
	//		getRowConsumer(connection,  this                          , consumer ).accept(resultSet);
	//		getRowConsumer(connection, (JoinInfo<JE1>)joinInfos.get(0), consumer1).accept(resultSet);
	//		getRowConsumer(connection, (JoinInfo<JE2>)joinInfos.get(1), consumer2).accept(resultSet);
	//	});
		executeQuery(connection, sql, parameters,
			getRowConsumer(connection, this, consumer)
			.andThen(getRowConsumer(connection, (JoinInfo<JE1>)joinInfos.get(0), consumer1))
			.andThen(getRowConsumer(connection, (JoinInfo<JE2>)joinInfos.get(1), consumer2))
		);
	////
	}

	/**
	 * Generates and executes a SELECT SQL that joins three tables.
	 *
	 * <p>
	 * Adds following strings to <b>columns</b> set
	 * if <b>columns</b> method is not called and
	 * <b>innerJoin</b>, <b>leftJoin</b> or <b>rightJoin</b> method called more than 3 times. <i>(since 1.8.4)</i>
	 * </p>
	 *
	 * <ul class="code" style="list-style-type:none">
	 *   <li>"&lt;Main Table Alias&gt;.*"</li>
	 *   <li>"&lt;1st Joined Table Alias&gt;.*"</li>
	 *   <li>"&lt;2nd Joined Table Alias&gt;.*"</li>
	 *   <li>"&lt;3rd Joined Table Alias&gt;.*"</li>
	 * </ul>
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 *     List&lt;Phone&gt; phones = new ArrayList&lt;&gt;();
	 *     List&lt;Email&gt; emails = new ArrayList&lt;&gt;();
	 *     List&lt;Address&gt; addresses = new ArrayList&lt;&gt;();
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .innerJoin(Phone.class, "P", "{P.contactId} = {C.id}")
	 *         .innerJoin(Email.class, "E", "{E.contactId} = {C.id}")
	 *         .innerJoin(Address.class, "A", "{A.contactId} = {C.id}")
	 *         .<b>&lt;Phone, Email, Address&gt;select(connection, contacts::add, phones::add,</b>
	 *             <b>emails::add, addresses::add)</b>;
	 * </pre></div>
	 *
	 * @param <JE1> the entity class related to the 1st joined table
	 * @param <JE2> the entity class related to the 2nd joined table
	 * @param <JE3> the entity class related to the 3rd joined table
	 * @param connection the database connection
	 * @param consumer the consumer of the entities related to the main table generated from retrieved rows
	 * @param consumer1 the consumer of the entities related to the 1st joined table generated from retrieved rows
	 * @param consumer2 the consumer of the entities related to the 2nd joined table generated from retrieved rows
	 * @param consumer3 the consumer of the entities related to the 3rd joined table generated from retrieved rows
	 *
	 * @throws NullPointerException if <b>connection</b>, <b>consumer</b>, <b>consumer1</b>, <b>consumer2</b> or <b>consumer3</b> is null
	 * @throws IllegalStateException if join information is less than 3
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 */
	public <JE1, JE2, JE3> void select(Connection connection,
		Consumer<? super  E > consumer,
		Consumer<? super JE1> consumer1,
		Consumer<? super JE2> consumer2,
		Consumer<? super JE3> consumer3) {
	//	if (joinInfos.size() < 3) throw new IllegalStateException("Sql.select: joinInfos.size < 3");
		if (joinInfos.size() < 3) throw new IllegalStateException("joinInfos.size < 3");

	// 1.8.2
		if (where.isEmpty())
			where = Condition.ALL;
	////
	// 1.8.4
		if (columns.size() == 0 && joinInfos.size() > 3) {
			columns.add(tableAlias + ".*");
			columns.add(joinInfos.get(0).tableAlias() + ".*");
			columns.add(joinInfos.get(1).tableAlias() + ".*");
			columns.add(joinInfos.get(2).tableAlias() + ".*");
		}
	////
		List<Object> parameters = new ArrayList<>();
		String sql = getDatabase().selectSql(this, parameters);

	// 1.8.3
	//	executeQuery(connection, sql, parameters, resultSet -> {
	//		getRowConsumer(connection,  this                          , consumer ).accept(resultSet);
	//		getRowConsumer(connection, (JoinInfo<JE1>)joinInfos.get(0), consumer1).accept(resultSet);
	//		getRowConsumer(connection, (JoinInfo<JE2>)joinInfos.get(1), consumer2).accept(resultSet);
	//		getRowConsumer(connection, (JoinInfo<JE3>)joinInfos.get(2), consumer3).accept(resultSet);
	//	});
		executeQuery(connection, sql, parameters,
			getRowConsumer(connection, this, consumer)
			.andThen(getRowConsumer(connection, (JoinInfo<JE1>)joinInfos.get(0), consumer1))
			.andThen(getRowConsumer(connection, (JoinInfo<JE2>)joinInfos.get(1), consumer2))
			.andThen(getRowConsumer(connection, (JoinInfo<JE3>)joinInfos.get(2), consumer3))
		);
	////
	}

	/**
	 * Generates and executes a SELECT SQL that joins four tables.
	 *
	 * <p>
	 * Adds following strings to <b>columns</b> set
	 * if <b>columns</b> method is not called and
	 * <b>innerJoin</b>, <b>leftJoin</b> or <b>rightJoin</b> method called more than 4 times. <i>(since 1.8.4)</i>
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
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 *     List&lt;Phone&gt; phones = new ArrayList&lt;&gt;();
	 *     List&lt;Email&gt; emails = new ArrayList&lt;&gt;();
	 *     List&lt;Address&gt; addresses = new ArrayList&lt;&gt;();
	 *     List&lt;Url&gt; urls = new ArrayList&lt;&gt;();
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .innerJoin(Phone.class, "P", "{P.contactId} = {C.id}")
	 *         .innerJoin(Email.class, "E", "{E.contactId} = {C.id}")
	 *         .innerJoin(Address.class, "A", "{A.contactId} = {C.id}")
	 *         .innerJoin(Url.class, "U", "{U.contactId} = {C.id}")
	 *         .<b>&lt;Phone, Email, Address, Url&gt;select(connection, contacts::add, phones::add,</b>
	 *             <b>emails::add, addresses::add, urls::add)</b>;
	 * </pre></div>
	 *
	 * @param <JE1> the entity class related to the 1st joined table
	 * @param <JE2> the entity class related to the 2nd joined table
	 * @param <JE3> the entity class related to the 3rd joined table
	 * @param <JE4> the entity class related to the 4th joined table
	 * @param connection the database connection
	 * @param consumer the consumer of the entities related to the main table generated from retrieved rows
	 * @param consumer1 the consumer of the entities related to the 1st join table generated from retrieved rows
	 * @param consumer2 the consumer of the entities related to the 2nd join table generated from retrieved rows
	 * @param consumer3 the consumer of the entities related to the 3rd join table generated from retrieved rows
	 * @param consumer4 the consumer of the entities related to the 4th join table generated from retrieved rows
	 *
	 * @throws NullPointerException if <b>connection</b>, <b>consumer</b>, <b>consumer1</b>, <b>consumer2</b>, <b>consumer3</b> or <b>consumer4</b> is null
	 * @throws IllegalStateException if join information is less than 4
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 */
	public <JE1, JE2, JE3, JE4> void select(Connection connection,
		Consumer<? super  E > consumer,
		Consumer<? super JE1> consumer1,
		Consumer<? super JE2> consumer2,
		Consumer<? super JE3> consumer3,
		Consumer<? super JE4> consumer4) {
	//	if (joinInfos.size() < 4) throw new IllegalStateException("Sql.select: joinInfos.size < 4");
		if (joinInfos.size() < 4) throw new IllegalStateException("joinInfos.size < 4");

	// 1.8.2
		if (where.isEmpty())
			where = Condition.ALL;
	////
	// 1.8.4
		if (columns.size() == 0 && joinInfos.size() > 4) {
			columns.add(tableAlias + ".*");
			columns.add(joinInfos.get(0).tableAlias() + ".*");
			columns.add(joinInfos.get(1).tableAlias() + ".*");
			columns.add(joinInfos.get(2).tableAlias() + ".*");
			columns.add(joinInfos.get(3).tableAlias() + ".*");
		}
	////
		List<Object> parameters = new ArrayList<>();
		String sql = getDatabase().selectSql(this, parameters);

	// 1.8.3
	//	executeQuery(connection, sql, parameters, resultSet -> {
	//		getRowConsumer(connection,  this                          , consumer ).accept(resultSet);
	//		getRowConsumer(connection, (JoinInfo<JE1>)joinInfos.get(0), consumer1).accept(resultSet);
	//		getRowConsumer(connection, (JoinInfo<JE2>)joinInfos.get(1), consumer2).accept(resultSet);
	//		getRowConsumer(connection, (JoinInfo<JE3>)joinInfos.get(2), consumer3).accept(resultSet);
	//		getRowConsumer(connection, (JoinInfo<JE4>)joinInfos.get(3), consumer4).accept(resultSet);
	//	});
		executeQuery(connection, sql, parameters,
			getRowConsumer(connection, this, consumer)
			.andThen(getRowConsumer(connection, (JoinInfo<JE1>)joinInfos.get(0), consumer1))
			.andThen(getRowConsumer(connection, (JoinInfo<JE2>)joinInfos.get(1), consumer2))
			.andThen(getRowConsumer(connection, (JoinInfo<JE3>)joinInfos.get(2), consumer3))
			.andThen(getRowConsumer(connection, (JoinInfo<JE4>)joinInfos.get(3), consumer4))
		);
	////
	}

	/**
	 * Generates and executes a SELECT SQL
	 * and returns an <b>Optional</b> of the entity if searched, <b>Optional.empty()</b> otherwise.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     Contact contact = new Sql&lt;&gt;(Contact.class)
	 *         .where("{id} = {}", contactId)
	 *         .<b>select(connection)</b>.orElse(null);
	 * </pre></div>
	 *
	 * @param connection the database connection
	 * @return an <b>Optional</b> of the entity if searched, <b>Optional.empty()</b> otherwise
	 *
	 * @throws NullPointerException if <b>connection</b> is null
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 * @throws ManyRowsException if more than one row searched
	 */
	public Optional<E> select(Connection connection) {
		List<E> entities = new ArrayList<>();
		select(connection, entity -> {
			if (entities.size() > 0)
			// 1.5.0
			//	throw new ManyRowsException();
				throw new ManyRowsException(generatedSql);
			////
			entities.add(entity);
		});
		return entities.size() == 0 ? Optional.empty() : Optional.of(entities.get(0));
	}

	/**
	 * Generates and executes a SELECT COUNT(*) SQL and returns the result.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     int count = new Sql&lt;&gt;(Contact.class)
	 *         .<b>selectCount(connection)</b>;
	 * </pre></div>
	 *
	 * @param connection the database connection
	 * @return the row count
	 *
	 * @throws NullPointerException if <b>connection</b> is null
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 */
	public int selectCount(Connection connection) {
	// 1.8.2
		if (where.isEmpty())
			where = Condition.ALL;
	////
		List<Object> parameters = new ArrayList<>();
		String sql = getDatabase().subSelectSql(this, () -> "COUNT(*)", parameters);

		int[] count = new int[1];
		executeQuery(connection, sql, parameters, resultSet -> {
			try {
				count[0] = resultSet.getInt(1);
			}
		// 1.3.0
		//	catch (Exception e) {throw new RuntimeSQLException(e);}
			catch (SQLException e) {throw new RuntimeSQLException(e);}
		////
		});
		return count[0];
	}

	/**
	 * Generates and executes an INSERT SQL.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     Contact contact = new Contact();
	 *       ...
	 *     int count = new Sql&lt;&gt;(Contact.class)
	 *         .<b>insert(connection, contact)</b>;
	 * </pre></div>
	 *
	 * @param connection the database connection
	 * @param entity the entity to be inserted
	 * @return the row count
	 *
	 * @throws NullPointerException if <b>connection</b> or <b>entity</b> is null
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 */
	public int insert(Connection connection, E entity) {
		Objects.requireNonNull(entity, "entity");

	// 1.6.0
		if (entity instanceof PreStore)
			((PreStore)entity).preStore();
	////

		int count = 0;

		// before INSERT
		if (entity instanceof PreInsert)
			count += ((PreInsert)entity).preInsert(connection);

		this.entity = entity;
		List<Object> parameters = new ArrayList<>();
		String sql = getDatabase().insertSql(this, parameters);
		count += executeUpdate(connection, sql, parameters);

		// after INSERT
		if (entity instanceof Composite)
			count += ((Composite)entity).postInsert(connection);

		return count;
	}

	/**
	 * Generates and executes INSERT SQLs for each element of entities.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 *       ...
	 *     int count = new Sql&lt;&gt;(Contact.class)
	 *         .<b>insert(connection, contacts)</b>;
	 * </pre></div>
	 *
	 * @param connection the database connection
	 * @param entities an <b>Iterable</b> of entities
	 * @return the row count
	 *
	 * @throws NullPointerException if <b>connection</b>, <b>entities</b> or any element of <b>entities</b> is null
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 */
	public int insert(Connection connection, Iterable<? extends E> entities) {
		Objects.requireNonNull(entities, "entities");

		int[] count = new int[1];
		entities.forEach(entity -> count[0] += insert(connection, entity));
		return count[0];
	}

	/**
	 * Generates and executes an UPDATE SQL.
	 *
	 * <p>
	 * If the <b>WHERE</b> condition is specified, updates by the condition.<br>
	 * To update all rows of the target table, specify <b>Condition.ALL</b> to <b>WHERE</b> conditions.
	 * </p>
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     Contact contact = new Contact();
	 *       ...
	 *     int count = new Sql&lt;&gt;(Contact.class)
	 *         .<b>update(connection, contact)</b>;
	 * </pre></div>
	 *
	 * @param connection the database connection
	 * @param entity the entity to be updated
	 * @return the row count
	 *
	 * @throws NullPointerException if <b>connection</b> or <b>entity</b> is null
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 */
	public int update(Connection connection, E entity) {
		Objects.requireNonNull(entity, "entity");

	// 1.6.0
		if (entity instanceof PreStore)
			((PreStore)entity).preStore();
	////

		this.entity = entity;
		if (where.isEmpty())
			where = Condition.of(entity);

		List<Object> parameters = new ArrayList<>();
		String sql = getDatabase().updateSql(this, parameters);
		int count = executeUpdate(connection, sql, parameters);

		// after UPDATE
		if (where instanceof EntityCondition && entity instanceof Composite)
			count += ((Composite)entity).postUpdate(connection);

		return count;
	}

	/**
	 * Generates and executes UPDATE SQLs for each element of <b>entities</b>.
	 *
	 * <p>
	 * Even if the <b>WHERE</b> condition is specified, <b>new EntityCondition(entity)</b> will be specified for each entity.
	 * </p>
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 *       ...
	 *     int count = new Sql&lt;&gt;(Contact.class)
	 *         .<b>update(connection, contacts)</b>;
	 * </pre></div>
	 *
	 * @param connection the database connection
	 * @param entities an <b>Iterable</b> of entities
	 * @return the row count
	 *
	 * @throws NullPointerException if <b>connection</b>, <b>entities</b> or any element of <b>entities</b> is null
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 */
	public int update(Connection connection, Iterable<? extends E> entities) {
		Objects.requireNonNull(entities, "entities");

		int[] count = new int[1];
		entities.forEach(entity -> {
			where = Condition.EMPTY;
			count[0] += update(connection, entity);
		});
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
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     int count = new Sql&lt;&gt;(Contact.class)
	 *         .where(Condition.ALL)
	 *         .<b>delete(connection)</b>;
	 * </pre></div>
	 *
	 * @param connection the database connection
	 * @return the row count
	 *
	 * @throws NullPointerException if <b>connection</b> is null
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 */
	public int delete(Connection connection) {
		if (where.isEmpty()) {
			logger.warn(messageNoWhereCondition);
			return 0;
		}

		List<Object> parameters = new ArrayList<>();
		String sql = getDatabase().deleteSql(this, parameters);
		int count = executeUpdate(connection, sql, parameters);
		return count;
	}

	/**
	 * Generates and executes a DELETE SQL.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     Contact contact = new Contact();
	 *       ...
	 *     int count = new Sql&lt;&gt;(Contact.class)
	 *         .<b>delete(connection, contact)</b>;
	 * </pre></div>
	 *
	 * @param connection the database connection
	 * @param entity the entity to be deleted
	 * @return the row count
	 *
	 * @throws NullPointerException if <b>connection</b> or <b>entity</b> is null
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 */
	public int delete(Connection connection, E entity) {
		Objects.requireNonNull(entity, "entity");

		where = Condition.of(entity);

		List<Object> parameters = new ArrayList<>();
		String sql = getDatabase().deleteSql(this, parameters);
		int count = executeUpdate(connection, sql, parameters);

		// after DELETE
		if (entity instanceof Composite)
			count += ((Composite)entity).postDelete(connection);

		return count;
	}

	/**
	 * Generates and executes DELETE SQLs for each element of entities.
	 *
	 * <div class="sampleTitle"><span>Example</span></div>
	 * <div class="sampleCode"><pre>
	 *     List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 *       ...
	 *     int count = new Sql&lt;&gt;(Contact.class)
	 *         .<b>delete(connection, contacts)</b>;
	 * </pre></div>
	 *
	 * @param connection the database connection
	 * @param entities an <b>Iterable</b> of entities
	 * @return the row count
	 *
	 * @throws NullPointerException if <b>connection</b>, <b>entities</b> or any element of <b>entities</b> is null
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 */
	public int delete(Connection connection, Iterable<? extends E> entities) {
		Objects.requireNonNull(entities, "entities");

		int[] count = new int[1];
		entities.forEach(entity -> count[0] += delete(connection, entity));
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
	 * @param connection the database connection
	 * @param sqlEntityInfo the <b>SqlEntityInfo</b> object
	 * @param consumer the consumer
	 *
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 * @throws RuntimeException InstantiationException, IllegalAccessException
	 */
	private <T> Consumer<ResultSet> getRowConsumer(Connection connection, SqlEntityInfo<T> sqlEntityInfo, Consumer<? super T> consumer) {
		return resultSet -> {
			EntityInfo<T> entityInfo = sqlEntityInfo.entityInfo();
			Accessor<T> accessor = entityInfo.accessor();
			String tableAlias = sqlEntityInfo.tableAlias();
			try {
				// Create an entity object
			// 1.3.0 for Java 9
			//	T entity = entityInfo.entityClass().newInstance();
				T entity = entityInfo.entityClass().getConstructor().newInstance();
			////

				//  Column loop
				sqlEntityInfo.selectedSqlColumnInfoStream(columns)
					.filter(sqlColumnInfo -> sqlColumnInfo.columnInfo().selectable())
					.forEach(sqlColumnInfo -> {

						ColumnInfo columnInfo = sqlColumnInfo.columnInfo();
						String columnAlias = columnInfo.getColumnAlias(tableAlias);

						try {
							// Gets a value and convert type to store
							Object value = resultSet.getObject(columnAlias);
							Class<?> type = accessor.getType(columnInfo.propertyName());
							Object convertedValue = getDatabase().convert(value, Utils.toClassType(type));
							entityInfo.accessor().setValue(entity, columnInfo.propertyName(), convertedValue);
						}
					// 1.3.0
					//	catch (Exception e) {throw new RuntimeSQLException(e);}
						catch (SQLException e) {throw new RuntimeSQLException(e);}
					////
					});

				// After get
			// 1.6.0
				if (entity instanceof PostLoad)
					((PostLoad)entity).postLoad();
			////
				if (entity instanceof Composite)
					((Composite)entity).postSelect(connection);

				// Consumes the entity
				consumer.accept(entity);
			}
		// 1.3.0 for Java 9
		//	catch (InstantiationException | IllegalAccessException e) {
		//		throw new RuntimeException(e);
		//	}
			catch (RuntimeException e) {throw e;}
			catch (Exception e) {throw new RuntimeException(e);}
		};
	}


	/**
	 * Executes the SELECT SQL.
	 *
	 * @param connection the database connection
	 * @param sql the SQL
	 * @param parameters the parameters of SQL
	 * @param consumer the consumer for the <b>ResultSet</b> object
	 *
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 */
	private void executeQuery(Connection connection, String sql, List<Object> parameters, Consumer<ResultSet> consumer) {
		Objects.requireNonNull(connection, "connection");
		Objects.requireNonNull(sql, "sql");
		Objects.requireNonNull(parameters, "parameters");
		Objects.requireNonNull(consumer, "consumer");

	// 1.5.0
		generatedSql = sql;
	////
		logger.info(() -> getDatabase().getClass().getSimpleName() + ": " + sql);

		// Prepares SQL
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			//  Sets the parameter values
			for (int index = 0; index < parameters.size(); ++index) {
				Object parameter = parameters.get(index);
			// 1.7.0
			//	if  (logger.isInfoEnabled())
			//		logger.info("  parameters[" + index + "]: " + Utils.toLogString(parameter));
				if  (logger.isDebugEnabled())
					logger.debug("  parameters[" + index + "]: " + Utils.toLogString(parameter));
			////

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
			if (rowOffset > 0 && !getDatabase().supportsOffsetLimit()) {
				//  Offset value was specified and cannot create SQL using 'OFFSET'
				if (resultSetType == ResultSet.TYPE_FORWARD_ONLY) {
					//  Skip rows for offset value
					for (int index = 0; index < rowOffset; ++index) {
						if (!resultSet.next())
							break;
					}
					logger.debug(() -> "resultSet.next() * " + rowOffset);
				} else {
					// Specifies absolute row offset
					boolean absoluteResult = resultSet.absolute(rowOffset);
					logger.debug(() -> "resultSet.absolute(" + rowOffset + ")=" + absoluteResult);
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
				double averageGetTime = rowCount == 0 ? getTime : getTime / rowCount;
				logger.info(MessageFormat.format(messageRowsSelect, rowCount,
					timeFormat.format(execTime), timeFormat.format(getTime), timeFormat.format(averageGetTime)
				));
				logger.info(""); // Line feed of log
			}
		}
		catch (SQLException e) {throw new RuntimeSQLException(e);}
	}

	/**
	 * Executes the SQL which is INSERT, UPDATE or DELETE SQL.
	 *
	 * @param connection the database connection
	 * @param sql the SQL
	 * @param parameters the parameters of SQL
	 *
	 * @throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	 */
	private int executeUpdate(Connection connection, String sql, List<Object> parameters) {
		Objects.requireNonNull(connection, "connection");
		Objects.requireNonNull(sql, "sql");
		Objects.requireNonNull(parameters, "parameters");

	// 1.5.0
		generatedSql = sql;
	////
		logger.info(() -> getDatabase().getClass().getSimpleName() + ": " + sql);

		// Prepares SQL
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			//  Sets the parameter values
			for (int index = 0; index < parameters.size(); ++index) {
				Object parameter = parameters.get(index);
			// 1.7.0
			//	if  (logger.isInfoEnabled())
			//		logger.info("  parameters[" + index + "]: " + Utils.toLogString(parameter));
				if  (logger.isDebugEnabled())
					logger.debug("  parameters[" + index + "]: " + Utils.toLogString(parameter));
			////

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
				logger.info(MessageFormat.format(messageRows, rowCount, timeFormat.format(execTime)));
				logger.info(""); // Line feed of log
			}

			return rowCount;
		}
		catch (SQLException e) {throw new RuntimeSQLException(e);}
	}

	/**
	 * Returns generated SQL.
	 *
	 * @return generated SQL
	 *
	 * @since 1.8.4
	 */
	public String generatedSql() {
		return generatedSql;
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
	// 1.8.2
	//	return entityInfo().columnInfos().stream();
		return entityInfo.columnInfos().stream();
	////
	}

// 1.8.2
//	/**
//	 * Returns a <b>ColumnInfo</b> stream with selected columns of the main table.<br>
//	 * <i> this method is used internally.</i>
//	 *
//	 * @return a <b>ColumnInfo</b> stream
//	 */
//	public Stream<ColumnInfo> selectedColumnInfoStream() {
//		return columns.isEmpty()
//			? columnInfoStream()
//			: columnInfoStream()
//				.filter(columnInfo ->
//					columns.stream().anyMatch(name ->
//						name.endsWith("*")
//							? columnInfo.propertyName().startsWith(name.substring(0, name.length() - 1))
//							: columnInfo.propertyName().equals(name)
//					)
//				);
//	}
////

// 1.8.2
//	/**
//	 * Returns a <b>SqlEntityInfo</b> stream of the main table and the joined tables.<br>
//	 * <i> this method is used internally.</i>
//	 *
//	 * @return a <b>SqlEntityInfo</b> stream
//	 */
//	public Stream<SqlEntityInfo<?>> sqlEntityInfoStream() {
//		return  Stream.concat(Stream.of(this), joinInfos.stream());
//	}
////

// 1.8.2
//	/**
//	 * Returns a <b>SqlColumnInfo</b> stream of the main table and the joined tables.<br>
//	 * <i> this method is used internally.</i>
//	 *
//	 * @return a <b>SqlColumnInfo</b> stream
//	 */
//	public Stream<SqlColumnInfo> joinSqlColumnInfoStream() {
//		return sqlEntityInfoStream()
//			.flatMap(sqlEntityInfo ->
//				sqlEntityInfo.entityInfo().columnInfos().stream()
//					.map(columnInfo -> new SqlColumnInfo(sqlEntityInfo.tableAlias(), columnInfo))
//			);
//	}
////

// 1.8.2
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
////

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
	// 1.8.2
	//	return columns.isEmpty()
	//		? joinSqlColumnInfoStream()
	//		: joinSqlColumnInfoStream()
	//			.filter(sqlColumnInfo -> columns.stream().anyMatch(name -> sqlColumnInfo.matches(name)));
		return Stream.concat(Stream.of(this), joinInfos.stream())
			.flatMap(sqlEntityInfo -> sqlEntityInfo.selectedSqlColumnInfoStream(columns));
	////
	}
}
