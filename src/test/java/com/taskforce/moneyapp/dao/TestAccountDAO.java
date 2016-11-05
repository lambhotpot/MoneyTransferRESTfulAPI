package com.taskforce.moneyapp.dao;


import com.taskforce.moneyapp.exception.DAOException;
import com.taskforce.moneyapp.objectModel.Account;
import com.taskforce.moneyapp.objectModel.UserTransaction;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertTrue;


public class TestAccountDAO {
    private static Logger log = Logger.getLogger(TestAccountDAO.class);
    private static final DAOFactory h2DaoFactory =
            DAOFactory.getDAOFactory(DAOFactory.H2);
    private static final int THREADS_COUNT = 100;

        @BeforeClass
        public static void setup() {
            //prepare test database and test data
            h2DaoFactory.populateTestData();
        }

        @After
        public void tearDown() {

    }

    @Test
    public void testAccountSingleThreadSameCcyTransfer() throws DAOException {

        final AccountDAO accountDAO = h2DaoFactory.getAccountDAO();

        BigDecimal transferAmount = new BigDecimal(50.01234);
        transferAmount.setScale(4, RoundingMode.HALF_EVEN);

        UserTransaction transaction = new UserTransaction("EUR",transferAmount,3L,4L);

        long startTime = System.currentTimeMillis();

        accountDAO.transferAccountBalance(transaction);
        long endTime = System.currentTimeMillis();

        log.info("TransferAccountBalance finished, time taken: " + (endTime - startTime) + "ms");

        Account accountFrom = accountDAO.getAccountById(3);

        Account accountTo = accountDAO.getAccountById(4);

        log.debug("Account From: "+ accountFrom);

        log.debug("Account From: "+ accountTo);


        assertTrue(accountFrom.getBalance().compareTo(new BigDecimal(449.9877).setScale(4,RoundingMode.HALF_EVEN))==0);
        assertTrue(accountTo.getBalance().equals(new BigDecimal(550.0123).setScale(4,RoundingMode.HALF_EVEN)));


    }



    @Test
    public void testAccountMultiThreadedTransfer() throws InterruptedException, DAOException {
        final AccountDAO accountDAO = h2DaoFactory.getAccountDAO();

        final CountDownLatch latch = new CountDownLatch(THREADS_COUNT);
        for(int i = 0;i < THREADS_COUNT; i++) {
            new Thread(new Runnable(){

                @Override
                public void run() {
                    try{
                        UserTransaction transaction = new UserTransaction("USD",new BigDecimal(2).setScale(4,RoundingMode.HALF_EVEN),1L,2L);
                        accountDAO.transferAccountBalance(transaction);
                    }catch (Exception e){
                        log.error("Error occurred during transfer ", e);
                    }
                    finally {
                        latch.countDown();
                    }
                }
            }).start();
        }

        latch.await();

        Account accountFrom = accountDAO.getAccountById(1);

        Account accountTo = accountDAO.getAccountById(2);

        log.debug("Account From: "+ accountFrom);

        log.debug("Account From: "+ accountTo);

        assertTrue(accountFrom.getBalance().equals(new BigDecimal(0).setScale(4,RoundingMode.HALF_EVEN)));
        assertTrue(accountTo.getBalance().equals(new BigDecimal(300).setScale(4,RoundingMode.HALF_EVEN)));

    }
}