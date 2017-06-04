Lightsleep / Manual
===========

This document is a manual of Lightsleep that is an O/R (Object-Relational) mapping library.

<div id="TOC"></div>

### Table of Contents

1. [Package](#Package)
1. [Create entity classes](#EntityClass)
    1. [Annotations to be used in entity classes](#Entity-Annotation)
        1. [@Table](#Entity-Table)
        1. [@Key](#Entity-Key)
        1. [@Column](#Entity-Column)
        1. [@ColumnType](#Entity-ColumnType)
        1. [@NonColumn](#Entity-NonColumn)
        1. [@NonSelect](#Entity-NonSelect)
        1. [@NonInsert](#Entity-NonInsert)
        1. [@NonUpdate](#Entity-NonUpdate)
        1. [@Select](#Entity-Select)
        1. [@Insert](#Entity-Insert)
        1. [@Update](#Entity-Update)
        1. [@KeyProperty, @ColumnProperty, ... and @UpdateProperty](#Entity-XxxxxProperty)
    1. [Interfaces implemented by entity classes](#Entity-Interface)
        1. [PreInsert Interface](#Entity-PreInsert)
        1. [Composite Interface](#Entity-Composite)
        1. [PreStore Interface](#Entity-PreStore)
        1. [PostLoad Interface](#Entity-PostLoad)
1. [Definition of lightsleep.properties](#lightsleep-properties)
    1. [Specifying a logging library class](#Logger)
    1. [Specifying a database handler class](#Database)
    1. [Specifying a connection supplier class](#ConnectionSupplier)
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

|Packages|Classes|
|:--|:--|
|org.lightsleep           |Main classes|
|org.lightsleep.component |Classes for creating SQL components|
|org.lightsleep.connection|Class for supplying connection|
|org.lightsleep.database  |Database handler classes|
|org.lightsleep.entity    |Annotation classes and interfaces to use when creating entity classes|
|org.lightsleep.helper    |Helper classes mainly used internally|
|org.lightsleep.logger    |Classes for using various log libraries|

<div id="EntityClass"></div>

[[To TOC]](#TOC)

### 2. Create entity classes
Entity classes are for storing data retrieved with SELECT SQL, create them for each database table.

<div id="Entity-Annotation"></div>

#### 2-1. Annotations to be used in entity classes
Lihgtsleep automatically associates with tables in methods with an entity class or object as an argument, but you may also need to use annotations for entity classes.

Lightsleep has the following annotations.

|Annotation|Content|Target|
|:--|:--|:--|
|[`@Table`             ](#Entity-Table        )|Related table name|Class|
|[`@Key`               ](#Entity-Key          )|Related to the primary key|Field|
|[`@Column`            ](#Entity-Column       )|Column name|Field|
|[`@ColumnType`        ](#Entity-ColumnType   )|Column type|Field|
|[`@NonColumn`         ](#Entity-NonColumn    )|Not related to columns|Field|
|[`@NonSelect`         ](#Entity-NonSelect    )|Not used in SELECT SQL|Field|
|[`@NonInsert`         ](#Entity-NonInsert    )|Not used in INSERT SQL|Field|
|[`@NonUpdate`         ](#Entity-NonUpdate    )|Not used in UPDATE SQL|Field|
|[`@Select`            ](#Entity-Select       )|Expression used in SELECT SQL|Field|
|[`@Insert`            ](#Entity-Insert       )|Expression used in INSERT SQL|Field|
|[`@Update`            ](#Entity-Update       )|Expression used in UPDATE SQL|Field|
|[`@KeyProperty`       ](#Entity-XxxxxProperty)|Related to the primary key|Class|
|[`@ColumnProperty`    ](#Entity-XxxxxProperty)|Column name|Class|
|[`@ColumnTypeProperty`](#Entity-XxxxxProperty)|Column type|Class|
|[`@NonColumnProperty` ](#Entity-XxxxxProperty)|Not related to columns|Class|
|[`@NonSelectProperty` ](#Entity-XxxxxProperty)|Not used in SELECT SQL|Class|
|[`@NonInsertProperty` ](#Entity-XxxxxProperty)|Not used in INSERT SQL|Class|
|[`@NonUpdateProperty` ](#Entity-XxxxxProperty)|Not used in UPDATE SQL|Class|
|[`@SelectProperty`    ](#Entity-XxxxxProperty)|Expression used in SELECT SQL|Class|
|[`@InsertProperty`    ](#Entity-XxxxxProperty)|Expression used in INSERT SQL|Class|
|[`@UpdateProperty`    ](#Entity-XxxxxProperty)|Expression used in UPDATE SQL|Class|

<div id="Entity-Table"></div>

[[To TOC]](#TOC) [[To Annotation List]](#Entity-Annotation)

##### 2-1-1. @Table
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

<div id="Entity-Key"></div>

##### 2-1-2. @Key
Indicates that the column associated with the field is part of the primary key.

```java:Java
@Key
public String id;
```

<div id="Entity-Column"></div>

##### 2-1-3. @Column
Indicates the name of column associated with the field.
If the column name is the same as the field name, you do not need to specify it.

```java:Java
    @Column("family_name")
    public String familyName;
```

<div id="Entity-ColumnType"></div>

##### 2-1-4. @ColumnType
Indicates the type of column associated with the field.
If the field type and column type are the same type, you do not need to specify it.
Specify if field type (e.g. date type) and column type (e.g. numerical type) are different.

```java:Java
    @ColumnType(Long.class)
    public Date birhtday;
```

<div id="Entity-NonColumn"></div>

[[To TOC]](#TOC) [[To Annotation List]](#Entity-Annotation)

##### 2-1-5. @NonColumn
Indicates that the field not related to any column.

```java:Java
    @NonColumn
    public List<Phone> phones = new ArrayList<>();
```

<div id="Entity-NonSelect"></div>

##### 2-1-6. @NonSelect
Indicates that the column related the field is not used in SELECT SQL.

```java:Java
    @NonSelect
    public String givenName;
```

<div id="Entity-NonInsert"></div>

##### 2-1-7. @NonInsert
Indicates that the column related the field is not used in INSERT SQL.

```java:Java
    @Select("CONCAT({givenName}, ' ', {familyName})") // MySQL, Oracle
    @NonInsert @NonUpdate
    public String fullName;
```

<div id="Entity-NonUpdate"></div>

##### 2-1-8. @NonUpdate
Indicates that the column related the field is not used in UPDATE SQL.

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @NonUpdate
    public Timestamp created;
```

<div id="Entity-Select"></div>

[[To TOC]](#TOC) [[To Annotation List]](#Entity-Annotation)

##### 2-1-9. @Select
Indicates a column expression instead of the column name in SELECT SQL.

```java:Java
    @Select("CONCAT({givenName}, ' ', {familyName})") // MySQL, Oracle
    @NonInsert @NonUpdate
    public String fullName;
```

<div id="Entity-Insert"></div>

##### 2-1-10. @Insert
Indicates an expression instead of the field value in INSERT SQL.
If this annotation is specified, the value of the field is not used.

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @NonUpdate
    public Timestamp created;
```

<div id="Entity-Update"></div>

##### 2-1-11. @Update
Indicates an expression instead of the field value in UPDATE SQL.
If this annotation is specified, the value of the field is not used.

```java:Java
    @Insert("CURRENT_TIMESTAMP")
    @Update("CURRENT_TIMESTAMP")
    public Timestamp modified;
```

<div id="Entity-XxxxxProperty"></div>

##### 2-1-12. @KeyProperty, @ColumnProperty, ... and @UpdateProperty
These annotations are used to specify for fields defined in superclass.
You can specify multiple same annotations for a class.

```java:Java
import org.lightsleep.entity.*;

@Table("super")
@ColumnProperty(property="familyName", column="family_name")
public class Contact1 extends Contact {
```

<div id="Entity-Interface"></div>

[[To TOC]](#TOC) [[To Annotation List]](#Entity-Annotation)

### 2-2. Interfaces implemented by entity classes

<div id="Entity-PreInsert"></div>

#### 2-2-1. PreInsert Interface
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

<div id="Entity-Composite"></div>

[[To TOC]](#TOC)

#### 2-2-2. Composite Interface
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

Lightsleep.properties is a properties file referenced by Lightsleep and define the following contents.

|Property Name|What to specify|
|:--|:--|
|[Logger            ](#Logger            )|The class corresponding to the logging library that Lightsleep uses for log output.|
|[Database          ](#Database          )|The database handler class corresponding to the DBMS to be used.|
|[ConnectionSupplier](#ConnectionSupplier)|The class corresponding to the connection supplier (Connection pool library etc.).|

In addition to the above define the properties used by the connection pool library.

Example of lightsleep.properties:

```properties:lightsleep.properties
Logger             = Log4j2
Database           = PostgreSQL
ConnectionSupplier = Dbcp
url                = jdbc:postgresql://postgresqlserver/example
username           = example
password           = _example_
initialSize        = 10
maxTotal           = 100
```

<div id="Logger"></div>

[[To TOC]](#TOC) [[To Properties List]](#lightsleep-properties)

#### 3-1. Specifying a logging library class

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

If not specified, `Std$Out$Info` is selected.

<div id="Database"></div>

[[To TOC]](#TOC) [[To Properties List]](#lightsleep-properties)

#### 3-2. Specifying a database handler class

Select the value of the `Database` property from the following.

If you are using a DBMS other than the above, specify `Standard` or nothing.
However, in that case, DBMS specific functions can not be used.

|Value|DBMS|
|:--|:--|
|DB2 *(since 1.9.0)*|<a href="https://www.ibm.com/us-en/marketplace/db2-express-c" target="_blank">DB2</a>|
|MySQL     |<a href="https://www.mysql.com/" target="_blank">MySQL</a>|
|Oracle    |<a href="https://www.oracle.com/database/index.html" target="_blank">Oracle Database</a>|
|PostgreSQL|<a href="https://www.postgresql.org/" target="_blank">PostgreSQL</a>|
|SQLite    |<a href="https://sqlite.org/index.html" target="_blank">SQLite</a>|
|SQLServer |<a href="https://www.microsoft.com/ja-jp/sql-server/sql-server-2016" target="_blank">Microsoft SQL Server</a>|

<div id="ConnectionSupplier"></div>

[[To TOC]](#TOC) [[To Properties List]](#lightsleep-properties)

#### 3-3. Specifying a connection supplier class

Select the value of the `ConnectionSupplier` property from the following.

|Value|Connection Supplier etc.|
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
url                = jdbc:db2://db2-11:50000/example
user               = example
password           = _example_
```

```properties:lightsleep.properties
# lightsleep.properties / C3p0
ConnectionSupplier = C3p0
url                = jdbc:mysql://mysql57/example
user               = example
password           = _example_
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
url                = jdbc:oracle:thin:@oracle121:1521:example
username           = example
password           = _example_
initialSize        = 20
maxTotal           = 30
```

```properties:lightsleep.properties
# lightsleep.properties / HikariCP
ConnectionSupplier = HikariCP
jdbcUrl            = jdbc:postgresql://postgres96/example
username           = example
password           = _example_
minimumIdle        = 10
maximumPoolSize    = 30
```

```properties:lightsleep.properties
# lightsleep.properties / TomcatCP
ConnectionSupplier = TomcatCP
url                = jdbc:sqlserver://sqlserver13;database=example
username           = example
password           = _example_
initialSize        = 20
maxActive          = 30
```

```properties:lightsleep.properties
# lightsleep.properties / Jndi
ConnectionSupplier = Jndi
dataSource         = jdbc/example
```

<div id="Transaction"></div>

[[To TOC]](#TOC)

### 4. Transaction
Execution of `Transaction.execute` method is equivalent to the execution of a transaction.
Define contents of the transaction by the argument `transaction` as a lambda expression.
The lambda expression is equivalent to the contents of `Transaction.executeBody` method and the argument of this method is a `Connection`.

```java:Java
// Example in Java
Contact contact = new Contact();
contact.id = 1;
contact.familyName = "Apple";
contact.givenName  = "Akane";

// An example of transaction
Transaction.execute(connection -> {
    // Start of transaction
    new Sql<>(Contact.class).insert(connection, contact);
    ...
    // End of transaction
});
```

```groovy:Groovy
// Example in Groovy
def contact = new Contact()
contact.id = 1
contact.familyName = 'Apple'
contact.givenName  = 'Akane'

// An example of transaction
Transaction.execute {
    // Start of transaction
    new Sql<>(Contact.class).insert(it, contact)
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
// Example in Java
Transaction.execute(connection -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class)
        .where("{id}={}", 1)
        .select(connection);
});
```

```groovy:Groovy
// Example in Groovy
Transaction.execute {
    def contactOpt = new Sql<>(Contact.class)
        .where('{id}={}', 1)
        .select(it)
}
```

```sql:SQL
-- Executed SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
```

<div id="ExecuteSQL-select-Entity"></div>

[[To TOC]](#TOC)

#### 5-1-2. SELECT 1 row with an Entity condition

```java:Java
// Example in Java
Contact contact = new Contact();
contact.id = 1;
Transaction.execute(connection -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class)
        .where(contact)
        .select(connection);
});
```

```groovy:Groovy
// Example in Groovy
def contact = new Contact()
contact.id = 1
Transaction.execute {
    def contactOpt = new Sql<>(Contact.class)
        .where(contact)
        .select(it)
}
```

```sql:SQL
-- Executed SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
```

<div id="ExecuteSQL-select-N-Expression"></div>

[[To TOC]](#TOC)

#### 5-1-3. SELECT multiple rows with an Expression condition

```java:Java
// Example in Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .select(connection, contacts::add)
);
```

```groovy:Groovy
// Example in Groovy
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Apple')
        .select(it, {contacts << it})
}
```

```sql:SQL
-- Executed SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple'
```

<div id="ExecuteSQL-select-Subquery"></div>

[[To TOC]](#TOC)

#### 5-1-4. SELECT with a Subquery condition

```java:Java
// Example in Java
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

```groovy:Groovy
// Example in Groovy
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class, 'C')
        .where('EXISTS',
            new Sql<>(Phone.class, 'P')
                .where('{P.contactId}={C.id}')
        )
        .select(it, {contacts << it})
}
```

```sql:SQL
-- Executed SQL
SELECT C.id AS C_id, C.familyName AS C_familyName, C.givenName AS C_givenName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime FROM Contact C WHERE EXISTS (SELECT * FROM Phone P WHERE P.contactId=C.id)
```

<div id="ExecuteSQL-select-Expression-and"></div>

[[To TOC]](#TOC)

#### 5-1-5. SELECT with Expression conditions (AND)

```java:Java
// Example in Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .and  ("{givenName}={}", "Akane")
        .select(connection, contacts::add)
);
```

```groovy:Groovy
// Example in Groovy
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Apple')
        .and  ('{givenName}={}', 'Akane')
        .select(it, {contacts << it})
}
```

```sql:SQL
-- Executed SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple' AND givenName='Akane'
```

<div id="ExecuteSQL-select-Expression-or"></div>

[[To TOC]](#TOC)

#### 5-1-6. SELECT with Expression Condition (OR)

```java:Java
// Example in Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .or   ("{familyName}={}", "Orange")
        .select(connection, contacts::add)
);
```

```groovy:Groovy
// Example in Groovy
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Apple')
        .or   ('{familyName}={}', 'Orange')
        .select(it, {contacts << it})
}
```

```sql:SQL
-- Executed SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple' OR familyName='Orange'
```

<div id="ExecuteSQL-select-Expression-andor"></div>

[[To TOC]](#TOC)

#### 5-1-7. SELECT with Expression conditions A AND B OR C AND D

```java:Java
// Example in Java
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

```groovy:Groovy
// Example in Groovy
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class)
        .where(Condition
            .of ('{familyName}={}', 'Apple')
            .and('{givenName}={}', 'Akane')
        )
        .or(Condition
            .of ('{familyName}={}', 'Orange')
            .and('{givenName}={}', 'Setoka')
        )
        .select(it, {contacts << it})
}
```

```sql:SQL
-- Executed SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple' AND givenName='Akane' OR familyName='Orange' AND givenName='Setoka'
```

<div id="ExecuteSQL-select-columns"></div>

[[To TOC]](#TOC)

#### 5-1-8. SELECT with selection of columns

```java:Java
// Example in Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apple")
        .columns("familyName", "givenName")
        .select(connection, contacts::add)
);
```

```groovy:Groovy
// Example in Groovy
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Apple')
        .columns('familyName', 'givenName')
        .select(it, {contacts << it})
}
```

```sql:SQL
-- Executed SQL
SELECT familyName, givenName FROM Contact WHERE familyName='Apple'
```

<div id="ExecuteSQL-select-groupBy-having"></div>

[[To TOC]](#TOC)

#### 5-1-9. SELECT with GROUP BY and HAVING

```java:Java
// Example in Java
List<Contact> contacts = new ArrayList<Contact>();
Transaction.execute(connection ->
    new Sql<>(Contact.class, "C")
        .columns("familyName")
        .groupBy("{familyName}")
        .having("COUNT({familyName})>=2")
        .select(connection, contacts::add)
);
```

```groovy:Groovy
// Example in Groovy
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class, 'C')
        .columns('familyName')
        .groupBy('{familyName}')
        .having('COUNT({familyName})>=2')
        .select(it, {contacts << it})
}
```

```sql:SQL
-- Executed SQL
SELECT MIN(C.familyName) AS C_familyName FROM Contact C GROUP BY C.familyName HAVING COUNT(C.familyName)>=2
```

<div id="ExecuteSQL-select-orderBy-offset-limit"></div>

[[To TOC]](#TOC)

#### 5-1-10. SELECT with ORDER BY, OFFSET and LIMIT

```java:Java
// Example in Java
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

```groovy:Groovy
// Example in Groovy
def contacts = []
Transaction.execute {
    new Sql<>(Contact.class)
        .orderBy('{familyName}')
        .orderBy('{givenName}')
        .orderBy('{id}')
        .offset(10).limit(5)
        .select(it, {contacts << it})
}
```

```sql:SQL
-- Executed SQL / DB2, MySQL, PostgreSQL, SQLite
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact ORDER BY familyName ASC, givenName ASC, id ASC LIMIT 5 OFFSET 10
```

```sql:SQL
-- Executed SQL / Oracle, SQLServer (Skip rows during getting)
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact ORDER BY familyName ASC, givenName ASC, id ASC
```

<div id="ExecuteSQL-select-forUpdate"></div>

[[To TOC]](#TOC)

#### 5-1-11. SELECT with FOR UPDATE

```java:Java
// Example in Java
Transaction.execute(connection -> {
    Optional<Contact> contactOpt = new Sql<>(Contact.class)
        .where("{id}={}", 1)
        .forUpdate()
        .select(connection);
});
```

```groovy:Groovy
// Example in Groovy
Transaction.execute {
    def contactOpt = new Sql<>(Contact.class)
        .where('{id}={}', 1)
        .forUpdate()
        .select(it)
}
```

```sql:SQL
-- Executed SQL / DB2
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1 FOR UPDATE WITH RS
```

```sql:SQL
-- Executed SQL / MySQL, Oracle, PostgreSQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1 FOR UPDATE
```

```sql:SQL
-- Executed SQL / SQLite
-- UnsupportedOperationException is thrown on SQLite because FOR UPDATE is not supported.
```

```sql:SQL
-- Executed SQL / SQLServer
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WITH (ROWLOCK,UPDLOCK) WHERE id=1
```

<div id="ExecuteSQL-select-innerJoin"></div>

[[To TOC]](#TOC)

#### 5-1-12. SELECT with INNER JOIN

```java:Java
// Example in Java
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
Transaction.execute(connection ->
    new Sql<>(Contact.class, "C")
        .innerJoin(Phone.class, "P", "{P.contactId}={C.id}")
        .where("{C.id}={}", 1)
        .<Phone>select(connection, contacts::add, phones::add)
);
```

```groovy:Groovy
// Example in Groovy
def contacts = []
def phones = []
Transaction.execute {
    new Sql<>(Contact.class, 'C')
        .innerJoin(Phone.class, 'P', '{P.contactId}={C.id}')
        .where('{C.id}={}', 1)
        .select(it, {contacts << it}, {phones << it})
}
```

```sql:SQL
-- Executed SQL
SELECT C.id AS C_id, C.familyName AS C_familyName, C.givenName AS C_givenName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C INNER JOIN Phone P ON P.contactId=C.id WHERE C.id=1
```

<div id="ExecuteSQL-select-leftJoin"></div>

[[To TOC]](#TOC)

#### 5-1-13. SELECT with LEFT OUTER JOIN
```java:Java
// Example in Java
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
Transaction.execute(connection ->
	new Sql<>(Contact.class, "C")
	    .leftJoin(Phone.class, "P", "{P.contactId}={C.id}")
	    .where("{C.familyName}={}", "Apple")
	    .<Phone>select(connection, contacts::add, phones::add)
);
```

```groovy:Groovy
// Example in Groovy
def contacts = []
def phones = []
Transaction.execute {
    new Sql<>(Contact.class, 'C')
        .leftJoin(Phone.class, 'P', '{P.contactId}={C.id}')
        .where('{C.familyName}={}', 'Apple')
        .select(it, {contacts << it}, {phones << it})
}
```

```sql:SQL
-- Executed SQL
SELECT C.id AS C_id, C.familyName AS C_familyName, C.givenName AS C_givenName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C LEFT OUTER JOIN Phone P ON P.contactId=C.id WHERE C.familyName='Apple'
```

<div id="ExecuteSQL-select-rightJoin"></div>

[[To TOC]](#TOC)

#### 5-1-14. SELECT with RIGHT OUTER JOIN
```java:Java
// Example in Java
List<Contact> contacts = new ArrayList<>();
List<Phone> phones = new ArrayList<>();
Transaction.execute(connection ->
    new Sql<>(Contact.class, "C")
        .rightJoin(Phone.class, "P", "{P.contactId}={C.id}")
        .where("{P.label}={}", "Main")
        .<Phone>select(connection, contacts::add, phones::add)
);
```

```groovy:Groovy
// Example in Groovy
def contacts = []
def phones = []
Transaction.execute {
    new Sql<>(Contact.class, 'C')
        .rightJoin(Phone.class, 'P', '{P.contactId}={C.id}')
        .where('{P.label}={}', 'Main')
        .select(it, {contacts << it}, {phones << it})
}
```

```sql:SQL
-- Executed SQL
-- An exception is thrown in SQLite because RIGHT OUTER JOIN is not supported.
SELECT C.id AS C_id, C.familyName AS C_familyName, C.givenName AS C_givenName, C.birthday AS C_birthday, C.updateCount AS C_updateCount, C.createdTime AS C_createdTime, C.updatedTime AS C_updatedTime, P.contactId AS P_contactId, P.childIndex AS P_childIndex, P.label AS P_label, P.content AS P_content FROM Contact C RIGHT OUTER JOIN Phone P ON P.contactId=C.id WHERE P.label='Main'
```

#### 5-1-15. SELECT COUNT(*)
```java:Java
// Example in Java
int[] rowCount = new int[1];
Transaction.execute(connection ->
    rowCount[0] = new Sql<>(Contact.class)
        .where("familyName={}", "Apple")
        .selectCount(connection)
);
```

```groovy:Groovy
// Example in Groovy
def rowCount = 0
Transaction.execute {
    rowCount = new Sql<>(Contact.class)
        .where('familyName={}', 'Apple')
        .selectCount(it)
}
```

```sql:SQL
-- Executed SQL
SELECT COUNT(*) FROM Contact WHERE familyName='Apple'
```

<div id="ExecuteSQL-insert"></div>

[[To TOC]](#TOC)

#### 5-2. INSERT

<div id="ExecuteSQL-insert-1"></div>

#### 5-2-1. INSERT 1 row

```java:Java
// Example in Java
Contact contact = new Contact();
contact.id = 1;
contact.familyName = "Apple";
contact.givenName = "Akane";
Calendar calendar = Calendar.getInstance();
calendar.set(2001, 1-1, 1, 0, 0, 0);
contact.birthday = new Date(calendar.getTimeInMillis())

Transaction.execute(connection ->
    new Sql<>(Contact.class).insert(connection, contact));
```

```groovy:Groovy
// Example in Groovy
def contact = new Contact()
contact.id = 1
contact.familyName = 'Apple'
contact.givenName = 'Akane'
Calendar calendar = Calendar.instance
calendar.set(2001, 1-1, 1, 0, 0, 0)
contact.birthday = new Date(calendar.timeInMillis)

Transaction.execute {
    new Sql<>(Contact.class).insert(it, contact)
}
```

```sql:SQL
-- Executed SQL / DB2, MySQL, Oracle, PostgreSQL
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (1, 'Apple', 'Akane', DATE'2001-01-01', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- Executed SQL / SQLite
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (1, 'Apple', 'Akane', '2001-01-01', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- Executed SQL / SQLServer
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (1, 'Apple', 'Akane', CAST('2001-01-01' AS DATE), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

<div id="ExecuteSQL-insert-N"></div>

[[To TOC]](#TOC)

#### 5-2-2. INSERT multiple rows

```java:Java
// Example in Java
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

```groovy:Groovy
// Example in Groovy
def contacts = []

def contact = new Contact()
contact.id = 2; contact.familyName = 'Apple'; contact.givenName = 'Yukari'
def calendar = Calendar.instance
calendar.set(2001, 1-1, 2, 0, 0, 0)
contact.birthday = new Date(calendar.timeInMillis)
contacts << contact

contact = new Contact()
contact.id = 3; contact.familyName = 'Apple'; contact.givenName = 'Azusa'
calendar = Calendar.instance
calendar.set(2001, 1-1, 3, 0, 0, 0)
contact.birthday = new Date(calendar.timeInMillis)
contacts << contact

Transaction.execute {
    new Sql<>(Contact.class).insert(it, contacts)
}
```

```sql:SQL
-- Executed SQL / DB2, MySQL, Oracle, PostgreSQL
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (2, 'Apple', 'Yukari', DATE'2001-01-02', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (3, 'Apple', 'Azusa', DATE'2001-01-03', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- Executed SQL / SQLite
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (2, 'Apple', 'Yukari', '2001-01-02', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (3, 'Apple', 'Azusa', '2001-01-03', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

```sql:SQL
-- Executed SQL / SQLServer
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (2, 'Apple', 'Yukari', CAST('2001-01-02' AS DATE), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO Contact (id, familyName, givenName, birthday, updateCount, createdTime, updatedTime) VALUES (3, 'Apple', 'Azusa', CAST('2001-01-03' AS DATE), 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
```

<div id="ExecuteSQL-update"></div>

[[To TOC]](#TOC)

#### 5-3. UPDATE

<div id="ExecuteSQL-update-1"></div>

#### 5-3-1. UPDATE 1 row

```java:Java
// Example in Java
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

```groovy:Groovy
// Example in Groovy
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{id}={}', 1)
        .select(it)
        .ifPresent {Contact contact ->
            contact.givenName = 'Akiyo'
            new Sql<>(Contact.class).update(it, contact)
        }
}
```

```sql:SQL
-- Executed SQL / DB2, MySQL, Oracle, PostgreSQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
UPDATE Contact SET familyName='Apple', givenName='Akiyo', birthday=DATE'2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
```

```sql:SQL
-- Executed SQL / SQLite
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
UPDATE Contact SET familyName='Apple', givenName='Akiyo', birthday='2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
```

```sql:SQL
-- Executed SQL / SQLServer
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
UPDATE Contact SET familyName='Apple', givenName='Akiyo', birthday=CAST('2001-01-01' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
```

<div id="ExecuteSQL-update-N"></div>

[[To TOC]](#TOC)

#### 5-3-2. UPDATE multiple rows

```java:Java
// Example in Java
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

```groovy:Groovy
// Example in Groovy
Transaction.execute {
    def contacts = []
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Apple')
        .select(it, {Contact contact ->
            contact.familyName = 'Apfel'
            contacts << contact
        })
    new Sql<>(Contact.class).update(it, contacts)
}
```

```sql:SQL
-- Executed SQL / DB2, MySQL, Oracle, PostgreSQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple'
UPDATE Contact SET familyName='Apfel', givenName='Akiyo', birthday=DATE'2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
UPDATE Contact SET familyName='Apfel', givenName='Yukari', birthday=DATE'2001-01-02', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=2
UPDATE Contact SET familyName='Apfel', givenName='Azusa', birthday=DATE'2001-01-03', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=3
```

```sql:SQL
-- Executed SQL / SQLite
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple'
UPDATE Contact SET familyName='Apfel', givenName='Akiyo', birthday='2001-01-01', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
UPDATE Contact SET familyName='Apfel', givenName='Yukari', birthday='2001-01-02', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=2
UPDATE Contact SET familyName='Apfel', givenName='Azusa', birthday='2001-01-03', updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=3
```

```sql:SQL
-- Executed SQL / SQLServer
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Apple'
UPDATE Contact SET familyName='Apfel', givenName='Akiyo', birthday=CAST('2001-01-01' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=1
UPDATE Contact SET familyName='Apfel', givenName='Yukari', birthday=CAST('2001-01-02' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=2
UPDATE Contact SET familyName='Apfel', givenName='Azusa', birthday=CAST('2001-01-03' AS DATE), updateCount=updateCount+1, updatedTime=CURRENT_TIMESTAMP WHERE id=3
```

<div id="ExecuteSQL-update-Condition"></div>

[[To TOC]](#TOC)

#### 5-3-3. UPDATE with a Condition and selection of columns

```java:Java
// Example in Java
Contact contact = new Contact();
contact.familyName = "Pomme";
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Apfel")
        .columns("familyName")
        .update(connection, contact)
);
```

```groovy:Groovy
// Example in Groovy
def contact = new Contact()
contact.familyName = 'Pomme'
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Apfel')
        .columns('familyName')
        .update(it, contact)
}
```

```sql:SQL
-- Executed SQL
UPDATE Contact SET familyName='Pomme' WHERE familyName='Apfel'
```

<div id="ExecuteSQL-update-all"></div>

[[To TOC]](#TOC)

#### 5-3-4. UPDATE all rows

```java:Java
// Example in Java
Contact contact = new Contact();
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where(Condition.ALL)
        .columns("birthday")
        .update(connection, contact)
);
```

```groovy:Groovy
// Example in Groovy
def contact = new Contact()
Transaction.execute {
    new Sql<>(Contact.class)
        .where(Condition.ALL)
        .columns('birthday')
        .update(it, contact)
}
```

```sql:SQL
-- Executed SQL
UPDATE Contact SET birthday=NULL
```


<div id="ExecuteSQL-delete"></div>

[[To TOC]](#TOC)

#### 5-4. DELETE

<div id="ExecuteSQL-delete-1"></div>

#### 5-4-1. DELETE 1 row

```java:Java
// Example in Java
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{id}={}", 1)
        .select(connection)
        .ifPresent(contact ->
            new Sql<>(Contact.class).delete(connection, contact))
);
```

```groovy:Groovy
// Example in Groovy
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{id}={}', 1)
        .select(it)
        .ifPresent {contact ->
            new Sql<>(Contact.class).delete(it, contact)
        }
}
```

```sql:SQL
-- Executed SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE id=1
DELETE FROM Contact WHERE id=1
```


<div id="ExecuteSQL-delete-N"></div>

#### 5-4-2. DELETE multiple rows

```java:Java
// Example in Java
Transaction.execute(connection -> {
    List<Contact> contacts = new ArrayList<>();
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Pomme")
        .select(connection, contacts::add);
    new Sql<>(Contact.class).delete(connection, contacts);
});
```

```groovy:Groovy
// Example in Groovy
Transaction.execute {
    def contacts = []
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Pomme')
        .select(it, {contacts << it})
    new Sql<>(Contact.class).delete(it, contacts)
}
```

```sql:SQL
-- Executed SQL
SELECT id, familyName, givenName, birthday, updateCount, createdTime, updatedTime FROM Contact WHERE familyName='Pomme'
DELETE FROM Contact WHERE id=2
DELETE FROM Contact WHERE id=3
```

<div id="ExecuteSQL-delete-Condition"></div>

#### 5-4-3. DELETE with a Condition

```java:Java
// Example in Java
Transaction.execute(connection ->
    new Sql<>(Contact.class)
        .where("{familyName}={}", "Orange")
        .delete(connection)
);
```

```groovy:Groovy
// Example in Groovy
Transaction.execute {
    new Sql<>(Contact.class)
        .where('{familyName}={}', 'Orange')
        .delete(it)
}
```

```sql:SQL
-- Executed SQL
DELETE FROM Contact WHERE familyName='Orange'
```

<div id="ExecuteSQL-delete-all"></div>

#### 5-4-4. DELETE all rows

```java:Java
// Example in Java
Transaction.execute(connection ->
    new Sql<>(Phone.class)
        .where(Condition.ALL)
        .delete(connection)
);
```

```groovy:Groovy
// Example in Groovy
Transaction.execute {
    new Sql<>(Phone.class)
        .where(Condition.ALL)
        .delete(it)
}
```

```sql:SQL
-- Executed SQL
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
|`{xxx}`|The column name associated with property `xxx`|
|`{A.xxx}`|`"A."` + The column name associated with property `xxx` (`A` is a table alias)|
|`{A_xxx}`|The column alias associated with table alias `A` and `xxx` property|
|`{#xxx}`|The value of property `xxx` of an entity set on the `Sql` object (or an entity argument of `Sql#insert` or `Sql#update` method)|

<div style="text-align:center; margin-top:20px"><i>&copy; 2016 Masato Kokubo</i></div>
