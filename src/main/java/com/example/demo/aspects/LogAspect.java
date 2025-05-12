package com.example.demo.aspects;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Around("@annotation(com.example.demo.aspects.annotations.ExecutionTime)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object obj = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        log.info("Execution time {}: {} ms", joinPoint.getSignature().getName(), endTime - startTime);
        return obj;
    }

    @AfterThrowing(value = "@annotation(com.example.demo.aspects.annotations.ExceptionHandling)",
        throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Exception ex)  {
        log.error("Exception in method {}, cause = {}", joinPoint.getSignature(),ex.getMessage());
    }

    @AfterReturning(
            pointcut = "@annotation(com.example.demo.aspects.annotations.ResultHandling)",
            returning = "result"
    )
    public void afterReturning(JoinPoint joinPoint, Object result) {
        log.info("Result in method {}, it can be your cache logic, result {}", joinPoint.getSignature(), result);
    }

    @Before("@annotation(com.example.demo.aspects.annotations.BeforeLog)")
    public void logMethodCall(JoinPoint jp) {
        String methodName = jp.getSignature().getName();
        Object[] args = jp.getArgs();
        log.info("Вызов метода {} с аргументами {}", methodName, Arrays.toString(args));
    }
}
