package com.taskforce.moneyapp.dao.impl;

import com.taskforce.moneyapp.dao.AccountDAO;
import com.taskforce.moneyapp.objectModel.Account;

import java.util.List;


public class AccountDAOImpl implements AccountDAO {

    public List<Account> getAllAccounts() {
        return null;
    }

    public Account getAccountById(long accountId) {
        return null;
    }

    public Account getAccountByName(String username) {
        return null;
    }

    public boolean createAccount(Account account) {
        return false;
    }

    public boolean deleteAccountByName(String username) {
        return false;
    }

    public boolean deleteAccountById(long accountId) {
        return false;
    }

    public boolean updateAccountBalance(long accountId, double balance) {
        return false;
    }
}
