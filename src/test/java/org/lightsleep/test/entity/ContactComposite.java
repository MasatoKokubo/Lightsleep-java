// ContactComposite.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lightsleep.Sql;
import org.lightsleep.entity.*;

/**
 * The composite entity of contact, address and phone table.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
@Table("super")
public class ContactComposite extends Contact implements Composite {
	/** Address */
	@NonColumn
	public Address address = new Address();

	/** Phones */
	@NonColumn
	public final List<Phone> phones = new ArrayList<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postSelect(Connection connection) {
		// Selects and gets the address
		if (addressId != 0)
			address = new Sql<>(Address.class)
				.where("{id} = {}", addressId)
				.select(connection).orElse(null);

		// Selects and gets phones
		if (id != 0)
			new Sql<>(Phone.class)
				.where("{contactId} = {}", id)
				.orderBy("{phoneNumber}")
				.select(connection, phones::add);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int preInsert(Connection connection) {
		super.preInsert(connection);

		int count = 0;

		// Inserts the address
		count += new Sql<>(Address.class)
			.insert(connection, address);
		addressId = address.id;

		return count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int postInsert(Connection connection) {
		int count = 0;

		// Inserts phones
		phones.forEach(phone -> phone.contactId = id);
		count += new Sql<>(Phone.class)
			.insert(connection, phones);

		return count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int postUpdate(Connection connection) {
		int count = 0;

		if (addressId != 0) {
			// Updates the address
			int updateCount = new Sql<>(Address.class)
				.update(connection, address);
			if (updateCount != 0)
				// Updated
				count += updateCount;
			else
				// Not Updated
				// Inserts  the address
				count += new Sql<>(Address.class)
					.insert(connection, address);
		} else
			// Deletes the address
			count += new Sql<>(Address.class)
				.where("{id} = {}", addressId)
				.delete(connection);

		List<Integer> phoneIds = phones.stream()
			.map(phone -> phone.id)
			.filter(id -> id != null)
			.collect(Collectors.toList());

		// Deletes phones
		count += new Sql<>(Phone.class)
			.where("{contactId} = {}", id)
			.doIf(phoneIds.size() > 0,
				sql -> sql.and("{id} NOT IN {}", phoneIds)
			)
			.delete(connection);

		// Uptete phones
		count += new Sql<>(Phone.class)
			.update(connection, phones.stream()
				.filter(phone -> phone.id != 0)
				.collect(Collectors.toList()));

		// Inserts phones
		count += new Sql<>(Phone.class)
			.insert(connection, phones.stream()
				.filter(phone -> phone.id == 0)
				.collect(Collectors.toList()));

		return count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int postDelete(Connection connection) {
		int count = 0;

		// Deletes the address
		count += new Sql<>(Address.class)
			.where("{id} = {}", addressId)
			.delete(connection);

		// Deletes phones
		count += new Sql<>(Phone.class)
			.where("{contactId} = {}", id)
			.delete(connection);

		return count;
	}
}
