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

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.mc.hibernate.memcached.MemcachedCache;
import com.mc.hibernate.memcached.region.MemcachedEntityRegion;

public class TransactionalMemcachedEntityRegionAccessStrategy
        extends AbstractMemcachedAccessStrategy<MemcachedEntityRegion>
        implements EntityRegionAccessStrategy {

    private final MemcachedCache cache;

    public TransactionalMemcachedEntityRegionAccessStrategy(MemcachedEntityRegion aThis, MemcachedCache cache, Settings settings) {
        super(aThis, settings);
        this.cache = cache;
    }

    /**
     * {@inheritDoc}
     */
    public boolean afterInsert(Object key, Object value, Object version) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Object get(Object key, long txTimestamp) throws CacheException {
        return cache.get(key);
    }

    /**
     * {@inheritDoc}
     */
    public boolean insert(Object key, Object value, Object version)
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
    public boolean putFromLoad(Object key, Object value, long txTimestamp,
                               Object version, boolean minimalPutOverride) throws CacheException {
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
    public void remove(Object key) throws CacheException {
        cache.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public boolean update(Object key, Object value, Object currentVersion,
                          Object previousVersion) throws CacheException {
        cache.put(key, value);
        return true;
    }
}
