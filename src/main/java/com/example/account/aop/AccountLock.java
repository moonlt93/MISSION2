package com.example.account.aop;


import java.lang.annotation.*;

@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccountLock {

    long tryLockTime() default 5000L;


}
