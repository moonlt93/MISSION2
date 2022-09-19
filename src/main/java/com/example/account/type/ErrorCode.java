package com.example.account.type;


import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * @writer 제로베이스 3기 문진수
 *
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {


    INTERNAL_SERVER_ERROR("내부 서버오류가 있습니다."),
    INVALID_REQUEST("요청이 유효하지 않습니다"),
    USER_NOT_FOUND("사용자가 없습니다."),
    ACCOUNT_NOT_FOUND("계좌가 없습니다."),
    ACCOUNT_TRANSACTION_LOCK("해당 계좌는 현재 사용중입니다."),
    AMOUNT_EXCEED_BALANCE("거래금액이 계좌 잔액보다 큽니다."),
    USER_ACCOUNT_UN_MATCH("계좌의 소유주가 없습니다."),
    ACCOUNT_ALREADY_UNREGISTERED("계좌가 이미 해지되었습니다."),
    BALANCE_NOT_EMPTY("잔액이 남아있는 계좌는 해지할 수 없습니다."),
    MAX_ACCOUNT_PER_USER_10("최대 가능 계좌 갯수는 10개 입니다."),

    TRANSACTION_NOT_FOUND("해당 거래가 없습니다."),

    TRANSACTION_ACCOUNT_UN_MATCH("이 거래는 해당 계좌에서 발생한 거래가 아닙니다."),

    CANCEL_MUST_FULLY("부분 취소는 허용되지 않습니다."),

    TOO_OLD_ORDER_TO_CANCEL("거래내역이 1년이 지난 거래는 취소가 불가능합니다.")
    ;



    private final String description;

}
