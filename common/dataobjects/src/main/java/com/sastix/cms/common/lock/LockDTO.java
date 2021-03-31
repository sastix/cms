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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * The specific object holds all the information related to a Lock.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
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

}
