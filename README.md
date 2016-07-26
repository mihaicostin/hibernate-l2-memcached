# Hibernate - Memcached
A library for using Memcached as a second level distributed cache in Hibernate.
  
  * Works with Hibernate 4
  * Uses spymemcached client
  * Supports entity and query caching.
  * Based on the work done on Hibernate-Memcached for hibernate 3.x
      * https://github.com/raykrueger/hibernate-memcached
      * https://github.com/kcarlson/hibernate-memcached

#Versions

## 1.1.0

Memcached client now respects the region timeout property
 - [Issue #1](https://github.com/mihaicostin/hibernate-l2-memcached/issues/1)
```xml
<property name="hibernate.memcached.REGION.cacheTimeSeconds">10</property>
```

  
## 1.0.0
* Compatible with hibernate 4.3.x
* Example config:

```xml
<property name="hibernate.cache.region.factory_class">com.mc.hibernate.memcached.MemcachedRegionFactory</property>
<property name="hibernate.memcached.operationTimeout">5000</property>
<property name="hibernate.memcached.connectionFactory">KetamaConnectionFactory</property>
<property name="hibernate.memcached.hashAlgorithm">HashAlgorithm.FNV1_64_HASH</property>
```

If memcached authentication is required, it can be specified using "hibernate.memcached.username" and "hibernate.memcached.password"

#Maven
In order to use this library as a maven dependency, just add the following dependency to your pom.

```xml
        <dependency>
            <groupId>com.github.mihaicostin</groupId>
            <artifactId>hibernate-l2-memcached</artifactId>
            <version>1.1.0</version>
        </dependency>
```

