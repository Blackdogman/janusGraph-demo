package xyz.rockbdm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记janusGraph节点的属性
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JGVertexField {
    /*
    JanusGraph的节点属性, 当为空字符串时, 读取属性的变量名
     */
    String value() default "";

    /**
     是否主键, 用于主键形式的修改与删除
     */
    boolean isPrimary() default false;
}
