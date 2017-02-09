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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

public interface HashedDirectoryService {

    String storeFile(final String UURI, final String tenantID, final byte[] resourceBinary) throws IOException;

    String replaceFile(final String UURI, final String tenantID, final byte[] resourceBinary) throws IOException;

    String storeFile(final String UURI, final String tenantID, final URI resourceURI) throws IOException;

    String replaceFile(final String UURI, final String tenantID, final URI resourceURI) throws IOException;

    String storeFile(final String UURI, final String tenantID, final String resourceURI) throws IOException, URISyntaxException;

    String replaceFile(final String UURI, final String tenantID, final String resourceURI) throws IOException, URISyntaxException;

    Path getDataByUID(final String UID, final String tenantID) throws IOException;

    byte[] getBytesByUID(final String UID, final String tenantID) throws IOException;

    Path getDataByURI(final String resourceURI, final String tenantID) throws IOException, URISyntaxException;

    byte[] getBytesByURI(final String resourceURI, final String tenantID) throws IOException;

    long getFileSize(final String resourceURI, final String tenantID) throws IOException;

    String hashText(String text);

    String getAbsolutePath(String resourceURI, final String tenantID);

    String createTenantDirectory(final String tenantID) throws IOException;

    String getVolume();

    void setVolume(String value);

    void storeChecksum(final String tenantID, final String checksum) throws IOException;

    String getChecksum(final String tenantID) throws IOException;
}
