// InterfaceSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.entity

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.connection.*
import org.lightsleep.entity.*
import org.lightsleep.spec.*
import org.lightsleep.test.entity.*

import spock.lang.*

// AnnotationSpec
@Unroll
class InterfaceSpec extends Base {
    @Table("super")
    static class Contact3 extends Contact implements 
            PreInsert, PreUpdate, PreDelete, PostInsert, PostUpdate, PostDelete, PostSelect {
        @NonColumn public int preInsertCount
        @NonColumn public int preUpdateCount
        @NonColumn public int preDeleteCount
        @NonColumn public int postInsertCount
        @NonColumn public int postUpdateCount
        @NonColumn public int postDeleteCount
        @NonColumn public int postSelectCount

        @Override
        public void preInsert(ConnectionWrapper connection) {
            ++preInsertCount
        }

        @Override
        public void preUpdate(ConnectionWrapper connection) {
            ++preUpdateCount
        }

        @Override
        public void preDelete(ConnectionWrapper connection) {
            ++preDeleteCount
        }

        @Override
        public void postInsert(ConnectionWrapper connection) {
            super.postInsert(connection);
            ++postInsertCount
        }

        @Override
        public void postUpdate(ConnectionWrapper connection) {
            ++postUpdateCount
        }

        @Override
        public void postDelete(ConnectionWrapper connection) {
            ++postDeleteCount
        }

        @Override
        public void postSelect(ConnectionWrapper connection) {
            ++postSelectCount
        }
    }

    // Pre(Insert Update Delete) Post(Insert Update Delete Select) single
    def "Pre(Insert Update Delete) Post(Insert Update Delete Select) single #connectionSupplier "(
            ConnectionSupplier connectionSupplier) {
        DebugTrace.enter() // for Debugging
        DebugTrace.print('connectionSupplier', connectionSupplier.toString()) // for Debugging
        setup:
            Transaction.execute(connectionSupplier) {
                def count = new Sql<>(Contact3).where(Condition.ALL).connection(it).delete()
            }

        // insert
        when:
            def contact = new Contact3()
            contact.name.first = 'Madoka'
            contact.name.last  = 'Peach'
            Transaction.execute(connectionSupplier) {
                def count = new Sql<>(Contact3).connection(it).insert(contact)
            }

        then:
            assert contact.preInsertCount  == 1
            assert contact.preUpdateCount  == 0
            assert contact.preDeleteCount  == 0
            assert contact.postInsertCount == 1
            assert contact.postUpdateCount == 0
            assert contact.postDeleteCount == 0
            assert contact.postSelectCount == 0

        // update
        when:
            contact.name.first = 'Kimiko'
            Transaction.execute(connectionSupplier) {
                def count = new Sql<>(Contact3).columns('name.first').connection(it).update(contact)
            }

        then:
            assert contact.preInsertCount  == 1
            assert contact.preUpdateCount  == 1
            assert contact.preDeleteCount  == 0
            assert contact.postInsertCount == 1
            assert contact.postUpdateCount == 1
            assert contact.postDeleteCount == 0
            assert contact.postSelectCount == 0

        // select
        when:
            def contact2 = null as Contact3
            Transaction.execute(connectionSupplier) {
                contact2 = new Sql<>(Contact3).where(contact).connection(it).select().orElse(null)
            }

        then:
            assert contact2 != null
            assert contact2.preInsertCount  == 0
            assert contact2.preUpdateCount  == 0
            assert contact2.preDeleteCount  == 0
            assert contact2.postInsertCount == 0
            assert contact2.postUpdateCount == 0
            assert contact2.postDeleteCount == 0
            assert contact2.postSelectCount == 1

        // delete
        when:
            Transaction.execute(connectionSupplier) {
                def count = new Sql<>(Contact3).connection(it).delete(contact)
            }

        then:
            assert contact.preInsertCount  == 1
            assert contact.preUpdateCount  == 1
            assert contact.preDeleteCount  == 1
            assert contact.postInsertCount == 1
            assert contact.postUpdateCount == 1
            assert contact.postDeleteCount == 1
            assert contact.postSelectCount == 0

        DebugTrace.leave() // for Debugging
        where:
            connectionSupplier << connectionSuppliers
    }

    // Pre(Insert Update Delete) Post(Insert Update Delete Select) multi
    @Ignore
    def "Pre(Insert Update Delete) Post(Insert Update Delete Select) multi #connectionSupplier "(
            ConnectionSupplier connectionSupplier) {
        DebugTrace.enter() // for Debugging
        DebugTrace.print('connectionSupplier', connectionSupplier.toString()) // for Debugging
        setup:
            Transaction.execute(connectionSupplier) {
                new Sql<>(Contact3).where(Condition.ALL).connection(it).delete()
            }

        // insert
        when:
            def contacts = [new Contact3(), new Contact3()]
            contacts[0].name.first = 'Madoka'
            contacts[0].name.last  = 'Apple'
            contacts[1].name.first = 'Kimiko'
            contacts[1].name.last  = 'Apple'
            Transaction.execute(connectionSupplier) {
                def count = new Sql<>(Contact3).connection(it).insert(contacts)
            }

        then:
            assert contacts.each {it.preInsertCount  == 1}
            assert contacts.each {it.preUpdateCount  == 0}
            assert contacts.each {it.preDeleteCount  == 0}
            assert contacts.each {it.postInsertCount == 1}
            assert contacts.each {it.postUpdateCount == 0}
            assert contacts.each {it.postDeleteCount == 0}
            assert contacts.each {it.postSelectCount == 0}

        // update
        when:
            contacts[0].name.last = 'Peach'
            contacts[0].name.last = 'Peach'
            Transaction.execute(connectionSupplier) {
                def count = new Sql<>(Contact3).columns('name.last').connection(it).update(contacts)
            }

        then:
            assert contacts.each {it.preInsertCount  == 1}
            assert contacts.each {it.preUpdateCount  == 1}
            assert contacts.each {it.preDeleteCount  == 0}
            assert contacts.each {it.postInsertCount == 1}
            assert contacts.each {it.postUpdateCount == 1}
            assert contacts.each {it.postDeleteCount == 0}
            assert contacts.each {it.postSelectCount == 0}

        // select
        when:
            def contacts2 = [] as List<Contact3>
            Transaction.execute(connectionSupplier) {
                def count = new Sql<>(Contact3).connection(it).select({contacts2 << it})
            }

        then:
            assert contacts2.size() == contacts2.size()
            assert contacts2.each {it.preInsertCount  == 0}
            assert contacts2.each {it.preUpdateCount  == 0}
            assert contacts2.each {it.preDeleteCount  == 0}
            assert contacts2.each {it.postInsertCount == 0}
            assert contacts2.each {it.postUpdateCount == 0}
            assert contacts2.each {it.postDeleteCount == 0}
            assert contacts2.each {it.postSelectCount == 1}

        // delete
        when:
            Transaction.execute(connectionSupplier) {
                def count = new Sql<>(Contact3).connection(it).delete(contacts)
            }

        then:
            assert contacts.each {it.preInsertCount  == 1}
            assert contacts.each {it.preUpdateCount  == 1}
            assert contacts.each {it.preDeleteCount  == 1}
            assert contacts.each {it.postInsertCount == 1}
            assert contacts.each {it.postUpdateCount == 1}
            assert contacts.each {it.postDeleteCount == 1}
            assert contacts.each {it.postSelectCount == 0}

        DebugTrace.leave() // for Debugging
        where:
            connectionSupplier << connectionSuppliers
    }
}
