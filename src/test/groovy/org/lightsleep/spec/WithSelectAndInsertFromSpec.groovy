// SelectSpec.groovy
// (C) 2016 Masato Kokubo

package org.lightsleep.spec

import java.sql.Date

import org.debugtrace.DebugTrace
import org.lightsleep.*
import org.lightsleep.component.*
import org.lightsleep.connection.*
import org.lightsleep.database.*
import org.lightsleep.entity.*
import org.lightsleep.test.entity.*

import spock.lang.*

// SelectWithSpec
// @since 4.0.0
@Unroll
public class WithSelectAndInsertFromSpec extends Base {
    @Shared Node node0
    @Shared Node node1
    @Shared Node node2
    @Shared Node node3
    @Shared Node node4
    @Shared Node node1_1
    @Shared Node node1_2
    @Shared Node node1_3
    @Shared Node node2_1
    @Shared Node node2_2
    @Shared Node node2_3
    @Shared Node node3_1
    @Shared Node node3_2
    @Shared Node node3_3
    @Shared Node node4_1
    @Shared Node node4_2
    @Shared Node node4_3
    @Shared Leaf leaf0_1
    @Shared Leaf leaf0_2
    @Shared Leaf leaf1_1
    @Shared Leaf leaf1_2
    @Shared Leaf leaf2_1
    @Shared Leaf leaf2_2
    @Shared Leaf leaf3_1
    @Shared Leaf leaf3_2
    @Shared Leaf leaf1_1_1
    @Shared Leaf leaf1_1_2
    @Shared Leaf leaf1_2_1
    @Shared Leaf leaf1_2_2
    @Shared Leaf leaf1_3_1
    @Shared Leaf leaf1_3_2
    @Shared Leaf leaf2_1_1
    @Shared Leaf leaf2_1_2
    @Shared Leaf leaf2_2_1
    @Shared Leaf leaf2_2_2
    @Shared Leaf leaf2_3_1
    @Shared Leaf leaf2_3_2
    @Shared Leaf leaf3_1_1
    @Shared Leaf leaf3_1_2
    @Shared Leaf leaf3_2_1
    @Shared Leaf leaf3_2_2
    @Shared Leaf leaf3_3_1
    @Shared Leaf leaf3_3_2

    @Shared List<Node> allNodes = []
    @Shared List<Leaf> allLeaves = []

