Lightsleep
===========

Lightsleep は、軽量のデータベース永続化ライブラリで、Java 8 以降に対応しています。

#### 特徴

- J2EE の Persistence API の実装ではなく、Java 8で取り入れられたラムダ関数、Optional クラスを使用した API です。
- Java ランタイム以外のライブラリを必要としないので、バッチ処理などのJava プログラムからも利用可能です。
- XML ファイルによる設定が不要です。
- 実装されているクラス/メソッド数が少ないため、学習が容易です。

#### 対応DBMS

- MySQL
- Oracle Database
- PostgreSQL
- SQL Server
- 標準SQL準拠DBMS

#### *build.gradle* での記述例

	repositories {
	    jcenter()
	}

	dependencies {
	    compile 'org.lightsleep:lightsleep:1.+'
	}


#### ライセンス

The MIT License (MIT)

*&copy; 2016 Masato Kokubo (小久保 雅人)*

[チュートリアル](Tutorial_ja.md)

[マニュアル](Manual_ja.md)

[English](README.md)
