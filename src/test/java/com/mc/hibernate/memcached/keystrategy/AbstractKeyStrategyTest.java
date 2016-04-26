package com.mc.hibernate.memcached.keystrategy;

import com.mc.hibernate.memcached.BaseTest;
import org.junit.Assert;
import org.junit.Before;

abstract class AbstractKeyStrategyTest extends BaseTest {

    KeyStrategy strategy;

    @Before
    public void setUp() {
        strategy = getKeyStrategy();
    }

    void assertCacheKeyEquals(String expected, String namespace, int clearIndex, String keyObject) {
        String key = strategy.toKey(namespace, clearIndex, keyObject);
        Assert.assertEquals(expected, key);
    }

    abstract KeyStrategy getKeyStrategy();
}
