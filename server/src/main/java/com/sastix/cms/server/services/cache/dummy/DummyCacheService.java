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

package com.sastix.cms.server.services.cache.dummy;

import com.sastix.cms.common.cache.CacheDTO;
import com.sastix.cms.common.cache.QueryCacheDTO;
import com.sastix.cms.common.cache.RemoveCacheDTO;
import com.sastix.cms.common.cache.exceptions.DataNotFound;
import com.sastix.cms.server.services.cache.CacheService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
@Profile("dummy")
public class DummyCacheService implements CacheService {

    @Override
    public void cacheResource(CacheDTO cacheDTO) throws DataNotFound {
        log.info("DummyCacheService->cacheResource");
    }

    @Override
    public CacheDTO getCachedResource(QueryCacheDTO queryCacheDTO) throws DataNotFound {
        log.info("DummyCacheService->getCachedResource");
        return null;
    }

    @Override
    public void removeCachedResource(RemoveCacheDTO removeCacheDTO) throws DataNotFound {
        log.info("DummyCacheService->removeCachedResource");
    }

    @Override
    public void clearCache(RemoveCacheDTO removeCacheDTO) throws DataNotFound {
        log.info("DummyCacheService->CLEAR_CACHE_REGION");

    }

    @Override
    public void clearCache() {
        log.info("DummyCacheService->CLEAR_CACHE_ALL");
    }

    @Override
    public void clearCacheExcept(List<String> cacheRegions) {

    }

    @Override
    public String getUID(String region) {
        return null;
    }
}
