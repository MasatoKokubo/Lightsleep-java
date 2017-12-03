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
		'java:comp/env/jdbc/db2-10_test1'     : 'jdbc:db2://db2-10:50000/test1',
		'java:comp/env/jdbc/db2-11_test1'     : 'jdbc:db2://db2-11:50000/test1',
		'java:comp/env/jdbc/mysql57_test1'    : 'jdbc:mysql://mysql57:3306/test1',
		'java:comp/env/jdbc/oracle121_test1'  : 'jdbc:oracle:thin:@oracle121:1521:test1',
		'java:comp/env/jdbc/postgresql9_test1': 'jdbc:postgresql://postgresql9:5433/test1',
		'java:comp/env/jdbc/sqlite_test1'     : 'jdbc:sqlite:C:/sqlite/',
		'java:comp/env/jdbc/sqlserver13_test1': 'jdbc:sqlserver://sqlserver13:1433;database=test1',
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
			propertiesName                    |keywords                                       |supplierClass|database
			'test/lightsleep-C3p0-url'        |[                                             ]|C3p0         |DB2.instance
			'test/lightsleep-C3p0-url'        |[':db2:'                                      ]|C3p0         |DB2.instance
			'test/lightsleep-C3p0-url'        |[':db2:'       , '/db2-10'                    ]|C3p0         |DB2.instance
			'test/lightsleep-C3p0-url'        |[':db2:'       , '/db2-10'          , '/test1']|C3p0         |DB2.instance

			'test/lightsleep-Dbcp-urls'       |[':db2:'       , '/db2-10'          , '/test1']|Dbcp         |DB2.instance
			'test/lightsleep-Dbcp-urls'       |[':db2:'       , '/db2-10'          , '/test2']|Dbcp         |DB2.instance
			'test/lightsleep-Dbcp-urls'       |[':db2:'       , '/db2-11'          , '/test1']|Dbcp         |DB2.instance
			'test/lightsleep-Dbcp-urls'       |[':db2:'       , '/db2-11'          , '/test2']|Dbcp         |DB2.instance
			'test/lightsleep-Dbcp-urls'       |[':mysql:'     , '/mysql56'         , '/test1']|Dbcp         |MySQL.instance
			'test/lightsleep-Dbcp-urls'       |[':mysql:'     , '/mysql56'         , '/test2']|Dbcp         |MySQL.instance
			'test/lightsleep-Dbcp-urls'       |[':mysql:'     , '/mysql57'         , '/test1']|Dbcp         |MySQL.instance
			'test/lightsleep-Dbcp-urls'       |[':mysql:'     , '/mysql57'         , '/test2']|Dbcp         |MySQL.instance
			'test/lightsleep-Dbcp-urls'       |[':oracle:'    , '@oracle120'       , ':test1']|Dbcp         |Oracle.instance
			'test/lightsleep-Dbcp-urls'       |[':oracle:'    , '@oracle120'       , ':test2']|Dbcp         |Oracle.instance
			'test/lightsleep-Dbcp-urls'       |[':oracle:'    , '@oracle121'       , ':test1']|Dbcp         |Oracle.instance
			'test/lightsleep-Dbcp-urls'       |[':oracle:'    , '@oracle121'       , ':test2']|Dbcp         |Oracle.instance
			'test/lightsleep-Dbcp-urls'       |[':postgresql:', '/postgresql9:5432', '/test1']|Dbcp         |PostgreSQL.instance
			'test/lightsleep-Dbcp-urls'       |[':postgresql:', '/postgresql9:5432', '/test2']|Dbcp         |PostgreSQL.instance
			'test/lightsleep-Dbcp-urls'       |[':postgresql:', '/postgresql9:5433', '/test1']|Dbcp         |PostgreSQL.instance
			'test/lightsleep-Dbcp-urls'       |[':postgresql:', '/postgresql9:5433', '/test2']|Dbcp         |PostgreSQL.instance
			'test/lightsleep-Dbcp-urls'       |[':sqlite:'    ,                      '/test1']|Dbcp         |SQLite.instance
			'test/lightsleep-Dbcp-urls'       |[':sqlite:'    ,                      '/test2']|Dbcp         |SQLite.instance
			'test/lightsleep-Dbcp-urls'       |[':sqlserver:' , '/sqlserver12'     , '=test1']|Dbcp         |SQLServer.instance
			'test/lightsleep-Dbcp-urls'       |[':sqlserver:' , '/sqlserver12'     , '=test2']|Dbcp         |SQLServer.instance
			'test/lightsleep-Dbcp-urls'       |[':sqlserver:' , '/sqlserver13'     , '=test1']|Dbcp         |SQLServer.instance
			'test/lightsleep-Dbcp-urls'       |['Jdbc'        , ':abc:'                      ]|Jdbc         |Standard.instance

			'test/lightsleep-HikariCP-urls'   |[':db2:'       , '/db2-10'          , '/test1']|HikariCP     |DB2.instance
			'test/lightsleep-HikariCP-urls'   |[':db2:'       , '/db2-10'          , '/test2']|HikariCP     |DB2.instance
			'test/lightsleep-HikariCP-urls'   |[':db2:'       , '/db2-11'          , '/test1']|HikariCP     |DB2.instance
			'test/lightsleep-HikariCP-urls'   |[':db2:'       , '/db2-11'          , '/test2']|HikariCP     |DB2.instance
			'test/lightsleep-HikariCP-urls'   |[':mysql:'     , '/mysql56'         , '/test1']|HikariCP     |MySQL.instance
			'test/lightsleep-HikariCP-urls'   |[':mysql:'     , '/mysql56'         , '/test2']|HikariCP     |MySQL.instance
			'test/lightsleep-HikariCP-urls'   |[':mysql:'     , '/mysql57'         , '/test1']|HikariCP     |MySQL.instance
			'test/lightsleep-HikariCP-urls'   |[':mysql:'     , '/mysql57'         , '/test2']|HikariCP     |MySQL.instance
			'test/lightsleep-HikariCP-urls'   |[':oracle:'    , '@oracle120'       , ':test1']|HikariCP     |Oracle.instance
			'test/lightsleep-HikariCP-urls'   |[':oracle:'    , '@oracle120'       , ':test2']|HikariCP     |Oracle.instance
			'test/lightsleep-HikariCP-urls'   |[':oracle:'    , '@oracle121'       , ':test1']|HikariCP     |Oracle.instance
			'test/lightsleep-HikariCP-urls'   |[':oracle:'    , '@oracle121'       , ':test2']|HikariCP     |Oracle.instance
			'test/lightsleep-HikariCP-urls'   |[':postgresql:', '/postgresql9:5432', '/test1']|HikariCP     |PostgreSQL.instance
			'test/lightsleep-HikariCP-urls'   |[':postgresql:', '/postgresql9:5432', '/test2']|HikariCP     |PostgreSQL.instance
			'test/lightsleep-HikariCP-urls'   |[':postgresql:', '/postgresql9:5433', '/test1']|HikariCP     |PostgreSQL.instance
			'test/lightsleep-HikariCP-urls'   |[':postgresql:', '/postgresql9:5433', '/test2']|HikariCP     |PostgreSQL.instance
			'test/lightsleep-HikariCP-urls'   |[':sqlite:'    ,                      '/test1']|HikariCP     |SQLite.instance
			'test/lightsleep-HikariCP-urls'   |[':sqlite:'    ,                      '/test2']|HikariCP     |SQLite.instance
			'test/lightsleep-HikariCP-urls'   |[':sqlserver:' , '/sqlserver12'     , '=test1']|HikariCP     |SQLServer.instance
			'test/lightsleep-HikariCP-urls'   |[':sqlserver:' , '/sqlserver12'     , '=test2']|HikariCP     |SQLServer.instance
			'test/lightsleep-HikariCP-urls'   |[':sqlserver:' , '/sqlserver13'     , '=test1']|HikariCP     |SQLServer.instance
			'test/lightsleep-HikariCP-urls'   |[':sqlserver:' , '/sqlserver13'     , '=test2']|HikariCP     |SQLServer.instance

			'test/lightsleep-Jdbc-url'        |[                                             ]|Jdbc         |DB2.instance
			'test/lightsleep-Jdbc-url'        |[':db2:'       , '/db2-10'          , '/test1']|Jdbc         |DB2.instance
			'test/lightsleep-TomcatCP-url'    |[                                             ]|TomcatCP     |DB2.instance
			'test/lightsleep-TomcatCP-url'    |[':db2:'       , '/db2-10'          , '/test1']|TomcatCP     |DB2.instance
			'test/lightsleep--url'            |[                                             ]|Jdbc         |DB2.instance
			'test/lightsleep--url'            |[':db2:'       , '/db2-10'          , '/test1']|Jdbc         |DB2.instance
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
			propertiesName                    |keywords             |supplierClass|database
			'test/lightsleep-Jndi-dataSource' |[                   ]|Jndi         |DB2.instance
			'test/lightsleep-Jndi-dataSource' |['db2-10_test1'     ]|Jndi         |DB2.instance
			'test/lightsleep-Jndi-dataSources'|['db2-11_test1'     ]|Jndi         |DB2.instance
			'test/lightsleep-Jndi-dataSources'|['mysql57_test1'    ]|Jndi         |MySQL.instance
			'test/lightsleep-Jndi-dataSources'|['oracle121_test1'  ]|Jndi         |Oracle.instance
			'test/lightsleep-Jndi-dataSources'|['postgresql9_test1']|Jndi         |PostgreSQL.instance
			'test/lightsleep-Jndi-dataSources'|['sqlite_test1'     ]|Jndi         |SQLite.instance
			'test/lightsleep-Jndi-dataSources'|['sqlserver13_test1']|Jndi         |SQLServer.instance
	}
}
