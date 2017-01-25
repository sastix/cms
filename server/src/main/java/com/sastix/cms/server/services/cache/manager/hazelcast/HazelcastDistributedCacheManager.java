/*
 * Copyright(c) 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sastix.cms.server.services.cache.manager.hazelcast;

import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;
import com.sastix.cms.server.services.cache.manager.DistributedCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Hazelcast specific implementation of distributed cache manager.
 *
 */
@Service
public class HazelcastDistributedCacheManager implements DistributedCacheManager {

    private volatile ConcurrentMap<String, IMap> caches = new ConcurrentHashMap<>();

    @Autowired
    @Qualifier(value = "hazelcastInstance")
    private HazelcastInstance hazelcastInstance;

    private volatile ConcurrentMap<String, IdGenerator> idGenerators = new ConcurrentHashMap<String, IdGenerator>();

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> IMap<K, V> getCache(String cacheName) {
        if (caches.get(cacheName) == null) {
            synchronized (this) {
                if (caches.get(cacheName) == null) {
                    final IMap<K, V> map = this.hazelcastInstance.getMap(cacheName);
                    caches.putIfAbsent(cacheName, map);

                    return map;
                }
            }
        }

        return caches.get(cacheName);
    }

    public ConcurrentMap<String, IMap> getCaches() {
        return caches;
    }

    @Override
    public IdGenerator getIdGenerator(String value) {
        if (idGenerators.get(value) == null) {
            synchronized (this) {
                if (idGenerators.get(value) == null) {
                    return this.hazelcastInstance.getIdGenerator(value);
                }
            }
        }
        return idGenerators.get(value);
    }

    public void clearAllCaches(){
        final Collection<DistributedObject> distributedObjects = hazelcastInstance.getDistributedObjects();
        for (DistributedObject distributedObject : distributedObjects) {
            if (distributedObject instanceof IMap) {
                final IMap<?, ?> map = (IMap) distributedObject;
                map.clear();
            }
        }
    }

    public void clearAllCachesExcept(List<String> cacheRegions){
        final Collection<DistributedObject> distributedObjects = hazelcastInstance.getDistributedObjects();
        for (DistributedObject distributedObject : distributedObjects) {
            if (distributedObject instanceof IMap) {
                final IMap<?, ?> map = (IMap) distributedObject;
                String region = map.getName();
                boolean exists = cacheRegions.stream().filter(s->region.contains(s)).count()>0;
                if(!exists) {
                    map.clear();
                }
            }
        }
    }
}
