package com.taskforce.moneyapp.dao;

import com.taskforce.moneyapp.objectModel.Account;
import com.taskforce.moneyapp.objectModel.UserTransaction;

import java.math.BigDecimal;
import java.util.List;


public interface AccountDAO {

    List<Account> getAllAccounts();
    Account getAccountById(long accountId);
    int createAccount(Account account);
    int deleteAccountById(long accountId);
    int updateAccountBalance(long accountId, BigDecimal balance);
    int transferAccountBalance(UserTransaction userTransaction);
}
