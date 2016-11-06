Lightsleep / Manual
===========

### 1. Entity Class
Entity class is a class for storing the retrieved data in SELECT SQL, create it for each database table.

#### 1-1. Annotations to be used in the entity classes
##### 1-1-1. Table Annotation
Specifies the table name associated with the class.
If the table name is the same as the class name, you do not need to specify this annotation.

```java:Java
@Table("Contact")
public class Contact1 extends Contact {
   ...
}
```

If you specify ```@Table("super")```, the class name of the superclass is the table name.

```java:Java
@Table("super")
public class Contact1 extends Contact {
   ...
}
```

##### 1-1-2. Key Annotation
Specifies that the column associated with the field is part of the primary key.

```java:Java
@Key
public String id;
```

##### 1-1-3. Column Annotation
Specifies the column name associated with the field.
If the column name is the same as the field name, you do not need to specify this annotation.

```java:Java
    @Column("family_name")
    public String familyName;
```

##### 1-1-4. NonColumn Annotation
Specifies that the field not related to any column.

```java:Java
    @NonColumn
    public List<Phone> phones = new ArrayList<>();
```

##### 1-1-5. NonSelect Annotation
Specifies that the column related the field are not used in the SELECT SQL.

```java:Java
    @NonSelect
    public String givenName;
```

##### 1-1-6. NonInsert Annotation
Specifies that the column related the field are not used in the INSERT SQL.

```java:Java
    @Select("CONCAT({givenName}, ' ', {familyName})") // MySQL, Oracle
    @NonInsert @NonUpdate
    public String fullName;
```

##### 1-1-7. NonUpdate Annotation
Specifies that the column related the field are not used in the UPDATE SQL.

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @NonUpdate
    public Timestamp created;
```

##### 1-1-8. Select Annotation
Specifies a column expression instead of the column name of the SELECT SQL.

```java:Java
    @Select("CONCAT({givenName}, ' ', {familyName})") // MySQL, Oracle
    @NonInsert @NonUpdate
    public String fullName;
```

##### 1-1-9. Insert Annotation
Specifies an expression as a value of the INSERT SQL.
If this annotation is specified, the value of the field is not used.

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @NonUpdate
    public Timestamp created;
```

##### 1-1-10. Update Annotation
Specifies an expression as a value of the UPDATE SQL.
If this annotation is specified, the value of the field is not used.

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @Update("CURRENT_TIMESTAMP")
    public Timestamp modified;
```

### 1-2. Interfaces that Entity Class to implement
#### 1-2-1. PreInsert Interface
If an entity class implements this interface, ```insert``` method of Sql class calls ```preInsert``` method of the entity before INSERT SQL execution.
In ```preInsert``` method, do the implementation of the numbering of the primary key or etc.

```java:Java
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
If an entity class implements this interface, ```select```, ```insert```, ```update``` or ```delete``` method of ```Sql``` class calls ```postSelect```, ```postInsert```, ```postUpdate``` or ```postDelete``` method of the entity class after the execution of each execute SQL.
However if ```update``` or ```delete``` method dose not have entity parameter, dose not call.
If an entity is enclose another entity, by implementing this interface, You can perform SQL processing to the enclosed entity in conjunction the entity which encloses.

```java:Java
@Table("super")
public class ContactComposite extends Contact implements Composite {
    @NonColumn
    public final List<Phone> phones = new ArrayList<>();

    @Override
    public void postSelect(Connection connection) {
        if (id > 0)
            new Sql<>(Phone.class)
                .where("{contactId} = {}", id)
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
            .where("{contactId} = {}", id)
            .delete(connection);
        return count;
    }
}
```

### 2. Transaction
Execution of ```Transaction.execute``` method is equivalent to the execution of a transaction.
Define contents of the transaction by the argument ```transaction``` as a lambda expression.
The lambda expression is equivalent to the contents of ```Transaction.executeBody``` method and the argument of this method is a ```Connection```.

```java:Java
// A definition example of transaction
Transaction.execute(connection -> {
    // Start of the transaction
    new Sql<>(Contact.class)
        .update(connection, contact);
    ...
    // End of the transaction
});
```

If an exception is thrown during the transaction, ```Transaction.rollback``` method is called.
Otherwise, ```Transaction.commit``` method is called.

