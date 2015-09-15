package com.googlecode.hibernate.memcached.integration;

import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;

import com.mc.hibernate.memcached.MemcachedRegionFactory;

public abstract class AbstractHibernateTestCase {

    protected Session session;
    protected Transaction transaction;

    static {
        System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.Log4JLogger");
    }


    private Configuration getConfiguration() {
        AnnotationConfiguration config = new AnnotationConfiguration();

        Properties properties = new Properties();
        properties.putAll(getDefaultProperties());
        properties.putAll(getConfigProperties());

        config.setProperties(properties);
        config.addAnnotatedClass(Contact.class);


        return config;
    }

    private Properties getDefaultProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.connection.driver_class", org.hsqldb.jdbcDriver.class.getName());
        props.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:test");
        props.setProperty("hibernate.connection.username", "sa");
        props.setProperty("hibernate.connection.password", "");
        props.setProperty("hibernate.cache.region.factory_class", MemcachedRegionFactory.class.getName());
        props.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        return props;
    }

    protected Properties getConfigProperties() {
        return new Properties();
    }

    void setupBeforeTransaction() {
    }

    @Before
    public void setUp() {
        setupBeforeTransaction();
        SessionFactory sessionFactory = getConfiguration().buildSessionFactory();
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        setupInTransaction();
    }

    protected void setupInTransaction() {
    }

    protected void tearDownInTransaction() {
    }

    @After
    public void tearDown() {
        try {
            tearDownInTransaction();
        } finally {
            transaction.rollback();
            session.close();
        }
    }

}
