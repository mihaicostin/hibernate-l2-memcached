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
import com.mc.hibernate.memcached.region.AbstractMemcachedRegion;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public abstract class AbstractMemcachedAccessStrategy<T extends AbstractMemcachedRegion> {

    protected final T region;

    private final SessionFactoryOptions settings;

    public AbstractMemcachedAccessStrategy(T region, SessionFactoryOptions settings) {
        this.region = region;
        this.settings = settings;
    }

    protected MemcachedCache region() {
        return region.getCache();
    }

    /**
     * The settings for this persistence unit.
     */
    protected SessionFactoryOptions settings() {
        return settings;
    }

    /**
     * This method is a placeholder for method signatures supplied by interfaces pulled in further down the class
     * hierarchy.
     */
    public final boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, long txTimestamp, Object version) throws CacheException {
        return putFromLoad(session, key, value, txTimestamp, version, settings.isMinimalPutsEnabled());
    }

    /**
     * This method is a placeholder for method signatures supplied by interfaces pulled in further down the class
     * hierarchy.
     */
    public abstract boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
            throws CacheException;

    /**
     * Region locks are not supported.
     */
    @SuppressWarnings("UnusedDeclaration")
    public SoftLock lockRegion() {
        return null;
    }

    /**
     * Region locks are not supported - perform a cache clear as a precaution.
     *
     * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#unlockRegion(org.hibernate.cache.spi.access.SoftLock)
     * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#unlockRegion(org.hibernate.cache.spi.access.SoftLock)
     */
    @SuppressWarnings("UnusedDeclaration")
    public void unlockRegion(SoftLock lock) throws CacheException {
        region.getCache().clear();
    }

    /**
     * A no-op since this is an asynchronous cache access strategy.
     */
    public void remove(SharedSessionContractImplementor session, Object key) throws CacheException {
    }

    /**
     * Called to evict data from the entire region
     */
    @SuppressWarnings("UnusedDeclaration")
    public final void removeAll() throws CacheException {
        region().clear();
    }

    /**
     * Remove the given mapping without regard to transactional safety
     */
    @SuppressWarnings("UnusedDeclaration")
    public final void evict(Object key) throws CacheException {
        region().remove(key);
    }

    /**
     * Remove all mappings without regard to transactional safety
     */
    @SuppressWarnings("UnusedDeclaration")
    public final void evictAll() throws CacheException {
        region().clear();
    }

    public T getRegion() {
        return region;
    }
}
