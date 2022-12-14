package com.example.account.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
/**
 * @writer 제로베이스 3기 문진수
 *
 */
public class DeleteAccount {

    @Setter
    @Getter
    @AllArgsConstructor
    public static class Request {
        @NotNull
        @Min(1)
        private Long userId;

        @NotBlank
        @Size(min=10,max=10)
        private String accountNumber;


    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Setter
    @Getter
    public static class Response {
        private Long userId;
        private String accountNumber;
        private LocalDateTime unRegisteredAt;

     public static Response from (AccountDto dto){
         return Response.builder()
                 .userId(dto.getUserId())
                 .accountNumber(dto.getAccountNumber())
                 .unRegisteredAt(dto.getUnRegisteredAt())
                 .build();
     };


    }



}
