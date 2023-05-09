package com.integration.com.mc.hibernate.memcached;

import com.integration.com.mc.hibernate.memcached.entities.Contact;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateHints;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Properties;

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
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Contact> criteriaQuery = criteriaBuilder.createQuery(Contact.class);
        Root<Contact> root = criteriaQuery.from(Contact.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("firstName"), "Jon"));
        criteriaQuery.where(criteriaBuilder.equal(root.get("lastName"), "Snow"));
        Contact contact1 = session.createQuery(criteriaQuery)
                .setHint(HibernateHints.HINT_CACHEABLE, true)
                .setHint(HibernateHints.HINT_CACHE_REGION, "contact.findByFirstNameAndLastName")
                .getSingleResultOrNull();
        Contact contact2 = session.createQuery(criteriaQuery)
                .setHint(HibernateHints.HINT_CACHEABLE, true)
                .setHint(HibernateHints.HINT_CACHE_REGION, "contact.findByFirstNameAndLastName")
                .getSingleResultOrNull();

        assertNotNull(contact1);
        assertEquals(contact1, contact2);
    }

    @Test
    public void testQueryCacheWithDate() throws Exception {

        Thread.sleep(1000);

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Contact> criteriaQuery = criteriaBuilder.createQuery(Contact.class);
        Root<Contact> root = criteriaQuery.from(Contact.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("firstName"), "Jon"));
        criteriaQuery.where(criteriaBuilder.equal(root.get("lastName"), "Snow"));
        criteriaQuery.where(criteriaBuilder.equal(root.get("birthday"), birthday.getTime()));
        Contact contact1 = session.createQuery(criteriaQuery)
                .setHint(HibernateHints.HINT_CACHEABLE, true)
                .setHint(HibernateHints.HINT_CACHE_REGION, "contact.findByFirstNameAndLastNameAndBirthday")
                .getSingleResultOrNull();
        Contact contact2 = session.createQuery(criteriaQuery)
                .setHint(HibernateHints.HINT_CACHEABLE, true)
                .setHint(HibernateHints.HINT_CACHE_REGION, "contact.findByFirstNameAndLastNameAndBirthday")
                .getSingleResultOrNull();

        assertNotNull(contact1);
        assertEquals(contact1, contact2);
    }
}
