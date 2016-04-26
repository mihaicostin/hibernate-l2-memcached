package com.mc.hibernate.memcached;

import com.mc.hibernate.memcached.mock.MockAppender;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class LoggingMemcacheExceptionHandlerTest extends BaseTest {

    private LoggingMemcacheExceptionHandler handler = new LoggingMemcacheExceptionHandler();
    private Logger logger = Logger.getLogger(LoggingMemcacheExceptionHandler.class);

    @Before
    public void setUp() {
        logger.removeAllAppenders();
    }

    @Test
    public void testDelete() {
        Exception exception = new Exception("blah");
        MockAppender appender = new MockAppender("Cache 'delete' failed for key [blah]", exception);
        logger.addAppender(appender);
        handler.handleErrorOnDelete("blah", exception);
        assertTrue(appender.isAppenderCalled());
    }

    @Test
    public void testGet() {
        Exception exception = new Exception("blah");
        MockAppender appender = new MockAppender("Cache 'get' failed for key [blah]", exception);
        logger.addAppender(appender);
        handler.handleErrorOnGet("blah", exception);
        assertTrue(appender.isAppenderCalled());
    }

    @Test
    public void testIncr() {
        Exception exception = new Exception("blah");
        MockAppender appender = new MockAppender("Cache 'incr' failed for key [blah]", exception);
        logger.addAppender(appender);
        handler.handleErrorOnIncr("blah", 10, 20, exception);
        assertTrue(appender.isAppenderCalled());
    }

    @Test
    public void testSet() {
        Exception exception = new Exception("blah");
        MockAppender appender = new MockAppender("Cache 'set' failed for key [blah]", exception);
        logger.addAppender(appender);
        handler.handleErrorOnSet("blah", 300, new Object(), exception);
        assertTrue(appender.isAppenderCalled());
    }
}
