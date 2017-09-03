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

import com.mc.hibernate.memcached.Config;
import com.mc.hibernate.memcached.MemcachedCache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.engine.spi.SessionImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemcachedQueryResultsRegion extends AbstractMemcachedRegion implements QueryResultsRegion {

    private final Logger log = LoggerFactory.getLogger(MemcachedQueryResultsRegion.class);

    public MemcachedQueryResultsRegion(MemcachedCache cache, Config config) {
        super(cache, config);
    }

    @Override
    public Object get(SessionImplementor session, Object key) throws CacheException {
        return cache.get(key);
    }

    @Override
    public void put(SessionImplementor session, Object key, Object value) throws CacheException {
        cache.put(key, value);
    }

    public void evict(Object key) throws CacheException {
        cache.remove(key);
    }

    public void evictAll() throws CacheException {
        cache.clear();
    }

}