    def setupSpec() {
        DebugTrace.enter() // for Debugging
        Transaction.execute(connectionSupplier) {
            new Sql<>(Node).connection(it).where(Condition.ALL).delete()
            new Sql<>(Leaf).connection(it).where(Condition.ALL).delete()
            new Sql<>(Leaf2).connection(it).where(Condition.ALL).delete()

            node0   = new Node(-1      , "node0"  ); new Sql<>(Node).connection(it).insert(node0  ); allNodes << node0
            node1   = new Node(node0.id, "node1"  ); new Sql<>(Node).connection(it).insert(node1  ); allNodes << node1
            node2   = new Node(node0.id, "node2"  ); new Sql<>(Node).connection(it).insert(node2  ); allNodes << node2
            node3   = new Node(node0.id, "node3"  ); new Sql<>(Node).connection(it).insert(node3  ); allNodes << node3
            node4   = new Node(node0.id, "node4"  ); new Sql<>(Node).connection(it).insert(node4  ); allNodes << node4
            node1_1 = new Node(node1.id, "node1-1"); new Sql<>(Node).connection(it).insert(node1_1); allNodes << node1_1
            node1_2 = new Node(node1.id, "node1-2"); new Sql<>(Node).connection(it).insert(node1_2); allNodes << node1_2
            node1_3 = new Node(node1.id, "node1-3"); new Sql<>(Node).connection(it).insert(node1_3); allNodes << node1_3
            node2_1 = new Node(node2.id, "node2-1"); new Sql<>(Node).connection(it).insert(node2_1); allNodes << node2_1
            node2_2 = new Node(node2.id, "node2-2"); new Sql<>(Node).connection(it).insert(node2_2); allNodes << node2_2
            node2_3 = new Node(node2.id, "node2-3"); new Sql<>(Node).connection(it).insert(node2_3); allNodes << node2_3
            node3_1 = new Node(node3.id, "node3-1"); new Sql<>(Node).connection(it).insert(node3_1); allNodes << node3_1
            node3_2 = new Node(node3.id, "node3-2"); new Sql<>(Node).connection(it).insert(node3_2); allNodes << node3_2
            node3_3 = new Node(node3.id, "node3-3"); new Sql<>(Node).connection(it).insert(node3_3); allNodes << node3_3
            node4_1 = new Node(node4.id, "node4-1"); new Sql<>(Node).connection(it).insert(node4_1); allNodes << node4_1
            node4_2 = new Node(node4.id, "node4-2"); new Sql<>(Node).connection(it).insert(node4_2); allNodes << node4_2
            node4_3 = new Node(node4.id, "node4-3"); new Sql<>(Node).connection(it).insert(node4_3); allNodes << node4_3

            leaf0_1   = new Leaf(node0.id  , "leaf0-1"  , "content0-1"  ); allLeaves << leaf0_1
            leaf0_2   = new Leaf(node0.id  , "leaf0-2"  , "content0-2"  ); allLeaves << leaf0_2
            leaf1_1   = new Leaf(node1.id  , "leaf1-1"  , "content1-1"  ); allLeaves << leaf1_1
            leaf1_2   = new Leaf(node1.id  , "leaf1-2"  , "content1-2"  ); allLeaves << leaf1_2
            leaf2_1   = new Leaf(node2.id  , "leaf2-1"  , "content2-1"  ); allLeaves << leaf2_1
            leaf2_2   = new Leaf(node2.id  , "leaf2-2"  , "content2-2"  ); allLeaves << leaf2_2
            leaf3_1   = new Leaf(node3.id  , "leaf3-1"  , "content3-1"  ); allLeaves << leaf3_1
            leaf3_2   = new Leaf(node3.id  , "leaf3-2"  , "content3-2"  ); allLeaves << leaf3_2
            leaf1_1_1 = new Leaf(node1_1.id, "leaf1-1-1", "content1-1-1"); allLeaves << leaf1_1_1
            leaf1_1_2 = new Leaf(node1_1.id, "leaf1-1-2", "content1-1-2"); allLeaves << leaf1_1_2
            leaf1_2_1 = new Leaf(node1_2.id, "leaf1-2-1", "content1-2-1"); allLeaves << leaf1_2_1
            leaf1_2_2 = new Leaf(node1_2.id, "leaf1-2-2", "content1-2-2"); allLeaves << leaf1_2_2
            leaf1_3_1 = new Leaf(node1_3.id, "leaf1-3-1", "content1-3-1"); allLeaves << leaf1_3_1
            leaf1_3_2 = new Leaf(node1_3.id, "leaf1-3-2", "content1-3-2"); allLeaves << leaf1_3_2
            leaf2_1_1 = new Leaf(node2_1.id, "leaf2-1-1", "content2-1-1"); allLeaves << leaf2_1_1
            leaf2_1_2 = new Leaf(node2_1.id, "leaf2-1-2", "content2-1-2"); allLeaves << leaf2_1_2
            leaf2_2_1 = new Leaf(node2_2.id, "leaf2-2-1", "content2-2-1"); allLeaves << leaf2_2_1
            leaf2_2_2 = new Leaf(node2_2.id, "leaf2-2-2", "content2-2-2"); allLeaves << leaf2_2_2
            leaf2_3_1 = new Leaf(node2_3.id, "leaf2-3-1", "content2-3-1"); allLeaves << leaf2_3_1
            leaf2_3_2 = new Leaf(node2_3.id, "leaf2-3-2", "content2-3-2"); allLeaves << leaf2_3_2
            leaf3_1_1 = new Leaf(node3_1.id, "leaf3-1-1", "content3-1-1"); allLeaves << leaf3_1_1
            leaf3_1_2 = new Leaf(node3_1.id, "leaf3-1-2", "content3-1-2"); allLeaves << leaf3_1_2
            leaf3_2_1 = new Leaf(node3_2.id, "leaf3-2-1", "content3-2-1"); allLeaves << leaf3_2_1
            leaf3_2_2 = new Leaf(node3_2.id, "leaf3-2-2", "content3-2-2"); allLeaves << leaf3_2_2
            leaf3_3_1 = new Leaf(node3_3.id, "leaf3-3-1", "content3-3-1"); allLeaves << leaf3_3_1
            leaf3_3_2 = new Leaf(node3_3.id, "leaf3-3-2", "content3-3-2"); allLeaves << leaf3_3_2
            new Sql<>(Leaf).connection(it).insert([
                leaf0_1  ,
                leaf0_2  ,
                leaf1_1  ,
                leaf1_2  ,
                leaf2_1  ,
                leaf2_2  ,
                leaf3_1  ,
                leaf3_2  ,
                leaf1_1_1,
                leaf1_1_2,
                leaf1_2_1,
                leaf1_2_2,
                leaf1_3_1,
                leaf1_3_2,
                leaf2_1_1,
                leaf2_1_2,
                leaf2_2_1,
                leaf2_2_2,
                leaf2_3_1,
                leaf2_3_2,
                leaf3_1_1,
                leaf3_1_2,
                leaf3_2_1,
                leaf3_2_2,
                leaf3_3_1,
                leaf3_3_2
            ])
        }
        DebugTrace.leave() // for Debugging
    }

