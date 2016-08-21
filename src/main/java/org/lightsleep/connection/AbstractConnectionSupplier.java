/*
	AbstractConnectionSupplier.java
	Copyright (c) 2016 Masato Kokubo
*/
package org.lightsleep.connection;

import java.util.Properties;

import org.lightsleep.helper.Resource;

/**
	The abstract connection supplier

	@since 1.1.0
	@author Masato Kokubo
*/
public abstract class AbstractConnectionSupplier implements ConnectionSupplier {
	// The properties
	protected Properties properties = Resource.globalResource.getProperties();

	/**
		Constructs a new <b>AbstractConnectionSupplier</b>.
		Use values specified in the lightsleep.properties as the connection information.
	*/
	public AbstractConnectionSupplier() {
		init();
	}

	/**
		Constructs a new <b>AbstractConnectionSupplier</b>.
		Use values specified in the lightsleep.properties and the <i>&lt<b>resourceName<b>&gt<i>.properties file as the connection information.

		@param resourceName the resource name
	*/
	public AbstractConnectionSupplier(String resourceName) {
		properties.putAll(new Resource(resourceName).getProperties());
		init();
	}

	/**
		Initialize this object
	*/
	protected abstract void init();
}
