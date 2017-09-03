// UtilTest.java
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.helper

import org.debugtrace.DebugTrace;
import org.lightsleep.entity.*;
import org.lightsleep.helper.Utils;
import org.lightsleep.spec.helper.AccessorSpec.Size

import spock.lang.*

// UtilSpec
@Unroll
public class UtilSpec extends Specification {
	@NonColumnProperties([
		@NonColumnProperty(property='test1_1'),
		@NonColumnProperty(property='test1_2')
	])
	public static class Test1 {
	}

	@NonColumnProperty(property='test2')
	public static class Test2 extends Test1 {
	}

	@NonColumnProperties([
		@NonColumnProperty(property='test3_1'),
		@NonColumnProperty(property='test3_2')
	])
	public static class Test3 extends Test2 {
	}

	@NonColumnProperty(property='test4')
	public static class Test4 extends Test3 {
	}

	def "UtilSpec getAnnotations"() {
	/**/DebugTrace.enter();

		when: def nonColumnProperties = Utils.getAnnotations(Test4, NonColumnProperty);
		then:
			nonColumnProperties*.property() == [
				'test1_1',
				'test1_2',
				'test2',
				'test3_1',
				'test3_2',
				'test4'
			]
			nonColumnProperties*.value() == [true]*nonColumnProperties.size()

	/**/DebugTrace.leave();
	}
}
