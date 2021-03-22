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
 *
 */

package com.sastix.cms.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ComponentScan("com.sastix.cms")
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableTransactionManagement
public class CmsServer {

    public static final String CONTEXT = "cms";

    static final Logger LOG = (Logger) LoggerFactory.getLogger(CmsServer.class);

    public static void main(String[] args) {
        LOG.info("**** Starting Sastix CMS server ****");
        SpringApplication.run(CmsServer.class, args);
    }
}

