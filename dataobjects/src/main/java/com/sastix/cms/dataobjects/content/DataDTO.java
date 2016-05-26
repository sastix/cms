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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sastix.cms.dataobjects.content.exceptions.ContentValidationException;

/**
 * The specific object holds all the information related to a DataDTO.
 */
public class DataDTO {

    /**
     * The UID of the object to lock.
     */
    private String resourceUID;

    /**
     * It is a self-sufficient URI that holds the precise resource location.
     */
    private String resourceURI;

    /**
     * Default Constructor.
     */
    public DataDTO() {
        //Empty
    }

    /**
     * Returns the resource UID.
     *
     * @return a String with the UID
     */
    public String getResourceUID() {
        return resourceUID;
    }

    /**
     * Set the resource UID.
     *
     * @param resourceUID the String with UID
     */
    public void setResourceUID(final String resourceUID) {
        this.resourceUID = resourceUID;
    }

    /**
     * Return the Resource URI.
     *
     * @return a String with the resource URI.
     */
    public String getResourceURI() {
        return resourceURI;
    }

    /**
     * Set the resource URI.
     *
     * @param resourceURI a String with the resource URI
     */
    public void setResourceURI(final String resourceURI) {
        this.resourceURI = resourceURI;
    }

    @Override
    public String toString() {
        return "DataDTO{" +
                "resourceUID='" + resourceUID + '\'' +
                ", resourceURI='" + resourceURI + '\'' +
                '}';
    }

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
                //Something went wrong
                throw new RuntimeException("Something went wrong");
            }
        } catch (Exception e) {
            throw new ContentValidationException("Tenant ID cannot be extracted");
        }
        return tenantID;
    }
}
