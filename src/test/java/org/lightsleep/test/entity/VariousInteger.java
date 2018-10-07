// VariousInteger.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

import org.lightsleep.entity.Key;
import org.lightsleep.entity.Table;

@Table("Various")
/**
 * The entity of various table.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class VariousInteger {
	@Key()
	/** ID                  */ public int      id            ;

	/** TINYINT  (NOT NULL) */ public int      tinyIntPValue ;
	/** SMALLINT (NOT NULL) */ public int      smallIntPValue;
	/** INT      (NOT NULL) */ public int      intPValue     ;
	/** BIGINT   (NOT NULL) */ public int      bigIntPValue  ;
	/** FLOAT    (NOT NULL) */ public int      floatPValue   ;
	/** DOUBLE   (NOT NULL) */ public int      doublePValue  ;

	/** TINYINT             */ public Integer  tinyIntValue  ;
	/** SMALLINT            */ public Integer  smallIntValue ;
	/** INT                 */ public Integer  intValue      ;
	/** BIGINT              */ public Integer  bigIntValue   ;
	/** FLOAT               */ public Integer  floatValue    ;
	/** DOUBLE              */ public Integer  doubleValue   ;
	/** DECIMAL(12.2)       */ public Integer  decimalValue  ;
}
