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

package com.mc.hibernate.memcached.region;

import com.mc.hibernate.memcached.MemcachedCache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.Region;

import java.util.Map;

public abstract class AbstractMemcachedRegion implements Region {

    protected MemcachedCache cache;

    AbstractMemcachedRegion(MemcachedCache cache) {
        this.cache = cache;
    }

    public String getName() {
        return cache.getRegionName();
    }

    public void destroy() throws CacheException {
        cache.destroy();
    }

    public boolean contains(Object key) {
        return cache.get(key) != null;
    }

    public long getSizeInMemory() {
        return cache.getSizeInMemory();
    }

    public long getElementCountInMemory() {
        return cache.getElementCountInMemory();
    }

    public long getElementCountOnDisk() {
        return cache.getElementCountOnDisk();
    }

    @Override
    public Map toMap() {
        return cache.toMap();
    }

    public long nextTimestamp() {
        return System.currentTimeMillis() / 100;
    }

    public int getTimeout() {
        return cache.getTimeout();
    }

    public MemcachedCache getCache() {
        return cache;
    }
}
