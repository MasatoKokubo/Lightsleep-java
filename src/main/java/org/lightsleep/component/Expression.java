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
import org.lightsleep.helper.ColumnInfo;
import org.lightsleep.helper.EntityInfo;
import org.lightsleep.helper.Resource;
import org.lightsleep.helper.SqlEntityInfo;

/**
 * Configures an expression with a string content and an array of argument objects embedded in the string.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class Expression implements Condition {
// 1.8.2
//	// The logger
//	private static final Logger logger = LoggerFactory.getLogger(Expression.class);
////

	// Class resources
	private static final Resource resource = new Resource(Expression.class);
// 1.8.2
//	private static final String messageParametersIsLess   = resource.get("messageParametersIsLess");
	private static final String messageLessArguments      = resource.get("messageLessArguments");
	private static final String messageMoreArguments      = resource.get("messageMoreArguments");
////
	private static final String messagePropertyIsNotFound = resource.get("messagePropertyIsNotFound");
// 1.8.5
	private static final String messagePropertiesAreNotFound = resource.get("messagePropertiesAreNotFound");
////

	/** The empty expression */
	public static final Expression EMPTY = new Expression("");

// 1.8.2 (Not used)
//	/** The default value expression */
//	public static final Expression DEFAULT = new Expression("DEFAULT");
////

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
	//	if (content == null) throw new NullPointerException("Expression.<init>: content == null");
	//	if (arguments == null) throw new NullPointerException("Expression.<init>: arguments == null");
	//
	//	this.content = content;
	//	this.arguments = arguments;
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
	// 1.8.2
	//	return content.isEmpty();
		return this == Condition.EMPTY || content.isEmpty();
	////
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
	 * @throws IllegalArgumentException if the expression arguments are less or more than the placements
	 */
	@Override
	public <E> String toString(Sql<E> sql, List<Object> parameters) {
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
				// 1.8.0
					if (Character.isWhitespace(ch)) continue;
				////
					if (ch != '}') {
						if (tempBuff.length() == 0) {
						// 1.8.0
						//	if (Character.isSpaceChar(ch)) continue;
						////
							if (ch == '#' && !referEntity) {
								referEntity = true;
								continue;
							}
						}

						tempBuff.append(ch);
						continue;
					}

					inBrace = false;
				// 1.8.0
				//	String propertyName = tempBuff.toString().trim();
					String propertyName = tempBuff.toString();
				////

					if (propertyName.length() == 0 || referEntity) {
						// Replaces an argument or refer the entity value
						Object value = null;
						if (propertyName.length() == 0) {
							// Replaces an argument
							if (argIndex >= arguments.length) {
								// Argument shortage
							// 1.8.2
							//	logger.warn(MessageFormat.format(messageParametersIsLess, content, arguments.length));
							//	buff.append("{***}");
							//	continue;
								throw new IllegalArgumentException(MessageFormat.format(
									messageLessArguments, content, arguments.length));
							////
							}
							value = arguments[argIndex++];

						} else {
							// Refers the entity value
						//	if (entity == null)
						//		throw new NullPointerException("Expression.toString: sql.entity == null, content = " + content);
							Objects.requireNonNull(entity, "sql.entity");

							value = entityInfo.accessor().getValue(entity, propertyName);
						// 1.8.0
							// converts value to the column type
							ColumnInfo columnInfo = entityInfo.getColumnInfo(propertyName);
							Class<?> columnType = columnInfo.columnType();
							if (columnType != null)
								value = Sql.getDatabase().convert(value, columnType);
						////
						}

						if (value == null)
							buff.append("NULL");
						else {
						// 1.7.0
						//	String string = Sql.getDatabase().convert(value, SqlString.class).toString();
						//	buff.append(string);
						//	if (string.equals("?"))
						//		parameters.add(value);
							SqlString sqlString = Sql.getDatabase().convert(value, SqlString.class);
							buff.append(sqlString.toString());
							parameters.addAll(Arrays.asList(sqlString.parameters()));
						////
						}
					// 1.8.2
					//	continue;
					////

					} else {
					// 1.8.2
					//	ColumnInfo columnInfo = null;
					//
					//	try {
					//		// Converts to a column name
					//		columnInfo = entityInfo.getColumnInfo(propertyName);
					//		buff.append(columnInfo.getColumnName(sql.tableAlias()));
					//		continue;
					//	}
					//	catch (IllegalArgumentException e) {
					//	}
					//
					//	// Try with the table alias
					//	int chIndex = propertyName.indexOf('.');
					//	if (chIndex >= 1) {
					//		String tableAlias = propertyName.substring(0, chIndex);
					//		SqlEntityInfo<?> sqlEntityInfo = sql.getSqlEntityInfo(tableAlias);
					//		if (sqlEntityInfo != null) {
					//			// Found an entity information of the table alias
					//			String propertyName2 = propertyName.substring(chIndex + 1);
					//
					//			try {
					//				columnInfo = sqlEntityInfo.entityInfo().getColumnInfo(propertyName2);
					//				buff.append(columnInfo.getColumnName(sqlEntityInfo.tableAlias()));
					//				continue;
					//			}
					//			catch (IllegalArgumentException e) {
					//			}
					//		}
					//	}
					//
					//	//  Try by column alias
					//	chIndex = propertyName.indexOf('_');
					//	if (chIndex >= 1) {
					//		String tableAlias = propertyName.substring(0, chIndex);
					//		SqlEntityInfo<?> sqlEntityInfo = sql.getSqlEntityInfo(tableAlias);
					//		if (sqlEntityInfo != null) {
					//			// Found an entity information of the table alias
					//			String propertyName2 = propertyName.substring(chIndex + 1);
					//			try {
					//				columnInfo = sqlEntityInfo.entityInfo().getColumnInfo(propertyName2);
					//				buff.append(columnInfo.getColumnAlias(sqlEntityInfo.tableAlias()));
					//				continue;
					//			}
					//			catch (IllegalArgumentException e) {
					//			}
					//		}
					//	}
					//	// Not found any column information
					// 1.5.1
					////logger.warn(MessageFormat.format(messagePropertyIsNotFound, propertyName, entityInfo.entityClass().getName()));
					//	logger.warn(MessageFormat.format(messagePropertyIsNotFound, entityInfo.entityClass().getName(), propertyName));
						appendsColumnName(buff, sql, entityInfo, propertyName);
					////
					}

				// 1.8.2
					continue;
				////
				}

				if (ch == '{') {
					// { start
					inBrace = true;
				// 1.8.0 #0022
					referEntity = false;
				////
					tempBuff.setLength(0);
					continue;
				}
			}

			buff.append(ch);
		}

	// 1.8.2
		if (argIndex < arguments.length)
			throw new IllegalArgumentException(MessageFormat.format(
				messageMoreArguments, content, arguments.length));
	////

		return buff.toString();
	}

// 1.8.2
	private static char[] delimiterChars = {'.', '_'};

	// Appends a column name
	private <E> void appendsColumnName(StringBuilder buff, Sql<E> sql, EntityInfo<E> entityInfo, String propertyName) {
	// 1.8.5
		List<String> propertyNames = new ArrayList<>();
	////
		try {
			// Converts to a column name
			ColumnInfo columnInfo = entityInfo.getColumnInfo(propertyName);
			buff.append(columnInfo.getColumnName(sql.tableAlias()));
			return;
		}
		catch (IllegalArgumentException e) {
		// 1.8.5
			propertyNames.add(propertyName);
		////
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
					// 1.8.5
						propertyNames.add(propertyName2);
					////
					}
				}
			}
		}

		propertyNames = propertyNames.stream().map(name -> '"' + name + '"').collect(Collectors.toList());
		throw new IllegalArgumentException(MessageFormat.format(
		// 1.8.5
		//	messagePropertyIsNotFound, entityInfo.entityClass().getName(), propertyName));
			propertyNames.size() == 1 ? messagePropertyIsNotFound : messagePropertiesAreNotFound,
			entityInfo.entityClass().getName(), String.join(", ", propertyNames)));
		////
	}
////
}
