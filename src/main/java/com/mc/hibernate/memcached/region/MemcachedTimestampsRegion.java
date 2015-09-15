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

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.TimestampsRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mc.hibernate.memcached.MemcachedCache;

public class MemcachedTimestampsRegion extends AbstractMemcachedRegion implements TimestampsRegion {

    private final Logger log = LoggerFactory.getLogger(MemcachedTimestampsRegion.class);

    public MemcachedTimestampsRegion(MemcachedCache cache) {
        super(cache);
    }

    public Object get(Object key) throws CacheException {
        return cache.get(key);
    }

    public void put(Object key, Object value) throws CacheException {
        cache.put(key, value);
    }

    public void evict(Object key) throws CacheException {
        cache.remove(key);
    }

    public void evictAll() throws CacheException {
        cache.clear();
    }


}
