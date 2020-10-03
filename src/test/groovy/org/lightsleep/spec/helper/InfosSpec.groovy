// InfoSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.helper

import java.util.ArrayList
import java.util.Arrays
import java.util.List

import org.debugtrace.DebugTrace
import org.lightsleep.Sql
import org.lightsleep.component.*
import org.lightsleep.entity.*
import org.lightsleep.helper.*

import spock.lang.*

// InfosSpec
@Unroll
class InfosSpec extends Specification {
    @Table('Entity1a')
    static class Entity1Base {
        @Key
        @Column('value1a')
        public int value1

        @Key
        @Select('1')
        public int value2

        @Insert('0')
        public int value3

        @Update('value4 + 1')
        public int value4

        @NonSelect
        public int value5

        @NonInsert
        public int value6

        @NonUpdate
        public int value7

        @NonColumn
        public int value8
    }

    @Table('super')
    static class Entity1 extends Entity1Base {
    }

    static class Entity2 {
        @Key
        @Column('value1b')
        public int value1

        @Key
        @Select('1')
        public int value2

        @Insert('0')
        public int value3

        @Update('value4 + 1')
        public int value4

        @NonSelect
        public int value5

        @NonInsert
        public int value6

        @NonUpdate
        public int value7

        @NonColumn
        public int value8
    }

    def "InfosSpec 1 EntityInfo 1 exception"() {
        DebugTrace.enter() // for Debugging

        when: new EntityInfo<>(null)
        then: thrown NullPointerException

        DebugTrace.leave() // for Debugging
    }

    def "InfosSpec 1 EntityInfo 2 normal"() {
        DebugTrace.enter() // for Debugging

        when:
            def entity1Info = new EntityInfo<>(Entity1)
            def entity2Info = new EntityInfo<>(Entity2)

        then:
            entity1Info.entityClass   () == Entity1
            entity1Info.accessor      () != null
            entity1Info.tableName     () == 'Entity1a'
            entity2Info.tableName     () == 'Entity2'
            entity1Info.columnInfos   ().size() == 7
            entity1Info.keyColumnInfos().size() == 2

        DebugTrace.leave() // for Debugging
    }

    def "InfosSpec 1 EntityInfo 3 normal"() {
        DebugTrace.enter() // for Debugging

        when:
            EntityInfo<Entity1> entity1Info  = new EntityInfo<>(Entity1)
            EntityInfo<Entity1> entity1Info2 = new EntityInfo<>(Entity1)
            EntityInfo<Entity2> entity2Info  = new EntityInfo<>(Entity2)

        then:
            entity1Info.getColumnInfo('value1').entityInfo  () == entity1Info
            entity1Info.getColumnInfo('value1').propertyName() == 'value1'
            entity1Info.getColumnInfo('value1').columnName  () == 'value1a'

            entity1Info.getColumnInfo('value1').isKey       ()
            entity1Info.getColumnInfo('value2').isKey       ()
            !entity1Info.getColumnInfo('value3').isKey      ()

            entity1Info.getColumnInfo('value1').selectable  ()
            !entity1Info.getColumnInfo('value5').selectable ()

            entity1Info.getColumnInfo('value1').insertable  ()
            !entity1Info.getColumnInfo('value6').insertable ()

            !entity1Info.getColumnInfo('value1').updatable  () // because it is a key
            !entity1Info.getColumnInfo('value7').updatable  ()

            entity1Info.getColumnInfo('value1').selectExpression() == Expression.EMPTY
            entity1Info.getColumnInfo('value2').selectExpression().content() == '1'

            entity1Info.getColumnInfo('value1').insertExpression() == Expression.EMPTY
            entity1Info.getColumnInfo('value3').insertExpression().content() == '0'

            entity1Info.getColumnInfo('value1').updateExpression() == null // because it is a key
            entity1Info.getColumnInfo('value4').updateExpression().content() == 'value4 + 1'

            entity1Info.getColumnInfo('value1').getColumnName ('' ) == 'value1a'
            entity1Info.getColumnInfo('value1').getColumnName ('E') == 'E.value1a'
            entity1Info.getColumnInfo('value1').getColumnAlias('' ) == 'value1a'
            entity1Info.getColumnInfo('value1').getColumnAlias('E') == 'E_value1a'

            entity1Info.getColumnInfo('value1') == entity1Info.getColumnInfo('value1')
            entity1Info.getColumnInfo('value1') != null
            entity1Info.getColumnInfo('value1') != entity1Info
            entity1Info.getColumnInfo('value1') != entity1Info.getColumnInfo('value2')
            entity1Info.getColumnInfo('value1') != entity2Info.getColumnInfo('value1')
            entity1Info.getColumnInfo('value1') == entity1Info2.getColumnInfo('value1')

            entity1Info.getColumnInfo('value1').hashCode() == entity2Info.getColumnInfo('value1').hashCode()

        DebugTrace.leave() // for Debugging
    }

