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

package com.sastix.cms.client;

import com.sastix.cms.common.api.ContentApi;
import com.sastix.cms.common.content.DataDTO;
import com.sastix.cms.common.content.exceptions.ContentValidationException;
import com.sastix.cms.common.content.exceptions.ResourceAccessError;
import com.sastix.cms.common.dataobjects.ResponseDTO;
import com.sastix.cms.common.dataobjects.VersionDTO;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public interface ContentClient extends ContentApi {
    // Add any additional client interfaces
    VersionDTO getApiVersion();

    ResponseDTO getDataStream(DataDTO dataDTO) throws ResourceAccessError, ContentValidationException, IOException;

    ResponseDTO getData(String uri) throws IOException;

    ResponseDTO getMultiPartData(String uri, Map<String, List<String>> reqHeaders) throws IOException;

    ResponseDTO getDataFromUUID(String uuid) throws IOException;

    URL getDataURL(String uri) throws IOException;
}
