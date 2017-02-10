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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CmsServer.class)
@WebAppConfiguration
@IntegrationTest({
        "spring.profiles.default:test",
        "spring.profiles.active:test",
        "cms.server.host:localhost",
        "cms.server.port:8080",
        //server properties
        "cms.retry.maxAttempts:1"
})
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

        assertNotNull("service should be resolved", service);
        VersionDTO apiVersion = service.getApiVersion();
        assertEquals("DTO should be the same", apiVersion, VersionConfiguration.CMS_SERVER_VERSION);

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

        assertEquals("Status should be 200 - OK", 200, result.getResponse().getStatus());
        assertEquals("JSON response should be exactly " + TEST_VERSION_JSON, result.getResponse().getContentAsString(), TEST_VERSION_JSON);
    }
}
