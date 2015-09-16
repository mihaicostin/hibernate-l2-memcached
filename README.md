# Hibernate - Memcached
A library for using Memcached as a second level distributed cache in Hibernate.
  
  * Works with Hibernate 4
  * Uses spymemcached client
  * Supports entity and query caching.
  * Based on the work done on Hibernate-Memcached for hibernate 3.x
      * https://github.com/raykrueger/hibernate-memcached
      * https://github.com/kcarlson/hibernate-memcached

#Versions
  
## 1.0
* Compatible with hibernate 4.3.x
* Example config:

```xml
<property name="hibernate.cache.region.factory_class">com.mc.hibernate.memcached.MemcachedRegionFactory</property>
<property name="hibernate.memcached.operationTimeout">5000</property>
<property name="hibernate.memcached.connectionFactory">KetamaConnectionFactory</property>
<property name="hibernate.memcached.hashAlgorithm">HashAlgorithm.FNV1_64_HASH</property>
```

If memcached authentication is required, it can be specified using "hibernate.memcached.username" and "hibernate.memcached.password"
