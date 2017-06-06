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
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.mc.hibernate.memcached.region.MemcachedNaturalIdRegion;

public class ReadWriteMemcachedNaturalIdRegionAccessStrategy
        extends AbstractReadWriteMemcachedAccessStrategy<MemcachedNaturalIdRegion>
        implements NaturalIdRegionAccessStrategy {

    public ReadWriteMemcachedNaturalIdRegionAccessStrategy(MemcachedNaturalIdRegion region, Settings settings, CacheDataDescription metadata) {
        super(region, settings, metadata);
    }

    public boolean insert(Object key, Object value) throws CacheException {
        return false;
    }

    public boolean afterInsert(Object key, Object value) throws CacheException {
        region.getCache().lock(key);
        try {
            Lockable item = (Lockable) region.getCache().get(key);

            if (item == null) {
                region.getCache().put(key, new Item(value, null, region.nextTimestamp()));
                return true;
            } else {
                return false;
            }
        } finally {
            region.getCache().unlock(key);
        }
    }

    public boolean update(Object key, Object value) throws CacheException {
        return false;
    }

    public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
        region.getCache().lock(key);
        try {
            Lockable item = (Lockable) region.getCache().get(key);

            if (item != null && item.isUnlockable(lock)) {
                final Lock lockItem = (Lock) item;
                if (lockItem.wasLockedConcurrently()) {
                    decrementLock(key, lockItem);
                    return false;
                } else {
                    region.getCache().put(key, new Item(value, null, region.nextTimestamp()));
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
}
