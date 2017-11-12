// PostLoad.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

/**
 * エンティティ･クラスがこのインターフェースを実装している場合、
 * そのエンティティ･クラスの<b>postLoad</b>メソッドが 各行の取得後に実行されます。
 *
 * @since 1.6.0
 * @author Masato Kokubo
 */
public interface PostLoad {
	/**
	 * このメソッドは、各行の取得後に実行されます。
	 */
	void postLoad();
}
