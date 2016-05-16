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

package com.sastix.cms.common.exception;

/**
 * Runtime Exception for unsupported REST API version.
 */
public class VersionNotSupportedException extends RuntimeException
{
    private static final long serialVersionUID = -908063904262705775L;

    /** Constructs a new runtime exception with {@code null} as its
     * detail message.
     */
    public VersionNotSupportedException() {
        super();
    }

    /** Constructs a new runtime exception with the specified detail message.
     *
     * @param   message   the detail message.
     */
    public VersionNotSupportedException(String message) {
        super(message);
    }
}
