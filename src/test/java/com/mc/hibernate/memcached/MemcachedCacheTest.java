package com.mc.hibernate.memcached;

import com.mc.hibernate.memcached.keystrategy.KeyStrategy;
import com.mc.hibernate.memcached.keystrategy.Sha1KeyStrategy;
import com.mc.hibernate.memcached.mock.MockMemcached;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MemcachedCacheTest extends BaseTest {

    private MemcachedCache cache;

    private Config emptyConfig = new Config(new PropertiesHelper(new Properties()));

    @Test
    public void testBasics() {
        cache = new MemcachedCache("region", new MockMemcached(), emptyConfig);
        assertNull(cache.get("test"));

        cache.put("test", "value");
        assertEquals("value", cache.get("test"));

        cache.update("test", "blah");
        assertEquals("blah", cache.read("test"));

        cache.remove("test");
        assertNull(cache.get("test"));

    }

    @Test
    public void testDogpileCacheMiss() {
        MockMemcached mockCache = new MockMemcached();

        cache = new MemcachedCache("region", mockCache, emptyConfig);
        Sha1KeyStrategy strategy = new Sha1KeyStrategy();
        cache.setKeyStrategy(strategy);
        cache.setDogpilePreventionEnabled(true);
        cache.setCacheTimeSeconds(1);
        cache.setDogpilePreventionExpirationFactor(2);
        String key = strategy.toKey("region", 0, "test");
        assertNull(cache.get("test"));
        assertEquals(MemcachedCache.DOGPILE_TOKEN, mockCache.get(key + ".dogpileTokenKey"));
        cache.put("test", "value");
        assertEquals("value", mockCache.get(key));
    }

    @Test
    public void testDogpileCacheHit() {
        MockMemcached mockCache = new MockMemcached();
        cache = new MemcachedCache("region", mockCache, emptyConfig);
        KeyStrategy strategy = new Sha1KeyStrategy();
        cache.setKeyStrategy(strategy);
        cache.setDogpilePreventionEnabled(true);
        cache.setCacheTimeSeconds(1);
        cache.setDogpilePreventionExpirationFactor(2);
        String key = strategy.toKey("region", 0, "test");
        cache.put("test", "value");
        assertEquals("value", mockCache.get(key));
        assertEquals(MemcachedCache.DOGPILE_TOKEN, mockCache.get(key + ".dogpileTokenKey"));
    }

    @Test
    public void testClearCacheWithProperties() {
        MockMemcached mockCache = new MockMemcached();
        Properties properties = new Properties();
        properties.put("hibernate.memcached.clearSupported", "true");
        Config config = new Config(new PropertiesHelper(properties));

        cache = new MemcachedCache("region", mockCache, config);
        cache.put("test", "value");
        Object retrieved = cache.get("test");
        assertEquals("value", retrieved);

        cache.clear();

        Object retrievedAfterClean = cache.get("test");
        assertNull(retrievedAfterClean);
    }


}
