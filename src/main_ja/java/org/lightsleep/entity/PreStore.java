// PreStore.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

/**
 * エンティティ･クラスがこのインターフェースを実装している場合、
 * そのエンティティ･クラスの<b>preStore</b>メソッドがINSERTまたはUPDATE SQLを実行する前に実行されます。
 *
 * @since 1.6.0
 * @author Masato Kokubo
 */
public interface PreStore {
	/**
	 * このメソッドは、INSERTまたはUPDATE SQLを実行する前に実行されます。
	 */
	void preStore();
}
