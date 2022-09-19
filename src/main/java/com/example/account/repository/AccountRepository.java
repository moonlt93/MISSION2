package com.example.account.repository;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
/**
 * @writer 제로베이스 3기 문진수
 *
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findFirstByOrderByIdDesc();


    Integer countByAccountUser(AccountUser user);

    Optional<Account> findByAccountNumber(String accountNumber);


    List<Account> findByAccountUser(AccountUser accountUser);

}
