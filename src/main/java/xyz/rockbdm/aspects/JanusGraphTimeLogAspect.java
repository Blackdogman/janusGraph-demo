package xyz.rockbdm.aspects;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Date;

@Profile(value = "debug")
@Aspect
@Component
public class JanusGraphTimeLogAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Around("execution(public * xyz.rockbdm.utils..*(..))")
    public Object runTimeStatistics(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = pjp.getSignature();
        //被代理的类的类名
        String className = pjp.getTarget().getClass().getName();
        //方法名
        String methodName = signature.getName();
        //参数数组
        Object[] requestParams = pjp.getArgs();
        StringBuilder sb = new StringBuilder();
        for (Object requestParam : requestParams) {
            if (requestParam != null) {
                sb.append(JSONUtil.toJsonStr(requestParam));
                sb.append(",");
            }
        }
        String requestParamsString = sb.toString();
        if (requestParamsString.length() > 0) {
            requestParamsString = requestParamsString.substring(0, requestParamsString.length() - 1);
        }
        //接口应答前打印日志
        log.info(String.format("【%s】类的【%s】方法，请求参数：%s", className, methodName, requestParamsString));
        //接口调用开始响应起始时间
        Date startDate = DateUtil.date();
        //正常执行方法，即让方法进行执行
        Object response = pjp.proceed();
        Date endDate = DateUtil.date();
        // 获取开始时间和结束时间的时间差
        long betweenDate = DateUtil.between(startDate, endDate, DateUnit.MS);
        // 格式化时间
        String formatBetween = DateUtil.formatBetween(betweenDate, BetweenFormatter.Level.MILLISECOND);
        //接口应答之后打印日志
        log.info(String.format("【%s】类的【%s】方法，应答参数：%s", className, methodName, JSONUtil.toJsonStr(response)));
        //接口耗时
        log.info(String.format("接口【%s】总耗时(毫秒)：%s", methodName, formatBetween));

        return response;
    }
}
