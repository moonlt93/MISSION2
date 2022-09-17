package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.domain.Transaction;
import com.example.account.dto.TransactionDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.repository.TransactionRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import com.example.account.type.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.account.type.TransactionResultType.F;
import static com.example.account.type.TransactionResultType.S;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    public static final long USE_AMOUNT = 200L;
    public static final long CANCEL_AMOUNT = 200L;
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private TransactionService transactionService;


    @Test
    void successUseBalance(){
    //given
    AccountUser user = AccountUser.builder()
            .name("Pobi").build();
    user.setId(12L);
    Account account = Account.builder()
                    .accountUser(user).accountStatus(AccountStatus.IN_USE)
                    .balance(10000L).accountNumber("1000000012")
                    .build();
        given(accountUserRepository.findById(anyLong()))
            .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                                .account(account)
                                .transactionType(TransactionType.USE)
                                .transactionResultType(S)
                                .transactionId("transactionId")
                                .transactedAt(LocalDateTime.now())
                                .amount(1000L)
                                .balanceSnapshot(9000L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        //when
        TransactionDto transactionDto= transactionService.useBalance(1L,"1000000000",200L);

    //then
        verify(transactionRepository,times(1)).save(captor.capture());
        assertEquals(USE_AMOUNT,captor.getValue().getAmount());
        assertEquals(9800L,captor.getValue().getBalanceSnapshot());
        assertEquals(TransactionType.USE,transactionDto.getTransactionType());
        assertEquals(S,transactionDto.getTransactionResultType());
        assertEquals(9000L,transactionDto.getBalanceSnapshot());
        assertEquals(1000L,transactionDto.getAmount());


    }



    @Test
    @DisplayName("해당 유저 없음 -잔액 사용 실패")
    void useBalance_userNotFound(){
        //given

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        //when
        AccountException exception= assertThrows(AccountException.class,
                () ->transactionService.useBalance(1L,"10000000000", 1000L));

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND,exception.getErrorCode());
//왜냐면 서비스에서 createAccount 할때 번호를 1씩 올리니깐

    }

    @Test
    @DisplayName("해당 계좌 없음 - 잔액 사용 실패 ")
    void deleteAccount_AccountNotFound(){
        //given
        AccountUser user= AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());


        //when
        AccountException exception= assertThrows(AccountException.class,
                () ->transactionService.useBalance(1L,"10000000000", 1000L));

        //then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND,exception.getErrorCode());


//
    }


    @Test
    @DisplayName("계좌 소유주가 다름_잔액 사용불가 ")
    void deleteAccountFailed_userUnMatch(){
        //given
        AccountUser Pobi= AccountUser.builder()
                .name("Pobi").build();
        Pobi.setId(12L);
        AccountUser Harry= AccountUser.builder()
                .name("Harry").build();
        Harry.setId(13L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Pobi));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(Harry)
                        .balance(0L)
                        .accountNumber("1000000012")
                        .build()));

        //when
        AccountException exception= assertThrows(AccountException.class,
                () ->transactionService.useBalance(1L,"1000000000", 1000L));

        //then
        assertEquals(ErrorCode.USER_ACCOUNT_UN_MATCH,exception.getErrorCode());

        // 다른 소유주일때 Exception 발생!
//
    }

    @Test
    @DisplayName("계좌가 이미 없는 계좌일때")
    void deleteAccountFailed_alreadyUnregistered(){
        //given
        AccountUser pobi= AccountUser.builder()

                .name("Pobi").build();
        pobi.setId(12L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(pobi));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(pobi)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("1000000012")
                        .balance(0L)
                        .build()));

        //when
        AccountException exception= assertThrows(AccountException.class,
                () ->transactionService.useBalance(1L,"1000000000",1000L));

        //then
        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED,exception.getErrorCode());

        // 다른 소유주일때 Exception 발생!
//
    }


    @Test
    @DisplayName("계좌 잔액보다 거래 금액이 큰 경우")
    void FailUseBalance(){
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);
        Account account = Account.builder()
                .accountUser(user).accountStatus(AccountStatus.IN_USE)
                .balance(100L).accountNumber("1000000012")
                .build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));


        //when
        //then
        AccountException exception= assertThrows(AccountException.class,
                () ->transactionService.useBalance(1L,"1234567890",1000L));


        //then
        assertEquals(ErrorCode.AMOUNT_EXCEED_BALANCE,exception.getErrorCode());
        verify(transactionRepository,times(0)).save(any());


    }


    @Test
    @DisplayName("실패 트랜잭션 저장 성공")
    void saveFailedUseTransaction(){
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);
        Account account = Account.builder()
                .accountUser(user).accountStatus(AccountStatus.IN_USE)
                .balance(10000L).accountNumber("1000000012")
                .build();
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(TransactionType.USE)
                        .transactionResultType(S)
                        .transactionId("transactionId")
                        .transactedAt(LocalDateTime.now())
                        .amount(1000L)
                        .balanceSnapshot(9000L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        //when
      transactionService.saveFailedUseTransaction("1000000000",USE_AMOUNT);

        //then
        verify(transactionRepository,times(1)).save(captor.capture());
        assertEquals(USE_AMOUNT,captor.getValue().getAmount());
        assertEquals(10000L,captor.getValue().getBalanceSnapshot());
        assertEquals(F,captor.getValue().getTransactionResultType());



    }


    @Test
    void successCancelBalance(){
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);
        Account account = Account.builder()
                .accountUser(user).accountStatus(AccountStatus.IN_USE)
                .balance(10000L).accountNumber("1000000012")
                .build();
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(TransactionType.USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(CANCEL_AMOUNT)
                .balanceSnapshot(9000L)
                .build();
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(TransactionType.CANCEL)
                        .transactionResultType(S)
                        .transactionId("transactionId_forCancel")
                        .transactedAt(LocalDateTime.now())
                        .amount(CANCEL_AMOUNT)
                        .balanceSnapshot(10000L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        //when
        TransactionDto transactionDto= transactionService.cancelBalance
                ("transactionId","1000000000",CANCEL_AMOUNT);

        //then
        verify(transactionRepository,times(1)).save(captor.capture());
        assertEquals(CANCEL_AMOUNT,captor.getValue().getAmount());
        assertEquals(10000L+CANCEL_AMOUNT,captor.getValue().getBalanceSnapshot());
        assertEquals(TransactionType.CANCEL,transactionDto.getTransactionType());
        assertEquals(S,transactionDto.getTransactionResultType());
        assertEquals(10000L,transactionDto.getBalanceSnapshot());
        assertEquals(CANCEL_AMOUNT,transactionDto.getAmount());


    }



    @Test
    @DisplayName("해당 계좌 없음 - 잔액 사용 취소실패 ")
    void cancelTransaction_AccountNotFound(){
        //given
        AccountUser user= AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(Transaction.builder().build()));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());


        //when
        AccountException exception= assertThrows(AccountException.class,
                () ->transactionService.cancelBalance("transactionId","10000000000", 1000L));

        //then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND,exception.getErrorCode());
