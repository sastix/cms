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

package com.sastix.cms.server.services.cache.hazelcast;

import com.hazelcast.core.IMap;
import com.hazelcast.core.IdGenerator;
import com.sastix.cms.common.Constants;
import com.sastix.cms.common.cache.CacheDTO;
import com.sastix.cms.common.cache.QueryCacheDTO;
import com.sastix.cms.common.cache.RemoveCacheDTO;
import com.sastix.cms.common.cache.exceptions.CacheValidationException;
import com.sastix.cms.common.cache.exceptions.DataNotFound;
import com.sastix.cms.server.services.cache.CacheFileUtilsService;
import com.sastix.cms.server.services.cache.CacheService;
import com.sastix.cms.server.services.cache.manager.DistributedCacheManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.CRC32;

@Slf4j
@Service
@Profile("production")
public class HazelcastCacheService implements CacheService, BeanFactoryAware {

    private static final String DEFAULT_HAZELCAST_MANAGER = "hazelcastDistributedCacheManager";

    private ConcurrentMap<String, CacheDTO> cache;
    DistributedCacheManager cm;

    @Value("${cms.cache.manager}")
    private String cacheManagerBean;

    private DistributedCacheManager cacheManager;

    @Autowired
    CacheFileUtilsService cacheFileUtilsService;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        try {
            this.cache = (ConcurrentMap<String, CacheDTO>) beanFactory.getBean(Constants.DEFAULT_CACHE_NAME);
            this.cacheManager = (DistributedCacheManager) beanFactory.getBean(cacheManagerBean);
        } catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
            /* Creating cache on the fly.
              Pay attention that 'default' map config key is going to be used in this case. */
            this.cacheManager = (DistributedCacheManager) beanFactory.getBean(DEFAULT_HAZELCAST_MANAGER);
            this.cm = cacheManager;
            this.cache = cacheManager.getCache(Constants.DEFAULT_CACHE_NAME);
        }
    }

    @Override
    public void cacheResource(CacheDTO cacheDTO) throws DataNotFound, CacheValidationException, IOException {
        log.info("HazelcastCacheService->cacheResource");
        nullValidationChecker(cacheDTO,CacheDTO.class);
        String cacheKey = cacheDTO.getCacheKey();
        String cacheRegion = cacheDTO.getCacheRegion();
        byte[] blob;
        String blobURI = cacheDTO.getCacheBlobURI();

        if(!StringUtils.isEmpty(blobURI) && cacheDTO.getCacheBlobBinary().length > 0){
            throw new CacheValidationException("Client should NOT be able to send both blob AND URI. Only one of them");
        }

        if (blobURI != null) {
            blob = cacheFileUtilsService.downloadResource(new URL(blobURI));
            cacheDTO.setCacheBlobBinary(blob);
        }

        if(cacheDTO.getCacheKey()==null){
            throw new CacheValidationException("You cannot cache a resource with a null key");
        }

        // Put value into cache.
        if (!StringUtils.isEmpty(cacheRegion)) {
            cm.getCache(cacheRegion).put(cacheKey, cacheDTO);
        } else {
            this.cache.put(cacheKey, cacheDTO);
        }
    }

    @Override
    public CacheDTO getCachedResource(QueryCacheDTO queryCacheDTO) throws DataNotFound, CacheValidationException {
        log.info("HazelcastCacheService->getCachedResource ({})", queryCacheDTO!=null?queryCacheDTO.toString():"null");
        nullValidationChecker(queryCacheDTO, QueryCacheDTO.class);
        String cacheKey = queryCacheDTO.getCacheKey();

        if(cacheKey == null){
            throw new CacheValidationException("You cannot get a cache a resource with a null key");
        }

        String cacheRegion = queryCacheDTO.getCacheRegion();
        CacheDTO cacheDTO;
        if (!StringUtils.isEmpty(cacheRegion)) { // GET from cache region
            ConcurrentMap<String, CacheDTO> cMap = cm.getCache(cacheRegion);
            cacheDTO = cMap.get(cacheKey);
        } else {// GET from default cache
            cacheDTO = this.cache.get(cacheKey);
        }

        boolean hasExpired = cacheFileUtilsService.isExpiredCachedResource(cacheDTO);

        if (hasExpired) {
            // Delete from cache silently and return null
            RemoveCacheDTO removeCacheDTO = new RemoveCacheDTO();
            removeCacheDTO.setCacheKey(cacheKey);
            removeCacheDTO.setCacheRegion(cacheRegion);
            removeCachedResource(removeCacheDTO);
            cacheDTO = null;
        }

        if(cacheDTO == null){
            throw new DataNotFound("A cached resource could not be found with the given key: "+cacheKey);
        }
        return cacheDTO;
    }

    @Override
    public void removeCachedResource(RemoveCacheDTO removeCacheDTO) throws DataNotFound,CacheValidationException {
        log.info("HazelcastCacheService->removeCachedResource");
        nullValidationChecker(removeCacheDTO, RemoveCacheDTO.class);
        String cacheKey = removeCacheDTO.getCacheKey();
        String cacheRegion = removeCacheDTO.getCacheRegion();

        if(cacheKey==null){
            throw new CacheValidationException("You cannot remove a cached a resource with a null cache key");
        }

        if (!StringUtils.isEmpty(cacheRegion)) {
            ConcurrentMap<String, String> cMap = cm.getCache(cacheRegion);
            if (cMap.containsKey(cacheKey)) {
                cMap.remove(cacheKey);
            }else {
                throw new DataNotFound("Nothing to remove. There is no cached resource with the given key: "+cacheKey);
            }
        } else {
            if (this.cache.containsKey(cacheKey)) {
                this.cache.remove(cacheKey);
            }else{
                throw new DataNotFound("Nothing to remove. There is no cached resource with the given key: "+cacheKey);
            }
        }
    }

    @Override
    public void clearCache(RemoveCacheDTO removeCacheDTO) throws DataNotFound, CacheValidationException {
        log.info("HazelcastCacheService->CLEAR_CACHE_REGION");
        nullValidationChecker(removeCacheDTO, RemoveCacheDTO.class);
        String cacheRegion = removeCacheDTO != null ? removeCacheDTO.getCacheRegion() : null;

        if (!StringUtils.isEmpty(cacheRegion)) {//clear specific region
            ConcurrentMap<String, IMap> regionCacheMap = (ConcurrentMap<String, IMap>) cm.getCaches().get(cacheRegion);
            if (regionCacheMap == null) {
                throw new DataNotFound("The supplied region was not available – did not exist");
            } else {
                ConcurrentMap<String, CacheDTO> cMap = cm.getCache(cacheRegion);
                cMap.clear();
            }
        }else{
            throw new CacheValidationException("A cacheRegion was not provided. If you want to clear all caches use the clearCache() without passing any object.");
        }
    }

    @Override
    public void clearCache() {
        log.info("HazelcastCacheService->CLEAR_ALL_CACHE_REGIONS_AND_MAIN_CACHE");
        ConcurrentMap<String, IMap> caches = cm.getCaches();
        for (Map.Entry<String, IMap> entry : caches.entrySet()) {
            entry.getValue().clear();
        }
        this.cache.clear();
        cm.clearAllCaches();
    }

    @Override
    public void clearCacheExcept(List<String> cacheRegions) {
        log.info("HazelcastCacheService->CLEAR_ALL_CACHE_REGIONS_AND_MAIN_CACHE Except for specific values");
        ConcurrentMap<String, IMap> caches = cm.getCaches();
        for (Map.Entry<String, IMap> entry : caches.entrySet()) {
            String region = entry.getValue().getName();
            boolean exists = cacheRegions.stream().filter(s->region.contains(s)).count()>0;
            if(!exists) {
                entry.getValue().clear();
            }
        }
        this.cache.clear();
        cm.clearAllCachesExcept(cacheRegions);
    }

    @Override
    public String getUID(String region) {
        CRC32 CRC_32 = new CRC32();
        log.info("HazelcastCacheService->GET_UID");
        IdGenerator idGenerator = cm.getIdGenerator(region);
        final String uid = String.valueOf(idGenerator.newId()); //assures uniqueness during the life cycle of the cluster
        final String uuid = UUID.randomUUID().toString();
        String ret = new StringBuilder(uuid).append(region).append(uid).toString();
        CRC_32.reset();
        CRC_32.update(ret.getBytes());
        return Long.toHexString(CRC_32.getValue());
    }

    private void nullValidationChecker(Object obj, Class aClass){
        if(obj == null){
            throw new CacheValidationException(aClass+" object cannot be null!");
        }
    }
}
