package com.example.account.dto;


import lombok.*;
/**
 * @writer 제로베이스 3기 문진수
 *
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo {


    private String accountNumber;
    private Long balance;


}
