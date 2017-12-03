// SaleComposite.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

import java.util.ArrayList;
import java.util.List;

import org.lightsleep.Sql;
import org.lightsleep.connection.ConnectionWrapper;
import org.lightsleep.entity.Composite;
import org.lightsleep.entity.NonColumn;
import org.lightsleep.entity.Table;

/**
 * The composite entity of sale and saleitem table.
 * Sale Composite Entity.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
@Table("super")
public class SaleComposite extends Sale implements Composite {
	/** Sale items */
	@NonColumn
	public final List<SaleItem> items = new ArrayList<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postSelect(ConnectionWrapper conn) {
		new Sql<>(SaleItem.class).connection(conn)
			.where("{saleId} = {}", id)
			.orderBy("{itemIndex}")
			.select(items::add);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int postInsert(ConnectionWrapper conn) {
		for (int index = 0; index < items.size(); ++index) {
			SaleItem item = items.get(index);
			item.saleId    = id;
			item.itemIndex = index;
		}
		return new Sql<>(SaleItem.class).connection(conn).insert(items);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int postUpdate(ConnectionWrapper conn) {
		return new Sql<>(SaleItem.class).connection(conn).update(items);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int postDelete(ConnectionWrapper conn) {
		return new Sql<>(SaleItem.class).connection(conn).delete(items);
	}
}
