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

package com.sastix.cms.server.services.cache.manager;

import com.hazelcast.core.IdGenerator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public interface DistributedCacheManager {

    /**
     * Get cache with specific cache name.
     * If cache does not exist - creates new one based on config.
     *
     * @param cacheName cache name.
     *
     * @return distributed cache.
     */
    <K, V> ConcurrentMap<K, V> getCache(final String cacheName);

    /**
     * Get all caches.
     *
     * @return all distributed caches.
     */
    <K, V> ConcurrentMap<K, V> getCaches();

    /**
     * Get specific id generator.
     *
     * @param value the specific context of id generator
     * @return id generator.
     */
    IdGenerator getIdGenerator(String value);

    void clearAllCaches();

    void clearAllCachesExcept(List<String> cacheRegions);
}
