Lightsleep / マニュアル
===========

### 1. エンティティ・クラス
エンティティ・クラスは、SELECT SQL で取得したデータを格納するためのクラスで、各データベース・テーブル毎に作成します。

#### 1-1. エンティティ・クラスで使用するアノテーション
##### 1-1-1. Table アノテーション
クラスに関連するデータベース・テーブル名を指定します。
テーブル名がクラス名と同じであれば、このアノテーションを指定する必要はありません。

```java:Java
@Table("Person")
public class Person1 extends Person {
   ...
}
```

```@Table("super")``` を指定した場合は、スーパークラスのクラス名がテーブル名となります。

```java:Java
@Table("super")
public class Person1 extends Person {
   ...
}
```

##### 1-1-2. Key アノテーション
フィールドに関連するカラムがプライマリー・キーの一部である事を指定します。

```java:Java
@Key
public String personId;
```

##### 1-1-3. Column アノテーション
フィールドに関連するデータベース・カラム名を指定します。
カラム名がフィールド名と同じであれば、このアノテーションを指定する必要がありません。

```java:Java
    @Column("lastName")
    public String last;
```

##### 1-1-4. NonColumn アノテーション
フィールドがどのカラムにも関連しない事を指定します。

```java:Java
    @NonColumn
    public Address address = new Address();
```

##### 1-1-5. NonSelect アノテーション
フィールドに関連するカラムが SELECT SQL で使用されない事を指定します。

```java:Java
    @Column("firstName")
    @NonSelect
    public String first;
```

##### 1-1-6. NonInsert アノテーション
フィールドに関連するカラムが INSERT SQL で使用されない事を指定します。

```java:Java
    @Select("CONCAT({name.first}, ' ', {name.last})") // MySQL, Oracle
    @NonInsert
    @NonUpdate
    public String fullName;
```

##### 1-1-7. NonUpdate アノテーション
フィールドに関連するカラムが UPDATE SQL で使用されない事を指定します。

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @NonUpdate
    public Timestamp created;
```

##### 1-1-8. Select アノテーション
SELECT SQL のカラム名の代わりの式を指定します。

```java:Java
    @Select("CONCAT({name.first}, ' ', {name.last})") // MySQL, Oracle
    @NonInsert
    @NonUpdate
    public String fullName;
```

##### 1-1-9. Insert アノテーション
INSERT SQL の挿入値の式を指定します。
このアノテーションが指定された場合、フィールドの値は使用されません。

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @NonUpdate
    public Timestamp created;
```

##### 1-1-10. Update アノテーション
UPDATE SQL の更新値の式を指定します。
このアノテーションが指定された場合、フィールドの値は使用されません。

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @Update("CURRENT_TIMESTAMP")
    public Timestamp updated;
```

### 1-2. エンティティ・クラスが実装するインターフェース
#### 1-2-1. PreInsert インターフェース
エンティティ・クラスがこのインターフェースを実装している場合、Sql クラスの ```insert``` メソッドで、INSERT SQL 実行前に ```preInsert``` メソッドがコールされます。
```preInsert``` メソッドでは、プライマリー・キーの採番の実装等を行います。

```java:Java
public class Person implements PreInsert {
    @Key
    public String personId;

   ...

    @Override
    public int preInsert(Connection connection) {
        personId = Numbering.getNewId(Person.class);
        return 0;
    }
}
```

#### 1-2-2. Composite インターフェース
エンティティ・クラスがこのインターフェースを実装している場合、Sql クラスの ```select```, ```insert```, ```update``` または ```delete``` メソッドで、各 SQL の実行後にエンティティ・クラスの ```postSelect```, ```postInsert```, ```postUpdate```  または ```postDelete``` メソッドがコールされます。
ただし ```update```, ```delete``` メソッドで、引数にエンティティがない場合は、コールされません。
エンティティが他のエンティティを内包する場合、このインターフェースを実装する事で、内包するエンティティへの SQL 処理を連動して行う事ができるようになります。

```java:Java
@Table("super")
public class PersonComposite extends Person implements Composite {
    @NonColumn
    public final List<Phone> phones = new ArrayList<>();

