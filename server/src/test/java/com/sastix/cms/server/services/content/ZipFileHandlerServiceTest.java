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

package com.sastix.cms.server.services.content;

import com.sastix.cms.server.dataobjects.DataMaps;
import com.sastix.cms.server.services.content.impl.GeneralFileHandlerServiceImpl;
import com.sastix.cms.server.services.content.impl.ZipFileHandlerServiceImpl;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@ActiveProfiles({"production", "test"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ZipFileHandlerServiceImpl.class, GeneralFileHandlerServiceImpl.class})
public class ZipFileHandlerServiceTest {

    private Logger LOG = (Logger) LoggerFactory.getLogger(ZipFileHandlerServiceTest.class);

    @Autowired
    ZipFileHandlerService zipFileHandlerService;

    URL demoContentZipFile = getClass().getClassLoader().getResource("./demo_content.zip");
    URL demoContentZipFileNoScorm = getClass().getClassLoader().getResource("./demo_content_no_scorm.zip");

    byte[] bytesDemoContentZipFile;
    byte[] bytesDemoContentZipFileNoScorm;

    @Before
    public void setUp() throws IOException {
        bytesDemoContentZipFile = FileUtils.readFileToByteArray(Paths.get(demoContentZipFile.getFile()).toFile());
        bytesDemoContentZipFileNoScorm = FileUtils.readFileToByteArray(Paths.get(demoContentZipFileNoScorm.getFile()).toFile());
    }

    /**
     * Validate that we are able to extract zip media format from byte array
     */
    @Test
    public void mediaTypeTest() throws IOException {
        boolean isZipFile = zipFileHandlerService.isZipFile(bytesDemoContentZipFile);
        assertTrue(isZipFile);
    }

    @Test
    public void unzipTest() throws IOException {
        DataMaps map = zipFileHandlerService.unzip(bytesDemoContentZipFile);
        LOG.info(map.getBytesMap().size() + "");
        assertThat(map.getBytesMap().size(), is(4));
        assertThat(map.getFoldersMap().size(), is(1));
    }

    @Test
    public void isScormBasedTest() throws IOException {
        DataMaps map = zipFileHandlerService.unzip(bytesDemoContentZipFile);
        boolean isScormBased = zipFileHandlerService.isScormType(map.getBytesMap());
        assertTrue(isScormBased);
    }

    @Test
    public void isNoScormBasedTest() throws IOException {
        DataMaps map = zipFileHandlerService.unzip(bytesDemoContentZipFileNoScorm);
        boolean isScormBased = zipFileHandlerService.isScormType(map.getBytesMap());
        assertFalse(isScormBased);
    }

    @Test
    public void isResourceWithStartPageTest() throws IOException {
        DataMaps map = zipFileHandlerService.unzip(bytesDemoContentZipFile);
        String startPage = zipFileHandlerService.getResourceStartPage(map.getBytesMap());
        boolean isResourceWithStartPage = startPage.equals("start.html");
        assertTrue(isResourceWithStartPage);
    }


    @Test
    public void findParentResourceTest() throws IOException {
        DataMaps map = zipFileHandlerService.unzip(bytesDemoContentZipFile);
        String resource = zipFileHandlerService.findParentResource(map.getBytesMap());
        assertThat(resource, is("index.html"));
    }

    @Test
    public void findNullParentResourceFromNonScormTest() throws IOException {
        DataMaps map = zipFileHandlerService.unzip(bytesDemoContentZipFileNoScorm);
        String resource = zipFileHandlerService.findParentResource(map.getBytesMap());
        assertNull(resource);
    }
}
