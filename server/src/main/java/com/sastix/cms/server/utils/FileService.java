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

package com.sastix.cms.server.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class FileService {

    @Value("${cms.volume}")
    String volume;
    public FileService() {
    }

    public String saveResource(byte[] resourceBinary,String relativePath) {
        String path = null;
        if (resourceBinary != null && resourceBinary.length > 0) {
            path = saveFile(relativePath, resourceBinary);
        }
        return path;
    }

    public String saveResource(String resourceExternalURI,String relativePath) {
        String path = null;
        if (!StringUtils.isEmpty(resourceExternalURI)) {
            path = downloadResource(resourceExternalURI, relativePath);
        }
        return path;
    }


    private String saveFile(String relativePath, byte[] resourceBinary) {
        Path path = Paths.get(volume + relativePath);
        try {
            Files.write(path, resourceBinary, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path.getFileName().toString();
    }

    public String downloadResource(String resourceExternalURI, String relativePath) {
        String path = null;
        try {
            URL url = new URL(resourceExternalURI);
            InputStream is = url.openStream();
            path = volume + relativePath;
            OutputStream os = new FileOutputStream(path);
            byte[] b = new byte[2048];
            int length;
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
            is.close();
            os.close();
            return path;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    public String getFileUri(String value){
        return new StringBuffer("file://").append(volume).append("/").append(value).toString();
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}
