// PostLoad.java
// (C) 2016 Masato Kokubo

package org.lightsleep.entity;

/**
 * エンティティ･クラスがこのインターフェースを実装している場合、
 * SELECT SQLを実行してエンティティを取得した後に<b>postLoad</b>メソッドが呼び出されます。
 *
 * <p>
 * @deprecated リリース 3.2.0 より。
 * 代わりに{@link PostSelect}インターフェースを使用してください。
 * </p>
 *
 * @since 1.6.0
 * @author Masato Kokubo
 */
@Deprecated
public interface PostLoad {
	/**
	 * SELECT SQLを実行してエンティティを取得した後に呼び出されます。
	 */
	void postLoad();
}
