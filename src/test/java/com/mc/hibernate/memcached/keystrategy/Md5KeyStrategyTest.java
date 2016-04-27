package com.mc.hibernate.memcached.keystrategy;

import org.junit.Test;

public class Md5KeyStrategyTest extends AbstractKeyStrategyTest {

    public KeyStrategy getKeyStrategy() {
        return new Md5KeyStrategy();
    }

    @Test
    public void test() {
        assertCacheKeyEquals("a088ce3b48a12c8a8f26058240f4518d", "test", 0, "boing");
    }

    @Test
    public void testNullRegion() {
        assertCacheKeyEquals("cf23c7bb0c99979d4be1129adc959e6f", null, 0, "boing");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_null_key_does_not_validate() {
        strategy.toKey(null, 0, null);
    }

    @Test
    public void testSpaces() {
        assertCacheKeyEquals("0564810c2fd4e86dc6f355ad99e7d01b", "I have spaces", 0, "so do I");
    }

    @Test
    public void testReallyLongKeysGetTruncated() {
        String regionName = "";
        for (int i = 0; i < 250; i++) {
            regionName += "x";
        }
        assertCacheKeyEquals("16df3d87c2f8bde43fcdbb545be10626", regionName, 0, "blah blah blah");
    }

}
