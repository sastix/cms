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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The specific object holds all the information related to a CreateResource.
 */
@Getter @Setter @NoArgsConstructor @ToString
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

}
