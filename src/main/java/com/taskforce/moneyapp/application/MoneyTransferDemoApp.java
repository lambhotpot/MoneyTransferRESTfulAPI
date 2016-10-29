package com.taskforce.moneyapp.application;


import com.taskforce.moneyapp.dao.DAOFactory;
import com.taskforce.moneyapp.services.UserService;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;


public class MoneyTransferDemoApp {
    static Logger log = Logger.getLogger(MoneyTransferDemoApp.class);


    public static void main(String[] args) throws Exception {
        //Initialize H2 database with demo data
        log.info("Initialize demo .....");
        DAOFactory h2DaoFactory =
                DAOFactory.getDAOFactory(DAOFactory.H2);
        h2DaoFactory.populateTestData();
        log.info("Initialisation Complete....");

        //test
        /*UserDAO userDAO = h2DaoFactory.getUserDAO();
        log.debug(userDAO.getUserById(2));
        log.debug(userDAO.getUserByName("yangluo"));
        User newUser = new User("yangluo2", "yangluo2@gmail.com");
        userDAO.insertUser(newUser);
        log.debug(userDAO.getUserByName("yangluo2"));
        User newUpdateUser = new User(2, "yangluo3", "yangluo3@gmail.com");
        userDAO.updateUser(newUpdateUser);
        log.debug(userDAO.getUserById(2));*/

        //Host service on jetty
        startService();

    }

    private static void startService() throws Exception {
        Server server = new Server(8083);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
        servletHolder.setInitParameter("jersey.config.server.provider.classnames",
                UserService.class.getCanonicalName());
        //  ClientService.class.getCanonicalName() + "," +
        //  CurrencyService.class.getCanonicalName());

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }

}
