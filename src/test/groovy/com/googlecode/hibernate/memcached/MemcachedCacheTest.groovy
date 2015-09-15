package com.googlecode.hibernate.memcached

import com.mc.hibernate.memcached.MemcachedCache
import com.mc.hibernate.memcached.keystrategy.Sha1KeyStrategy

class MemcachedCacheTest extends BaseTestCase {

    MemcachedCache cache

    void test_basics() {
        cache = new MemcachedCache("region", new MockMemcached())
        assertNull cache.get("test")

        cache.put "test", "value"
        assertEquals "value", cache.get("test")

        cache.update "test", "blah"
        assertEquals "blah", cache.read("test")

        cache.remove "test"
        assertNull cache.get("test")

    }

    void test_dogpile_cache_miss() {
        MockMemcached mockCache = new MockMemcached()
        cache = new MemcachedCache("region", mockCache)
        def strategy = new Sha1KeyStrategy()
        cache.setKeyStrategy(strategy);
        cache.dogpilePreventionEnabled = true
        cache.cacheTimeSeconds = 1
        cache.dogpilePreventionExpirationFactor = 2
        def key = strategy.toKey("region", 0, "test")
        assertNull cache.get("test")
        assertEquals MemcachedCache.DOGPILE_TOKEN, mockCache.cache[key + ".dogpileTokenKey"]
        cache.put("test", "value")
        assertEquals "value", mockCache.cache[key]
    }

    void test_dogpile_cache_hit() {
        MockMemcached mockCache = new MockMemcached()
        cache = new MemcachedCache("region", mockCache)
        def strategy = new Sha1KeyStrategy()
        cache.setKeyStrategy(strategy);
        cache.dogpilePreventionEnabled = true
        cache.cacheTimeSeconds = 1
        cache.dogpilePreventionExpirationFactor = 2
        def key = strategy.toKey("region", 0, "test")
        cache.put("test", "value")
        assertEquals "value", mockCache.cache[key]
        assertEquals MemcachedCache.DOGPILE_TOKEN, mockCache.cache[key + ".dogpileTokenKey"]
    }

}
