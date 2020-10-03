// ColumnInfo.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

import java.util.Objects;

import org.lightsleep.component.Expression;

/**
 * Has the information of a column.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class ColumnInfo {
    // The entity information
    private final EntityInfo<?> entityInfo;

    // The property name
    private final String propertyName;

    // The column name
    private final String columnName;

    // The column type
    private final Class<?> columnType;

    // Is key?
    private final boolean isKey;

    // The expression to be used to create SELECT SQL
    private final Expression selectExpression;

    // The expression to be used to create INSERT SQL
    private final Expression insertExpression;

    // The expression to be used to create UPDATE SQL
    private final Expression updateExpression;

    /**
     * Constructs a new <b>ColumnInfo</b>.
     *
     * @param entityInfo the entity information
     * @param propertyName the property name
     * @param columnName the column name
     * @param columnType the column type (permit null)
     * @param isKey <b>true</b> if key, <b>false</b> otherwise
     * @param selectExpression the expression to be used to create SELECT SQL (permit null)
     * @param insertExpression the expression to be used to create INSERT SQL (permit null)
     * @param updateExpression the expression to be used to create UPDATE SQL (permit null)
     *
     * @throws NullPointerException <code>entityInfo</code>, <code>propertyName</code> または <code>columnName</code> が null の場合
     */
    public ColumnInfo(
        EntityInfo<?> entityInfo, String propertyName, String columnName, Class<?> columnType, boolean isKey,
        Expression selectExpression, Expression insertExpression, Expression updateExpression) {

        this.entityInfo       = Objects.requireNonNull(entityInfo, "entityInfo is null");
        this.propertyName     = Objects.requireNonNull(propertyName, "propertyName is null");
        this.columnName       = Objects.requireNonNull(columnName, "columnName is null");
        this.columnType       = columnType;
        this.isKey            = isKey;
        this.selectExpression = selectExpression;
        this.insertExpression = insertExpression;
        this.updateExpression = updateExpression;
    }

    /**
     * Returns the entity information.
     *
     * @return the entity information
     */
    public EntityInfo<?> entityInfo() {
        return entityInfo;
    }

    /**
     * Returns the property name.
     *
     * @return the property name
     */
    public String propertyName() {
        return propertyName;
    }

    /**
     * Returns the associated column name.
     *
     * @return the associated column name
     */
    public String columnName() {
        return columnName;
    }

    /**
     * Returns the associated column type.
     *
     * @return the associated column type
     *
     * @since 1.8.0
     */
    public Class<?> columnType() {
        return columnType;
    }

    /**
     * Returns whether the associated column is the primary key.
     *
     * @return <b>true</b> if the associated column is the primary key, <b>false</b> otherwise
     */
    public boolean isKey() {
        return isKey;
    }

    /**
     * Returns whether the associated column is used in SELECT SQL.
     *
     * @return <b>true</b> if the associated column is used in SELECT SQL, <b>false</b> otherwise
     */
    public boolean selectable() {
        return selectExpression != null;
    }

    /**
     * Returns whether the associated column is used in INSERT SQL.
     *
     * @return <b>true</b> if the associated column is used in INSERT SQL, <b>false</b> otherwise
     */
    public boolean insertable() {
        return insertExpression != null;
    }

    /**
     * Returns whether the associated column is used in UPDATE SQL.
     *
     * @return <b>true</b> if the associated column is used in UPDATE SQL, <b>false</b> otherwise
     */
    public boolean updatable() {
        return updateExpression != null;
    }

    /**
     * Returns the expression to be used to create SELECT SQL.
     *
     * @return the expression to be used to create SELECT SQL (null if not used)
     */
    public Expression selectExpression() {
        return selectExpression;
    }

    /**
     * Returns the expression to be used to create INSERT SQL.
     *
     * @return the expression to be used to create INSERT SQL (null if not used)
     */
    public Expression insertExpression() {
        return insertExpression;
    }

    /**
     * Returns the expression to be used to create UPDATE SQL.
     *
     * @return the expression to be used to create UPDATE SQL (null if not used)
     */
    public Expression updateExpression() {
        return updateExpression;
    }

    /**
     * Returns <b>tableAlias + '.' + <i>property name</i></b> if <b>tableAlias</b> is not empty,
     * <b><i>property name</i></b> otherwise.
     *
     * @param tableAlias the table alias
     * @return <b>tableAlias + '.' + <i>column name</i></b> or <b><i>column name</i></b>
     *
     * @since 1.8.2
     */
    public String getPropertyName(String tableAlias) {
        return tableAlias.isEmpty() ? propertyName : tableAlias + '.' + propertyName;
    }

    /**
     * Returns <b>tableAlias + '.' + <i>column name</i></b> if <b>tableAlias</b> is not empty,
     * <b><i>column name</i></b> otherwise.
     *
     * @param tableAlias the table alias
     * @return <b>tableAlias + '.' + <i>column name</i></b> or <b><i>column name</i></b>
     */
    public String getColumnName(String tableAlias) {
        return tableAlias.isEmpty() ? columnName : tableAlias + '.' + columnName;
    }

    /**
     * Returns <b>tableAlias + '_' + <i>column name</i></b> if <b>tableAlias</b> is not empty,
     * <b><i>column name</i></b> otherwise.
     *
     * @param tableAlias the table alias
     * @return <b>tableAlias + '_' + <i>column name</i></b> or <b><i>column name</i></b>
     */
    public String getColumnAlias(String tableAlias) {
        return tableAlias.isEmpty() ? columnName : tableAlias + '_' + columnName;
    }

    @Override
    public int hashCode() {
        return propertyName.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        if (getClass() != object.getClass()) return false;
        return entityInfo.entityClass() == (((ColumnInfo)object).entityInfo.entityClass())
            && propertyName.equals(((ColumnInfo)object).propertyName);
    }
}
