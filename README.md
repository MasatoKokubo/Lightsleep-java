Lightsleep
===========

Lightsleep is a lightweight Object-Relational mapping library and is available in Java 8. It does not work in Java 7 or earlier.
It is not compatible with the Java Persistence API (JPA).

#### Features

- Has APIs using features added in Java 8 (functional interface and Optional class).
- It is easy to understand intuitively because method names resemble reserved words in SQL.
- It is easy to use for batch programs because there is no library dependent on Java runtime and JDBC driver.
- No mapping definition file such as XML file is necessary.
- Learning is relatively easy because it is not a large library.

#### Supported DBMS

- MySQL
- Oracle Database
- PostgreSQL
- SQLite (since 1.7.0)
- SQL Server
- DBMSs that conforms to the standard SQL

#### Description example in *build.gradle*

	repositories {
	    jcenter()
	}

	dependencies {
	    compile 'org.lightsleep:lightsleep:1.+'
	}

#### License

The MIT License (MIT)

*&copy; 2016 Masato Kokubo*

[Tutorial](Tutorial.md)

[Manual](Manual.md)

[API Specification](http://masatokokubo.github.io/Lightsleep/javadoc/index.html)

[Japanese](README_ja.md)
