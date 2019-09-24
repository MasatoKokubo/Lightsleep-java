// ConnectionSupplierSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.connection

import java.sql.Connection
import java.sql.DatabaseMetaData
import javax.sql.DataSource
import javax.naming.Context
import javax.naming.InitialContext
import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.connection.*
import org.lightsleep.database.*
import org.lightsleep.helper.*
import org.lightsleep.test.entity.*

import spock.lang.*

// ConnectionSupplierSpec
@Unroll
class ConnectionSupplierSpec extends Specification {
	static binds = [
		'java:comp/env/jdbc/db2_test1'       : 'jdbc:db2://db2:50000/test1',
		'java:comp/env/jdbc/mariadb_test1'   : 'jdbc:mariadb://mariadb:3306/test1',
		'java:comp/env/jdbc/mysql_test1'     : 'jdbc:mysql://mysql:3306/test1',
		'java:comp/env/jdbc/oracle_test1'    : 'jdbc:oracle:thin:@oracle122:1521:test1',
		'java:comp/env/jdbc/postgresql_test1': 'jdbc:postgresql://postgresql:5432/test1',
		'java:comp/env/jdbc/sqlite_test1'    : 'jdbc:sqlite:C:/sqlite/',
		'java:comp/env/jdbc/sqlserver_test1' : 'jdbc:sqlserver://sqlserver:1433;database=test1',
	]

	def setupSpec() {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, 'org.apache.naming.java.javaURLContextFactory')

		def ic = new InitialContext()
		ic.createSubcontext('java:')
		ic.createSubcontext('java:comp')
		ic.createSubcontext('java:comp/env')
		ic.createSubcontext('java:comp/env/jdbc')

