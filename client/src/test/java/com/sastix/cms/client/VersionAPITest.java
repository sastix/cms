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
import org.junit.Assert;
import org.junit.Before;
import org.mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {CmsClient.class, CmsClient.class,VersionAPITest.class, RestTemplateConfiguration.class, ApiClientConfig.class})
@ActiveProfiles({"production", "test"})
@IntegrationTest({
        // cr client properties
        "cms.server.host:localhost",
        "cms.server.port:8080",
        "api.version:1.0"})
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

    @Before
    public void init() throws IOException, URISyntaxException {
        MockitoAnnotations.initMocks(this);
        //spy syntax
        //Mockito.doReturn(TEST_VERSION).when(apiVersionClient).getApiVersion();
        Mockito.when(apiVersionClient.getApiVersion()).thenReturn(TEST_VERSION);
    }

    @Test
    public void getApiVersion() {

        assertNotNull("service should be resolved", cmsClient);
        VersionDTO apiVersion = cmsClient.getApiVersion();
        Assert.assertEquals("DTO should be the same", apiVersion, TEST_VERSION);

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
