package com.example.account.dto;

import com.example.account.type.ErrorCode;
import lombok.*;
/**
 * @writer 제로베이스 3기 문진수
 *
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

    private ErrorCode errorCode;
    private String errorMessage;


}
