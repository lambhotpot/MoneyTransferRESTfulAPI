package com.taskforce.moneyapp.services;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration testing for RestAPI
 */
public class TestUserService {
    private static Server server = null;

    @BeforeClass
    public static void setup() throws Exception {
        startServer();
    }

   /* @AfterClass
    public static void tearDown() {
        server.destroy();
    }*/

    @Test
    public void testGetUser() {
        System.out.print("Test server start");
    }

    private static void startServer() throws Exception {
        if (server == null) {
            server = new Server(8084);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);
            ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
            servletHolder.setInitParameter("jersey.config.server.provider.classnames",
                    UserService.class.getCanonicalName() + "," +
                            AccountService.class.getCanonicalName() + "," +
                            ServiceExceptionMapper.class.getCanonicalName() + "," +
                            TransactionService.class.getCanonicalName());
            server.start();
        }
    }

}
