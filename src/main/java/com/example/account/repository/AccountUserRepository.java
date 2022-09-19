package com.example.account.repository;

import com.example.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * @writer 제로베이스 3기 문진수
 *
 */
@Repository
public interface AccountUserRepository extends JpaRepository<AccountUser,Long> {


}
