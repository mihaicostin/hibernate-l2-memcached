package com.mc.hibernate.memcached.cache;

public interface ConfigSettings {
    String PROP_PREFIX = "hibernate.cache.memcached.";
    String CACHE_MANAGER = PROP_PREFIX + "cache_manager";
    String MISSING_CACHE_STRATEGY = PROP_PREFIX + "missing_cache_strategy";
}
