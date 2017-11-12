// LoggerFactory.java
// (C) 2016 Masato Kokubo

package org.lightsleep.logger;

/**
 * プロパティファイル (lightsleep.properties) に、<b>logger</b>
 * キーで指定されたロガーオブジェクトを生成します。<br>
 *
 * ロガークラスは、<b>Jdk</b>, <b>Log4j</b>, <b>Log4j2</b>, <b>SLF4J</b> または <b>StdOut</b>
 * のいずれかを指定してください。<br>
 *
 * <div class="exampleTitle"><span>lightsleep.properties 例</span></div>
 * <div class="exampleCode"><pre>
 * logger = Log4j
 * </pre></div>
 *
 * 指定がない場合は、<b>StdOut</b> が選択されます。<br>
 * <br>
 *
 * <b>Jdk</b>, <b>Log4j</b>, <b>Log4j2</b>および<b>SLF4J</b>
 * のログレベルは、次の表のようにマッピングされます。<br>
 *
 * <table class="additional">
 *   <caption><span>ログレベルのマッピング</span></caption>
 *   <tr><th>本クラス</th><th>Jdk    </th><th>Log4j, Log4j2</th><th>SLF4J</th></tr>
 *   <tr><td>trace   </td><td>finest </td><td>trace        </td><td>trace</td></tr>
 *   <tr><td>debug   </td><td>fine   </td><td>debug        </td><td>debug</td></tr>
 *   <tr><td>info    </td><td>info   </td><td>info         </td><td>info </td></tr>
 *   <tr><td>warn    </td><td>warning</td><td>warn         </td><td>warn </td></tr>
 *   <tr><td>error   </td><td>server </td><td>error        </td><td>error</td></tr>
 *   <tr><td>fatal   </td><td>server </td><td>fatal        </td><td>error</td></tr>
 * </table>
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public class LoggerFactory {
	/**
	 * 指定の名前のロガーを返します。
	 *
	 * @param name 名前
	 * @return Logger
	 */
	public static Logger getLogger(String name) {
		return null;
	}

	/**
	 * 指定のクラス名のロガーを返します。
	 *
	 * @param clazz クラス
	 * @return Logger
	 */
	public static Logger getLogger(Class<?> clazz) {
		return null;
	}
}
