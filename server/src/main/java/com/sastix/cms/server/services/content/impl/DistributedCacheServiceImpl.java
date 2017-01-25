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

package com.sastix.cms.server.services.content.impl;

import com.sastix.cms.common.Constants;
import com.sastix.cms.common.cache.CacheDTO;
import com.sastix.cms.common.cache.RemoveCacheDTO;
import com.sastix.cms.common.content.exceptions.ResourceAccessError;
import com.sastix.cms.server.services.cache.CacheService;
import com.sastix.cms.server.services.content.DistributedCacheService;
import com.sastix.cms.server.services.content.HashedDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DistributedCacheServiceImpl implements DistributedCacheService {

    @Autowired
    HashedDirectoryService hashedDirectoryService;

    @Autowired
    CacheService cacheService;

    @Override
    public void cacheIt(String uri, String tenantId) {
        try {
            if (hashedDirectoryService.getFileSize(uri, tenantId) < Constants.MAX_FILE_SIZE_TO_CACHE) {
                final byte[] data = hashedDirectoryService.getBytesByURI(uri, tenantId);
                final CacheDTO cacheDTO = new CacheDTO(uri, data);
                cacheService.cacheResource(cacheDTO);
            }
        } catch (IOException e) {
            throw new ResourceAccessError(e.toString());
        }

    }

    @Override
    public void updateCache(final String uri, final String tenantId) {
        RemoveCacheDTO removeCacheDTO = new RemoveCacheDTO(uri);
        cacheService.removeCachedResource(removeCacheDTO);
        cacheIt(uri, tenantId);
    }
}
