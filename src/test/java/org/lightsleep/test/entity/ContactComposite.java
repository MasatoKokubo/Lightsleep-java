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
	public void postSelect(Connection conn) {
		// Select and get the address
		if (addressId != 0)
			address = new Sql<>(Address.class).connection(conn)
				.where("{id}={}", addressId)
				.select().orElse(null);

		// Select and get phones
		if (id != 0)
			new Sql<>(Phone.class).connection(conn)
				.where("{contactId}={}", id)
				.orderBy("{phoneNumber}")
				.select(phones::add);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int preInsert(Connection conn) {
		super.preInsert(conn);

		int count = 0;

		// Insert the address
		count += new Sql<>(Address.class).connection(conn)
			.insert(address);
		addressId = address.id;

		return count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int postInsert(Connection conn) {
		int count = 0;

		// Insert phones
		phones.forEach(phone -> phone.contactId = id);
		count += new Sql<>(Phone.class).connection(conn)
			.insert(phones);

		return count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int postUpdate(Connection conn) {
		int count = 0;

		if (addressId != 0) {
			// Update the address
			int updateCount = new Sql<>(Address.class).connection(conn)
				.update(address);
			if (updateCount != 0)
				// Updated
				count += updateCount;
			else
				// Not Updated
				// Inserts  the address
				count += new Sql<>(Address.class).connection(conn)
					.insert(address);
		} else
			// Delete the address
			count += new Sql<>(Address.class).connection(conn)
				.where("{id}={}", addressId)
				.delete();

		List<Integer> phoneIds = phones.stream()
			.map(phone -> phone.id)
			.filter(id -> id != 0)
			.collect(Collectors.toList());

		// Delete phones
		count += new Sql<>(Phone.class).connection(conn)
			.where("{contactId}={}", id)
			.doIf(phoneIds.size() > 0,
				sql -> sql.and("{id} NOT IN {}", phoneIds)
			)
			.delete();

		// Uptete phones
		count += new Sql<>(Phone.class).connection(conn)
			.update(phones.stream()
				.filter(phone -> phone.id != 0)
				.collect(Collectors.toList()));

		// Insert phones
		count += new Sql<>(Phone.class).connection(conn)
			.insert(phones.stream()
				.filter(phone -> phone.id == 0)
				.collect(Collectors.toList()));

		return count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int postDelete(Connection conn) {
		int count = 0;

		// Delete the address
		count += new Sql<>(Address.class).connection(conn)
			.where("{id}={}", addressId)
			.delete();

		// Delete phones
		count += new Sql<>(Phone.class).connection(conn)
			.where("{contactId}={}", id)
			.delete();

		return count;
	}
}
