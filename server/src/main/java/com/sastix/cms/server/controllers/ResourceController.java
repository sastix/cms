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

package com.sastix.cms.server.controllers;

import com.sastix.cms.common.Constants;
import com.sastix.cms.common.content.*;
import com.sastix.cms.common.content.exceptions.ContentValidationException;
import com.sastix.cms.common.content.exceptions.ResourceAccessError;
import com.sastix.cms.common.content.exceptions.ResourceNotFound;
import com.sastix.cms.common.content.exceptions.ResourceNotOwned;
import com.sastix.cms.server.CmsServer;
import com.sastix.cms.server.domain.repositories.ResourceRepository;
import com.sastix.cms.server.services.content.HashedDirectoryService;
import com.sastix.cms.server.services.content.ResourceService;
import com.sastix.cms.server.utils.MultipartFileSender;
import com.sastix.cms.server.utils.ValidationHelper;
import org.apache.tika.Tika;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/" + CmsServer.CONTEXT)
public class ResourceController implements BeanFactoryAware {

    @Value("${cms.resource.service:singleResourceService}")
    private String cmsResourceName;

    @Value("${keycloak.enabled:false}")
    private boolean keycloakEnabled;

    private ResourceService resourceService;

    private final Tika tika = new Tika();

    @Autowired
    private ValidationHelper validationHelper;

    @Autowired
    HashedDirectoryService hashedDirectoryService;

