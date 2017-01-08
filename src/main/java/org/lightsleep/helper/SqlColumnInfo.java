// SqlColumnInfo.java
// (C) 2016 Masato Kokubo

package org.lightsleep.helper;

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
	 *
	 * @throws NullPointerException <b>tableAlias</b> or <b>columnInfo</b> is <b>null</b>
	 */
	public SqlColumnInfo(String tableAlias, ColumnInfo columnInfo) {
		this.tableAlias = tableAlias;
		this.columnInfo = columnInfo;
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
}
