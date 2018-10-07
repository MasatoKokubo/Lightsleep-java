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
class InterfaceSpec extends SpecCommon {
	@Table("super")
	static class Contact2 extends Contact implements PreStore, PostLoad {
		@NonColumn public int preStoreCount
		@NonColumn public int postLoadCount

		@Override
		public void preStore() {
			++preStoreCount
		}

		@Override
		public void postLoad() {
			++postLoadCount
		}
	}

	// PreStore PostLoad
	def "InterfaceSpec PreStore PostLoad #connectionSupplier"(ConnectionSupplier connectionSupplier) {
		DebugTrace.enter() // for Debugging
		DebugTrace.print('connectionSupplier', connectionSupplier.toString()) // for Debugging
		when:
			def contact = new Contact2()
			contact.name.first = 'firstName'
			contact.name.last  = 'lastName'

		then:
			contact.preStoreCount == 0
			contact.postLoadCount == 0

			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact2).where(Condition.ALL).connection(it).delete()
				assert contact.preStoreCount == 0
				assert contact.postLoadCount == 0

				new Sql<>(Contact2).connection(it).insert(contact)
				assert contact.preStoreCount == 1
				assert contact.postLoadCount == 0

				contact.name.first = 'firstName2'
				contact.name.last  = 'lastName2'

				new Sql<>(Contact2).connection(it).update(contact)
				assert contact.preStoreCount == 2
				assert contact.postLoadCount == 0

				contact.name.first = 'firstName3'
				contact.name.last  = 'lastName3'

				new Sql<>(Contact2).where(Condition.ALL).connection(it).update(contact)
				assert contact.preStoreCount == 3
				assert contact.postLoadCount == 0

				Optional<Contact2> contactOpt = new Sql<>(Contact2).where('{id}={}', contact.id).connection(it).select()
				assert contactOpt.isPresent()
				assert contactOpt.get().preStoreCount == 0
				assert contactOpt.get().postLoadCount == 1
			}

		DebugTrace.leave() // for Debugging
		where:
			connectionSupplier << connectionSuppliers
	}
}
