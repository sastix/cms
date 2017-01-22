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

import com.sastix.cms.common.exception.BusinessException;

/**
 * General Resource Exception.
 */
public class GeneralResourceException extends BusinessException {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -3279801053786932264L;

    /**
     * Constructor that allows a specific error message to be specified.
     *
     * @param message detail message.
     */
    public GeneralResourceException(String message) {
        super(message);
    }

    /**
     * Creates a {@code GeneralResourceException} with the specified
     * detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public GeneralResourceException(String message, Throwable cause) {
        super(message, cause);
    }

}
