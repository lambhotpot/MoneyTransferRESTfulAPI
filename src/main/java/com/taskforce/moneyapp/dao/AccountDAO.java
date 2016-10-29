package com.taskforce.moneyapp.dao;

import com.taskforce.moneyapp.objectModel.Account;

import java.util.List;


public interface AccountDAO {

    List<Account> getAllAccounts();
    Account getAccountById(long accountId);
    Account getAccountByName(String username);
    int createAccount(Account account);
    int deleteAccountByName(String username);
    int deleteAccountById(long accountId);
    int updateAccountBalance(long accountId, double balance);

}
