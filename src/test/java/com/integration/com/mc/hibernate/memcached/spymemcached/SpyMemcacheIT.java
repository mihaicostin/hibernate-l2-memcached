package com.integration.com.mc.hibernate.memcached.spymemcached;

import com.mc.hibernate.memcached.Config;
import com.mc.hibernate.memcached.MemcachedCache;
import com.mc.hibernate.memcached.PropertiesHelper;
import com.mc.hibernate.memcached.spymemcached.SpyMemcache;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Map;
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
    public void test() throws InterruptedException {
        cache.put("test", "value");
        Thread.sleep(100);
        assertEquals("value", cache.get("test"));
    }

    @Test
    public void testClear() throws InterruptedException {
        cache.setClearSupported(true);
        cache.put("test", "value");
        Thread.sleep(100);
        assertEquals("value", cache.get("test"));
        cache.clear();
        Thread.sleep(100);
        assertNull(cache.get("test"));
    }

}