    def "InfosSpec 2 ColumnInfo 1 exception"() {
        DebugTrace.enter() // for Debugging

        when:
            def entity1Info = new EntityInfo<>(Entity1)
            new ColumnInfo(null, null, null, null, false, null, null, null)

        then: thrown NullPointerException

        DebugTrace.leave() // for Debugging
    }

    def "InfosSpec 2 ColumnInfo 2 exception"() {
        DebugTrace.enter() // for Debugging

        when:
            def entity1Info = new EntityInfo<>(Entity1)
            new ColumnInfo(entity1Info, null, null, null, false, null, null, null)

        then: thrown NullPointerException
        DebugTrace.leave() // for Debugging
    }

    def "InfosSpec 2 ColumnInfo 3 exception"() {
        DebugTrace.enter() // for Debugging

        when:
            def entity1Info = new EntityInfo<>(Entity1)
            new ColumnInfo(entity1Info, 'value1', null, null, false, null, null, null)

        then: thrown NullPointerException

        DebugTrace.leave() // for Debugging
    }

    def "InfosSpec 2 ColumnInfo 4 columnName"() {
        DebugTrace.enter() // for Debugging

        when:
            List<String> columnNames = []
            new Sql<>(Entity1, 'E')
                .sqlColumnInfoStream()
                .each {SqlColumnInfo sqlColumnInfo ->
                    assert sqlColumnInfo.tableAlias() == 'E'
                    columnNames.add(sqlColumnInfo.columnInfo().columnName())
                }
            DebugTrace.print('columnNames', columnNames) // for Debugging

        then:
            columnNames == [
                'value1a',
                'value2',
                'value3',
                'value4',
                'value5',
                'value6',
                'value7'
            ]

        DebugTrace.leave() // for Debugging
    }

    def "InfosSpec 2 ColumnInfo 5 columnName"() {
        DebugTrace.enter() // for Debugging

        when:
            List<String> columnNames = []
            new Sql<>(Entity1, 'E')
                .selectedJoinSqlColumnInfoStream()
                .each {SqlColumnInfo sqlColumnInfo ->
                    assert sqlColumnInfo.tableAlias() == 'E'
                    columnNames.add(sqlColumnInfo.columnInfo().columnName())
                }
            DebugTrace.print('columnNames', columnNames) // for Debugging

        then:
            columnNames == [
                'value1a',
                'value2',
                'value3',
                'value4',
                'value5',
                'value6',
                'value7'
            ]

        DebugTrace.leave() // for Debugging
    }

    def "InfosSpec 2 ColumnInfo 6 columnName"() {
        DebugTrace.enter() // for Debugging

        when:
            List<String> columnNames = []
            new Sql<>(Entity1, 'E')
                .columns('E.value6', 'E.value7', 'value7', 'E.value8')
                .selectedJoinSqlColumnInfoStream()
                .each {SqlColumnInfo sqlColumnInfo ->
                    assert sqlColumnInfo.tableAlias() == 'E'
                    columnNames.add(sqlColumnInfo.columnInfo().columnName())
                }
            DebugTrace.print('columnNames', columnNames) // for Debugging

        then: columnNames == ['value6', 'value7']


        DebugTrace.leave() // for Debugging
    }

    def "InfosSpec 2 ColumnInfo 7 columnName"() {
        DebugTrace.enter() // for Debugging

        when:
            List<String> columnNames = []
            new Sql<>(Entity1)
                .columns('*')
                .selectedJoinSqlColumnInfoStream()
                .each {SqlColumnInfo sqlColumnInfo ->
                    assert sqlColumnInfo.tableAlias() == ''
                    columnNames.add(sqlColumnInfo.columnInfo().columnName())
                }
            DebugTrace.print('columnNames', columnNames) // for Debugging

        then:
            columnNames == [
                'value1a',
                'value2',
                'value3',
                'value4',
                'value5',
                'value6',
                'value7'
            ]

        DebugTrace.leave() // for Debugging
    }

