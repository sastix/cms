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

package com.sastix.cms.server.dataobjects;

import java.util.HashMap;
import java.util.Map;

public class DataMaps {
    Map<String, byte[]> bytesMap;
    Map<String, byte[]> modifiedBytesMap;
    Map<String, String> foldersMap = new HashMap<>();
    Map<String, String> uidMap = new HashMap<>();
    Map<String, String> uidPathMap = new HashMap<>();

    public Map<String, byte[]> getBytesMap() {
        return bytesMap;
    }

    public void setBytesMap(Map<String, byte[]> bytesMap) {
        this.bytesMap = bytesMap;
    }

    public Map<String, String> getFoldersMap() {
        return foldersMap;
    }

    public void setFoldersMap(Map<String, String> foldersMap) {
        this.foldersMap = foldersMap;
    }

    public Map<String, String> getUriMap() {
        return uidMap;
    }

    public void setUidMap(Map<String, String> uidMap) {
        this.uidMap = uidMap;
    }

    public Map<String, byte[]> getModifiedBytesMap() {
        return modifiedBytesMap;
    }

    public void setModifiedBytesMap(Map<String, byte[]> modifiedBytesMap) {
        this.modifiedBytesMap = modifiedBytesMap;
    }

    public Map<String, String> getUidPathMap() {
        return uidPathMap;
    }

    public void setUidPathMap(Map<String, String> uidPathMap) {
        this.uidPathMap = uidPathMap;
    }
}
