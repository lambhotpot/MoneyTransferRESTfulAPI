package com.taskforce.moneyapp.dao.impl;

import com.taskforce.moneyapp.dao.H2DAOFactory;
import com.taskforce.moneyapp.dao.UserDAO;
import com.taskforce.moneyapp.exception.DAOException;
import com.taskforce.moneyapp.objectModel.User;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDAOImpl implements UserDAO {
    private static Logger log = Logger.getLogger(UserDAOImpl.class);
    private final static String SQL_GET_USER_BY_ID = "SELECT * FROM User WHERE UserId = ? ";
    private final static String SQL_GET_ALL_USERS = "SELECT * FROM User";
    private final static String SQL_GET_USER_BY_NAME = "SELECT * FROM User WHERE UserName = ? ";
    private final static String SQL_INSERT_USER = "INSERT INTO User (UserName, EmailAddress) VALUES (?, ?)";
    private final static String SQL_UPDATE_USER = "UPDATE User SET UserName = ?, EmailAddress = ? WHERE UserId = ? ";
    private final static String SQL_DELETE_USER_BY_ID = "DELETE * FROM User WHERE  UserId = ?";

    public List<User> getAllUsers() throws DAOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<User>();
        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ALL_USERS);
            rs = stmt.executeQuery();
            while (rs.next()) {
                User u = new User(rs.getLong("UserId"), rs.getString("UserName"), rs.getString("EmailAddress"));
                users.add(u);
                if (log.isDebugEnabled())
                    log.debug("getAllUsers() Retrieve User: " + u);
            }
            return users;
        } catch (SQLException e) {
            throw new DAOException("Error reading user data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    public User getUserById(long userId) throws DAOException {
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
                    log.debug("getUserById(): Retrieve User: " + u);
            }
            return u;
        } catch (SQLException e) {
            throw new DAOException("Error reading user data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    public User getUserByName(String userName) throws DAOException {
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
            throw new DAOException("Error reading user data", e);
        } finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    public long insertUser(User user) throws DAOException {
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
            throw new DAOException("Error creating user data", e);
        } finally {
            DbUtils.closeQuietly(conn,stmt,generatedKeys);
        }

    }

    public int updateUser(User user) throws DAOException {
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
            throw new DAOException("Error update user data", e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(stmt);
        }
    }

    public int deleteUser(long userId) throws DAOException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = H2DAOFactory.getConnection();
            stmt = conn.prepareStatement(SQL_DELETE_USER_BY_ID);
            stmt.setLong(1, userId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Error Deleting User :" + userId);
            throw new DAOException("Error Deleting User ID:"+ userId, e);
        } finally {
            DbUtils.closeQuietly(conn);
            DbUtils.closeQuietly(stmt);
        }
    }

}
