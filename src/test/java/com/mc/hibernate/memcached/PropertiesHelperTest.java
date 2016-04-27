package com.mc.hibernate.memcached;

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class PropertiesHelperTest extends BaseTest {

    private PropertiesHelper helper;

    @Before
    public void setUp() {
        helper = newHelper();
    }

    @Test
    public void testStrings() {
        assertEquals("world", helper.get("hello"));
        assertEquals("world", helper.get("hello", "blah"));
        assertEquals("default", helper.get("nothing", "default"));
    }

    @Test
    public void testBoolean() {
        assertFalse(helper.getBoolean("blah", false));
        assertTrue(helper.getBoolean("blah", true));
        assertTrue(helper.getBoolean("thisIsTrue", false));
        assertFalse(helper.getBoolean("thisIsFalse", true));
        assertFalse(helper.getBoolean("hello", true));
    }

    @Test(expected = NumberFormatException.class)
    public void testLong() {
        assertEquals(1L, helper.getLong("one", 10));
        assertEquals(10L, helper.getLong("nothing", 10));
        helper.getLong("hello", 10);
    }

    @Test(expected = NumberFormatException.class)
    public void testInt() {
        assertEquals(1, helper.getInt("one", 10));
        assertEquals(10, helper.getInt("nothing", 10));
        helper.getInt("hello", 10);
    }

    @Test
    public void testEnum() {
        TimeUnit seconds = helper.getEnum("seconds", TimeUnit.class, TimeUnit.NANOSECONDS);
        assertEquals(TimeUnit.SECONDS, seconds);

        TimeUnit timeUnit = helper.getEnum("nothing", java.util.concurrent.TimeUnit.class, TimeUnit.NANOSECONDS);
        assertEquals(TimeUnit.NANOSECONDS, timeUnit);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIvalidEnum() {
        helper.getEnum("hello", java.util.concurrent.TimeUnit.class, java.util.concurrent.TimeUnit.NANOSECONDS);
    }

    @Test
    public void testFindValues() {
        assertNull(helper.findValue("this", "does", "not", "exist"));
        assertEquals("world", helper.findValue("this", "does", "not", "exist", "hello"));
    }

    private PropertiesHelper newHelper() {
        Properties props = new Properties();
        props.put("hello", "world");
        props.put("one", "1");
        props.put("thisIsTrue", "true");
        props.put("thisIsFalse", "false");
        props.put("seconds", "SECONDS");
        return new PropertiesHelper(props);
    }

}
