Lightsleep / Tutorial
===========

Try to create a simple program that gets rows from a table and output to the console.

#### 1. Preparing the table

Create the Contact table to any database in MySQL, Oracle, PostgreSQL, SQLite or SQL Server, and then insert the sample data.

Create a table by doing one of the following SQL.

```sql:ddl_mysql.sql
-- for MySQL
CREATE TABLE Contact (
    id          INT         NOT NULL,
    familyName  VARCHAR(20)     NULL,
    givenName   VARCHAR(20)     NULL,
    birthday    DATE            NULL,

    PRIMARY KEY(id)
);
```

```sql:ddl_oracle.sql
-- for Oracle
CREATE TABLE Contact (
    id          NUMBER  ( 9)      NOT NULL,
    familyName  VARCHAR2(20 CHAR) NOT NULL,
    givenName   VARCHAR2(20 CHAR) NOT NULL,
    birthday    DATE                  NULL,

    PRIMARY KEY(id)
);
```

```sql:ddl_postgresql.sql
-- for PostgreSQL
CREATE TABLE Contact (
    id         INT         NOT NULL,
    familyName VARCHAR(20)     NULL,
    givenName  VARCHAR(20)     NULL,
    birthday   DATE            NULL,

    PRIMARY KEY(id)
);
```

```sql:ddl_sqlite.sql
-- for SQLite
CREATE TABLE Contact (
    id         INTEGER         NOT NULL,
    familyName VARCHAR(20)     NULL,
    givenName  VARCHAR(20)     NULL,
    birthday   TEXT            NULL,

    PRIMARY KEY(id)
);
```

```sql:ddl_sqlserver.sql
-- for SQLServer
CREATE TABLE Contact (
    id         INT         NOT NULL,
    familyName VARCHAR(20)     NULL,
    givenName  VARCHAR(20)     NULL,
    birthday   DATE            NULL,

    PRIMARY KEY(id)
);
```

Execute the following SQL to insert the data into the table.

```sql:sample.sql
DELETE FROM Contact;
INSERT INTO Contact VALUES (1, 'First' , 'Sample', DATE'1991-01-01');
INSERT INTO Contact VALUES (2, 'Second', 'Sample', DATE'1992-02-02');
INSERT INTO Contact VALUES (3, 'Third' , 'Sample', DATE'1993-03-03');
INSERT INTO Contact VALUES (4, 'Fourth', 'Sample', DATE'1994-04-04');
````

#### 2. Creating the entity class

Create the entity class to hold rows obtained from the Contact table.

```java:Contact.java
package org.lightsleep.tutorial.entity;

import java.sql.Date;

import org.lightsleep.entity.*;

/**
    Contact
*/
public class Contact {
    /** Contact ID */
    @Key
    public Integer id;

    /** First Name */
    public String givenName;

    /** Last Name */
    public String familyName;

    /** Birthday */
    public Date birthday;
}
```

#### 3. Preparation of properties file

Create ```lightsleep.properties``` below.
Change to match the database environment to use the value of JdbcConnection.url, JdbcConnection.user and JdbcConnection.password.

```properties:lightsleep.properties
# for MySQL
Logger             = Std$Out$Info
Database           = MySQL
ConnectionSupplier = Jdbc
url                = jdbc:mysql://mysql57/test
user               = test
password           = _test_
```

```properties:lightsleep.properties
# for Oracle
Logger             = Std$Out$Info
Database           = Oracle
ConnectionSupplier = Jdbc
url                = jdbc:oracle:thin:@oracle121:1521:test
user               = test
password           = _test_
```

```properties:lightsleep.properties
# for PostgreSQL
Logger             = Std$Out$Info
Database           = PostgreSQL
ConnectionSupplier = Jdbc
url                = jdbc:postgresql://postgres96/test
user               = test
password           = _test_
```

```properties:lightsleep.properties
# for SQLite
Logger             = Std$Out$Info
Database           = SQLite
ConnectionSupplier = Jdbc
url                = jdbc:sqlite:C:/sqlite/test
```

```properties:lightsleep.properties
# for SQL Server
Logger             = Std$Out$Info
Database           = SQLServer
ConnectionSupplier = Jdbc
url                = jdbc:sqlserver://sqlserver13;Database=test
user               = test
password           = _test_
```

#### 4. Getting of data
Create a program to retrieve all the rows from the table.

```java:Sample1.java
package org.lightsleep.tutorial;

import java.util.ArrayList;
import java.util.List;

import org.lightsleep.Sql;
import org.lightsleep.Transaction;
import org.lightsleep.tutorial.entity.Contact;

public class Sample1 {
    public static void main(String[] args) {
        try {
            List<Contact> contacts = new ArrayList<>();
            Transaction.execute(connection -> {
                new Sql<>(Contact.class)
                    .select(connection, contacts::add);
            });

            for (int index = 0; index < contacts.size(); ++index) {
                Contact contact = contacts.get(index);
                System.out.println(
                    index
                    + ": Name: " + contact.givenName + " " + contact.familyName
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

When you run the Sample1 following is displayed on the console.

```log:stdout
    ...
    ...
    ...
0: Name: First Sample, Birthday: 1991-01-01
1: Name: Second Sample, Birthday: 1992-02-02
2: Name: Third Sample, Birthday: 1993-03-03
3: Name: Fourth Sample, Birthday: 1994-04-04
```

<div style="text-align:center; margin-top:20px"><i>&copy; 2016 Masato Kokubo</i></div>
