// Common.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

import java.sql.Timestamp;

import org.lightsleep.Sql;
import org.lightsleep.connection.ConnectionWrapper;
import org.lightsleep.entity.*;

/**
 * Common properties of entities.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public abstract class Common implements PostInsert {
    /** Identifier */
    @Key
    @NonInsert // 3.2.0
    public int id;

    /** Update count */
    @Insert("0")
    @Update("{updateCount}+1")
    public int updateCount;

    /** Created Timestamp */
    @Insert("CURRENT_TIMESTAMP")
    @NonUpdate
    public Timestamp created;

    /** Updated Timestamp */
    @Insert("CURRENT_TIMESTAMP")
    @Update("CURRENT_TIMESTAMP")
    public Timestamp updated;

    @SuppressWarnings("unchecked")
    @Override
    public void postInsert(ConnectionWrapper conn) {
        Class<? extends Common> entityClass = getClass();
        if (PostSelect.class.isAssignableFrom(entityClass))
            entityClass = (Class<? extends Common>)entityClass.getSuperclass();
        new Sql<>(entityClass)
            .columns("id")
            .where("id=",
                new Sql<>(entityClass)
                    .columns("id")
                    .expression("id", "MAX({id})")
            )
            .connection(conn)
            .select(entity -> id = entity.id);
    }
}
