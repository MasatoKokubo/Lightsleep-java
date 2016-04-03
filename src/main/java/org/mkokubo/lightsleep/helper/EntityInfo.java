/*
	EntityInfo.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.helper;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mkokubo.lightsleep.annotation.Column;
import org.mkokubo.lightsleep.annotation.Insert;
import org.mkokubo.lightsleep.annotation.Key;
import org.mkokubo.lightsleep.annotation.NonInsert;
import org.mkokubo.lightsleep.annotation.NonSelect;
import org.mkokubo.lightsleep.annotation.NonUpdate;
import org.mkokubo.lightsleep.annotation.Select;
import org.mkokubo.lightsleep.annotation.Table;
import org.mkokubo.lightsleep.annotation.Update;
import org.mkokubo.lightsleep.component.Expression;

/**
	Has information of an entity class.

	@param <E> the type the entity

	@since 1.0
	@author Masato Kokubo
*/
public class EntityInfo<E> {
	// The entity class 
	private final Class<E> entityClass;

	// The accessor
	private final Accessor<E> accessor;

	// The table name
	private final String tableName;

	// The column information map (key: column name)
	private final Map<String, ColumnInfo> columnInfoMap;

	// The list of information of the columns
	private final List<ColumnInfo> columnInfos;

	// The list of information of the columns which are key
	private final List<ColumnInfo> keyColumnInfos;

	/**
		Constructs a new <b>EntityInfo</b>.

		@param entityClass the entity class

		@throws NullPointerException <b>entityClass</b> is null
	*/
	public EntityInfo(Class<E> entityClass) {
		if (entityClass == null) throw new NullPointerException("EntityInfo.<init>: entityClass == null");

		this.entityClass = entityClass;
		accessor = new Accessor<>(entityClass);

		// @Table / the table name
		Class<? super E> superEntityClass = entityClass;
		String tableName = null;
		for (;;) {
			Table table = superEntityClass.getAnnotation(Table.class);
			if (table == null) {
				tableName = superEntityClass.getSimpleName();
				break;
			}
			tableName = table.value();
			if (!tableName.equals("super"))
				break;
			superEntityClass = superEntityClass.getSuperclass();
		}
		this.tableName = tableName;

		columnInfoMap = new LinkedHashMap<>();

		for (String propertyName : accessor.valuePropertyNames()) {
			// the field
			Field field = accessor.getField(propertyName);

			// @Column / the column name
			Column column = field.getAnnotation(Column.class);
			String columnName = column != null ? column.value(): field.getName();

			// @Key / is key?
			boolean isKey = field.getAnnotation(Key.class) != null;

			// @NonSelect / the expression to be used to create SELECT SQL
			boolean nonSelect = field.getAnnotation(NonSelect.class) != null;
			Select select = field.getAnnotation(Select.class);
			Expression selectExpression = nonSelect
					? null
					: select == null
						? Expression.EMPTY
						: new Expression(select.value());

			// @NonInsert / the expression to be used to create INSERT SQL
			boolean nonInsert = field.getAnnotation(NonInsert.class) != null;
			Insert insert = field.getAnnotation(Insert.class);
			Expression insertExpression = nonInsert
						? null
						: insert == null
							? new Expression("{#" + propertyName + "}")
							: new Expression(insert.value());

			// @NonUpdate / the expression to be used to create UPDATE SQL
			boolean nonUpdate = field.getAnnotation(NonUpdate.class) != null;
			Update update = field.getAnnotation(Update.class);
			Expression updateExpression = nonUpdate
					? null
					: update == null
						? new Expression("{#" + propertyName + "}")
						: new Expression(update.value());

			ColumnInfo columnInfo = new ColumnInfo(this, propertyName, columnName, isKey, selectExpression, insertExpression, updateExpression);
			columnInfoMap.put(propertyName, columnInfo);
		}

		columnInfos = columnInfoMap.values().stream().collect(Collectors.toList());

		keyColumnInfos = columnInfos.stream().filter(columnInfo -> columnInfo.isKey()).collect(Collectors.toList());
	}

	/**
		Returns the entity class.

		@return the entity class
	*/
	public Class<E> entityClass() {
		return entityClass;
	}

	/**
		Returns the accessor.

		@return the accessor
	*/
	public Accessor<E> accessor() {
		return accessor;
	}

	/**
		Returns the table name.

		@return the table name
	*/
	public String tableName() {
		return tableName;
	}

	/**
		Returns the column information.

		@param propertyName the property name

		@return the column information

		@throws NullPointerException if <b>propertyName</b> is <b>null</b>
		@throws IllegalArgumentException if the column information related to <b>propertyName</b> can not be found
	*/
	public ColumnInfo getColumnInfo(String propertyName) {
		if (propertyName == null) throw new NullPointerException("EntityInfo.getColumnInfo: propertyName == null");

		ColumnInfo columnInfo = columnInfoMap.get(propertyName);
		if (columnInfo == null)
			throw new IllegalArgumentException(
				"EntityInfo.getColumnInfo: propertyName = " + propertyName
				+ ", entityClass = " + entityClass
				+ ", columnInfoMap.keySet = " + columnInfoMap.keySet()
				);

		return columnInfo;
	}

	/**
		Returns the list of information of the columns.

		@return the list of information of the columns
	*/
	public List<ColumnInfo> columnInfos() {
		return columnInfos;
	}

	/**
		Returns the list of information of the columns which are key

		@return the list of information of the columns
	*/
	public List<ColumnInfo> keyColumnInfos() {
		return keyColumnInfos;
	}
}
