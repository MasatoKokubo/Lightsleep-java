Lightsleep / マニュアル
===========

### 1. エンティティ・クラス
エンティティ・クラスは、SELECT SQL で取得したデータを格納するためのクラスで、各データベース・テーブル毎に作成します。

#### 1-1. エンティティ・クラスで使用するアノテーション
##### 1-1-1. Table アノテーション
クラスに関連するデータベース・テーブル名を指定します。
テーブル名がクラス名と同じであれば、このアノテーションを指定する必要はありません。

```java:Java
import org.lightsleep.entity.*;

@Table("Contact")
public class Contact1 extends Contact {
   ...
}
```

`@Table("super")` を指定した場合は、スーパークラスのクラス名がテーブル名となります。

```java:Java
import org.lightsleep.entity.*;

@Table("super")
public class Contact1 extends Contact {
   ...
}
```

##### 1-1-2. Key アノテーション
フィールドに関連するカラムがプライマリー・キーの一部である事を指定します。

```java:Java
@Key
public String id;
```

##### 1-1-3. Column アノテーション
フィールドに関連するデータベース・カラム名を指定します。
カラム名がフィールド名と同じであれば、このアノテーションを指定する必要がありません。

```java:Java
    @Column("family_name")
    public String familyName;
```

##### 1-1-4. ColumnType アノテーション
フィールドに関連するカラムの型を指定します。
フィールド型とカラム型が同種類の場合は、指定する必要がありません。
フィールド型が日付型で、カラム型が数値型のように異なる場合に指定します。

```java:Java
    @ColumnType(Long.class)
    public Date birhtday;
```

##### 1-1-5. NonColumn アノテーション
フィールドがどのカラムにも関連しない事を指定します。

```java:Java
    @NonColumn
    public List<Phone> phones = new ArrayList<>();
```

##### 1-1-6. NonSelect アノテーション
フィールドに関連するカラムが SELECT SQL で使用されない事を指定します。

```java:Java
    @NonSelect
    public String givenName;
```

##### 1-1-7. NonInsert アノテーション
フィールドに関連するカラムが INSERT SQL で使用されない事を指定します。

```java:Java
    @Select("CONCAT({givenName}, ' ', {familyName})") // MySQL, Oracle
    @NonInsert @NonUpdate
    public String fullName;
```

##### 1-1-8. NonUpdate アノテーション
フィールドに関連するカラムが UPDATE SQL で使用されない事を指定します。

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @NonUpdate
    public Timestamp created;
```

##### 1-1-9. Select アノテーション
SELECT SQL のカラム名の代わりの式を指定します。

```java:Java
    @Select("CONCAT({givenName}, ' ', {familyName})") // MySQL, Oracle
    @NonInsert @NonUpdate
    public String fullName;
```

##### 1-1-10. Insert アノテーション
INSERT SQL の挿入値の式を指定します。
このアノテーションが指定された場合、フィールドの値は使用されません。

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @NonUpdate
    public Timestamp created;
```

##### 1-1-11. Update アノテーション
UPDATE SQL の更新値の式を指定します。
このアノテーションが指定された場合、フィールドの値は使用されません。

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @Update("CURRENT_TIMESTAMP")
    public Timestamp modified;
```

##### 1-1-12. XxxxxProperty アノテーション
ColumnProperty, ColumnTypeProperty, NonColumnProperty, NonSelectProperty, NonInsertProperty, NonUpdateProperty, SelectProperty, InsertProperty, UpdateProperty は、目的は、Column, NonColumn, NonSelect, NonInsert, NonUpdate, Select, Insert, Update アノテーションと同じですが、フィールドではなく、クラスに指定して使用します。

スーパークラスで定義されているフィールドに関連させる場合に使用します。

```java:Java
import org.lightsleep.entity.*;

@Table("super")
@ColumnProperty(property="familyName", column="family_name")
public class Contact1 extends Contact {
```

### 1-2. エンティティ・クラスが実装するインターフェース
#### 1-2-1. PreInsert インターフェース
エンティティ・クラスがこのインターフェースを実装している場合、`Sql クラス`の `insert` メソッドで、INSERT SQL 実行前に `preInsert` メソッドがコールされます。
`preInsert` メソッドでは、プライマリー・キーの採番の実装等を行います。

```java:Java
import org.lightsleep.entity.*;

public class Contact implements PreInsert {
    @Key
    public String id;

