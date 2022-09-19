package com.example.account.repository;

import com.example.account.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * @writer 제로베이스 3기 문진수
 *
 */
@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(String transactionId);



}
