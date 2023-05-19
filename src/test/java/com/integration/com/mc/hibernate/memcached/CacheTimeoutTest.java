package com.integration.com.mc.hibernate.memcached;

import com.integration.com.mc.hibernate.memcached.entities.Person;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;
import java.util.Random;

public class CacheTimeoutTest extends AbstractHibernateTestCase {

    private static long randomId() {
        Random r = new Random();
        return r.nextInt(10000)  + 1;
    }

    @Test
    public void testQueryCacheQuickExpire() throws Exception {

        Properties extraProp = new Properties();
        extraProp.put("hibernate.memcached.com.integration.com.mc.hibernate.memcached.entities.Person.cacheTimeSeconds", "2");

        SessionFactory sessionFactory = getConfiguration(extraProp).buildSessionFactory();

        long id = randomId();
        Person p = new Person(id, "John");

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(p);
        transaction.commit();

        session.get(Person.class, id);

        boolean containsEntity = sessionFactory.getCache().containsEntity(Person.class, id);
        Assert.assertTrue(containsEntity);

        Thread.sleep(3000);

        containsEntity = sessionFactory.getCache().containsEntity(Person.class, id);
        Assert.assertFalse(containsEntity);

    }

    @Test
    public void testQueryCacheSlowExpire() throws Exception {

        Properties extraProp = new Properties();
        extraProp.put("hibernate.memcached.com.integration.com.mc.hibernate.memcached.entities.Person.cacheTimeSeconds", "5");

        SessionFactory sessionFactory = getConfiguration(extraProp).buildSessionFactory();

        long id = randomId();
        Person p = new Person(id, "Long John");

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(p);
        transaction.commit();

        session.get(Person.class, id);

        boolean containsEntity = sessionFactory.getCache().containsEntity(Person.class, id);
        Assert.assertTrue(containsEntity);

        Thread.sleep(3000);

        containsEntity = sessionFactory.getCache().containsEntity(Person.class, id);
        Assert.assertTrue(containsEntity);

    }

    @Test
    public void testNoExpireProvided() throws Exception {

        SessionFactory sessionFactory = getConfiguration(null).buildSessionFactory();

        long anneId = randomId();
        Person p = new Person(anneId, "Anne");

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(p);
        transaction.commit();

        session.get(Person.class, anneId);

        boolean containsEntity = sessionFactory.getCache().containsEntity(Person.class, anneId);
        Assert.assertTrue(containsEntity);

        Thread.sleep(3000);

        containsEntity = sessionFactory.getCache().containsEntity(Person.class, anneId);
        Assert.assertTrue(containsEntity);

    }


}
