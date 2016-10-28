package com.taskforce.moneyapp.dao;

import com.taskforce.moneyapp.objectModel.Account;

import java.util.List;


public interface AccountDAO {

    List<Account> getAllAccounts();
    Account getAccountById(long accountId);
    Account getAccountByName(String username);
    boolean createAccount(Account account);
    //boolean updateAccount(Account account);
    boolean deleteAccountByName(String username);
    boolean deleteAccountById(long accountId);
    boolean updateAccountBalance(long accountId, double balance);

}
