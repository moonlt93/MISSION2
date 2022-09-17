package com.example.account.dto;

import com.example.account.aop.AccountLockIdInterface;
import com.example.account.type.TransactionResultType;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

public class CancelBalance {
    /**
     * {
     *   "transactionId": "wefsdf",
     *   "accountNumber": 1000
     *   "amount": 1000
     * }
     */


    @Setter
    @Getter
    @AllArgsConstructor
    //innerclass네?
    public static class Request implements AccountLockIdInterface {

        @NotBlank
        private String transactionId;

        @NotNull
        @Size(min =10, max =10)
        private String accountNumber;

        @NotNull
        @Min(10)
        @Max(1000_000_000)
        private Long amount;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Setter
    @Getter
    //innerclass네?
    public static class Response {
        private String accountNumber;
        private TransactionResultType transactionResult;
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;


        public static CancelBalance.Response from(TransactionDto transactionDto) {
            return CancelBalance.Response.builder()
                    .accountNumber(transactionDto.getAccountNumber())
                    .transactionResult(transactionDto.getTransactionResultType())
                    .transactionId(transactionDto.getTransactionId())
                    .transactedAt(transactionDto.getTransactedAt())
                    .amount(transactionDto.getAmount())
                    .build();
        }
    }
}
