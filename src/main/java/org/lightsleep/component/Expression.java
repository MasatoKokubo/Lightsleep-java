// Expression.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.lightsleep.Sql;
import org.lightsleep.database.Database;
import org.lightsleep.helper.ColumnInfo;
import org.lightsleep.helper.EntityInfo;
import org.lightsleep.helper.MissingPropertyException;
import org.lightsleep.helper.Resource;
import org.lightsleep.helper.SqlEntityInfo;

/**
 * Configures an expression with a string content and an array of argument objects embedded in the string.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class Expression implements Condition {
	// Class resources
	private static final Resource resource = new Resource(Expression.class);
	private static final String messageLessArguments     = resource.getString("messageLessArguments");
	private static final String messageMoreArguments     = resource.getString("messageMoreArguments");
	private static final String messageMissingProperty   = resource.getString("messageMissingProperty");
	private static final String messageMissingProperties = resource.getString("messageMissingProperties");

	/** The empty expression */
	public static final Expression EMPTY = new Expression("");

	// The content
	private final String content;

	// The arguments
	private final Object[] arguments;

	/**
	 * Constructs a new <b>Expression</b>.
	 *
	 * @param content the content of the expression
	 * @param arguments the arguments of the expression
	 *
	 * @throws NullPointerException <b>content</b> or <b>arguments</b> is null
	 */
	public Expression(String content, Object... arguments) {
		this.content = Objects.requireNonNull(content, "content");
		this.arguments = Objects.requireNonNull(arguments, "arguments");
	}

	/**
	 * Returns the content of the expression.
	 *
	 * @return the content
	 */
	public String content() {
		return content;
	}

	/**
	 * Returns the arguments.
	 *
	 * @return the arguments
	 */
	public Object[] arguments() {
		return arguments;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return content.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 *
	 * <table class="additional">
	 *   <caption><span>Conversion Process</span></caption>
	 *   <tr>
	 *     <th>String before convert</th>
	 *     <th>String after convert</th>
	 *   </tr>
	 *   <tr>
	 *     <td>{}</td>
	 *     <td>An element of <b>arguments</b></td>
	 *   </tr>
	 *   <tr>
	 *     <td>{<i>Property Name</i>}</td>
	 *     <td>Column Name</td>
	 *   </tr>
	 *   <tr>
	 *     <td>{<i>Table Alias</i>.<i>Property Name</i>}</td>
	 *     <td>Table Alias.Column Name</td>
	 *   </tr>
	 *   <tr>
	 *     <td>{<i>Table Alias</i>_<i>Property Name</i>}</td>
	 *     <td>Column Alias</td>
	 *   </tr>
	 *   <tr>
	 *     <td>{#<i>Property Name</i>}</td>
	 *     <td>Property Value of the entity</td>
	 *   </tr>
	 * </table>
	 *
	 * @throws MissingArgumentsException if the number of arguments does not match the number of placements in the expression
	 * @throws MissingPropertyException if a property that does not exist in the expression is referenced
	 */
	@Override
// 2.1.0
//	public <E> String toString(Sql<E> sql, List<Object> parameters) {
	public <E> String toString(Database database, Sql<E> sql, List<Object> parameters) {
		Objects.requireNonNull(database, "database");
		Objects.requireNonNull(sql, "sql");
		Objects.requireNonNull(parameters, "parameters");
////
		EntityInfo<E> entityInfo = sql.entityInfo();
		E entity = sql.entity();
		StringBuilder buff = new StringBuilder(content.length());
		StringBuilder tempBuff = new StringBuilder();
		boolean inBrace = false;
		boolean escaped = false;
		boolean referEntity = false;
		int argIndex = 0;
		for (int index = 0; index < content.length(); ++index) {
			char ch = content.charAt(index);

			if (escaped) {
				// In escaping
				escaped = false;

			} else {
				// Not in escaping
				if (ch == '\\') {
					// Escape character
					escaped = true;
					continue;
				}

				if (inBrace) {
					// in {}
					if (Character.isWhitespace(ch)) continue;
					if (ch != '}') {
						if (tempBuff.length() == 0) {
							if (ch == '#' && !referEntity) {
								referEntity = true;
								continue;
							}
						}

						tempBuff.append(ch);
						continue;
					}

					inBrace = false;
					String propertyName = tempBuff.toString();

					if (propertyName.length() == 0 || referEntity) {
						// Replaces an argument or refer the entity value
						Object value = null;
						if (propertyName.length() == 0) {
							// Replaces an argument
							if (argIndex >= arguments.length) {
								// Argument shortage
							// 2.0.0
							//	throw new IllegalArgumentException(MessageFormat.format(
								throw new MissingArgumentsException(MessageFormat.format(
							////
									messageLessArguments, content, arguments.length));
							}
							value = arguments[argIndex++];

						} else {
							// Refers the entity value
							Objects.requireNonNull(entity, "sql.entity");

							value = entityInfo.accessor().getValue(entity, propertyName);
							ColumnInfo columnInfo = entityInfo.getColumnInfo(propertyName);
							Class<?> columnType = columnInfo.columnType();
							if (columnType != null)
							// 2.1.0
							//	value = Sql.getDatabase().convert(value, columnType);
								value = database.convert(value, columnType);
							////
						}

						if (value == null)
							buff.append("NULL");
						else {
						// 2.1.0
						//	SqlString sqlString = Sql.getDatabase().convert(value, SqlString.class);
							SqlString sqlString = database.convert(value, SqlString.class);
						////
							buff.append(sqlString.toString());
							parameters.addAll(Arrays.asList(sqlString.parameters()));
						}
					} else {
						appendsColumnName(buff, sql, entityInfo, propertyName);
					}

					continue;
				}

				if (ch == '{') {
					// { start
					inBrace = true;
					referEntity = false;
					tempBuff.setLength(0);
					continue;
				}
			}

			buff.append(ch);
		}

		if (argIndex < arguments.length)
		// 2.0.0
		//	throw new IllegalArgumentException(MessageFormat.format(
			throw new MissingArgumentsException(MessageFormat.format(
		////
				messageMoreArguments, content, arguments.length));

		return buff.toString();
	}

	private static char[] delimiterChars = {'.', '_'};

	// Appends a column name
	private <E> void appendsColumnName(StringBuilder buff, Sql<E> sql, EntityInfo<E> entityInfo, String propertyName) {
		List<String> propertyNames = new ArrayList<>();
		try {
			// Converts to a column name
			ColumnInfo columnInfo = entityInfo.getColumnInfo(propertyName);
			buff.append(columnInfo.getColumnName(sql.tableAlias()));
			return;
		}
		catch (IllegalArgumentException e) {
			propertyNames.add(propertyName);
		}

		// Try with the table alias and column alias
		for (char delimiterChar : delimiterChars) {
			int chIndex = propertyName.indexOf(delimiterChar);
			if (chIndex >= 1) {
				String tableAlias = propertyName.substring(0, chIndex);
				SqlEntityInfo<?> sqlEntityInfo = sql.getSqlEntityInfo(tableAlias);
				if (sqlEntityInfo != null) {
					// Found an entity information with the table alias or column alias
					String propertyName2 = propertyName.substring(chIndex + 1);

					try {
						ColumnInfo columnInfo = sqlEntityInfo.entityInfo().getColumnInfo(propertyName2);
						if (delimiterChar == '.')
							buff.append(columnInfo.getColumnName(sqlEntityInfo.tableAlias()));
						else
							buff.append(columnInfo.getColumnAlias(sqlEntityInfo.tableAlias()));
						return;
					}
					catch (IllegalArgumentException e) {
						propertyNames.add(propertyName2);
					}
				}
			}
		}

		propertyNames = propertyNames.stream().map(name -> '"' + name + '"').collect(Collectors.toList());
	// 2.0.0
	//	throw new IllegalArgumentException(MessageFormat.format(
	//		propertyNames.size() == 1 ? messagePropertyIsNotFound : messagePropertiesAreNotFound,
	//		entityInfo.entityClass().getName(), String.join(", ", propertyNames)));
		throw new MissingPropertyException(propertyNames.size() == 1
			? MessageFormat.format(messageMissingProperty,
				entityInfo.entityClass().getName(), propertyNames.get(0))
			: MessageFormat.format(messageMissingProperties,
				entityInfo.entityClass().getName(), '[' + String.join(", ", propertyNames) + ']')
		);
	////
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.9.1
	 */
	@Override
	public int hashCode() {
		return 31 * content.hashCode() + Arrays.hashCode(arguments);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.9.1
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Expression other = (Expression)obj;
		if (!content.equals(other.content)) return false;
		if (!Arrays.equals(arguments, other.arguments)) return false;
		return true;
	}
}
