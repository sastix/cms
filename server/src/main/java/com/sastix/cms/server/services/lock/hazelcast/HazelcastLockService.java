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

package com.sastix.cms.server.services.lock.hazelcast;

import com.sastix.cms.common.lock.LockDTO;
import com.sastix.cms.common.lock.NewLockDTO;
import com.sastix.cms.common.lock.QueryLockDTO;
import com.sastix.cms.common.lock.exceptions.LockNotAllowed;
import com.sastix.cms.common.lock.exceptions.LockNotFound;
import com.sastix.cms.common.lock.exceptions.LockNotHeld;
import com.sastix.cms.common.lock.exceptions.LockValidationException;
import com.sastix.cms.server.services.lock.LockService;
import com.sastix.cms.server.services.lock.manager.DistributedLockManager;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentMap;

@Profile("production")
@Service
public class HazelcastLockService implements LockService,BeanFactoryAware {

    /**
     * Static LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(HazelcastLockService.class);

    private static final String DEFAULT_HAZELCAST_MANAGER = "hazelcastDistributedLockManager";

    private ConcurrentMap<String, LockDTO> locks;
    public static final int DEFAULT_EXPIRATION_MINUTES=30;

    @Value("${cms.lock.manager}")
    private String lockManagerBean;
    
    private DistributedLockManager lockManager;
    
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        try {
            this.lockManager = (DistributedLockManager) beanFactory.getBean(lockManagerBean);
            this.locks = lockManager.getLockMap(DEFAULT_HAZELCAST_MANAGER);
        } catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
            this.lockManager = (DistributedLockManager) beanFactory.getBean(DEFAULT_HAZELCAST_MANAGER);
            this.locks = lockManager.getLockMap(DEFAULT_HAZELCAST_MANAGER);
        }
    }

    @Override
    public LockDTO lockResource(final NewLockDTO newLockDTO) throws LockNotAllowed,LockValidationException {
        LOG.info("hazelcastLockService->lockResource");
        nullValidationChecker(newLockDTO,NewLockDTO.class);

        if(newLockDTO.getUID() == null){
            throw new LockValidationException("Resource UID was not set in NewLockDTO.");
        }
        if (locks.containsKey(newLockDTO.getUID())) {
            //UID already Locked
            final LockDTO lockDTO = locks.get(newLockDTO.getUID());
            //Check author
            if (lockDTO.getLockOwner().equals(newLockDTO.getLockOwner())) {
                throw new LockNotAllowed("The supplied UID["+newLockDTO.getUID()+"] cannot be modified, user "
                        + lockDTO.getLockOwner()
                        + " already has the lock and cannot lock more than once");
            } else {
                throw new LockNotAllowed("The supplied UID["+newLockDTO.getUID()+"] cannot be modified, the lock is held by someone else already."
                        + " Owner: " + lockDTO.getLockOwner()
                        + " ExpirationTime: " + lockDTO.getLockExpiration());
            }
        }
        LockDTO lockedDTO = new LockDTO();
        lockedDTO.setLockID(lockManager.getIdGenerator(DEFAULT_HAZELCAST_MANAGER).newId() +"-"+ System.currentTimeMillis());
        if(newLockDTO.getLockExpiration()!=null){
            lockedDTO.setLockExpiration(newLockDTO.getLockExpiration());
        }else {
            lockedDTO.setLockExpiration(DateTime.now().plusMinutes(DEFAULT_EXPIRATION_MINUTES));
        }
        lockedDTO.setLockOwner(newLockDTO.getLockOwner());
        lockedDTO.setUID(newLockDTO.getUID());

        locks.put(newLockDTO.getUID(), lockedDTO);
        
        return lockedDTO;
    }

    @Override
    public void unlockResource(final LockDTO lockDTO) throws LockNotHeld,LockValidationException {
        LOG.info("hazelcastLockService->unlockResource");
        nullValidationChecker(lockDTO, LockDTO.class);
        if(lockDTO.getUID() == null){
            throw new LockValidationException("Resource UID was not set in LockDTO.");
        }
        if (locks.containsKey(lockDTO.getUID())) {
            //UID already Locked
            final LockDTO lockedDTO = locks.get(lockDTO.getUID());
            //Check author
            if (!lockedDTO.getLockOwner().equals(lockDTO.getLockOwner())) {
                throw new LockNotHeld("The supplied UID["+lockDTO.getUID()+"] cannot be unlocked, the lock is held by someone else already."
                        + " Owner: " + lockedDTO.getLockOwner()
                        + " ExpirationTime: " + lockDTO.getLockExpiration());
            }

            if(!lockedDTO.getLockID().equals(lockDTO.getLockID())){
                throw new LockNotHeld("The supplied UID["+lockDTO.getUID()+"] cannot be unlocked, the lock id is invalid!"
                        + " LockId: " + lockedDTO.getLockID());
            }
        } else {
            throw new LockNotHeld("Lock does not exist");
        }

        locks.remove(lockDTO.getUID());
    }

    @Override
    public LockDTO renewResourceLock(final LockDTO lockDTO) throws LockNotHeld, LockNotAllowed,LockValidationException {
        LOG.info("hazelcastLockService->renewResourceLock");
        nullValidationChecker(lockDTO, LockDTO.class);
        if(lockDTO.getUID() == null){
            throw new LockValidationException("Resource UID was not set in LockDTO.");
        }
        if (locks.containsKey(lockDTO.getUID())) {
            //UID already Locked
            final LockDTO lockedDTO = locks.get(lockDTO.getUID());
            //Check author
            if (!lockedDTO.getLockOwner().equals(lockDTO.getLockOwner()) ||
                    (lockedDTO.getLockOwner().equals(lockDTO.getLockOwner()) && lockedDTO.getLockExpiration().isBeforeNow())) {
                throw new LockNotHeld("The supplied resource UID["+lockDTO.getUID()+"] cannot be renewed, it is not locked by this owner or has already expired"
                        + " Author: " + lockedDTO.getLockOwner()
                        + " ExpirationTime: " + lockedDTO.getLockExpiration());
            }else if (!lockedDTO.getLockID().equals(lockDTO.getLockID())){
                throw new LockNotHeld("The supplied UID["+lockDTO.getUID()+"] cannot be renewed, the lock id is invalid"
                        + " Author: " + lockedDTO.getLockOwner()
                        + " ExpirationTime: " + lockedDTO.getLockExpiration());
            }

        } else {
            throw new LockNotAllowed("Lock does not exist");
        }

        LockDTO savedLockedDTO = locks.get(lockDTO.getUID());
        if(savedLockedDTO.getLockExpiration()!=null && lockDTO.getLockExpiration().isAfter(savedLockedDTO.getLockExpiration().plusMinutes(DEFAULT_EXPIRATION_MINUTES))){
            savedLockedDTO.setLockExpiration(lockDTO.getLockExpiration());
        }else {
            savedLockedDTO.setLockExpiration(DateTime.now().plusMinutes(DEFAULT_EXPIRATION_MINUTES));
        }

        if(savedLockedDTO == null){
            //TODO: this is dead code
            throw new LockNotHeld("The lock is not held anymore.This is not expected..");
        }

        return savedLockedDTO;
    }

    @Override
    public LockDTO queryResourceLock(final QueryLockDTO queryLockDTO) throws LockNotFound,LockValidationException{
        LOG.info("hazelcastLockService->queryResourceLock");
        nullValidationChecker(queryLockDTO, QueryLockDTO.class);
        if(queryLockDTO.getUID() == null){
            throw new LockValidationException("Resource UID was not set in QueryLockDTO.");
        }
        if (locks.containsKey(queryLockDTO.getUID())) {
            return locks.get(queryLockDTO.getUID());
        } else {
            throw new LockNotFound("The queried lockDTO UID["+queryLockDTO.getUID()+"] could not be found");
        }
    }

    private void nullValidationChecker(Object obj, Class aClass){
        if(obj == null){
            throw new LockValidationException(aClass+" object cannot be null!");
        }
    }
}
