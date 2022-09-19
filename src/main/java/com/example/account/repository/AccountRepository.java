package com.example.account.repository;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // 완전 그냥 쿼리네
    Optional<Account> findFirstByOrderByIdDesc();


    Integer countByAccountUser(AccountUser user);

    Optional<Account> findByAccountNumber(String accountNumber);


    List<Account> findByAccountUser(AccountUser accountUser);

    //JPA 관련 기능인데 제너릭 클래스에 인수를 받을 변수가 없으면 자동으로 같은 이름으로 생성해준다고 함.
}
