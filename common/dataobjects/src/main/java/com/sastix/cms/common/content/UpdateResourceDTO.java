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
import java.util.Arrays;

/**
 * The specific object all the information related to Update Resource.
 */
@Getter @Setter @NoArgsConstructor @ToString
public class UpdateResourceDTO extends LockedResourceDTO {

    /**
     * The author creating this resource.
     */
    @NotNull
    private String resourceAuthor;

    /**
     * The byte[] array of this resource.
     */
    private byte[] resourceBinary;

    /**
     * The URI holding this resource.
     */
    private String resourceExternalURI;

    /**
     * The name of the resource with optional relative path.
     */
    @NotNull
    private String resourceName;

    /**
     * Constructor with mandatory fields.
     *
     * @param resourceUID           a String with the UID
     * @param author                a String with the author
     * @param lockID                a String with the lock id
     * @param lockExpirationDate    a Datetime with the expiration
     */
    public UpdateResourceDTO(final String resourceUID,
                             final String author,
                             final String lockID,
                             final DateTime lockExpirationDate) {
        super(resourceUID, author, lockID, lockExpirationDate);
    }

    /**
     * Constructor with mandatory fields.
     *
     * @param resourceUID           a String with the UID
     * @param author                a String with the author
     * @param lockID                a String with the lock id
     * @param lockExpirationDate    a Datetime with the expiration date of the lock
     * @param resourceName          a String with resource name
     * @param newAuthor a String with the new resource author
     * @param newResourceName   a String with the new resource name
     */
    public UpdateResourceDTO(final String resourceUID,
                             final String author,
                             final String lockID,
                             final DateTime lockExpirationDate,
                             final String resourceName,
                             final String newAuthor,
                             final String newResourceName) {
        super(resourceUID, author, lockID, lockExpirationDate, resourceName);
        this.resourceAuthor = newAuthor;
        this.resourceName = newResourceName;
    }

}
