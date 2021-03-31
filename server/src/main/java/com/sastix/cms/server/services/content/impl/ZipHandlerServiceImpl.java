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

import com.sastix.cms.common.content.CreateResourceDTO;
import com.sastix.cms.common.content.ResourceDTO;
import com.sastix.cms.common.content.exceptions.ResourceAccessError;
import com.sastix.cms.server.domain.entities.Resource;
import com.sastix.cms.server.services.content.DistributedCacheService;
import com.sastix.cms.server.services.content.HashedDirectoryService;
import com.sastix.cms.server.services.content.ResourceService;
import com.sastix.cms.server.services.content.ZipHandlerService;
import org.apache.tika.Tika;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ZipHandlerServiceImpl implements ZipHandlerService {

    public static final String METADATA_XML_FILE = "imsmanifest.xml";
    public static final String METADATA_JSON_FILE = "info.json";
    public static final String METADATA_STARTPAGE = "startpage";
    public static final String METADATA_STARTPAGE_CAMEL = "startPage";

    private final GsonJsonParser gsonJsonParser = new GsonJsonParser();

    @Autowired
    DistributedCacheService distributedCacheService;

    @Autowired
    HashedDirectoryService hashedDirectoryService;

    @Autowired
    ResourceService resourceService;

    @Autowired
    CommonResourceServiceImpl crs;

    private final Tika tika = new Tika();

    @Override
    public ResourceDTO handleZip(Resource zipResource) {

        final Path zipPath;
        try {
            zipPath = hashedDirectoryService.getDataByURI(zipResource.getUri(), zipResource.getResourceTenantId());
        } catch (IOException | URISyntaxException e) {
            throw new ResourceAccessError(e.getMessage());
        }

        FileSystem zipfs = null;
        try {
            zipfs = FileSystems.newFileSystem(zipPath, null);
            final Path root = zipfs.getPath("/");
            final int maxDepth = 1;

            // Search for specific files.
            final Map<String, Path> map = Files.find(root, maxDepth,
                    (path_, attr) -> path_.getFileName() != null
                            && (path_.toString().endsWith(METADATA_JSON_FILE)
                            || path_.toString().endsWith(METADATA_XML_FILE)))
                    .collect(Collectors.toMap(p -> p.toAbsolutePath().toString().substring(1), p -> p));


            final String zipType;
            final String startPage;

            // Check if it is a cms file
            if (map.containsKey(METADATA_JSON_FILE)) {
                zipType = METADATA_JSON_FILE;
                log.info("Found CMS Metadata File " + map.get(zipType).toString());
                startPage = findStartPage(map.get(zipType));
                // Check if it is a Scrom file
            } else if (map.containsKey(METADATA_XML_FILE)) {
                zipType = METADATA_XML_FILE;
                log.info("Found CMS Metadata File " + map.get(zipType).toString());
                startPage = findScormStartPage(map.get(zipType));

            } else {
                throw new ResourceAccessError("Zip " + zipResource.getName() + " is not supported. "
                        + METADATA_JSON_FILE + " and " + METADATA_XML_FILE + " are missing");
            }


            log.trace(startPage);


            final List<ResourceDTO> resourceDTOs = new LinkedList<>();

             /* Path inside ZIP File */
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                    final String parentContext = zipResource.getUri().split("-")[0] + "-" + zipResource.getResourceTenantId();
                    final CreateResourceDTO createResourceDTO = new CreateResourceDTO();
                    createResourceDTO.setResourceMediaType(tika.detect(file.toString()));
                    createResourceDTO.setResourceAuthor(zipResource.getAuthor());
                    createResourceDTO.setResourceExternalURI(file.toUri().toString());
                    createResourceDTO.setResourceName(file.toString().substring(1));
                    createResourceDTO.setResourceTenantId(zipResource.getResourceTenantId());

                    final Resource resource = resourceService.insertChildResource(createResourceDTO, parentContext, zipResource);

                    distributedCacheService.cacheIt(resource.getUri(), resource.getResourceTenantId());

                    if (file.toString().substring(1).equals(startPage)) {
                        resourceDTOs.add(0, crs.convertToDTO(resource));
                    } else {
                        resourceDTOs.add(crs.convertToDTO(resource));
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir,
                                                         BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });

            final ResourceDTO parentResourceDto = resourceDTOs.remove(0);
            parentResourceDto.setResourcesList(resourceDTOs);
            return parentResourceDto;

        } catch (IOException e) {
            throw new ResourceAccessError("Error while analyzing " + zipResource.toString());
        } finally {
            if (zipfs != null && zipfs.isOpen()) {
                try {
                    log.info("Closing FileSystem");
                    zipfs.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                    throw new ResourceAccessError("Error while analyzing " + zipResource.toString());
                }
            }
        }
    }

    @Override
    public boolean isZip(Resource zipResource) {
        String type = tika.detect(zipResource.getName());
        return type.toLowerCase().contains("zip");
    }

    public String findStartPage(final Path metadataPath) {
        final String json;
        try {
            json = new String(Files.readAllBytes(metadataPath), "UTF-8");
        } catch (IOException e) {
            log.error("Error in determining if it is a cms zip resource: {}", e.getLocalizedMessage());
            throw new ResourceAccessError("Zip " + metadataPath.getFileName() + " cannot be read. ");
        }

        final Map<String, Object> map = gsonJsonParser.parseMap(json);

        String startPage;
        if (map.get(METADATA_STARTPAGE) != null) {
            startPage = (String) map.get(METADATA_STARTPAGE);
        } else if (map.get(METADATA_STARTPAGE_CAMEL) != null) {
            startPage = (String) map.get(METADATA_STARTPAGE_CAMEL);
        } else {
            throw new ResourceAccessError("Start page in Zip " + metadataPath.getFileName() + " cannot be found");
        }

        return startPage;
    }

    public String findScormStartPage(final Path metadataPath) {
        Document doc;
        try {
            doc = Jsoup.parse(new String(Files.readAllBytes(metadataPath), "UTF-8"), "", Parser.xmlParser());
        } catch (final IOException e) {
            throw new ResourceAccessError("Zip " + metadataPath.getFileName() + " cannot be read. ");
        }
        String startPage = null;
        for (Element e : doc.select("resources")) {
            startPage = e.select("resource").get(0).attr("href");
        }
        if (startPage == null) {
            throw new ResourceAccessError("Start page in Zip " + metadataPath.getFileName() + " cannot be found");
        }
        return startPage;
    }
}
