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

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The specific object holds all the information related to a Resource.
 */
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

    /**
     * A list of all linked resources for this UID, valid only if this UID is a "package" containing several other resources that have been inserted.
     */
    private List<ResourceDTO> resourcesList;

    /**
     * Default Constructor.
     */
    public ResourceDTO() {
        //Empty
    }

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
     * Returns the author requesting this lock.
     *
     * @return a String with the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Set the author.
     *
     * @param author A String with author.
     */
    public void setAuthor(final String author) {
        this.author = author;
    }

    /**
     * Return the Resource URI, if available.
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

    /**
     * Returns a List of linked resources for this UID.
     *
     * @return a List with ResourceDTOs
     */
    public List<ResourceDTO> getResourcesList() {
        return resourcesList;
    }

    /**
     * Set the list of linked resources for this UID.
     *
     * @param resourcesList a list with ResourceDTOs
     */
    public void setResourcesList(final List<ResourceDTO> resourcesList) {
        this.resourcesList = resourcesList;
    }

    @Override
    public String toString() {
        return "ResourceDTO{" +
                "resourceUID='" + resourceUID + '\'' +
                ", author='" + author + '\'' +
                ", resourceURI='" + resourceURI + '\'' +
                ", resourcesList=" + resourcesList +
                '}';
    }
}
