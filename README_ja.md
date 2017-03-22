Lightsleep
===========

Lightsleep は、軽量の O/R マッピング・ライブラリで、Java 8 で利用できます。Java 7 以前には対応していません。
また Java Persistence API (JPA) との互換性はありません。

#### 特徴

- Java 8 で追加された機能 (関数型インタフェース、Optional クラス) を使用した API 。
- メソッド名を SQL の予約語に似せてあるため、直観的に理解しやすい。
- Java Runtime と JDBC ドライバー以外に依存するライブラリがないため、バッチ処理にも使用しやすい。
- XML ファイル等によるマッピング定義ファイルは不要。
- 大規模なライブラリではないため、学習が比較的容易。

#### 対応DBMS

- MySQL
- Oracle Database
- PostgreSQL
- SQLite (since 1.7.0)
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

[API 仕様](http://masatokokubo.github.io/Lightsleep/javadoc_ja/index.html)

Qiita 記事
- <a href="http://qiita.com/MasatoKokubo/items/ab46696b203d7f67036c" target="_blank">Java Runtime と JDBC ドライバーだけで動作する O/R マッピング・ライブラリ Lightsleep の紹介</a>
- <a href="http://qiita.com/MasatoKokubo/items/1080d1277e2b51d88f89" target="_blank">Java 8 用 O/R マッピング・ライブラリ Lightsleep の柔軟なデータ型変換の仕組み</a>

[English](README.md)
