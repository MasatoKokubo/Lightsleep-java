Lightsleep / Manual
===========

### 1. Entity Class
Entity class is a class for storing the retrieved data in SELECT SQL, create it for each database table.

#### 1-1. Annotations to be used in the entity classes
##### 1-1-1. Table Annotation
Specifies the table name associated with the class.
If the table name is the same as the class name, you do not need to specify this annotation.

```java:Java
@Table("Person")
public class Person1 extends Person {
   ...
}
```

If you specify ```@Table("super")```, the class name of the superclass is the table name.

```java:Java
@Table("super")
public class Person1 extends Person {
   ...
}
```

##### 1-1-2. Key Annotation
Specifies that the column associated with the field is part of the primary key.

```java:Java
@Key
public String personId;
```

##### 1-1-3. Column Annotation
Specifies the column name associated with the field.
If the column name is the same as the field name, you do not need to specify this annotation.

```java:Java
    @Column("lastName")
    public String last;
```

##### 1-1-4. NonColumn Annotation
Specifies that the field not related to any column.

```java:Java
    @NonColumn
    public Address address = new Address();
```

##### 1-1-5. NonSelect Annotation
Specifies that the column related the field are not used in the SELECT SQL.

```java:Java
    @Column("firstName")
    @NonSelect
    public String first;
```

##### 1-1-6. NonInsert Annotation
Specifies that the column related the field are not used in the INSERT SQL.

```java:Java
    @Select("CONCAT({name.first}, ' ', {name.last})") // MySQL, Oracle
    @NonInsert
    @NonUpdate
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
    @Select("CONCAT({name.first}, ' ', {name.last})") // MySQL, Oracle
    @NonInsert
    @NonUpdate
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
    public Timestamp updated;
```

### 1-2. Interfaces that Entity Class to implement
#### 1-2-1. PreInsert Interface
If an entity class implements this interface, ```insert``` method of Sql class calls ```preInsert``` method of the entity before INSERT SQL execution.
In ```preInsert``` method, do the implementation of the numbering of the primary key or etc.

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

#### 1-2-2. Composite Interface
If an entity class implements this interface, ```select```, ```insert```, ```update``` or ```delete``` method of ```Sql``` class calls ```postSelect```, ```postInsert```, ```postUpdate``` or ```postDelete``` method of the entity class after the execution of each execute SQL.
However if ```update``` or ```delete``` method dose not have entity parameter, dose not call.
If an entity is enclose another entity, by implementing this interface, You can perform SQL processing to the enclosed entity in conjunction the entity which encloses.

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

### 2. Transaction
Execution of ```Transaction.execute``` method is equivalent to the execution of a transaction.
Define contents of the transaction by the argument ```transaction``` as a lambda expression.
The lambda expression is equivalent to the contents of ```Transaction.executeBody``` method and the argument of this method is a ```Connection```.

```java:Java
// A definition example of transaction
Transaction.execute(connection -> {
    // Start of the transaction
    new Sql<>(Person.class)
        .update(connection, person);
    ...
    // End of the transaction
});
```

If an exception is thrown during the transaction, ```Transaction.rollback``` method is called.
Otherwise, ```Transaction.commit``` method is called.

### 3. Connection Supplier
Getting of database connection (```java.sql.Connection```) is done in the ```Transaction.execute``` method.
Lightsleep has following classes to supply connections.

1. org.lightsleep.connection.JdbcConnection
1. org.lightsleep.connection.JndiConnection

```JdbcConnection``` class gets database connections using the ```java.sql.DriverManager.getConnection``` method.
```JndiConnection``` class gets database connections from the data source (```javax.sql.DataSource```) that was obtained using JNDI (Java Naming and Directory Interface).
Define the connection supplier class and information needed to connect in the *lightsleep.properties*.

```properties:lightsleep.properties
# JdbcConnection
ConnectionSupplier      = JdbcConnection
JdbcConnection.driver   = (JDBC Driver Class)
JdbcConnection.url      = (JDBC URL)
JdbcConnection.user     = (Database User)
JdbcConnection.password = (Database Password)
```

```properties:lightsleep.properties
# JndiConnection
connectionSupplier        = JndiConnection
JndiConnection.dataSource = jdbc/(Data Source Name)
```

