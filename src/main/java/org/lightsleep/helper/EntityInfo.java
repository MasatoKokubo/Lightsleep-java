// EntityInfo.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
	 * @throws NullPointerException if <b>entityClass</b> is <b>null</b>
	 * @throws IllegalArgumentException if <b>entityClass</b> is illegal
	 */
	public EntityInfo(Class<E> entityClass) {
		this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");

		if (entityClass.isAnnotation())
			throw new IllegalArgumentException("entityClass: " + entityClass.getName() + " is an annotation class");

		if (entityClass.isArray())
			throw new IllegalArgumentException("entityClass: " + entityClass.getName() + " is an array class");

		if (entityClass.isEnum())
			throw new IllegalArgumentException("entityClass: " + entityClass.getName() + " is an enum class");

		if (entityClass.isInterface())
			throw new IllegalArgumentException("entityClass: " + entityClass.getName() + " is an interface");

		if (entityClass.isPrimitive())
			throw new IllegalArgumentException("entityClass: " + entityClass.getName() + " is a primitive class");

		if ((entityClass.getModifiers() & Modifier.ABSTRACT) != 0)
			throw new IllegalArgumentException("entityClass: " + entityClass.getName() + " is an abstract class");

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
		Map<String, Boolean> keyMap = new HashMap<>();
		List<KeyProperty> keyProperties = Utils.getAnnotations(entityClass, KeyProperty.class);
		keyProperties.forEach(annotation -> keyMap.put(annotation.property(), annotation.value()));

		// @ColumnProperty, @ColumnProperties
		Map<String, String> columnMap = new HashMap<>();
		List<ColumnProperty> columnProperties = Utils.getAnnotations(entityClass, ColumnProperty.class);
		columnProperties.forEach(annotation -> columnMap.put(annotation.property(), annotation.column()));

		// @ColumnTypeProperty, @ColumnTypeProperties
		Map<String, Class<?>> columnTypeMap = new HashMap<>();
		List<ColumnTypeProperty> columnTypeProperties = Utils.getAnnotations(entityClass, ColumnTypeProperty.class);
		columnTypeProperties.stream().forEach(annotation -> columnTypeMap.put(annotation.property(), annotation.type()));

		// @NonSelectProperty, @NonSelectProperties
		Map<String, Boolean> nonSelectMap = new HashMap<>();
		List<NonSelectProperty> nonSelectProperties = Utils.getAnnotations(entityClass, NonSelectProperty.class);
		nonSelectProperties.forEach(annotation -> nonSelectMap.put(annotation.property(), annotation.value()));

		// @NonInsertProperty, @NonInsertProperties
		Map<String, Boolean> nonInsertMap = new HashMap<>();
		List<NonInsertProperty> nonInsertProperties = Utils.getAnnotations(entityClass, NonInsertProperty.class);
		nonInsertProperties.forEach(annotation -> nonInsertMap.put(annotation.property(), annotation.value()));

		// @NonUpdateProperty, @NonUpdateProperties
		Map<String, Boolean> nonUpdateMap = new HashMap<>();
		List<NonUpdateProperty> nonUpdateProperties = Utils.getAnnotations(entityClass, NonUpdateProperty.class);
		nonUpdateProperties.forEach(annotation -> nonUpdateMap.put(annotation.property(), annotation.value()));

		// @SelectProperty, @SelectProperties
		Map<String, String> selectMap = new HashMap<>();
		List<SelectProperty> selectProperties = Utils.getAnnotations(entityClass, SelectProperty.class);
		selectProperties.forEach(annotation -> selectMap.put(annotation.property(), annotation.expression()));

		// @InsertProperty, @InsertProperties
		Map<String, String> insertMap = new HashMap<>();
		List<InsertProperty> insertProperties = Utils.getAnnotations(entityClass, InsertProperty.class);
		insertProperties.forEach(annotation -> insertMap.put(annotation.property(), annotation.expression()));

		// @UpdateProperty, @UpdateProperties
		Map<String, String> updateMap = new HashMap<>();
		List<UpdateProperty> updateProperties = Utils.getAnnotations(entityClass, UpdateProperty.class);
		updateProperties.forEach(annotation -> updateMap.put(annotation.property(), annotation.expression()));

		columnInfoMap = new LinkedHashMap<>();

		for (String propertyName : accessor.valuePropertyNames()) {
			// the field
			Field field = accessor.getField(propertyName);

			// @Column / the column name
			String columnName = columnMap.get(propertyName);
			if (columnName == null) {
				Column column = field.getAnnotation(Column.class);
				if (column != null)
					columnName = column.value();
			}
			if (columnName == null || columnName.isEmpty())
				columnName = field.getName();

			// @ColumnType / the column type
			Class<?> columnType = columnTypeMap.get(propertyName);
			if (columnType == null) {
				ColumnType columnTypeAnn = field.getAnnotation(ColumnType.class);
				if (columnTypeAnn != null)
					columnType = columnTypeAnn.value();
			}
			if (columnType == Void.class)
				columnType = null;
			if (columnType != null && columnType.isPrimitive())
				throw new IllegalArgumentException(
					"@ColumnType value or @ColumnTypeProperty type: " + columnType.getClass().getName()
					+ "(primitive type), property: " + propertyName
					+ ", class: " + entityClass.getName());

			// @Key / is key?
			boolean isKey = false;
			if (keyMap.containsKey(propertyName)) {
				isKey = keyMap.get(propertyName);
			} else {
				Key key = field.getAnnotation(Key.class);
				if (key != null)
					isKey = key.value();
			}

			// @NonSelect
			boolean isNonSelect = false;
			if (nonSelectMap.containsKey(propertyName)) {
				isNonSelect = nonSelectMap.get(propertyName);
			} else {
				NonSelect nonSelect = field.getAnnotation(NonSelect.class);
				if (nonSelect != null)
					isNonSelect = nonSelect.value();
			}

			// @Select
			Expression selectExpression = null; // Null means non-selection
			if (!isNonSelect) {
				String selectString = selectMap.get(propertyName);
				if (selectString == null) {
					Select select = field.getAnnotation(Select.class);
					if (select != null)
						selectString = select.value();
				}
				selectExpression = selectString == null || selectString.isEmpty()
					? Expression.EMPTY // Empty means no expression specified
					: new Expression(selectString);
			}

			// @NonInsert
			boolean isNonInsert = false;
			if (nonInsertMap.containsKey(propertyName)) {
				isNonInsert = nonInsertMap.get(propertyName);
			} else {
				NonInsert nonInsert = field.getAnnotation(NonInsert.class);
				if (nonInsert != null)
					isNonInsert = nonInsert.value();
			}

			// @Insert
			Expression insertExpression = null; // Null means non-insertion
			if (!isNonInsert) {
				String insertString = insertMap.get(propertyName);
				if (insertString == null) {
					Insert insert = field.getAnnotation(Insert.class);
					if (insert != null)
						insertString = insert.value();
				}
				insertExpression = insertString == null || insertString.isEmpty()
					? Expression.EMPTY // Empty means no expression specified
					: new Expression(insertString);
			}

			// @NonUpdate
			boolean isNonUpdate = isKey; // Not updating for keys
			if (!isNonUpdate) {
				if (nonUpdateMap.containsKey(propertyName)) {
					isNonUpdate = nonUpdateMap.get(propertyName);
				} else {
					NonUpdate nonUpdate = field.getAnnotation(NonUpdate.class);
					if (nonUpdate != null)
						isNonUpdate = nonUpdate.value();
				}
			}

			// @Update
			Expression updateExpression = null; // Null means non-updating
			if (!isNonUpdate) {
				String updateString = updateMap.get(propertyName);
				if (updateString == null) {
					Update update = field.getAnnotation(Update.class);
					if (update != null)
						updateString = update.value();
				}
				updateExpression = updateString == null || updateString.isEmpty()
					? Expression.EMPTY // Empty means no expression specified
					: new Expression(updateString);
			}

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
	 * @throws NullPointerException if <b>propertyName</b> is <b>null</b>
	 * @throws IllegalArgumentException if the column information related to <b>propertyName</b> can not be found
	 */
	public ColumnInfo getColumnInfo(String propertyName) {
		Objects.requireNonNull(propertyName, "propertyName is null");

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
