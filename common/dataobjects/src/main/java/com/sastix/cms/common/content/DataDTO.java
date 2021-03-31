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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sastix.cms.common.content.exceptions.ContentValidationException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The specific object holds all the information related to a DataDTO.
 */
@Getter @Setter @NoArgsConstructor @ToString
public class DataDTO {

    /**
     * The UID of the object to lock.
     */
    private String resourceUID;

    /**
     * It is a self-sufficient URI that holds the precise resource location.
     */
    private String resourceURI;

    @JsonIgnore
    public String getTenantID() throws ContentValidationException {
        final String tenantID;
        try {
            if (resourceUID != null && !resourceUID.isEmpty()) {
                tenantID = resourceUID.substring(resourceUID.lastIndexOf('-') + 1);
            } else if (resourceURI != null && !resourceURI.isEmpty()) {
                final String context = resourceURI.substring(0, resourceURI.indexOf("/"));
                tenantID = context.substring(context.lastIndexOf('-') + 1);
            } else {
                throw new RuntimeException("Something went wrong");
            }
        } catch (Exception e) {
            throw new ContentValidationException("Tenant ID cannot be extracted");
        }
        return tenantID;
    }
}
