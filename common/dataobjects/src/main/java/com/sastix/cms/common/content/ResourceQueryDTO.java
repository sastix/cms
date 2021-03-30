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
 * The specific object holds all the information related to Resource Query.
 */
@Getter @Setter @NoArgsConstructor @ToString
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
     * Constructor with the mandatory fields.
     *
     * @param queryUID a String with query UID
     */
    public ResourceQueryDTO(final String queryUID) {
        this.queryUID = queryUID;
    }

}
