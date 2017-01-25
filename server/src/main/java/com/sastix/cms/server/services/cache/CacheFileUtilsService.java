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

package com.sastix.cms.server.services.cache;

import com.sastix.cms.common.cache.CacheDTO;

import java.io.IOException;
import java.net.URL;

public interface CacheFileUtilsService {
    byte[] downloadResource(URL url) throws IOException;

    /**
     * Check if the cached object has expired
     *
     * @return true if the cached resource has expired
     * */
    boolean isExpiredCachedResource(CacheDTO cacheDTO);
}
