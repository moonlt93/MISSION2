package com.example.account.service;

import com.example.account.aop.AccountLockIdInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LockAopAspect {

    private final LockService lockService;

    //어떤 타이밍에 쓸것인가?
    @Around("@annotation(com.example.account.aop.AccountLock) && args(request)")
    // args(request) => 어노테이션 붙인곳의 request를 가져옴.
    public Object aroundMethod(
            ProceedingJoinPoint pjp,
            AccountLockIdInterface request
    )throws Throwable {
        //lock 취득
        lockService.Lock(request.getAccountNumber());
        try{
            //before
            //joinPoint가져와서 진행
            return pjp.proceed();
            //after
        }finally{
            //lock해제
            lockService.unlock(request.getAccountNumber());

        }
    }

}
