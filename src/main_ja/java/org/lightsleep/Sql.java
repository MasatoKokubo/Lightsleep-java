/*
	Sql.java
	(C) 2016 Masato Kokubo
*/
package org.lightsleep;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.Optional;

import org.lightsleep.component.*;
import org.lightsleep.connection.*;
import org.lightsleep.database.*;
import org.lightsleep.helper.*;

/**
	SQL を構築および実行するためのクラスです。<br>

	<div class="sampleTitle"><span>使用例</span></div>
<div class="sampleCode"><pre>
List&lt;Contact&gt; contacts = new ArrayList&lt;&gt;();
Transaction.execute(connection -&gt; {
    new <b>Sql</b>&lt;&gt;(Contact.class)
        .<b>where</b>("{familyName} = {}", "Smith")
        .<b>select</b>(connection, contacts::add);
});
</pre></div>

	<div class="sampleTitle"><span>SQL</span></div>
<div class="sampleCode"><pre>
SELECT contactId, familyName, givenName, ... FROM Contact WHERE familyName='Smith'
</pre></div>

	@param <E> メイン・テーブルのエンティティ・クラス

	@since 1.0
	@author Masato Kokubo
*/
public class Sql<E> implements SqlEntityInfo<E> {
	/**
		データベース・ハンドラを返します。

		@return データベース・ハンドラ
	*/
	public static Database getDatabase() {
		return null;
	}

	/**
		データベース・ハンドラを設定します。

		@param database データベース・ハンドラ

		@throws NullPointerException <b>database</b> が <b>null</b> の場合
	*/
	public static void setDatabase(Database database) {
	}

	/**
		コネクション・サプライヤーを返します。

		@return コネクション・サプライヤー
	*/
	public static ConnectionSupplier getConnectionSupplier() {
		return null;
	}

	/**
		コネクション・サプライヤーを設定します。

		@param supplier コネクション・サプライヤー

		@throws NullPointerException <b>supplier</b> が <b>null</b> の場合
	*/
	public static void setConnectionSupplier(ConnectionSupplier supplier) {
	}

	/**
		指定のエンティティ・クラスのエンティティ情報を返します。

		@param <E> エンティティ・クラス

		@param entityClass エンティティ・クラス

		@return エンティティ情報

		@throws NullPointerException <b>entityClass</b> が <b>null</b> の場合
	*/
	public static <E> EntityInfo<E> getEntityInfo(Class<E> entityClass) {
		return null;
	}

	/**
		<b>Sql</b> を構築します。

		@param entityClass エンティティ・クラス

		@throws NullPointerException <b>entityClass</b> が <b>null</b> の場合
	*/
	public Sql(Class<E> entityClass) {
	}