### 3. Connection Supplier
Getting of database connection (```java.sql.Connection```) is done in the ```Transaction.execute``` method.
Lightsleep has following classes to supply connections.

1. org.lightsleep.connection.C3p0
1. org.lightsleep.connection.Dbcp
1. org.lightsleep.connection.HikariCP
1. org.lightsleep.connection.TomcatCP
1. org.lightsleep.connection.Jdbc
1. org.lightsleep.connection.Jndi

```C3p0```, ```Dbcp 2```, ```HikariCP``` and ```TomcatCP``` class gets database connectiona using the corresponding connection pool library.  
```JdbcConnection``` class gets database connections using the ```java.sql.DriverManager.getConnection``` method.  
```JndiConnection``` class gets database connections from the data source (```javax.sql.DataSource```) that was obtained using JNDI (Java Naming and Directory Interface).  
Define the connection supplier class and information needed to connect in the **lightsleep.properties** file.

```properties:lightsleep.properties
# lightsleep.properties / Example for C3p0
ConnectionSupplier = C3p0
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
# lightsleep.properties / Example for Dbcp 2
ConnectionSupplier = Dbcp
url         = jdbc:oracle:thin:@Oracle121:1521:test
username    = test
password    = _test_
initialSize = 20
maxTotal    = 30
```

```properties:lightsleep.properties
# lightsleep.properties / Example for HikariCP
ConnectionSupplier = HikariCP
jdbcUrl         = jdbc:postgresql://Postgres95/test
username        = test
password        = _test_
minimumIdle     = 10
maximumPoolSize = 30
```

```properties:lightsleep.properties
# lightsleep.properties / Example for TomcatCP
ConnectionSupplier = TomcatCP
url         = jdbc:sqlserver://SQLServer13;database=test
username    = test
password    = _test_
initialSize = 20
maxActive   = 30
```

```properties:lightsleep.properties
# lightsleep.properties / Example for Jdbc
ConnectionSupplier = Jdbc
url      = jdbc:mysql://MySQL57/test
user     = test
password = _test_
```

```properties:lightsleep.properties
# lightsleep.properties / Example for Jndi
connectionSupplier = Jndi
dataSource = jdbc/Sample
```

### 4. Execution of SQL
Use the various methods of Sql class to execute SQLs and define it in the lambda expression argument of ```Transaction.execute``` method.

#### 4-1. SELECT
#### 4-1-1. SELECT / 1 row / Expression Condition

```java:Java
Optional<Contact> contactOpt = new Sql<>(Contact.class)
    .where("{id} = {}", id)
    .select(connection);

Contact contact = new Sql<>(Contact.class)
    .where("{id} = {}", id)
    .select(connection).orElse(null);
```

```sql:SQL
SELECT id, familyName, givenName, ... FROM Contact WHERE id = '...'
```

#### 4-1-2. SELECT / 1 row / Entity Condition

```java:Java
Contact contact = new Contact();
contact.id = id;
Optional<Contact> contactOpt = new Sql<>(Contact.class)
    .where(contact)
    .select(connection);
```

```sql:SQL
SELECT id, familyName, givenName, ... FROM Contact WHERE id = '...'
```

#### 4-1-3. SELECT / Multiple rows / Expression Condition

```java:Java
List<Contact> contact = new ArrayList<Contact>();
new Sql<>(Contact.class)
    .where("{familyName} = {}", familyName)
    .select(connection, contact::add);
```

```sql:SQL
SELECT id, familyName, givenName, ... FROM Contact WHERE familyName = '...'
```

#### 4-1-4. SELECT / Subquery Condition

```java:Java
List<Contact> contact = new ArrayList<Contact>();
new Sql<>(Contact.class, "PS")
    .where("EXISTS",
        new Sql<>(Phone.class, "PH")
            .where("{PH.contactId} = {PS.id}")
    )
    .select(connection, contact::add);
```

```sql:SQL
SELECT PS.id AS PS_id,
  PS.familyName AS PS_familyName,
  PS.givenName AS PS_givenName,
  ...
  FROM Contact PS
  WHERE EXISTS (SELECT * FROM Phone PH WHERE PH.contactId = PS.id)
```

#### 4-1-5. SELECT Expression Condition / AND

