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
import com.sastix.cms.common.content.*;
import com.sastix.cms.common.content.exceptions.ContentValidationException;
import com.sastix.cms.common.content.exceptions.ResourceAccessError;
import com.sastix.cms.common.content.exceptions.ResourceNotFound;
import com.sastix.cms.common.content.exceptions.ResourceNotOwned;
import com.sastix.cms.common.dataobjects.ResponseDTO;
import com.sastix.cms.common.dataobjects.VersionDTO;
import com.sastix.cms.common.lock.LockDTO;
import com.sastix.cms.common.lock.NewLockDTO;
import com.sastix.cms.common.lock.QueryLockDTO;
import com.sastix.cms.common.lock.exceptions.LockNotAllowed;
import com.sastix.cms.common.lock.exceptions.LockNotFound;
import com.sastix.cms.common.lock.exceptions.LockNotHeld;
import com.sastix.cms.common.lock.exceptions.LockValidationException;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public interface ContentApi {

    LockedResourceDTO lockResource(ResourceDTO resourceDTO) throws ResourceNotFound, ResourceNotOwned, ResourceAccessError, ContentValidationException;

    void unlockResource(LockedResourceDTO lockedResourceDTO) throws ResourceNotFound, ResourceNotOwned, ContentValidationException;

    LockedResourceDTO renewResourceLock(LockedResourceDTO lockedResourceDTO) throws ResourceNotFound, ResourceNotOwned, ContentValidationException;

    ResourceDTO createResource(CreateResourceDTO createResourceDTO) throws ResourceAccessError, ContentValidationException;

    LockedResourceDTO updateResource(UpdateResourceDTO updateResourceDTO) throws ResourceNotOwned, ResourceAccessError, ContentValidationException;

    ResourceDTO queryResource(ResourceQueryDTO resourceQueryDTO) throws ResourceAccessError, ResourceNotFound, ContentValidationException;

    ResourceDTO deleteResource(LockedResourceDTO lockedResourceDTO) throws ResourceNotOwned, ResourceAccessError, ContentValidationException;

    String getParentResource(String uuid);

    byte[] getData(DataDTO dataDTO) throws ResourceAccessError, ContentValidationException, IOException;


}