   ...

    @Override
    public int preInsert(Connection connection) {
        id = NextId.getNewId(connection, Contact.class);
        return 0;
    }
}
```

#### 1-2-2. Composite インターフェース
エンティティ・クラスがこのインターフェースを実装している場合、`Sql` クラスの `select`, `insert`, `update` または `delete` メソッドで、各 SQL の実行後にエンティティ・クラスの `postSelect`, `postInsert`, `postUpdate`  または `postDelete` メソッドがコールされます。
ただし `update`, `delete` メソッドで、引数にエンティティがない場合は、コールされません。
エンティティが他のエンティティを内包する場合、このインターフェースを実装する事で、内包するエンティティへの SQL 処理を連動して行う事ができるようになります。

```java:Java
import org.lightsleep.entity.*;

@Table("super")
public class ContactComposite extends Contact implements Composite {
    @NonColumn
    public final List<Phone> phones = new ArrayList<>();

    @Override
    public void postSelect(Connection connection) {
        if (id > 0)
            new Sql<>(Phone.class)
                .where("{contactId} = {}", id)
                .orderBy("{childIndex}")
                .select(connection, phones::add);
    }

    @Override
    public int postInsert(Connection connection) {
        short[] childIndex = new short[1];
        // Inserts phones
        childIndex[0] = 1;
        phones.stream().forEach(phone -> {
            phone.contactId = id;
            phone.childIndex = childIndex[0]++;
        });
        int count = new Sql<>(Phone.class).insert(connection, phones);
        return count;
    }

    @Override
    public int postUpdate(Connection connection) {
      // Deletes and inserts phones
      int count = postDelete(connection);
      count += postInsert(connection);
      return count;
    }

    @Override
    public int postDelete(Connection connection) {
        int count += new Sql<>(Phone.class)
            .where("{contactId} = {}", id)
            .delete(connection);
        return count;
    }
}
```

#### 1-2-3. PreStore インターフェース
エンティティ・クラスがこのインターフェースを実装している場合、`Sql` クラスの `insert` または `update` メソッドで、各 SQL が実行される前にエンティティ・クラスの `preStore` メソッドがコールされます。

#### 1-2-4. PostLoad インターフェース
エンティティ・クラスがこのインターフェースを実装している場合、`Sql` クラスの `select` メソッドで SELECT SQL が実行されエンティティにデータベースから取得した値が設定された後にエンティティ・クラスの `postLoad` メソッドがコールされます。

```java:Java
import org.lightsleep.entity.*;

public class Contact implements PreStore, PostLoad {

    @Column("phone")
    public String[] phones_

    @NonColumn
    public final List<String> phones = new ArrayList<>();

    public void preStore() {
        phones_ = phones.toArray(new String[phones.size()]);
    }

