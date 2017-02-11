Lightsleep / Manual
===========

### 1. Entity Class
Entity class is a class for storing the retrieved data in SELECT SQL, create it for each database table.

#### 1-1. Annotations to be used in the entity classes
##### 1-1-1. Table Annotation
Specifies the table name associated with the class.
If the table name is the same as the class name, you do not need to specify this annotation.

```java:Java
import org.lightsleep.entity.*;

@Table("Contact")
public class Contact1 extends Contact {
   ...
}
```

If you specify `@Table("super")`, the class name of the superclass is the table name.

```java:Java
import org.lightsleep.entity.*;

@Table("super")
public class Contact1 extends Contact {
   ...
}
```

##### 1-1-2. Key Annotation
Indicates that the column associated with the field is part of the primary key.

```java:Java
@Key
public String id;
```

##### 1-1-3. Column Annotation
Indicates the name of column associated with the field.
If the column name is the same as the field name, you do not need to specify it.

```java:Java
    @Column("family_name")
    public String familyName;
```

##### 1-1-4. ColumnType Annotation
Indicates the type of column associated with the field.
If the field type and column type are the same type, you do not need to specify it.
Specify if field type (e.g. date type) and column type (e.g. numerical type) are different.

```java:Java
    @Column("Long")
    public Date birhtday;
```

##### 1-1-5. NonColumn Annotation
Indicates that the field not related to any column.

```java:Java
    @NonColumn
    public List<Phone> phones = new ArrayList<>();
```

##### 1-1-6. NonSelect Annotation
Indicates that the column related the field are not used in the SELECT SQL.

```java:Java
    @NonSelect
    public String givenName;
```

##### 1-1-7. NonInsert Annotation
Indicates that the column related the field are not used in the INSERT SQL.

```java:Java
    @Select("CONCAT({givenName}, ' ', {familyName})") // MySQL, Oracle
    @NonInsert @NonUpdate
    public String fullName;
```

##### 1-1-8. NonUpdate Annotation
Indicates that the column related the field are not used in the UPDATE SQL.

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @NonUpdate
    public Timestamp created;
```

##### 1-1-9. Select Annotation
Indicates a column expression instead of the column name of the SELECT SQL.

```java:Java
    @Select("CONCAT({givenName}, ' ', {familyName})") // MySQL, Oracle
    @NonInsert @NonUpdate
    public String fullName;
```

##### 1-1-10. Insert Annotation
Indicates an expression as a value of the INSERT SQL.
If this annotation is specified, the value of the field is not used.

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @NonUpdate
    public Timestamp created;
```

##### 1-1-11. Update Annotation
Indicates an expression as a value of the UPDATE SQL.
If this annotation is specified, the value of the field is not used.

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @Update("CURRENT_TIMESTAMP")
    public Timestamp modified;
```

##### 1-1-12. XxxxxProperty Annotations

ColumnProperty, ColumnTypeProperty, NonColumnProperty, NonSelectProperty, NonInsertProperty, NonUpdateProperty, SelectProperty, InsertProperty and UpdateProperty are the same as the Column, NonColumn, NonSelect, NonInsert, NonUpdate, Select, Insert and Update annotations. They are specified for classes, not fields.

These annotations are used when relating to fields defined in the superclass.

```java:Java
import org.lightsleep.entity.*;

