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
import com.sastix.cms.common.lock.LockDTO;
import com.sastix.cms.common.lock.NewLockDTO;
import com.sastix.cms.common.lock.QueryLockDTO;
import com.sastix.cms.common.lock.exceptions.LockNotAllowed;
import com.sastix.cms.common.lock.exceptions.LockNotFound;
import com.sastix.cms.common.lock.exceptions.LockNotHeld;
import com.sastix.cms.common.lock.exceptions.LockValidationException;
import com.sastix.cms.server.CmsServer;
import com.sastix.cms.server.services.lock.LockService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/" + CmsServer.CONTEXT)
public class LockController implements BeanFactoryAware {

    private static final String DEFAULT_LOCK_SERVICE = "hazelcastLockService";

    @Value("${cms.lock.service:hazelcastLockService}")
    private String lockServiceToUse;

    private LockService lockService;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        try {
            lockService = (LockService) beanFactory.getBean(this.lockServiceToUse);
        } catch (final Exception e) {
            lockService = (LockService) beanFactory.getBean(DEFAULT_LOCK_SERVICE);
        }
    }

    @RequestMapping(value = "/v" + Constants.REST_API_1_0 + "/" + Constants.LOCK_RESOURCE, method = RequestMethod.POST)
    public LockDTO lockResource(@RequestBody NewLockDTO newLockDTO) throws LockNotAllowed {
        log.debug(Constants.LOCK_RESOURCE);
        //validationHelper.validate(result);
        LockDTO lockedResourceDTO = lockService.lockResource(newLockDTO);
        log.debug(lockedResourceDTO.toString());
        return lockedResourceDTO;
    }

    @RequestMapping(value = "/v" + Constants.REST_API_1_0 + "/" + Constants.UNLOCK_RESOURCE, method = RequestMethod.POST)
    public void unlockResource(@RequestBody LockDTO lockDTO) throws LockNotHeld,LockNotAllowed {
        log.debug(Constants.UNLOCK_RESOURCE);
        //validationHelper.validate(result);
        lockService.unlockResource(lockDTO);
    }

    @RequestMapping(value = "/v" + Constants.REST_API_1_0 + "/" + Constants.RENEW_RESOURCE_LOCK, method = RequestMethod.POST)
    public LockDTO renewResourceLock(@RequestBody LockDTO lockDTO) throws LockNotHeld, LockNotAllowed {
        log.debug(Constants.RENEW_RESOURCE_LOCK);
        //validationHelper.validate(result);
        final LockDTO newLockedResourceDTO = lockService.renewResourceLock(lockDTO);
        log.debug(newLockedResourceDTO.toString());
        return newLockedResourceDTO;
    }

    @RequestMapping(value = "/v" + Constants.REST_API_1_0 + "/" + Constants.QUERY_RESOURCE_LOCK, method = RequestMethod.POST)
    public LockDTO queryResourceLock(@RequestBody QueryLockDTO queryLockDTO) throws LockNotFound, LockValidationException {
        log.debug(Constants.QUERY_RESOURCE_LOCK);
        //validationHelper.validate(result);
        final LockDTO newLockedResourceDTO = lockService.queryResourceLock(queryLockDTO);
        log.debug(newLockedResourceDTO != null ? newLockedResourceDTO.toString() : "queryResourceLock: NULL");
        return newLockedResourceDTO;
    }
}
