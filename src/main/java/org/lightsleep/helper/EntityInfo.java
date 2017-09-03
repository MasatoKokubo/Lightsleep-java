// EntityInfo.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

import java.lang.reflect.Field;
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
	// 2.0.0
	//	Set<String> keySet = new HashSet<>();
		Map<String, Boolean> keyMap = new HashMap<>();
	////
		List<KeyProperty> keyProperties = Utils.getAnnotations(entityClass, KeyProperty.class);
	// 2.0.0
	//	if (keyProperties != null)
	//		keyProperties.forEach(annotation -> keySet.add(annotation.value()));
		keyProperties.forEach(annotation -> keyMap.put(annotation.property(), annotation.value()));
	////

		// @ColumnProperty, @ColumnProperties
		Map<String, String> columnMap = new HashMap<>();
		List<ColumnProperty> columnProperties = Utils.getAnnotations(entityClass, ColumnProperty.class);
	// 2.0.0
	//	if (columnProperties != null)
	////
		columnProperties.forEach(annotation -> columnMap.put(annotation.property(), annotation.column()));

		// @ColumnTypeProperty, @ColumnTypeProperties
		Map<String, Class<?>> columnTypeMap = new HashMap<>();
		List<ColumnTypeProperty> columnTypeProperties = Utils.getAnnotations(entityClass, ColumnTypeProperty.class);
	// 2.0.0
	//	if (columnTypeProperties != null)
	////
		columnTypeProperties.stream().forEach(annotation -> columnTypeMap.put(annotation.property(), annotation.type()));

		// @NonSelectProperty, @NonSelectProperties
	// 2.0.0
	//	Set<String> nonSelectSet = new HashSet<>();
		Map<String, Boolean> nonSelectMap = new HashMap<>();
	////
		List<NonSelectProperty> nonSelectProperties = Utils.getAnnotations(entityClass, NonSelectProperty.class);
	// 2.0.0
	//	if (nonSelectProperties != null)
	//		nonSelectProperties.forEach(annotation -> nonSelectSet.add(annotation.value()));
		nonSelectProperties.forEach(annotation -> nonSelectMap.put(annotation.property(), annotation.value()));
	////

		// @NonInsertProperty, @NonInsertProperties
	// 2.0.0
	//	Set<String> nonInsertSet = new HashSet<>();
		Map<String, Boolean> nonInsertMap = new HashMap<>();
	////
		List<NonInsertProperty> nonInsertProperties = Utils.getAnnotations(entityClass, NonInsertProperty.class);
	// 2.0.0
	//	if (nonInsertProperties != null)
	//		nonInsertProperties.forEach(annotation -> nonInsertSet.add(annotation.value()));
		nonInsertProperties.forEach(annotation -> nonInsertMap.put(annotation.property(), annotation.value()));
	////

		// @NonUpdateProperty, @NonUpdateProperties
	// 2.0.0
	//	Set<String> nonUpdateSet = new HashSet<>();
		Map<String, Boolean> nonUpdateMap = new HashMap<>();
	////
		List<NonUpdateProperty> nonUpdateProperties = Utils.getAnnotations(entityClass, NonUpdateProperty.class);
	// 2.0.0
	//	if (nonUpdateProperties != null)
	//		nonUpdateProperties.forEach(annotation -> nonUpdateSet.add(annotation.value()));
		nonUpdateProperties.forEach(annotation -> nonUpdateMap.put(annotation.property(), annotation.value()));
	////

		// @SelectProperty, @SelectProperties
		Map<String, String> selectMap = new HashMap<>();
		List<SelectProperty> selectProperties = Utils.getAnnotations(entityClass, SelectProperty.class);
	// 2.0.0
	//	if (selectProperties != null)
	////
		selectProperties.forEach(annotation -> selectMap.put(annotation.property(), annotation.expression()));

		// @InsertProperty, @InsertProperties
		Map<String, String> insertMap = new HashMap<>();
		List<InsertProperty> insertProperties = Utils.getAnnotations(entityClass, InsertProperty.class);
	// 2.0.0
	//	if (insertProperties != null)
	////
		insertProperties.forEach(annotation -> insertMap.put(annotation.property(), annotation.expression()));

		// @UpdateProperty, @UpdateProperties
		Map<String, String> updateMap = new HashMap<>();
		List<UpdateProperty> updateProperties = Utils.getAnnotations(entityClass, UpdateProperty.class);
	// 2.0.0
	//	if (updateProperties != null)
	////
		updateProperties.forEach(annotation -> updateMap.put(annotation.property(), annotation.expression()));

		columnInfoMap = new LinkedHashMap<>();

		for (String propertyName : accessor.valuePropertyNames()) {
			// the field
			Field field = accessor.getField(propertyName);

			// @Column / the column name
		// 2.0.0
		//	String columnName = columnMap.get(propertyName);
		//	if (columnName == null) {
		//		Column column = field.getAnnotation(Column.class);
		//		columnName = column != null ? column.value() : field.getName();
		//	}
			String columnName = columnMap.get(propertyName);
			if (columnName == null) {
				Column column = field.getAnnotation(Column.class);
				if (column != null)
					columnName = column.value();
			}
			if (columnName == null || columnName.isEmpty())
				columnName = field.getName();
		////

			// @ColumnType / the column type
		// 2.0.0
		//	Class<?> columnType = columnTypeMap.get(propertyName);
		//	if (columnType == null) {
		//		ColumnType columnTypeAnn = field.getAnnotation(ColumnType.class);
		//		if (columnTypeAnn != null)
		//			columnType = columnTypeAnn.value();
		//	}
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
		////

			// @Key / is key?
		// 2.0.0
		//	boolean isKey = field.getAnnotation(Key.class) != null
		//		|| keySet.contains(propertyName);
			boolean isKey = false;
			if (keyMap.containsKey(propertyName)) {
				isKey = keyMap.get(propertyName);
			} else {
				Key key = field.getAnnotation(Key.class);
				if (key != null)
					isKey = key.value();
			}
		////

			// @NonSelect
		// 2.0.0
		//	boolean nonSelect = field.getAnnotation(NonSelect.class) != null
		//		|| nonSelectSet.contains(propertyName);
			boolean isNonSelect = false;
			if (nonSelectMap.containsKey(propertyName)) {
				isNonSelect = nonSelectMap.get(propertyName);
			} else {
				NonSelect nonSelect = field.getAnnotation(NonSelect.class);
				if (nonSelect != null)
					isNonSelect = nonSelect.value();
			}
		////

			// @Select
		// 2.0.0
		//	String selectString = selectMap.get(propertyName);
		//	if (selectString == null) {
		//		Select select = field.getAnnotation(Select.class);
		//		if (select != null) selectString = select.value();
		//	}
		//	Expression selectExpression = nonSelect ? null 
		//		: selectString != null
		//			? new Expression(selectString)
		//			: Expression.EMPTY;
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
		////

			// @NonInsert
		// 2.0.0
		//	boolean nonInsert = field.getAnnotation(NonInsert.class) != null
		//		|| nonInsertSet.contains(propertyName);
			boolean isNonInsert = false;
			if (nonInsertMap.containsKey(propertyName)) {
				isNonInsert = nonInsertMap.get(propertyName);
			} else {
				NonInsert nonInsert = field.getAnnotation(NonInsert.class);
				if (nonInsert != null)
					isNonInsert = nonInsert.value();
			}
		////

			// @Insert
		// 2.0.0
		//	String insertString = insertMap.get(propertyName);
		//	if (insertString == null) {
		//		Insert insert = field.getAnnotation(Insert.class);
		//		if (insert != null) insertString = insert.value();
		//	}
		//	Expression insertExpression = nonInsert ? null 
		//		: insertString != null
		//			? new Expression(insertString)
		//			: Expression.EMPTY;
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
		////

			// @NonUpdate
		// 2.0.0
		//	boolean nonUpdate = isKey
		//		|| field.getAnnotation(NonUpdate.class) != null
		//		|| nonUpdateSet.contains(propertyName);
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
		////

			// @Update
		// 2.0.0
		//	String updateString = updateMap.get(propertyName);
		//	if (updateString == null) {
		//		Update update = field.getAnnotation(Update.class);
		//		if (update != null) updateString = update.value();
		//	}
		//	Expression updateExpression = nonUpdate ? null
		//		: updateString != null
		//			? new Expression(updateString)
		//			: Expression.EMPTY;
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
		////

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
