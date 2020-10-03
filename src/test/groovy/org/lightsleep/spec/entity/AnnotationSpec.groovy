// AnnotationSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec.entity

import java.sql.Timestamp
import java.util.ArrayList

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.connection.*
import org.lightsleep.database.*
import org.lightsleep.component.*
import org.lightsleep.entity.*

import spock.lang.*

// AnnotationSpec
@Unroll
class AnnotationSpec extends Specification {
    static class TestEntity1 {
        @Key
        public int key = -1

        public int c1 = 1

        public int c2 = 2

        @Column('_c3_')
        public int c3 = 3

        @Select('c4 * 4')
        @Insert('44')
        @Update('444')
        public int c4 = 4

        @NonSelect
        public int c5 = 5

        @NonInsert
        public int c6 = 6

        @NonUpdate
        public int c7 = 7

        @NonColumn
        public int c8 = 8

        public int c9 = 9
    }

    @Table('_TestEntity2_')
    static class TestEntity2 {
        public int test1 = 1
    }

    static class TestEntity3 {
    }

    @Table('super')
    @KeyProperty(property='key2')
    @ColumnProperty(property='c13', column='_c13_')
    @SelectProperty(property='c14', expression='c14 * 14')
    @InsertProperty(property='c14', expression='144')
    @UpdateProperty(property='c14', expression='1444')
    @NonSelectProperty(property='c15')
    @NonInsertProperty(property='c16')
    @NonUpdateProperty(property='c17')
    @NonColumnProperty(property='c18')
    static class TestEntity4 extends TestEntity1 {
        public int key2 = -2

        public int c11 = 11
        public int c12 = 12
        public int c13 = 13
        public int c14 = 14
        public int c15 = 15
        public int c16 = 16
        public int c17 = 17
        public int c18 = 18
        public int c19 = 19
    }

    @Table('super')
    @KeyProperties([
        @KeyProperty(property='key2'),
        @KeyProperty(property='key3')
    ])
    @ColumnProperties([
        @ColumnProperty(property='c13', column='_c13_'),
        @ColumnProperty(property='sub.c23', column='_c23_')
    ])
    @SelectProperties([
        @SelectProperty(property='c14', expression='c14 * 14'),
        @SelectProperty(property='sub.c24', expression='c24 * 24')
    ])
    @InsertProperties([
        @InsertProperty(property='c14', expression='144'),
        @InsertProperty(property='sub.c24', expression='244')
    ])
    @UpdateProperties([
        @UpdateProperty(property='c14', expression='1444'),
        @UpdateProperty(property='sub.c24', expression='2444')
    ])
    @NonSelectProperties([
        @NonSelectProperty(property='c15'),
        @NonSelectProperty(property='sub.c25')
    ])
    @NonInsertProperties([
        @NonInsertProperty(property='c16'),
        @NonInsertProperty(property='sub.c26')
    ])
    @NonUpdateProperties([
        @NonUpdateProperty(property='c17'),
        @NonUpdateProperty(property='sub.c27')
    ])
    @NonColumnProperties([
        @NonColumnProperty(property='c18'),
        @NonColumnProperty(property='sub.c28')
    ])
    static class TestEntity5 extends TestEntity1 {
        public int key2 = -2
        public int key3 = -3

        public int c11 = 11
        public int c12 = 12
        public int c13 = 13
        public int c14 = 14
        public int c15 = 15
        public int c16 = 16
        public int c17 = 17
        public int c18 = 18
        public int c19 = 19

        static class Sub {
            public int c21 = 21
            public int c22 = 22
            public int c23 = 23
            public int c24 = 24
            public int c25 = 25
            public int c26 = 26
            public int c27 = 27
            public int c28 = 28
            public int c29 = 29
        }

        public Sub sub = new Sub()
    }

    def "AnnotationSpec field annotations"() {
        DebugTrace.enter() // for Debugging

        setup:
            def sql1 = new Sql<>(TestEntity1)
            sql1.where(new TestEntity1()).setEntity(new TestEntity1())

            def sql2 = new Sql<>(TestEntity2)
            sql2.setEntity(new TestEntity2())

            def sql3 = new Sql<>(TestEntity3)
            sql3.setEntity(new TestEntity3())

        when: def selectSql1 = Standard.instance.selectSql(sql1, new ArrayList<Object>()).toString()
        then:
            selectSql1.startsWith("SELECT ")
            selectSql1.indexOf("FROM TestEntity1") >= 0
            selectSql1.indexOf("key, c1, c2, _c3_, c4 * 4 c4, c6, c7, c9") >= 0

        when: def insertSql1 = Standard.instance.insertSql(sql1, new ArrayList<Object>()).toString()
        then:
            insertSql1.startsWith("INSERT INTO TestEntity1")
            insertSql1.indexOf("key, c1, c2, _c3_, c4, c5, c7, c9") >= 0
            insertSql1.indexOf("-1, 1, 2, 3, 44, 5, 7, 9") >= 0

        when: def updateSql1 = Standard.instance.updateSql(sql1, new ArrayList<Object>()).toString()
        then:
            updateSql1.startsWith("UPDATE TestEntity1")
            updateSql1.indexOf("c1=1, c2=2, _c3_=3, c4=444, c5=5, c6=6, c9=9") >= 0
            updateSql1.indexOf("key=-1") >= 0

        when: def selectSql2 = Standard.instance.selectSql(sql2, new ArrayList<Object>()).toString()
        then: selectSql2.indexOf("FROM _TestEntity2_") >= 0

        when: def selectSql3 = Standard.instance.selectSql(sql3, new ArrayList<Object>()).toString()
        then: thrown IllegalStateException

        DebugTrace.leave() // for Debugging
    }

