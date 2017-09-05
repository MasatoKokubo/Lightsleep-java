Lightsleep 1.9.2 / マニュアル
===========

この文書は、O/R マッピング・ライブラリ Lightsleep のマニュアルです。

<div id="TOC"></div>

### 目次

1. [パッケージ](#Package)
1. [エンティティ・クラスの作成](#EntityClass)
    1. [エンティティ・クラスで使用するアノテーション](#Entity-Annotation)
        1. [@Table](#Entity-Table)
        1. [@Key](#Entity-Key)
        1. [@Column](#Entity-Column)
        1. [@ColumnType](#Entity-ColumnType)
        1. [@NonColumn](#Entity-NonColumn)
        1. [@NonSelect](#Entity-NonSelect)
        1. [@NonInsert](#Entity-NonInsert)
        1. [@NonUpdate](#Entity-NonUpdate)
        1. [@Select](#Entity-Select)
        1. [@Insert](#Entity-Insert)
        1. [@Update](#Entity-Update)
        1. [@KeyProperty, @ColumnProperty, ... @UpdateProperty](#Entity-XxxxxProperty)
    1. [エンティティ・クラスで実装するインターフェース](#Entity-Interface)
        1. [PreInsert インターフェース](#Entity-PreInsert)
        1. [Composite インターフェース](#Entity-Composite)
        1. [PreStore インターフェース](#Entity-PreStore)
        1. [PostLoad インターフェース](#Entity-PostLoad)
1. [lightsleep.properties の定義](#lightsleep-properties)
    1. [ログ・ライブラリ・クラスの指定](#Logger)
    1. [データベース・ハンドラ・クラスの指定](#Database)
    1. [コネクションを供給するクラスの指定](#ConnectionSupplier)
1. [トランザクション](#Transaction)
1. [SQLの実行](#ExecuteSQL)
    1. [SELECT](#ExecuteSQL-select)
        1. [SELECT 1行 / 式条件](#ExecuteSQL-select-1-Expression)
        1. [SELECT 1行 / エンティティ条件](#ExecuteSQL-select-Entity)
        1. [SELECT 複数行 / 式条件](#ExecuteSQL-select-N-Expression)
        1. [SELECT サブクエリ条件](#ExecuteSQL-select-Subquery)
        1. [SELECT 式条件 / AND](#ExecuteSQL-select-Expression-and)
        1. [SELECT 式条件 / OR](#ExecuteSQL-select-Expression-or)
        1. [SELECT 式条件 / SELECT 式条件 / A AND B OR C AND D](#ExecuteSQL-select-Expression-andor)
        1. [SELECT カラムの選択](#ExecuteSQL-select-columns)
        1. [SELECT GROUP BY, HAVING](#ExecuteSQL-select-groupBy-having)
        1. [SELECT ORDER BY, OFFSET, LIMIT](#ExecuteSQL-select-orderBy-offset-limit)
        1. [SELECT FOR UPDATE](#ExecuteSQL-select-forUpdate)
        1. [SELECT 内部結合](#ExecuteSQL-select-innerJoin)
        1. [SELECT 左外部結合](#ExecuteSQL-select-leftJoin)
        1. [SELECT 右外部結合](#ExecuteSQL-select-rightJoin)
    1. [INSERT](#ExecuteSQL-insert)
        1. [INSERT 1行](#ExecuteSQL-insert-1)
        1. [INSERT 複数行](#ExecuteSQL-insert-N)
    1. [UPDATE](#ExecuteSQL-update)
        1. [UPDATE 1行](#ExecuteSQL-update-1)
        1. [UPDATE 複数行](#ExecuteSQL-update-N)
        1. [UPDATE 指定条件, カラム選択](#ExecuteSQL-update-Condition)
        1. [UPDATE 全行](#ExecuteSQL-update-all)
    1. [DELETE](#ExecuteSQL-delete)
        1. [DELETE 1行](#ExecuteSQL-delete-1)
        1. [DELETE 複数行](#ExecuteSQL-delete-N)
        1. [DELETE 指定条件](#ExecuteSQL-delete-Condition)
        1. [DELETE 全行](#ExecuteSQL-delete-all)
1. [式の変換処理](#Expression)

<div id="Package"></div>

[【目次へ】](#TOC)

### 1. パッケージ

以下のパッケージがあります。

|パッケージ|含まれるクラス|
|:--|:--|
|org.lightsleep           |主要クラス|
|org.lightsleep.component |SQL の構成要素を作成するクラス|
|org.lightsleep.connection|コネクションを供給するためのクラス|
|org.lightsleep.database  |データベース・ハンドラ・クラス|
|org.lightsleep.entity    |エンティティー・クラスを作成する際に使用するアノテーション・クラスおよびインタフェース|
|org.lightsleep.helper    |主に内部的に使用するヘルパー・クラス|
|org.lightsleep.logger    |各種ログ・ライブラリを使用するためのクラス|

<div id="EntityClass"></div>

[【目次へ】](#TOC)

### 2. エンティティ・クラスの作成
エンティティ・クラスは、SELECT SQL で取得したデータを格納するためのクラスで、各データベース・テーブル毎に作成します。

<div id="Entity-Annotation"></div>

#### 2-1. エンティティ・クラスで使用するアノテーション
Lihgtsleep は、エンティティ・クラスまたはオブジェクトを引数とするメソッドでは自動でテーブルとの関連付けを行いますが、エンティティ・クラスにアノテーションを使用する事が必要な場合もあります。
Lightsleep には、以下のアノテーションがあります。

|アノテーション・クラス|指定する内容|付与する対象|
|:--|:--|:--|
|[`@Table`             ](#Entity-Table        )|テーブル名|クラス|
|[`@Key`               ](#Entity-Key          )|プライマリ・キーに対応|フィールド|
|[`@Column`            ](#Entity-Column       )|カラム名|フィールド|
|[`@ColumnType`        ](#Entity-ColumnType   )|カラムの型|フィールド|
|[`@NonColumn`         ](#Entity-NonColumn    )|カラムに関連しない|フィールド|
|[`@NonSelect`         ](#Entity-NonSelect    )|SELECT SQL に使用しない|フィールド|
|[`@NonInsert`         ](#Entity-NonInsert    )|INSERT SQL に使用しない|フィールド|
|[`@NonUpdate`         ](#Entity-NonUpdate    )|UPDATE SQL に使用しない|フィールド|
|[`@Select`            ](#Entity-Select       )|SELECT SQL で使用する式|フィールド|
|[`@Insert`            ](#Entity-Insert       )|INSERT SQL で使用する式|フィールド|
|[`@Update`            ](#Entity-Update       )|UPDATE SQL で使用する式|フィールド|
|[`@KeyProperty`       ](#Entity-XxxxxProperty)|プライマリ・キーに対応|クラス|
|[`@ColumnProperty`    ](#Entity-XxxxxProperty)|カラム名|クラス|
|[`@ColumnTypeProperty`](#Entity-XxxxxProperty)|カラムの型|クラス|
|[`@NonColumnProperty` ](#Entity-XxxxxProperty)|カラムに関連しない|クラス|
|[`@NonSelectProperty` ](#Entity-XxxxxProperty)|SELECT SQL に使用しない|クラス|
|[`@NonInsertProperty` ](#Entity-XxxxxProperty)|INSERT SQL に使用しない|クラス|
|[`@NonUpdateProperty` ](#Entity-XxxxxProperty)|UPDATE SQL に使用しない|クラス|
|[`@SelectProperty`    ](#Entity-XxxxxProperty)|SELECT SQL で使用する式|クラス|
|[`@InsertProperty`    ](#Entity-XxxxxProperty)|INSERT SQL で使用する式|クラス|
|[`@UpdateProperty`    ](#Entity-XxxxxProperty)|UPDATE SQL で使用する式|クラス|

<div id="Entity-Table"></div>

[【目次へ】](#TOC) [【アノテーション一覧へ】](#Entity-Annotation)

##### 2-1-1. @Table
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

<div id="Entity-Key"></div>

##### 2-1-2. @Key
フィールドに関連するカラムがプライマリー・キーの一部である事を指定します。

```java:Java
@Key
public String id;
```

<div id="Entity-Column"></div>

##### 2-1-3. @Column
フィールドに関連するデータベース・カラム名を指定します。
カラム名がフィールド名と同じであれば、このアノテーションを指定する必要がありません。

```java:Java
    @Column("family_name")
    public String familyName;
```

<div id="Entity-ColumnType"></div>

##### 2-1-4. @ColumnType
フィールドに関連するカラムの型を指定します。
フィールド型とカラム型が同種類の場合は、指定する必要がありません。
フィールド型が日付型で、カラム型が数値型のように異なる場合に指定します。

```java:Java
    @ColumnType(Long.class)
    public Date birhtday;
```

<div id="Entity-NonColumn"></div>

[【目次へ】](#TOC) [【アノテーション一覧へ】](#Entity-Annotation)

##### 2-1-5. @NonColumn
フィールドがどのカラムにも関連しない事を指定します。

```java:Java
    @NonColumn
    public List<Phone> phones = new ArrayList<>();
```

<div id="Entity-NonSelect"></div>

##### 2-1-6. @NonSelect
フィールドに関連するカラムが SELECT SQL で使用されない事を指定します。

```java:Java
    @NonSelect
    public String givenName;
```

<div id="Entity-NonInsert"></div>

##### 2-1-7. @NonInsert
フィールドに関連するカラムが INSERT SQL で使用されない事を指定します。

```java:Java
    @Select("CONCAT({givenName}, ' ', {familyName})") // MySQL, Oracle
    @NonInsert @NonUpdate
    public String fullName;
```

<div id="Entity-NonUpdate"></div>

##### 2-1-8. @NonUpdate
フィールドに関連するカラムが UPDATE SQL で使用されない事を指定します。

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @NonUpdate
    public Timestamp created;
```

<div id="Entity-Select"></div>

[【目次へ】](#TOC) [【アノテーション一覧へ】](#Entity-Annotation)

##### 2-1-9. @Select
SELECT SQL のカラム名の代わりの式を指定します。

```java:Java
    @Select("CONCAT({givenName}, ' ', {familyName})") // MySQL, Oracle
    @NonInsert @NonUpdate
    public String fullName;
```

<div id="Entity-Insert"></div>

##### 2-1-10. @Insert
INSERT SQL の挿入値の式を指定します。
このアノテーションが指定された場合、フィールドの値は使用されません。

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @NonUpdate
    public Timestamp created;
```

<div id="Entity-Update"></div>

##### 2-1-11. @Update
UPDATE SQL の更新値の式を指定します。
このアノテーションが指定された場合、フィールドの値は使用されません。

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @Update("CURRENT_TIMESTAMP")
    public Timestamp modified;
```

<div id="Entity-XxxxxProperty"></div>

##### 2-1-12. @KeyProperty, @ColumnProperty, ... @UpdateProperty
これらのアノテーションは、スーパークラスで定義されているフィールドに対して指定する場合に使用します。
同一アノテーションを1つのクラスに複数付与できます。

```java:Java
import org.lightsleep.entity.*;

@Table("super")
@ColumnProperty(property="familyName", column="family_name")
public class Contact1 extends Contact {
```

<div id="Entity-Interface"></div>

[【目次へ】](#TOC) [【アノテーション一覧へ】](#Entity-Annotation)

### 2-2. エンティティ・クラスで実装するインターフェース

<div id="Entity-PreInsert"></div>

#### 2-2-1. PreInsert インターフェース
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

<div id="Entity-Composite"></div>

[【目次へ】](#TOC)

#### 2-2-2. Composite インターフェース
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


<div id="Entity-PreStore"></div>

[【目次へ】](#TOC)

#### 2-2-3. PreStore インターフェース
エンティティ・クラスがこのインターフェースを実装している場合、`Sql` クラスの `insert` および `update` メソッドで、各 SQL が実行される前にエンティティ・クラスの `preStore` メソッドがコールされます。

<div id="Entity-PostLoad"></div>

#### 2-2-4. PostLoad インターフェース
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

<div id="lightsleep-properties"></div>

[【目次へ】](#TOC)

### 3. lightsleep.properties の定義

lightsleep.properties は、Lightsleep が参照するプロパティ・ファイルで、以下の内容を定義します。

|プロパティ名|指定する内容|
|:--|:--|
|[Logger            ](#Logger            )|Lightsleep がログ出力で使用するログ・ライブラリに対応するクラス|
|[Database          ](#Database          )|使用する DBMS に対応するデータベース・ハンドラ・クラス|
|[ConnectionSupplier](#ConnectionSupplier)|コネクション・サプライヤー (コネクション・プール・ライブラリなど) に対応するクラス|

上記以外にもコネクション・プール・ライブラリが使用するプロパティを定義します。

lightsleep.properties の例: 

```properties:lightsleep.properties
Logger             = Log4j2
Database           = PostgreSQL
ConnectionSupplier = Dbcp
url                = jdbc:postgresql://postgresqlserver/example
username           = example
password           = _example_
initialSize        = 10
maxTotal           = 100
```

<div id="Logger"></div>

[【目次へ】](#TOC) [【プロパティ一覧へ】](#lightsleep-properties)

#### 3-1. ログ・ライブラリ・クラスの指定

Logger プロパティの値を以下から選択します。

|指定値|ログ・ライブラリなど|ログ・レベル|ログ・ライブラリが使用する定義ファイル|
|:--|:--|:-:|:--|
|`Jdk`    |Java Runtime|－|logging.properties|
|`Log4j`  |Log4j       |－|log4j.properties または log4j.xml|
|`Log4j2` |Log4j 2     |－|log4j2.xml|
|`SLF4J`  |SLF4J       |－|対象とするログ・ライブラリ実装に依存|
|`Std$Out$Trace`|System.out に出力|trace|－|
|`Std$Out$Debug`|同上|debug|－|
|`Std$Out$Info` |同上|info |－|
|`Std$Out$Warn` |同上|warn |－|
|`Std$Out$Error`|同上|error|－|
|`Std$Out$Fatal`|同上|fatal|－|
|`Std$Err$Trace`|System.err に出力|trace|－|
|`Std$Err$Debug`|同上|debug|－|
|`Std$Err$Info` |同上|info |－|
|`Std$Err$Warn` |同上|warn |－|
|`Std$Err$Error`|同上|error|－|
|`Std$Err$Fatal`|同上|fatal|－|

指定がない場合は、`Std$Out$Info` が選択されます。

<div id="Database"></div>

[【目次へ】](#TOC) [【プロパティ一覧へ】](#lightsleep-properties)

#### 3-2. データベース・ハンドラ・クラスの指定

Database プロパティの値を以下から選択します。

|指定値|DBMS|
|:--|:--|
|DB2 *(since 1.9.0)*|<a href="https://www.ibm.com/us-en/marketplace/db2-express-c" target="_blank">DB2</a>|
|MySQL     |<a href="https://www.mysql.com/" target="_blank">MySQL</a>|
|Oracle    |<a href="https://www.oracle.com/database/index.html" target="_blank">Oracle Database</a>|
|PostgreSQL|<a href="https://www.postgresql.org/" target="_blank">PostgreSQL</a>|
|SQLite    |<a href="https://sqlite.org/index.html" target="_blank">SQLite</a>|
|SQLServer |<a href="https://www.microsoft.com/ja-jp/sql-server/sql-server-2016" target="_blank">Microsoft SQL Server</a>|

上記以外の DBMS を使用する場合は、指定しないか `Standard` を指定します。
ただしその場合は、DBMS 固有の機能は使用できません。

<div id="ConnectionSupplier"></div>

[【目次へ】](#TOC) [【プロパティ一覧へ】](#lightsleep-properties)

#### 3-3. コネクションを供給するクラスの指定

ConnectionSupplier プロパティの値を以下から選択します。

|指定値|対応するコネクション・プール・ライブラリなど|
|:--|:--|
|C3p0    |<a href="http://www.mchange.com/projects/c3p0/" target="_blank">c3p0</a>|
|Dbcp    |<a href="https://commons.apache.org/proper/commons-dbcp/" target="_blank">Apache Commons DBCP</a>|
|HikariCP|<a href="http://brettwooldridge.github.io/HikariCP/" target="_blank">HikariCP</a>|
|TomcatCP|<a href="http://tomcat.apache.org/tomcat-8.5-doc/jdbc-pool.html" target="_blank">Tomcat JDBC Connection Pool</a>|
|Jndi    |Java Naming and Directory Interface (JNDI) (<a href="http://tomcat.apache.org/tomcat-8.5-doc/jndi-datasource-examples-howto.html" target="_blank">Tomcat の場合</a>)|
|Jdbc    |DriverManager#getConnection(String url, Properties info) メソッド|

`C3p0`, `Dbcp 2`, `HikariCP`, `TomcatCP` クラスは、それぞれ対応するコネクション・プール・ライブラリを使用してデータベース・コネクションを取得します。  
`Jndi` クラスは、JNDI (Java Naming and Directory Interface) を使用して取得したデータソース (`javax.sql.DataSource`) からデータベース・コネクションを取得します。  
`Jdbc` クラスは、`java.sql.DriverManager.getConnection` メソッドを使用してデータベース・コネクションを取得します。  
コネクション・プール・ライブラリが必要する情報も lightsleep.properties ファイルに定義してください。
以下の lightsleep.properties の定義例の ConnectionSupplier より下 (url ~) は、コネクション・プール・ライブラリに渡す内容です。

```properties:lightsleep.properties
# lightsleep.properties / Jdbc
ConnectionSupplier = Jdbc
url                = jdbc:db2://db2-11:50000/example
user               = example
password           = _example_
```

```properties:lightsleep.properties
# lightsleep.properties / C3p0
ConnectionSupplier = C3p0
url                = jdbc:mysql://mysql57/example
user               = example
password           = _example_
```

```properties:c3p0.properties
# c3p0.properties
c3p0.initialPoolSize = 20
c3p0.minPoolSize     = 10
c3p0.maxPoolSize     = 30
```

```properties:lightsleep.properties
# lightsleep.properties / Dbcp
ConnectionSupplier = Dbcp
url                = jdbc:oracle:thin:@oracle121:1521:example
username           = example
password           = _example_
initialSize        = 20
maxTotal           = 30
```

```properties:lightsleep.properties
# lightsleep.properties / HikariCP
ConnectionSupplier = HikariCP
jdbcUrl            = jdbc:postgresql://postgres96/example
username           = example
password           = _example_
minimumIdle        = 10
maximumPoolSize    = 30
```

```properties:lightsleep.properties
# lightsleep.properties / TomcatCP
ConnectionSupplier = TomcatCP
url                = jdbc:sqlserver://sqlserver13;database=example
username           = example
password           = _example_
initialSize        = 20
maxActive          = 30
```

```properties:lightsleep.properties
# lightsleep.properties / Jndi
ConnectionSupplier = Jndi
dataSource         = jdbc/example
```

[【目次へ】](#TOC)

### 4. トランザクション

`Transaction.execute` メソッドの実行が1つのトランザクションの実行に相当します。
トランザクションの内容を引数 `transaction` (ラムダ式) で定義してください。
ラムダ式は、`Transaction.executeBody` メソッドの内容に相当し、このメソッドの引数は、`Connection` です。

```java:Java
// Java での例
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
// Groovy での例
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

<div id="ExecuteSQL"></div>

[【目次へ】](#TOC)

### 5. SQLの実行
SQLの実行は、`Sql` クラスの各種メソッドを使用し、`Transaction.execute` メソッドの引数のラムダ式内に定義します。

<div id="ExecuteSQL-select"></div>

#### 5-1. SELECT

<div id="ExecuteSQL-select-1-Expression"></div>

#### 5-1-1. SELECT 1行 / 式条件

```java:Java
// Java での例
Transaction.execute(connection -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class)
        .where("{id}={}", 1)
        .select(connection);
});
```

```groovy:Groovy
// Groovy での例
Transaction.execute {
    def contactOpt = new Sql<>(Contact.class)
        .where('{id}={}', 1)
        .select(it)
}
```

```sql:SQL
-- 実行される SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
```

<div id="ExecuteSQL-select-Entity"></div>

[【目次へ】](#TOC)

#### 5-1-2. SELECT 1行 / エンティティ条件

```java:Java
// Java での例
Contact contact = new Contact();
contact.id = 1;
Transaction.execute(connection -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class)
        .where(contact)
        .select(connection);
});
```

```groovy:Groovy
// Groovy での例
def contact = new Contact()
contact.id = 1
Transaction.execute {
    def contactOpt = new Sql<>(Contact.class)
        .where(contact)
        .select(it)
}
```

```sql:SQL
-- 実行される SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
```

<div id="ExecuteSQL-select-N-Expression"></div>

[【目次へ】](#TOC)

#### 5-1-3. SELECT 複数行 / 式条件

```java:Java
// Java での例
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .select(connection, contacts::add)
);
```

```groovy:Groovy
// Groovy での例
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Apple')
        .select(it, {contacts << it})
}
```

<div id="ExecuteSQL-select-Subquery"></div>

[【目次へ】](#TOC)

#### 5-1-4. SELECT サブクエリ条件

```java:Java
// Java での例
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
// Groovy での例
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
-- 実行される SQL
SELECT C.id AS C_id, C.familyName AS C_familyName, C.givenName AS C_givenName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime FROM Contact C WHERE EXISTS (SELECT * FROM Phone P WHERE P.contactId=C.id)
```

<div id="ExecuteSQL-select-Expression-and"></div>

[【目次へ】](#TOC)

#### 5-1-5. SELECT 式条件 / AND

```java:Java
// Java での例
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .and  ("{givenName}={}", "Akane")
        .select(connection, contacts::add)
);
```

```groovy:Groovy
// Groovy での例
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Apple')
        .and  ('{givenName}={}', 'Akane')
        .select(it, {contacts << it})
}
```

```sql:SQL
-- 実行される SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple' AND givenName='Akane'
```

<div id="ExecuteSQL-select-Expression-or"></div>

[【目次へ】](#TOC)

#### 5-1-6. SELECT 式条件 / OR

```java:Java
// Java での例
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .or   ("{familyName}={}", "Orange")
        .select(connection, contacts::add)
);
```

```groovy:Groovy
// Groovy での例
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Apple')
        .or   ('{familyName}={}', 'Orange')
        .select(it, {contacts << it})
}
```

```sql:SQL
-- 実行される SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple' OR familyName='Orange'
```

<div id="ExecuteSQL-select-Expression-andor"></div>

[【目次へ】](#TOC)

#### 5-1-7. SELECT 式条件 / A AND B OR C AND D

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
-- 実行される SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple' AND givenName='Akane' OR familyName='Orange' AND givenName='Setoka'
```

<div id="ExecuteSQL-select-columns"></div>

[【目次へ】](#TOC)

#### 5-1-8. SELECT カラムの選択

```java:Java
// Java での例
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .columns("familyName", "givenName")
        .select(connection, contacts::add)
);
```

```groovy:Groovy
// Groovy での例
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Apple')
        .columns('familyName', 'givenName')
        .select(it, {contacts << it})
}
```

```sql:SQL
-- 実行される SQL
SELECT familyName, givenName FROM Contact WHERE familyName='Apple'
```

<div id="ExecuteSQL-select-groupBy-having"></div>

[【目次へ】](#TOC)

#### 5-1-9. SELECT GROUP BY, HAVING

```java:Java
// Java での例
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
// Groovy での例
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
-- 実行される SQL
SELECT MIN(C.familyName) AS C_familyName FROM Contact C GROUP BY C.familyName HAVING COUNT(C.familyName)>=2
```

<div id="ExecuteSQL-select-orderBy-offset-limit"></div>

[【目次へ】](#TOC)

#### 5-1-10. SELECT ORDER BY, OFFSET, LIMIT

```java:Java
// Java での例
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
// Groovy での例
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
-- 実行される SQL / DB2, MySQL, PostgreSQL, SQLite
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact ORDER BY familyName ASC, givenName ASC, id ASC LIMIT 5 OFFSET 10
```

```sql:SQL
-- 実行される SQL / Oracle, SQLServer (取得時に行をスキップする)
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact ORDER BY familyName ASC, givenName ASC, id ASC
```

<div id="ExecuteSQL-select-forUpdate"></div>

[【目次へ】](#TOC)

#### 5-1-11. SELECT FOR UPDATE

```java:Java
// Java での例
Transaction.execute(connection -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class)
        .where("{id}={}", 1)
        .forUpdate()
        .select(connection);
});
```

```groovy:Groovy
// Groovy での例
Transaction.execute {
    def contactOpt = new Sql<>(Contact.class)
        .where('{id}={}', 1)
        .forUpdate()
        .select(it)
}
```

```sql:SQL
-- 実行される SQL / DB2
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1 FOR UPDATE WITH RS
```

```sql:SQL
-- 実行される SQL / MySQL, Oracle, PostgreSQL, SQLite
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1 FOR UPDATE
```

```sql:SQL
-- 実行される SQL / SQLite
-- SQLite では FOR UPDATE をサポートしていないので UnsupportedOperationException がスローされます。
```

```sql:SQL
-- 実行される SQL / SQLServer
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WITH (ROWLOCK,UPDLOCK) WHERE id=1
```

-- SQLite では、FOR UPDATE が未サポートのため、例外がスローされます。

<div id="ExecuteSQL-select-innerJoin"></div>

[【目次へ】](#TOC)

#### 5-1-12. SELECT 内部結合

```java:Java
// Java での例
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
// Groovy での例
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
-- 実行される SQL
SELECT C.id AS C_id, C.familyName AS C_familyName, C.givenName AS C_givenName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C INNER JOIN Phone P ON P.contactId=C.id WHERE C.id=1
```

<div id="ExecuteSQL-select-leftJoin"></div>

[【目次へ】](#TOC)

#### 5-1-13. SELECT 左外部結合

```java:Java
// Java での例
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
// Groovy での例
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
-- 実行される SQL
SELECT C.id AS C_id, C.familyName AS C_familyName, C.givenName AS C_givenName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C LEFT OUTER JOIN Phone P ON P.contactId=C.id WHERE C.familyName='Apple'
```

<div id="ExecuteSQL-select-rightJoin"></div>

[【目次へ】](#TOC)

#### 5-1-14. SELECT 右外部結合

```java:Java
// Java での例
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
// Groovy での例
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
-- 実行される SQL
-- SQLite では、RIGHT OUTER JOIN が未サポートのため、例外がスローされます。
SELECT C.id AS C_id, C.familyName AS C_familyName, C.givenName AS C_givenName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C RIGHT OUTER JOIN Phone P ON P.contactId=C.id WHERE P.label='Main'
```

#### 5-1-15. SELECT COUNT(*)
```java:Java
// Java での例
int[] rowCount = new int[1];
Transaction.execute(connection ->
    rowCount[0] = new Sql<>(Contact.class)
        .where("familyName={}", "Apple")
        .selectCount(connection)
);
```

```groovy:Groovy
// Groovy での例
def rowCount = 0
Transaction.execute {
    rowCount = new Sql<>(Contact.class)
        .where('familyName={}', 'Apple')
        .selectCount(it)
}
```

```sql:SQL
-- 実行される SQL
SELECT COUNT(*) FROM Contact WHERE familyName='Apple'
```

<div id="ExecuteSQL-insert"></div>

[【目次へ】](#TOC)

#### 5-2. INSERT

<div id="ExecuteSQL-insert-1"></div>

#### 5-2-1. INSERT 1行

```java:Java
// Java での例
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
// Groovy での例
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
-- 実行される SQL / DB2, MySQL, Oracle, PostgreSQL
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (1, 'Apple', 'Akane', DATE'2001-01-01', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- 実行される SQL / SQLite
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (1, 'Apple', 'Akane', '2001-01-01', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- 実行される SQL / SQLServer
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (1, 'Apple', 'Akane', CAST('2001-01-01' AS DATE), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

<div id="ExecuteSQL-insert-N"></div>

[【目次へ】](#TOC)

#### 5-2-2. INSERT 複数行

```java:Java
// Java での例
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
// Groovy での例
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
-- 実行される SQL / DB2, MySQL, Oracle, PostgreSQL
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (2, 'Apple', 'Yukari', DATE'2001-01-02', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (3, 'Apple', 'Azusa', DATE'2001-01-03', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- 実行される SQL / SQLite
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (2, 'Apple', 'Yukari', '2001-01-02', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (3, 'Apple', 'Azusa', '2001-01-03', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- 実行される SQL / SQLServer
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (2, 'Apple', 'Yukari', CAST('2001-01-02' AS DATE), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (3, 'Apple', 'Azusa', CAST('2001-01-03' AS DATE), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```


<div id="ExecuteSQL-update"></div>

[【目次へ】](#TOC)

#### 5-3. UPDATE

<div id="ExecuteSQL-update-1"></div>

#### 5-3-1. UPDATE 1行

```java:Java
// Java での例
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
// Groovy での例
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
-- 実行される SQL / DB2, MySQL, Oracle, PostgreSQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
UPDATE Contact SET familyName='Apple', givenName='Akiyo', birthday=DATE'2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
```

```sql:SQL
-- 実行される SQL / SQLite
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
UPDATE Contact SET familyName='Apple', givenName='Akiyo', birthday='2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
```

```sql:SQL
-- 実行される SQL / SQLServer
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
UPDATE Contact SET familyName='Apple', givenName='Akiyo', birthday=CAST('2001-01-01' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
```

<div id="ExecuteSQL-update-N"></div>

[【目次へ】](#TOC)

#### 5-3-2. UPDATE 複数行

```java:Java
// Java での例
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
// Groovy での例
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
-- 実行される SQL / DB2, MySQL, Oracle, PostgreSQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple'
UPDATE Contact SET familyName='Apfel', givenName='Akiyo', birthday=DATE'2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
UPDATE Contact SET familyName='Apfel', givenName='Yukari', birthday=DATE'2001-01-02', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=2
UPDATE Contact SET familyName='Apfel', givenName='Azusa', birthday=DATE'2001-01-03', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=3
```

```sql:SQL
-- 実行される SQL / SQLite
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple'
UPDATE Contact SET familyName='Apfel', givenName='Akiyo', birthday='2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
UPDATE Contact SET familyName='Apfel', givenName='Yukari', birthday='2001-01-02', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=2
UPDATE Contact SET familyName='Apfel', givenName='Azusa', birthday='2001-01-03', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=3
```

```sql:SQL
-- 実行される SQL / SQLServer
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple'
UPDATE Contact SET familyName='Apfel', givenName='Akiyo', birthday=CAST('2001-01-01' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
UPDATE Contact SET familyName='Apfel', givenName='Yukari', birthday=CAST('2001-01-02' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=2
UPDATE Contact SET familyName='Apfel', givenName='Azusa', birthday=CAST('2001-01-03' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=3
```

<div id="ExecuteSQL-update-Condition"></div>

[【目次へ】](#TOC)

#### 5-3-3. UPDATE 指定条件, カラム選択

```java:Java
// Java での例
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
// Groovy での例
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
-- 実行される SQL
UPDATE Contact SET familyName='Pomme' WHERE familyName='Apfel'
```

<div id="ExecuteSQL-update-all"></div>

[【目次へ】](#TOC)

#### 5-3-4. UPDATE 全行

```java:Java
// Java での例
Contact contact = new Contact();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where(Condition.ALL)
        .columns("birthday")
        .update(connection, contact)
);
```

```groovy:Groovy
// Groovy での例
def contact = new Contact()
Transaction.execute {
    new Sql<>(Contact.class)
        .where(Condition.ALL)
        .columns('birthday')
        .update(it, contact)
}
```

```sql:SQL
-- 実行される SQL
UPDATE Contact SET birthday=NULL
```

<div id="ExecuteSQL-delete"></div>

[【目次へ】](#TOC)

#### 5-4. DELETE

<div id="ExecuteSQL-delete-1"></div>

#### 5-4-1. DELETE  1行

```java:Java
// Java での例
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{id}={}", 1)
        .select(connection)
        .ifPresent(contact ->
            new Sql<>(Contact.class).delete(connection, contact))
);
```

```groovy:Groovy
// Groovy での例
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
-- 実行される SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
DELETE FROM Contact WHERE id=1
```

<div id="ExecuteSQL-delete-N"></div>

[【目次へ】](#TOC)

#### 5-4-2. DELETE  複数行

```java:Java
// Java での例
Transaction.execute(connection -> {
    List<Contact> contacts = new ArrayList<>();
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Pomme")
        .select(connection, contacts::add);
    new Sql<>(Contact.class).delete(connection, contacts);
});
```

```groovy:Groovy
// Groovy での例
Transaction.execute {
    def contacts = []
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Pomme')
        .select(it, {contacts << it})
    new Sql<>(Contact.class).delete(it, contacts)
}
```

```sql:SQL
-- 実行される SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Pomme'
DELETE FROM Contact WHERE id=2
DELETE FROM Contact WHERE id=3
```

<div id="ExecuteSQL-delete-Condition"></div>

[【目次へ】](#TOC)

#### 5-4-3. DELETE  指定条件

```java:Java
// Java での例
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Orange")
        .delete(connection)
);
```

```groovy:Groovy
// Groovy での例
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Orange')
        .delete(it)
}
```

```sql:SQL
-- 実行される SQL
DELETE FROM Contact WHERE familyName='Orange'
```

<div id="ExecuteSQL-delete-all"></div>

[【目次へ】](#TOC)

#### 5-4-4. DELETE 全行

```java:Java
// Java での例
Transaction.execute(connection ->
    new Sql<>(Phone.class)
        .where(Condition.ALL)
        .delete(connection)
);
```

```groovy:Groovy
// Groovy での例
Transaction.execute {
    new Sql<>(Phone.class)
        .where(Condition.ALL)
        .delete(it)
}
```

```sql:SQL
-- 実行される SQL
DELETE FROM Phone
```

<div id="Expression"></div>

[【目次へ】](#TOC)

### 6. 式の変換処理

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

<div style="text-align:center; margin-top:20px"><i>&copy; 2016 Masato Kokubo</i></div>
