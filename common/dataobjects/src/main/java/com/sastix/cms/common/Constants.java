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

package com.sastix.cms.common;

public interface Constants {

    /**
     * Rest API Version.
     */
    static String REST_API_1_0 = "1.0";
    String GET_API_VERSION = "/apiversion";
    String DEFAULT_LANGUAGE = "en";

    /**
     * CONTENT API ENDPOINTS
     */
    static String CREATE_RESOURCE = "createResource";
    static String UPDATE_RESOURCE = "updateResource";
    static String QUERY_RESOURCE = "queryResource";
    static String QUERY_RESOURCE_BY_FIELDS = "queryResourceByFields";
    static String DELETE_RESOURCE = "deleteResource";
    static String GET_DATA = "getData";
    static String GET_MULTIPART_DATA = "getMultiPartData";
    static String GET_DATA_FROM_UUID = "getDataUuid";
    static String GET_PARENT_UUID = "getParentUuid";
    static String LOCK_RESOURCE_DTO = "lockResourceDTO";
    static String UNLOCK_RESOURCE_DTO = "unlockResourceDTO";
    static String RENEW_RESOURCE_DTO_LOCK = "renewResourceDtoLock";

    /**
     * CACHE API ENDPOINTS
     */
    static final String GET_CACHE = "getCache";
    static final String PUT_CACHE = "putCache";
    static final String REMOVE_CACHE = "removeCache";
    static final String CLEAR_CACHE_REGION = "clearCache";
    static final String CLEAR_CACHE_ALL = "clearCacheAll";
    static final String CLEAR_CACHE_ALL_EXCEPT = "clearCacheAllExcept";
    static final String GET_UID = "getUID";
    static final String UID_REGION = "uidRegion";
    static final String DEFAULT_CACHE_NAME = "dynamicCache";

    /**
     * LOCK API ENDPOINTS
     */
    static String LOCK_RESOURCE = "lockResource";
    static String UNLOCK_RESOURCE = "unlockResource";
    static String RENEW_RESOURCE_LOCK = "renewResourceLock";
    static String QUERY_RESOURCE_LOCK = "queryResourceLock";

    /**
     * Error messages.
     */

    static String LOCK_NOT_ALLOWED = "The supplied resource UID cannot be locked, it is already locked by another user";
    static String LOCK_NOT_HELD = "The supplied resource UID cannot be unlocked, it is not locked by this owner or has already expired";

    static String RESOURCE_ACCESS_ERROR = "The supplied resource UID cannot be modified – the user already has the lock and cannot lock more than once.";
    static String RESOURCE_NOT_OWNED = "The supplied resource UID cannot be modified, the lock is held by someone else already";
    static String RESOURCE_NOT_FOUND = "The supplied resource UID does not exist. This is possible if the UID was recently deleted.";
    static String VALIDATION_ERROR = "The supplied resource data are invalid.";


    static Long MAX_FILE_SIZE_TO_CACHE = (long) (10 * 1024 * 1024);
}
