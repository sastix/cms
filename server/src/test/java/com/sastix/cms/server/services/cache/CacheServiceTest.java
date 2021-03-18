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
import com.sastix.cms.common.cache.QueryCacheDTO;
import com.sastix.cms.common.cache.RemoveCacheDTO;
import com.sastix.cms.common.cache.exceptions.CacheValidationException;
import com.sastix.cms.common.cache.exceptions.DataNotFound;
import com.sastix.cms.server.CmsServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles({"production", "test"})
@SpringBootTest(classes = {CmsServer.class})
public class CacheServiceTest {
    private Logger LOG = LoggerFactory.getLogger(CacheServiceTest.class);

    @Autowired
    private CacheService cacheService;

    private byte[] bytesToBeCached;

    @BeforeAll
    public void setUp() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = Paths.get(classLoader.getResource("logo.png").getFile());
        bytesToBeCached = Files.readAllBytes(path);
    }

    @Test
    public void cacheResourceTest() throws IOException {
        String cacheKey = "cacheKey1";
        CacheDTO cacheDTO = new CacheDTO(cacheKey, bytesToBeCached);

        cacheService.cacheResource(cacheDTO);

        //get from cache
        CacheDTO dto = cacheService.getCachedResource(new QueryCacheDTO(cacheKey));
        assertArrayEquals(bytesToBeCached, dto.getCacheBlobBinary());
    }

    @Test
    public void cacheResourceWithNullKeyTest() throws IOException {
        CacheDTO cacheDTO = new CacheDTO();
        try {
            cacheService.cacheResource(cacheDTO);
            fail("Expected CacheValidationException exception");
        } catch (CacheValidationException e) {
            assertTrue(e.getMessage().contains("You cannot cache a resource with a null key"));
        }
    }

    @Test
    public void cacheResourceWithEmptyDataTest() throws IOException {
        String cacheKey = "cacheKey-cacheResourceWithEmptyDataTest";
        CacheDTO cacheDTO = new CacheDTO();
        cacheDTO.setCacheKey(cacheKey);
        cacheService.cacheResource(cacheDTO);

        //get from cache
        CacheDTO dto = cacheService.getCachedResource(new QueryCacheDTO(cacheKey));
        assertNull(dto.getCacheBlobBinary());
    }

    @Test
    public void cacheResourceWithNullTest() throws IOException {
        try {
            cacheService.cacheResource(null);
            fail("Expected CacheValidationException exception");
        } catch (CacheValidationException e) {
            assertTrue(e.getMessage().contains("CacheDTO object cannot be null"));
        }
    }

    @Test
    public void cacheResourceWithBlobAndUriTest() throws IOException {
        String cacheKey = "cacheKey-cacheResourceWithBlobAndUriTest";
        CacheDTO cacheDTO = new CacheDTO();
        cacheDTO.setCacheKey(cacheKey);
        cacheDTO.setCacheBlobBinary(bytesToBeCached);
        cacheDTO.setCacheBlobURI("http://www.google.com");
        try {
            cacheService.cacheResource(cacheDTO);
            fail("Expected CacheValidationException exception");
        } catch (CacheValidationException e) {
            assertTrue(e.getMessage().contains("Client should NOT be able to send both blob AND URI. Only one of them"));
        }
    }

    @Test
    public void getCachedResourceWithEmptyObjectTest() throws IOException {
        try {
            cacheService.getCachedResource(new QueryCacheDTO());
            fail("Expected CacheValidationException exception");
        } catch (CacheValidationException e) {
            assertTrue(e.getMessage().contains("You cannot get a cache a resource with a null key"));
        }
    }

    @Test
    public void getCachedResourceWithNullTest() throws IOException {
        try {
            cacheService.getCachedResource(null);
            fail("Expected CacheValidationException exception");
        } catch (CacheValidationException e) {
            assertTrue(e.getMessage().contains("QueryCacheDTO object cannot be null"));
        }
    }

    @Test
    public void getNonExistingCachedResourceTest() throws IOException {
        QueryCacheDTO queryCacheDTO = new QueryCacheDTO();
        queryCacheDTO.setCacheKey("dummy key");
        try {
            cacheService.getCachedResource(queryCacheDTO);
            fail("Expected DataNotFound exception");
        } catch (DataNotFound e) {
            assertTrue(e.getMessage().contains("A cached resource could not be found with the given key: dummy key"));
        }
    }


    @Test
    public void removeCachedResourceTest() throws IOException {
        String cacheKey = "cacheKey1-removeCachedResourceTest";
        CacheDTO cacheDTO = new CacheDTO(cacheKey, bytesToBeCached);

        cacheService.cacheResource(cacheDTO);

        //get from cache
        CacheDTO dto = cacheService.getCachedResource(new QueryCacheDTO(cacheKey));
        assertArrayEquals(bytesToBeCached, dto.getCacheBlobBinary());

        //remove from cache
        cacheService.removeCachedResource(new RemoveCacheDTO(cacheKey));

        try {
            cacheService.getCachedResource(new QueryCacheDTO(cacheKey));
            fail("Expected DataNotFound exception");
        } catch (DataNotFound e) {
            assertTrue(e.getMessage().contains("A cached resource could not be found with the given key: " + cacheKey));
        }
    }

    @Test
    public void removeCachedResourceWithEmptyKeyTest() throws IOException {
        try {
            cacheService.removeCachedResource(new RemoveCacheDTO());
            fail("Expected CacheValidationException exception");
        } catch (CacheValidationException e) {
            assertTrue(e.getMessage().contains("You cannot remove a cached a resource with a null cache key"));
        }
    }

    @Test
    public void removeCachedResourceNullTest() throws IOException {
        try {
            cacheService.removeCachedResource(null);
            fail("Expected CacheValidationException exception");
        } catch (CacheValidationException e) {
            assertTrue(e.getMessage().contains("RemoveCacheDTO object cannot be null"));
        }
    }

    @Test
    public void removeCachedResourceWithNonExistingKeyTest() throws IOException {
        String key = "dummy-doom";
        RemoveCacheDTO removeCacheDTO = new RemoveCacheDTO();
        removeCacheDTO.setCacheKey(key);
        try {
            cacheService.removeCachedResource(removeCacheDTO);
            fail("Expected DataNotFound exception");
        } catch (DataNotFound e) {
            assertTrue(e.getMessage().contains("Nothing to remove. There is no cached resource with the given key: " + key));
        }
    }

    @Test
    public void removeCachedResourceFromRegionWithNonExistingKeyTest() throws IOException {
        String key = "dummy-doom";
        RemoveCacheDTO removeCacheDTO = new RemoveCacheDTO();
        removeCacheDTO.setCacheKey(key);
        removeCacheDTO.setCacheRegion("custom-region");
        try {
            cacheService.removeCachedResource(removeCacheDTO);
            fail("Expected DataNotFound exception");
        } catch (DataNotFound e) {
            assertTrue(e.getMessage().contains("Nothing to remove. There is no cached resource with the given key: " + key));
        }
    }

    @Test
    public void clearCacheWithEmptyRegionTest() throws IOException {
        try {
            cacheService.clearCache(new RemoveCacheDTO());
            fail("Expected CacheValidationException exception");
        } catch (CacheValidationException e) {
            assertTrue(e.getMessage().contains("A cacheRegion was not provided. If you want to clear all caches use the clearCache() without passing any object."));
        }
    }

    @Test
    public void clearCacheNonExistingRegionTest() throws IOException {
        RemoveCacheDTO removeCacheDTO = new RemoveCacheDTO();
        removeCacheDTO.setCacheRegion("dummyRegionNotExists");
        try {
            cacheService.clearCache(removeCacheDTO);
            fail("Expected DataNotFound exception");
        } catch (DataNotFound e) {
            assertTrue(e.getMessage().contains("The supplied region was not available – did not exist"));
        }
    }

    @Test
    public void clearCacheRegionNullTest() throws IOException {
        try {
            cacheService.clearCache(null);
            fail("Expected CacheValidationException exception");
        } catch (CacheValidationException e) {
            assertTrue(e.getMessage().contains("RemoveCacheDTO object cannot be null"));
        }
    }
}