```java:Java
List<Contact> contact = new ArrayList<Contact>();
new Sql<>(Contact.class)
    .where("{familyName} = {}", familyName)
    .and  ("{givenName} = {}", givenName)
    .select(connection, contact::add);
```

```sql:SQL
SELECT id, familyName, givenName, ... FROM Contact
  WHERE (familyName = '...' AND givenName = '...')
```

#### 4-1-6. SELECT Expression Condition / OR

```java:Java
List<Contact> contact = new ArrayList<Contact>();
new Sql<>(Contact.class)
    .where("{familyName} = {}", familyName1)
    .or   ("{familyName} = {}", familyName2)
    .select(connection, contact::add);
```

```sql:SQL
SELECT id, familyName, givenName, ... FROM Contact
  WHERE (familyName = '...' OR familyName = '...')
```

#### 4-1-7. SELECT Expression Condition / (A AND B) OR (C AND D)

```java:Java
List<Contact> contact = new ArrayList<Contact>();
new Sql<>(Contact.class)
    .where(Condition
        .of ("{familyName} = {}", familyName1)
        .and("{givenName} = {}", givenName1)
    )
    .or(Condition
        .of ("{familyName} = {}", familyName2)
        .and("{givenName} = {}", givenName2)
    )
    .select(connection, contact::add);
```

```sql:SQL
SELECT id, familyName, givenName, ... FROM Contact
  WHERE ((familyName = '...' AND givenName = '...')
    OR (familyName = '...' AND givenName = '...'))
```

#### 4-1-8. SELECT / Select Columns

```java:Java
List<Contact> contact = new ArrayList<Contact>();
new Sql<>(Contact.class)
    .where("{familyName} = {}", familyName)
    .columns("familyName", "givenName")
    .select(connection, contact::add);
```

```sql:SQL
SELECT familyName, givenName FROM Contact WHERE familyName = '...'
```

#### 4-1-9. SELECT / GROUP BY, HAVING

```java:Java
List<Contact> contact = new ArrayList<Contact>();
new Sql<>(Contact.class)
    .columns("givenName")
    .groupBy("{givenName}")
    .having("COUNT({givenName}) = 2")
    .select(connection, contact::add);
```

```sql:SQL
SELECT MIN(givenName) FROM Contact GROUP BY givenName HAVING COUNT(givenName) = 2
```

#### 4-1-10. SELECT / OFFSET, LIMIT, ORDER BY

```java:Java
List<Contact> contact = new ArrayList<Contact>();
new Sql<>(Contact.class)
    .orderBy("{familyName}")
    .orderBy("{givenName}")
    .orderBy("{id}")
    .offset(100).limit(10)
    .select(connection, contact::add);
```

```sql:SQL
SELECT id, familyName, givenName, ... FROM Contact
  ORDER BY familyName ASC, givenName ASC, id ASC
  LIMIT 10 OFFSET 100
```

#### 4-1-11. SELECT / FOR UPDATE

```java:Java
Optional<Contact> contactOpt = new Sql<>(Contact.class)
    .where("{id} = {}", id)
    .forUpdate()
    .select(connection);
```

```sql:SQL
SELECT id, familyName, givenName, birthday, ..., updated FROM Contact
  WHERE id = '...' FOR UPDATE
```

#### 4-1-12. SELECT / INNER JOIN

```java:Java
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
new Sql<>(Contact.class, "PS")
    .innerJoin(Phone.class, "PH", "{PH.contactId} = {PS.id}")
    .where("{PS.id} = {}", id)
    .<Phone>select(connection, contacts::add, phones::add);
```

```sql:SQL
SELECT PS.id AS PS_id,
  PS.familyName AS PS_familyName,
  PS.givenName AS PS_givenName,
  ...,
  PH.contactId AS PH_contactId,
  PH.childIndex AS PH_childIndex,
  PH.content AS PH_content,
  ...
  FROM Contact PS
  INNER JOIN Phone PH ON PH.contactId = PS.id
  WHERE PS.id = '...'
```

#### 4-1-13. SELECT / LEFT OUTER JOIN

```java:Java
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
new Sql<>(Contact.class, "PS")
    .leftJoin(Phone.class, "PH", "{PH.contactId} = {PS.id}")
    .where("{PS.id} = {}", id)
    .<Phone>select(connection, contacts::add, phones::add);
```

