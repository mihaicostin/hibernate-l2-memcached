package com.integration.com.mc.hibernate.memcached.spymemcached;

import com.mc.hibernate.memcached.Config;
import com.mc.hibernate.memcached.MemcachedCache;
import com.mc.hibernate.memcached.PropertiesHelper;
import com.mc.hibernate.memcached.keystrategy.Md5KeyStrategy;
import com.mc.hibernate.memcached.keystrategy.Sha1KeyStrategy;
import com.mc.hibernate.memcached.spymemcached.SpyMemcache;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SpyMemcacheIT {

    private MemcachedCache cache;
    private MemcachedClient client;

    @Before
    public void setUp() throws IOException {
        client = new MemcachedClient(AddrUtil.getAddresses("localhost:11211"));
        Properties properties = new Properties();
        PropertiesHelper props = new PropertiesHelper(properties);
        Config config = new Config(props);
        cache = new MemcachedCache("MemcachedCacheTest", new SpyMemcache(client), config);
    }

    @After
    public void tearDown() {
        client.shutdown();
    }

    @Test
    public void testGetFromCache() throws InterruptedException {
        cache.put("test_1", "value");
        Thread.sleep(100);
        assertEquals("value", cache.get("test_1"));
    }

    @Test
    public void testClear() throws InterruptedException {
        cache.setClearSupported(true);
        cache.put("test_2", "value");
        Thread.sleep(100);
        assertEquals("value", cache.get("test_2"));
        cache.clear();
        Thread.sleep(100);
        assertNull(cache.get("test_2"));
    }

    @Test
    public void testPropertiesClear() throws InterruptedException {

        Properties properties = new Properties();
        properties.put("hibernate.memcached.clearSupported", "true");
        PropertiesHelper props = new PropertiesHelper(properties);
        Config config = new Config(props);
        cache = new MemcachedCache("MemcachedCacheTest", new SpyMemcache(client), config);

        cache.put("test_3", "value");
        Thread.sleep(100);
        assertEquals("value", cache.get("test_3"));

        cache.clear();
        Thread.sleep(100);
        assertNull(cache.get("test_3"));
    }

    @Test
    public void testPropertiesKeyStrategy() throws InterruptedException {

        Properties properties = new Properties();
        properties.put("hibernate.memcached.keyStrategy", Md5KeyStrategy.class.getName());
        PropertiesHelper props = new PropertiesHelper(properties);
        Config config = new Config(props);
        MemcachedCache mc1 = new MemcachedCache("MemcachedCacheTest", new SpyMemcache(client), config);
        assertEquals(mc1.getKeyStrategy().getClass(), Md5KeyStrategy.class);
        String key = "test_md5_" + System.currentTimeMillis();
        mc1.put(key, "value");
        Thread.sleep(100);
        assertEquals("value", mc1.get(key));
    }

    @Test
    public void testNoPropertyKeyStrategy() throws InterruptedException {

        Properties properties = new Properties();
        properties.put("hibernate.memcached.keyStrategy", "InvalidClass");
        PropertiesHelper props = new PropertiesHelper(properties);
        Config config = new Config(props);
        MemcachedCache mc1 = new MemcachedCache("MemcachedCacheTest", new SpyMemcache(client), config);
        assertEquals(mc1.getKeyStrategy().getClass(), Sha1KeyStrategy.class);
        String key = "test_6" + System.currentTimeMillis();
        mc1.put(key, "value");
        Thread.sleep(100);
        assertEquals("value", mc1.get(key));
    }

    @Test
    public void testInvalidPropertyKeyStrategy() throws InterruptedException {

        Properties properties = new Properties();
        PropertiesHelper props = new PropertiesHelper(properties);
        Config config = new Config(props);
        MemcachedCache mc1 = new MemcachedCache("MemcachedCacheTest", new SpyMemcache(client), config);
        assertEquals(mc1.getKeyStrategy().getClass(), Sha1KeyStrategy.class);
        String key = "test_7" + System.currentTimeMillis();
        mc1.put(key, "value");
        Thread.sleep(100);
        assertEquals("value", mc1.get(key));
    }


    @Test
    public void testClearFalse() throws InterruptedException {
        cache.setClearSupported(false);
        cache.put("test_8", "value");
        Thread.sleep(100);
        assertEquals("value", cache.get("test_8"));
        cache.clear();
        Thread.sleep(100);
        assertEquals("value", cache.get("test_8"));
    }


}