    @Override
    public void postSelect(Connection connection) {
        if (personId != null)
            new Sql<>(Phone.class)
                .where("{personId} = {}", personId)
                .orderBy("{phoneNumber}")
                .select(connection, phones::add);
    }

    @Override
    public int postInsert(Connection connection) {
        int count = 0;

        phones.stream().forEach(phone -> phone.personId = personId);
        count += new Sql<>(Phone.class)
            .insert(connection, phones);

        return count;
    }

    @Override
    public int postUpdate(Connection connection) {
        int count = 0;

        List<String> phoneIds = phones.stream()
            .map(phone -> phone.phoneId)
            .filter(phoneId -> phoneId != null)
            .collect(Collectors.toList());

        count += new Sql<>(Phone.class)
            .where("{personId} = {}", personId)
            .doIf(phoneIds.size() > 0,
                sql -> sql.and("{phoneId} NOT IN {}", phoneIds)
            )
            .delete(connection);

        count += new Sql<>(Phone.class)
            .update(connection, phones.stream()
                .filter(phone -> phone.phoneId != null)
                .collect(Collectors.toList()));

        count += new Sql<>(Phone.class)
            .insert(connection, phones.stream()
                .filter(phone -> phone.phoneId == null)
                .collect(Collectors.toList()));

        return count;
    }

    @Override
    public int postDelete(Connection connection) {
        int count = 0;

        count += new Sql<>(Phone.class)
            .where("{personId} = {}", personId)
            .delete(connection);

        return count;
    }
}
```

### 2. トランザクション

```Transaction.execute``` メソッドの実行が1つのトランザクションの実行に相当します。
トランザクションの内容を引数 ```transaction``` (ラムダ式) で定義してください。
ラムダ式は、```Transaction.executeBody``` メソッドの内容に相当し、このメソッドの引数は、```Connection``` です。

```java:Java
// トランザクション定義例
Transaction.execute(connection -> {
    // トランザクション内容開始
    new Sql<>(Person.class)
        .update(connection, person);
   ...
    // トランザクション内容終了
});
```

トランザクション中に例外がスローされた場合は、```Transaction.rollback``` メソッドが実行され、
そうでなければ ```Transaction.commit``` メソッドが実行されます。

### 3. コネクション・サプライヤー
データベース・コネクション(```java.sql.Connection```) の取得は、```Transaction.execute``` メソッド内で行われます。
Lightsleep にはコネクションを供給するクラスとして以下があります。

1. org.lightsleep.connection.C3p0
1. org.lightsleep.connection.Dbcp
1. org.lightsleep.connection.HikariCP
1. org.lightsleep.connection.TomcatCP
1. org.lightsleep.connection.Jdbc
1. org.lightsleep.connection.Jndi

```C3p0```, ```Dbcp```, ```HikariCP```, ```TomcatCP``` クラスは、それぞれ対応するコネクション・プール・ライブラリを使用してデータベース・コネクションを取得します。  
```JdbcConnection``` クラスは、```java.sql.DriverManager.getConnection``` メソッドを使用してデータベース・コネクションを取得します。  
```JndiConnection``` クラスは、JNDI (Java Naming and Directory Interface) を使用して取得したデータソース (```javax.sql.DataSource```) からデータベース・コネクションを取得します。  
コネクションを供給するクラスおよび接続に必要な情報 **lightsleep.properties** ファイルに定義してください。

```properties:lightsleep.properties
# lightsleep.properties / C3p0 設定サンプル
ConnectionSupplier = C3p0
driver   = com.mysql.jdbc.Driver
url      = jdbc:mysql://MySQL57/test
user     = test
password = _test_
```

```properties:c3p0.properties
# c3p0.properties
c3p0.initialPoolSize = 20
c3p0.minPoolSize     = 10
c3p0.maxPoolSize     = 30
```

```properties:lightsleep.properties
# lightsleep.properties / Dbcp 設定サンプル
ConnectionSupplier = Dbcp
driverClassName = oracle.jdbc.driver.OracleDriver
url             = jdbc:oracle:thin:@Oracle121:1521:test
username        = test
password        = _test_
initialSize     = 20
maxTotal        = 30
```

```properties:lightsleep.properties
# lightsleep.properties / HikariCP 設定サンプル
ConnectionSupplier = HikariCP
driverClassName = org.postgresql.Driver
jdbcUrl         = jdbc:postgresql://Postgres95/test
user            = test
password        = _test_
minimumIdle     = 10
maximumPoolSize = 30
```

```properties:lightsleep.properties
# lightsleep.properties / TomcatCP 設定サンプル
ConnectionSupplier = TomcatCP
driverClassName = com.microsoft.sqlserver.jdbc.SQLServerDriver
url             = jdbc:sqlserver://SQLServer13;database=test
username        = test
password        = _test_
initialSize     = 20
maxActive       = 30
```

```properties:lightsleep.properties
# lightsleep.properties / Jdbc 設定サンプル
ConnectionSupplier      = Jdbc
driver   = com.mysql.jdbc.Driver
url      = jdbc:mysql://MySQL57/test
user     = test
password = _test_
```

```properties:lightsleep.properties
# lightsleep.properties / Jndi 設定サンプル
connectionSupplier = Jndi
dataSource = jdbc/Sample
```


### 4. SQLの実行
SQLの実行は、Sql クラスの各種メソッドを使用し、Transaction.execute メソッドの引数のラムダ式内に定義します。

#### 4-1. SELECT
#### 4-1-1. SELECT 1行 / 式条件

```java:Java
Optional<Person> personOpt = new Sql<>(Person.class)
    .where("{personId} = {}", personId)
    .select(connection);

