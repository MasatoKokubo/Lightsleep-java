// DateAndTime.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import org.debugtrace.DebugTrace;
import org.lightsleep.entity.*;
import org.lightsleep.helper.Utils;

/**
 * The entity of DateAndTime table.
 *
 * @since 3.0.0
 * @author Masato Kokubo
 */
public abstract class DateAndTime {
    @Key
    /** PRIMARY KEY */
    public int id;

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "DateAndTime [id=" + id + "]";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        DateAndTime other = (DateAndTime) obj;
        if (id != other.id) return false;
        return true;
    }

    /** Field types are Date, Time or Timestamp. */
    @Table("super")
    public static class JavaSql extends DateAndTime {
        /** DATE */
        public Date dateValue;

        /** TIME */
        public Time timeValue;

        /** TIMESTAMP */
        public Timestamp timestampValue;

        /** TIMESTAMP WITH TIME ZONE */
        @ColumnType(ZonedDateTime.class)
        public Timestamp timestampTZValue;

        /** TIMESTAMP LOCAL WITH TIME ZONE */
        public Timestamp timestampLTZValue;

        /** for Db2 */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class Db2 extends JavaSql {
        }

        /** for MariaDB */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class MariaDB extends JavaSql {
        }

        /** for MySQL */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class MySQL extends JavaSql {
        }

        /** for Oracle */
        @Table("super")
        public static class Oracle extends JavaSql {
        }

        /** for PostgreSQL */
        @Table("super")
        @NonColumnProperty(property="timestampLTZValue")
        public static class PostgreSQL extends JavaSql {
        }

        /** for SQLite */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class SQLite extends JavaSql {
        }

        /** for SQLServer */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=OffsetDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class SQLServer extends JavaSql {
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "DateAndTime.JavaSql [id=" + id +
                ", dateValue="         + Utils.toLogString(dateValue        )+
                ", timeValue="         + Utils.toLogString(timeValue        )+
                ", timestampValue="    + Utils.toLogString(timestampValue   )+
                ", timestampTZValue="  + Utils.toLogString(timestampTZValue )+
                ", timestampLTZValue=" + Utils.toLogString(timestampLTZValue)
                + "]";
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((dateValue == null) ? 0 : dateValue.hashCode());
            result = prime * result + ((timeValue == null) ? 0 : timeValue.hashCode());
            result = prime * result + ((timestampValue == null) ? 0 : timestampValue.hashCode());
            result = prime * result + ((timestampTZValue == null) ? 0 : timestampTZValue.hashCode());
            result = prime * result + ((timestampLTZValue == null) ? 0 : timestampLTZValue.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;

            if (getClass() != obj.getClass()) {DebugTrace.print("DateAndTime.JavaSql.equals: 1"); return false;}

            JavaSql other = (JavaSql) obj;

            if (dateValue == null) {
                if (other.dateValue != null) {DebugTrace.print("DateAndTime.JavaSql.equals: 2-1"); return false;}
            } else if (!dateValue.equals(other.dateValue)) {DebugTrace.print("DateAndTime.JavaSql.equals: 2-2"); return false;}

            if (timeValue == null) {
                if (other.timeValue != null) {DebugTrace.print("DateAndTime.JavaSql.equals: 3-1"); return false;}
            } else if (!timeValue.equals(other.timeValue)) {
                DebugTrace.print("timeValue", timeValue);
                DebugTrace.print("other.timeValue", other.timeValue);
                DebugTrace.print("DateAndTime.JavaSql.equals: 3-2"); return false;
            }

            if (timestampValue == null) {
                if (other.timestampValue != null) {DebugTrace.print("DateAndTime.JavaSql.equals: 4-1"); return false;}
            } else if (!timestampValue.equals(other.timestampValue)) {
                DebugTrace.print("timestampValue", timestampValue);
                DebugTrace.print("other.timestampValue", other.timestampValue);
                DebugTrace.print("DateAndTime.JavaSql.equals: 4-2"); return false;
            }

            if (timestampTZValue == null) {
                if (other.timestampTZValue != null) {DebugTrace.print("DateAndTime.JavaSql.equals: 5=1"); return false;}
            } else if (!timestampTZValue.equals(other.timestampTZValue)) {DebugTrace.print("DateAndTime.JavaSql.equals: 5-2"); return false;}

            if (timestampLTZValue == null) {
                if (other.timestampLTZValue != null) {DebugTrace.print("DateAndTime.JavaSql.equals: 6-1"); return false;}
            } else if (!timestampLTZValue.equals(other.timestampLTZValue)) {DebugTrace.print("DateAndTime.JavaSql.equals: 6-2"); return false;}

            return true;
        }
    }

    /** Field types are Long. */
    @Table("super")
    public static class JavaLong extends DateAndTime {
        /** TIMESTAMP */
        @ColumnType(LocalDateTime.class)
        public Long timestampValue;

        /** TIMESTAMP WITH TIME ZONE */
        @ColumnType(ZonedDateTime.class)
        public Long timestampTZValue;

        /** TIMESTAMP WITH LOCAL TIME ZONE */
        @ColumnType(LocalDateTime.class)
        public Long timestampLTZValue;

        /** for Db2 */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class Db2 extends JavaLong {
        }

        /** for MariaDB */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class MariaDB extends JavaLong {
        }

        /** for MySQL */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class MySQL extends JavaLong {
        }

        /** for Oracle */
        @Table("super")
        public static class Oracle extends JavaLong {
        }

        /** for PostgreSQL */
        @Table("super")
        @NonColumnProperty(property="timestampLTZValue")
        public static class PostgreSQL extends JavaLong {
        }

        /** for SQLite */
        @Table("super")
        @NonColumnProperty(property="timestampLTZValue")
        public static class SQLite extends JavaLong {
        }

        /** for SQLServer */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=OffsetDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class SQLServer extends JavaLong {
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "DateAndTime.JavaLong [id=" + id +
                ", timestampValue="    + Utils.toLogString(timestampValue   )+
                ", timestampTZValue="  + Utils.toLogString(timestampTZValue )+
                ", timestampLTZValue=" + Utils.toLogString(timestampLTZValue)
                + "]";
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((timestampValue == null) ? 0 : timestampValue.hashCode());
            result = prime * result + ((timestampTZValue == null) ? 0 : timestampTZValue.hashCode());
            result = prime * result + ((timestampLTZValue == null) ? 0 : timestampLTZValue.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;

            if (getClass() != obj.getClass()) return false;

            JavaLong other = (JavaLong) obj;

            if (timestampValue == null) {
                if (other.timestampValue != null) return false;
            } else if (!timestampValue.equals(other.timestampValue)) return false;

            if (timestampTZValue == null) {
                if (other.timestampTZValue != null) return false;
            } else if (!timestampTZValue.equals(other.timestampTZValue)) return false;

            if (timestampLTZValue == null) {
                if (other.timestampLTZValue != null) return false;
            } else if (!timestampLTZValue.equals(other.timestampLTZValue)) return false;

            return true;
        }
    }

    /** Field types are LocalDate, LocalTime or LocalDateTime. */
    @Table("super")
    public static class Local extends DateAndTime {
        /** DATE */
        public LocalDate dateValue;

        /** TIME */
        public LocalTime timeValue;

        /** TIMESTAMP */
        public LocalDateTime timestampValue;

        /** TIMESTAMP WITH TIME ZONE */
        @ColumnType(ZonedDateTime.class)
        public LocalDateTime timestampTZValue;

        /** TIMESTAMP WITH LOCAL TIME ZONE */
        public LocalDateTime timestampLTZValue;

        /** for Db2 */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class Db2 extends Local {
        }

        /** for MariaDB */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class MariaDB extends Local {
        }

        /** for MySQL */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class MySQL extends Local {
        }

        /** for Oracle */
        @Table("super")
        public static class Oracle extends Local {
        }

        /** for PostgreSQL */
        @Table("super")
        @NonColumnProperty(property="timestampLTZValue")
        public static class PostgreSQL extends Local {
        }

        /** for SQLite */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class SQLite extends Local {
        }

        /** for SQLServer */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=OffsetDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class SQLServer extends Local {
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "DateAndTime.Local [id=" + id +
                ", dateValue="         + Utils.toLogString(dateValue        )+
                ", timeValue="         + Utils.toLogString(timeValue        )+
                ", timestampValue="    + Utils.toLogString(timestampValue   )+
                ", timestampTZValue="  + Utils.toLogString(timestampTZValue )+
                ", timestampLTZValue=" + Utils.toLogString(timestampLTZValue)
                + "]";
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((dateValue == null) ? 0 : dateValue.hashCode());
            result = prime * result + ((timeValue == null) ? 0 : timeValue.hashCode());
            result = prime * result + ((timestampValue == null) ? 0 : timestampValue.hashCode());
            result = prime * result + ((timestampTZValue == null) ? 0 : timestampTZValue.hashCode());
            result = prime * result + ((timestampLTZValue == null) ? 0 : timestampLTZValue.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;

            if (getClass() != obj.getClass()) return false;

            Local other = (Local) obj;

            if (dateValue == null) {
                if (other.dateValue != null) return false;
            } else if (!dateValue.equals(other.dateValue)) return false;

            if (timeValue == null) {
                if (other.timeValue != null) return false;
            } else if (!timeValue.equals(other.timeValue)) return false;

            if (timestampValue == null) {
                if (other.timestampValue != null) return false;
            } else if (!timestampValue.equals(other.timestampValue)) return false;

            if (timestampTZValue == null) {
                if (other.timestampTZValue != null) return false;
            } else if (!timestampTZValue.equals(other.timestampTZValue)) return false;

            if (timestampLTZValue == null) {
                if (other.timestampLTZValue != null) return false;
            } else if (!timestampLTZValue.equals(other.timestampLTZValue)) return false;

            return true;
        }
    }

    /** Field types are OffsetDateTime. */
    @Table("super")
    public static class Offset extends DateAndTime {
        /** TIMESTAMP */
        @ColumnType(LocalDateTime.class)
        public OffsetDateTime timestampValue;

        /** TIMESTAMP WITH TIME ZONE */
        public OffsetDateTime timestampTZValue;

        /** TIMESTAMP WITH LOCAL TIME ZONE */
        @ColumnType(LocalDateTime.class)
        public OffsetDateTime timestampLTZValue;

        /** for Db2 */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class Db2 extends Offset {
        }

        /** for MariaDB */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class MariaDB extends Offset {
        }

        /** for MySQL */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class MySQL extends Offset {
        }

        /** for Oracle */
        @Table("super")
        public static class Oracle extends Offset {
        }

        /** for PostgreSQL */
        @Table("super")
        @NonColumnProperty(property="timestampLTZValue")
        public static class PostgreSQL extends Offset {
        }

        /** for SQLite */
        @Table("super")
        @NonColumnProperty(property="timestampLTZValue")
        public static class SQLite extends Offset {
        }

        /** for SQLServer */
        @Table("super")
        @NonColumnProperty(property="timestampLTZValue")
        public static class SQLServer extends Offset {
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "DateAndTime.Offset [id=" + id +
                ", timestampValue="    + Utils.toLogString(timestampValue   )+
                ", timestampTZValue="  + Utils.toLogString(timestampTZValue )+
                ", timestampLTZValue=" + Utils.toLogString(timestampLTZValue)
                + "]";
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((timestampValue == null) ? 0 : timestampValue.hashCode());
            result = prime * result + ((timestampTZValue == null) ? 0 : timestampTZValue.hashCode());
            result = prime * result + ((timestampLTZValue == null) ? 0 : timestampLTZValue.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;

            if (getClass() != obj.getClass()) return false;

            Offset other = (Offset) obj;

            if (timestampValue == null) {
                if (other.timestampValue != null) return false;
            } else if (!timestampValue.equals(other.timestampValue)) return false;

            if (timestampTZValue == null) {
                if (other.timestampTZValue != null) return false;
            } else if (!timestampTZValue.equals(other.timestampTZValue)) return false;

            if (timestampLTZValue == null) {
                if (other.timestampLTZValue != null) return false;
            } else if (!timestampLTZValue.equals(other.timestampLTZValue)) return false;

            return true;
        }
    }

    /** Field types are ZonedDateTime. */
    @Table("super")
    public static class Zoned extends DateAndTime {
        /** TIMESTAMP */
        @ColumnType(LocalDateTime.class)
        public ZonedDateTime timestampValue;

        /** TIMESTAMP WITH TIME ZONE */
        public ZonedDateTime timestampTZValue;

        /** TIMESTAMP WITH LOCAL TIME ZONE */
        @ColumnType(LocalDateTime.class)
        public ZonedDateTime timestampLTZValue;

        /** for Db2 */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class Db2 extends Zoned {
        }

        /** for MariaDB */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class MariaDB extends Zoned {
        }

        /** for MySQL */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class MySQL extends Zoned {
        }

        /** for Oracle */
        @Table("super")
        public static class Oracle extends Zoned {
        }

        /** for PostgreSQL */
        @Table("super")
        @NonColumnProperty(property="timestampLTZValue")
        public static class PostgreSQL extends Zoned {
        }

        /** for SQLite */
        @Table("super")
        @NonColumnProperty(property="timestampLTZValue")
        public static class SQLite extends Zoned {
        }

        /** for SQLServer */
        @Table("super")
        @NonColumnProperty(property="timestampLTZValue")
        public static class SQLServer extends Zoned {
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "DateAndTime.Zoned [id=" + id +
                ", timestampValue="    + Utils.toLogString(timestampValue   )+
                ", timestampTZValue="  + Utils.toLogString(timestampTZValue )+
                ", timestampLTZValue=" + Utils.toLogString(timestampLTZValue)
                + "]";
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((timestampValue == null) ? 0 : timestampValue.hashCode());
            result = prime * result + ((timestampTZValue == null) ? 0 : timestampTZValue.hashCode());
            result = prime * result + ((timestampLTZValue == null) ? 0 : timestampLTZValue.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;

            if (getClass() != obj.getClass()) return false;

            Zoned other = (Zoned) obj;

            if (timestampValue == null) {
                if (other.timestampValue != null) return false;
            } else if (!timestampValue.equals(other.timestampValue)) return false;

            if (timestampTZValue == null) {
                if (other.timestampTZValue != null) return false;
            } else if (!timestampTZValue.equals(other.timestampTZValue)) return false;

            if (timestampLTZValue == null) {
                if (other.timestampLTZValue != null) return false;
            } else if (!timestampLTZValue.equals(other.timestampLTZValue)) return false;

            return true;
        }
    }

    /** Field types are java.time.Instant. */
    @Table("super")
    public static class Instant extends DateAndTime {
        /** TIMESTAMP */
        @ColumnType(LocalDateTime.class)
        public java.time.Instant timestampValue;

        /** TIMESTAMP WITH TIME ZONE */
        @ColumnType(OffsetDateTime.class)
        public java.time.Instant timestampTZValue;

        /** TIMESTAMP WITH LOCAL TIME ZONE */
        @ColumnType(LocalDateTime.class)
        public java.time.Instant timestampLTZValue;

        /** for Db2 */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class Db2 extends Instant {
        }

        /** for MariaDB */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class MariaDB extends Instant {
        }

        /** for MySQL */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=LocalDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class MySQL extends Instant {
        }

        /** for Oracle */
        @Table("super")
        public static class Oracle extends Instant {
        }

        /** for PostgreSQL */
        @Table("super")
        @NonColumnProperty(property="timestampLTZValue")
        public static class PostgreSQL extends Instant {
        }

        /** for SQLite */
        @Table("super")
        @NonColumnProperty(property="timestampLTZValue")
        public static class SQLite extends Instant {
        }

        /** for SQLServer */
        @Table("super")
        @ColumnTypeProperty(property="timestampTZValue", type=OffsetDateTime.class)
        @NonColumnProperty(property="timestampLTZValue")
        public static class SQLServer extends Instant {
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "DateAndTime.Instant [id=" + id +
                ", timestampValue="    + Utils.toLogString(timestampValue   )+
                ", timestampTZValue="  + Utils.toLogString(timestampTZValue )+
                ", timestampLTZValue=" + Utils.toLogString(timestampLTZValue)
                + "]";
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((timestampTZValue == null) ? 0 : timestampTZValue.hashCode());
            result = prime * result + ((timestampValue == null) ? 0 : timestampValue.hashCode());
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;

            if (getClass() != obj.getClass()) return false;

            Instant other = (Instant) obj;

            if (timestampTZValue == null) {
                if (other.timestampTZValue != null) return false;
            } else if (!timestampTZValue.equals(other.timestampTZValue)) return false;

            if (timestampValue == null) {
                if (other.timestampValue != null) return false;
            } else if (!timestampValue.equals(other.timestampValue)) return false;

            return true;
        }
    }
}
