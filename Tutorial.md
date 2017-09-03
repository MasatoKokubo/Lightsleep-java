Lightsleep / Tutorial
===========

Let's create a simple program that gets rows from the table and output to the console.

#### 1. Preparing the table

Create the Contact table to any database in MySQL, Oracle, PostgreSQL, SQLite or SQL Server, and then insert the sample data.

Create a table using one of the following SQL.

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

Execute the following SQL to insert the data into the table.

```sql:sample.sql
DELETE FROM Contact;
INSERT INTO Contact VALUES (1, 'First' , 'Example', DATE'1991-01-01');
INSERT INTO Contact VALUES (2, 'Second', 'Example', DATE'1992-02-02');
INSERT INTO Contact VALUES (3, 'Third' , 'Example', DATE'1993-03-03');
INSERT INTO Contact VALUES (4, 'Fourth', 'Example', DATE'1994-04-04');
````

#### 2. Creating the entity class

Create the entity class to hold rows obtained from the Contact table.

```java:Contact.java
package org.lightsleep.tutorial.entity;

import java.sql.Date;

import org.lightsleep.entity.*;

/**
 * Contact entity
 */
public class Contact {
    /** Contact ID */
    @Key
    public Integer id;

    /** First Name */
    public String firstName;

    /** Last Name */
    public String lastName;

    /** Birthday */
    public Date birthday;
}
```

#### 3. Preparation of properties file

Create ```lightsleep.properties``` file below.
Change to match the database environment to use the value of url, user and password.

```properties:lightsleep.properties
# for DB2
Logger             = Std$Out$Info
Database           = DB2
ConnectionSupplier = Jdbc
url                = jdbc:db2://<DB Server>:50000/<Database>
user               = <User Name>
password           = <Password>
```

```properties:lightsleep.properties
# for MySQL
Logger             = Std$Out$Info
Database           = MySQL
ConnectionSupplier = Jdbc
url                = jdbc:mysql://<DB Server>/<Database>
user               = <User Name>
password           = <Password>
```

```properties:lightsleep.properties
# for Oracle
Logger             = Std$Out$Info
Database           = Oracle
ConnectionSupplier = Jdbc
url                = jdbc:oracle:thin:@<DB Server>:1521:<SID>
user               = <User Name>
password           = <Password>
```

```properties:lightsleep.properties
# for PostgreSQL
Logger             = Std$Out$Info
Database           = PostgreSQL
ConnectionSupplier = Jdbc
url                = jdbc:postgresql://<DB Server>/<Database>
user               = <User Name>
password           = <Password>
```

```properties:lightsleep.properties
# for SQLite
Logger             = Std$Out$Info
Database           = SQLite
ConnectionSupplier = Jdbc
url                = jdbc:sqlite:<Installed Directory>/<Database>
```

```properties:lightsleep.properties
# for SQL Server
Logger             = Std$Out$Info
Database           = SQLServer
ConnectionSupplier = Jdbc
url                = jdbc:sqlserver://<DB Server>;Database=<Database>
user               = <User Name>
password           = <Password>
```

#### 4. Getting data
Create a program to retrieve all the rows from the table.

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

When you run the Example1 following is displayed on the console.

```log:stdout
    ...
    ...
    ...
0: Name: First Example, Birthday: 1991-01-01
1: Name: Second Example, Birthday: 1992-02-02
2: Name: Third Example, Birthday: 1993-03-03
3: Name: Fourth Example, Birthday: 1994-04-04
```

<div style="text-align:center; margin-top:20px"><i>&copy; 2016 Masato Kokubo</i></div>
