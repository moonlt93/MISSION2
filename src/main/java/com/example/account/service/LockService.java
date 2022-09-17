package com.example.account.service;

import com.example.account.exception.AccountException;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {
    private final RedissonClient redissonClient;

    public String Lock(String accountNumber) {
        RLock lock = redissonClient.getLock(getLockKey(accountNumber));
        log.debug("Trying lock for accountNumber : {}",accountNumber);
        try {
            boolean isLock = lock.tryLock(1, 15, TimeUnit.SECONDS);
            if(!isLock) {
                log.error("======Lock acquisition failed=====");
              throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);

            }
        }catch (AccountException e) {
            // 커스텀 EXCEPTION이므로 따로 먼저 돈 후 , 처리될수 있게 한다.
         throw e;
        }  catch (Exception e) {
            log.error("Redis lock failed",e);
        }

        return "Lock success";
    }

    private static String getLockKey(String accountNumber) {
        return "ACLK:" + accountNumber;
    }

    public void unlock(String accountNumber){
        log.debug("unlock for accountNumber: {}", accountNumber);
        redissonClient.getLock(getLockKey(accountNumber)).unlock();
    }
}