Person person = new Sql<>(Person.class)
    .where("{personId} = {}", personId)
    .select(connection).orElse(null);
```

```sql:SQL
SELECT personId, lastName, firstName, ... FROM Person WHERE personId = '...'
```

#### 4-1-2. SELECT 1行 / エンティティ条件

```java:Java
Person person = new Person();
person.id = personId;
Optional<Person> personOpt = new Sql<>(Person.class)
    .where(person)
    .select(connection);
```

```sql:SQL
SELECT personId, lastName, firstName, ... FROM Person WHERE personId = '...'
```

#### 4-1-3. SELECT 複数行 / 式条件

```java:Java
List<Person> person = new ArrayList<Person>();
new Sql<>(Person.class)
    .where("{name.last} = {}", lastName)
    .select(connection, person::add);
```

```sql:SQL
SELECT personId, lastName, firstName, ... FROM Person WHERE lastName = '...'
```

#### 4-1-4. SELECT サブクエリ条件

```java:Java
List<Person> person = new ArrayList<Person>();
new Sql<>(Person.class, "PS")
    .where("EXISTS",
        new Sql<>(Phone.class, "PH")
            .where("{PH.personId} = {PS.personId}")
    )
    .select(connection, person::add);
```

```sql:SQL
SELECT PS.personId AS PS_personId,
  PS.lastName AS PS_lastName,
  PS.firstName AS PS_firstName,
  ...
  FROM Person PS
  WHERE EXISTS (SELECT * FROM Phone PH WHERE PH.personId = PS.personId)
```

#### 4-1-5. SELECT 式条件 / AND

```java:Java
List<Person> person = new ArrayList<Person>();
new Sql<>(Person.class)
    .where("{name.last} = {}", lastName)
    .and  ("{name.first} = {}", firstName)
    .select(connection, person::add);
```

```sql:SQL
SELECT personId, lastName, firstName, ... FROM Person
  WHERE (lastName = '...' AND firstName = '...')
```

#### 4-1-6. SELECT 式条件 / OR

```java:Java
List<Person> person = new ArrayList<Person>();
new Sql<>(Person.class)
    .where("{name.last} = {}", lastName1)
    .or   ("{name.last} = {}", lastName2)
    .select(connection, person::add);
