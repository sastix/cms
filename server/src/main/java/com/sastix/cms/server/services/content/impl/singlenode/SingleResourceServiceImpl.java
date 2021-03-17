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

package com.sastix.cms.server.services.content.impl.singlenode;

import com.sastix.cms.common.content.*;
import com.sastix.cms.common.content.exceptions.ContentValidationException;
import com.sastix.cms.common.content.exceptions.ResourceAccessError;
import com.sastix.cms.common.content.exceptions.ResourceNotFound;
import com.sastix.cms.common.content.exceptions.ResourceNotOwned;
import com.sastix.cms.server.domain.entities.Resource;
import com.sastix.cms.server.domain.entities.Revision;
import com.sastix.cms.server.domain.repositories.ResourceRepository;
import com.sastix.cms.server.domain.repositories.RevisionRepository;
import com.sastix.cms.server.services.content.HashedDirectoryService;
import com.sastix.cms.server.services.content.ResourceService;
import com.sastix.cms.server.services.content.ZipFileHandlerService;
import com.sastix.cms.server.services.content.impl.CommonResourceServiceImpl;
import com.sastix.cms.server.utils.MultipartFileSender;
import org.apache.tika.Tika;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile("single")
public class SingleResourceServiceImpl implements ResourceService {
    private Logger LOG = (Logger) LoggerFactory.getLogger(SingleResourceServiceImpl.class);

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    RevisionRepository revisionRepository;

    @Autowired
    CommonResourceServiceImpl crs;

    @Autowired
    HashedDirectoryService hashedDirectoryService;

    @Autowired
    ZipFileHandlerService zipFileHandlerService;

    private static final String LOCK_ID = "dummyMockedLockId";

    private final Tika tika = new Tika();

    /**
     * <UID, Author>
     */
    private ConcurrentHashMap<String, LockedResourceDTO> lockStatus = new ConcurrentHashMap<>();

    @Override
    public LockedResourceDTO lockResource(ResourceDTO resourceDTO) throws ResourceNotFound, ResourceNotOwned, ResourceAccessError {
        final Revision latestRevision = crs.getLatestRevision(resourceDTO.getResourceUID());
        if (latestRevision == null) {
            throw new ResourceNotFound("The supplied resource UID[" + resourceDTO.getResourceUID() + "] does not exist.");
        }

        if (lockStatus.containsKey(resourceDTO.getResourceUID())) {
            //UID already Locked
            final LockedResourceDTO lockedResourceDTO = lockStatus.get(resourceDTO.getResourceUID());
            //Check author
            if (lockedResourceDTO.getAuthor().equals(resourceDTO.getAuthor())) {
                throw new ResourceAccessError("The supplied resource UID cannot be modified, user "
                        + resourceDTO.getAuthor()
                        + " already has the lock and cannot lock more than once");
            } else {
                throw new ResourceNotOwned("The supplied resource UID cannot be modified, the lock is held by someone else already."
                        + " Author: " + lockedResourceDTO.getAuthor()
                        + " ExpirationTime: " + lockedResourceDTO.getLockExpirationDate());
            }
        }
        //TODO: Lock mechanism integration point: to be implemented
        LockedResourceDTO lockedResourceDTO = new LockedResourceDTO();
        lockedResourceDTO.setLockID(LOCK_ID + System.currentTimeMillis());
        lockedResourceDTO.setLockExpirationDate(DateTime.now().plusMinutes(30));
        //lockedResourceDTO.setResourceName(); //TODO: does not exist in ResourceDTO
        lockedResourceDTO.setResourceUID(resourceDTO.getResourceUID()); //inheritance
        lockedResourceDTO.setResourcesList(resourceDTO.getResourcesList());//inheritance
        lockedResourceDTO.setResourceURI(resourceDTO.getResourceURI());//inheritance
        lockedResourceDTO.setAuthor(resourceDTO.getAuthor());//inheritance

        lockStatus.put(resourceDTO.getResourceUID(), lockedResourceDTO);
        return lockedResourceDTO;
    }