@Table("super")
@ColumnProperty(property="familyName", column="family_name")
public class Contact1 extends Contact {
```

### 1-2. Interfaces that Entity Class to implement
#### 1-2-1. PreInsert Interface
If an entity class implements this interface, `insert` method of Sql class calls `preInsert` method of the entity before INSERT SQL execution.
In `preInsert` method, do the implementation of the numbering of the primary key or etc.

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

#### 1-2-2. Composite Interface
If an entity class implements this interface, `select`, `insert`, `update` or `delete` method of `Sql` class calls `postSelect`, `postInsert`, `postUpdate` or `postDelete` method of the entity class after the execution of each execute SQL.
However if `update` or `delete` method dose not have entity parameter, dose not call.
If an entity is enclose another entity, by implementing this interface, You can perform SQL processing to the enclosed entity in conjunction the entity which encloses.

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
                .where("{contactId}={}", id)
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
            .where("{contactId}={}", id)
            .delete(connection);
        return count;
    }
}
```

### 2. Transaction
Execution of `Transaction.execute` method is equivalent to the execution of a transaction.
Define contents of the transaction by the argument `transaction` as a lambda expression.
The lambda expression is equivalent to the contents of `Transaction.executeBody` method and the argument of this method is a `Connection`.

```java:Java
import org.lightsleep.*;

// A definition example of transaction
Transaction.execute(connection -> {
    // Start of the transaction
    new Sql<>(Contact.class)
        .update(connection, contact);
    ...
    // End of the transaction
});
```

If an exception is thrown during the transaction, `Transaction.rollback` method is called.
Otherwise, `Transaction.commit` method is called.

### 3. Connection Supplier
Getting of database connection (`java.sql.Connection`) is done in the `Transaction.execute` method.
Lightsleep has following classes to supply connections.

1. org.lightsleep.connection.C3p0
1. org.lightsleep.connection.Dbcp
1. org.lightsleep.connection.HikariCP
1. org.lightsleep.connection.TomcatCP
1. org.lightsleep.connection.Jdbc
1. org.lightsleep.connection.Jndi

`C3p0`, `Dbcp 2`, `HikariCP` and `TomcatCP` class gets database connectiona using the corresponding connection pool library.  
`JdbcConnection` class gets database connections using the `java.sql.DriverManager.getConnection` method.  
`JndiConnection` class gets database connections from the data source (`javax.sql.DataSource`) that was obtained using JNDI (Java Naming and Directory Interface).  
Define the connection supplier class and information needed to connect in the **lightsleep.properties** file.

```properties:lightsleep.properties
# lightsleep.properties / Example for C3p0
ConnectionSupplier = C3p0
url                = jdbc:mysql://mysql57/test
user               = test
password           = _test_
```

```properties:c3p0.properties
# c3p0.properties
c3p0.initialPoolSize = 20
c3p0.minPoolSize     = 10
c3p0.maxPoolSize     = 30
```

```properties:lightsleep.properties
# lightsleep.properties / Example for Dbcp
ConnectionSupplier = Dbcp
url                = jdbc:oracle:thin:@oracle121:1521:test
username           = test
password           = _test_
initialSize        = 20
maxTotal           = 30
```

```properties:lightsleep.properties
# lightsleep.properties / Example for HikariCP
ConnectionSupplier = HikariCP
jdbcUrl            = jdbc:postgresql://postgres96/test
username           = test
password           = _test_
minimumIdle        = 10
maximumPoolSize    = 30
```

```properties:lightsleep.properties
# lightsleep.properties / Example for TomcatCP
ConnectionSupplier = TomcatCP
url                = jdbc:sqlserver://sqlserver13;database=test
username           = test
password           = _test_
initialSize        = 20
maxActive          = 30
```

```properties:lightsleep.properties
# lightsleep.properties / Example for Jdbc
ConnectionSupplier = Jdbc
url                = jdbc:sqlite:C:/sqlite/test
user               = test
password           = _test_
```

```properties:lightsleep.properties
# lightsleep.properties / Example for Jndi
ConnectionSupplier = Jndi
dataSource         = jdbc/Sample
```

### 4. Execution of SQL
Use the various methods of `Sql` class to execute SQLs and define it in the lambda expression argument of `Transaction.execute` method.

#### 4-1. SELECT
#### 4-1-1. SELECT / 1 row / Expression Condition

```java:Java
Transaction.execute(connection -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class)
        .where("{id}={}", 1)
        .select(connection);
});
```

```sql:SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
```

#### 4-1-2. SELECT / 1 row / Entity Condition

```java:Java
Contact contact = new Contact();
contact.id = 1;
Transaction.execute(connection -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class)
        .where(contact)
        .select(connection);
});
```

```sql:SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
```

#### 4-1-3. SELECT / Multiple rows / Expression Condition

```java:Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .select(connection, contacts::add)
);
```

```sql:SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple'
```

#### 4-1-4. SELECT / Subquery Condition

```java:Java
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

```sql:SQL
SELECT C.id AS C_id, C.familyName AS C_familyName, C.givenName AS C_givenName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime FROM Contact C WHERE EXISTS (SELECT * FROM Phone P WHERE P.contactId=C.id)
```

#### 4-1-5. SELECT / Expression Condition / AND

```java:Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .and  ("{givenName}={}", "Akane")
        .select(connection, contacts::add)
);
```

```sql:SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE (familyName='Apple' AND givenName='Akane')
```

#### 4-1-6. SELECT / Expression Condition / OR

```java:Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .or   ("{familyName}={}", "Orange")
        .select(connection, contacts::add)
);
```

```sql:SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE (familyName='Apple' OR familyName='Orange')
```

#### 4-1-7. SELECT / Expression Condition / (A AND B) OR (C AND D)

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
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE ((familyName='Apple' AND givenName='Akane') OR (familyName='Orange' AND givenName='Setoka'))
```

#### 4-1-8. SELECT / Select Columns

