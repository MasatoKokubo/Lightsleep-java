// Base.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec

import java.sql.Connection
import java.sql.Date
import java.util.function.Consumer

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.connection.*
import org.lightsleep.database.*
import org.lightsleep.helper.Resource
import org.lightsleep.test.entity.*

import spock.lang.*

// Base
// @since 2.1.0
public class Base extends Specification {
    @Shared List<ConnectionSupplier> connectionSuppliers
    @Shared ConnectionSupplier connectionSupplier
    @Shared boolean doesNotSupportRightJoin        // Dose not support SELECT ... RIGHT OUTER JOIN ...
    @Shared boolean doesNotSupportWithClause       // Dose not support WITH ...
    @Shared boolean doesNotSupportUpdateWithJoin   // Dose not support UPDATE ... JOIN ... 
    @Shared boolean doesNotSupportForUpdate        // Dose not support SELECT ... FOR UPDATE 
    @Shared boolean doesNotSupportForUpdateNoWait  // Dose not support SELECT ... FOR UPDATE NOWAIT
    @Shared boolean doesNotSupportForUpdateNoWaitN // Dose not support SELECT ... FOR UPDATE WAIT N

    def setupSpec() {
        def databaseResource = new Resource('Database')
        def databaseKeyword = databaseResource.getString('Database')
        doesNotSupportRightJoin = databaseKeyword.contains('sqlite')
        doesNotSupportWithClause = databaseKeyword.contains('mysql:3306') // MySQL 4.7
        doesNotSupportUpdateWithJoin =
            databaseKeyword.contains('db2'       ) ||
            databaseKeyword.contains('oracle'    ) ||
            databaseKeyword.contains('postgresql') ||
            databaseKeyword.contains('sqlite'    )
        doesNotSupportForUpdate = databaseKeyword.contains('sqlite')
        doesNotSupportForUpdateNoWait = 
            databaseKeyword.contains('db2'       ) ||
            databaseKeyword.contains('mariadb'   ) ||
            databaseKeyword.contains('mysql'     ) ||
            databaseKeyword.contains('postgresql') ||
            databaseKeyword.contains('sqlite'    )
        doesNotSupportForUpdateNoWaitN = 
            databaseKeyword.contains('db2'       ) ||
            databaseKeyword.contains('mariadb'   ) ||
            databaseKeyword.contains('mysql'     ) ||
            databaseKeyword.contains('postgresql') ||
            databaseKeyword.contains('sqlite'    ) ||
            databaseKeyword.contains('sqlserver' )

        connectionSuppliers = [
            Jdbc    .simpleName,
            C3p0    .simpleName,
            Dbcp    .simpleName,
            HikariCP.simpleName,
            TomcatCP.simpleName,
        ].collect {ConnectionSupplier.find(it, databaseKeyword)}

        connectionSupplier = connectionSuppliers[0]
    }

    def deleteAllTables() {
        Transaction.execute(connectionSupplier) {
            new Sql<>(Contact ).connection(it).where(Condition.ALL).delete()
            new Sql<>(Address ).connection(it).where(Condition.ALL).delete()
            new Sql<>(Phone   ).connection(it).where(Condition.ALL).delete()
            new Sql<>(Product ).connection(it).where(Condition.ALL).delete()
            new Sql<>(Sale    ).connection(it).where(Condition.ALL).delete()
            new Sql<>(SaleItem).connection(it).where(Condition.ALL).delete()
        }
    }
}
