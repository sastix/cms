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
import java.util.Date;

@Entity
@Table(name = "revision")
@NamedQueries({
        @NamedQuery(name = "Revision.findAll", query = "SELECT revision FROM Revision revision")
        , @NamedQuery(name = "Revision.findByTitle", query = "SELECT revision FROM Revision revision WHERE revision.title = :title")
        , @NamedQuery(name = "Revision.findByTitleContaining", query = "SELECT revision FROM Revision revision WHERE revision.title like :title")
        , @NamedQuery(name = "Revision.findByCreatedAt", query = "SELECT revision FROM Revision revision WHERE revision.createdAt = :createdAt")
        , @NamedQuery(name = "Revision.findByUpdatedAt", query = "SELECT revision FROM Revision revision WHERE revision.updatedAt = :updatedAt")
        , @NamedQuery(name = "Revision.findByDeletedAt", query = "SELECT revision FROM Revision revision WHERE revision.deletedAt = :deletedAt")
})
@Getter @Setter @NoArgsConstructor
public class Revision implements Serializable {


    public static final String FIND_ALL = "Revision.findAll";
    public static final String FIND_BY_TITLE = "Revision.findByTitle";
    public static final String FIND_BY_TITLE_CONTAINING = "Revision.findByTitleContaining";
    public static final String FIND_BY_CREATEDAT = "Revision.findByCreatedAt";
    public static final String FIND_BY_UPDATEDAT = "Revision.findByUpdatedAt";
    public static final String FIND_BY_DELETEDAT = "Revision.findByDeletedAt";
    private static final long serialVersionUID = 805162517678101376L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "title", length = 45, nullable = true, unique = false)
    private String title;

    @Temporal(TemporalType.DATE)
    @Column(name = "created_at", length = 0)
    private Date createdAt;

    @Temporal(TemporalType.DATE)
    @Column(name = "updated_at", length = 0)
    private Date updatedAt;

    @Temporal(TemporalType.DATE)
    @Column(name = "deleted_at", length = 0)
    private Date deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archived_resource_id", nullable = true)
    private Resource archivedResource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_resource_id", nullable = false)
    private Resource parentResource;

}
