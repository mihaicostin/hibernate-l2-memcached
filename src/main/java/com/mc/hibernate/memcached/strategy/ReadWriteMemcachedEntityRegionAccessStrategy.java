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

import com.mc.hibernate.memcached.region.MemcachedEntityRegion;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

public class ReadWriteMemcachedEntityRegionAccessStrategy
        extends AbstractReadWriteMemcachedAccessStrategy<MemcachedEntityRegion>
        implements EntityRegionAccessStrategy {

    public ReadWriteMemcachedEntityRegionAccessStrategy(MemcachedEntityRegion region, SessionFactoryOptions settings) {
        super(region, settings, region.getCacheDataDescription());
    }

    /**
     * {@inheritDoc}
     * <p>
     * A no-op since this is an asynchronous cache access strategy.
     */
    @Override
    public boolean insert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Inserts will only succeed if there is no existing value mapped to this key.
     */
    @Override
    public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
        region.getCache().lock(key);
        try {
            final Lockable item = (Lockable) region().get(key);
            if (item == null) {
                region().put(key, new Item(value, version, region().nextTimestamp()));
                return true;
            } else {
                return false;
            }
        } finally {
            region.getCache().unlock(key);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * A no-op since this is an asynchronous cache access strategy.
     */
    @Override
    public boolean update(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion)
            throws CacheException {
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Updates will only succeed if this entry was locked by this transaction and exclusively this transaction for the
     * duration of this transaction.  It is important to also note that updates will fail if the soft-lock expired during
     * the course of this transaction.
     */
    @Override
    public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
            throws CacheException {
        //what should we do with previousVersion here?
        region.getCache().lock(key);
        try {
            final Lockable item = (Lockable) region().get(key);

            if (item != null && item.isUnlockable(lock)) {
                final Lock lockItem = (Lock) item;
                if (lockItem.wasLockedConcurrently()) {
                    decrementLock(key, lockItem);
                    return false;
                } else {
                    region().put(key, new Item(value, currentVersion, region().nextTimestamp()));
                    return true;
                }
            } else {
                handleLockExpiry(key, item);
                return false;
            }
        } finally {
            region.getCache().unlock(key);
        }
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
