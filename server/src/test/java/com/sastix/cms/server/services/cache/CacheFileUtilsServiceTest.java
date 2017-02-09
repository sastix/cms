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

package com.sastix.cms.server.services.cache;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@ActiveProfiles({"production", "test"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=CacheFileUtilsServiceImpl.class)
public class CacheFileUtilsServiceTest {
    private static final Logger LOG = LoggerFactory.getLogger(CacheFileUtilsServiceTest.class);

    @Autowired
    CacheFileUtilsService cacheFileUtilsService;

    @Test
    public void downloadLocalResourceTest() throws IOException, URISyntaxException {
        //from local storage
        URL localFile = getClass().getClassLoader().getResource("./logo.png");
        byte[] bytesFound = cacheFileUtilsService.downloadResource(localFile);
        byte[] expected = Files.readAllBytes(Paths.get(localFile.getFile()));
        Assert.assertArrayEquals(expected,bytesFound);

    }

    @Test
    public void downloadOnlineResourceTest() throws IOException, URISyntaxException {
        //from online resource
        URL localFile = getClass().getClassLoader().getResource("./logo.png");
        byte[] expected = Files.readAllBytes(Paths.get(localFile.getFile()));
        System.setProperty("http.agent", "Chrome");
        byte[] bytesFound = cacheFileUtilsService.downloadResource(new URL("https://www.sastix.com/logo.png"));
        Assert.assertArrayEquals(expected,bytesFound);
    }
}
