/*
	Sql.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep;

import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import org.lightsleep.entity.PreInsert;
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
	The class to build and execute SQLs.

	<div class="sampleTitle"><span>Example of use</span></div>
<div class="sampleCode"><pre>
List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
Transaction.execute(connection -&gt; {
    new <b>Sql</b>&lt;&gt;(Contact.class)
        .<b>where</b>("{familyName} = {}", "Smith")
        .<b>select</b>(connection, contacts::add);
});
</pre></div>

	<div class="sampleTitle"><span>SQL</span></div>
<div class="sampleCode"><pre>
SELECT contactId, familyName, givenName, ... FROM Contact WHERE familyName='Smith'
</pre></div>

	@param <E> the entity class corresponding to the main table

	@since 1.0.0
	@author Masato Kokubo
*/
@SuppressWarnings("unchecked")
public class Sql<E> implements SqlEntityInfo<E> {
	// The logger
	private static final Logger logger = LoggerFactory.getLogger(Sql.class);

	// The version resource
	private static final String version = new Resource("lightsleep-version").get("version");

	// Cass resources
	private static final Resource resource = new Resource(Sql.class);
	private static final String messageDatabaseHandler            = resource.get("messageDatabaseHandler");
	private static final String messageDatabaseHandlerNotFound    = resource.get("messageDatabaseHandlerNotFound");
	private static final String messageConnectionSupplier         = resource.get("messageConnectionSupplier");
	private static final String messageConnectionSupplierNotFound = resource.get("messageConnectionSupplierNotFound");
	private static final String messageNoWhereCondition           = resource.get("messageNoWhereCondition");
	private static final String messageRows                       = resource.get("messageRows");
	private static final String messageRowsSelect                 = resource.get("messageRowsSelect");

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

	// Whether with NO WAIT
	private boolean noWait = false;

	// The database handler
	private static Database database;

	// The connection supplier
	private static ConnectionSupplier connectionSupplier;

	// Recently generated SQL @since 1.5.0
	private String sql;

	static {
		logger.info("Lightsleep " + version + " / logger: " + LoggerFactory.loggerClass.getName());
	}

	/**
		Returns the database handler.

		@return database the database handler
	*/
	public static Database getDatabase() {
		return database;
	}

	/**
		Sets the database handler.

		@param database the database handler

		@throws NullPointerException if <b>database</b> is <b>null</b>
	*/
	public static void setDatabase(Database database) {
		if (database == null) throw new NullPointerException("Sql.setDatabase: database == null");

		Sql.database = database;
		logger.info(() -> MessageFormat.format(messageDatabaseHandler, Sql.database.getClass().getName()));
	}

