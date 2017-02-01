/* Copyright 2015, the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mc.hibernate.memcached.strategy;

import com.mc.hibernate.memcached.MemcachedCache;
import com.mc.hibernate.memcached.region.MemcachedEntityRegion;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

public class TransactionalMemcachedEntityRegionAccessStrategy
        extends AbstractMemcachedAccessStrategy<MemcachedEntityRegion>
        implements EntityRegionAccessStrategy {

    private final MemcachedCache cache;

    public TransactionalMemcachedEntityRegionAccessStrategy(MemcachedEntityRegion aThis, MemcachedCache cache, SessionFactoryOptions settings) {
        super(aThis, settings);
        this.cache = cache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(SharedSessionContractImplementor session, Object key, long txTimestamp) throws CacheException {
        return cache.get(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean insert(SharedSessionContractImplementor session, Object key, Object value, Object version)
            throws CacheException {

        //OptimisticCache? versioning?
        cache.put(key, value);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public SoftLock lockItem(Object key, Object version) throws CacheException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean putFromLoad(
            SharedSessionContractImplementor session,
            Object key,
            Object value,
            long txTimestamp,
            Object version,
            boolean minimalPutOverride) throws CacheException {

        if (minimalPutOverride && cache.get(key) != null) {
            return false;
        }
        //OptimisticCache? versioning?
        cache.put(key, value);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(SharedSessionContractImplementor session, Object key) throws CacheException {
        cache.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(SharedSessionContractImplementor session,
                          Object key, Object value,
                          Object currentVersion, Object previousVersion) throws CacheException {
        cache.put(key, value);
        return true;
    }


    @Override
    public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value, Object version) {
        return false;
    }

    @Override
    public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) {
        return false;
    }

    @Override
    public SoftLock lockItem(SharedSessionContractImplementor session, Object key, Object version) throws CacheException {
        return null;
    }

    @Override
    public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) throws CacheException {
        // no-op
    }

    @Override
    public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
        return DefaultCacheKeysFactory.INSTANCE.createEntityKey(id, persister, factory, tenantIdentifier);
    }

    @Override
    public Object getCacheKeyId(Object cacheKey) {
        return DefaultCacheKeysFactory.INSTANCE.getEntityId(cacheKey);
    }
}