    @Autowired
    ResourceRepository resourceRepository;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        try {
            resourceService = (ResourceService) beanFactory.getBean(this.cmsResourceName);
        } catch (final Exception e) {
            log.trace("Error in Resource Service {} not found", cmsResourceName);
            log.error("Exception Message: ", e);
        }
    }

    @RequestMapping(value = "/v" + Constants.REST_API_1_0 + "/" + Constants.LOCK_RESOURCE_DTO, method = RequestMethod.POST)
    @PreAuthorize("hasRole('admin') or #resourceDTO.author == authentication.principal.name")
    public LockedResourceDTO lockResource(@Valid @RequestBody ResourceDTO resourceDTO, BindingResult result) throws ContentValidationException, ResourceNotOwned, ResourceNotFound, ResourceAccessError {
        log.trace(Constants.LOCK_RESOURCE);
        validationHelper.validate(result);
        log.trace(resourceDTO.toString());
        LockedResourceDTO lockedResourceDTO = resourceService.lockResource(resourceDTO);
        log.trace(lockedResourceDTO.toString());
        return lockedResourceDTO;
    }

    @RequestMapping(value = "/v" + Constants.REST_API_1_0 + "/" + Constants.UNLOCK_RESOURCE_DTO, method = RequestMethod.POST)
    @PreAuthorize("hasRole('admin') or #lockedResourceDTO.author == authentication.principal.name")
    public void unlockResource(@Valid @RequestBody LockedResourceDTO lockedResourceDTO, BindingResult result) throws ContentValidationException, ResourceNotOwned, ResourceNotFound {
        log.trace(Constants.UNLOCK_RESOURCE);
        validationHelper.validate(result);
        resourceService.unlockResource(lockedResourceDTO);
    }

    @RequestMapping(value = "/v" + Constants.REST_API_1_0 + "/" + Constants.RENEW_RESOURCE_DTO_LOCK, method = RequestMethod.POST)
    @PreAuthorize("hasRole('admin') or #lockedResourceDTO.author == authentication.principal.name")
    public LockedResourceDTO renewResourceDtoLock(@Valid @RequestBody LockedResourceDTO lockedResourceDTO, BindingResult result) throws ContentValidationException, ResourceNotOwned, ResourceNotFound {
        log.trace(Constants.RENEW_RESOURCE_LOCK);
        validationHelper.validate(result);
        final LockedResourceDTO newLockedResourceDTO = resourceService.renewResourceLock(lockedResourceDTO);
        log.trace(newLockedResourceDTO.toString());
        return newLockedResourceDTO;
    }

    @RequestMapping(value = "/v" + Constants.REST_API_1_0 + "/" + Constants.CREATE_RESOURCE, method = RequestMethod.POST)
    @PreAuthorize("hasRole('admin') or hasRole('creator')")
    public ResourceDTO createResource(@Valid @RequestBody CreateResourceDTO createResourceDTO, BindingResult result) throws ContentValidationException, ResourceAccessError {
        if (keycloakEnabled){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            createResourceDTO.setResourceAuthor(authentication.getPrincipal().toString());
        }
        log.trace(Constants.CREATE_RESOURCE);
        validationHelper.validate(result);
        final ResourceDTO resourceDTO = resourceService.createResource(createResourceDTO);
        log.trace(resourceDTO.toString());
        return resourceDTO;
    }

    @RequestMapping(value = "/v" + Constants.REST_API_1_0 + "/" + Constants.UPDATE_RESOURCE, method = RequestMethod.POST)
    @PreAuthorize("hasRole('admin') or #updateResourceDTO.resourceAuthor == authentication.principal.name")
    public LockedResourceDTO updateResource(@Valid @RequestBody UpdateResourceDTO updateResourceDTO, BindingResult result) throws ContentValidationException, ResourceNotOwned, ResourceAccessError {
        log.trace(Constants.UPDATE_RESOURCE);
        validationHelper.validate(result);
        final LockedResourceDTO lockedResourceDTO = resourceService.updateResource(updateResourceDTO);
        log.trace(lockedResourceDTO.toString());
        return lockedResourceDTO;
    }

    @RequestMapping(value = "/v" + Constants.REST_API_1_0 + "/" + Constants.QUERY_RESOURCE, method = RequestMethod.POST)
    @PostFilter("hasRole('admin') or filterObject.author == authentication.principal.name")
    public ResourceDTO queryResource(@Valid @RequestBody ResourceQueryDTO resourceQueryDTO, BindingResult result) throws ContentValidationException, ResourceNotFound, ResourceAccessError {
        log.trace(Constants.QUERY_RESOURCE);
        validationHelper.validate(result);
        final ResourceDTO resourceDTO = resourceService.queryResource(resourceQueryDTO);
        log.trace("{}", resourceDTO);
        return resourceDTO;
    }

    @RequestMapping(value = "/v" + Constants.REST_API_1_0 + "/" + Constants.QUERY_RESOURCE_BY_FIELDS, method = RequestMethod.POST)
    @PostFilter("hasRole('admin') or filterObject.author == authentication.principal.name")
    public List<ResourceDTO> queryResourceByFields(@Valid @RequestBody ResourceFieldsQueryDTO resourceFieldsQueryDTO, BindingResult result) throws ContentValidationException, ResourceNotFound, ResourceAccessError {
        log.trace(Constants.QUERY_RESOURCE);
        validationHelper.validate(result);
        List<ResourceDTO> resourceDTOs = resourceService.queryResourceByFields(resourceFieldsQueryDTO);
        return resourceDTOs;
    }

    @RequestMapping(value = "/v" + Constants.REST_API_1_0 + "/" + Constants.DELETE_RESOURCE, method = RequestMethod.POST)
    @PreAuthorize("hasRole('admin') or #LockedResourceDTO.author == authentication.principal.name")
    public ResourceDTO deleteResource(@Valid @RequestBody LockedResourceDTO lockedResourceDTO, BindingResult result) throws ContentValidationException, ResourceNotOwned, ResourceAccessError {
        log.trace(Constants.DELETE_RESOURCE);
        validationHelper.validate(result);
        final ResourceDTO resourceDTO = resourceService.deleteResource(lockedResourceDTO);
        log.trace(resourceDTO.toString());
        return resourceDTO;
    }

    @RequestMapping(value = "/v" + Constants.REST_API_1_0 + "/" + Constants.GET_DATA, method = RequestMethod.POST)
    public byte[] getData(@Valid @RequestBody DataDTO dataDTO, HttpServletResponse response, BindingResult result) throws ContentValidationException, ResourceAccessError, IOException {
        log.trace(Constants.GET_DATA);
        final Path responseFile = resourceService.getDataPath(dataDTO);
        final byte[] responseData = Files.readAllBytes(responseFile);
        final String mimeType = tika.detect(responseData);
        response.setContentType(mimeType);
        response.setContentLength(responseData.length);
        return responseData;
    }

    @RequestMapping(value = "/v" + Constants.REST_API_1_0 + "/" + Constants.GET_DATA + "/{context}/**", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getDataResponse(@PathVariable String context, HttpServletRequest req, HttpServletResponse response) throws Exception {
        log.trace(Constants.GET_DATA + " uri={}", context);

        //Extract UURI
        String resourceUri = req.getRequestURI().substring(req.getRequestURI().indexOf(context));
        //Decode URI
        resourceUri = URLDecoder.decode(resourceUri, "UTF-8");
        //Extract TenantID
        final String tenantID = context.substring(context.lastIndexOf('-') + 1);

        // Populate UID
        final String uuid = hashedDirectoryService.hashText(resourceUri);

        ResponseEntity<InputStreamResource> responseEntity = resourceService.getResponseInputStream(uuid);
        if (responseEntity == null) {
            responseEntity = resourceService.getResponseInputStream(uuid + "-" + tenantID);
        }
        if (responseEntity == null) {
            log.error("resource {} was requested but does not exist", context);
            throw new ResourceNotFound("Resource " + context + " was requested but does not exist");
            // an alternative way would be to return the ResponseEntity
            // return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        log.trace(responseEntity.toString());
        return responseEntity;
    }

    @RequestMapping(value = "/v" + Constants.REST_API_1_0 + "/" + Constants.GET_MULTIPART_DATA + "/{context}/**", method = RequestMethod.GET)
    public void getMultiPartDataResponse(@PathVariable String context, HttpServletRequest req, HttpServletResponse response) throws Exception {
        log.trace(Constants.GET_MULTIPART_DATA + " uri={}", context);

        //Extract UURI
        String resourceUri = req.getRequestURI().substring(req.getRequestURI().indexOf(context));
        //Decode URI
        resourceUri = URLDecoder.decode(resourceUri, "UTF-8");
        //Extract TenantID
        final String tenantID = context.substring(context.lastIndexOf('-') + 1);

        // Populate UID
        final String uuid = hashedDirectoryService.hashText(resourceUri);

        MultipartFileSender multipartFileSender = resourceService.getMultipartFileSender(uuid);
        if (multipartFileSender == null) {
            multipartFileSender = resourceService.getMultipartFileSender(uuid + "-" + tenantID);
        }

        if (multipartFileSender == null) {
            log.error("resource {} was requested but does not exist", context);
            throw new ResourceNotFound("Resource " + context + " was requested but does not exist");
        }

        multipartFileSender.with(req)
                .with(response)
                .serveResource();
    }

    @RequestMapping(value = "/v" + Constants.REST_API_1_0 + "/" + Constants.GET_DATA_FROM_UUID + "/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getDataResponseFromUUID(@PathVariable String uuid, HttpServletRequest req) throws ContentValidationException, ResourceAccessError, IOException {
        log.trace(Constants.GET_DATA_FROM_UUID + " uuid={}", uuid);
        ResponseEntity<InputStreamResource> responseEntity = resourceService.getResponseInputStream(uuid);
        if (responseEntity == null) {
            log.error("resource {} was requested but does not exist", uuid);
            throw new ResourceAccessError("Resource " + uuid + " was requested but does not exist");
        }
        return responseEntity;
    }

    @RequestMapping(value = "/v" + Constants.REST_API_1_0 + "/" + Constants.GET_PARENT_UUID, method = RequestMethod.POST)
    public String getParentResource(@Valid @RequestBody String uuid, BindingResult result) throws ContentValidationException, ResourceAccessError, IOException {
        log.trace(Constants.GET_PARENT_UUID + " uuid={}", uuid);
        String ret = resourceService.getParentResource(uuid);
        return ret;
    }
}