    def "AnnotationSpec type annotations"() {
        DebugTrace.enter() // for Debugging

        setup:
            def sql = new Sql<>(TestEntity4)
            sql.where(new TestEntity4()).setEntity(new TestEntity4())

        when: def selectSql = Standard.instance.selectSql(sql, new ArrayList<Object>()).toString()
        then:
            selectSql.startsWith("SELECT ")
            selectSql.indexOf("FROM TestEntity1") >= 0
            selectSql.indexOf("key, c1, c2, _c3_, c4 * 4 c4, c6, c7, c9, key2, c11, c12, _c13_, c14 * 14 c14, c16, c17, c19") >= 0

        when: def insertSql = Standard.instance.insertSql(sql, new ArrayList<Object>()).toString()
        then:
            insertSql.startsWith("INSERT INTO TestEntity1")
            insertSql.indexOf("key, c1, c2, _c3_, c4, c5, c7, c9, key2, c11, c12, _c13_, c14, c15, c17, c19") >= 0
            insertSql.indexOf("-1, 1, 2, 3, 44, 5, 7, 9, -2, 11, 12, 13, 144, 15, 17, 19") >= 0

        when: def updateSql = Standard.instance.updateSql(sql, new ArrayList<Object>()).toString()
        then:
            updateSql.startsWith("UPDATE TestEntity1")
            updateSql.indexOf("c1=1, c2=2, _c3_=3, c4=444, c5=5, c6=6, c9=9, c11=11, c12=12, _c13_=13, c14=1444, c15=15, c16=16, c19=19") >= 0
            updateSql.indexOf("key=-1 AND key2=-2") >= 0

        DebugTrace.leave() // for Debugging
    }

    def "AnnotationSpec type annotations (multiple)"() {
        DebugTrace.enter() // for Debugging

        setup:
            def sql = new Sql<>(TestEntity5)
            sql.where(new TestEntity5()).setEntity(new TestEntity5())

        when: def selectSql = Standard.instance.selectSql(sql, new ArrayList<Object>()).toString()
        then:
            selectSql.startsWith("SELECT ")
            selectSql.indexOf("FROM TestEntity1") >= 0
            selectSql.indexOf("key, c1, c2, _c3_, c4 * 4 c4, c6, c7, c9, key2, key3, c11, c12, _c13_, c14 * 14 c14, c16, c17, c19, c21, c22, _c23_, c24 * 24 c24, c26, c27, c29") >= 0

        when: def insertSql = Standard.instance.insertSql(sql, new ArrayList<Object>()).toString()
        then:
            insertSql.startsWith("INSERT INTO TestEntity1")
            insertSql.indexOf("key, c1, c2, _c3_, c4, c5, c7, c9, key2, key3, c11, c12, _c13_, c14, c15, c17, c19, c21, c22, _c23_, c24, c25, c27, c29") >= 0
            insertSql.indexOf("-1, 1, 2, 3, 44, 5, 7, 9, -2, -3, 11, 12, 13, 144, 15, 17, 19, 21, 22, 23, 244, 25, 27, 29") >= 0

        when: def updateSql = Standard.instance.updateSql(sql, new ArrayList<Object>()).toString()
        then:
            updateSql.startsWith("UPDATE TestEntity1")
            updateSql.indexOf("c1=1, c2=2, _c3_=3, c4=444, c5=5, c6=6, c9=9, c11=11, c12=12, _c13_=13, c14=1444, c15=15, c16=16, c19=19, c21=21, c22=22, _c23_=23, c24=2444, c25=25, c26=26, c29=29") >= 0
            updateSql.indexOf("key=-1 AND key2=-2 AND key3=-3") >= 0

        DebugTrace.leave() // for Debugging
    }

    static class ColumnTypeEntityBase {
        @Key public int id
        @ColumnType(Long)
        public Timestamp timestamp1
        public Timestamp timestamp2
        public Timestamp timestamp3
    }

    @ColumnTypeProperties([
        @ColumnTypeProperty(property = "timestamp2", type = Long),
        @ColumnTypeProperty(property = "timestamp3", type = Long)
    ])
    static class ColumnTypeEntity extends ColumnTypeEntityBase {
    }

    def "AnnotationSpec ColumnType annotation"() {
        DebugTrace.enter() // for Debugging

        setup:
            def entity = new ColumnTypeEntity()
            entity.id = 1
            entity.timestamp1 = new Timestamp(-123456789L)
            entity.timestamp2 = new Timestamp(         0L)
            entity.timestamp3 = new Timestamp( 123456789L)

            def sql = new Sql<>(ColumnTypeEntity)
            sql.setEntity(entity).where(new EntityCondition<>(entity))

        when: def insertSql = Standard.instance.insertSql(sql, new ArrayList<Object>()).toString()
        then: insertSql == "INSERT INTO ColumnTypeEntity (id, timestamp1, timestamp2, timestamp3) VALUES (1, -123456789, 0, 123456789)"

        when: def updateSql = Standard.instance.updateSql(sql, new ArrayList<Object>()).toString()
        then: updateSql == "UPDATE ColumnTypeEntity SET timestamp1=-123456789, timestamp2=0, timestamp3=123456789 WHERE id=1"

        DebugTrace.leave() // for Debugging
    }
}
