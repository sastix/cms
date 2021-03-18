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

package com.sastix.cms.server.services.content;

import com.sastix.cms.common.content.*;
import com.sastix.cms.common.content.exceptions.ContentValidationException;
import com.sastix.cms.common.content.exceptions.ResourceAccessError;
import com.sastix.cms.common.content.exceptions.ResourceNotFound;
import com.sastix.cms.server.CmsServer;
import com.sastix.cms.server.services.content.impl.HashedDirectoryServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles({"production", "test"})
@SpringBootTest(classes = {CmsServer.class})
public class ResourceServiceTest {

    @Autowired
    ResourceService resourceService;

    @Autowired
    HashedDirectoryService hashedDirectoryService;
    
    @BeforeAll
    public void setup() throws Exception {
        //set a temporary volume for this test
        String temporaryFolder = Files.createTempDirectory("temporaryDirectoryPrefix").toFile().getAbsolutePath();
        hashedDirectoryService.setVolume(temporaryFolder + "/");
    }

    @Test
    public void shouldCreateResourceFromExternalUri() throws Exception {
        URL localFile = getClass().getClassLoader().getResource("./logo.png");
        CreateResourceDTO createResourceDTO = new CreateResourceDTO();
        createResourceDTO.setResourceAuthor("Demo Author");
        createResourceDTO.setResourceExternalURI(localFile.getProtocol() + "://" + localFile.getFile());
        createResourceDTO.setResourceMediaType("image/png");
        createResourceDTO.setResourceName("logo.png");
        createResourceDTO.setResourceTenantId("zaq12345");
        ResourceDTO resourceDTO = resourceService.createResource(createResourceDTO);


        String resourceUri = resourceDTO.getResourceURI();

        //Extract TenantID
        final String tenantID = resourceUri.substring(resourceUri.lastIndexOf('-') + 1, resourceUri.indexOf("/"));

        final Path responseFile = hashedDirectoryService.getDataByURI(resourceUri, tenantID);
        File file = responseFile.toFile();

        byte[] actualBytes = Files.readAllBytes(file.toPath());
        byte[] expectedBytes = Files.readAllBytes(Paths.get(localFile.getPath()));
        assertTrue(Arrays.equals(expectedBytes, actualBytes));
    }

    @Test
    public void shouldNotCreateResource() throws Exception {
        CreateResourceDTO createResourceDTO = new CreateResourceDTO();
        try {
            resourceService.createResource(createResourceDTO);
            fail("Expected ContentValidationException exception");
        } catch (ContentValidationException e) {
            assertTrue(e.getMessage().contains("Field errors: resourceBinary OR resourceExternalURI may not be null"));
        }

    }

    @Test
    public void shouldNotLockResource() throws Exception {
        ResourceDTO resourceDTO = new ResourceDTO();
        try {
            resourceService.lockResource(resourceDTO);
            fail("Expected ResourceNotFound exception");
        } catch (ResourceNotFound e) {
            assertTrue(e.getMessage().contains("The supplied resource UID[null] does not exist."));
        }
    }

    @Test
    public void shouldNotUnlockResource() throws Exception {
        LockedResourceDTO lockedResourceDTO = new LockedResourceDTO();
        try {
            resourceService.unlockResource(lockedResourceDTO);
            fail("Expected ResourceNotFound exception");
        } catch (ResourceNotFound e) {
            assertTrue(e.getMessage().contains("The supplied resource UID[null] does not exist."));
        }
    }

    @Test
    public void shouldNotRenewResourceLock() throws Exception {
        LockedResourceDTO lockedResourceDTO = new LockedResourceDTO();
        try {
            resourceService.renewResourceLock(lockedResourceDTO);
            fail("Expected ResourceNotFound exception");
        } catch (ResourceNotFound e) {
            assertTrue(e.getMessage().contains("The supplied resource UID[null] does not exist."));
        }
    }

    @Test
    public void shouldNotUpdateResource() throws Exception {
        UpdateResourceDTO updateResourceDTO = new UpdateResourceDTO();
        try {
            resourceService.updateResource(updateResourceDTO);
            fail("Expected ResourceNotFound exception");
        } catch (ResourceNotFound e) {
            assertTrue(e.getMessage().contains("The supplied resource UID[null] does not exist."));
        }
    }

    @Test
    public void shouldNotQueryResource() throws Exception {
        ResourceQueryDTO resourceQueryDTO = new ResourceQueryDTO();
        try {
            resourceService.queryResource(resourceQueryDTO);
            fail("Expected ResourceAccessError exception");
        } catch (ResourceAccessError e) {
            assertTrue(e.getMessage().contains("The supplied resource data are invalid and the resource cannot be retrieved."));
        }

    }

    @Test
    public void shouldNotDeleteResource() throws Exception {
        LockedResourceDTO lockedResourceDTO = new LockedResourceDTO();
        try {
            resourceService.deleteResource(lockedResourceDTO);
            fail("Expected ResourceAccessError exception");
        } catch (ResourceAccessError e) {
            assertTrue(e.getMessage().contains("The supplied resource UID[null] does not exist."));
        }
    }

    private String getAbsolutePath(final String temporaryFolderPath, final String relativePath) {
        final Path volumeIdentifier = Paths.get(HashedDirectoryServiceImpl.VOLUME_IDENTIFIER);
        final Path relative = Paths.get(relativePath);
        return new StringBuilder().append(temporaryFolderPath).append("/").append("/").append(volumeIdentifier.relativize(relative).toString()).toString();
    }
}