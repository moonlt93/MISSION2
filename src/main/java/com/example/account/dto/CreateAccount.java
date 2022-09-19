package com.example.account.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateAccount {

    @Setter
    @Getter
    @AllArgsConstructor
    public static class Request {
        @NotNull
        @Min(1)
        private Long userId;

        @NotNull
        @Min(0)
        private Long initialBalance;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Setter
    @Getter
    public static class Response {
        private Long userId;
        private String accountNumber;
        private LocalDateTime registeredAt;

        public static Response from(AccountDto dto) {
            return Response.builder()
                    .userId(dto.getUserId())
                    .accountNumber(dto.getAccountNumber())
                    .registeredAt(dto.getRegisteredAt())
                    .build();
        }

        ;


    }


}
