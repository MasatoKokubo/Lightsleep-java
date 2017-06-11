// LogicalConditionSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.component

import java.util.stream.Stream

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.entity.*

import spock.lang.*

// LogicalConditionSpec
@Unroll
class LogicalConditionSpec extends Specification {
	static class Entity {
		@Key id
	}

	def "LogicalConditionSpec #className #type #elements -> #sql"(
		Class<? extends LogicalCondition> clazz, String type, List<String> elements, String sql, String className) {
	/**/DebugTrace.enter()
		when:
			def expressions = elements.collect {new Expression(it)}
			def condition = null
			def condition2 = null
			if (clazz == And.class) {
				switch (type) {
				case 'list':
					condition = new And(expressions)
					condition2 = Condition.and(expressions)
					break
				case 'array':
					condition = new And(expressions as Expression[])
					condition2 = Condition.and(expressions as Expression[])
					break
				case 'stream':
					condition = new And(expressions.stream())
					condition2 = Condition.and(expressions.stream())
					break
				default: assert false
				}
			} else if (clazz == Or.class) {
				switch (type) {
				case 'list':
					condition = new Or(expressions)
					condition2 = Condition.or(expressions)
					break
				case 'array':
					condition = new Or(expressions as Expression[])
					condition2 = Condition.or(expressions as Expression[])
					break
				case 'stream':
					condition = new Or(expressions.stream())
					condition2 = Condition.or(expressions.stream())
					break
				default: assert false
				}
			} else assert false
		/**/DebugTrace.print('condition', condition)
			def string = condition.toString(new Sql<>(Entity.class), [])
			def string2 = condition2.toString(new Sql<>(Entity.class), [])
		/**/DebugTrace.print('string', string)
		/**/DebugTrace.print('string2', string2)

		then:
			string == sql
			string2 == sql

			if (string.empty)
				assert condition.empty
			else
				assert !condition.empty

			if (string2.empty)
				assert condition2 == Condition.EMPTY
			else if (string2.indexOf('AND') >= 0)
				assert condition2 instanceof And
			else if (string2.indexOf('OR') >= 0 )
				assert condition2 instanceof Or
			else {
				assert !condition2.empty
				assert condition2 instanceof Expression
			}

	/**/DebugTrace.leave()

		where:
			clazz|type    |elements       |sql
			And  |'list'  |[]             |''
			And  |'list'  |['' ]          |''
			And  |'list'  |['A']          |'A'
			And  |'list'  |['' , '' ]     |''
			And  |'list'  |['' , 'B']     |'B'
			And  |'list'  |['A', '' ]     |'A'
			And  |'list'  |['A', 'B']     |'A AND B'
			And  |'list'  |['' , '' , '' ]|''
			And  |'list'  |['' , '' , 'C']|'C'
			And  |'list'  |['' , 'B', '' ]|'B'
			And  |'list'  |['' , 'B', 'C']|'B AND C'
			And  |'list'  |['A', '' , '' ]|'A'
			And  |'list'  |['A', '' , 'C']|'A AND C'
			And  |'list'  |['A', 'B', '' ]|'A AND B'
			And  |'list'  |['A', 'B', 'C']|'A AND B AND C'
			And  |'array' |[]             |''
			And  |'array' |['' ]          |''
			And  |'array' |['A']          |'A'
			And  |'array' |['' , '' ]     |''
			And  |'array' |['' , 'B']     |'B'
			And  |'array' |['A', '' ]     |'A'
			And  |'array' |['A', 'B']     |'A AND B'
			And  |'array' |['' , '' , '' ]|''
			And  |'array' |['' , '' , 'C']|'C'
			And  |'array' |['' , 'B', '' ]|'B'
			And  |'array' |['' , 'B', 'C']|'B AND C'
			And  |'array' |['A', '' , '' ]|'A'
			And  |'array' |['A', '' , 'C']|'A AND C'
			And  |'array' |['A', 'B', '' ]|'A AND B'
			And  |'array' |['A', 'B', 'C']|'A AND B AND C'
			And  |'stream'|[]             |''
			And  |'stream'|['' ]          |''
			And  |'stream'|['A']          |'A'
			And  |'stream'|['' , '' ]     |''
			And  |'stream'|['' , 'B']     |'B'
			And  |'stream'|['A', '' ]     |'A'
			And  |'stream'|['A', 'B']     |'A AND B'
			And  |'stream'|['' , '' , '' ]|''
			And  |'stream'|['' , '' , 'C']|'C'
			And  |'stream'|['' , 'B', '' ]|'B'
			And  |'stream'|['' , 'B', 'C']|'B AND C'
			And  |'stream'|['A', '' , '' ]|'A'
			And  |'stream'|['A', '' , 'C']|'A AND C'
			And  |'stream'|['A', 'B', '' ]|'A AND B'
			And  |'stream'|['A', 'B', 'C']|'A AND B AND C'
			Or   |'list'  |[]             |''
			Or   |'list'  |['' ]          |''
			Or   |'list'  |['A']          |'A'
			Or   |'list'  |['' , '' ]     |''
			Or   |'list'  |['' , 'B']     |'B'
			Or   |'list'  |['A', '' ]     |'A'
			Or   |'list'  |['A', 'B']     |'A OR B'
			Or   |'list'  |['' , '' , '' ]|''
			Or   |'list'  |['' , '' , 'C']|'C'
			Or   |'list'  |['' , 'B', '' ]|'B'
			Or   |'list'  |['' , 'B', 'C']|'B OR C'
			Or   |'list'  |['A', '' , '' ]|'A'
			Or   |'list'  |['A', '' , 'C']|'A OR C'
			Or   |'list'  |['A', 'B', '' ]|'A OR B'
			Or   |'list'  |['A', 'B', 'C']|'A OR B OR C'
			Or   |'array' |[]             |''
			Or   |'array' |['' ]          |''
			Or   |'array' |['A']          |'A'
			Or   |'array' |['' , '' ]     |''
			Or   |'array' |['' , 'B']     |'B'
			Or   |'array' |['A', '' ]     |'A'
			Or   |'array' |['A', 'B']     |'A OR B'
			Or   |'array' |['' , '' , '' ]|''
			Or   |'array' |['' , '' , 'C']|'C'
			Or   |'array' |['' , 'B', '' ]|'B'
			Or   |'array' |['' , 'B', 'C']|'B OR C'
			Or   |'array' |['A', '' , '' ]|'A'
			Or   |'array' |['A', '' , 'C']|'A OR C'
			Or   |'array' |['A', 'B', '' ]|'A OR B'
			Or   |'array' |['A', 'B', 'C']|'A OR B OR C'
			Or   |'stream'|[]             |''
			Or   |'stream'|['' ]          |''
			Or   |'stream'|['A']          |'A'
			Or   |'stream'|['' , '' ]     |''
			Or   |'stream'|['' , 'B']     |'B'
			Or   |'stream'|['A', '' ]     |'A'
			Or   |'stream'|['A', 'B']     |'A OR B'
			Or   |'stream'|['' , '' , '' ]|''
			Or   |'stream'|['' , '' , 'C']|'C'
			Or   |'stream'|['' , 'B', '' ]|'B'
			Or   |'stream'|['' , 'B', 'C']|'B OR C'
			Or   |'stream'|['A', '' , '' ]|'A'
			Or   |'stream'|['A', '' , 'C']|'A OR C'
			Or   |'stream'|['A', 'B', '' ]|'A OR B'
			Or   |'stream'|['A', 'B', 'C']|'A OR B OR C'
			className = clazz.simpleName
	}

