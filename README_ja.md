Lightsleep
===========

[[English]](README.md)

Lightsleepは、軽量のO/Rマッピング･ライブラリで、Java 8以降で利用できます。Java 7以前には対応していません。
またJava Persistence API (JPA)との互換性はありません。

### 特徴

- Java 8で追加された機能(関数型インタフェース、Optionalクラス)を使用したAPI 。
- SQLを構築する際のメソッド名がSQLの予約語と同じため、直観的に理解しやすい。
- J2EEが不要(Java RuntimeとJDBCドライバー以外に依存するライブラリがない)なため、適用範囲が広い。
- XMLファイル等によるテーブルとJavaクラスとのマッピング用の定義ファイルは不要。
- ライブラリがコンパクトなため学習が容易。
- 各種のDBMSに同時に接続可能。**(バージョン2.1.0~)**
- 各種のコネクション･プール･ライブラリを同時に使用可能。**(バージョン2.1.0~)**
- 内部ログを各種のロギング･ライブラリから選択して出力可能。

### 対応DBMS

- DB2
- MySQL
- Oracle Database
- PostgreSQL
- SQLite
- Microsoft SQL Server
- 標準SQL準拠DBMS

#### build.gradleでの依存関係の記述例

```gradle:build.gradle
# build.gradle
repositories {
    jcenter()
}

dependencies {
    compile 'org.lightsleep:lightsleep:2.1.0'
}
```

### Lightsleepで使用するエンティティ･クラスの定義例

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

### Lightsleepの使用例

```java:Java
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

```sql
-- 生成されるSQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple' OR lastName='Orange' ORDER BY lastName ASC, firstName ASC
```

### ライセンス

The MIT License (MIT)

*&copy; 2016 Masato Kokubo (小久保 雅人)*

### ドキュメント

[リリース･ノート](ReleaseNotes_ja.md)

[チュートリアル](Tutorial_ja.md)

[マニュアル](Manual_ja.md)

[API仕様](http://masatokokubo.github.io/Lightsleep/javadoc_ja/index.html)

Qiita記事
- <a href="http://qiita.com/MasatoKokubo/items/ab46696b203d7f67036c" target="_blank">Java RuntimeとJDBCドライバーだけで動作するO/R マッピング･ライブラリLightsleepの紹介</a>
- <a href="http://qiita.com/MasatoKokubo/items/1080d1277e2b51d88f89" target="_blank">Java 8用O/Rマッピング･ライブラリLightsleepの柔軟なデータ型変換の仕組み</a>
