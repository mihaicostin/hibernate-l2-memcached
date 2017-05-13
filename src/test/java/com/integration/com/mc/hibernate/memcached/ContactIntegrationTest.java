package com.integration.com.mc.hibernate.memcached;

import com.integration.com.mc.hibernate.memcached.entities.Contact;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Properties;

import static org.hibernate.criterion.Restrictions.eq;
import static org.junit.Assert.*;

public class ContactIntegrationTest extends AbstractHibernateTestCase {

    private Contact contact;
    private Calendar birthday;

    private SessionFactory sessionFactory;

    @Before
    public void setUp() {
        Properties props = new Properties();

        props.setProperty("hibernate.cache.use_query_cache", "true");
        props.setProperty("hibernate.memcached.clearSupported", "true");

        sessionFactory = getConfiguration(props).buildSessionFactory();
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        setupInTransaction();
    }

    @After
    public void tearDown() {
        transaction.rollback();
        session.close();
    }

    private void setupInTransaction() {

        contact = new Contact();
        contact.setFirstName("Jon");
        contact.setLastName("Snow");

        birthday = Calendar.getInstance();
        birthday.set(Calendar.HOUR_OF_DAY, 0);
        birthday.set(Calendar.MINUTE, 0);
        birthday.set(Calendar.SECOND, 0);
        birthday.set(Calendar.MILLISECOND, 0);

        contact.setBirthday(birthday.getTime());

        session.saveOrUpdate(contact);
        session.flush();
        session.clear();
    }

    @Test
    public void test() {
        Contact fromDB = (Contact) session.get(Contact.class, contact.getId());
        assertNotNull(fromDB);
    }

    @Test
    public void testQueryCache() {
        Criteria criteria = session.createCriteria(Contact.class)
                .add(eq("firstName", "Jon"))
                .add(eq("lastName", "Snow"))
                .setCacheable(true)
                .setCacheRegion("contact.findByFirstNameAndLastName");

        assertNotNull(criteria.uniqueResult());

        criteria.uniqueResult();
        criteria.uniqueResult();
        criteria.uniqueResult();
        criteria.uniqueResult();

        assertEquals(criteria.uniqueResult(), criteria.uniqueResult());
    }

    @Test
    public void testQueryCacheWithDate() throws Exception {

        Thread.sleep(1000);

        Criteria criteria = session.createCriteria(Contact.class)
                .add(eq("firstName", "Jon"))
                .add(eq("lastName", "Snow"))
                .add(eq("birthday", birthday.getTime()))
                .setCacheable(true)
                .setCacheRegion("contact.findByFirstNameAndLastNameAndBirthday");

        assertNotNull(criteria.uniqueResult());
        criteria.uniqueResult();
        criteria.uniqueResult();
        criteria.uniqueResult();

        assertEquals(criteria.uniqueResult(), criteria.uniqueResult());
    }
}
