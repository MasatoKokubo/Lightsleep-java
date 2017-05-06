// UtilTest.java
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.helper

import org.debugtrace.DebugTrace;
import org.lightsleep.entity.*;
import org.lightsleep.helper.Utils;

import spock.lang.*

// UtilSpec
@Unroll
public class UtilSpec extends Specification {
	@NonColumnProperties([
		@NonColumnProperty('test1_1'),
		@NonColumnProperty('test1_2')
	])
	public static class Test1 {
	}

	@NonColumnProperty('test2')
	public static class Test2 extends Test1 {
	}

	@NonColumnProperties([
		@NonColumnProperty('test3_1'),
		@NonColumnProperty('test3_2')
	])
	public static class Test3 extends Test2 {
	}

	@NonColumnProperty('test4')
	public static class Test4 extends Test3 {
	}

	def "getAnnotations"() {
	/**/DebugTrace.enter();
		when:
			def nonColumnProperties = Utils.getAnnotations(Test4.class, NonColumnProperty.class);
		/**/DebugTrace.print('nonColumnProperties', nonColumnProperties);

		then:
			nonColumnProperties.collect {it.value()} == [
				'test1_1',
				'test1_2',
				'test2',
				'test3_1',
				'test3_2',
				'test4'
			]

	/**/DebugTrace.leave();
	}
}