```java:Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .columns("familyName", "givenName")
        .select(connection, contacts::add)
);
```

```sql:SQL
SELECT familyName, givenName FROM Contact WHERE familyName='Apple'
```

#### 4-1-9. SELECT / GROUP BY, HAVING

```java:Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class, "C")
        .columns("familyName")
        .groupBy("{familyName}")
        .having("COUNT({familyName})>=2")
        .select(connection, contacts::add)
);
```

```sql:SQL
SELECT MIN(C.familyName) AS C_familyName FROM Contact C GROUP BY C.familyName HAVING COUNT(C.familyName)>=2
```

#### 4-1-10. SELECT / ORDER BY, OFFSET, LIMIT

```java:Java
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

```sql:SQL
-- MySQL, PostgreSQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact ORDER BY familyName ASC, givenName ASC, id ASC LIMIT 5 OFFSET 10
```

#### 4-1-11. SELECT / FOR UPDATE

```java:Java
Transaction.execute(connection -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class)
        .where("{id}={}", 1)
        .forUpdate()
        .select(connection);
});
```

```sql:SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1 FOR UPDATE
```

#### 4-1-12. SELECT / INNER JOIN

```java:Java
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
Transaction.execute(connection ->
    new Sql<>(Contact.class, "C")
        .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
        .where("{C.id}={}", 1)
        .<Phone>select(connection, contacts::add, phones::add)
);
```

```sql:SQL
SELECT C.id AS C_id, C.familyName AS C_familyName, C.givenName AS C_givenName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C INNER JOIN Phone P ON P.contactId=C.id WHERE C.id=1
```

#### 4-1-13. SELECT / LEFT OUTER JOIN
```java:Java
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
Transaction.execute(connection ->
	new Sql<>(Contact.class, "C")
	    .leftJoin(Phone.class, "P", "{P.contactId}={C.id}")
	    .where("{C.familyName}={}", "Apple")
	    .<Phone>select(connection, contacts::add, phones::add)
);
```

```sql:SQL
SELECT C.id AS C_id, C.familyName AS C_familyName, C.givenName AS C_givenName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C LEFT OUTER JOIN Phone P ON P.contactId=C.id WHERE C.familyName='Apple'
```

#### 4-1-14. SELECT / RIGHT OUTER JOIN
```java:Java
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
Transaction.execute(connection ->
    new Sql<>(Contact.class, "C")
        .rightJoin(Phone.class, "P", "{P.contactId}={C.id}")
        .where("{P.label}={}", "Main")
        .<Phone>select(connection, contacts::add, phones::add)
);
```

```sql:SQL
SELECT C.id AS C_id, C.familyName AS C_familyName, C.givenName AS C_givenName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C RIGHT OUTER JOIN Phone P ON P.contactId=C.id WHERE P.label='Main'
```

#### 4-2. INSERT
#### 4-2-1. INSERT / 1 row

```java:Java
Contact contact = new Contact();
contact.id = 1;
contact.familyName = "Apple";
contact.givenName = "Akane";
Calendar calendar = Calendar.getInstance();
calendar.set(2001, 1-1, 1, 0, 0, 0);
contact.birthday = new Date(calendar.getTimeInMillis())
Transaction.execute(connection -> {
    new Sql<>(Contact.class).insert(connection, contact));
```

```sql:SQL
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (1, 'Apple', 'Akane', DATE'2001-01-01', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

#### 4-2-2. INSERT / Multiple rows
```java:Java
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

```sql:SQL
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (2, 'Apple', 'Yukari', DATE'2001-01-02', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (3, 'Apple', 'Azusa', DATE'2001-01-03', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

#### 4-3. UPDATE
#### 4-3-1. UPDATE / 1 row

```java:Java
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

```sql:SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
UPDATE Contact SET familyName='Apple', givenName='Akiyo', birthday=DATE'2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
```

#### 4-3-2. UPDATE / Multiple rows

```java:Java
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

```sql:SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple'
UPDATE Contact SET familyName='Apfel', givenName='Akiyo', birthday=DATE'2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
UPDATE Contact SET familyName='Apfel', givenName='Yukari', birthday=DATE'2001-01-02', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=2
UPDATE Contact SET familyName='Apfel', givenName='Azusa', birthday=DATE'2001-01-03', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=3
```

#### 4-3-3. UPDATE / Specified Condition, Select Columns

```java:Java
Contact contact = new Contact();
contact.familyName = "Pomme";
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apfel")
        .columns("familyName")
        .update(connection, contact)
);
```

```sql:SQL
UPDATE Contact SET familyName='Pomme' WHERE familyName='Apfel'
```

#### 4-3-4. UPDATE / All rows

```java:Java
Contact contact = new Contact();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where(Condition.ALL)
        .columns("birthday")
        .update(connection, contact)
);
```

```sql:SQL
UPDATE Contact SET birthday=NULL
```

#### 4-4. DELETE
#### 4-4-1. DELETE / 1 row
```java:Java
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{id}={}", 1)
        .select(connection)
        .ifPresent(contact ->
            new Sql<>(Contact.class).delete(connection, contact))
);
```

```sql:SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
DELETE FROM Contact WHERE id=1
```

#### 4-4-2. DELETE / Multiple rows
```java:Java
Transaction.execute(connection -> {
    List<Contact> contacts = new ArrayList<>();
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Pomme")
        .select(connection, contacts::add);
    new Sql<>(Contact.class).delete(connection, contacts);
});
```

```sql:SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Pomme'
DELETE FROM Contact WHERE id=2
DELETE FROM Contact WHERE id=3
```

#### 4-4-3. DELETE / Specified Condition
```java:Java
Transaction.execute(connection -> {
    List<Contact> contacts = new ArrayList<>();
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Pomme")
        .select(connection, contacts::add);
    new Sql<>(Contact.class).delete(connection, contacts);
});
```

```sql:SQL
DELETE FROM Contact WHERE familyName='Orange'
```

#### 4-4-4. DELETE / All rows
```java:Java
Transaction.execute(connection ->
    new Sql<>(Phone.class).where(Condition.ALL).delete(connection));
```

```sql:SQL
DELETE FROM Phone
```

### 5. Expression conversion processing

When generating SQL, evaluates the following character string as an expression and perform conversion processing.

- The value of `@Select`, `@Insert` and `@Update`

- The value of `expression` of `@SelectProperty`, `@InsertProperty` and `@UpdateProperty` annotations.

- Arguments for the following methods of the `Sql` class
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

- Arguments for the following methods of the `Condition` interface
    - `of(String content)`
    - `of(String content, Object... arguments)`
    - `Condition of(String content, Sql<E> outerSql, Sql<SE> subSql)`
    - `and(String content, Object... arguments)`
    - `and(String content, Sql<E> outerSql, Sql<SE> subSql)`
    - `or(String content, Object... arguments)`
    - `or(String content, Sql<E> outerSql, Sql<SE> subSql)`

- Arguments of the following constructor of the `Expression` class
    - `Expression(String content, Object... arguments)`

Conversion of expressions has the followings.

|Format|Conversion Content|
|:--|:--|
|`{}`|An element of `arguments` in appearance|
|`{xxx}`|The column name associated with property `xxx`|
|`{A.xxx}`|`"A."` + The column name associated with property `xxx` (`A` is a table alias)|
|`{A_xxx}`|The column alias associated with table alias `A` and `xxx` property|
|`{#xxx}`|The value of property `xxx` of an entity set on the `Sql` object (or an entity argument of `Sql#insert` or `Sql#update` method)|

### 6. Logging
Lightsleep is compatible with the following logging Libraries.

- java.util.logging.Logger
- Apache Log4j
- Apache Log4j2
- SLF4J

You can also log to stdout and stderr.

Specify the logging library in the ```lightsleep.properties```.

```properties:lightsleep.properties
# Using java.util.logging.Logger class
Logger = Jdk

# Using Apache Log4j
Logger = Log4j

# Using Apache Log4j2
Logger = Log4j2

# Using SLF4J
Logger = SLF4J

# Output the FATAL level to stdout
Logger = Std$Out$Fatal

# Output the ERROR and FATAL level to stdout
Logger = Std$Out$Error

# Output the WARN, ERROR and FATAL level to stdout
Logger = Std$Out$Warn

# Output the INFO, WARN, ERROR and FATAL level to stdout
Logger = Std$Out$Info

# Output the DEBUG, INFO, WARN, ERROR and FATAL level to stdout
Logger = Std$Out$Debug

# Output the TRACE, DEBUG, INFO, WARN, ERROR and FATAL level to stdout
Logger = Std$Out$Trace

# Output the FATAL level to stderr
Logger = Std$Err$Fatal

# Output the ERROR and FATAL level to stderr
Logger = Std$Err$Error

# Output the WARN, ERROR and FATAL level to stderr
Logger = Std$Err$Warn

# Output the INFO, WARN, ERROR and FATAL level to stderr
Logger = Std$Err$Info

# Output the DEBUG, INFO, WARN, ERROR and FATAL level to stderr
Logger = Std$Err$Debug

# Output the TRACE, DEBUG, INFO, WARN, ERROR and FATAL level to stderr
Logger = Std$Err$Trace
```
