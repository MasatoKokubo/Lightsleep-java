// Condition.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.List;
import java.util.stream.Stream;

import org.lightsleep.Sql;

/**
 * 条件インタフェースです。
 *
 * @since 1.0.0
 * @author Masato Kokubo
 */
public interface Condition extends SqlComponent {
	/** 空の条件 */
	static final Condition EMPTY = null;

	/** 全行を対象する条件 */
	static final Condition ALL = null;

// 1.8.8
//	/**
//	 * {@inheritDoc}
//	 *
//	 * @since 1.8.2
//	 */
//	@Override
//	default boolean isEmpty() {
//		return false;
//	}
////

// 1.8.3
//	/**
//	 *	条件式を生成して返します。
//	 *
//	 * @see Expression
//	 *
//	 * @param content 条件式の文字列内容
//	 *
//	 *	@return 条件式
//	 *
//	 * @throws NullPointerException <b>content</b> が null の場合
//	 */
//	static Condition of(String content) {
//		return null;
//	}
////

	/**
	 * 条件式を生成して返します。
	 *
	 * @param content 条件式の文字列内容
	 * @param arguments 条件式に埋め込む引数配列
	 * @return 条件式
	 *
	 * @throws NullPointerException <b>content</b> または <b>arguments</b> が null の場合
	 *
	 * @see Expression
	 */
	static Condition of(String content, Object... arguments) {
		return null;
	}

	/**
	 * エンティティ条件を生成して返します。
	 *
	 * @param <E> エンティティ・クラス
	 * @param entity エンティティ
	 * @return エンティティ条件
	 *
	 * @see EntityCondition
	 */
	static <E> Condition of(E entity) {
		return null;
	}

	/**
	 * サブクエリ条件を生成して返します。
	 *
	 * @param <E> 外側のクエリの対象テーブルに対応するエンティティ・クラス
	 * @param <SE> サブクエリの対象テーブルに対応するエンティティ・クラス
	 * @param content サブクエリの SELECT 文より左部分の式の文字列内容
	 * @param outerSql 外側の Sql オブジェクト
	 * @param subSql サブクエリ用の Sql オブジェクト
	 * @return サブクエリ条件
	 *
	 * @throws NullPointerException <b>content</b>, <b>outerSql</b> または <b>subSql</b> が null の場合
	 *
	 * @see SubqueryCondition
	 */
	static <E, SE> Condition of(String content, Sql<E> outerSql, Sql<SE> subSql) {
		return null;
	}

	/**
	 * 否定条件を返します。
	 *
	 * @return NOT(この条件)
	 *
	 * @see Not
	 */
	default Condition not() {
		return null;
	}

	/**
	 * (この条件 AND 指定の条件) を返します。
	 *
	 * @param condition 条件
	 * @return この条件 AND 指定の条件
	 *
	 * @throws NullPointerException <b>condition</b> が null の場合
	 *
	 * @see And
	 */
	default Condition and(Condition condition) {
		return null;
	}

	/**
	 * (この条件 AND 指定の条件) を返します。
	 *
	 * @param content 条件式の文字列内容
	 * @param arguments 条件式に埋め込む引数配列
	 *
	 * @return この条件 AND 指定の条件
	 *
	 * @throws NullPointerException <b>content</b> または <b>arguments</b> が null の場合
	 *
	 * @see And
	 * @see Expression
	 */
	default Condition and(String content, Object... arguments) {
		return null;
	}

	/**
	 * (この条件 AND 指定の条件) を返します。
	 *
	 * @param <E> 外側のクエリの対象テーブルに対応するエンティティ・クラス
	 * @param <SE> サブクエリの対象テーブルに対応するエンティティ・クラス
	 * @param content サブクエリの SELECT 文より左部分の式の文字列内容
	 * @param outerSql 外側の Sql オブジェクト
	 * @param subSql サブクエリ用の Sql オブジェクト
	 * @return この条件 AND 指定の条件
	 *
	 * @throws NullPointerException <b>content</b>, <b>outerSql</b> または <b>subSql</b> が null の場合
	 *
	 * @see And
	 * @see SubqueryCondition
	 */
	default <E, SE> Condition and(String content, Sql<E> outerSql, Sql<SE> subSql) {
		return null;
	}

	/**
	 * (この条件 OR 指定の条件) を返します。
	 *
	 * @param condition 条件
	 * @return この条件 OR 指定の条件
	 *
	 * @throws NullPointerException <b>condition</b> が null の場合
	 *
	 * @see Or
	 */
	default Condition or(Condition condition) {
		return null;
	}

