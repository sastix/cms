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

import java.io.IOException;
import java.util.Map;

public interface ZipFileHandlerService {


    /**
     * Get the zip file in byte array and unzip it
     *
     * @param bytes
     * @return a DataMaps with the extracted resources (bytes, filenames, folders)
     * */
    DataMaps unzip(byte[] bytes) throws IOException;

    /**
     * Check if a byte array is a zip file
     * */
    boolean isZipFile(byte[] bytes) throws IOException;

    /**
     * Check if the extracted files from byte array include a scorm type
     * */
    boolean isScormType(Map<String, byte[]> bytesMap);

    /**
     * Check if the extracted files from byte array include info.json file with a startpage attribute.
     * If present, the specified zip is a cms resource.
     *
     * a sample of info.json could be:
     * {
     *    "content_uid":"12345qwerty",
     *    "thumbnailphoto_uid":"54321ytrewq",
     *    "startpage":"start.html",
     *    "totalpages":"1"
     *    }
     * */
    String getResourceStartPage(Map<String, byte[]> bytesMap);

    /**
     * Find the parent resource from metadata
     * */
    String findParentResource(Map<String, byte[]> bytesMap);

    /**
     * Replace relative paths in web files with paths from CMS
     * */

    @Deprecated
    DataMaps replaceRelativePaths(DataMaps dataMaps) throws IOException;
}
