// ConnectionSupplierSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.connection

import java.sql.Connection
import java.sql.Date
import java.util.function.Consumer
import javax.naming.Context
import javax.naming.InitialContext
import javax.sql.DataSource
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
	def cleanupSpec() {
		System.properties.remove('lightsleep.resource')
		Resource.initClass()
		AbstractConnectionSupplier.initClass()
	/**/DebugTrace.print("cleanupSpec: AbstractConnectionSupplier.supplierMap.keySet", AbstractConnectionSupplier.supplierMap.keySet())
	}

	def "ConnectionSupplier of #propertiesName / #keywords"(String propertiesName, List<String> keywords,
		Class<? extends ConnectionSupplier> supplierClass, Class<? extends Database> databaseClass) {
		/**/DebugTrace.print("propertiesName", propertiesName)
		setup:
			System.properties.setProperty('lightsleep.resource', propertiesName)
			Resource.initClass()
			AbstractConnectionSupplier.initClass()
		/**/DebugTrace.print("setup: AbstractConnectionSupplier.supplierMap.keySet", AbstractConnectionSupplier.supplierMap.keySet())

		when:
			def supplier = ConnectionSupplier.find(keywords as String[])

		then:
			supplier.getClass() == supplierClass
			supplier.database.getClass() == databaseClass

		where:
			propertiesName                    |keywords                                   |supplierClass|databaseClass
			'test/lightsleep-C3p0-url'        |[                                         ]|C3p0         |DB2
			'test/lightsleep-C3p0-url'        |[':db2:'                                  ]|C3p0         |DB2
			'test/lightsleep-C3p0-url'        |[':db2:'       , '/db2-10:'               ]|C3p0         |DB2
			'test/lightsleep-C3p0-url'        |[':db2:'       , '/db2-10:'     , '/test1']|C3p0         |DB2

			'test/lightsleep-Dbcp-urls'       |[':db2:'       , '/db2-10:'     , '/test1']|Dbcp         |DB2
			'test/lightsleep-Dbcp-urls'       |[':db2:'       , '/db2-10:'     , '/test2']|Dbcp         |DB2
			'test/lightsleep-Dbcp-urls'       |[':db2:'       , '/db2-11:'     , '/test1']|Dbcp         |DB2
			'test/lightsleep-Dbcp-urls'       |[':db2:'       , '/db2-11:'     , '/test2']|Dbcp         |DB2
			'test/lightsleep-Dbcp-urls'       |[':mysql:'     , '/mysql56/'    , '/test1']|Dbcp         |MySQL
			'test/lightsleep-Dbcp-urls'       |[':mysql:'     , '/mysql56/'    , '/test2']|Dbcp         |MySQL
			'test/lightsleep-Dbcp-urls'       |[':mysql:'     , '/mysql57/'    , '/test1']|Dbcp         |MySQL
			'test/lightsleep-Dbcp-urls'       |[':mysql:'     , '/mysql57/'    , '/test2']|Dbcp         |MySQL
			'test/lightsleep-Dbcp-urls'       |[':oracle:'    , '@oracle120:'  , ':test1']|Dbcp         |Oracle
			'test/lightsleep-Dbcp-urls'       |[':oracle:'    , '@oracle120:'  , ':test2']|Dbcp         |Oracle
			'test/lightsleep-Dbcp-urls'       |[':oracle:'    , '@oracle121:'  , ':test1']|Dbcp         |Oracle
			'test/lightsleep-Dbcp-urls'       |[':oracle:'    , '@oracle121:'  , ':test2']|Dbcp         |Oracle
			'test/lightsleep-Dbcp-urls'       |[':postgresql:', '/postgres95/' , '/test1']|Dbcp         |PostgreSQL
			'test/lightsleep-Dbcp-urls'       |[':postgresql:', '/postgres95/' , '/test2']|Dbcp         |PostgreSQL
			'test/lightsleep-Dbcp-urls'       |[':postgresql:', '/postgres96/' , '/test1']|Dbcp         |PostgreSQL
			'test/lightsleep-Dbcp-urls'       |[':postgresql:', '/postgres96/' , '/test2']|Dbcp         |PostgreSQL
			'test/lightsleep-Dbcp-urls'       |[':sqlite:'    ,                  '/test1']|Dbcp         |SQLite
			'test/lightsleep-Dbcp-urls'       |[':sqlite:'    ,                  '/test2']|Dbcp         |SQLite
			'test/lightsleep-Dbcp-urls'       |[':sqlserver:' , '/sqlserver12;', '=test1']|Dbcp         |SQLServer
			'test/lightsleep-Dbcp-urls'       |[':sqlserver:' , '/sqlserver12;', '=test2']|Dbcp         |SQLServer
			'test/lightsleep-Dbcp-urls'       |[':sqlserver:' , '/sqlserver13;', '=test1']|Dbcp         |SQLServer
			'test/lightsleep-Dbcp-urls'       |['Jdbc'        , ':abc:'                  ]|Jdbc         |Standard

			'test/lightsleep-HikariCP-urls'   |[':db2:'       , '/db2-10:'     , '/test1']|HikariCP     |DB2
			'test/lightsleep-HikariCP-urls'   |[':db2:'       , '/db2-10:'     , '/test2']|HikariCP     |DB2
			'test/lightsleep-HikariCP-urls'   |[':db2:'       , '/db2-11:'     , '/test1']|HikariCP     |DB2
			'test/lightsleep-HikariCP-urls'   |[':db2:'       , '/db2-11:'     , '/test2']|HikariCP     |DB2
			'test/lightsleep-HikariCP-urls'   |[':mysql:'     , '/mysql56/'    , '/test1']|HikariCP     |MySQL
			'test/lightsleep-HikariCP-urls'   |[':mysql:'     , '/mysql56/'    , '/test2']|HikariCP     |MySQL
			'test/lightsleep-HikariCP-urls'   |[':mysql:'     , '/mysql57/'    , '/test1']|HikariCP     |MySQL
			'test/lightsleep-HikariCP-urls'   |[':mysql:'     , '/mysql57/'    , '/test2']|HikariCP     |MySQL
			'test/lightsleep-HikariCP-urls'   |[':oracle:'    , '@oracle120:'  , ':test1']|HikariCP     |Oracle
			'test/lightsleep-HikariCP-urls'   |[':oracle:'    , '@oracle120:'  , ':test2']|HikariCP     |Oracle
			'test/lightsleep-HikariCP-urls'   |[':oracle:'    , '@oracle121:'  , ':test1']|HikariCP     |Oracle
			'test/lightsleep-HikariCP-urls'   |[':oracle:'    , '@oracle121:'  , ':test2']|HikariCP     |Oracle
			'test/lightsleep-HikariCP-urls'   |[':postgresql:', '/postgres95/' , '/test1']|HikariCP     |PostgreSQL
			'test/lightsleep-HikariCP-urls'   |[':postgresql:', '/postgres95/' , '/test2']|HikariCP     |PostgreSQL
			'test/lightsleep-HikariCP-urls'   |[':postgresql:', '/postgres96/' , '/test1']|HikariCP     |PostgreSQL
			'test/lightsleep-HikariCP-urls'   |[':postgresql:', '/postgres96/' , '/test2']|HikariCP     |PostgreSQL
			'test/lightsleep-HikariCP-urls'   |[':sqlite:'    ,                  '/test1']|HikariCP     |SQLite
			'test/lightsleep-HikariCP-urls'   |[':sqlite:'    ,                  '/test2']|HikariCP     |SQLite
			'test/lightsleep-HikariCP-urls'   |[':sqlserver:' , '/sqlserver12;', '=test1']|HikariCP     |SQLServer
			'test/lightsleep-HikariCP-urls'   |[':sqlserver:' , '/sqlserver12;', '=test2']|HikariCP     |SQLServer
			'test/lightsleep-HikariCP-urls'   |[':sqlserver:' , '/sqlserver13;', '=test1']|HikariCP     |SQLServer
			'test/lightsleep-HikariCP-urls'   |[':sqlserver:' , '/sqlserver13;', '=test2']|HikariCP     |SQLServer

			'test/lightsleep-Jdbc-url'        |[                                         ]|Jdbc         |DB2
			'test/lightsleep-Jdbc-url'        |[':db2:'       , '/db2-10:'     , '/test1']|Jdbc         |DB2
			'test/lightsleep-TomcatCP-url'    |[                                         ]|TomcatCP     |DB2
			'test/lightsleep-TomcatCP-url'    |[':db2:'       , '/db2-10:'     , '/test1']|TomcatCP     |DB2
			'test/lightsleep--url'            |[                                         ]|Jdbc         |DB2
			'test/lightsleep--url'            |[':db2:'       , '/db2-10:'     , '/test1']|Jdbc         |DB2

			'test/lightsleep-Jndi-dataSource' |[                                         ]|Jndi         |DB2
			'test/lightsleep-Jndi-dataSource' |['db2-10_test1'                           ]|Jndi         |DB2

			'test/lightsleep-Jndi-dataSources'|['db2-11_test1'                           ]|Jndi         |DB2
			'test/lightsleep-Jndi-dataSources'|['mysql57_test1'                          ]|Jndi         |MySQL
			'test/lightsleep-Jndi-dataSources'|['oracle121_test1'                        ]|Jndi         |Oracle
			'test/lightsleep-Jndi-dataSources'|['postgres96_test1'                       ]|Jndi         |PostgreSQL
			'test/lightsleep-Jndi-dataSources'|['sqlite_test1'                           ]|Jndi         |SQLite
			'test/lightsleep-Jndi-dataSources'|['sqlserver13_test1'                      ]|Jndi         |SQLServer
	}
}
