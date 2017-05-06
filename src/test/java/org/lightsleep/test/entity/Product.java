// Product.java
// (C) 2016 Masato Kokubo

package org.lightsleep.test.entity;

import org.lightsleep.database.*;
import org.lightsleep.helper.*;

/**
 * The entity of product table.
 *
 * @since 1.0
 * @author Masato Kokubo
 */
public class Product extends Common {
	static {
		TypeConverter<String, Size> typeConverter = new TypeConverter<>(String.class, Size.class,
			object -> Enum.valueOf(Size.class, object.trim())
		);

		TypeConverter.put(Standard  .instance().typeConverterMap(), typeConverter);
		TypeConverter.put(MySQL     .instance().typeConverterMap(), typeConverter);
		TypeConverter.put(Oracle    .instance().typeConverterMap(), typeConverter);
		TypeConverter.put(PostgreSQL.instance().typeConverterMap(), typeConverter);
		TypeConverter.put(SQLite    .instance().typeConverterMap(), typeConverter); // 1.7.0
		TypeConverter.put(SQLServer .instance().typeConverterMap(), typeConverter);
	}

	/** Size enum */
	public enum Size {XS, S, M, L, XL};

	/** Product Name */
	public String productName;

	/** Price */
	public int price;

	/** Size */
	public Size productSize;

	/** Color */
	public String color;
}
