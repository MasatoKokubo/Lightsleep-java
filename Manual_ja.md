Lightsleep  2.x.x / マニュアル
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

|アノテーション型|要素|示す内容|付与する対象|
|:--|:--|:--|:--|
|[`@Table`             ](#Entity-Table        )|String value                                   |関連するテーブル名     |クラス|
|[`@Key`               ](#Entity-Key          )|boolean value (省略値: true)                   |プライマリ・キーに対応 |フィールド|
|[`@Column`            ](#Entity-Column       )|String value                                   |関連するカラムの名前   |フィールド|
|[`@ColumnType`        ](#Entity-ColumnType   )|Class<?> value                                 |関連するカラムの型     |フィールド|
|[`@NonColumn`         ](#Entity-NonColumn    )|boolean value (省略値: true)                   |カラムに関連しない     |フィールド|
|[`@NonSelect`         ](#Entity-NonSelect    )|boolean value (省略値: true)                   |SELECT SQL に使用しない|フィールド|
|[`@NonInsert`         ](#Entity-NonInsert    )|boolean value (省略値: true)                   |INSERT SQL に使用しない|フィールド|
|[`@NonUpdate`         ](#Entity-NonUpdate    )|boolean value (省略値: true)                   |UPDATE SQL に使用しない|フィールド|
|[`@Select`            ](#Entity-Select       )|String value                                   |SELECT SQL で使用する式|フィールド|
|[`@Insert`            ](#Entity-Insert       )|String value                                   |INSERT SQL で使用する式|フィールド|
|[`@Update`            ](#Entity-Update       )|String value                                   |UPDATE SQL で使用する式|フィールド|
|[`@KeyProperty`       ](#Entity-XxxxxProperty)|String property<br>boolean value (省略値: true)|プライマリ・キーに対応 |クラス|
|[`@ColumnProperty`    ](#Entity-XxxxxProperty)|String property<br>String column               |関連するカラムの名前   |クラス|
|[`@ColumnTypeProperty`](#Entity-XxxxxProperty)|String property<br>Class<?> type               |関連するカラムの型     |クラス|
|[`@NonColumnProperty` ](#Entity-XxxxxProperty)|String property<br>boolean value (省略値: true)|カラムに関連しない     |クラス|
|[`@NonSelectProperty` ](#Entity-XxxxxProperty)|String property<br>boolean value (省略値: true)|SELECT SQL に使用しない|クラス|
|[`@NonInsertProperty` ](#Entity-XxxxxProperty)|String property<br>boolean value (省略値: true)|INSERT SQL に使用しない|クラス|
|[`@NonUpdateProperty` ](#Entity-XxxxxProperty)|String property<br>boolean value (省略値: true)|UPDATE SQL に使用しない|クラス|
|[`@SelectProperty`    ](#Entity-XxxxxProperty)|String property<br>String expression           |SELECT SQL で使用する式|クラス|
|[`@InsertProperty`    ](#Entity-XxxxxProperty)|String property<br>String expression           |INSERT SQL で使用する式|クラス|
|[`@UpdateProperty`    ](#Entity-XxxxxProperty)|String property<br>String expression           |UPDATE SQL で使用する式|クラス|

<div id="Entity-Table"></div>

[【目次へ】](#TOC) [【アノテーション一覧へ】](#Entity-Annotation)

##### 2-1-1. @Table
クラスに関連するデータベース・テーブル名を示します。
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
// Java での例
@Table("Contact")
public class Person extends PersonBase {

    @Table("super")
     public static class Ex extends Person {
```

```groovy:Groovy
// Groovy での例
@Table('Contact')
class Person extends PersonBase {

    @Table('super')
     static class Ex extends Person {
```

<div id="Entity-Key"></div>

##### 2-1-2. @Key
フィールドに関連するカラムがプライマリー・キーの一部である事を示します。

```java:Java
// Java での例
    @Key
    public int contactId;
    @Key
    public short childIndex;
```

```groovy:Groovy
// Groovy での例
    @Key
    int contactId
    @Key
    short childIndex
```

<div id="Entity-Column"></div>

##### 2-1-3. @Column
フィールドに関連するデータベース・カラム名を示します。
カラム名がフィールド名と同じであれば、このアノテーションを指定する必要がありません。

```java:Java
// Java での例
    @Column("firstName")
    public String first;
    @Column("lastName")
    public String last;
```

```groovy:Groovy
// Groovy での例
    @Column('firstName')
    String first
    @Column('lastName')
    String last
```

<div id="Entity-ColumnType"></div>

##### 2-1-4. @ColumnType
フィールドに関連するカラムの型を示します。
フィールド型とカラム型が同種類の場合は、指定する必要がありません。
フィールド型が日付型で、カラム型が数値型のように異なる場合に指定します。

```java:Java
// Java での例
    @ColumnType(Long.class)
    public Date birthday;
```

```groovy:Groovy
// Groovy での例
    @ColumnType(Long)
    Date birthday
```

<div id="Entity-NonColumn"></div>

[【目次へ】](#TOC) [【アノテーション一覧へ】](#Entity-Annotation)

##### 2-1-5. @NonColumn
フィールドがどのカラムにも関連しない事を示します。

```java:Java
// Java での例
    @NonColumn
    public List<Phone> phones;
    @NonColumn
    public List<Address> addresses;
```

```groovy:Groovy
// Groovy での例
    @NonColumn
    List<Phone> phones
    @NonColumn
    List<Address> addresses
```

<div id="Entity-NonSelect"></div>

##### 2-1-6. @NonSelect
フィールドに関連するカラムが SELECT SQL で使用されない事を示します。

```java:Java
// Java での例
    @NonSelect
    public Timestamp createdTime;
    @NonSelect
    public Timestamp updatedTime;
```

```groovy:Groovy
// Groovy での例
    @NonSelect
    Timestamp createdTime
    @NonSelect
    Timestamp updatedTime
```

<div id="Entity-NonInsert"></div>

##### 2-1-7. @NonInsert
フィールドに関連するカラムが INSERT SQL で使用されない事を示します。

```java:Java
// Java での例
    @NonInsert
    public Timestamp createdTime;
    @NonInsert
    public Timestamp updatedTime;
```

```groovy:Groovy
// Groovy での例
    @NonInsert
    Timestamp createdTime
    @NonInsert
    Timestamp updatedTime
```

<div id="Entity-NonUpdate"></div>

##### 2-1-8. @NonUpdate
フィールドに関連するカラムが UPDATE SQL で使用されない事を示します。

```java:Java
// Java での例
    @NonUpdate
    public Timestamp createdTime;
```

```groovy:Groovy
// Groovy での例
    @NonUpdate
    Timestamp createdTime
```

<div id="Entity-Select"></div>

[【目次へ】](#TOC) [【アノテーション一覧へ】](#Entity-Annotation)

##### 2-1-9. @Select
SELECT SQL のカラム名の代わりの式を指定します。

```java:Java
// Java での例
    @Select("{firstName}||' '||{lastName}")
    @NonInsert@NonUpdate
    public String fullName;
```

```groovy:Groovy
// Groovy での例
    @Select("{firstName}||' '||{lastName}")
    @NonInsert@NonUpdate
    String fullName
```

<div id="Entity-Insert"></div>

##### 2-1-10. @Insert
INSERT SQL の挿入値の式を示します。
このアノテーションが指定された場合、フィールドの値は使用されません。

```java:Java
// Java での例
    @Insert("CURRENT_TIMESTAMP")
    public Timestamp createdTime;
    @Insert("CURRENT_TIMESTAMP")
    public Timestamp updatedTime;
```

```groovy:Groovy
// Groovy での例
    @Insert('CURRENT_TIMESTAMP')
    Timestamp createdTime
    @Insert('CURRENT_TIMESTAMP')
    Timestamp updatedTime
```

<div id="Entity-Update"></div>

##### 2-1-11. @Update
UPDATE SQL の更新値の式を示します。
このアノテーションが指定された場合、フィールドの値は使用されません。

```java:Java
// Java での例
    @Update("{updateCount}+1")
    public int updateCount;
    @Update("CURRENT_TIMESTAMP")
    public Timestamp updatedTime;
```

```groovy:Groovy
// Groovy での例
    @Update('{updateCount}+1')
    int updateCount
    @Update("CURRENT_TIMESTAMP")
    Timestamp updatedTime
```

<div id="Entity-XxxxxProperty"></div>

[【目次へ】](#TOC) [【アノテーション一覧へ】](#Entity-Annotation)

##### 2-1-12. @KeyProperty, @ColumnProperty, ... @UpdateProperty
これらのアノテーションは、スーパークラスで定義されているフィールドに対して指定する場合に使用します。
指定された内容はサブクラスにも影響しますが、サブクラスでの指定が優先されます。
`value=false`, `column=""`, `type=Void.class`, `expression=""` を指定すると、スーパークラスでの指定が打ち消されます。

```java:Java
// Java での例
@KeyProperty(property="contactId")
@KeyProperty(property="childIndex")
public class Child extends ChildKey {
```

```groovy:Groovy
// Groovy での例
@KeyProperties([
    @KeyProperty(property='contactId'),
    @KeyProperty(property='childIndex')
])
class Child extends ChildKey {
```

<div id="Entity-Interface"></div>

### 2-2. エンティティ・クラスで実装するインターフェース

<div id="Entity-PreInsert"></div>

[【目次へ】](#TOC)

#### 2-2-1. PreInsert インターフェース
エンティティ・クラスがこのインターフェースを実装している場合、`Sql クラス`の `insert` メソッドで、INSERT SQL 実行前に `preInsert` メソッドがコールされます。
`preInsert` メソッドでは、プライマリー・キーの採番の実装等を行います。

```java:Java
// Java での例
public abstract class Common implements PreInsert {
    @Key
    public int id;
        ...

    @Override
    public int preInsert(Connection conn) {
        id = Numbering.getNewId(conn, getClass());
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
// Java での例
@Table("super")
public class ContactComposite extends Contact implements Composite {
    @NonColumn
    public final List<Phone> phones = new ArrayList<>();

    @Override
    public void postSelect(Connection conn) {
        if (id != 0) {
            new Sql<>(Phone.class).connection(conn)
                .where("{contactId}={}", id)
                .orderBy("{phoneNumber}")
                .select(phones::add);
        }
    }

    @Override
    public int postInsert(Connection conn) {
        phones.forEach(phone -> phone.contactId = id);
        int count = new Sql<>(Phone.class).connection(conn)
                .insert(phones);
        return count;
    }

    @Override
    public int postUpdate(Connection conn) {
        List<Integer> phoneIds = phones.stream()
            .map(phone -> phone.id)
            .filter(id -> id != 0)
            .collect(Collectors.toList());

        // Delete phones
        int count += new Sql<>(Phone.class).connection(conn)
            .where("{contactId}={}", id)
            .doIf(phoneIds.size() > 0,
                sql -> sql.and("{id} NOT IN {}", phoneIds)
            )
            .delete();

        // Uptete phones
        count += new Sql<>(Phone.class).connection(conn)
            .update(phones.stream()
                .filter(phone -> phone.id != 0)
                .collect(Collectors.toList()));

        // Insert phones
        count += new Sql<>(Phone.class).connection(conn)
            .insert(phones.stream()
                .filter(phone -> phone.id == 0)
                .collect(Collectors.toList()));

        return count;
    }
 
    @Override
    public int postDelete(Connection conn) {
        int count = new Sql<>(Phone.class).connection(conn)
            .where("{contactId}={}", id)
            .delete(conn);
        return count;
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
Contact contact = new Contact(1, "Akane", "Apple");

// トランザクション例
Transaction.execute(conn -> {
    // トランザクション開始
    new Sql<>(Contact.class).connection(conn)
        .insert(contact);
   ...
    // トランザクション終了
});
```


```groovy:Groovy
// Groovy での例
def contact = new Contact(1, 'Akane', 'Apple')

// トランザクション例
Transaction.execute {
    // トランザクション開始
    new Sql<>(Contact).connection(it)
        .insert(contact)
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
Transaction.execute(conn -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class).connection(conn)
        .where("{id}={}", 1)
        .select();
});
```

```groovy:Groovy
// Groovy での例
Transaction.execute {
    def contactOpt = new Sql<>(Contact).connection(it)
        .where('{id}={}', 1)
        .select()
}
```

```sql:SQL
-- 生成される SQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
```

<div id="ExecuteSQL-select-Entity"></div>

[【目次へ】](#TOC)

#### 5-1-2. SELECT 1行 / エンティティ条件

```java:Java
// Java での例
Contact contact = new Contact();
contact.id = 1;
Transaction.execute(conn -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class).connection(conn)
        .where(contact)
        .select();
});
```

```groovy:Groovy
// Groovy での例
def contact = new Contact()
contact.id = 1
Transaction.execute {
    def contactOpt = new Sql<>(Contact).connection(it)
        .where(contact)
        .select()
}
```

```sql:SQL
-- 生成される SQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
```

<div id="ExecuteSQL-select-N-Expression"></div>

[【目次へ】](#TOC)

#### 5-1-3. SELECT 複数行 / 式条件

```java:Java
// Java での例
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where("{lastName}={}", "Apple")
        .select(contacts::add)
);
```

```groovy:Groovy
// Groovy での例
def contacts = []
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where('{lastName}={}', 'Apple')
        .select({contacts << it})
}
```


```sql:SQL
-- 生成される SQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple'
```

<div id="ExecuteSQL-select-Subquery"></div>

[【目次へ】](#TOC)

#### 5-1-4. SELECT サブクエリ条件

```java:Java
// Java での例
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class, "C").connection(conn)
        .where("EXISTS",
            new Sql<>(Phone.class, "P")
                .where("{P.contactId}={C.id}")
        )
        .select(contacts::add)
);
```

```groovy:Groovy
// Groovy での例
def contacts = []
Transaction.execute {
    new Sql<>(Contact, 'C').connection(it)
        .where('EXISTS',
            new Sql<>(Phone, 'P')
                .where('{P.contactId}={C.id}')
        )
        .select({contacts << it})
}
```

```sql:SQL
-- 生成される SQL
SELECT C.id AS C_id, C.firstName AS C_firstName, C.lastName AS C_lastName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime FROM Contact C WHERE EXISTS (SELECT * FROM Phone P WHERE P.contactId=C.id)
```

<div id="ExecuteSQL-select-Expression-and"></div>

[【目次へ】](#TOC)

#### 5-1-5. SELECT 式条件 / AND

```java:Java
// Java での例
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where("{lastName}={}", "Apple")
        .and  ("{firstName}={}", "Akane")
        .select(contacts::add)
);
```

```groovy:Groovy
// Groovy での例
def contacts = []
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where('{lastName}={}', 'Apple')
        .and  ('{firstName}={}', 'Akane')
        .select({contacts << it})
}
```

```sql:SQL
-- 生成される SQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple' AND firstName='Akane'
```

<div id="ExecuteSQL-select-Expression-or"></div>

[【目次へ】](#TOC)

#### 5-1-6. SELECT 式条件 / OR

```java:Java
// Java での例
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where("{lastName}={}", "Apple")
        .or   ("{lastName}={}", "Orange")
        .select(contacts::add)
);
```

```groovy:Groovy
// Groovy での例
def contacts = []
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where('{lastName}={}', 'Apple')
        .or   ('{lastName}={}', 'Orange')
        .select({contacts << it})
}
```

```sql:SQL
-- 生成される SQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple' OR lastName='Orange'
```

<div id="ExecuteSQL-select-Expression-andor"></div>

[【目次へ】](#TOC)

#### 5-1-7. SELECT 式条件 / A AND B OR C AND D

```java:Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where(Condition
            .of ("{lastName}={}", "Apple")
            .and("{firstName}={}", "Akane")
        )
        .or(Condition
            .of ("{lastName}={}", "Orange")
            .and("{firstName}={}", "Setoka")
        )
        .select(contacts::add)
);
```
```java:Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact).connection(it)
        .where(Condition
            .of ('{lastName}={}', 'Apple')
            .and('{firstName}={}', 'Akane')
        )
        .or(Condition
            .of ('{lastName}={}', 'Orange')
            .and('{firstName}={}', 'Setoka')
        )
        .select({contacts << it})
);
```

```sql:SQL
-- 生成される SQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple' AND firstName='Akane' OR lastName='Orange' AND firstName='Setoka'
```

<div id="ExecuteSQL-select-columns"></div>

[【目次へ】](#TOC)

#### 5-1-8. SELECT カラムの選択

```java:Java
// Java での例
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where("{lastName}={}", "Apple")
        .columns("lastName", "firstName")
        .select(contacts::add)
);
```

```groovy:Groovy
// Groovy での例
def contacts = []
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where('{lastName}={}', 'Apple')
        .columns('lastName', 'firstName')
        .select({contacts << it})
}
```

```sql:SQL
-- 生成される SQL
SELECT firstName, lastName FROM Contact WHERE lastName='Apple'
```

<div id="ExecuteSQL-select-groupBy-having"></div>

[【目次へ】](#TOC)

#### 5-1-9. SELECT GROUP BY, HAVING

```java:Java
// Java での例
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class, "C").connection(conn)
        .columns("lastName")
        .groupBy("{lastName}")
        .having("COUNT({lastName})>=2")
        .select(contacts::add)
);
```

```groovy:Groovy
// Groovy での例
def contacts = []
Transaction.execute {
    new Sql<>(Contact, 'C').connection(it)
        .columns('lastName')
        .groupBy('{lastName}')
        .having('COUNT({lastName})>=2')
        .select({contacts << it})
}
```

```sql:SQL
-- 生成される SQL
SELECT MIN(C.lastName) AS C_lastName FROM Contact C GROUP BY C.lastName HAVING COUNT(C.lastName)>=2
```

<div id="ExecuteSQL-select-orderBy-offset-limit"></div>

[【目次へ】](#TOC)

#### 5-1-10. SELECT ORDER BY, OFFSET, LIMIT

```java:Java
// Java での例
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .orderBy("{lastName}")
        .orderBy("{firstName}")
        .orderBy("{id}")
        .offset(10).limit(5)
        .select(contacts::add)
);
```

```groovy:Groovy
// Groovy での例
def contacts = []
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .orderBy('{lastName}')
        .orderBy('{firstName}')
        .orderBy('{id}')
        .offset(10).limit(5)
        .select({contacts << it})
}
```

```sql:SQL
-- 生成される SQL / DB2, MySQL, PostgreSQL, SQLite
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact ORDER BY lastName ASC, firstName ASC, id ASC LIMIT 5 OFFSET 10
```

```sql:SQL
-- 生成される SQL / Oracle, SQLServer (取得時に行をスキップする)
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact ORDER BY lastName ASC, firstName ASC, id ASC
```

<div id="ExecuteSQL-select-forUpdate"></div>

[【目次へ】](#TOC)

#### 5-1-11. SELECT FOR UPDATE

```java:Java
// Java での例
Transaction.execute(conn -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class).connection(conn)
        .where("{id}={}", 1)
        .forUpdate()
        .select();
});
```

```groovy:Groovy
// Groovy での例
Transaction.execute {
    def contactOpt = new Sql<>(Contact).connection(it)
        .where('{id}={}', 1)
        .forUpdate()
        .select()
}
```

```sql:SQL
-- 生成される SQL / DB2
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1 FOR UPDATE WITH RS
```

```sql:SQL
-- 生成される SQL / MySQL, Oracle, PostgreSQL, SQLite
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1 FOR UPDATE
```

```sql:SQL
-- 生成される SQL / SQLite
-- SQLite では FOR UPDATE をサポートしていないので UnsupportedOperationException がスローされます。
```

```sql:SQL
-- 生成される SQL / SQLServer
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WITH (ROWLOCK,UPDLOCK) WHERE id=1
```

-- SQLite では、FOR UPDATE が未サポートのため、例外がスローされます。

<div id="ExecuteSQL-select-innerJoin"></div>

[【目次へ】](#TOC)

#### 5-1-12. SELECT 内部結合

```java:Java
// Java での例
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
Transaction.execute(conn ->
    new Sql<>(Contact.class, "C").connection(conn)
        .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
        .where("{C.id}={}", 1)
        .<Phone>select(contacts::add, phones::add)
);
```

```groovy:Groovy
// Groovy での例
def contacts = []
def phones = []
Transaction.execute {
    new Sql<>(Contact, 'C').connection(it)
        .innerJoin(Phone, 'P', '{P.contactId}={C.id}')
        .where('{C.id}={}', 1)
        .select({contacts << it}, {phones << it})
}
```

```sql:SQL
-- 生成される SQL
SELECT C.id AS C_id, C.firstName AS C_firstName, C.lastName AS C_lastName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C INNER JOIN Phone P ON P.contactId=C.id WHERE C.id=1
```

<div id="ExecuteSQL-select-leftJoin"></div>

[【目次へ】](#TOC)

#### 5-1-13. SELECT 左外部結合

```java:Java
// Java での例
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
Transaction.execute(conn ->
	new Sql<>(Contact.class, "C").connection(conn)
	    .leftJoin(Phone.class, "P", "{P.contactId}={C.id}")
	    .where("{C.lastName}={}", "Apple")
	    .<Phone>select(contacts::add, phones::add)
);
```

```groovy:Groovy
// Groovy での例
def contacts = []
def phones = []
Transaction.execute {
    new Sql<>(Contact, 'C').connection(it)
        .leftJoin(Phone, 'P', '{P.contactId}={C.id}')
        .where('{C.lastName}={}', 'Apple')
        .select({contacts << it}, {phones << it})
}
```

```sql:SQL
-- 生成される SQL
SELECT C.id AS C_id, C.firstName AS C_firstName, C.lastName AS C_lastName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C LEFT OUTER JOIN Phone P ON P.contactId=C.id WHERE C.lastName='Apple'
```

<div id="ExecuteSQL-select-rightJoin"></div>

[【目次へ】](#TOC)

#### 5-1-14. SELECT 右外部結合

```java:Java
// Java での例
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
Transaction.execute(conn ->
    new Sql<>(Contact.class, "C").connection(conn)
        .rightJoin(Phone.class, "P", "{P.contactId}={C.id}")
        .where("{P.label}={}", "Main")
        .<Phone>select(contacts::add, phones::add)
);
```

```groovy:Groovy
// Groovy での例
def contacts = []
def phones = []
Transaction.execute {
    new Sql<>(Contact, 'C').connection(it)
        .rightJoin(Phone, 'P', '{P.contactId}={C.id}')
        .where('{P.label}={}', 'Main')
        .select({contacts << it}, {phones << it})
}
```

```sql:SQL
-- 生成される SQL
-- SQLite では、RIGHT OUTER JOIN が未サポートのため、例外がスローされます。
SELECT C.id AS C_id, C.firstName AS C_firstName, C.lastName AS C_lastName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C RIGHT OUTER JOIN Phone P ON P.contactId=C.id WHERE P.label='Main'
```

#### 5-1-15. SELECT COUNT(*)

```java:Java
// Java での例
int[] rowCount = new int[1];
Transaction.execute(conn ->
    count[0] = new Sql<>(Contact.class).connection(conn)
        .where("lastName={}", "Apple")
        .selectCount()
);
```

```groovy:Groovy
// Groovy での例
def rowCount = 0
Transaction.execute {
    count = new Sql<>(Contact).connection(it)
        .where('lastName={}', 'Apple')
        .selectCount()
}
```

```sql:SQL
-- 生成される SQL
SELECT COUNT(*) FROM Contact WHERE lastName='Apple'
```

<div id="ExecuteSQL-insert"></div>

[【目次へ】](#TOC)

#### 5-2. INSERT

<div id="ExecuteSQL-insert-1"></div>

#### 5-2-1. INSERT 1行

```java:Java
// Java での例
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .insert(new Contact(1, "Akane", "Apple", 2001, 1, 1))
```

```groovy:Groovy
// Groovy での例
Transaction.execute {
    new Sql<>(Contact).connection(it)
       .insert(new Contact(1, "Akane", "Apple", 2001, 1, 1))
}
```

```sql:SQL
-- 生成される SQL / DB2, MySQL, Oracle, PostgreSQL
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (1, 'Akane', 'Apple', DATE'2001-01-01', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- 生成される SQL / SQLite
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (1, 'Akane', 'Apple', '2001-01-01', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- 生成される SQL / SQLServer
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (1, 'Akane', 'Apple', CAST('2001-01-01' AS DATE), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

<div id="ExecuteSQL-insert-N"></div>

[【目次へ】](#TOC)

#### 5-2-2. INSERT 複数行

```java:Java
// Java での例
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .insert(Arrays.asList(
            new Contact(2, "Yukari", "Apple", 2001, 1, 2),
            new Contact(3, "Azusa", "Apple", 2001, 1, 3)
        ))
```

```groovy:Groovy
// Groovy での例
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .insert([
            new Contact(2, "Yukari", "Apple", 2001, 1, 2),
            new Contact(3, "Azusa", "Apple", 2001, 1, 3)
        ])
}
```

```sql:SQL
-- 生成される SQL / DB2, MySQL, Oracle, PostgreSQL
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (2, 'Yukari', 'Apple', DATE'2001-01-02', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (3, 'Azusa', 'Apple', DATE'2001-01-03', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- 生成される SQL / SQLite
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (2, 'Yukari', 'Apple', '2001-01-02', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (3, 'Azusa', 'Apple', '2001-01-03', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- 生成される SQL / SQLServer
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (2, 'Yukari', 'Apple', CAST('2001-01-02' AS DATE), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (3, 'Azusa', 'Apple', CAST('2001-01-03' AS DATE), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

<div id="ExecuteSQL-update"></div>

[【目次へ】](#TOC)

#### 5-3. UPDATE

<div id="ExecuteSQL-update-1"></div>

#### 5-3-1. UPDATE 1行

```java:Java
// Java での例
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where("{id}={}", 1)
        .select()
        .ifPresent(contact -> {
            contact.firstName = "Akiyo";
            new Sql<>(Contact.class).connection(conn)
                .update(contact);
        })
);
```

```groovy:Groovy
// Groovy での例
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where('{id}={}', 1)
        .select()
        .ifPresent {Contact contact ->
            contact.firstName = 'Akiyo'
            new Sql<>(Contact).connection(it)
                .update(contact)
        }
}
```

```sql:SQL
-- 生成される SQL / DB2, MySQL, Oracle, PostgreSQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
UPDATE Contact SET firstName='Akiyo', lastName='Apple', birthday=DATE'2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
```

```sql:SQL
-- 生成される SQL / SQLite
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
UPDATE Contact SET firstName='Akiyo', lastName='Apple', birthday='2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
```

```sql:SQL
-- 生成される SQL / SQLServer
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
UPDATE Contact SET firstName='Akiyo', lastName='Apple', birthday=CAST('2001-01-01' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
```

<div id="ExecuteSQL-update-N"></div>

[【目次へ】](#TOC)

#### 5-3-2. UPDATE 複数行

```java:Java
// Java での例
Transaction.execute(conn -> {
    List<Contact> contacts = new ArrayList<>();
    new Sql<>(Contact.class).connection(conn)
        .where("{lastName}={}", "Apple")
        .select(contact -> {
            contact.lastName = "Apfel";
            contacts.add(contact);
        });
    new Sql<>(Contact.class).connection(conn)
        .update(contacts);
});
```

```groovy:Groovy
// Groovy での例
Transaction.execute {
    def contacts = []
    new Sql<>(Contact).connection(it)
        .where('{lastName}={}', 'Apple')
        .select({Contact contact ->
            contact.lastName = 'Apfel'
            contacts << contact
        })
    new Sql<>(Contact).connection(it)
        .update(contacts)
}
```

```sql:SQL
-- 生成される SQL / DB2, MySQL, Oracle, PostgreSQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple'
UPDATE Contact SET firstName='Akiyo', lastName='Apfel', birthday=DATE'2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
UPDATE Contact SET firstName='Yukari', lastName='Apfel', birthday=DATE'2001-01-02', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=2
UPDATE Contact SET firstName='Azusa', lastName='Apfel', birthday=DATE'2001-01-03', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=3
```

```sql:SQL
-- 生成される SQL / SQLite
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple'
UPDATE Contact SET firstName='Akiyo', lastName='Apfel', birthday='2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
UPDATE Contact SET firstName='Yukari', lastName='Apfel', birthday='2001-01-02', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=2
UPDATE Contact SET firstName='Azusa', lastName='Apfel', birthday='2001-01-03', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=3
```

```sql:SQL
-- 生成される SQL / SQLServer
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple'
UPDATE Contact SET firstName='Akiyo', lastName='Apfel', birthday=CAST('2001-01-01' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
UPDATE Contact SET firstName='Yukari', lastName='Apfel', birthday=CAST('2001-01-02' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=2
UPDATE Contact SET firstName='Azusa', lastName='Apfel', birthday=CAST('2001-01-03' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=3
```

<div id="ExecuteSQL-update-Condition"></div>

[【目次へ】](#TOC)

#### 5-3-3. UPDATE 指定条件, カラム選択

```java:Java
// Java での例
Contact contact = new Contact();
contact.lastName = "Pomme";
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where("{lastName}={}", "Apfel")
        .columns("lastName")
        .update(contact)
);
```

```groovy:Groovy
// Groovy での例
def contact = new Contact()
contact.lastName = 'Pomme'
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where('{lastName}={}', 'Apfel')
        .columns('lastName')
        .update(contact)
}
```

```sql:SQL
-- 生成される SQL
UPDATE Contact SET lastName='Pomme' WHERE lastName='Apfel'
```

<div id="ExecuteSQL-update-all"></div>

[【目次へ】](#TOC)

#### 5-3-4. UPDATE 全行

```java:Java
// Java での例
Contact contact = new Contact();
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where(Condition.ALL)
        .columns("birthday")
        .update(contact)
);
```

```groovy:Groovy
// Groovy での例
def contact = new Contact()
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where(Condition.ALL)
        .columns('birthday')
        .update(contact)
}
```

```sql:SQL
-- 生成される SQL
UPDATE Contact SET birthday=NULL
```

<div id="ExecuteSQL-delete"></div>

[【目次へ】](#TOC)

#### 5-4. DELETE

<div id="ExecuteSQL-delete-1"></div>

#### 5-4-1. DELETE  1行

```java:Java
// Java での例
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where("{id}={}", 1)
        .select()
        .ifPresent(contact ->
            new Sql<>(Contact.class).connection(conn)
                .delete(contact))
);
```

```groovy:Groovy
// Groovy での例
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where('{id}={}', 1)
        .select()
        .ifPresent {contact ->
            new Sql<>(Contact).connection(it)
                .delete(contact)
        }
}
```

```sql:SQL
-- 生成される SQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
DELETE FROM Contact WHERE id=1
```

<div id="ExecuteSQL-delete-N"></div>

[【目次へ】](#TOC)

#### 5-4-2. DELETE  複数行

```java:Java
// Java での例
Transaction.execute(conn -> {
    List<Contact> contacts = new ArrayList<>();
    new Sql<>(Contact.class).connection(conn)
        .where("{lastName}={}", "Pomme")
        .select(contacts::add);
    new Sql<>(Contact.class).connection(conn)
        .delete(contacts);
});
```

```groovy:Groovy
// Groovy での例
Transaction.execute {
    def contacts = []
    new Sql<>(Contact).connection(it)
        .where('{lastName}={}', 'Pomme')
        .select({contacts << it})
    new Sql<>(Contact).connection(it)
        .delete(contacts)
}
```

```sql:SQL
-- 生成される SQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Pomme'
DELETE FROM Contact WHERE id=2
DELETE FROM Contact WHERE id=3
```

<div id="ExecuteSQL-delete-Condition"></div>

[【目次へ】](#TOC)

#### 5-4-3. DELETE  指定条件

```java:Java
// Java での例
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where("{lastName}={}", "Orange")
        .delete()
);
```

```groovy:Groovy
// Groovy での例
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where('{lastName}={}', 'Orange')
        .delete()
}
```

```sql:SQL
-- 生成される SQL
DELETE FROM Contact WHERE lastName='Orange'
```

<div id="ExecuteSQL-delete-all"></div>

[【目次へ】](#TOC)

#### 5-4-4. DELETE 全行

```java:Java
// Java での例
Transaction.execute(conn ->
    new Sql<>(Phone.class).connection(conn)
        .where(Condition.ALL)
        .delete()
);
```

```groovy:Groovy
// Groovy での例
Transaction.execute {
    new Sql<>(Phone).connection(it)
        .where(Condition.ALL)
        .delete()
}
```

```sql:SQL
-- 生成される SQL
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
