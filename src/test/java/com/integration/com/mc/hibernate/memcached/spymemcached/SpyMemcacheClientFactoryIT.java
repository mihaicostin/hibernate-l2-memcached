package com.integration.com.mc.hibernate.memcached.spymemcached;

import com.mc.hibernate.memcached.Memcache;
import com.mc.hibernate.memcached.PropertiesHelper;
import com.mc.hibernate.memcached.spymemcached.SpyMemcache;
import com.mc.hibernate.memcached.spymemcached.SpyMemcacheClientFactory;
import net.spy.memcached.DefaultHashAlgorithm;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SpyMemcacheClientFactoryIT {

    private Properties properties = new Properties();
    private Memcache client;
    private SpyMemcacheClientFactory factory = new SpyMemcacheClientFactory(new PropertiesHelper(properties));

    @Test
    public void testDefaults() throws Exception {
        client = factory.createMemcacheClient();
        Assert.assertNotNull(client);
    }

    @Test
    public void testAllPropertiesSet() throws Exception {

        properties.setProperty("hibernate.memcached.servers", "localhost:11211 localhost:11212");
        properties.setProperty("hibernate.memcached.hashAlgorithm", DefaultHashAlgorithm.CRC_HASH.name());
        properties.setProperty("hibernate.memcached.operationQueueLength", "8192");
        properties.setProperty("hibernate.memcached.readBufferLength", "8192");
        properties.setProperty("hibernate.memcached.operationTimeout", "5000");
        properties.setProperty("hibernate.memcached.daemonMode", "true");

        client = factory.createMemcacheClient();
        Assert.assertNotNull(client);
    }

    @Test
    public void testNoAuth() throws Exception {
        client = factory.createMemcacheClient();
        assertTrue(client instanceof SpyMemcache);
        int anInt = new Random().nextInt();

        client.set(String.valueOf(anInt), 1, anInt);
        Object retrieved = client.get(String.valueOf(anInt));

        assertEquals(anInt, retrieved);
    }

    @Test
    public void testAuth() throws Exception {
        String username = "user";
        String password = "pass";

        properties.setProperty("hibernate.memcached.connectionFactory", "BinaryConnectionFactory");
        properties.setProperty("hibernate.memcached.username", username);
        properties.setProperty("hibernate.memcached.password", password);

        client = factory.createMemcacheClient();
        assertTrue(client instanceof SpyMemcache);

        client.set("ten", 1, 10L);

        //auth failed
        Assert.assertNull(client.get("ten"));
    }

    @After
    public void tearDown() {
        if (client != null) {
            client.shutdown();
        }
    }
}
