package com.shopverse.product.aop;

import org.apache.juli.logging.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeAspect {
    private static final Logger log = LoggerFactory.getLogger(ExecutionTimeAspect.class);

    // Pointcut: every public method of every class in a ..service.. or ..inventory.. package
    @Around("execution(public * com.shopverse.product.service..*(..)) || " +
            "execution(public * com.shopverse.product.inventory.*Service.*(..))")
    public Object logTime(ProceedingJoinPoint joinPoint) throws Throwable{
        long start = System.nanoTime();
        try{
            return joinPoint.proceed();
        }finally {
            long ms = (System.nanoTime() - start) / 1_00_000;
            log.info("{} took {} ms",joinPoint.getSignature().toShortString(),ms);
        }
    }
}
