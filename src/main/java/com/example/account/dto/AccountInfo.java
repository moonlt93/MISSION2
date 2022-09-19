package com.example.account.dto;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo {
    //전용 dto륾 만드는 이유
    // 다목적으로 쓰면 의도치않은 오류가 생겨버린다.

    private String accountNumber;
    private Long balance;


}
