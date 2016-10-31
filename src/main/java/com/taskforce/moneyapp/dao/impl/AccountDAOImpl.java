package com.taskforce.moneyapp.dao.impl;

import com.taskforce.moneyapp.dao.AccountDAO;
import com.taskforce.moneyapp.dao.H2DAOFactory;
import com.taskforce.moneyapp.objectModel.Account;
import com.taskforce.moneyapp.objectModel.UserTransaction;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class AccountDAOImpl implements AccountDAO {
    private static Logger log = Logger.getLogger(AccountDAOImpl.class);
    private final String SQL_GET_ACC_BY_ID = "SELECT * FROM Account WHERE AccountId = ? ";
    private final String SQL_LOCK_ACC_BY_ID = "SELECT * FROM Account WHERE AccountId = ? FOR UPDATE";
    private final String SQL_GET_ACC_BY_USER_NAME = "SELECT * FROM Account WHERE UserName = ? ";
    private final String SQL_CREATE_ACC = "INSERT INTO Account (UserName, Balance, CurrencyCode) VALUES (?, ?, ?)";
    private final String SQL_UPDATE_ACC_BALANCE = "UPDATE Account SET Balance = ? WHERE AccountId = ? ";


    public List<Account> getAllAccounts() {
        return null;
    }

    public Account getAccountById(long accountId) {
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
                acc = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getDouble("Balance"), rs.getString("CurrencyCode"));
                if (log.isDebugEnabled())
                    log.debug("Retrieve Account By Id: " + acc);
            }
            return acc;
        } catch (SQLException e) {
            throw new RuntimeException("getAccountById(): Error reading account data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }


    }

    public Account getAccountByName(String username) {
        return null;
    }

    public int createAccount(Account account) {
        return 0;
    }

    public int deleteAccountByName(String username) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public int deleteAccountById(long accountId) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public int updateAccountBalance(long accountId, double balance) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
            stmt.setDouble(1, balance);
            stmt.setLong(2, accountId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(String.format("Error Updating Account Id = %d Balance = %.4f: ", accountId, balance));
            throw new RuntimeException("updateAccountBalance(): Error update user data", e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(stmt);
        }
    }


    public int transferAccountBalance(UserTransaction userTransaction) {
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
                fromAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getDouble("Balance"), rs.getString("CurrencyCode"));
                if (log.isDebugEnabled())
                    log.debug("transferAccountBalance from Account: " + fromAccount);
            }
            lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
            lockStmt.setLong(1, userTransaction.getToAccountId());
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                toAccount = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getDouble("Balance"), rs.getString("CurrencyCode"));
                if (log.isDebugEnabled())
                    log.debug("transferAccountBalance to Account: " + toAccount);
            }

            if(fromAccount != null && toAccount != null){

                updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
                //TODO: money function for calculating
                updateStmt.setDouble(1, fromAccount.getBalance() - userTransaction.getAmount());
                updateStmt.setLong(2, userTransaction.getFromAccountId());
                updateStmt.addBatch();
                updateStmt.setDouble(1, toAccount.getBalance() + userTransaction.getAmount());
                updateStmt.setLong(2, userTransaction.getToAccountId());
                updateStmt.addBatch();
                updateStmt.executeBatch();
            }
            // If there is no error, commit the transaction
            conn.commit();
        } catch (SQLException se) {
            // rollback transaction if exception occurs
            try {
                conn.rollback();
            } catch (SQLException e) {
                throw new RuntimeException("transferAccountBalance(): Fail to rollback transaction");
            }
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(lockStmt);
            DbUtils.closeQuietly(updateStmt);
        }

        return 0;
    }

}
