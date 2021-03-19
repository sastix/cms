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

package com.sastix.cms.server.services.content.impl;

import com.sastix.cms.common.content.ResourceDTO;
import com.sastix.cms.common.content.exceptions.ContentValidationException;
import com.sastix.cms.common.content.exceptions.ResourceAccessError;
import com.sastix.cms.server.dataobjects.DataMaps;
import com.sastix.cms.server.domain.entities.Resource;
import com.sastix.cms.server.domain.entities.Revision;
import com.sastix.cms.server.domain.repositories.ResourceRepository;
import com.sastix.cms.server.domain.repositories.RevisionRepository;
import com.sastix.cms.server.services.content.HashedDirectoryService;
import com.sastix.cms.server.services.content.ZipFileHandlerService;
import org.apache.tika.Tika;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Service
public class CommonResourceServiceImpl {
    @Autowired
    RevisionRepository revisionRepository;

    @Autowired
    HashedDirectoryService hashedDirectoryService;

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    ZipFileHandlerService zipFileHandlerService;

    private final Tika tika = new Tika();

    //TODO: is there a better way for this?
    public Resource cloneResource(Resource r) {
        Resource resource = new Resource();
        resource.setName(r.getName());
        resource.setAuthor(r.getAuthor());
        resource.setMediaType(r.getMediaType());
        resource.setPath(r.getPath());
        resource.setResource(r.getResource());
        resource.setUri(r.getUri());
        //resource.setResources(r.getResources()); //TODO: check org.hibernate.HibernateException: Found shared references to a collection: com.sastix.cms.server.domain.Resource.resources
        resource.setResourceTenantId(r.getResourceTenantId());
        resource.setUid(r.getUid());
        return resource;
    }

    public Revision getLatestRevision(String uid){
        final List<Revision> revisions = revisionRepository.findRevisions(uid, PageRequest.of(0, 1));
        return revisions.isEmpty()?null:revisions.get(0);
    }

    public ResourceDTO convertToDTO(Resource resource) {
        ResourceDTO resourceDTO = null;
        if (resource != null) {
            resourceDTO = new ResourceDTO();
            resourceDTO.setAuthor(resource.getAuthor());
            resourceDTO.setResourceUID(resource.getUid());
            resourceDTO.setResourceURI(resource.getUri());
            Set<Resource> resources = resource.getResources();
            if (resources != null && resources.size() > 0) {
                List<ResourceDTO> resourceList = new ArrayList<>();
                for (Resource resourceItem : resources) {
                    resourceList.add(createDTO(resourceItem));
                }
                resourceDTO.setResourcesList(resourceList);
            }
        }
        return resourceDTO;
    }

    public ResourceDTO createDTO(Resource resource) {
        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setAuthor(resource.getAuthor());
        resourceDTO.setResourceUID(resource.getUid());
        resourceDTO.setResourceURI(resource.getUri());
        return resourceDTO;
    }

    public String getRelativePath(final String uuri, final String tenantID, final byte[] binary, final String binaryURI) throws ContentValidationException, ResourceAccessError {
        //save resource in volume
        String relativePath;
        try {
            if (binary != null && !StringUtils.isEmpty(binaryURI)) {
                throw new ContentValidationException("Field errors: resourceBinary OR resourceExternalURI should be null");
            } else if (binary != null && binary.length > 0) {
                relativePath = hashedDirectoryService.storeFile(uuri, tenantID, binary);
            } else if (!StringUtils.isEmpty(binaryURI)) {
                relativePath = hashedDirectoryService.storeFile(uuri, tenantID, binaryURI);
            } else {
                throw new ContentValidationException("Field errors: resourceBinary OR resourceExternalURI may not be null");
            }
        } catch (Exception e) {
            if (e instanceof ContentValidationException) {
                throw (ContentValidationException) e;
            }
            throw new ResourceAccessError(e.toString());
        }
        return relativePath;
    }

    public String updateLocalFile(final String uuri, final String tenantID, final byte[] binary, final String binaryURI) throws ContentValidationException, ResourceAccessError {
        //save resource in volume
        String relativePath;
        try {
            if (binary != null && !StringUtils.isEmpty(binaryURI)) {
                throw new ContentValidationException("Field errors: resourceBinary OR resourceExternalURI should be null");
            } else if (binary != null && binary.length > 0) {
                relativePath = hashedDirectoryService.replaceFile(uuri, tenantID, binary);
            } else if (!StringUtils.isEmpty(binaryURI)) {
                relativePath = hashedDirectoryService.replaceFile(uuri, tenantID, binaryURI);
            } else {
                throw new ContentValidationException("Field errors: resourceBinary OR resourceExternalURI may not be null");
            }
        } catch (Exception e) {
            if (e instanceof ContentValidationException) {
                throw (ContentValidationException) e;
            }
            throw new ResourceAccessError(e.toString());
        }
        return relativePath;
    }

