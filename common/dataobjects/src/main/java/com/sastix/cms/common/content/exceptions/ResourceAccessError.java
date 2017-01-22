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

package com.sastix.cms.common.content.exceptions;

import com.sastix.cms.common.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The specified Resource UID cannot be modified or The supplied resource data are invalid.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = Constants.RESOURCE_ACCESS_ERROR)
public class ResourceAccessError extends GeneralResourceException {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 7739893346328334895L;

    /**
     * Constructor that allows a specific error message to be specified.
     *
     * @param message detail message.
     */
    public ResourceAccessError(String message) {
        super(message);
    }
}