```

```sql:SQL
SELECT personId, lastName, firstName, ... FROM Person
  WHERE (lastName = '...' OR lastName = '...')
```

#### 4-1-7. SELECT 式条件 / (A AND B) OR (C AND D)

```java:Java
List<Person> person = new ArrayList<Person>();
new Sql<>(Person.class)
    .where(Condition
        .of ("{name.last} = {}", lastName1)
        .and("{name.first} = {}", firstName1)
    )
    .or(Condition
        .of ("{name.last} = {}", lastName2)
        .and("{name.first} = {}", firstName2)
    )
    .select(connection, person::add);
```

```sql:SQL
SELECT personId, lastName, firstName, ... FROM Person
  WHERE ((lastName = '...' AND firstName = '...')
    OR (lastName = '...' AND firstName = '...'))
```

#### 4-1-8. SELECT カラムの選択

```java:Java
List<Person> person = new ArrayList<Person>();
new Sql<>(Person.class)
    .where("{name.last} = {}", lastName)
    .columns("name.last", "name.first")
    .select(connection, person::add);
```

```sql:SQL
SELECT lastName, firstName FROM Person WHERE lastName = '...'
```

#### 4-1-9. SELECT GROUP BY, HAVING

```java:Java
List<Person> person = new ArrayList<Person>();
new Sql<>(Person.class)
    .columns("name.first")
    .groupBy("{name.first}")
    .having("COUNT({name.first}) = 2")
    .select(connection, person::add);
```

```sql:SQL
SELECT MIN(firstName) FROM Person GROUP BY firstName HAVING COUNT(firstName) = 2
```

#### 4-1-10. SELECT OFFSET, LIMIT, ORDER BY

```java:Java
List<Person> person = new ArrayList<Person>();
new Sql<>(Person.class)
    .orderBy("{name.last}")
    .orderBy("{name.first}")
    .orderBy("{personId}")
    .offset(100).limit(10)
    .select(connection, person::add);
```

```sql:SQL
SELECT personId, lastName, firstName, ... FROM Person
  ORDER BY lastName ASC, firstName ASC, personId ASC
  LIMIT 10 OFFSET 100
```

#### 4-1-11. SELECT FOR UPDATE

```java:Java
Optional<Person> personOpt = new Sql<>(Person.class)
    .where("{personId} = {}", personId)
    .forUpdate()
    .select(connection);
```

```sql:SQL
SELECT personId, lastName, firstName, birthday, ..., updated FROM Person
  WHERE personId = '...' FOR UPDATE
```

#### 4-1-12. SELECT 内部結合

```java:Java
List<Person> persons = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
new Sql<>(Person.class, "PS")
    .innerJoin(Phone.class, "PH", "{PH.personId} = {PS.personId}")
    .where("{PS.personId} = {}", personId)
    .<Phone>select(connection, persons::add, phones::add);
```

```sql:SQL
SELECT PS.personId AS PS_personId,
  PS.lastName AS PS_lastName,
  PS.firstName AS PS_firstName,
  ...,
  PH.phoneId AS PH_phoneId,
  PH.personId AS PH_personId,
  PH.phoneNumber AS PH_phoneNumber,
  ...
  FROM Person PS
  INNER JOIN Phone PH ON PH.personId = PS.personId
  WHERE PS.personId = '...'
```

#### 4-1-13. SELECT 左外部結合
```java:Java
List<Person> persons = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
new Sql<>(Person.class, "PS")
    .leftJoin(Phone.class, "PH", "{PH.personId} = {PS.personId}")
    .where("{PS.personId} = {}", personId)
    .<Phone>select(connection, persons::add, phones::add);
```

```sql:SQL
SELECT PS.personId AS PS_personId,
  PS.lastName AS PS_lastName,
  PS.firstName AS PS_firstName,
  ...,
  PH.phoneId AS PH_phoneId,
  PH.personId AS PH_personId,
  PH.phoneNumber AS PH_phoneNumber,
  ...
  FROM Person PS
  LEFT OUTER JOIN Phone PH ON PH.personId = PS.personId
  WHERE PS.personId = '...'
