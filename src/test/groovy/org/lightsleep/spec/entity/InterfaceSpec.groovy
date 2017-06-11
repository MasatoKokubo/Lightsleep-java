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
class InterfaceSpec extends Specification {
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
	def "InterfaceSpec PreStore PostLoad #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)

		setup:
			ConnectionSupplier connectionSupplier = ConnectionSpec.getConnectionSupplier(connectionSupplierClass)

		when:
			def contact = new Contact2()
			contact.name.family = 'familyName'
			contact.name.given  = 'givenName'

		then:
			contact.preStoreCount == 0
			contact.postLoadCount == 0

			Transaction.execute(connectionSupplier) {
				new Sql<>(Contact2.class).where(Condition.ALL).delete(it)
				assert contact.preStoreCount == 0
				assert contact.postLoadCount == 0

				new Sql<>(Contact2.class).insert(it, contact)
				assert contact.preStoreCount == 1
				assert contact.postLoadCount == 0

				contact.name.family = 'familyName2'
				contact.name.given  = 'givenName2'

				new Sql<>(Contact2.class).update(it, contact)
				assert contact.preStoreCount == 2
				assert contact.postLoadCount == 0

				contact.name.family = 'familyName3'
				contact.name.given  = 'givenName3'

				new Sql<>(Contact2.class).where(Condition.ALL).update(it, contact)
				assert contact.preStoreCount == 3
				assert contact.postLoadCount == 0

				Optional<Contact2> contactOpt = new Sql<>(Contact2.class).where('{id}={}', contact.id).select(it)
				assert contactOpt.isPresent()
				assert contactOpt.get().preStoreCount == 0
				assert contactOpt.get().postLoadCount == 1
			}

	/**/DebugTrace.leave()
		where:
			connectionSupplierClass << [C3p0.class, Dbcp.class, HikariCP.class, TomcatCP.class, Jdbc.class]
			connectionSupplierName = connectionSupplierClass.simpleName
	}
}