    public AnalyzedZipResource analyzeZipFile(String context, String resourceURI, Resource zipResource) {
        byte[] insertedBytes;
        DataMaps modifiedDataMaps = null;
        List<ResourceDTO> resourceDTOList = new ArrayList<>();
        String startPage = null;
        String scormLandingPage = null;
        Resource parentResource = null;
        boolean isScormType=false;
        boolean isResourceWithStartPage = false;
        try {
            //get the zip bytes inserted
            insertedBytes = hashedDirectoryService.getBytesByURI(resourceURI, zipResource.getResourceTenantId());
            boolean isZipFile = zipFileHandlerService.isZipFile(insertedBytes);
            if (isZipFile) {
                //unzip it
                DataMaps dataMaps = zipFileHandlerService.unzip(insertedBytes);
                //is a scorm type
                isScormType= zipFileHandlerService.isScormType(dataMaps.getBytesMap());
                //is a cms resource with a startpage
                startPage = zipFileHandlerService.getResourceStartPage(dataMaps.getBytesMap());
                isResourceWithStartPage=!StringUtils.isEmpty(startPage);
                if (isScormType || isResourceWithStartPage) {
                    /**
                     * Keep uid in a map for each resource found in zip
                     * uid: context (uuid of the zip) + filename
                     * eg
                     * 1429362943604-02f29f2a-f8df-4aaf-ad83-e2e65ffa1910-0-zaq12345/css/style.css
                     * */
                    for (Map.Entry<String, byte[]> entry : dataMaps.getBytesMap().entrySet()) {
                        String filename = entry.getKey();

                        String thisResourceURI = context + filename;
                        //String extension = filename.substring(filename.lastIndexOf("."));
                        //String queryParams = filename.indexOf("?") > 0 ? "&" + filename.substring(filename.indexOf("?")) : "";
                        //String cpath = GET_DATA_DOMAIN + "?uid=" + uuid + extension + queryParams;
                        dataMaps.getUriMap().put(filename, thisResourceURI);
                        //dataMaps.getUidPathMap().put(filename, cpath);
                    }

                    /**
                     * Replace urls in resources
                     * */
                    //modifiedDataMaps = zipFileHandlerService.replaceRelativePaths(dataMaps);
                }

                //find parent
                String parentFilename = zipFileHandlerService.findParentResource(dataMaps.getBytesMap());
                if(isResourceWithStartPage){
                    parentFilename = startPage;
                }

                //first insert the parent file
                String parentUri = dataMaps.getUriMap().get(parentFilename);
                byte[] parentData = dataMaps.getBytesMap().get(parentFilename);
                parentResource = insertResource(parentUri, parentData, parentFilename, zipResource.getAuthor(), zipResource.getResourceTenantId(), zipResource);
//                resourceDTOList.add(this.convertToDTO(parentResource));

                //insert exploded resources
                for (Map.Entry<String, byte[]> entry : dataMaps.getBytesMap().entrySet()) {
                    String filename = entry.getKey();
                    if (!filename.equals(parentFilename)) {//we do not want to add the parent file twice
                        String uid = dataMaps.getUriMap().get(filename);
                        byte[] data = entry.getValue();
                        byte[] modifiedData = dataMaps.getBytesMap().get(filename);
                        //check if it is modified
                        if (modifiedData != null) {
                            data = modifiedData;
                        }

                        //insert
                        Resource resourceDTO = insertResource(uid, data, filename, zipResource.getAuthor(), zipResource.getResourceTenantId(), parentResource);
                        resourceDTOList.add(this.convertToDTO(resourceDTO));
                    }
                }

            }
        } catch (IOException e) {
            throw new ResourceAccessError(e.toString());
        }


        AnalyzedZipResource analyzedZipResource = new AnalyzedZipResource(resourceDTOList.isEmpty() ? null : resourceDTOList, startPage);
        analyzedZipResource.setParentID(parentResource != null ? parentResource.getId() : null);
        return analyzedZipResource;
    }

    private Resource insertResource(String uri, byte[] data, String filename, String author, String tenantId, Resource parentResource) {
        // Save resource
        Resource persistedResource = new Resource();
        String mediaType = tika.detect(data);
        persistedResource.setMediaType(mediaType == null ? "unknown" : mediaType);
        persistedResource.setAuthor(author);
        persistedResource.setUri(uri);

        //save resource in volume
        String relativePath;
        try {
            if (data != null && data.length > 0) {
                relativePath = hashedDirectoryService.storeFile(uri, tenantId, data);
            } else {
                throw new ContentValidationException("Field errors: resourceBinary may not be null");
            }
        } catch (Exception e) {
            if (e instanceof ContentValidationException) {
                throw (ContentValidationException) e;
            }
            throw new ResourceAccessError(e.toString());
        }

        persistedResource.setPath(relativePath);
        persistedResource.setName(filename);
        persistedResource.setUid(hashedDirectoryService.hashText(uri));
        persistedResource.setResourceTenantId(tenantId);
        persistedResource.setResource(parentResource);
        persistedResource = resourceRepository.save(persistedResource);
        // Save revision
        Revision persistedRevision = new Revision();
        persistedRevision.setCreatedAt(new Date());
        persistedRevision.setTitle(DateTime.now().toString()); //TODO: what kind of title should we store? It is not described in specs
        persistedRevision.setResource(persistedResource);
        persistedRevision.setParentResource(parentResource);
        revisionRepository.save(persistedRevision);
        return persistedResource;
    }

    public class AnalyzedZipResource{
        List<ResourceDTO> resourceDTOs;
        String startPage;
        Integer parentID;

        public AnalyzedZipResource(List<ResourceDTO> resourceDTOs, String startPage) {
            this.resourceDTOs = resourceDTOs;
            this.startPage = startPage;
        }

        public List<ResourceDTO> getResourceDTOs() {
            return resourceDTOs;
        }

        public void setResourceDTOs(List<ResourceDTO> resourceDTOs) {
            this.resourceDTOs = resourceDTOs;
        }

        public String getStartPage() {
            return startPage;
        }

        public void setStartPage(String startPage) {
            this.startPage = startPage;
        }

        public Integer getParentID() {
            return parentID;
        }

        public void setParentID(Integer parentID) {
            this.parentID = parentID;
        }
    }
}
