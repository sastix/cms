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

package com.sastix.cms.server.config;

import com.sastix.cms.common.Constants;
import com.sastix.cms.common.dataobjects.VersionDTO;
import com.sastix.cms.common.services.api.ApiVersionService;
import com.sastix.cms.common.services.api.ApiVersionServiceImpl;
import com.sastix.cms.server.CmsServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.sastix.cms")
public class VersionConfiguration {
    public static VersionDTO CMS_SERVER_VERSION = new VersionDTO()
            .withMinVersion(Double.valueOf(Constants.REST_API_1_0))
            .withMaxVersion(Double.valueOf(Constants.REST_API_1_0))
            .withVersionContext(Double.valueOf(Constants.REST_API_1_0), "/" + CmsServer.CONTEXT + "/v" + Constants.REST_API_1_0);

    @Bean
    public ApiVersionService apiVersionService() {
			/*
			 * you need to configure the api version service with the
			 * constructor argument of the api ranges you support
			 */
        return new ApiVersionServiceImpl(CMS_SERVER_VERSION);
    }
}
