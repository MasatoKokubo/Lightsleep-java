Lightsleep
===========

Lightsleep is a lightweight Object-Relational (O/R) mapping library and is available in Java 8. It does not work in Java 7 or earlier.
It is not compatible with the Java Persistence API (JPA).

#### Features

- Has APIs using features added in Java 8 (functional interface and Optional class).
- It is easy to understand intuitively because method names resemble reserved words in SQL.
- It is easy to use for batch programs because there is no library dependent on Java runtime and JDBC driver.
- No mapping definition file such as XML file is necessary.
- Learning is relatively easy because it is not a large library.

#### Supported DBMS

- DB2
- MySQL
- Oracle Database
- PostgreSQL
- SQLite
- Microsoft SQL Server
- DBMSs that conforms to the standard SQL

#### Description example of dependency in build.gradle

```gradle:build.gradle
repositories {
    jcenter()
}

dependencies {
    compile 'org.lightsleep:lightsleep:1.+'
}
```
#### Definition example of entity class used in Lightsleep

```java:Contact.java
// Java
package org.lightsleep.example.java.entity;
import java.sql.Date;
import java.sql.Timestamp;
import org.lightsleep.entity.*;

public class Contact {
	@Key
	public int    id;
	public String familyName;
	public String givenName;
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
// Groovy
package org.lightsleep.example.groovy.entity
import java.sql.Date
import java.sql.Timestamp
import org.lightsleep.entity.*

class Contact {
	@Key
	int    id
	String familyName
	String givenName
	Date   birthday

	@Insert('0') @Update('{updateCount}+1')
	int updateCount

	@Insert('CURRENT_TIMESTAMP') @NonUpdate
	Timestamp createdTime

	@Insert('CURRENT_TIMESTAMP') @Update('CURRENT_TIMESTAMP')
	Timestamp updatedTime
}
```

#### Examples of using Lightsleep

```java:Java
// Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .or   ("{familyName}={}", "Orange")
        .orderBy("{familyName}")
        .orderBy("{givenName}")
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
        .orderBy('{familyName}')
        .orderBy('{givenName}')
        .select(it, {contacts << it})
}
```

```sql
-- Executed SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple' OR familyName='Orange' ORDER BY familyName ASC, givenName ASC
```

#### License

The MIT License (MIT)

*&copy; 2016 Masato Kokubo*

[Tutorial](Tutorial.md)

[Manual](Manual.md)

[API Specification](http://masatokokubo.github.io/Lightsleep/javadoc/index.html)

<a href="http://lightsleep.hatenablog.com/" target="_blank">BLOG @Hatena</a>

[Japanese](README_ja.md)
