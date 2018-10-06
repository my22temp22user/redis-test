package com.test;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        String applicationContextName = Configuration.get().getApplicationContextName();

        logger.info("starting app with application context " + applicationContextName);

        new ClassPathXmlApplicationContext(applicationContextName);
    }
}
