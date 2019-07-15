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
import org.lightsleep.test.entity.Product.Size

import spock.lang.*

// SelectBase
// @since 3.1.0
public class SelectBase extends Base {
	def setupSpec() {
		DebugTrace.enter() // for Debugging
		Transaction.execute(connectionSupplier) {
			new Sql<>(Contact).where(Condition.ALL).connection(it).delete()
			new Sql<>(Address).where(Condition.ALL).connection(it).delete()
			new Sql<>(Phone).where(Condition.ALL).connection(it).delete()

			// Contacts
			def contacts = [
				['Yukari', 'Azusa', 'Chiyuki', 'Honoka', 'Akane', 'Haruka', 'Setoka', 'Kiyomi', 'Natsumi', 'Harumi'],
				['Apple' , 'Apple', 'Apple'  , 'Apple' , 'Apple', 'Orange', 'Orange', 'Orange', 'Orange' , 'Orange'],
				[ 2001   ,  2002  ,  2003    ,  2004   ,  2001  ,  2001   ,  2002   ,  2003   ,  2004    ,  2001   ],
				[    1   ,     2  ,     3    ,     4   ,     5  ,     6   ,     7   ,     8   ,     9    ,    10   ],
				[   10   ,     9  ,     8    ,     7   ,     6  ,     5   ,     4   ,     3   ,     2    ,     1   ]
			].transpose().collect {
				firstName, lastName, year, month, day ->

				def contact = new Contact()
				contact.name.first = firstName
				contact.name.last  = lastName

				def calendar = Calendar.instance
				calendar.clear()
				calendar.set(year, month - 1, day, 0, 0, 0)
				contact.birthday = new Date(calendar.timeInMillis)

				new Sql<>(Contact).connection(it).insert(contact)
				return contact
			}
			DebugTrace.print('contacts*.id', contacts*.id) // for Debugging

			// Phones
			def phones = [
				'08000010001',
				'08000020001', '08000020002',
				'08000030001', '08000030002', '08000030003',
				'08000040001',
				'08000050001', '08000050002',
				'08000060001', '08000060002', '08000060003',
				'08000070001',
				'08000080001', '08000080002',
				'08000090001', '08000090002', '08000090003',
				'08000100001',

				// No owner
				'08000110001',
				'08000120001', '08000120002',
				'08000130001', '08000130002', '08000130003',
			].collect {
				phoneNumber ->
				def index = Integer.parseInt(phoneNumber.substring(3, 7)) - 1
				DebugTrace.print('index', index) // for Debugging
				def phone = new Phone()
				phone.contactId = index < contacts.size()
					? contacts[index].id
					: contacts[contacts.size() - 1].id + 1 + (index - contacts.size())
				phone.phoneNumber = phoneNumber

				new Sql<>(Phone).connection(it).insert(phone)
				return phone
			}
			DebugTrace.print('phones.contactId', phones*.contactId) // for Debugging

		}
		DebugTrace.leave() // for Debugging
	}
}

@Table('super')
public class ContactYMD extends Contact {
	/** Year of the birthday */
	@Select('EXTRACT(YEAR FROM {birthday})')
	@NonInsert @NonUpdate
	public short birthdayYear;

	/** Month of the birthday */
	@Select('EXTRACT(MONTH FROM {birthday})')
	@NonInsert @NonUpdate
	public short birthdayMonth;

	/** Day of the birthday */
	@Select('EXTRACT(DAY FROM {birthday})')
	@NonInsert @NonUpdate
	public short birthdayDay;

	@Table('super')
	public static class DB2 extends ContactYMD {
	}

	@Table('super')
	@SelectProperty(property='birthdayYear' , expression='YEAR({birthday})')
	@SelectProperty(property='birthdayMonth', expression='MONTH({birthday})')
	@SelectProperty(property='birthdayDay'  , expression='DAY({birthday})')
	public static class MySQL extends ContactYMD {
	}

	@Table('super')
	public static class Oracle extends ContactYMD {
	}

	@Table('super')
	public static class PostgreSQL extends ContactYMD {
	}

	@Table('super')
	@SelectProperty(property='birthdayYear' , expression="strftime('%Y',{birthday})+0")
	@SelectProperty(property='birthdayMonth', expression="strftime('%m',{birthday})+0")
	@SelectProperty(property='birthdayDay'  , expression="strftime('%d',{birthday})+0")
	public static class SQLite extends ContactYMD {
	}

	@Table('super')
	@SelectProperty(property='birthdayYear' , expression='YEAR({birthday})')
	@SelectProperty(property='birthdayMonth', expression='MONTH({birthday})')
	@SelectProperty(property='birthdayDay'  , expression='DAY({birthday})')
	public static class SQLServer extends ContactYMD {
	}

	public static Class<? extends ContactYMD> getClass(Class<? extends ContactYMD> baseClass, Database database) {
		try {
			String className = baseClass.name + '$' + database.getClass().simpleName;
			return (Class<? extends ContactYMD>)Class.forName(className);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

@Table('super')
public class Contact1 extends ContactYMD {
	@Table('super') static class DB2        extends ContactYMD.DB2        {}
	@Table('super') static class MySQL      extends ContactYMD.MySQL      {}
	@Table('super') static class Oracle     extends ContactYMD.Oracle     {}
	@Table('super') static class PostgreSQL extends ContactYMD.PostgreSQL {}
	@Table('super') static class SQLite     extends ContactYMD.SQLite     {}
	@Table('super') static class SQLServer  extends ContactYMD.SQLServer  {}
}

@Table('super')
public class Contact2 extends ContactYMD {
	@Table('super') static class DB2        extends ContactYMD.DB2        {}
	@Table('super') static class MySQL      extends ContactYMD.MySQL      {}
	@Table('super') static class Oracle     extends ContactYMD.Oracle     {}
	@Table('super') static class PostgreSQL extends ContactYMD.PostgreSQL {}
	@Table('super') static class SQLite     extends ContactYMD.SQLite     {}
	@Table('super') static class SQLServer  extends ContactYMD.SQLServer  {}
}

@Table('super')
public class Contact3 extends ContactYMD {
	@Table('super') static class DB2        extends ContactYMD.DB2        {}
	@Table('super') static class MySQL      extends ContactYMD.MySQL      {}
	@Table('super') static class Oracle     extends ContactYMD.Oracle     {}
	@Table('super') static class PostgreSQL extends ContactYMD.PostgreSQL {}
	@Table('super') static class SQLite     extends ContactYMD.SQLite     {}
	@Table('super') static class SQLServer  extends ContactYMD.SQLServer  {}
}
