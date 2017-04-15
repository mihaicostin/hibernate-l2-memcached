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
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;


public class ReadOnlyMemcachedNaturalIdRegionAccessStrategy
        extends AbstractMemcachedAccessStrategy<MemcachedNaturalIdRegion>
        implements NaturalIdRegionAccessStrategy {

    public ReadOnlyMemcachedNaturalIdRegionAccessStrategy(MemcachedNaturalIdRegion region, SessionFactoryOptions settings) {
        super(region, settings);
    }

    @Override
    public Object get(SessionImplementor session, Object key, long txTimestamp) throws CacheException {
        return region().get(key);
    }

    @Override
    public boolean putFromLoad(SessionImplementor session, Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
            throws CacheException {
        if (minimalPutOverride && region.contains(key)) {
            return false;
        } else {
            region().put(key, value);
            return true;
        }
    }

    @Override
    public SoftLock lockItem(SessionImplementor session, Object key, Object version) throws UnsupportedOperationException {
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * A no-op since this cache is read-only
     */
    @Override
    public void unlockItem(SessionImplementor session, Object key, SoftLock lock) throws CacheException {
        region().remove(key);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This cache is asynchronous hence a no-op
     */
    @Override
    public boolean insert(SessionImplementor session, Object key, Object value) throws CacheException {
        return false;
    }

    @Override
    public boolean afterInsert(SessionImplementor session, Object key, Object value) throws CacheException {
        region().put(key, value);
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Throws UnsupportedOperationException since this cache is read-only
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean update(SessionImplementor session, Object key, Object value) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Can't write to a readonly object");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Throws UnsupportedOperationException since this cache is read-only
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public boolean afterUpdate(SessionImplementor session, Object key, Object value, SoftLock lock) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Can't write to a readonly object");
    }

    @Override
    public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
        return DefaultCacheKeysFactory.createNaturalIdKey(naturalIdValues, persister, session);
    }

    @Override
    public Object[] getNaturalIdValues(Object cacheKey) {
        return DefaultCacheKeysFactory.getNaturalIdValues(cacheKey);
    }
}
