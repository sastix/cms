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

import com.sastix.cms.server.services.content.GeneralFileHandlerService;
import org.apache.tika.Tika;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

@Service
public class GeneralFileHandlerServiceImpl implements GeneralFileHandlerService {
    private final Tika tika = new Tika();
    @Override
    public Charset guessCharset(InputStream is) throws IOException {
        return Charset.forName(tika.detect(is));
    }

    @Override
    public String getMediaType(byte[] bytes) throws IOException {
        String mimeType = tika.detect(bytes);
        return mimeType;
    }

    @Override
    public String findParentFile(String xml) {
        String ret = null;
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
        for (Element e : doc.select("resources")) {
            ret = e.select("resource").get(0).attr("href");
        }
        return ret;
    }

    @Override
    public void replaceRelativePathsInWebFiles(File file, Map<String, String> paths) {

    }
}
