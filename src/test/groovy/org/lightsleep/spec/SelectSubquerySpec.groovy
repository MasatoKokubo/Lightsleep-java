// SelectSubquery.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec

import java.sql.Date

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.connection.*
import org.lightsleep.database.*
import org.lightsleep.entity.*
import org.lightsleep.helper.JoinInfo
import org.lightsleep.test.entity.*

import spock.lang.*

// SelectSubquery
// @since 3.1.0
@Unroll
public class SelectSubquerySpec extends SelectBase {
    // SelectSubquerySpec EXISTS (SELECT...)
    def "SelectSubquerySpec EXISTS (SELECT...)"() {
        DebugTrace.enter() // for Debugging
        when:
            List<Phone> phones = []
            Transaction.execute(connectionSupplier) {
                new Sql<>(Phone, 'P')
                    .where('EXISTS', new Sql<>(Contact).where('{id}={P.contactId}'))
                    .orderBy('{phoneNumber}').desc()
                    .limit(1)
                    .connection(it)
                    .select({phones << it})
            }
            DebugTrace.print('phones*.phoneNumber', phones*.phoneNumber) // for Debugging

        then:
            phones.size() == 1
            phones[0].phoneNumber == '08000100001'
        DebugTrace.leave() // for Debugging
    }

    // SelectSubquerySpec NOT EXISTS (SELECT...)
    def "SelectSubquerySpec NOT EXISTS (SELECT...)"() {
        DebugTrace.enter() // for Debugging
        when:
            List<Phone> phones = []
            Transaction.execute(connectionSupplier) {
                new Sql<>(Phone, 'P')
                    .where('NOT EXISTS', new Sql<>(Contact).where('{id}={P.contactId}'))
                    .orderBy('{phoneNumber}').asc()
                    .connection(it).select({phones << it})
            }
            DebugTrace.print('phones*.phoneNumber', phones*.phoneNumber) // for Debugging

        then:
            phones.size() == 6
            phones[0].phoneNumber == '08000110001'
        DebugTrace.leave() // for Debugging
    }

    // SelectSubquerySpec ... IN (SELECT...)
    def "SelectSubquerySpec ... IN (SELECT...)"() {
        DebugTrace.enter() // for Debugging
        when:
            List<Phone> phones = []
            Transaction.execute(connectionSupplier) {
                new Sql<>(Phone)
                    .where('{contactId} IN', new Sql<>(Contact).columns('id'))
                    .orderBy('{phoneNumber}').desc()
                    .limit(1)
                    .connection(it).select({phones << it})
            }
            DebugTrace.print('phones*.phoneNumber', phones*.phoneNumber) // for Debugging

        then:
            phones.size() == 1
            phones[0].phoneNumber == '08000100001'
        DebugTrace.leave() // for Debugging
    }

    // SelectSubquerySpec ... NOT IN (SELECT...)
    def "SelectSubquerySpec ... NOT IN (SELECT...)"() {
        DebugTrace.enter() // for Debugging
        when:
            List<Phone> phones = []
            Transaction.execute(connectionSupplier) {
                new Sql<>(Phone)
                    .where('{contactId} NOT IN', new Sql<>(Contact).columns('id'))
                    .orderBy('{phoneNumber}').asc()
                    .connection(it).select({phones << it})
            }
            DebugTrace.print('phones*.phoneNumber', phones*.phoneNumber) // for Debugging

        then:
            phones.size() == 6
            phones[0].phoneNumber == '08000110001'
        DebugTrace.leave() // for Debugging
    }

    // SelectSubquerySpec (SELECT...) ...
    def "SelectSubquerySpec (SELECT...) ..."() {
        DebugTrace.enter() // for Debugging
        when:
            List<Contact> constants = []
            Transaction.execute(connectionSupplier) {
                new Sql<>(Contact, 'C')
                    .where(new Sql<>(Phone)
                        .columns('id')
                        .expression('id', 'COUNT({id})')
                        .where('contactId={C.id}'),
                        '>=2')
                    .connection(it).select({constants << it})
            }
            DebugTrace.print('constants*.id', constants*.id) // for Debugging

        then:
            constants.size() == 6
        DebugTrace.leave() // for Debugging
    }

    // @since 4.0.0
    def "#ignore SelectSubquerySpec WITH: #useWithClause SELECT...  #joinType JOIN"(
            boolean useWithClause, JoinInfo.JoinType joinType, int resultCount, String containsStr,
            String ignore) {
        if (!ignore.empty) return
        DebugTrace.enter() // for Debugging

        when:
            List<Contact> constants = []
            List<Phone> phones = []
            def joinSql = new Sql<>(Phone)
                .where('{phoneNumber} LIKE {}', '%0002')
            def sql = new Sql<>(Contact, 'C')
                .doIf(useWithClause) {it.with(joinSql)}
                .doIf(joinType == JoinInfo.JoinType.INNER) {
                    it.innerJoin(joinSql, 'P', '{P.contactId}={C.id}')
                }
                .doIf(joinType == JoinInfo.JoinType.LEFT) {
                    it.leftJoin(joinSql, 'P', '{P.contactId}={C.id}')
                }
                .doIf(joinType == JoinInfo.JoinType.RIGHT) {
                    it.rightJoin(joinSql, 'P', '{P.contactId}={C.id}')
                }
            Transaction.execute(connectionSupplier) {
                sql.connection(it).select({constants << it}, {phones << it})
            }
            DebugTrace.print('constants*.id', constants*.id) // for Debugging
            DebugTrace.print('phones*.id', phones*.id) // for Debugging

        then:
            constants.size() == resultCount
            sql.generatedSql().contains(containsStr)

        DebugTrace.leave() // for Debugging
        where:
            useWithClause|joinType               |resultCount|containsStr
            false        |JoinInfo.JoinType.INNER| 6         |'INNER JOIN (SELECT'
            false        |JoinInfo.JoinType.LEFT |10         |'LEFT OUTER JOIN (SELECT'
            false        |JoinInfo.JoinType.RIGHT| 8         |'RIGHT OUTER JOIN (SELECT'
            true         |JoinInfo.JoinType.INNER| 6         |'INNER JOIN W1'
            true         |JoinInfo.JoinType.LEFT |10         |'LEFT OUTER JOIN W1'
            true         |JoinInfo.JoinType.RIGHT| 8         |'RIGHT OUTER JOIN W1'

            ignore = 
                doesNotSupportWithClause && useWithClause ||
                doesNotSupportRightJoin && joinType == JoinInfo.JoinType.RIGHT
                    ? "*IGNORE*" : ""
    }
}
