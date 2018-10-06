package com.test;

import static com.test.Constants.*;

public class Configuration {

    private static Configuration INSTANCE = new Configuration();

    public static Configuration get() {
        return INSTANCE;
    }

    private Configuration() {
    }

    public int getServerPort() {
        return Integer.parseInt(System.getProperty(SERVER_PORT, String.valueOf(DEFAULT_SERVER_PORT)));
    }

    public String getRedisHost() {
        return System.getProperty(REDIS_HOST, DEFAULT_REDIS_HOST);
    }

    public int getRedisPort() {
        return Integer.parseInt(System.getProperty(REDIS_PORT, String.valueOf(DEFAULT_REDIS_PORT)));
    }

    public String getApplicationContextName() {
        return System.getProperty(APPLICATION_CONTEXT_NAME, DEFAULT_APPLICATION_CONTEXT_NAME);
    }
}
