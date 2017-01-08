// Expression.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.text.MessageFormat;
import java.util.List;

import org.lightsleep.Sql;
import org.lightsleep.helper.ColumnInfo;
import org.lightsleep.helper.EntityInfo;
import org.lightsleep.helper.Resource;
import org.lightsleep.helper.SqlEntityInfo;
import org.lightsleep.logger.Logger;
import org.lightsleep.logger.LoggerFactory;

/**
 * Configures an expression with a string content and an array of argument objects embedded in the string.
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class Expression implements Condition {
	// The logger
	private static final Logger logger = LoggerFactory.getLogger(Expression.class);

	// Class resources
	private static final Resource resource = new Resource(Expression.class);
	private static final String messageParametersIsLess   = resource.get("messageParametersIsLess");
	private static final String messagePropertyIsNotFound = resource.get("messagePropertyIsNotFound");

	/** The empty expression */
	public static final Expression EMPTY = new Expression("");

	/** The default value expression */
	public static final Expression DEFAULT = new Expression("DEFAULT");

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
	 * @throws NullPointerException <b>content</b> or <b>arguments</b> is <b>null</b>
	 */
	public Expression(String content, Object... arguments) {
		if (content == null) throw new NullPointerException("Expression.<init>: content == null");
		if (arguments == null) throw new NullPointerException("Expression.<init>: arguments == null");

		this.content = content;
		this.arguments = arguments;
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
					if (ch != '}') {
						if (tempBuff.length() == 0) {
							if (Character.isSpaceChar(ch))
								continue;

							if (ch == '#' && !referEntity) {
								referEntity = true;
								continue;
							}
						}

						tempBuff.append(ch);
						continue;
					}

					inBrace = false;
					String propertyName = tempBuff.toString().trim();

					if (propertyName.length() == 0 || referEntity) {
						// Replaces an argument or refer the entity value
						Object value = null;
						if (propertyName.length() == 0) {
							// Replaces an argument
							if (argIndex >= arguments.length) {
								// Argument shortage
								logger.warn(MessageFormat.format(messageParametersIsLess, content, arguments.length));
								buff.append("{***}");
								continue;
							}
							value = arguments[argIndex++];

						} else {
							// Refers the entity value
							if (entity == null)
								throw new NullPointerException("Expression.toString: sql.entity = null, content = " + content);

							value = entityInfo.accessor().getValue(entity, propertyName);
						}

						if (value == null)
							buff.append("NULL");
						else {
							String string = Sql.getDatabase().convert(value, SqlString.class).toString();
							buff.append(string);
							if (string.equals("?"))
								parameters.add(value);
						}
						continue;

					} else {
						ColumnInfo columnInfo = null;

						try {
							// Converts to a column name
							columnInfo = entityInfo.getColumnInfo(propertyName);
							buff.append(columnInfo.getColumnName(sql.tableAlias()));
							continue;
						}
						catch (IllegalArgumentException e) {
						}

						// Try with the table alias
						int chIndex = propertyName.indexOf('.');
						if (chIndex >= 1) {
							String tableAlias = propertyName.substring(0, chIndex);
							SqlEntityInfo<?> sqlEntityInfo = sql.getSqlEntityInfo(tableAlias);
							if (sqlEntityInfo != null) {
								// Found an entity information of the table alias
								String propertyName2 = propertyName.substring(chIndex + 1);

								try {
									columnInfo = sqlEntityInfo.entityInfo().getColumnInfo(propertyName2);
									buff.append(columnInfo.getColumnName(sqlEntityInfo.tableAlias()));
									continue;
								}
								catch (IllegalArgumentException e) {
								}
							}
						}

						//  Try by column alias
						chIndex = propertyName.indexOf('_');
						if (chIndex >= 1) {
							String tableAlias = propertyName.substring(0, chIndex);
							SqlEntityInfo<?> sqlEntityInfo = sql.getSqlEntityInfo(tableAlias);
							if (sqlEntityInfo != null) {
								// Found an entity information of the table alias
								String propertyName2 = propertyName.substring(chIndex + 1);
								try {
									columnInfo = sqlEntityInfo.entityInfo().getColumnInfo(propertyName2);
									buff.append(columnInfo.getColumnAlias(sqlEntityInfo.tableAlias()));
									continue;
								}
								catch (IllegalArgumentException e) {
								}
							}
						}

						// Not found any column information
					// 1.5.1
					//	logger.warn(MessageFormat.format(messagePropertyIsNotFound, propertyName, entityInfo.entityClass().getName()));
						logger.warn(MessageFormat.format(messagePropertyIsNotFound, entityInfo.entityClass().getName(), propertyName));
					////
						buff.append('{').append(propertyName).append('}');
						continue;
					}
				}

				if (ch == '{') {
					// { start
					inBrace = true;
					tempBuff.setLength(0);
					continue;
				}
			}

			buff.append(ch);
		}

		return buff.toString();
	}
}