	//  Initialize the database handler
	static {
	// 1.2.0
	//	String databaseName = Resource.globalResource.get("Database", "Standard");
		String databaseName = Resource.globalResource.get(Database.class.getSimpleName(), Standard.class.getSimpleName());
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
			logger.error("", e);
		}
	}

	/**
		Returns the connection supplier.

		@return the connection supplier
	*/
	public static ConnectionSupplier getConnectionSupplier() {
		return connectionSupplier;
	}

	/**
		Sets the connection supplier.

		@param supplier the connection supplier

		@throws NullPointerException if <b>supplier</b> is <b>null</b>
	*/
	public static void setConnectionSupplier(ConnectionSupplier supplier) {
		if (supplier == null) throw new NullPointerException("Sql.setConnectionSupplier: supplier == null");

		connectionSupplier = supplier;
		logger.debug(() -> MessageFormat.format(messageConnectionSupplier, connectionSupplier.getClass().getName()));
	}

	// Initialize the connection supplier
	static {
	// 1.2.0
	//	String supplierName = Resource.globalResource.get("ConnectionSupplier", "Jdbc");
		String supplierName = Resource.globalResource.get(ConnectionSupplier.class.getSimpleName(), Jdbc.class.getSimpleName());
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
			logger.error("", e);
		}
	}

	/**
		Returns the entity information related to the specified entity class.

		@param <E> the entity class corresponding to the main table

		@param entityClass the entity class

		@return the entity information

		@throws NullPointerException if <b>entityClass</b> is <b>null</b>
	*/
	public static <E> EntityInfo<E> getEntityInfo(Class<E> entityClass) {
		if (entityClass == null) throw new NullPointerException("Sql.getEntityInfo: entityClass == null");

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
		Constructs a new <b>Sql</b>.

		@param entityClass the entity class
	*/
	public Sql(Class<E> entityClass) {
		this(entityClass, "");
	}

	/**
		Constructs a new <b>Sql</b>.

		@param entityClass the entity class
		@param tableAlias the table alias

		@throws NullPointerException if <b>entityClass</b> or <b>tableAlias</b> is <b>null</b>
	*/
	public Sql(Class<E> entityClass, String tableAlias) {
		if (entityClass == null) throw new NullPointerException("Sql.<init>: entityClass == null");
		if (tableAlias == null) throw new NullPointerException("Sql.<init>: alias == null");

		entityInfo = getEntityInfo(entityClass);
		this.tableAlias = tableAlias;
		addSqlEntityInfo(this);
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public EntityInfo<E> entityInfo() {
		return entityInfo;
	}

	/**
		Returns the entity class.

		@return the entity class
	*/
	public Class<E> entityClass() {
		return entityInfo.entityClass();
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public String tableAlias() {
		return tableAlias;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public E entity() {
		return entity;
	}

	/**
		Sets the entity that is referenced in expressions.

		@param entity the entity

		@return this object
	*/
	public Sql<E> setEntity(E entity) {
		this.entity = entity;
		return this;
	}

	/**
		Specifies that appends <b>DISTINCT</b> to SELECT SQL.

		@return this object
	*/
	public Sql<E> distinct() {
		distinct = true;
		return this;
	}

	/**
		Returns whether to append <b>DISTINCT</b> to SELECT SQL.

		@return <b>true</b> if appends <b>DISTINCT</b>, <b>false</b> otherwise
	*/
	public boolean isDistinct() {
		return distinct;
	}

	/**
		Specifies the columns for SELECT SQL.
		Specify the property names that related to the columns.
		You can also be specified <b>"*"</b> or <b>"<i>&lt;table alias&gt;</i>.*"</b>.
		If this method is not called it will be in the same manner as <b>"*"</b> is specified.

		@param columns the array of the property names related to the columns

		@return this object

		@throws NullPointerException if <b>columns</b> or any of <b>columns</b> is <b>null</b>
	*/
	public Sql<E> columns(String... columns) {
		if (columns == null) throw new NullPointerException("Sql.columns: columns == null");

		for (String column : columns)
			this.columns.add(column);
		return this;
	}

	/**
		Returns the columns for SELECT SQL.

		@return a set of the specified property names in <b>columns</b> method
	*/
	public Set<String> getColumns() {
		return columns;
	}

	/**
		Associates <b>expression</b> to the column related to <b>propertyName</b>.<br>
		If <b>expression</b> is empty, releases the previous association of <b>propertyName</b>.

		@param propertyName the property name
		@param expression the expression

		@return this object

		@throws NullPointerException if <b>propertyName</b> or <b>expression</b> is <b>null</b>
	*/
	public Sql<E> expression(String propertyName, Expression expression) {
		if (propertyName == null) throw new NullPointerException("Sql.expression: propertyName == null");
		if (expression == null) throw new NullPointerException("Sql.expression: expression == null");

		if (expression.content().isEmpty())
			expressionMap.remove(propertyName);
		else
			expressionMap.put(propertyName, expression);

		return this;
	}

	/**
		Associates the expression to the column related to <b>propertyName</b>.<br>
		If the expression is empty, releases the previous association of <b>propertyName</b>.

		@param propertyName the property name
		@param content the content of the expression
		@param arguments the arguments of the expression

		@return this object

		@throws NullPointerException if <b>propertyName</b>, <b>content</b> or <b>arguments</b> is <b>null</b>
	*/
	public Sql<E> expression(String propertyName, String content, Object... arguments) {
		return expression(propertyName, new Expression(content, arguments));
	}

	/**
		Returns the expression associated <b>propertyName</b> or <b>Expression.EMPTY</b> if not associated.

		@param propertyName the property name

		@return the expression associated <b>propertyName</b> or <b>Expression.EMPTY</b>

		@throws NullPointerException if <b>propertyName</b> is <b>null</b>
	*/
	public Expression getExpression(String propertyName) {
		if (propertyName == null) throw new NullPointerException("Sql.expression: propertyName == null");

		return expressionMap.getOrDefault(propertyName, Expression.EMPTY);
	}

	/**
		Add an information of the table that join with <b>INNER JOIN</b>.

		@param <JE> the entity class that relateed to the table to join

		@param entityClass the entity class that relateed to the table to join
		@param tableAlias the alias of the table to join
		@param on the join condition

		@return this object

		@throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
	*/
	public <JE> Sql<E> innerJoin(Class<JE> entityClass, String tableAlias, Condition on) {
		return join(JoinInfo.JoinType.INNER, entityClass, tableAlias, on);
	}

	/**
		Add an information of the table that join with <b>INNER JOIN</b>.

		@param <JE> the entity class that relateed to the table to join

		@param entityClass the entity class that relateed to the table to join
		@param tableAlias the alias of the table to join
		@param on the join condition
		@param arguments the arguments of the join condition

		@return this object

		@throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b>, <b>on</b> or <b>arguments</b> is <b>null</b>
	*/
	public <JE> Sql<E> innerJoin(Class<JE> entityClass, String tableAlias, String on, Object... arguments) {
		return join(JoinInfo.JoinType.INNER, entityClass, tableAlias, Condition.of(on, arguments));
	}

	/**
		Add an information of the table that join with <b>LEFT OUTER JOIN</b>.

		@param <JE> the entity class that relateed to the table to join

		@param entityClass the entity class that relateed to the table to join
		@param tableAlias the alias of the table to join
		@param on the join condition

		@return this object

		@throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
	*/
	public <JE> Sql<E> leftJoin(Class<JE> entityClass, String tableAlias, Condition on) {
		return join(JoinInfo.JoinType.LEFT, entityClass, tableAlias, on);
	}

	/**
		Add an information of the table that join with <b>LEFT OUTER JOIN</b>.

		@param <JE> the entity class that relateed to the table to join

		@param entityClass the entity class that relateed to the table to join
		@param tableAlias the alias of the table to join
		@param on the join condition
		@param arguments the arguments of the join condition

		@return this object

		@throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b>, <b>on</b> or <b>arguments</b> is <b>null</b>
	*/
	public <JE> Sql<E> leftJoin(Class<JE> entityClass, String tableAlias, String on, Object... arguments) {
		return join(JoinInfo.JoinType.LEFT, entityClass, tableAlias, Condition.of(on, arguments));
	}

	/**
		Add an information of the table that join with <b>RIGHT OUTER JOIN</b>.

		@param <JE> the entity class that relateed to the table to join

		@param entityClass the entity class that relateed to the table to join
		@param tableAlias the alias of the table to join
		@param on the join condition

		@return this object

		@throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
	*/
	public <JE> Sql<E> rightJoin(Class<JE> entityClass, String tableAlias, Condition on) {
		return join(JoinInfo.JoinType.RIGHT, entityClass, tableAlias, on);
	}

	/**
		Add an information of the table that join with <b>RIGHT OUTER JOIN</b>.

		@param <JE> the entity class that relateed to the table to join

		@param entityClass the entity class that relateed to the table to join
		@param tableAlias the alias of the table to join
		@param on the join condition
		@param arguments the arguments of the join condition

		@return this object

		@throws NullPointerException if <b>entityClass</b>, <b>tableAlias</b>, <b>on</b> or <b>arguments</b> is <b>null</b>
	*/
	public <JE> Sql<E> rightJoin(Class<JE> entityClass, String tableAlias, String on, Object... arguments) {
		return join(JoinInfo.JoinType.RIGHT, entityClass, tableAlias, Condition.of(on, arguments));
	}

	/**
		Add an information of the table that join with
		<b>INNER JOIN</b>, <b>LEFT OUTER JOIN</b> or <b>RIGHT OUTER JOIN</b>.

		@param <JE> the entity class that relateed to the table to join

		@param joinType the join type
		@param entityClass the entity class that relateed to the table to join
		@param tableAlias the alias of the table to join
		@param on the join condition

		@return this object

		@throws NullPointerException if <b>joinType</b>, <b>entityClass</b>, <b>tableAlias</b> or <b>on</b> is <b>null</b>
	*/
	private <JE> Sql<E> join(JoinInfo.JoinType joinType, Class<JE> entityClass, String tableAlias, Condition on) {
		EntityInfo<JE> entityInfo = Sql.getEntityInfo(entityClass);
		JoinInfo<JE> joinInfo = new JoinInfo<>(joinType, entityInfo, tableAlias, on);
		addSqlEntityInfo(joinInfo);
		joinInfos.add(joinInfo);
		return this;
	}

	/**
		Returns a list of join information that was added.

		@return a list of join information
	*/
	public List<JoinInfo<?>> getJoinInfos() {
		return joinInfos;
	}

	/**
		Specifies the <b>WHERE</b> condition.

		@param condition the condition

		@return this object

		@throws NullPointerException if <b>condition</b> is <b>null</b>
	*/
	public Sql<E> where(Condition condition) {
		if (condition == null) throw new NullPointerException("Sql.where: condition == null");

		where = condition;
		return this;
	}

	/**
		Specifies the <b>WHERE</b> condition by <b>Expression</b>.

		@param content the content of the <b>Expression</b>
		@param arguments the arguments of the <b>Expression</b>

		@return this object

		@throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
	*/
	public Sql<E> where(String content, Object... arguments) {
		where = Condition.of(content, arguments);
		return this;
	}

	/**
		Specifies the <b>WHERE</b> condition by EntityCondition.

		@param entity the entity of the EntityCondition

		@return this object

		@throws NullPointerException if <b>entity</b> is <b>null</b>
	*/
	public Sql<E> where(E entity) {
		where = Condition.of(entity);
		return this;
	}

	/**
		Specifies the <b>WHERE</b> condition by SubqueryCondition.

		@param <SE> the entity class of the subquery

		@param content the content of the SubqueryCondition
		@param subSql the Sql object of the SubqueryCondition

		@return this object

		@throws NullPointerException if <b>content</b> or <b>subSql</b> is <b>null</b>
	*/
	public <SE> Sql<E> where(String content, Sql<SE> subSql) {
		where = Condition.of(content, this, subSql);
		return this;
	}

	/**
		Returns the <b>WHERE</b> condition that was specified.

		@return the <b>WHERE</b> condition
	*/
	public Condition getWhere() {
		return where;
	}

	/**
		If after you call <b>having</b> method, 
		add the condition to the <b>HAVING</b> condition useing <b>AND</b>.
		Otherwise, add the condition to the <b>WHERE</b> condition.

		@param condition the condition

		@return this object

		@throws NullPointerException if <b>condition</b> is <b>null</b>
	*/
	public Sql<E> and(Condition condition) {
		if (condition == null) throw new NullPointerException("Sql.and: condition == null");

		if (having.isEmpty())
			where = where.and(condition);
		else
			having = having.and(condition);
		return this;
	}

	/**
		If after you call <b>having</b> method, 
		add the <b>Expression</b> condition to the <b>HAVING</b> condition useing <b>AND</b>.
		Otherwise, add the condition to the <b>WHERE</b> condition.

		@param content the content of the <b>Expression</b>
		@param arguments the arguments of the <b>Expression</b>

		@return this object

		@throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
	*/
	public Sql<E> and(String content, Object... arguments) {
		return and(Condition.of(content, arguments));
	}

	/**
		If after you call <b>having</b> method, 
		add a SubqueryCondition condition to the <b>HAVING</b> condition useing <b>AND</b>.
		Otherwise, add the condition to the <b>WHERE</b> condition.

		@param <SE> the entity class of the subquery

		@param content the content of the SubqueryCondition
		@param subSql the Sql object of the SubqueryCondition

		@return this object

		@throws NullPointerException if <b>content</b> or <b>subSql</b> is <b>null</b>
	*/
	public <SE> Sql<E> and(String content, Sql<SE> subSql) {
		return and(Condition.of(content, this, subSql));
	}

	/**
		If after you call <b>having</b> method, 
		add the condition to the <b>HAVING</b> condition useing <b>OR</b>.
		Otherwise, add the condition to the <b>WHERE</b> condition.

		@param condition the condition

		@return this object

		@throws NullPointerException if <b>condition</b> is <b>null</b>
	*/
	public Sql<E> or(Condition condition) {
		if (condition == null) throw new NullPointerException("Sql.and: condition == null");

		if (having.isEmpty())
			where = where.or(condition);
		else
			having = having.or(condition);
		return this;
	}

	/**
		If after you call <b>having</b> method, 
		add the <b>Expression</b> condition to the <b>HAVING</b> condition useing <b>OR</b>.
		Otherwise, add the condition to the <b>WHERE</b> condition.

		@param content the content of the <b>Expression</b>
		@param arguments the arguments of the <b>Expression</b>

		@return this object

		@throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
	*/
	public Sql<E> or(String content, Object... arguments) {
		return or(Condition.of(content, arguments));
	}

	/**
		If after you call <b>having</b> method, 
		add a SubqueryCondition condition to the <b>HAVING</b> condition useing <b>OR</b>.
		Otherwise, add the condition to the <b>WHERE</b> condition.

		@param <SE> the entity class of the subquery

		@param content the content of the SubqueryCondition
		@param subSql the Sql object of the SubqueryCondition

		@return this object

		@throws NullPointerException if <b>content</b> or <b>subSql</b> is <b>null</b>
	*/
	public <SE> Sql<E> or(String content, Sql<SE> subSql) {
		return or(Condition.of(content, this, subSql));
	}

	/**
		Specifies the <b>GROUP BY</b> expression.

		@param content the content of the <b>Expression</b>
		@param arguments the arguments of the <b>Expression</b>

		@return this object

		@throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
	*/
	public Sql<E> groupBy(String content, Object... arguments) {
		groupBy = groupBy.add(new Expression(content, arguments));
		return this;
	}

	/**
		Returns the <b>GROUP BY</b> information that was specified.

		@return the <b>GROUP BY</b> information
	*/
	public GroupBy getGroupBy() {
		return groupBy;
	}

	/**
		Specifies the <b>HAVING</b> condition.

		@param condition the condition

		@return this object

		@throws NullPointerException if <b>condition</b> is <b>null</b>
	*/
	public Sql<E> having(Condition condition) {
		if (condition == null) throw new NullPointerException("Sql.having: condition == null");

		having = condition;
		return this;
	}

	/**
		Specifies the <b>HAVING</b> condition by <b>Expression</b>.

		@param content the content of the <b>Expression</b>
		@param arguments the arguments of the <b>Expression</b>

		@return this object

		@throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
	*/
	public Sql<E> having(String content, Object... arguments) {
		having = Condition.of(content, arguments);
		return this;
	}

	/**
		Specifies the <b>HAVING</b> condition by SubqueryCondition.

		@param <SE> the entity class of the subquery

		@param content the content of the SubqueryCondition
		@param subSql the Sql object of the SubqueryCondition

		@return this object

		@throws NullPointerException if <b>content</b> or <b>subSql</b> is <b>null</b>
	*/
	public <SE> Sql<E> having(String content, Sql<SE> subSql) {
		having = Condition.of(content, this, subSql);
		return this;
	}

	/**
		Returns the <b>HAVING</b> condition that was specified.

		@return the <b>HAVING</b> condition
	*/
	public Condition getHaving() {
		return having;
	}

	/**
		Specifies the <b>ORDER BY</b> expression.

		@param content the content of the <b>Expression</b>
		@param arguments the arguments of the <b>Expression</b>

		@return this object

		@throws NullPointerException if <b>content</b> or <b>arguments</b> is <b>null</b>
	*/
	public Sql<E> orderBy(String content, Object... arguments) {
		orderBy = orderBy.add(new OrderBy.Element(content, arguments));
		return this;
	}

	/**
		Specifies the sort order to ascend.

		@return this object
	*/
	public Sql<E> asc() {
		orderBy.asc();
		return this;
	}

	/**
		Specifies the sort order to descend.

		@return this object
	*/
	public Sql<E> desc() {
		orderBy.desc();
		return this;
	}

	/**
		Returns the <b>ORDER BY</b> information that was specified.

		@return the <b>ORDER BY</b> information
	*/
	public OrderBy getOrderBy() {
		return orderBy;
	}

	/**
		Specifies the <b>LIMIT</b> value.

		@param limit the <b>LIMIT</b> value

		@return this object
	*/
	public Sql<E> limit(int limit) {
		this.limit = limit;
		return this;
	}

	/**
		Returns the <b>LIMIT</b> value that was specified.

		@return the <b>LIMIT</b> value
	*/
	public int getLimit() {
		return limit;
	}

	/**
		Specifies the <b>OFFSET</b> value.

		@param offset the <b>OFFSET</b> value

		@return this object
	*/
	public Sql<E> offset(int offset) {
		this.offset = offset;
		return this;
	}

	/**
		Returns the <b>OFFSET</b> value that was specified.

		@return the <b>OFFSET</b> value
	*/
	public int getOffset() {
		return offset;
	}

	/**
		Specifies that appends <b>FOR UPDATE</b> to SELECT SQL.

		@return this object
	*/
	public Sql<E> forUpdate() {
		forUpdate = true;
		return this;
	}

	/**
		Returns whether to append <b>FOR UPDATE</b> to SELECT SQL.

		@return <b>true</b> if appends <b>FOR UPDATE</b>, <b>false</b> otherwise
	*/
	public boolean isForUpdate() {
		return forUpdate;
	}

	/**
		Specifies that appends <b>NO WAIT</b> to SELECT SQL.

		@return this object
	*/
	public Sql<E> noWait() {
		noWait = true;
		return this;
	}

	/**
		Returns whether to append <b>NO WAIT</b> to SELECT SQL.

		@return <b>true</b> if appends <b>NO WAIT</b>, <b>false</b> otherwise
	*/
	public boolean isNoWait() {
		return noWait;
	}

	/**
		Executes <b>action</b> if <b>condition</b> is true.

		@param condition the condition
		@param action the action that is executed if <b>condition</b> is true

		@return this object
	*/
	public Sql<E> doIf(boolean condition, Consumer<Sql<E>> action) {
		if (action == null) throw new NullPointerException("Sql.doIf: action == null");

		if (condition)
			action.accept(this);

		return this;
	}

	/**
		Executes <b>action</b> if <b>condition</b> is true, <b>elseAction</b> otherwise.

		@param condition the condition
		@param action the action that is executed if <b>condition</b> is true
		@param elseAction the action that is executed if <b>condition</b> is false

		@return this object
	*/
	public Sql<E> doIf(boolean condition, Consumer<Sql<E>> action, Consumer<Sql<E>> elseAction) {
		if (action == null) throw new NullPointerException("Sql.doIf: action == null");
		if (elseAction == null) throw new NullPointerException("Sql.doIf: elseAction == null");

		if (condition)
			action.accept(this);
		else
			elseAction.accept(this);

		return this;
	}

	/**
		Returns the <b>SqlEntityInfo</b> object corresponding to the specified table alias.

		@param tableAlias a table alias

		@return the SqlEntityInfo objcet

		@throws NullPointerException if <b>tableAlias</b> is <b>null</b>
	*/
	public SqlEntityInfo<?> getSqlEntityInfo(String tableAlias) {
		return sqlEntityInfoMap.get(tableAlias);
	}

	/**
		Adds the <b>SqlEntityInfo</b> object.

		@param sqlEntityInfo the SqlEntityInfo object
	*/
	public void addSqlEntityInfo(SqlEntityInfo<?> sqlEntityInfo) {
		String tableAlias = sqlEntityInfo.tableAlias();
		if (!sqlEntityInfoMap.containsKey(tableAlias))
			sqlEntityInfoMap.put(tableAlias, sqlEntityInfo);

		if (sqlEntityInfo instanceof Sql) {
			((Sql<?>)sqlEntityInfo).sqlEntityInfoMap.values().stream()
				.filter(sqlEntityInfo2 -> sqlEntityInfo2 != sqlEntityInfo)
				.forEach(sqlEntityInfo2 -> addSqlEntityInfo(sqlEntityInfo2));
		}
	}

	/**
		Generates and executes a SELECT SQL that joins no tables.

		@param connection the database connection
		@param consumer a consumer for the entities

		@throws NullPointerException if <b>connection</b> or consumer is <b>null</b>
		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	*/
	public void select(Connection connection, Consumer<? super E> consumer) {
		List<Object> parameters = new ArrayList<>();
		String sql = getDatabase().selectSql(this, parameters);

		executeQuery(connection, sql, parameters, getRowConsumer(connection, this, consumer));
	}

	/**
		Generates and executes a SELECT SQL that joins one table.

		@param <JE1> the entity class of the joined table

		@param connection the database connection
		@param consumer a consumer for the entities of the main table
		@param consumer1 a consumer for the entities of the joined table

		@throws NullPointerException if <b>connection</b>, <b>consumer</b> or consumer1 is <b>null</b>
		@throws IllegalStateException if joinInfo information is less than 1
		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	*/
	public <JE1> void select(Connection connection,
		Consumer<? super E> consumer,
		Consumer<? super JE1> consumer1) {
		if (joinInfos.size() < 1) throw new IllegalStateException("Sql.select: joinInfos.size < 1");

		List<Object> parameters = new ArrayList<>();
		String sql = getDatabase().selectSql(this, parameters);

		executeQuery(connection, sql, parameters, resultSet -> {
			getRowConsumer(connection,  this                          , consumer ).accept(resultSet);
			getRowConsumer(connection, (JoinInfo<JE1>)joinInfos.get(0), consumer1).accept(resultSet);
		});
	}

	/**
		Generates and executes a SELECT SQL that joins two tables.

		@param <JE1> the entity class of the 1st joined table
		@param <JE2> the entity class of the 2nd joined table

		@param connection the database connection
		@param consumer a consumer for the entities of the main table
		@param consumer1 a consumer for the entities of the 1st join table
		@param consumer2 a consumer for the entities of the 2nd join table

		@throws NullPointerException if <b>connection</b>, <b>consumer</b>, <b>consumer1</b> or <b>consumer2</b> is <b>null</b>
		@throws IllegalStateException if join information is less than 2
		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	*/
	public <JE1, JE2> void select(Connection connection,
			Consumer<? super  E > consumer,
			Consumer<? super JE1> consumer1,
			Consumer<? super JE2> consumer2) {
		if (joinInfos.size() < 2) throw new IllegalStateException("Sql.select: joinInfos.size < 2");

		List<Object> parameters = new ArrayList<>();
		String sql = getDatabase().selectSql(this, parameters);

		executeQuery(connection, sql, parameters, resultSet -> {
			getRowConsumer(connection,  this                          , consumer ).accept(resultSet);
			getRowConsumer(connection, (JoinInfo<JE1>)joinInfos.get(0), consumer1).accept(resultSet);
			getRowConsumer(connection, (JoinInfo<JE2>)joinInfos.get(1), consumer2).accept(resultSet);
		});
	}

	/**
		Generates and executes a SELECT SQL that joins three tables.

		@param <JE1> the entity class of the 1st joined table
		@param <JE2> the entity class of the 2nd joined table
		@param <JE3> the entity class of the 3rd joined table

		@param connection the database connection
		@param consumer a consumer for the entities of the main table
		@param consumer1 a consumer for the entities of the 1st join table
		@param consumer2 a consumer for the entities of the 2nd join table
		@param consumer3 a consumer for the entities of the 3rd join table

		@throws NullPointerException if <b>connection</b>, <b>consumer</b>, <b>consumer1</b>, <b>consumer2</b> or <b>consumer3</b> is <b>null</b>
		@throws IllegalStateException if join information is less than 3
		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	*/
	public <JE1, JE2, JE3> void select(Connection connection,
			Consumer<? super  E > consumer,
			Consumer<? super JE1> consumer1,
			Consumer<? super JE2> consumer2,
			Consumer<? super JE3> consumer3) {
		if (joinInfos.size() < 3) throw new IllegalStateException("Sql.select: joinInfos.size < 3");

		List<Object> parameters = new ArrayList<>();
		String sql = getDatabase().selectSql(this, parameters);

		executeQuery(connection, sql, parameters, resultSet -> {
			getRowConsumer(connection,  this                          , consumer ).accept(resultSet);
			getRowConsumer(connection, (JoinInfo<JE1>)joinInfos.get(0), consumer1).accept(resultSet);
			getRowConsumer(connection, (JoinInfo<JE2>)joinInfos.get(1), consumer2).accept(resultSet);
			getRowConsumer(connection, (JoinInfo<JE3>)joinInfos.get(2), consumer3).accept(resultSet);
		});
	}

	/**
		Generates and executes a SELECT SQL that joins four tables.

		@param <JE1> the entity class of the 1st joined table
		@param <JE2> the entity class of the 2nd joined table
		@param <JE3> the entity class of the 3rd joined table
		@param <JE4> the entity class of the 4th joined table

		@param connection the database connection
		@param consumer a consumer for the entities of the main table
		@param consumer1 a consumer for the entities of the 1st join table
		@param consumer2 a consumer for the entities of the 2nd join table
		@param consumer3 a consumer for the entities of the 3rd join table
		@param consumer4 a consumer for the entities of the 4th join table

		@throws NullPointerException if <b>connection</b>, <b>consumer</b>, <b>consumer1</b>, <b>consumer2</b>, <b>consumer3</b> or <b>consumer4</b> is <b>null</b>
		@throws IllegalStateException if join information is less than 4
		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	*/
	public <JE1, JE2, JE3, JE4> void select(Connection connection,
			Consumer<? super  E > consumer,
			Consumer<? super JE1> consumer1,
			Consumer<? super JE2> consumer2,
			Consumer<? super JE3> consumer3,
			Consumer<? super JE4> consumer4) {
		if (joinInfos.size() < 4) throw new IllegalStateException("Sql.select: joinInfos.size < 4");

		List<Object> parameters = new ArrayList<>();
		String sql = getDatabase().selectSql(this, parameters);

		executeQuery(connection, sql, parameters, resultSet -> {
			getRowConsumer(connection,  this                          , consumer ).accept(resultSet);
			getRowConsumer(connection, (JoinInfo<JE1>)joinInfos.get(0), consumer1).accept(resultSet);
			getRowConsumer(connection, (JoinInfo<JE2>)joinInfos.get(1), consumer2).accept(resultSet);
			getRowConsumer(connection, (JoinInfo<JE3>)joinInfos.get(2), consumer3).accept(resultSet);
			getRowConsumer(connection, (JoinInfo<JE4>)joinInfos.get(3), consumer4).accept(resultSet);
		});
	}

	/**
		Generates and executes a SELECT SQL
		and returns an <b>Optional</b> of the entity if searched, <b>Optional.empty()</b> otherwise.

		@param connection the database connection

		@return an <b>Optional</b> of the entity if searched, <b>Optional.empty()</b> otherwise

		@throws NullPointerException if <b>connection</b> is <b>null</b>
		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
		@throws ManyRowsException if more than one row searched
	*/
	public Optional<E> select(Connection connection) {
		List<E> entities = new ArrayList<>();
		select(connection, entity -> {
			if (entities.size() > 0)
			// 1.5.0
			//	throw new ManyRowsException();
				throw new ManyRowsException(sql);
			////
			entities.add(entity);
		});
		return entities.size() == 0 ? Optional.empty() : Optional.of(entities.get(0));
	}

	/**
		Generates and executes a SELECT COUNT(*) SQL and returns the result.

		@param connection the database connection

		@return the row count

		@throws NullPointerException if <b>connection</b> is <b>null</b>
		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	*/
	public int selectCount(Connection connection) {
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
		Generates and executes an INSERT SQL.

		@param connection the database connection
		@param entity the entity to be inserted

		@return the row count

		@throws NullPointerException if <b>connection</b> or <b>entity</b> is <b>null</b>
		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	*/
	public int insert(Connection connection, E entity) {
		if (entity == null) throw new NullPointerException("Sql.insert: entity == null");

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
		Generates and executes INSERT SQLs for each element of entities.

		@param connection the database connection
		@param entities an <b>Iterable</b> of entities

		@return the row count

		@throws NullPointerException if <b>connection</b>, <b>entities</b> or any element of <b>entities</b> is <b>null</b>
		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	*/
	public int insert(Connection connection, Iterable<? extends E> entities) {
		if (entities == null) throw new NullPointerException("Sql.insert: entities == null");

		int[] count = new int[1];
		entities.forEach(entity -> count[0] += insert(connection, entity));
		return count[0];
	}

	/**
		Generates and executes an UPDATE SQL.<br>
		If the <b>WHERE</b> condition is specified, updates by the condition.<br>
		To update all rows of the target table, specify <b>Condition.ALL</b> to <b>WHERE</b> conditions.

		@param connection the database connection
		@param entity the entity to be updated

		@return the row count

		@throws NullPointerException if <b>connection</b> or <b>entity</b> is <b>null</b>
		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	*/
	public int update(Connection connection, E entity) {
		if (entity == null) throw new NullPointerException("Sql.update: entity == null");

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
		Generates and executes UPDATE SQLs for each element of entities.<br>
		Even if the <b>WHERE</b> condition is specified, <b> new EntityCondition(entity)</b> will be specified for each entity.

		@param connection the database connection
		@param entities an <b>Iterable</b> of entities

		@return the row count

		@throws NullPointerException if <b>connection</b>, <b>entities</b> or any element of <b>entities</b> is <b>null</b>
		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	*/
	public int update(Connection connection, Iterable<? extends E> entities) {
		if (entities == null) throw new NullPointerException("Sql.update: entities == null");

		int[] count = new int[1];
		entities.forEach(entity -> {
			where = Condition.EMPTY;
			count[0] += update(connection, entity);
		});
		return count[0];
	}

	/**
		Generates and executes a DELETE SQL.<br>
		If the <B>WHERE</b> condition is not specified, dose not delete.<br>
		To delete all rows of the target table, specify <b>Condition.ALL</b> to <b>WHERE</b> conditions.

		@param connection the database connection

		@return the row count

		@throws NullPointerException if <b>connection</b> is <b>null</b>
		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
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
		Generates and executes a DELETE SQL.

		@param connection the database connection
		@param entity the entity to be deleted

		@return the row count

		@throws NullPointerException if <b>connection</b> or <b>entity</b> is <b>null</b>
		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	*/
	public int delete(Connection connection, E entity) {
		if (entity == null) throw new NullPointerException("Sql.delete: entity == null");

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
		Generates and executes DELETE SQLs for each element of entities.

		@param connection the database connection
		@param entities an <b>Iterable</b> of entities

		@return the row count

		@throws NullPointerException if <b>connection</b>, <b>entities</b> or any element of <b>entities</b> is <b>null</b>
		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	*/
	public int delete(Connection connection, Iterable<? extends E> entities) {
		if (entities == null) throw new NullPointerException("Sql.delete: entities == null");

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
		Returns a row consumer.

		@param connection the database connection
		@param sqlEntityInfo the <b>SqlEntityInfo</b> object
		@param consumer the consumer

		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
		@throws RuntimeException InstantiationException, IllegalAccessException
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
		Executes the SELECT SQL.

		@param connection the database connection
		@param sql the SQL
		@param parameters the parameters of SQL
		@param consumer the consumer for the <b>ResultSet</b> object

		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	*/
	private void executeQuery(Connection connection, String sql, List<Object> parameters, Consumer<ResultSet> consumer) {
		if (connection == null) throw new NullPointerException("Sql.executeQuery: connection == null");
		if (sql == null) throw new NullPointerException("Sql.executeQuery: sql == null");
		if (parameters == null) throw new NullPointerException("Sql.executeQuery: parameters == null");
		if (consumer == null) throw new NullPointerException("Sql.executeQuery: consumer == null");

	// 1.5.0
		this.sql = sql;
	////
		logger.info(() -> getDatabase().getClass().getSimpleName() + ": " + sql);

		// Prepares SQL
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			//  Sets the parameter values
			for (int index = 0; index < parameters.size(); ++index) {
				Object parameter = parameters.get(index);
				if  (logger.isInfoEnabled())
					logger.info("  parameters[" + index + "]: " + Utils.toLogString(parameter));

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
		Executes the SQL which is INSERT, UPDATE or DELETE SQL.

		@param connection the database connection
		@param sql the SQL
		@param parameters the parameters of SQL

		@throws RuntimeSQLException if a <b>SQLException</b> is thrown while accessing the database, replaces it with this exception
	*/
	private int executeUpdate(Connection connection, String sql, List<Object> parameters) {
		if (connection == null) throw new NullPointerException("Sql.executeUpdate: connection == null");
		if (sql == null) throw new NullPointerException("Sql.executeUpdate: sql == null");
		if (parameters == null) throw new NullPointerException("Sql.executeUpdate: parameters == null");

	// 1.5.0
		this.sql = sql;
	////
		logger.info(() -> getDatabase().getClass().getSimpleName() + ": " + sql);

		// Prepares SQL
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			//  Sets the parameter values
			for (int index = 0; index < parameters.size(); ++index) {
				Object parameter = parameters.get(index);
				if  (logger.isInfoEnabled())
					logger.info("  parameters[" + index + "]: " + Utils.toLogString(parameter));

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
		Returns a <b>ColumnInfo</b> stream of the main table.<br>
		<i>This method is used internally.</i>

		@return a <b>ColumnInfo</b> stream
	*/
	public Stream<ColumnInfo> columnInfoStream() {
		return entityInfo().columnInfos().stream();
	}

	/**
		Returns a <b>ColumnInfo</b> stream of selected columns of the main table.<br>
		<i>This method is used internally.</i>

		@return a <b>ColumnInfo</b> stream
	*/
	public Stream<ColumnInfo> selectedColumnInfoStream() {
		return columns.isEmpty()
			? columnInfoStream()
			: columnInfoStream()
				.filter(columnInfo ->
					columns.stream().anyMatch(name ->
						name.endsWith("*")
							? columnInfo.propertyName().startsWith(name.substring(0, name.length() - 1))
							: columnInfo.propertyName().equals(name)
					)
				);
	}

	/**
		Returns a <b>SqlEntityInfo</b> stream of the main table and the joined tables.<br>
		<i>This method is used internally.</i>

		@return a <b>SqlEntityInfo</b> stream
	*/
	public Stream<SqlEntityInfo<?>> sqlEntityInfoStream() {
		return  Stream.concat(Stream.of(this), joinInfos.stream());
	}

	/**
		Returns a <b>SqlColumnInfo</b> stream of the main table and the joined tables.<br>
		<i>This method is used internally.</i>

		@return a <b>SqlColumnInfo</b> stream
	*/
	public Stream<SqlColumnInfo> joinSqlColumnInfoStream() {
		return sqlEntityInfoStream()
			.flatMap(sqlEntityInfo ->
				sqlEntityInfo.entityInfo().columnInfos().stream()
					.map(columnInfo -> new SqlColumnInfo(sqlEntityInfo.tableAlias(), columnInfo))
			);
	}

	/**
		Returns a <b>SqlColumnInfo</b> stream of selected columns
		of the main table and the joined tables.<br>
		<i>This method is used internally.</i>

		@return a <b>SqlColumnInfo</b> stream
	*/
	public Stream<SqlColumnInfo> selectedJoinSqlColumnInfoStream() {
		return columns.isEmpty()
			? joinSqlColumnInfoStream()
			: joinSqlColumnInfoStream()
				.filter(sqlColumnInfo -> columns.stream().anyMatch(name -> sqlColumnInfo.matches(name)));
	}
}
