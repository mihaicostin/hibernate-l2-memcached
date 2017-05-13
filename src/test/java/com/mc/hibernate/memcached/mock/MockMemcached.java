package com.mc.hibernate.memcached.mock;

import com.mc.hibernate.memcached.Memcache;

import java.util.HashMap;
import java.util.Map;

public class MockMemcached implements Memcache {

    private Map<String, Object> cache = new HashMap<>();

    public Object get(String key) {
        return cache.get(key);
    }

    public void set(String key, int cacheTimeSeconds, Object o) {
        cache.put(key, o);
    }

    public void delete(String key) {
        cache.remove(key);
    }

    public void incr(String key, int factor, int startingValue) {
        String counter = (String) cache.get(key);
        if (counter != null) {
            cache.put(key, Long.parseLong(counter) + 1);
        } else {
            cache.put(key, String.valueOf(startingValue));
        }
    }

    public void shutdown() {

    }

    public Map<String, Object> getMulti(String... keys) {
        Map<String, Object> result = new HashMap<>();
        for (String key : keys) {
            result.put(key, cache.get(key));
        }
        return result;
    }

}
