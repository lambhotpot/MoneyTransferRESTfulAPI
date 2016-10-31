package com.taskforce.moneyapp.dao;


import com.taskforce.moneyapp.objectModel.Account;
import com.taskforce.moneyapp.objectModel.UserTransaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestAccountDAO {

    private final DAOFactory h2DaoFactory =
            DAOFactory.getDAOFactory(DAOFactory.H2);


    @Before
    public void setup() {
        //prepare test database
        h2DaoFactory.populateTestData();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testAccountSameCcyTransfer() {
        final AccountDAO accountDAO = h2DaoFactory.getAccountDAO();
        UserTransaction transaction = new UserTransaction("USD",50.1234,1L,2L);

        accountDAO.transferAccountBalance(transaction);

        Account accountFrom = accountDAO.getAccountById(1);
        Account accountTo = accountDAO.getAccountById(2);



        System.out.println("Account From: "+ accountFrom);

        System.out.println("Account From: "+ accountTo);
    }
}