### 4. SQLの実行
Use the various methods of Sql class to execute SQLs and define it in the lambda expression argument of ```Transaction.execute``` method.

#### 4-1. SELECT
#### 4-1-1. SELECT / 1 row / Expression Condition

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

#### 4-1-2. SELECT / 1 row / Entity Condition

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

#### 4-1-3. SELECT / Multiple rows / Expression Condition

```java:Java
List<Person> person = new ArrayList<Person>();
new Sql<>(Person.class)
    .where("{name.last} = {}", lastName)
    .select(connection, person::add);
```

```sql:SQL
SELECT personId, lastName, firstName, ... FROM Person WHERE lastName = '...'
```

#### 4-1-4. SELECT / Subquery Condition

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

#### 4-1-5. SELECT Expression Condition / AND

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

#### 4-1-6. SELECT Expression Condition / OR

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

#### 4-1-7. SELECT Expression Condition / (A AND B) OR (C AND D)

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

#### 4-1-8. SELECT / Select Columns

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

#### 4-1-9. SELECT / GROUP BY, HAVING

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

#### 4-1-10. SELECT / OFFSET, LIMIT, ORDER BY

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

#### 4-1-11. SELECT / FOR UPDATE

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

#### 4-1-12. SELECT / INNER JOIN

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

#### 4-1-13. SELECT / LEFT OUTER JOIN

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

#### 4-1-13. SELECT / RIGHT OUTER JOIN

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
#### 4-2-1. INSERT / 1 row

```java:Java
Person person = new Person();
...
new Sql<>(Person.class)
    .insert(connection, person);
```

```sql:SQL
INSERT INTO Person (personId, lastName, firstName, ...) VALUES ('...', '...', '...', ...)
```

#### 4-2-2. INSERT / Multiple rows

```java:Java
List<Person> persons = new ArrayList<>();
...
new Sql<>(Person.class)
    .insert(connection, persons);
```

```sql:SQL
INSERT INTO Person (personId, lastName, firstName, ...) VALUES ('...', '...', '...', ...)
...
```

#### 4-3. UPDATE
#### 4-3-3. UPDATE / 1 row
```java:Java
Person person = new Person();
...
new Sql<>(Person.class)
    .update(connection, person);
```
```sql:SQL
UPDATE Person SET lastName='...', firstName='...', ... WHERE personId = '...'
```

#### 4-3-3. UPDATE / Multiple rows
```java:Java
List<Person> persons = new ArrayList<>();
...
new Sql<>(Person.class)
    .update(connection, persons);
```
```sql:SQL
UPDATE Person SET lastName='...', firstName='...', ... WHERE personId = '...'
UPDATE Person SET lastName='...', firstName='...', ... WHERE personId = '...'
   ...
```

#### 4-3-3. UPDATE / Specified Condition
```java:Java
Person person = new Person();
...
new Sql<>(Person.class)
    .where("{name.last} = {}", lastName)
    .update(connection, person);
```
```sql:SQL
UPDATE Person SET lastName='...', firstName='...', ... WHERE lastName = '...'
```

#### 4-3-4. UPDATE / All rows
```java:Java
Person person = new Person();
...
new Sql<>(Person.class)
    .where(Condition.ALL)
    .update(connection, person);
```
```sql:SQL
UPDATE Person SET lastName='...', firstName='...', ...
```

#### 4-4. DELETE
#### 4-4-1. DELETE / 1 row
```java:Java
Person person = new Person();
...
new Sql<>(Person.class)
    .delete(connection, person);
```
```sql:SQL
DELETE FROM Person WHERE personId = '...'
```

#### 4-4-2. DELETE / Multiple rows
```java:Java
List<Person> persons = new ArrayList<>();
...
new Sql<>(Person.class)
    .delete(connection, persons);
```
```sql:SQL
DELETE FROM Person WHERE personId = '...'
DELETE FROM Person WHERE personId = '...'
   ...
```

#### 4-4-3. DELETE / Specified Condition
```java:Java
new Sql<>(Person.class)
    .where("{name.last} = {}", lastName)
    .delete(connection);
```
```sql:SQL
DELETE FROM Person WHERE lastName = '...'
```

#### 4-4-4. DELETE / All rows
```java:Java
new Sql<>(Person.class)
    .where(Condition.ALL)
    .delete(connection);
```
```sql:SQL
DELETE FROM Person
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
