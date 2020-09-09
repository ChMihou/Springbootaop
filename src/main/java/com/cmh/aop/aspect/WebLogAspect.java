package com.cmh.aop.aspect;

import com.cmh.aop.utils.HttpContextUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Order(5)
@Component
public class WebLogAspect {
    private final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);
    ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 第一个*表示返回任何类型,com.cmh.aop.controller下任何类,任何方法,任何参数
     * 也可以加入参数限定例如com.cmh.aop.controller.*.*(..)&&args(name,..)
     * <p>
     * 下面那中表示方法也是对的,表示com.cmh.aop.下面任何子包下任何方法,任何参数
     **/
    @Pointcut("execution(public * com.cmh.aop.controller.*.*(..))")
    public void webLog() {
    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        startTime.set(System.currentTimeMillis());

        // 接收到请求，记录请求内容

        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        String ipAddress = HttpContextUtils.getIpAddress();

        // 记录下请求内容
        logger.info("URL : " + request.getRequestURL().toString());
        logger.info("HTTP_METHOD : " + request.getMethod());
        logger.info("IP : " + request.getRemoteAddr());
        logger.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));
        logger.info("ip:" + ipAddress);

    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        logger.info("RESPONSE : " + ret);
        logger.info("SPEND TIME : " + (System.currentTimeMillis() - startTime.get()));
    }


}