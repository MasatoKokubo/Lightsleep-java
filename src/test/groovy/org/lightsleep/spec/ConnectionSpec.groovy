// ConnectionSpec.groovy
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
import org.lightsleep.test.entity.*

import spock.lang.*

// ConnectionSpec
@Unroll
class ConnectionSpec extends Specification {
	static final int THREAD_COUNT         =   50
	static final long SLEEP_TIME1         =   10 // ms
	static final long SLEEP_TIME2         = 1000 // ms
	static final int THREAD_COUNT_SQLITE  =   10
	static final long SLEEP_TIME1_SQLITE  = 2000 // ms
	static final long SLEEP_TIME2_SQLITE  =    1 // ms

	// The map of isolation levels
	static isolationLevelsMap = [
		(Connection.TRANSACTION_NONE            ): 'none',
		(Connection.TRANSACTION_READ_COMMITTED  ): 'read-committed',
		(Connection.TRANSACTION_READ_UNCOMMITTED): 'read-uncommitted',
		(Connection.TRANSACTION_REPEATABLE_READ ): 'repeatable-read',
		(Connection.TRANSACTION_SERIALIZABLE    ): 'serializable'
	] as Map

	static databaseName = 'test'

	static modifier = {Properties proprties ->
		proprties['url'    ] = proprties['url'    ] + databaseName
		proprties['jdbcUrl'] = proprties['jdbcUrl'] + databaseName
	}

	static connectionSupplierMap = [
		(C3p0    .class): new C3p0    (modifier),
		(Dbcp    .class): new Dbcp    (modifier),
		(HikariCP.class): new HikariCP(modifier),
		(Jdbc    .class): new Jdbc    (modifier),
		(TomcatCP.class): new TomcatCP(modifier)
	] as Map

	static def defaultConnectionSupplier = connectionSupplierMap[Jdbc.class]

	static ConnectionSupplier getConnectionSupplier(Class<? extends ConnectionSupplier> connectionSupplierClass) {
		return connectionSupplierMap.get(connectionSupplierClass)
	}

	/**
	 * Deletes test data.
	 */
	def setupSpec() {
		Sql.connectionSupplier = defaultConnectionSupplier

		Transaction.execute {
			new Sql<>(Contact .class).where(Condition.ALL).delete(it)
			new Sql<>(Address.class).where(Condition.ALL).delete(it)
			new Sql<>(Phone  .class).where(Condition.ALL).delete(it)
		}
	}

	def "connection #connectionSupplierName"(
		Class<? extends ConnectionSupplier> connectionSupplierClass, String connectionSupplierName) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('connectionSupplierClass', connectionSupplierClass)
		setup:
			def connectionSupplier = getConnectionSupplier(connectionSupplierClass)

			// Makes test data.
		/**/DebugTrace.print('Makes test data.')
			def contacts = InsertUpdateDeleteSpec.makeTestData(null, 0, THREAD_COUNT)

			def isSQLite = Sql.getDatabase() instanceof SQLite
		/**/DebugTrace.print('isSQLite', isSQLite)

			Thread[] threads = new Thread[isSQLite ? THREAD_COUNT_SQLITE : THREAD_COUNT]
			def count = 0
			def errorCount = 0

		when:
			(0..<threads.length).each {index ->
			/**/DebugTrace.print('index', index)
				ContactComposite contact = contacts.get(index)
				threads[index] = new Thread({
				/**/DebugTrace.enter()
					int index2 = index
					try {
						Transaction.execute(connectionSupplier) {
							++count
						/**/DebugTrace.print(index2 + ': start: connection count', count)

							if (Sql.database instanceof SQLServer) {
								def beforeTransactionIsolation = it.transactionIsolation
								it.transactionIsolation = Connection.TRANSACTION_READ_UNCOMMITTED
								def afterTransactionIsolation = it.transactionIsolation
								/**/DebugTrace.print(
									getClass().simpleName
									+ 'connection.transactionIsolation: '
									+ isolationLevelsMap.getOrDefault(beforeTransactionIsolation, 'unknow')
									+ ' -> '
									+ isolationLevelsMap.getOrDefault(afterTransactionIsolation, 'unknow')
								)
							}

							new Sql<>(ContactComposite.class).insert(it, contact)

							ContactComposite contact2 = new Sql<>(ContactComposite.class).where(contact).select(it).orElse(null)
							try {
								Thread.sleep(isSQLite ? SLEEP_TIME2_SQLITE : SLEEP_TIME2)
							}
							catch (InterruptedException e4) {
								new RuntimeException(e4)
							}

						/**/DebugTrace.print(index2 + ': end')
							--count
						}
					}
					catch (Exception e) {
						++errorCount
					/**/DebugTrace.print('e', e)
					}
				/**/DebugTrace.leave()
				})
				threads[index].start()
				Thread.sleep(isSQLite ? SLEEP_TIME1_SQLITE : SLEEP_TIME1)
			}

			// Waits for all threads to finish.
			for (def index = 0; index < threads.length; ++index)
				threads[index].join()

		then:
			errorCount == 0

	/**/DebugTrace.leave()

		where:
			connectionSupplierClass << [C3p0.class, Dbcp.class, HikariCP.class, TomcatCP.class]
			connectionSupplierName = connectionSupplierClass.simpleName
	}
}
