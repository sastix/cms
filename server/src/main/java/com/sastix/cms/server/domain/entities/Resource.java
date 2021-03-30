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
 *
 */

package com.sastix.cms.server.domain.entities;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "resource")
@NamedQueries({
        @NamedQuery(name = "Resource.findAll", query = "SELECT resource FROM Resource resource")
        , @NamedQuery(name = "Resource.findByAuthor", query = "SELECT resource FROM Resource resource WHERE resource.author = :author")
        , @NamedQuery(name = "Resource.findByAuthorContaining", query = "SELECT resource FROM Resource resource WHERE resource.author like :author")
        , @NamedQuery(name = "Resource.findByName", query = "SELECT resource FROM Resource resource WHERE resource.name = :name")
        , @NamedQuery(name = "Resource.findByNameContaining", query = "SELECT resource FROM Resource resource WHERE resource.name like :name")
        , @NamedQuery(name = "Resource.findByMediaType", query = "SELECT resource FROM Resource resource WHERE resource.mediaType = :mediaType")
        , @NamedQuery(name = "Resource.findByMediaTypeContaining", query = "SELECT resource FROM Resource resource WHERE resource.mediaType like :mediaType")
        , @NamedQuery(name = "Resource.findByPath", query = "SELECT resource FROM Resource resource WHERE resource.path = :path")
        , @NamedQuery(name = "Resource.findByPathContaining", query = "SELECT resource FROM Resource resource WHERE resource.path like :path")
        , @NamedQuery(name = "Resource.findByUid", query = "SELECT resource FROM Resource resource WHERE resource.uid = :uid")
        , @NamedQuery(name = "Resource.findByUidContaining", query = "SELECT resource FROM Resource resource WHERE resource.uid like :uid")
})
@Getter @Setter @NoArgsConstructor
public class Resource implements Serializable {

    public static final String FIND_ALL = "Resource.findAll";
    public static final String FIND_BY_AUTHOR = "Resource.findByAuthor";
    public static final String FIND_BY_AUTHOR_CONTAINING = "Resource.findByAuthorContaining";
    public static final String FIND_BY_NAME = "Resource.findByName";
    public static final String FIND_BY_NAME_CONTAINING = "Resource.findByNameContaining";
    public static final String FIND_BY_MEDIATYPE = "Resource.findByMediaType";
    public static final String FIND_BY_MEDIATYPE_CONTAINING = "Resource.findByMediaTypeContaining";
    public static final String FIND_BY_PATH = "Resource.findByPath";
    public static final String FIND_BY_PATH_CONTAINING = "Resource.findByPathContaining";
    public static final String FIND_BY_UID = "Resource.findByUid";
    public static final String FIND_BY_UID_CONTAINING = "Resource.findByUidContaining";
    private static final long serialVersionUID = -7222707205353947820L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "author", nullable = false, unique = false)
    private String author;

    @Column(name = "name", nullable = false, unique = false)
    private String name;

    @Column(name = "media_type", nullable = false, unique = false)
    private String mediaType;

    @Column(name = "path", length = 512, nullable = false, unique = false)
    private String path;

    @Column(name = "uid", length = 255, nullable = false, unique = false)
    private String uid;

    @Column(name = "uri", length = 255, nullable = false, unique = false)
    private String uri;

    @Column(name = "resource_tenant_id", nullable = false)
    private String resourceTenantId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    private Resource resource;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "resource")
    private Set<Resource> resources = new HashSet<Resource>(0);

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "resource")
    private Set<Revision> revisions = new HashSet<Revision>(0);

}
