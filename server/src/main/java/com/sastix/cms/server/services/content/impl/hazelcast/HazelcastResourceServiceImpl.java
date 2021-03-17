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

package com.sastix.cms.server.services.content.impl.hazelcast;

import com.sastix.cms.common.Constants;
import com.sastix.cms.common.cache.CacheDTO;
import com.sastix.cms.common.cache.QueryCacheDTO;
import com.sastix.cms.common.cache.RemoveCacheDTO;
import com.sastix.cms.common.cache.exceptions.DataNotFound;
import com.sastix.cms.common.content.*;
import com.sastix.cms.common.content.exceptions.*;
import com.sastix.cms.common.lock.LockDTO;
import com.sastix.cms.common.lock.NewLockDTO;
import com.sastix.cms.common.lock.QueryLockDTO;
import com.sastix.cms.common.lock.exceptions.LockNotAllowed;
import com.sastix.cms.common.lock.exceptions.LockNotHeld;
import com.sastix.cms.server.domain.entities.Resource;
import com.sastix.cms.server.domain.entities.Revision;
import com.sastix.cms.server.domain.repositories.ResourceRepository;
import com.sastix.cms.server.domain.repositories.RevisionRepository;
import com.sastix.cms.server.services.cache.CacheService;
import com.sastix.cms.server.services.content.*;
import com.sastix.cms.server.services.content.impl.CommonResourceServiceImpl;
import com.sastix.cms.server.services.lock.LockService;
import com.sastix.cms.server.utils.MultipartFileSender;
import org.apache.tika.Tika;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
@Profile("production")
public class HazelcastResourceServiceImpl implements ResourceService {

    private Logger LOG = (Logger) LoggerFactory.getLogger(HazelcastResourceServiceImpl.class);

    @Autowired
    LockService lockService;

    @Autowired
    CacheService cacheService;

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

    @Autowired
    GeneralFileHandlerService generalFileHandlerService;

    @Autowired
    TenantService tenantService;

    @Autowired
    ZipHandlerService zipHandlerService;

    @Autowired
    DistributedCacheService distributedCacheService;

    private final Tika tika = new Tika();

    @Override
    @Transactional(readOnly=true)
    public LockedResourceDTO lockResource(ResourceDTO resourceDTO) throws ResourceNotFound, ResourceNotOwned, ResourceAccessError {
        final Revision latestRevision = crs.getLatestRevision(resourceDTO.getResourceUID());
        if (latestRevision == null) {
            throw new ResourceNotFound("The supplied resource UID[" + resourceDTO.getResourceUID() + "] does not exist.");
        }
        final LockDTO lockDTO;
        try {
            lockDTO = lockService.lockResource(new NewLockDTO(resourceDTO.getResourceUID(), resourceDTO.getAuthor()));
        } catch (final Exception e) {
            if (e instanceof LockNotAllowed) {
                if (!e.getMessage().contains("ExpirationTime")) {
                    throw new ResourceAccessError(e.getMessage());
                } else {
                    throw new ResourceNotOwned(e.getMessage());
                }
            } else {
                throw new GeneralResourceException(e.getMessage());
            }
        }

        final LockedResourceDTO lockedResourceDTO = new LockedResourceDTO();
        lockedResourceDTO.setLockID(lockDTO.getLockID());
        lockedResourceDTO.setLockExpirationDate(lockDTO.getLockExpiration());
        //lockedResourceDTO.setResourceName(); //TODO: does not exist in ResourceDTO
        lockedResourceDTO.setResourceUID(resourceDTO.getResourceUID()); //inheritance
        lockedResourceDTO.setResourcesList(resourceDTO.getResourcesList());//inheritance
        lockedResourceDTO.setResourceURI(resourceDTO.getResourceURI());//inheritance
        lockedResourceDTO.setAuthor(resourceDTO.getAuthor());//inheritance

        return lockedResourceDTO;
    }