	/**
	 * (この条件 OR 指定の条件) を返します。
	 *
	 * @param content 条件式の文字列内容
	 * @param arguments 条件式に埋め込む引数配列
	 * @return この条件 OR 指定の条件
	 *
	 * @throws NullPointerException <b>content</b> または <b>arguments</b> が null の場合
	 *
	 * @see Or
	 * @see Expression
	 */
	default Condition or(String content, Object... arguments) {
		return null;
	}

	/**
	 * (この条件 OR 指定の条件) を返します。
	 *
	 * @param <E> 外側のクエリの対象テーブルに対応するエンティティ・クラス
	 * @param <SE> サブクエリの対象テーブルに対応するエンティティ・クラス
	 * @param content サブクエリの SELECT 文より左部分の式の文字列内容
	 * @param outerSql 外側の Sql オブジェクト
	 * @param subSql サブクエリ用の Sql オブジェクト
	 * @return この条件 OR 指定の条件
	 *
	 * @throws NullPointerException <b>content</b>, <b>outerSql</b> または <b>subSql</b> が null の場合
	 *
	 * @see Or
	 * @see SubqueryCondition
	 */
	default <E, SE> Condition or(String content, Sql<E> outerSql, Sql<SE> subSql) {
		return null;
	}

	/**
	 * <b>new And(conditions)</b> を最適化した条件を返します。
	 *
	 * @param conditions 条件のストリーム
	 * @return <b>new And(conditions)</b> を最適化した条件
	 *
	 * @throws NullPointerException <b>conditions</b> か <b>conditions</b> の要素のいずれかが null の場合
	 *
	 * @since 1.8.8
	 * @see And
	 * @see LogicalCondition#optimized()
	 */
	static Condition and(Stream<Condition> conditions) {
		return null;
	}

	/**
	 * <b>new And(conditions)</b> を最適化した条件を返します。
	 *
	 * @param conditions 条件のリスト
	 * @return <b>new And(conditions)</b> を最適化した条件
	 *
	 * @throws NullPointerException <b>conditions</b> か <b>conditions</b> の要素のいずれかが null の場合
	 *
	 * @since 1.8.8
	 * @see And
	 * @see LogicalCondition#optimized()
	 */
	static Condition and(List<Condition> conditions) {
		return null;
	}

	/**
	 * <b>new And(conditions)</b> を最適化した条件を返します。
	 *
	 * @param conditions 条件の配列
	 * @return <b>new And(conditions)</b> を最適化した条件
	 *
	 * @throws NullPointerException <b>conditions</b> か <b>conditions</b> の要素のいずれかが null の場合
	 *
	 * @since 1.8.8
	 * @see And
	 * @see LogicalCondition#optimized()
	 */
	static Condition and(Condition... conditions) {
		return null;
	}

	/**
	 * <b>new Or(conditions)</b> を最適化した条件を返します。
	 *
	 * @param conditions 条件のストリーム
	 * @return <b>new Or(conditions)</b> を最適化した条件
	 *
	 * @throws NullPointerException <b>conditions</b> か <b>conditions</b> の要素のいずれかが null の場合
	 *
	 * @since 1.8.8
	 * @see And
	 * @see LogicalCondition#optimized()
	 */
	static Condition or(Stream<Condition> conditions) {
		return null;
	}

	/**
	 * <b>new Or(conditions)</b> を最適化した条件を返します。
	 *
	 * @param conditions 条件のリスト
	 * @return <b>new Or(conditions)</b> を最適化した条件
	 *
	 * @throws NullPointerException <b>conditions</b> か <b>conditions</b> の要素のいずれかが null の場合
	 *
	 * @since 1.8.8
	 * @see Or
	 * @see LogicalCondition#optimized()
	 */
	static Condition or(List<Condition> conditions) {
		return null;
	}

	/**
	 * <b>new Or(conditions)</b> を最適化した条件を返します。
	 *
	 * @param conditions 条件の配列
	 * @return <b>new Or(conditions)</b> を最適化した条件
	 *
	 * @throws NullPointerException <b>conditions</b> か <b>conditions</b> の要素のいずれかが null の場合
	 *
	 * @since 1.8.8
	 * @see Or
	 * @see LogicalCondition#optimized()
	 */
	static Condition or(Condition... conditions) {
		return null;
	}
}
