// VariousString.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

import org.lightsleep.entity.Key;
import org.lightsleep.entity.Table;

@Table("Various")
/**
 * The entity of Various table.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class VariousString {
    @Key()
    /** ID                  */ public int    id              ;

    /** CHAR(1)  (NOT NULL) */ public String char1PValue     ;
    /** TINYINT  (NOT NULL) */ public String tinyIntPValue   ;
    /** SMALLINT (NOT NULL) */ public String smallIntPValue  ;
    /** INT      (NOT NULL) */ public String intPValue       ;
    /** BIGINT   (NOT NULL) */ public String bigIntPValue    ;
    /** FLOAT    (NOT NULL) */ public String floatPValue     ;
    /** DOUBLE   (NOT NULL) */ public String doublePValue    ;

    /** CHAR(1)             */ public String char1Value      ;
    /** TINYINT             */ public String tinyIntValue    ;
    /** SMALLINT            */ public String smallIntValue   ;
    /** INT                 */ public String intValue        ;
    /** BIGINT              */ public String bigIntValue     ;
    /** FLOAT               */ public String floatValue      ;
    /** DOUBLE              */ public String doubleValue     ;
    /** DECIMAL(12.2)       */ public String decimalValue    ;

    /** CHAR(20)            */ public String charValue       ;
    /** VARCHAR(40)         */ public String varCharValue    ;
    /** BINARY(20)          */ public String binaryValue     ;
    /** VARBINARY(40)       */ public String varBinaryValue  ;
    /** TEXT                */ public String textValue       ;
    /** BLOB                */ public String blobValue       ;
}
