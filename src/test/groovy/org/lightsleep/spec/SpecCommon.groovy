// SpecCommon.groovy
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

// SpecCommon
// @since 2.1.0
public class SpecCommon extends Specification {
	@Shared List<ConnectionSupplier> connectionSuppliers
	@Shared ConnectionSupplier connectionSupplier

	def setupSpec() {
		def databaseResource = new Resource('Database')
		def databaseKeyword = databaseResource.getString('Database').toLowerCase()

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
			new Sql<>(Contact ).where(Condition.ALL).connection(it).delete()
			new Sql<>(Address ).where(Condition.ALL).connection(it).delete()
			new Sql<>(Phone   ).where(Condition.ALL).connection(it).delete()
			new Sql<>(Product ).where(Condition.ALL).connection(it).delete()
			new Sql<>(Sale    ).where(Condition.ALL).connection(it).delete()
			new Sql<>(SaleItem).where(Condition.ALL).connection(it).delete()
		}
	}
}
