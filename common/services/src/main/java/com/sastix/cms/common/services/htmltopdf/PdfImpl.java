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
import com.sastix.cms.common.dataobjects.htmltopdf.Params;
import com.sastix.cms.common.dataobjects.htmltopdf.page.Page;
import com.sastix.cms.common.dataobjects.htmltopdf.page.PageType;
import com.sastix.cms.common.services.htmltopdf.config.HtmlToPdfConfig;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PdfImpl implements Pdf {
    private static final String STDINOUT = "-";

    HtmlToPdfConfig htmlToPdfConfig;

    private Params params;

    private List<Page> pages;

    private boolean hasToc = false;

    public PdfImpl(HtmlToPdfConfig htmlToPdfConfig) {
        this.htmlToPdfConfig = htmlToPdfConfig;
        this.params = new Params();
        this.pages = new ArrayList<Page>();
    }

    public void addPage(String source, PageType type) {
        this.pages.add(new Page(source, type));
    }

    public void addToc() {
        this.hasToc = true;
    }

    public void addParam(Param param) {
        params.add(param);
    }

    public void addParam(Param... params) {
        for (Param param : params) {
            addParam(param);
        }
    }

    public void saveAs(String path) throws IOException, InterruptedException {
        saveAs(path, getPDF());
    }

    private File saveAs(String path, byte[] document) throws IOException {
        File file = new File(path);

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
        bufferedOutputStream.write(document);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();

        return file;
    }

    public byte[] getPDF() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(getCommandAsArray());
        Process process = processBuilder.start();
        //Runtime runtime = Runtime.getRuntime();
        //Process process = runtime.exec(getCommandAsArray());

        for (Page page : pages) {
            if (page.getType().equals(PageType.htmlAsString)) {
                OutputStream stdInStream = process.getOutputStream();
                stdInStream.write(page.getSource().getBytes("UTF-8"));
                stdInStream.close();
            }
        }

        StreamEater outputStreamEater = new StreamEater(process.getInputStream());
        outputStreamEater.start();

        StreamEater errorStreamEater = new StreamEater(process.getErrorStream());
        errorStreamEater.start();

        outputStreamEater.join();
        errorStreamEater.join();
        process.waitFor();

        if (process.exitValue() != 0) {
            throw new RuntimeException("Process (" + getCommand() + ") exited with status code " + process.exitValue() + ":\n" + new String(errorStreamEater.getBytes()));
        }

        if (outputStreamEater.getError() != null) {
            throw outputStreamEater.getError();
        }

        if (errorStreamEater.getError() != null) {
            throw errorStreamEater.getError();
        }

        return outputStreamEater.getBytes();
    }

    private String[] getCommandAsArray() {
        List<String> commandLine = new ArrayList<String>();

        commandLine.add(htmlToPdfConfig.getWkhtmltopdfCommand());

        if (hasToc)
            commandLine.add("toc");

        commandLine.addAll(params.getParamsAsStringList());

        for (Page page : pages) {
            if (page.getType().equals(PageType.htmlAsString)) {
                commandLine.add(STDINOUT);
            } else {
                commandLine.add(page.getSource());
            }
        }
        commandLine.add(STDINOUT);
        return commandLine.toArray(new String[commandLine.size()]);
    }

    public String getCommand() {
        return StringUtils.join(getCommandAsArray(), " ");
    }

    private class StreamEater extends Thread {

        private InputStream stream;
        private ByteArrayOutputStream bytes;

        private IOException error;

        public StreamEater(InputStream stream) {
            this.stream = stream;

            bytes = new ByteArrayOutputStream();
        }

        public void run() {
            try {
                int bytesRead = stream.read();
                while (bytesRead >= 0) {
                    bytes.write(bytesRead);
                    bytesRead = stream.read();
                }

                stream.close();
            } catch (IOException e) {
                e.printStackTrace();

                error = e;
            }
        }

        public IOException getError() {
            return error;
        }

        public byte[] getBytes() {
            return bytes.toByteArray();
        }
    }
}

