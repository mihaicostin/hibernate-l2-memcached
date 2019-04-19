package com.mc.hibernate.memcached.cache;

import org.hibernate.internal.util.StringHelper;

public enum MissingCacheStrategy {

    FAIL("fail"),
    CREATE_WARN("create-warn"),
    CREATE("create");

    private final String externalRepresentation;

    MissingCacheStrategy(String externalRepresentation) {
        this.externalRepresentation = externalRepresentation;
    }

    public static MissingCacheStrategy interpretSetting(Object value) {
        if (value instanceof MissingCacheStrategy) {
            return (MissingCacheStrategy) value;
        }

        final String externalRepresentation = value == null ? null : value.toString();

        if (StringHelper.isEmpty(externalRepresentation)) {
            return MissingCacheStrategy.CREATE_WARN;
        }

        for (MissingCacheStrategy strategy : values()) {
            if (strategy.externalRepresentation.equals(externalRepresentation)) {
                return strategy;
            }
        }

        throw new IllegalArgumentException("Unrecognized missing cache strategy value : " + value);
    }
}
