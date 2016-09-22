Lightsleep / チュートリアル
===========

テーブルの行を取得してコンソールに出力する簡単なプログラムを作成してみます。

#### 1. テーブルの準備

MySQL, Oracle, PostgreSQL または SQL Server のいずれかのデータベースに Person テーブルを作成し、サンプルデータを挿入します。

以下の SQL のいずれかを実行してテーブルを作成します。

```sql:ddl_mysql.sql
CREATE TABLE Person (
    personId    CHAR   (12) NOT NULL,
    firstName   VARCHAR(10) NOT NULL,
    lastName    VARCHAR(10) NOT NULL,
    birthday    DATE        NOT NULL,
    addressId   CHAR   (12) NOT NULL,

    updateCount INT         NOT NULL,
    created     DATETIME    NOT NULL,
    updated     DATETIME    NOT NULL,

    PRIMARY KEY(personId)
);
```

```sql:ddl_oracle.sql
CREATE TABLE Person (
    personId    CHAR    (12)      NOT NULL,
    firstName   VARCHAR2(10 CHAR) NOT NULL,
    lastName    VARCHAR2(10 CHAR) NOT NULL,
    birthday    DATE              NOT NULL,
    addressId   CHAR    (12)      NOT NULL,

    updateCount NUMBER  ( 9)      NOT NULL,
    created     TIMESTAMP         NOT NULL,
    updated     TIMESTAMP         NOT NULL,

    PRIMARY KEY(personId)
);
```

```sql:ddl_postgresql.sql
CREATE TABLE Person (
    personId    CHAR   (12) NOT NULL,
    firstName   VARCHAR(10) NOT NULL,
    lastName    VARCHAR(10) NOT NULL,
    birthday    DATE        NOT NULL,
    addressId   CHAR   (12) NOT NULL,

    updateCount INT         NOT NULL,
    created     TIMESTAMP   NOT NULL,
    updated     TIMESTAMP   NOT NULL,

    PRIMARY KEY(personId)
);
```

```sql:ddl_sqlserver.sql
CREATE TABLE Person (
    personId    CHAR   (12)  NOT NULL,
    firstName   VARCHAR(20)  NOT NULL, -- x2
    lastName    VARCHAR(20)  NOT NULL, -- x2
    birthday    DATE         NOT NULL,
    addressId   CHAR   (12)  NOT NULL,

    updateCount INT          NOT NULL,
    created     DATETIME2(3) NOT NULL,
    updated     DATETIME2(3) NOT NULL,

    PRIMARY KEY(personId)
);
```

以下の SQL を実行してテーブルにデータを挿入します。

```sql:sample.sql
DELETE FROM Person;
INSERT INTO Person VALUES ('PRSN00000001', 'First' , 'Sample', DATE'1991-01-01', 'ADDR00000001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO Person VALUES ('PRSN00000002', 'Second', 'Sample', DATE'1992-02-02', 'ADDR00000001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO Person VALUES ('PRSN00000003', 'Third' , 'Sample', DATE'1993-03-03', 'ADDR00000001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO Person VALUES ('PRSN00000004', 'Fourth', 'Sample', DATE'1994-04-04', 'ADDR00000001', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
````

#### 2. エンティティ・クラスの作成

Person テーブルから取得した行を保持するためのエンティティ・クラスを作成します。

```java:Person.java
package org.lightsleep.tutorial.entity;

import java.sql.Date;
import java.sql.Timestamp;

import org.lightsleep.entity.*;

/**
    Person
*/
public class Person {
    /** Person ID */
    @Key
    public String personId;

    /** First Name */
    public String firstName;

    /** Last Name */
    public String lastName;

    /** Birthday */
    public Date birthday;

    /** Address ID */
    public String addressId;

    /** Update Count */
    @Insert("0")
    @Update("{updateCount} + 1")
    public int updateCount;

    /** Created Timestamp */
    @Insert("CURRENT_TIMESTAMP")
    @NonUpdate
    public Timestamp created;

    /** Updated Timestamp */
    @Insert("CURRENT_TIMESTAMP")
    @Update("CURRENT_TIMESTAMP")
    public Timestamp updated;
}
```

#### 3. プロパティ・ファイルの準備

下記の lightsleep.properties を作成します。
url, user および password の値は、使用するデータベース環境に合わせて変更してください。

```properties:lightsleep.properties
# for MySQL
Logger             = Std$Out$Info
Database           = MySQL
ConnectionSupplier = Jdbc
driver             = com.mysql.jdbc.Driver
url                = jdbc:mysql://MySQL57/test
user               = test
password           = _test_
```

```properties:lightsleep.properties
# for Oracle
Logger             = Std$Out$Info
Database           = Oracle
ConnectionSupplier = Jdbc
driver             = oracle.jdbc.driver.OracleDriver
url                = jdbc:oracle:thin:@Oracle121:1521:test
user               = test
password           = _test_
```

```properties:lightsleep.properties
# for PostgreSQL
Logger             = Std$Out$Info
Database           = PostgreSQL
ConnectionSupplier = Jdbc
driver             = org.postgresql.Driver
url                = jdbc:mysql://Postgres95/test
user               = test
password           = _test_
```

```properties:lightsleep.properties
# for SQL Server
Logger             = Std$Out$Info
Database           = SQLServer
ConnectionSupplier = Jdbc
driver             = com.microsoft.sqlserver.jdbc.SQLServerDriver
url                = jdbc:sqlserver://SQLServer13;Database=test
user               = test
password           = _test_
```

#### 4. データの取得
テーブルから全行を取得するプログラムを作成します。

```java:Sample1.java
package org.lightsleep.tutorial;

import java.util.ArrayList;
import java.util.List;

import org.lightsleep.Sql;
import org.lightsleep.Transaction;
import org.lightsleep.tutorial.entity.Person;

public class Sample1 {
    public static void main(String[] args) {
        try {
            List<Person> persons = new ArrayList<>();
            Transaction.execute(connection -> {
                new Sql<>(Person.class)
                    .select(connection, persons::add);
            });

            for (int index = 0; index < persons.size(); ++index) {
                Person person = persons.get(index);
                System.out.println(
                    index
                    + ": Name: " + person.firstName + " " + person.lastName
                    + ", Birthday: " + person.birthday
                );
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

Sample1 を実行すると以下がコンソールに表示されます。

```log:標準出力
    ...
    ...
    ...
0: Name: First Sample, Birthday: 1991-01-01
1: Name: Second Sample, Birthday: 1992-02-02
2: Name: Third Sample, Birthday: 1993-03-03
3: Name: Fourth Sample, Birthday: 1994-04-04
```
