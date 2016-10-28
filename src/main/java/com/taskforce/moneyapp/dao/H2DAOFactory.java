package com.taskforce.moneyapp.dao;


import com.taskforce.moneyapp.dao.impl.AccountDAOImpl;
import com.taskforce.moneyapp.dao.impl.UserDAOImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * H2 DAO
 */
public class H2DAOFactory extends DAOFactory {

    public static Connection createConnection() throws SQLException, ClassNotFoundException {
        //read from properties file
        Class.forName("org.h2.Driver");
        Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "test", "" );
        return connection;
    }

    public UserDAO getUserDAO() {
        return new UserDAOImpl();
    }

    public AccountDAO getAccountDAO() {
        return new AccountDAOImpl();
    }




}
