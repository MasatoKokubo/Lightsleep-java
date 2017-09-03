Lightsleep / チュートリアル
===========

テーブルの行を取得してコンソールに出力する簡単なプログラムを作成してみます。

#### 1. テーブルの準備

MySQL, Oracle, PostgreSQL, SQLite または SQL Server のいずれかのデータベースに Contact テーブルを作成し、サンプルデータを挿入します。

以下の SQL のいずれかを実行してテーブルを作成します。

```sql:ddl_db2.sql
-- for DB2
CREATE TABLE Contact (
    id        INTEGER        NOT NULL,
    firstName VARGRAPHIC(20) NOT NULL,
    lastName  VARGRAPHIC(20) NOT NULL,
    birthday  DATE               NULL,

    PRIMARY KEY(id)
);
```

```sql:ddl_mysql.sql
-- for MySQL
CREATE TABLE Contact (
    id         INT         NOT NULL,
    firstName  VARCHAR(20) NOT NULL,
    lastName   VARCHAR(20) NOT NULL,
    birthday   DATE            NULL,

    PRIMARY KEY(id)
);
```

```sql:ddl_oracle.sql
-- for Oracle
CREATE TABLE Contact (
    id         NUMBER  ( 9)      NOT NULL,
    firstName  VARCHAR2(20 CHAR) NOT NULL,
    lastName   VARCHAR2(20 CHAR) NOT NULL,
    birthday   DATE                  NULL,

    PRIMARY KEY(id)
);
```

```sql:ddl_postgresql.sql
-- for PostgreSQL
CREATE TABLE Contact (
    id         INT         NOT NULL,
    firstName  VARCHAR(20) NOT NULL,
    lastName   VARCHAR(20) NOT NULL,
    birthday   DATE            NULL,

    PRIMARY KEY(id)
);
```

```sql:ddl_sqlite.sql
-- for SQLite
CREATE TABLE Contact (
    id        INTEGER     NOT NULL,
    firstName VARCHAR(20) NOT NULL,
    lastName  VARCHAR(20) NOT NULL,
    birthday  TEXT            NULL,

    PRIMARY KEY(id)
);
```

```sql:ddl_sqlserver.sql
-- for SQLServer
CREATE TABLE Contact (
    id         INT         NOT NULL,
    firstName  VARCHAR(20) NOT NULL,
    lastName   VARCHAR(20) NOT NULL,
    birthday   DATE            NULL,

    PRIMARY KEY(id)
);
```

以下の SQL を実行してテーブルにデータを挿入します。

```sql:sample.sql
DELETE FROM Contact;
INSERT INTO Contact VALUES (1, 'First' , 'Example', DATE'1991-01-01');
INSERT INTO Contact VALUES (2, 'Second', 'Example', DATE'1992-02-02');
INSERT INTO Contact VALUES (3, 'Third' , 'Example', DATE'1993-03-03');
INSERT INTO Contact VALUES (4, 'Fourth', 'Example', DATE'1994-04-04');
````

#### 2. エンティティ・クラスの作成

Contact テーブルから取得した行を保持するためのエンティティ・クラスを作成します。

```java:Contact.java
package org.lightsleep.tutorial.entity;

import java.sql.Date;

import org.lightsleep.entity.*;

/**
 * Contact エンティティ
 */
public class Contact {
    /** ID */
    @Key
    public int id;

    /** 名前 */
    public String firstName;

    /** 苗字 */
    public String lastName;

    /** 誕生日 */
    public Date birthday;
}
```

#### 3. プロパティ・ファイルの準備

下記の lightsleep.properties を作成します。
url, user および password の値は、使用するデータベース環境に合わせて変更してください。

```properties:lightsleep.properties
# for DB2
Logger             = Std$Out$Info
Database           = DB2
ConnectionSupplier = Jdbc
url                = jdbc:db2://<DB Server>:50000/<データベース>
user               = <ユーザー名>
password           = <パスワード>
```

```properties:lightsleep.properties
# for MySQL
Logger             = Std$Out$Info
Database           = MySQL
ConnectionSupplier = Jdbc
url                = jdbc:mysql://<DB Server>/<データベース>
user               = <ユーザー名>
password           = <パスワード>
```

```properties:lightsleep.properties
# for Oracle
Logger             = Std$Out$Info
Database           = Oracle
ConnectionSupplier = Jdbc
url                = jdbc:oracle:thin:@<DB Server>:1521:<SID>
user               = <ユーザー名>
password           = <パスワード>
```

```properties:lightsleep.properties
# for PostgreSQL
Logger             = Std$Out$Info
Database           = PostgreSQL
ConnectionSupplier = Jdbc
url                = jdbc:postgresql://<DB Server>/<データベース>
user               = <ユーザー名>
password           = <パスワード>
```

```properties:lightsleep.properties
# for SQLite
Logger             = Std$Out$Info
Database           = SQLite
ConnectionSupplier = Jdbc
url                = jdbc:sqlite:<Installed Directory>/<データベース>
```

```properties:lightsleep.properties
# for SQL Server
Logger             = Std$Out$Info
Database           = SQLServer
ConnectionSupplier = Jdbc
url                = jdbc:sqlserver://<DB Server>;Database=<データベース>
user               = <ユーザー名>
password           = <パスワード>
```

#### 4. データの取得
テーブルから全行を取得するプログラムを作成します。

```java:Example1.java
package org.lightsleep.tutorial;

import java.util.ArrayList;
import java.util.List;

import org.lightsleep.Sql;
import org.lightsleep.Transaction;
import org.lightsleep.tutorial.entity.Contact;

public class Example1 {
    public static void main(String[] args) {
        try {
            List<Contact> contacts = new ArrayList<>();
            Transaction.execute(conn -> {
                new Sql<>(Contact.class).connection(conn)
                    .select(contacts::add);
            });

            for (int index = 0; index < contacts.size(); ++index) {
                Contact contact = contacts.get(index);
                System.out.println(
                    index
                    + ": Name: " + contact.firstName + " " + contact.lastName
                    + ", Birthday: " + contact.birthday
                );
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

Example1 を実行すると以下がコンソールに表示されます。

```log:標準出力
    ...
    ...
    ...
0: Name: First Example, Birthday: 1991-01-01
1: Name: Second Example, Birthday: 1992-02-02
2: Name: Third Example, Birthday: 1993-03-03
3: Name: Fourth Example, Birthday: 1994-04-04
```

<div style="text-align:center; margin-top:20px"><i>&copy; 2016 Masato Kokubo</i></div>
