/*
 * Copyright(c) 2016 the original author or authors.
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

package com.sastix.cms.client;


import com.sastix.cms.common.dataobjects.ResponseDTO;
import com.sastix.cms.common.dataobjects.VersionDTO;
import com.sastix.cms.dataobjects.cache.CacheDTO;
import com.sastix.cms.dataobjects.cache.QueryCacheDTO;
import com.sastix.cms.dataobjects.cache.RemoveCacheDTO;
import com.sastix.cms.dataobjects.cache.exceptions.CacheValidationException;
import com.sastix.cms.dataobjects.cache.exceptions.DataNotFound;
import com.sastix.cms.dataobjects.content.*;
import com.sastix.cms.dataobjects.content.exceptions.ContentValidationException;
import com.sastix.cms.dataobjects.content.exceptions.ResourceAccessError;
import com.sastix.cms.dataobjects.content.exceptions.ResourceNotFound;
import com.sastix.cms.dataobjects.content.exceptions.ResourceNotOwned;
import com.sastix.cms.dataobjects.lock.LockDTO;
import com.sastix.cms.dataobjects.lock.NewLockDTO;
import com.sastix.cms.dataobjects.lock.QueryLockDTO;
import com.sastix.cms.dataobjects.lock.exceptions.LockNotAllowed;
import com.sastix.cms.dataobjects.lock.exceptions.LockNotFound;
import com.sastix.cms.dataobjects.lock.exceptions.LockNotHeld;
import com.sastix.cms.dataobjects.lock.exceptions.LockValidationException;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public interface CmsClient {
    VersionDTO getApiVersion();

    LockedResourceDTO lockResource(ResourceDTO resourceDTO) throws ResourceNotFound, ResourceNotOwned, ResourceAccessError, ContentValidationException;

    void unlockResource(LockedResourceDTO lockedResourceDTO) throws ResourceNotFound, ResourceNotOwned, ContentValidationException;

    LockedResourceDTO renewResourceLock(LockedResourceDTO lockedResourceDTO) throws ResourceNotFound, ResourceNotOwned, ContentValidationException;

    ResourceDTO createResource(CreateResourceDTO createResourceDTO) throws ResourceAccessError, ContentValidationException;

    LockedResourceDTO updateResource(UpdateResourceDTO updateResourceDTO) throws ResourceNotOwned, ResourceAccessError, ContentValidationException;

    ResourceDTO queryResource(ResourceQueryDTO resourceQueryDTO) throws ResourceAccessError, ResourceNotFound, ContentValidationException;

    ResourceDTO deleteResource(LockedResourceDTO lockedResourceDTO) throws ResourceNotOwned, ResourceAccessError, ContentValidationException;

    byte[] getData(DataDTO dataDTO) throws ResourceAccessError, ContentValidationException;

    ResponseDTO getDataStream(DataDTO dataDTO) throws ResourceAccessError, ContentValidationException, IOException;

    ResponseDTO getData(String uri) throws IOException;

    ResponseDTO getMultiPartData(String uri, Map<String, List<String>> reqHeaders) throws IOException;

    ResponseDTO getDataFromUUID(String uuid) throws IOException;

    URL getDataURL(String uri) throws IOException;

    /**
     * Cache a resource
     *
     * @param cacheDTO a CacheDTO object
     * */
    void cacheResource(CacheDTO cacheDTO) throws DataNotFound,CacheValidationException;

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
    String getUID();




    /**
     * Returns a LockDTO with a lock identifier and the proposed expiration date/time
     *
     * @param newLockDTO
     * @return a LockDTO object
     * */
    public LockDTO lockResource(NewLockDTO newLockDTO) throws LockNotAllowed,LockValidationException;

    /**
     * This call has only incoming LockDTO information
     *
     * @param lockDTO
     * */
    public void unlockResource(LockDTO lockDTO) throws LockNotHeld,LockValidationException;

    /**
     * Returns a new LockDTO with new information for this lock
     *
     * @param lockDTO
     * @return a LockDTO object
     * */
    public LockDTO renewResourceLock(LockDTO lockDTO) throws LockNotAllowed, LockNotHeld,LockValidationException;

    /**
     * Return a LockDTO or null if there is no lock found.
     *
     * @param queryLockDTO a QueryLockDTO object with the query
     * @return a LockDTO object with the information for this lock
     */
    LockDTO queryResourceLock(QueryLockDTO queryLockDTO) throws LockNotFound, LockValidationException;
}
