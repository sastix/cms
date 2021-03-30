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

package com.sastix.cms.common.content;

import org.joda.time.DateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * The specific object holds all the information related to Locked Resource.
 */
@Getter @Setter @NoArgsConstructor @ToString
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

}
