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

import java.util.List;

/**
 * The specific object holds all the information related to a Resource.
 */
@Getter @Setter @NoArgsConstructor @ToString
public class ResourceDTO {

    /**
     * The UID of the object to lock.
     */
    @NotNull
    private String resourceUID;

    /**
     * The author requesting the lock.
     */
    @NotNull
    private String author;

    /**
     * If available, this URI can fetch this resource on a subsequent request, as R/O content.
     */
    private String resourceURI;

    private String name;

    private String mediaType;

    /**
     * A list of all linked resources for this UID, valid only if this UID is a "package" containing several other resources that have been inserted.
     */
    private List<ResourceDTO> resourcesList;

    /**
     * Constructor with the mandatory fields.
     *
     * @param resourceUID a String with the resource UID
     * @param author      a String with the author
     */
    public ResourceDTO(final String resourceUID, final String author) {
        this.resourceUID = resourceUID;
        this.author = author;
    }

}
