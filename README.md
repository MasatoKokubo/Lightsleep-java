Lightsleep 2
===========

[[Japanese]](README_ja.md)

Lightsleep is a lightweight Object-Relational (O/R) mapping library and is available in Java 8 or later. It does not work in Java 7 or earlier.
It is not compatible with the Java Persistence API (JPA).

### Features

- Has APIs using features added in Java 8 (functional interface and Optional class).
- It is easy to understand intuitively because method names resemble reserved words in SQL.
- It is easy to use for batch programs because there is no library dependent on Java runtime and JDBC driver.
- No mapping definition file such as XML file is necessary.
- Learning is relatively easy because it is not a large library.

### Supported DBMS

- DB2
- MySQL
- Oracle Database
- PostgreSQL
- SQLite
- Microsoft SQL Server
- DBMSs that conforms to the standard SQL

### Description example of dependency in build.gradle

```gradle:build.gradle
repositories {
    jcenter()
}

dependencies {
    compile 'org.lightsleep:lightsleep:2.+' // If you use the latest version

    compile 'org.lightsleep:lightsleep:1.+' // If you use the previous version.
}
```
### Definition example of entity class used in Lightsleep

```java:Contact.java
// Java Example
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
// Groovy Example
package org.lightsleep.example.groovy.entity
import java.sql.Date
import java.sql.Timestamp
import org.lightsleep.entity.*

class Contact {
	@Key
	int    id
	String lastName
	String firstName
	Date   birthday

	@Insert('0') @Update('{updateCount}+1')
	int updateCount

	@Insert('CURRENT_TIMESTAMP') @NonUpdate
	Timestamp createdTime

	@Insert('CURRENT_TIMESTAMP') @Update('CURRENT_TIMESTAMP')
	Timestamp updatedTime
}
```

### Examples of using Lightsleep

```java:Java
// Java Example using Lightsleep 2.x.x
List<Contact> contacts = new ArrayList<>();
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
// Groovy Example using Lightsleep 2.x.x
List<Contact> contacts = []
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
// Java Example using Lightsleep 1.x.x
List<Contact> contacts = new ArrayList<>();
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
// Groovy Example using Lightsleep 1.x.x
List<Contact> contacts = []
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
-- Generated SQLs
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple' OR lastName='Orange' ORDER BY lastName ASC, firstName ASC
```

### Changes since version 1.9.2

##### Changes
- Added `property` element and changed the specification of `value` element to following annotations.
    - `KeyProperty`
    - `NonColumnProperty`
    - `NonInsertProperty`
    - `NonSelectProperty`
    - `NonUpdateProperty`

##### Added methods
- `Sql` class
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

##### Deprecated methods
- `Sql` class
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

##### Added exception class
- `MissingPropertyException`

### License

The MIT License (MIT)

*&copy; 2016 Masato Kokubo*

### Documents

[Tutorial](Tutorial.md)

[Manual](Manual.md)

[Manual (version 1.9.2)](Manual-v1.md)

[API Specification](http://masatokokubo.github.io/Lightsleep/javadoc/index.html)

[API Specification (version 1.9.2)](http://masatokokubo.github.io/Lightsleep/javadoc-v1/index.html)

<a href="http://lightsleep.hatenablog.com/" target="_blank">BLOG @Hatena</a>
