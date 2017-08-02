// EntityInfo.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.lightsleep.component.Expression;
import org.lightsleep.entity.*;

/**
 * Has information of an entity class.
 *
 * @param <E> the type the entity
 *
 * @since 1.0
 * @author Masato Kokubo
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
	 * Constructs a new <b>EntityInfo</b>.
	 *
	 * @param entityClass the entity class
	 *
	 * @throws NullPointerException <b>entityClass</b> is null
	 */
	public EntityInfo(Class<E> entityClass) {
		this.entityClass = Objects.requireNonNull(entityClass, "entityClass");
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

		// @KeyProperty, @KeyProperties
		Set<String> keySet = new HashSet<>();
		List<KeyProperty> keyProperties = Utils.getAnnotations(entityClass, KeyProperty.class);
		if (keyProperties != null)
			keyProperties.forEach(annotation -> keySet.add(annotation.value()));

		// @NonSelectProperty, @NonSelectProperties
		Set<String> nonSelectSet = new HashSet<>();
		List<NonSelectProperty> nonSelectProperties = Utils.getAnnotations(entityClass, NonSelectProperty.class);
		if (nonSelectProperties != null)
			nonSelectProperties.forEach(annotation -> nonSelectSet.add(annotation.value()));

		// @NonInsertProperty, @NonInsertProperties
		Set<String> nonInsertSet = new HashSet<>();
		List<NonInsertProperty> nonInsertProperties = Utils.getAnnotations(entityClass, NonInsertProperty.class);
		if (nonInsertProperties != null)
			nonInsertProperties.forEach(annotation -> nonInsertSet.add(annotation.value()));

		// @NonUpdateProperty, @NonUpdateProperties
		Set<String> nonUpdateSet = new HashSet<>();
		List<NonUpdateProperty> nonUpdateProperties = Utils.getAnnotations(entityClass, NonUpdateProperty.class);
		if (nonUpdateProperties != null)
			nonUpdateProperties.forEach(annotation -> nonUpdateSet.add(annotation.value()));

		// @ColumnProperty, @ColumnProperties
		Map<String, String> columnMap = new HashMap<>();
		List<ColumnProperty> columnProperties = Utils.getAnnotations(entityClass, ColumnProperty.class);
		if (columnProperties != null)
			columnProperties.forEach(annotation -> columnMap.put(annotation.property(), annotation.column()));

		// @ColumnTypeProperty, @ColumnTypeProperties
		Map<String, Class<?>> columnTypeMap = new HashMap<>();
		List<ColumnTypeProperty> columnTypeProperties = Utils.getAnnotations(entityClass, ColumnTypeProperty.class);
		if (columnTypeProperties != null)
			columnTypeProperties.forEach(annotation -> columnTypeMap.put(annotation.property(), annotation.type()));

		// @SelectProperty, @SelectProperties
		Map<String, String> selectMap = new HashMap<>();
		List<SelectProperty> selectProperties = Utils.getAnnotations(entityClass, SelectProperty.class);
		if (selectProperties != null)
			selectProperties.forEach(annotation -> selectMap.put(annotation.property(), annotation.expression()));

		// @InsertProperty, @InsertProperties
		Map<String, String> insertMap = new HashMap<>();
		List<InsertProperty> insertProperties = Utils.getAnnotations(entityClass, InsertProperty.class);
		if (insertProperties != null)
			insertProperties.forEach(annotation -> insertMap.put(annotation.property(), annotation.expression()));

		// @UpdateProperty, @UpdateProperties
		Map<String, String> updateMap = new HashMap<>();
		List<UpdateProperty> updateProperties = Utils.getAnnotations(entityClass, UpdateProperty.class);
		if (updateProperties != null)
			updateProperties.forEach(annotation -> updateMap.put(annotation.property(), annotation.expression()));

		columnInfoMap = new LinkedHashMap<>();

		for (String propertyName : accessor.valuePropertyNames()) {
			// the field
			Field field = accessor.getField(propertyName);

			// @Column / the column name
			String columnName = columnMap.get(propertyName);
			if (columnName == null) {
				Column column = field.getAnnotation(Column.class);
				columnName = column != null ? column.value() : field.getName();
			}

			// @ColumnType / the column type
			Class<?> columnType = columnTypeMap.get(propertyName);
			if (columnType == null) {
				ColumnType columnTypeAnn = field.getAnnotation(ColumnType.class);
				if (columnTypeAnn != null)
					columnType = columnTypeAnn.value();
			}

			// @Key / is key?
			boolean isKey = field.getAnnotation(Key.class) != null
				|| keySet.contains(propertyName);

			// @NonSelect
			boolean nonSelect = field.getAnnotation(NonSelect.class) != null
				|| nonSelectSet.contains(propertyName);

			// @Select
			String selectString = selectMap.get(propertyName);
			if (selectString == null) {
				Select select = field.getAnnotation(Select.class);
				if (select != null) selectString = select.value();
			}
			Expression selectExpression = nonSelect ? null 
				: selectString != null
					? new Expression(selectString)
					: Expression.EMPTY;

			// @NonInsert / the expression to be used to create INSERT SQL
			boolean nonInsert = field.getAnnotation(NonInsert.class) != null
				|| nonInsertSet.contains(propertyName);

			// @Insert
			String insertString = insertMap.get(propertyName);
			if (insertString == null) {
				Insert insert = field.getAnnotation(Insert.class);
				if (insert != null) insertString = insert.value();
			}
			Expression insertExpression = nonInsert ? null 
				: insertString != null
					? new Expression(insertString)
					: Expression.EMPTY;

			// @NonUpdate
			boolean nonUpdate = isKey
				|| field.getAnnotation(NonUpdate.class) != null
				|| nonUpdateSet.contains(propertyName);

			// @Update / the expression to be used to create UPDATE SQL
			String updateString = updateMap.get(propertyName);
			if (updateString == null) {
				Update update = field.getAnnotation(Update.class);
				if (update != null) updateString = update.value();
			}
			Expression updateExpression = nonUpdate ? null
				: updateString != null
					? new Expression(updateString)
					: Expression.EMPTY;

			// creates a new ColumnInfo
			ColumnInfo columnInfo = new ColumnInfo(
				this, propertyName, columnName, columnType, isKey,
				selectExpression, insertExpression, updateExpression);
			columnInfoMap.put(propertyName, columnInfo);
		}

		columnInfos = columnInfoMap.values().stream().collect(Collectors.toList());

		keyColumnInfos = columnInfos.stream().filter(ColumnInfo::isKey).collect(Collectors.toList());
	}

	/**
	 * Returns the entity class.
	 *
	 * @return the entity class
	 */
	public Class<E> entityClass() {
		return entityClass;
	}

	/**
	 * Returns the accessor.
	 *
	 * @return the accessor
	 */
	public Accessor<E> accessor() {
		return accessor;
	}

	/**
	 * Returns the table name.
	 *
	 * @return the table name
	 */
	public String tableName() {
		return tableName;
	}

	/**
	 * Returns the column information.
	 *
	 * @param propertyName the property name
	 * @return the column information
	 *
	 * @throws NullPointerException if <b>propertyName</b> is null
	 * @throws IllegalArgumentException if the column information related to <b>propertyName</b> can not be found
	 */
	public ColumnInfo getColumnInfo(String propertyName) {
		Objects.requireNonNull(propertyName, "propertyName");

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
	 * Returns the list of information of the columns.
	 *
	 * @return the list of information of the columns
	 */
	public List<ColumnInfo> columnInfos() {
		return columnInfos;
	}

	/**
	 * Returns the list of information of the columns which are key
	 *
	 * @return the list of information of the columns
	 */
	public List<ColumnInfo> keyColumnInfos() {
		return keyColumnInfos;
	}
}