	/**
		<b>Sql</b> を構築します。

		@param entityClass エンティティ・クラス
		@param tableAlias テーブルの別名

		@throws NullPointerException <b>entityClass</b> または <b>tableAlias</b> が <b>null</b> の場合
	*/
	public Sql(Class<E> entityClass, String tableAlias) {
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public EntityInfo<E> entityInfo() {
		return null;
	}

	/**
		エンティティ・クラスを返します。

		@return エンティティ・クラス
	*/
	public Class<E> entityClass() {
		return null;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public String tableAlias() {
		return null;
	}

	/**
		{@inheritDoc}
	*/
	@Override
	public E entity() {
		return null;
	}

	/**
		<b>Expression</b> クラスで参照されるエンティティを設定します。

		@param entity エンティティ

		@return このオブジェクト
	*/
	public Sql<E> setEntity(E entity) {
		return this;
	}

	/**
		SELECT SQL に <b>DISTINCT</b> を追加する事を指定します。

		@return このオブジェクト
	*/
	public Sql<E> distinct() {
		return null;
	}

	/**
		SELECT SQL に <b>DISTINCT</b> が追加されるかどうかを返します。

		@return 追加されるなら <b>true</b>、そうでなければ <b>false</b>
	*/
	public boolean isDistinct() {
		return false;
	}

	/**
		SELECT SQL のカラムを指定します。
		カラムに対応するプロパティ名を指定してください。
		<b>"*"</b> または <b>"<i>テーブル別名</i>.*"</b> で指定する事もできます。
		このメソッドがコールされない場合は、<b>"*"</b> が指定されたのと同様になります。

		@param columns カラムに関連するプロパティ名の配列

		@return このオブジェクト

		@throws NullPointerException <b>columns</b> または <b>columns</b> の要素が <b>null</b> の場合
	*/
	public Sql<E> columns(String... columns) {
		return null;
	}

	/**
		SELECT SQL で使用されるカラム列のセットを返します。

		@return columns メソッドで指定されたプロパティ名のセット
	*/
	public Set<String> getColumns() {
		return null;
	}

	/**
		プロパティ名に関連するカラムに式を関連付けします。<br>
		式が空の場合、以前のこのプロパティ名の関連付けを解除します。

		@param propertyName プロパティ名
		@param expression 式

		@return このオブジェクト

		@throws NullPointerException <b>propertyName</b> または <b>expression</b> が <b>null</b> の場合
	*/
	public Sql<E> expression(String propertyName, Expression expression) {
		return null;
	}

	/**
		プロパティ名に関連するカラムに式を関連付けします。<br>
		式が空の場合、以前のこのプロパティ名の関連付けを解除します。

		@param propertyName プロパティ名
		@param content 式の文字列内容
		@param arguments 式に埋め込む引数配列

		@return このオブジェクト

		@throws NullPointerException <b>propertyName</b>, <b>content</b> または <b>arguments</b> が <b>null</b> の場合
	*/
	public Sql<E> expression(String propertyName, String content, Object... arguments) {
		return null;
	}

	/**
		プロパティ名に関連する式を返します。<br>
		関連する式がない場合は、<b>Expression.EMPTY</b> を返します。

		@param propertyName プロパティ名

		@return プロパティ名に関連する式または <b>Expression.EMPTY</b>

		@throws NullPointerException <b>propertyName</b> が <b>null</b> の場合
	*/
	public Expression getExpression(String propertyName) {
		return null;
	}

	/**
		<b>INNER JOIN</b> で結合するテーブルの情報を追加します。

		@param <JE> 結合するエンティティ・クラス

		@param entityClass 結合するエンティティ・クラス
		@param tableAlias 結合するテーブルの別名
		@param on 結合条件式

		@return このオブジェクト

		@throws NullPointerException <b>entityClass</b>, <b>tableAlias</b> または <b>on</b> が <b>null</b> の場合
	*/
	public <JE> Sql<E> innerJoin(Class<JE> entityClass, String tableAlias, Condition on) {
		return null;
	}

	/**
		<b>INNER JOIN</b> で結合するテーブルの情報を追加します。

		@param <JE> 結合するエンティティ・クラス

		@param entityClass 結合するエンティティ・クラス
		@param tableAlias 結合するテーブルの別名
		@param on 結合条件式
		@param arguments 結合条件式の引数

		@return このオブジェクト

		@throws NullPointerException <b>entityClass</b>, <b>tableAlias</b>, <b>on</b> または <b>arguments</b> が <b>null</b> の場合
	*/
	public <JE> Sql<E> innerJoin(Class<JE> entityClass, String tableAlias, String on, Object... arguments) {
		return null;
	}

	/**
		<b>LEFT OUTER JOIN</b> で結合するテーブルの情報を追加します。

		@param <JE> 結合するエンティティ・クラス

		@param entityClass 結合するエンティティ・クラス
		@param tableAlias 結合するテーブルの別名
		@param on 結合条件

		@return このオブジェクト

		@throws NullPointerException <b>entityClass</b>, <b>tableAlias</b>, <b>on</b> または <b>arguments</b> が <b>null</b> の場合
	*/
	public <JE> Sql<E> leftJoin(Class<JE> entityClass, String tableAlias, Condition on) {
		return null;
	}

	/**
		<b>LEFT OUTER JOIN</b> で結合するテーブルの情報を追加します。

		@param <JE> 結合するエンティティ・クラス

		@param entityClass 結合するエンティティ・クラス
		@param tableAlias 結合するテーブルの別名
		@param on 結合条件式
		@param arguments 結合条件式の引数

		@return このオブジェクト

		@throws NullPointerException <b>entityClass</b>, <b>tableAlias</b>, <b>on</b> または <b>arguments</b> が <b>null</b> の場合
	*/
	public <JE> Sql<E> leftJoin(Class<JE> entityClass, String tableAlias, String on, Object... arguments) {
		return null;
	}

	/**
		<b>RIGHT OUTER JOIN</b> で結合するテーブルの情報を追加します。

		@param <JE> 結合するエンティティ・クラス

		@param entityClass 結合するエンティティ・クラス
		@param tableAlias 結合するテーブルの別名
		@param on 結合条件

		@return このオブジェクト

		@throws NullPointerException <b>entityClass</b>, <b>tableAlias</b>, <b>on</b> または <b>arguments</b> が <b>null</b> の場合
	*/
	public <JE> Sql<E> rightJoin(Class<JE> entityClass, String tableAlias, Condition on) {
		return null;
	}

	/**
		<b>RIGHT OUTER JOIN</b> で結合するテーブルの情報を追加します。

		@param <JE> 結合するエンティティ・クラス

		@param entityClass 結合するエンティティ・クラス
		@param tableAlias 結合するテーブルの別名
		@param on 結合条件式
		@param arguments 結合条件式の引数

		@return このオブジェクト

		@throws NullPointerException <b>entityClass</b>, <b>tableAlias</b>, <b>on</b> または <b>arguments</b> が <b>null</b> の場合
	*/
	public <JE> Sql<E> rightJoin(Class<JE> entityClass, String tableAlias, String on, Object... arguments) {
		return null;
	}

	/**
		追加された結合情報のリストを返します。

		@return JOIN 情報リスト
	*/
	public List<JoinInfo<?>> getJoinInfos() {
		return null;
	}

	/**
		<b>WHERE</b> 条件を指定します。

		@param condition 条件

		@return このオブジェクト

		@throws NullPointerException <b>condition</b> が <b>null</b> の場合
	*/
	public Sql<E> where(Condition condition) {
		return this;
	}

	/**
		<b>WHERE</b> 条件を指定します。

		@param content 条件式
		@param arguments 条件式の引数

		@return このオブジェクト

		@throws NullPointerException <b>content</b> または <b>arguments</b> が <b>null</b> の場合

		@see org.lightsleep.component.Condition#of(java.lang.String, java.lang.Object...)
	*/
	public Sql<E> where(String content, Object... arguments) {
		return this;
	}

	/**
		<b>WHERE</b> 条件をエンティティ条件で指定します。

		@param entity エンティティ

		@return this

		@see org.lightsleep.component.Condition#of(java.lang.Object)
	*/
	public Sql<E> where(E entity) {
		return this;
	}

	/**
		<b>WHERE</b> 条件をサブクエリで指定します。

		@param <SE> サブクエリの対象テーブルのエンティティ・クラス

		@param content サブクエリの SELECT SQL より左側の式
		@param subSql サブクエリの SELECT SQL 部分

		@return このオブジェクト

		@throws NullPointerException <b>content</b> または <b>subSql</b> が <b>null</b> の場合

		@see org.lightsleep.component.Condition#of(java.lang.String, Sql, Sql)
	*/
	public <SE> Sql<E> where(String content, Sql<SE> subSql) {
		return this;
	}

	/**
		指定されている <b>WHERE</b> 条件を返します。

		@return <b>WHERE</b> 条件
	*/
	public Condition getWhere() {
		return null;
	}

	/**
		<b>having</b> メソッドのコール後であれば、<b>HAVING</b> 条件に、
		そうでなければ <b>WHERE</b> 条件に <b>AND</b> で条件を追加します。

		@param condition 条件

		@return このオブジェクト

		@throws NullPointerException <b>condition</b> が <b>null</b> の場合
	*/
	public Sql<E> and(Condition condition) {
		return this;
	}

	/**
		<b>having</b> メソッドのコール後であれば、<b>HAVING</b> 条件に、
		そうでなければ <b>WHERE</b> 条件に <b>AND</b> で式条件を追加します。

		@param content 条件式
		@param arguments 条件式の引数

		@return このオブジェクト

		@throws NullPointerException <b>content</b> または <b>arguments</b> が <b>null</b> の場合

		@see org.lightsleep.component.Condition#of(java.lang.String, java.lang.Object...)
	*/
	public Sql<E> and(String content, Object... arguments) {
		return this;
	}

	/**
		<b>having</b> メソッドのコール後であれば、<b>HAVING</b> 条件に、
		そうでなければ <b>WHERE</b> 条件に <b>AND</b> でサブクエリ条件を追加します。

		@param <SE> サブクエリの対象テーブルのエンティティ・クラス

		@param content サブクエリの SELECT SQL より左側の式
		@param subSql サブクエリの SELECT SQL 部分

		@return このオブジェクト

		@see org.lightsleep.component.Condition#of(java.lang.String, Sql, Sql)
	*/
	public <SE> Sql<E> and(String content, Sql<SE> subSql) {
		return this;
	}

	/**
		<b>having</b> メソッドのコール後であれば、<b>HAVING</b> 条件に、
		そうでなければ <b>WHERE</b> 条件に <b>OR</b> で条件を追加します。

		@param condition 条件

		@return このオブジェクト

		@throws NullPointerException <b>condition</b> が <b>null</b> の場合
	*/
	public Sql<E> or(Condition condition) {
		return this;
	}

	/**
		<b>having</b> メソッドのコール後であれば、<b>HAVING</b> 条件に、
		そうでなければ <b>WHERE</b> 条件に <b>OR</b> で式条件を追加します。

		@param content 条件式
		@param arguments 条件式の引数

		@return このオブジェクト

		@throws NullPointerException <b>content</b> または <b>arguments</b> が <b>null</b> の場合

		@see org.lightsleep.component.Condition#of(java.lang.String, java.lang.Object...)
	*/
	public Sql<E> or(String content, Object... arguments) {
		return this;
	}

	/**
		<b>having</b> メソッドのコール後であれば、<b>HAVING</b> 条件に、
		そうでなければ <b>WHERE</b> 条件に <b>OR</b> でサブクエリ条件を追加します。

		@param <SE> サブクエリの対象テーブルのエンティティ・クラス

		@param content サブクエリの SELECT SQL より左側の式
		@param subSql サブクエリの SELECT SQL 部分

		@return このオブジェクト

		@throws NullPointerException <b>content</b> または <b>subSql</b> が <b>null</b> の場合

		@see org.lightsleep.component.Condition#of(java.lang.String, Sql, Sql)
	*/
	public <SE> Sql<E> or(String content, Sql<SE> subSql) {
		return this;
	}

	/**
		<b>GROUP BY</b> 式を指定します。

		@param content 式
		@param arguments 式の引数

		@return このオブジェクト

		@throws NullPointerException <b>content</b> または <b>arguments</b> が <b>null</b> の場合
	*/
	public Sql<E> groupBy(String content, Object... arguments) {
		return this;
	}

	/**
		指定された <b>GROUP BY</b> 情報を返します。

		@return <b>GROUP BY</b> 情報
	*/
	public GroupBy getGroupBy() {
		return null;
	}

	/**
		<b>HAVING</b> 条件を指定します。

		@param condition 条件

		@return このオブジェクト

		@throws NullPointerException <b>condition</b> が <b>null</b> の場合
	*/
	public Sql<E> having(Condition condition) {
		return this;
	}

	/**
		<b>HAVING</b> 条件を指定します。

		@param content 条件式
		@param arguments 条件式の引数

		@return このオブジェクト

		@throws NullPointerException <b>content</b> または <b>arguments</b> が <b>null</b> の場合

		@see org.lightsleep.component.Condition#of(java.lang.String, java.lang.Object...)
	*/
	public Sql<E> having(String content, Object... arguments) {
		return this;
	}

	/**
		指定されている <b>HAVING</b> 条件を返します。

		@return HAVING 条件
	*/
	public Condition getHaving() {
		return null;
	}

	/**
		<b>ORDER BY</b> を指定します。

		@param content 式
		@param arguments 式の引数

		@return このオブジェクト

		@throws NullPointerException <b>content</b> または <b>arguments</b> が <b>null</b> の場合
	*/
	public Sql<E> orderBy(String content, Object... arguments) {
		return this;
	}

	/**
		ソート順を昇順に指定します。

		@return このオブジェクト
	*/
	public Sql<E> asc() {
		return this;
	}

	/**
		ソート順を降順に指定します。

		@return このオブジェクト
	*/
	public Sql<E> desc() {
		return this;
	}

	/**
		指定されている <b>ORDER BY</b> 情報を返します。

		@return ORDER BY 情報
	*/
	public OrderBy getOrderBy() {
		return null;
	}

	/**
		SELECT SQL の <b>LIMIT</b> 値を指定します。

		@param limit <b>LIMIT</b> 値

		@return このオブジェクト
	*/
	public Sql<E> limit(int limit) {
		return this;
	}

	/**
		指定されている <b>LIMIT</b> 値を返します。

		@return <b>LIMIT</b> 値
	*/
	public int getLimit() {
		return 0;
	}

	/**
		SELECT SQL の <b>OFFSET</b> 値を指定します。

		@param offset <b>OFFSET</b> 値

		@return このオブジェクト
	*/
	public Sql<E> offset(int offset) {
		return this;
	}

	/**
		指定されている <b>OFFSET</b> 値を返します。

		@return <b>OFFSET</b> 値
	*/
	public int getOffset() {
		return 0;
	}

	/**
		SELECT SQL に <b>FOR UPDATE</b> を追加する事を指定します。

		@return このオブジェクト
	*/
	public Sql<E> forUpdate() {
		return this;
	}

	/**
		SELECT SQL に <b>FOR UPDATE</b> が追加されるかどうかを返します。

		@return <b>FOR UPDATE</b> が追加されるなら <b>true</b>、そうでなければ <b>false</b>
	*/
	public boolean isForUpdate() {
		return false;
	}

	/**
		SELECT SQL に <b>NO WAIT</b> を追加する事を指定します。

		@return このオブジェクト
	*/
	public Sql<E> noWait() {
		return this;
	}

	/**
		SELECT SQL に <b>NO WAIT</b> が追加されるどうかを返します。

		@return <b>NO WAIT</b> が追加されるなら <b>true</b>、そうでなければ <b>false</b>
	*/
	public boolean isNoWait() {
		return false;
	}

	/**
		<b>condition</b> が true なら <b>action</b> を実行します。

		@param condition 条件
		@param action <b>condition</b> が true の場合に実行するアクション

		@return このオブジェクト
	*/
	public Sql<E> doIf(boolean condition, Consumer<Sql<E>> action) {
		return this;
	}

	/**
		<b>condition</b> が true なら <b>action</b> を実行し、そうでなければ <b>elseAction</b> を実行します。

		@param condition 条件
		@param action <b>condition</b> が true の場合に実行するアクション
		@param elseAction <b>condition</b> が false の場合に実行するアクション

		@return このオブジェクト
	*/
	public Sql<E> doIf(boolean condition, Consumer<Sql<E>> action, Consumer<Sql<E>> elseAction) {
		return this;
	}

	/**
		指定のテーブル別名に対応する <b>SqlEntityInfo</b> オブジェクトを返します。

		@param tableAlias テーブル別名

		@return SqlEntityInfo オブジェクト

		@throws NullPointerException <b>tableAlias</b> が <b>null</b> の場合
	*/
	public SqlEntityInfo<?> getSqlEntityInfo(String tableAlias) {
		return null;
	}

	/**
		SqlEntityInfo オブジェクトを追加します。

		@param sqlEntityInfo SqlEntityInfo オブジェクト
	*/
	public void addSqlEntityInfo(SqlEntityInfo<?> sqlEntityInfo) {
	}

	/**
		テーブルを結合しない SELECT SQL を生成して実行します。

		@param connection データベース・コネクション
		@param consumer エンティティのコンシューマ

		@throws NullPointerException <b>connection</b> または <b>consumer</b> が <b>null</b> の場合
		@throws RuntimeSQLException データベース・アクセス・エラーが発生した場合
	*/
	public void select(Connection connection, Consumer<? super E> consumer) {
	}

	/**
		1つのテーブルを結合する SELECT SQL を生成して実行します。

		@param <JE1> 結合テーブル1のエンティティ・クラス

		@param connection データベース・コネクション
		@param consumer メイン・テーブルのエンティティのコンシューマ
		@param consumer1 結合テーブル1のエンティティのコンシューマ


		@throws NullPointerException <b>connection</b>, <b>consumer</b> または <b>consumer1</b> が <b>null</b> の場合
		@throws IllegalStateException 結合テーブル情報がない場合
		@throws RuntimeSQLException データベース・アクセス・エラーが発生した場合
	*/
	public <JE1> void select(Connection connection,
		Consumer<? super E> consumer,
		Consumer<? super JE1> consumer1) {
	}

	/**
		2つのテーブルを結合する SELECT SQL を生成して実行します。

		@param <JE1> 結合テーブル1のエンティティ・クラス
		@param <JE2> 結合テーブル2のエンティティ・クラス

		@param connection データベース・コネクション
		@param consumer メイン・テーブルのエンティティのコンシューマ
		@param consumer1 結合テーブル1のエンティティのコンシューマ
		@param consumer2 結合テーブル2のエンティティのコンシューマ

		@throws NullPointerException <b>connection</b>, <b>consumer</b>, <b>consumer1</b> または <b>consumer2</b> が <b>null</b> の場合
		@throws IllegalStateException 結合テーブル情報が2より少ない場合
		@throws RuntimeSQLException データベース・アクセス・エラーが発生した場合
	*/
	public <JE1, JE2> void select(Connection connection,
			Consumer<? super  E > consumer,
			Consumer<? super JE1> consumer1,
			Consumer<? super JE2> consumer2) {
	}

	/**
		3つのテーブルを結合する SELECT SQL を生成して実行します。

		@param <JE1> 結合テーブル1のエンティティ・クラス
		@param <JE2> 結合テーブル2のエンティティ・クラス
		@param <JE3> 結合テーブル3のエンティティ・クラス

		@param connection データベース・コネクション
		@param consumer メイン・テーブルのエンティティのコンシューマ
		@param consumer1 結合テーブル1のエンティティのコンシューマ
		@param consumer2 結合テーブル2のエンティティのコンシューマ
		@param consumer3 結合テーブル3のエンティティのコンシューマ

		@throws NullPointerException <b>connection</b>, <b>consumer</b>, <b>consumer1</b>, <b>consumer2</b> または <b>consumer3</b> が <b>null</b> の場合
		@throws IllegalStateException 結合テーブル情報が3より少ない場合
		@throws RuntimeSQLException データベース・アクセス・エラーが発生した場合
	*/
	public <JE1, JE2, JE3> void select(Connection connection,
			Consumer<? super  E > consumer,
			Consumer<? super JE1> consumer1,
			Consumer<? super JE2> consumer2,
			Consumer<? super JE3> consumer3) {
	}

	/**
		4つのテーブルを結合する SELECT SQL を生成して実行します。

		@param <JE1> 結合テーブル1のエンティティ・クラス
		@param <JE2> 結合テーブル2のエンティティ・クラス
		@param <JE3> 結合テーブル3のエンティティ・クラス
		@param <JE4> 結合テーブル4のエンティティ・クラス

		@param connection データベース・コネクション
		@param consumer メイン・テーブルのエンティティのコンシューマ
		@param consumer1 結合テーブル1のエンティティのコンシューマ
		@param consumer2 結合テーブル2のエンティティのコンシューマ
		@param consumer3 結合テーブル3のエンティティのコンシューマ
		@param consumer4 結合テーブル4のエンティティのコンシューマ

		@throws NullPointerException <b>connection</b>, <b>consumer</b>, <b>consumer1</b>, <b>consumer2</b>, <b>consumer3</b> または <b>consumer4</b> が <b>null</b> の場合
		@throws IllegalStateException 結合テーブル情報が4より少ない場合
		@throws RuntimeSQLException データベース・アクセス・エラーが発生した場合
	*/
	public <JE1, JE2, JE3, JE4> void select(Connection connection,
			Consumer<? super  E > consumer,
			Consumer<? super JE1> consumer1,
			Consumer<? super JE2> consumer2,
			Consumer<? super JE3> consumer3,
			Consumer<? super JE4> consumer4) {
	}

	/**
		SELECT SQL を生成して実行します。<br>
		検索されない場合は、<b>Optional.empty()</b> を返します。

		@param connection データベース・コネクション

		@return 検索されたエンティティ (検索されない場合は <b>Optional.empty()</b>)

		@throws NullPointerException <b>connection</b> が <b>null</b> の場合
		@throws RuntimeSQLException データベース・アクセス・エラーが発生した場合
		@throws ManyRowsException 複数行が検索された場合
	*/
	public Optional<E> select(Connection connection) {
		return Optional.empty();
	}

	/**
		SELECT COUNT(*) SQL を生成して実行します。

		@param connection データベース・コネクション

		@return 実行結果行数

		@throws NullPointerException connection が <b>null</b> の場合
		@throws RuntimeSQLException データベース・アクセス・エラーが発生した場合
	*/
	public int selectCount(Connection connection) {
		return 0;
	}

	/**
		INSERT SQL を生成して実行します。

		@param connection データベース・コネクション
		@param entity 挿入対象のエンティティ

		@return 実行結果行数

		@throws NullPointerException <b>connection</b> または <b>entity</b> が <b>null</b> の場合
		@throws RuntimeSQLException データベース・アクセス・エラーが発生した場合
	*/
	public int insert(Connection connection, E entity) {
		return 0;
	}

	/**
		<b>entities</b> の各要素毎に INSERT SQL を生成して実行します。

		@param connection データベース・コネクション
 		@param entities 挿入対象のエンティティの <b>Iterable</b>

		@return 実行結果行数

		@throws NullPointerException <b>connection</b>, <b>entities</b> または <b>entities</b> の要素のいずれか <b>null</b> の場合
		@throws RuntimeSQLException データベース・アクセス・エラーが発生した場合
	*/
	public int insert(Connection connection, Iterable<? extends E> entities) {
		return 0;
	}

	/**
		UPDATE SQL を生成して実行します。<br>
		<b>WHERE</b> 条件が指定されている場合は、その条件で更新が行われます。<br>
		対象のテーブルのすべての行を更新するには、<b>WHERE</b> 条件に <b>Condition.ALL</b> を指定してください。

		@param connection データベース・コネクション
		@param entity 更新対象のエンティティ

		@return 実行結果行数

		@throws NullPointerException <b>connection</b> または <b>entity</b> が <b>null</b> の場合
		@throws RuntimeSQLException データベース・アクセス・エラーが発生した場合

		@see org.lightsleep.component.Condition#ALL
	*/
	public int update(Connection connection, E entity) {
		return 0;
	}

	/**
		<b>entities</b> の各要素毎に UPDATE SQL を生成して実行します。<br>
		<b>WHERE</b> 条件が指定されている場合でも、各エンティティ毎に <b>new EntityCondition(entity)</b> が指定されます。

		@param connection データベース・コネクション
		@param entities 更新対象のエンティティの <b>Iterable</b>

		@return 実行結果行数

		@throws NullPointerException <b>connection</b>, <b>entityStream</b> または <b>entityStream</b> の要素のいずれかが <b>null</b> の場合
		@throws RuntimeSQLException データベース・アクセス・エラーが発生した場合
	*/
	public int update(Connection connection, Iterable<? extends E> entities) {
		return 0;
	}

	/**
		DELETE SQL を生成して実行します。<br>
		<b>WHERE</b> 条件が指定されていない場合は実行されません。<br>
		対象のテーブルのすべての行を削除するには、<b>WHERE</b> 条件に <b>Condition.ALL</b> を指定してください。

		@param connection データベース・コネクション

		@return 実行結果行数

		@throws NullPointerException <b>connection</b> が <b>null</b> の場合
		@throws RuntimeSQLException データベース・アクセス・エラーが発生した場合

		@see org.lightsleep.component.Condition#ALL
	*/
	public int delete(Connection connection) {
		return 0;
	}

	/**
		DELETE SQL を生成して実行します。

		@param connection データベース・コネクション
		@param entity 削除対象のエンティティ

		@return 実行結果行数

		@throws NullPointerException <b>connection</b> または <b>entity</b> が <b>null</b> の場合
		@throws RuntimeSQLException データベース・アクセス・エラーが発生した場合
	*/
	public int delete(Connection connection, E entity) {
		return 0;
	}

	/**
		<b>entities</b> の各要素毎に DELETE SQL を生成して実行します。

		@param connection データベース・コネクション
		@param entities 削除対象のエンティティの <b>Iterable</b>

		@return 実行結果行数

		@throws NullPointerException <b>connection</b>, <b>entityStream</b> または <b>entityStream</b> の要素のいずれか <b>null</b> の場合
		@throws RuntimeSQLException データベース・アクセス・エラーが発生した場合
	*/
	public int delete(Connection connection, Iterable<? extends E> entities) {
		return 0;
	}

	/**
		メイン・テーブルの <b>ColumnInfo</b> ストリームを返します。<br>
		<br>
		<i>このメソッドは、内部的に使用されます。</i>

		@return <b>ColumnInfo</b> ストリーム
	*/
	public Stream<ColumnInfo> columnInfoStream() {
		return null;
	}

	/**
		メイン・テーブルの選択対象のカラムの <b>ColumnInfo</b> ストリームを返します。<br>
		<br>
		<i>このメソッドは、内部的に使用されます。</i>

		@return <b>ColumnInfo</b> ストリーム
	*/
	public Stream<ColumnInfo> selectedColumnInfoStream() {
		return null;
	}

	/**
		メイン・テーブルと結合テーブルの <b>SqlEntityInfo</b> ストリームを返します。<br>
		<br>
		<i>このメソッドは、内部的に使用されます。</i>

		@return <b>SqlEntityInfo</b> ストリーム
	*/
	public Stream<SqlEntityInfo<?>> sqlEntityInfoStream() {
		return null;
	}

	/**
		メイン・テーブルと結合テーブルの全カラムの <b>SqlColumnInfo</b> ストリームを返します。<br>
		<br>
		<i>このメソッドは、内部的に使用されます。</i>

		@return <b>SqlColumnInfo</b> ストリーム
	*/
	public Stream<SqlColumnInfo> joinSqlColumnInfoStream() {
		return null;
	}

	/**
		メイン・テーブルと結合テーブルの選択対象のカラムの <b>SqlColumnInfo</b> ストリームを返します。<br>
		<br>
		<i>このメソッドは、内部的に使用されます。</i>

		@return <b>SqlColumnInfo</b> ストリーム
	*/
	public Stream<SqlColumnInfo> selectedJoinSqlColumnInfoStream() {
		return null;
	}
}
