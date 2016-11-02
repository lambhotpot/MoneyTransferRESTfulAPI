package com.taskforce.moneyapp.dao;


import com.taskforce.moneyapp.objectModel.Account;
import com.taskforce.moneyapp.objectModel.UserTransaction;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;


public class TestAccountDAO {
    private static Logger log = Logger.getLogger(TestAccountDAO.class);
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

        UserTransaction transaction = new UserTransaction("USD",new BigDecimal(50.1234),1L,2L);

        long startTime = System.currentTimeMillis();

        accountDAO.transferAccountBalance(transaction);
        long endTime = System.currentTimeMillis();

        log.info("TransferAccountBalance finished, time taken: " + (endTime - startTime) + "ms");

        Account accountFrom = accountDAO.getAccountById(1);

        Account accountTo = accountDAO.getAccountById(2);

        

        System.out.println("Account From: "+ accountFrom);

        System.out.println("Account From: "+ accountTo);
    }
}