    @Override
    public void unlockResource(LockedResourceDTO lockedResourceDTO) throws ResourceNotFound, ResourceNotOwned {
        final Revision latestRevision = crs.getLatestRevision(lockedResourceDTO.getResourceUID());
        if (latestRevision == null) {
            throw new ResourceNotFound("The supplied resource UID[" + lockedResourceDTO.getResourceUID() + "] does not exist.");
        }

        if (lockStatus.containsKey(lockedResourceDTO.getResourceUID())) {
            //UID already Locked
            final LockedResourceDTO cachedLockedResourceDTO = lockStatus.get(lockedResourceDTO.getResourceUID());
            //Check author
            if (!cachedLockedResourceDTO.getAuthor().equals(lockedResourceDTO.getAuthor())) {
                throw new ResourceNotOwned("The supplied resource UID cannot be unlocked, the lock is held by someone else already."
                        + " Author: " + cachedLockedResourceDTO.getAuthor()
                        + " ExpirationTime: " + lockedResourceDTO.getLockExpirationDate());
            }
        } else {
            throw new RestClientException("Lock does not exist");
        }

        lockStatus.remove(lockedResourceDTO.getResourceUID());
    }

    @Override
    public LockedResourceDTO renewResourceLock(LockedResourceDTO lockedResourceDTO) throws ResourceNotFound, ResourceNotOwned {
        final Revision latestRevision = crs.getLatestRevision(lockedResourceDTO.getResourceUID());
        if (latestRevision == null) {
            throw new ResourceNotFound("The supplied resource UID[" + lockedResourceDTO.getResourceUID() + "] does not exist.");
        }

        if (lockStatus.containsKey(lockedResourceDTO.getResourceUID())) {
            //UID already Locked
            final LockedResourceDTO cachedLockedResourceDTO = lockStatus.get(lockedResourceDTO.getResourceUID());
            //Check author
            if (!cachedLockedResourceDTO.getAuthor().equals(lockedResourceDTO.getAuthor())) {
                throw new ResourceNotOwned("The supplied resource UID cannot be unlocked, the lock is held by someone else already."
                        + " Author: " + cachedLockedResourceDTO.getAuthor()
                        + " ExpirationTime: " + lockedResourceDTO.getLockExpirationDate());
            }
        } else {
            throw new RestClientException("Lock does not exist");
        }

        LockedResourceDTO savedLockedResourceDTO = lockStatus.get(lockedResourceDTO.getResourceUID());
        savedLockedResourceDTO.setLockExpirationDate(DateTime.now().plusMinutes(30));
        return savedLockedResourceDTO;
    }

    @Override
    public ResourceDTO createResource(CreateResourceDTO createResourceDTO) throws ResourceAccessError {
        /**
         * Generate a UUID for the resource to be created
         * In single node we are not using the Hazelcast IdGenerator
         * */
        String uuid = UUID.randomUUID().toString();

        //Unique URI -- DCUID-tenantID/relativePath
        final String context = uuid + "-" + createResourceDTO.getResourceTenantId() + "/";
        final String uuri = context + createResourceDTO.getResourceName();
        //Create UID
        final String UID = hashedDirectoryService.hashText(uuri) + "-" + createResourceDTO.getResourceTenantId();
        //Get Data
        byte[] binary = createResourceDTO.getResourceBinary();
        String binaryURI = createResourceDTO.getResourceExternalURI();

        //Get RelativePath
        String relativePath = crs.getRelativePath(uuri, createResourceDTO.getResourceTenantId(), binary, binaryURI);

        // Save resource
        Resource persistedResource = new Resource();
        persistedResource.setMediaType(createResourceDTO.getResourceMediaType());
        persistedResource.setAuthor(createResourceDTO.getResourceAuthor());
        persistedResource.setPath(relativePath);
        persistedResource.setName(createResourceDTO.getResourceName());
        persistedResource.setUid(UID);
        persistedResource.setUri(uuri);
        persistedResource.setResourceTenantId(createResourceDTO.getResourceTenantId());
        persistedResource = resourceRepository.save(persistedResource);

        // Save revision
        Revision persistedRevision = new Revision();
        persistedRevision.setCreatedAt(new Date());
        persistedRevision.setTitle(DateTime.now().toString()); //TODO: what kind of title should we store? It is not described in specs
        persistedRevision.setResource(persistedResource);
        persistedRevision.setParentResource(persistedResource);
        revisionRepository.save(persistedRevision);

        //integrate zip handler
        CommonResourceServiceImpl.AnalyzedZipResource analyzedZipResource = crs.analyzeZipFile(context, persistedResource.getUri(), persistedResource);
        List<ResourceDTO> resourceDTOList = analyzedZipResource.getResourceDTOs();
        ResourceDTO ret = crs.convertToDTO(persistedResource);
        ret.setResourcesList(resourceDTOList);
        return ret;
    }

