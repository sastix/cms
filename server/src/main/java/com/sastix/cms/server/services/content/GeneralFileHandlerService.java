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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

public interface GeneralFileHandlerService {
    /**
     * Find the charset from a stream
     *
     * @param is
     * @return charset
     * */
    Charset guessCharset(InputStream is) throws IOException;

    /**
     * Get the media type format from bytes
     *
     * @param bytes
     * @return a string representation of mediaType format
     * */
    String getMediaType(byte[] bytes) throws IOException;

    /**
     * Find the parent file from the metadata xml provided in the unit resource
     *
     * @param xml string content
     * @return the filename of the parent
     * */
    String findParentFile(String xml);

    /**
     * Replace all relative paths used in web files (html, htm, js, css)
     * with resource UID urls
     *
     * */
    void replaceRelativePathsInWebFiles(File file, Map<String, String> paths);
}
