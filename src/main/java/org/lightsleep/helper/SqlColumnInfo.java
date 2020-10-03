// SqlColumnInfo.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

import java.util.Objects;

/**
 * Has a table alias and a column information.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class SqlColumnInfo {
    // The table alias
    private final String tableAlias;

    // The column information
    private final ColumnInfo columnInfo;

    /**
     * Constructs a new <b>SqlColumnInfo</b>.
     *
     * @param tableAlias the table alias
     * @param columnInfo the column information
     */
    public SqlColumnInfo(String tableAlias, ColumnInfo columnInfo) {
        this.tableAlias = Objects.requireNonNull(tableAlias, "tableAlias is null");
        this.columnInfo = Objects.requireNonNull(columnInfo, "columnInfo is null");
    }

    /**
     * Returns the table alias.
     *
     * @return the table alias
     */
    public String tableAlias() {
        return tableAlias;
    }

    /**
     * Returns the column information.
     *
     * @return the column information
     */
    public ColumnInfo columnInfo() {
        return columnInfo;
    }

    /**
     * Returns whether <b>name</b> matches the table alias and the property name of this column information.<br>
     *
     * If it contains <b>'.'</b> in the name,
     * the left side of the '.' is compared with the table alias'
     * and the right side of the '.' is compared with the property name.<br>
     * Otherwise all of the name is compared with the property name.<br>
     *
     * @param name the name
     * @return <b>true</b> if matches, otherwose <b>false</b>
     *
     * @throws NullPointerException <b>name</b> is <b>null</b>
     */
    public boolean matches(String name) {
        boolean result = false;

        if (name.startsWith(tableAlias + '.'))
            name = name.substring(tableAlias.length() + 1);

        result = name.equals("*") || columnInfo.propertyName().equals(name);

        return result;
    }

    /**
     * @since 4.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(tableAlias, columnInfo);
    }

    /**
     * @since 4.0.0
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        if (getClass() != object.getClass()) return false;
        return
            tableAlias.equals(((SqlColumnInfo)object).tableAlias) &&
            columnInfo.equals(((SqlColumnInfo)object).columnInfo);
    }
}
