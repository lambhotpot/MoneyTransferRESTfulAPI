package com.taskforce.moneyapp.dao.impl;

import com.taskforce.moneyapp.dao.H2DAOFactory;
import com.taskforce.moneyapp.dao.UserDAO;
import com.taskforce.moneyapp.objectModel.User;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.List;


public class UserDAOImpl implements UserDAO {
    private static Logger log = Logger.getLogger(UserDAOImpl.class);
    private final String SQL_GET_USER_BY_ID = "SELECT * FROM User WHERE UserId = ? ";
    private final String SQL_GET_USER_BY_NAME = "SELECT * FROM User WHERE UserName = ? ";
    private final String SQL_INSERT_USER = "INSERT INTO User (UserName, EmailAddress) VALUES (?, ?)";
    private final String SQL_UPDATE_USER = "UPDATE User SET UserName = ?, EmailAddress = ? WHERE UserId = ? ";


    public List<User> getAllUsers() {
        throw new UnsupportedOperationException("Method Not Implemented");
    }

    public User getUserById(long userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User u = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_GET_USER_BY_ID);
            stmt.setLong(1, userId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                u = new User(rs.getLong("UserId"), rs.getString("UserName"), rs.getString("EmailAddress"));
                if (log.isDebugEnabled())
                    log.debug("Retrieve User: " + u);
            }
            return u;
        } catch (SQLException e) {
            throw new RuntimeException("getUserById(): Error reading user data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    public User getUserByName(String userName) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        User u = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_GET_USER_BY_NAME);
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            if (rs.next()) {
                u = new User(rs.getLong("UserId"), rs.getString("UserName"), rs.getString("EmailAddress"));
                if (log.isDebugEnabled())
                    log.debug("Retrieve User: " + u);
            }
            return u;
        } catch (SQLException e) {
            throw new RuntimeException("getUserByName(): Error reading user data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    public long insertUser(User user) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getEmailAddress());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                log.error("Creating user failed, no rows affected.");
                return -1l;
            }
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong("UserId");
            } else {
                log.error("Creating user failed, no ID obtained.");
                return -1l;
            }
        } catch (SQLException e) {
            log.error("Error Inserting User :" + user);
            throw new RuntimeException("insertUser(): Error inserting user data", e);
        } finally {
            DbUtils.closeQuietly(conn,stmt,generatedKeys);

        }

    }

    public int updateUser(User user) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_USER);
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getEmailAddress());
            stmt.setLong(3, user.getUserId());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error Updating User :" + user);
            throw new RuntimeException("updateUser(): Error update user data", e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(stmt);
        }
    }

    public int deleteUser(long userId) {
        throw new UnsupportedOperationException("Method Not Implemented");
    }

}
