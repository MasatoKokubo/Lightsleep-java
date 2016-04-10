Lightsleep
===========

Lightsleep はデータベースへの永続化ライブラリです。
Persistence API とは異なるアプローチをしています。
Java 8 より導入された関数型プログラミングを取り入れているため、Java 7 以前はサポートしません。
実装されているクラス数が比較的少ないため、学習が容易です。

#### 対応DBMS

* MySQL
* Oracle Database
* PostgreSQL
* SQL Server
* 標準SQL準拠DBMS

#### 使用例

```java
Transaction.execute(connection -> {
    Person person = new Sql<>(Person.class)
        .where("{name.last } = {}", "Kokubo")
          .and("{name.first} = {}", "Masato")
        .select(connection).orElseThrow(() -> {throw new NotFoundException();});
});
```

#### ライセンス

The MIT License (MIT)

*&copy; 2016 小久保 雅人*

[English](README.md)
