// OrderBy.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.List;

import org.lightsleep.Sql;

/**
 * SQL の ORDER BY  を構成します。
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class OrderBy implements SqlComponent, Cloneable {
	/** 空の OrderBy */
	public static final OrderBy EMPTY = new OrderBy();

	/**
	 * OrderBy の構成要素です。
	 */
	public static class Element extends Expression {
		/**
		 * 	Element を構築します。
		 *
		 * @param content 式の文字列内容
		 * @param arguments 式に埋め込む引数配列
		 *
		 * @throws NullPointerException <b>content</b> または <b>arguments</b> が null の場合
		 */
		public Element(String content, Object... arguments) {
			super(content, arguments);
		}

		/**
		 * 昇順に設定します。
		 *
		 * @return このオブジェクト
		 */
		public Element asc() {
			return this;
		}

		/**
		 * 	降順に設定します。
		 *
		 * @return このオブジェクト
		 */
		public Element desc() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <E> String toString(Sql<E> sql, List<Object> parameters) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 *
		 * @since 1.9.1
		 */
		@Override
		public int hashCode() {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 *
		 * @since 1.9.1
		 */
		@Override
		public boolean equals(Object obj) {
			return false;
		}
	}

	/**
	 * 空の OrderBy を構築します。
	 */
	public OrderBy() {
	}

	/**
	 * 構成要素を追加します。
	 *
	 * @param element 追加する構成要素
	 *
	 * @return このオブジェクト
	 *
	 * @throws NullPointerException <b>element</b> が null の場合
	 */
	public OrderBy add(Element element) {
		return null;
	}

	/**
	 * 直前に追加した構成要素を昇順に設定します。
	 *
	 * @return このオブジェクト
	 *
	 * @throws IllegalStateException 構成要素がない場合
	 */
	public OrderBy asc() {
		return null;
	}

	/**
	 * 直前に追加した構成要素を昇順に設定します。
	 *
	 * @return このオブジェクト
	 *
	 * @throws IllegalStateException 構成要素がない場合
	 */
	public OrderBy desc() {
		return null;
	}

	/**
	 * 構成要素のリストを返します。
	 *
	 * @return 構成要素のリスト
	 */
	public List<Element> elements() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> String toString(Sql<E> sql, List<Object> parameters) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.9.1
	 */
	@Override
	public int hashCode() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.9.1
	 */
	@Override
	public boolean equals(Object obj) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @since 1.9.1
	 */
	@Override
	public OrderBy clone() {
		return null;
	}
}