    def "InfosSpec 2 ColumnInfo 8 columnName"() {
        DebugTrace.enter() // for Debugging

        when:
            List<String> columnNames = []
            new Sql<>(Entity1, 'E1')

                .innerJoin(Entity2, 'E2',
                    Condition.of('{E2.value1} = {E1.value1}').and('{E2.value2} = {E1.value2}'))

                .leftJoin(Entity1, 'E1_2',
                    Condition.of('{E1_2.value1} = {E1.value1}').and('{E1_2.value2} = {E1.value2}'))

                .rightJoin(Entity2, 'E2_2',
                    Condition.of('{E2_2.value1} = {E1.value1}').and('{E2_2.value2} = {E1.value2}'))

                .columns('value1', 'value2', 'value3', 'E2_2.value7')
                .selectedJoinSqlColumnInfoStream()
                .each {SqlColumnInfo sqlColumnInfo ->
                    columnNames.add(sqlColumnInfo.columnInfo().getColumnName(sqlColumnInfo.tableAlias()))
                }
            DebugTrace.print('columnNames', columnNames) // for Debugging

        then:
            columnNames == [
                'E1.value1a',
                'E1.value2',
                'E1.value3',
            //    'E1.value4',
            //    'E1.value5',
            //    'E1.value6',
            //    'E1.value7',
                'E2.value1b',
                'E2.value2',
                'E2.value3',
            //    'E2.value4',
            //    'E2.value5',
            //    'E2.value6',
            //    'E2.value7',
                'E1_2.value1a',
                'E1_2.value2',
                'E1_2.value3',
            //    'E1_2.value4',
            //    'E1_2.value5',
            //    'E1_2.value6',
            //    'E1_2.value7',
                'E2_2.value1b',
                'E2_2.value2',
                'E2_2.value3',
            //    'E2_2.value4',
            //    'E2_2.value5',
            //    'E2_2.value6',
                'E2_2.value7'
            ]
        ////

        DebugTrace.leave() // for Debugging
    }

    def "InfosSpec 3 JoinInfo 1 exception"() {
        DebugTrace.enter() // for Debugging

        when: new JoinInfo<>(null, (EntityInfo)null, null, null)
        then: thrown NullPointerException

        when: new JoinInfo<>(null, (Sql)null, null, null)
        then: thrown NullPointerException

        DebugTrace.leave() // for Debugging
    }

    def "InfosSpec 3 JoinInfo 2 exception"() {
        DebugTrace.enter() // for Debugging

        when: new JoinInfo<>(JoinInfo.JoinType.INNER, (EntityInfo)null, null, null)
        then: thrown NullPointerException

        when: new JoinInfo<>(JoinInfo.JoinType.INNER, (Sql)null, null, null)
        then: thrown NullPointerException

        DebugTrace.leave() // for Debugging
    }

    def "InfosSpec 3 JoinInfo 3 exception"() {
        DebugTrace.enter() // for Debugging

        when:
            def entity1Info = new EntityInfo<>(Entity1)
            new JoinInfo<>(JoinInfo.JoinType.LEFT, entity1Info, null, null)
        then: thrown NullPointerException

        when:
            def sql1 = new Sql<>(Entity1)
            new JoinInfo<>(JoinInfo.JoinType.LEFT, sql1, null, null)
        then: thrown NullPointerException

        DebugTrace.leave() // for Debugging
    }

    def "InfosSpec 3 JoinInfo 4 exception"() {
        DebugTrace.enter() // for Debugging

        when:
            def entity1Info = new EntityInfo<>(Entity1)
            new JoinInfo<>(JoinInfo.JoinType.RIGHT, entity1Info, '', null)
        then: thrown NullPointerException

        when:
            def sql1 = new Sql<>(Entity1)
            new JoinInfo<>(JoinInfo.JoinType.RIGHT, sql1, '', null)
        then: thrown NullPointerException

        DebugTrace.leave() // for Debugging
    }

    def "InfosSpec 3 JoinInfo 5 normal"() {
        DebugTrace.enter() // for Debugging

        when:
            def entity1Info = new EntityInfo<>(Entity1)
            def joinInfo = new JoinInfo<>(JoinInfo.JoinType.LEFT, entity1Info, 'E', new Expression('a'))

        then:
            joinInfo.joinType() == JoinInfo.JoinType.LEFT
            joinInfo.joinType().sql() == ' LEFT OUTER JOIN '
            joinInfo.entityInfo().entityClass() == Entity1
            joinInfo.tableAlias() == 'E'
            joinInfo.entity() == null
            ((Expression)joinInfo.on()).content() == 'a'

            DebugTrace.leave() // for Debugging
    }
}
