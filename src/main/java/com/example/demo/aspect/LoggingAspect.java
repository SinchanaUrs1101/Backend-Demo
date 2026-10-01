package com.example.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.example.demo.service.*.*(..))")
    public void serviceMethods() {
    }

    @Around("serviceMethods()")
    public Object logExecution(ProceedingJoinPoint joinPoint)
            throws Throwable {

        String methodName = joinPoint.getSignature().getName();

        System.out.println(
                "Entering method: " + methodName
        );

        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();

        System.out.println(
                "Exiting method: " + methodName
        );

        System.out.println(
                "Execution time: " +
                        (endTime - startTime) +
                        " ms"
        );

        return result;
    }

    @AfterThrowing(
            pointcut = "serviceMethods()",
            throwing = "ex"
    )
    public void logException(
            JoinPoint joinPoint,
            Throwable ex) {

        System.out.println(
                "Exception occurred in: " +
                        joinPoint.getSignature().getName()
        );

        System.out.println(
                "Exception: " +
                        ex.getMessage()
        );
    }
}