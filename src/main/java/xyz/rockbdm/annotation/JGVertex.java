package xyz.rockbdm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记JanusGraph节点
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JGVertex {
    /*
    对应JanusGraph的列, 如果为空字符串, 则直接读取类名
     */
    String value() default "";
}