    public void postLoad() {
        phones.clear();
        Arrays.stream(phones_).forEach(phones::add);
    }
```

### 2. トランザクション

`Transaction.execute` メソッドの実行が1つのトランザクションの実行に相当します。
トランザクションの内容を引数 `transaction` (ラムダ式) で定義してください。
ラムダ式は、`Transaction.executeBody` メソッドの内容に相当し、このメソッドの引数は、`Connection` です。

```java:Java
// Java
import org.lightsleep.*;
contact.id = 1;
contact.familyName = "Apple";
contact.givenName  = "Akane";

// トランザクション例
Transaction.execute(connection -> {
    // トランザクション開始
    new Sql<>(Contact.class).insert(connection, contact);
   ...
    // トランザクション終了
});
```


```groovy:Groovy
// Groovy
def contact = new Contact()
contact.id = 1
contact.familyName = 'Apple'
contact.givenName  = 'Akane'

// トランザクション例
Transaction.execute {
    // トランザクション開始
    new Sql<>(Contact.class).insert(it, contact)
    ...
    // トランザクション終了
}
```

トランザクション中に例外がスローされた場合は、`Transaction.rollback` メソッドが実行され、
そうでなければ `Transaction.commit` メソッドが実行されます。

### 3. コネクション・サプライヤー
データベース・コネクション(`java.sql.Connection`) の取得は、`Transaction.execute` メソッド内で行われます。
Lightsleep にはコネクションを供給するクラスとして以下があります。

1. org.lightsleep.connection.C3p0
1. org.lightsleep.connection.Dbcp
1. org.lightsleep.connection.HikariCP
1. org.lightsleep.connection.TomcatCP
1. org.lightsleep.connection.Jdbc
1. org.lightsleep.connection.Jndi

`C3p0`, `Dbcp 2`, `HikariCP`, `TomcatCP` クラスは、それぞれ対応するコネクション・プール・ライブラリを使用してデータベース・コネクションを取得します。  
`JdbcConnection` クラスは、`java.sql.DriverManager.getConnection` メソッドを使用してデータベース・コネクションを取得します。  
`JndiConnection` クラスは、JNDI (Java Naming and Directory Interface) を使用して取得したデータソース (`javax.sql.DataSource`) からデータベース・コネクションを取得します。  
コネクションを供給するクラスおよび接続に必要な情報 **lightsleep.properties** ファイルに定義してください。

```properties:lightsleep.properties
# lightsleep.properties / C3p0 設定例
ConnectionSupplier = C3p0
url                = jdbc:mysql://mysql57/test
user               = test
password           = _test_
```

```properties:c3p0.properties
# c3p0.properties
c3p0.initialPoolSize = 20
c3p0.minPoolSize     = 10
c3p0.maxPoolSize     = 30
```

```properties:lightsleep.properties
# lightsleep.properties / Dbcp 設定例
ConnectionSupplier = Dbcp
url                = jdbc:oracle:thin:@oracle121:1521:test
username           = test
password           = _test_
initialSize        = 20
maxTotal           = 30
```

```properties:lightsleep.properties
# lightsleep.properties / HikariCP 設定例
ConnectionSupplier = HikariCP
jdbcUrl            = jdbc:postgresql://postgres96/test
username           = test
password           = _test_
minimumIdle        = 10
maximumPoolSize    = 30
```

```properties:lightsleep.properties
# lightsleep.properties / TomcatCP 設定例
ConnectionSupplier = TomcatCP
url                = jdbc:sqlserver://sqlserver13;database=test
username           = test
password           = _test_
initialSize        = 20
maxActive          = 30
```

```properties:lightsleep.properties
# lightsleep.properties / Jdbc 設定例
ConnectionSupplier = Jdbc
url                = jdbc:sqlite:C:/sqlite/test
user               = test
password           = _test_
```

```properties:lightsleep.properties
# lightsleep.properties / Jndi 設定例
ConnectionSupplier = Jndi
dataSource         = jdbc/Sample
```


### 4. SQLの実行
SQLの実行は、`Sql` クラスの各種メソッドを使用し、`Transaction.execute` メソッドの引数のラムダ式内に定義します。

#### 4-1. SELECT
#### 4-1-1. SELECT 1行 / 式条件

```java:Java
// Java
Transaction.execute(connection -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class)
        .where("{id}={}", 1)
        .select(connection);
});
```

```groovy:Groovy
// Groovy
Transaction.execute {
    def contactOpt = new Sql<>(Contact.class)
        .where('{id}={}', 1)
        .select(it)
}
```

```sql:SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
```

#### 4-1-2. SELECT 1行 / エンティティ条件

```java:Java
// Java
Contact contact = new Contact();
contact.id = 1;
Transaction.execute(connection -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class)
        .where(contact)
        .select(connection);
});
```

```groovy:Groovy
// Groovy
def contact = new Contact()
contact.id = 1
Transaction.execute {
    def contactOpt = new Sql<>(Contact.class)
        .where(contact)
        .select(it)
}
```

```sql:SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
```

#### 4-1-3. SELECT 複数行 / 式条件

```java:Java
// Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .select(connection, contacts::add)
);
```

```groovy:Groovy
// Groovy
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Apple')
        .select(it, {contacts << it})
}
```

#### 4-1-4. SELECT サブクエリ条件

```java:Java
// Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class, "C")
        .where("EXISTS",
            new Sql<>(Phone.class, "P")
                .where("{P.contactId}={C.id}")
        )
        .select(connection, contacts::add)
);
```

```groovy:Groovy
// Groovy
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class, 'C')
        .where('EXISTS',
            new Sql<>(Phone.class, 'P')
                .where('{P.contactId}={C.id}')
        )
        .select(it, {contacts << it})
}
```

```sql:SQL
SELECT C.id AS C_id, C.familyName AS C_familyName, C.givenName AS C_givenName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime FROM Contact C WHERE EXISTS (SELECT * FROM Phone P WHERE P.contactId=C.id)
```

#### 4-1-5. SELECT 式条件 / AND

```java:Java
// Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .and  ("{givenName}={}", "Akane")
        .select(connection, contacts::add)
);
```

```groovy:Groovy
// Groovy
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Apple')
        .and  ('{givenName}={}', 'Akane')
        .select(it, {contacts << it})
}
```

#### 4-1-6. SELECT 式条件 / OR

```java:Java
// Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .or   ("{familyName}={}", "Orange")
        .select(connection, contacts::add)
);
```

```groovy:Groovy
// Groovy
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Apple')
        .or   ('{familyName}={}', 'Orange')
        .select(it, {contacts << it})
}
```

```sql:SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE (familyName='Apple' OR familyName='Orange')
```

#### 4-1-7. SELECT 式条件 / (A AND B) OR (C AND D)

```java:Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where(Condition
            .of ("{familyName}={}", "Apple")
            .and("{givenName}={}", "Akane")
        )
        .or(Condition
            .of ("{familyName}={}", "Orange")
            .and("{givenName}={}", "Setoka")
        )
        .select(connection, contacts::add)
);
```
```java:Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where(Condition
            .of ("{familyName}={}", "Apple")
            .and("{givenName}={}", "Akane")
        )
        .or(Condition
            .of ("{familyName}={}", "Orange")
            .and("{givenName}={}", "Setoka")
        )
        .select(connection, contacts::add)
);
```

```sql:SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE ((familyName='Apple' AND givenName='Akane') OR (familyName='Orange' AND givenName='Setoka'))
```

#### 4-1-8. SELECT カラムの選択

```java:Java
// Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .columns("familyName", "givenName")
        .select(connection, contacts::add)
);
```

```groovy:Groovy
// Groovy
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Apple')
        .columns('familyName', 'givenName')
        .select(it, {contacts << it})
}
```

```sql:SQL
SELECT familyName, givenName FROM Contact WHERE familyName='Apple'
```

#### 4-1-9. SELECT GROUP BY, HAVING

```java:Java
// Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class, "C")
        .columns("familyName")
        .groupBy("{familyName}")
        .having("COUNT({familyName})>=2")
        .select(connection, contacts::add)
);
```

```groovy:Groovy
// Groovy
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class, 'C')
        .columns('familyName')
        .groupBy('{familyName}')
        .having('COUNT({familyName})>=2')
        .select(it, {contacts << it})
}
```

```sql:SQL
SELECT MIN(C.familyName) AS C_familyName FROM Contact C GROUP BY C.familyName HAVING COUNT(C.familyName)>=2
```

#### 4-1-10. SELECT ORDER BY, OFFSET, LIMIT

```java:Java
// Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .orderBy("{familyName}")
        .orderBy("{givenName}")
        .orderBy("{id}")
        .offset(10).limit(5)
        .select(connection, contacts::add)
);
```

```groovy:Groovy
// Groovy
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class)
        .orderBy('{familyName}')
        .orderBy('{givenName}')
        .orderBy('{id}')
        .offset(10).limit(5)
        .select(it, {contacts << it})
}
```

```sql:SQL
-- MySQL, PostgreSQL, SQLite
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact ORDER BY familyName ASC, givenName ASC, id ASC LIMIT 5 OFFSET 10
```

```sql:SQL
-- Oracle, SQLServer (取得時に行をスキップする)
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact ORDER BY familyName ASC, givenName ASC, id ASC
```

#### 4-1-11. SELECT FOR UPDATE

```java:Java
// Java
Transaction.execute(connection -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class)
        .where("{id}={}", 1)
        .forUpdate()
        .select(connection);
});
```

```groovy:Groovy
// Groovy
Transaction.execute {
    def contactOpt = new Sql<>(Contact.class)
        .where('{id}={}', 1)
        .forUpdate()
        .select(it)
}
```

```sql:SQL
-- MySQL, Oracle, PostgreSQL, SQLite
-- SQLite では、FOR UPDATE が未サポートのため、例外がスローされます。
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1 FOR UPDATE
```

```sql:SQL
-- SQLServer
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WITH (ROWLOCK,UPDLOCK) WHERE id=1
```

#### 4-1-12. SELECT 内部結合

```java:Java
// Java
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
Transaction.execute(connection ->
    new Sql<>(Contact.class, "C")
        .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
        .where("{C.id}={}", 1)
        .<Phone>select(connection, contacts::add, phones::add)
);
```

```groovy:Groovy
// Groovy
def contacts = []
def phones = []
Transaction.execute {
    new Sql<>(Contact.class, 'C')
        .innerJoin(Phone.class, 'P', '{P.contactId}={C.id}')
        .where('{C.id}={}', 1)
        .select(it, {contacts << it}, {phones << it})
}
```

```sql:SQL
SELECT C.id AS C_id, C.familyName AS C_familyName, C.givenName AS C_givenName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C INNER JOIN Phone P ON P.contactId=C.id WHERE C.id=1
```

#### 4-1-13. SELECT 左外部結合

```java:Java
// Java
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
Transaction.execute(connection ->
	new Sql<>(Contact.class, "C")
	    .leftJoin(Phone.class, "P", "{P.contactId}={C.id}")
	    .where("{C.familyName}={}", "Apple")
	    .<Phone>select(connection, contacts::add, phones::add)
);
```

```groovy:Groovy
// Groovy
def contacts = []
def phones = []
Transaction.execute {
    new Sql<>(Contact.class, 'C')
        .leftJoin(Phone.class, 'P', '{P.contactId}={C.id}')
        .where('{C.familyName}={}', 'Apple')
        .select(it, {contacts << it}, {phones << it})
}
```

```sql:SQL
SELECT C.id AS C_id, C.familyName AS C_familyName, C.givenName AS C_givenName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C LEFT OUTER JOIN Phone P ON P.contactId=C.id WHERE C.familyName='Apple'
```

#### 4-1-14. SELECT 右外部結合

```java:Java
// Java
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
Transaction.execute(connection ->
    new Sql<>(Contact.class, "C")
        .rightJoin(Phone.class, "P", "{P.contactId}={C.id}")
        .where("{P.label}={}", "Main")
        .<Phone>select(connection, contacts::add, phones::add)
);
```

```groovy:Groovy
// Groovy
def contacts = []
def phones = []
Transaction.execute {
    new Sql<>(Contact.class, 'C')
        .rightJoin(Phone.class, 'P', '{P.contactId}={C.id}')
        .where('{P.label}={}', 'Main')
        .select(it, {contacts << it}, {phones << it})
}
```

```sql:SQL
-- SQLite では、RIGHT OUTER JOIN が未サポートのため、例外がスローされます。
SELECT C.id AS C_id, C.familyName AS C_familyName, C.givenName AS C_givenName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C RIGHT OUTER JOIN Phone P ON P.contactId=C.id WHERE P.label='Main'
```

#### 4-2. INSERT
#### 4-2-1. INSERT 1行

```java:Java
// Java
Contact contact = new Contact();
contact.id = 1;
contact.familyName = "Apple";
contact.givenName = "Akane";
Calendar calendar = Calendar.getInstance();
calendar.set(2001, 1-1, 1, 0, 0, 0);
contact.birthday = new Date(calendar.getTimeInMillis())

