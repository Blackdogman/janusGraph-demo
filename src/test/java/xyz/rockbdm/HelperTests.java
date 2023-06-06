package xyz.rockbdm;


import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.rockbdm.entity.MyLineage;
import xyz.rockbdm.utils.JanusGraphHelper;

import java.util.List;
import java.util.Map;

@SpringBootTest(classes = WebApplication.class)
public class HelperTests {
    @Autowired
    private JanusGraphHelper janusGraphHelper;

    @Test
    public void doMy() throws Exception {
        GraphTraversalSource g = janusGraphHelper.getG();
        g.tx().commit();
    }

    @Test
    public void valueMap() throws Exception {
        List<Object> res = janusGraphHelper.valueMap();
        for (Object re : res) {
            ((Map<?, ?>) re).forEach((k, v) -> {
                System.out.println("k.class: " + k.getClass().getName());
                System.out.println("k: " + k);
                System.out.println("v.class: " + v.getClass().getName());
                System.out.println("v: " + v);
            });
        }
    }

    @Test
    public void queryById() throws Exception {
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
        janusGraphHelper.addEdge(o1, o3, label);
        janusGraphHelper.addEdge(o5, o3, label);
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
}