    @Override
    @Transactional(readOnly=true)
    public void unlockResource(LockedResourceDTO lockedResourceDTO) throws ResourceNotFound, ResourceNotOwned {
        final Revision latestRevision = crs.getLatestRevision(lockedResourceDTO.getResourceUID());
        if (latestRevision == null) {
            throw new ResourceNotFound("The supplied resource UID[" + lockedResourceDTO.getResourceUID() + "] does not exist.");
        }

        try {
            lockService.unlockResource(new LockDTO(lockedResourceDTO.getResourceUID(), lockedResourceDTO.getLockID(), lockedResourceDTO.getAuthor(), lockedResourceDTO.getLockExpirationDate()));
        } catch (Exception e) {
            if (e instanceof LockNotHeld) {
                throw new ResourceNotOwned(e.getMessage());
            } else {
                throw new GeneralResourceException(e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly=true)
    public LockedResourceDTO renewResourceLock(LockedResourceDTO lockedResourceDTO) throws ResourceNotFound, ResourceNotOwned {
        final Revision latestRevision = crs.getLatestRevision(lockedResourceDTO.getResourceUID());
        if (latestRevision == null) {
            throw new ResourceNotFound("The supplied resource UID[" + lockedResourceDTO.getResourceUID() + "] does not exist.");
        }
        LockDTO lockDTO;
        try {
            lockDTO = lockService.renewResourceLock(new LockDTO(lockedResourceDTO.getResourceUID(), lockedResourceDTO.getLockID(), lockedResourceDTO.getAuthor(), lockedResourceDTO.getLockExpirationDate()));
        } catch (Exception e) {
            if (e instanceof LockNotHeld) {
                throw new ResourceNotOwned(e.getMessage());
            } else {
                throw new GeneralResourceException(e.getMessage());
            }
        }

        lockedResourceDTO.setLockID(lockDTO.getLockID());
        lockedResourceDTO.setAuthor(lockDTO.getLockOwner());
        lockedResourceDTO.setLockExpirationDate(lockDTO.getLockExpiration());
        return lockedResourceDTO;
    }

    @Override
    @Transactional
    public ResourceDTO createResource(CreateResourceDTO createResourceDTO) throws ResourceAccessError {
        // Save Resource
        final Resource resource = insertResource(createResourceDTO);
        // Cache Resource
        distributedCacheService.cacheIt(resource.getUri(), resource.getResourceTenantId());

        // Check if is zip file
        if (resource.getMediaType().endsWith("zip")) {
            return zipHandlerService.handleZip(resource);
        }

        return crs.convertToDTO(resource);
    }

    @Override
    @Transactional
    public LockedResourceDTO updateResource(UpdateResourceDTO updateResourceDTO) throws ResourceNotOwned, ResourceAccessError {
        final Revision latestRevision = crs.getLatestRevision(updateResourceDTO.getResourceUID());
        if (latestRevision == null) {
            throw new ResourceNotFound("The supplied resource UID[" + updateResourceDTO.getResourceUID() + "] does not exist.");
        }

        LockDTO lockDTO = lockService.queryResourceLock(new QueryLockDTO(updateResourceDTO.getResourceUID()));
        if (lockDTO != null) {
            //Check author
            if (!lockDTO.getLockID().equals(updateResourceDTO.getLockID())) {
                throw new ResourceNotOwned("The supplied resource UID[" + updateResourceDTO.getResourceUID() + "] cannot be modified, the lock is held by someone else already."
                        + " Author: " + lockDTO.getLockOwner()
                        + " ExpirationTime: " + lockDTO.getLockExpiration());
            } else {
                if (lockDTO.getLockExpiration().isBeforeNow()
                        || !lockDTO.getLockOwner().equals(updateResourceDTO.getAuthor())) {
                    throw new ResourceNotOwned("The supplied resource UID[" + updateResourceDTO.getResourceUID() + "] cannot be modified, it is not locked by this owner or has already expired."
                            + " Author: " + lockDTO.getLockOwner()
                            + " ExpirationTime: " + lockDTO.getLockExpiration());
                }
            }
        } else {
            throw new ResourceNotOwned("The supplied resource UID[" + updateResourceDTO.getResourceUID() + "] cannot be modified, there is no active lock.");
        }

        final ResourceDTO resourceDTO = new ResourceDTO();
        // Get saved resource
        // check if resource has been deleted
        if (latestRevision.getDeletedAt() != null) {
            throw new ResourceNotFound("The supplied resource UID[" + updateResourceDTO.getResourceUID() + "] does not exist. It has been deleted");
        } else {
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
            final String dcuid;
            try {
                dcuid = cacheService.getUID(Constants.UID_REGION);
            } catch (Exception e) {
                throw new ResourceAccessError(e.getMessage());
            }
            //Unique URI: DCUID-tenantID/relativePath
            final String context = dcuid + "-" + persistedResource.getResourceTenantId() + "/";

            //Check if Tenant Checksum file exists
            try {
                tenantService.createTenantChecksum(persistedResource.getResourceTenantId());
            } catch (Exception e) {
                throw new ResourceAccessError(e.getMessage());
            }

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

            //update the cache
            try {
                distributedCacheService.updateCache(persistedResource.getUri(), persistedResource.getResourceTenantId());
            }catch (DataNotFound e){
                //eat it and warn
                LOG.warn("DataNotFound in cache for uri:{}, uid:{} "+persistedResource.getUri(),persistedResource.getUid());
            }
        }


        final LockedResourceDTO lockedResourceDTO = new LockedResourceDTO();
        lockedResourceDTO.setLockID(lockDTO.getLockID());
        lockedResourceDTO.setLockExpirationDate(lockDTO.getLockExpiration());
        lockedResourceDTO.setResourceName(updateResourceDTO.getResourceName());
        lockedResourceDTO.setResourceUID(resourceDTO.getResourceUID()); //inheritance
        lockedResourceDTO.setResourcesList(resourceDTO.getResourcesList());//inheritance
        lockedResourceDTO.setResourceURI(resourceDTO.getResourceURI());//inheritance
        lockedResourceDTO.setAuthor(resourceDTO.getAuthor());//inheritance
        return lockedResourceDTO;
    }

    @Override
    @Transactional(readOnly=true)
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
    @Transactional
    public ResourceDTO deleteResource(LockedResourceDTO lockedResourceDTO) throws ResourceNotOwned, ResourceAccessError {
        final Revision latestRevision = crs.getLatestRevision(lockedResourceDTO.getResourceUID());
        if (latestRevision == null) {
            throw new ResourceAccessError("The supplied resource UID[" + lockedResourceDTO.getResourceUID() + "] does not exist.");
        }

        LockDTO lockDTO = lockService.queryResourceLock(new QueryLockDTO(lockedResourceDTO.getResourceUID()));
        if (lockDTO != null) {
            //UID already Locked
            //Check author
            if (!lockDTO.getLockID().equals(lockedResourceDTO.getLockID())) {
                throw new ResourceNotOwned("The supplied resource UID[" + lockedResourceDTO.getResourceUID() + "] cannot be modified, the lock is held by someone else already."
                        + " Author: " + lockDTO.getLockOwner()
                        + " ExpirationTime: " + lockDTO.getLockExpiration());
            } else {
                if (lockDTO.getLockExpiration().isBeforeNow()
                        || !lockDTO.getLockOwner().equals(lockedResourceDTO.getAuthor())) {
                    throw new ResourceNotOwned("The supplied resource UID[" + lockedResourceDTO.getResourceUID() + "] cannot be modified, it is not locked by this owner or has already expired."
                            + " Author: " + lockDTO.getLockOwner()
                            + " ExpirationTime: " + lockDTO.getLockExpiration());
                }
            }
        } else {
            throw new ResourceNotOwned("The supplied resource UID[" + lockedResourceDTO.getResourceUID() + "] cannot be modified, there is no active lock.");
        }

        Revision newRevision = new Revision();
        newRevision.setCreatedAt(latestRevision.getCreatedAt());
        newRevision.setUpdatedAt(latestRevision.getUpdatedAt());
        newRevision.setDeletedAt(DateTime.now().toDate());
        newRevision.setTitle(DateTime.now().toString()); //TODO: what kind of title should we store? It is not described in specs
        newRevision.setResource(latestRevision.getResource());
        newRevision.setParentResource(latestRevision.getResource());
        revisionRepository.save(newRevision);

        //remove from cache
        RemoveCacheDTO removeCacheDTO = new RemoveCacheDTO(latestRevision.getResource().getUri());
        cacheService.removeCachedResource(removeCacheDTO);

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
    @Transactional(readOnly=true)
    public ResponseEntity<InputStreamResource> getResponseInputStream(String uuid) throws ResourceAccessError, IOException {
        // Find Resource
        final Resource resource = resourceRepository.findOneByUid(uuid);
        if (resource == null) {
            return null;
        }

        InputStream inputStream;
        int size;
        //check cache first
        QueryCacheDTO queryCacheDTO = new QueryCacheDTO(resource.getUri());
        CacheDTO cacheDTO = null;
        try {
            cacheDTO = cacheService.getCachedResource(queryCacheDTO);
        } catch (DataNotFound e) {
            LOG.warn("Resource '{}' is not cached.", resource.getUri());
        } catch (Exception e) {
            LOG.error("Exception: {}", e.getLocalizedMessage());
        }
        if (cacheDTO != null && cacheDTO.getCacheBlobBinary() != null) {
            LOG.info("Getting resource {} from cache", resource.getUri());
            inputStream = new ByteArrayInputStream(cacheDTO.getCacheBlobBinary());
            size = cacheDTO.getCacheBlobBinary().length;
        } else {
            try {
                final Path responseFile = hashedDirectoryService.getDataByURI(resource.getUri(), resource.getResourceTenantId());
                inputStream = Files.newInputStream(responseFile);
                size = (int) Files.size(responseFile);

                // Adding resource to cache
                distributedCacheService.cacheIt(resource.getUri(), resource.getResourceTenantId());
            } catch (IOException ex) {
                LOG.info("Error writing file to output stream. Filename was '{}'", resource.getUri(), ex);
                throw new RuntimeException("IOError writing file to output stream");
            } catch (URISyntaxException e) {
                e.printStackTrace();
                throw new RuntimeException("IOError writing file to output stream");
            }
        }

        return ResponseEntity.ok()
                .contentLength(size)
                .contentType(MediaType.parseMediaType(resource.getMediaType()))
                .body(new InputStreamResource(inputStream));
    }

    @Override
    public MultipartFileSender getMultipartFileSender(String uuid) throws ResourceAccessError, IOException {
        // Find Resource
        final Resource resource = resourceRepository.findOneByUid(uuid);
        if (resource == null) {
            return null;
        }

        final Path responseFile;
        try {
            responseFile = hashedDirectoryService.getDataByURI(resource.getUri(), resource.getResourceTenantId());
        } catch (URISyntaxException e) {
            throw new RuntimeException("IOError writing file to output stream");
        }

        return MultipartFileSender.fromPath(responseFile, resource.getMediaType());
    }

    @Override
    public Resource insertResource(final CreateResourceDTO createResourceDTO) {
        final String dcuid;
        try {
            dcuid = cacheService.getUID(Constants.UID_REGION);
        } catch (Exception e) {
            throw new ResourceAccessError(e.getMessage());
        }

        //Unique URI: DCUID-tenantID/relativePath
        final String context = dcuid + "-" + createResourceDTO.getResourceTenantId() + "/";

        //Check if Tenant Checksum file exists
        try {
            tenantService.createTenantChecksum(createResourceDTO.getResourceTenantId());
        } catch (Exception e) {
            throw new ResourceAccessError(e.getMessage());
        }


        final String uuri = context + createResourceDTO.getResourceName();

        //Create UID
        //UID: hash(URI)-tenantID
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

        return persistedResource;
    }

    @Override
    public Resource insertChildResource(CreateResourceDTO createResourceDTO, String parentContext, Resource parentResource) {
        //Unique URI: parentContext/relativePath
        final String uuri = parentContext + "/" + createResourceDTO.getResourceName();

        //Create UID
        //UID: hash(URI)-tenantID
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
        persistedResource.setResource(parentResource);
        persistedResource.setResourceTenantId(createResourceDTO.getResourceTenantId());
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

    @Override
    public String getParentResource(String uuid) throws ResourceNotFound{
        List<Resource> list = resourceRepository.findByUID(uuid);
        if(list == null || list.size()==0){
            throw new ResourceNotFound("The resuroce for uuid="+uuid+" could not be found");
        }
        return list.get(0).getResource().getUid();
    }

    @Override
    public byte[] getData(DataDTO dataDTO) throws ResourceAccessError, ContentValidationException, IOException {
        final Path responseFile = getDataPath(dataDTO);
        final byte[] responseData = Files.readAllBytes(responseFile);
        return responseData;
    }

}
