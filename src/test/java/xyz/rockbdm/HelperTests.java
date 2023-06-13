package xyz.rockbdm;


import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.rockbdm.entity.MyLineage;
import xyz.rockbdm.entity.enums.code.DepthOpt;
import xyz.rockbdm.utils.JanusGraphHelper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.tinkerpop.gremlin.process.traversal.P.within;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;

@SpringBootTest(classes = WebApplication.class)
public class HelperTests {
    @Autowired
    private JanusGraphHelper janusGraphHelper;

    @Test
    public void doMy() throws Exception {
        GraphTraversalSource g = janusGraphHelper.getG();
        GraphTraversal<Vertex, Path> data = g.V().out().path().by();
        System.out.println(data);
    }

    @Test
    public void doMy2() throws Exception {
//        mgmt = graph.openManagement()
//        summary = mgmt.makePropertyKey("booksummary").dataType(String.class).make()
//        mgmt.buildIndex("booksBySummary", Vertex.class).addKey(summary, Mapping.TEXT.asParameter()).buildMixedIndex("search")
//        mgmt.commit()
    }

    @Test
    public void queryById() {
        String id = "12528";
        System.out.println(janusGraphHelper.queryById(id));
    }

    @Test
    public void addVertex() {
        MyLineage lineage = new MyLineage();
        int total = 5;
        for (int i = 1; i <= total; i++) {
            System.out.println("当前执行: " + i + "/" + total);
            lineage.setId("li000000001" + i);
            lineage.setLineageId("123456");
            lineage.setSourceClassId("Table");
            lineage.setSourceInstId("sourceInstId");
            lineage.setSourceSysId("sys1");
            lineage.setSrcType("01");
            lineage.setSrcInstId("srcInstId");
            lineage.setTargetClassId("Table");
            lineage.setTargetInstId("targetInstId");
            lineage.setTargetSysId("sys2");
            lineage.setOrderNum(1);
            long startTime = System.currentTimeMillis();
            janusGraphHelper.addVertex(lineage);
            System.out.println("插入耗时: " + (System.currentTimeMillis() - startTime));
        }
    }

    @Test
    public void queryVertex() {
        MyLineage lineage = new MyLineage();
        List<MyLineage> resData = janusGraphHelper.queryVertex(lineage);
        for (MyLineage resDatum : resData) {
            System.out.println(resDatum);
        }
    }

    @Test
    public void dropVertex() {
        MyLineage lineage = new MyLineage();
        lineage.setId("li0000000002");
        Object res = janusGraphHelper.dropVertex(lineage);
        System.out.println(res);
    }

    @Test
    public void modifyVertex() {
        MyLineage lineage = new MyLineage();
        lineage.setId("li0000000002");
        lineage.setLineageId("114514");
        Object res = janusGraphHelper.modifyVertexByPrimary(lineage);
        System.out.println(res);
    }

    @Test
    public void dropVertexByPrimary() {
        MyLineage lineage = new MyLineage();
        lineage.setId("li0000000002");
        Object res = janusGraphHelper.dropVertexByPrimary(lineage);
        System.out.println(res);
    }

    @Test
    public void addEdge() {
        String label = "Lineage";
        MyLineage o1 = new MyLineage();
        o1.setId("li0000000011");
        MyLineage o2 = new MyLineage();
        o2.setId("li0000000012");
        MyLineage o3 = new MyLineage();
        o3.setId("li0000000013");
        MyLineage o4 = new MyLineage();
        o4.setId("li0000000014");
        MyLineage o5 = new MyLineage();
        o5.setId("li0000000015");
        janusGraphHelper.addEdge(o1, o2, label);
        janusGraphHelper.addEdge(o2, o3, label);
        janusGraphHelper.addEdge(o2, o4, label);
        janusGraphHelper.addEdge(o3, o4, label);
        janusGraphHelper.addEdge(o4, o5, label);
    }

    @Test
    public void dropEdge() {
        String label = "Lineage";
        MyLineage o1 = new MyLineage();
        o1.setId("li0000000011");
        MyLineage o2 = new MyLineage();
        o2.setId("li0000000012");
        MyLineage o3 = new MyLineage();
        o3.setId("li0000000013");
        MyLineage o4 = new MyLineage();
        o4.setId("li0000000014");
        MyLineage o5 = new MyLineage();
        o5.setId("li0000000015");
        janusGraphHelper.dropEdge(o1, o3);
    }

    @Test
    public void dropVertexProperty() {
        MyLineage o1 = new MyLineage();
        o1.setId("li0000000011");
        janusGraphHelper.dropVertexProperty(o1, "srcType");
    }

    @Test
    public void dropVertexPropertyWithLabel() {
        janusGraphHelper.dropVertexPropertyWithLabel("Lineage", "srcType");
    }

    @Test
    public void addVertexBatch() {
        List<MyLineage> oList = Lists.newArrayList();
        for (int i = 0; i < 500000; i++) {
            MyLineage o = new MyLineage();
            o.setId("li" + i);
            oList.add(o);
        }
        janusGraphHelper.addVertexBatch(oList);
    }

    @Test
    public void count() throws Exception {
        GraphTraversalSource g = janusGraphHelper.getG();
        GraphTraversal<Vertex, Long> res = g.V().count();
        System.out.println(res.next());
    }

    @Test
    public void path() throws Exception {
        GraphTraversalSource g = janusGraphHelper.getG();
        GraphTraversal<Vertex, Path> res = g.V().has("id", "li0000000015").repeat(in().simplePath()).until(where(loops().is(5).or().inE().count().is(0))).path().by(valueMap());
        while(res.hasNext()) {
            Path line = res.next();
            System.out.println("line_change");
            System.out.println(line);
            line.forEach(System.out::println);
        }
    }

    @Test
    public void outE() throws Exception {
        GraphTraversalSource g = janusGraphHelper.getG();
        // 4256
        GraphTraversal<Vertex, Map<String, Object>> res = g.V().as("sourceId","sourceSysId").has("id", within("li0000000012","li0000000011")).out().as("targetId","targetSysId").select("sourceId","sourceSysId","targetId","targetSysId").by("sourceId").by("sourceSysId").by("targetId").by("targetSysId");
        System.out.println(res.toList());
    }

    @Test
    public void queryRel() throws Exception {
        MyLineage o1 = new MyLineage();
        o1.setId("li0000000015");
        List<List<MyLineage>> res = janusGraphHelper.queryVertexRel(
                o1, DepthOpt.IN, 1);
        res.forEach(System.out::println);
    }
}
