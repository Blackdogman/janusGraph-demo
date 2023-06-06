package xyz.rockbdm.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.rockbdm.annotation.JGVertex;
import xyz.rockbdm.annotation.JGVertexField;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

@Component
public class JanusGraphHelper {

    /*
    TODO 事务
    Transaction tx = g.tx();
    // spawn a GraphTraversalSource from the Transaction. Traversals spawned
    // from gtx will be essentially be bound to tx
    GraphTraversalSource gtx = tx.begin();
    try {
        gtx.addV('person').iterate();
        gtx.addV('software').iterate();
        tx.commit();
    } catch (Exception ex) {
        tx.rollback();
    }
     */

    @Value(value = "${janusGraph.config.path:}")
    private String configPath;

    private GraphTraversalSource g;

    private final String DEFAULT_CONFIG_PATH = "janusgraph-conf/remote-graph.properties";

    /**
     * 获得图操作基类
     *
     * @return 图操作基类
     */
    public GraphTraversalSource getG() throws Exception {
        if (!ObjUtil.isNull(this.g)) {
            // GraphTraversalSource复用
            return this.g;
        }
        String confPath = StrUtil.isBlank(configPath) ? DEFAULT_CONFIG_PATH : configPath;
        return traversal().withRemote(confPath);
    }

    /**
     * 查询value集合
     *
     * @return valueMap
     */
    public List<Object> valueMap() throws Exception {
        List<Object> resList = Lists.newArrayList();
        GraphTraversalSource g = this.getG();
        GraphTraversal<Vertex, Map<Object, Object>> values = g.V().valueMap(true);
        while (values.hasNext()) {
            resList.add(values.next());
        }
        return resList;
    }

    /**
     * 通过节点id查询节点
     *
     * @param id 节点id
     * @return 节点的内容
     */
    public Object queryById(String id) throws Exception {
        GraphTraversalSource g = this.getG();
        return g.V().hasId(id).valueMap().next();
    }

