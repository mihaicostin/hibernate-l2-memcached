package com.mc.hibernate.memcached;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class ConfigTest extends BaseTest {

    private Config newConfig(Properties props) {
        return new Config(new PropertiesHelper(props));
    }

    @Test
    public void testCacheTimeSeconds() {
        Properties p = new Properties();
        p.put("hibernate.memcached.cacheTimeSeconds", "10");
        p.put("hibernate.memcached.REGION.cacheTimeSeconds", "20");

        Config config = newConfig(p);
        assertEquals(10, config.getCacheTimeSeconds(null));
        assertEquals(20, config.getCacheTimeSeconds("REGION"));
    }

    @Test
    public void testClearSupported() {

        Properties p = new Properties();
        p.put("hibernate.memcached.clearSupported", "true");
        p.put("hibernate.memcached.REGION.clearSupported", "false");

        Config config = newConfig(p);
        assertTrue(config.isClearSupported(null));
        assertFalse(config.isClearSupported("REGION"));
    }

    @Test
    public void testKeyStrategyName() {

        Properties p = new Properties();
        p.put("hibernate.memcached.keyStrategy", "batman");
        p.put("hibernate.memcached.REGION.keyStrategy", "robin");

        Config config = newConfig(p);
        assertEquals("batman", config.getKeyStrategyName(null));
        assertEquals("robin", config.getKeyStrategyName("REGION"));
    }

    @Test
    public void testDogpilePrevention() {

        Properties p = new Properties();
        p.put("hibernate.memcached.dogpilePrevention", "true");
        p.put("hibernate.memcached.REGION.dogpilePrevention", "false");

        Config config = newConfig(p);
        assertTrue(config.isDogpilePreventionEnabled(null));
        assertFalse(config.isDogpilePreventionEnabled("REGION"));
    }

    @Test
    public void testDogpilePreventionExpirationFactor() {
        Properties p = new Properties();
        p.put("hibernate.memcached.dogpilePrevention.expirationFactor", "10");
        p.put("hibernate.memcached.REGION.dogpilePrevention.expirationFactor", "20");

        Config config = newConfig(p);
        assertEquals(10, config.getDogpilePreventionExpirationFactor(null), 0.0);
        assertEquals(20, config.getDogpilePreventionExpirationFactor("REGION"), 0.0);
    }


    @Test
    public void testMemcacheClientFactoryName() {

        Properties p = new Properties();
        Config config = newConfig(p);

        //test default
        assertEquals("com.mc.hibernate.memcached.spymemcached.SpyMemcacheClientFactory",
                config.getMemcachedClientFactoryName());

        p.put("hibernate.memcached.memcacheClientFactory", "blah");
        assertEquals("blah", config.getMemcachedClientFactoryName());


    }
}