    @Override
    public LockedResourceDTO updateResource(UpdateResourceDTO updateResourceDTO) throws ResourceNotOwned, ResourceAccessError {
        final Revision latestRevision = crs.getLatestRevision(updateResourceDTO.getResourceUID());
        if (latestRevision == null) {
            throw new ResourceNotFound("The supplied resource UID[" + updateResourceDTO.getResourceUID() + "] does not exist.");
        }

        if (lockStatus.containsKey(updateResourceDTO.getResourceUID())) {
            //UID already Locked
            final LockedResourceDTO lockedResourceDTO = lockStatus.get(updateResourceDTO.getResourceUID());
            //Check author
            if (!lockedResourceDTO.getLockID().equals(updateResourceDTO.getLockID())) {
                throw new ResourceNotOwned("The supplied resource UID cannot be modified, the lock is held by someone else already."
                        + " Author: " + lockedResourceDTO.getAuthor()
                        + " ExpirationTime: " + lockedResourceDTO.getLockExpirationDate());
            } else {
                if (lockedResourceDTO.getLockExpirationDate().isBeforeNow()
                        || !lockedResourceDTO.getAuthor().equals(updateResourceDTO.getAuthor())) {
                    throw new ResourceNotOwned("The supplied resource UID cannot be modified, it is not locked by this owner or has already expired."
                            + " Author: " + lockedResourceDTO.getAuthor()
                            + " ExpirationTime: " + lockedResourceDTO.getLockExpirationDate());
                }
            }
        } else {
            throw new ResourceNotOwned("The supplied resource UID cannot be modified, there is no active lock.");
        }

        final ResourceDTO resourceDTO = new ResourceDTO();
        // Get saved resource
        // check if resource has been deleted
        if(latestRevision.getDeletedAt()!=null) {
            throw new ResourceNotFound("The supplied resource UID[" + updateResourceDTO.getResourceUID() + "] does not exist. It has been deleted");
        }else{
            Resource persistedResource = resourceRepository.findByUidOrderByIdAsc(updateResourceDTO.getResourceUID(), PageRequest.of(0, 1)).get(0);
            Resource archivedResource = crs.cloneResource(persistedResource);
            byte[] binaryForUpdate = updateResourceDTO.getResourceBinary();

            /**
             * Archive the resource to be replaced
             *
             * */
            byte[] archivedData;
            try {
                archivedData = hashedDirectoryService.getBytesByURI(persistedResource.getUri(), persistedResource.getResourceTenantId());
            } catch (IOException e) {
                throw new ResourceNotFound("The supplied resource UID[" + updateResourceDTO.getResourceUID() + "] does not exist.");
            }
            String dcuid = UUID.randomUUID().toString();

            //Unique URI: DCUID-tenantID/relativePath
            final String context = dcuid + "-" + persistedResource.getResourceTenantId() + "/";
            //TODO: Check if tenant exists in db.
            final String uuri = context + persistedResource.getPath();

            //Create UID
            //UID: hash(URI)-tenantID
            final String UID = hashedDirectoryService.hashText(uuri) + "-" + persistedResource.getResourceTenantId();

            String relativePath = crs.getRelativePath(UID, persistedResource.getResourceTenantId(), archivedData, null);
            archivedResource.setPath(relativePath);
            archivedResource.setUid(UID);
            //insert archived entry
            Resource insertedArchivedResource = resourceRepository.save(archivedResource);


            /**
             * keep the same uid and update the existing resource
             *
             * */

            String binaryURIForUpdate = updateResourceDTO.getResourceExternalURI();

            String relativePathUpdatedData = crs.updateLocalFile(persistedResource.getUri(), persistedResource.getResourceTenantId(), binaryForUpdate, binaryURIForUpdate);
            persistedResource.setPath(relativePathUpdatedData);//not really needed since it will remain the path. Just keeping it for assertion

            try {
                persistedResource.setMediaType(tika.detect(hashedDirectoryService.getBytesByURI(persistedResource.getUri(), persistedResource.getResourceTenantId())));
            } catch (IOException e) {
                throw new ResourceNotFound("The supplied resource UID[" + persistedResource.getUid() + "] does not exist.");
            }

            if (updateResourceDTO.getAuthor() != null) {
                persistedResource.setAuthor(updateResourceDTO.getResourceAuthor());
            }

            if (updateResourceDTO.getResourceName() != null) {
                persistedResource.setName(updateResourceDTO.getResourceName());
            }

            Resource updatedResource = resourceRepository.save(persistedResource);

            //insert revision for update operation
            Revision updateRevision = new Revision();
            updateRevision.setCreatedAt(latestRevision.getCreatedAt());
            updateRevision.setUpdatedAt(DateTime.now().toDate());
            updateRevision.setTitle(DateTime.now().toString());
            updateRevision.setResource(latestRevision.getResource());
            updateRevision.setParentResource(latestRevision.getParentResource());
            revisionRepository.save(updateRevision);

            //archive revision
            latestRevision.setUpdatedAt(DateTime.now().toDate());
            latestRevision.setArchivedResource(archivedResource);
            revisionRepository.save(latestRevision);

            resourceDTO.setAuthor(updatedResource.getAuthor());
            resourceDTO.setResourceUID(updatedResource.getUid());
            resourceDTO.setResourceURI(updatedResource.getUri());
            Set<Resource> resources = updatedResource.getResources();
            if (resources != null && resources.size() > 0) {
                List<ResourceDTO> resourceList = new ArrayList<>();
                for (Resource resourceItem : resources) {
                    resourceList.add(crs.createDTO(resourceItem));
                }
                resourceDTO.setResourcesList(resourceList);
            }
        }

        LockedResourceDTO ldto=lockStatus.get(resourceDTO.getResourceUID());
        final LockedResourceDTO lockedResourceDTO = new LockedResourceDTO();
        lockedResourceDTO.setLockID(ldto.getLockID());
        lockedResourceDTO.setLockExpirationDate(ldto.getLockExpirationDate());
        lockedResourceDTO.setResourceName(updateResourceDTO.getResourceName());
        lockedResourceDTO.setResourceUID(resourceDTO.getResourceUID()); //inheritance
        lockedResourceDTO.setResourcesList(resourceDTO.getResourcesList());//inheritance
        lockedResourceDTO.setResourceURI(resourceDTO.getResourceURI());//inheritance
        lockedResourceDTO.setAuthor(resourceDTO.getAuthor());//inheritance
        return lockedResourceDTO;
    }

