// SaleItem.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

import org.lightsleep.entity.Key;

/**
 * The entity of saleitem table.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class SaleItem {
	/** Identifier of the sale */
	@Key
	public int saleId;

	/** Item index (0～) */
	@Key
	public int itemIndex;

	/** Identifier of the product */
	public int productId;

	/** Quantity */
	public short quantity;
}
