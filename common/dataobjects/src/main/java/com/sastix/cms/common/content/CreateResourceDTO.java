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

import javax.validation.constraints.NotNull;

/**
 * The specific object holds all the information related to a CreateResource.
 */
public class CreateResourceDTO {

    /**
     * The media-type of this new resource.
     */
    @NotNull
    private String resourceMediaType;

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
     * A system-specific tenant identifier for multi-tenancy reasons.
     */
    @NotNull
    private String resourceTenantId;
    
    // TODO: Should we add a parentResourceUID?
    
    /**
     * Default Constructor.
     */
    public CreateResourceDTO() {
    }

    /**
     * Constructor with mandatory fields.
     *
     * @param resourceMediaType a String with the media type
     * @param resourceAuthor    a String with the author
     * @param resourceName      a String with resource name
     * @param resourceTenantId  a String with the tenant id
     */
    public CreateResourceDTO(final String resourceMediaType,
                             final String resourceAuthor,
                             final String resourceName,
                             final String resourceTenantId) {
        this.resourceMediaType = resourceMediaType;
        this.resourceAuthor = resourceAuthor;
        this.resourceName = resourceName;
        this.resourceTenantId = resourceTenantId;
    }

    /**
     * Returns the Resource Media Type.
     *
     * @return a String with the Media type
     */
    public String getResourceMediaType() {
        return resourceMediaType;
    }

    /**
     * Set the media type of the resource.
     *
     * @param resourceMediaType a String with the media type
     */
    public void setResourceMediaType(final String resourceMediaType) {
        this.resourceMediaType = resourceMediaType;
    }

    /**
     * Return the author of this resource.
     *
     * @return a String with the author
     */
    public String getResourceAuthor() {
        return resourceAuthor;
    }

    /**
     * Set the author of the resource
     *
     * @param resourceAuthor a String with the author
     */
    public void setResourceAuthor(final String resourceAuthor) {
        this.resourceAuthor = resourceAuthor;
    }

    /**
     * Returns a byte array of this resource.
     *
     * @return a byte[] of this resource
     */
    public byte[] getResourceBinary() {
        return resourceBinary;
    }

    /**
     * Set the byte array of the resource.
     *
     * @param resourceBinary a byte[] of this resource
     */
    public void setResourceBinary(final byte[] resourceBinary) {
        this.resourceBinary = resourceBinary;
    }

    /**
     * Returns the URI holding this resource.
     *
     * @return A String with the URI
     */
    public String getResourceExternalURI() {
        return resourceExternalURI;
    }

    /**
     * Set the URI of this resource.
     *
     * @param resourceExternalURI a String with the URI
     */
    public void setResourceExternalURI(final String resourceExternalURI) {
        this.resourceExternalURI = resourceExternalURI;
    }

    /**
     * Returns the name of this resource.
     *
     * @return a String with the name
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Set the name of this resource.
     *
     * @param resourceName a String with the name
     */
    public void setResourceName(final String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * Returns the tenant id of this resource.
     *
     * @return a String with the tenant id.
     */
    public String getResourceTenantId() {
        return resourceTenantId;
    }

    /**
     * Returns the tenant id of this resource.
     *
     * @param resourceTenantId a String with the tenant id
     */
    public void setResourceTenantId(final String resourceTenantId) {
        this.resourceTenantId = resourceTenantId;
    }

    @Override
    public String toString() {
        return "CreateResourceDTO{" +
                "resourceMediaType='" + resourceMediaType + '\'' +
                ", resourceAuthor='" + resourceAuthor + '\'' +
                ", resourceBinary=" + (resourceBinary == null ? "empty" : Integer.toString(resourceBinary.length)) +
                ", resourceExternalURI='" + resourceExternalURI + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", resourceTenantId='" + resourceTenantId + '\'' +
                '}';
    }

//    @AssertTrue(message="passVerify field should be equal than pass field")
//    public boolean isValid() {
//        return !(resourceExternalURI == null && resourceBinary == null);
//    }
}