	def "LogicalConditionSpec #className #type - exception - argument is null"(
		Class<? extends LogicalCondition> clazz, String type, String className) {
	/**/DebugTrace.enter()
		when:
			if (clazz == And.class) {
				switch (type) {
				case 'list'  : new And(null as List<Condition>  ); break
				case 'array' : new And(null as Condition[]      ); break
				case 'stream': new And(null as Stream<Condition>); break
				default: assert false
				}
			} else if (clazz == Or.class) {
				switch (type) {
				case 'list'  : new Or(null as List<Condition>  ); break
				case 'array' : new Or(null as Condition[]      ); break
				case 'stream': new Or(null as Stream<Condition>); break
				default: assert false
				}
			} else assert false

		then:
			def e = thrown NullPointerException
		/**/DebugTrace.print('e', e)
			
	/**/DebugTrace.leave()

		where:
			clazz|type    
			And  |'list'  
			And  |'array' 
			And  |'stream'
			Or   |'list'  
			Or   |'array' 
			Or   |'stream'
			className = clazz.simpleName
	}

	def "LogicalConditionSpec #className #type - exception - element of argument is null"(
		Class<? extends LogicalCondition> clazz, String type, String className) {
	/**/DebugTrace.enter()
		when:
			if (clazz == And.class) {
				switch (type) {
				case 'list'  : new And([null]); break
				case 'array' : new And([null] as Condition[]); break
				case 'stream': new And([null].stream()); break
				default: assert false
				}
			} else if (clazz == Or.class) {
				switch (type) {
				case 'list'  : new Or([null]); break
				case 'array' : new Or([null] as Condition[]); break
				case 'stream': new Or([null].stream()); break
				default: assert false
				}
			} else assert false

		then:
			def e = thrown NullPointerException
		/**/DebugTrace.print('e', e)
			
	/**/DebugTrace.leave()

		where:
			clazz|type    
			And  |'list'  
			And  |'array' 
			And  |'stream'
			Or   |'list'  
			Or   |'array' 
			Or   |'stream'
			className = clazz.simpleName
	}
}
