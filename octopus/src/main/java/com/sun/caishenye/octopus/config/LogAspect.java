package com.sun.caishenye.octopus.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Around("execution(* com.sun.caishenye.octopus..*(..)))")
    public Object methodAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        log.info("{}.{}    ::     start", className, methodName);
        Object result = joinPoint.proceed();
        log.info("{}.{}    ::     end", className, methodName);
        return result;
    }
}
