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
import com.mc.hibernate.memcached.strategy.NonStrictReadWriteMemcachedNaturalIdRegionAccessStrategy;
import com.mc.hibernate.memcached.strategy.ReadOnlyMemcachedNaturalIdRegionAccessStrategy;
import com.mc.hibernate.memcached.strategy.ReadWriteMemcachedNaturalIdRegionAccessStrategy;
import com.mc.hibernate.memcached.strategy.TransactionalMemcachedNaturalIdRegionAccessStrategy;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemcachedNaturalIdRegion extends AbstractMemcachedRegion implements NaturalIdRegion {

    private final Logger log = LoggerFactory.getLogger(MemcachedNaturalIdRegion.class);

    private final CacheDataDescription metadata;
    private final SessionFactoryOptions settings;

    public MemcachedNaturalIdRegion(MemcachedCache cache, SessionFactoryOptions settings, CacheDataDescription metadata, Config config) {
        super(cache, config);
        this.metadata = metadata;
        this.settings = settings;
    }

    public NaturalIdRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {

        switch (accessType) {

            case READ_ONLY:
                if (metadata.isMutable()) {
                    log.warn("read-only cache configured for mutable entity ["
                            + getName() + "]");
                }
                return new ReadOnlyMemcachedNaturalIdRegionAccessStrategy(this, settings);
            case READ_WRITE:
                return new ReadWriteMemcachedNaturalIdRegionAccessStrategy(this, settings, metadata);
            case NONSTRICT_READ_WRITE:
                return new NonStrictReadWriteMemcachedNaturalIdRegionAccessStrategy(this, settings);
            case TRANSACTIONAL:
                return new TransactionalMemcachedNaturalIdRegionAccessStrategy(this, settings);
            default:
                throw new IllegalArgumentException("unrecognized access strategy type [" + accessType + "]");
        }
    }

    public boolean isTransactionAware() {
        return false;
    }

    public CacheDataDescription getCacheDataDescription() {
        return metadata;
    }
}
