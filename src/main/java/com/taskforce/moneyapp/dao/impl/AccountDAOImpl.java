package com.taskforce.moneyapp.dao.impl;

import com.taskforce.moneyapp.dao.AccountDAO;
import com.taskforce.moneyapp.objectModel.Account;

import java.util.List;


public class AccountDAOImpl implements AccountDAO {
    private final String SQL_GET_ACC_BY_ID = "SELECT * FROM Account WHERE AccountId = ? ";
    private final String SQL_GET_ACC_BY_USER_NAME = "SELECT * FROM Account WHERE UserName = ? ";
    private final String SQL_CREATE_ACC= "INSERT INTO Account (UserName, Balance, CurrencyCode) VALUES (?, ?, ?)";
    private final String SQL_UPDATE_ACC_BALANCE = "UPDATE Account SET Balance = ? WHERE AccountId = ? ";


    public List<Account> getAllAccounts() {
        return null;
    }

    public Account getAccountById(long accountId) {
        return null;
    }

    public Account getAccountByName(String username) {
        return null;
    }

    public int createAccount(Account account) {
        return 0;
    }

    public int deleteAccountByName(String username) {
        return 0;
    }

    public int deleteAccountById(long accountId) {
        return 0;
    }

    public int updateAccountBalance(long accountId, double balance) {
        return 0;
    }

}
