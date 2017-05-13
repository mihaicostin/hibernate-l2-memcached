# Hibernate - Memcached
A library for using Memcached as a second level distributed cache in Hibernate.
  * Uses spymemcached client
  * Supports entity and query caching.

# Maven dependency

To install it, you just need to add the following Maven dependency (check Versions for the right version for you):
```xml
<dependency>
    <groupId>com.github.mihaicostin</groupId>
    <artifactId>hibernate-l2-memcached</artifactId>
    <version>5.2.7.0</version>
</dependency>
```

https://mvnrepository.com/artifact/com.github.mihaicostin/hibernate-l2-memcached


# Example config:

```xml
<property name="hibernate.cache.region.factory_class">com.mc.hibernate.memcached.MemcachedRegionFactory</property>
<property name="hibernate.memcached.operationTimeout">5000</property>
<property name="hibernate.memcached.connectionFactory">KetamaConnectionFactory</property>
<property name="hibernate.memcached.hashAlgorithm">FNV1_64_HASH</property>
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

# Config options 

## Targeting the memcached client (net.spy.memcached configurations)

| Property                                  | Default Value |
|-------------------------------------------|---------------|
| hibernate.memcached.servers| localhost:11211|
| hibernate.memcached.operationQueueLength | (default provided by net.spy.memcached.DefaultConnectionFactory) |
| hibernate.memcached.readBufferSize | (default provided by net.spy.memcached.DefaultConnectionFactory) |
| hibernate.memcached.operationTimeout | (default provided by net.spy.memcached.DefaultConnectionFactory) |
| hibernate.memcached.hashAlgorithm | NATIVE_HASH |
| hibernate.memcached.connectionFactory | DefaultConnectionFactory |
| hibernate.memcached.daemonMode | false |
| hibernate.memcached.username | - |
| hibernate.memcached.password | - |

## Targeting the cache implementation (global or region specific)

| Property                                  | Default Value |
|-------------------------------------------|---------------|
| hibernate.memcached.cacheTimeSeconds      |300            |
| hibernate.memcached.clearSupported        |false          |
| hibernate.memcached.memcacheClientFactory | com.mc.hibernate.memcached.spymemcached.SpyMemcacheClientFactory |
| hibernate.memcached.dogpilePrevention     | false |
| hibernate.memcached.dogpilePrevention.expirationFactor| 2 |
| hibernate.memcached.keyStrategy | com.mc.hibernate.memcached.keystrategy.Sha1KeyStrategy |

In order to specify a property for a specific region add the region name right after `memcached`. ex: `hibernate.memcached.myregion.cacheTimeSeconds`

# Versions
Use the version compatible with your hibernate version (ex: Version 5.2.1.x is developed for and compatible with hibernate 5.2.1.Final)

## Hibernate 5.2.7.Final - 5.2.10.Final
*  v 5.2.7.1
    - Fix for [Issue #9](https://github.com/mihaicostin/hibernate-l2-memcached/issues/9)      
*  v 5.2.7.0
    - Support Hibernate 5.2.7.Final ([PR](https://github.com/mihaicostin/hibernate-l2-memcached/pull/6) by @akhalikov )
    - Update spymemcached to [2.12.2](https://github.com/couchbase/spymemcached/releases/tag/2.12.2)
    - Tested with 5.2.7.Final - 5.2.10.Final

## Hibernate 5.2.1.Final
*  v 5.2.1.0
    - Update [spymemcached](https://github.com/couchbase/spymemcached) to the latest version: 2.12.1

## Hibernate 5.1.1.Final - 5.1.6.Final
* v 5.1.5.1
    - Fix for [Issue #9](https://github.com/mihaicostin/hibernate-l2-memcached/issues/9)
* v 5.1.5.0
    - Support for Hibernate 5.1.x (tested with 5.1.1.Final - 5.1.5.Final)

## Hibernate 5.0.1.Final
* v 5.0.1.0
    - Support for Hibernate 5.0.x

## Hibernate 4.3.x.Final
* v 1.1.0
    - Memcached client now respects the region timeout property [Issue #1](https://github.com/mihaicostin/hibernate-l2-memcached/issues/1)
      ```xml
      <property name="hibernate.memcached.REGION.cacheTimeSeconds">10</property>
      ``` 
* v 1.0.0
    - Compatible with hibernate 4.3.x

# Known issues
- SecondLevelCacheStatistics won't work with this adapter since it tries to get the cache content as a Map (and that's not easily done with memcached)
- Cache region eviction won't actually evict the key, but invalidate them (see https://github.com/memcached/memcached/wiki/ProgrammingTricks#deleting-by-namespace)  
 
  
# Acknowledgements
* Based on the work done on Hibernate-Memcached for hibernate 3.x
    * https://github.com/raykrueger/hibernate-memcached
    * https://github.com/kcarlson/hibernate-memcached

