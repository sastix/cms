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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * The specific object holds all the information related to a new Lock.
 */
@Getter @Setter @NoArgsConstructor
public class NewLockDTO implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 8706080938884218602L;

    /**
     * The Unique Identifier that the caller attempts to lock.
     */
    private String UID;

    /**
     * The owner of this lock.
     */
    private String lockOwner;

    /**
     * If provided, the expiration of the lock is set to this DateTime.
     * </p>
     * If not provided, the default lock expiration is set to T0+30m. Lock Expiration cannot be set to more than 8hrs.
     */
    private DateTime lockExpiration;

    /**
     * Constructor with Mandatory fields.
     *
     * @param UID            a String with the UID
     * @param lockOwner      a String with owner of this lock
     */
    public NewLockDTO(final String UID, final String lockOwner) {
        this.UID = UID;
        this.lockOwner = lockOwner;
        lockExpiration = new DateTime().plusMinutes(30);
    }

}
