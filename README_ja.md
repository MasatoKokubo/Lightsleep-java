Lightsleep 2
===========

[[English]](README.md)

Lightsleep は、軽量の O/R マッピング・ライブラリで、Java 8 以降で利用できます。Java 7 以前には対応していません。
また Java Persistence API (JPA) との互換性はありません。

### 特徴

- Java 8 で追加された機能 (関数型インタフェース、Optional クラス) を使用した API 。
- メソッド名を SQL の予約語に似せてあるため、直観的に理解しやすい。
- Java Runtime と JDBC ドライバー以外に依存するライブラリがないため、バッチ処理にも使用しやすい。
- XML ファイル等によるマッピング定義ファイルは不要。
- 大規模なライブラリではないため、学習が比較的容易。

### 対応DBMS

- DB2
- MySQL
- Oracle Database
- PostgreSQL
- SQLite
- Microsoft SQL Server
- 標準SQL準拠DBMS

#### build.gradle での依存関係の記述例

```gradle:build.gradle
# build.gradle
repositories {
    jcenter()
}

dependencies {
    compile 'org.lightsleep:lightsleep:2.+' // 最新バージョンを使用する場合

    compile 'org.lightsleep:lightsleep:1.+' // 以前のバージョンを使用する場合
}
```

### Lightsleep で使用するエンティティ・クラスの定義例

```java:Contact.java
// Java
package org.lightsleep.example.java.entity;
import java.sql.Date;
import java.sql.Timestamp;
import org.lightsleep.entity.*;

public class Contact {
	@Key
	public int    id;
	public String lastName;
	public String firstName;
	public Date   birthday;

	@Insert("0") @Update("{updateCount}+1")
	public int updateCount;

	@Insert("CURRENT_TIMESTAMP") @NonUpdate
	public Timestamp createdTime;

	@Insert("CURRENT_TIMESTAMP") @Update("CURRENT_TIMESTAMP")
	public Timestamp updatedTime;
}
```

```groovy:Contact.groovy
// Groovy
package org.lightsleep.example.groovy.entity
import java.sql.Date
import java.sql.Timestamp
import org.lightsleep.entity.*

class Contact {
	@Key
	int    id
	String firstName
	String lastName
	Date   birthday

	@Insert('0') @Update('{updateCount}+1')
	int updateCount

	@Insert('CURRENT_TIMESTAMP') @NonUpdate
	Timestamp createdTime

	@Insert('CURRENT_TIMESTAMP') @Update('CURRENT_TIMESTAMP')
	Timestamp updatedTime
}
```

### Lightsleep の使用例

```java:Java
// Java (Lightsleep 2.x.x 使用)
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where("{lastName}={}", "Apple")
        .or   ("{lastName}={}", "Orange")
        .orderBy("{lastName}")
        .orderBy("{firstName}")
        .select(contacts::add)
);
```

```groovy:Groovy
// Groovy (Lightsleep 2.x.x 使用)
def contacts = []
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where('{lastName}={}', 'Apple')
        .or   ('{lastName}={}', 'Orange')
        .orderBy('{lastName}')
        .orderBy('{firstName}')
        .select({contacts << it})
}
```

```java:Java
// Java (Lightsleep 1.x.x 使用)
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class)
        .where("{lastName}={}", "Apple")
        .or   ("{lastName}={}", "Orange")
        .orderBy("{lastName}")
        .orderBy("{firstName}")
        .select(conn, contacts::add)
);
```

```groovy:Groovy
// Groovy (Lightsleep 1.x.x 使用)
def contacts = []
Transaction.execute {
    new Sql<>(Contact)
        .where('{lastName}={}', 'Apple')
        .or   ('{lastName}={}', 'Orange')
        .orderBy('{lastName}')
        .orderBy('{firstName}')
        .select(it, {contacts << it})
}
```

```sql
-- 生成される SQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple' OR lastName='Orange' ORDER BY lastName ASC, firstName ASC
```

