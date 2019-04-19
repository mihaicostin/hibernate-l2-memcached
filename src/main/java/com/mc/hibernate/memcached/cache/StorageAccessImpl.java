package com.mc.hibernate.memcached.cache;

import com.mc.hibernate.memcached.MemcachedCache;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class StorageAccessImpl implements DomainDataStorageAccess {

    private final MemcachedCache cache;

    public StorageAccessImpl(MemcachedCache cache) {
        this.cache = cache;
    }

    public MemcachedCache getCache() {
        return cache;
    }

    @Override
    public boolean contains(Object key) {
        return getCache().get(key) != null;
    }

    @Override
    public Object getFromCache(Object key, SharedSessionContractImplementor session) {
        return getCache().get(key);
    }

    @Override
    public void putIntoCache(Object key, Object value, SharedSessionContractImplementor session) {
        getCache().put(key, value);
    }

    @Override
    public void evictData(Object key) {
        getCache().remove(key);
    }

    @Override
    public void evictData() {
        getCache().clear();
    }

    @Override
    public void release() {

    }
}
