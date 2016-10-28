package com.taskforce.moneyapp.dao;


import com.taskforce.moneyapp.dao.impl.AccountDAOImpl;
import com.taskforce.moneyapp.dao.impl.UserDAOImpl;

public class H2DAOFactory extends DAOFactory {


    public UserDAO getUserDAO() {
        return new UserDAOImpl();
    }

    public AccountDAO getAccountDAO() {
        return new AccountDAOImpl();
    }
}
