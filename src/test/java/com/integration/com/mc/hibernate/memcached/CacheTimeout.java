package com.integration.com.mc.hibernate.memcached;

import com.integration.com.mc.hibernate.memcached.entities.Person;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

public class CacheTimeout extends AbstractHibernateTestCase {

    @Test
    public void testQueryCacheQuickExpire() throws Exception {

        Properties extraProp = new Properties();
        extraProp.put("hibernate.memcached.com.integration.com.mc.hibernate.memcached.entities.Person.cacheTimeSeconds", "2");

        SessionFactory sessionFactory = getConfiguration(extraProp).buildSessionFactory();

        Person p = new Person(10L, "John");

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.save(p);
        transaction.commit();

        session.get(Person.class, 10L);

        boolean containsEntity = sessionFactory.getCache().containsEntity(Person.class, 10L);
        Assert.assertTrue(containsEntity);

        Thread.sleep(3000);

        containsEntity = sessionFactory.getCache().containsEntity(Person.class, 10L);
        Assert.assertFalse(containsEntity);

    }


    @Test
    public void testQueryCacheSlowExpire() throws Exception {

        Properties extraProp = new Properties();
        extraProp.put("hibernate.memcached.com.integration.com.mc.hibernate.memcached.entities.Person.cacheTimeSeconds", "5");

        SessionFactory sessionFactory = getConfiguration(extraProp).buildSessionFactory();

        Person p = new Person(101L, "Long John");

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.save(p);
        transaction.commit();

        session.get(Person.class, 101L);

        boolean containsEntity = sessionFactory.getCache().containsEntity(Person.class, 101L);
        Assert.assertTrue(containsEntity);

        Thread.sleep(3000);

        containsEntity = sessionFactory.getCache().containsEntity(Person.class, 101L);
        Assert.assertTrue(containsEntity);

    }

    @Test
    public void testNoExpireProvided() throws Exception {

        SessionFactory sessionFactory = getConfiguration(null).buildSessionFactory();

        long anneId = 11L;
        Person p = new Person(anneId, "Anne");

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.save(p);
        transaction.commit();

        session.get(Person.class, anneId);

        boolean containsEntity = sessionFactory.getCache().containsEntity(Person.class, anneId);
        Assert.assertTrue(containsEntity);

        Thread.sleep(3000);

        containsEntity = sessionFactory.getCache().containsEntity(Person.class, anneId);
        Assert.assertTrue(containsEntity);

    }


}
