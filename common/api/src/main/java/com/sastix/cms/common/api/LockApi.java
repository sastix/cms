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

import com.sastix.cms.common.lock.LockDTO;
import com.sastix.cms.common.lock.NewLockDTO;
import com.sastix.cms.common.lock.QueryLockDTO;
import com.sastix.cms.common.lock.exceptions.LockNotAllowed;
import com.sastix.cms.common.lock.exceptions.LockNotFound;
import com.sastix.cms.common.lock.exceptions.LockNotHeld;
import com.sastix.cms.common.lock.exceptions.LockValidationException;

public interface LockApi {
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
