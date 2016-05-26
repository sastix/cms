/*
 * Copyright(c) 2016 the original author or authors.
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

package com.sastix.cms.dataobjects.content;

import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

/**
 * The specific object holds all the information related to Locked Resource.
 */
public class LockedResourceDTO extends ResourceDTO {

    /**
     * A unique identifier for the lock.
     */
    @NotNull
    private String lockID;

    /**
     * The exact date/time for the expiration of this lock.
     * If the system does not refresh the lock within the time of expiry,
     * the object is considered to be unlocked.
     */
    @NotNull
    private DateTime lockExpirationDate;

    /**
     * The name of the resource with optional relative path.
     */
    //@NotNull //TODO: when trying to lock you could get an object with resourceName (v0.15 of api specs)
    private String resourceName;

    /**
     * Default Constructor.
     */
    public LockedResourceDTO() {
        //Empty
    }

    /**
     * Constructor with mandatory fields.
     *
     * @param resourceUID        a String with the resource UID
     * @param author             a String with the author
     * @param lockID             a String with the lock ID
     * @param lockExpirationDate a Datetime with the expiration date
     */
    public LockedResourceDTO(final String resourceUID,
                             final String author,
                             final String lockID,
                             final DateTime lockExpirationDate) {
        super(resourceUID, author);
        this.lockID = lockID;
        this.lockExpirationDate = lockExpirationDate;
    }

    /**
     * Constructor with mandatory fields.
     *
     * @param resourceUID        a String with the resource UID
     * @param author             a String with the author
     * @param lockID             a String with the lock ID
     * @param lockExpirationDate a Datetime with the expiration date
     * @param resourceName       a String with the name of the resource
     */
    public LockedResourceDTO(final String resourceUID,
                             final String author,
                             final String lockID,
                             final DateTime lockExpirationDate,
                             final String resourceName) {
        super(resourceUID, author);
        this.lockID = lockID;
        this.lockExpirationDate = lockExpirationDate;
        this.resourceName = resourceName;
    }

    /**
     * Returns the lock ID.
     *
     * @return a String with lock id.
     */
    public String getLockID() {
        return lockID;
    }

    /**
     * Set the lock id.
     *
     * @param lockID a String with lock id.
     */
    public void setLockID(final String lockID) {
        this.lockID = lockID;
    }

    /**
     * Returns the Expiration Date of this lock.
     *
     * @return a Datetime object
     */
    public DateTime getLockExpirationDate() {
        return lockExpirationDate;
    }

    /**
     * Set the expiration date of this lock.
     *
     * @param lockExpirationDate a Datetime object.
     */
    public void setLockExpirationDate(final DateTime lockExpirationDate) {
        this.lockExpirationDate = lockExpirationDate;
    }

    /**
     * Return the resource name.
     *
     * @return a String with the resource name.
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Set the resource name.
     *
     * @param resourceName a String with the resource name
     */
    public void setResourceName(final String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public String toString() {
        return "LockedResourceDTO{" +
                "lockID='" + lockID + '\'' +
                ", lockExpirationDate=" + lockExpirationDate +
                ", resourceName='" + resourceName + '\'' +
                '}';
    }
}

