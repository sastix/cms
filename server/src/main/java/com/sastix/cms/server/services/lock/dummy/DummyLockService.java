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

package com.sastix.cms.server.services.lock.dummy;

import com.sastix.cms.common.lock.LockDTO;
import com.sastix.cms.common.lock.NewLockDTO;
import com.sastix.cms.common.lock.QueryLockDTO;
import com.sastix.cms.common.lock.exceptions.LockNotAllowed;
import com.sastix.cms.common.lock.exceptions.LockNotHeld;
import com.sastix.cms.server.services.lock.LockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("dummy")
@Service
public class DummyLockService implements LockService {

    /**
     * Static LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DummyLockService.class);

    @Override
    public LockDTO lockResource(final NewLockDTO newLockDTO) throws LockNotAllowed {
        LOG.info("DummyLockService->lockResource");
        return null;
    }

    @Override
    public void unlockResource(final LockDTO lockDTO) throws LockNotHeld {
        LOG.info("DummyLockService->unlockResource");
    }

    @Override
    public LockDTO renewResourceLock(final LockDTO lockDTO) throws LockNotHeld, LockNotAllowed {
        LOG.info("DummyLockService->renewResourceLock");
        return null;
    }

    @Override
    public LockDTO queryResourceLock(QueryLockDTO queryLockDTO) {
        LOG.info("DummyLockService->queryResourceLock");
        return null;
    }
}
