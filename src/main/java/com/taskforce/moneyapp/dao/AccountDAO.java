package com.taskforce.moneyapp.dao;

import com.taskforce.moneyapp.exception.DAOException;
import com.taskforce.moneyapp.objectModel.Account;
import com.taskforce.moneyapp.objectModel.UserTransaction;

import java.math.BigDecimal;
import java.util.List;


public interface AccountDAO {

    List<Account> getAllAccounts() throws DAOException;
    Account getAccountById(long accountId) throws DAOException;
    long createAccount(Account account) throws DAOException;
    int deleteAccountById(long accountId) throws DAOException;

    /**
     *
     * @param accountId user accountId
     * @param deltaAmount amount to be debit(less than 0)/credit(greater than 0).
     * @return no. of rows updated
     */
    int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws DAOException;
    int transferAccountBalance(UserTransaction userTransaction) throws DAOException;
}
