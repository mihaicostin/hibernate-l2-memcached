package com.mc.hibernate.memcached.cache;

import com.mc.hibernate.memcached.MemcachedRegionFactory;
import org.hibernate.boot.registry.selector.SimpleStrategyRegistrationImpl;
import org.hibernate.boot.registry.selector.StrategyRegistration;
import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
import org.hibernate.cache.spi.RegionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * used by {@link org.hibernate.boot.registry.selector.spi.StrategySelector} service.
 */
public class StrategyRegistrationProviderImpl implements StrategyRegistrationProvider {
    @Override
    @SuppressWarnings("unchecked")
    public Iterable<StrategyRegistration> getStrategyRegistrations() {
        final List<StrategyRegistration> strategyRegistrations = new ArrayList<StrategyRegistration>();

        strategyRegistrations.add(
                new SimpleStrategyRegistrationImpl(
                        RegionFactory.class,
                        MemcachedRegionFactory.class,
                        "memcached",
                        MemcachedRegionFactory.class.getName(),
                        MemcachedRegionFactory.class.getSimpleName(),
                        // legacy impl class name
                        "com.mc.hibernate.memcached.MemcachedRegionFactory"
                )
        );

        return strategyRegistrations;
    }
}
