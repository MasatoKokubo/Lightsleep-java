// ContactComposite.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lightsleep.Sql;
import org.lightsleep.connection.ConnectionWrapper;
import org.lightsleep.entity.*;

/**
 * The composite entity of contact, address and phone table.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
@Table("super")
// public class ContactComposite extends Contact implements PreInsert, Composite { // 3.2.0
public class ContactComposite extends Contact implements PreInsert, PostSelect, PostInsert, PostUpdate, PostDelete {
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
	public void postSelect(ConnectionWrapper conn) {
		// Select and get the address
		if (addressId != 0)
			address = new Sql<>(Address.class)
				.where("{id}={}", addressId)
				.connection(conn)
				.select().orElse(null);

		// Select and get phones
		if (id != 0)
			new Sql<>(Phone.class)
				.where("{contactId}={}", id)
				.orderBy("{phoneNumber}")
				.connection(conn)
				.select(phones::add);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void preInsert(ConnectionWrapper conn) {
		// Insert the address
		new Sql<>(Address.class)
			.connection(conn)
			.insert(address);
		addressId = address.id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postInsert(ConnectionWrapper conn) {
		super.postInsert(conn);

		// Insert phones
		phones.forEach(phone -> phone.contactId = id);
		new Sql<>(Phone.class)
			.connection(conn)
			.insert(phones);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postUpdate(ConnectionWrapper conn) {
		if (addressId != 0) {
			// Update the address
			int updateCount = new Sql<>(Address.class)
				.connection(conn)
				.update(address);
			if (updateCount == 0)
				// Not Updated
				// Inserts  the address
				new Sql<>(Address.class)
					.connection(conn)
					.insert(address);
		} else
			// Delete the address
			new Sql<>(Address.class)
				.where("{id}={}", addressId)
				.connection(conn)
				.delete();

		List<Integer> phoneIds = phones.stream()
			.map(phone -> phone.id)
			.filter(id -> id != 0)
			.collect(Collectors.toList());

		// Delete phones
		new Sql<>(Phone.class)
			.where("{contactId}={}", id)
			.doIf(phoneIds.size() > 0,
				sql -> sql.and("{id} NOT IN {}", phoneIds)
			)
			.connection(conn)
			.delete();

		// Uptete phones
		new Sql<>(Phone.class)
			.connection(conn)
			.update(phones.stream()
				.filter(phone -> phone.id != 0)
				.collect(Collectors.toList()));

		// Insert phones
		new Sql<>(Phone.class)
			.connection(conn)
			.insert(phones.stream()
				.filter(phone -> phone.id == 0)
				.collect(Collectors.toList()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postDelete(ConnectionWrapper conn) {
		// Delete the address
		new Sql<>(Address.class)
			.where("{id}={}", addressId)
			.connection(conn)
			.delete();

		// Delete phones
		new Sql<>(Phone.class)
			.where("{contactId}={}", id)
			.connection(conn)
			.delete();
	}
}
