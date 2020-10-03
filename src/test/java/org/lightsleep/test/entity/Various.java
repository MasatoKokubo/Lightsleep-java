// Various.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.lightsleep.connection.ConnectionWrapper;
import org.lightsleep.entity.*;

/**
 * The entity of Various table.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
//public class Various extends VariousBase {
public class Various {
    @Key
    /**     PRIMARY KEY          */   public int          id              ;

    /** BOOLEAN  (NOT NULL)      */   public boolean      booleanPValue   ;
    /** CHAR(1)  (NOT NULL)      */   public char         char1PValue     ;
    /** TINYINT  (NOT NULL)      */   public byte         tinyIntPValue   ;
    /** SMALLINT (NOT NULL)      */   public short        smallIntPValue  ;
    /** INT      (NOT NULL)      */   public int          intPValue       ;
    /** BIGINT   (NOT NULL)      */   public long         bigIntPValue    ;
    /** FLOAT    (NOT NULL)      */   public float        floatPValue     ;
    /** DOUBLE   (NOT NULL)      */   public double       doublePValue    ;

    /** BOOLEAN                  */   public Boolean      booleanValue     ;
    /** CHAR(1)                  */   public Character    char1Value       ;
    /** TINYINT                  */   public Byte         tinyIntValue     ;
    /** SMALLINT                 */   public Short        smallIntValue    ;
    /** INT                      */   public Integer      intValue         ;
    /** BIGINT                   */   public Long         bigIntValue      ;
    /** FLOAT                    */   public Float        floatValue       ;
    /** DOUBLE                   */   public Double       doubleValue      ;
    /** DECIMAL(12.2)            */   public BigDecimal   decimalValue     ;

    /** DATE      */ @ColumnType(Long.class) public Date      longDate     ; // since 1.8.0
    /** TIME      */ @ColumnType(Long.class) public Time      longTime     ; // since 1.8.0
    /** TIMESTAMP */ @ColumnType(Long.class) public Timestamp longTimestamp; // since 1.8.0

    /** CHAR(20)                 */   public String       charValue        ;
    /** VARCHAR(40)              */   public String       varCharValue     ;
    /** BINARY(20)               */   public byte[]       binaryValue      ;
    /** VARBINARY(40)            */   public byte[]       varBinaryValue   ;
    /** TEXT                     */   public String       textValue        ;
    /** BLOB                     */   public byte[]       blobValue        ;

    @Table("super")
    public static class PostgreSQL extends Various implements PostSelect, PreInsert, PreUpdate {
        /** JSON                */
        @Insert("CAST({#jsonValue} AS JSON)")
        @Update("CAST({#jsonValue} AS JSON)") public String jsonValue ;
        /** JSONB               */
        @Insert("CAST({#jsonbValue} AS JSONB)")
        @Update("CAST({#jsonbValue} AS JSONB)") public String jsonbValue;
        @Select("({jsonValue}->>'x')::integer")  @NonInsert @NonUpdate public Integer jsonX;
        @Select("({jsonValue}->>'y')::integer")  @NonInsert @NonUpdate public Integer jsonY;
        @Select("({jsonbValue}->>'x')::integer") @NonInsert @NonUpdate public Integer jsonbX;
        @Select("({jsonbValue}->>'y')::integer") @NonInsert @NonUpdate public Integer jsonbY;

    //    @Column("(jsonValue->>'x')")  @NonInsert public Integer jsonX;
    //    @Column("(jsonValue->>'y')")  @NonInsert public Integer jsonY;
    //    @Column("(jsonbValue->>'x')") @NonInsert public Integer jsonbX;
    //    @Column("(jsonbValue->>'y')") @NonInsert public Integer jsonbY;

        /** BOOLEAN       ARRAY */ public boolean   [] booleans  ;
        /** SMALLINT      ARRAY */ public short     [] shorts    ;
        /** INT           ARRAY */ public int       [] ints      ;
        /** BIGINT        ARRAY */ public long      [] longs     ;
        /** FLOAT         ARRAY */ public float     [] floats    ;
        /** DOUBLE        ARRAY */ public double    [] doubles   ;
        /** DECIMAL(12.2) ARRAY */ public BigDecimal[] decimals  ;
        /** TEXT          ARRAY */ public String    [] texts     ;
        /** DATE          ARRAY */ public Date      [] dates     ;
        /** TIME          ARRAY */ public Time      [] times     ;
        /** TIMESTAMP     ARRAY */ public Timestamp [] timestamps;

        @NonColumn
        public List<Short> shortList;

        @Override
        public void preInsert(ConnectionWrapper connection) {
            if (shortList == null) {
                shorts = null;
            } else {
                shorts = new short[shortList.size()];
                for (int index = 0; index < shorts.length; ++index)
                    shorts[index] = shortList.get(index);
            }
        }

        @Override
        public void preUpdate(ConnectionWrapper connection) {
            preInsert(connection);
        }

        @Override
        public void postSelect(ConnectionWrapper connection) {
            if (shorts == null) {
                shortList = null;
            } else {
                shortList = new ArrayList<>();
                for (int index = 0; index < shorts.length; ++index)
                    shortList.add(shorts[index]);
            }
        }
    }
}
