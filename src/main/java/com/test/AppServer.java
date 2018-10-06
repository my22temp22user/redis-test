package com.test;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Required;

public class AppServer {

    private final Logger logger = Logger.getLogger(getClass());

    private RetrievingCreditCardServlet retrievingCreditCardServlet;
    private PersistingCreditCardServlet persistingCreditCardServlet;

    public void start() throws Exception {

        logger.info("starting jetty server on " + Configuration.get().getServerPort());

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        context.addServlet(new ServletHolder(persistingCreditCardServlet), "/creditcard");
        context.addServlet(new ServletHolder(retrievingCreditCardServlet), "/creditcard/*");

        Server server = new Server(Configuration.get().getServerPort());
        server.setHandler(context);

        server.start();
    }

    @Required
    public void setRetrievingCreditCardServlet(RetrievingCreditCardServlet retrievingCreditCardServlet) {
        this.retrievingCreditCardServlet = retrievingCreditCardServlet;
    }

    @Required
    public void setPersistingCreditCardServlet(PersistingCreditCardServlet persistingCreditCardServlet) {
        this.persistingCreditCardServlet = persistingCreditCardServlet;
    }
}
