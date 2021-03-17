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

package com.sastix.cms.server.services.content.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sastix.cms.server.services.content.HashedDirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

@Service
public class HashedDirectoryServiceImpl implements HashedDirectoryService {

    private Logger LOG = LoggerFactory.getLogger(HashedDirectoryServiceImpl.class);

    @Value("${cms.volume:/var/tmp}")
    private String VOLUME;

    public static final String VOLUME_IDENTIFIER = "${VOLUME}";

    private static final String CHECKSUM_FILE = "t_checksum";

    /**
     * Default Value is 3.
     */
    public final static int DIRECTORY_DEPTH = 3;

    /**
     * Temporary buffer used for copying between channels.
     */
    final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);

    private final static EnumSet<StandardOpenOption> FILE_OPEN_OPTIONS =
            EnumSet.of(StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);

    private final static EnumSet<StandardOpenOption> FILE_REPLACE_OPTIONS =
            EnumSet.of(StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);

    @Override
    public String storeFile(final String UURI, final String tenantID, final byte[] resourceBinary) throws IOException {
        //Create Unique hash
        final String hash = hashText(UURI);
        if (hash == null) {
            //TODO: Throw exception
            LOG.error("Unable to create HASH for UID {}", UURI);
        }

        //Get Filename (including directories)
        final String filename = getFilename(hash);

        //Add Volume to the Path
        final Path file = Paths.get(VOLUME + "/" + tenantID + filename);

        //Create the directories.
        Files.createDirectories(file.getParent());

        //Write the contents to the file
        writeFile(file, resourceBinary);

        //Returns the relative Path
        return getRelativePath(file);
    }

    @Override
    public String replaceFile(String UURI, String tenantID, byte[] resourceBinary) throws IOException {
        //Create Unique hash
        final String hash = hashText(UURI);
        if (hash == null) {
            //TODO: Throw exception
            LOG.error("Unable to create HASH for UID {}", UURI);
        }

        //Get Filename (including directories)
        final String filename = getFilename(hash);

        //Add Volume to the Path
        final Path file = Paths.get(VOLUME + "/" + tenantID + filename);

        //Write the contents to the file
        replaceFile(file, resourceBinary);

        //Returns the relative Path
        return getRelativePath(file);
    }

    @Override
    public String storeFile(final String UURI, final String tenantID, final URI resourceURI) throws IOException {
        //Create Unique hash
        final String hash = hashText(UURI);
        if (hash == null) {
            //TODO: Throw exception
            LOG.error("Unable to create HASH for UID {}", UURI);
        }

        //Get Filename (including directories)
        final String filename = getFilename(hash);

        //Add Volume to the Path
        final Path file = Paths.get(VOLUME + "/" + tenantID + filename);

        //Create the directories.
        Files.createDirectories(file.getParent());

        //Convert URI to URL
        final URL resourceUrl = resourceURI.toURL();

        //Write the contents to the file
        writeFile(file, resourceUrl);

        return getRelativePath(file);
    }

    @Override
    public String replaceFile(final String UURI, final String tenantID, final URI resourceURI) throws IOException {
        //Create Unique hash
        final String hash = hashText(UURI);
        if (hash == null) {
            //TODO: Throw exception
            LOG.error("Unable to create HASH for UID {}", UURI);
        }

        //Get Filename (including directories)
        final String filename = getFilename(hash);

        //Add Volume to the Path
        final Path file = Paths.get(VOLUME + "/" + tenantID + filename);

        //Convert URI to URL
        final URL resourceUrl = resourceURI.toURL();

        //Write the contents to the file
        replaceFile(file, resourceUrl);

        return getRelativePath(file);
    }

    @Override
    public String storeFile(final String UURI, final String tenantID, final String resourceURI) throws IOException, URISyntaxException {
        return storeFile(UURI, tenantID, new URL(resourceURI).toURI());
    }

    @Override
    public String replaceFile(final String UURI, final String tenantID, final String resourceURI) throws IOException, URISyntaxException {
        return replaceFile(UURI, tenantID, new URL(resourceURI).toURI());
    }


    @Override
    public Path getDataByUID(final String uid, final String tenantID) throws IOException {
        //Get Filename (including directories)
        final String filename = getFilename(uid);

        //Add Volume and tenant ID to the Path
        return Paths.get(VOLUME + "/" + tenantID + filename);
    }

    @Override
    public byte[] getBytesByUID(final String uid, final String tenantID) throws IOException {
        //Get Filename (including directories)
        final String filename = getFilename(uid);

        //Add Volume and tenant ID to the Path
        return Files.readAllBytes(Paths.get(VOLUME + "/" + tenantID + filename));
    }

    @Override
    public Path getDataByURI(final String resourceURI, final String tenantID) throws IOException, URISyntaxException {
        return Paths.get(getAbsolutePath(resourceURI, tenantID));
    }

    @Override
    public byte[] getBytesByURI(final String resourceURI, final String tenantID) throws IOException {
        return Files.readAllBytes(Paths.get(getAbsolutePath(resourceURI, tenantID)));
    }

    @Override
    public long getFileSize(String resourceURI, String tenantID) throws IOException {
        return Files.size(Paths.get(getAbsolutePath(resourceURI, tenantID)));
    }

    /**
     * Writes file to disk and copy the contents of the input byte array.
     *
     * @param file a Path with file path.
     * @param url  a remote/local url to be saved as file
     * @throws IOException
     */
    private void writeFile(final Path file, final URL url) throws IOException {

        if (url.toString().startsWith("jar:file:///")) {
            try {
                writeZipFile(file, url);
            } catch (Exception e) {
                throw new IOException(e);
            }
        } else {

            //Create the file
            SeekableByteChannel sbc = null;
            try {
                //Creates a new Readable Byte channel from URL
                final ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                //Create the file
                sbc = Files.newByteChannel(file, FILE_OPEN_OPTIONS);

                //Clears the buffer
                buffer.clear();

                //Read input Channel
                while (rbc.read(buffer) != -1) {
                    // prepare the buffer to be drained
                    buffer.flip();
                    // write to the channel, may block
                    sbc.write(buffer);
                    // If partial transfer, shift remainder down
                    // If buffer is empty, same as doing clear()
                    buffer.compact();
                }
                // EOF will leave buffer in fill state
                buffer.flip();
                // make sure the buffer is fully drained.
                while (buffer.hasRemaining()) {
                    sbc.write(buffer);
                }
            } finally {
                if (sbc != null) {
                    sbc.close();
                }
            }
        }
    }

    /**
     * Writes file to disk and copy the contents of the input byte array.
     *
     * @param file   a Path with file path.
     * @param zipUrl a remote zip url to be saved as file
     * @throws IOException
     */
    private void writeZipFile(final Path file, final URL zipUrl) throws URISyntaxException, IOException {
        final URI resourceURI = zipUrl.toURI();
        final Map<String, String> env = new HashMap<>();
        final String resourceUriStr = URLDecoder.decode(resourceURI.toString(), "UTF-8");
        final int indexOfSeparator = resourceUriStr.indexOf("!");
        final FileSystem fs = FileSystems.newFileSystem(resourceURI, env);
        final Path path = fs.getPath(resourceUriStr.substring(indexOfSeparator + 1));
        try {
            Files.copy(path, file, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            fs.close();
        }
    }

    private void replaceFile(final Path file, final URL url) throws IOException {
        //Create the file
        SeekableByteChannel sbc = null;
        try {
            //Creates a new Readable Byte channel from URL
            final ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            //Create the file
            sbc = Files.newByteChannel(file, FILE_REPLACE_OPTIONS);

            //Clears the buffer
            buffer.clear();

            //Read input Channel
            while (rbc.read(buffer) != -1) {
                // prepare the buffer to be drained
                buffer.flip();
                // write to the channel, may block
                sbc.write(buffer);
                // If partial transfer, shift remainder down
                // If buffer is empty, same as doing clear()
                buffer.compact();
            }
            // EOF will leave buffer in fill state
            buffer.flip();
            // make sure the buffer is fully drained.
            while (buffer.hasRemaining()) {
                sbc.write(buffer);
            }
        } finally {
            if (sbc != null) {
                sbc.close();
            }
        }
    }

    /**
     * Writes file to disk and copy the contents of the input byte array.
     *
     * @param file     a Path with file path.
     * @param contents a byte[] with the contents
     * @throws IOException
     */
    private void writeFile(final Path file, final byte[] contents) throws IOException {
        final ByteBuffer bb = ByteBuffer.wrap(contents);
        //Create the file
        final SeekableByteChannel sbc = Files.newByteChannel(file, FILE_OPEN_OPTIONS);
        //Copy contents to the new File
        sbc.write(bb);
        //Close the byte channel
        sbc.close();
    }

    private void replaceFile(final Path file, final byte[] contents) throws IOException {
        final ByteBuffer bb = ByteBuffer.wrap(contents);
        //Create the file
        final SeekableByteChannel sbc = Files.newByteChannel(file, FILE_REPLACE_OPTIONS);
        //Copy contents to the new File
        sbc.write(bb);
        //Close the byte channel
        sbc.close();
    }

    /**
     * Generates a new filename based  on the input filename.
     *
     * @param hash a String with the hash
     * @return a String with the new filepath
     */
    private String getFilename(String hash) {
        final int hashcode = hash.hashCode();
        final int mask = 255;
        final int[] directories = new int[DIRECTORY_DEPTH];
        for (int i = 0; i < DIRECTORY_DEPTH; i++) {
            directories[i] = (hashcode >> 8 * i) & mask;
        }
        final StringBuilder path = new StringBuilder(File.separator);
        for (int directory : directories) {
            path.append(String.format("%03d", directory));
            path.append(File.separator);
        }
        path.append(hash);
        return path.toString();
    }

    /**
     * Returns the crc32 hash for the input String.
     *
     * @param text a String with the text
     * @return a BigInteger with the hash
     */
    @Override
    public String hashText(final String text) {
        final CRC32 crc32 = new CRC32();
        crc32.reset();
        crc32.update(text.getBytes());
        return Long.toHexString(crc32.getValue());
    }

    public String getRelativePath(final Path file) {
        return new StringBuilder().append(VOLUME_IDENTIFIER).append("/").append(Paths.get(VOLUME).relativize(file).toString()).toString();
    }

    private String getRelativePath(final String UURI) {
        //Create Unique hash
        final String hash = hashText(UURI);
        if (hash == null) {
            LOG.error("Unable to create HASH for UID {}", UURI);
        }

        //Get Filename (including directories)
        final String filename = getFilename(hash);

        //Add Volume to the Path
        final Path file = Paths.get(VOLUME + filename);

        return getRelativePath(file);
    }

    @Override
    public String getAbsolutePath(final String resourceURI, final String tenantID) {
        final Path volumeIdentifier = Paths.get(VOLUME_IDENTIFIER);
        final Path relative = Paths.get(getRelativePath(resourceURI));
        return new StringBuilder().append(VOLUME).append("/").append(tenantID).append("/").append(volumeIdentifier.relativize(relative).toString()).toString();
    }

    /**
     * Used only JUnit tests.
     *
     * @param volume the shared Volume to be used.
     */
    public void setVolume(String volume) {
        this.VOLUME = volume;
    }


    @Override
    public String createTenantDirectory(final String tenantID) throws IOException {
        //Add Tenant to the Volume
        final Path file = Paths.get(VOLUME + "/" + tenantID);
        //Create the directories.
        Files.createDirectories(file);
        return getRelativePath(file);
    }

    @Override
    public String getVolume() {
        return VOLUME;
    }

    @Override
    public void storeChecksum(String tenantID, String checksum) throws IOException {

        //Write the contents to the file
        HashMap<String, String> map = new HashMap<>();
        map.put("tenantId", tenantID);
        map.put("checksum", checksum);
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        File to = new File(VOLUME + "/" + tenantID + "/" + CHECKSUM_FILE);

        mapper.writeValue(to, map);

    }

    @Override
    public String getChecksum(String tenantID) throws IOException {
        String fileName = VOLUME + "/" + tenantID + "/" + CHECKSUM_FILE;

        try {
            JsonFactory factory = new JsonFactory();
            ObjectMapper mapper = new ObjectMapper(factory);

            File from = new File(fileName);
            TypeReference<HashMap<String, String>> typeRef
                    = new TypeReference<HashMap<String, String>>() {
            };

            HashMap<String, String> map = mapper.readValue(from, typeRef);

            return map.get("checksum");
        } catch (IOException ioe) {
            LOG.warn("IOException reading checksum (" + fileName + "), cause " + ioe.getMessage(), ioe);
            throw ioe;
        }
    }
}
