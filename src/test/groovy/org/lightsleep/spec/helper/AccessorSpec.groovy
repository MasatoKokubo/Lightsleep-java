// AccessorSpec.java
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.helper


import java.lang.reflect.Field
import java.util.Arrays
import java.util.List

import org.debugtrace.DebugTrace
import org.lightsleep.entity.*
import org.lightsleep.helper.*

import spock.lang.*

@Unroll
// AccessorSpec
class AccessorSpec extends Specification {
	enum Size {XS, S, M, L, XL}

	static class Entity1Base {
		private int value1
		public int value1() {return value1}
		public void value1(int value1) {this.value1 = value1}
	}

	static class Entity1 extends Entity1Base {
		private String[]  value3
		public String[] getValue3() {return value3}
		public void setValue3(String[] value3) {this.value3 = value3}

		private long    value4
		public int value4() {return (int)value4}
		@NonColumn
		public  float   value5
		public  Entity4 value6

		public Size size
	}

	static class Entity2 {
		private boolean value2
		public boolean isValue2() {return value2}
		public void value2(boolean value2) {this.value2 = value2}

		public char value9
	}

	static class Entity3 {
		public Entity1 entity1 = new Entity1()

		private Entity2 entity2 = new Entity2()
		public Entity2 getEntity2() {return entity2}

		private byte value7
		public byte value7() {return value7}
		public void value7(byte value7) {this.value7 = value7}

		public short value8
	}

	@NonColumnProperty(property='metaClass')
	static class Entity4 {
	}

	@Shared Accessor<Entity3> entity3Accessor = new Accessor<>(Entity3)

	// constructor
	def constructor() {
	/**/DebugTrace.enter()

		when: new Accessor<>((Class<Entity1>)null)
		then: thrown NullPointerException

	/**/DebugTrace.leave()
	}

	// setValue, getValue
	def "AccessorSpec setValue getValue"() {
	/**/DebugTrace.enter()
		setup:
			Entity3 entity3 = new Entity3()

		when: entity3Accessor.setValue(entity3, 'entity1.value1', 123456789)
		then: entity3Accessor.getValue(entity3, 'entity1.value1') == 123456789

		when: entity3Accessor.setValue(entity3, 'entity1.value1', null)
		then: entity3Accessor.getValue(entity3, 'entity1.value1') == 123456789 // int

		when: entity3Accessor.setValue(entity3, 'entity1.value3', ['ABCDEFGH', 'abcdefgh'] as String[])
		then: entity3Accessor.getValue(entity3, 'entity1.value3') == ['ABCDEFGH', 'abcdefgh'] as String[]

		when: entity3Accessor.setValue(entity3, 'entity1.value3', null)
		then: entity3Accessor.getValue(entity3, 'entity1.value3') == null

		when: entity3Accessor.setValue(entity3, 'entity1.size', Size.M)
		then: entity3Accessor.getValue(entity3, 'entity1.size') == Size.M

		when: entity3Accessor.setValue(entity3, 'entity2.value2', true)
		then: entity3Accessor.getValue(entity3, 'entity2.value2') == true

		when: entity3Accessor.setValue(entity3, 'entity2.value9', 'A' as char)
		then: entity3Accessor.getValue(entity3, 'entity2.value9') == 'A' as char

		when: entity3Accessor.setValue(entity3, 'value7', (byte)127)
		then: entity3Accessor.getValue(entity3, 'value7') == (byte)127

		when: entity3Accessor.setValue(entity3, 'value8', (short)-32768)
		then: entity3Accessor.getValue(entity3, 'value8') == (short)-32768

		when: entity3Accessor.setValue(null, 'entity1.value1', 123456789)
		then: thrown NullPointerException

		when: entity3Accessor.setValue(entity3, 'entity1.value4', 123456789L)
		then:
			def e = thrown MissingPropertyException
		/**/DebugTrace.print('e', e)
			e.message.indexOf(Entity3.name) >= 0
			e.message.indexOf('entity1.value4') >= 0

		when: entity3Accessor.getValue(entity3, 'entity1.value4')
		then:
			e = thrown MissingPropertyException
		/**/DebugTrace.print('e', e)
			e.message.indexOf(Entity3.name) >= 0
			e.message.indexOf('entity1.value4') >= 0

	/**/DebugTrace.leave()
	}

	// propertyNames, valuePropertyNames, getField, getType, 
	def "AccessorSpec propertyNames, valuePropertyNames, getField, getType"() {
	/**/DebugTrace.enter()

		expect:
			entity3Accessor.propertyNames() == [
				'entity1',
				'entity1.value1',
				'entity1.value3',
				'entity1.value4',
				'entity1.value6',
				'entity1.size',
				'entity2',
				'entity2.value2',
				'entity2.value9',
				'value7',
				'value8'
			]

			entity3Accessor.valuePropertyNames() == [
				'entity1.value1',
				'entity1.value3',
				'entity1.value4',
				'entity1.size',
				'entity2.value2',
				'entity2.value9',
				'value7',
				'value8'
			]

		when: def entity1_value1Field = Entity1Base.getDeclaredField('value1')
		then:
			entity3Accessor.getField('entity1.value1') == entity1_value1Field
			entity3Accessor.getType('entity1.value1') == int
			entity3Accessor.getType('entity1.value3') == String[]

		when: entity3Accessor.getField('entity1.entityX')
		then:
			def e = thrown MissingPropertyException
		/**/DebugTrace.print('e', e)
			e.message.indexOf(Entity3.name) >= 0
			e.message.indexOf('entity1.entityX') >= 0

		when: entity3Accessor.getType('entity1.entityX')
		then:
			e = thrown MissingPropertyException
		/**/DebugTrace.print('e', e)
			e.message.indexOf(Entity3.name) >= 0
			e.message.indexOf('entity1.entityX') >= 0

	/**/DebugTrace.leave()
	}

}
