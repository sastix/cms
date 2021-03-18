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

package com.sastix.cms.server.services.version;

import com.sastix.cms.common.Constants;
import com.sastix.cms.common.dataobjects.VersionDTO;
import com.sastix.cms.common.services.api.ApiVersionService;
import com.sastix.cms.common.services.web.ApiVersionController;
import com.sastix.cms.server.CmsServer;
import com.sastix.cms.server.config.VersionConfiguration;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(classes = CmsServer.class,properties ={
        "spring.profiles.default:test",
        "spring.profiles.active:test",
        "cms.server.host:localhost",
        "cms.server.port:8080",
        //server properties
        "cms.retry.maxAttempts:1"
} )
@WebAppConfiguration
@ActiveProfiles({"production", "test"})
public class ApiVersionControllerTest {
    private Logger LOG = LoggerFactory.getLogger(ApiVersionControllerTest.class);

    static String TEST_VERSION_JSON = "{\"minVersion\":1.0,\"maxVersion\":1.0,\"versionContexts\":{\"1.0\":\"/cms/v1.0\"}}";

    @Autowired
    @Qualifier("apiVersionService")
    ApiVersionService service;

    @Autowired
    ApiVersionController controller;

    @Autowired
    WebApplicationContext wac;

    @Test
    public final void testApiService() {

        assertNotNull(service, "service should be resolved");
        VersionDTO apiVersion = service.getApiVersion();
        assertEquals(apiVersion, VersionConfiguration.CMS_SERVER_VERSION, "DTO should be the same");

        LOG.info("DTO returned by api call : {} ", apiVersion);

    }

    @Test
    public final void testRestController() throws Exception {

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        MvcResult result = mockMvc
                .perform(
                        get(Constants.GET_API_VERSION)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus(), "Status should be 200 - OK");
        assertEquals(result.getResponse().getContentAsString(), TEST_VERSION_JSON, "JSON response should be exactly " + TEST_VERSION_JSON);
    }
}
