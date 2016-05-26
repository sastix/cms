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

/**
 * The specific object holds all the information related to Resource Query.
 */
public class ResourceQueryDTO {

    /**
     * The name of the resource queried.
     */
    private String queryResourceName;

    /**
     * The media-type of this resource.
     */
    private String queryResourceMediaType;

    /**
     * The author last updated this resource.
     */
    private String queryResourceAuthor;

    /**
     * The UID of this resource.
     */
    @NotNull
    private String queryUID;

    /**
     * Default Constructor.
     */
    public ResourceQueryDTO() {
        //Empty
    }

    /**
     * Constructor with the mandatory fields.
     *
     * @param queryUID a String with query UID
     */
    public ResourceQueryDTO(final String queryUID) {
        this.queryUID = queryUID;
    }

    /**
     * Return the name of the resource queried.
     *
     * @return a String with the name
     */
    public String getQueryResourceName() {
        return queryResourceName;
    }

    /**
     * Set the name of the resource queried.
     *
     * @param queryResourceName a String with name
     */
    public void setQueryResourceName(final String queryResourceName) {
        this.queryResourceName = queryResourceName;
    }

    /**
     * Return the media type of this resource.
     *
     * @return a String with the media type
     */
    public String getQueryResourceMediaType() {
        return queryResourceMediaType;
    }

    /**
     * Set the media type of this resource.
     *
     * @param queryResourceMediaType a String with the media type
     */
    public void setQueryResourceMediaType(final String queryResourceMediaType) {
        this.queryResourceMediaType = queryResourceMediaType;
    }

    /**
     * Returns the author last updated this resource.
     *
     * @return a String with the author
     */
    public String getQueryResourceAuthor() {
        return queryResourceAuthor;
    }

    /**
     * Set the author last updated this resource.
     *
     * @param queryResourceAuthor a String with the author
     */
    public void setQueryResourceAuthor(final String queryResourceAuthor) {
        this.queryResourceAuthor = queryResourceAuthor;
    }

    /**
     * Returns the UID of this resource.
     *
     * @return a String with the UID
     */
    public String getQueryUID() {
        return queryUID;
    }

    /**
     * Set the UID of this resource.
     *
     * @param queryUID a String with the UID
     */
    public void setQueryUID(final String queryUID) {
        this.queryUID = queryUID;
    }

    @Override
    public String toString() {
        return "ResourceQueryDTO{" +
                "queryResourceName='" + queryResourceName + '\'' +
                ", queryResourceMediaType='" + queryResourceMediaType + '\'' +
                ", queryResourceAuthor='" + queryResourceAuthor + '\'' +
                ", queryUID='" + queryUID + '\'' +
                '}';
    }
}
