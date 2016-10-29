package com.taskforce.moneyapp.dao;


public abstract class DAOFactory {


    public static final int H2 = 1;
    //TODO: add other DAO factory code to migrate to different type of datasource

    public abstract UserDAO getUserDAO();
    public abstract AccountDAO getAccountDAO();
    public abstract void populateTestData();

    public static DAOFactory getDAOFactory(
            int factoryCode) {

        switch (factoryCode) {
            case H2:
                return new H2DAOFactory();
            default:
                return null;
        }
    }
}



