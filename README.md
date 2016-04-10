Lightsleep
===========

Lightsleep is a class library for the persistence of the database.
It is a different approach from the Persistence API.
It does not support Java 7 previously because of incorporating functional programming introduced from Java 8.
It is easy to learn because of the number of classes is small.

#### Supported DBMS

* MySQL
* Oracle Database
* PostgreSQL
* SQL Server
* DBMSs that conforms to the standard SQL

#### Example

```java
Transaction.execute(connection -> {
    Person person = new Sql<>(Person.class)
        .where("{name.last } = {}", "Kokubo")
          .and("{name.first} = {}", "Masato")
        .select(connection).orElseThrow(() -> {throw new NotFoundException();});
});
```

#### License

The MIT License (MIT)

*&copy; 2016 Masato Kokubo*

[Japanese](README_ja.md)
