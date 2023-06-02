package xyz.rockbdm;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;


public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("初始化janusGraph Source开始...");
        GraphTraversalSource g = traversal().withRemote("janusgraph-conf/remote-graph.properties");
        System.out.println("初始化janusGraph Source结束...");
        g.tx().open();
        System.out.println("新增开始...");
        g.addV("user").property("name","user11").property("age","18-24").iterate();
        g.addV("user").property("name","user12").property("age","18-24").iterate();
        g.addV("user").property("name","user13").property("age","18-24").iterate();
        System.out.println("新增结束");
        GraphTraversal<Vertex, Map<Object, Object>> resData = g.V().valueMap(false);
        while (resData.hasNext()) {
            Map<Object, Object> data = resData.next();
            System.out.println(data);
        }
        g.tx().commit();
        g.tx().close();
        g.close();
    }
}