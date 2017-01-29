Lightsleep
===========

Lightsleep is a database persistence library of lightweight. It can be used in Java 8.

#### Features

- Lightsleep is not an implementation of Persistence API of J2EE. It is a new API using the lambda function and Optional class that was introduced in Java 8.
- Libraries other than the Java runtime is not required, it can also be used from Java programs, such as batch processing.
- XML configuration file is not required.
- Because of the small number of classes and methods that have been implemented, it is easy to learn.

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

[Japanese](README_ja.md)