```sql:SQL
SELECT PS.id AS PS_id,
  PS.familyName AS PS_familyName,
  PS.givenName AS PS_givenName,
  ...,
  PH.contactId AS PH_contactId,
  PH.childIndex AS PH_childIndex,
  PH.content AS PH_content,
  ...
  FROM Contact PS
  LEFT OUTER JOIN Phone PH ON PH.contactId = PS.id
  WHERE PS.id = '...'
```

#### 4-1-13. SELECT / RIGHT OUTER JOIN

```java:Java
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
new Sql<>(Contact.class, "PS")
    .rightJoin(Phone.class, "PH", "{PH.contactId} = {PS.id}")
    .where("{PS.id} = {}", id)
    .<Phone>select(connection, contacts::add, phones::add);
```

```sql:SQL
SELECT PS.id AS PS_id,
  PS.familyName AS PS_familyName,
  PS.givenName AS PS_givenName,
  ...,
  PH.contactId AS PH_contactId,
  PH.childIndex AS PH_childIndex,
  PH.content AS PH_content,
  ...
  FROM Contact PS
  RIGHT OUTER JOIN Phone PH ON PH.contactId = PS.id
  WHERE PS.id = '...'
```

#### 4-2. INSERT
#### 4-2-1. INSERT / 1 row

```java:Java
Contact contact = new Contact();
...
new Sql<>(Contact.class)
    .insert(connection, contact);
```

```sql:SQL
INSERT INTO Contact (id, familyName, givenName, ...) VALUES ('...', '...', '...', ...)
```

#### 4-2-2. INSERT / Multiple rows

```java:Java
List<Contact> contacts = new ArrayList<>();
...
new Sql<>(Contact.class)
    .insert(connection, contacts);
```

```sql:SQL
INSERT INTO Contact (id, familyName, givenName, ...) VALUES ('...', '...', '...', ...)
...
```

#### 4-3. UPDATE
#### 4-3-3. UPDATE / 1 row
```java:Java
Contact contact = new Contact();
...
new Sql<>(Contact.class)
    .update(connection, contact);
```
```sql:SQL
UPDATE Contact SET familyName='...', givenName='...', ... WHERE id = '...'
```

#### 4-3-3. UPDATE / Multiple rows
```java:Java
List<Contact> contacts = new ArrayList<>();
...
new Sql<>(Contact.class)
    .update(connection, contacts);
```
```sql:SQL
UPDATE Contact SET familyName='...', givenName='...', ... WHERE id = '...'
UPDATE Contact SET familyName='...', givenName='...', ... WHERE id = '...'
   ...
```

#### 4-3-3. UPDATE / Specified Condition
```java:Java
Contact contact = new Contact();
...
new Sql<>(Contact.class)
    .where("{familyName} = {}", familyName)
    .update(connection, contact);
```
```sql:SQL
UPDATE Contact SET familyName='...', givenName='...', ... WHERE familyName = '...'
```

#### 4-3-4. UPDATE / All rows
```java:Java
Contact contact = new Contact();
...
new Sql<>(Contact.class)
    .where(Condition.ALL)
    .update(connection, contact);
```
```sql:SQL
UPDATE Contact SET familyName='...', givenName='...', ...
```

#### 4-4. DELETE
#### 4-4-1. DELETE / 1 row
```java:Java
Contact contact = new Contact();
...
new Sql<>(Contact.class)
    .delete(connection, contact);
```
```sql:SQL
DELETE FROM Contact WHERE id = '...'
```

#### 4-4-2. DELETE / Multiple rows
```java:Java
List<Contact> contacts = new ArrayList<>();
...
new Sql<>(Contact.class)
    .delete(connection, contacts);
```
```sql:SQL
DELETE FROM Contact WHERE id = '...'
DELETE FROM Contact WHERE id = '...'
   ...
```

#### 4-4-3. DELETE / Specified Condition
```java:Java
new Sql<>(Contact.class)
    .where("{familyName} = {}", familyName)
    .delete(connection);
```
```sql:SQL
DELETE FROM Contact WHERE familyName = '...'
```

#### 4-4-4. DELETE / All rows
```java:Java
new Sql<>(Contact.class)
    .where(Condition.ALL)
    .delete(connection);
```
```sql:SQL
DELETE FROM Contact
```

### 5. Logging
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