    def "#ignore WITH ... Node and Leaf"(Node rootNode, int expectedCount, String ignore) {
        if (!ignore.empty) return

        DebugTrace.enter() // for Debugging
        DebugTrace.print('WITH ... Node and Leaf') // for Debugging
        when:
            ArrayList<Node> nodeAndLeaves = []
            def nodeUnionLeafSql = new Sql<>(Node)
                .columns(Node)
                .unionAll(new Sql<>(Node))
                .unionAll(new Sql<>(Leaf))

            Transaction.execute(connectionSupplier) {
                new Sql<>(Node).connection(it)
                    .with(nodeUnionLeafSql)
                    .from(nodeUnionLeafSql)
                    .where("{parentId}={}", rootNode.id)
                    .select {nodeAndLeaves << it}
            }
            DebugTrace.print('nodeAndLeaves*.name', nodeAndLeaves*.name) // for Debugging

        then:
            nodeAndLeaves.size() == expectedCount
        DebugTrace.leave() // for Debugging

        where:
            rootNode|expectedCount
            node0   |4 + 2
            ignore = doesNotSupportWithClause ? "*IGNORE*" : ""
    }

    def "#ignore 2 WITH ... Node and Leaf"(Condition nodeCondition, Condition leafCondition, int expectedCount, String ignore) {
        if (!ignore.empty) return

        DebugTrace.enter() // for Debugging
        DebugTrace.print('2 WITH ... Node and Leaf') // for Debugging
        when:
            ArrayList<Node> nodeAndLeaves = []
            def nodeSql = new Sql<>(Node).where(nodeCondition)
            def leafSql = new Sql<>(Leaf).where(leafCondition)

            Transaction.execute(connectionSupplier) {
                new Sql<>(Node).connection(it)
                    .with(nodeSql, leafSql)
                    .columns(Node)
                    .unionAll(nodeSql)
                    .unionAll(leafSql)
                    .select {nodeAndLeaves << it}
            }
            DebugTrace.print('nodeAndLeaves*.name', nodeAndLeaves*.name) // for Debugging

        then:
            nodeAndLeaves.size() == expectedCount
        DebugTrace.leave() // for Debugging

        where:
            nodeCondition                            |leafCondition                            |expectedCount
            Condition.of('{name} LIKE {}', 'node1-%')|Condition.of('{name} LIKE {}', 'leaf2-%')|3 + 8
            ignore = doesNotSupportWithClause ? "*IGNORE*" : ""
    }

