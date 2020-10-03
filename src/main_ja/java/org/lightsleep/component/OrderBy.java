// OrderBy.java
// (C) 2016 Masato Kokubo

package org.lightsleep.component;

import java.util.List;

import org.lightsleep.Sql;
import org.lightsleep.database.Database;

/**
 * SQLのORDER BYを構成します。
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class OrderBy implements SqlComponent, Cloneable {
    /**
     * OrderByの構成要素です。
     */
    public static class Element extends Expression {
        /**
         * Elementを構築します。
         *
         * @param content 式の文字列内容
         * @param arguments 式に埋め込む引数配列
         *
         * @throws NullPointerException <b>content</b>または<b>arguments</b>が<b>null</b>の場合
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
         * 降順に設定します。
         *
         * @return このオブジェクト
         */
        public Element desc() {
            return this;
        }

        @Override
        public <E> String toString(Database database, Sql<E> sql, List<Object> parameters) {
            return null;
        }

        /**
         * @since 1.9.1
         */
        @Override
        public int hashCode() {
            return 0;
        }

        /**
         * @since 1.9.1
         */
        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }

    /**
     * 空の OrderByを構築します。
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
     * @throws NullPointerException <b>element</b>が<b>null</b>の場合
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

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public <E> String toString(Database database, Sql<E> sql, List<Object> parameters) {
        return null;
    }

    /**
     * @since 1.9.1
     */
    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * @since 1.9.1
     */
    @Override
    public boolean equals(Object obj) {
        return false;
    }

    /**
     * @since 1.9.1
     */
    @Override
    public OrderBy clone() {
        return null;
    }
}
