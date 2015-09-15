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
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.mc.hibernate.memcached.region.MemcachedCollectionRegion;

public class ReadOnlyMemcachedCollectionRegionAccessStrategy
        extends AbstractMemcachedAccessStrategy<MemcachedCollectionRegion>
        implements CollectionRegionAccessStrategy {

    public ReadOnlyMemcachedCollectionRegionAccessStrategy(MemcachedCollectionRegion aThis, Settings settings) {
        super(aThis, settings);
    }

    @Override
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
        if (minimalPutOverride && region.getCache().get(key) != null) {
            return false;
        } else {
            region.getCache().put(key, value);
            return true;
        }
    }

    public Object get(Object key, long txTimestamp) throws CacheException {
        return region.getCache().get(key);
    }

    public SoftLock lockItem(Object key, Object version) throws CacheException {
        return null;
    }

    /**
     * A no-op since this cache is read-only
     */
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
    }

}
