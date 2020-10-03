// DatabaseSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.database

import org.debugtrace.DebugTrace
import org.lightsleep.component.*
import org.lightsleep.database.*

import spock.lang.*

// DatabaseSpec
@Unroll
class DatabaseSpec extends Specification {
    // getInstance
    def "Database.getInstance '#jdbcUrl'"(String jdbcUrl, Database database) {
        expect: Database.getInstance(jdbcUrl) == database

        where:
            jdbcUrl           |database
            'jdbc:db2:'       |Db2.instance
            'jdbc:mariadb:'   |MariaDB.instance
            'jdbc:mysql:'     |MySQL.instance
            'jdbc:oracle:'    |Oracle.instance
            'jdbc:postgresql:'|PostgreSQL.instance
            'jdbc:sqlite:'    |SQLite.instance
            'jdbc:sqlserver:' |SQLServer.instance
    }

    // getInstance exception
    def "Database.getInstance exception '#jdbcUrl'"(String jdbcUrl) {
        when: Database.getInstance(jdbcUrl)
        then: thrown IllegalArgumentException

        where:
            jdbcUrl << [
                'jdbc:Db2:',
                'jdbc:MariaDB:',
                'jdbc:MySQL:',
                'jdbc:Oracle:',
                'jdbc:PostgreSQL:',
                'jdbc:SQLite:',
                'jdbc:SQLServer:'
            ]
    }
}
