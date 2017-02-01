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

import com.mc.hibernate.memcached.region.MemcachedNaturalIdRegion;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

public class TransactionalMemcachedNaturalIdRegionAccessStrategy
        extends AbstractMemcachedAccessStrategy<MemcachedNaturalIdRegion>
        implements NaturalIdRegionAccessStrategy {

    public TransactionalMemcachedNaturalIdRegionAccessStrategy(MemcachedNaturalIdRegion region,
                                                               SessionFactoryOptions settings) {
        super(region, settings);
    }

    @Override
    public Object get(SharedSessionContractImplementor session, Object key, long txTimestamp) throws CacheException {
        return region.getCache().get(key);
    }

    @Override
    public boolean insert(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
        region.getCache().put(key, value);
        return true;
    }

    @Override
    public boolean putFromLoad(
            SharedSessionContractImplementor session, Object key,
            Object value,
            long txTimestamp,
            Object version,
            boolean minimalPutOverride) throws CacheException {

        if (minimalPutOverride && region.getCache().get(key) != null) {
            return false;
        } else {
            region.getCache().put(key, value);
            return true;
        }
    }


    @Override
    public void remove(SharedSessionContractImplementor session, Object key) throws CacheException {
        region.getCache().remove(key);
    }

    @Override
    public boolean update(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
        region.getCache().put(key, value);
        return true;
    }


    @Override
    public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value) {
        return false;
    }

    @Override
    public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, SoftLock lock) {
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
    public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SharedSessionContractImplementor session) {
        return DefaultCacheKeysFactory.INSTANCE.createNaturalIdKey(naturalIdValues, persister, session);
    }

    @Override
    public Object[] getNaturalIdValues(Object cacheKey) {
        return DefaultCacheKeysFactory.INSTANCE.getNaturalIdValues(cacheKey);
    }
}
