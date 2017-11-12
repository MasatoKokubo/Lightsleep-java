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
import org.lightsleep.helper.Resource
import org.lightsleep.test.entity.*

import spock.lang.*

// ConnectionSpec
@Unroll
public class ConnectionSpec extends SpecCommon {
	private static final int THREAD_COUNT         =   50
	private static final long SLEEP_TIME1         =   10 // ms
	private static final long SLEEP_TIME2         = 1000 // ms
	private static final int THREAD_COUNT_SQLITE  =   10
	private static final long SLEEP_TIME1_SQLITE  = 2000 // ms
	private static final long SLEEP_TIME2_SQLITE  =    1 // ms

	// The map of isolation levels
	private static isolationLevelsMap = [
		(Connection.TRANSACTION_NONE            ): 'none',
		(Connection.TRANSACTION_READ_COMMITTED  ): 'read-committed',
		(Connection.TRANSACTION_READ_UNCOMMITTED): 'read-uncommitted',
		(Connection.TRANSACTION_REPEATABLE_READ ): 'repeatable-read',
		(Connection.TRANSACTION_SERIALIZABLE    ): 'serializable'
	] as Map

	def setup() {
		deleteAllTables()
	}

	def "ConnectionSpec #connectionSupplier"(ConnectionSupplier connectionSupplier) {
	/**/DebugTrace.enter()
	/**/DebugTrace.print('connectionSupplier', connectionSupplier.toString())
		setup:
			// Make test data.
		/**/DebugTrace.print('Make test data.')
			def contacts = InsertUpdateDeleteSpec.makeTestData(null, 0, THREAD_COUNT)

			def isSQLite = connectionSupplier.database instanceof SQLite
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

							if (it.database instanceof SQLServer) {
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

							new Sql<>(ContactComposite).connection(it).insert(contact)

							ContactComposite contact2 = new Sql<>(ContactComposite).connection(it).where(contact).select().orElse(null)
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

			// Wait for all threads to finish.
			for (def index = 0; index < threads.length; ++index)
				threads[index].join()

		then:
			errorCount == 0

	/**/DebugTrace.leave()

		where:
			connectionSupplier << connectionSuppliers
	}
}
