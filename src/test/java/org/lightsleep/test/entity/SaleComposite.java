// SaleComposite.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

import java.util.ArrayList;
import java.util.List;

import org.lightsleep.Sql;
import org.lightsleep.connection.ConnectionWrapper;
import org.lightsleep.entity.*;

/**
 * The composite entity of sale and saleitem table.
 * Sale Composite Entity.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
@Table("super")
public class SaleComposite extends Sale implements PostSelect, PostInsert, PostUpdate, PostDelete {
    /** Sale items */
    @NonColumn
    public final List<SaleItem> items = new ArrayList<>();

    @Override
    public void postSelect(ConnectionWrapper conn) {
        new Sql<>(SaleItem.class)
            .where("{saleId} = {}", id)
            .orderBy("{itemIndex}")
            .connection(conn)
            .select(items::add);
    }

    @Override
    public void postInsert(ConnectionWrapper conn) {
        super.postInsert(conn);

        for (int index = 0; index < items.size(); ++index) {
            SaleItem item = items.get(index);
            item.saleId    = id;
            item.itemIndex = index;
        }
        new Sql<>(SaleItem.class)
            .connection(conn)
            .insert(items);
    }

    @Override
    public void postUpdate(ConnectionWrapper conn) {
        new Sql<>(SaleItem.class)
            .connection(conn)
            .update(items);
    }

    @Override
    public void postDelete(ConnectionWrapper conn) {
        new Sql<>(SaleItem.class)
            .connection(conn)
            .delete(items);
    }
}
