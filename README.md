# Hibernate - Memcached
A library for using Memcached as a second level distributed cache in Hibernate.

  * Hibernate 5.2.7.Final   : use version 5.2.7.0
  * Hibernate 5.2.1.Final   : use version 5.2.1.0
  * Hibernate 5.1.5.Final   : use version 5.1.5.0
  * Hibernate 5.0.1.Final   : use version 5.0.1.0
  * Hibernate 4.3.x         : use version 1.1.0
  * Uses spymemcached client
  * Supports entity and query caching.
  * Based on the work done on Hibernate-Memcached for hibernate 3.x
      * https://github.com/raykrueger/hibernate-memcached
      * https://github.com/kcarlson/hibernate-memcached

# Maven dependency

To install it, you just need to add the following Maven dependency (check Versions for the right version for you):
```xml
<dependency>
    <groupId>com.github.mihaicostin</groupId>
    <artifactId>hibernate-l2-memcached</artifactId>
    <version>5.2.7.0</version>
</dependency>
```

# Example config:

```xml
<property name="hibernate.cache.region.factory_class">com.mc.hibernate.memcached.MemcachedRegionFactory</property>
<property name="hibernate.memcached.operationTimeout">5000</property>
<property name="hibernate.memcached.connectionFactory">KetamaConnectionFactory</property>
<property name="hibernate.memcached.hashAlgorithm">HashAlgorithm.FNV1_64_HASH</property>
```

If memcached authentication is required you can specify username and password:

```xml
<property name="hibernate.memcached.username">memcached-username</property>
<property name="hibernate.memcached.password">memcached-password</property>
```

If memcached are running on a remote server, e.g. on AWS ElastiCache, you can specify the connection URL:

```xml
<property name="hibernate.memcached.servers">cache.c3wd5k.cfg.euw1.cache.amazonaws.com:11211</property>
```

# Versions

## 5.2.7.0
- Support Hibernate 5.2.7.Final ([PR](https://github.com/mihaicostin/hibernate-l2-memcached/pull/6) by @akhalikov )
- Update spymemcached to [2.12.2](https://github.com/couchbase/spymemcached/releases/tag/2.12.2)
- Tested with 5.2.7.Final - 5.2.10.Final

## 5.2.1.0

- Switched to a new versioning schema, that goes hand in had with [hibernate](http://hibernate.org/orm/).
    - Version 5.2.1.x is developed for (and compatible with) hibernate 5.2.1.Final
- Update [spymemcached](https://github.com/couchbase/spymemcached) to the latest version: 2.12.1

## 5.1.5.0
- Support for Hibernate 5.1.x (tested with 5.1.1.Final - 5.1.5.Final)

## 5.0.1.0
- Support for Hibernate 5.0.x

### Known issues
- SecondLevelCacheStatistics won't work with this adapter since it tries to get the cache content as a Map (and that's not easily done with memcached)

## 1.1.0

Memcached client now respects the region timeout property
 - [Issue #1](https://github.com/mihaicostin/hibernate-l2-memcached/issues/1)
```xml
<property name="hibernate.memcached.REGION.cacheTimeSeconds">10</property>
```

## 1.0.0
* Compatible with hibernate 4.3.x

