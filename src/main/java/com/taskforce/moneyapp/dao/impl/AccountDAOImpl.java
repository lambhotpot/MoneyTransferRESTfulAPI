package com.taskforce.moneyapp.dao.impl;

import com.taskforce.moneyapp.dao.AccountDAO;
import com.taskforce.moneyapp.dao.H2DAOFactory;
import com.taskforce.moneyapp.objectModel.Account;
import com.taskforce.moneyapp.objectModel.UserTransaction;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class AccountDAOImpl implements AccountDAO {
    private static Logger log = Logger.getLogger(AccountDAOImpl.class);
    private final String SQL_GET_ACC_BY_ID = "SELECT * FROM Account WHERE AccountId = ? ";
    private final String SQL_LOCK_ACC_BY_ID = "SELECT * FROM Account WHERE AccountId = ? FOR UPDATE";
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
                acc = new Account(rs.getLong("AccountId"), rs.getString("UserName"), rs.getBigDecimal("Balance"), rs.getString("CurrencyCode"));
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


    public int createAccount(Account account) {
        return 0;
    }

    public int deleteAccountById(long accountId) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public int updateAccountBalance(long accountId, BigDecimal balance) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
            stmt.setBigDecimal(1, balance);
            stmt.setLong(2, accountId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error Updating Account Id = " + accountId + "Balance =  " + balance);
            throw new RuntimeException("updateAccountBalance(): Error update user data", e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(stmt);
        }
    }


    /**
     *
     * @param userTransaction
     * @return no. of rows in Account affected by the the user transaction. return -1 on error
     */
    public int transferAccountBalance(UserTransaction userTransaction) {
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
            //begin to transfer money between accounts
            if (fromAccount != null && toAccount != null && fromAccount.getCurrencyCode().equals(toAccount.getCurrencyCode())) {
                updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
                updateStmt.setBigDecimal(1, fromAccount.getBalance().subtract(userTransaction.getAmount()));
                updateStmt.setLong(2, userTransaction.getFromAccountId());
                updateStmt.addBatch();
                updateStmt.setBigDecimal(1, toAccount.getBalance().add(userTransaction.getAmount()));
                updateStmt.setLong(2, userTransaction.getToAccountId());
                updateStmt.addBatch();
                int[] rowsUpdated = updateStmt.executeBatch();
                result = rowsUpdated[0]+rowsUpdated[1];
                if (log.isDebugEnabled()){
                    log.debug("Number of rows updated for the transfer : " + result);
                }
            }
            // If there is no error, commit the transaction
            conn.commit();
        } catch (SQLException se) {
            // rollback transaction if exception occurs
            log.error("transferAccountBalance(): User Transaction Failed, rollback initiated for: " + userTransaction,se);
            try {
                conn.rollback();
            } catch (SQLException re) {
                throw new RuntimeException("transferAccountBalance(): Fail to rollback transaction",re);
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
