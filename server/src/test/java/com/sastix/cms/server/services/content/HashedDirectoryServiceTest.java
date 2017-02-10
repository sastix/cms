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

import com.sastix.cms.server.services.content.impl.HashedDirectoryServiceImpl;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@ActiveProfiles({"production", "test"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HashedDirectoryServiceImpl.class)
public class HashedDirectoryServiceTest {

    private static final String TENANT_ID = "test_tenant";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private String VOLUME;


    public Logger LOGGER = LoggerFactory.getLogger(HashedDirectoryServiceTest.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    HashedDirectoryService hashedDirectoryService;

    private URI localUri;

    @Before
    public void init() throws URISyntaxException {
        VOLUME = temporaryFolder.getRoot() + "/";
        ((HashedDirectoryServiceImpl) hashedDirectoryService).setVolume(VOLUME);
        LOGGER.info("Test volume directory {}", VOLUME);
        final URL localFile = getClass().getClassLoader().getResource("./logo.png");
        localUri = localFile.toURI();
    }

    @After
    public void cleanup() throws Exception {
        final Path path = Paths.get(VOLUME);
        LOGGER.info("Cleaning temp path {}", path.toString());
        try {
            deleteRecursive(path);
        } catch (IOException e) {
            //eat it
        }
    }

    @Test
    public void verifyStoringOfCheckSum() throws Exception {
        //this is a dummy checksum - not the real deal!
        String checksum = UUID.randomUUID().toString();
        //we make the folder

        Path path = Paths.get(VOLUME, "23");
        Files.createDirectories(path);

        hashedDirectoryService.storeChecksum("23", checksum);

        String newSum = hashedDirectoryService.getChecksum("23");
        assertEquals("checksum should be the same", checksum, newSum);
    }


    @Test
    public void testFileAlreadyExistsException() throws IOException {
        final String UURI = UUID.randomUUID().toString() + "-" + TENANT_ID + "/demo.txt";

        String path = hashedDirectoryService.storeFile(UURI, TENANT_ID, "HELLO".getBytes());

        exception.expect(FileAlreadyExistsException.class);
        String path2 = hashedDirectoryService.storeFile(UURI, TENANT_ID, "HELLO".getBytes());
    }

    @Test
    public void testURIFileAlreadyExistsException() throws IOException, URISyntaxException {
        final String UURI = UUID.randomUUID().toString() + "-" + TENANT_ID + "/logo.png";

        String path = hashedDirectoryService.storeFile(UURI, TENANT_ID, localUri);

        exception.expect(FileAlreadyExistsException.class);
        String path2 = hashedDirectoryService.storeFile(UURI, TENANT_ID, localUri);
    }

    @Test
    public void testStoreFile() throws IOException {
        final String UURI = UUID.randomUUID().toString() + "-" + TENANT_ID + "/demo.txt";
        final String path = hashedDirectoryService.storeFile(UURI, TENANT_ID, "HELLO".getBytes());
        LOGGER.info(path);
    }


    @Test
    public void testURIStoreFileWithoutCreationTime() throws IOException {
        final String UURI = UUID.randomUUID().toString() + "-" + TENANT_ID + "/logo.png";
        final String path = hashedDirectoryService.storeFile(UURI, TENANT_ID, localUri);
        LOGGER.info(path);
    }

    @Test
    public void testRepeatedStore() throws IOException {
        final int iterations = 1000;
        for (int i = 0; i < iterations; i++) {
            final String UURI = UUID.randomUUID().toString() + "-" + TENANT_ID + "/demo.txt";
            hashedDirectoryService.storeFile(UURI, TENANT_ID, "HELLO".getBytes());
        }
        final Path path = Paths.get(VOLUME);
        assertEquals("Number of files should be the same", iterations, listRecursive(path));
    }

    @Test
    public void testURIRepeatedStore() throws IOException {
        final int iterations = 1000;
        for (int i = 0; i < iterations; i++) {
            final String UURI = UUID.randomUUID().toString() + "-" + TENANT_ID + "/logo.png";
            hashedDirectoryService.storeFile(UURI, TENANT_ID, localUri);
        }
        final Path path = Paths.get(VOLUME);
        assertEquals("Number of files should be the same", iterations, listRecursive(path));
    }


    @Test
    public void testStoreFileHTTPURI() throws IOException, URISyntaxException {
        URL remoteURL = new URL("http://localhost/index.html");
        int response = checkIfUrlExists(remoteURL);
        if (response != 200) {
            LOGGER.error("URL {} does not exists", remoteURL.toString());
            return;
        }
        final URI externalURI = remoteURL.toURI();
        final String UURI = UUID.randomUUID().toString() + "-" + TENANT_ID + "/index.html";

        final String path = hashedDirectoryService.storeFile(UURI, TENANT_ID, externalURI);
        LOGGER.info(path);
    }


    @Test
    public void testGetDataByUID() throws IOException, URISyntaxException {
        final String UURI = UUID.randomUUID().toString() + "-" + TENANT_ID + "/demo.txt";
        final String path = hashedDirectoryService.storeFile(UURI, TENANT_ID, "HELLO Data".getBytes());

        final Path content = hashedDirectoryService.getDataByUID(hashedDirectoryService.hashText(UURI), TENANT_ID);

        Assert.assertEquals(new String(Files.readAllBytes(content)), "HELLO Data");
    }


    @Test
    public void testGetDataByURI() throws IOException, URISyntaxException {
        final String UURI = UUID.randomUUID().toString() + "-" + TENANT_ID + "/demo.txt";
        final String path = hashedDirectoryService.storeFile(UURI, TENANT_ID, "HELLO Data".getBytes());

        Path content = hashedDirectoryService.getDataByURI(UURI, TENANT_ID);
        Assert.assertEquals(new String(Files.readAllBytes(content)), "HELLO Data");
    }

    @Test
    public void testURINotFound() throws IOException, URISyntaxException {
        URL remoteURL = new URL("http://127.0.0.1/cr.html");

        final URI externalURI = remoteURL.toURI();
        final String uuid = UUID.randomUUID().toString();

        exception.expect(IOException.class);
        final String path = hashedDirectoryService.storeFile(uuid, TENANT_ID, externalURI);
        LOGGER.info(path);
    }

    private int checkIfUrlExists(final URL url) {
        try {
            final HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");  //OR  huc.setRequestMethod ("HEAD");
            huc.connect();
            return huc.getResponseCode();
        } catch (IOException e) {
            //eat it
        }
        return 400;
    }

    private int listRecursive(Path path) throws IOException {
        final int[] counter = {0};
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                counter[0]++;
                LOGGER.trace("Listing: [" + counter[0] + "]\t" + file.getParent() + "/" + file.getFileName());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
        return counter[0];
    }

    private void deleteRecursive(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }


}
