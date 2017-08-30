package com.sun.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by TY on 2017/8/29.
 */
@Aspect
@Component
public class RequestCountAspect {
    private static final Logger logger = LoggerFactory.getLogger(RequestCountAspect.class);

    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    private AtomicLong requestCount = new AtomicLong();

    @Pointcut("execution(* com.sun.controller.*.*(..))")
    public void webLog(){}

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint){
        requestCount.incrementAndGet();

        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        logger.info("URL:" + request.getRequestURL().toString());
        logger.info("HTTP_METHOD:" + request.getMethod());
        logger.info("IP:" + request.getRemoteAddr());
        logger.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName()
                + "." + joinPoint.getSignature().getName());
        startTime.set(System.currentTimeMillis());
    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturing(Object ret){
        logger.info("Reponse:" + ret);
        logger.info("SPEND TIME:" + (System.currentTimeMillis()-startTime.get()));
        logger.info("RUQUEST_COUNTS:" + requestCount.get());
    }

    public long getCount(){
        return requestCount.get();
    }
}
