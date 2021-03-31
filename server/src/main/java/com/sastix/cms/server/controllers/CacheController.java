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

package com.sastix.cms.server.controllers;

import com.sastix.cms.common.Constants;
import com.sastix.cms.common.cache.CacheDTO;
import com.sastix.cms.common.cache.QueryCacheDTO;
import com.sastix.cms.common.cache.RemoveCacheDTO;
import com.sastix.cms.common.cache.exceptions.CacheValidationException;
import com.sastix.cms.common.cache.exceptions.DataNotFound;
import com.sastix.cms.server.CmsServer;
import com.sastix.cms.server.services.cache.CacheService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * Controller that handles all requests related to dynamically defined Hazelcast cache.
 */
@Slf4j
@Lazy
@RestController
@RequestMapping("/" + CmsServer.CONTEXT)
public class CacheController implements BeanFactoryAware {

    private static final String DEFAULT_CACHE_SERVICE = "hazelcastCacheService";

    @Value("${cms.cache.service:hazelcastCacheService}")
    private String cacheServiceToUse;

    private CacheService cacheService;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        try {
            cacheService = (CacheService) beanFactory.getBean(this.cacheServiceToUse);
        } catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
            cacheService = (CacheService) beanFactory.getBean(DEFAULT_CACHE_SERVICE);
        }
    }

    @RequestMapping(value = "/v"+ Constants.REST_API_1_0+"/"+ Constants.GET_CACHE, method = RequestMethod.POST)
    CacheDTO getCachedResource(@Valid @RequestBody QueryCacheDTO queryCacheDTO) throws DataNotFound, CacheValidationException {
        log.debug(Constants.GET_CACHE);
        CacheDTO cacheDTO = cacheService.getCachedResource(queryCacheDTO);
        return cacheDTO;
    }

    @RequestMapping(value = "/v"+Constants.REST_API_1_0+"/"+ Constants.PUT_CACHE, method = RequestMethod.POST)
    void cacheResource(@Valid @RequestBody CacheDTO cacheDTO) throws DataNotFound, CacheValidationException, IOException {
        log.debug(Constants.PUT_CACHE);
        cacheService.cacheResource(cacheDTO);
    }

    @RequestMapping(value = "/v"+Constants.REST_API_1_0+"/"+ Constants.REMOVE_CACHE, method = RequestMethod.POST)
    void removeCachedResource(@RequestBody RemoveCacheDTO removeCacheDTO) throws DataNotFound,CacheValidationException {
        log.debug(Constants.REMOVE_CACHE);
        cacheService.removeCachedResource(removeCacheDTO);
    }

    @RequestMapping(value = "/v"+Constants.REST_API_1_0+"/"+ Constants.CLEAR_CACHE_REGION, method = RequestMethod.POST)
    void clearCache(@RequestBody RemoveCacheDTO removeCacheDTO) throws DataNotFound,CacheValidationException {
        log.debug(Constants.CLEAR_CACHE_REGION);
        cacheService.clearCache(removeCacheDTO);
    }

    @RequestMapping(value = "/v"+Constants.REST_API_1_0+"/"+ Constants.CLEAR_CACHE_ALL, method = RequestMethod.POST)
    void clearCacheAll(@RequestBody Boolean all) {
        log.debug(Constants.CLEAR_CACHE_ALL);
        cacheService.clearCache();
    }

    @RequestMapping(value = "/v"+Constants.REST_API_1_0+"/"+ Constants.CLEAR_CACHE_ALL, method = RequestMethod.GET)
    void clearCacheAllGet() {
        log.debug(Constants.CLEAR_CACHE_ALL);
        cacheService.clearCache();
    }

    @RequestMapping(value = "/v"+Constants.REST_API_1_0+"/"+ Constants.CLEAR_CACHE_ALL_EXCEPT, method = RequestMethod.POST)
    void clearCacheAll(@RequestBody List<String> cacheRegions) {
        log.debug(Constants.CLEAR_CACHE_ALL_EXCEPT);
        cacheService.clearCacheExcept(cacheRegions);
    }

    @RequestMapping(value = "/v"+Constants.REST_API_1_0+"/"+ Constants.GET_UID, method = RequestMethod.POST)
    String getUID(@RequestBody String region) {
        log.debug(Constants.GET_UID);
        String uid = cacheService.getUID(region);
        log.debug("UID: "+uid);
        return uid;
    }
}

