// Sale.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

import java.sql.Date;

/**
 * The entity of Sale table.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class Sale extends Common {
    /** Identifier of the sales destination */
    public int contactId;

    /** Sale date */
    public Date saleDate;

    /** Tax rate (%) */
    public Short taxRate;
}
