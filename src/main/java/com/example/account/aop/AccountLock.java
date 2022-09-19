package com.example.account.aop;


import java.lang.annotation.*;

/**
 * @writer 제로베이스 3기 문진수
 *
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccountLock {

    long tryLockTime() default 5000L;


}
