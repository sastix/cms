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

import com.sastix.cms.server.dataobjects.DataMaps;
import com.sastix.cms.server.services.content.GeneralFileHandlerService;
import com.sastix.cms.server.services.content.ZipFileHandlerService;
import org.apache.tika.Tika;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
public class ZipFileHandlerServiceImpl implements ZipFileHandlerService {

    String acceptedExtensions = "html, htm, js, css";

    private final Tika tika = new Tika();
    GsonJsonParser gsonJsonParser = new GsonJsonParser();


    public static final String METADATA_XML_FILE = "imsmanifest.xml";
    public static final String METADATA_JSON_FILE = "info.json";
    public static final String METADATA_STARTPAGE = "startpage";
    public static final String METADATA_STARTPAGE_CAMEL = "startPage";

    @Autowired
    GeneralFileHandlerService generalFileHandlerService;

    @Override
    public DataMaps unzip(byte[] bytes) throws IOException {
        Map<String, String> foldersMap = new HashMap<>();

        Map<String, byte[]> extractedBytesMap = new HashMap<>();
        InputStream byteInputStream = new ByteArrayInputStream(bytes);
        //validate that it is a zip file
        if(isZipFile(bytes)) {
            try {
                //get the zip file content
                ZipInputStream zis = new ZipInputStream(byteInputStream);
                //get the zipped file list entry
                ZipEntry ze = zis.getNextEntry();

                while (ze != null) {
                    String fileName = ze.getName();
                    if (!ze.isDirectory()) {//if entry is a directory, we should not add it as a file
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        try {
                            ByteBuffer bufIn = ByteBuffer.allocate(1024);
                            int bytesRead;
                            while ((bytesRead = zis.read(bufIn.array())) > 0) {
                                baos.write(bufIn.array(), 0, bytesRead);
                                bufIn.rewind();
                            }
                            bufIn.clear();
                            extractedBytesMap.put(fileName, baos.toByteArray());
                        } finally {
                            baos.close();
                        }
                    }else{
                        foldersMap.put(fileName, fileName);
                    }
                    ze = zis.getNextEntry();
                }
                zis.closeEntry();
                zis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        DataMaps dataMaps = new DataMaps();
        dataMaps.setBytesMap(extractedBytesMap);
        dataMaps.setFoldersMap(foldersMap);
        return dataMaps;
    }

    @Override
    public boolean isZipFile(byte[] bytes) throws IOException {
        String type = tika.detect(bytes);
        return type.toLowerCase().contains("zip");
    }

    @Override
    public boolean isScormType(Map<String, byte[]> bytesMap) {
        byte[] dataXml = bytesMap.get(METADATA_XML_FILE);
        if(dataXml == null){
            return false;
        }else {
            String xml = "";
            String ret = "";
            try {
                xml = new String(dataXml, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("Error in determining if it is a scorm type: {}", e.getLocalizedMessage());
            }
            Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
            for (Element e : doc.select("resources")) {
                ret = e.select("resource").get(0).attr("adlcp:scormType");
            }
            return ret.equals("sco");
        }
    }

    @Override
    public String getResourceStartPage(Map<String, byte[]> bytesMap) {
        byte[] dataJson = bytesMap.get(METADATA_JSON_FILE);
        String json ="";
        if (dataJson == null){
            return null;
        }
        try {
            json = new String(dataJson,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("Error in determining if it is a cms zip resource: {}", e.getLocalizedMessage());
            return null;
        }

        Map<String,Object> map=gsonJsonParser.parseMap(json);

        String ret = null;
        if(map.get(METADATA_STARTPAGE)!=null){
            ret = (String) map.get(METADATA_STARTPAGE);
        }else if(map.get(METADATA_STARTPAGE_CAMEL)!=null){
            ret = (String) map.get(METADATA_STARTPAGE_CAMEL);
        }

        return ret;
    }

    @Override
    public String findParentResource(Map<String, byte[]> bytesMap) {
        byte[] dataXml = bytesMap.get(METADATA_XML_FILE);
        if(dataXml == null){
            return null;
        }
        String xml ="";
        String ret="";
        try {
            xml = new String(dataXml,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("Error in finding parent resource name: {}", e.getLocalizedMessage());
        }
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
        for (Element e : doc.select("resources")) {
            ret = e.select("resource").get(0).attr("href");
        }
        return ret;
    }

    @Override
    @Deprecated
    public DataMaps replaceRelativePaths(DataMaps dataMaps) throws IOException {
        /**
         * TODO: check performance...
         * */
        Map<String,byte[]> modifiedBytesMap =new HashMap<>();
        for (Map.Entry<String, byte[]> entry : dataMaps.getBytesMap().entrySet()) {
            String filename = entry.getKey();
            byte[] bytes = entry.getValue();

            if (acceptedExtensions.contains(filename.substring(filename.lastIndexOf(".")+1))) {
                String content = new String(bytes, "UTF-8");
                dataMaps.getBytesMap();
                for (String key : dataMaps.getBytesMap().keySet()) {
                    String replace = dataMaps.getUidPathMap().get(key);
                    content = content.replaceAll(key, replace);
                    content = content.replaceAll("(\\.\\.\\/)", "");
                    content = content.replaceAll("(\\.\\/)", "");
                }
                byte[] modifiedData = content.getBytes("UTF-8");
                modifiedBytesMap.put(filename,modifiedData);
            }
        }
        dataMaps.setModifiedBytesMap(modifiedBytesMap);
        return dataMaps;
    }
}