    @Override
    public ResourceDTO queryResource(ResourceQueryDTO resourceQueryDTO) throws ResourceAccessError, ResourceNotFound {
        //find a non deleted resource
        Revision latestRevision = crs.getLatestRevision(resourceQueryDTO.getQueryUID());
        if (latestRevision != null) {
            boolean isNotDeleted = latestRevision.getDeletedAt() == null;
            if (isNotDeleted) {
                return crs.convertToDTO(latestRevision.getResource());
            } else {
                throw new ResourceNotFound("The supplied resource UID [" + resourceQueryDTO.getQueryUID() + "] does not exist.");
            }
        } else {
            throw new ResourceAccessError("The supplied resource data are invalid and the resource cannot be retrieved.");
        }
    }

    @Override
    public ResourceDTO deleteResource(LockedResourceDTO lockedResourceDTO) throws ResourceNotOwned, ResourceAccessError {
        final Revision latestRevision = crs.getLatestRevision(lockedResourceDTO.getResourceUID());
        if (latestRevision == null) {
            throw new ResourceAccessError("The supplied resource UID [" + lockedResourceDTO.getResourceUID() + "] does not exist.");
        }

        if (lockStatus.containsKey(lockedResourceDTO.getResourceUID())) {
            //UID already Locked
            final LockedResourceDTO mapLockedResourceDTO = lockStatus.get(lockedResourceDTO.getResourceUID());
            //Check author
            if (!mapLockedResourceDTO.getLockID().equals(lockedResourceDTO.getLockID())) {
                throw new ResourceNotOwned("The supplied resource UID cannot be modified, the lock is held by someone else already."
                        + " Author: " + mapLockedResourceDTO.getAuthor()
                        + " ExpirationTime: " + mapLockedResourceDTO.getLockExpirationDate());
            } else {
                if (mapLockedResourceDTO.getLockExpirationDate().isBeforeNow()
                        || !mapLockedResourceDTO.getAuthor().equals(lockedResourceDTO.getAuthor())) {
                    throw new ResourceNotOwned("The supplied resource UID cannot be modified, it is not locked by this owner or has already expired."
                            + " Author: " + mapLockedResourceDTO.getAuthor()
                            + " ExpirationTime: " + mapLockedResourceDTO.getLockExpirationDate());
                }
            }
        } else {
            throw new ResourceNotOwned("The supplied resource UID cannot be modified, there is no active lock.");
        }

        Revision newRevision = new Revision();
        newRevision.setCreatedAt(latestRevision.getCreatedAt());
        newRevision.setUpdatedAt(latestRevision.getUpdatedAt());
        newRevision.setDeletedAt(DateTime.now().toDate());
        newRevision.setTitle(DateTime.now().toString()); //TODO: what kind of title should we store? It is not described in specs
        newRevision.setResource(latestRevision.getResource());
        newRevision.setParentResource(latestRevision.getResource());
        revisionRepository.save(newRevision);

        return crs.convertToDTO(latestRevision.getResource());
    }

