package com.mc.hibernate.memcached;

import org.apache.log4j.BasicConfigurator;

public abstract class BaseTest {

    static {
        //configure logging.
        System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.Log4JLogger");
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
    }
}
