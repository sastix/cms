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

package com.sastix.cms.common.services;

import com.sastix.cms.common.Constants;
import com.sastix.cms.common.dataobjects.VersionDTO;
import com.sastix.cms.common.services.api.ApiVersionService;
import com.sastix.cms.common.services.api.ApiVersionServiceImpl;
import com.sastix.cms.common.services.web.ApiVersionController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import lombok.extern.slf4j.Slf4j;

import com.sastix.cms.common.services.ApiVersionControllerTest.TestConfig;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@Slf4j
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class ApiVersionControllerTest {

	static VersionDTO TEST_VERSION = new VersionDTO()
		.withMinVersion(1.0)
		.withMaxVersion(1.0)
		.withVersionContext(1.0,"/testapi/v1.0");
	static String TEST_VERSION_JSON = "{\"minVersion\":1.0,\"maxVersion\":1.0,\"versionContexts\":{\"1.0\":\"/testapi/v1.0\"}}";
	
	@Autowired
	ApiVersionService service;
	
	@Autowired
	ApiVersionController controller;
	
	@Autowired 
	WebApplicationContext wac; 
	
	@Test
	public final void testApiService() {

		assertNotNull("service should be resolved",service);
		VersionDTO apiVersion = service.getApiVersion();
		assertEquals("DTO should be the same",apiVersion, TEST_VERSION);
		
		log.info("DTO returned by api call : {} " ,apiVersion);
		
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
		assertEquals("JSON response should be exactly "+TEST_VERSION_JSON, result.getResponse().getContentAsString(), TEST_VERSION_JSON);
	}

	
	@Configuration
	@WebAppConfiguration
	@EnableWebMvc
	@ComponentScan("com.sastix.cms.common.services.web")
	static class TestConfig {
		
		@Bean
		public ApiVersionService apiVersionService() {
			/*
			 * you need to configure the api version service with the 
			 * constructor argument of the api ranges you support
			 */
			return new ApiVersionServiceImpl(TEST_VERSION);
		}
		
		
	}
}