    @Override
    public Path getDataPath(DataDTO dataDTO) throws ResourceAccessError {
        try {
            if (dataDTO.getResourceUID() != null && !dataDTO.getResourceUID().isEmpty()) {
                return hashedDirectoryService.getDataByUID(dataDTO.getResourceUID().substring(0, dataDTO.getResourceUID().lastIndexOf("-")), dataDTO.getTenantID());
            } else if (dataDTO.getResourceURI() != null && !dataDTO.getResourceURI().isEmpty()) {
                return hashedDirectoryService.getDataByURI(dataDTO.getResourceURI(), dataDTO.getTenantID());
            } else {
                throw new ContentValidationException("Field errors: resourceUID OR resourceURI may not be null");
            }
        } catch (Exception e) {
            if (e instanceof ContentValidationException) {
                throw (ContentValidationException) e;
            }
            throw new ResourceAccessError(e.toString());
        }
    }

    @Override
    public ResponseEntity<InputStreamResource> getResponseInputStream(String uuid) throws ResourceAccessError, IOException {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public Resource insertResource(CreateResourceDTO createResourceDTO) {
        return null;
    }

    @Override
    public Resource insertChildResource(CreateResourceDTO createResourceDTO, String parentUid, Resource parentResource) {
        return null;
    }

    @Override
    public MultipartFileSender getMultipartFileSender(String uuid) throws ResourceAccessError, IOException {
        return null;
    }

    @Override
    public String getParentResource(String uuid) {
        return null;
    }

    @Override
    public byte[] getData(DataDTO dataDTO) throws ResourceAccessError, ContentValidationException, IOException {
        final Path responseFile = getDataPath(dataDTO);
        final byte[] responseData = Files.readAllBytes(responseFile);
        return responseData;
    }
}
