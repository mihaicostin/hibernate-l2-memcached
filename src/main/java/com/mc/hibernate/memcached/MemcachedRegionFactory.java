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

import com.mc.hibernate.memcached.cache.CacheManager;
import com.mc.hibernate.memcached.cache.ConfigSettings;
import com.mc.hibernate.memcached.cache.MissingCacheStrategy;
import com.mc.hibernate.memcached.cache.StorageAccessImpl;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.SecondLevelCacheLogger;
import org.hibernate.cache.spi.support.*;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class MemcachedRegionFactory extends RegionFactoryTemplate {

    private static final Logger log = LoggerFactory.getLogger(MemcachedRegionFactory.class);

    private final CacheKeysFactory cacheKeysFactory;

    private volatile CacheManager cacheManager;
    private volatile MissingCacheStrategy missingCacheStrategy;

    public MemcachedRegionFactory() {
        this(DefaultCacheKeysFactory.INSTANCE);
    }

    public MemcachedRegionFactory(CacheKeysFactory cacheKeysFactory) {
        this.cacheKeysFactory = cacheKeysFactory;
    }

    @Override
    protected CacheKeysFactory getImplicitCacheKeysFactory() {
        return cacheKeysFactory;
    }

    @Override
    public DomainDataRegion buildDomainDataRegion(DomainDataRegionConfig regionConfig, DomainDataRegionBuildingContext buildingContext) {
        return new DomainDataRegionImpl(regionConfig, this, createDomainDataStorageAccess(regionConfig, buildingContext), cacheKeysFactory, buildingContext);
    }

    @Override
    protected DomainDataStorageAccess createDomainDataStorageAccess(DomainDataRegionConfig regionConfig, DomainDataRegionBuildingContext buildingContext) {
        return new StorageAccessImpl(getOrCreateCache(regionConfig.getRegionName(), buildingContext.getSessionFactory()));
    }

    @Override
    protected StorageAccess createQueryResultsRegionStorageAccess(String regionName, SessionFactoryImplementor sessionFactory) {
        String defaultedRegionName = defaultRegionName(regionName, sessionFactory, DEFAULT_QUERY_RESULTS_REGION_UNQUALIFIED_NAME, LEGACY_QUERY_RESULTS_REGION_UNQUALIFIED_NAMES);
        return new StorageAccessImpl(getOrCreateCache(defaultedRegionName, sessionFactory));
    }

    @Override
    protected StorageAccess createTimestampsRegionStorageAccess(String regionName, SessionFactoryImplementor sessionFactory) {
        String defaultedRegionName = defaultRegionName(regionName, sessionFactory, DEFAULT_UPDATE_TIMESTAMPS_REGION_UNQUALIFIED_NAME, LEGACY_UPDATE_TIMESTAMPS_REGION_UNQUALIFIED_NAMES);
        return new StorageAccessImpl(getOrCreateCache(defaultedRegionName, sessionFactory));
    }

    private String defaultRegionName(String regionName, SessionFactoryImplementor sessionFactory,
                                     String defaultRegionName, List<String> legacyDefaultRegionNames) {
        if (defaultRegionName.equals(regionName) && !cacheExists(regionName, sessionFactory)) {
            // Maybe the user configured caches explicitly with legacy names; try them and use the first that exists
            for (String legacyDefaultRegionName : legacyDefaultRegionNames) {
                if (cacheExists(legacyDefaultRegionName, sessionFactory)) {
                    SecondLevelCacheLogger.L2CACHE_LOGGER.usingLegacyCacheName(defaultRegionName, legacyDefaultRegionName);
                    return legacyDefaultRegionName;
                }
            }
        }

        return regionName;
    }

    private MemcachedCache getOrCreateCache(String unqualifiedRegionName, SessionFactoryImplementor sessionFactory) {

        verifyStarted();
        assert !RegionNameQualifier.INSTANCE.isQualified(unqualifiedRegionName, sessionFactory.getSessionFactoryOptions());

        final String qualifiedRegionName = RegionNameQualifier.INSTANCE.qualify(
                unqualifiedRegionName,
                sessionFactory.getSessionFactoryOptions()
        );

        final MemcachedCache cache = cacheManager.getCache(qualifiedRegionName);
        if (cache == null) {
            return createCache(qualifiedRegionName);
        }
        return cache;
    }

    private MemcachedCache createCache(String regionName) {
        switch (missingCacheStrategy) {
            case CREATE_WARN:
                log.warn("Creating new cache region " + regionName);
                cacheManager.addCache(regionName);
                return cacheManager.getCache(regionName);
            case CREATE:
                cacheManager.addCache(regionName);
                return cacheManager.getCache(regionName);
            case FAIL:
                throw new CacheException("On-the-fly creation of Cache objects is not supported [" + regionName + "]");
            default:
                throw new IllegalStateException("Unsupported missing cache strategy: " + missingCacheStrategy);
        }
    }

    private boolean cacheExists(String unqualifiedRegionName, SessionFactoryImplementor sessionFactory) {
        final String qualifiedRegionName = RegionNameQualifier.INSTANCE.qualify(
                unqualifiedRegionName,
                sessionFactory.getSessionFactoryOptions()
        );
        return cacheManager.getCache(qualifiedRegionName) != null;
    }


    protected boolean isStarted() {
        return super.isStarted() && cacheManager != null;
    }

    @Override
    protected void prepareForUse(SessionFactoryOptions settings, Map configValues) {
        synchronized (this) {
            this.cacheManager = resolveCacheManager(settings, configValues);
            if (this.cacheManager == null) {
                throw new CacheException("Could not start CacheManager");
            }
            this.missingCacheStrategy = MissingCacheStrategy.interpretSetting(configValues.get(ConfigSettings.MISSING_CACHE_STRATEGY));
        }
    }

    private CacheManager resolveCacheManager(SessionFactoryOptions settings, Map properties) {
        final Object explicitCacheManager = properties.get(ConfigSettings.CACHE_MANAGER);
        if (explicitCacheManager != null) {
            return useExplicitCacheManager(settings, explicitCacheManager);
        }

        return useNormalCacheManager(settings, properties);
    }

    /**
     * Locate the CacheManager during start-up.  protected to allow for subclassing
     */
    private static CacheManager useNormalCacheManager(SessionFactoryOptions settings, Map properties) {
        return new CacheManager(settings, properties);
    }

    private CacheManager useExplicitCacheManager(SessionFactoryOptions settings, Object setting) {
        if (setting instanceof CacheManager) {
            return (CacheManager) setting;
        }

        final Class<? extends CacheManager> cacheManagerClass;
        if (setting instanceof Class) {
            cacheManagerClass = (Class<? extends CacheManager>) setting;
        } else {
            cacheManagerClass = settings.getServiceRegistry().getService(ClassLoaderService.class).classForName(setting.toString());
        }

        try {
            return cacheManagerClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CacheException("Could not use explicit CacheManager : " + setting);
        }
    }

    @Override
    protected void releaseFromUse() {
        try {
            cacheManager.releaseFromUse();
        } finally {
            cacheManager = null;
        }
    }
}
