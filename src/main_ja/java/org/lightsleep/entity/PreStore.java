// PreStore.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

/**
 * エンティティクラスがこのインターフェースを実装している場合、
 * <b>Sql&lt;E&gt;</b>クラスの<b>insert(E)</b>, <b>insert(Iterable)</b>,
 * <b>update(E)</b>および<b>update(Iterable)</b>メソッドから
 * INSERTまたはUPDATE SQLの実行前に<b>preStore</b>メソッドが呼び出されます。
 *
 * <p>
 * @deprecated リリース 3.2.0 より。
 * 代わりに{@link PreInsert}と{@link PreUpdate}の両方のインターフェースを使用してください。
 * </p>
 *
 * @since 1.6.0
 * @author Masato Kokubo
 */
@Deprecated
public interface PreStore {
    /**
     * INSERTまたはUPDATE SQLの実行前に呼び出されます。
     */
    void preStore();
}
