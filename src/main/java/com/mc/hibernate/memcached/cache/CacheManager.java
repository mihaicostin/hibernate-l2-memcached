package com.mc.hibernate.memcached.cache;

import com.mc.hibernate.memcached.*;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    private final Memcache client;
    private Map<String, MemcachedCache> caches = new HashMap<>();
    private Map properties;

    public CacheManager(SessionFactoryOptions settings, Map properties) {
        this.properties = properties;
        try {
            client = getMemcachedClientFactory(wrapInConfig(properties)).createMemcacheClient();
        } catch (Exception e) {
            throw new CacheException("Unable to initialize MemcachedClient", e);
        }
    }

    private Config wrapInConfig(Map properties) {
        return new Config(new PropertiesHelper(properties));
    }

    private MemcacheClientFactory getMemcachedClientFactory(Config config) {
        String factoryClassName = config.getMemcachedClientFactoryName();

        Constructor<?> constructor;
        try {
            constructor = Class.forName(factoryClassName).getConstructor(PropertiesHelper.class);
        } catch (ClassNotFoundException e) {
            throw new CacheException("Unable to find factory class [" + factoryClassName + "]", e);
        } catch (NoSuchMethodException e) {
            throw new CacheException("Unable to find PropertiesHelper constructor for factory class [" + factoryClassName + "]", e);
        }

        MemcacheClientFactory clientFactory;
        try {
            clientFactory = (MemcacheClientFactory) constructor.newInstance(config.getPropertiesHelper());
        } catch (Exception e) {
            throw new CacheException("Unable to instantiate factory class [" + factoryClassName + "]", e);
        }

        return clientFactory;
    }

    public MemcachedCache getCache(String regionName) {
        return caches.get(regionName);
    }

    public void releaseFromUse() {
        caches.clear();
    }

    public void addCache(String regionName) {
        MemcachedCache cache = new MemcachedCache(regionName, client, wrapInConfig(properties));
        caches.put(regionName, cache);
    }
}
