// Not.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.List;

import org.lightsleep.Sql;
import org.lightsleep.database.Database;

/**
 * 否定条件を構成します。
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class Not implements Condition {
	private Condition condition;

	/**
	 * Notを構築します。
	 *
	 * @param condition 否定対象の条件
	 *
	 * @throws NullPointerException <b>condition</b>が<b>null</b>の場合
	 */
	public Not( Condition condition) {
	}

	/**
	 * 否定対象の条件を返します。
	 *
	 * @return 否定対象の条件
	 */
	public Condition condition() {
		return condition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return false;
	}

	/**
	 * 否定対象の条件が<b>Not</b>であれば否定対象の条件、そうでなければ<b>this</b>を返します。
	 *
	 * @return 否定対象の条件または<b>this</b>
	 *
	 * @since 1.8.8
	 */
	public Condition optimized() {
		if (condition instanceof Not) return condition;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String toString(Database database, Sql<E> sql, List<Object> parameters) {
		return null;
	}
}