		binds.each {
			def metaData = Stub(DatabaseMetaData)
			metaData.URL >> it.value

			def connection = Stub(Connection)
			connection.metaData >> metaData

			def dataSource = Stub(DataSource)
			dataSource.connection >> connection

			ic.bind(it.key, dataSource)
		}
	}

	def cleanupSpec() {
		System.properties.remove('lightsleep.resource')
		Resource.initClass()
		AbstractConnectionSupplier.initClass()
	}

	def "ConnectionSupplier find #propertiesName / #keywords"(String propertiesName, List<String> keywords,
		Class<? extends ConnectionSupplier> supplierClass, Database database) {
		setup:
			System.properties.setProperty('lightsleep.resource', propertiesName)
			Resource.initClass()
			AbstractConnectionSupplier.initClass()

		when:
			def supplier = ConnectionSupplier.find(keywords as String[])

		then:
			supplier.getClass() == supplierClass
			supplier.database == database

		where:
			propertiesName                 |keywords                                       |supplierClass|database
			'test/lightsleep-C3p0-url'     |[                                             ]|C3p0         |Db2.instance
			'test/lightsleep-C3p0-url'     |[':db2:'                                      ]|C3p0         |Db2.instance
			'test/lightsleep-C3p0-url'     |[':db2:'       , '/db2:50000'                 ]|C3p0         |Db2.instance
			'test/lightsleep-C3p0-url'     |[':db2:'       , '/db2:50000'       , '/test1']|C3p0         |Db2.instance
		
			'test/lightsleep-Dbcp-urls'    |[':db2:'       , '/db2:50000'       , '/test1']|Dbcp         |Db2.instance
			'test/lightsleep-Dbcp-urls'    |[':db2:'       , '/db2:50000'       , '/test2']|Dbcp         |Db2.instance
			'test/lightsleep-Dbcp-urls'    |[':db2:'       , '/db2:50001'       , '/test1']|Dbcp         |Db2.instance
			'test/lightsleep-Dbcp-urls'    |[':db2:'       , '/db2:50001'       , '/test2']|Dbcp         |Db2.instance
			'test/lightsleep-Dbcp-urls'    |[':mariadb:'   , '/mariadb:3306'    , '/test1']|Dbcp         |MariaDB.instance
			'test/lightsleep-Dbcp-urls'    |[':mariadb:'   , '/mariadb:3306'    , '/test2']|Dbcp         |MariaDB.instance
			'test/lightsleep-Dbcp-urls'    |[':mariadb:'   , '/mariadb:3307'    , '/test1']|Dbcp         |MariaDB.instance
			'test/lightsleep-Dbcp-urls'    |[':mariadb:'   , '/mariadb:3307'    , '/test2']|Dbcp         |MariaDB.instance
			'test/lightsleep-Dbcp-urls'    |[':mysql:'     , '/mysql:3306'      , '/test1']|Dbcp         |MySQL.instance
			'test/lightsleep-Dbcp-urls'    |[':mysql:'     , '/mysql:3306'      , '/test2']|Dbcp         |MySQL.instance
			'test/lightsleep-Dbcp-urls'    |[':mysql:'     , '/mysql:3307'      , '/test1']|Dbcp         |MySQL.instance
			'test/lightsleep-Dbcp-urls'    |[':mysql:'     , '/mysql:3307'      , '/test2']|Dbcp         |MySQL.instance
			'test/lightsleep-Dbcp-urls'    |[':oracle:'    , '@oracle121'       , ':test1']|Dbcp         |Oracle.instance
			'test/lightsleep-Dbcp-urls'    |[':oracle:'    , '@oracle121'       , ':test2']|Dbcp         |Oracle.instance
			'test/lightsleep-Dbcp-urls'    |[':oracle:'    , '@oracle122'       , ':test1']|Dbcp         |Oracle.instance
			'test/lightsleep-Dbcp-urls'    |[':oracle:'    , '@oracle122'       , ':test2']|Dbcp         |Oracle.instance
			'test/lightsleep-Dbcp-urls'    |[':postgresql:', '/postgresql:5432' , '/test1']|Dbcp         |PostgreSQL.instance
			'test/lightsleep-Dbcp-urls'    |[':postgresql:', '/postgresql:5432' , '/test2']|Dbcp         |PostgreSQL.instance
			'test/lightsleep-Dbcp-urls'    |[':postgresql:', '/postgresql:5433' , '/test1']|Dbcp         |PostgreSQL.instance
			'test/lightsleep-Dbcp-urls'    |[':postgresql:', '/postgresql:5433' , '/test2']|Dbcp         |PostgreSQL.instance
			'test/lightsleep-Dbcp-urls'    |[':sqlite:'    ,                      '/test1']|Dbcp         |SQLite.instance
			'test/lightsleep-Dbcp-urls'    |[':sqlite:'    ,                      '/test2']|Dbcp         |SQLite.instance
			'test/lightsleep-Dbcp-urls'    |[':sqlserver:' , '/sqlserver:1433'  , '=test1']|Dbcp         |SQLServer.instance
			'test/lightsleep-Dbcp-urls'    |[':sqlserver:' , '/sqlserver:1433'  , '=test2']|Dbcp         |SQLServer.instance
			'test/lightsleep-Dbcp-urls'    |[':sqlserver:' , '/sqlserver:1434'  , '=test1']|Dbcp         |SQLServer.instance
			'test/lightsleep-Dbcp-urls'    |[':sqlserver:' , '/sqlserver:1434'  , '=test1']|Dbcp         |SQLServer.instance
			'test/lightsleep-Dbcp-urls'    |['Jdbc'        , ':abc:'                      ]|Jdbc         |Standard.instance

			'test/lightsleep-HikariCP-urls'|[':db2:'       , '/db2:50000'       , '/test1']|HikariCP     |Db2.instance
			'test/lightsleep-HikariCP-urls'|[':db2:'       , '/db2:50000'       , '/test2']|HikariCP     |Db2.instance
			'test/lightsleep-HikariCP-urls'|[':db2:'       , '/db2:50001'       , '/test1']|HikariCP     |Db2.instance
			'test/lightsleep-HikariCP-urls'|[':db2:'       , '/db2:50001'       , '/test2']|HikariCP     |Db2.instance
			'test/lightsleep-HikariCP-urls'|[':mariadb:'   , '/mariadb:3306'    , '/test1']|HikariCP     |MariaDB.instance
			'test/lightsleep-HikariCP-urls'|[':mariadb:'   , '/mariadb:3306'    , '/test2']|HikariCP     |MariaDB.instance
			'test/lightsleep-HikariCP-urls'|[':mariadb:'   , '/mariadb:3307'    , '/test1']|HikariCP     |MariaDB.instance
			'test/lightsleep-HikariCP-urls'|[':mariadb:'   , '/mariadb:3307'    , '/test2']|HikariCP     |MariaDB.instance
			'test/lightsleep-HikariCP-urls'|[':mysql:'     , '/mysql:3306'      , '/test1']|HikariCP     |MySQL.instance
			'test/lightsleep-HikariCP-urls'|[':mysql:'     , '/mysql:3306'      , '/test2']|HikariCP     |MySQL.instance
			'test/lightsleep-HikariCP-urls'|[':mysql:'     , '/mysql:3307'      , '/test1']|HikariCP     |MySQL.instance
			'test/lightsleep-HikariCP-urls'|[':mysql:'     , '/mysql:3307'      , '/test2']|HikariCP     |MySQL.instance
			'test/lightsleep-HikariCP-urls'|[':oracle:'    , '@oracle121'       , ':test1']|HikariCP     |Oracle.instance
			'test/lightsleep-HikariCP-urls'|[':oracle:'    , '@oracle121'       , ':test2']|HikariCP     |Oracle.instance
			'test/lightsleep-HikariCP-urls'|[':oracle:'    , '@oracle122'       , ':test1']|HikariCP     |Oracle.instance
			'test/lightsleep-HikariCP-urls'|[':oracle:'    , '@oracle122'       , ':test2']|HikariCP     |Oracle.instance
			'test/lightsleep-HikariCP-urls'|[':postgresql:', '/postgresql:5432' , '/test1']|HikariCP     |PostgreSQL.instance
			'test/lightsleep-HikariCP-urls'|[':postgresql:', '/postgresql:5432' , '/test2']|HikariCP     |PostgreSQL.instance
			'test/lightsleep-HikariCP-urls'|[':postgresql:', '/postgresql:5433' , '/test1']|HikariCP     |PostgreSQL.instance
			'test/lightsleep-HikariCP-urls'|[':postgresql:', '/postgresql:5433' , '/test2']|HikariCP     |PostgreSQL.instance
			'test/lightsleep-HikariCP-urls'|[':sqlite:'    ,                      '/test1']|HikariCP     |SQLite.instance
			'test/lightsleep-HikariCP-urls'|[':sqlite:'    ,                      '/test2']|HikariCP     |SQLite.instance
			'test/lightsleep-HikariCP-urls'|[':sqlserver:' , '/sqlserver:1433'  , '=test1']|HikariCP     |SQLServer.instance
			'test/lightsleep-HikariCP-urls'|[':sqlserver:' , '/sqlserver:1433'  , '=test2']|HikariCP     |SQLServer.instance
			'test/lightsleep-HikariCP-urls'|[':sqlserver:' , '/sqlserver:1434'  , '=test1']|HikariCP     |SQLServer.instance
			'test/lightsleep-HikariCP-urls'|[':sqlserver:' , '/sqlserver:1434'  , '=test2']|HikariCP     |SQLServer.instance

			'test/lightsleep-Jdbc-url'     |[                                             ]|Jdbc         |Db2.instance
			'test/lightsleep-Jdbc-url'     |[':db2:'       , '/db2:50000'       , '/test1']|Jdbc         |Db2.instance
			'test/lightsleep-TomcatCP-url' |[                                             ]|TomcatCP     |Db2.instance
			'test/lightsleep-TomcatCP-url' |[':db2:'       , '/db2:50000'       , '/test1']|TomcatCP     |Db2.instance
			'test/lightsleep--url'         |[                                             ]|Jdbc         |Db2.instance
			'test/lightsleep--url'         |[':db2:'       , '/db2:50000'       , '/test1']|Jdbc         |Db2.instance
	}

	def "ConnectionSupplier find Jndi #propertiesName / #keywords"(String propertiesName, List<String> keywords,
		Class<? extends ConnectionSupplier> supplierClass, Database database) {
		setup:
			System.properties.setProperty('lightsleep.resource', propertiesName)
			Resource.initClass()
			AbstractConnectionSupplier.initClass()

		when:
			def supplier = ConnectionSupplier.find(keywords as String[])

		then:
			supplier.getClass() == supplierClass
			supplier.database == Standard.instance

		when:
			def connectionWrapper = supplier.get()

		then:
			supplier.database == database
			connectionWrapper.database == database

		where:
			propertiesName                    |keywords            |supplierClass|database
			'test/lightsleep-Jndi-dataSource' |[                  ]|Jndi         |Db2.instance
			'test/lightsleep-Jndi-dataSource' |['db2_test1'       ]|Jndi         |Db2.instance
			'test/lightsleep-Jndi-dataSources'|['mariadb_test1'   ]|Jndi         |MariaDB.instance
			'test/lightsleep-Jndi-dataSources'|['mysql_test1'     ]|Jndi         |MySQL.instance
			'test/lightsleep-Jndi-dataSources'|['oracle_test1'    ]|Jndi         |Oracle.instance
			'test/lightsleep-Jndi-dataSources'|['postgresql_test1']|Jndi         |PostgreSQL.instance
			'test/lightsleep-Jndi-dataSources'|['sqlite_test1'    ]|Jndi         |SQLite.instance
			'test/lightsleep-Jndi-dataSources'|['sqlserver_test1' ]|Jndi         |SQLServer.instance
	}
}
