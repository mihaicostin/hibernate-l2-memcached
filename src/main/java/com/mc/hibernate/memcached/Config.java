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

package com.mc.hibernate.memcached;

import com.mc.hibernate.memcached.keystrategy.KeyStrategy;
import com.mc.hibernate.memcached.keystrategy.Sha1KeyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {

    private final Logger log = LoggerFactory.getLogger(Config.class);

    public static final String PROP_PREFIX = "hibernate.memcached.";

    private static final String CACHE_TIME_SECONDS = "cacheTimeSeconds";
    private static final String CLEAR_SUPPORTED = "clearSupported";
    private static final String MEMCACHE_CLIENT_FACTORY = "memcacheClientFactory";
    private static final String DOGPILE_PREVENTION = "dogpilePrevention";
    private static final String DOGPILE_PREVENTION_EXPIRATION_FACTOR = "dogpilePrevention.expirationFactor";
    private static final String KEY_STRATEGY = "keyStrategy";

    private static final int DEFAULT_CACHE_TIME_SECONDS = 300;
    private static final boolean DEFAULT_CLEAR_SUPPORTED = false;
    private static final boolean DEFAULT_DOGPILE_PREVENTION = false;
    private static final int DEFAULT_DOGPILE_EXPIRATION_FACTOR = 2;
    private static final String DEFAULT_MEMCACHE_CLIENT_FACTORY = "com.mc.hibernate.memcached.spymemcached.SpyMemcacheClientFactory";

    private PropertiesHelper props;

    public Config(PropertiesHelper props) {
        this.props = props;
    }

    public int getCacheTimeSeconds(String cacheRegion) {
        int globalCacheTimeSeconds = props.getInt(PROP_PREFIX + CACHE_TIME_SECONDS, DEFAULT_CACHE_TIME_SECONDS);
        return props.getInt(cacheRegionPrefix(cacheRegion) + CACHE_TIME_SECONDS, globalCacheTimeSeconds);
    }

    public String getKeyStrategyName(String cacheRegion) {
        String globalKeyStrategy = props.get(PROP_PREFIX + KEY_STRATEGY, Sha1KeyStrategy.class.getName());
        return props.get(cacheRegionPrefix(cacheRegion) + KEY_STRATEGY, globalKeyStrategy);
    }

    private KeyStrategy instantiateKeyStrategy(String cls) {
        try {
            return (KeyStrategy) Class.forName(cls).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            log.warn("Could not instantiate keyStrategy class " + cls + ". Will use default: Sha1KeyStrategy", e);
        }
        return new Sha1KeyStrategy();
    }

    public KeyStrategy getKeyStrategy(String cacheRegion) {
        String strategyClassName = getKeyStrategyName(cacheRegion);
        return instantiateKeyStrategy(strategyClassName);
    }

    public boolean isClearSupported(String cacheRegion) {
        boolean globalClearSupported = props.getBoolean(PROP_PREFIX + CLEAR_SUPPORTED, DEFAULT_CLEAR_SUPPORTED);
        return props.getBoolean(cacheRegionPrefix(cacheRegion) + CLEAR_SUPPORTED, globalClearSupported);
    }

    public boolean isDogpilePreventionEnabled(String cacheRegion) {
        boolean globalDogpilePrevention = props.getBoolean(PROP_PREFIX + DOGPILE_PREVENTION, DEFAULT_DOGPILE_PREVENTION);
        return props.getBoolean(cacheRegionPrefix(cacheRegion) + DOGPILE_PREVENTION, globalDogpilePrevention);
    }

    public double getDogpilePreventionExpirationFactor(String cacheRegion) {
        double globalFactor = props.getDouble(PROP_PREFIX + DOGPILE_PREVENTION_EXPIRATION_FACTOR, DEFAULT_DOGPILE_EXPIRATION_FACTOR);
        return props.getDouble(cacheRegionPrefix(cacheRegion) + DOGPILE_PREVENTION_EXPIRATION_FACTOR, globalFactor);
    }

    public String getMemcachedClientFactoryName() {
        return props.get(PROP_PREFIX + MEMCACHE_CLIENT_FACTORY, DEFAULT_MEMCACHE_CLIENT_FACTORY);
    }

    private String cacheRegionPrefix(String cacheRegion) {
        return PROP_PREFIX + cacheRegion + ".";
    }

    public PropertiesHelper getPropertiesHelper() {
        return props;
    }
}
