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

package com.sastix.cms.client;

import com.sastix.cms.client.config.RestTemplateConfiguration;
import com.sastix.cms.client.impl.CmsClient;
import com.sastix.cms.common.client.ApiVersionClient;
import com.sastix.cms.common.client.RetryRestTemplate;
import com.sastix.cms.common.client.impl.ApiVersionClientImpl;
import com.sastix.cms.common.dataobjects.VersionDTO;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(classes = {CmsClient.class, CmsClient.class,VersionAPITest.class, RestTemplateConfiguration.class, ApiClientConfig.class},
properties = {
        // cr client properties
        "cms.server.host:localhost",
        "cms.server.port:8080",
        "api.version:1.0"})
@ActiveProfiles({"production", "test"})

public class VersionAPITest {

    private Logger LOG = (Logger) LoggerFactory.getLogger(VersionAPITest.class);

    static VersionDTO TEST_VERSION = new VersionDTO()
            .withMinVersion(1.0)
            .withMaxVersion(1.0)
            .withVersionContext(1.0, "/cms/v1.0");

    @InjectMocks
    CmsClient cmsClient = new CmsClient();

    @Mock
    ApiVersionClientImpl apiVersionClient;

    @BeforeAll
    public void init() throws IOException, URISyntaxException {
        //spy syntax
        //Mockito.doReturn(TEST_VERSION).when(apiVersionClient).getApiVersion();
        Mockito.when(apiVersionClient.getApiVersion()).thenReturn(TEST_VERSION);
    }

    @Test
    public void getApiVersion() {

        assertNotNull(cmsClient, "service should be resolved");
        VersionDTO apiVersion = cmsClient.getApiVersion();
        assertEquals(apiVersion, TEST_VERSION, "DTO should be the same");

        LOG.info("DTO returned by api call : {} ", apiVersion);
    }
}

@Configuration
@EnableAutoConfiguration
class ApiClientConfig {

    @Value("${cms.server.host}")
    private String host;

    @Value("${cms.server.port}")
    private String port;

    @Value("${api.version}")
    private String apiVersion;


    @Autowired
    @Qualifier("CmsRestTemplate")
    RetryRestTemplate retryRestTemplate;

    @Bean(name = "CmsApiVersionClient")
    public ApiVersionClient getApiVersionClient() throws Exception {
        return new ApiVersionClientImpl(host, port, apiVersion, retryRestTemplate);
    }
}
