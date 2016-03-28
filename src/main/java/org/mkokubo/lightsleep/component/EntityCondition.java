/*
	EntityCondition.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.mkokubo.lightsleep.component;

import java.text.MessageFormat;
import java.util.List;

import org.mkokubo.lightsleep.helper.Accessor;
import org.mkokubo.lightsleep.helper.EntityInfo;
import org.mkokubo.lightsleep.helper.Resource;
import org.mkokubo.lightsleep.Sql;

/**
	Configure a condition using the value of the primary key of an entity.

	@param <E> the entity class

	@since 1.0.0
	@author Masato Kokubo
*/
public class EntityCondition<E> implements Condition {
	// Class resources
	private static final Resource resource = new Resource(EntityCondition.class);
	private static final String messageEntityNotHaveKeyColumns = resource.get("messageEntityNotHaveKeyColumns");

	// The entity
	private E entity;

	// The entity info
	private EntityInfo<E> entityInfo;

	/**
		Constructs a new <b>EntityCondition</b>.

		@param entity the entity

		@throws NullPointerException <b>entity</b> is <b>null</b>
	*/
	@SuppressWarnings("unchecked")
	public EntityCondition(E entity) {
		if (entity == null) throw new NullPointerException("EntityCondition.<init>: entity == null");

		entityInfo = Sql.getEntityInfo((Class<E>)entity.getClass());
		if (entityInfo.keyColumnInfos().size() == 0)
			throw new IllegalArgumentException(MessageFormat.format(messageEntityNotHaveKeyColumns, entityInfo.entityClass()));

		this.entity = entity;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public boolean isEmpty() {
		return false;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public <T> String toString(Sql<T> sql, List<Object> parameters) {
		String tableAlias = sql.tableAlias();
		Accessor<E> accessor = entityInfo.accessor();

		Condition[] condition = new Condition[] {Condition.EMPTY};

		entityInfo.keyColumnInfos().stream()
			.forEach(columnInfo -> {
				String propertyName = columnInfo.propertyName();
				String columnName = columnInfo.getColumnName(tableAlias);
				condition[0] = condition[0].and(columnName + " = {}", accessor.getValue(entity, propertyName));
			});

		return condition[0].toString(sql, parameters);
	}
}
