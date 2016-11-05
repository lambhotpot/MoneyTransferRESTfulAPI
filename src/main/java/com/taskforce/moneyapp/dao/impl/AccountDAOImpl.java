package com.taskforce.moneyapp.dao.impl;

import com.taskforce.moneyapp.dao.AccountDAO;
import com.taskforce.moneyapp.dao.H2DAOFactory;
import com.taskforce.moneyapp.exception.DAOException;
import com.taskforce.moneyapp.objectModel.Account;
import com.taskforce.moneyapp.objectModel.UserTransaction;
import com.taskforce.moneyapp.utilities.MoneyUtil;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AccountDAOImpl implements AccountDAO {
    private static Logger log = Logger.getLogger(AccountDAOImpl.class);
    private final static String SQL_GET_ACC_BY_ID = "SELECT * FROM Account WHERE AccountId = ? ";
    private final static String SQL_LOCK_ACC_BY_ID = "SELECT * FROM Account WHERE AccountId = ? FOR UPDATE";
    private final static String SQL_CREATE_ACC = "INSERT INTO Account (UserName, Balance, CurrencyCode) VALUES (?, ?, ?)";
    private final static String SQL_UPDATE_ACC_BALANCE = "UPDATE Account SET Balance = ? WHERE AccountId = ? ";
    private final static String SQL_GET_ALL_ACC = "SELECT * FROM Account";
    private final static String SQL_DELETE_ACC_BY_ID = "DELETE * FROM Account WHERE AccountId = ?";

    public List<Account> getAllAccounts() throws DAOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Account> allAccounts = new ArrayList<Account>();
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ALL_ACC);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Account acc = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
                if (log.isDebugEnabled())
                    log.debug("getAllAccounts(): Get  Account " + acc);
                allAccounts.add(acc);
            }
            return allAccounts;
        } catch (SQLException e) {
            throw new DAOException("getAccountById(): Error reading account data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    public Account getAccountById(long accountId) throws DAOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Account acc = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ACC_BY_ID);
            stmt.setLong(1, accountId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                acc = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
                if (log.isDebugEnabled())
                    log.debug("Retrieve Account By Id: " + acc);
            }
            return acc;
        } catch (SQLException e) {
            throw new DAOException("getAccountById(): Error reading account data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }


    }


    public int createAccount(Account account) throws DAOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_CREATE_ACC);
            stmt.setString(1, account.getUserName());
            stmt.setBigDecimal(2, account.getBalance());
            stmt.setString(3, account.getCurrencyCode());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error Inserting Account  " + account);
            throw new DAOException("createAccount(): Error creating user account " + account, e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(stmt);
        }
    }

    public int deleteAccountById(long accountId) throws DAOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_DELETE_ACC_BY_ID);
            stmt.setLong(1, accountId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("deleteAccountById(): Error deleting user account Id " + accountId, e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(stmt);
        }
    }

    public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws DAOException {
        Connection conn = null;
        PreparedStatement lockStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        Account targetAccount = null;
        int updateCount = -1;
        try {
            conn = H2DAOFactory.getConnection();
            conn.setAutoCommit(false);
            //lock account for writing:
            lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
            lockStmt.setLong(1, accountId);
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                targetAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
                if (log.isDebugEnabled())
                    log.debug("updateAccountBalance from Account: " + targetAccount);
            }

            if (targetAccount == null) {
                throw new DAOException("updateAccountBalance(): fail to lock account : " + accountId);
            }
            //update account upon success locking
            BigDecimal balance = targetAccount.getBalance().add(deltaAmount);
            if(balance.compareTo(MoneyUtil.zeroAmount) < 0){
                throw new DAOException("updateAccountBalance(): Not sufficient Fund for account: " + accountId);
            }

            updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
            updateStmt.setBigDecimal(1, balance);
            updateStmt.setLong(2, accountId);
            updateCount = updateStmt.executeUpdate();
            conn.commit();
            if (log.isDebugEnabled())
                log.debug("New Balance after Update: " + targetAccount);
            return updateCount;
        } catch (SQLException se) {
            // rollback transaction if exception occurs
            log.error("updateAccountBalance(): User Transaction Failed, rollback initiated for: " + accountId, se);
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                throw new DAOException("updateAccountBalance(): Fail to rollback transaction", re);
            }
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(lockStmt);
            DbUtils.closeQuietly(updateStmt);
        }
        return updateCount;
    }


    /**
     * @param userTransaction User Transaction object
     * @return no. of rows in Account affected by the the user transaction. return -1 on error
     */

    public int transferAccountBalance(UserTransaction userTransaction) throws DAOException {
        int result = -1;
        Connection conn = null;
        PreparedStatement lockStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        Account fromAccount = null;
        Account toAccount = null;

        try {
            conn = H2DAOFactory.getConnection();
            conn.setAutoCommit(false);
            //lock the credit and debit account for writing:
            lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
            lockStmt.setLong(1, userTransaction.getFromAccountId());
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                fromAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
                if (log.isDebugEnabled())
                    log.debug("transferAccountBalance from Account: " + fromAccount);
            }
            lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
            lockStmt.setLong(1, userTransaction.getToAccountId());
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                toAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
                if (log.isDebugEnabled())
                    log.debug("transferAccountBalance to Account: " + toAccount);
            }


            //check locking status
            if (fromAccount == null || toAccount == null) {
                throw new DAOException("Fail to lock both accounts for write");
            }

            //check transaction currency
            if (!fromAccount.getCurrencyCode().equals(userTransaction.getCurrencyCode())) {
                throw new DAOException("Fail to transfer Fund, transaction ccy are different from source/destination");
            }

            //check ccy is the same for both accounts
            if (!fromAccount.getCurrencyCode().equals(toAccount.getCurrencyCode())) {
                throw new DAOException("Fail to transfer Fund, the source and destination account are in different currency");
            }

            //check enough fund in source account
            BigDecimal fromAccountLeftOver = fromAccount.getBalance().subtract(userTransaction.getAmount());
            if (fromAccountLeftOver.compareTo(MoneyUtil.zeroAmount) < 0) {
                throw new DAOException("Not enough Fund from source Account ");
            }
            //proceed with update
            updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
            updateStmt.setBigDecimal(1, fromAccountLeftOver);
            updateStmt.setLong(2, userTransaction.getFromAccountId());
            updateStmt.addBatch();
            updateStmt.setBigDecimal(1, toAccount.getBalance().add(userTransaction.getAmount()));
            updateStmt.setLong(2, userTransaction.getToAccountId());
            updateStmt.addBatch();
            int[] rowsUpdated = updateStmt.executeBatch();
            result = rowsUpdated[0] + rowsUpdated[1];
            if (log.isDebugEnabled()) {
                log.debug("Number of rows updated for the transfer : " + result);
            }
            // If there is no error, commit the transaction
            conn.commit();
        } catch (SQLException se) {
            // rollback transaction if exception occurs
            log.error("transferAccountBalance(): User Transaction Failed, rollback initiated for: " + userTransaction, se);
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                throw new DAOException("Fail to rollback transaction", re);
            }
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(lockStmt);
            DbUtils.closeQuietly(updateStmt);
        }
        return result;
    }

}
