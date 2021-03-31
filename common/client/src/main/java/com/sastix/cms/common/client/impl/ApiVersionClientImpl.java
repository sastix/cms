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
 */

package com.sastix.cms.common.client.impl;

import com.sastix.cms.common.Constants;
import com.sastix.cms.common.dataobjects.VersionDTO;
import com.sastix.cms.common.client.ApiVersionClient;
import com.sastix.cms.common.client.RetryRestTemplate;
import com.sastix.cms.common.exception.VersionNotSupportedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiVersionClientImpl implements ApiVersionClient {

    private String host;
    private String port;
    private String context = null;
    private String apiVersion;
    private int apiUrlCounter = 0;
    private boolean lazyUpdate = true;

    @Autowired
    Environment env;

    RetryRestTemplate retryRestTemplate;

    public ApiVersionClientImpl(final String host, final String port, final String apiVersion, final RetryRestTemplate retryRestTemplate) {
        this.host = host;
        this.port = port;
        this.apiVersion = apiVersion;
        this.retryRestTemplate = retryRestTemplate;
    }


    /**
     * For testing/mocking only usage
     */
    public ApiVersionClientImpl(final String host, final String port, final String apiVersion, final VersionDTO versionDTO, final RetryRestTemplate retryRestTemplate) {
        //disable lazyness when mocking
        lazyUpdate = false;
        this.host = host;
        this.port = port;
        this.retryRestTemplate = retryRestTemplate;
        this.apiVersion = apiVersion;
        if (!versionDTO.getVersionContexts().containsKey(apiVersion)) {
            if (versionDTO.getMinVersion() > Double.valueOf(apiVersion)) {
                throw new VersionNotSupportedException("API Version " + apiVersion + " is outdated. Supported Versions are " + versionDTO.toString());
            } else {
                throw new VersionNotSupportedException("API Version " + apiVersion + " is not supported. Supported Versions are " + versionDTO.toString());
            }
        } else {
            context = versionDTO.getVersionContexts().get(apiVersion);
        }
    }

    @Override
    public VersionDTO getApiVersion() {
        String url = getUrlRoot() + "/" + Constants.GET_API_VERSION;
        log.trace("API call: " + url);
        VersionDTO versionDTO = retryRestTemplate.getForObject(url, VersionDTO.class);
        log.trace("Response: " + versionDTO.toString());
        return versionDTO;
    }

    @Override
    public String getContext() {
        return context;
    }

    private String getUrlRoot() {
        return "http://" + host + ":" + port;
    }

    public String getApiUrl() {
        if(lazyUpdate){ // the only case for now lazeUpdate is false, is when mocking VersionDTO
            if (apiUrlCounter % 200 == 0) { //true on first time (when apiUrlCounter=0)
                updateContext();
                if(apiUrlCounter > 0){
                    apiUrlCounter = 0; //reset counter to avoid overflow
                }
            }
            apiUrlCounter++;
        }
        return getUrlRoot() + context;
    }

    @Override
    public void updateContext() {
        final VersionDTO versionDTO = getApiVersion();
        context = versionDTO.getVersionContexts().get(apiVersion);
    }

    public boolean isLazyUpdate() {
        return lazyUpdate;
    }

    public void setLazyUpdate(boolean lazyUpdate) {
        this.lazyUpdate = lazyUpdate;
    }
}
