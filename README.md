Lightsleep
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
    compile 'org.lightsleep:lightsleep:2.1.0'
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

```sql
-- Generated SQLs
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple' OR lastName='Orange' ORDER BY lastName ASC, firstName ASC
```

### License

The MIT License (MIT)

*&copy; 2016 Masato Kokubo*

### Documents

[Release Notes](ReleaseNotes.md)

[Tutorial](Tutorial.md)

[Manual](Manual.md)

[API Specification](http://masatokokubo.github.io/Lightsleep/javadoc/index.html)

<a href="http://lightsleep.hatenablog.com/" target="_blank">BLOG @Hatena</a>