```

#### 4-1-13. SELECT 右外部結合
```java:Java
List<Person> persons = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
new Sql<>(Person.class, "PS")
    .rightJoin(Phone.class, "PH", "{PH.personId} = {PS.personId}")
    .where("{PS.personId} = {}", personId)
    .<Phone>select(connection, persons::add, phones::add);
```

```sql:SQL
SELECT PS.personId AS PS_personId,
  PS.lastName AS PS_lastName,
  PS.firstName AS PS_firstName,
  ...,
  PH.phoneId AS PH_phoneId,
  PH.personId AS PH_personId,
  PH.phoneNumber AS PH_phoneNumber,
  ...
  FROM Person PS
  RIGHT OUTER JOIN Phone PH ON PH.personId = PS.personId
  WHERE PS.personId = '...'
```

#### 4-2. INSERT
#### 4-2-1. INSERT 1行

```java:Java
Person person = new Person();
・・・
new Sql<>(Person.class)
    .insert(connection, person);
```

```sql:SQL
INSERT INTO Person (personId, lastName, firstName, ...) VALUES ('...', '...', '...', ...)
```

#### 4-2-2. INSERT 複数行
```java:Java
List<Person> persons = new ArrayList<>();
・・・
new Sql<>(Person.class)
    .insert(connection, persons);
```

```sql:SQL
INSERT INTO Person (personId, lastName, firstName, ...) VALUES ('...', '...', '...', ...)
・・・
```

#### 4-3. UPDATE
#### 4-3-3. UPDATE 1行

```java:Java
Person person = new Person();
・・・
new Sql<>(Person.class)
    .update(connection, person);
```

```sql:SQL
UPDATE Person SET lastName='...', firstName='...', ... WHERE personId = '...'
```

#### 4-3-3. UPDATE 複数行

```java:Java
List<Person> persons = new ArrayList<>();
・・・
new Sql<>(Person.class)
    .update(connection, persons);
```

```sql:SQL
UPDATE Person SET lastName='...', firstName='...', ... WHERE personId = '...'
UPDATE Person SET lastName='...', firstName='...', ... WHERE personId = '...'
   ...
```

#### 4-3-3. UPDATE 指定条件

```java:Java
Person person = new Person();
・・・
new Sql<>(Person.class)
    .where("{name.last} = {}", lastName)
    .update(connection, person);
```

```sql:SQL
UPDATE Person SET lastName='...', firstName='...', ... WHERE lastName = '...'
```

#### 4-3-4. UPDATE 全行

```java:Java
Person person = new Person();
・・・
new Sql<>(Person.class)
    .where(Condition.ALL)
    .update(connection, person);
```

```sql:SQL
UPDATE Person SET lastName='...', firstName='...', ...
```

#### 4-4. DELETE
#### 4-4-1. DELETE  1行
```java:Java
Person person = new Person();
・・・
new Sql<>(Person.class)
    .delete(connection, person);
```

```sql:SQL
DELETE FROM Person WHERE personId = '...'
```

#### 4-4-2. DELETE  複数行
```java:Java
List<Person> persons = new ArrayList<>();
・・・
new Sql<>(Person.class)
    .delete(connection, persons);
```

```sql:SQL
DELETE FROM Person WHERE personId = '...'
DELETE FROM Person WHERE personId = '...'
   ...
```

#### 4-4-3. DELETE  指定条件
```java:Java
new Sql<>(Person.class)
    .where("{name.last} = {}", lastName)
    .delete(connection);
```

```sql:SQL
DELETE FROM Person WHERE lastName = '...'
```

#### 4-4-4. DELETE  全行
```java:Java
new Sql<>(Person.class)
    .where(Condition.ALL)
    .delete(connection);
```

```sql:SQL
DELETE FROM Person
```

### 5. ログ出力
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
