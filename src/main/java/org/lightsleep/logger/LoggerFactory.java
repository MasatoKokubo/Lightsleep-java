// LoggerFactory.java
// (C) 2016 Masato Kokubo

package org.lightsleep.logger;

import java.util.LinkedHashMap;
import java.util.Map;

import org.lightsleep.helper.Resource;

/**
 * Generate a logger object that is specified in the properties file (lightsleep.properties)
 * with <b>logger</b> key.
 *
 * Specify either of <b>Jdk</b>, <b>Log4j</b>, <b>Log4j2</b>, <b>SLF4J</b> or <b>StdOut</b>
 * as the logger class.<br>
 * 
 * <div class="sampleTitle"><span>Example of lightsleep.properties</span></div>
 * <div class="sampleCode"><pre>
 * logger = Log4j
 * </pre></div>
 * 
 * If not specified, will be selected <b>StdOut</b>.
 * <br>

 * The log level of
 * <b>Jdk</b>, <b>Log4j</b>, <b>Log4j2</b> and <b>SLF4J</b>
 * are mapped as shown in the following table.
 *
 * <table class="additional">
 *   <caption><span>Mappings of Log Levels</span></caption>
 *   <tr>
 *     <th>This Class</th>
 *     <th>Jdk</th>
 *     <th>Log4j, Log4j2</th>
 *     <th>SLF4J</th>
 *   </tr>
 *   <tr>
 *     <td>trace </td>
 *     <td>finest</td>
 *     <td>trace </td>
 *     <td>trace </td>
 *   </tr>
 *   <tr>
 *     <td>debug</td>
 *     <td>fine </td>
 *     <td>debug</td>
 *     <td>debug </td>
 *   </tr>
 *   <tr>
 *     <td>info</td>
 *     <td>info</td>
 *     <td>info</td>
 *     <td>info</td>
 *   </tr>
 *   <tr>
 *     <td>warn   </td>
 *     <td>warning</td>
 *     <td>warn   </td>
 *     <td>warn   </td>
 *   </tr>
 *   <tr>
 *     <td>error </td>
 *     <td>server</td>
 *     <td>error </td>
 *     <td>error </td>
 *   </tr>
 *   <tr>
 *     <td>fatal </td>
 *     <td>server</td>
 *     <td>fatal </td>
 *     <td>error </td>
 *   </tr>
 * </table>

 * @since 1.0.0
 * @author Masato Kokubo
 */
@SuppressWarnings("unchecked")
public class LoggerFactory {
	// The logger class
	public static Class<? extends Logger> loggerClass;

	// The logger map
	private static final Map<String, Logger> loggerMap = new LinkedHashMap<>();

	static {
		String loggerName = null;
		Logger logger = null;

		Resource globalResource = new Resource(System.getProperty("lightsleep.resource", "lightsleep"));
	// 1.2.0
	//	loggerName = globalResource.get("Logger", null);
		loggerName = globalResource.get(Logger.class.getSimpleName(), null);
	////
		if (loggerName != null) {
			if (loggerName.indexOf('.') < 0)
				loggerName = Logger.class.getPackage().getName() + '.' + loggerName;

			// Checks whether there is a Logger class that is specified in the property
			try {
				loggerClass = (Class<? extends Logger>)Class.forName(loggerName);
				logger = getLogger(loggerClass, LoggerFactory.class);
			}
			catch (Exception e) {
			}
		}

		if (logger == null) {
			loggerClass = Std.Out.Info.class;

			try {
				logger = getLogger(loggerClass, LoggerFactory.class);
			}
			catch (Exception e) {
				System.out.println(loggerClass.getName() + ": " + e.getMessage());
			}
		}
	}

	// 
	private static Logger getLogger(Class<? extends Logger> loggerClass, String name) throws Exception {
		Logger logger = loggerClass.getConstructor(String.class).newInstance(name);
		return logger;
	}

	// 
	private static Logger getLogger(Class<? extends Logger> loggerClass, Class<?> clazz) throws Exception {
		Logger logger = getLogger(loggerClass, clazz.getName());
		return logger;
	}

	/**
	 * Returns a Logger of the specified name.
	 *
	 * @param name a name
	 *
	 * @return a logger
	 */
	public static Logger getLogger(String name) {
		Logger logger = loggerMap.get(name);
		if (logger == null) {
			try {
				logger = getLogger(loggerClass, name);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			loggerMap.put(name, logger);
		}
		return logger;
	}

	/**
	 * Returns a Logger of the name of the specified class.
	 *
	 * @param clazz a class 
	 *
	 * @return a logger
	 */
	public static Logger getLogger(Class<?> clazz) {
		Logger logger = getLogger(clazz.getName());
		return logger;
	}
}
