package com.integration.com.mc.hibernate.memcached;

import com.integration.com.mc.hibernate.memcached.entities.Book;
import com.integration.com.mc.hibernate.memcached.entities.Contact;
import com.integration.com.mc.hibernate.memcached.entities.Person;
import com.mc.hibernate.memcached.BaseTest;
import com.mc.hibernate.memcached.MemcachedRegionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public abstract class AbstractHibernateTestCase extends BaseTest {

    protected Session session;

    protected Transaction transaction;

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

    protected Configuration getConfiguration(Properties prop) {
        Configuration config = new Configuration();
        Properties properties = new Properties();
        properties.putAll(getDefaultProperties());
        if (prop != null) {
            properties.putAll(prop);
        }
        config.setProperties(properties);

        config.addAnnotatedClass(Person.class);
        config.addAnnotatedClass(Contact.class);
        config.addAnnotatedClass(Book.class);

        return config;
    }

    protected Configuration getConfiguration() {
        return getConfiguration(null);
    }

}
