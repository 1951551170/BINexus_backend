package com.BINexus.back.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 请求响应日志 AOP
 *
 *
 **/
@Slf4j
@Aspect
public class LogInterceptor {

    @Around("execution(* com.BINexus.back.controller.*.*(..))")
    public Object doIntercept(ProceedingJoinPoint point) throws Throwable {
        long begin = System.currentTimeMillis();

        // 获取请求路径
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        String url = httpServletRequest.getRequestURI();
        String urlId = UUID.randomUUID().toString();
        String className = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        Object[] args=point.getArgs();
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";
        log.info("request begin:{},urlId:{},url:{},className:{},methodName:{},args:{}",begin,urlId,url,className,methodName,reqParam);
        Object result = point.proceed();

        long end = System.currentTimeMillis();
        long cost= end-begin;
        log.info("request end:{}.cost:{}ms",end,cost);
        return result;
    }
}
