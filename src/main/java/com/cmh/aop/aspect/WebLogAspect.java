package com.cmh.aop.aspect;

import com.cmh.aop.annotation.PointcutAnnotation;
import com.cmh.aop.utils.HttpContextUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;

@Aspect
@Order(5)
@Component
public class WebLogAspect {
    private final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);
    ThreadLocal<Long> startTime = new ThreadLocal<>();
    @Autowired
    private HttpServletRequest request;

    /**
     * 第一个*表示返回任何类型,com.cmh.aop.controller下任何类,任何方法,任何参数
     * 也可以加入参数限定例如com.cmh.aop.controller.*.*(..)&&args(name,..)
     * <p>
     * 下面那中表示方法也是对的,表示com.cmh.aop.下面任何子包下任何方法,任何参数
     **/
    @Pointcut("@annotation(pointcutAnnotation)")
    public void webLog(PointcutAnnotation pointcutAnnotation) {
    }

    /**
     * @description 进入方法之前拦截操作日志
     * @param joinPoint
     * @param pointcutAnnotation
     * @throws Throwable
     */
    @Before("webLog(pointcutAnnotation)")
    public void doBefore(JoinPoint joinPoint, PointcutAnnotation pointcutAnnotation) throws Throwable {
        long time = pointcutAnnotation.timeout();
        startTime.set(System.currentTimeMillis());
        System.out.println(time);
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

    /**
     * @description 环绕方法前后，通过session的token信息进行表单重复提交拦截
     * @param pjp
     * @param pointcutAnnotation
     * @return
     * @throws Throwable
     */
    @Around("webLog(pointcutAnnotation)")
    public Object around(ProceedingJoinPoint pjp,PointcutAnnotation pointcutAnnotation) throws Throwable {
        logger.info("around-begin");
        String formToken = request.getParameter("token");
        String token = (String) request.getSession().getAttribute("token");
        System.out.println(token);
        System.out.println(formToken);
        if (!Objects.equals(formToken, token)) {
            throw new RepeatableCommitException("表单重复提交");
        }
        Object o = pjp.proceed();
        logger.info("around-end");
        return o;
    }

    /**
     * @description 处理完请求，返回内容
     * @param ret
     * @param pointcutAnnotation
     * @throws Throwable
     */
    @AfterReturning(returning = "ret", pointcut = "webLog(pointcutAnnotation)")
    public void doAfterReturning(Object ret,PointcutAnnotation pointcutAnnotation) throws Throwable {
        // 处理完请求，返回内容
        logger.info("RESPONSE : " + ret);
        logger.info("SPEND TIME : " + (System.currentTimeMillis() - startTime.get()));
    }


}