package xyz.rockbdm;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;


public class Demo {
    public static void main(String[] args) throws Exception {
        /*
        FIXME 需要用jdk8!!!需要用jdk8!!!需要用jdk8!!!需要用jdk8!!!需要用jdk8!!!需要用jdk8!!!
         */
        System.out.println("初始化janusGraph Source开始...");
        GraphTraversalSource g = traversal().withRemote("janusgraph-conf/remote-graph.properties");
        System.out.println("初始化janusGraph Source结束...");
//        System.out.println("新增开始...");
//        g.addV("user").property("name","user21").property("age","18-24").iterate();
//        g.addV("user").property("name","user22").property("age","18-24").iterate();
//        g.addV("user").property("name","user23").property("age","18-24").iterate();

//        System.out.println("新增结束");
//        GraphTraversal<Vertex, Map<Object, Object>> resData = g.V().valueMap(false);
//        while (resData.hasNext()) {
//            Map<Object, Object> data = resData.next();
//            System.out.println(data);
//        }
        GraphTraversal<Vertex, Vertex> one = g.V();
        one.hasLabel("user");
        one.limit(5);
        GraphTraversal<Vertex, Map<Object, Object>> userRes = one.valueMap();
        while (userRes.hasNext()) {
            Map<Object, Object> row = userRes.next();
            System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            System.out.println(row);
            row.forEach((k,v) -> {
                System.out.println("k.class: " + k.getClass().getName());
                System.out.println("v.class: " + v.getClass().getName());
            });
        }
        g.close();
    }
}