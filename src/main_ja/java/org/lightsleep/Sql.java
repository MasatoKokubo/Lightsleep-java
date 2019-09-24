// Sql.java
// (C) 2016 Masato Kokubo

package org.lightsleep;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.Optional;

import org.lightsleep.component.*;
import org.lightsleep.connection.*;
import org.lightsleep.helper.*;

/**
 * SQLを構築および実行するためのクラスです。<br>
 *
 * <div class="exampleTitle"><span>使用例/Java</span></div>
 * <div class="exampleCode"><pre>
 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
 * Transaction.execute(conn -&gt; {
 *     new <b>Sql</b>&lt;&gt;(Contact.class)
 *         <b>.where</b>("{lastName}={}", "Apple")
 *         <b>.connection</b>(conn)
 *         <b>.select</b>(contacts::add);
 * });
 * </pre></div>
 *
 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
 * <div class="exampleCode"><pre>
 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
 * List&lt;Contact&gt; contacts = []
 * Transaction.execute {
 *     new <b>Sql</b>&lt;&gt;(Contact)
 *         <b>.where</b>('{lastName}={}', 'Apple')
 *         <b>.connection</b>(it)
 *         <b>.select</b>({contacts &lt;&lt; it})
 * }
 * </pre></div>
 *
 * <div class="exampleTitle"><span>生成されるSQL</span></div>
 * <div class="exampleCode"><pre>
 * SELECT id, lastName, firstName, ... FROM Contact WHERE lastName='Apple'
 * </pre></div>
 *
 * @param <E> メイン･テーブルのエンティティの型
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class Sql<E> implements Cloneable, SqlEntityInfo<E> {
	/**
	 * 永遠に待つ wait 値
	 * @since 1.9.0
	 */
	public static final int FOREVER = Integer.MAX_VALUE;

	/**
	 * 指定のエンティティ･クラスのエンティティ情報を返します。
	 *
	 * @param <E> エンティティの型
	 * @param entityClass エンティティ･クラス
	 * @return エンティティ情報
	 *
	 * @throws NullPointerException <b>entityClass</b>が<b>null</b>の場合
	 *
	 * @see #entityInfo()
	 */
	public static <E> EntityInfo<E> getEntityInfo(Class<E> entityClass) {
		return null;
	}

	/**
	 * <b>Sql</b>を構築します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class)
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact)
	 * </pre></div>
	 *
	 * @param entityClass エンティティ･クラス
	 *
	 * @throws NullPointerException <b>entityClass</b>が<b>null</b>の場合
	 */
	public Sql(Class<E> entityClass) {
	}

	/**
	 * <b>Sql</b>を構築します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 *     new Sql&lt;&gt;(Contact, 'C')
	 * </pre></div>
	 *
	 * @param entityClass エンティティ･クラス
	 * @param tableAlias テーブルの別名
	 *
	 * @throws NullPointerException <b>entityClass</b>または<b>tableAlias</b>が<b>null</b>の場合
	 */
	public Sql(Class<E> entityClass, String tableAlias) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Sql<E> clone() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see #getEntityInfo(Class)
	 */
	@Override
	public EntityInfo<E> entityInfo() {
		return null;
	}

	/**
	 * エンティティ･クラスを返します。
	 *
	 * @return エンティティ･クラス
	 */
	public Class<E> entityClass() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see #Sql(Class, String)
	 */
	@Override
	public String tableAlias() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see #setEntity(Object)
	 */
	@Override
	public E entity() {
		return null;
	}

	/**
	 * 式から参照されるエンティティを設定します。
	 *
	 * @param entity エンティティ
	 *
	 * @return このオブジェクト
	 *
	 * @see #entity()
	 */
	public Sql<E> setEntity(E entity) {
		return null;
	}

	/**
	 * SELECT SQLに<b>DISTINCT</b>を追加する事を指定します。
	 *
	 * @return このオブジェクト
	 *
	 * @see #isDistinct()
	 */
	public Sql<E> distinct() {
		return null;
	}

	/**
	 * SELECT SQLに<b>DISTINCT</b>が追加されるかどうかを返します。
	 *
	 * @return 追加されるなら<b>true</b>、そうでなければ<b>false</b>
	 *
	 * @see #distinct()
	 */
	public boolean isDistinct() {
		return false;
	}

	/**
	 * 生成されるSELECT SQLおよびUPDATE SQLの対象カラムを指定します。
	 *
	 * <p>
	 * <b>from</b>, <b>union</b>および<b>unionAll</b>メソッドで設定された<b>Sql</b>オブジェクトに対しても未設定であれば設定します。
	 * </p>
	 *
	 * <p>
	 * <b>"*"</b>または<b>"<i>テーブル別名</i>.*"</b>で指定する事もできます。
	 * このメソッドがコールされない場合は、<b>"*"</b>が指定されたのと同様になります。
	 * </p>
	 *
	 * <div class="exampleTitle"><span>使用例1 / Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         <b>.columns("lastName", "firstName")</b>
	 *         .where("{lastName}={}", "Apple")
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例1 / Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         <b>.columns('lastName', 'firstName')</b>
	 *         .where('{lastName}={}', 'Apple')
	 *         .connection(it)
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例2 / Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * List&lt;Phone&gt; phones = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
	 *         <b>.columns("C.id", "P.*")</b>
	 *         .connection(conn)
	 *         .&lt;Phone&gt;select(contacts::add, phones::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例2 / Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * List&lt;Phone&gt; phones = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         .innerJoin(Phone, 'P', '{P.contactId}={C.id}')
	 *         <b>.columns('C.id', 'P.*')</b>
	 *         .connection(it)
	 *         .&lt;Phone&gt;select({contacts &lt;&lt; it}, {phones &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param propertyNames カラムに関連するプロパティ名の配列
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>propertyNames</b>または<b>propertyNames</b>の要素いずれかが<b>null</b> の場合
	 *
	 * @see #columns(Collection)
	 * @see #getColumns()
	 * @see #setColumns(Set)
	 * @see #setColumns(Class)
	 */
	public Sql<E> columns(String... propertyNames) {
		return null;
	}

	/**
	 * 生成されるSELECT SQLおよびUPDATE SQLの対象カラムを指定します。
	 *
	 * <p>
	 * <b>from</b>, <b>union</b>および<b>unionAll</b>メソッドで設定された<b>Sql</b>オブジェクトに対しても未設定であれば設定します。
	 * </p>
	 *
	 * <p>
	 * <b>"*"</b>または<b>"<i>テーブル別名</i>.*"</b>で指定する事もできます。
	 * このメソッドがコールされない場合は、<b>"*"</b>が指定されたのと同様になります。
	 * </p>
	 *
	 * @param propertyNames カラムに関連するプロパティ名のコレクション
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>propertyNames</b>または<b>propertyNames</b>の要素いずれかが<b>null</b> の場合
	 *
	 * @since 3.1.0
	 * @see #columns(String...)
	 * @see #getColumns()
	 * @see #setColumns(Set)
	 * @see #setColumns(Class)
	 */
	public Sql<E> columns(Collection<String> propertyNames) {
		return null;
	}

	/**
	 * 生成されるSELECT SQLおよびUPDATE SQLの対象カラムを指定します。
	 *
	 * @param <RE>結果エンティティの型
	 * @param resultClass プロパティ名のセットを含むエンティティクラス
	 * @return このオブジェクト
	 *
	 * @since 3.1.0
	 * @see #columns(String...)
	 * @see #columns(Collection)
	 * @see #getColumns()
	 */
	public <RE> Sql<E> columns(Class<RE> resultClass) {
		return null;
	}

	/**
	 * 生成されるSELECT SQLおよびUPDATE SQLのカラムに関連するプロパティ名のセットを返します。
	 *
	 * @return メソッドで指定されたプロパティ名のセット
	 *
	 * @see #columns(String...)
	 * @see #columns(Collection)
	 * @see #setColumns(Set)
	 * @see #setColumns(Class)
	 */
	public Set<String> getColumns() {
		return null;
	}

	/**
	 * 生成される SELECT SQLおよびUPDATE SQLのカラムに関連するプロパティ名のセットを指定します。
	 *
	 * <p>
	 * @deprecated リリース 3.1.0 より。
	 * 代わりに{@link #columns(Collection)}を使用してください。
	 * </p>
	 *
	 * @param propertyNames プロパティ名のセット
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>columns</b>が<b>null</b>の場合
	 *
	 * @since 1.8.4
	 * @see #columns(String...)
	 * @see #columns(Collection)
	 * @see #getColumns()
	 * @see #setColumns(Class)
	 */
	@Deprecated
	public Sql<E> setColumns(Set<String> propertyNames) {
		return null;
	}

	/**
	 * 指定のクラスに含まれるプロパティ名のセットを指定します。
	 *
	 * <p>
	 * @deprecated リリース 3.1.0 より。
	 * 代わりに{@link #columns(Class)}を使用してください。
	 * </p>
	 *
	 * @param <RE>結果エンティティの型
	 * @param resultClass プロパティ名のセットを含むエンティティクラス
	 * @return このオブジェクト
	 *
	 * @since 2.0.0
	 * @see #columns(String...)
	 * @see #columns(Collection)
	 * @see #getColumns()
	 * @see #setColumns(Set)
	 */
	@Deprecated
	public <RE> Sql<E> setColumns(Class<RE> resultClass) {
		return null;
	}

	/**
	 * 指定のプロパティ名に関連するカラムに式を関係付けします。
	 *
	 * <p>
	 * 式が空の場合、以前のこのプロパティ名の関連付けを解除します。
	 * </p>
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         <b>.expression("firstName", "'['||{firstName}||']'")</b>
	 *         .where("{lastName}={}", "Orange")
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         <b>.expression('firstName', "'['||{firstName}||']'")</b>
	 *         .where('{lastName}={}', 'Orange')
	 *         .connection(it)
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param propertyName プロパティ名
	 * @param expression 式
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>propertyName</b>または<b>expression</b>が<b>null</b>の場合
	 *
	 * @see #getExpression(String)
	 */
	public Sql<E> expression(String propertyName, Expression expression) {
		return null;
	}

	/**
	 * プロパティ名に関連するカラムに式を関連付けします。
	 *
	 * <p>
	 * 式が空の場合、以前のこのプロパティ名の関連付けを解除します。
	 * </p>
	 *
	 * @param propertyName プロパティ名
	 * @param content 式の内容
	 * @param arguments 式の引数
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>propertyName</b>, <b>content</b>または<b>arguments</b>が<b>null</b>の場合
	 *
	 * @see #getExpression(String)
	 * @see Expression#Expression(String, Object...)
	 */
	public Sql<E> expression(String propertyName, String content, Object... arguments) {
		return null;
	}

	/**
	 * プロパティ名に関連する式を返します。
	 *
	 * <p>
	 * 関連する式がない場合は、<b>Expression.EMPTY</b>を返します。
	 * </p>
	 *
	 * @param propertyName プロパティ名
	 * @return プロパティ名に関連する式または<b>Expression.EMPTY</b>
	 *
	 * @throws NullPointerException <b>propertyName</b>が<b>null</b>の場合
	 *
	 * @see #expression(String, Expression)
	 * @see #expression(String, String, Object...)
	 */
	public Expression getExpression(String propertyName) {
		return null;
	}

	/**
	 * SELECT SQLのFROM句をサブクエリで指定します。
	 *
	 * @param fromSql FROM句を生成するための<b>Sql</b> オブジェクト
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>fromSql</b>が<b>null</b>の場合
	 *
	 * @since 3.1.0
	 */
	public Sql<E> from(Sql<?> fromSql) {
		return null;
	}

	/**
	 * SELECT SQLのFROM句を生成するための<b>Sql</b>オブジェクトを返します。
	 * 指定されていなければ<b>null</b>を返します。
	 *
	 * @return SELECT SQLのFROM句のサブクエリまたは<b>null</b>
	 *
	 * @since 3.1.0
	 */
	public Sql<?> getFrom() {
		return null;
	}

	/**
	 * <b>INNER JOIN</b>で結合するテーブルの情報を追加します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * List&lt;Phone&gt; phones = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         <b>.innerJoin(Phone.class, "P", "{P.contactId}={C.id}")</b>
	 *         .connection(conn)
	 *         .&lt;Phone&gt;select(contacts::add, phones::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * List&lt;Phone&gt; phones = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         <b>.innerJoin(Phone, 'P', '{P.contactId}={C.id}')</b>
	 *         .connection(it)
	 *         .&lt;Phone&gt;select({contacts &lt;&lt; it}, {phones &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param <JE> 結合するエンティティの型
	 * @param entityClass 結合するエンティティ･クラス
	 * @param tableAlias 結合するテーブルの別名
	 * @param on 結合条件式
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>entityClass</b>, <b>tableAlias</b>または<b>on</b>が<b>null</b>の場合
	 *
	 * @see #getJoinInfos()
	 * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
	 */
	public <JE> Sql<E> innerJoin(Class<JE> entityClass, String tableAlias, Condition on) {
		return null;
	}

	/**
	 * <b>INNER JOIN</b>で結合するテーブルの情報を追加します。
	 *
	 * @param <JE> 結合するエンティティの型
	 * @param entityClass 結合するエンティティ･クラス
	 * @param tableAlias 結合するテーブルの別名
	 * @param on 結合条件式
	 * @param arguments 結合条件式の引数
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>entityClass</b>, <b>tableAlias</b>, <b>on</b>または<b>arguments</b>が<b>null</b>の場合
	 *
	 * @see #getJoinInfos()
	 * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
	 */
	public <JE> Sql<E> innerJoin(Class<JE> entityClass, String tableAlias, String on, Object... arguments) {
		return null;
	}

	/**
	 * <b>LEFT OUTER JOIN</b>で結合するテーブルの情報を追加します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * List&lt;Phone&gt; phones = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         <b>.leftJoin(Phone.class, "P", "{P.contactId}={C.id}")</b>
	 *         .connection(conn)
	 *         .&lt;Phone&gt;select(contacts::add, phones::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * List&lt;Phone&gt; phones = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         <b>.leftJoin(Phone, 'P', '{P.contactId}={C.id}')</b>
	 *         .connection(it)
	 *         .&lt;Phone&gt;select({contacts &lt;&lt; it}, {phones &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param <JE> 結合するエンティティの型
	 * @param entityClass 結合するエンティティ･クラス
	 * @param tableAlias 結合するテーブルの別名
	 * @param on 結合条件
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>entityClass</b>, <b>tableAlias</b>, <b>on</b>または<b>arguments</b>が<b>null</b>の場合
	 *
	 * @see #getJoinInfos()
	 * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
	 */
	public <JE> Sql<E> leftJoin(Class<JE> entityClass, String tableAlias, Condition on) {
		return null;
	}

	/**
	 * <b>LEFT OUTER JOIN</b>で結合するテーブルの情報を追加します。
	 *
	 * @param <JE> 結合するエンティティの型
	 *
	 * @param entityClass 結合するエンティティ･クラス
	 * @param tableAlias 結合するテーブルの別名
	 * @param on 結合条件式
	 * @param arguments 結合条件式の引数
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>entityClass</b>, <b>tableAlias</b>, <b>on</b>または<b>arguments</b>が<b>null</b>の場合
	 *
	 * @see #getJoinInfos()
	 * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
	 */
	public <JE> Sql<E> leftJoin(Class<JE> entityClass, String tableAlias, String on, Object... arguments) {
		return null;
	}

	/**
	 * <b>RIGHT OUTER JOIN</b>で結合するテーブルの情報を追加します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * List&lt;Phone&gt; phones = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         <b>.rightJoin(Phone.class, "P", "{P.contactId}={C.id}")</b>
	 *         .connection(conn)
	 *         .&lt;Phone&gt;select(contacts::add, phones::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * List&lt;Phone&gt; phones = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         <b>.rightJoin(Phone, 'P', '{P.contactId}={C.id}')</b>
	 *         .connection(it)
	 *         .&lt;Phone&gt;select({contacts &lt;&lt; it}, {phones &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param <JE> 結合するエンティティの型
	 * @param entityClass 結合するエンティティ･クラス
	 * @param tableAlias 結合するテーブルの別名
	 * @param on 結合条件
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>entityClass</b>, <b>tableAlias</b>, <b>on</b>または<b>arguments</b>が<b>null</b>の場合
	 *
	 * @see #getJoinInfos()
	 * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
	 */
	public <JE> Sql<E> rightJoin(Class<JE> entityClass, String tableAlias, Condition on) {
		return null;
	}

	/**
	 * <b>RIGHT OUTER JOIN</b>で結合するテーブルの情報を追加します。
	 *
	 * @param <JE> 結合するエンティティの型
	 * @param entityClass 結合するエンティティ･クラス
	 * @param tableAlias 結合するテーブルの別名
	 * @param on 結合条件式
	 * @param arguments 結合条件式の引数
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>entityClass</b>, <b>tableAlias</b>, <b>on</b>または<b>arguments</b>が<b>null</b>の場合
	 *
	 * @see #getJoinInfos()
	 * @see JoinInfo#JoinInfo(JoinInfo.JoinType, EntityInfo, String, Condition)
	 */
	public <JE> Sql<E> rightJoin(Class<JE> entityClass, String tableAlias, String on, Object... arguments) {
		return null;
	}

	/**
	 * 追加された結合情報のリストを返します。
	 *
	 * @return JOIN 情報リスト
	 *
	 * @see #innerJoin(Class, String, Condition)
	 * @see #innerJoin(Class, String, String, Object...)
	 * @see #leftJoin(Class, String, Condition)
	 * @see #leftJoin(Class, String, String, Object...)
	 * @see #rightJoin(Class, String, Condition)
	 * @see #rightJoin(Class, String, String, Object...)
	 */
	public List<JoinInfo<?>> getJoinInfos() {
		return null;
	}

	/**
	 * <b>WHERE</b>句の条件を指定します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         <b>.where("{birthday} IS NULL")</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         <b>.where('{birthday} IS NULL')</b>
	 *         .connection(it)
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param condition 条件
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>condition</b>が<b>null</b>の場合
	 *
	 * @see #getWhere()
	 */
	public Sql<E> where(Condition condition) {
		return null;
	}

	/**
	 * <b>WHERE</b>句の条件を指定します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * int id = 1;
	 * Contact[] contact = new Contact[1];
	 * Transaction.execute(conn -&gt; {
	 *     contact[0] = new Sql&lt;&gt;(Contact.class)
	 *         <b>.where("{id}={}", id)</b>
	 *         .connection(conn).select().orElse(null);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * int id = 1
	 * Contact contact
	 * Transaction.execute {
	 *     contact = new Sql&lt;&gt;(Contact)
	 *         <b>.where('{id}={}', id)</b>
	 *         .connection(it)
	 *         .select().orElse(null)
	 * }
	 * </pre></div>
	 *
	 * @param content 式の内容
	 * @param arguments 式の引数
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>content</b>または<b>arguments</b>が<b>null</b>の場合
	 *
	 * @see #getWhere()
	 * @see Condition#of(String, Object...)
	 */
	public Sql<E> where(String content, Object... arguments) {
		return null;
	}

	/**
	 * <b>WHERE</b>句の条件をエンティティ条件で指定します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * Contact[] contact = new Contact[1];
	 * Transaction.execute(conn -&gt; {
	 *     contact[0] = new Sql&lt;&gt;(Contact.class)
	 *         <b>.where(new ContactKey(2))</b>
	 *         .connection(conn)
	 *         .select().orElse(null);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * Contact contact
	 * Transaction.execute {
	 *     Contact key = new Contact()
	 *     contact = new Sql&lt;&gt;(Contact)
	 *         <b>.where(new ContactKey(2))</b>
	 *         .connection(it)
	 *         .select().orElse(null)
	 * }
	 * </pre></div>
	 *
	 * @param <K> エンティティの型
	 * @param entity エンティティ
	 * @return このオブジェクト
	 *
	 * @see #getWhere()
	 * @see Condition#of(Object)
	 */
	public <K> Sql<E> where(K entity) {
		return null;
	}

	/**
	 * <b>WHERE</b>句の条件をサブクエリで指定します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         <b>.where("EXISTS",</b>
	 *              <b>new Sql&lt;&gt;(Phone.class, "P")</b>
	 *                  <b>.where("{P.contactId}={C.id}")</b>
	 *                  <b>.and("{P.content} LIKE {}", "0800001%")</b>
	 *         <b>)</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         <b>.where('EXISTS',</b>
	 *              <b>new Sql&lt;&gt;(Phone, 'P')</b>
	 *                  <b>.where('{P.contactId}={C.id}')</b>
	 *                  <b>.and('{P.content} LIKE {}', '0800001%')</b>
	 *         <b>)</b>
	 *         .connection(it)
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param <SE> サブクエリに関連するエンティティの型
	 * @param content サブクエリの SELECT 文の左部分
	 * @param subSql サブクエリ用の<b>Sql</b>オブジェクト
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>content</b>または<b>subSql</b>が<b>null</b>の場合
	 *
	 * @see #where(Sql, String)
	 * @see #getWhere()
	 * @see Condition#of(String, Sql, Sql)
	 * @see SubqueryCondition#SubqueryCondition(Expression, Sql, Sql)
	 */
	public <SE> Sql<E> where(String content, Sql<SE> subSql) {
		return null;
	}

	/**
	 * <b>WHERE</b>句の条件をサブクエリで指定します。
	 *
	 * @param <SE> サブクエリに関連するエンティティの型
	 * @param subSql サブクエリ用の<b>Sql</b>オブジェクト
	 * @param content サブクエリの SELECT 文の右部分
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>subSql</b>または<b>content</b>が<b>null</b>の場合
	 *
	 * @since 3.1.0
	 * @see #where(String, Sql)
	 * @see #getWhere()
	 * @see Condition#of(String, Sql, Sql)
	 * @see SubqueryCondition#SubqueryCondition(Expression, Sql, Sql)
	 */
	public <SE> Sql<E> where(Sql<SE> subSql, String content) {
		return null;
	}

	/**
	 * 指定されている<b>WHERE</b>句の条件を返します。
	 *
	 * @return <b>WHERE</b>句の条件
	 *
	 * @see #where(Condition)
	 * @see #where(Object)
	 * @see #where(String, Object...)
	 * @see #where(String, Sql)
	 */
	public Condition getWhere() {
		return null;
	}

	/**
	 * <b>having</b>メソッドのコール後であれば、<b>HAVING</b>句の条件に、
	 * そうでなければ<b>WHERE</b>句の条件に<b>AND</b>で条件を追加します。
	 *
	 * @param condition 条件
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>condition</b>が<b>null</b>の場合
	 *
	 * @see #and(String, Object...)
	 * @see #and(String, Sql)
	 * @see #and(Sql, String)
	 * @see Condition#and(Condition)
	 */
	public Sql<E> and(Condition condition) {
		return null;
	}

	/**
	 * <b>having</b>メソッドのコール後であれば、<b>HAVING</b>句の条件に、
	 * そうでなければ<b>WHERE</b>句の条件に<b>AND</b>で式条件を追加します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .where("{lastName}={}", "Apple")
	 *         <b>.and("{firstName}={}", "Akiyo")</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         .where('{lastName}={}', 'Apple')
	 *         <b>.and('{firstName}={}', 'Akiyo')</b>
	 *         .connection(it)
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param content 式の内容
	 * @param arguments 式の引数
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>content</b>または<b>arguments</b>が<b>null</b>の場合
	 *
	 * @see #and(Condition)
	 * @see #and(String, Sql)
	 * @see #and(Sql, String)
	 * @see Condition#and(Condition)
	 * @see Condition#and(String, Object...)
	 */
	public Sql<E> and(String content, Object... arguments) {
		return null;
	}

	/**
	 * <b>having</b>メソッドのコール後であれば、<b>HAVING</b>句の条件に、
	 * そうでなければ<b>WHERE</b>句の条件に<b>AND</b>でサブクエリ条件を追加します。
	 *
	 * @param <SE> サブクエリに関連するエンティティの型
	 * @param content サブクエリの SELECT 文の左部分
	 * @param subSql サブクエリ用の<b>Sql</b>オブジェクト
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>content</b>または<b>subSql</b>が<b>null</b>の場合
	 *
	 * @see #and(Condition)
	 * @see #and(String, Object...)
	 * @see #and(Sql, String)
	 * @see Condition#and(String, Sql, Sql)
	 * @see Condition#and(Condition)
	 * @see Condition#of(String, Sql, Sql)
	 * @see SubqueryCondition#SubqueryCondition(Expression, Sql, Sql)
	 */
	public <SE> Sql<E> and(String content, Sql<SE> subSql) {
		return null;
	}

	/**
	 * <b>having</b>メソッドのコール後であれば、<b>HAVING</b>句の条件に、
	 * そうでなければ<b>WHERE</b>句の条件に<b>AND</b>でサブクエリ条件を追加します。
	 *
	 * @param <SE> サブクエリに関連するエンティティの型
	 * @param subSql サブクエリ用の<b>Sql</b>オブジェクト
	 * @param content サブクエリの SELECT 文の右部分
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>subSql</b>または<b>content</b>が<b>null</b>の場合
	 *
	 * @since 3.1.0
	 * @see #and(Condition)
	 * @see #and(String, Object...)
	 * @see #and(String, Sql)
	 * @see Condition#and(Condition)
	 * @see Condition#of(Sql, Sql, String)
	 * @see SubqueryCondition#SubqueryCondition(Sql, Sql, Expression)
	 */
	public <SE> Sql<E> and(Sql<SE> subSql, String content) {
		return null;
	}

	/**
	 * <b>having</b>メソッドのコール後であれば、<b>HAVING</b>句の条件に、
	 * そうでなければ<b>WHERE</b>句の条件に<b>OR</b>で条件を追加します。
	 *
	 * @param condition 条件
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>condition</b>が<b>null</b>の場合
	 *
	 * @see #or(String, Object...)
	 * @see #or(String, Sql)
	 * @see #or(Sql, String)
	 * @see Condition#or(Condition)
	 */
	public Sql<E> or(Condition condition) {
		return null;
	}

	/**
	 * <b>having</b>メソッドのコール後であれば、<b>HAVING</b>句の条件に、
	 * そうでなければ<b>WHERE</b>句の条件に<b>OR</b>で式条件を追加します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .where("{lastName}={}", "Apple")
	 *         <b>.or("{lastName}={}", "Orange")</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         .where('{lastName}={}', 'Apple')
	 *         <b>.or('{lastName}={}', 'Orange')</b>
	 *         .connection(it)
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param content 式の内容
	 * @param arguments 式の引数
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>content</b>または<b>arguments</b>が<b>null</b>の場合
	 *
	 * @see #or(Condition)
	 * @see #or(String, Sql)
	 * @see #or(Sql, String)
	 * @see Condition#or(String, Object...)
	 */
	public Sql<E> or(String content, Object... arguments) {
		return null;
	}

	/**
	 * <b>having</b>メソッドのコール後であれば、<b>HAVING</b>句の条件に、
	 * そうでなければ<b>WHERE</b>句の条件に<b>OR</b>でサブクエリ条件を追加します。
	 *
	 * @param <SE> サブクエリに関連するエンティティの型
	 * @param content サブクエリの SELECT 文の左部分
	 * @param subSql サブクエリ用の<b>Sql</b>オブジェクト
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>content</b>または<b>subSql</b>が<b>null</b>の場合
	 *
	 * @see #or(Condition)
	 * @see #or(String, Object...)
	 * @see #or(Sql, String)
	 * @see Condition#or(String, Sql, Sql)
	 * @see Condition#or(Condition)
	 * @see Condition#of(String, Sql, Sql)
	 * @see SubqueryCondition#SubqueryCondition(Expression, Sql, Sql)
	 */
	public <SE> Sql<E> or(String content, Sql<SE> subSql) {
		return null;
	}

	/**
	 * <b>having</b>メソッドのコール後であれば、<b>HAVING</b>句の条件に、
	 * そうでなければ<b>WHERE</b>句の条件に<b>OR</b>でサブクエリ条件を追加します。
	 *
	 * @param <SE> サブクエリに関連するエンティティの型
	 * @param subSql サブクエリ用の<b>Sql</b>オブジェクト
	 * @param content サブクエリの SELECT 文の右部分
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>subSql</b>または<b>content</b>が<b>null</b>の場合
	 *
	 * @since 3.1.0
	 * @see #or(Condition)
	 * @see #or(String, Object...)
	 * @see #or(String, Sql)
	 * @see Condition#or(Sql, Sql, String)
	 * @see Condition#or(Condition)
	 * @see Condition#of(Sql, Sql, String)
	 * @see SubqueryCondition#SubqueryCondition(Sql, Sql, Expression)
	 */
	public <SE> Sql<E> or(Sql<SE> subSql, String content) {
		return null;
	}

	/**
	 * <b>GROUP BY</b>句の1つの要素を指定します。
	 *
	 * @param content <b>Expression</b>の内容
	 * @param arguments <b>Expression</b>の引数
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>content</b>または<b>arguments</b>が<b>null</b>の場合
	 *
	 * @see #getGroupBy()
	 * @see #setGroupBy(GroupBy)
	 * @see GroupBy
	 * @see Expression#Expression(String, Object...)
	 */
	public Sql<E> groupBy(String content, Object... arguments) {
		return null;
	}

	/**
	 * <b>GROUP BY</b>句の内容を設定します。
	 *
	 * @param groupBy <b>GROUP BY</b>句の内容
	 * @return このオブジェクト
	 *
	 * @since 1.9.1
	 *
	 * @see #groupBy(String, Object...)
	 * @see #getGroupBy()
	 */
	public Sql<E> setGroupBy(GroupBy groupBy) {
		return null;
	}

	/**
	 * 指定された<b>GROUP BY</b>句の内容を返します。
	 *
	 * @return <b>GROUP BY</b>句の内容
	 *
	 * @see #groupBy(String, Object...)
	 * @see #setGroupBy(GroupBy)
	 */
	public GroupBy getGroupBy() {
		return null;
	}

	/**
	 * <b>HAVING</b>句の条件を指定します。
	 *
	 * @param condition 条件
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>condition</b>が<b>null</b>の場合
	 *
	 * @see #getHaving()
	 */
	public Sql<E> having(Condition condition) {
		return null;
	}

	/**
	 * <b>Expression</b>で<b>HAVING</b>句の条件を指定します。
	 *
	 * @param content <b>Expression</b>の内容
	 * @param arguments <b>Expression</b>の引数
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>content</b>または<b>arguments</b>が<b>null</b>の場合
	 *
	 * @see #getHaving()
	 * @see Condition#of(String, Object...)
	 */
	public Sql<E> having(String content, Object... arguments) {
		return null;
	}

	/**
	 * <b>SubqueryCondition</b>で<b>HAVING</b>句の条件を指定します。
	 *
	 * @param <SE> サブクエリに関連するエンティティの型
	 * @param content サブクエリの SELECT 文の左部分
	 * @param subSql サブクエリ用の<b>Sql</b>オブジェクト
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException if <b>content</b>または<b>subSql</b>が<b>null</b>の場合
	 *
	 * @see #having(Sql, String)
	 * @see #getHaving()
	 * @see Condition#of(String, Sql, Sql)
	 */
	public <SE> Sql<E> having(String content, Sql<SE> subSql) {
		return null;
	}

	/**
	 * <b>SubqueryCondition</b>で<b>HAVING</b>句の条件を指定します。
	 *
	 * @param <SE> サブクエリに関連するエンティティの型
	 * @param subSql サブクエリ用の<b>Sql</b>オブジェクト
	 * @param content サブクエリの SELECT 文の右部分
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException if <b>subSql</b>または<b>content</b>が<b>null</b>の場合
	 *
	 * @since 3.1.0
	 * @see #having(String, Sql)
	 * @see #getHaving()
	 * @see Condition#of(String, Sql, Sql)
	 */
	public <SE> Sql<E> having(Sql<SE> subSql, String content) {
		return null;
	}

	/**
	 * 指定されている<b>HAVING</b>句の条件を返します。
	 *
	 * @return HAVING 条件
	 *
	 * @see #having(Condition)
	 * @see #having(String, Object...)
	 * @see #having(String, Sql)
	 */
	public Condition getHaving() {
		return null;
	}

	/**
	 * UNION SQLを構成要素の1つのSELECT SQLを生成する<b>Sql</b>オブジェクトを追加します。
	 *
	 * @param <UE> UNION SQLの構成要素に関連するエンティティの型
	 * @param unionSql UNION SQLを構成要素の1つのSELECT SQLを生成する<b>Sql</b>オブジェクト
	 * @return このオブジェクト
	 *
	 * @throws IllegalStateException <b>unionAll</b>メソッドがすでに呼び出されている場合
	 *
	 * @see #unionAll(Sql)
	 * @since 3.1.0
	 */
	public <UE> Sql<E> union(Sql<UE> unionSql) {
		return this;
	}

	/**
	 * UNION ALL SQLを構成要素の1つのSELECT SQLを生成する<b>Sql</b>オブジェクトを追加します。
	 *
	 * @param <UE> UNION SQLの構成要素に関連するエンティティの型
	 * @param unionSql UNION SQLを構成要素の1つのSELECT SQLを生成する<b>Sql</b>オブジェクト
	 * @return このオブジェクト
	 *
	 * @throws IllegalStateException <b>union</b>メソッドがすでに呼び出されている場合
	 *
	 * @see #union(Sql)
	 * @since 3.1.0
	 */
	public <UE> Sql<E> unionAll(Sql<UE> unionSql) {
		return this;
	}

	/**
	 * UNION SQLまたはUNION ALL SQLをの構成要素のSELECT SQLを生成する<b>Sql</b>オブジェクトのリストを返します。
	 *
	 * @return UNION SQLまたはUNION ALL SQLをの構成要素のSELECT SQLを生成する<b>Sql</b>オブジェクトのリスト
	 *
	 * @since 3.1.0
	 */
	public List<Sql<?>> getUnionSqls() {
		return null;
	}

	/**
	 * UNION ALL SQLを生成するなら<b>true</b>、UNION SQLを生成するなら<b>false</b>を返します。
	 *
	 * @return UNION ALL SQLを生成するなら<b>true</b>、UNION SQLを生成するなら<b>false</b>
	 *
	 * @since 3.1.0
	 */
	public boolean isUnionAll() {
		return false;
	}

	/**
	 * <b>ORDER BY</b>句の1つの要素を指定します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         <b>.orderBy("{lastName}")</b>
	 *         <b>.orderBy("{firstName}")</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         <b>.orderBy('{lastName}')</b>
	 *         <b>.orderBy('{firstName}')</b>
	 *         .connection(it)
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param content <b>OrderBy.Element</b>の内容
	 * @param arguments <b>OrderBy.Element</b>の引数
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>content</b>または<b>arguments</b>が<b>null</b>の場合
	 *
	 * @see #asc()
	 * @see #desc()
	 * @see #setOrderBy(OrderBy)
	 * @see #getOrderBy()
	 * @see OrderBy#add(OrderBy.Element)
	 * @see OrderBy.Element#Element(String, Object...)
	 */
	public Sql<E> orderBy(String content, Object... arguments) {
		return null;
	}

	/**
	 * 最後に指定した<b>ORDER BY</b>句の要素を昇順に設定します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .orderBy("{id}")<b>.asc()</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         .orderBy('{id}')<b>.asc()</b>
	 *         .connection(it)
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @return このオブジェクト
	 *
	 * @see #orderBy(String, Object...)
	 * @see #desc()
	 * @see #getOrderBy()
	 * @see OrderBy#asc
	 */
	public Sql<E> asc() {
		return null;
	}

	/**
	 * 最後に指定した<b>ORDER BY</b>句の要素を降順に設定します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .orderBy("{id}")<b>.desc()</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         .orderBy('{id}')<b>.desc()</b>
	 *         .connection(it)
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @return このオブジェクト
	 *
	 * @see #orderBy(java.lang.String, java.lang.Object...)
	 * @see #asc()
	 * @see #getOrderBy()
	 * @see OrderBy#desc
	 */
	public Sql<E> desc() {
		return null;
	}

	/**
	 * <b>ORDER BY</b>句の内容を設定します。
	 *
	 * @param orderBy <b>ORDER BY</b>句の内容
	 *
	 * @return このオブジェクト
	 *
	 * @since 1.9.1
	 *
	 * @see #orderBy(java.lang.String, java.lang.Object...)
	 * @see #getOrderBy()
	 */
	public Sql<E> setOrderBy(OrderBy orderBy) {
		return null;
	}

	/**
	 * 指定された<b>ORDER BY</b>句の内容を返します。
	 *
	 * @return <b>ORDER BY</b>句の内容
	 *
	 * @see #orderBy(java.lang.String, java.lang.Object...)
	 * @see #setOrderBy(OrderBy)
	 */
	public OrderBy getOrderBy() {
		return null;
	}

	/**
	 * SELECT SQLの<b>LIMIT</b> 値を指定します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         <b>.limit(5)</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         <b>.limit(5)</b>
	 *         .connection(it)
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param limit <b>LIMIT</b> 値
	 * @return このオブジェクト
	 *
	 * @see #getLimit()
	 */
	public Sql<E> limit(int limit) {
		return null;
	}

	/**
	 * 指定されている<b>LIMIT</b> 値を返します。
	 *
	 * @return <b>LIMIT</b> 値
	 *
	 * @see #limit(int)
	 */
	public int getLimit() {
		return 0;
	}

	/**
	 * SELECT SQLの<b>OFFSET</b> 値を指定します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .limit(5)<b>.offset(5)</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         .limit(5)<b>.offset(5)</b>
	 *         .connection(it)
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param offset <b>OFFSET</b> 値
	 * @return このオブジェクト
	 *
	 * @see #getOffset()
	 */
	public Sql<E> offset(int offset) {
		return null;
	}

	/**
	 * 指定されている<b>OFFSET</b> 値を返します。
	 *
	 * @return <b>OFFSET</b> 値
	 *
	 * @see #offset(int)
	 */
	public int getOffset() {
		return 0;
	}

	/**
	 * 	SELECT SQLに<b>FOR UPDATE</b>を追加する事を指定します。
	 *
	 * @return このオブジェクト
	 *
	 * @see #isForUpdate()
	 */
	public Sql<E> forUpdate() {
		return null;
	}

	/**
	 * SELECT SQLに<b>FOR UPDATE</b>が追加されるかどうかを返します。
	 *
	 * @return <b>FOR UPDATE</b>が追加されるなら<b>true</b>、そうでなければ<b>false</b>
	 *
	 * @see #forUpdate()
	 */
	public boolean isForUpdate() {
		return false;
	}

	/**
	 * SELECT SQLに<b>NO WAIT</b>を追加する事を指定します。
	 *
	 * @return このオブジェクト
	 *
	 * @see #wait()
	 * @see #getWaitTime()
	 * @see #isNoWait()
	 * @see #isWaitForever()
	 */
	public Sql<E> noWait() {
		return null;
	}

	/**
	 * SELECT SQLに<b>WAIT n</b>を追加する事を指定します。
	 *
	 * @param waitTime 待ち時間(秒)
	 * @return このオブジェクト
	 *
	 * @since 1.9.0
	 *
	 * @see #noWait()
	 * @see #getWaitTime()
	 * @see #isNoWait()
	 * @see #isWaitForever()
	 */
	public Sql<E> wait(int waitTime) {
		return null;
	}

	/**
	 * SELECT SQLの<b>WAIT</b>の引数を返します。
	 *
	 * @return 待ち時間(秒)
	 *
	 * @since 1.9.0
	 *
	 * @see #noWait()
	 * @see #wait(int)
	 * @see #isNoWait()
	 * @see #isWaitForever()
	 */
	public int getWaitTime() {
		return 0;
	}

	/**
	 * SELECT SQLに<b>NOWAIT</b>が追加されるどうかを返します。
	 *
	 * @return <b>NO WAIT</b>が追加されるなら<b>true</b>、そうでなければ<b>false</b>
	 *
	 * @see #noWait()
	 * @see #wait(int)
	 * @see #getWaitTime()
	 * @see #isWaitForever()
	 */
	public boolean isNoWait() {
		return false;
	}

	/**
	 * SELECT SQLに<b>NOWAIT</b>と<b>WAIT n</b>のどちらも追加されないかどうかを返します。
	 *
	 * @return <b>NOWAIT</b>と<b>WAIT n</b>のどちらも追加されないなら<b>true</b>、そうでなければ<b>false</b>
	 *
	 * @since 1.9.0
	 *
	 * @see #noWait()
	 * @see #wait(int)
	 * @see #getWaitTime()
	 * @see #isNoWait()
	 */
	public boolean isWaitForever() {
		return false;
	}

	/**
	 * select, insert, update and deleteで使用するコネクション･ラッパーを指定します。
	 *
	 * <p>
	 * <span class="simpleTagLabel">注意:</span>
	 * 2.0.0版で追加されたデータベース･アクセスを行うメソッド
	 * (select, insert, updateおよびdelete)を使用する前にこのメソッドをコールしてください。
	 * </p>
	 *
	 * @param connection コネクション･ラッパー
	 * @return this object
	 *
	 * @since 2.0.0
	 * @see #getConnection()
	 */
	public Sql<E> connection(ConnectionWrapper connection) {
		return null;
	}

	/**
	 * コネクション･ラッパーを返します。
	 *
	 * @return コネクション･ラッパー
	 *
	 * @since 2.1.0
	 * @see #connection(ConnectionWrapper)
	 */
	public ConnectionWrapper getConnection() {
		return null;
	}

	/**
	 * 生成されたSQLを返します。
	 *
	 * @return 生成された SQL
	 *
	 * @since 1.8.4
	 */
	public String generatedSql() {
		return null;
	}

	/**
	 * アクションを実行します。
	 *
	 * @param action 実行するアクション
	 * @return このオブジェクト
	 *
	 * @since 2.0.0
	 * @see #doIf(boolean, Consumer)
	 */
	public Sql<E> doAlways(Consumer<Sql<E>> action) {
		return null;
	}

	/**
	 * 実行条件が<b>true</b>ならアクションを実行します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         <b>.doIf(!(Sql.getDatabase() instanceof SQLite), Sql::forUpdate)</b>
	 *         .connection(conn)
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         <b>.doIf(!(Sql.database instanceof SQLite)) {it.forUpdate}</b>
	 *         .connection(it)
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param condition 実行条件
	 * @param action 実行条件が<b>true</b> の場合に実行するアクション
	 * @return このオブジェクト
	 */
	public Sql<E> doIf(boolean condition, Consumer<Sql<E>> action) {
		return null;
	}

	/**
	 * 実行条件が<b>false</b>ならアクションを実行します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .connection(conn)
	 *         <b>.doNotIf(Sql.getDatabase() instanceof SQLite, Sql::forUpdate)</b>
	 *         .select(contacts::add);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         .connection(it)
	 *         <b>.doNotIf(Sql.getDatabase() instanceof SQLite) {it.forUpdate}</b>
	 *         .select({contacts &lt;&lt; it})
	 * }
	 * </pre></div>
	 *
	 * @param condition 実行条件
	 * @param action 実行するアクション
	 * @return このオブジェクト
	 *
	 * @since 3.0.0
	 * @see #doIf(boolean, Consumer)
	 * @see #doElse(Consumer)
	 * @see #doAlways(Consumer)
	 */
	public Sql<E> doNotIf(boolean condition, Consumer<Sql<E>> action) {
		return null;
	}

	/**
	 * このメソッドより前に実行された<b>doIf</b>の実行条件が<b>false</b>または
	 * <b>doNotIf</b>の実行条件が<b>true</b>ならアクションを実行します。
	 *
	 * @param elseAction an action
	 * @return this object
	 *
	 * @since 3.0.0
	 * @see #doIf(boolean, Consumer)
	 * @see #doNotIf(boolean, Consumer)
	 * @see #doAlways(Consumer)
	 */
	public Sql<E> doElse(Consumer<Sql<E>> elseAction) {
		return null;
	}

	/**
	 * 実行条件が<b>true</b>なら<b>action</b>を実行し、そうでなければ<b>elseAction</b>を実行します。
	 *
	 * <p>
	 * @deprecated リリース 3.0.0 より。
	 * 代わりに{@link #doIf(boolean, Consumer)}と{@link #doElse(Consumer)}を使用してください。
	 * </p>
	 *
	 * @param condition 実行条件
	 * @param action 実行条件が<b>true</b> の場合に実行するアクション
	 * @param elseAction 実行条件が<b>false</b> の場合に実行するアクション
	 * @return このオブジェクト
	 */
	@Deprecated
	public Sql<E> doIf(boolean condition, Consumer<Sql<E>> action, Consumer<Sql<E>> elseAction) {
		return null;
	}

	/**
	 * 指定のテーブル別名に対応する<b>SqlEntityInfo</b>オブジェクトを返します。
	 *
	 * <p>
	 * <i>このメソッドは内部的に使用されます。</i>
	 * </p>
	 *
	 * @param tableAlias テーブル別名
	 * @return SqlEntityInfo オブジェクト
	 *
	 * @throws NullPointerException <b>tableAlias</b>が<b>null</b>の場合
	 */
	public SqlEntityInfo<?> getSqlEntityInfo(String tableAlias) {
		return null;
	}

	/**
	 * SqlEntityInfo オブジェクトを追加します。
	 *
	 * <p>
	 * <i>このメソッドは内部的に使用されます。</i>
	 * </p>
	 *
	 * @param sqlEntityInfo SqlEntityInfo オブジェクト
	 */
	public void addSqlEntityInfo(SqlEntityInfo<?> sqlEntityInfo) {
	}

	/**
	 * テーブルを結合しないSELECT SQLを生成して実行します。<br>
	 *
	 * <p>
	 * <b>columns</b>がコールされてなく、
	 * <b>innerJoin</b>, <b>leftJoin</b>, <b>rightJoin</b>メソッドのいずれかコールされている場合は、
	 * 以下の文字列を columns セットに設定します。<br>
	 * </p>
	 *
	 * <ul class="code" style="list-style-type:none">
	 *   <li>"&lt;メイン･テーブルの別名&gt;.*"</li>
	 * </ul>
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.select(contacts::add)</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.select({contacts &lt;&lt; it})</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">注意:</span>
	 * このメソッドを使用する前にコネクション･ラッパーを指定する
	 * {@link #connection(ConnectionWrapper)}メソッドをコールしてください。
	 * </p>
	 *
	 * @param consumer 取得した行から生成されたエンティティのコンシューマ
	 *
	 * @throws NullPointerException <b>consumer</b>が<b>null</b>の場合
	 * @throws IllegalStateException カラムのないSELECT SQLが生成された場合
	 * @throws RuntimeSQLException データベース･アクセス･エラーが発生した場合
	 *
	 * @since 2.0.0
	 * @see #selectAs(Class, Consumer)
	 */
	public void select(Consumer<? super E> consumer) {
	}

	/**
	 * テーブルを結合しないSELECT SQLを生成して実行します。<br>
	 *
	 * <p>
	 * <b>columns</b>がコールされてなく、
	 * <b>innerJoin</b>, <b>leftJoin</b>, <b>rightJoin</b>メソッドのいずれかコールされている場合は、
	 * 以下の文字列を columns セットに設定します。<br>
	 * </p>
	 *
	 * <ul class="code" style="list-style-type:none">
	 *   <li>"&lt;メイン･テーブルの別名&gt;.*"</li>
	 * </ul>
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;ContactName&gt; contactNames = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.selectAs(ContactName.class, contactNames::add)</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;ContactName&gt; contactNames = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.selectAs(ContactName, {contactNames &lt;&lt; it})</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">注意:</span>
	 * このメソッドを使用する前にコネクション･ラッパーを指定する
	 * {@link #connection(ConnectionWrapper)}メソッドをコールしてください。
	 * </p>
	 *
	 * @param <RE> コンシューマの引数の型
	 * @param resultClass コンシューマの引数のクラス
	 * @param consumer 取得した行から生成されたエンティティのコンシューマ
	 *
	 * @throws NullPointerException <b>consumer</b>が<b>null</b>の場合
	 * @throws IllegalStateException カラムのないSELECT SQLが生成された場合
	 * @throws RuntimeSQLException データベース･アクセス･エラーが発生した場合
	 *
	 * @since 2.0.0
	 * @see #select(Consumer)
	 */
	public <RE> void selectAs(Class<RE> resultClass, Consumer<? super RE> consumer) {
	}

	/**
	 * 1つのテーブルを結合する SELECT SQLを生成して実行します。
	 *
	 * <p>
	 * <b>columns</b>がコールされてなく、
	 * <b>innerJoin</b>, <b>leftJoin</b>, <b>rightJoin</b>メソッドのいずれか1回より多くコールされている場合は、
	 * 以下の文字列を columns セットに設定します。<br>
	 * </p>
	 *
	 * <ul class="code" style="list-style-type:none">
	 *   <li>"&lt;メイン･テーブルの別名&gt;.*"</li>
	 *   <li>"&lt;結合テーブル1の別名&gt;.*"</li>
	 * </ul>
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * List&lt;Phone&gt;   phones = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
	 *         .connection(conn)
	 *         <b>.&lt;Phone&gt;select(contacts::add, phones::add)</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * List&lt;Phone&gt;   phones = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         .innerJoin(Phone, 'P', '{P.contactId}={C.id}')
	 *         .connection(it)
	 *         <b>.select({contacts &lt;&lt; it}, {phones &lt;&lt; it})</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">注意:</span>
	 * このメソッドを使用する前にコネクション･ラッパーを指定する
	 * {@link #connection(ConnectionWrapper)}メソッドをコールしてください。
	 * </p>
	 *
	 * @param <JE1> 結合テーブル1のエンティティの型
	 * @param consumer 取得した行から生成されたメイン･テーブルのエンティティのコンシューマ
	 * @param consumer1 取得した行から生成された結合テーブル1のエンティティのコンシューマ
	 *
	 * @throws NullPointerException <b>consumer</b>または<b>consumer1</b>が<b>null</b>の場合
	 * @throws IllegalStateException 結合テーブル情報がない場合
	 * @throws IllegalStateException カラムのないSELECT SQLが生成された場合
	 * @throws RuntimeSQLException データベース･アクセス･エラーが発生した場合
	 *
	 * @since 2.0.0
	 */
	public <JE1> void select(
		Consumer<? super E> consumer,
		Consumer<? super JE1> consumer1) {
	}

	/**
	 * 2つのテーブルを結合する SELECT SQLを生成して実行します。
	 *
	 * <p>
	 * <b>columns</b>がコールされてなく、
	 * <b>innerJoin</b>, <b>leftJoin</b>, <b>rightJoin</b>メソッドのいずれか2回より多くコールされている場合は、
	 * 以下の文字列を columns セットに設定します。
	 * </p>
	 *
	 * <ul class="code" style="list-style-type:none">
	 *   <li>"&lt;メイン･テーブルの別名&gt;.*"</li>
	 *   <li>"&lt;結合テーブル1の別名&gt;.*"</li>
	 *   <li>"&lt;結合テーブル2の別名&gt;.*"</li>
	 * </ul>
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * List&lt;Phone&gt;   phones = new ArrayList&lt;&gt;();
	 * List&lt;Email&gt;   emails = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
	 *         .innerJoin(Email.class, "E", "{E.contactId}={C.id}")
	 *         .connection(conn)
	 *         <b>.&lt;Phone, Email&gt;select(contacts::add, phones::add, emails::add)</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * List&lt;Phone&gt;   phones = []
	 * List&lt;Email&gt;   emails = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         .innerJoin(Phone, 'P', '{P.contactId}={C.id}')
	 *         .innerJoin(Email, 'E', '{E.contactId}={C.id}')
	 *         .connection(it)
	 *         <b>.select({contacts &lt;&lt; it}, {phones &lt;&lt; it}, {emails &lt;&lt; it})</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">注意:</span>
	 * このメソッドを使用する前にコネクション･ラッパーを指定する
	 * {@link #connection(ConnectionWrapper)}メソッドをコールしてください。
	 * </p>
	 *
	 * @param <JE1> 結合テーブル1のエンティティの型
	 * @param <JE2> 結合テーブル2のエンティティの型
	 * @param consumer 取得した行から生成されたメイン･テーブルのエンティティのコンシューマ
	 * @param consumer1 取得した行から生成された結合テーブル1のエンティティのコンシューマ
	 * @param consumer2 取得した行から生成された結合テーブル2のエンティティのコンシューマ
	 *
	 * @throws NullPointerException <b>connection</b>, <b>consumer</b>, <b>consumer1</b>または<b>consumer2</b>が<b>null</b>の場合
	 * @throws IllegalStateException 結合テーブル情報が2より少ない場合
	 * @throws IllegalStateException カラムのないSELECT SQLが生成された場合
	 * @throws RuntimeSQLException データベース･アクセス･エラーが発生した場合
	 *
	 * @since 2.0.0
	 */
	public <JE1, JE2> void select(
		Consumer<? super  E > consumer,
		Consumer<? super JE1> consumer1,
		Consumer<? super JE2> consumer2) {
	}

	/**
	 * 3つのテーブルを結合する SELECT SQLを生成して実行します。
	 *
	 * <p>
	 * <b>columns</b>がコールされてなく、
	 * <b>innerJoin</b>, <b>leftJoin</b>, <b>rightJoin</b>メソッドのいずれか3回より多くコールされている場合は、
	 * 以下の文字列を columns セットに設定します。
	 * </p>
	 *
	 * <ul class="code" style="list-style-type:none">
	 *   <li>"&lt;メイン･テーブルの別名&gt;.*"</li>
	 *   <li>"&lt;結合テーブル1の別名&gt;.*"</li>
	 *   <li>"&lt;結合テーブル2の別名&gt;.*"</li>
	 *   <li>"&lt;結合テーブル3の別名&gt;.*"</li>
	 * </ul>
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * List&lt;Phone&gt;   phones = new ArrayList&lt;&gt;();
	 * List&lt;Email&gt;   emails = new ArrayList&lt;&gt;();
	 * List&lt;Address&gt; addresses = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .innerJoin(  Phone.class, "P", "{P.contactId}={C.id}")
	 *         .innerJoin(  Email.class, "E", "{E.contactId}={C.id}")
	 *         .innerJoin(Address.class, "A", "{A.contactId}={C.id}")
	 *         .connection(conn)
	 *         <b>.&lt;Phone, Email, Address&gt;select(</b>
	 *             <b>contacts::add, phones::add, emails::add, addresses::add)</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * List&lt;Phone&gt;   phones = []
	 * List&lt;Email&gt;   emails = []
	 * List&lt;Address&gt; addresses = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         .innerJoin(Phone  , 'P', '{P.contactId}={C.id}')
	 *         .innerJoin(Email  , 'E', '{E.contactId}={C.id}')
	 *         .innerJoin(Address, 'A', '{A.contactId}={C.id}')
	 *         .connection(it)
	 *         <b>.select(</b>
	 *             <b>{contacts &lt;&lt; it}, {phones &lt;&lt; it}, {emails &lt;&lt; it}, {addresses &lt;&lt; it})</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">注意:</span>
	 * このメソッドを使用する前にコネクション･ラッパーを指定する
	 * {@link #connection(ConnectionWrapper)}メソッドをコールしてください。
	 * </p>
	 *
	 * @param <JE1> 結合テーブル1のエンティティの型
	 * @param <JE2> 結合テーブル2のエンティティの型
	 * @param <JE3> 結合テーブル3のエンティティの型
	 * @param consumer 取得した行から生成されたメイン･テーブルのエンティティのコンシューマ
	 * @param consumer1 取得した行から生成された結合テーブル1のエンティティのコンシューマ
	 * @param consumer2 取得した行から生成された結合テーブル2のエンティティのコンシューマ
	 * @param consumer3 取得した行から生成された結合テーブル3のエンティティのコンシューマ
	 *
	 * @throws NullPointerException <b>connection</b>, <b>consumer</b>, <b>consumer1</b>, <b>consumer2</b>または<b>consumer3</b>が<b>null</b>の場合
	 * @throws IllegalStateException 結合テーブル情報が3より少ない場合
	 * @throws IllegalStateException カラムのないSELECT SQLが生成された場合
	 * @throws RuntimeSQLException データベース･アクセス･エラーが発生した場合
	 */
	public <JE1, JE2, JE3> void select(
			Consumer<? super  E > consumer,
			Consumer<? super JE1> consumer1,
			Consumer<? super JE2> consumer2,
			Consumer<? super JE3> consumer3) {
	}

	/**
	 * 4つのテーブルを結合する SELECT SQLを生成して実行します。
	 *
	 * <p>
	 * <b>columns</b>がコールされてなく、
	 * <b>innerJoin</b>, <b>leftJoin</b>, <b>rightJoin</b>メソッドのいずれか4回より多くコールされている場合は、
	 * 以下の文字列を columns セットに設定します。
	 * </p>
	 *
	 * <ul class="code" style="list-style-type:none">
	 *   <li>"&lt;メイン･テーブルの別名&gt;.*"</li>
	 *   <li>"&lt;結合テーブル1の別名&gt;.*"</li>
	 *   <li>"&lt;結合テーブル2の別名&gt;.*"</li>
	 *   <li>"&lt;結合テーブル3の別名&gt;.*"</li>
	 *   <li>"&lt;結合テーブル4の別名&gt;.*"</li>
	 * </ul>
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
	 * List&lt;Phone&gt;   phones = new ArrayList&lt;&gt;();
	 * List&lt;Email&gt;   emails = new ArrayList&lt;&gt;();
	 * List&lt;Address&gt; addresses = new ArrayList&lt;&gt;();
	 * List&lt;Url&gt;     urls = new ArrayList&lt;&gt;();
	 * Transaction.execute(conn -&gt; {
	 *     new Sql&lt;&gt;(Contact.class, "C")
	 *         .innerJoin(  Phone.class, "P", "{P.contactId}={C.id}")
	 *         .innerJoin(  Email.class, "E", "{E.contactId}={C.id}")
	 *         .innerJoin(Address.class, "A", "{A.contactId}={C.id}")
	 *         .innerJoin(    Url.class, "U", "{U.contactId}={C.id}")
	 *         .connection(conn)
	 *         <b>.&lt;Phone, Email, Address, Url&gt;select(</b>
	 *             <b>contacts::add, phones::add, emails::add,</b>
	 *             <b>addresses::add, urls::add)</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * List&lt;Contact&gt; contacts = []
	 * List&lt;Phone&gt;   phones = []
	 * List&lt;Email&gt;   emails = []
	 * List&lt;Address&gt; addresses = []
	 * List&lt;Url&gt;     urls = []
	 * Transaction.execute {
	 *     new Sql&lt;&gt;(Contact, 'C')
	 *         .innerJoin(Phone  , 'P', '{P.contactId}={C.id}')
	 *         .innerJoin(Email  , 'E', '{E.contactId}={C.id}')
	 *         .innerJoin(Address, 'A', '{A.contactId}={C.id}')
	 *         .innerJoin(Url    , 'U', '{U.contactId}={C.id}')
	 *         .connection(it)
	 *         <b>.select(</b>
	 *             <b>{contacts &lt;&lt; it}, {phones &lt;&lt; it}, {emails &lt;&lt; it},</b>
	 *             <b>{addresses &lt;&lt; it}, {urls &lt;&lt; it})</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">注意:</span>
	 * このメソッドを使用する前にコネクション･ラッパーを指定する
	 * {@link #connection(ConnectionWrapper)}メソッドをコールしてください。
	 * </p>
	 *
	 * @param <JE1> 結合テーブル1のエンティティの型
	 * @param <JE2> 結合テーブル2のエンティティの型
	 * @param <JE3> 結合テーブル3のエンティティの型
	 * @param <JE4> 結合テーブル4のエンティティの型
	 * @param consumer 取得した行から生成されたメイン･テーブルのエンティティのコンシューマ
	 * @param consumer1 取得した行から生成された結合テーブル1のエンティティのコンシューマ
	 * @param consumer2 取得した行から生成された結合テーブル2のエンティティのコンシューマ
	 * @param consumer3 取得した行から生成された結合テーブル3のエンティティのコンシューマ
	 * @param consumer4 取得した行から生成された結合テーブル4のエンティティのコンシューマ
	 *
	 * @throws NullPointerException <b>connection</b>, <b>consumer</b>, <b>consumer1</b>, <b>consumer2</b>, <b>consumer3</b>または<b>consumer4</b>が<b>null</b>の場合
	 * @throws IllegalStateException 結合テーブル情報が4より少ない場合
	 * @throws IllegalStateException カラムのないSELECT SQLが生成された場合
	 * @throws RuntimeSQLException データベース･アクセス･エラーが発生した場合
	 */
	public <JE1, JE2, JE3, JE4> void select(
		Consumer<? super  E > consumer,
		Consumer<? super JE1> consumer1,
		Consumer<? super JE2> consumer2,
		Consumer<? super JE3> consumer3,
		Consumer<? super JE4> consumer4) {
	}

	/**
	 * SELECT SQLを生成して実行します。
	 * 取得されない場合は、<b>Optional.empty()</b>を返します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * Contact[] contact = new Contact[1];
	 * Transaction.execute(conn -&gt; {
	 *     contact[0] = new Sql&lt;&gt;(Contact.class)
	 *         .where("{id}={}", 1)
	 *         .connection(conn)
	 *         <b>.select()</b>.orElse(null);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * Contact contact
	 * Transaction.execute {
	 *     contact = new Sql&lt;&gt;(Contact)
	 *         .where('{id}={}', 1)
	 *         .connection(it)
	 *         <b>.select()</b>.orElse(null)
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">注意:</span>
	 * このメソッドを使用する前にコネクション･ラッパーを指定する
	 * {@link #connection(ConnectionWrapper)}メソッドをコールしてください。
	 * </p>
	 *
	 * @return 取得した行から生成されたエンティティ、取得されない場合は<b>Optional.empty()</b>
	 *
	 * @throws NullPointerException <b>connection</b>が<b>null</b>の場合
	 * @throws IllegalStateException カラムのないSELECT SQLが生成された場合
	 * @throws RuntimeSQLException データベース･アクセス･エラーが発生した場合
	 * @throws ManyRowsException 複数行が検索された場合
	 */
	public Optional<E> select() {
		return null;
	}

	/**
	 * SELECT SQLを生成して実行します。
	 * 取得されない場合は、<b>Optional.empty()</b>を返します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * ContactName[] contactName = new ContactName[1];
	 * Transaction.execute(conn -&gt; {
	 *     contactName[0] = new Sql&lt;&gt;(Contact.class)
	 *         .where("{id}={}", 1)
	 *         .connection(conn)
	 *         <b>.selectAs(ContactName.class)</b>.orElse(null);
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * ContactName contactName
	 * Transaction.execute {
	 *     contactName = new Sql&lt;&gt;(Contact)
	 *         .where('{id}={}', 1)
	 *         .connection(it)
	 *         <b>.selectAs(ContactName)</b>.orElse(null)
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">注意:</span>
	 * このメソッドを使用する前にコネクション･ラッパーを指定する
	 * {@link #connection(ConnectionWrapper)}メソッドをコールしてください。
	 * </p>
	 *
	 * @param <RE> 戻り値のエンティティの型
	 * @param resultClass 戻り値のエンティティクラス
	 * @return 取得した行から生成されたエンティティ、取得されない場合は<b>Optional.empty()</b>
	 *
	 * @throws NullPointerException <b>connection</b>が<b>null</b>の場合
	 * @throws IllegalStateException カラムのないSELECT SQLが生成された場合
	 * @throws RuntimeSQLException データベース･アクセス･エラーが発生した場合
	 * @throws ManyRowsException 複数行が検索された場合
	 *
	 * @since 2.0.0
	 * @see #select()
	 */
	public <RE> Optional<RE> selectAs(Class<RE> resultClass) {
		return null;
	}

	/**
	 * SELECT COUNT(*) SQLを生成して実行します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * int[] count = new int[1];
	 * Transaction.execute(conn -&gt; {
	 *     count[0] = new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.selectCount()</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * int count
	 * Transaction.execute {
	 *     count = new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.selectCount()</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">注意:</span>
	 * このメソッドを使用する前にコネクション･ラッパーを指定する
	 * {@link #connection(ConnectionWrapper)}メソッドをコールしてください。
	 * </p>
	 *
	 * @return 実行結果行数
	 *
	 * @throws RuntimeSQLException データベース･アクセス･エラーが発生した場合
	 *
	 * @since 2.0.0
	 */
	public int selectCount() {
		return 0;
	}

	/**
	 * INSERT SQLを生成して実行します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * int[] count = new int[1];
	 * Transaction.execute(conn -&gt; {
	 *     count[0] = new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.insert(new Contact(6, "Setoka", "Orange", 2001, 2, 1))</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * int count
	 * Transaction.execute {
	 *     count = new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.insert(new Contact(6, 'Setoka', 'Orange', 2001, 2, 1))</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">注意:</span>
	 * このメソッドを使用する前にコネクション･ラッパーを指定する
	 * {@link #connection(ConnectionWrapper)}メソッドをコールしてください。
	 * </p>
	 *
	 * @param entity 挿入対象のエンティティ
	 * @return 挿入した行数
	 *
	 * @throws NullPointerException <b>entity</b>が<b>null</b>の場合
	 * @throws RuntimeSQLException データベース･アクセス･エラーが発生した場合
	 *
	 * @since 2.0.0
	 */
	public int insert(E entity) {
		return 0;
	}

	/**
	 * <b>entities</b>の各要素毎に INSERT SQLを生成して実行します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * int[] count = new int[1];
	 * Transaction.execute(conn -&gt; {
	 *     count[0] = new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.insert(Arrays.asList(</b>
	 *             <b>new Contact(7, "Harumi", "Orange", 2001, 2, 2),</b>
	 *             <b>new Contact(8, "Mihaya", "Orange", 2001, 2, 3),</b>
	 *             <b>new Contact(9, "Asumi" , "Orange", 2001, 2, 4)</b>
	 *         <b>))</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * int count
	 * Transaction.execute {
	 *     count = new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.insert([</b>
	 *             <b>new Contact(7, 'Harumi', 'Orange', 2001, 2, 2),</b>
	 *             <b>new Contact(8, 'Mihaya', 'Orange', 2001, 2, 3),</b>
	 *             <b>new Contact(9, 'Asumi' , 'Orange', 2001, 2, 4)</b>
	 *         <b>])</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">注意:</span>
	 * このメソッドを使用する前にコネクション･ラッパーを指定する
	 * {@link #connection(ConnectionWrapper)}メソッドをコールしてください。
	 * </p>
	 *
 	 * @param entities 挿入対象のエンティティの<b>Iterable</b>
	 * @return 挿入した行数
	 *
	 * @throws NullPointerException <b>entities</b>または<b>entities</b>の要素のいずれかが<b>null</b>の場合
	 * @throws RuntimeSQLException データベース･アクセス･エラーが発生した場合
	 */
	public int insert(Iterable<? extends E> entities) {
		return 0;
	}

	/**
	 * UPDATE SQLを生成して実行します。
	 *
	 * <p>
	 * <b>WHERE</b>句の条件が指定されている場合は、その条件で更新が行われます。<br>
	 * 対象のテーブルのすべての行を更新するには、<b>WHERE</b>句の条件に<b>Condition.ALL</b>を指定してください。
	 * </p>
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * int[] count = new int[1];
	 * Transaction.execute(conn -&gt; {
	 *     count[0] = new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.update(new Contact(6, "Setoka", "Orange", 2017, 2, 1))</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * int count
	 * Transaction.execute {
	 *     count = new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.update(new Contact(6, 'Setoka', 'Orange', 2017, 2, 1))</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">注意:</span>
	 * このメソッドを使用する前にコネクション･ラッパーを指定する
	 * {@link #connection(ConnectionWrapper)}メソッドをコールしてください。
	 * </p>
	 *
	 * @param entity 更新対象のエンティティ
	 * @return 更新した行数
	 *
	 * @throws NullPointerException <b>entity</b>が<b>null</b>の場合
	 * @throws RuntimeSQLException データベース･アクセス･エラーが発生した場合
	 *
	 * @see org.lightsleep.component.Condition#ALL
	 */
	public int update(E entity) {
		return 0;
	}

	/**
	 * <b>entities</b>の各要素毎に UPDATE SQLを生成して実行します。
	 *
	 * <p>
	 * <b>WHERE</b>句の条件が指定されている場合でも、各エンティティ毎に<b>new EntityCondition(entity)</b>が指定されます。
	 * </p>
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * int[] count = new int[1];
	 * Transaction.execute(conn -&gt; {
	 *     count[0] = new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.update(Arrays.asList(</b>
	 *             <b>new Contact(7, "Harumi", "Orange", 2017, 2, 2),</b>
	 *             <b>new Contact(8, "Mihaya", "Orange", 2017, 2, 3),</b>
	 *             <b>new Contact(9, "Asumi" , "Orange", 2017, 2, 4)</b>
	 *         <b>))</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * int count
	 * Transaction.execute {
	 *     count = new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.update([</b>
	 *             <b>new Contact(7, 'Harumi', 'Orange', 2017, 2, 2),</b>
	 *             <b>new Contact(8, 'Mihaya', 'Orange', 2017, 2, 3),</b>
	 *             <b>new Contact(9, 'Asumi' , 'Orange', 2017, 2, 4)</b>
	 *         <b>])</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">注意:</span>
	 * このメソッドを使用する前にコネクション･ラッパーを指定する
	 * {@link #connection(ConnectionWrapper)}メソッドをコールしてください。
	 * </p>
	 *
	 * @param entities 更新対象のエンティティの<b>Iterable</b>
	 * @return 更新した行数
	 *
	 * @throws NullPointerException <b>entityStream</b>または<b>entityStream</b>の要素のいずれかが<b>null</b> の場合
	 * @throws RuntimeSQLException データベース･アクセス･エラーが発生した場合
	 */
	public int update(Iterable<? extends E> entities) {
		return 0;
	}

	/**
	 * DELETE SQLを生成して実行します。
	 *
	 * <p>
	 * <b>WHERE</b>句の条件が指定されていない場合は実行されません。<br>
	 * 対象のテーブルのすべての行を削除するには、<b>WHERE</b>句の条件に<b>Condition.ALL</b>を指定してください。
	 * </p>
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * int[] count = new int[1];
	 * Transaction.execute(conn -&gt; {
	 *     count[0] = new Sql&lt;&gt;(Contact.class)
	 *         .where(Condition.ALL)
	 *         .connection(conn)
	 *         <b>.delete()</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * int count
	 * Transaction.execute {
	 *     count = new Sql&lt;&gt;(Contact)
	 *         .where(Condition.ALL)
	 *         .connection(it)
	 *         <b>.delete()</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">注意:</span>
	 * このメソッドを使用する前にコネクション･ラッパーを指定する
	 * {@link #connection(ConnectionWrapper)}メソッドをコールしてください。
	 * </p>
	 *
	 * @return 削除した行数
	 *
	 * @throws RuntimeSQLException データベース･アクセス･エラーが発生した場合
	 *
	 * @see org.lightsleep.component.Condition#ALL
	 */
	public int delete() {
		return 0;
	}

	/**
	 * DELETE SQLを生成して実行します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * int[] count = new int[1];
	 * Transaction.execute(conn -&gt; {
	 *     count[0] = new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.delete(new Contact(6))</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * int count
	 * Transaction.execute {
	 *     count = new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.delete(new Contact(6))</b>
	 * }
	 * </pre></div>
	 *
	 * <p>
	 * <span class="simpleTagLabel">注意:</span>
	 * このメソッドを使用する前にコネクション･ラッパーを指定する
	 * {@link #connection(ConnectionWrapper)}メソッドをコールしてください。
	 * </p>
	 *
	 * @param entity 削除対象のエンティティ
	 * @return 削除した行数
	 *
	 * @throws NullPointerException <b>entity</b>が<b>null</b>の場合
	 * @throws RuntimeSQLException データベース･アクセス･エラーが発生した場合
	 */
	public int delete(E entity) {
		return 0;
	}

	/**
	 * <b>entities</b>の各要素毎にDELETE SQLを生成して実行します。
	 *
	 * <div class="exampleTitle"><span>使用例/Java</span></div>
	 * <div class="exampleCode"><pre>
	 * int[] count = new int[1];
	 * Transaction.execute(conn -&gt; {
	 *     count[0] = new Sql&lt;&gt;(Contact.class)
	 *         .connection(conn)
	 *         <b>.delete(Arrays.asList(new Contact(7), new Contact(8), new Contact(9)))</b>;
	 * });
	 * </pre></div>
	 *
	 * <div class="exampleTitle"><span>使用例/Groovy</span></div>
	 * <div class="exampleCode"><pre>
	 * int count
	 * Transaction.execute {
	 *     count = new Sql&lt;&gt;(Contact)
	 *         .connection(it)
	 *         <b>.delete([new Contact(7), new Contact(8), new Contact(9)])</b>
	 * }
	 * </pre></div>
	 *
	 * @param entities 削除対象のエンティティの<b>Iterable</b>
	 * @return 削除した行数
	 *
	 * @throws NullPointerException <b>entityStream</b>または<b>entityStream</b>の要素のいずれかが<b>null</b>の場合
	 * @throws RuntimeSQLException データベース･アクセス･エラーが発生した場合
	 */
	public int delete(Iterable<? extends E> entities) {
		return 0;
	}

	/**
	 * SQLを実行します。
	 *
	 * @param sql 実行するSQL
	 *
	 * @throws NullPointerException <b>sql</b>が<b>null</b>の場合
	 * @throws RuntimeSQLException データベース･アクセス･エラーが発生した場合
	 *
	 * @since 3.0.0
	 */
	public void executeUpdate(String sql) {
	}

	/**
	 * メイン･テーブルの<b>ColumnInfo</b>ストリームを返します。
	 *
	 * <p>
	 * <i>このメソッドは内部的に使用されます。</i>
	 * </p>
	 *
	 * @return <b>ColumnInfo</b>ストリーム
	 */
	public Stream<ColumnInfo> columnInfoStream() {
		return null;
	}

	/**
	 * メイン･テーブルの選択対象のカラムの<b>SqlColumnInfo</b>ストリームを返します。
	 *
	 * <p>
	 * <i>このメソッドは内部的に使用されます。</i>
	 * </p>
	 *
	 * @return <b>SqlColumnInfo</b>ストリーム
	 */
	public Stream<SqlColumnInfo> selectedSqlColumnInfoStream() {
		return null;
	}

	/**
	 * メイン･テーブルと結合テーブルの選択対象のカラムの<b>SqlColumnInfo</b>ストリームを返します。
	 *
	 * <p>
	 * <i>このメソッドは内部的に使用されます。</i>
	 * </p>
	 *
	 * @return <b>SqlColumnInfo</b>ストリーム
	 */
	public Stream<SqlColumnInfo> selectedJoinSqlColumnInfoStream() {
		return null;
	}
}
