/*
 * Copyright(c) 2016 the original author or authors.
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

package com.sastix.cms.common.services.htmltopdf;

import com.sastix.cms.common.dataobjects.htmltopdf.Param;
import com.sastix.cms.common.dataobjects.htmltopdf.page.PageType;

import java.io.IOException;

/**
 * A java interface (wrapping usually a 3rd party  command line tool) exposing methods for html to pdf conversion
 */
public interface Pdf {
    void addPage(String page, PageType type);

    void addToc();

    void addParam(Param param);

    void addParam(Param... params);

    void saveAs(String path) throws IOException, InterruptedException;

    String getCommand();

    byte[] getPDF() throws IOException, InterruptedException;
}
