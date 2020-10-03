// InsertUpdateDeleteSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec


import java.sql.Date

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.connection.*
import org.lightsleep.database.*
import org.lightsleep.test.entity.*
import org.lightsleep.test.exception.DeletedException
import org.lightsleep.test.exception.UpdateException
import spock.lang.*

// SelectInsertUpdateDeleteErrorSpec
@Unroll
class SelectInsertUpdateDeleteErrorSpec extends Base {
    def "IllegalStateException no connection Sql.#name"(String name, Closure closure) {
        DebugTrace.enter() // for Debugging
        when:
            Transaction.execute(connectionSupplier) {
                closure(new Sql<>(Contact))
            }

        then: IllegalStateException e = thrown()
            DebugTrace.print('e', e) // for Debugging

        DebugTrace.leave() // for Debugging
        where:
            name                       |closure
            'select(Consumer)'         |{Sql sql -> sql.select({})}
            'selectAs(Class, Consumer)'|{Sql sql -> sql.selectAs(Contact, {})}
            'select(2 Consumers)'      |{Sql sql -> sql.select({}, {})}
            'select(3 Consumers)'      |{Sql sql -> sql.select({}, {}, {})}
            'select(4 Consumers)'      |{Sql sql -> sql.select({}, {}, {}, {})}
            'select(5 Consumers)'      |{Sql sql -> sql.select({}, {}, {}, {}, {})}
            'select()'                 |{Sql sql -> sql.select()}
            'select(Class)'            |{Sql sql -> sql.selectAs(Contact)}
            'selectCount()'            |{Sql sql -> sql.selectCount()}
            'insert()'                 |{Sql sql -> sql.insert()}
            'insert(E)'                |{Sql sql -> sql.insert(new Contact())}
            'insert(Iterable)'         |{Sql sql -> sql.insert([new Contact()])}
            'update(E)'                |{Sql sql -> sql.update(new Contact())}
            'update(Iterable)'         |{Sql sql -> sql.update([new Contact()])}
            'delete()'                 |{Sql sql -> sql.delete()}
            'delete(E)'                |{Sql sql -> sql.delete(new Contact())}
            'delete(Iterable)'         |{Sql sql -> sql.delete([new Contact()])}
            'executeUpdate(String)'    |{Sql sql -> sql.executeUpdate('')}
    }

    def "IllegalStateException no columns Sql.#name"(String name, Closure closure) {
        DebugTrace.enter() // for Debugging
        when:
            Transaction.execute(connectionSupplier) {
                closure(new Sql<>(Contact).connection(it).columns(""))
            }

        then: IllegalStateException e = thrown()
            DebugTrace.print('e', e) // for Debugging

        DebugTrace.leave() // for Debugging
        where:
            name                       |closure
            'select(Consumer)'         |{Sql sql -> sql.select({})}
            'selectAs(Class, Consumer)'|{Sql sql -> sql.selectAs(Contact, {})}
            'select(2 Consumers)'      |{Sql sql -> sql.select({}, {})}
            'select(3 Consumers)'      |{Sql sql -> sql.select({}, {}, {})}
            'select(4 Consumers)'      |{Sql sql -> sql.select({}, {}, {}, {})}
            'select(5 Consumers)'      |{Sql sql -> sql.select({}, {}, {}, {}, {})}
            'select()'                 |{Sql sql -> sql.select()}
            'select(Class)'            |{Sql sql -> sql.selectAs(Contact)}
    }

    def "IllegalStateException no fromSql Sql.#name"(String name, Closure closure) {
        DebugTrace.enter() // for Debugging
        when:
            Transaction.execute(connectionSupplier) {
                closure(new Sql<>(Contact).connection(it))
            }

        then: IllegalStateException e = thrown()
            DebugTrace.print('e', e) // for Debugging

        DebugTrace.leave() // for Debugging
        where:
            name       |closure
            'insert()' |{Sql sql -> sql.insert()}
    }

    def "NullPointerException Sql.#name"(String name, Closure closure) {
        DebugTrace.enter() // for Debugging
        when:
            Transaction.execute(connectionSupplier) {
                closure(new Sql<>(Contact).connection(it))
            }

        then: NullPointerException e = thrown()
            DebugTrace.print('e', e) // for Debugging

        DebugTrace.leave() // for Debugging
        where:
            name                   |closure
            'insert(E)'            |{Sql sql -> sql.insert((Contact)null)}
            'insert(Iterable)'     |{Sql sql -> sql.insert((Iterable)null)}
            'update(E)'            |{Sql sql -> sql.update((Contact)null)}
            'update(Iterable)'     |{Sql sql -> sql.update((Iterable)null)}
            'delete(E)'            |{Sql sql -> sql.delete((Contact)null)}
            'delete(Iterable)'     |{Sql sql -> sql.delete((Iterable)null)}
            'executeUpdate(String)'|{Sql sql -> sql.executeUpdate(null)}
    }
}