Transaction.execute(connection ->
    new Sql<>(Contact.class).insert(connection, contact));
```

```groovy:Groovy
// Groovy
def contact = new Contact()
contact.id = 1
contact.familyName = 'Apple'
contact.givenName = 'Akane'
Calendar calendar = Calendar.instance
calendar.set(2001, 1-1, 1, 0, 0, 0)
contact.birthday = new Date(calendar.timeInMillis)

Transaction.execute {
    new Sql<>(Contact.class).insert(it, contact)
}
```

```sql:SQL
-- MySQL, Oracle, PostgreSQL
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (1, 'Apple', 'Akane', DATE'2001-01-01', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- SQLite
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (1, 'Apple', 'Akane', '2001-01-01', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- SQLServer
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (1, 'Apple', 'Akane', CAST('2001-01-01' AS DATE), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

#### 4-2-2. INSERT 複数行

```java:Java
// Java
List<Contact> contacts = new ArrayList<>();

Contact contact = new Contact();
contact.id = 2; contact.familyName = "Apple"; contact.givenName = "Yukari";
Calendar calendar = Calendar.getInstance();
calendar.set(2001, 1-1, 2, 0, 0, 0);
contact.birthday = new Date(calendar.getTimeInMillis());
contacts.add(contact);

contact = new Contact();
contact.id = 3; contact.familyName = "Apple"; contact.givenName = "Azusa";
calendar = Calendar.getInstance();
calendar.set(2001, 1-1, 3, 0, 0, 0);
contact.birthday = new Date(calendar.getTimeInMillis());
contacts.add(contact);

Transaction.execute(connection ->
    new Sql<>(Contact.class).insert(connection, contacts));
```

```groovy:Groovy
// Groovy
def contacts = []

def contact = new Contact()
contact.id = 2; contact.familyName = 'Apple'; contact.givenName = 'Yukari'
def calendar = Calendar.instance
calendar.set(2001, 1-1, 2, 0, 0, 0)
contact.birthday = new Date(calendar.timeInMillis)
contacts << contact

contact = new Contact()
contact.id = 3; contact.familyName = 'Apple'; contact.givenName = 'Azusa'
calendar = Calendar.instance
calendar.set(2001, 1-1, 3, 0, 0, 0)
contact.birthday = new Date(calendar.timeInMillis)
contacts << contact

Transaction.execute {
    new Sql<>(Contact.class).insert(it, contacts)
}
```

```sql:SQL
-- MySQL, Oracle, PostgreSQL
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (2, 'Apple', 'Yukari', DATE'2001-01-02', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (3, 'Apple', 'Azusa', DATE'2001-01-03', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- SQLite
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (2, 'Apple', 'Yukari', '2001-01-02', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (3, 'Apple', 'Azusa', '2001-01-03', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- SQLServer
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (2, 'Apple', 'Yukari', CAST('2001-01-02' AS DATE), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (3, 'Apple', 'Azusa', CAST('2001-01-03' AS DATE), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

#### 4-3. UPDATE
#### 4-3-1. UPDATE 1行

```java:Java
// Java
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{id}={}", 1)
        .select(connection)
        .ifPresent(contact -> {
            contact.givenName = "Akiyo";
            new Sql<>(Contact.class).update(connection, contact);
        })
);
```

```groovy:Groovy
// Groovy
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{id}={}', 1)
        .select(it)
        .ifPresent {Contact contact ->
            contact.givenName = 'Akiyo'
            new Sql<>(Contact.class).update(it, contact)
        }
}
```

```sql:SQL
-- MySQL, Oracle, PostgreSQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
UPDATE Contact SET familyName='Apple', givenName='Akiyo', birthday=DATE'2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
```

```sql:SQL
-- SQLite
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
UPDATE Contact SET familyName='Apple', givenName='Akiyo', birthday='2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
```

```sql:SQL
-- SQLServer
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
UPDATE Contact SET familyName='Apple', givenName='Akiyo', birthday=CAST('2001-01-01' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
```

#### 4-3-2. UPDATE 複数行

```java:Java
// Java
Transaction.execute(connection -> {
    List<Contact> contacts = new ArrayList<>();
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .select(connection, contact -> {
            contact.familyName = "Apfel";
            contacts.add(contact);
        });
    new Sql<>(Contact.class).update(connection, contacts);
});
```

```groovy:Groovy
// Groovy
Transaction.execute {
    def contacts = []
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Apple')
        .select(it, {Contact contact ->
            contact.familyName = 'Apfel'
            contacts << contact
        })
    new Sql<>(Contact.class).update(it, contacts)
}
```

```sql:SQL
-- MySQL, Oracle, PostgreSQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple'
UPDATE Contact SET familyName='Apfel', givenName='Akiyo', birthday=DATE'2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
UPDATE Contact SET familyName='Apfel', givenName='Yukari', birthday=DATE'2001-01-02', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=2
UPDATE Contact SET familyName='Apfel', givenName='Azusa', birthday=DATE'2001-01-03', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=3
```

```sql:SQL
-- SQLite
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple'
UPDATE Contact SET familyName='Apfel', givenName='Akiyo', birthday='2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
UPDATE Contact SET familyName='Apfel', givenName='Yukari', birthday='2001-01-02', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=2
UPDATE Contact SET familyName='Apfel', givenName='Azusa', birthday='2001-01-03', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=3
```

```sql:SQL
-- SQLServer
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple'
UPDATE Contact SET familyName='Apfel', givenName='Akiyo', birthday=CAST('2001-01-01' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
UPDATE Contact SET familyName='Apfel', givenName='Yukari', birthday=CAST('2001-01-02' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=2
UPDATE Contact SET familyName='Apfel', givenName='Azusa', birthday=CAST('2001-01-03' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=3
```

#### 4-3-3. UPDATE 指定条件, カラム選択

```java:Java
// Java
Contact contact = new Contact();
contact.familyName = "Pomme";
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apfel")
        .columns("familyName")
        .update(connection, contact)
);
```

```groovy:Groovy
// Groovy
def contact = new Contact()
contact.familyName = 'Pomme'
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Apfel')
        .columns('familyName')
        .update(it, contact)
}
```

```sql:SQL
UPDATE Contact SET familyName='Pomme' WHERE familyName='Apfel'
```

#### 4-3-4. UPDATE 全行

```java:Java
// Java
Contact contact = new Contact();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where(Condition.ALL)
        .columns("birthday")
        .update(connection, contact)
);
```

```groovy:Groovy
// Groovy
def contact = new Contact()
Transaction.execute {
    new Sql<>(Contact.class)
        .where(Condition.ALL)
        .columns('birthday')
        .update(it, contact)
}
```

```sql:SQL
UPDATE Contact SET birthday=NULL
```

#### 4-4. DELETE
#### 4-4-1. DELETE  1行

```java:Java
// Java
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{id}={}", 1)
        .select(connection)
        .ifPresent(contact ->
            new Sql<>(Contact.class).delete(connection, contact))
);
```

```groovy:Groovy
// Groovy
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{id}={}', 1)
        .select(it)
        .ifPresent {contact ->
            new Sql<>(Contact.class).delete(it, contact)
        }
}
```

```sql:SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
DELETE FROM Contact WHERE id=1
```

#### 4-4-2. DELETE  複数行

```java:Java
// Java
Transaction.execute(connection -> {
    List<Contact> contacts = new ArrayList<>();
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Pomme")
        .select(connection, contacts::add);
    new Sql<>(Contact.class).delete(connection, contacts);
});
```

```groovy:Groovy
// Groovy
Transaction.execute {
    def contacts = []
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Pomme')
        .select(it, {contacts << it})
    new Sql<>(Contact.class).delete(it, contacts)
}
```

```sql:SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Pomme'
DELETE FROM Contact WHERE id=2
DELETE FROM Contact WHERE id=3
```

#### 4-4-3. DELETE  指定条件

```java:Java
// Java
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Orange")
        .delete(connection)
);
```

```groovy:Groovy
// Groovy
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Orange')
        .delete(it)
}
```

```sql:SQL
DELETE FROM Contact WHERE familyName='Orange'
```

#### 4-4-4. DELETE  全行

```java:Java
// Java
Transaction.execute(connection ->
    new Sql<>(Phone.class)
        .where(Condition.ALL)
        .delete(connection)
);
```

```groovy:Groovy
// Groovy
Transaction.execute {
    new Sql<>(Phone.class)
        .where(Condition.ALL)
        .delete(it)
}
```

```sql:SQL
DELETE FROM Phone
```

### 5. 式の変換処理

SQL を生成する時に、以下の文字列を式として評価し、変換処理を行います。

- `@Select`, `@Insert`, `@Update` アノテーションの値

- `@SelectProperty`, `@InsertProperty`, `@UpdateProperty` アノテーションの `expression` の値

- `Sql` クラスの以下のメソッドの引数
    - `where(String content, Object... arguments)`
    - `where(String content, Sql<SE> subSql)`
    - `and(String content, Object... arguments)`
    - `and(String content, Sql<SE> subSql)`
    - `or(String content, Object... arguments)`
    - `or(String content, Sql<SE> subSql)`
    - `groupBy(String content, Object... arguments)`
    - `having(String content, Object... arguments)`
    - `having(String content, Sql<SE> subSql)`
    - `orderBy(String content, Object... arguments)`

- `Condition` インターフェースの以下のメソッドの引数
    - `of(String content, Object... arguments)`
    - `of(String content, Sql<E> outerSql, Sql<SE> subSql)`
    - `and(String content, Object... arguments)`
    - `and(String content, Sql<E> outerSql, Sql<SE> subSql)`
    - `or(String content, Object... arguments)`
    - `or(String content, Sql<E> outerSql, Sql<SE> subSql)`

- `Expression` クラスの以下のコンストラクタの引数
    - `Expression(String content, Object... arguments)`

式の変換には以下があります。

|書式|変換内容|
|:--|:--|
|`{}`|出現順に `arguments` の要素|
|`{xxx}`|`xxx` プロパティに関連するカラム名|
|`{A.xxx}`|`"A."` + `xxx` プロパティに関連するカラム名 (`A` はテーブル別名)|
|`{A_xxx}`|テーブル別名 `A` と `xxx` プロパティに関連するカラム別名|
|`{#xxx}`|`Sql` オブジェクトに設定されたエンティティ(または `Sql#insert`, `Sql#update` メソッドのエンティティ引数) の `xxx` プロパティの値|

### 6. ログ出力
Lightsleep は以下のログ出力ライブラリに対応しています。

- java.util.logging.Logger
- Apache Log4j
- Apache Log4j2
- SLF4J

また標準出力および標準エラー出力にログを出力する事もできます。

使用するログ出力ライブラリは、*lightsleep.properties* に指定してください。

```properties:lightsleep.properties
# java.util.logging.Logger クラスを使用
Logger = Jdk

# Apache Log4j を使用
Logger = Log4j

# Apache Log4j2 を使用
Logger = Log4j2

# SLF4J を使用
Logger = SLF4J

# 標準出力に FATAL レベルを出力
Logger = Std$Out$Fatal

# 標準出力に ERROR, FATAL レベルを出力
Logger = Std$Out$Error

# 標準出力に WARN, ERROR, FATAL レベルを出力
Logger = Std$Out$Warn

# 標準出力に INFO, WARN, ERROR, FATAL レベルを出力
Logger = Std$Out$Info

# 標準出力に DEBUG, INFO, WARN, ERROR, FATAL レベルを出力
Logger = Std$Out$Debug

# 標準出力に TRACE, DEBUG, INFO, WARN, ERROR, FATAL レベルを出力
Logger = Std$Out$Trace

# 標準エラー出力に FATAL レベルを出力
Logger = Std$Err$Fatal

# 標準エラー出力に ERROR, FATAL レベルを出力
Logger = Std$Err$Error

# 標準エラー出力に WARN, ERROR, FATAL レベルを出力
Logger = Std$Err$Warn

# 標準エラー出力に INFO, WARN, ERROR, FATAL レベルを出力
Logger = Std$Err$Info

# 標準エラー出力に DEBUG, INFO, WARN, ERROR, FATAL レベルを出力
Logger = Std$Err$Debug

# 標準エラー出力に TRACE, DEBUG, INFO, WARN, ERROR, FATAL レベルを出力
Logger = Std$Err$Trace
```
