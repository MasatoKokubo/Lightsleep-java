Lightsleep 2.1.0 / Manual
===========

[[Japanese]](Manual_ja.md)

<div id="TOC"></div>

### Table of Contents

1. [Package](#Package)
1. [Create entity classes](#EntityClass)
    1. [Annotations to be used in entity classes](#Entity-Annotation)
        1. [`@Table`](#Entity-Table)
        1. [`@Key]`(#Entity-Key)
        1. [`@Column`](#Entity-Column)
        1. [`@ColumnType`](#Entity-ColumnType)
        1. [`@NonColumn`](#Entity-NonColumn)
        1. [`@NonSelect`](#Entity-NonSelect)
        1. [`@NonInsert`](#Entity-NonInsert)
        1. [`@NonUpdate`](#Entity-NonUpdate)
        1. [`@Select`](#Entity-Select)
        1. [`@Insert`](#Entity-Insert)
        1. [`@Update`](#Entity-Update)
        1. [`@KeyProperty`, `@ColumnProperty`, ... and `@UpdateProperty`](#Entity-XxxxxProperty)
    1. [Interfaces implemented by entity classes](#Entity-Interface)
        1. [`PreInsert` Interface](#Entity-PreInsert)
        1. [`Composite` Interface](#Entity-Composite)
        1. [`PreStore` Interface](#Entity-PreStore)
        1. [`PostLoad` Interface](#Entity-PostLoad)
1. [Definition of `lightsleep.properties` file](#lightsleep-properties)
    1. [Logging library class](#Logger)
    1. [Database handler class](#Database)
    1. [Connection supplier class](#ConnectionSupplier)
1. [Transaction](#Transaction)
1. [Execution of SQL](#ExecuteSQL)
    1. [SELECT](#ExecuteSQL-select)
        1. [SELECT 1 row with an Expression condition](#ExecuteSQL-select-1-Expression)
        1. [SELECT 1 row with an Entity condition](#ExecuteSQL-select-Entity)
        1. [SELECT multiple rows with an Expression condition](#ExecuteSQL-select-N-Expression)
        1. [SELECT with a Subquery condition](#ExecuteSQL-select-Subquery)
        1. [SELECT with Expression conditions AND](#ExecuteSQL-select-Expression-and)
        1. [SELECT with Expression conditions OR](#ExecuteSQL-select-Expression-or)
        1. [SELECT with Expression conditions (A AND B) OR (C AND D)](#ExecuteSQL-select-Expression-andor)
        1. [SELECT with selection of columns](#ExecuteSQL-select-columns)
        1. [SELECT with GROUP BY and HAVING](#ExecuteSQL-select-groupBy-having)
        1. [SELECT with ORDER BY, OFFSET and LIMIT](#ExecuteSQL-select-orderBy-offset-limit)
        1. [SELECT with FOR UPDATE](#ExecuteSQL-select-forUpdate)
        1. [SELECT with INNER JOIN](#ExecuteSQL-select-innerJoin)
        1. [SELECT with LEFT OUTER JOIN](#ExecuteSQL-select-leftJoin)
        1. [SELECT with RIGHT OUTER JOIN](#ExecuteSQL-select-rightJoin)
    1. [INSERT](#ExecuteSQL-insert)
        1. [INSERT 1 row](#ExecuteSQL-insert-1)
        1. [INSERT multiple rows](#ExecuteSQL-insert-N)
    1. [UPDATE](#ExecuteSQL-update)
        1. [UPDATE 1 row](#ExecuteSQL-update-1)
        1. [UPDATE multiple rows](#ExecuteSQL-update-N)
        1. [UPDATE with a Condition and selection of columns](#ExecuteSQL-update-Condition)
        1. [UPDATE all rows](#ExecuteSQL-update-all)
    1. [DELETE](#ExecuteSQL-delete)
        1. [DELETE 1 row](#ExecuteSQL-delete-1)
        1. [DELETE multiple rows](#ExecuteSQL-delete-N)
        1. [DELETE with a Condition](#ExecuteSQL-delete-Condition)
        1. [DELETE all rows](#ExecuteSQL-delete-all)
1. [Expression Conversion](#Expression)

<div id="Package"></div>

[[To TOC]](#TOC)

### 1. Packages

Has the following packages.

|Packages|Contained classes and interfaces|
|:--|:--|
|org.lightsleep                |Classes you use primarily|
|org.lightsleep.component      |Classes you use to create SQL components such as conditions and expressions|
|org.lightsleep.connection     |Classes that supply connection wrapper classes to this library using various connection pool libraries|
|org.lightsleep.database       |Classes for generating SQL for various DBMSs|
|org.lightsleep.database.anchor|Classes used in mapping words contained in JDBC URLs to classes in the *org.lightsleep.database* package|
|org.lightsleep.entity         |Annotation classes and interfaces you use when creating entity classes|
|org.lightsleep.helper         |Helper classes used inside this library|
|org.lightsleep.logger         |Classes that output logs inside this library using various logging libraries|

<div id="EntityClass"></div>

[[To TOC]](#TOC)

### 2. Create entity classes
Create corresponding entity classes for each table in the database.

<div id="Entity-Annotation"></div>

#### 2-1. Annotations to be used in entity classes
Lihgtsleep automatically associates with tables in methods with an entity class or object as an argument, but you may also need to use annotations for entity classes.

Lightsleep has the following annotations.

|Annotation Type|Element(s)|Content of Indication|Target|
|:--|:--|:--|:--|
|[`@Table`             ](#Entity-Table        )|String value                                    |Related table name           |Class|
|[`@Key`               ](#Entity-Key          )|boolean value (default: true)                   |Related to the primary key   |Field|
|[`@Column`            ](#Entity-Column       )|String value                                    |Related column name          |Field|
|[`@ColumnType`        ](#Entity-ColumnType   )|Class<?> value                                  |Related column type          |Field|
|[`@NonColumn`         ](#Entity-NonColumn    )|boolean value (default: true)                   |Not related to any column    |Field|
|[`@NonSelect`         ](#Entity-NonSelect    )|boolean value (default: true)                   |Not used in SELECT SQL       |Field|
|[`@NonInsert`         ](#Entity-NonInsert    )|boolean value (default: true)                   |Not used in INSERT SQL       |Field|
|[`@NonUpdate`         ](#Entity-NonUpdate    )|boolean value (default: true)                   |Not used in UPDATE SQL       |Field|
|[`@Select`            ](#Entity-Select       )|String value                                    |Expression used in SELECT SQL|Field|
|[`@Insert`            ](#Entity-Insert       )|String value                                    |Expression used in INSERT SQL|Field|
|[`@Update`            ](#Entity-Update       )|String value                                    |Expression used in UPDATE SQL|Field|
|[`@KeyProperty`       ](#Entity-XxxxxProperty)|String property<br>boolean value (default: true)|Related to the primary key   |Class|
|[`@ColumnProperty`    ](#Entity-XxxxxProperty)|String property<br>String column                |Related column name          |Class|
|[`@ColumnTypeProperty`](#Entity-XxxxxProperty)|String property<br>Class<?> type                |Related column type          |Class|
|[`@NonColumnProperty` ](#Entity-XxxxxProperty)|String property<br>boolean value (default: true)|Not related to any columns   |Class|
|[`@NonSelectProperty` ](#Entity-XxxxxProperty)|String property<br>boolean value (default: true)|Not used in SELECT SQL       |Class|
|[`@NonInsertProperty` ](#Entity-XxxxxProperty)|String property<br>boolean value (default: true)|Not used in INSERT SQL       |Class|
|[`@NonUpdateProperty` ](#Entity-XxxxxProperty)|String property<br>boolean value (default: true)|Not used in UPDATE SQL       |Class|
|[`@SelectProperty`    ](#Entity-XxxxxProperty)|String property<br>String expression            |Expression used in SELECT SQL|Class|
|[`@InsertProperty`    ](#Entity-XxxxxProperty)|String property<br>String expression            |Expression used in INSERT SQL|Class|
|[`@UpdateProperty`    ](#Entity-XxxxxProperty)|String property<br>String expression            |Expression used in UPDATE SQL|Class|

<div id="Entity-Table"></div>

[[To TOC]](#TOC) [[To Annotation List]](#Entity-Annotation)

##### 2-1-1. @Table
Specifies the table name related to the class.
If the table name is the same as the class name, you do not need to specify this annotation.

```java:Java
// Java Example
@Table("Contact")
public class Person extends PersonBase {

    @Table("super")
     public static class Ex extends Person {
```

```groovy:Groovy
// Groovy Example
@Table('Contact')
class Person extends PersonBase {

    @Table('super')
     static class Ex extends Person {
```

If you specify `@Table("super")`, the class name of the superclass is the table name.

<div id="Entity-Key"></div>

##### 2-1-2. @Key
Indicates that the column related to the field is part of the primary key.

```java:Java
// Java Example
    @Key
    public int contactId;
    @Key
    public short childIndex;
```

```groovy:Groovy
// Groovy Example
    @Key
    int contactId
    @Key
    short childIndex
```

<div id="Entity-Column"></div>

##### 2-1-3. @Column
Indicates the name of column related to the field.
If the column name is the same as the field name, you do not need to specify it.

```java:Java
// Java Example
    @Column("firstName")
    public String first;
    @Column("lastName")
    public String last;
```

```groovy:Groovy
// Groovy Example
    @Column('firstName')
    String first
    @Column('lastName')
    String last
```

<div id="Entity-ColumnType"></div>

##### 2-1-4. @ColumnType
Indicates the type of column related to the field.
If the field type and column type are the same type, you do not need to specify it.
Specify if field type (e.g. date type) and column type (e.g. numerical type) are different.

```java:Java
// Java Example
    @ColumnType(Long.class)
    public Date birthday;
```

```groovy:Groovy
// Groovy Example
    @ColumnType(Long)
    Date birthday
```

<div id="Entity-NonColumn"></div>

[[To TOC]](#TOC) [[To Annotation List]](#Entity-Annotation)

##### 2-1-5. @NonColumn
Indicates that the field not related to any column.

```java:Java
// Java Example
    @NonColumn
    public List<Phone> phones;
    @NonColumn
    public List<Address> addresses;
```

```groovy:Groovy
// Groovy Example
    @NonColumn
    List<Phone> phones
    @NonColumn
    List<Address> addresses
```

<div id="Entity-NonSelect"></div>

##### 2-1-6. @NonSelect
Indicates that the column related the field is not used in SELECT SQL.

```java:Java
// Java Example
    @NonSelect
    public Timestamp createdTime;
    @NonSelect
    public Timestamp updatedTime;
```

```groovy:Groovy
// Groovy Example
    @NonSelect
    Timestamp createdTime
    @NonSelect
    Timestamp updatedTime
```

<div id="Entity-NonInsert"></div>

##### 2-1-7. @NonInsert
Indicates that the column related the field is not used in INSERT SQL.

```java:Java
// Java Example
    @NonInsert
    public Timestamp createdTime;
    @NonInsert
    public Timestamp updatedTime;
```

```groovy:Groovy
// Groovy Example
    @NonInsert
    Timestamp createdTime
    @NonInsert
    Timestamp updatedTime
```

<div id="Entity-NonUpdate"></div>

##### 2-1-8. @NonUpdate
Indicates that the column related the field is not used in UPDATE SQL.

```java:Java
// Java Example
    @NonUpdate
    public Timestamp createdTime;
```

```groovy:Groovy
// Groovy Example
    @NonUpdate
    Timestamp createdTime
```

<div id="Entity-Select"></div>

[[To TOC]](#TOC) [[To Annotation List]](#Entity-Annotation)

##### 2-1-9. @Select
Indicates a column expression instead of the column name in SELECT SQL.

```java:Java
// Java Example
    @Select("{firstName}||' '||{lastName}")
    @NonInsert@NonUpdate
    public String fullName;
```

```groovy:Groovy
// Groovy Example
    @Select("{firstName}||' '||{lastName}")
    @NonInsert@NonUpdate
    String fullName
```

<div id="Entity-Insert"></div>

##### 2-1-10. @Insert
Indicates an expression instead of the field value in INSERT SQL.
If this annotation is specified, the value of the field is not used.

```java:Java
// Java Example
    @Insert("CURRENT_TIMESTAMP")
    public Timestamp createdTime;
    @Insert("CURRENT_TIMESTAMP")
    public Timestamp updatedTime;
```

```groovy:Groovy
// Groovy Example
    @Insert('CURRENT_TIMESTAMP')
    Timestamp createdTime
    @Insert('CURRENT_TIMESTAMP')
    Timestamp updatedTime
```

<div id="Entity-Update"></div>

##### 2-1-11. @Update
Indicates an expression instead of the field value in UPDATE SQL.
If this annotation is specified, the value of the field is not used.

```java:Java
// Java Example
    @Update("{updateCount}+1")
    public int updateCount;
    @Update("CURRENT_TIMESTAMP")
    public Timestamp updatedTime;
```

```groovy:Groovy
// Groovy Example
    @Update('{updateCount}+1')
    int updateCount
    @Update('CURRENT_TIMESTAMP')
    Timestamp updatedTime
```

<div id="Entity-XxxxxProperty"></div>

[[To TOC]](#TOC) [[To Annotation List]](#Entity-Annotation)

##### 2-1-12. @KeyProperty, @ColumnProperty, ... and @UpdateProperty
These annotations are used to specify for fields defined in superclass.
The specified contents also affects subclasses, but specifications in the subclass takes precedence.
If you specify `value=false`, `column="" `, `type=Void.class` or `expression=""`, specifications in the superclass are canceled.

```java:Java
// Java Example
@KeyProperty(property="contactId")
@KeyProperty(property="childIndex")
public class Child extends ChildKey {
```

```groovy:Groovy
// Groovy Example
@KeyProperties([
    @KeyProperty(property='contactId'),
    @KeyProperty(property='childIndex')
])
class Child extends ChildKey {
```

### 2-2. Interfaces implemented by entity classes

<div id="Entity-PreInsert"></div>

[[To TOC]](#TOC)

#### 2-2-1. PreInsert Interface
If an entity class implements this interface, `insert` method of Sql class calls `preInsert` method of the entity before INSERT SQL execution.
In `preInsert` method, do the implementation of the numbering of the primary key or etc.

```java:Java
// Java Example
public abstract class Common implements PreInsert {
    @Key
    public int id;
        ...

    @Override
    public int preInsert(ConnectionWrapper conn) {
        id = Numbering.getNewId(conn, getClass());
        return 0;
    }
}
```

<div id="Entity-Composite"></div>

[[To TOC]](#TOC)

#### 2-2-2. Composite Interface
If an entity class implements this interface, `select`, `insert`, `update` or `delete` method of `Sql` class calls `postSelect`, `postInsert`, `postUpdate` or `postDelete` method of the entity class after the execution of each execute SQL.
However if `update` or `delete` method dose not have entity parameter, dose not call.
If an entity is enclose another entity, by implementing this interface, You can perform SQL processing to the enclosed entity in conjunction the entity which encloses.

```java:Java
// Java Example
@Table("super")
public class ContactComposite extends Contact implements Composite {
    @NonColumn
    public final List<Phone> phones = new ArrayList<>();

    @Override
    public void postSelect(ConnectionWrapper conn) {
        if (id != 0) {
            new Sql<>(Phone.class).connection(conn)
                .where("{contactId}={}", id)
                .orderBy("{phoneNumber}")
                .select(phones::add);
        }
    }

    @Override
    public int postInsert(ConnectionWrapper conn) {
        phones.forEach(phone -> phone.contactId = id);
        int count = new Sql<>(Phone.class).connection(conn)
                .insert(phones);
        return count;
    }

    @Override
    public int postUpdate(ConnectionWrapper conn) {
        List<Integer> phoneIds = phones.stream()
            .map(phone -> phone.id)
            .filter(id -> id != 0)
            .collect(Collectors.toList());

        // Delete phones
        int count += new Sql<>(Phone.class).connection(conn)
            .where("{contactId}={}", id)
            .doIf(phoneIds.size() > 0,
                sql -> sql.and("{id} NOT IN {}", phoneIds)
            )
            .delete();

        // Uptete phones
        count += new Sql<>(Phone.class).connection(conn)
            .update(phones.stream()
                .filter(phone -> phone.id != 0)
                .collect(Collectors.toList()));

        // Insert phones
        count += new Sql<>(Phone.class).connection(conn)
            .insert(phones.stream()
                .filter(phone -> phone.id == 0)
                .collect(Collectors.toList()));

        return count;
    }
 
    @Override
    public int postDelete(ConnectionWrapper conn) {
        int count = new Sql<>(Phone.class).connection(conn)
            .where("{contactId}={}", id)
            .delete(conn);
        return count;
    }
```

<div id="Entity-PreStore"></div>

[[To TOC]](#TOC)

#### 2-2-3. PreStore Interface
If the entity class implements this interface, the `preStore` method of the entity class is called in the `insert` and `update` methods of the `Sql` class before each SQL is executed.

<div id="Entity-PostLoad"></div>

#### 2-2-4. PostLoad Interface
If the entity class implements this interface, `postLoad` method of the entity class is called in the `select` methods of the `Sql` class after the SELECT SQL is executed and the entity's value obtained from the database is set.

```java:Java
import org.lightsleep.entity.*;

public class Contact implements PreStore, PostLoad {

    @Column("phone")
    public String[] phones_

    @NonColumn
    public final List<String> phones = new ArrayList<>();

    public void preStore() {
        phones_ = phones.toArray(new String[phones.size()]);
    }

    public void postLoad() {
        phones.clear();
        Arrays.stream(phones_).forEach(phones::add);
    }
```
<div id="lightsleep-properties"></div>

[[To TOC]](#TOC)

### 3. Definition of lightsleep.properties

Lightsleep.properties is a properties file referenced by Lightsleep and you can specify the following contents.  
*(The `Database` property up to version 2.0.0 has been removed in version 2.1.0, the database handler is automatically determined from the corresponding JDBC URL.)*

|Property Name|Content|Default Value|
|:------------|:------|:------------|
|[Logger            ](#Logger            )|Logging class|`Std$Out$Info`|
|[ConnectionSupplier](#ConnectionSupplier)|Connection Supplier class|`Jdbc`|
| url                                     |JDBC URL|None|
| urls                                    |JDBC URLs|None|
| maxStringLiteralLength                  |Maximum length of string literals when generates SQL|128|
| maxBinaryLiteralLength                  |Maximum length of binary literals when generates SQL|128|
| maxLogStringLength                      |Maximum length of string values output to log|200|
| maxLogByteArrayLength                   |Maximum number of elements of byte arrays output to log|200|
| maxLogArrayLength                       |Maximum number of elements of arrays output to log|100|
| maxLogMapSize                           |Maximum number of elements of maps output to log|100|

Place the `lightsleep.properties` file in one of the class paths. Or you can specify the file path with the system property `lightsleep.resource`. *(java -Dlightsleep.resource=...)*  
In addition to the above define the properties used by the connection pool library.

Example of lightsleep.properties:

```properties:lightsleep.properties
Logger      = Log4j2
ConnectionSupplier = Dbcp
url         = jdbc:postgresql://postgresqlserver/example
username    = example
password    = _example_
initialSize = 10
maxTotal    = 100
```

You can specify multiple JDBC URLs in the `urls` property separated by commas. *(since 2.1.0)*  
If you define a property with more than one line, append a backslash (`\`) to the end of the line other than the last line.  
If you specify `urls`, the specification of `url` will be invalid.

```properties:lightsleep.properties
# Case of specifying multiple JDBC URLs
Logger      = Log4j2
ConnectionSupplier = Dbcp
urls        = jdbc:postgresql://postgresqlserver/example1,\
              jdbc:postgresql://postgresqlserver/example2
user        = example
password    = _example_
initialSize = 10
maxTotal    = 100
```

You can specify a different DBMS URL for each JDBC URL. If the user and password are different for each JDBC URL, specify them in the URL.

```properties:lightsleep.properties
# Case of using multiple DBMS (specifying user and password in URL)
Logger = Log4j2
ConnectionSupplier = Dbcp
urls = \
    jdbc:db2://db2-11:50000/example:user=example;password=_example_;,\
    jdbc:mysql://mysql57/example?user=example&password=_example_,\
    jdbc:oracle:thin:example/_example_@oracle121:1521:example,\
    jdbc:postgresql://postgresql101/example?user=example&password=_example_,\
    jdbc:sqlite:C:/sqlite/example,\
    jdbc:sqlserver://sqlserver13;database=example;user=example;password=_example_,\

initialSize = 10
maxTotal    = 100
```

To specify a connection supplier for each URL, write it within `[]` at the head of the URL. *(since 2.1.0)*  
The specification of this form takes precedence over the specification of `ConnectionSupplier` property.  
You can specify the `username` and `jdbcUrl` property with the `user` and `url` property, but specify properties other than those with the property name specific to the connection pool library.

```properties:lightsleep.properties
# Case of specifying a connection supplier for each URL
Logger = Log4j2
urls = \
    [  Jdbc  ]jdbc:db2://db2-11:50000/example:user=example;password=_example_;,\
    [  C3p0  ]jdbc:mysql://mysql57/example?user=example&password=_example_,\
    [  Dbcp  ]jdbc:oracle:thin:example/_example_@oracle121:1521:example,\
    [HikariCP]jdbc:postgresql://postgresql101/example?user=example&password=_example_,\
    [TomcatCP]jdbc:sqlite:C:/sqlite/example,\
    [  Jdbc  ]jdbc:sqlserver://sqlserver13;database=example;user=example;password=_example_,\

# Dbcp, HikariCP, TomcatCP
initialSize = 10

# Dbcp
maxTotal    = 10

# TomcatCP
maxActive   = 10

# HikariCP
minimumIdle     = 10
maximumPoolSize = 10
```

<div id="Logger"></div>

[[To TOC]](#TOC) [[To Properties List]](#lightsleep-properties)

#### 3-1. Logging library class

Select the value of the `Logger` property from the following.

|Value|Logging library etc.|Log level|Definition file used by the logging library|
|:--|:--|:-:|:--|
|`Jdk`   |Java Runtime|-|logging.properties|
|`Log4j` |Log4j       |-|log4j.properties or log4j.xml|
|`Log4j2`|Log4j 2     |-|log4j2.xml|
|`SLF4J` |SLF4J       |-|Depends on target logging library implementation|
|`Std$Out$Trace`|Output to System.out|trace|(*nothing*)|
|`Std$Out$Debug`|*(same as above)*|debug|*(nothing)*|
|`Std$Out$Info` |*(same as above)*|info |*(nothing)*|
|`Std$Out$Warn` |*(same as above)*|warn |*(nothing)*|
|`Std$Out$Error`|*(same as above)*|error|*(nothing)*|
|`Std$Out$Fatal`|*(same as above)*|fatal|*(nothing)*|
|`Std$Err$Trace`|Output to System.err|trace|*(nothing)*|
|`Std$Err$Debug`|*(same as above)*|debug|*(nothing)*|
|`Std$Err$Info` |*(same as above)*|info |*(nothing)*|
|`Std$Err$Warn` |*(same as above)*|warn |*(nothing)*|
|`Std$Err$Error`|*(same as above)*|error|*(nothing)*|
|`Std$Err$Fatal`|*(same as above)*|fatal|*(nothing)*|

If you do not specify it, `Std$Out$Info` is selected.

<div id="Database"></div>

[[To TOC]](#TOC) [[To Properties List]](#lightsleep-properties)

#### 3-2. Database handler class

The database handler class is automatically selected from the contents of the JDBC URL specified in the `url` or `urls` property. *(since 2.1.0)*

|Word included in JDBC URL|Selected class|Corresponding DBMS|
|:--|:--|:--|
|db2       |DB2       |<a href="https://www.ibm.com/us-en/marketplace/db2-express-c" target="_blank">DB2</a>|
|mysql     |MySQL     |<a href="https://www.mysql.com/" target="_blank">MySQL</a>|
|oracle    |Oracle    |<a href="https://www.oracle.com/database/index.html" target="_blank">Oracle Database</a>|
|postgresql|PostgreSQL|<a href="https://www.postgresql.org/" target="_blank">PostgreSQL</a>|
|sqlite    |SQLite    |<a href="https://sqlite.org/index.html" target="_blank">SQLite</a>|
|sqlserver |SQLServer |<a href="https://www.microsoft.com/ja-jp/sql-server/sql-server-2016" target="_blank">Microsoft SQL Server</a>|

If the JDBC URL does not contain any of the words above, `Standard` class is selected.

<div id="ConnectionSupplier"></div>

[[To TOC]](#TOC) [[To Properties List]](#lightsleep-properties)

#### 3-3. Connection supplier class

Select the value of the `ConnectionSupplier` property from the following.

|Value|Corresponding connection pool libraries|
|:--|:--|
|C3p0    |<a href="http://www.mchange.com/projects/c3p0/" target="_blank">c3p0</a>|
|Dbcp    |<a href="https://commons.apache.org/proper/commons-dbcp/" target="_blank">Apache Commons DBCP</a>|
|HikariCP|<a href="http://brettwooldridge.github.io/HikariCP/" target="_blank">HikariCP</a>|
|TomcatCP|<a href="http://tomcat.apache.org/tomcat-8.5-doc/jdbc-pool.html" target="_blank">Tomcat JDBC Connection Pool</a>|
|Jndi    |Java Naming and Directory Interface (JNDI) (<a href="http://tomcat.apache.org/tomcat-8.5-doc/jndi-datasource-examples-howto.html" target="_blank">In the case of Tomcat</a>)|
|Jdbc    |DriverManager#getConnection(String url, Properties info) Method|

Also define the information required by the connection pool library in the lightsleep.properties file.
Below the ConnectionSupplier (from `url`) in definition examples of lightsleep.properties are the definition contents to be passed to the connection supplier.

```properties:lightsleep.properties
# lightsleep.properties / Jdbc
ConnectionSupplier = Jdbc
url      = jdbc:db2://db2-11:50000/example
user     = example
password = _example_
```

```properties:lightsleep.properties
# lightsleep.properties / C3p0
ConnectionSupplier = C3p0
url      = jdbc:mysql://mysql57/example
user     = example
password = _example_
```

```properties:c3p0.properties
# c3p0.properties
c3p0.initialPoolSize = 20
c3p0.minPoolSize     = 10
c3p0.maxPoolSize     = 30
```

```properties:lightsleep.properties
# lightsleep.properties / Dbcp
ConnectionSupplier = Dbcp
url         = jdbc:oracle:thin:@oracle121:1521:example
user        = example
  or
username    = example
password    = _example_
initialSize = 20
maxTotal    = 30
```

```properties:lightsleep.properties
# lightsleep.properties / HikariCP
ConnectionSupplier = HikariCP
url             = jdbc:postgresql://postgres96/example
  or
jdbcUrl         = jdbc:postgresql://postgres96/example
user            = example
  or
username        = example
password        = _example_
minimumIdle     = 10
maximumPoolSize = 30
```

```properties:lightsleep.properties
# lightsleep.properties / TomcatCP
ConnectionSupplier = TomcatCP
url         = jdbc:sqlserver://sqlserver13;database=example
user        = example
  or
username    = example
password    = _example_
initialSize = 20
maxActive   = 30
```

```properties:lightsleep.properties
# lightsleep.properties / Jndi
ConnectionSupplier = Jndi
dataSource         = jdbc/example
  or
dataSource         = example
```

<div id="Transaction"></div>

[[To TOC]](#TOC)

### 4. Transaction
Execution of `Transaction.execute` method is equivalent to the execution of a transaction.
Define contents of the transaction by the argument `transaction` as a lambda expression.
The lambda expression is equivalent to the contents of `Transaction.executeBody` method and the argument of this method is a `ConnectionWrapper`.

```java:Java
// Java Example
Contact contact = new Contact(1, "Akane", "Apple");

Transaction.execute(conn -> {
    // Start of transaction
    new Sql<>(Contact.class).connection(conn)
        .insert(contact);
    ...
    // End of transaction
});
```

```groovy:Groovy
// Groovy Example
def contact = new Contact(1, 'Akane', 'Apple')

Transaction.execute {
    // Start of transaction
    new Sql<>(Contact).connection(it)
        .insert(contact)
    ...
    // End of transaction
}
```

If you define multiple JDBC URLs in `lightsleep.properties`, you need to specify which URL to execute the transaction.
The `ConnectionSupplier.find` method searches for a JDBC URL that contains all of the string array of arguments.
An exception will be thrown if more than one is found or if it can not be found.

```java:Java
// Java Example
public static final ConnectionSupplier supplier1 = ConnectionSupplier.find("example1");
    ・・・
Contact contact = new Contact(1, "Akane", "Apple");

Transaction.execute(supplier1, conn -> {
    // Start of transaction
    new Sql<>(Contact.class).connection(conn)
        .insert(contact);
   ...
    // End of transaction
});
```

```groovy:Groovy
// Groovy Example
static final supplier1 = ConnectionSupplier.find('example1')
    ・・・
def contact = new Contact(1, 'Akane', 'Apple')

Transaction.execute(supplier1) {
    // Start of transaction
    new Sql<>(Contact).connection(it)
        .insert(contact)
    ...
    // End of transaction
}
```

If an exception is thrown during the transaction, `Transaction.rollback` method is called.
Otherwise, `Transaction.commit` method is called.

<div id="ExecuteSQL"></div>

[[To TOC]](#TOC)

### 5. Execution of SQL
Use the various methods of `Sql` class to execute SQLs and define it in the lambda expression argument of `Transaction.execute` method.

<div id="ExecuteSQL-select"></div>

#### 5-1. SELECT

<div id="ExecuteSQL-select-1-Expression"></div>

#### 5-1-1. SELECT 1 row with an Expression condition

```java:Java
// Java Example
Transaction.execute(conn -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class).connection(conn)
        .where("{id}={}", 1)
        .select();
});
```

```groovy:Groovy
// Groovy Example
Transaction.execute {
    def contactOpt = new Sql<>(Contact).connection(it)
        .where('{id}={}', 1)
        .select()
}
```

```sql:SQL
-- Generated SQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
```

<div id="ExecuteSQL-select-Entity"></div>

[[To TOC]](#TOC)

#### 5-1-2. SELECT 1 row with an Entity condition

```java:Java
// Java Example
Contact contact = new Contact();
contact.id = 1;
Transaction.execute(conn -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class).connection(conn)
        .where(contact)
        .select();
});
```

```groovy:Groovy
// Groovy Example
def contact = new Contact()
contact.id = 1
Transaction.execute {
    def contactOpt = new Sql<>(Contact).connection(it)
        .where(contact)
        .select()
}
```

```sql:SQL
-- Generated SQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
```

<div id="ExecuteSQL-select-N-Expression"></div>

[[To TOC]](#TOC)

#### 5-1-3. SELECT multiple rows with an Expression condition

```java:Java
// Java Example
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where("{lastName}={}", "Apple")
        .select(contacts::add)
);
```

```groovy:Groovy
// Groovy Example
List<Contact> contacts = []
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where('{lastName}={}', 'Apple')
        .select({contacts << it})
}
```

```sql:SQL
-- Generated SQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple'
```

<div id="ExecuteSQL-select-Subquery"></div>

[[To TOC]](#TOC)

#### 5-1-4. SELECT with a Subquery condition

```java:Java
// Java Example
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class, "C").connection(conn)
        .where("EXISTS",
            new Sql<>(Phone.class, "P")
                .where("{P.contactId}={C.id}")
        )
        .select(contacts::add)
);
```

```groovy:Groovy
// Groovy Example
List<Contact> contacts = []
Transaction.execute {
    new Sql<>(Contact, 'C').connection(it)
        .where('EXISTS',
            new Sql<>(Phone, 'P')
                .where('{P.contactId}={C.id}')
        )
        .select({contacts << it})
}
```

```sql:SQL
-- Generated SQL
SELECT C.id AS C_id, C.firstName AS C_firstName, C.lastName AS C_lastName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime FROM Contact C WHERE EXISTS (SELECT * FROM Phone P WHERE P.contactId=C.id)
```

<div id="ExecuteSQL-select-Expression-and"></div>

[[To TOC]](#TOC)

#### 5-1-5. SELECT with Expression conditions (AND)

```java:Java
// Java Example
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where("{lastName}={}", "Apple")
        .and  ("{firstName}={}", "Akane")
        .select(contacts::add)
);
```

```groovy:Groovy
// Groovy Example
List<Contact> contacts = []
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where('{lastName}={}', 'Apple')
        .and  ('{firstName}={}', 'Akane')
        .select({contacts << it})
}
```

```sql:SQL
-- Generated SQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple' AND firstName='Akane'
```

<div id="ExecuteSQL-select-Expression-or"></div>

[[To TOC]](#TOC)

#### 5-1-6. SELECT with Expression Condition (OR)

```java:Java
// Java Example
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where("{lastName}={}", "Apple")
        .or   ("{lastName}={}", "Orange")
        .select(contacts::add)
);
```

```groovy:Groovy
// Groovy Example
List<Contact> contacts = []
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where('{lastName}={}', 'Apple')
        .or   ('{lastName}={}', 'Orange')
        .select({contacts << it})
}
```

```sql:SQL
-- Generated SQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple' OR lastName='Orange'
```

<div id="ExecuteSQL-select-Expression-andor"></div>

[[To TOC]](#TOC)

#### 5-1-7. SELECT with Expression conditions A AND B OR C AND D

```java:Java
// Java Example
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where(Condition
            .of ("{lastName}={}", "Apple")
            .and("{firstName}={}", "Akane")
        )
        .or(Condition
            .of ("{lastName}={}", "Orange")
            .and("{firstName}={}", "Setoka")
        )
        .select(contacts::add)
);
```

```groovy:Groovy
// Groovy Example
List<Contact> contacts = []
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where(Condition
            .of ('{lastName}={}', 'Apple')
            .and('{firstName}={}', 'Akane')
        )
        .or(Condition
            .of ('{lastName}={}', 'Orange')
            .and('{firstName}={}', 'Setoka')
        )
        .select({contacts << it})
}
```

```sql:SQL
-- Generated SQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple' AND firstName='Akane' OR lastName='Orange' AND firstName='Setoka'
```

<div id="ExecuteSQL-select-columns"></div>

[[To TOC]](#TOC)

#### 5-1-8. SELECT with selection of columns

```java:Java
// Java Example
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where("{lastName}={}", "Apple")
        .columns("lastName", "firstName")
        .select(contacts::add)
);
```

```groovy:Groovy
// Groovy Example
List<Contact> contacts = []
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where('{lastName}={}', 'Apple')
        .columns('lastName', 'firstName')
        .select({contacts << it})
}
```

```sql:SQL
-- Generated SQL
SELECT firstName, lastName FROM Contact WHERE lastName='Apple'
```

<div id="ExecuteSQL-select-groupBy-having"></div>

[[To TOC]](#TOC)

#### 5-1-9. SELECT with GROUP BY and HAVING

```java:Java
// Java Example
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class, "C").connection(conn)
        .columns("lastName")
        .groupBy("{lastName}")
        .having("COUNT({lastName})>=2")
        .select(contacts::add)
);
```

```groovy:Groovy
// Groovy Example
List<Contact> contacts = []
Transaction.execute {
    new Sql<>(Contact, 'C').connection(it)
        .columns('lastName')
        .groupBy('{lastName}')
        .having('COUNT({lastName})>=2')
        .select({contacts << it})
}
```

```sql:SQL
-- Generated SQL
SELECT MIN(C.lastName) AS C_lastName FROM Contact C GROUP BY C.lastName HAVING COUNT(C.lastName)>=2
```

<div id="ExecuteSQL-select-orderBy-offset-limit"></div>

[[To TOC]](#TOC)

#### 5-1-10. SELECT with ORDER BY, OFFSET and LIMIT

```java:Java
// Java Example
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .orderBy("{lastName}")
        .orderBy("{firstName}")
        .orderBy("{id}")
        .offset(10).limit(5)
        .select(contacts::add)
);
```

```groovy:Groovy
// Groovy Example
List<Contact> contacts = []
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .orderBy('{lastName}')
        .orderBy('{firstName}')
        .orderBy('{id}')
        .offset(10).limit(5)
        .select({contacts << it})
}
```

```sql:SQL
-- Generated SQL / DB2, MySQL, PostgreSQL, SQLite
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact ORDER BY lastName ASC, firstName ASC, id ASC LIMIT 5 OFFSET 10
```

```sql:SQL
-- Generated SQL / Oracle, SQLServer (Skip rows during getting)
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact ORDER BY lastName ASC, firstName ASC, id ASC
```

<div id="ExecuteSQL-select-forUpdate"></div>

[[To TOC]](#TOC)

#### 5-1-11. SELECT with FOR UPDATE

```java:Java
// Java Example
Transaction.execute(conn -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class).connection(conn)
        .where("{id}={}", 1)
        .forUpdate()
        .select();
});
```

```groovy:Groovy
// Groovy Example
Transaction.execute {
    def contactOpt = new Sql<>(Contact).connection(it)
        .where('{id}={}', 1)
        .forUpdate()
        .select()
}
```

```sql:SQL
-- Generated SQL / DB2
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1 FOR UPDATE WITH RS
```

```sql:SQL
-- Generated SQL / MySQL, Oracle, PostgreSQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1 FOR UPDATE
```

```sql:SQL
-- Generated SQL / SQLite
-- UnsupportedOperationException is thrown on SQLite because FOR UPDATE is not supported.
```

```sql:SQL
-- Generated SQL / SQLServer
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WITH (ROWLOCK,UPDLOCK) WHERE id=1
```

<div id="ExecuteSQL-select-innerJoin"></div>

[[To TOC]](#TOC)

#### 5-1-12. SELECT with INNER JOIN

```java:Java
// Java Example
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
Transaction.execute(conn ->
    new Sql<>(Contact.class, "C").connection(conn)
        .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
        .where("{C.id}={}", 1)
        .<Phone>select(contacts::add, phones::add)
);
```

```groovy:Groovy
// Groovy Example
List<Contact> contacts = []
List<Phone> phones = []
Transaction.execute {
    new Sql<>(Contact, 'C').connection(it)
        .innerJoin(Phone, 'P', '{P.contactId}={C.id}')
        .where('{C.id}={}', 1)
        .select({contacts << it}, {phones << it})
}
```

```sql:SQL
-- Generated SQL
SELECT C.id AS C_id, C.firstName AS C_firstName, C.lastName AS C_lastName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C INNER JOIN Phone P ON P.contactId=C.id WHERE C.id=1
```

<div id="ExecuteSQL-select-leftJoin"></div>

[[To TOC]](#TOC)

#### 5-1-13. SELECT with LEFT OUTER JOIN

```java:Java
// Java Example
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
Transaction.execute(conn ->
	new Sql<>(Contact.class, "C").connection(conn)
	    .leftJoin(Phone.class, "P", "{P.contactId}={C.id}")
	    .where("{C.lastName}={}", "Apple")
	    .<Phone>select(contacts::add, phones::add)
);
```

```groovy:Groovy
// Groovy Example
List<Contact> contacts = []
List<Phone> phones = []
Transaction.execute {
    new Sql<>(Contact, 'C').connection(it)
        .leftJoin(Phone, 'P', '{P.contactId}={C.id}')
        .where('{C.lastName}={}', 'Apple')
        .select({contacts << it}, {phones << it})
}
```

```sql:SQL
-- Generated SQL
SELECT C.id AS C_id, C.firstName AS C_firstName, C.lastName AS C_lastName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C LEFT OUTER JOIN Phone P ON P.contactId=C.id WHERE C.lastName='Apple'
```

<div id="ExecuteSQL-select-rightJoin"></div>

[[To TOC]](#TOC)

#### 5-1-14. SELECT with RIGHT OUTER JOIN

```java:Java
// Java Example
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
Transaction.execute(conn ->
    new Sql<>(Contact.class, "C").connection(conn)
        .rightJoin(Phone.class, "P", "{P.contactId}={C.id}")
        .where("{P.label}={}", "Main")
        .<Phone>select(contacts::add, phones::add)
);
```

```groovy:Groovy
// Groovy Example
List<Contact> contacts = []
List<Phone> phones = []
Transaction.execute {
    new Sql<>(Contact, 'C').connection(it)
        .rightJoin(Phone, 'P', '{P.contactId}={C.id}')
        .where('{P.label}={}', 'Main')
        .select({contacts << it}, {phones << it})
}
```

```sql:SQL
-- Generated SQL
-- An exception is thrown in SQLite because RIGHT OUTER JOIN is not supported.
SELECT C.id AS C_id, C.firstName AS C_firstName, C.lastName AS C_lastName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C RIGHT OUTER JOIN Phone P ON P.contactId=C.id WHERE P.label='Main'
```

#### 5-1-15. SELECT COUNT(*)

```java:Java
// Java Example
int[] count = new int[1];
Transaction.execute(conn ->
    count[0] = new Sql<>(Contact.class).connection(conn)
        .where("lastName={}", "Apple")
        .selectCount()
);
```

```groovy:Groovy
// Groovy Example
def count = 0
Transaction.execute {
    count = new Sql<>(Contact).connection(it)
        .where('lastName={}', 'Apple')
        .selectCount()
}
```

```sql:SQL
-- Generated SQL
SELECT COUNT(*) FROM Contact WHERE lastName='Apple'
```

<div id="ExecuteSQL-insert"></div>

[[To TOC]](#TOC)

#### 5-2. INSERT

<div id="ExecuteSQL-insert-1"></div>

#### 5-2-1. INSERT 1 row

```java:Java
// Java Example
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .insert(new Contact(1, "Akane", "Apple", 2001, 1, 1))
```

```groovy:Groovy
// Groovy Example
Transaction.execute {
    new Sql<>(Contact).connection(it)
       .insert(new Contact(1, "Akane", "Apple", 2001, 1, 1))
}
```

```sql:SQL
-- Generated SQL / DB2, MySQL, Oracle, PostgreSQL
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (1, 'Akane', 'Apple', DATE'2001-01-01', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- Generated SQL / SQLite
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (1, 'Akane', 'Apple', '2001-01-01', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- Generated SQL / SQLServer
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (1, 'Akane', 'Apple', CAST('2001-01-01' AS DATE), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

<div id="ExecuteSQL-insert-N"></div>

[[To TOC]](#TOC)

#### 5-2-2. INSERT multiple rows

```java:Java
// Java Example
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .insert(Arrays.asList(
            new Contact(2, "Yukari", "Apple", 2001, 1, 2),
            new Contact(3, "Azusa", "Apple", 2001, 1, 3)
        ))
```

```groovy:Groovy
// Groovy Example
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .insert([
            new Contact(2, "Yukari", "Apple", 2001, 1, 2),
            new Contact(3, "Azusa", "Apple", 2001, 1, 3)
        ])
}
```

```sql:SQL
-- Generated SQL / DB2, MySQL, Oracle, PostgreSQL
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (2, 'Yukari', 'Apple', DATE'2001-01-02', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (3, 'Azusa', 'Apple', DATE'2001-01-03', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- Generated SQL / SQLite
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (2, 'Yukari', 'Apple', '2001-01-02', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (3, 'Azusa', 'Apple', '2001-01-03', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- Generated SQL / SQLServer
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (2, 'Yukari', 'Apple', CAST('2001-01-02' AS DATE), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO Contact (id, firstName, lastName, birthday, updateCount, createdTime, updatedTime) VALUES (3, 'Azusa', 'Apple', CAST('2001-01-03' AS DATE), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

<div id="ExecuteSQL-update"></div>

[[To TOC]](#TOC)

#### 5-3. UPDATE

<div id="ExecuteSQL-update-1"></div>

#### 5-3-1. UPDATE 1 row

```java:Java
// Java Example
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where("{id}={}", 1)
        .select()
        .ifPresent(contact -> {
            contact.firstName = "Akiyo";
            new Sql<>(Contact.class).connection(conn)
                .update(contact);
        })
);
```

```groovy:Groovy
// Groovy Example
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where('{id}={}', 1)
        .select()
        .ifPresent {Contact contact ->
            contact.firstName = 'Akiyo'
            new Sql<>(Contact).connection(it)
                .update(contact)
        }
}
```

```sql:SQL
-- Generated SQL / DB2, MySQL, Oracle, PostgreSQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
UPDATE Contact SET firstName='Akiyo', lastName='Apple', birthday=DATE'2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
```

```sql:SQL
-- Generated SQL / SQLite
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
UPDATE Contact SET firstName='Akiyo', lastName='Apple', birthday='2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
```

```sql:SQL
-- Generated SQL / SQLServer
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
UPDATE Contact SET firstName='Akiyo', lastName='Apple', birthday=CAST('2001-01-01' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
```

<div id="ExecuteSQL-update-N"></div>

[[To TOC]](#TOC)

#### 5-3-2. UPDATE multiple rows

```java:Java
// Java Example
Transaction.execute(conn -> {
    List<Contact> contacts = new ArrayList<>();
    new Sql<>(Contact.class).connection(conn)
        .where("{lastName}={}", "Apple")
        .select(contact -> {
            contact.lastName = "Apfel";
            contacts.add(contact);
        });
    new Sql<>(Contact.class).connection(conn)
        .update(contacts);
});
```

```groovy:Groovy
// Groovy Example
Transaction.execute {
    List<Contact> contacts = []
    new Sql<>(Contact).connection(it)
        .where('{lastName}={}', 'Apple')
        .select({Contact contact ->
            contact.lastName = 'Apfel'
            contacts << contact
        })
    new Sql<>(Contact).connection(it)
        .update(contacts)
}
```

```sql:SQL
-- Generated SQL / DB2, MySQL, Oracle, PostgreSQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple'
UPDATE Contact SET firstName='Akiyo', lastName='Apfel', birthday=DATE'2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
UPDATE Contact SET firstName='Yukari', lastName='Apfel', birthday=DATE'2001-01-02', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=2
UPDATE Contact SET firstName='Azusa', lastName='Apfel', birthday=DATE'2001-01-03', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=3
```

```sql:SQL
-- Generated SQL / SQLite
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple'
UPDATE Contact SET firstName='Akiyo', lastName='Apfel', birthday='2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
UPDATE Contact SET firstName='Yukari', lastName='Apfel', birthday='2001-01-02', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=2
UPDATE Contact SET firstName='Azusa', lastName='Apfel', birthday='2001-01-03', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=3
```

```sql:SQL
-- Generated SQL / SQLServer
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Apple'
UPDATE Contact SET firstName='Akiyo', lastName='Apfel', birthday=CAST('2001-01-01' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
UPDATE Contact SET firstName='Yukari', lastName='Apfel', birthday=CAST('2001-01-02' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=2
UPDATE Contact SET firstName='Azusa', lastName='Apfel', birthday=CAST('2001-01-03' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=3
```

<div id="ExecuteSQL-update-Condition"></div>

[[To TOC]](#TOC)

#### 5-3-3. UPDATE with a Condition and selection of columns

```java:Java
// Java Example
Contact contact = new Contact();
contact.lastName = "Pomme";
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where("{lastName}={}", "Apfel")
        .columns("lastName")
        .update(contact)
);
```

```groovy:Groovy
// Groovy Example
def contact = new Contact()
contact.lastName = 'Pomme'
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where('{lastName}={}', 'Apfel')
        .columns('lastName')
        .update(contact)
}
```

```sql:SQL
-- Generated SQL
UPDATE Contact SET lastName='Pomme' WHERE lastName='Apfel'
```

<div id="ExecuteSQL-update-all"></div>

[[To TOC]](#TOC)

#### 5-3-4. UPDATE all rows

```java:Java
// Java Example
Contact contact = new Contact();
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where(Condition.ALL)
        .columns("birthday")
        .update(contact)
);
```

```groovy:Groovy
// Groovy Example
def contact = new Contact()
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where(Condition.ALL)
        .columns('birthday')
        .update(contact)
}
```

```sql:SQL
-- Generated SQL
UPDATE Contact SET birthday=NULL
```


<div id="ExecuteSQL-delete"></div>

[[To TOC]](#TOC)

#### 5-4. DELETE

<div id="ExecuteSQL-delete-1"></div>

#### 5-4-1. DELETE 1 row

```java:Java
// Java Example
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where("{id}={}", 1)
        .select()
        .ifPresent(contact ->
            new Sql<>(Contact.class).connection(conn)
                .delete(contact))
);
```

```groovy:Groovy
// Groovy Example
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where('{id}={}', 1)
        .select()
        .ifPresent {contact ->
            new Sql<>(Contact).connection(it)
                .delete(contact)
        }
}
```

```sql:SQL
-- Generated SQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
DELETE FROM Contact WHERE id=1
```


<div id="ExecuteSQL-delete-N"></div>

#### 5-4-2. DELETE multiple rows

```java:Java
// Java Example
Transaction.execute(conn -> {
    List<Contact> contacts = new ArrayList<>();
    new Sql<>(Contact.class).connection(conn)
        .where("{lastName}={}", "Pomme")
        .select(contacts::add);
    new Sql<>(Contact.class).connection(conn)
        .delete(contacts);
});
```

```groovy:Groovy
// Groovy Example
Transaction.execute {
    List<Contact> contacts = []
    new Sql<>(Contact).connection(it)
        .where('{lastName}={}', 'Pomme')
        .select({contacts << it})
    new Sql<>(Contact).connection(it)
        .delete(contacts)
}
```

```sql:SQL
-- Generated SQL
SELECT id, firstName, lastName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE lastName='Pomme'
DELETE FROM Contact WHERE id=2
DELETE FROM Contact WHERE id=3
```

<div id="ExecuteSQL-delete-Condition"></div>

#### 5-4-3. DELETE with a Condition

```java:Java
// Java Example
Transaction.execute(conn ->
    new Sql<>(Contact.class).connection(conn)
        .where("{lastName}={}", "Orange")
        .delete()
);
```

```groovy:Groovy
// Groovy Example
Transaction.execute {
    new Sql<>(Contact).connection(it)
        .where('{lastName}={}', 'Orange')
        .delete()
}
```

```sql:SQL
-- Generated SQL
DELETE FROM Contact WHERE lastName='Orange'
```

<div id="ExecuteSQL-delete-all"></div>

#### 5-4-4. DELETE all rows

```java:Java
// Java Example
Transaction.execute(conn ->
    new Sql<>(Phone.class).connection(conn)
        .where(Condition.ALL)
        .delete()
);
```

```groovy:Groovy
// Groovy Example
Transaction.execute {
    new Sql<>(Phone).connection(it)
        .where(Condition.ALL)
        .delete()
}
```

```sql:SQL
-- Generated SQL
DELETE FROM Phone
```

<div id="Expression"></div>

[[To TOC]](#TOC)

### 6. Expression Conversion

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
    - `of(String content, Object... arguments)`
    - `of(String content, Sql<E> outerSql, Sql<SE> subSql)`
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
|`{xxx}`|The column name related to property `xxx`|
|`{A.xxx}`|`"A."` + The column name related to property `xxx` (`A` is a table alias)|
|`{A_xxx}`|The column alias related to table alias `A` and `xxx` property|
|`{#xxx}`|The value of property `xxx` of an entity set on the `Sql` object (or an entity argument of `Sql#insert` or `Sql#update` method)|

<div style="text-align:center; margin-top:20px"><i>&copy; 2016 Masato Kokubo</i></div>