    def "#ignore 2 WITH ... Leaf JOIN Node"(Node ancestorNode, int expectedCount, String ignore) {
        if (!ignore.empty) return

        DebugTrace.enter() // for Debugging
        DebugTrace.print('2 WITH ... Leaf JOIN Node') // for Debugging
        when:
            ArrayList<Node> leaves = []
            ArrayList<Node> nodes = []
            def nodeSql = new Sql<>(Node)
            def leafSql = new Sql<>(Leaf)

            Transaction.execute(connectionSupplier) {
                new Sql<>(Leaf, 'L').connection(it)
                    .with(nodeSql, leafSql)
                    .from(leafSql)
                    .innerJoin(nodeSql, 'N', '{N.id}={L.parentId}')
                    .where('{N.parentId}={}', ancestorNode.id)
                    .select({leaves << it}, {nodes << it})
            }
            DebugTrace.print('leaves*.name', leaves*.name) // for Debugging
            DebugTrace.print('nodes*.name', nodes*.name) // for Debugging

        then:
            leaves.size() == expectedCount
            nodes.size() == expectedCount
        DebugTrace.leave() // for Debugging

        where:
            ancestorNode|expectedCount
            node0       |2 + 2 + 2
            ignore = doesNotSupportWithClause ? "*IGNORE*" : ""
    }

    def "#ignore WITH RECURSIVE ... Node"(Node rootNode, int expectedCount, String ignore) {
        if (!ignore.empty) return

        DebugTrace.enter() // for Debugging
        DebugTrace.print('WITH RECURSIVE ... Node') // for Debugging
        setup:
            ArrayList<Node> nodes = []

        when:
            def nodeSql = new Sql<>(Node).where(rootNode)
                .recursive(new Sql<>(Node, 'node').where('{node.parentId}={W1.id}'))
            Transaction.execute(connectionSupplier) {

                new Sql<>(Node).connection(it)
                    .with(nodeSql)
                    .from(nodeSql)
                    .select {nodes << it}
            }
            DebugTrace.print('nodes*.name', nodes*.name) // for Debugging

        then:
            nodes.size() == expectedCount

        DebugTrace.leave() // for Debugging
        where:
            rootNode|expectedCount
            node0   |allNodes.size()
            node1   |4
            node2   |4
            node3   |4
            node1_1 |1
            node3_3 |1
            ignore = doesNotSupportWithClause ? "*IGNORE*" : ""
    }

    def "#ignore WITH RECURSIVE ... Leaf"(Node rootNode, int expectedCount, String ignore) {
        if (!ignore.empty) return

        DebugTrace.enter() // for Debugging
        DebugTrace.print('WITH RECURSIVE ... Leaf') // for Debugging
        setup:
            ArrayList<Leaf> leaves = []

        when:
            def leafSql = new Sql<>(Node, 'r').columns('id').where(rootNode)
                .recursive(new Sql<>(Node, 'node').where('{node.parentId}={r.id}'))
            Transaction.execute(connectionSupplier) {
                new Sql(Leaf).connection(it)
                    .with(leafSql)
                    .where('{parentId} IN',
                        new Sql<>(Node)
                            .from(leafSql)
                    )
                .select {leaves << it}
            }
            DebugTrace.print('leaves*.name', leaves*.name) // for Debugging

        then:
            leaves.size() == expectedCount

        DebugTrace.leave() // for Debugging
        where:
            rootNode|expectedCount
            node0   |allLeaves.size()
            node1   |8
            node2_1 |2
            ignore = doesNotSupportWithClause ? "*IGNORE*" : ""
    }
    
    def "INSERT FROM SELECT ...." () {
        DebugTrace.enter() // for Debugging
        setup:
            Transaction.execute(connectionSupplier) {
                new Sql<>(Leaf2).connection(it).where(Condition.ALL).delete()
            }

        when:
            Transaction.execute(connectionSupplier) {
                new Sql<>(Leaf2).connection(it)
                    .from(new Sql<>(Leaf).where('{name} LIKE {}', 'leaf1-%'))
                    .insert()
            }

            List<Leaf2> leaves = [] 
            Transaction.execute(connectionSupplier) {
                new Sql<>(Leaf2).connection(it)
                    .select({leaves << it})
            }
            DebugTrace.print("leaves", leaves); // for Debugging

        then:
            leaves.size() == 8
        DebugTrace.leave() // for Debugging
    }
}