//
    }

    @Test
    @DisplayName("원 사용 거래 없음 - 잔액 사용 취소실패 ")
    void cancelTransaction_TransActionNotFound(){
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception= assertThrows(AccountException.class,
                () ->transactionService.cancelBalance("transactionId","10000000000", 1000L));

        //then
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND,exception.getErrorCode());

    }


    @Test
    @DisplayName("거래와 계좌가 매칭실패 - 잔액 사용 취소실패 ")
    void cancelTransaction_TransactionAccountUnMatch(){
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);
        Account account = Account.builder()
                .accountUser(user).accountStatus(AccountStatus.IN_USE)
                .balance(10000L).accountNumber("1000000012")
                .build();
        user.setId(1L);
        Account accountNotUse = Account.builder()
                .accountUser(user).accountStatus(AccountStatus.IN_USE)
                .balance(10000L).accountNumber("1000000013")
                .build();
        accountNotUse.setId(2L);
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(TransactionType.USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(CANCEL_AMOUNT)
                .balanceSnapshot(9000L)
                .build();
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(accountNotUse));


        //when
        AccountException exception= assertThrows(AccountException.class,
                () ->transactionService.cancelBalance(
                         "transactionId"
                        ,"10000000000"
                        , CANCEL_AMOUNT));

        //then
        assertEquals(ErrorCode.TRANSACTION_ACCOUNT_UN_MATCH,exception.getErrorCode());
//
    }


    @Test
    @DisplayName("거래금액과 취소금액이 다름 - 잔액 사용 취소실패 ")
    void cancelTransaction_CancelMustFully(){
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);
        Account account = Account.builder()
                .accountUser(user).accountStatus(AccountStatus.IN_USE)
                .balance(10000L).accountNumber("1000000012")
                .build();
        account.setId(1L);
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(TransactionType.USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(CANCEL_AMOUNT+1000L)
                .balanceSnapshot(9000L)
                .build();
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));


        //when
        AccountException exception= assertThrows(AccountException.class,
                () ->transactionService.cancelBalance(
                        "transactionId"
                        ,"10000000000"
                        , CANCEL_AMOUNT));

        //then
        assertEquals(ErrorCode.CANCEL_MUST_FULLY,exception.getErrorCode());
//
    }

    @Test
    @DisplayName("취소는 1년까지만 가능 - 잔액 사용 취소실패 ")
    void cancelTransaction_TOO_OLD_ORDER(){
        //given
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(12L);
        Account account = Account.builder()
                .accountUser(user).accountStatus(AccountStatus.IN_USE)
                .balance(10000L).accountNumber("1000000012")
                .build();
        account.setId(1L);
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(TransactionType.USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now().minusYears(1).minusDays(1))
                .amount(CANCEL_AMOUNT)
                .balanceSnapshot(9000L)
                .build();
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));


        //when
        AccountException exception= assertThrows(AccountException.class,
                () ->transactionService.cancelBalance(
                        "transactionId"
                        ,"10000000000"
                        , CANCEL_AMOUNT));

        //then
        assertEquals(ErrorCode.TOO_OLD_ORDER_TO_CANCEL,exception.getErrorCode());
//
    }

    @Test
    void successQueryTransaction(){
    //given
        AccountUser user = AccountUser.builder()
                .name("Pobi").build();
        user.setId(1L);
        Account account = Account.builder()
                .accountUser(user).accountStatus(AccountStatus.IN_USE)
                .balance(10000L).accountNumber("1000000012")
                .build();
        account.setId(1L);
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(TransactionType.USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now().minusYears(1).minusDays(1))
                .amount(CANCEL_AMOUNT)
                .balanceSnapshot(9000L)
                .build();
    given(transactionRepository.findByTransactionId(anyString()))
            .willReturn(Optional.of(transaction));
    //when
        TransactionDto transactionDto = transactionService.queryTransaction("trxId");
        //then
    assertEquals(TransactionType.USE,transactionDto.getTransactionType());
    assertEquals(S,transactionDto.getTransactionResultType());
    assertEquals(CANCEL_AMOUNT,transactionDto.getAmount());
    assertEquals("transactionId",transactionDto.getTransactionId());

    }


    @Test
    @DisplayName("원 거래 없음 - 거래 조회실패 ")
    void queryTransaction_TransActionNotFound(){
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception= assertThrows(AccountException.class,
                () ->transactionService.queryTransaction("transactionId"));

        //then
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND,exception.getErrorCode());

    }
}