package com.mc.hibernate.memcached.keystrategy;

import org.junit.Test;

public class Sha1KeyStrategyTest extends AbstractKeyStrategyTest {

    public KeyStrategy getKeyStrategy() {
        return new Sha1KeyStrategy();
    }

    @Test
    public void test() {
        assertCacheKeyEquals("cd23e26dd7ab1d052e1c0a04daa27a03f6cd5d1c", "test", 0, "boing");
    }

    @Test
    public void testNullRegion() {
        assertCacheKeyEquals("6afcec5614479d46a1ec6d73dabbc2cea154da3c", null, 0, "boing");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullKeyDoesNotValidate() {
        strategy.toKey(null, 0, null);
    }

    @Test
    public void testSpaces() {
        assertCacheKeyEquals("949b2a6fce917d85bd56e6197c93b3affa694e50", "I have spaces", 0, "so do I");
    }

    @Test
    public void testReallyLongKeysGetTruncated() {
        String regionName = "";
        for (int i = 0; i < 250; i++) {
            regionName += "x";
        }
        assertCacheKeyEquals("7f00c6faf1fefaf62cabb512285cc60ce641d5c8", regionName, 0, "blah blah blah");
    }

}