    /**
     * 添加节点
     *
     * @param v 具有@JGVertex与@JGVertexField注解的对象
     * @return 创建响应
     */
    public Object addVertex(Object v) {
        try (GraphTraversalSource g = this.getG()) {
            Class<?> vClazz = v.getClass();
            String vertexLabel = this.getVertexLabel(vClazz);
            // 通过addV开启一个创建的GraphTraversal
            GraphTraversal<Vertex, Vertex> gremlin = g.addV(vertexLabel);

            // 处理JGVertex列
            Field[] fields = vClazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(JGVertexField.class)) {
                    field.setAccessible(true);
                    String fieldLabel = this.getVertexFieldLabel(field);
                    gremlin.property(fieldLabel, field.get(v));
                }
            }
            // 提交插入
            return gremlin.iterate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询节点
     *
     * @param v   查询的对象
     * @param <T> 泛型
     * @return 对象集合
     */
    public <T> List<T> queryVertex(T v) {
        // 返回结果集
        List<T> resData = Lists.newArrayList();
        Class<?> vClazz = v.getClass();
        try (GraphTraversalSource g = this.getG()) {
            // 定位到对应的节点
            GraphTraversal<Vertex, Vertex> gremlin = this.locateVertex(g, v);
            // 获取到定位的节点的值
            GraphTraversal<Vertex, Map<Object, Object>> gremlinResult = gremlin.valueMap();
            // 遍历结果集
            while (gremlinResult.hasNext()) {
                // 单行的Map结果集, 最后转为T对象, 因为默认gremlin返回的结果的value是一个list, 需要取第0位
                Map<String, Object> row = Maps.newHashMap();
                Map<Object, Object> rowRes = gremlinResult.next();
                rowRes.forEach((key, value) -> {
                    Object rowValue = null;
                    if (value instanceof List) {
                        // 如果为集合, 则判断是否长度为1, 如果为1则把下标为0的记录取出来
                        List lValue = (List) value;
                        if (lValue.size() <= 1) {
                            rowValue = lValue.get(0);
                        }
                    }
                    if (ObjUtil.isNull(rowValue)) {
                        rowValue = value;
                    }
                    row.put(key.toString(), rowValue);
                });
                T rowBean = (T) BeanUtil.fillBeanWithMap(row, vClazz.getDeclaredConstructor().newInstance(), false);
                resData.add(rowBean);
            }
            return resData;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除节点, 有什么属性都会作为条件去操作
     *
     * @param v 删除对象
     */
    public Object dropVertex(Object v) {
        try (GraphTraversalSource g = this.getG()) {
            GraphTraversal<Vertex, Vertex> gremlin = this.locateVertex(g, v);
            return gremlin.drop().iterate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除节点的某个属性
     * @param v 节点对象
     * @param property 节点属性property的key
     * @return 执行结果
     */
    public Object dropVertexProperty(Object v, String property) {
        // TODO 需要考虑命中节点的属性移除和全量label的属性移除
        return null;
    }

    /**
     * 删除节点, 通过primary的属性
     *
     * @param v 删除对象
     */
    public Object dropVertexByPrimary(Object v) {
        try (GraphTraversalSource g = this.getG()) {
            GraphTraversal<Vertex, Vertex> gremlin = this.locateVertexByPrimary(g, v);
            return gremlin.drop().iterate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改节点, 通过primary的属性
     *
     * @param v 填写了primary值的对象
     */
    public Object modifyVertexByPrimary(Object v) {
        Class<?> vClazz = v.getClass();
        try (GraphTraversalSource g = this.getG()) {
            GraphTraversal<Vertex, Vertex> gremlin = this.locateVertexByPrimary(g, v);
            // 处理JGVertex列
            Field[] fields = vClazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(JGVertexField.class)) {
                    boolean isPrimary = field.getAnnotation(JGVertexField.class).isPrimary();
                    if (isPrimary) {
                        // 如果当前属性是primary的话, 则不加入修改队列
                        continue;
                    }
                    field.setAccessible(true);
                    String fieldLabel = this.getVertexFieldLabel(field);
                    gremlin.property(fieldLabel, field.get(v));
                }
            }
            // 提交修改
            return gremlin.iterate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 添加节点关系
     *
     * @param o1    来源节点
     * @param o2    目标节点
     * @param label 关系的label
     * @return 执行结果
     */
    public Object addEdge(Object o1, Object o2, String label) {
        return this.addEdge(o1, o2, label, null);
    }

    /**
     * 添加节点关系
     *
     * @param o1     来源节点
     * @param o2     目标节点
     * @param label  关系的label
     * @param property 关系的的属性
     * @return 执行结果
     */
    public Object addEdge(Object o1, Object o2, String label, Map<String, Object> property) {
        try (GraphTraversalSource g = this.getG()) {
            // 来源
            Vertex v1 = this.locateVertexByPrimary(g, o1).next();
            // 目标
            Vertex v2 = this.locateVertexByPrimary(g, o2).next();
            // 明确关系
            GraphTraversal<Vertex, Edge> gremlin = g.V(v1).addE(label).to(v2);
            // 遍历params去添加edge的property
            if (!CollUtil.isEmpty(property)) {
                property.forEach(gremlin::property);
            }
            // 提交执行
            gremlin.iterate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 删除关系, o1, o2分别作为来源和目标节点 <br/>
     * o1 != null && o2 != null: 删除两个节点中间的关系 <br/>
     * o1 != null && o2 == null: 删除以o1为开始节点的所有直接目标关系 <br/>
     * o1 == null && o2 != null: 删除以o2为结束节点的所有直接来源关系 <br/>
     *
     * @param o1 来源节点
     * @param o2 目标节点
     * @return 执行结果
     */
    public Object dropEdge(Object o1, Object o2) {
        return this.dropEdge(o1, o2, null);
    }

    /**
     * 删除关系, o1, o2分别作为来源和目标节点 <br/>
     * o1 != null && o2 != null: 删除两个节点中间的关系 <br/>
     * o1 != null && o2 == null: 删除以o1为开始节点的所有直接目标关系 <br/>
     * o1 == null && o2 != null: 删除以o2为结束节点的所有直接来源关系 <br/>
     *
     * @param o1 来源节点
     * @param o2 目标节点
     * @param label 关系的label, 如果为null则会删除所有label的关系
     * @return 执行结果
     */
    public Object dropEdge(Object o1, Object o2, String label) {
        try (GraphTraversalSource g = this.getG()) {
            if (o1 == null && o2 == null) {
                // 抛出异常
                throw new Exception("来源与目标节点不能同时为空");
            }
            if (o1 != null && o2 != null) {
                // 删除两个节点间的关系
                // FIXME 这里采用了遍历的方式找了一次关系ID, 需要研究是否有原生Gremlin语句支持删除两个节点间的关系
                Vertex v1 = this.locateVertexByPrimary(g, o1).next();
                Vertex v2 = this.locateVertexByPrimary(g, o2).next();
                GraphTraversal<Vertex, Vertex> vE1 = this.locateVertexByPrimary(g, o1);
                List<Edge> eList;
                if(StrUtil.isBlank(label)) {
                    eList = vE1.outE().toList();
                }else {
                    eList = vE1.outE(label).toList();
                }
                for (Edge edge : eList) {
                    Vertex inVertex = edge.inVertex();
                    Vertex outVertex = edge.outVertex();
                    boolean inEq = ObjUtil.equals(v1.id(), outVertex.id());
                    boolean outEq = ObjUtil.equals(v2.id(), inVertex.id());
                    if(inEq && outEq) {
                        // 命中关系, 直接删除
                        Object eId = edge.id();
                        g.E(eId).drop().iterate();
                    }
                }
            }
            if (o1 != null && o2 == null) {
                // 删除以o1开始的所有关系
                GraphTraversal<Vertex, Vertex> v1 = this.locateVertexByPrimary(g, o1);
                if(StrUtil.isBlank(label)) {
                    v1.outE();
                }else {
                    v1.outE(label);
                }
                return v1.drop().iterate();
            }
            if (o1 == null && o2 != null) {
                // 删除以o2结束的所有关系
                GraphTraversal<Vertex, Vertex> v2 = this.locateVertexByPrimary(g, o2);
                if(StrUtil.isBlank(label)) {
                    v2.inE();
                }else {
                    v2.inE(label);
                }
                return v2.drop().iterate();
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 用于定位到指定的节点, 比如查询、删除、修改时，首先要先定位到对应的节点才提交最后的操作是查询、删除还是修改
     *
     * @param g 图操作基类
     * @return GraphTraversal操作类
     */
    public GraphTraversal<Vertex, Vertex> locateVertex(GraphTraversalSource g, Object v) throws IllegalAccessException {
        GraphTraversal<Vertex, Vertex> gremlin = g.V();
        Class<?> vClazz = v.getClass();
        String vertexLabel = this.getVertexLabel(vClazz);
        Field[] fields = vClazz.getDeclaredFields();
        gremlin.hasLabel(vertexLabel);
        for (Field field : fields) {
            if (field.isAnnotationPresent(JGVertexField.class)) {
                field.setAccessible(true);
                String fieldLabel = this.getVertexFieldLabel(field);
                Object fieldValue = field.get(v);
                if (ObjUtil.isNull(fieldValue)) {
                    // 如果该属性值为空, 则不加入到查询中
                    continue;
                }
                gremlin.has(fieldLabel, fieldValue);
            }
        }
        return gremlin;
    }

    /**
     * 通过@JGVertexField.isPrimary()定位到指定的节点, 比如查询、删除、修改时，首先要先定位到对应的节点才提交最后的操作是查询、删除还是修改
     *
     * @param g 图操作基类
     * @return GraphTraversal操作类
     */
    public GraphTraversal<Vertex, Vertex> locateVertexByPrimary(GraphTraversalSource g, Object v) throws IllegalAccessException {
        GraphTraversal<Vertex, Vertex> gremlin = g.V();
        Class<?> vClazz = v.getClass();
        String vertexLabel = this.getVertexLabel(vClazz);
        Field[] fields = vClazz.getDeclaredFields();
        gremlin.hasLabel(vertexLabel);
        boolean primaryFlg = false;
        for (Field field : fields) {
            if (field.isAnnotationPresent(JGVertexField.class)) {
                field.setAccessible(true);
                boolean isPrimary = field.getAnnotation(JGVertexField.class).isPrimary();
                // 如果是一个primary键, 就作为条件加入到检索里面
                if (isPrimary) {
                    primaryFlg = true;
                    String fieldLabel = this.getVertexFieldLabel(field);
                    Object fieldValue = field.get(v);
                    if (ObjUtil.isNull(fieldValue)) {
                        // 如果该属性值为空, 则不加入到查询中
                        continue;
                    }
                    gremlin.has(fieldLabel, fieldValue);
                }
            }
        }
        if (!primaryFlg) {
            throw new RuntimeException(vClazz.getName() + "没有字段被注册为primary, @JGVertexField(isPrimary = true)");
        }
        return gremlin;
    }


    /**
     * 获取节点的label, 当@JGVertex的value为空则使用class的名称作为Label
     *
     * @param vClazz 实体类
     * @return label
     */
    public String getVertexLabel(Class<?> vClazz) {
        if (!vClazz.isAnnotationPresent(JGVertex.class)) {
            throw new RuntimeException(vClazz.getName() + "不是@JGVertex类");
        }
        String vertexLabel = vClazz.getAnnotation(JGVertex.class).value();
        // 如果@JGVertex的value为空则使用class的名称作为Label
        return StrUtil.isBlank(vertexLabel) ? vClazz.getSimpleName() : vertexLabel;
    }

    /**
     * 获取节点属性的label,当@JGVertexField的value为空则使用class的名称作为Label
     *
     * @param field 节点属性
     * @return 节点属性label
     */
    public String getVertexFieldLabel(Field field) {
        String propertyLabel = field.getAnnotation(JGVertexField.class).value();
        // 如果@JGVertexField的value为空, 则使用field的名称作为label
        return StrUtil.isBlank(propertyLabel) ? field.getName() : propertyLabel;
    }
}
