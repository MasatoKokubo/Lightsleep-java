/*
	ColumnInfo.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep.helper;

import org.lightsleep.component.Expression;

/**
	Has the information of a column.

	@since 1.0.0
	@author Masato Kokubo
*/
public class ColumnInfo {
	// The entity information
	private final EntityInfo<?> entityInfo;

	// The property name
	private final String propertyName;

	// The column name
	private final String columnName;

	// Is key?
	private final boolean isKey;

	// The expression to be used to create SELECT SQL
	private final Expression selectExpression;

	// The expression to be used to create INSERT SQL
	private final Expression insertExpression;

	// The expression to be used to create UPDATE SQL
	private final Expression updateExpression;

	/**
		Constructs a new <b>ColumnInfo</b>.

		@param entityInfo the entity information
		@param propertyName the property name
		@param columnName the column name
		@param isKey <b>true</b> if key, <b>false</b> otherwise
		@param selectExpression the expression to be used to create SELECT SQL (permit <b>null</b>)
		@param insertExpression the expression to be used to create INSERT SQL (permit <b>null</b>)
		@param updateExpression the expression to be used to create UPDATE SQL (permit <b>null</b>)

		@throws NullPointerException <code>entityInfo</code>, <code>propertyName</code> または <code>columnName</code> が null の場合
	*/
	public ColumnInfo(
		EntityInfo<?> entityInfo, String propertyName, String columnName, boolean isKey,
		Expression selectExpression, Expression insertExpression, Expression updateExpression) {

		if (entityInfo == null) throw new NullPointerException("ColumnInfo.<init>: entityInfo == null");
		if (propertyName == null) throw new NullPointerException("ColumnInfo.<init>: propertyName == null");
		if (columnName == null) throw new NullPointerException("ColumnInfo.<init>: columnName == null");

		this.entityInfo       = entityInfo;
		this.propertyName     = propertyName;
		this.columnName       = columnName;
		this.isKey            = isKey;
		this.selectExpression = selectExpression;
		this.insertExpression = insertExpression;
		this.updateExpression = updateExpression;
	}

	/**
		Returns the entity information.

		@return the entity information
	*/
	public EntityInfo<?> entityInfo() {
		return entityInfo;
	}

	/**
		Returns the property name.

		@return the property name
	*/
	public String propertyName() {
		return propertyName;
	}

	/**
		Returns the associated column name.

		@return the associated column name
	*/
	public String columnName() {
		return columnName;
	}

	/**
		Returns whether the associated column is the primary key.

		@return <b>true</b> if the associated column is the primary key, <b>false</b> otherwise
	*/
	public boolean isKey() {
		return isKey;
	}

	/**
		Returns whether the associated column is used in SELECT SQL.

		@return <b>true</b> if the associated column is used in SELECT SQL, <b>false</b> otherwise
	*/
	public boolean selectable() {
		return selectExpression != null;
	}

	/**
		Returns whether the associated column is used in INSERT SQL.

		@return <b>true</b> if the associated column is used in INSERT SQL, <b>false</b> otherwise
	*/
	public boolean insertable() {
		return insertExpression != null;
	}

	/**
		Returns whether the associated column is used in UPDATE SQL.

		@return <b>true</b> if the associated column is used in UPDATE SQL, <b>false</b> otherwise
	*/
	public boolean updatable() {
		return updateExpression != null;
	}

	/**
		Returns the expression to be used to create SELECT SQL.

		@return the expression to be used to create SELECT SQL (<b>null</b> if not used)
	*/
	public Expression selectExpression() {
		return selectExpression;
	}

	/**
		Returns the expression to be used to create INSERT SQL.

		@return the expression to be used to create INSERT SQL (<b>null</b> if not used)
	*/
	public Expression insertExpression() {
		return insertExpression;
	}

	/**
		Returns the expression to be used to create UPDATE SQL.

		@return the expression to be used to create UPDATE SQL (<b>null</b> if not used)
	*/
	public Expression updateExpression() {
		return updateExpression;
	}

	/**
		Returns <b>tableAlias + '.' + <i>column name</i></b> if <b>tableAlias</b> is not empty,
		<b><i>column name</i></b> otherwise.

		@param tableAlias the table alias
		@return <b>tableAlias + '.' + <i>column name</i></b> or <b><i>column name</i></b>
	*/
	public String getColumnName(String tableAlias) {
		return tableAlias.isEmpty() ? columnName : tableAlias + '.' + columnName;
	}

	/**
		returns <b>tableAlias + '_' + <i>column name</i></b> if <b>tableAlias</b> is not empty,
		<b><i>column name</i></b> otherwise.

		@param tableAlias the table alias
		@return <b>tableAlias + '_' + <i>column name</i></b> or <b><i>column name</i></b>
	*/
	public String getColumnAlias(String tableAlias) {
		return tableAlias.isEmpty() ? columnName : tableAlias + '_' + columnName;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public int hashCode() {
		return propertyName.hashCode();
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null) return false;
		if (getClass() != object.getClass()) return false;
		return entityInfo.entityClass() == (((ColumnInfo)object).entityInfo.entityClass())
			&& propertyName.equals(((ColumnInfo)object).propertyName);
	}
}