### バージョン 1.9.2 からの変更点

##### 仕様変更
- 以下のアノテーションに対して `property` 要素を追加し `value` 要素の仕様を変更しました。
    - `KeyProperty`
    - `NonColumnProperty`
    - `NonInsertProperty`
    - `NonSelectProperty`
    - `NonUpdateProperty`

##### 追加されたメソッド
- `Sql` クラス
    - `connection(Connection connection)`
    - `doAlways(Consumer<Sql<E>> action)`
    - `select(Consumer<? super E> consumer)`
    - `selectAs(Class<R> resultClass, Consumer<? super R> consumer)`
    - `select(Consumer<? super E> consumer, Consumer<? super JE1> consumer1)`
    - `select(Consumer<? super  E > consumer, Consumer<? super JE1> consumer1, Consumer<? super JE2> consumer2)`
    - `select(Consumer<? super  E > consumer, Consumer<? super JE1> consumer1, Consumer<? super JE2> consumer2, Consumer<? super JE3> consumer3)`
    - `select(Consumer<? super  E > consumer, Consumer<? super JE1> consumer, Consumer<? super JE2> consumer2, Consumer<? super JE3> consumer3, Consumer<? super JE4> consumer4)`
    - `select()`
    - `selectAs(Class<R> resultClass)`
    - `selectCount()`
    - `insert(E entity)`
    - `insert(Iterable<? extends E> entities)`
    - `update(E entity)`
    - `update(Iterable<? extends E> entities)`
    - `delete()`
    - `delete(E entity)`
    - `delete(Iterable<? extends E> entities)`

##### 非推奨になったメソッド
- `Sql` クラス
    - `select(Connection connection, Consumer<? super E> consumer)`
    - `select(Connection connection, Consumer<? super E> consumer, Consumer<? super JE1> consumer1)`
    - `select(Connection connection, Consumer<? super  E > consumer, Consumer<? super JE1> consumer1, Consumer<? super JE2> consumer2)`
    - `select(Connection connection, Consumer<? super  E > consumer, Consumer<? super JE1> consumer1, Consumer<? super JE2> consumer2, Consumer<? super JE3> consumer3)`
    - `select(Connection connection, Consumer<? super  E > consumer, Consumer<? super JE1> consumer1, Consumer<? super JE2> consumer2, Consumer<? super JE3> consumer3, Consumer<? super JE4> consumer4)`
    - `select(Connection connection)`
    - `selectCount(Connection connection)`
    - `insert(Connection connection, E entity)`
    - `insert(Connection connection, Iterable<? extends E> entities)`
    - `update(Connection connection, E entity)`
    - `update(Connection connection, Iterable<? extends E> entities)`
    - `delete(Connection connection)`
    - `delete(Connection connection, E entity)`
    - `delete(Connection connection, Iterable<? extends E> entities)`

##### 追加された例外クラス
- `MissingPropertyException`

### ライセンス

The MIT License (MIT)

*&copy; 2016 Masato Kokubo (小久保 雅人)*

### ドキュメント

[チュートリアル](Tutorial_ja.md)

[マニュアル](Manual_ja.md)

[マニュアル (v1.9.2)](Manual-v1_ja.md)

[API 仕様](http://masatokokubo.github.io/Lightsleep/javadoc_ja/index.html)

[API 仕様 (v1.9.2)](http://masatokokubo.github.io/Lightsleep/javadoc-v1_ja/index.html)

Qiita 記事
- <a href="http://qiita.com/MasatoKokubo/items/ab46696b203d7f67036c" target="_blank">Java Runtime と JDBC ドライバーだけで動作する O/R マッピング・ライブラリ Lightsleep の紹介</a>
- <a href="http://qiita.com/MasatoKokubo/items/1080d1277e2b51d88f89" target="_blank">Java 8 用 O/R マッピング・ライブラリ Lightsleep の柔軟なデータ型変換の仕組み</a>
