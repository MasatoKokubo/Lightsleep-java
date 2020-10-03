// SelectSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec

import java.sql.Date

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.connection.*
import org.lightsleep.database.*
import org.lightsleep.entity.*
import org.lightsleep.test.entity.*

import spock.lang.*

// SelectUnionSpec
// @since 3.1.0
@Unroll
public class SelectUnionSpec extends SelectBase {
    // select ... union select ...
    // select ... union all select ...
    def "SelectUnionSpec select ... union select ..."() {
        DebugTrace.enter() // for Debugging
        DebugTrace.print('select ... union select ...') // for Debugging
        setup:
            def contacts = new ArrayList<>()
            def contactClass = ContactYMD.getClass(ContactYMD, connectionSupplier.database)
            def contact1Class = ContactYMD.getClass(Contact1, connectionSupplier.database);
            def contact2Class = ContactYMD.getClass(Contact2, connectionSupplier.database);
            def contact3Class = ContactYMD.getClass(Contact3, connectionSupplier.database);
            def columns = ['name.last', 'birthdayYear'] as String[]

        // SELECT ...
        when:
            Transaction.execute(connectionSupplier) {
                new Sql<>(contactClass)
                    .from(new Sql<>(contactClass))
                    .where('{birthdayYear}={}', 2001)
                    .orderBy('{name.last}').asc()
                    .orderBy('{birthdayYear}').asc()
                    .connection(it)
                    .select({contacts << it})
            }

        then:
            contacts.size() == 4

        // SELECT ... UNION SELECT ...
        when:
            contacts.clear()

            Transaction.execute(connectionSupplier) {
                new Sql<>(ContactYMD)
                    .union(
                        new Sql<>(contact1Class)
                            .from(new Sql<>(contact1Class))
                            .where('{birthdayYear}={}', 2001)
                    )
                    .columns(columns) // columns
                    .union(
                        new Sql<>(contact2Class)
                            .from(new Sql<>(contact2Class))
                            .where('{birthdayYear}={}', 2002)
                    )
                    .union(
                        new Sql<>(contact3Class)
                            .from(new Sql<>(contact3Class))
                            .where('{birthdayYear}={}', 2003)
                    )
                    .orderBy('{name.last}').asc()
                    .orderBy('{birthdayYear}').asc()
                    .connection(it)
                    .select({contacts << it})
            }

        then:
            contacts.size() == 6

        // SELECT ... UNION ALL SELECT ...
        when:
            contacts.clear()
            Transaction.execute(connectionSupplier) {
                new Sql<>(ContactYMD)
                    .unionAll(
                        new Sql<>(contact1Class)
                            .from(new Sql<>(contact1Class))
                            .where('{birthdayYear}={}', 2001)
                    )
                    .unionAll(
                        new Sql<>(contact2Class)
                            .from(new Sql<>(contact2Class).columns(columns)) // columns
                            .where('{birthdayYear}={}', 2002)
                    )
                    .unionAll(
                        new Sql<>(contact3Class)
                            .from(new Sql<>(contact3Class))
                            .where('{birthdayYear}={}', 2003)
                    )
                    .orderBy('{name.last}').asc()
                    .orderBy('{birthdayYear}').asc()
                    .connection(it)
                    .select({contacts << it})
            }

        then:
            contacts.size() == 8
        DebugTrace.leave() // for Debugging
    }

    // select ... union select ... union all ...
    def "SelectUnionSpec exception union and unionAll"() {
        DebugTrace.enter() // for Debugging
        when:
            new Sql<>(ContactYMD)
                .union   (new Sql<>(ContactYMD))
                .unionAll(new Sql<>(ContactYMD))

        then:
            def e1 = thrown IllegalStateException
            DebugTrace.print('e1', e1) // for Debugging

        when:
            new Sql<>(ContactYMD)
                .unionAll(new Sql<>(ContactYMD))
                .union   (new Sql<>(ContactYMD))

        then:
            def e2 = thrown IllegalStateException
            DebugTrace.print('e2', e2) // for Debugging
        DebugTrace.leave() // for Debugging
    }
}
