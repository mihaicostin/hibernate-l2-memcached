package com.mc.hibernate.memcached.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StringUtilsTest {

    @Test
    public void test_simple_join() {
        assertEquals("1, 2, 3", StringUtils.join(new Object[]{1, 2, 3}, ", "));
    }

    @Test
    public void test_empty_join() {
        assertEquals("", StringUtils.join(new Object[]{}, ", "));
    }

    @Test
    public void test_null_join() {
        assertNull(StringUtils.join(null, ","));
    }

    @Test
    public void test_md5_hex() {
        assertEquals("eae4b23daa656ea031c2b90106304cf2", StringUtils.md5Hex("boosh! and/or kakow"));
    }

    @Test
    public void test_sha1_hex() {
        assertEquals("f18f2dcf68655fe9112ac57c62931cc490c3397c", StringUtils.sha1Hex("boosh! and/or kakow"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_null_md5_hex() {
        StringUtils.md5Hex(null);
    }


}
