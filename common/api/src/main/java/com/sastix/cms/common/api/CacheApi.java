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

package com.sastix.cms.common.api;

import com.sastix.cms.common.cache.CacheDTO;
import com.sastix.cms.common.cache.QueryCacheDTO;
import com.sastix.cms.common.cache.RemoveCacheDTO;
import com.sastix.cms.common.cache.exceptions.CacheValidationException;
import com.sastix.cms.common.cache.exceptions.DataNotFound;

import java.io.IOException;
import java.util.List;

public interface CacheApi {
    /**
     * Cache a resource
     *
     * @param cacheDTO a CacheDTO object
     * */
    void cacheResource(CacheDTO cacheDTO) throws DataNotFound, CacheValidationException, IOException;

    /**
     * Returns a CacheDTO found in distributed cache
     *
     * @return a CacheDTO object
     * */
    CacheDTO getCachedResource(QueryCacheDTO queryCacheDTO) throws DataNotFound,CacheValidationException;

    /**
     * Remove a resource
     * */
    void removeCachedResource (RemoveCacheDTO removeCacheDTO) throws DataNotFound,CacheValidationException;

    /**
     * Clear a specific region
     * */
    void clearCache(RemoveCacheDTO removeCacheDTO) throws DataNotFound,CacheValidationException;

    /**
     * Clear all caches
     * */
    void clearCache();

    /**
     * Clear all caches except for
     * */
    void clearCacheExcept(List<String> cacheRegions);

    /**
     * Get a unique id
     * */
    String getUID(String region);
}
