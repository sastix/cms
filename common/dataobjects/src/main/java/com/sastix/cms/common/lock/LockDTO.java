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

package com.sastix.cms.common.lock;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * The specific object holds all the information related to a Lock.
 */
public class LockDTO implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -1244256114246115089L;

    /**
     * The UID that has been now locked.
     */
    private String UID;

    /**
     * A Unique Lock ID that has been defined to hold this lock.
     */
    private String lockID;

    /**
     * The owner of this lock.
     */
    private String lockOwner;

    /**
     * The DateTime that this lock will expire.
     */
    private DateTime lockExpiration;

    /**
     * Default Constructor.
     */
    public LockDTO() {
    }

    /**
     * Constructor with Mandatory fields.
     *
     * @param UID            a String with the UID
     * @param lockID         a String with the lock ID
     * @param lockOwner      a String with owner of this lock
     * @param lockExpiration a Datetime with the expiration time
     */
    public LockDTO(final String UID,
                   final String lockID,
                   final String lockOwner, final
                   DateTime lockExpiration) {
        this.UID = UID;
        this.lockID = lockID;
        this.lockOwner = lockOwner;
        this.lockExpiration = lockExpiration;
    }

    /**
     * Returns the UID.
     *
     * @return a String with the UID.
     */
    public String getUID() {
        return UID;
    }

    /**
     * Set the UID.
     *
     * @param UID a String with the UID.
     */
    public void setUID(final String UID) {
        this.UID = UID;
    }

    /**
     * Returns the lock ID.
     *
     * @return a String wiht the lock id
     */
    public String getLockID() {
        return lockID;
    }

    /**
     * Set the lock id.
     *
     * @param lockID a String with the lock id.
     */
    public void setLockID(final String lockID) {
        this.lockID = lockID;
    }

    /**
     * Returns the owner of this lock.
     *
     * @return a String with the owner
     */
    public String getLockOwner() {
        return lockOwner;
    }

    /**
     * Set the owner of this lock.
     *
     * @param lockOwner a String with the owner
     */
    public void setLockOwner(final String lockOwner) {
        this.lockOwner = lockOwner;
    }

    /**
     * Returns the lock expiration.
     *
     * @return a Datetime with the expiration of the lock
     */
    public DateTime getLockExpiration() {
        return lockExpiration;
    }

    /**
     * Set the expiration of this lock.
     *
     * @param lockExpiration a Datetime with the expiration
     */
    public void setLockExpiration(final DateTime lockExpiration) {
        this.lockExpiration = lockExpiration;
    }
}
